package com.flyingeffects.com.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTImage;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.R;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.constans.UiStep;
import com.flyingeffects.com.enity.CommonNewsBean;
import com.flyingeffects.com.enity.DownVideoPath;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.manager.AlbumManager;
import com.flyingeffects.com.manager.GlideRoundTransform;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.ui.model.GetPathTypeModel;
import com.flyingeffects.com.ui.view.activity.VideoCropActivity;
import com.flyingeffects.com.ui.view.activity.intoOtherAppActivity;
import com.flyingeffects.com.utils.LogUtil;
import com.nineton.ntadsdk.utils.DeviceUtil;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.MediaView;
import com.qq.e.ads.nativ.NativeADEventListener;
import com.qq.e.ads.nativ.NativeADMediaListener;
import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.qq.e.ads.nativ.widget.NativeAdContainer;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.util.AdError;
import com.shixing.sxve.ui.albumType;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

import static com.nineton.ntadsdk.bean.FeedAdConfigBean.FeedAdResultBean.BAIDU_FEED_AD_EVENT;
import static com.nineton.ntadsdk.bean.FeedAdConfigBean.FeedAdResultBean.GDT_FEED_AD_EVENT;
import static com.nineton.ntadsdk.bean.FeedAdConfigBean.FeedAdResultBean.TT_FEED_AD_EVENT;


/**
 * user :TongJu  ; email:jutongzhang@sina.com
 * time：2019/1/25
 * describe:首页适配
 **/
public class main_recycler_adapter extends BaseQuickAdapter<new_fag_template_item, BaseViewHolder> {

    private Context context;
    public final static String TAG = "main_recycler_adapter";
    //0 模板  1 背景 2 搜索/我的收藏 3 表示背景模板下载
    private int fromType;
    private TextView tv_advertising_title;
    private TextView csjTitle;
    private FrameLayout custom_container;
    private ImageView poster;
    private NativeAdContainer container;
    private MediaView mediaView;
    private VideoOption videoOption = null;
    private ImageView csj_listitem_image;
    private FrameLayout videoView;// 穿山甲的视频
    private ArrayList<CommonNewsBean>listCommentBean;


    public main_recycler_adapter(int layoutResId, @Nullable List<new_fag_template_item> allData, Context context, int fromType) {
        super(layoutResId, allData);
        this.context = context;
        this.fromType = fromType;
        SetVideoOption();
    }


    @Override
    protected void convert(final BaseViewHolder helper, final new_fag_template_item item) {
        int offset = helper.getLayoutPosition();
        LinearLayout ll_content_patents = helper.getView(R.id.ll_content_patents);
        custom_container = helper.getView(R.id.custom_container);
        container = helper.getView(R.id.native_ad_container);
        videoView = helper.getView(R.id.iv_listitem_video);
        csj_listitem_image = helper.getView(R.id.iv_listitem_image);
        mediaView = helper.getView(R.id.gdt_media_view);
        poster = helper.getView(R.id.img_poster);
        NativeAdContainer ll_ad_container = helper.getView(R.id.native_ad_container); //广告通
        csjTitle = helper.getView(R.id.tv_advertising_title_csj);
        tv_advertising_title = helper.getView(R.id.tv_advertising_title);
        LinearLayout csj_ad_container = helper.getView(R.id.listitem_ad_large_video); //穿山甲
        if (item.isHasShowAd()&&listCommentBean!=null) {
            int needGetAdPosition=offset%10;
            LogUtil.d("OOM","needGetAdPosition="+needGetAdPosition);
            if(listCommentBean.size()>=needGetAdPosition){
                CommonNewsBean commonNewsBean=listCommentBean.get(needGetAdPosition);
                ll_content_patents.setVisibility(View.GONE);
                if (commonNewsBean.getEventType() == GDT_FEED_AD_EVENT) {
                    NativeUnifiedADData ad =commonNewsBean.getGdtAdData();
                    if (ad != null) {
                        ll_ad_container.setVisibility(View.VISIBLE);
                        csj_ad_container.setVisibility(View.GONE);
                        initItemView(ad);
                    } else { //没有数据就隐藏,全部数据隐藏
                        showNullView(ll_content_patents, ll_ad_container, csj_ad_container);
                    }
                } else if (commonNewsBean.getEventType() == TT_FEED_AD_EVENT) {
                    TTFeedAd ad= commonNewsBean.getTtFeedAd();
                    if (ad != null ) {  //有数据才显示
                        ll_content_patents.setVisibility(View.GONE);
                        ll_ad_container.setVisibility(View.GONE);
                        csj_ad_container.setVisibility(View.VISIBLE);
                        bindData(csj_ad_container, ad);
                        int adMode = ad.getImageMode();
                        if (adMode == TTAdConstant.IMAGE_MODE_VIDEO) {  //视频类型广告
                            csj_listitem_image.setVisibility(View.GONE);
                            videoView.setVisibility(View.VISIBLE);
                            if (videoView != null) {
                                //获取视频播放view,该view SDK内部渲染，在媒体平台可配置视频是否自动播放等设置。
                                View video = ad.getAdView();
                                if (video != null) {
                                    if (video.getParent() == null) {
                                        videoView.removeAllViews();
                                        videoView.addView(video);
                                    }
                                }
                            }
                        } else { //其他均默认为图文广告类型，不区分其他类型广告
                            csj_listitem_image.setVisibility(View.VISIBLE);
                            videoView.setVisibility(View.GONE);
                            if (ad.getImageList() != null && !ad.getImageList().isEmpty()) {
                                TTImage image = ad.getImageList().get(0);
                                if (image != null && image.isValid()) {
                                    Glide.with(mContext).load(image.getImageUrl()).into(csj_listitem_image);
                                }
                            }
                        }
                    } else {
                        showNullView(ll_content_patents, ll_ad_container, csj_ad_container);
                    }


                } else {
                    //其他类型的格式。百度，目前暂时不配置
                    LogUtil.d("OOM", "其他的数据格式");
                    showNullView(ll_content_patents, ll_ad_container, csj_ad_container);

                }
            }else{
                showNullView(ll_content_patents, ll_ad_container, csj_ad_container);
            }

        } else {
            ll_ad_container.setVisibility(View.GONE);
            csj_ad_container.setVisibility(View.GONE);
            Glide.with(context)
                    .load(item.getImage())
                    .apply(RequestOptions.bitmapTransform(new GlideRoundTransform(context, 5)))
                    .apply(RequestOptions.placeholderOf(R.mipmap.placeholder))
                    .into((ImageView) helper.getView(R.id.iv_cover));
            ImageView iv_show_author = helper.getView(R.id.iv_show_author);
            RelativeLayout ConstraintLayout_addVideo = helper.getView(R.id.ConstraintLayout_addVideo);
            RelativeLayout ll_relative_2 = helper.getView(R.id.ll_relative_2);
            LinearLayout ll_relative_1 = helper.getView(R.id.ll_relative_1);
            RelativeLayout ll_relative_0 = helper.getView(R.id.ll_relative_0);
            TextView tv_name = helper.getView(R.id.tv_name);
            tv_name.setText(item.getTitle());
            if (fromType == 1) {
                if (offset == 1) {
                    ll_relative_2.setVisibility(View.GONE);
                    ll_relative_1.setVisibility(View.VISIBLE);
                    ll_relative_0.setVisibility(View.VISIBLE);
                    ConstraintLayout_addVideo.setVisibility(View.VISIBLE);
                    helper.setText(R.id.firstline, BaseConstans.configList.getFirstline());
                    helper.setText(R.id.secondline, BaseConstans.configList.getSecondline());
                    helper.setText(R.id.thirdline, BaseConstans.configList.getThirdline());
                    ConstraintLayout_addVideo.setOnClickListener(v -> {
                        Intent intent = new Intent(context, intoOtherAppActivity.class);
                        intent.putExtra("wx", "");
                        intent.putExtra("kuaishou", "");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                    });
                } else {
                    ConstraintLayout_addVideo.setVisibility(View.GONE);
                }
                helper.setText(R.id.tv_name2, item.getAuth());
                ImageView iv_show_author_template = helper.getView(R.id.iv_show_author_template);
                Glide.with(context)
                        .load(item.getAuth_image())
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(iv_show_author_template);
                iv_show_author.setVisibility(View.GONE);
                helper.setText(R.id.tv_zan_count, item.getPraise());
                tv_name.setVisibility(View.VISIBLE);
                ImageView iv_zan_state = helper.getView(R.id.iv_zan_state);
                iv_zan_state.setImageResource(item.getIs_praise() != 0 ? R.mipmap.zan_clicked : R.mipmap.zan_unclicked);
            } else if (fromType == 3) {
                //背景下载
                if (offset == 0) {
                    ll_relative_2.setVisibility(View.VISIBLE);
                    ll_relative_1.setVisibility(View.GONE);
                    ll_relative_0.setVisibility(View.GONE);
                    ConstraintLayout_addVideo.setVisibility(View.VISIBLE);
                    ConstraintLayout_addVideo.setOnClickListener(v -> {
                        AlbumManager.chooseAlbum(context, 1, 1, (tag, paths, isCancel, albumFileList) -> {
                            if (!isCancel) {

                                if (UiStep.isFromDownBj) {
                                    statisticsEventAffair.getInstance().setFlag(context, "7_local");
                                } else {
                                    statisticsEventAffair.getInstance().setFlag(context, "8_local");
                                }
                                // EventBus.getDefault().post(new DownVideoPath(paths.get(0)));
                                String pathType = GetPathTypeModel.getInstance().getMediaType(paths.get(0));
                                if (albumType.isImage(pathType)) {
                                    EventBus.getDefault().post(new DownVideoPath(paths.get(0)));
                                } else {
                                    //如果选择的视频
                                    Intent intent = new Intent(context, VideoCropActivity.class);
                                    intent.putExtra("videoPath", paths.get(0));
                                    intent.putExtra("comeFrom", FromToTemplate.ISFROMEDOWNVIDEOFORUSER);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    context.startActivity(intent);
                                }
                            }
                        }, "");
                    });
                } else {
                    ConstraintLayout_addVideo.setVisibility(View.GONE);
                }
                helper.setText(R.id.tv_name2, item.getAuth());
                ImageView iv_show_author_template = helper.getView(R.id.iv_show_author_template);
                Glide.with(context)
                        .load(item.getAuth_image())
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(iv_show_author_template);
                iv_show_author.setVisibility(View.GONE);
                helper.setText(R.id.tv_zan_count, item.getPraise());
                tv_name.setVisibility(View.VISIBLE);
                ImageView iv_zan_state = helper.getView(R.id.iv_zan_state);
                iv_zan_state.setImageResource(item.getIs_praise() != 0 ? R.mipmap.zan_clicked : R.mipmap.zan_unclicked);
                iv_show_author.setVisibility(View.GONE);
            } else {
                //模板
                if (offset == 1 && fromType == 0) {
                    ll_relative_1.setVisibility(View.VISIBLE);
                    ll_relative_0.setVisibility(View.VISIBLE);
                    ll_relative_2.setVisibility(View.GONE);
                    helper.setText(R.id.firstline, BaseConstans.configList.getFirstline());
                    helper.setText(R.id.secondline, BaseConstans.configList.getSecondline());
                    helper.setText(R.id.thirdline, BaseConstans.configList.getThirdline());
                    ConstraintLayout_addVideo.setVisibility(View.VISIBLE);
                    ConstraintLayout_addVideo.setOnClickListener(v -> {
                        Intent intent = new Intent(context, intoOtherAppActivity.class);
                        intent.putExtra("wx", "");
                        intent.putExtra("kuaishou", "");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                    });
                } else {
                    ConstraintLayout_addVideo.setVisibility(View.GONE);
                }
                helper.setText(R.id.tv_name2, item.getAuth());
                ImageView iv_show_author_template = helper.getView(R.id.iv_show_author_template);
                Glide.with(context)
                        .load(item.getAuth_image())
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(iv_show_author_template);
                iv_show_author.setVisibility(View.GONE);
                helper.setText(R.id.tv_zan_count, item.getPraise());
                tv_name.setVisibility(View.VISIBLE);
                ImageView iv_zan_state = helper.getView(R.id.iv_zan_state);
                iv_zan_state.setImageResource(item.getIs_praise() != 0 ? R.mipmap.zan_clicked : R.mipmap.zan_unclicked);
                ll_content_patents.setVisibility(View.VISIBLE);
            }
        }
        ImageView iv_zan_state = helper.getView(R.id.iv_zan_state);
        if(item.getIs_ad_recommend()==1){
            iv_zan_state.setVisibility(View.GONE);
            helper.setText(R.id.tv_zan_count, "");
        }else{
            iv_zan_state.setVisibility(View.VISIBLE);
        }
    }


    private void initItemView(NativeUnifiedADData ad) {
        tv_advertising_title.setText(ad.getTitle());
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(custom_container);
        if (ad.getAdPatternType() == 2) {  // 视频广告
//            LogUtil.d("onNoAD","视频来了");
            poster.setVisibility(View.INVISIBLE);
            mediaView.setVisibility(View.VISIBLE);
        } else {
            poster.setVisibility(View.VISIBLE);
            mediaView.setVisibility(View.INVISIBLE);
            Glide.with(context).load(ad.getImgUrl()).into(poster);
        }
        ad.bindAdToView(context, container, null,
                clickableViews);
        setAdListener(ad);
    }


    /**
     * description ：广告回调
     * date: ：2019/9/25 18:55
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    private void setAdListener(final NativeUnifiedADData ad) {
        ad.setNativeAdEventListener(new NativeADEventListener() {
            @Override
            public void onADExposed() {
                Log.d(TAG, "onADExposed: " + ad.getTitle());
            }

            @Override
            public void onADClicked() {
                Log.d(TAG, "onADClicked: " + ad.getTitle());
            }

            @Override
            public void onADError(AdError error) {
                Log.d(TAG, "onADError error code :" + error.getErrorCode()
                        + "  error msg: " + error.getErrorMsg());
            }

            @Override
            public void onADStatusChanged() {
                // updateAdAction(download, ad);
            }
        });
        // 视频广告
        if (ad.getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
//            VideoOption videoOption =
//                    getVideoOption(getIntent());
            ad.bindMediaView(mediaView, videoOption, new NativeADMediaListener() {
                @Override
                public void onVideoInit() {
                    Log.d(TAG, "onVideoInit: ");
                }

                @Override
                public void onVideoLoading() {
                    Log.d(TAG, "onVideoLoading: ");
                }

                @Override
                public void onVideoReady() {
                    Log.d(TAG, "onVideoReady: duration:" + ad.getVideoDuration());
                }

                @Override
                public void onVideoLoaded(int videoDuration) {
                    Log.d(TAG, "onVideoLoaded: ");
                }

                @Override
                public void onVideoStart() {
                    Log.d(TAG, "onVideoStart: duration:" + ad.getVideoDuration());
                }

                @Override
                public void onVideoPause() {
                    Log.d(TAG, "onVideoPause: ");
                }

                @Override
                public void onVideoResume() {
                    Log.d(TAG, "onVideoResume: ");
                }

                @Override
                public void onVideoCompleted() {
                    Log.d(TAG, "onVideoCompleted: ");
                }

                @Override
                public void onVideoError(AdError error) {
                    Log.d(TAG, "onVideoError: ");
                }

                @Override
                public void onVideoStop() {
                    Log.d(TAG, "onVideoStop");
                }

                @Override
                public void onVideoClicked() {
                    Log.d(TAG, "onVideoClicked");
                }
            });

        }
    }


    private void SetVideoOption() {
        VideoOption.Builder builder = new VideoOption.Builder();
        builder.setAutoPlayMuted(true);
        builder.setNeedCoverImage(true);
        builder.setNeedProgressBar(false);
        builder.setEnableDetailPage(true);
        builder.setEnableUserControl(false);
        videoOption = builder.build();
    }


    private void showNullView(LinearLayout iv_show_content, NativeAdContainer ll_ad_container, LinearLayout csj_ad_container) {
        iv_show_content.setVisibility(View.GONE);
        ll_ad_container.setVisibility(View.GONE);
        csj_ad_container.setVisibility(View.GONE);
    }



    /**
     * description ：穿山甲广告
     * date: ：2019/10/16 13:42
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */

    private void bindData(View convertView, TTFeedAd ad) {
        //可以被点击的view, 也可以把convertView放进来意味item可被点击
        List<View> clickViewList = new ArrayList<>();
        clickViewList.add(convertView);
        //触发创意广告的view（点击下载或拨打电话）
        List<View> creativeViewList = new ArrayList<>();
        creativeViewList.add(convertView);
        //重要! 这个涉及到广告计费，必须正确调用。convertView必须使用ViewGroup。
        ad.registerViewForInteraction((ViewGroup) convertView, clickViewList, creativeViewList, new TTNativeAd.AdInteractionListener() {
            @Override
            public void onAdClicked(View view, TTNativeAd ad) {
                if (ad != null) {
                    LogUtil.d("csj", "广告" + ad.getTitle() + "被点击");
                }
            }

            @Override
            public void onAdCreativeClick(View view, TTNativeAd ad) {
                if (ad != null) {
                    LogUtil.d("csj", "广告" + ad.getTitle() + "被创意按钮被点击");
                }
            }

            @Override
            public void onAdShow(TTNativeAd ad) {
                if (ad != null) {
                    LogUtil.d("csj", "广告" + ad.getTitle() + "展示");
                }
            }
        });
        csjTitle.setText(ad.getTitle()); //title为广告的简单信息提示
        switch (ad.getInteractionType()) {
            case TTAdConstant.INTERACTION_TYPE_DOWNLOAD:  //下载
                //如果初始化ttAdManager.createAdNative(getApplicationContext())没有传入activity 则需要在此传activity，否则影响使用Dislike逻辑
                if (mContext instanceof Activity) {
                    ad.setActivityForDownloadApp((Activity) mContext);
                }
                break;
            case TTAdConstant.INTERACTION_TYPE_DIAL:  //拨打电话
                break;
            case TTAdConstant.INTERACTION_TYPE_LANDING_PAGE:  //
            case TTAdConstant.INTERACTION_TYPE_BROWSER:  //浏览

                break;
            default:
        }
    }



    public void setAdList(ArrayList<CommonNewsBean>listCommentBean){
        this.listCommentBean=listCommentBean;
    }


}










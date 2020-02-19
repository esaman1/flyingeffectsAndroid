package com.mobile.flyingeffects.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTImage;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gyf.immersionbar.ImmersionBar;
import com.mobile.flyingeffects.R;
import com.mobile.flyingeffects.enity.new_fag_template_item;
import com.mobile.kadian.R;
import com.mobile.kadian.constans.BaseConstans;
import com.mobile.kadian.enity.new_fag_template_item;
import com.mobile.kadian.manager.GlideRoundTransform;
import com.mobile.kadian.manager.statisticsEventAffair;
import com.mobile.kadian.ui.view.activity.intoOtherApp;
import com.mobile.kadian.ui.view.activity.webViewActivity;
import com.mobile.kadian.utils.LogUtil;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.MediaView;
import com.qq.e.ads.nativ.NativeADEventListener;
import com.qq.e.ads.nativ.NativeADMediaListener;
import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.qq.e.ads.nativ.widget.NativeAdContainer;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.util.AdError;

import java.util.ArrayList;
import java.util.List;


/**
 * user :TongJu  ; email:jutongzhang@sina.com
 * time：2019/1/25
 * describe:首页适配
 **/
public class main_recycler_adapter extends BaseQuickAdapter<new_fag_template_item, BaseViewHolder> {

    private Context context;
    public final static String TAG = "main_recycler_adapter";
    public showOnitemClick callback;
    private boolean hasContribute = true;
    public TextView title;
    private MediaView mediaView;
    private FrameLayout videoView;// 穿山甲的视频
    private ImageView csj_listitem_image;
    private TextView csjTitle;
    private ImageView poster;
    private NativeAdContainer container;
    private FrameLayout custom_container;
    private TextView tv_advertising_title;
    private VideoOption videoOption = null;
    private int adType = 0;  //广告类型 0 为广点通 1为穿山甲

    public main_recycler_adapter(int layoutResId, @Nullable List<new_fag_template_item> allData, Context context, showOnitemClick callback, int adType) {
        super(layoutResId, allData);
        this.context = context;
        this.callback = callback;
        this.adType = adType;
        ImmersionBar.with((Activity) context).statusBarColor(R.color.white).init();
        SetVideoOption();
    }


    public main_recycler_adapter(int layoutResId, @Nullable List<new_fag_template_item> allData, Context context, showOnitemClick callback, boolean hasContribute) {
        super(layoutResId, allData);
        this.context = context;
        this.callback = callback;
        this.hasContribute = hasContribute;
        ImmersionBar.with((Activity) context).statusBarColor(R.color.white).init();
        SetVideoOption();
    }

    @Override
    protected void convert(final BaseViewHolder helper, final new_fag_template_item item) {
        int offset = helper.getLayoutPosition();
        NativeAdContainer ll_ad_container = helper.getView(R.id.native_ad_container); //广告通
        LinearLayout listitem_ad_large_video = helper.getView(R.id.listitem_ad_large_video); //穿山甲
        videoView = helper.getView(R.id.iv_listitem_video);
        csjTitle = helper.getView(R.id.tv_advertising_title_csj);
        csj_listitem_image = helper.getView(R.id.iv_listitem_image);
        LinearLayout iv_show_content = helper.getView(R.id.iv_show_content);
        RelativeLayout relative_faddish = helper.getView(R.id.relative_faddish);
        custom_container = helper.getView(R.id.custom_container);
        mediaView = helper.getView(R.id.gdt_media_view);
        tv_advertising_title = helper.getView(R.id.tv_advertising_title);
        poster = helper.getView(R.id.img_poster);
        if (item.getIs_advices() != 0 && !BaseConstans.getIsNewUser()) {  //是否存在激励视频
            relative_faddish.setVisibility(View.VISIBLE);
        } else {
            relative_faddish.setVisibility(View.GONE);
        }
        container = helper.getView(R.id.native_ad_container);
        if (item.isAdvertisinga()) {  //当前item 应该为广告
            int nowShowAdvertisingPosition = getAdPosition(offset);
            showAllAd(iv_show_content, ll_ad_container, listitem_ad_large_video, nowShowAdvertisingPosition);
        } else {
            listitem_ad_large_video.setVisibility(View.GONE);
            iv_show_content.setVisibility(View.VISIBLE);
            ll_ad_container.setVisibility(View.GONE);
            helper.setText(R.id.tv_time, "P" + item.getRequired_material());
            helper.setText(R.id.tv_title, item.getTitle());
            LinearLayout ll_show_parents = helper.getView(R.id.ll_show_parents);
            ll_show_parents.setOnClickListener(v -> {
                statisticsEventAffair.getInstance().setFlag(context, "4_search_click", "4_search_click");
                if (item.getDiversion() != null && !item.getDiversion().trim().equals("")) {
                    if (item.getIs_nav() == 1) {
                        StringBuilder builder = new StringBuilder();
                        String url = item.getDiversion();
                        builder.append(url);
                        if (url.contains("kadianactivity.nineton.cn")) {
                            builder.append("?iswebView=true");
                        }
                        Intent intent = new Intent(context, webViewActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("webUrl", builder.toString());
                        context.startActivity(intent);
                    } else {
                        try {
                            String url = item.getDiversion();
                            String versionCode = BaseConstans.getVersionCode();
                            int versionCodeInter = Integer.parseInt(versionCode);
                            if (url.contains("christmas") && versionCodeInter >= 25) {
                                Intent intent = new Intent(context, webViewActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("webUrl", url);
                                context.startActivity(intent);
                            } else {
                                Uri uri = Uri.parse(item.getDiversion());
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                context.startActivity(intent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                } else {
                    callback.clickItem(offset);
                }
            });


            Glide.with(context)
                    .load(item.getThumb())
                    .apply(RequestOptions.bitmapTransform(new GlideRoundTransform(context, 5)))
                    .apply(RequestOptions.placeholderOf(getDrawble(offset)))
                    .into((ImageView) helper.getView(R.id.iv_show_gif));
            LinearLayout ConstraintLayout_addVideo = helper.getView(R.id.ConstraintLayout_addVideo);
            if (offset == 1 && hasContribute) {
                ConstraintLayout_addVideo.setVisibility(View.VISIBLE);
                ConstraintLayout_addVideo.setOnClickListener(v -> {
                    statisticsEventAffair.getInstance().setFlag(context, "1_contribute", "1_contribute");
                    Intent intent = new Intent(context, intoOtherApp.class);
                    intent.putExtra("wx", "");
                    intent.putExtra("kuaishou", "");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                });
            } else {
                ConstraintLayout_addVideo.setVisibility(View.GONE);
            }
        }
    }


    /**
     * description ：显示广告，2种类型，穿山甲的feed 和广点通2.0自渲染
     * date: ：2019/10/16 14:31
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    private void showAllAd(LinearLayout iv_show_content, NativeAdContainer ll_ad_container, LinearLayout csj_ad_container, int nowShowAdvertisingPosition) {
        if (adType == 0) { //广点通广告
            if (mAds != null && mAds.size() > nowShowAdvertisingPosition && mAds.get(nowShowAdvertisingPosition) != null) {  //有数据才显示
                iv_show_content.setVisibility(View.GONE);
                ll_ad_container.setVisibility(View.VISIBLE);
                csj_ad_container.setVisibility(View.GONE);
                initItemView(nowShowAdvertisingPosition);
            } else { //没有数据就隐藏,全部数据隐藏
                showNullView(iv_show_content, ll_ad_container, csj_ad_container);
            }
        } else { //穿山甲广告
            if (mAdsForCSJ != null && mAdsForCSJ.size() > nowShowAdvertisingPosition && mAdsForCSJ.get(nowShowAdvertisingPosition) != null) {  //有数据才显示
                iv_show_content.setVisibility(View.GONE);
                ll_ad_container.setVisibility(View.GONE);
                csj_ad_container.setVisibility(View.VISIBLE);
                TTFeedAd ad = mAdsForCSJ.get(nowShowAdvertisingPosition);
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
                showNullView(iv_show_content, ll_ad_container, csj_ad_container);
            }
        }
    }


    private void initItemView(int position) {
        final NativeUnifiedADData ad = mAds.get(position);
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


    private int[] glide_bg = {R.drawable.glide_loading_bg_0, R.drawable.glide_loading_bg_1, R.drawable.glide_loading_bg_2, R.drawable.glide_loading_bg_3, R.drawable.glide_loading_bg_4, R.drawable.glide_loading_bg_5,};

    private int getDrawble(int num) {

        int nowChoose = num % 6;
        LogUtil.d("nowChoose", nowChoose + "");
        return glide_bg[nowChoose];
    }


    public interface showOnitemClick {

        void clickItem(int position);

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


    public void addAdToPosition(List<NativeUnifiedADData> mAds) {
        this.mAds = mAds;
    }


    public void addCSJAdToPosition(List<TTFeedAd> mAdsForCSJ) {
        this.mAdsForCSJ = mAdsForCSJ;
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


    /**
     * description ：获取应该取广告的位置
     * date: ：2019/10/16 14:28
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    private int getAdPosition(int offset) {
        int waterfallFlowCount = BaseConstans.waterfallFlowCount; //每一页显示的数量
        int nowShowAdvertisingPosition;
        float nowPosition = offset / (float) 10;
        int integerPosition = (int) nowPosition;
        if (waterfallFlowCount != 1) {  //存在2个的情况
            float floatAdvertisingPosition = nowPosition - integerPosition;
            if (floatAdvertisingPosition > 0.5) {
                nowShowAdvertisingPosition = (integerPosition * 2) + 1;
            } else {
                nowShowAdvertisingPosition = (integerPosition * 2);
            }
        } else {
            nowShowAdvertisingPosition = integerPosition;
        }

        return nowShowAdvertisingPosition;
    }


    private void showNullView(LinearLayout iv_show_content, NativeAdContainer ll_ad_container, LinearLayout csj_ad_container) {
        iv_show_content.setVisibility(View.GONE);
        ll_ad_container.setVisibility(View.GONE);
        csj_ad_container.setVisibility(View.GONE);
    }


}

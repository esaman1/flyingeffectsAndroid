package com.flyingeffects.com.adapter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.GlideRoundTransform;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.AlbumChooseCallback;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.ui.model.GetPathTypeModel;
import com.flyingeffects.com.ui.view.activity.UploadMaterialActivity;
import com.flyingeffects.com.ui.view.activity.VideoCropActivity;
import com.flyingeffects.com.ui.view.activity.intoOtherAppActivity;
import com.flyingeffects.com.ui.view.dialog.CommonMessageDialog;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.MediaView;
import com.qq.e.ads.nativ.NativeADEventListener;
import com.qq.e.ads.nativ.NativeADMediaListener;
import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.qq.e.ads.nativ.widget.NativeAdContainer;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.util.AdError;
import com.shixing.sxve.ui.albumType;
import com.yanzhenjie.album.AlbumFile;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import de.greenrobot.event.EventBus;

import static com.nineton.ntadsdk.bean.FeedAdConfigBean.FeedAdResultBean.GDT_FEED_AD_EVENT;
import static com.nineton.ntadsdk.bean.FeedAdConfigBean.FeedAdResultBean.TT_FEED_AD_EVENT;


/**
 * user :TongJu  ; email:jutongzhang@sina.com
 * time：2019/1/25
 * describe:首页适配
 **/
public class MainRecyclerAdapter extends BaseQuickAdapter<new_fag_template_item, BaseViewHolder> {
    public static final int FROM_TEMPLATE_CODE = 0;
    public static final int FROM_BACK_CODE = 1;
    public static final int FROM_SEARCH_CODE = 2;
    public static final int FROM_DOWNLOAD_CODE = 3;
    public static final int FROM_DRESS_CODE = 4;

    public final static String TAG = "MainRecyclerAdapter";
    //0 模板  1 背景 2 搜索/我的收藏 3 表示背景模板下载 4 换装
    private final int fromType;
    private TextView tvAdvertisingTitle;
    private TextView csjTitle;
    private FrameLayout customContainer;
    private ImageView poster;
    private NativeAdContainer container;
    private MediaView mediaView;
    private VideoOption videoOption = null;
    private ImageView csjListitemImage;
    /**
     * 穿山甲的视频
     */
    private FrameLayout videoView;
    private ArrayList<CommonNewsBean> listCommentBean;
    boolean isFromSearch;


    public MainRecyclerAdapter(int layoutResId, @Nullable
            List<new_fag_template_item> allData, int fromType, boolean isFromSearch) {
        super(layoutResId, allData);
        this.fromType = fromType;
        this.isFromSearch = isFromSearch;
        setVideoOption();
    }


    @Override
    protected void convert(final BaseViewHolder helper, final new_fag_template_item item) {
        int offset = helper.getLayoutPosition();

        LinearLayout llContentPatents = helper.getView(R.id.ll_content_patents);
        customContainer = helper.getView(R.id.custom_container);
        container = helper.getView(R.id.native_ad_container);
        videoView = helper.getView(R.id.iv_listitem_video);
        csjListitemImage = helper.getView(R.id.iv_listitem_image);
        mediaView = helper.getView(R.id.gdt_media_view);
        poster = helper.getView(R.id.img_poster);

        NativeAdContainer llAdContainer = helper.getView(R.id.native_ad_container); //广告通
        csjTitle = helper.getView(R.id.tv_advertising_title_csj);
        tvAdvertisingTitle = helper.getView(R.id.tv_advertising_title);

        LinearLayout csjAdContainer = helper.getView(R.id.listitem_ad_large_video); //穿山甲
        if (item.isHasShowAd() && listCommentBean != null) {
            int needGetAdPosition = offset % 10;
            LogUtil.d("OOM", "needGetAdPosition=" + needGetAdPosition);
            if (listCommentBean.size() >= needGetAdPosition) {
                CommonNewsBean commonNewsBean = listCommentBean.get(needGetAdPosition);
                llContentPatents.setVisibility(View.GONE);
                if (commonNewsBean.getEventType() == GDT_FEED_AD_EVENT) {
                    NativeUnifiedADData ad = commonNewsBean.getGdtAdData();
                    if (ad != null) {
                        llAdContainer.setVisibility(View.VISIBLE);
                        csjAdContainer.setVisibility(View.GONE);
                        initItemView(ad);
                    } else { //没有数据就隐藏,全部数据隐藏
                        showNullView(llContentPatents, llAdContainer, csjAdContainer);
                    }
                } else if (commonNewsBean.getEventType() == TT_FEED_AD_EVENT) {
                    TTFeedAd ad = commonNewsBean.getTtFeedAd();
                    if (ad != null) {  //有数据才显示
                        llContentPatents.setVisibility(View.GONE);
                        llAdContainer.setVisibility(View.GONE);
                        csjAdContainer.setVisibility(View.VISIBLE);
                        bindData(csjAdContainer, ad);
                        int adMode = ad.getImageMode();
                        if (adMode == TTAdConstant.IMAGE_MODE_VIDEO) {  //视频类型广告
                            csjListitemImage.setVisibility(View.GONE);
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
                            csjListitemImage.setVisibility(View.VISIBLE);
                            videoView.setVisibility(View.GONE);
                            if (ad.getImageList() != null && !ad.getImageList().isEmpty()) {
                                TTImage image = ad.getImageList().get(0);
                                if (image != null && image.isValid()) {
                                    Glide.with(mContext).load(image.getImageUrl()).into(csjListitemImage);
                                }
                            }
                        }
                    } else {
                        showNullView(llContentPatents, llAdContainer, csjAdContainer);
                    }


                } else {
                    //其他类型的格式。百度，目前暂时不配置
                    LogUtil.d("OOM", "其他的数据格式");
                    showNullView(llContentPatents, llAdContainer, csjAdContainer);
                }
            } else {
                showNullView(llContentPatents, llAdContainer, csjAdContainer);
            }

        } else {
            llAdContainer.setVisibility(View.GONE);
            csjAdContainer.setVisibility(View.GONE);
            Glide.with(mContext)
                    .load(item.getImage())
                    .apply(RequestOptions.bitmapTransform(new GlideRoundTransform(mContext, 5)))
                    .apply(RequestOptions.placeholderOf(R.mipmap.placeholder))
                    .into((ImageView) helper.getView(R.id.iv_cover));
            ImageView ivShowAuthor = helper.getView(R.id.iv_show_author);
            RelativeLayout constraintLayoutAddVideo = helper.getView(R.id.ConstraintLayout_addVideo);
            RelativeLayout llRelative2 = helper.getView(R.id.ll_relative_2);
            RelativeLayout addImage = helper.getView(R.id.add_image);
            LinearLayout llRelative1 = helper.getView(R.id.ll_relative_1);
            RelativeLayout llRelative0 = helper.getView(R.id.ll_relative_0);
            TextView tvName = helper.getView(R.id.tv_name);
            tvName.setText(item.getTitle());
            if (fromType == FROM_BACK_CODE) {
                if (offset == 1) {
                    llRelative2.setVisibility(View.GONE);
                    llRelative1.setVisibility(View.VISIBLE);
                    llRelative0.setVisibility(View.VISIBLE);
                    constraintLayoutAddVideo.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(BaseConstans.configList.getFirstline())) {
                        helper.setText(R.id.firstline, BaseConstans.configList.getFirstline());
                    }

                    if (!TextUtils.isEmpty(BaseConstans.configList.getSecondline())) {
                        helper.setText(R.id.secondline, BaseConstans.configList.getSecondline());
                    }

                    if (!TextUtils.isEmpty(BaseConstans.configList.getThirdline())) {
                        helper.setText(R.id.thirdline, BaseConstans.configList.getThirdline());
                    }

                    constraintLayoutAddVideo.setOnClickListener(v -> {
                        //jumpToIntoOtherApp();
                        showMessageDialog();
                    });
                } else {
                    constraintLayoutAddVideo.setVisibility(View.GONE);
                }
                helper.setText(R.id.tv_name2, item.getAuth());
                ImageView ivShowAuthorTemplate = helper.getView(R.id.iv_show_author_template);
                Glide.with(mContext)
                        .load(item.getAuth_image())
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(ivShowAuthorTemplate);
                ivShowAuthor.setVisibility(View.GONE);
                helper.setText(R.id.tv_zan_count, item.getPraise());
                tvName.setVisibility(View.VISIBLE);
                ImageView ivZanState = helper.getView(R.id.iv_zan_state);
                ivZanState.setImageResource(item.getIs_praise() != 0 ? R.mipmap.zan_clicked : R.mipmap.zan_unclicked);
            } else if (fromType == FROM_DOWNLOAD_CODE) {
                //背景下载
                if (offset == 0 && !isFromSearch) {
                    llRelative2.setVisibility(View.VISIBLE);
                    llRelative1.setVisibility(View.GONE);
                    llRelative0.setVisibility(View.GONE);
                    constraintLayoutAddVideo.setVisibility(View.VISIBLE);
                    constraintLayoutAddVideo.setOnClickListener(v -> {
                        AlbumManager.chooseAlbum(mContext, 1, 1, (tag, paths, isCancel, isFromCamera, albumFileList) -> {
                            if (!isCancel) {
                                if (UiStep.isFromDownBj) {
                                    StatisticsEventAffair.getInstance().setFlag(mContext, "7_local");
                                } else {
                                    StatisticsEventAffair.getInstance().setFlag(mContext, "8_local");
                                }
                                // EventBus.getDefault().post(new DownVideoPath(paths.get(0)));
                                String pathType = GetPathTypeModel.getInstance().getMediaType(paths.get(0));
                                if (albumType.isImage(pathType)) {
                                    EventBus.getDefault().post(new DownVideoPath(paths.get(0)));
                                } else {
                                    //如果选择的视频
                                    Intent intent = new Intent(mContext, VideoCropActivity.class);
                                    intent.putExtra("videoPath", paths.get(0));
                                    intent.putExtra("comeFrom", FromToTemplate.ISFROMEDOWNVIDEOFORUSER);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    mContext.startActivity(intent);
                                }
                            }
                        }, "");
                    });
                } else {
                    constraintLayoutAddVideo.setVisibility(View.GONE);
                }
                helper.setText(R.id.tv_name2, item.getAuth());
                ImageView ivShowAuthorTemplate = helper.getView(R.id.iv_show_author_template);
                Glide.with(mContext)
                        .load(item.getAuth_image())
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(ivShowAuthorTemplate);
                ivShowAuthor.setVisibility(View.GONE);
                helper.setText(R.id.tv_zan_count, item.getPraise());
                tvName.setVisibility(View.VISIBLE);
                ImageView ivZanState = helper.getView(R.id.iv_zan_state);
                ivZanState.setImageResource(item.getIs_praise() != 0 ? R.mipmap.zan_clicked : R.mipmap.zan_unclicked);
                ivShowAuthor.setVisibility(View.GONE);
            } else if (fromType == FROM_DRESS_CODE) {
                //换装
                if (offset == 1 && TextUtils.isEmpty(tabName)) {
                    addImage.setVisibility(View.VISIBLE);
                    constraintLayoutAddVideo.setVisibility(View.GONE);
                } else {
                    addImage.setVisibility(View.GONE);
                }
                addImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!DoubleClick.getInstance().isFastDoubleClick()) {
                            StatisticsEventAffair.getInstance().setFlag(mContext, "21_face_up");
                            AlbumManager.chooseImageAlbum(mContext, 1, 0, new AlbumChooseCallback() {
                                @Override
                                public void resultFilePath(int tag, List<String> paths, boolean isCancel, boolean isFromCamera, ArrayList<AlbumFile> albumFileList) {
                                    if (!isCancel) {
                                        intoUploadMaterialActivity(paths.get(0));
                                    }
                                }
                            }, "");
                        }
                    }
                });
                helper.setText(R.id.tv_name2, item.getAuth());
                ImageView ivShowAuthorTemplate = helper.getView(R.id.iv_show_author_template);
                Glide.with(mContext)
                        .load(item.getAuth_image())
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(ivShowAuthorTemplate);
                ivShowAuthor.setVisibility(View.GONE);
                helper.setText(R.id.tv_zan_count, item.getPraise());
                tvName.setVisibility(View.VISIBLE);
                ImageView ivZanState = helper.getView(R.id.iv_zan_state);
                ivZanState.setImageResource(item.getIs_praise() != 0 ? R.mipmap.zan_clicked : R.mipmap.zan_unclicked);
                ivShowAuthor.setVisibility(View.GONE);
            } else {
                //模板
                if (offset == 1 && fromType == FROM_TEMPLATE_CODE) {
                    llRelative1.setVisibility(View.VISIBLE);
                    llRelative0.setVisibility(View.VISIBLE);
                    llRelative2.setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(BaseConstans.configList.getFirstline())) {
                        helper.setText(R.id.firstline, BaseConstans.configList.getFirstline());
                    }
                    if (!TextUtils.isEmpty(BaseConstans.configList.getSecondline())) {
                        helper.setText(R.id.secondline, BaseConstans.configList.getSecondline());
                    }
                    if (!TextUtils.isEmpty(BaseConstans.configList.getThirdline())) {
                        helper.setText(R.id.thirdline, BaseConstans.configList.getThirdline());
                    }
                    constraintLayoutAddVideo.setVisibility(View.VISIBLE);
                    constraintLayoutAddVideo.setOnClickListener(v -> {
                        //jumpToIntoOtherApp();
                        showMessageDialog();
                    });
                } else {
                    constraintLayoutAddVideo.setVisibility(View.GONE);
                }
                helper.setText(R.id.tv_name2, item.getAuth());
                ImageView ivShowAuthorTemplate = helper.getView(R.id.iv_show_author_template);
                Glide.with(mContext)
                        .load(item.getAuth_image())
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(ivShowAuthorTemplate);
                ivShowAuthor.setVisibility(View.GONE);
                helper.setText(R.id.tv_zan_count, item.getPraise());
                tvName.setVisibility(View.VISIBLE);
                ImageView ivZanState = helper.getView(R.id.iv_zan_state);
                ivZanState.setImageResource(item.getIs_praise() != 0 ? R.mipmap.zan_clicked : R.mipmap.zan_unclicked);
                llContentPatents.setVisibility(View.VISIBLE);
            }
        }
        ImageView ivZanState = helper.getView(R.id.iv_zan_state);
        if (item.getIs_ad_recommend() == 1) {
            ivZanState.setVisibility(View.GONE);
            helper.setText(R.id.tv_zan_count, "");
        } else {
            ivZanState.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 弹出dialog
     */
    private void showMessageDialog() {
        //复制到剪贴板
        ClipboardManager tvCopy = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        tvCopy.setPrimaryClip(ClipData.newPlainText(null, BaseConstans.getService_wxi()));
        //弹出dialog
        CommonMessageDialog.getBuilder(mContext)
                .setContentView(R.layout.dialog_common_message)
                .setAdStatus(CommonMessageDialog.AD_STATUS_MIDDLE)
                .setTitle(BaseConstans.configList.getTitle())
                .setMessage(BaseConstans.configList.getContent())
                .setMessage2(BaseConstans.configList.getCopydata())
                .setMessage3(BaseConstans.configList.getDescription())
                .setPositiveButton("立即打开微信获取")
                .setDialogBtnClickListener(new CommonMessageDialog.DialogBtnClickListener() {
                    @Override
                    public void onPositiveBtnClick(CommonMessageDialog dialog) {
                        openWx();
                    }

                    @Override
                    public void onCancelBtnClick(CommonMessageDialog dialog) {
                        dialog.dismiss();
                    }
                }).build()
                .show();
    }

    private void openWx() {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            ToastUtil.showToast(mContext.getString(R.string.check_login_notification));
        }
    }

    private void jumpToIntoOtherApp() {
        Intent intent = new Intent(mContext, intoOtherAppActivity.class);
        intent.putExtra("wx", "");
        intent.putExtra("kuaishou", "");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }


    private void initItemView(NativeUnifiedADData ad) {
        tvAdvertisingTitle.setText(ad.getTitle());
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(customContainer);
        if (ad.getAdPatternType() == 2) {  // 视频广告
//            LogUtil.d("onNoAD","视频来了");
            poster.setVisibility(View.INVISIBLE);
            mediaView.setVisibility(View.VISIBLE);
        } else {
            poster.setVisibility(View.VISIBLE);
            mediaView.setVisibility(View.INVISIBLE);
            Glide.with(mContext).load(ad.getImgUrl()).into(poster);
        }
        ad.bindAdToView(mContext, container, null,
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


    private void setVideoOption() {
        VideoOption.Builder builder = new VideoOption.Builder();
        builder.setAutoPlayMuted(true);
        builder.setNeedCoverImage(true);
        builder.setNeedProgressBar(false);
        builder.setEnableDetailPage(true);
        builder.setEnableUserControl(false);
        videoOption = builder.build();
    }


    private void showNullView(LinearLayout ivShowContent, NativeAdContainer llAdContainer, LinearLayout csjAdContainer) {
        ivShowContent.setVisibility(View.GONE);
        llAdContainer.setVisibility(View.GONE);
        csjAdContainer.setVisibility(View.GONE);
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
        //title为广告的简单信息提示
        csjTitle.setText(ad.getTitle());
        switch (ad.getInteractionType()) {
            //下载
            case TTAdConstant.INTERACTION_TYPE_DOWNLOAD:
                //如果初始化ttAdManager.createAdNative(getApplicationContext())没有传入activity 则需要在此传activity，否则影响使用Dislike逻辑
                if (mContext instanceof Activity) {
                    ad.setActivityForDownloadApp((Activity) mContext);
                }
                break;
            //拨打电话
            case TTAdConstant.INTERACTION_TYPE_DIAL:
                break;
            case TTAdConstant.INTERACTION_TYPE_LANDING_PAGE:
            //浏览
            case TTAdConstant.INTERACTION_TYPE_BROWSER:

                break;
            default:
        }
    }


    public void setAdList(ArrayList<CommonNewsBean> listCommentBean) {
        this.listCommentBean = listCommentBean;
    }


    /**
     * description ：跳转到上传页面
     * creation date: 2020/12/7
     * user : zhangtongju
     */
    private void intoUploadMaterialActivity(String path) {
        Intent intent = new Intent(mContext, UploadMaterialActivity.class);
        intent.putExtra("isFrom", 2);
        intent.putExtra("videoPath", path);
        mContext.startActivity(intent);
    }

    String tabName;

    /**
     * 设置换装收藏tab没有上传功能
     */
    public void setDressUPTabNameFavorites(String tabName) {
        this.tabName = tabName;
    }

}










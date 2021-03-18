package com.flyingeffects.com.adapter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.constans.UiStep;
import com.flyingeffects.com.enity.CommonNewsBean;
import com.flyingeffects.com.enity.DownVideoPath;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.manager.AdConfigs;
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
import com.nineton.ntadsdk.NTAdSDK;
import com.nineton.ntadsdk.manager.FeedAdManager;
import com.nineton.ntadsdk.utils.DeviceUtil;
import com.nineton.ntadsdk.utils.ScreenUtils;
import com.flyingeffects.com.utils.ToastUtil;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.MediaView;
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

import static com.nineton.ntadsdk.bean.FeedAdConfigBean.FeedAdResultBean.BAIDU_FEED_AD_EVENT;
import static com.nineton.ntadsdk.bean.FeedAdConfigBean.FeedAdResultBean.GDT_FEED_AD_EVENT;
import static com.nineton.ntadsdk.bean.FeedAdConfigBean.FeedAdResultBean.TT_FEED_AD_EVENT;
import static com.nineton.ntadsdk.bean.FeedAdConfigBean.FeedAdResultBean.TYPE_GDT_FEED_EXPRESS_AD;
import static com.nineton.ntadsdk.bean.FeedAdConfigBean.FeedAdResultBean.TYPE_TT_FEED_EXPRESS_AD;


/**
 * user :TongJu  ; email:jutongzhang@sina.com
 * time：2019/1/25
 * describe:首页适配
 **/
public class MainRecyclerAdapter extends BaseMultiItemQuickAdapter<new_fag_template_item, BaseViewHolder> {
    public static final int FROM_TEMPLATE_CODE = 0;
    public static final int FROM_BACK_CODE = 1;
    public static final int FROM_SEARCH_CODE = 2;
    public static final int FROM_DOWNLOAD_CODE = 3;
    public static final int FROM_DRESS_CODE = 4;

    public final static String TAG = "MainRecyclerAdapter";
    //0 模板  1 背景 2 搜索/我的收藏 3 表示背景模板下载 4 换装
    private int fromType;
    boolean isFromSearch;
    public FeedAdManager mAdManager;
    private NativeUnifiedADData mAdBean;


    public MainRecyclerAdapter(@Nullable List<new_fag_template_item> allData, int fromType, boolean isFromSearch, FeedAdManager mAdManager) {
        super(allData);
        this.fromType = fromType;
        this.isFromSearch = isFromSearch;
        this.mAdManager = mAdManager;
        addItemType(0, R.layout.item_home_normal);
        //      case BAIDU_FEED_AD_EVENT  case TT_FEED_AD_EVENT:
        addItemType(11, R.layout.item_news_right_image);
        //广点通
        addItemType(12, R.layout.item_gdt_news_right_image);
        //自渲染 TYPE_TT_FEED_EXPRESS_AD TYPE_GDT_FEED_EXPRESS_AD
        addItemType(13, R.layout.item_view_feed_express);
    }


    @Override
    protected void convert(final BaseViewHolder helper, final new_fag_template_item item) {
        int offset = helper.getLayoutPosition();
        LinearLayout ll_content_patents = helper.getView(R.id.ll_content_patents);
        LogUtil.d("OOM2", "getItemViewType=" + helper.getItemViewType());

        switch (helper.getItemViewType()) {
            case 0: {
                //默认样式，正常的模板
                Glide.with(mContext)
                        .load(item.getImage())
                        .apply(RequestOptions.bitmapTransform(new GlideRoundTransform(mContext, 5)))
                        .apply(RequestOptions.placeholderOf(R.mipmap.placeholder))
                        .into((ImageView) helper.getView(R.id.iv_cover));
                ImageView iv_show_author = helper.getView(R.id.iv_show_author);
                RelativeLayout constraintLayoutAddVideo = helper.getView(R.id.ConstraintLayout_addVideo);
                RelativeLayout ll_relative_2 = helper.getView(R.id.ll_relative_2);
                RelativeLayout add_image = helper.getView(R.id.add_image);
                LinearLayout ll_relative_1 = helper.getView(R.id.ll_relative_1);
                RelativeLayout ll_relative_0 = helper.getView(R.id.ll_relative_0);
                TextView tv_name = helper.getView(R.id.tv_name);
                tv_name.setText(item.getTitle());
                if (fromType == FROM_BACK_CODE) {
                    if (offset == 1) {
                        ll_relative_2.setVisibility(View.GONE);
                        ll_relative_1.setVisibility(View.VISIBLE);
                        ll_relative_0.setVisibility(View.VISIBLE);
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
                            showMessageDialog();
                        });
                    } else {
                        constraintLayoutAddVideo.setVisibility(View.GONE);
                    }
                    helper.setText(R.id.tv_name2, item.getAuth());
                    ImageView iv_show_author_template = helper.getView(R.id.iv_show_author_template);
                    Glide.with(mContext)
                            .load(item.getAuth_image())
                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                            .into(iv_show_author_template);
                    iv_show_author.setVisibility(View.GONE);
                    helper.setText(R.id.tv_zan_count, item.getPraise());
                    tv_name.setVisibility(View.VISIBLE);
                    ImageView iv_zan_state = helper.getView(R.id.iv_zan_state);
                    iv_zan_state.setImageResource(item.getIs_praise() != 0 ? R.mipmap.zan_clicked : R.mipmap.zan_unclicked);
                } else if (fromType == FROM_DOWNLOAD_CODE) {
                    //背景下载
                    if (offset == 0 && !isFromSearch) {
                        ll_relative_2.setVisibility(View.VISIBLE);
                        ll_relative_1.setVisibility(View.GONE);
                        ll_relative_0.setVisibility(View.GONE);
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
                    ImageView iv_show_author_template = helper.getView(R.id.iv_show_author_template);
                    Glide.with(mContext)
                            .load(item.getAuth_image())
                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                            .into(iv_show_author_template);
                    iv_show_author.setVisibility(View.GONE);
                    helper.setText(R.id.tv_zan_count, item.getPraise());
                    tv_name.setVisibility(View.VISIBLE);
                    ImageView iv_zan_state = helper.getView(R.id.iv_zan_state);
                    iv_zan_state.setImageResource(item.getIs_praise() != 0 ? R.mipmap.zan_clicked : R.mipmap.zan_unclicked);
                    iv_show_author.setVisibility(View.GONE);
                } else if (fromType == FROM_DRESS_CODE) {
                    //换装
                    if (offset == 1 && TextUtils.isEmpty(tabName)) {
                        add_image.setVisibility(View.VISIBLE);
                        constraintLayoutAddVideo.setVisibility(View.GONE);
                    } else {
                        add_image.setVisibility(View.GONE);
                    }
                    add_image.setOnClickListener(new View.OnClickListener() {
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
                    iv_show_author.setVisibility(View.GONE);
                    helper.setText(R.id.tv_zan_count, item.getPraise());
                    tv_name.setVisibility(View.VISIBLE);
                    ImageView ivZanState = helper.getView(R.id.iv_zan_state);
                    ivZanState.setImageResource(item.getIs_praise() != 0 ? R.mipmap.zan_clicked : R.mipmap.zan_unclicked);
                    iv_show_author.setVisibility(View.GONE);
                } else {
                    //模板
                    if (offset == FROM_BACK_CODE && fromType == FROM_TEMPLATE_CODE) {
                        ll_relative_1.setVisibility(View.VISIBLE);
                        ll_relative_0.setVisibility(View.VISIBLE);
                        ll_relative_2.setVisibility(View.GONE);
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
                            showMessageDialog();
                        });
                    } else {
                        constraintLayoutAddVideo.setVisibility(View.GONE);
                    }
                    helper.setText(R.id.tv_name2, item.getAuth());
                    ImageView iv_show_author_template = helper.getView(R.id.iv_show_author_template);
                    Glide.with(mContext)
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
                ImageView iv_zan_state = helper.getView(R.id.iv_zan_state);
                if (item.getIs_ad_recommend() == 1) {
                    iv_zan_state.setVisibility(View.GONE);
                    helper.setText(R.id.tv_zan_count, "");
                } else {
                    iv_zan_state.setVisibility(View.VISIBLE);
                }
                break;
            }
            case 11: {

                // 设置图片
                String imageUrl = item.getFeedAdResultBean().getImageUrl();
                if (!TextUtils.isEmpty(imageUrl)) {
                    // 视频
                    Glide.with(mContext).load(imageUrl).into((ImageView) helper.getView(R.id.item_news_hot_image));
                }
                if (item.getFeedAdResultBean().isShowCloseButton()) {
                    helper.getView(R.id.btnDisLike).setVisibility(View.VISIBLE);
                    helper.getView(R.id.btnDisLike).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mAdManager.registerCloseListener(item.getFeedAdResultBean().getEventType(), helper.getAdapterPosition());
                        }
                    });
                }
                if (item.getFeedAdResultBean().getEventType() == TT_FEED_AD_EVENT) {
                    //设置头条logo
                    Bitmap bitmap = item.getFeedAdResultBean().getFeedResultBean().getTtNativeExpressAd().getAdLogo();
                    if (bitmap != null) {
                        LinearLayout.LayoutParams adLogoLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        helper.getView(R.id.logo_iv).setLayoutParams(adLogoLp);
                        ((ImageView) helper.getView(R.id.logo_iv)).setImageBitmap(bitmap);
                        helper.getView(R.id.baidu_ad_bottom_ll).setVisibility(View.VISIBLE);
                        helper.getView(R.id.ad_text_iv).setVisibility(View.GONE);
                    } else {
                        helper.getView(R.id.baidu_ad_bottom_ll).setVisibility(View.GONE);
                    }
                } else if (item.getFeedAdResultBean().getEventType() == BAIDU_FEED_AD_EVENT) {
                    //设置百度logo文字和图标
                    if (!TextUtils.isEmpty(item.getFeedAdResultBean().getFeedResultBean().getBaiduNativeResponse().getAdLogoUrl()) && !TextUtils.isEmpty(item.getFeedAdResultBean().getFeedResultBean().getBaiduNativeResponse().getBaiduLogoUrl())) {
                        Glide.with(mContext).load(item.getFeedAdResultBean().getFeedResultBean().getBaiduNativeResponse().getAdLogoUrl()).into((ImageView) helper.getView(R.id.ad_text_iv));
                        Glide.with(mContext).load(item.getFeedAdResultBean().getFeedResultBean().getBaiduNativeResponse().getBaiduLogoUrl()).into((ImageView) helper.getView(R.id.logo_iv));
                        helper.getView(R.id.baidu_ad_bottom_ll).setVisibility(View.VISIBLE);
                    } else {
                        helper.getView(R.id.baidu_ad_bottom_ll).setVisibility(View.GONE);
                    }
                } else {
                    helper.getView(R.id.baidu_ad_bottom_ll).setVisibility(View.GONE);
                }
                break;


            }
            case 12: {

                String gdtImageUrl = item.getFeedAdResultBean().getImageUrl();
                helper.setText(R.id.tv_name,item.getFeedAdResultBean().getTitle());
                if (!TextUtils.isEmpty(gdtImageUrl)) {
                    try {
                        helper.getView(R.id.item_news_hot_image).setVisibility(View.VISIBLE);
                        Glide.with(mContext).load(gdtImageUrl).into((ImageView) helper.getView(R.id.item_news_hot_image));
                    } catch (Exception e) {
                        LogUtil.e(e.getMessage());
                    }
                }
                if (item.getFeedAdResultBean().isShowCloseButton()) {
                    helper.getView(R.id.btnDisLike).setVisibility(View.VISIBLE);
                    helper.getView(R.id.btnDisLike).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mAdManager.registerCloseListener(item.getFeedAdResultBean().getEventType(), helper.getAdapterPosition());
                        }
                    });
                }



                //logo 位置
                FrameLayout.LayoutParams lp01 = new FrameLayout.LayoutParams(ScreenUtils.dp2px(mContext, 28), ScreenUtils.dp2px(mContext, 10));
                lp01.setMargins(0, ScreenUtils.dp2px(NTAdSDK.getAppContext(), 271), 0, 0);

                NativeAdContainer rightImageContainer = helper.getView(R.id.item_news_sigle_image_ad);
                List<View> rightImageViews = new ArrayList<>();
                rightImageViews.add(helper.getView(R.id.item_news_sigle_image_ll));
                mAdBean = item.getFeedAdResultBean().getFeedResultBean().getGdtNativeUnifiedADData();
                mAdBean.bindAdToView(mContext, rightImageContainer, lp01, rightImageViews);
                if (mAdBean.getAdPatternType() == AdPatternType.NATIVE_VIDEO) {

                    helper.getView(R.id.item_news_hot_image).setVisibility(View.GONE);
                    helper.getView(R.id.fl_ad_feed_video).setVisibility(View.VISIBLE);
                    MediaView mediaView = helper.getView(R.id.mv_ad_gdt);
                    // 视频广告需对MediaView进行绑定，MediaView必须为容器mContainer的子View
                    // 视频素材加载完成，此时展示广告不会有进度条。
                    mAdBean.bindMediaView(mediaView, new VideoOption.Builder()
                                    .setAutoPlayMuted(true).setAutoPlayPolicy(VideoOption.AutoPlayPolicy.WIFI).build(),
                            // 视频相关回调
                            new NativeADMediaListener() {
                                @Override
                                public void onVideoInit() {
                                }

                                @Override
                                public void onVideoLoading() {
                                }

                                @Override
                                public void onVideoReady() {
                                }

                                @Override
                                public void onVideoLoaded(int videoDuration) {
                                }

                                @Override
                                public void onVideoStart() {
                                }

                                @Override
                                public void onVideoPause() {

                                }

                                @Override
                                public void onVideoResume() {
                                }

                                @Override
                                public void onVideoCompleted() {
                                }

                                @Override
                                public void onVideoError(AdError error) {
                                }

                                @Override
                                public void onVideoStop() {
                                }

                                @Override
                                public void onVideoClicked() {
                                }
                            });
                }
                break;


            }
            case 13: {
                ((FrameLayout) helper.getView(R.id.item_news_sigle_image_fl)).removeAllViews();
                if (null != item.getFeedAdResultBean().getAdView() && null != item.getFeedAdResultBean().getAdView().getParent()) {
                    ((ViewGroup) item.getFeedAdResultBean().getAdView().getParent()).removeAllViews();
                }
                ((FrameLayout) helper.getView(R.id.item_news_sigle_image_fl)).addView(item.getFeedAdResultBean().getAdView());
                break;
            }
            default:
                break;
        }

        if (item.getFeedAdResultBean() != null) {
            //根据类型注册广告点击事件
            switch (item.getFeedAdResultBean().getEventType()) {
                case BAIDU_FEED_AD_EVENT:
                    mAdManager.registerClickedListener(BAIDU_FEED_AD_EVENT, item.getFeedAdResultBean(), (ViewGroup) helper.getConvertView(), 0, null, null);
                    break;
                case GDT_FEED_AD_EVENT:
                    mAdManager.registerClickedListener(GDT_FEED_AD_EVENT, item.getFeedAdResultBean(), (ViewGroup) helper.getConvertView(), 0, null, null);
                    break;
                case TT_FEED_AD_EVENT:
                    List<View> clickViewList = new ArrayList<>();
                    clickViewList.add(helper.itemView);
                    ArrayList<View> images = new ArrayList<>();
                    images.add(helper.getView(R.id.item_news_hot_image));
                    mAdManager.registerClickedListener(TT_FEED_AD_EVENT, item.getFeedAdResultBean(), (ViewGroup) helper.getConvertView(), 0, images, clickViewList);
                    break;
                case TYPE_TT_FEED_EXPRESS_AD:
                    mAdManager.registerClickedListener(TYPE_TT_FEED_EXPRESS_AD, item.getFeedAdResultBean(), (ViewGroup) helper.getConvertView(), helper.getAdapterPosition(), null, null);
                    break;
                case TYPE_GDT_FEED_EXPRESS_AD:
                    mAdManager.registerClickedListener(TYPE_GDT_FEED_EXPRESS_AD, item.getFeedAdResultBean(), (ViewGroup) helper.getConvertView(), helper.getAdapterPosition(), null, null);
                    break;
                default:
                    break;
            }
        }


    }

    /**
     * 弹出dialog
     */
    private void showMessageDialog() {
        //复制到剪贴板
        ClipboardManager tvCopy = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        tvCopy.setPrimaryClip(ClipData.newPlainText(null, BaseConstans.getService_wxi()));
        StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "alert_wechat");
        //弹出dialog
        CommonMessageDialog.getBuilder(mContext)
                .setContentView(R.layout.dialog_common_message)
                .setAdStatus(CommonMessageDialog.AD_STATUS_MIDDLE)
                .setAdId(AdConfigs.AD_IMAGE_WX_DIALOG)
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










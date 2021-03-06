package com.flyingeffects.com.ui.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.commonlyModel.SaveAlbumPathModel;
import com.flyingeffects.com.commonlyModel.getVideoInfo;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.entity.BackgroundTemplateCollectionEvent;
import com.flyingeffects.com.entity.NewFragmentTemplateItem;
import com.flyingeffects.com.entity.SystemMessageDetailAllEnity;
import com.flyingeffects.com.entity.UserInfo;
import com.flyingeffects.com.entity.VideoInfo;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.AdConfigs;
import com.flyingeffects.com.manager.AdManager;
import com.flyingeffects.com.manager.BitmapManager;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.DownloadVideoManage;
import com.flyingeffects.com.manager.DownloadZipManager;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.manager.GifManager;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.manager.TTAdManagerHolder;
import com.flyingeffects.com.manager.ZipFileHelperManager;
import com.flyingeffects.com.manager.mediaManager;
import com.flyingeffects.com.ui.interfaces.model.PreviewUpAndDownMvpCallback;
import com.flyingeffects.com.ui.view.activity.DressUpPreviewActivity;
import com.flyingeffects.com.ui.view.activity.LoginActivity;
import com.flyingeffects.com.ui.view.activity.MemeKeepActivity;
import com.flyingeffects.com.ui.view.activity.ReportActivity;
import com.flyingeffects.com.ui.view.activity.TemplateAddStickerActivity;
import com.flyingeffects.com.ui.view.dialog.LoadingDialog;
import com.flyingeffects.com.utils.FileUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.NetworkUtils;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.utils.screenUtil;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shixing.sxve.ui.AlbumType;
import com.shixing.sxve.ui.view.WaitingDialog;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMMin;
import com.umeng.socialize.media.UMWeb;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;


public class PreviewUpAndDownMvpModel {
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    /**
     * 0 ???????????? 1 ?????? 2 ????????????
     */
    private static int sIsRefresh = 0;
    private PreviewUpAndDownMvpCallback callback;

    private Context context;
    private int selectPage = 1;
    private String mVideoFolder;
    private String mRunCatchFolder;
    private int perPageCount = 10;
    private SmartRefreshLayout smartRefreshLayout;
    private List<NewFragmentTemplateItem> allData;
    private String fromTo;
    private String category_id, tc_id;
    private TTAdNative mTTAdNative;
    private String soundFolder;
    private String toUserID;
    private String searchText = "";
    private boolean isCanLoadMore;

    public PreviewUpAndDownMvpModel(Context context, PreviewUpAndDownMvpCallback callback, List<NewFragmentTemplateItem> allData, int nowSelectPage, String fromTo, String category_id, String toUserID, String searchText, boolean isCanLoadMore, String tc_id) {
        this.context = context;
        this.isCanLoadMore = isCanLoadMore;
        this.selectPage = nowSelectPage;
        this.callback = callback;
        this.toUserID = toUserID;
        FileManager fileManager = new FileManager();
        mVideoFolder = fileManager.getFileCachePath(context, "downVideo");

        mRunCatchFolder = fileManager.getFileCachePath(context, "runCatch");
        this.allData = allData;
        this.searchText = searchText;
        this.fromTo = fromTo;
        this.category_id = category_id;
        this.tc_id = tc_id;
       mTTAdNative = TTAdManagerHolder.get().createAdNative(context);
//        //????????????????????????????????????read_phone_state,??????????????????imei?????????????????????????????????????????????
//        TTAdManagerHolder.get().requestPermissionIfNecessary(context);
        soundFolder = fileManager.getFileCachePath(context, "soundFolder");
    }

    public void initSmartRefreshLayout(SmartRefreshLayout smartRefreshLayout) {
        this.smartRefreshLayout = smartRefreshLayout;
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            isOnRefresh();
            sIsRefresh = 0;
            if (isCanLoadMore) {
                refreshLayout.setEnableLoadMore(true);
            }
            selectPage = 1;
            requestFagData();
        });
        smartRefreshLayout.setOnLoadMoreListener(refresh -> {
            //?????????requestMoreData ????????????

        });

        if (isCanLoadMore) {
            smartRefreshLayout.setEnableLoadMore(true);
        } else {
            smartRefreshLayout.setEnableRefresh(false);
            smartRefreshLayout.setEnableLoadMore(false);
        }
    }


    public void getBackgroundMusic(String videoPath) {
        mediaManager manager = new mediaManager(context);
        manager.splitMp4(videoPath, new File(soundFolder), new mediaManager.splitMp4Callback() {
            @Override
            public void splitSuccess(boolean isSuccess, String putPath) {
                if (isSuccess) {
                    callback.returnSpliteMusic(putPath, videoPath);
                } else {
                    callback.returnSpliteMusic("", videoPath);
                }

            }
        });
    }

    public void requestMoreData() {
        isOnLoadMore();
        sIsRefresh = 1;
        selectPage++;
        requestFagData();
    }


    public void requestTemplateDetail(String templateId) {
        LogUtil.d("OOM", "requestTemplateDetail");
        if (!TextUtils.isEmpty(templateId)) {
            HashMap<String, String> params = new HashMap<>();
            params.put("template_id", templateId);
            // ????????????
            Observable ob = Api.getDefault().templateLInfo(BaseConstans.getRequestHead(params));
            LogUtil.d("OOM", StringUtil.beanToJSONString(params));
            HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<NewFragmentTemplateItem>(context) {
                        @Override
                        protected void onSubError(String message) {
//                ToastUtil.showToast(message);
                            LogUtil.d("OOM", "requestTemplateDetail-error=" + message);
                        }

                        @Override
                        protected void onSubNext(NewFragmentTemplateItem data) {
                            callback.getTemplateLInfo(data);
                        }
                    }, "cacheKey", ActivityLifeCycleEvent.DESTROY,
                    lifecycleSubject, false, true, false);

        }
    }


    private LoadingDialog mLoadingDialog;
    private BottomSheetDialog bottomSheetDialog;

    private boolean nowHasCollect;
    private ImageView mIvCollect;


    private LoadingDialog buildLoadingDialog() {
        LoadingDialog dialog = LoadingDialog.getBuilder(context)
                .setHasAd(false)
                .setTitle("?????????...")
                .build();
        dialog.show();
        return dialog;
    }

    public void showBottomSheetDialog(String path, String imagePath, String id, NewFragmentTemplateItem fag_template_item, String fromTo) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.gaussianDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.preview_bottom_sheet_dialog, null);
        bottomSheetDialog.setContentView(view);
        LinearLayout ll_collect = view.findViewById(R.id.ll_collect);
        mIvCollect = view.findViewById(R.id.iv_collect);
        if (BaseConstans.hasLogin() && fag_template_item.getIs_collection() == 1) {
            nowHasCollect = true;
            //????????????
            mIvCollect.setImageResource(R.mipmap.new_version_collect_ed);
        } else {
            nowHasCollect = false;
            mIvCollect.setImageResource(R.mipmap.new_version_collect);
        }


        ll_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (BaseConstans.hasLogin()) {
                    callback.onclickCollect();
                } else {
                    ToastUtil.showToast(context.getResources().getString(R.string.need_login));
                }
            }
        });

        LinearLayout ivDownload = view.findViewById(R.id.ll_download);
//        if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.SPECIAL)) {
//            ivDownload.setVisibility(View.INVISIBLE);
//        }

        ivDownload.setOnClickListener(view12 -> {
            if (BaseConstans.hasLogin()) {
                if (fromTo.equals(FromToTemplate.ISTEMPLATE)) {
                    StatisticsEventAffair.getInstance().setFlag(context, "11_yj_save1");
                } else {
                    StatisticsEventAffair.getInstance().setFlag(context, "10_bj_csave1");
                }
                templateBehaviorStatistics(3, id);

                StatisticsEventAffair.getInstance().setFlag(context, "save_back_template");
                mLoadingDialog = buildLoadingDialog();
                LogUtil.d("OOM2", "needImagePath=" + fag_template_item.getImage());


                String image = fag_template_item.getImage();
                String pathType = GetPathTypeModel.getInstance().getMediaType(image);


                if (!TextUtils.isEmpty(path)) {
                    //???????????????
                    //??????????????????
                    downVideo(path, imagePath, id, true, false);
                    dismissDialog();
                } else {
                    if (AlbumType.isImage(pathType)) {
                        Observable.just(fag_template_item.getImage()).map(needImagePath -> BitmapManager.getInstance().GetBitmapForHttp(needImagePath)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Bitmap>() {
                            @Override
                            public void call(Bitmap bitmap) {
                                mLoadingDialog.dismiss();
                                LogUtil.d("OOM3", "??????bitmap");
                                String fileName = mRunCatchFolder + File.separator + UUID.randomUUID() + ".png";
                                BitmapManager.getInstance().saveBitmapToPath(bitmap, fileName, new BitmapManager.saveToFileCallback() {
                                    @Override
                                    public void isSuccess(boolean isSuccess) {
                                        saveToAlbum(fileName);
                                        if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
                                            AdManager.getInstance().showCpAd(context, AdConfigs.AD_PREVIEW_SCREEN_AD_ID);
                                        }
                                    }
                                });
                            }
                        });
                    } else {
                        //????????????gif
                        GifManager gifManager = new GifManager(context, new GifManager.downGifCallback() {
                            @Override
                            public void downSuccess(String path) {
                                ToastUtil.showToast("????????????");
                            }
                        });
                        gifManager.toDownGif(image);

                    }
                }


            } else {
                Intent intent = new Intent(context, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
            }
        });
        LinearLayout llFriendCircle = view.findViewById(R.id.ll_friend_circle);
        llFriendCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fromTo.equals(FromToTemplate.ISTEMPLATE)) {
                    StatisticsEventAffair.getInstance().setFlag(context, "11_yjj_WeChat");
                } else {
                    StatisticsEventAffair.getInstance().setFlag(context, "10_bj_WeChat");
                }
                templateBehaviorStatistics(2, id);

                UMImage image = new UMImage(context, fag_template_item.getImage());//????????????
                UMWeb web = new UMWeb(getShareWeiXinCircleText(fag_template_item.getId() + "")); //???????????? ??????????????????????????????http??????
                web.setTitle(BaseConstans.getminapp_share_title() + fag_template_item.getTitle());//??????
                web.setThumb(image);  //?????????
                new ShareAction((Activity) context).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                        .withMedia(web)
                        .setCallback(shareListener)
                        .share();
            }
        });


        //???????????????????????????
        LinearLayout llShareWx = view.findViewById(R.id.ll_share_wx);
        llShareWx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fromTo.equals(FromToTemplate.ISTEMPLATE)) {
                    StatisticsEventAffair.getInstance().setFlag(context, "11_yj_circle");
                } else {
                    StatisticsEventAffair.getInstance().setFlag(context, "10_bj_circle");
                }
                templateBehaviorStatistics(1, id);
                shareToApplet(fag_template_item);
            }
        });

        LinearLayout llReport = view.findViewById(R.id.ll_report);
        llReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fromTo.equals(FromToTemplate.ISTEMPLATE)) {
                    StatisticsEventAffair.getInstance().setFlag(context, "11_yj_Report");
                } else {
                    StatisticsEventAffair.getInstance().setFlag(context, "10_bj_Report");
                }
                Intent intent = new Intent(context, ReportActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            }
        });


        TextView tvCancel = view.findViewById(R.id.tv_cancle);
        tvCancel.setOnClickListener(view1 -> {
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        View parent = (View) view.getParent();     //????????????????????????  https://www.jianshu.com/p/38af0cf77352
        parent.setBackgroundResource(android.R.color.transparent);

        BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
        view.measure(0, 0);
        behavior.setPeekHeight(view.getMeasuredHeight());
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) parent.getLayoutParams();
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        parent.setLayoutParams(params);
        bottomSheetDialog.show();
    }


    /**
     * ????????????????????????
     *
     * @param type       1=????????????,2=?????????,3=???????????????,4=??????
     * @param templateId ??????ID
     */
    private void templateBehaviorStatistics(int type, String templateId) {
        if (!TextUtils.isEmpty(templateId)) {
            HashMap<String, String> params = new HashMap<>();
            params.put("template_id", templateId);
            params.put("type", String.valueOf(type));
            // ????????????
            Observable ob = Api.getDefault().templateBehaviorStatistics(BaseConstans.getRequestHead(params));
            HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(context) {
                @Override
                protected void onSubError(String message) {
                    LogUtil.d("OOM", "requestTemplateDetail-error=" + message);
                }

                @Override
                protected void onSubNext(Object data) {
                    LogUtil.d("OOM", "????????????????????????");
                }
            }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);

        }
    }


    /**
     * description ?????????????????????
     * creation date: 2020/7/1
     * user : zhangtongju
     */
    private void shareToApplet(NewFragmentTemplateItem fag_template_item) {
        UMImage image = new UMImage(context, fag_template_item.getImage());//????????????
        String url = "pages/background/background?path=detail&from_path=app&id=" + fag_template_item.getId();
        LogUtil.d("OOM", "?????????????????????" + url);
        UMMin umMin = new
                UMMin(url);
        umMin.setPath(url);
        umMin.setThumb(image);
        umMin.setUserName("gh_4161ca2837f7");
        umMin.setTitle(BaseConstans.getminapp_share_title() + fag_template_item.getTitle());
        new ShareAction((Activity) context)
                .withMedia(umMin)
                .setPlatform(SHARE_MEDIA.WEIXIN)
                .setCallback(shareListener).share();

    }


    private UMShareListener shareListener = new UMShareListener() {
        /**
         * @descrption ?????????????????????
         * @param platform ????????????
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {
        }

        /**
         * @descrption ?????????????????????
         * @param platform ????????????
         */
        @Override
        public void onResult(SHARE_MEDIA platform) {
//            ToastUtil.showToast("????????????");
        }

        /**
         * @descrption ?????????????????????
         * @param platform ????????????
         * @param t ????????????
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            String string = t.getMessage();
            String str = string.substring(string.lastIndexOf("???"));
            Toast.makeText(context, "??????" + str, Toast.LENGTH_LONG).show();
        }

        /**
         * @descrption ?????????????????????
         * @param platform ????????????
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(context, "?????????", Toast.LENGTH_LONG).show();
        }
    };

    /**
     * description ???
     * creation date: 2020/7/1
     * user : zhangtongju
     */
    private String getShareWeiXinCircleText(String id) {
        String str = "http://www.flyingeffect.com/index/index/share?id=" + id + "&";
        HashMap<String, String> params = BaseConstans.getRequestHead(new HashMap<>());
        String strParams = params.toString();
        strParams = strParams.replace("{", "");
        strParams = strParams.replace("}", "");
        strParams = strParams.replaceAll(",", "&");
        strParams = strParams.trim();
        return str + strParams;
    }


    private void dismissDialog() {
        try {
            if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
                bottomSheetDialog.dismiss();
                bottomSheetDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void requestAD() {
        //step3:????????????????????????AdSlot,??????????????????????????????
        float expressViewWidth = screenUtil.getScreenWidth((Activity) context);
        float expressViewHeight = screenUtil.getScreenHeight((Activity) context);
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(AdConfigs.POST_ID_CSJ_Feed)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920) //?????????????????????????????????
                .setExpressViewAcceptedSize(1080, 1920) //??????????????????view???size,??????dp
                .setAdCount(1) //?????????????????????1???3???
                .build();
        //step4:????????????,???????????????????????????????????????
        mTTAdNative.loadExpressDrawFeedAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
//                Log.d(TAG, message);
//                showToast(message);
                StatisticsEventAffair.getInstance().setFlag(context, "draw_ad_request_error");
                LogUtil.d("OOM", "loadFeedAd+code=" + code + ";message=" + message);
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.isEmpty()) {
                    LogUtil.d("OOM", "on FeedAdLoaded: ad is null!");
                    return;
                }
                StatisticsEventAffair.getInstance().setFlag(context, "draw_ad_request_success");

                LogUtil.d("OOM", "success" + ads.size());
//                for (int i = 0; i < 5; i++) {
//                    int random = (int) (Math.random() * 100);
//                    int index = random % videos.length;
//                    datas.add(new Item(TYPE_COMMON_ITEM, null, videos[index], imgs[index]));
//                }
                for (final TTNativeExpressAd ad : ads) {
                    //????????????????????????getAdView?????????
                    ad.setVideoAdListener(new TTNativeExpressAd.ExpressVideoAdListener() {
                        @Override
                        public void onVideoLoad() {
                            StatisticsEventAffair.getInstance().setFlag(context, "draw_ad_request_show");
                        }

                        @Override
                        public void onVideoError(int errorCode, int extraCode) {

                        }

                        @Override
                        public void onVideoAdStartPlay() {

                        }

                        @Override
                        public void onVideoAdPaused() {

                        }

                        @Override
                        public void onVideoAdContinuePlay() {

                        }

                        @Override
                        public void onProgressUpdate(long current, long duration) {

                        }

                        @Override
                        public void onVideoAdComplete() {

                        }

                        @Override
                        public void onClickRetry() {
                            Log.d("drawss", "onClickRetry!");
                        }

                    });
                    ad.setCanInterruptVideoPlay(true);
                    ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
                        @Override
                        public void onAdClicked(View view, int type) {

                        }

                        @Override
                        public void onAdShow(View view, int type) {

                        }

                        @Override
                        public void onRenderFail(View view, String msg, int code) {

                        }

                        @Override
                        public void onRenderSuccess(View view, float width, float height) {

                        }

                    });
                    ad.render();
                }
                callback.resultAd(ads);
            }
        });
    }


    /**
     * description ???
     * creation date: 2020/3/11
     * param : template_type  1????????? 2?????????
     * user : zhangtongju
     */
    private void requestFagData() {
        Observable ob = null;
        HashMap<String, String> params = new HashMap<>();
        LogUtil.d("templateId", "fromTo=" + fromTo);
        if (!TextUtils.isEmpty(category_id)) {
            params.put("category_id", category_id);
        }
        if (!TextUtils.isEmpty(tc_id) && Integer.parseInt(tc_id) >= 0) {
            params.put("tc_id", tc_id);
        }
        params.put("page", selectPage + "");
        params.put("pageSize", perPageCount + "");
        switch (fromTo) {
            case FromToTemplate.ISHOMEFROMBJ:
                params.put("to_user_id", BaseConstans.getUserId());
                params.put("type", "1");
                ob = Api.getDefault().uploadList(BaseConstans.getRequestHead(params));
                break;
            case FromToTemplate.ISHOMEMYLIKE:
                params.put("to_user_id", BaseConstans.getUserId());
                params.put("type", "2");
                ob = Api.getDefault().getMyProduction(BaseConstans.getRequestHead(params));
                break;
            case FromToTemplate.ISHOMEMYTEMPLATECOLLECT:
                params.put("template_type", "1");
                String str = StringUtil.beanToJSONString(params);
                LogUtil.d("OOM", "??????????????????------" + str);

                ob = Api.getDefault().collectionList(BaseConstans.getRequestHead(params));
                break;
            case FromToTemplate.ISMESSAGEMYPRODUCTION:
                params.put("to_user_id", toUserID);
                params.put("type", "1");
                ob = Api.getDefault().getMyProduction(BaseConstans.getRequestHead(params));

                break;
            case FromToTemplate.ISMESSAGEMYLIKE:
                params.put("to_user_id", toUserID);
                params.put("type", "2");
                ob = Api.getDefault().getMyProduction(BaseConstans.getRequestHead(params));
                break;
            case FromToTemplate.ISTEMPLATE:
            case FromToTemplate.TEMPLATESPECIAL:
                params.put("template_type", "1");
                ob = Api.getDefault().getTemplate(BaseConstans.getRequestHead(params));
                break;
            case FromToTemplate.ISBJ:
            case FromToTemplate.ISCHOOSEBJ:
                params.put("template_type", "2");
                ob = Api.getDefault().getTemplate(BaseConstans.getRequestHead(params));
                break;
            case FromToTemplate.ISBJCOLLECT:
                params.put("template_type", "2");
                params.put("token", BaseConstans.getUserToken());
                ob = Api.getDefault().collectionList(BaseConstans.getRequestHead(params));
                break;

            case FromToTemplate.ISSEARCHBJ:
                params.put("search", searchText);
                params.put("template_type", "2");
                ob = Api.getDefault().getTemplate(BaseConstans.getRequestHead(params));
                break;
            case FromToTemplate.ISSEARCHTEMPLATE:
                params.put("search", searchText);
                params.put("template_type", "1");
                ob = Api.getDefault().getTemplate(BaseConstans.getRequestHead(params));
                break;
            case FromToTemplate.CHOOSEBJ:
            case FromToTemplate.FACEGIF:
            case FromToTemplate.SPECIAL:
            case FromToTemplate.DRESSUP:
                params.put("template_type", "3");
                ob = Api.getDefault().materialList(BaseConstans.getRequestHead(params));
                break;
            default:
                params.put("template_type", "3");
                ob = Api.getDefault().materialList(BaseConstans.getRequestHead(params));
                break;
        }

        String str = StringUtil.beanToJSONString(params);
        LogUtil.d("OOM3", "?????????????????????------" + str);
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<NewFragmentTemplateItem>>(context) {
            @Override
            protected void onSubError(String message) {
                LogUtil.d("OOM3", "?????????????????????" + message);
                finishData();
                ToastUtil.showToast("?????????" + message);
            }

            @Override
            protected void onSubNext(List<NewFragmentTemplateItem> data) {
                String str = StringUtil.beanToJSONString(data);
                LogUtil.d("OOM3", "?????????????????????" + str);
                finishData();
                boolean isRefresh = false;
                if (sIsRefresh == 0) {
                    allData.clear();
                    isRefresh = true;
                } else if (sIsRefresh == 2) {
                    allData.clear();
                }

                if (data.size() < perPageCount) {
                    smartRefreshLayout.setEnableLoadMore(false);
                }
                List<NewFragmentTemplateItem> needData = getFiltration(data);
                allData.addAll(needData);
                callback.showNewData(allData, isRefresh);
            }
        }, "fagBjItem", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }

    public List<NewFragmentTemplateItem> getFiltration(List<NewFragmentTemplateItem> allData) {
        List<NewFragmentTemplateItem> needData = new ArrayList<>();
        for (int i = 0; i < allData.size(); i++) {
            NewFragmentTemplateItem item = allData.get(i);
            if (item.getIs_ad_recommend() == 0) {
                needData.add(item);
            }
        }
        return needData;
    }

    private void finishData() {
        smartRefreshLayout.finishRefresh();
        smartRefreshLayout.finishLoadMore();
    }

    public void isOnLoadMore() {

    }

    public void isOnRefresh() {

    }


    /**
     * description ???
     * creation date: 2020/3/24
     * param : template_type 1 muban  2??????
     * user : zhangtongju
     */
    public void collectTemplate(String templateId, String title, String template_type) {
        HashMap<String, String> params = new HashMap<>();
        params.put("template_id", templateId);
        params.put("token", BaseConstans.getUserToken());
        params.put("template_type", template_type);
        // ????????????
        Observable ob = Api.getDefault().newCollection(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(context) {
            @Override
            protected void onSubError(String message) {
                ToastUtil.showToast(message);
//                LogUtil.d("");
            }

            @Override
            protected void onSubNext(Object data) {
                String str = StringUtil.beanToJSONString(data);
                LogUtil.d("OOM", "collectTemplate=" + str);

                nowHasCollect = !nowHasCollect;
                callback.collectionResult(nowHasCollect);
                if (nowHasCollect) {
                    mIvCollect.setImageResource(R.mipmap.new_version_collect_ed);
                    templateBehaviorStatistics(4, templateId);
                } else {
                    mIvCollect.setImageResource(R.mipmap.new_version_collect);
                }
                EventBus.getDefault().post(new BackgroundTemplateCollectionEvent());
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);

    }


    /**
     * description ???
     * creation date: 2020/3/24
     * param : template_type 1 muban  2??????
     * user : zhangtongju
     */
    public void zanTemplate(String templateId, String title, String templateType) {
        HashMap<String, String> params = new HashMap<>();
        params.put("template_id", templateId);
//        params.put("token", BaseConstans.GetUserToken());
        params.put("type", templateType);
        // ????????????
        Observable ob = Api.getDefault().addPraise(BaseConstans.getRequestHead(params));

        LogUtil.d("OOM", StringUtil.beanToJSONString(params));

        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(context) {
            @Override
            protected void onSubError(String message) {
                ToastUtil.showToast(message);
//                LogUtil.d("");
            }

            @Override
            protected void onSubNext(Object data) {
                String str = StringUtil.beanToJSONString(data);
                LogUtil.d("OOM", "collectTemplate=" + str);
                callback.zanResult();
            }

        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }

    public void requestUserInfo() {
        HashMap<String, String> params = new HashMap<>();
        params.put("to_user_id", BaseConstans.getUserId());
        // ????????????
        Observable ob = Api.getDefault().getOtherUserinfo(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<UserInfo>(context) {
            @Override
            protected void onSubError(String message) {
                BaseConstans.setUserToken("");
                callback.hasLogin(false);
            }

            @Override
            protected void onSubNext(UserInfo data) {
                callback.hasLogin(true);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }

    public void downVideo(String path, String imagePath, String id, boolean keepAlbum, boolean isFromAgainChooseBj) {
        String videoName = mVideoFolder + File.separator + id + "synthetic.mp4";
        File file = new File(videoName);
        if (file.exists()) {
            if (mLoadingDialog != null) {
                mLoadingDialog.dismiss();
            }
            if (!keepAlbum) {
                //??????????????????????????????path
                callback.downVideoSuccess(videoName, imagePath);
            } else {
                WaitingDialog.closeProgressDialog();
                saveToAlbum(videoName);
                if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
                    AdManager.getInstance().showCpAd(context, AdConfigs.AD_SCREEN_FOR_DOWNLOAD);
                }
            }
            return;
        }

        if (mLoadingDialog == null) {
            LogUtil.d("OOM", "downProgressDialog != null");
            mLoadingDialog = buildLoadingDialog();
        }
        LogUtil.d("OOM", "???????????????");

        Observable.just(path).subscribeOn(Schedulers.io()).subscribe(s -> {
            DownloadVideoManage manage = new DownloadVideoManage(isSuccess -> Observable.just(videoName).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
                @Override
                public void call(String s1) {
                    VideoInfo info = getVideoInfo.getInstance().getRingDuring(s1);
                    videoCutDurationForVideoOneDo.getInstance().cutVideoForDrawPadAllExecute2(context, false, info.getDuration(), videoName, 0, new videoCutDurationForVideoOneDo.isSuccess() {
                        @Override
                        public void progresss(int progress) {
                            LogUtil.d("oom", "????????????????????????????????????=" + progress);
                            if (mLoadingDialog != null) {
                                if (isFromAgainChooseBj) {
                                    mLoadingDialog.setTitleStr("???????????????");
                                } else {
                                    mLoadingDialog.setTitleStr("?????????");
                                }
                                mLoadingDialog.setProgress(progress);
                            }
                        }

                        @Override
                        public void isSuccess(boolean isSuccess, String path1) {
                            if (mLoadingDialog != null) {
                                mLoadingDialog.dismiss();
                                mLoadingDialog = null;
                            }
                            if (!keepAlbum) {
                                callback.downVideoSuccess(path1, imagePath);//????????????????????????
                            } else {
                                WaitingDialog.closeProgressDialog();
                                saveToAlbum(path1);
                                if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
                                    AdManager.getInstance().showCpAd(context, AdConfigs.AD_SCREEN_FOR_DOWNLOAD);
                                }
                            }
                        }
                    });
                }
            }));
            manage.downloadVideo(path, videoName);
        });
    }


    private void saveToAlbum(String path) {
        String albumPath;
        String pathType = GetPathTypeModel.getInstance().getMediaType(path);
        if (AlbumType.isImage(pathType)) {
            albumPath = SaveAlbumPathModel.getInstance().getKeepOutputForImage();
        } else if (AlbumType.isVideo(pathType)) {
            albumPath = SaveAlbumPathModel.getInstance().getKeepOutput();
        } else {
            albumPath = SaveAlbumPathModel.getInstance().getKeepOutputForGif();
        }
        try {
            FileUtil.copyFile(new File(path), albumPath);
            albumBroadcast(albumPath);
            showKeepSuccessDialog(albumPath);
            dismissDialog();
        } catch (IOException e) {
            LogUtil.d("OOM", e.getMessage());
            e.printStackTrace();
        }
    }

    private void showKeepSuccessDialog(String path) {
        if (!DoubleClick.getInstance().isFastDoubleClick()) {
            AlertDialog.Builder builder = new AlertDialog.Builder( //????????????
                    new ContextThemeWrapper(context, R.style.Theme_Transparent));
            builder.setTitle(R.string.notification);
            builder.setMessage("????????????????????????,?????????????????????\n" + "???" + path + context.getString(R.string.folder) + "???");
            builder.setNegativeButton(context.getString(R.string.got_it), (dialog, which) -> dialog.dismiss());
            builder.setCancelable(true);
            Dialog dialog = builder.show();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }


    /**
     * description ?????????????????????
     * date: ???2019/8/16 14:24
     * author: ????????? @?????? jutongzhang@sina.com
     */
    private void albumBroadcast(String outputFile) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(new File(outputFile)));
        context.sendBroadcast(intent);
    }

    public void prepareDownZip(String url, String zipPid) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            readyDown(zipPid, url);
        } else {
            ToastUtil.showToast("?????????????????????");
        }
    }

    private File mFolder;
    private int mProgress;
    private boolean isDownZipUrl = false;

    private void readyDown(String zipPid, String downZipUrl) {
        LogUtil.d("onVideoAdError", "getPermission");
        mFolder = context.getExternalFilesDir("dynamic/" + zipPid);
        if (mFolder != null) {
            String folderPath = mFolder.getParent();
            if (!isDownZipUrl) {
                if (mFolder == null || mFolder.list().length == 0) {
                    LogUtil.d("searchActivity", "????????????");
                    downZip(downZipUrl, folderPath);
                    mProgress = 0;
                    showMakeProgress();
                } else {
                    intoTemplateActivity(mFolder.getPath());
                }
            } else {
                ToastUtil.showToast("???????????????????????????");
            }
        } else {
            ToastUtil.showToast("?????????sd???");
        }
    }


    private void showMakeProgress() {
        callback.showDownProgress(mProgress);
    }

    private void intoTemplateActivity(String filePath) {
        callback.getTemplateFileSuccess(filePath);
    }


    private void downZip(String loadUrl, String path) {
        mProgress = 0;
        if (!TextUtils.isEmpty(loadUrl)) {
            new Thread() {
                @Override
                public void run() {
                    isDownZipUrl = true;
                    try {
                        DownloadZipManager.getInstance().getFileFromServer(loadUrl, path, (progress, isSucceed, zipPath) -> {
                            if (!isSucceed) {
                                LogUtil.d("onVideoAdError", "progress=" + progress);
                                mProgress = progress;
                                showMakeProgress();
                            } else {
                                showMakeProgress();
                                LogUtil.d("onVideoAdError", "????????????");
                                isDownZipUrl = false;
                                //???????????????????????????
                                File file = new File(zipPath);
                                try {
                                    ZipFileHelperManager.upZipFile(file, path, path1 -> {
                                        if (file.exists()) { //???????????????
                                            file.delete();
                                        }
//                                        videoPause();
                                        mProgress = 100;
                                        showMakeProgress();
                                        intoTemplateActivity(path1);
                                    });
                                } catch (IOException e) {
                                    LogUtil.d("onVideoAdError", "Exception=" + e.getMessage());
                                    e.printStackTrace();

                                }
                            }
                        });

                    } catch (Exception e) {
                        isDownZipUrl = false;
                        Observable.just(e).subscribeOn(AndroidSchedulers.mainThread()).subscribe(e1 -> new Handler().post(() -> ToastUtil.showToast("????????????????????????")));
                        LogUtil.d("onVideoAdError", "Exception=" + e.getMessage());
                        callback.showDownProgress(100);
                    }
                    super.run();
                }
            }.start();
        } else {
            ToastUtil.showToast("??????zip??????");
        }
    }


    /**
     * description ?????????
     * creation date: 2020/12/3
     * user : zhangtongju
     */
    public void toDressUp(String path, String templateId, String templateTitle) {
        Observable.just(0).observeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
            LogUtil.d("OOM3", "toDressUp");
            DressUpModel dressUpModel = new DressUpModel(context, new DressUpModel.DressUpCallback() {
                @Override
                public void isSuccess(List<String> paths) {
                    LogUtil.d("OOM3", "?????????????????????");
                    if (paths != null) {
                        Intent intent = new Intent(context, DressUpPreviewActivity.class);
                        intent.putExtra("url", paths.get(0));
                        intent.putExtra("template_id", templateId);
                        intent.putExtra("localImage", path);
                        intent.putExtra("templateTitle", templateTitle);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        context.startActivity(intent);
                    }
                }
            }, false);
            dressUpModel.toDressUp(path, templateId);
        });
    }


    /**
     * description ????????????????????????
     * creation date: 2020/12/8
     * user : zhangtongju
     */
    public void GetDressUpPath(List<String> paths) {
        callback.getDressUpPathResult(paths);
    }


    /**
     * description ???????????????????????????
     * type 1=??????????????????,2=??????????????????3=??????????????????,
     * creation date: 2020/8/6
     * user : zhangtongju
     */
    public void requestMessageStatistics(String type, String message_id, String template_id) {
        HashMap<String, String> params = new HashMap<>();
        params.put("template_id", template_id);
        params.put("type", type);
        params.put("message_id", message_id);
        Observable ob = Api.getDefault().addTimes(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<SystemMessageDetailAllEnity>(context) {
            @Override
            protected void onSubError(String message) {
            }

            @Override
            protected void onSubNext(SystemMessageDetailAllEnity AllData) {

            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }


    /**
     * description ????????????????????????????????????????????????
     * paths ???????????????
     * api_type??????????????????
     * creation date: 2021/4/19
     * user : zhangtongju
     */
    public void ToDressUpSpecial(List<String> paths, int api_type, String templateId, String title, String templateType) {
        Observable.just(0).observeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
            LogUtil.d("OOM3", "toDressUp");
            DressUpSpecialModel dressUpModel = new DressUpSpecialModel(context, url -> {
                LogUtil.d("OOM3", "DressUpSpecialModel=" + url);
                if (!TextUtils.isEmpty(url)) {
                    if (url.contains("mp4")) {
                        //?????????????????????gif ??????
                        Intent intent = new Intent(context, MemeKeepActivity.class);
                        intent.putExtra("templateType", templateType);
                        intent.putExtra("videoPath", url);
                        intent.putExtra("title", title);
                        intent.putExtra("templateId", templateId);
                        intent.putExtra("IsFrom", fromTo);
                        context.startActivity(intent);

                    } else {
                        //??????????????????????????????
                        Intent intent = new Intent(context, DressUpPreviewActivity.class);
                        intent.putExtra("url", url);
                        intent.putExtra("template_id", templateId);
                        intent.putExtra("localImage", url);
                        intent.putExtra("isSpecial", true);
                        intent.putExtra("templateTitle", "");
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        context.startActivity(intent);
                    }
                }

            }, templateId);
            dressUpModel.toDressUp(paths, api_type);
        });

    }


    public void ToTemplateAddStickerActivity(List<String> strToList1, String templateName, String templateId, int api_type, String templateType) {

        DressUpSpecialModel dressUpModel = new DressUpSpecialModel(context, url -> {
            Intent intent = new Intent(context, TemplateAddStickerActivity.class);
            intent.putExtra("templateType", templateType);
            intent.putExtra("videoPath", url);
            intent.putExtra("title", templateName);
            intent.putExtra("templateId", templateId);
            intent.putExtra("IsFrom", fromTo);
            context.startActivity(intent);
        }, templateId);
        dressUpModel.toDressUp(strToList1, api_type);
    }


}

package com.flyingeffects.com.ui.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.commonlyModel.SaveAlbumPathModel;
import com.flyingeffects.com.commonlyModel.getVideoInfo;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.UserInfo;
import com.flyingeffects.com.enity.VideoInfo;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.AdConfigs;
import com.flyingeffects.com.manager.AdManager;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.DownloadVideoManage;
import com.flyingeffects.com.manager.DownloadZipManager;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.manager.TTAdManagerHolder;
import com.flyingeffects.com.manager.ZipFileHelperManager;
import com.flyingeffects.com.manager.mediaManager;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.model.PreviewUpAndDownMvpCallback;
import com.flyingeffects.com.ui.view.activity.DressUpPreviewActivity;
import com.flyingeffects.com.ui.view.activity.ReportActivity;
import com.flyingeffects.com.utils.FileUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.NetworkUtils;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.utils.screenUtil;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shixing.sxve.ui.view.WaitingDialog;
import com.shixing.sxve.ui.view.WaitingDialog_progress;
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

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;


public class PreviewUpAndDownMvpModel {
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    /**
     * 0 下拉刷新 1 加载 2 更新数据
     */
    private static int sIsRefresh = 0;
    private PreviewUpAndDownMvpCallback callback;
    private Context context;
    private int selectPage = 1;
    private String mVideoFolder;
    private int perPageCount = 10;
    private SmartRefreshLayout smartRefreshLayout;
    private List<new_fag_template_item> allData;
    private String fromTo;
    private String category_id, tc_id;
    private TTAdNative mTTAdNative;
    private String soundFolder;
    private String toUserID;
    private String searchText="";
    private boolean isCanLoadMore;

    public PreviewUpAndDownMvpModel(Context context, PreviewUpAndDownMvpCallback callback, List<new_fag_template_item> allData, int nowSelectPage, String fromTo, String category_id, String toUserID, String searchText, boolean isCanLoadMore, String tc_id) {
        this.context = context;
        this.isCanLoadMore = isCanLoadMore;
        this.selectPage = nowSelectPage;
        this.callback = callback;
        this.toUserID = toUserID;
        FileManager fileManager = new FileManager();
        mVideoFolder = fileManager.getFileCachePath(context, "downVideo");
        this.allData = allData;
        this.searchText = searchText;
        this.fromTo = fromTo;
        this.category_id = category_id;
        this.tc_id = tc_id;
        mTTAdNative = TTAdManagerHolder.get().createAdNative(context);
        //在合适的时机申请权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题
        TTAdManagerHolder.get().requestPermissionIfNecessary(context);
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
            //被方法requestMoreData 所替换了

        });

        if (isCanLoadMore) {
            smartRefreshLayout.setEnableLoadMore(true);
        } else {
            smartRefreshLayout.setEnableRefresh(false);
            smartRefreshLayout.setEnableLoadMore(false);
        }
    }


    public void GetBackgroundMusic(String videoPath) {
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
            // 启动时间
            Observable ob = Api.getDefault().templateLInfo(BaseConstans.getRequestHead(params));
            LogUtil.d("OOM", StringUtil.beanToJSONString(params));
            HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<new_fag_template_item>(context) {
                @Override
                protected void _onError(String message) {
//                ToastUtil.showToast(message);
                    LogUtil.d("OOM", "requestTemplateDetail-error=" + message);
                }

                @Override
                protected void _onNext(new_fag_template_item data) {
                    callback.getTemplateLInfo(data);
                }
            }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);

        }
    }


    private WaitingDialog_progress downProgressDialog;
    private BottomSheetDialog bottomSheetDialog;

    private boolean nowHasCollect;
    private ImageView iv_collect;

    public void showBottomSheetDialog(String path, String imagePath, String id, new_fag_template_item fag_template_item) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.gaussianDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.preview_bottom_sheet_dialog, null);
        bottomSheetDialog.setContentView(view);
        LinearLayout ll_collect = view.findViewById(R.id.ll_collect);
        iv_collect = view.findViewById(R.id.iv_collect);
        if (BaseConstans.hasLogin() && fag_template_item.getIs_collection() == 1) {
            nowHasCollect = true;
            //表示收藏
            iv_collect.setImageResource(R.mipmap.new_version_collect_ed);
        } else {
            nowHasCollect = false;
            iv_collect.setImageResource(R.mipmap.new_version_collect);
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


        LinearLayout iv_download = view.findViewById(R.id.ll_download);
        iv_download.setOnClickListener(view12 -> {

            if (fromTo.equals(FromToTemplate.ISTEMPLATE)) {
                statisticsEventAffair.getInstance().setFlag(context, "11_yj_save1");
            } else {
                statisticsEventAffair.getInstance().setFlag(context, "10_bj_csave1");
            }
            templateBehaviorStatistics(3, id);

            statisticsEventAffair.getInstance().setFlag(context, "save_back_template");
            downProgressDialog = new WaitingDialog_progress(context);
            downProgressDialog.openProgressDialog();
            DownVideo(path, imagePath, id, true, false);
            dismissDialog();
        });
        LinearLayout ll_friend_circle = view.findViewById(R.id.ll_friend_circle);
        ll_friend_circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fromTo.equals(FromToTemplate.ISTEMPLATE)) {
                    statisticsEventAffair.getInstance().setFlag(context, "11_yjj_WeChat");
                } else {
                    statisticsEventAffair.getInstance().setFlag(context, "10_bj_WeChat");
                }
                templateBehaviorStatistics(2, id);

                UMImage image = new UMImage(context, fag_template_item.getImage());//分享图标
                UMWeb web = new UMWeb(getShareWeiXinCircleText(fag_template_item.getId() + "")); //切记切记 这里分享的链接必须是http开头
                web.setTitle(BaseConstans.getminapp_share_title() + fag_template_item.getTitle());//标题
                web.setThumb(image);  //缩略图
                new ShareAction((Activity) context).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                        .withMedia(web)
                        .setCallback(shareListener)
                        .share();
            }
        });


        //分享小程序飞给好友
        LinearLayout ll_share_wx = view.findViewById(R.id.ll_share_wx);
        ll_share_wx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fromTo.equals(FromToTemplate.ISTEMPLATE)) {
                    statisticsEventAffair.getInstance().setFlag(context, "11_yj_circle");
                } else {
                    statisticsEventAffair.getInstance().setFlag(context, "10_bj_circle");
                }
                templateBehaviorStatistics(1, id);
                shareToApplet(fag_template_item);
            }
        });

        LinearLayout ll_report = view.findViewById(R.id.ll_report);
        ll_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fromTo.equals(FromToTemplate.ISTEMPLATE)) {
                    statisticsEventAffair.getInstance().setFlag(context, "11_yj_Report");
                } else {
                    statisticsEventAffair.getInstance().setFlag(context, "10_bj_Report");
                }


                Intent intent = new Intent(context, ReportActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            }
        });


        TextView tv_cancle = view.findViewById(R.id.tv_cancle);
        tv_cancle.setOnClickListener(view1 -> {
            bottomSheetDialog.dismiss();


        });
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        View parent = (View) view.getParent();     //处理高度显示完全  https://www.jianshu.com/p/38af0cf77352
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
     * 模板操作行为统计
     *
     * @param type       1=微信好友,2=朋友圈,3=保存到本地,4=其他
     * @param templateId 模板ID
     */
    private void templateBehaviorStatistics(int type, String templateId) {
        if (!TextUtils.isEmpty(templateId)) {
            HashMap<String, String> params = new HashMap<>();
            params.put("template_id", templateId);
            params.put("type", String.valueOf(type));
            // 启动时间
            Observable ob = Api.getDefault().templateBehaviorStatistics(BaseConstans.getRequestHead(params));
            HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(context) {
                @Override
                protected void _onError(String message) {
                    LogUtil.d("OOM", "requestTemplateDetail-error=" + message);
                }

                @Override
                protected void _onNext(Object data) {
                    LogUtil.d("OOM", "行为统计上传成功");
                }
            }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);

        }
    }


    /**
     * description ：分享到小程序
     * creation date: 2020/7/1
     * user : zhangtongju
     */
    private void shareToApplet(new_fag_template_item fag_template_item) {
        UMImage image = new UMImage(context, fag_template_item.getImage());//分享图标
        String url = "pages/background/background?path=detail&from_path=app&id=" + fag_template_item.getId();
        LogUtil.d("OOM", "小程序的地址为" + url);
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
         * @descrption 分享开始的回调
         * @param platform 平台类型
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {
        }

        /**
         * @descrption 分享成功的回调
         * @param platform 平台类型
         */
        @Override
        public void onResult(SHARE_MEDIA platform) {
//            ToastUtil.showToast("分享成功");
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(context, "失败" + t.getMessage(), Toast.LENGTH_LONG).show();
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(context, "取消了", Toast.LENGTH_LONG).show();
        }
    };

    /**
     * description ：
     * creation date: 2020/7/1
     * user : zhangtongju
     */
    private String getShareWeiXinCircleText(String id) {
        String str = "http://www.flyingeffect.com/index/index/share?id=" + id + "&";
        HashMap params = BaseConstans.getRequestHead(new HashMap<>());
        String str_params = params.toString();
        str_params = str_params.replace("{", "");
        str_params = str_params.replace("}", "");
        str_params = str_params.replaceAll(",", "&");
        str_params = str_params.trim();
        return str + str_params;
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
        //step3:创建广告请求参数AdSlot,具体参数含义参考文档
        float expressViewWidth = screenUtil.getScreenWidth((Activity) context);
        float expressViewHeight = screenUtil.getScreenHeight((Activity) context);
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(AdConfigs.POST_ID_CSJ_Feed)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920) //符合广告场景的广告尺寸
                .setExpressViewAcceptedSize(1080, 1920) //期望模板广告view的size,单位dp
                .setAdCount(1) //请求广告数量为1到3条
                .build();
        //step4:请求广告,对请求回调的广告作渲染处理
        mTTAdNative.loadExpressDrawFeedAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
//                Log.d(TAG, message);
//                showToast(message);
                statisticsEventAffair.getInstance().setFlag(context, "draw_ad_request_error");
                LogUtil.d("OOM", "loadFeedAd+code=" + code + ";message=" + message);
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.isEmpty()) {
                    LogUtil.d("OOM", "on FeedAdLoaded: ad is null!");
                    return;
                }
                statisticsEventAffair.getInstance().setFlag(context, "draw_ad_request_success");

                LogUtil.d("OOM", "success" + ads.size());
//                for (int i = 0; i < 5; i++) {
//                    int random = (int) (Math.random() * 100);
//                    int index = random % videos.length;
//                    datas.add(new Item(TYPE_COMMON_ITEM, null, videos[index], imgs[index]));
//                }
                for (final TTNativeExpressAd ad : ads) {
                    //点击监听器必须在getAdView之前调
                    ad.setVideoAdListener(new TTNativeExpressAd.ExpressVideoAdListener() {
                        @Override
                        public void onVideoLoad() {
                            statisticsEventAffair.getInstance().setFlag(context, "draw_ad_request_show");
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
     * description ：
     * creation date: 2020/3/11
     * param : template_type  1是模板 2是背景
     * user : zhangtongju
     */
    private void requestFagData() {
        Observable ob = null;
        HashMap<String, String> params = new HashMap<>();
        LogUtil.d("templateId", "templateId=" + category_id);
        if (!TextUtils.isEmpty(category_id)) {
            params.put("category_id", category_id);
        }
        if (!TextUtils.isEmpty(tc_id)&&Integer.parseInt(tc_id) >= 0) {
            params.put("tc_id", tc_id);
        }
        params.put("page", selectPage + "");
        params.put("pageSize", perPageCount + "");
        switch (fromTo) {
            case FromToTemplate.ISHOMEFROMBJ:
                params.put("to_user_id", BaseConstans.GetUserId());
                params.put("type", "1");
                ob = Api.getDefault().uploadList(BaseConstans.getRequestHead(params));
                break;
            case FromToTemplate.ISHOMEMYLIKE:
                params.put("to_user_id", BaseConstans.GetUserId());
                params.put("type", "2");
                ob = Api.getDefault().getMyProduction(BaseConstans.getRequestHead(params));
                break;
            case FromToTemplate.ISHOMEMYTEMPLATECOLLECT:
                params.put("template_type", "1");
                String str = StringUtil.beanToJSONString(params);
                LogUtil.d("OOM", "请求的参数为------" + str);

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
                params.put("token", BaseConstans.GetUserToken());
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

            case FromToTemplate.DRESSUP:
                params.put("template_type", "3");
                ob = Api.getDefault().getTemplate(BaseConstans.getRequestHead(params));
                break;


            default:
                break;
        }

        String str = StringUtil.beanToJSONString(params);
        LogUtil.d("OOM3", "总请求的参数为------" + str);
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<new_fag_template_item>>(context) {
            @Override
            protected void _onError(String message) {
                LogUtil.d("OOM3", "下一页数据请求" + message);
                finishData();
                ToastUtil.showToast("错误为" + message);
            }

            @Override
            protected void _onNext(List<new_fag_template_item> data) {
                String str = StringUtil.beanToJSONString(data);
                LogUtil.d("OOM3", "下一页数据请求" + str);
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
                List<new_fag_template_item> needData = getFiltration(data);
                allData.addAll(needData);
                callback.showNewData(allData, isRefresh);
            }
        }, "fagBjItem", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }

    public List<new_fag_template_item> getFiltration(List<new_fag_template_item> allData) {
        List<new_fag_template_item> needData = new ArrayList<>();
        for (int i = 0; i < allData.size(); i++) {
            new_fag_template_item item = allData.get(i);
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
     * description ：
     * creation date: 2020/3/24
     * param : template_type 1 muban  2背景
     * user : zhangtongju
     */
    public void collectTemplate(String templateId, String title, String template_type) {
        HashMap<String, String> params = new HashMap<>();
        params.put("template_id", templateId);
        params.put("token", BaseConstans.GetUserToken());
        params.put("template_type", template_type);
        // 启动时间
        Observable ob = Api.getDefault().newCollection(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(context) {
            @Override
            protected void _onError(String message) {
                ToastUtil.showToast(message);
//                LogUtil.d("");
            }

            @Override
            protected void _onNext(Object data) {
                String str = StringUtil.beanToJSONString(data);
                LogUtil.d("OOM", "collectTemplate=" + str);

                nowHasCollect = !nowHasCollect;
                callback.collectionResult(nowHasCollect);
                if (nowHasCollect) {
                    iv_collect.setImageResource(R.mipmap.new_version_collect_ed);
                    templateBehaviorStatistics(4, templateId);
                } else {
                    iv_collect.setImageResource(R.mipmap.new_version_collect);
                }

            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);

    }


    /**
     * description ：
     * creation date: 2020/3/24
     * param : template_type 1 muban  2背景
     * user : zhangtongju
     */
    public void ZanTemplate(String templateId, String title, String template_type) {
        HashMap<String, String> params = new HashMap<>();
        params.put("template_id", templateId);
//        params.put("token", BaseConstans.GetUserToken());
        params.put("type", template_type);
        // 启动时间
        Observable ob = Api.getDefault().addPraise(BaseConstans.getRequestHead(params));

        LogUtil.d("OOM", StringUtil.beanToJSONString(params));

        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(context) {
            @Override
            protected void _onError(String message) {
                ToastUtil.showToast(message);
//                LogUtil.d("");
            }

            @Override
            protected void _onNext(Object data) {
                String str = StringUtil.beanToJSONString(data);
                LogUtil.d("OOM", "collectTemplate=" + str);
                callback.ZanResult();
            }

        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }

    public void requestUserInfo() {
        HashMap<String, String> params = new HashMap<>();
        params.put("to_user_id", BaseConstans.GetUserId());
        // 启动时间
        Observable ob = Api.getDefault().getOtherUserinfo(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<UserInfo>(context) {
            @Override
            protected void _onError(String message) {
                BaseConstans.SetUserToken("");
                callback.hasLogin(false);
            }

            @Override
            protected void _onNext(UserInfo data) {
                callback.hasLogin(true);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }


    public void DownVideo(String path, String imagePath, String id, boolean keepAlbum, boolean isFromAgainChooseBj) {
        String videoName = mVideoFolder + File.separator + id + "synthetic.mp4";
        File file = new File(videoName);
        if (file.exists()) {
            if (downProgressDialog != null) {
                downProgressDialog.closePragressDialog();
            }
            if (!keepAlbum) {
                //文件已存在，直接回传path
                callback.downVideoSuccess(videoName, imagePath);
            } else {
                WaitingDialog.closePragressDialog();
                saveToAlbum(videoName);
                if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
                    AdManager.getInstance().showCpAd(context, AdConfigs.AD_SCREEN_FOR_DOWNLOAD);
                }
            }
            return;
        }

        if (downProgressDialog == null) {
            LogUtil.d("OOM", "downProgressDialog != null");
            downProgressDialog = new WaitingDialog_progress(context);
            downProgressDialog.openProgressDialog();
        }

        Observable.just(path).subscribeOn(Schedulers.io()).subscribe(s -> {
            DownloadVideoManage manage = new DownloadVideoManage(isSuccess -> Observable.just(videoName).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
                @Override
                public void call(String s1) {
                    VideoInfo info = getVideoInfo.getInstance().getRingDuring(s1);
                    videoCutDurationForVideoOneDo.getInstance().CutVideoForDrawPadAllExecute2(context, false, info.getDuration(), videoName, 0, new videoCutDurationForVideoOneDo.isSuccess() {
                        @Override
                        public void progresss(int progress) {
                            LogUtil.d("oom", "下载时候后重新裁剪进度为=" + progress);
                            if (downProgressDialog != null) {
                                if (isFromAgainChooseBj) {
                                    downProgressDialog.setProgress("正在生成中" + progress + "%");
                                } else {
                                    downProgressDialog.setProgress("下载进度为" + progress + "%");
                                }
                            }
                        }

                        @Override
                        public void isSuccess(boolean isSuccess, String path1) {
                            if (downProgressDialog != null) {
                                downProgressDialog.closePragressDialog();
                                downProgressDialog = null;
                            }
                            if (!keepAlbum) {
                                callback.downVideoSuccess(path1, imagePath);//下载成功后的回调
                            } else {
                                WaitingDialog.closePragressDialog();
                                saveToAlbum(path1);
                                if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
                                    AdManager.getInstance().showCpAd(context, AdConfigs.AD_SCREEN_FOR_DOWNLOAD);
                                }
                            }
                        }
                    });
                }
            }));
            manage.DownloadVideo(path, videoName);
        });
    }


    private void saveToAlbum(String path) {
        String albumPath = SaveAlbumPathModel.getInstance().getKeepOutput();
        try {
            FileUtil.copyFile(new File(path), albumPath);
            albumBroadcast(albumPath);
            showKeepSuccessDialog(albumPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void showKeepSuccessDialog(String path) {
        if (!DoubleClick.getInstance().isFastDoubleClick()) {
            AlertDialog.Builder builder = new AlertDialog.Builder( //去除黑边
                    new ContextThemeWrapper(context, R.style.Theme_Transparent));
            builder.setTitle(R.string.notification);
            builder.setMessage("已为你保存到相册,多多分享给友友\n" + "【" + path + context.getString(R.string.folder) + "】");
            builder.setNegativeButton(context.getString(R.string.got_it), (dialog, which) -> dialog.dismiss());
            builder.setCancelable(true);
            Dialog dialog = builder.show();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }


    /**
     * description ：通知相册更新
     * date: ：2019/8/16 14:24
     * author: 张同举 @邮箱 jutongzhang@sina.com
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
            ToastUtil.showToast("网络连接失败！");
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
                    LogUtil.d("searchActivity", "开始下载");
                    downZip(downZipUrl, folderPath);
                    mProgress = 0;
                    showMakeProgress();
                } else {
                    intoTemplateActivity(mFolder.getPath());
                }
            } else {
                ToastUtil.showToast("下载中，请稍后再试");
            }
        } else {
            ToastUtil.showToast("没找到sd卡");
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
                                LogUtil.d("onVideoAdError", "下载完成");
                                isDownZipUrl = false;
                                //可以制作了，先解压
                                File file = new File(zipPath);
                                try {
                                    ZipFileHelperManager.upZipFile(file, path, path1 -> {
                                        if (file.exists()) { //删除压缩包
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
                        Observable.just(e).subscribeOn(AndroidSchedulers.mainThread()).subscribe(e1 -> new Handler().post(() -> ToastUtil.showToast("下载异常，请重试")));
                        LogUtil.d("onVideoAdError", "Exception=" + e.getMessage());
                        callback.showDownProgress(100);
//                        ToastUtil.showToast(e.getMessage());
//                        Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                    super.run();
                }
            }.start();
        } else {
            ToastUtil.showToast("没有zip地址");
        }
    }


    /**
     * description ：换装
     * creation date: 2020/12/3
     * user : zhangtongju
     */
    public void toDressUp(String path, String templateId) {
        DressUpModel dressUpModel = new DressUpModel(context, url -> {
            LogUtil.d("OOM3", "融合结果的url为" + url);
            Intent intent = new Intent(context, DressUpPreviewActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("template_id", templateId);
            intent.putExtra("localImage", path);


            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent);
        });
        dressUpModel.toDressUp(path, templateId);
    }
}

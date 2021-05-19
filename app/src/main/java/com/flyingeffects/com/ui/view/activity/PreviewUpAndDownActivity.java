package com.flyingeffects.com.ui.view.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.bigkoo.convenientbanner.utils.ScreenUtil;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.flyingeffects.com.BuildConfig;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.PreviewUpDownAdapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.constans.UiStep;
import com.flyingeffects.com.databinding.ActivityPreviewUpAndDownBinding;
import com.flyingeffects.com.enity.CreateCutCallback;
import com.flyingeffects.com.enity.DownVideoPath;
import com.flyingeffects.com.enity.ListForUpAndDown;
import com.flyingeffects.com.enity.NewFragmentTemplateItem;
import com.flyingeffects.com.enity.ReplayMessageEvent;
import com.flyingeffects.com.enity.showAdCallback;
import com.flyingeffects.com.enity.TemplateDataCollectRefresh;
import com.flyingeffects.com.enity.templateDataZanRefresh;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.AdConfigs;
import com.flyingeffects.com.manager.AlbumManager;
import com.flyingeffects.com.manager.CompressionCuttingManage;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.manager.StimulateControlManage;
import com.flyingeffects.com.ui.interfaces.AlbumChooseCallback;
import com.flyingeffects.com.ui.interfaces.view.PreviewUpAndDownMvpView;
import com.flyingeffects.com.ui.model.DressUpModel;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.ui.model.GetPathTypeModel;
import com.flyingeffects.com.ui.model.MattingImage;
import com.flyingeffects.com.ui.model.initFaceSdkModel;
import com.flyingeffects.com.ui.presenter.PreviewUpAndDownMvpPresenter;
import com.flyingeffects.com.ui.view.dialog.CommonMessageDialog;
import com.flyingeffects.com.ui.view.dialog.LoadingDialog;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.PermissionUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.view.MattingVideoEnity;
import com.github.penfeizhou.animation.apng.APNGDrawable;
import com.github.penfeizhou.animation.loader.ResourceStreamLoader;
import com.lansosdk.videoeditor.MediaInfo;
import com.nineton.ntadsdk.itr.VideoAdCallBack;
import com.nineton.ntadsdk.manager.VideoAdManager;
import com.shixing.sxve.ui.AlbumType;
import com.shixing.sxve.ui.view.WaitingDialog;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.yanzhenjie.album.AlbumFile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


/**
 * description ：仿抖音效果的预览页面,替换掉目前存在的预览页面
 * creation date: 2020/6/24
 * user : zhangtongju
 */
public class PreviewUpAndDownActivity extends BaseActivity implements PreviewUpAndDownMvpView, AlbumChooseCallback {

    public final static int SELECTALBUM = 0;
    public final static int SELECTALBUMFROMBJ = 1;
    public final static int SELECTALBUMFROMDressUp = 2;

    private static final String TAG = "PreviewUpDownActivity";
    private Context mContext;

    private ActivityPreviewUpAndDownBinding mBinding;

    private PreviewUpAndDownMvpPresenter mMvpPresenter;

    private PreviewUpDownAdapter adapter;


    /**
     * 所有数据
     */
    private List<NewFragmentTemplateItem> allData = new ArrayList<>();

    private NewFragmentTemplateItem templateItem;

    //当前选中的页码
    private int nowChoosePosition;

    //上次选中的页码
    private int lastChoosePosition;

    //当前滑动趋势，false 是向下，true 是向上
    private boolean nowSlideOrientationIsUp = false;

    //    private String toUserID;
    private int nowPraise;

    private boolean ondestroy;

    //是否需要抠图
    private int mIsPicOut;

    private List<String> originalImagePath = new ArrayList<>();

    //下载视频成功后跳转到创作界面
    private String createDownVideoPath;

    //素材数量
    private int defaultnum;

    //模板下载地址
    private String TemplateFilePath;

    private LoadingDialog mLoadingDialog;

    //是否需要插入广告
    private boolean isNeedAddaD = false;

    //随机插入位置
    private int randomPosition;

    //目前已经插入的最大值
    private int insertMaxNum;

    //目前已经插入的最小值
    private int insertMinNum;

    private String alert = "飞闪极速抠图中...";

    private TTNativeExpressAd ad;

    private MattingImage mattingImage;

    //表示进入的入口
    private String mOldFromTo;


    private boolean nowItemIsAd = false;

    private String templateType;
    private boolean mIsFollow;
    private boolean mAdDialogIsShow;
    private static boolean sHasReward;

    @Override
    protected int getLayoutId() {
        return 0;
    }

    private String templateId;

    //1表示可以合拍，而0表示不能合拍
    private int mIsWithPlay;

    /**
     * 第一次进入的类型，如果加载更多了，可能类型就得变了
     */
    private String keepOldFrom;

    //0 表示不是影集，1表示影集
    private int isPic;

    private boolean isSlideViewpager = false;
    boolean isCanLoadMore;

    @Override
    protected void initView() {
        mContext = PreviewUpAndDownActivity.this;
        EventBus.getDefault().register(this);

        mBinding = ActivityPreviewUpAndDownBinding.inflate(getLayoutInflater());
        View rootView = mBinding.getRoot();
        setContentView(rootView);

        setOnClickListener();

        mattingImage = new MattingImage();
        BaseConstans.TemplateHasWatchingAd = false;
        ListForUpAndDown listForUpAndDown = (ListForUpAndDown) getIntent().getSerializableExtra("person");
        allData = listForUpAndDown.getAllData();

        ondestroy = false;

        mLoadingDialog = buildLoadingDialog();
        nowChoosePosition = getIntent().getIntExtra("position", 0);
        LogUtil.d("OOM2", "nowChoosePosition=" + nowChoosePosition);
        isCanLoadMore = getIntent().getBooleanExtra("isCanLoadMore", true);
        LogUtil.d("OOM", "isCanLoadMore=" + isCanLoadMore);
        //默认插入最值为当前位置
        insertMaxNum = nowChoosePosition;
        insertMinNum = nowChoosePosition;
        templateItem = allData.get(nowChoosePosition);
        mIsPicOut = templateItem.getIs_picout();
        isPic = templateItem.getIs_pic();
        mIsWithPlay = templateItem.getIs_with_play();
        templateType = templateItem.getTemplate_type();
        String searchText = getIntent().getStringExtra("searchText");
        defaultnum = templateItem.getDefaultnum();
        String toUserId = getIntent().getStringExtra("toUserID");
        mOldFromTo = getIntent().getStringExtra("fromTo");
        keepOldFrom = mOldFromTo;
        //如果模板是来自一键模板，但是模板类型是背景，那么修改状态值
        if (!TextUtils.isEmpty(templateItem.getPre_url())) {
            mOldFromTo = FromToTemplate.ISBJ;
        }

        //种类
        String categoryId = getIntent().getStringExtra("category_id");
        String tcId = getIntent().getStringExtra("tc_id");

        templateId = templateItem.getId() + "";
        //需要得到之前allData 已经滑到的页数和分类的类别以及是模板页面或者背景页面等
        int nowSelectPage = getIntent().getIntExtra("nowSelectPage", 1);
        nowPraise = templateItem.getIs_praise();
        mMvpPresenter = new PreviewUpAndDownMvpPresenter(this, this, allData, nowSelectPage, keepOldFrom, categoryId, toUserId, searchText, isCanLoadMore, tcId);

        mMvpPresenter.initSmartRefreshLayout(mBinding.refresh);
        if (isCanLoadMore) {
            if (nowChoosePosition >= allData.size() - 2) {
                mMvpPresenter.requestMoreData();
            }
        }

        adapter = new PreviewUpDownAdapter(R.layout.list_preview_up_down_item, allData, mOldFromTo);

        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.iv_zan) {
                onclickZan();
            } else if (view.getId() == R.id.tv_make) {
                if (mContext.getPackageManager().checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                        BuildConfig.APPLICATION_ID) == PackageManager.PERMISSION_GRANTED) {
                    toClickMake();
                } else {
                    ActivityCompat
                            .requestPermissions(PreviewUpAndDownActivity.this
                                    , PERMISSION_STORAGE, 1);
                }
            } else if (view.getId() == R.id.iv_writer ||
                    view.getId() == R.id.tv_describe ||
                    view.getId() == R.id.tv_writer_name) {
                intoUserHome(position);
            } else if (view.getId() == R.id.tv_title_music) {
                if (!TextUtils.isEmpty(mOldFromTo) && mOldFromTo.equals(FromToTemplate.ISBJ)) {
                    StatisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, "13_muisc", templateItem.getAuth());
                } else {
                    StatisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, "12_muisc", templateItem.getAuth());
                }
                intoUserHome(position);
            } else if (view.getId() == R.id.iv_download_bj) {
                StatisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, "10_bj_arrow");
                ActivityCompat.requestPermissions(PreviewUpAndDownActivity.this, PERMISSION_STORAGE, 2);
            } else if (view.getId() == R.id.ll_comment) {
                BaseFullBottomSheetFragment fullSheetDialogFragment = new BaseFullBottomSheetFragment();
                int height = (int) (ScreenUtil.getScreenHeight(this) * 0.3f);
                fullSheetDialogFragment.setTopOffset(height);
                fullSheetDialogFragment.setNowTemplateId(templateId);
                fullSheetDialogFragment.setNowTemplateTitle(templateItem.getTitle());
                fullSheetDialogFragment.setNowTemplateType(templateItem.getTemplate_type());
                fullSheetDialogFragment.show(getSupportFragmentManager(), "FullSheetDialogFragment");
            } else if (view.getId() == R.id.tv_btn_follow) {
                if (BaseConstans.hasLogin()) {
                    mIsFollow = allData.get(position).getIs_follow() == 1;
                    LogUtil.d(TAG, "isfollow = " + mIsFollow);
                    LogUtil.d(TAG, "isfollow = " + allData.get(position).getIs_follow());
                    isOnPause = true;
                    requestFollowThisUser(allData.get(position).getAdmin_id(), view, position);
                } else {
                    goActivity(LoginActivity.class);
                }
            }
        });

        mBinding.page2.setAdapter(adapter);
        mBinding.page2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);

            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position != -1) {
                    mOldFromTo = keepOldFrom;
                    LogUtil.d("OOM2", "当前位置为" + position);
                    adapter.nowPreviewChooseItem(position);
                    adapter.pauseVideo();
                    adapter.notifyItemChanged(position);
                    updateFromToData(allData.get(position));
                    nowItemIsAd = allData.size() > 0 && allData.get(position).getAd() != null;
                    nowChoosePosition = position;
                    //判断当前滑动状态
                    nowSlideOrientationIsUp = nowChoosePosition < lastChoosePosition;
                    refreshData();
                    if (!DoubleClick.getInstance().isFastZDYDoubleClick(2000)) {
                        if (position >= insertMaxNum || position <= insertMinNum) {
                            if (isSlideViewpager) {
                                mMvpPresenter.requestAD();
                                LogUtil.d("OOM", "开始请求广告position=" + position + "insertMaxNum=" + insertMaxNum + "insertMinNum=" + insertMinNum);
                            }
                        }
                    }
                    int allDataCount = allData.size();
                    if (isCanLoadMore) {
                        if (position == allDataCount - 3) {
                            LogUtil.d("OOM3", "请求更多数据");
                            mMvpPresenter.requestMoreData();
                        }
                    }
                    lastChoosePosition = position;
                    if (BaseConstans.hasLogin()) {
                        //主要用于刷新当前页面
                        LogUtil.d("OOM", "onPageSelected-----templateItem.getId()" + templateItem.getId());
                        //不是广告的情况
                        if (templateItem.getId() != 0) {
                            mMvpPresenter.requestTemplateDetail(templateItem.getId() + "");
                        }
                    }
                }
                isSlideViewpager = true;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }

        });

        mBinding.page2.setCurrentItem(nowChoosePosition, false);
        if (nowPraise == 1 && BaseConstans.hasLogin()) {
            setIsZan(true);
        }

        if (BaseConstans.isFirstUseDownAndUpAct()) {
            mBinding.relaParentShowAlert.setVisibility(View.VISIBLE);
            ResourceStreamLoader resourceLoader = new ResourceStreamLoader(this, R.mipmap.guide);
            APNGDrawable apngDrawable = new APNGDrawable(resourceLoader);
            mBinding.ivGuide.setImageDrawable(apngDrawable);
            mBinding.relaParentShowAlert.setOnClickListener(this::onViewClick);
            BaseConstans.setFirstUseDownAndUpAct();
        } else {
            mBinding.relaParentShowAlert.setVisibility(View.GONE);
        }
    }

    private void setOnClickListener() {
        mBinding.ibBack.setOnClickListener(this::onViewClick);
    }


    private void intoUserHome(int position) {
        Intent intent = new Intent(PreviewUpAndDownActivity.this, UserHomepageActivity.class);
        intent.putExtra("toUserId", allData.get(position).getAdmin_id());
        intent.putExtra("templateType", templateType);
        startActivity(intent);
    }

    private LoadingDialog buildLoadingDialog() {
        LoadingDialog dialog = LoadingDialog.getBuilder(mContext)
                .setHasAd(false)
                .setTitle("生成中...")
                .build();
        return dialog;
    }

    /**
     * description ：点击关注当前作者
     * creation date: 2020/7/30
     * user : zhangtongju
     */
    private void requestFollowThisUser(String to_user_id, View view, int position) {
        HashMap<String, String> params = new HashMap<>();
        params.put("to_user_id", to_user_id);

        // 启动时间
        Observable ob = Api.getDefault().followUser(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(this) {
                    @Override
                    protected void onSubError(String message) {
                        ToastUtil.showToast(message);
                    }

                    @Override
                    protected void onSubNext(Object data) {
                        LogUtil.d("follow", StringUtil.beanToJSONString(data));
                        if (mIsFollow) {
                            ((AppCompatTextView) view.findViewById(R.id.tv_btn_follow)).setText("关注");
                            mIsFollow = false;
                        } else {
                            // ((AppCompatTextView) view.findViewById(R.id.tv_btn_follow)).setText("取消关注");
                            mIsFollow = true;
                        }
                        LogUtil.d("OOM", "requestFollowThisUser");
                        mMvpPresenter.requestTemplateDetail(templateItem.getId() + "");
                    }
                }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject,
                false, true, true);
    }


    /**
     * description ：刷新初始数据，保障翻页的时候数据还是最新的
     * creation date: 2020/7/2
     * user : zhangtongju
     */
    private void refreshData() {
        if (nowChoosePosition >= 0 && nowChoosePosition < allData.size()) {
            templateItem = allData.get(nowChoosePosition);
            templateId = templateItem.getId() + "";
            defaultnum = templateItem.getDefaultnum();
            mIsPicOut = templateItem.getIs_picout();
            nowPraise = templateItem.getIs_praise();
            mIsWithPlay = templateItem.getIs_with_play();
        }
    }


    private void setIsZan(boolean isCollect) {
        NewFragmentTemplateItem item1 = allData.get(nowChoosePosition);
        if (isCollect) {
            item1.setIs_praise(1);
            adapter.setIsZan(true);
        } else {
            item1.setIs_praise(0);
            adapter.setIsZan(false);
        }
        allData.set(nowChoosePosition, item1);
    }


    /**
     * description ：点赞之后数据是加还是减
     * creation date: 2020/8/11
     * user : zhangtongju
     */
    private void planZanNum(boolean isAdd) {
        String zanNum = templateItem.getPraise();
        int iZanNum = Integer.parseInt(zanNum);
        if (isAdd) {
            iZanNum++;

            if ("1".equals(templateType)) {
                StatisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, " 12_like", templateItem.getTitle());
            } else {
                StatisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, " 13_like", templateItem.getTitle());
            }
        } else {
            if ("1".equals(templateType)) {
                StatisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, " 12_cancel", templateItem.getTitle());
            } else {
                StatisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, " 13_cancel", templateItem.getTitle());
            }
            iZanNum--;
        }
        NewFragmentTemplateItem item1 = allData.get(nowChoosePosition);
        item1.setPraise(iZanNum + "");
        allData.set(nowChoosePosition, item1);
        adapter.setIsZanCount(iZanNum);
        int needID = item1.getTemplate_id();
        if (needID == 0) {
            needID = item1.getId();
        }
        LogUtil.d("OOM", "needID=" + needID);
        EventBus.getDefault().post(new templateDataZanRefresh(nowChoosePosition, iZanNum, isAdd, needID));
    }

    @Override
    protected void initAction() {

    }

    private boolean isOnPause = false;

    @Override
    protected void onPause() {
        super.onPause();
        GSYVideoManager.onPause();
        adapter.pauseVideo();
        isOnPause = true;
        LogUtil.d("OOM22", "onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        ondestroy = true;
        if (adapter != null) {
            adapter.onDestroy();
        }
        GSYVideoManager.releaseAllVideos();

    }


    public void onViewClick(View view) {
        if (view == mBinding.relaParentShowAlert) {
            mBinding.relaParentShowAlert.setVisibility(View.GONE);
        } else if (view == mBinding.ibBack) {
            finish();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * description ：点击了收藏功能
     * creation date: 2020/7/2
     * user : zhangtongju
     */
    public void onclickZan() {
        if (BaseConstans.hasLogin()) {
            String needId;
            int id = templateItem.getTemplate_id();
            if (id != 0) {
                needId = id + "";
            } else {
                needId = templateItem.getId() + "";
            }
            mMvpPresenter.ZanTemplate(needId, templateItem.getTitle(), templateType);
        } else {
            goActivity(LoginActivity.class);
        }
    }

    @Override
    public void collectionResult(boolean collectionResult) {

        NewFragmentTemplateItem item = allData.get(nowChoosePosition);
        if (collectionResult) {
            item.setIs_collection(1);
        } else {
            item.setIs_collection(0);
        }
        allData.set(nowChoosePosition, item);

        switch (mOldFromTo) {
            case FromToTemplate.ISHOMEFROMBJ:
                EventBus.getDefault().post(new TemplateDataCollectRefresh(nowChoosePosition, collectionResult, 1));
                break;
            case FromToTemplate.ISHOMEMYLIKE:
                break;
            case FromToTemplate.ISHOMEMYTEMPLATECOLLECT:
                EventBus.getDefault().post(new TemplateDataCollectRefresh(nowChoosePosition, collectionResult, 0));
                break;
            case FromToTemplate.ISMESSAGEMYPRODUCTION:
                break;
            case FromToTemplate.ISMESSAGEMYLIKE:
                break;
            case FromToTemplate.ISTEMPLATE:
                EventBus.getDefault().post(new TemplateDataCollectRefresh(nowChoosePosition, collectionResult, 3));
                break;
            case FromToTemplate.ISBJ:
                EventBus.getDefault().post(new TemplateDataCollectRefresh(nowChoosePosition, collectionResult, 4));
                break;
            case FromToTemplate.ISBJCOLLECT:
                EventBus.getDefault().post(new TemplateDataCollectRefresh(nowChoosePosition, collectionResult, 2));
                break;
            case FromToTemplate.ISCHOOSEBJ:
                break;
            case FromToTemplate.ISSEARCHBJ:
                break;
            case FromToTemplate.ISSEARCHTEMPLATE:
                break;
            default:
                break;
        }
    }

    @Override
    public void zanResult() {
        if (nowPraise == 0) {
            nowPraise = 1;
        } else {
            nowPraise = 0;
        }
        planZanNum(nowPraise == 1);
        showZantState(nowPraise == 0);
    }

    @Override
    public void hasLogin(boolean hasLogin) {
        StimulateControlManage.getInstance().InitRefreshStimulate();
        if (!TextUtils.isEmpty(templateItem.getType()) && "1"
                .equals(templateItem.getType()) && BaseConstans.getIncentiveVideo()&&!BaseConstans.getIsNewUser()) {
            showMessageDialog();
        } else {
            hasLoginToNext();
        }
    }

    private void showMessageDialog() {
        String tag;
        if (TextUtils.equals(mOldFromTo, FromToTemplate.DRESSUP)) {
            tag = "alert_video_ad_face";
        } else {
            tag = "alert_video_ad_mb";
        }
        StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), tag);
        mAdDialogIsShow = true;
        StatisticsEventAffair.getInstance().setFlag(mContext, "video_ad_alert", "");

        int showAd = BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser() ?
                CommonMessageDialog.AD_STATUS_BOTTOM : CommonMessageDialog.AD_STATUS_NONE;

        CommonMessageDialog.getBuilder(mContext)
                .setContentView(R.layout.dialog_common_message_ad_under)
                .setAdStatus(showAd)
                .setAdId(AdConfigs.AD_IMAGE_DIALOG_OPEN_VIDEO)
                .setTitle("亲爱的友友")
                .setMessage("模板需要观看几秒广告")
                .setMessage2("「看完后就能制作飞闪视频」")
                .setPositiveButton("观看广告并制作")
                .setNegativeButton("取消")
                .setDialogBtnClickListener(new CommonMessageDialog.DialogBtnClickListener() {
                    @Override
                    public void onPositiveBtnClick(CommonMessageDialog dialog) {
                        StatisticsEventAffair.getInstance().setFlag(mContext, "bj_ad_open", templateItem.getTitle());
                        StatisticsEventAffair.getInstance().setFlag(mContext, "video_ad_alert_click_confirm");
                        EventBus.getDefault().post(new showAdCallback("PreviewActivity"));
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelBtnClick(CommonMessageDialog dialog) {
                        StatisticsEventAffair.getInstance().setFlag(mContext, "mb_ad_cancel", templateItem.getTitle());
                        StatisticsEventAffair.getInstance().setFlag(mContext, "video_ad_alert_click_cancel");
                        dialog.dismiss();
                    }

                })
                .setDialogDismissListener(() -> mAdDialogIsShow = false)
                .build().show();
    }

    @Override
    public void downVideoSuccess(String videoPath, String imagePath) {
        LogUtil.d("OOM2", "downVideoSuccess");
        toCloseProgressDialog();
        createDownVideoPath = videoPath;
        //背景选择页面
        if (mOldFromTo.equals(FromToTemplate.ISCHOOSEBJ)) {
            EventBus.getDefault().post(new DownVideoPath(videoPath));
            finish();
        } else {
            if (!TextUtils.isEmpty(imagePath)) {
                Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
                    if (originalImagePath.get(0).equals(imagePath)) {
                        //源图地址和剪切之后的地址完全一样，那说明只有一个情况，就是当前选择的素材是视频的情况，那么需要去得到视频的第一针，然后传过去
                        Intent intent = new Intent(PreviewUpAndDownActivity.this, VideoCropActivity.class);
                        intent.putExtra("videoPath", imagePath);
                        intent.putExtra("comeFrom", FromToTemplate.ISCHOOSEBJ);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        intoCreationTemplateActivity(imagePath, videoPath, originalImagePath.get(0), true);
                    }
                });
            } else {
                mMvpPresenter.getSpliteMusic(videoPath);
            }
        }
    }

    boolean isDownIng = false;

    @Override
    public void showDownProgress(int progress) {
        Observable.just(progress).subscribeOn(AndroidSchedulers.mainThread()).subscribe(progress1 -> {
            if (!ondestroy) {
                if (progress1 >= 100) {
                    isDownIng = false;
                    mLoadingDialog.dismiss();
                } else {
                    if (!isDownIng) {
                        mLoadingDialog.show();
                        isDownIng = true;
                    }
                    mLoadingDialog.setProgress(progress1);
                }
            }
        });
    }

    /**
     * description ：一键模板文件下载成功，背景页面是下载视频，而一键模板是下载zip
     * creation date: 2020/7/20
     * param :filePath 下载后的地址
     * user : zhangtongju
     */
    private String bjMp3;
    private float bjMp3Duration;

    @Override
    public void getTemplateFileSuccess(String filePath) {
        if (!ondestroy) {
            //file 文件下载成功
            this.TemplateFilePath = filePath;
            Log.d(TAG, "getTemplateFileSuccess: TemplateFilePath = " + TemplateFilePath);
            if (!TextUtils.isEmpty(templateItem.getVideotime()) && !"0".equals(templateItem.getVideotime()) && mIsWithPlay == 1) {
                bjMp3Duration = Float.parseFloat(templateItem.getVideotime());
                LogUtil.d("OOM", "bj.mp3=" + TemplateFilePath);
                bjMp3 = TemplateFilePath + File.separator + "bj.mp3";
                if (mOldFromTo.equals(FromToTemplate.FACEGIF)||mOldFromTo.equals(FromToTemplate.TEMPLATESPECIAL)) {
                    //闪图表情包不需要跟随音乐拍摄
                    AlbumManager.chooseAlbum(this, defaultnum, SELECTALBUM, this, "");
                } else {
                    AlbumManager.chooseAlbum(this, defaultnum, SELECTALBUM, this, "", (long) (bjMp3Duration * 1000), templateItem.getTitle(), bjMp3);
                }

            } else {
                AlbumManager.chooseImageAlbum(this, defaultnum, SELECTALBUM, this, "");
            }
        }
    }


    /**
     * description ：上啦加载下拉刷新更新后的数据
     * creation date: 2020/7/2
     * user : zhangtongju
     */
    @Override
    public void showNewData(List<NewFragmentTemplateItem> newAllData, boolean isRefresh) {
        allData = newAllData;
        if (isRefresh) {
            templateItem = newAllData.get(0);
        }
        if (isRefresh) {
            isNeedAddaD = false;
            randomPosition = 0;
            insertMaxNum = 0;
            insertMinNum = 0;
        } else {
            //如果当前页面需要广告，则插入广告
            if (isNeedAddaD && allData.size() > randomPosition) {
                isNeedAddaD = false;
                NewFragmentTemplateItem item = new NewFragmentTemplateItem();
                item.setAd(ad);
                allData.add(randomPosition, item);
            }
        }
        adapter.notifyDataSetChanged();
    }


    /**
     * description ：第一次进入默认之后会下滑
     * creation date: 2020/7/10
     * user : zhangtongju
     */
    @Override
    public void resultAd(List<TTNativeExpressAd> ads) {
        ad = ads.get(0);
        int minNum = BaseConstans.getFeedShowPosition(false);
        int MaxNum = BaseConstans.getFeedShowPosition(true);
        Random random = new Random();
        randomPosition = random.nextInt(MaxNum) % (MaxNum - minNum + 1) + minNum;
        LogUtil.d("OOM", "minNum=" + minNum + "MaxNum=" + MaxNum + "当前随机数=" + randomPosition);
        //需要判断是上滑还是下滑
        if (nowSlideOrientationIsUp) {
            //上滑的情况,要考虑数组改变的情况，比如，广告插入前面去了，那么当前的值应该也要做出改变，当前的位置应该+1
            randomPosition = insertMinNum - randomPosition;
            insertMinNum = randomPosition;
            LogUtil.d("OOM", "上滑的情况=" + randomPosition);
            LogUtil.d("OOM", "需要去的位置=" + randomPosition + "insertMinNum=" + insertMinNum + "当前随机数=" + randomPosition);
            if (randomPosition > 1) {
                NewFragmentTemplateItem item = new NewFragmentTemplateItem();
                item.setAd(ad);
                allData.add(randomPosition, item);
                adapter.notifyDataSetChanged();
                LogUtil.d("OOM", "广告插入的位置=" + randomPosition);
                //因为提前插入的，所以需要加1
                mBinding.page2.setCurrentItem(nowChoosePosition + 1, false);
            }
        } else {
            //下滑的情况
            randomPosition = insertMaxNum + randomPosition;
            insertMaxNum = randomPosition;
            LogUtil.d("OOM", "广告插入的位置=" + randomPosition);
            if (allData != null && allData.size() > randomPosition) {
                isNeedAddaD = false;
                NewFragmentTemplateItem item = new NewFragmentTemplateItem();
                item.setAd(ad);
                allData.add(randomPosition, item);
            } else {
                //在第二页了
                isNeedAddaD = true;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isOnPause = false;
        LogUtil.d("OOM22", "onResume " + mAdDialogIsShow);
        //出现bug 不能继续播放的问题
        if (!mAdDialogIsShow) {
            //  adapter.notifyDataSetChanged();
            if (!nowItemIsAd) {
                GSYVideoManager.onResume();
                LogUtil.d("OOM22", "GSYVideoManager.onResume()");
            }
            if (BaseConstans.hasLogin()) {
                //主要用于刷新当前页面
                mMvpPresenter.requestTemplateDetail(templateItem.getId() + "");
            }
        }
        WaitingDialog.closeProgressDialog();
    }


    /**
     * description ：刷新当前页面数据 ，主要用来显示收藏状态
     * creation date: 2020/7/13
     * user : zhangtongju
     */

    @Override
    public void getTemplateInfo(NewFragmentTemplateItem data) {
        if (data != null) {
            templateItem = data;
            setIsZan(data.getIs_praise() == 1);
            nowPraise = data.getIs_praise();
            templateType = data.getTemplate_type();
            isPic = templateItem.getIs_pic();
            updateFromToData(data);
            //2021-4-30 end 解決bug 列表进入，加载数据后列表类型没有更新的问题
            adapter.setCommentCount(data.getComment());
            mIsWithPlay = templateItem.getIs_with_play();
            mIsFollow = data.getIs_follow() == 1;
            adapter.setIsFollow(templateItem.getIs_follow(), templateItem.getAdmin_id());
            //更新页面数据，防止数据不全的情况
            if (!isOnPause) {
                allData.set(nowChoosePosition, data);
                adapter.notifyItemChanged(nowChoosePosition);
            }
        }
    }


    /**
     * description ：更新来源界面
     * creation date: 2021/5/6
     * user : zhangtongju
     */
    private void updateFromToData(NewFragmentTemplateItem data ){
        //如果模板是来自一键模板，但是模板类型是背景，那么修改状态值
        if(!data.isHasShowAd()){
            if (!TextUtils.isEmpty(templateItem.getPre_url())) {
                mOldFromTo = FromToTemplate.ISBJ;
            }
            //2021-4-30 解決bug 列表进入，加载数据后列表类型没有更新的问题
            String templateType = data.getTemplate_type();
            //templateType 类型:1=模板,2=背景,3=换脸,4=换背景,5=表情包
            if (!TextUtils.isEmpty(templateType)) {
                int api_type = data.getApi_type();
                if (api_type != 0) {
                    //只要闪图页面，特殊模板，都走dressUp逻辑
                    if (!"1".equals(templateType) && !"2".equals(templateType)) {
                        mOldFromTo = FromToTemplate.SPECIAL;
                    }
                    //只要模板页面，特殊模板，都走模板逻辑
                    if ("1".equals(templateType)) {
                        mOldFromTo = FromToTemplate.TEMPLATESPECIAL1;
                    } else if ("5".equals(templateType)) {
                        //表情包走特效，会进入模板页面
                        mOldFromTo = FromToTemplate.TEMPLATESPECIAL;
                    }
                } else {
                    if ("3".equals(templateType)) {
                        //换脸
                        mOldFromTo = FromToTemplate.DRESSUP;
                    } else if ("4".equals(templateType)) {
                        //换背景
                        mOldFromTo = FromToTemplate.CHOOSEBJ;
                    } else if ("5".equals(templateType)) {
                        //表情包
                        mOldFromTo = FromToTemplate.FACEGIF;
                    }
                }
            }
            adapter.SetOldFromTo(mOldFromTo);
        }

    }


    private void toCloseProgressDialog() {
        if (!ondestroy) {
            Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> new Handler().postDelayed(WaitingDialog::closeProgressDialog, 200));
        }
    }


    /**
     * description ：得到了背景音乐
     * creation date: 2020/7/20
     * user : zhangtongju
     */
    private String videoPath;

    @Override
    public void returnSpliteMusic(String musicPath, String videoPath) {
        this.videoPath = videoPath;
        if (!TextUtils.isEmpty(musicPath) && mIsWithPlay == 1) {
            if (mOldFromTo.equals(FromToTemplate.ISBJ)) {
                AlbumManager.chooseAlbum(this, 1, SELECTALBUMFROMBJ, this, "", (long) adapter.getVideoDuration(), templateItem.getTitle(), musicPath);
            } else {
                AlbumManager.chooseAlbum(this, 1, SELECTALBUMFROMBJ, this, "", (long) adapter.getVideoDuration(), templateItem.getTitle(), musicPath);
            }
        } else {
            AlbumManager.chooseAlbum(this, 1, SELECTALBUMFROMBJ, this, "");
        }
    }

    @Override
    public void onclickCollect() {
        String needId;
        int id = templateItem.getTemplate_id();
        if (id != 0) {
            needId = id + "";
        } else {
            needId = templateItem.getId() + "";
        }
        mMvpPresenter.collectTemplate(needId, templateItem.getTitle(), templateType);
    }

    @Override
    public void getDressUpPathResult(List<String> paths) {
        if (paths != null) {
            for (int i = 0; i < paths.size(); i++) {
                LogUtil.d("OOM3", "换装之后保存本地的地址" + paths.get(i));
            }
            intoTemplateActivity(paths, TemplateFilePath);
        }
    }

    @Override
    public void shareSaveToAlbum() {

    }

    /**
     * 这里逻辑优化下,背景页面是选择图片后在去下载背景
     * 现在修改为先下载视频，下载完成后在打开相册选择图片
     */
    private void hasLoginToNext() {
        mMvpPresenter.requestMessageStatistics("1", "", templateId);

        switch (mOldFromTo) {
            case FromToTemplate.ISHOMEFROMBJ:
                StatisticsEventAffair.getInstance().setFlag(this, "8_Selectvideo");
                mMvpPresenter.DownVideo(templateItem.getVidoefile(), "", templateItem.getId() + "", false);
                break;
            case FromToTemplate.ISCHOOSEBJ:
                Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> new Handler().postDelayed(() -> {
                    if (!ondestroy) {
                        String alert = "正在生成中...";
                        WaitingDialog.openPragressDialog(PreviewUpAndDownActivity.this, alert);
                        mMvpPresenter.DownVideo(templateItem.getVidoefile(), "", templateItem.getId() + "", true);
                    }
                }, 200));
                break;
            case FromToTemplate.ISBJ:
                LogUtil.d("OOM", "来自背景");
                StatisticsEventAffair.getInstance().setFlag(this, "8_Selectvideo");
                mMvpPresenter.DownVideo(templateItem.getVidoefile(), "", templateItem.getId() + "", false);
                break;
            case FromToTemplate.ISHOMEMYLIKE:
            case FromToTemplate.ISHOMEMYTEMPLATECOLLECT:
            case FromToTemplate.ISMESSAGEMYPRODUCTION:
            case FromToTemplate.ISMESSAGEMYLIKE:
            case FromToTemplate.ISTEMPLATE:
            case FromToTemplate.TEMPLATESPECIAL:
            case FromToTemplate.FACEGIF:
            case FromToTemplate.ISBJCOLLECT:
            case FromToTemplate.ISSEARCHBJ:
            case FromToTemplate.ISSEARCHTEMPLATE:
                if ("2".equals(templateType)) {
                    StatisticsEventAffair.getInstance().setFlag(this, "8_Selectvideo");
                    mMvpPresenter.DownVideo(templateItem.getVidoefile(), "", templateItem.getId() + "", false);
                } else {
                    if(templateItem.getApi_type()!=0&&mOldFromTo.equals(FromToTemplate.ISSEARCHTEMPLATE)){
                        //搜索特殊模板的情况，这里比较特殊，模板都是直接旋转素材，而不需要现在zip
                        AlbumManager.chooseImageAlbum(this, templateItem.getDefaultnum(), SELECTALBUMFROMDressUp, this, "");
                    }else{
                        if (mOldFromTo.equals(FromToTemplate.ISSEARCHBJ) || mOldFromTo.equals(FromToTemplate.ISSEARCHTEMPLATE)) {
                            StatisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, "4_search_make", templateItem.getTitle());
                        }
                        StatisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, "mb_make", templateItem.getTitle());
                        adapter.pauseVideo();
                        mMvpPresenter.downZip(templateItem.getTemplatefile(), templateItem.getZipid());
                    }

                }
                break;
            case FromToTemplate.DRESSUP:
            case FromToTemplate.CHOOSEBJ:
            case FromToTemplate.SPECIAL:
            case FromToTemplate.TEMPLATESPECIAL1:
                AlbumManager.chooseImageAlbum(this, templateItem.getDefaultnum(), SELECTALBUMFROMDressUp, this, "");
                break;
            default:
                break;
        }

    }


    private void showZantState(boolean unSelected) {
        if (unSelected) {
            nowPraise = 0;
            setIsZan(false);
        } else {
            nowPraise = 1;
            setIsZan(true);
        }
    }


    private void toClickMake() {

        if (!DoubleClick.getInstance().isFastZDYDoubleClick(1000)) {
            adapter.pauseVideo();
            if (mOldFromTo.equals(FromToTemplate.ISBJ) || mOldFromTo.equals(FromToTemplate.ISHOMEFROMBJ)) {
                StatisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, "5_bj_Make", templateItem.getTitle());
                UiStep.isFromDownBj = true;
            } else if (mOldFromTo.equals(FromToTemplate.DRESSUP)) {
                StatisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, "21_face_made", templateItem.getTitle());
            } else if (mOldFromTo.equals(FromToTemplate.CHOOSEBJ)) {
                StatisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, "st_bj_make", templateItem.getTitle());
            } else if (mOldFromTo.equals(FromToTemplate.FACEGIF)) {
                StatisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, "st_bqb_make", templateItem.getTitle());
            } else if (mOldFromTo.equals(FromToTemplate.SPECIAL)) {
                StatisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, "st_ft_make", templateItem.getTitle());
            } else {
                StatisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, "1_mb_make", templateItem.getTitle());
            }
            if (BaseConstans.hasLogin()) {
                //登录可能被挤下去，所以这里加个用户信息刷新请求
                mMvpPresenter.requestUserInfo();
            } else {
                Intent intent = new Intent(PreviewUpAndDownActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
    }


    @Override
    public void resultFilePath(int tag, List<String> paths, boolean isCancel,
                               boolean isFromCamera, ArrayList<AlbumFile> albumFileList) {
        initFaceSdkModel.getHasLoadSdkOk(() -> {
            StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "all_next_step_type_num", templateItem.getTemplate_type());
            LogUtil.d("OOM3", "模型也加载完成");
            if (!isCancel && !ondestroy && paths != null && paths.size() > 0) {
                if (albumFileList.get(0).isClickToCamera()) {
                    StatisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, "12_mb_shoot", templateItem.getTitle());
                    //拍摄新页面
                    Intent intent = new Intent(PreviewUpAndDownActivity.this, FUBeautyActivity.class);

                    if ("2".equals(templateItem.getTemplate_type())) {
                        //背景
                        MediaInfo mediaInfo = new MediaInfo(videoPath);
                        mediaInfo.prepare();
                        intent.putExtra("musicPath", videoPath);
                        intent.putExtra("createDownVideoPath", createDownVideoPath);
                        long duration = mediaInfo.getDurationUs() / 1000;
                        intent.putExtra("duration", duration);
                        mediaInfo.release();
                    } else {
                        intent.putExtra("musicPath", bjMp3);
                        intent.putExtra("duration", (long) (bjMp3Duration * 1000));
                    }

                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("isFrom", 1);
                    intent.putExtra("defaultnum", defaultnum);
                    intent.putExtra("templateItem", templateItem);
                    intent.putExtra("TemplateFilePath", TemplateFilePath);
                    intent.putExtra("videoPath", videoPath);
                    intent.putExtra("OldfromTo", mOldFromTo);
                    intent.putExtra("title", templateItem.getTitle());

                    startActivity(intent);
                } else {
                    if (isFromCamera) {
                        if (mOldFromTo.equals(FromToTemplate.ISBJ) || mOldFromTo.equals(FromToTemplate.ISHOMEFROMBJ)) {
                            LogUtil.d("OOM2", "背景页面自己拍摄");
                            StatisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, "10_bj_success");
                        } else {
                            LogUtil.d("OOM2", "模板页面自己拍摄");
                            StatisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, "11_mb_success");
                        }
                    }
                    if (mOldFromTo.equals(FromToTemplate.DRESSUP)) {
                        //来自换装,闪图页面
                        LogUtil.d(TAG, "toCreation " + templateItem.getBackground_image());
                        if ("0".equals(templateItem.getBackground_image())) {
                            mMvpPresenter.toDressUp(paths.get(0), templateId, templateItem.getTitle());
                        } else {
                            createMattingImage(paths);
                        }
                    } else if (mOldFromTo.equals(FromToTemplate.SPECIAL)) {
                        int api_type = templateItem.getApi_type();
                        mMvpPresenter.ToDressUpSpecial(paths, api_type, templateId, templateItem.getTitle(),templateItem.getType());
                    }else if (mOldFromTo.equals(FromToTemplate.TEMPLATESPECIAL1)) {
                        int api_type = templateItem.getApi_type();
                        mMvpPresenter.ToTemplateAddStickerActivity(paths, templateItem.getTitle(), templateId,api_type,templateItem.getType());
                    }

                    else if (templateItem.getIs_anime() == 1) {
                        //模板换装新逻辑
                        DressUpModel dressUpModel = new DressUpModel(this, paths1 -> mMvpPresenter.GetDressUpPath(paths1), true);
                        dressUpModel.toDressUp(paths.get(0), templateId);

                    } else {
                        chooseAlbumStatistics(paths);
                        LogUtil.d("OOM", "pathsSize=" + paths.size());
                        createMattingImage(paths);
                    }

                }
            }

        }, this);
    }

    private void createMattingImage(List<String> paths) {

        mattingImage.createHandle(PreviewUpAndDownActivity.this, isDone -> {
            if (isDone) {
                Observable.just("tag").subscribeOn(AndroidSchedulers.mainThread()).subscribe(str -> {
                    if (mOldFromTo.equals(FromToTemplate.ISBJ)) {
                        //背景模板文案
                        alert = "正在生成中~";
                    } else {
                        //一键模板不抠图的情况下
                        if (mIsPicOut == 0) {
                            alert = "正在生成中~";
                        }
                    }

                    new Handler().postDelayed(() -> {
                        if (!ondestroy) {
                            WaitingDialog.openPragressDialog(PreviewUpAndDownActivity.this, alert);
                            GSYVideoManager.onPause();
                        }
                    }, 200);

                    new Thread(() -> {
                        originalImagePath = paths;
                        //如果是视频，就不抠图了
                        if (isPic == 0) {
                            LogUtil.d("OOM6", "is_pic==0");
                            String path = paths.get(0);
                            String pathType = GetPathTypeModel.getInstance().getMediaType(path);
                            if (AlbumType.isImage(pathType)) {
                                //选择的时图片
                                if (mOldFromTo.equals(FromToTemplate.ISBJ)) {
                                    StatisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, "8_SelectImage");
                                }
                                compressImage(paths, templateItem.getId() + "");
                            } else {
                                //选择的时视频
                                if ("2".equals(templateType)) {
                                    Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
                                        toCloseProgressDialog();
                                        if (originalImagePath.get(0).equals(paths.get(0))) {
                                            createDownVideoPath = videoPath;
                                            //源图地址和剪切之后的地址完全一样，那说明只有一个情况，就是当前选择的素材是视频的情况，那么需要去得到视频的第一针，然后传过去
                                            Intent intent = new Intent(PreviewUpAndDownActivity.this, VideoCropActivity.class);
                                            intent.putExtra("videoPath", paths.get(0));
                                            intent.putExtra("comeFrom", FromToTemplate.ISCHOOSEBJ);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        } else {
                                            intoCreationTemplateActivity(paths.get(0), videoPath, originalImagePath.get(0), true);
                                        }
                                    });
                                } else {
                                    toCloseProgressDialog();
                                    String videoTime = templateItem.getVideotime();
                                    if (!TextUtils.isEmpty(videoTime) && !"0".equals(videoTime)) {
                                        float needVideoTime = Float.parseFloat(videoTime);
                                        LogUtil.d("OOM", "needVideoTime=" + needVideoTime);
                                        Intent intoCutVideo = new Intent(PreviewUpAndDownActivity.this, TemplateCutVideoActivity.class);
                                        intoCutVideo.putExtra("needCropDuration", needVideoTime);
                                        intoCutVideo.putExtra("templateName", templateItem.getTitle());
                                        intoCutVideo.putExtra("videoPath", paths.get(0));
                                        intoCutVideo.putExtra("picout", templateItem.getIs_picout());
                                        startActivity(intoCutVideo);
                                    } else {
                                        intoTemplateActivity(paths, TemplateFilePath);
                                    }
                                }
                            }
                        } else {
                            LogUtil.d("OOM6", "进入到了intoTemplate");
                            intoTemplateActivity(paths, TemplateFilePath);
                        }

                    }).start();
                });
            }
        });

    }

    /**
     * 换脸-换背景
     */
    private void toCreationActivity(String imgUrl, String originalPath) {
        LogUtil.d(TAG, "toCreation");

        Intent intent = CreationTemplateActivity.buildIntent(mContext, CreationTemplateActivity.FROM_DRESS_UP_BACK_CODE, imgUrl,
                "", originalPath, true, templateItem.getTitle(),
                templateItem.getTemplate_id(), false, templateItem.getBackground_image());
        startActivity(intent);
    }


    private void chooseAlbumStatistics(List<String> paths) {
        if (paths != null && paths.size() > 0) {
            for (String path : paths) {
                if (AlbumType.isImage(GetPathTypeModel.getInstance().getMediaType(path))) {
                    StatisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, "userChooseType", "选择的是图片");
                    LogUtil.d("OOM", "当前选择的是图片");
                } else {
                    StatisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, "userChooseType", "选择的是视频");
                    LogUtil.d("OOM", "当前选择的是视频");
                }
            }
        }
    }


    /***
     * 这里的逻辑是选择图片后抠图后再去的下载视频
     * @param templateId id
     */
    private void compressImage(List<String> paths, String templateId) {
        boolean hasCache = templateItem.getIs_anime() != 1;

        CompressionCuttingManage manage = new CompressionCuttingManage(PreviewUpAndDownActivity.this, templateId, hasCache, tailorPaths -> {
            //
            if (mOldFromTo.equals(FromToTemplate.CHOOSEBJ)) {
                toCreationActivity(tailorPaths.get(0), paths.get(0));
            } else if ("2".equals(templateType)) {
                mMvpPresenter.DownVideo(templateItem.getVidoefile(), tailorPaths.get(0), templateItem.getId() + "", false);
            } else {
                toCloseProgressDialog();
                intoTemplateActivity(tailorPaths, TemplateFilePath);
            }
        });
        manage.toMatting(paths);
    }


    /**
     * description ：选择图片进入的逻辑
     * creation date: 2020/5/7
     * user : zhangtongju
     */
    private void intoTemplateActivity(List<String> paths, String templateFilePath) {
        toCloseProgressDialog();
        Intent intent = TemplateActivity
                .buildIntent(mContext, paths, originalImagePath, defaultnum,
                        mOldFromTo, templateItem.getIs_picout(), templateItem, templateFilePath);
        startActivity(intent);
        Observable.just(200).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                new Handler().postDelayed(() -> adapter.pauseVideo(), 200);
            }
        });

    }


    /**
     * description ：裁剪页面裁剪成功后返回的数据,针对跳转到自定义创作页面
     * creation date: 2020/4/13
     * user : zhangtongju
     */
    @Subscribe
    public void onEventMainThread(CreateCutCallback event) {
        LogUtil.d("OOM", "event.getCoverPath()=" + event.getCoverPath() + "createDownVideoPath=" + createDownVideoPath + "createDownVideoPath=" + createDownVideoPath + "event.isNeedCut()=" + event.isNeedCut());
        intoCreationTemplateActivity(event.getCoverPath(),
                createDownVideoPath, event.getOriginalPath(), event.isNeedCut());
    }

    /**
     * description ：裁剪页面裁剪成功后返回的数据,针对跳转到一键模板
     * creation date: 2020/4/13
     * user : zhangtongju
     */
    @Subscribe
    public void onEventMainThread(MattingVideoEnity event) {
        LogUtil.d("OOM3", "onEventMainThread=" + event.getTag());
        if (event.getTag() == 0) {
            if (originalImagePath != null) {
                originalImagePath.clear();
            }
            ArrayList<String> paths = new ArrayList<>();
            paths.add(event.getMattingPath());
            LogUtil.d("OOM3", "111");
            Intent intent = new Intent(this, TemplateActivity.class);
            Bundle bundle = new Bundle();

            bundle.putStringArrayList("paths", paths);
            bundle.putInt("isPicNum", defaultnum);
            bundle.putString("fromTo", mOldFromTo);
            //用户没选择抠图
            if (originalImagePath != null && event.getOriginalPath() != null) {
                originalImagePath.add(event.getOriginalPath());
                bundle.putInt("picout", 1);
                LogUtil.d("OOM3", "222");
            } else {
                originalImagePath = null;
                bundle.putInt("picout", 0);
                LogUtil.d("OOM3", "333");
            }
            bundle.putString("primitivePath", event.getPrimitivePath());
            bundle.putInt("is_anime", templateItem.getIs_anime());

            bundle.putString("templateName", templateItem.getTitle());
            intent.putExtra("person", templateItem);//直接存入被序列化的对象实例
            bundle.putString("templateId", templateItem.getId() + "");
            bundle.putString("videoTime", templateItem.getVideotime());
            bundle.putStringArrayList("originalPath", (ArrayList<String>) originalImagePath);
            bundle.putString("templateFilePath", TemplateFilePath);
            bundle.putInt("isSpecial", templateItem.getApi_type());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("Message", bundle);
            LogUtil.d("OOM3", "startActivity=");
            LogUtil.d("OOM3", "is_anime=" + templateItem.getIs_anime());
            LogUtil.d("OOM3", "is_animeaLLdATA=" + StringUtil.beanToJSONString(templateItem));
            startActivity(intent);
        }
    }

    private void intoCreationTemplateActivity(String imagePath, String videoPath, String
            originalPath, boolean isNeedCut) {
        Intent intent = new Intent(PreviewUpAndDownActivity.this, CreationTemplateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("paths", imagePath);
        bundle.putSerializable("bjTemplateTitle", templateItem.getTitle());
        bundle.putString("originalPath", originalPath);
        bundle.putString("video_path", videoPath);
        boolean isLandscape = templateItem.getIsLandscape() == 1;
        bundle.putBoolean("isLandscape", isLandscape);
        bundle.putBoolean("isNeedCut", isNeedCut);
        int id = templateItem.getTemplate_id();
        if (id == 0) {
            id = templateItem.getId();
        }
        bundle.putSerializable("templateId", id);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Message", bundle);
        startActivity(intent);
        setResult(Activity.RESULT_OK, intent);

        Observable.just(200).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                new Handler().postDelayed(() -> adapter.pauseVideo(), 200);
            }
        });

    }


    /**
     * description ：激励视频回调
     * creation date: 2020/4/13
     * user : zhangtongju
     */
    @Subscribe
    public void onEventMainThread(showAdCallback event) {
        if(!DoubleClick.getInstance().isFastZDYDoubleClick(200)){
            if (event != null && "PreviewActivity".equals(event.getIsFrom())) {
                //需要激励视频
                BaseConstans.TemplateHasWatchingAd = false;
                if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
                    VideoAdManager videoAdManager = new VideoAdManager();
                    String adId;
                    if(TextUtils.equals(mOldFromTo, FromToTemplate.DRESSUP)){
                        adId= AdConfigs.AD_DRESSUP_video;
                    }else if(TextUtils.equals(mOldFromTo, FromToTemplate.ISBJ)){
                        adId= AdConfigs.AD_stimulate_video_bj;
                    }else{
                        adId= AdConfigs.AD_stimulate_video;
                    }
                    videoAdManager.showVideoAd(this, adId, new VideoAdCallBack() {
                        @Override
                        public void onVideoAdSuccess() {
                            StatisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, "video_ad_alert_request_sucess");
                            LogUtil.d("OOM4", "onVideoAdSuccess");
                        }

                        @Override
                        public void onVideoAdError(String s) {
                            StatisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, "video_ad_alert_request_fail");
                            LogUtil.d("OOM4", "onVideoAdError" + s);
                            if ("1".equals(BaseConstans.getAdShowErrorCanSave())) {
                                hasLoginToNext();
                            }
                        }

                        public void onVideoAdClose() {
                            LogUtil.d("OOM4", "onVideoAdClose");
//                        BaseConstans.TemplateHasWatchingAd = false;
                            if (sHasReward) {
                                hasLoginToNext();
                                sHasReward = false;
                            } else {
                                ToastUtil.showToast("看完广告才可获取权益");
                            }
                        }

                        @Override
                        public void onRewardVerify() {
                            sHasReward = true;
                            BaseConstans.TemplateHasWatchingAd = true;
                            //hasLoginToNext();
                        }

                        @Override
                        public void onVideoAdSkip() {
                            LogUtil.d("OOM4", "onVideoAdSkip");
                        }

                        @Override
                        public void onVideoAdComplete() {
                            LogUtil.d("OOM4", "onVideoAdComplete");
                        }

                        @Override
                        public void onVideoAdClicked() {
                            LogUtil.d("OOM4", "onVideoAdClicked");
                        }
                    });
                } else {
                    hasLoginToNext();
                }
            }
        }

    }


    @Subscribe
    public void onEventMainThread(ReplayMessageEvent event) {
        if (BaseConstans.hasLogin()) {
            //主要用于刷新当前页面
            LogUtil.d("OOM", "ReplayMessageEvent");
            isOnPause = true;
            mMvpPresenter.requestTemplateDetail(templateItem.getId() + "");
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> deniedPermission = new ArrayList<>();
        deniedPermission.clear();
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            int result = grantResults[i];
            if (result != PackageManager.PERMISSION_GRANTED) {
                deniedPermission.add(permission);
            }
        }
        if (deniedPermission.isEmpty()) {
            if (requestCode == 1) {
                toClickMake();
            } else {
                if (!DoubleClick.getInstance().isFastZDYDoubleClick(1000)) {
                    mMvpPresenter.showBottomSheetDialog(templateItem.getVidoefile(), "", templateItem.getId() + "", templateItem, mOldFromTo);
                }
            }
        } else {
            new AlertDialog.Builder(this)
                    .setMessage("读取相册必须获取存储权限，如需使用接下来的功能，请同意授权~")
                    .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .setPositiveButton("去授权", (dialog, which) -> {
                        PermissionUtil.gotoPermission(mContext);
                        dialog.dismiss();
                    }).create()
                    .show();
        }
    }

}

package com.flyingeffects.com.ui.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bigkoo.convenientbanner.utils.ScreenUtil;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.Preview_up_and_down_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.constans.UiStep;
import com.flyingeffects.com.enity.CreateCutCallback;
import com.flyingeffects.com.enity.DownVideoPath;
import com.flyingeffects.com.enity.ListForUpAndDown;
import com.flyingeffects.com.enity.ReplayMessageEvent;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.enity.showAdCallback;
import com.flyingeffects.com.enity.templateDataCollectRefresh;
import com.flyingeffects.com.enity.templateDataZanRefresh;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.AdConfigs;
import com.flyingeffects.com.manager.AlbumManager;
import com.flyingeffects.com.manager.CompressionCuttingManage;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.StimulateControlManage;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.AlbumChooseCallback;
import com.flyingeffects.com.ui.interfaces.view.PreviewUpAndDownMvpView;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.ui.model.GetPathTypeModel;
import com.flyingeffects.com.ui.model.MattingImage;
import com.flyingeffects.com.ui.presenter.PreviewUpAndDownMvpPresenter;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.view.MattingVideoEnity;
import com.github.penfeizhou.animation.apng.APNGDrawable;
import com.github.penfeizhou.animation.loader.ResourceStreamLoader;
import com.nineton.ntadsdk.itr.VideoAdCallBack;
import com.nineton.ntadsdk.manager.VideoAdManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shixing.sxve.ui.albumType;
import com.shixing.sxve.ui.view.WaitingDialog;
import com.shixing.sxve.ui.view.WaitingDialog_progress;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.yanzhenjie.album.AlbumFile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.viewpager2.widget.ViewPager2;

import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;


/**
 * description ：仿抖音效果的预览页面,替换掉目前存在的预览页面
 * creation date: 2020/6/24
 * user : zhangtongju
 */
public class PreviewUpAndDownActivity extends BaseActivity implements PreviewUpAndDownMvpView, AlbumChooseCallback {
    public final static int SELECTALBUM = 0;
    public final static int SELECTALBUMFROMBJ = 1;
    private static final String TAG = "PreviewUpDownActivity";
    @BindView(R.id.page2)
    ViewPager2 viewPage2;

    @BindView(R.id.refresh)
    SmartRefreshLayout smartRefreshLayout;

    PreviewUpAndDownMvpPresenter mMvpPresenter;

    private Preview_up_and_down_adapter adapter;

    private List<new_fag_template_item> allData = new ArrayList<>();
    private new_fag_template_item templateItem;
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
    private int is_picout;

    private List<String> originalImagePath = new ArrayList<>();


    //下载视频成功后跳转到创作界面
    private String createDownVideoPath;

    //素材数量
    private int defaultnum;

    //模板下载地址
    private String TemplateFilePath;


    private WaitingDialog_progress waitingDialog_progress;

    //是否需要插入广告
    private boolean isNeedAddaD = false;

    //随机插入位置
    private int randomPosition;


    //目前已经插入的最大值
    private int insertMaxNum;

    //目前已经插入的最小值
    private int insertMinNum;


    @BindView(R.id.rela_parent_show_alert)
    LinearLayout rela_parent_show_alert;

    @BindView(R.id.iv_guide)
    ImageView iv_guide;

    private String alert = "飞闪极速抠图中...";

    private TTNativeExpressAd ad;


    private MattingImage mattingImage;


    //表示进入的入口
    private String OldfromTo;


    private boolean nowItemIsAd = false;


    private String templateType;
    private boolean mIsFollow;


    @Override
    protected int getLayoutId() {
        return R.layout.act_preview_up_and_down;
    }

    private String templateId;

    //1表示可以合拍，而0表示不能合拍
    private int is_with_play;

    private String keepOldFrom;


    private boolean isSlideViewpager = false;
    boolean isCanLoadMore;

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        mattingImage = new MattingImage();
        BaseConstans.TemplateHasWatchingAd = false;
        ListForUpAndDown listForUpAndDown = (ListForUpAndDown) getIntent().getSerializableExtra("person");
        allData = listForUpAndDown.getAllData();
        ondestroy = false;
        waitingDialog_progress = new WaitingDialog_progress(this);
        nowChoosePosition = getIntent().getIntExtra("position", 0);
        LogUtil.d("OOM2", "nowChoosePosition=" + nowChoosePosition);
        isCanLoadMore = getIntent().getBooleanExtra("isCanLoadMore", true);
        LogUtil.d("OOM", "isCanLoadMore=" + isCanLoadMore);
        //默认插入最值为当前位置
        insertMaxNum = nowChoosePosition;
        insertMinNum = nowChoosePosition;
        templateItem = allData.get(nowChoosePosition);
        is_picout = templateItem.getIs_picout();
        is_with_play = templateItem.getIs_with_play();
        templateType = templateItem.getTemplate_type();
        String searchText = getIntent().getStringExtra("searchText");
        defaultnum = templateItem.getDefaultnum();
        String toUserID = getIntent().getStringExtra("toUserID");
        OldfromTo = getIntent().getStringExtra("fromTo");
        keepOldFrom = OldfromTo;
        //如果模板是来自一键模板，但是模板类型是背景，那么修改状态值
        if (!TextUtils.isEmpty(templateItem.getPre_url())) {
            OldfromTo = FromToTemplate.ISBJ;
        }

        //种类
        String category_id = getIntent().getStringExtra("category_id");
        templateId = templateItem.getId() + "";
        //需要得到之前allData 已经滑到的页数和分类的类别以及是模板页面或者背景页面等
        int nowSelectPage = getIntent().getIntExtra("nowSelectPage", 1);
        nowPraise = templateItem.getIs_praise();
        mMvpPresenter = new PreviewUpAndDownMvpPresenter(this, this, allData, nowSelectPage, keepOldFrom, category_id, toUserID, searchText, isCanLoadMore);
        mMvpPresenter.initSmartRefreshLayout(smartRefreshLayout);
        if (isCanLoadMore) {
            if (nowChoosePosition >= allData.size() - 2) {
                mMvpPresenter.requestMoreData();
            }
        }

        adapter = new Preview_up_and_down_adapter(R.layout.list_preview_up_down_item, allData, PreviewUpAndDownActivity.this, OldfromTo);
        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.iv_zan:
                    if (BaseConstans.hasLogin()) {
                        onclickZan();
                    } else {
                        goActivity(LoginActivity.class);
                    }
                    break;

                case R.id.tv_make:
                    toClickMake();
                    break;

                case R.id.iv_writer:
                case R.id.tv_describe:
                    Intent intent = new Intent(PreviewUpAndDownActivity.this, UserHomepageActivity.class);
                    intent.putExtra("toUserId", allData.get(position).getAdmin_id());
                    intent.putExtra("templateType", templateType);
                    startActivity(intent);
                    break;

                case R.id.iv_download_bj:
                    statisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, "10_bj_arrow");
                    mMvpPresenter.showBottomSheetDialog(templateItem.getVidoefile(), "", templateItem.getId() + "", templateItem);
                    break;

                case R.id.ll_comment:
                    BaseFullBottomSheetFragment fullSheetDialogFragment = new BaseFullBottomSheetFragment();
                    int height = (int) (ScreenUtil.getScreenHeight(this) * 0.3f);
                    fullSheetDialogFragment.setTopOffset(height);
                    fullSheetDialogFragment.setNowTemplateId(templateId);
                    fullSheetDialogFragment.setNowTemplateTitle(templateItem.getTitle());
                    fullSheetDialogFragment.setNowTemplateType(templateItem.getTemplate_type());
                    fullSheetDialogFragment.show(getSupportFragmentManager(), "FullSheetDialogFragment");
                    break;
                case R.id.tv_btn_follow:
                    if (BaseConstans.hasLogin()) {
                        mIsFollow = allData.get(position).getIs_follow() == 1;
                        LogUtil.d(TAG, "isfollow = " + mIsFollow);
                        LogUtil.d(TAG, "isfollow = " + allData.get(position).getIs_follow());
                        requestFollowThisUser(allData.get(position).getAdmin_id(), view, position);
                    } else {
                        goActivity(LoginActivity.class);
                    }
                    break;
                default:
                    break;
            }
        });

        viewPage2.setAdapter(adapter);
        viewPage2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position != -1) {
                    //置空数据
//                    fromTo=OldfromTo;
                    OldfromTo = keepOldFrom;
                    LogUtil.d("OOM", "当前位置为" + position);
                    adapter.NowPreviewChooseItem(position);
                    adapter.notifyItemChanged(position);
                    nowItemIsAd = allData.size() > 0 && allData.get(position).getAd() != null;
                    nowChoosePosition = position;
                    //判断当前滑动状态
                    nowSlideOrientationIsUp = nowChoosePosition < lastChoosePosition;
                    refeshData();

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
                            LogUtil.d("OOM", "请求更多数据");
                            mMvpPresenter.requestMoreData();
                        }
                    }
                    lastChoosePosition = position;
                    if (BaseConstans.hasLogin()) {
                        //主要用于刷新当前页面
                        mMvpPresenter.requestTemplateDetail(templateItem.getId() + "");
                    }
                }
                isSlideViewpager = true;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
        viewPage2.setCurrentItem(nowChoosePosition, false);
        if (nowPraise == 1 && BaseConstans.hasLogin()) {
            setIsZan(true);
        }
        if (BaseConstans.isFirstUseDownAndUpAct()) {
            rela_parent_show_alert.setVisibility(View.VISIBLE);
            ResourceStreamLoader resourceLoader = new ResourceStreamLoader(this, R.mipmap.guide);
            APNGDrawable apngDrawable = new APNGDrawable(resourceLoader);
            iv_guide.setImageDrawable(apngDrawable);
            rela_parent_show_alert.setOnClickListener(view -> rela_parent_show_alert.setVisibility(View.GONE));
            BaseConstans.setFirstUseDownAndUpAct();
        } else {
            rela_parent_show_alert.setVisibility(View.GONE);
        }
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
            protected void _onError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(Object data) {
                LogUtil.d("follow", StringUtil.beanToJSONString(data));
                if (mIsFollow) {
                    ((AppCompatTextView) view.findViewById(R.id.tv_btn_follow)).setText("关注");
                    mIsFollow = false;
                } else {
                    ((AppCompatTextView) view.findViewById(R.id.tv_btn_follow)).setText("取消关注");
                    mIsFollow = true;
                }
                mMvpPresenter.requestTemplateDetail(templateItem.getId() + "");
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);
    }


    /**
     * description ：刷新初始数据，保障翻页的时候数据还是最新的
     * creation date: 2020/7/2
     * user : zhangtongju
     */
    private void refeshData() {
        if (nowChoosePosition >= 0 && nowChoosePosition < allData.size()) {
            templateItem = allData.get(nowChoosePosition);
            templateId = templateItem.getId() + "";
            defaultnum = templateItem.getDefaultnum();
            is_picout = templateItem.getIs_picout();
            nowPraise = templateItem.getIs_praise();
            is_with_play = templateItem.getIs_with_play();
        }
    }


    private void setIsZan(boolean isCollect) {
        new_fag_template_item item1 = allData.get(nowChoosePosition);
        if (isCollect) {
            item1.setIs_praise(1);
            adapter.setIsZan(true);
        } else {
            item1.setIs_praise(0);
            adapter.setIsZan(false);
        }
        allData.set(nowChoosePosition, item1);
//        adapter.notifyItemChanged(nowChoosePosition);
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

            if (templateType.equals("1")) {
                statisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, " 12_like", templateItem.getTitle());
            } else {
                statisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, " 13_like", templateItem.getTitle());
            }
        } else {
            if (templateType.equals("1")) {
                statisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, " 12_cancel", templateItem.getTitle());
            } else {
                statisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, " 13_cancel", templateItem.getTitle());
            }
            iZanNum--;
        }
        new_fag_template_item item1 = allData.get(nowChoosePosition);
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
        isOnPause = true;
//        isIntoPause = true;
        LogUtil.d("OOM", "onPause");
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

    @Override
    @OnClick({R.id.ibBack})
    public void onClick(View view) {
        if (view.getId() == R.id.ibBack) {
            this.finish();
        }
    }

    /**
     * description ：点击了收藏功能
     * creation date: 2020/7/2
     * user : zhangtongju
     */
    public void onclickZan() {
        String needId;
        int id = templateItem.getTemplate_id();
        if (id != 0) {
            needId = id + "";
        } else {
            needId = templateItem.getId() + "";
        }
        mMvpPresenter.ZanTemplate(needId, templateItem.getTitle(), templateType);
    }

    @Override
    public void collectionResult(boolean collectionResult) {

        if (collectionResult) {
            new_fag_template_item item = allData.get(nowChoosePosition);
            item.setIs_collection(1);
            allData.set(nowChoosePosition, item);
        } else {
            new_fag_template_item item = allData.get(nowChoosePosition);
            item.setIs_collection(0);
            allData.set(nowChoosePosition, item);
        }

        switch (OldfromTo) {
            case FromToTemplate.ISHOMEFROMBJ:
                EventBus.getDefault().post(new templateDataCollectRefresh(nowChoosePosition, collectionResult, 1));
                break;
            case FromToTemplate.ISHOMEMYLIKE:
                break;
            case FromToTemplate.ISHOMEMYTEMPLATECOLLECT:
                EventBus.getDefault().post(new templateDataCollectRefresh(nowChoosePosition, collectionResult, 0));
                break;
            case FromToTemplate.ISMESSAGEMYPRODUCTION:
                break;
            case FromToTemplate.ISMESSAGEMYLIKE:
                break;
            case FromToTemplate.ISTEMPLATE:
                EventBus.getDefault().post(new templateDataCollectRefresh(nowChoosePosition, collectionResult, 3));
                break;
            case FromToTemplate.ISBJ:
                EventBus.getDefault().post(new templateDataCollectRefresh(nowChoosePosition, collectionResult, 4));
                break;
            case FromToTemplate.ISBJCOLLECT:
                EventBus.getDefault().post(new templateDataCollectRefresh(nowChoosePosition, collectionResult, 2));
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
    public void ZanResult() {
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
        if (!TextUtils.isEmpty(templateItem.getType()) && templateItem.getType().equals("1") && BaseConstans.getIncentiveVideo()) {
            Intent intent = new Intent(PreviewUpAndDownActivity.this, AdHintActivity.class);
            intent.putExtra("from", "PreviewActivity");
            intent.putExtra("templateTitle", templateItem.getTitle());
            startActivity(intent);
        } else {
            hasLoginToNext();
        }
    }

    @Override
    public void downVideoSuccess(String videoPath, String imagePath) {
        LogUtil.d("OOM", "downVideoSuccess");
        toCloseProgressDialog();

        //背景选择页面
        if (OldfromTo.equals(FromToTemplate.ISCHOOSEBJ)) {
            EventBus.getDefault().post(new DownVideoPath(videoPath));
            finish();
        } else {
            if (!TextUtils.isEmpty(imagePath)) {
                Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
                    if (originalImagePath.get(0).equals(imagePath)) {
                        createDownVideoPath = videoPath;
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
        Observable.just(progress).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
            if (!ondestroy) {
                if (integer >= 100) {
                    isDownIng = false;
                    waitingDialog_progress.closePragressDialog();
                } else {
                    if (!isDownIng) {
                        waitingDialog_progress.openProgressDialog();
                        isDownIng = true;
                    }
                    waitingDialog_progress.setProgress(integer + "%");
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
    @Override
    public void getTemplateFileSuccess(String filePath) {
        if (!ondestroy) {
            //file 文件下载成功
            this.TemplateFilePath = filePath;
            Log.d(TAG, "getTemplateFileSuccess: TemplateFilePath = " + TemplateFilePath);
            if (!TextUtils.isEmpty(templateItem.getVideotime()) && !templateItem.getVideotime().equals("0") && is_with_play == 1) {
                float videoTime = Float.parseFloat(templateItem.getVideotime());
                LogUtil.d("OOM", "bj.mp3=" + TemplateFilePath);
                String bjMp3 = TemplateFilePath + File.separator + "bj.mp3";
                AlbumManager.chooseAlbum(this, defaultnum, SELECTALBUM, this, "", (long) (videoTime * 1000), templateItem.getTitle(), bjMp3);
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
    public void showNewData(List<new_fag_template_item> newAllData, boolean isRefresh) {
        allData = newAllData;
        templateItem = newAllData.get(0);
        if (isRefresh) {
            isNeedAddaD = false;
            randomPosition = 0;
            insertMaxNum = 0;
            insertMinNum = 0;
        } else {
            //如果当前页面需要广告，则插入广告
            if (isNeedAddaD && allData.size() > randomPosition) {
                isNeedAddaD = false;
                new_fag_template_item item = new new_fag_template_item();
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
                new_fag_template_item item = new new_fag_template_item();
                item.setAd(ad);
                allData.add(randomPosition, item);
                adapter.notifyDataSetChanged();
                LogUtil.d("OOM", "广告插入的位置=" + randomPosition);
                //因为提前插入的，所以需要加1
                viewPage2.setCurrentItem(nowChoosePosition + 1, false);
            }
//            else {
//                //否则永远都是第一个
//                new_fag_template_item item = new new_fag_template_item();
//                item.setAd(ad);
//                allData.add(0, item);
//                adapter.notifyDataSetChanged();
//                viewPage2.setCurrentItem(nowChoosePosition + 1, false);
//                LogUtil.d("OOM", "超过数据限制，第一个为广告");
//            }
        } else {
            //下滑的情况
            randomPosition = insertMaxNum + randomPosition;
            insertMaxNum = randomPosition;
            LogUtil.d("OOM", "广告插入的位置=" + randomPosition);
            if (allData != null && allData.size() > randomPosition) {
                isNeedAddaD = false;
                new_fag_template_item item = new new_fag_template_item();
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
        LogUtil.d("OOM", "onResume");
        //出现bug 不能继续播放的问题
        if (!nowItemIsAd) {
            GSYVideoManager.onResume();
        }
        if (BaseConstans.hasLogin()) {
            //主要用于刷新当前页面
            mMvpPresenter.requestTemplateDetail(templateItem.getId() + "");
        }
        LogUtil.d("OOM", "onResume");
        WaitingDialog.closePragressDialog();
    }


    /**
     * description ：刷新当前页面数据 ，主要用来显示收藏状态
     * creation date: 2020/7/13
     * user : zhangtongju
     */

    @Override
    public void getTemplateLInfo(new_fag_template_item data) {
        if (data != null) {
            templateItem = data;
            setIsZan(data.getIs_praise() == 1);
            nowPraise = data.getIs_praise();
            templateType = data.getTemplate_type();
            //如果模板是来自一键模板，但是模板类型是背景，那么修改状态值
            if (!TextUtils.isEmpty(templateItem.getPre_url())) {
                OldfromTo = FromToTemplate.ISBJ;
            }
            adapter.setCommentCount(data.getComment());
            is_with_play = templateItem.getIs_with_play();
            //更新页面数据，防止数据不全的情况
            if (!isOnPause) {
                allData.set(nowChoosePosition, data);
                adapter.notifyItemChanged(nowChoosePosition);
            }
        }

    }


    private void toCloseProgressDialog() {
        if (!ondestroy) {
            Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> new Handler().postDelayed(WaitingDialog::closePragressDialog, 200));
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
        if (!TextUtils.isEmpty(musicPath) && is_with_play == 1) {
            if (OldfromTo.equals(FromToTemplate.ISBJ)) {
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


    /**
     * 这里逻辑优化下,背景页面是选择图片后在去下载背景
     * 现在修改为先下载视频，下载完成后在打开相册选择图片
     */
    private void hasLoginToNext() {

        LogUtil.d("OOM", "hasLoginToNext");
        switch (OldfromTo) {
            case FromToTemplate.ISHOMEFROMBJ:
                statisticsEventAffair.getInstance().setFlag(this, "8_Selectvideo");
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
                statisticsEventAffair.getInstance().setFlag(this, "8_Selectvideo");
                mMvpPresenter.DownVideo(templateItem.getVidoefile(), "", templateItem.getId() + "", false);
                break;
            case FromToTemplate.ISHOMEMYLIKE:
            case FromToTemplate.ISHOMEMYTEMPLATECOLLECT:
            case FromToTemplate.ISMESSAGEMYPRODUCTION:
            case FromToTemplate.ISMESSAGEMYLIKE:
            case FromToTemplate.ISTEMPLATE:
            case FromToTemplate.ISBJCOLLECT:
            case FromToTemplate.ISSEARCHBJ:
            case FromToTemplate.ISSEARCHTEMPLATE:
                if (templateType.equals("2")) {
                    LogUtil.d("OOM", "来自背景");
                    statisticsEventAffair.getInstance().setFlag(this, "8_Selectvideo");
                    mMvpPresenter.DownVideo(templateItem.getVidoefile(), "", templateItem.getId() + "", false);
                } else {
                    if (OldfromTo.equals(FromToTemplate.ISSEARCHBJ) || OldfromTo.equals(FromToTemplate.ISSEARCHTEMPLATE)) {
                        statisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, "4_search_make", templateItem.getTitle());
                    }
                    statisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, "mb_make", templateItem.getTitle());
                    adapter.pauseVideo();
                    mMvpPresenter.downZip(templateItem.getTemplatefile(), templateItem.getZipid());
                }
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
            if (OldfromTo.equals(FromToTemplate.ISBJ) || OldfromTo.equals(FromToTemplate.ISHOMEFROMBJ)) {
                statisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, "5_bj_Make", templateItem.getTitle());
                UiStep.isFromDownBj = true;
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
    public void resultFilePath(int tag, List<String> paths, boolean isCancel, ArrayList<AlbumFile> albumFileList) {
        if (!isCancel && !ondestroy && paths != null && paths.size() > 0) {
            chooseAlbumStatistics(paths);
            LogUtil.d("OOM", "pathsSize=" + paths.size());
            mattingImage.createHandle(PreviewUpAndDownActivity.this, isDone -> {
                if (isDone) {
                    Observable.just("tag").subscribeOn(AndroidSchedulers.mainThread()).subscribe(str -> {
                        if (OldfromTo.equals(FromToTemplate.ISBJ)) {
                            //背景模板文案
                            alert = "正在生成中~";
                        } else {
                            //一键模板不抠图的情况下
                            if (is_picout == 0) {
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
                            String path = paths.get(0);
                            String pathType = GetPathTypeModel.getInstance().getMediaType(path);
                            if (albumType.isImage(pathType)) {
                                //选择的时图片
                                if (OldfromTo.equals(FromToTemplate.ISBJ)) {
                                    statisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, "8_SelectImage");
                                }
                                if (templateItem.getIs_anime() != 1) {
                                    compressImage(paths, templateItem.getId() + "");
                                } else {
                                    //漫画需要去服务器请求
                                    compressImageForServers(paths, templateItem.getId() + "");
                                }
                            } else {
                                //选择的时视频
//                                if (OldfromTo.equals(FromToTemplate.ISBJ) || OldfromTo.equals(FromToTemplate.ISHOMEFROMBJ)) {
                                if (templateType.equals("2")) {
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
                                    if (!TextUtils.isEmpty(videoTime) && !videoTime.equals("0")) {
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
                        }).start();
                    });
                }
            });


        }
    }


    private void chooseAlbumStatistics(List<String> paths) {
        if (paths != null && paths.size() > 0) {
            for (String path : paths
            ) {
                if (albumType.isImage(GetPathTypeModel.getInstance().getMediaType(path))) {
                    {
                        statisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, "userChooseType", "选择的是图片");
                        LogUtil.d("OOM", "当前选择的是图片");
                    }
                } else {
                    statisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, "userChooseType", "选择的是视频");
                    LogUtil.d("OOM", "当前选择的是视频");
                }
            }
        }
    }


    private void compressImageForServers(List<String> paths, String templateId) {
        boolean hasCache = templateItem.getIs_anime() != 1;
        CompressionCuttingManage manage = new CompressionCuttingManage(PreviewUpAndDownActivity.this, templateId, hasCache, tailorPaths -> {
            if (OldfromTo.equals(FromToTemplate.ISBJ)) {
                mMvpPresenter.DownVideo(templateItem.getVidoefile(), tailorPaths.get(0), templateItem.getId() + "", false);
            } else if (OldfromTo.equals(FromToTemplate.ISHOMEFROMBJ)) {
                mMvpPresenter.DownVideo(templateItem.getVidoefile(), tailorPaths.get(0), templateItem.getId() + "", false);
            } else {
                toCloseProgressDialog();
                intoTemplateActivity(tailorPaths, TemplateFilePath);
            }
        });
        manage.compressImgAndCache(paths);
    }


    /***
     * 这里的逻辑是选择图片后抠图后再去的下载视频
     * @param templateId id
     */
    private void compressImage(List<String> paths, String templateId) {
        boolean hasCache = templateItem.getIs_anime() != 1;
        CompressionCuttingManage manage = new CompressionCuttingManage(PreviewUpAndDownActivity.this, templateId, hasCache, tailorPaths -> {
            if (templateType.equals("2")) {
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
        Intent intent = new Intent(this, TemplateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("paths", (ArrayList<String>) paths);
        bundle.putInt("isPicNum", defaultnum);
        bundle.putString("fromTo", OldfromTo);
        bundle.putInt("picout", templateItem.getIs_picout());
        bundle.putInt("is_anime", templateItem.getIs_anime());
        bundle.putString("templateName", templateItem.getTitle());
        bundle.putString("templateId", templateItem.getId() + "");
        bundle.putString("videoTime", templateItem.getVideotime());
        bundle.putStringArrayList("originalPath", (ArrayList<String>) originalImagePath);
        bundle.putString("templateFilePath", templateFilePath);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Message", bundle);
        intent.putExtra("person", templateItem);
        startActivity(intent);
    }


    /**
     * description ：裁剪页面裁剪成功后返回的数据,针对跳转到自定义创作页面
     * creation date: 2020/4/13
     * user : zhangtongju
     */
    @Subscribe
    public void onEventMainThread(CreateCutCallback event) {
        LogUtil.d("OOM", "event.getCoverPath()=" + event.getCoverPath() + "createDownVideoPath=" + createDownVideoPath + "createDownVideoPath=" + createDownVideoPath + "event.isNeedCut()=" + event.isNeedCut());
        intoCreationTemplateActivity(event.getCoverPath(), createDownVideoPath, event.getOriginalPath(), event.isNeedCut());

    }

    /**
     * description ：裁剪页面裁剪成功后返回的数据,针对跳转到一键模板
     * creation date: 2020/4/13
     * user : zhangtongju
     */
    @Subscribe
    public void onEventMainThread(MattingVideoEnity event) {
        if (event.getTag() == 0) {
            originalImagePath.clear();
            ArrayList<String> paths = new ArrayList<>();
            paths.add(event.getMattingPath());
            Intent intent = new Intent(this, TemplateActivity.class);
            Bundle bundle = new Bundle();
            //用户没选择抠图
            if (event.getOriginalPath() != null) {
                originalImagePath.add(event.getOriginalPath());
                bundle.putInt("picout", 1);
            } else {
                originalImagePath = null;
                bundle.putInt("picout", 0);
            }
            bundle.putStringArrayList("paths", paths);
            bundle.putInt("isPicNum", defaultnum);
            bundle.putString("fromTo", OldfromTo);
            bundle.putString("primitivePath", event.getPrimitivePath());
            bundle.putInt("is_anime", templateItem.getIs_anime());
            bundle.putString("templateName", templateItem.getTitle());
            intent.putExtra("person", templateItem);//直接存入被序列化的对象实例
            bundle.putString("templateId", templateItem.getId() + "");
            bundle.putString("videoTime", templateItem.getVideotime());
            bundle.putStringArrayList("originalPath", (ArrayList<String>) originalImagePath);
            bundle.putString("templateFilePath", TemplateFilePath);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("Message", bundle);
            startActivity(intent);
        }
    }

    private void intoCreationTemplateActivity(String imagePath, String videoPath, String originalPath, boolean isNeedCut) {
        Intent intent = new Intent(PreviewUpAndDownActivity.this, CreationTemplateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("paths", imagePath);
        bundle.putSerializable("bjTemplateTitle", templateItem.getTitle());
        bundle.putString("originalPath", originalPath);
        bundle.putString("video_path", videoPath);
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
    }


    /**
     * description ：激励视频回调
     * creation date: 2020/4/13
     * user : zhangtongju
     */
    @Subscribe
    public void onEventMainThread(showAdCallback event) {
        if (event != null && event.getIsFrom().equals("PreviewActivity")) {
            //需要激励视频
            if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
                VideoAdManager videoAdManager = new VideoAdManager();
                videoAdManager.showVideoAd(this, AdConfigs.AD_stimulate_video, new VideoAdCallBack() {
                    @Override
                    public void onVideoAdSuccess() {
                        statisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, "video_ad_alert_request_sucess");
                        LogUtil.d("OOM", "onVideoAdSuccess");
                    }

                    @Override
                    public void onVideoAdError(String s) {
                        statisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, "video_ad_alert_request_fail");
                        LogUtil.d("OOM", "onVideoAdError" + s);
                        BaseConstans.TemplateHasWatchingAd = true;
                        hasLoginToNext();
                    }

                    @Override
                    public void onVideoAdClose() {
                        LogUtil.d("OOM", "onVideoAdClose");
                        BaseConstans.TemplateHasWatchingAd = true;
                        hasLoginToNext();
                    }

                    @Override
                    public void onVideoAdSkip() {
                        LogUtil.d("OOM", "onVideoAdSkip");
                    }

                    @Override
                    public void onVideoAdComplete() {
                    }

                    @Override
                    public void onVideoAdClicked() {
                        LogUtil.d("OOM", "onVideoAdClicked");
                    }
                });
            } else {
                hasLoginToNext();
            }
        }


    }


    @Subscribe
    public void onEventMainThread(ReplayMessageEvent event) {
        if (BaseConstans.hasLogin()) {
            //主要用于刷新当前页面
            mMvpPresenter.requestTemplateDetail(templateItem.getId() + "");
        }
    }

}

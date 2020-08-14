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

import androidx.viewpager2.widget.ViewPager2;

import com.bigkoo.convenientbanner.utils.ScreenUtil;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.Preview_up_and_down_adapter;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.constans.UiStep;
import com.flyingeffects.com.enity.CreateCutCallback;
import com.flyingeffects.com.enity.DownVideoPath;
import com.flyingeffects.com.enity.ListForUpAndDown;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.enity.showAdCallback;
import com.flyingeffects.com.enity.templateDataCollectRefresh;
import com.flyingeffects.com.enity.templateDataZanRefresh;
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
import java.util.List;
import java.util.Random;

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

    @BindView(R.id.page2)
    ViewPager2 viewPage2;

    @BindView(R.id.refresh)
    SmartRefreshLayout smartRefreshLayout;

    PreviewUpAndDownMvpPresenter Presenter;

    private Preview_up_and_down_adapter adapter;

    private List<new_fag_template_item> allData = new ArrayList<>();
    private new_fag_template_item templateItem;
    //当前选中的页码
    private int nowChoosePosition;

    //上次选中的页码
    private int lastChoosePosition;

    //当前滑动趋势，false 是向下，true 是向上
    private boolean nowSlideOrientationIsUp = false;


    boolean readOnly;
    private boolean fromToMineCollect;

    //来着来个页面
    private String fromTo;

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


    private boolean nowItemIsAd = false;


    @Override
    protected int getLayoutId() {
        return R.layout.act_preview_up_and_down;
    }

    private String templateId;

    private String category_id;

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
        //默认插入最值为当前位置
        insertMaxNum = nowChoosePosition;
        insertMinNum = nowChoosePosition;
        templateItem = allData.get(nowChoosePosition);
        is_picout = templateItem.getIs_picout();
        defaultnum = templateItem.getDefaultnum();
        readOnly = getIntent().getBooleanExtra("readOnly", false);
        fromToMineCollect = getIntent().getBooleanExtra("fromToMineCollect", false);
        fromTo = getIntent().getStringExtra("fromTo");
        category_id=getIntent().getStringExtra("category_id");
        templateId = templateItem.getId() + "";
        //需要得到之前allData 已经滑到的页数和分类的类别以及是模板页面或者背景页面等
        int nowSelectPage = getIntent().getIntExtra("nowSelectPage", 1);
        nowPraise = templateItem.getIs_praise();
        Presenter = new PreviewUpAndDownMvpPresenter(this, this, allData, nowSelectPage, fromTo, category_id, fromToMineCollect);
        Presenter.initSmartRefreshLayout(smartRefreshLayout);
        if (nowChoosePosition >= allData.size() - 2) {
            Presenter.requestMoreData();
        }
        adapter = new Preview_up_and_down_adapter(R.layout.list_preview_up_down_item, allData, PreviewUpAndDownActivity.this, readOnly, fromTo);
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
                    Intent intent = new Intent(PreviewUpAndDownActivity.this, UserHomepageActivity.class);
                    intent.putExtra("toUserId", allData.get(position).getAdmin_id());
                    startActivity(intent);


                    break;

                case R.id.iv_download_bj:
                    statisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, "10_bj_arrow");
                    Presenter.showBottomSheetDialog(templateItem.getVidoefile(), "", templateItem.getId(), templateItem);
                    break;


                case R.id.ll_comment:
                    BaseFullBottomSheetFragment fullSheetDialogFragment = new BaseFullBottomSheetFragment();
                    int height = ScreenUtil.getScreenHeight(this) / 3;
                    fullSheetDialogFragment.setTopOffset(height);
                    fullSheetDialogFragment.setNowTemplateId(templateId);
                    fullSheetDialogFragment.show(getSupportFragmentManager(), "FullSheetDialogFragment");
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
                    LogUtil.d("OOM", "当前位置为" + position);
                    adapter.NowPreviewChooseItem(position);
                    adapter.notifyItemChanged(position);
                    nowItemIsAd = allData.size() > 0 && allData.get(position).getAd() != null;
                    nowChoosePosition = position;
                    //判断当前滑动状态
                    nowSlideOrientationIsUp = nowChoosePosition < lastChoosePosition;
                    refeshData();
                    if (position >= insertMaxNum || position <= insertMinNum) {
                        Presenter.requestAD();
                        LogUtil.d("OOM", "开始请求广告position=" + position + "insertMaxNum=" + insertMaxNum + "insertMinNum=" + insertMinNum);
                    }
                    int allDataCount = allData.size();
                    if (position == allDataCount - 3) {
                        LogUtil.d("OOM", "请求更多数据");
                        Presenter.requestMoreData();
                    }
                    lastChoosePosition = position;
                    if (BaseConstans.hasLogin()) {
                        //主要用于刷新当前页面
                        Presenter.requestTemplateDetail(templateItem.getId());
                    }

                }
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
        } else {
            iZanNum--;
        }
        new_fag_template_item item1 = allData.get(nowChoosePosition);
        item1.setPraise(iZanNum + "");
        allData.set(nowChoosePosition, item1);
        adapter.setIsZanCount(iZanNum);
        if (fromToMineCollect) {
            //模板收藏
            if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.ISFROMTEMPLATE)) {
                EventBus.getDefault().post(new templateDataZanRefresh(nowChoosePosition, iZanNum, isAdd, 0));
            } else if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.ISFROMUPDATEBJ)) {
                //我上传的背景
                EventBus.getDefault().post(new templateDataZanRefresh(nowChoosePosition, iZanNum, isAdd, 1));
            } else {
                //背景 收藏
                EventBus.getDefault().post(new templateDataZanRefresh(nowChoosePosition, iZanNum, isAdd, 2));
            }
        } else {
            if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.ISFROMTEMPLATE)) {
                EventBus.getDefault().post(new templateDataZanRefresh(nowChoosePosition, iZanNum, isAdd, 3));
            } else {
                EventBus.getDefault().post(new templateDataZanRefresh(nowChoosePosition, iZanNum, isAdd, 4));
            }
        }
    }

    @Override
    protected void initAction() {

    }


    @Override
    protected void onPause() {
        super.onPause();
        GSYVideoManager.onPause();
//        isIntoPause = true;
        LogUtil.d("OOM", "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //出现bug 不能继续播放的问题
        if (!nowItemIsAd) {
            GSYVideoManager.onResume();
        }
        LogUtil.d("OOM", "onResume");
        WaitingDialog.closePragressDialog();
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
        if (fromToMineCollect) {
            //模板收藏
            if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.ISFROMTEMPLATE)) {
                Presenter.ZanTemplate(templateItem.getId() + "", templateItem.getTitle(), 1 + "");
            } else if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.ISFROMUPDATEBJ)) {
                //我上传的背景
                Presenter.ZanTemplate(templateItem.getTemplate_id() + "", templateItem.getTitle(), 2 + "");
            } else {
                //背景 收藏
                Presenter.ZanTemplate(templateItem.getId() + "", templateItem.getTitle(), 2 + "");
            }
        } else {
            if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.ISFROMTEMPLATE)) {
                Presenter.ZanTemplate(templateItem.getId(), templateItem.getTitle(), 1 + "");
            } else {
                Presenter.ZanTemplate(templateItem.getId(), templateItem.getTitle(), 2 + "");
            }
        }
    }


    @Override
    public void collectionResult(boolean collectionResult) {


        if(collectionResult){
            new_fag_template_item item=   allData.get(nowChoosePosition);
            item.setIs_collection(1);
            allData.set(nowChoosePosition, item);
        }else{
            new_fag_template_item item=   allData.get(nowChoosePosition);
            item.setIs_collection(0);
            allData.set(nowChoosePosition, item);
        }


        if (fromToMineCollect) {
            if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.ISFROMTEMPLATE)) {
                EventBus.getDefault().post(new templateDataCollectRefresh(nowChoosePosition, collectionResult, 0));
            } else if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.ISFROMUPDATEBJ)) {
                //我上传的背景
                EventBus.getDefault().post(new templateDataCollectRefresh(nowChoosePosition, collectionResult, 1));
            } else {
                //背景 收藏
                EventBus.getDefault().post(new templateDataCollectRefresh(nowChoosePosition, collectionResult, 2));
            }
        } else {
            if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.ISFROMTEMPLATE)) {
                EventBus.getDefault().post(new templateDataCollectRefresh(nowChoosePosition, collectionResult, 3));
            } else {
                EventBus.getDefault().post(new templateDataCollectRefresh(nowChoosePosition, collectionResult, 4));
            }
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
        if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.ISFROMEDOWNVIDEO)) {
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
                        intent.putExtra("comeFrom", FromToTemplate.ISFROMEDOWNVIDEO);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    } else {
                        intoCreationTemplateActivity(imagePath, videoPath, originalImagePath.get(0), true);
                    }
                });
            } else {
                Presenter.getSpliteMusic(videoPath);
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
            if (!TextUtils.isEmpty(templateItem.getVideotime()) && !templateItem.getVideotime().equals("0")) {
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
        if (isRefresh) {
            isNeedAddaD = false;
            randomPosition = 0;
            insertMaxNum = 0;
            insertMinNum = 0;
        } else {
            //如果当前页面需要广告，则插入广告
            if (isNeedAddaD && allData != null && allData.size() > randomPosition) {
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
            } else {
                //否则永远都是第一个
                new_fag_template_item item = new new_fag_template_item();
                item.setAd(ad);
                allData.add(0, item);
                adapter.notifyDataSetChanged();
                viewPage2.setCurrentItem(nowChoosePosition + 1, false);
                LogUtil.d("OOM", "超过数据限制，第一个为广告");
            }
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


    /**
     * description ：刷新当前页面数据 ，主要用来显示收藏状态
     * creation date: 2020/7/13
     * user : zhangtongju
     */

    @Override
    public void getTemplateLInfo(new_fag_template_item data) {
        if (data != null) {
            setIsZan(data.getIs_praise() == 1);
            nowPraise = data.getIs_praise();
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
        if (!TextUtils.isEmpty(musicPath)) {
            if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.ISFROMBJ)) {
                AlbumManager.chooseAlbum(this, 1, SELECTALBUMFROMBJ, this, "", (long) adapter.getVideoDuration(), templateItem.getTitle(), musicPath);
            } else if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.ISFROMUPDATEBJ)) {
                AlbumManager.chooseAlbum(this, 1, SELECTALBUMFROMBJ, this, "", (long) adapter.getVideoDuration(), templateItem.getTitle(), musicPath);
            }
        } else {
            AlbumManager.chooseAlbum(this, 1, SELECTALBUMFROMBJ, this, "");
        }
    }

    @Override
    public void onclickCollect() {
        if (fromToMineCollect) {
            //模板收藏
            if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.ISFROMTEMPLATE)) {
                Presenter.collectTemplate(templateItem.getId() + "", templateItem.getTitle(), 1 + "");
            } else if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.ISFROMUPDATEBJ)) {
                //我上传的背景
                Presenter.collectTemplate(templateItem.getTemplate_id() + "", templateItem.getTitle(), 2 + "");
            } else {
                //背景 收藏
                Presenter.collectTemplate(templateItem.getId() + "", templateItem.getTitle(), 2 + "");
            }
        } else {
            if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.ISFROMTEMPLATE)) {
                Presenter.collectTemplate(templateItem.getId(), templateItem.getTitle(), 1 + "");
            } else {
                Presenter.collectTemplate(templateItem.getId(), templateItem.getTitle(), 2 + "");
            }
        }
    }


    /**
     * 这里逻辑优化下,背景页面是选择图片后在去下载背景
     * 现在修改为先下载视频，下载完成后在打开相册选择图片
     */
    private void hasLoginToNext() {
        if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.ISFROMBJ)) {
            LogUtil.d("OOM", "来自背景");
            statisticsEventAffair.getInstance().setFlag(this, "8_Selectvideo");
            Presenter.DownVideo(templateItem.getVidoefile(), "", templateItem.getId(), false);
        } else if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.ISFROMUPDATEBJ)) {
            LogUtil.d("OOM", "来自背景");
            statisticsEventAffair.getInstance().setFlag(this, "8_Selectvideo");
            Presenter.DownVideo(templateItem.getVidoefile(), "", templateItem.getId(), false);
        } else if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.ISFROMEDOWNVIDEO)) {
            //来自下载背景，就是用户重新选择背景页面
            Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> new Handler().postDelayed(() -> {
                if (!ondestroy) {
                    String alert = "正在生成中...";
                    WaitingDialog.openPragressDialog(PreviewUpAndDownActivity.this, alert);
                    Presenter.DownVideo(templateItem.getVidoefile(), "", templateItem.getId(), true);
                }
            }, 200));
        } else {
            LogUtil.d("OOM", "来自其他");
            if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.ISFROMSEARCH)) {
                statisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, "4_search_make", templateItem.getTitle());
            }
            statisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, "mb_make", templateItem.getTitle());
            adapter.pauseVideo();
            Presenter.downZip(templateItem.getTemplatefile(), templateItem.getZipid());
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
        adapter.pauseVideo();
        if (!DoubleClick.getInstance().isFastZDYDoubleClick(3000)) {
            if (!TextUtils.isEmpty(fromTo)) {
                if (fromTo.equals(FromToTemplate.ISFROMBJ) || fromTo.equals(FromToTemplate.ISFROMUPDATEBJ)) {
                    statisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, "5_bj_Make", templateItem.getTitle());
                    UiStep.isFromDownBj = true;
                }
            }

            if (BaseConstans.hasLogin()) {
                //登录可能被挤下去，所以这里加个用户信息刷新请求
                Presenter.requestUserInfo();
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
                        if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.ISFROMBJ)) {
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
                                if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.ISFROMBJ)) {
                                    statisticsEventAffair.getInstance().setFlag(PreviewUpAndDownActivity.this, "8_SelectImage");
                                }
                                if (templateItem.getIs_anime() != 1) {
                                    compressImage(paths, templateItem.getId());
                                } else {
                                    //漫画需要去服务器请求
                                    compressImageForServers(paths, templateItem.getId());
                                }
                            } else {
                                //选择的时视频
                                if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.ISFROMBJ) || !TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.ISFROMUPDATEBJ)) {
                                    Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
                                        toCloseProgressDialog();
                                        if (originalImagePath.get(0).equals(paths.get(0))) {
                                            createDownVideoPath = videoPath;
                                            //源图地址和剪切之后的地址完全一样，那说明只有一个情况，就是当前选择的素材是视频的情况，那么需要去得到视频的第一针，然后传过去
                                            Intent intent = new Intent(PreviewUpAndDownActivity.this, VideoCropActivity.class);
                                            intent.putExtra("videoPath", paths.get(0));
                                            intent.putExtra("comeFrom", FromToTemplate.ISFROMEDOWNVIDEO);
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
            if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.ISFROMBJ)) {
                Presenter.DownVideo(templateItem.getVidoefile(), tailorPaths.get(0), templateItem.getId(), false);
            } else if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.ISFROMUPDATEBJ)) {
                Presenter.DownVideo(templateItem.getVidoefile(), tailorPaths.get(0), templateItem.getId(), false);
            } else {
                toCloseProgressDialog();
                intoTemplateActivity(tailorPaths, TemplateFilePath);
            }
        });
        manage.CompressImgAndCache(paths);
    }


    /***
     * 这里的逻辑是选择图片后抠图后再去的下载视频
     * @param templateId id
     */
    private void compressImage(List<String> paths, String templateId) {
        boolean hasCache = templateItem.getIs_anime() != 1;
        CompressionCuttingManage manage = new CompressionCuttingManage(PreviewUpAndDownActivity.this, templateId, hasCache, tailorPaths -> {
            if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.ISFROMBJ)) {
                Presenter.DownVideo(templateItem.getVidoefile(), tailorPaths.get(0), templateItem.getId(), false);
            } else if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.ISFROMUPDATEBJ)) {
                Presenter.DownVideo(templateItem.getVidoefile(), tailorPaths.get(0), templateItem.getId(), false);
            } else {
                toCloseProgressDialog();
                intoTemplateActivity(tailorPaths, TemplateFilePath);
            }
        });
        manage.ToMatting(paths);
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
        bundle.putString("fromTo", fromTo);
        bundle.putInt("picout", templateItem.getIs_picout());
        bundle.putInt("is_anime", templateItem.getIs_anime());
        bundle.putString("templateName", templateItem.getTitle());
        bundle.putString("templateId", templateItem.getId());
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
            bundle.putString("fromTo", fromTo);
            bundle.putString("primitivePath", event.getPrimitivePath());
            bundle.putInt("is_anime", templateItem.getIs_anime());
            bundle.putString("templateName", templateItem.getTitle());
            intent.putExtra("person", templateItem);//直接存入被序列化的对象实例
            bundle.putString("templateId", templateItem.getId());
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
}

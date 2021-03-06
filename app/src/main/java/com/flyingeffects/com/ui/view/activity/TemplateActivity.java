package com.flyingeffects.com.ui.view.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.TemplateThumbAdapter;
import com.flyingeffects.com.adapter.TemplateViewPager;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.commonlyModel.GetPathType;
import com.flyingeffects.com.databinding.ActivityTemplateBinding;
import com.flyingeffects.com.entity.CutSuccess;
import com.flyingeffects.com.entity.DownVideoPath;
import com.flyingeffects.com.entity.TabEntity;
import com.flyingeffects.com.entity.TemplateThumbItem;
import com.flyingeffects.com.entity.NewFragmentTemplateItem;
import com.flyingeffects.com.manager.AdConfigs;
import com.flyingeffects.com.manager.AlbumManager;
import com.flyingeffects.com.manager.AnimForViewShowAndHide;
import com.flyingeffects.com.manager.CompressionCuttingManage;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.AlbumChooseCallback;
import com.flyingeffects.com.ui.interfaces.VideoPlayerCallbackForTemplate;
import com.flyingeffects.com.ui.interfaces.view.TemplateMvpView;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.ui.model.GetPathTypeModel;
import com.flyingeffects.com.ui.model.VideoFusionModel;
import com.flyingeffects.com.ui.presenter.TemplatePresenter;
import com.flyingeffects.com.ui.view.ViewChooseTemplate;
import com.flyingeffects.com.ui.view.dialog.CommonMessageDialog;
import com.flyingeffects.com.ui.view.dialog.LoadingDialog;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.TimeUtils;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.view.MattingVideoEnity;
import com.shixing.sxve.ui.AlbumType;
import com.shixing.sxve.ui.AssetDelegate;
import com.shixing.sxve.ui.SxveConstans;
import com.shixing.sxve.ui.model.GroupModel;
import com.shixing.sxve.ui.model.MediaUiModel;
import com.shixing.sxve.ui.model.MediaUiModel2;
import com.shixing.sxve.ui.model.TemplateModel;
import com.shixing.sxve.ui.model.TextUiModel;
import com.shixing.sxve.ui.view.TemplateView;
import com.shixing.sxve.ui.view.WaitingDialog;
import com.shixing.sxve.ui.view.WaitingDialog_progress;
import com.shixing.sxvideoengine.SXTemplate;
import com.shixing.sxvideoengine.SXTemplatePlayer;
import com.yanzhenjie.album.AlbumFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * ????????????
 * ???????????????gif????????????,
 */
public class TemplateActivity extends BaseActivity implements TemplateMvpView, AssetDelegate, AlbumChooseCallback {
    private static final String TAG = "TemplateActivity";

    public static final String TEMPLATE_BUNDLE_NAME = "Message";
    public static final String TEMPLATE_ITEM_NAME = "person";
    public static final String INTENT_FROM_TO = "fromTo";
    public static final String INTENT_IS_PIC_NUM = "isPicNum";
    public static final String INTENT_TEMPLATE_ID = "templateId";
    public static final String INTENT_IS_SPECIAL = "isSpecial";
    public static final String INTENT_TEMPLATE_FILE_PATH = "templateFilePath";
    public static final String INTENT_IMAGE_PATH = "paths";
    public static final String INTENT_VIDEO_TIME = "videoTime";
    public static final String INTENT_CHANGE_TEMPLATE_POSITION = "changeTemplatePosition";
    public static final String INTENT_PRIMITIVE_PATH = "primitivePath";
    public static final String INTENT_PIC_OUT = "picout";
    public static final String INTENT_ORIGINAL_PATH = "originalPath";
    public static final String INTENT_TEMPLATE_NAME = "templateName";
    public static final String INTENT_IS_ANIME = "is_anime";

    private Context mContext;

    RecyclerView recyclerView;

    private SXTemplatePlayer mPlayer;

    private TemplatePresenter presenter;
    private List<String> imgPath = new ArrayList<>();
    private TemplateModel mTemplateModel;

    private File mFolder;
    /**
     * ??????????????????????????????
     */
    private TemplateThumbAdapter templateThumbAdapter;
    private ArrayList<TemplateThumbItem> listItem = new ArrayList<>();

    private ArrayList<TemplateView> mTemplateViews;
    private String mAudio1Path;
    private static final String MUSIC_PATH = "/bj.mp3";

    private int nowSeekBarProgress;

    /**
     * ????????????,???????????????????????????????????????null,??????????????????????????????????????????
     */
    private List<String> mOriginalPathList;
    /**
     * intent???????????????????????????
     */
    private String mTemplateFilePath;
    /**
     * ??????????????????
     */
    private int changeTemplatePosition;

    /**
     * ??????????????????
     */
    private int bottomButtonCount;
    /**
     * ??????????????????
     */
    private int needAssetsCount;
    private String templateName;

    private String fromTo;
    //??????
    private int cutVideoTag = 3;


    private String templateId;


    private static final int REQUEST_SINGLE_MEDIA = 11;

    /**
     * ??????????????????????????????
     */
    private int pickIndex;

    /**
     * ????????????????????????group ?????????
     */
    private int pickGroupIndex;

    /**
     * ?????????????????????
     */
    private boolean isRealtime = true;

    private boolean isPlaying = false;

    private int nowChoosePosition;
    private int lastChoosePosition;


    /**
     * ????????????????????????1???????????????
     */
    private int nowTemplateIsAnim;


    /**
     * ????????????????????????
     */
    private int nowTemplateIsMattingVideo;

    /**
     * ????????????????????????
     */
    private boolean isCanChooseVideo = false;

    /**
     * ??????????????????
     */
    private String videoTime;


    /**
     * ????????????????????????????????????????????????????????????
     */
    private Bitmap videoMattingCaver;

    private boolean nowIsChooseMatting = true;

    /**
     * 0 ????????????????????????1??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     */
    private int picout;

    private long needDuration;

    private boolean nowIsPhotographAlbum = false;

    /**
     * ??????????????????????????????
     */
    private WaitingDialog_progress waitingDialogProgress;


    //?????????????????? 0?????????????????? 1??????????????????  2 ?????????????????? 3??????????????????
    private int nowChooseMusic = 0;

    /**
     * ?????????????????????????????????
     */
    private String nowSpliteMusic;

    /**
     * ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????originalPath ???????????????????????????
     */
    private String primitivePath;

    private NewFragmentTemplateItem templateItem;

    /**
     * ???????????????gif??????
     */
    private boolean nowIsGifTemplate = false;

    /**
     * ?????????????????????????????????????????????ui ????????????????????????????????????????????????
     */
    private boolean isToSing = false;

    /**
     * ???isToSing ??????????????????????????????????????????????????????????????????
     */
    private boolean isSpecial = false;

    private LoadingDialog mLoadingDialog;

    private int api_type;
    private ActivityTemplateBinding mBinding;

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initView() {
        mContext = TemplateActivity.this;

        mBinding = ActivityTemplateBinding.inflate(getLayoutInflater());
        View rootView = mBinding.getRoot();
        setContentView(rootView);

        EventBus.getDefault().register(this);
        mLoadingDialog = buildProgressDialog();
        getLifecycle().addObserver(mLoadingDialog);
        initData();
        setOnClickListener();
        mBinding.ivTopBack.setOnClickListener(this);
        mBinding.tvTopSubmit.setVisibility(View.VISIBLE);
        mBinding.tvTopSubmit.setText("?????????");
        //??????????????????????????????????????????????????????????????????
        if (nowTemplateIsAnim == 1) {
            needAssetsCount = imgPath.size();
        }
        if (nowTemplateIsAnim == 2) {
            isToSing = true;
        }
        presenter = new TemplatePresenter(this, this, fromTo, templateName, templateId, templateItem.getType());
        LogUtil.d("OOM3", "templateName=" + templateName);

        if (mOriginalPathList != null && mOriginalPathList.size() > 0) {
            int totalMaterial = needAssetsCount;
            if (mOriginalPathList.size() < totalMaterial) {
                //???????????????????????????????????????????????????????????????????????????????????????????????????
                for (int i = 0; i < totalMaterial; i++) {
                    if (mOriginalPathList.size() > i && !TextUtils.isEmpty(mOriginalPathList.get(i))) {
                        LogUtil.d("OOM", "????????????");
                    } else {
                        mOriginalPathList.add(SxveConstans.default_bg_path);
                        imgPath.add(SxveConstans.default_bg_path);
                    }
                }
            }
        }

        if (templateItem.getIs_pic() != 1) {
            if (!TextUtils.isEmpty(videoTime) && !"0".equals(videoTime) && AlbumType.isVideo(GetPathType.getInstance().
                    getMediaType(imgPath.get(0)))) {
                nowTemplateIsMattingVideo = 1;
                //?????????????????????????????????????????????
                if (mOriginalPathList != null && mOriginalPathList.size() != 0) {
                    handler.sendEmptyMessage(1);
                    new Thread(() -> presenter.getMattingVideoCover(mOriginalPathList.get(0))).start();
                }
            }
        }

        if (!TextUtils.isEmpty(videoTime) && !"0".equals(videoTime)) {
            isCanChooseVideo = true;
        }

        mFolder = new File(mTemplateFilePath);
        mAudio1Path = mFolder.getPath() + MUSIC_PATH;
        FileManager manager = new FileManager();
        String dir = manager.getFileCachePath(this, "");

        mTemplateViews = new ArrayList<>();

        SxveConstans.default_bg_path = new
                File(dir, "default_bj.png").
                getPath();

        mBinding.seekBar.setOnSeekBarChangeListener(seekBarListener);

        mBinding.switchButton.setOnCheckedChangeListener((view, isChecked) -> {
            LogUtil.d("OOM3", "????????????CheckedChangeListener");
            if (!isFastDoubleClick()) {
                mTemplateModel.resetUi();
                if (!isChecked) {
                    nowIsChooseMatting = false;
                    if (nowTemplateIsMattingVideo == 1 && !AlbumType.isImage(GetPathType.getInstance().getPathType(imgPath.get(0)))) {
                        if (mOriginalPathList != null && mOriginalPathList.size() != 0) {
                            changeMaterialCallbackForVideo(null, mOriginalPathList.get(0),
                                    false);

                        } else {
                            changeMaterialCallbackForVideo(null, imgPath.get(0), false);
                        }
                    } else {
                        StatisticsEventAffair.getInstance().setFlag(TemplateActivity.this, "1_mb_bj_Cutoutopen");
                        //??????????????????????????????
                        presenter.changeMaterial(mOriginalPathList, bottomButtonCount, needAssetsCount);
                    }
                } else {
                    chooseChecked();
                }

                if (mPlayer != null) {
                    mPlayer.pause();
                    mBinding.ivPlay.setImageResource(R.mipmap.iv_play);
                    isPlaying = false;
                }
                showPreview(false, true);
                AnimForViewShowAndHide.getInstance().show(mBinding.editViewContainer);
            }
        });

        LogUtil.d("OOM3", "fromTo=" + fromTo);
        if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.PICTUREALBUM)) {
            nowIsPhotographAlbum = true;
            mBinding.llMatting.setVisibility(View.GONE);
        } else {
            int is_pic = templateItem.getIs_pic();
            if (is_pic == 1) {
                mBinding.llMatting.setVisibility(View.GONE);
            }
        }

        //??????????????????
        if (isToSing || isSpecial) {
            Log.d("OOM22", "isToSing=" + isToSing);
            Log.d("OOM22", "isSpecial=" + isSpecial);
            mBinding.llProgress.setVisibility(View.GONE);
            mBinding.llViewpagerContainer.setVisibility(View.GONE);
        }


    }

    /**
     * ???????????????
     */
    private void initData() {
        Bundle bundle = getIntent().getBundleExtra(TEMPLATE_BUNDLE_NAME);
        templateItem = (NewFragmentTemplateItem) getIntent().getSerializableExtra(TEMPLATE_ITEM_NAME);
        String templateType = templateItem.getTemplate_type();
        if (!TextUtils.isEmpty(templateType) && "5".equals(templateType)) {
            nowIsGifTemplate = true;
        }
        if (bundle != null) {
            fromTo = bundle.getString(INTENT_FROM_TO);
            needAssetsCount = bundle.getInt(INTENT_IS_PIC_NUM);
            templateId = bundle.getString(INTENT_TEMPLATE_ID);
            mTemplateFilePath = bundle.getString(INTENT_TEMPLATE_FILE_PATH);
            imgPath = bundle.getStringArrayList(INTENT_IMAGE_PATH);
            videoTime = bundle.getString(INTENT_VIDEO_TIME);
            changeTemplatePosition = bundle.getInt(INTENT_CHANGE_TEMPLATE_POSITION);
            primitivePath = bundle.getString(INTENT_PRIMITIVE_PATH);
            picout = bundle.getInt(INTENT_PIC_OUT);
            mOriginalPathList = bundle.getStringArrayList(INTENT_ORIGINAL_PATH);
            templateName = bundle.getString(INTENT_TEMPLATE_NAME);
            nowTemplateIsAnim = bundle.getInt(INTENT_IS_ANIME);
            api_type = bundle.getInt(INTENT_IS_SPECIAL);
            if (api_type != 0) {
                isSpecial = true;
            }
        }
    }

    private void setOnClickListener() {
        mBinding.tvTopSubmit.setOnClickListener(this::onViewClick);
        mBinding.ivPlay.setOnClickListener(this::onViewClick);
        mBinding.ivTopBack.setOnClickListener(this::onViewClick);
    }

    private void onViewClick(View view) {
        if (view == mBinding.tvTopSubmit){
            topSubmitClicked();
        }else if (view == mBinding.ivPlay){
            if (!DoubleClick.getInstance().isFastZDYDoubleClick(500)) {
                onclickPlaying();
            }
        }else if (view == mBinding.ivTopBack){
            onBackPressed();
        }

    }

    private void topSubmitClicked() {
        if (!DoubleClick.getInstance().isFastZDYDoubleClick(3000)) {
            if (isToSing) {
                MediaUiModel2 mediaUi2 = (MediaUiModel2) mTemplateModel.getAssets().get(lastChoosePosition).ui;
                String path = mediaUi2.getSnapPath(Objects.requireNonNull(this.getExternalFilesDir("runCatch/")).getPath());
                LogUtil.d("OOM2", "????????????????????????" + path);
                mediaUi2.getTransFormChangeData(new MediaUiModel2.TranChangeCallback() {
                    @Override
                    public void changeBack(float tranX, float tranY, float scale) {
                        String bjPath = mOriginalPathList.get(0);
                        if (nowIsChooseMatting) {
                            bjPath = imgPath.get(0);
                        }
                        VideoFusionModel videoFusionModel = new VideoFusionModel(TemplateActivity.this, path, bjPath, fromTo, templateName, mediaUi2.getOriginalBitmapWidth(), mediaUi2.getOriginalBitmapHeight(), tranX, tranY, scale, templateItem.getType());

                        videoFusionModel.uploadFileToHuawei(path, templateId);
                    }
                });
            } else if (isSpecial) {
                presenter.SaveSpecialTemplate(api_type, nowIsGifTemplate, needAssetsCount, nowIsChooseMatting);
            } else {
                LogUtil.d(TAG, "renderVideo");
                if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.ISSEARCHTEMPLATE)) {
                    StatisticsEventAffair.getInstance().setFlag(TemplateActivity.this, "4_search_save", templateName);
                }
                StatisticsEventAffair.getInstance().setFlag(TemplateActivity.this, "1_mb_bj_save", templateName);
                if (isPlaying) {
                    if (mPlayer != null) {
                        mPlayer.pause();
                        mPlayer = null;
                        mBinding.ivPlay.setImageResource(R.mipmap.iv_play);
                        isPlaying = false;
                        showPreview(true, false);
                    }
                }
                if (nowChooseMusic != 0) {
                    if (nowChooseMusic == 3) {
                        presenter.renderVideo(mFolder.getPath(), downMusicPath, false, nowTemplateIsAnim, imgPath, nowIsGifTemplate);
                    } else {
                        presenter.renderVideo(mFolder.getPath(), nowSpliteMusic, false, nowTemplateIsAnim, imgPath, nowIsGifTemplate);
                    }
                } else {
                    presenter.renderVideo(mFolder.getPath(), mAudio1Path, false, nowTemplateIsAnim, imgPath, nowIsGifTemplate);
                }
            }
        }

    }




    /**
     * description ???????????????
     * creation date: 2021/1/4
     * user : zhangtongju
     */
    private void chooseChecked() {
        nowIsChooseMatting = true;
        //????????????
        if (nowTemplateIsMattingVideo == 1 && !AlbumType.isImage(GetPathType.getInstance().getPathType(imgPath.get(0)))) {
            handler.sendEmptyMessage(1);
            new Thread(() -> presenter.intoMattingVideo(imgPath.get(0), templateName)).start();
        } else {
            StatisticsEventAffair.getInstance().setFlag(TemplateActivity.this, "1_mb_bj_Cutoutoff");
            //???????????????????????????
            presenter.changeMaterial(imgPath, bottomButtonCount, needAssetsCount);
        }
    }


    private void setMattingBtnState() {
        if (picout == 0) {
            //????????????????????????????????????????????????????????????,??????????????????????????????
            mBinding.switchButton.setChecked(false);
            nowIsChooseMatting = false;
        } else {
            mBinding.switchButton.setChecked(true);
            nowIsChooseMatting = true;
        }
        //??????????????????????????????????????????
        if (nowTemplateIsAnim == 1) {
            mBinding.llMatting.setVisibility(View.GONE);
        }
    }


    @Override
    protected void initAction() {

        presenter.loadTemplate(mFolder.getPath(), this, nowTemplateIsAnim, nowTemplateIsMattingVideo, isToSing);

        mBinding.playerSurfaceView.setPlayCallback(mListener);
    }

    @Override
    public void completeTemplate(TemplateModel templateModel) {
        mTemplateModel = templateModel;
        mBinding.llViewpagerContainer.setVisibility(View.VISIBLE);
        LogUtil.d("OOM3", "initBottomLayout");
        initBottomLayout();
        if (nowTemplateIsAnim == 1 || nowTemplateIsMattingVideo == 1) {
            //???????????????
            mTemplateModel.cartoonPath = imgPath.get(0);
        }
        bottomButtonCount = templateModel.groupSize;
        int duration = mTemplateModel.getDuration();
        needDuration = (long) (duration / mTemplateModel.fps);
        mBinding.tvEndTime.setText(TimeUtils.secondToTime((needDuration)));
        getNowChooseIndexMidiaUi();
        LogUtil.d("OOM3", "initTemplateViews");
        initTemplateViews(mTemplateModel);
        //??????????????????
        new Handler().postDelayed(this::setMattingBtnState, 500);
    }

    @Override
    public void toPreview(String path) {
        mBinding.videoPlayer.setUp(path, true, "");
        mBinding.videoPlayer.startPlayLogic();
        mBinding.videoPlayer.setGSYVideoProgressListener((progress, secProgress, currentPosition, duration) ->
                mBinding.seekBar.setProgress(progress));
//        videoPlayer.setVideoAllCallBack(new VideoPlayerCallbackForTemplate(isSuccess -> ));
        mBinding.videoPlayer.setVideoAllCallBack(new VideoPlayerCallbackForTemplate(new VideoPlayerCallbackForTemplate.videoPlayerStopListener() {
            @Override
            public void isStop(boolean isSuccess) {
                showPreview(false, true);
            }

            @Override
            public void onPrepared(boolean onPrepared) {
            }
        }));

        showPreview(true, true);
    }

    @Override
    public void changeMaterialCallback(ArrayList<TemplateThumbItem> callbackListItem, List<String> list_all, List<String> listAssets) {
        listItem.clear();
        listItem.addAll(callbackListItem);
        templateThumbAdapter.notifyDataSetChanged();
        mTemplateModel.setReplaceAllMaterial(listAssets);
//        mTemplateViews.get(nowChooseIndex).invalidate();
        invalidateView();
    }


    private void onclickPlaying() {

        if (isPlaying) {
            if (mPlayer != null) {
                mPlayer.pause();
                mBinding.ivPlay.setImageResource(R.mipmap.iv_play);
                isPlaying = false;
                showPreview(true, false);
            }
        } else {
            isPlaying = true;
            mBinding.ivPlay.setImageResource(R.mipmap.pause);
            if (mBinding.realTimePreview.getVisibility() == View.VISIBLE) {
                if (mPlayer != null) {
                    StatisticsEventAffair.getInstance().setFlag(TemplateActivity.this, " 14_preview_video_template");
                    mPlayer.start();
                    showPreview(true, false);
                } else {
                    toShowPreview();
                }
            } else {
                toShowPreview();
            }
        }
    }


    private void toShowPreview() {
        waitingDialogProgress = new WaitingDialog_progress(this);
        waitingDialogProgress.openProgressDialog();
        waitingDialogProgress.setProgress("?????????~\n" +
                "???????????????\n" +
                "??????????????????");
//        if(nowTemplateIsAnim==1){
//            String [] strArray = imgPath.toArray(new String[imgPath.size()]);
//            returnReplaceableFilePath(strArray);
//        }else{
        new Thread(() -> presenter.getReplaceableFilePath()).start();
//        }
    }

    @Override
    public void returnReplaceableFilePath(String[] paths) {
        for (String path : paths) {
            LogUtil.d("oom", "????????????????????????" + path);
        }
        Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> new Handler().post(() -> showPreview(true, false)));

        if (mTemplateModel.HasBj && !TextUtils.isEmpty(mTemplateModel.getBackgroundPath())) {
            String[] newPaths = new String[paths.length + 1];
            System.arraycopy(paths, 0, newPaths, 0, paths.length);
            MediaUiModel2 mediaUiModel2 = (MediaUiModel2) mTemplateModel.mAssets.get(0).ui;
            if (AlbumType.isVideo(GetPathType.getInstance().getPathType(mTemplateModel.getBackgroundPath()))) {
                newPaths[newPaths.length - 1] = mediaUiModel2.getpathForThisBjMatrixVideo(Objects.requireNonNull(getExternalFilesDir("runCatch/")).getPath(), mTemplateModel.getBackgroundPath());
            } else {
                newPaths[newPaths.length - 1] = mediaUiModel2.getpathForThisBjMatrixImage(Objects.requireNonNull(getExternalFilesDir("runCatch/")).getPath(), mTemplateModel.getBackgroundPath());
            }
            switchTemplate(mFolder.getPath(), newPaths);
        } else {
            switchTemplate(mFolder.getPath(), paths);
        }

    }

    @Override
    public void getCartoonPath(String getCartoonPath) {
//        this.getCartoonPath = getCartoonPath;
    }


    /**
     * description ?????????????????????????????????????????????
     * creation date: 2020/4/14
     * user : zhangtongju
     */
    @Override
    public void showMattingVideoCover(Bitmap bp, String bpPath) {
        LogUtil.d("OOM4", "???????????????" + bpPath);
        if (mTemplateModel != null) {
            if (!TextUtils.isEmpty(videoTime) && !"0".equals(videoTime)) {
                if (bp != null) {
                    for (int i = 0; i < mTemplateModel.mAssets.size(); i++) {
                        MediaUiModel2 mediaUiModel2 = (MediaUiModel2) mTemplateModel.mAssets.get(i).ui;
                        mediaUiModel2.setVideoCover(bp);
                    }
                    if (mTemplateViews != null && mTemplateViews.size() > 0) {
                        Observable.just(nowChoosePosition).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
                            @Override
                            public void call(Integer integer) {
                                Log.d("OOM5", "showMattingVideoCover");
                                WaitingDialog.closeProgressDialog();
                                mTemplateViews.get(nowChoosePosition).invalidate(); //???????????????????????????
                            }
                        });
                    }
                }
            }
        }
        videoMattingCaver = bp;
        runOnUiThread(() ->
                modificationSingleThumbItem(bpPath));
    }

    @Override
    public void showBottomIcon(String path) {
        LogUtil.d("OOM", "?????????????????????" + path);
        TemplateThumbItem item1 = listItem.get(lastChoosePosition);
        item1.setPathUrl(path);
        listItem.set(lastChoosePosition, item1);
        templateThumbAdapter.notifyItemChanged(lastChoosePosition);
    }

    /**
     * description ??????????????????????????? ???????????????
     * needMatting ??????????????????
     * creation date: 2020/4/14
     * user : zhangtongju
     */
    @Override
    public void changeMaterialCallbackForVideo(String originalVideoPath, String path, boolean needMatting) {
        //????????????????????????????????????originalPath ???null???????????????null ??????
        if (needMatting) {
            LogUtil.d("OOM2", "??????");
            if (mOriginalPathList == null) {
                mOriginalPathList = new ArrayList<>();
            }
            mOriginalPathList.clear();
            mOriginalPathList.add(originalVideoPath);
            imgPath.clear();
            imgPath.add(path);
            List<String> list = new ArrayList<>();
            if (!TextUtils.isEmpty(originalVideoPath)) {
                LogUtil.d("OOM2", "originalVideoPath=" + originalVideoPath);
                list.add(originalVideoPath);
                mTemplateModel.setReplaceAllMaterial(list);
            } else {
                LogUtil.d("OOM2", "originalVideoPath=null");
                list.add(path);
                mTemplateModel.setReplaceAllMaterial(list);
            }
            //?????????????????????????????????????????????
            if (mOriginalPathList != null && mOriginalPathList.size() != 0) {
                //???????????????
                mTemplateModel.cartoonPath = imgPath.get(0);
                LogUtil.d("OOM2", "switch_button.isChecked()=" + mBinding.switchButton.isChecked());
                Observable.just(1).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                presenter.getMattingVideoCover(mOriginalPathList.get(0));
                            }
                        }, 500);
                    }
                });
            } else {
                waitingDialogProgress.openProgressDialog();
            }
        } else {
            LogUtil.d("OOM4", "?????????");
            //???????????????
            mOriginalPathList = null;
            imgPath.clear();
            imgPath.add(path);
            mTemplateModel.cartoonPath = path;
            mTemplateModel.setReplaceAllMaterial(imgPath);
            WaitingDialog.closeProgressDialog();
            presenter.getButtomIcon(path);
            Observable.just(nowChoosePosition).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> new Handler().postDelayed(() -> mTemplateViews.get(integer).invalidate(), 200));
        }
    }


    @Override
    public void getSpliteMusic(String path) {
        nowSpliteMusic = path;
        setBjMusic();
    }


    /**
     * description ???????????????????????????
     * creation date: 2020/12/15
     * user : zhangtongju
     */
    @Override
    public void GetChangeDressUpData(List<String> paths) {
        if (paths != null && paths.size() > 0) {
            imgPath.clear();
            imgPath.addAll(paths);
//          resultFileDispose(paths.get(0));
            if (nowTemplateIsAnim == 1) {
                refreshAllData();
            }
        }

    }

    @Override
    public void setDialogProgress(int progress) {
        mLoadingDialog.setTitleStr("?????????????????????");
        mLoadingDialog.setProgress(progress);
        mLoadingDialog.setContentStr("??????????????????");
    }

    @Override
    public void setDialogDismiss() {
        mLoadingDialog.setProgress(0);
        mLoadingDialog.dismiss();
    }

    @Override
    public void showProgressDialog() {
        LogUtil.d(TAG, "renderVideo + showProgressDialog");
        mLoadingDialog.show();
    }


    /**
     * description ?????????????????????mediaUiModel2
     * creation date: 2020/12/15
     * user : zhangtongju
     */
    private void refreshAllData() {
        for (int x = 1; x <= needAssetsCount; x++) {
            for (int i = 0; i < mTemplateModel.getAssets().size(); i++) {
                MediaUiModel2 mediaUiModel2 = (MediaUiModel2) mTemplateModel.getAssets().get(i).ui;
                if (x == mediaUiModel2.getNowGroup()) {
                    mediaUiModel2.setImageAsset(imgPath.get(x - 1));
                    break;
                }
            }
        }
        modificationSingleThumbItem(imgPath.get(0));
    }


    @Override
    protected void onPause() {
        super.onPause();
        mBinding.videoPlayer.onVideoPause();
        showPreview(false, true);
        isPlaying = false;
        mBinding.ivPlay.setImageResource(R.mipmap.iv_play);
        if (mPlayer != null) {
            mPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.videoPlayer.onVideoResume();
    }


    private void showPreview(boolean isPreview, boolean hasAnim) {
        LogUtil.d("OOM", "showPreview=" + isPreview + "isRealtime=" + isRealtime);
        if (isPreview) {
            if (isRealtime) {
                AnimForViewShowAndHide.getInstance().show(mBinding.realTimePreview);
//                real_time_preview.setVisibility(View.VISIBLE);
            } else {
//                videoPlayer.setVisibility(View.VISIBLE);
                AnimForViewShowAndHide.getInstance().show(mBinding.videoPlayer);
            }
            if (hasAnim) {
                AnimForViewShowAndHide.getInstance().hide(mBinding.editViewContainer);
            } else {
//                mContainer.setVisibility(View.GONE);
                AnimForViewShowAndHide.getInstance().hide(mBinding.editViewContainer);

            }
            modificationThumbForRedactData(true);
        } else {
            mBinding.videoPlayer.setVisibility(View.GONE);
            mBinding.realTimePreview.setVisibility(View.INVISIBLE);
            mBinding.editViewContainer.setVisibility(View.VISIBLE);
            modificationThumbForRedactData(false);
        }
    }


    @Override
    public void pickMedia(MediaUiModel model) {
        if (!DoubleClick.getInstance().isFastZDYDoubleClick(1000)) {
            pickIndex = model.getNowIndex();
            pickGroupIndex = model.getNowGroup();
            LogUtil.d("OOM", "????????????????????????" + pickIndex + "pickGroupIndex=" + pickGroupIndex);
            if (isToSing) {
                AlbumManager.chooseWhichAlbum(TemplateActivity.this, 1, REQUEST_SINGLE_MEDIA, this, 1, "");
            } else {
                if (isCanChooseVideo || nowIsPhotographAlbum) {
                    // ?????????????????????????????????
                    float videoTimeF;
                    if (nowIsPhotographAlbum) {
                        videoTimeF = 0f;
                    } else {
                        videoTimeF = Float.parseFloat(videoTime);
                    }
                    AlbumManager.chooseAlbum(TemplateActivity.this, 1, REQUEST_SINGLE_MEDIA, this, "", (long) (videoTimeF * 1000));
                } else {
                    AlbumManager.chooseWhichAlbum(TemplateActivity.this, 1, REQUEST_SINGLE_MEDIA, this, 1, "");
                }
            }
        }
    }

    @Override
    public void editText(TextUiModel model) {
        mBinding.textEditLayout.setVisibility(View.VISIBLE);
        mBinding.textEditLayout.setupWidth(model);
    }


    /**
     * description ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * date: ???2019/11/28 14:10
     * author: ????????? @?????? jutongzhang@sina.com
     */
    WaitingDialog_progress progress;

    private void initTemplateViews(TemplateModel templateModel) {
        for (int i = 1; i <= templateModel.groupSize; i++) {
            TemplateView templateView = new TemplateView(TemplateActivity.this);
            templateView.SetonGestureCallback(() -> StatisticsEventAffair.getInstance().setFlag(TemplateActivity.this, "1_mb_bj_drag"));
            templateView.setBackgroundColor(Color.BLACK);
            templateView.setVisibility(i == 1 ? View.VISIBLE : View.GONE);
            GroupModel groupModel = templateModel.groups.get(i);
            templateView.setAssetGroup(groupModel);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            params.gravity = Gravity.CENTER;
            mTemplateViews.add(templateView);
            mBinding.editViewContainer.addView(templateView, params);
        }
        progress = new WaitingDialog_progress(this);
        progress.openProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.d("OOM4", "??????????????????");
                isFirstReplace(imgPath);
            }
        }).start();

    }


    /**
     * description ????????????????????????
     * creation date: 2020/4/8
     * param :  paths ?????????????????????????????????
     * user : zhangtongju
     */
    private void isFirstReplace(List<String> paths) {
        LogUtil.d("OOM4", "isFirstReplace");

        if (mTemplateViews != null && mTemplateViews.size() > 0) {
            //??????????????????????????????
            List<String> list_all = new ArrayList<>();
            for (int i = 0; i < bottomButtonCount; i++) {  //??????????????????????????????
                if (paths.size() > i && !TextUtils.isEmpty(paths.get(i))) {
                    list_all.add(paths.get(i)); //????????????path ????????????????????????path
                } else {
                    list_all.add(SxveConstans.default_bg_path);
                }
            }

            if (nowTemplateIsAnim == 1 || nowTemplateIsMattingVideo == 1) {
                //?????? ??????
                TemplateThumbItem templateThumbItem = new TemplateThumbItem();
                if (mOriginalPathList != null && mOriginalPathList.size() != 0) {
                    templateThumbItem.setPathUrl(mOriginalPathList.get(0));
                } else {
                    templateThumbItem.setPathUrl(imgPath.get(0));
                }
                templateThumbItem.setIsCheck(0);
                listItem.set(0, templateThumbItem);

            } else {
                for (int i = 0; i < list_all.size(); i++) {  //?????????????????????
//                    MediaUiModel2 mediaUiModel2 = (MediaUiModel2) mTemplateModel.mAssets.get(i).ui;
                    for (int x = 0; x < mTemplateModel.getAssets().size(); x++) {
                        MediaUiModel2 mediaUi2 = (MediaUiModel2) mTemplateModel.getAssets().get(x).ui;
                        if (mediaUi2.getNowGroup() == i + 1) {
                            mediaUi2.setPathOrigin(list_all.get(i));
                            TemplateThumbItem templateThumbItem = new TemplateThumbItem();
                            templateThumbItem.setPathUrl(list_all.get(i));
                            if (i == 0) {
                                templateThumbItem.setIsCheck(0);
                            } else {
                                templateThumbItem.setIsCheck(1);
                            }
                            listItem.set(i, templateThumbItem);
                            break;
                        }
                    }
                }
            }
            // templateThumbAdapter.notifyDataSetChanged();
            //??????????????????????????????????????????
            List<String> listAssets = new ArrayList<>();
            for (int i = 0; i < needAssetsCount; i++) {  //??????????????????????????????
                if (paths.size() > i && !TextUtils.isEmpty(paths.get(i))) {
                    if (nowTemplateIsAnim == 1 || nowTemplateIsMattingVideo == 1) {
                        //????????????????????????
                        if (mOriginalPathList != null && mOriginalPathList.size() != 0) {
                            listAssets.add(mOriginalPathList.get(i));
                        } else {
                            listAssets.add(paths.get(i));
                        }
                    } else {
                        listAssets.add(paths.get(i));
                    }
                } else {
                    listAssets.add(SxveConstans.default_bg_path);
                }
            }

            mTemplateModel.setReplaceAllFiles(listAssets, complete -> TemplateActivity.this.runOnUiThread(() -> {
                LogUtil.d("OOM4", "????????????isCOMPALTE");
                if (!isOndestroy) {
                    WaitingDialog.openPragressDialog(this);
                    selectGroup(0);
                    nowChoosePosition = 0;
                    templateThumbAdapter.notifyDataSetChanged();

                    if (!TextUtils.isEmpty(videoTime) && !"0".equals(videoTime)) {
                        if (videoMattingCaver != null) {
                            for (int i = 0; i < mTemplateModel.mAssets.size(); i++) {
                                MediaUiModel2 mediaUiModel2 = (MediaUiModel2) mTemplateModel.mAssets.get(i).ui;
                                mediaUiModel2.setVideoCover(videoMattingCaver);
                            }
                        }
                    }

                    if (mTemplateViews != null && mTemplateViews.size() > 0) {
                        mTemplateViews.get(nowChoosePosition).invalidate(); //???????????????????????????
                    }

                    LogUtil.d("OOM4", "???????????????");
                    progress.closeProgressDialog();
                    WaitingDialog.closeProgressDialog();

                }

            }));  //??????????????????

        }
    }


    /**
     * description ????????????????????????????????????mModel ?????????mModel ??????????????????????????????mediaModel ???mModel,??????????????????????????????
     * date: ???2019/11/28 13:58
     * author: ????????? @?????? jutongzhang@sina.com
     */
    public void selectGroup(final int index) {
        if (mTemplateViews != null && mTemplateViews.size() > 0) {
            try {
                TemplateView nowChooseTemplateView = mTemplateViews.get(index);
                nowChooseTemplateView.setVisibility(View.VISIBLE);
                nowChooseTemplateView.invalidate();
                Observable.from(mTemplateViews).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(templateView -> {
                    if (templateView != nowChooseTemplateView && templateView.getVisibility() != View.GONE) {
                        templateView.setVisibility(View.GONE);
//                        templateView.setIsShow();
                    }
                });
            } catch (Exception e) {
                LogUtil.d("Exception", e.getMessage());
            }
        }
    }


    public void initTemplateThumb(int bottomButtonCount) {

        for (int i = 0; i < bottomButtonCount; i++) {
            listItem.add(new TemplateThumbItem("", 1, false));
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        if (nowTemplateIsAnim == 1 && listItem.size() > 1) {
            ArrayList<TemplateThumbItem> newTem = new ArrayList<>();
            newTem.add(listItem.get(1));
            listItem.clear();
            listItem.addAll(newTem);
        }

        templateThumbAdapter = new TemplateThumbAdapter(R.layout.item_group_thumb, listItem, TemplateActivity.this);

        templateThumbAdapter.setOnItemChildClickListener((adapter, view, position) -> {

            if (!DoubleClick.getInstance().isFastZDYDoubleClick(200)) {
                nowChoosePosition = position;
                getNowChooseIndexMidiaUi();

                if (view.getId() == R.id.iv_show_un_select) {
                    if (mPlayer != null) {
                        mPlayer.pause();
                        mBinding.ivPlay.setImageResource(R.mipmap.iv_play);
                        isPlaying = false;
                    }

                    if (nowChoosePosition != lastChoosePosition) {
                        selectGroup(position);
                        modificationThumbData(lastChoosePosition, position);
                    } else {
                        if (nowIsPhotographAlbum && !DoubleClick.getInstance().isFastDoubleClick()) {
//                            MediaUiModel2 mediaUi2 = (MediaUiModel2) mTemplateModel.getAssets().get(lastChoosePosition).ui;
                            if (nowClickMediaUi2.isVideoType()) {
                                //?????????????????????
                                float needCropDuration;
                                boolean isNeedSlow;
                                lastChooseFilePath = nowClickMediaUi2.getPathOrigin();
                                Intent intent = new Intent(TemplateActivity.this, TemplateCutVideoActivity.class);
                                needCropDuration = nowClickMediaUi2.getDuration() / (float) nowClickMediaUi2.getFps();
                                isNeedSlow = false;
                                intent.putExtra("isFrom", cutVideoTag);
                                intent.putExtra("videoPath", nowClickMediaUi2.getPathOrigin());
                                LogUtil.d("oom2", "nowClickMediaUi2gROUPID=" + nowClickMediaUi2.getNowGroup() + "nowClickMediaUi2.getPathOrigin()=" + nowClickMediaUi2.getPathOrigin());
                                intent.putExtra("needCropDuration", needCropDuration);
                                intent.putExtra("isNeedSlow", isNeedSlow);
                                intent.putExtra("videoFps", nowClickMediaUi2.getFps());
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        }
                    }
                    lastChoosePosition = nowChoosePosition;
                    showPreview(false, true);
                    AnimForViewShowAndHide.getInstance().show(mBinding.editViewContainer);
                }
            }
        });

        recyclerView.setAdapter(templateThumbAdapter);
    }


    private void modificationThumbData(int lastPosition, int position) {
        TemplateThumbItem item1 = listItem.get(position);
        item1.setIsCheck(0);
        listItem.set(position, item1);
        TemplateThumbItem item2 = listItem.get(lastPosition);
        item2.setIsCheck(1);
        listItem.set(lastPosition, item2);
        templateThumbAdapter.notifyDataSetChanged();
    }

    private void modificationThumbForRedactData(boolean isRedate) {
        for (TemplateThumbItem item : listItem) {
            item.setRedate(isRedate);
        }
        if (templateThumbAdapter != null) {
            templateThumbAdapter.notifyDataSetChanged();
        }

    }


    private void modificationSingleThumbItem(String path) {
        if (listItem != null && listItem.size() > 0) {
            TemplateThumbItem item1 = listItem.get(lastChoosePosition);
            item1.setPathUrl(path);
            listItem.set(lastChoosePosition, item1);
            templateThumbAdapter.notifyItemChanged(lastChoosePosition);
            LogUtil.d("OOM", "ModificationSingleThumbItempath2222=" + path);
        }
    }

    private LoadingDialog buildProgressDialog() {
        LoadingDialog dialog = LoadingDialog.getBuilder(mContext)
                .setHasAd(true)
                .setTitle("?????????...")
                .build();
        return dialog;
    }



    @Override
    public void onBackPressed() {
        if (mBinding.textEditLayout.getVisibility() == View.VISIBLE) {
            mBinding.textEditLayout.hide();
        } else {
            showBackMessage();
        }
    }

    private void showBackMessage() {
        if (fromTo.equals(FromToTemplate.PICTUREALBUM)) {
            StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "alert_edit_back_yj");
        } else {
            StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "alert_edit_back_mb");
        }
        CommonMessageDialog.getBuilder(mContext)
                .setAdStatus(CommonMessageDialog.AD_STATUS_MIDDLE)
                .setAdId(AdConfigs.AD_IMAGE_EXIT)
                .setTitle("??????????????????")
                .setPositiveButton("??????")
                .setNegativeButton("??????")
                .setDialogBtnClickListener(new CommonMessageDialog.DialogBtnClickListener() {
                    @Override
                    public void onPositiveBtnClick(CommonMessageDialog dialog) {
                        dialog.dismiss();
                        finish();
                    }

                    @Override
                    public void onCancelBtnClick(CommonMessageDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .build().show();
    }


    private boolean isOndestroy = false;

    @Override
    public void onDestroy() {
        super.onDestroy();
        clearAllData();
        isOndestroy = true;
        EventBus.getDefault().unregister(this);
    }


    private void clearAllData() {
        presenter.onDestroy();
        mBinding.videoPlayer.release();
        if (mPlayer != null) {
            mPlayer.stop();
        }
    }


    SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int nowProgress, boolean fromUser) {
            if (fromUser && mPlayer != null) {
                LogUtil.d("OOM", "nowProgress=" + nowProgress);
                nowSeekBarProgress = nowProgress;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (mPlayer != null) {
                mPlayer.seek(nowSeekBarProgress);
            }
        }
    };


    private int mDuration;

    private void switchTemplate(String folder, String[] mSources) {
//        mSources = repairRandomPaths.randomPaths(mSources);
        final SXTemplate template = new SXTemplate(folder, SXTemplate.TemplateUsage.kForPreview);
        for (String mSource : mSources) {
            LogUtil.d("OOM", "?????????" + mSource);
        }

        template.setReplaceableFilePaths(mSources);
        template.enableSourcePrepare();
        new Thread() {
            @Override
            public void run() {
                template.commit();
                runOnUiThread(() -> {
                    new Handler().post(waitingDialogProgress::closeProgressDialog);
                    showPreview(true, false);
                    mDuration = template.realDuration();
                    mBinding.seekBar.setMax(mDuration);
                    mPlayer = mBinding.playerSurfaceView.setTemplate(template);
                    mBinding.seekBar.setProgress(0);
                    LogUtil.d("OOM", "start");
                    if (nowChooseMusic != 0) {
                        if (nowChooseMusic == 3) {
                            mPlayer.replaceAudio(downMusicPath);
                        } else {
                            mPlayer.replaceAudio(nowSpliteMusic);
                        }
                    } else {
                        mPlayer.replaceAudio(mAudio1Path);
                    }
                    isPlaying = true;
                    mPlayer.start();
                });
            }
        }.start();
    }


    private SXTemplatePlayer.PlayStateListener mListener = new SXTemplatePlayer.PlayStateListener() {
        @Override
        public void onProgressChanged(final int frame) {
            mBinding.playerSurfaceView.post(() -> {
                LogUtil.d("OOM", "onProgressChangedFrame=" + frame);
                mBinding.seekBar.setProgress(frame);
                float nowDuration = frame / mTemplateModel.fps;
                mBinding.tvStartTime.setText(TimeUtils.secondToTime((long) (nowDuration)));
            });
        }

        @Override
        public void onFinish() {
            runOnUiThread(() -> {
                isPlaying = false;
                mBinding.tvStartTime.setText("00:00");
                showPreview(false, true);
                mBinding.ivPlay.setImageResource(R.mipmap.iv_play);
                mBinding.seekBar.setProgress(0);
            });
        }
    };


    private String lastChooseFilePath;

    @Override
    public void resultFilePath(int tag, List<String> paths, boolean isCancel, boolean isFromCamera, ArrayList<AlbumFile> albumFileList) {
        if (!isCancel) {
            if (tag == REQUEST_SINGLE_MEDIA) {
                if (paths != null && paths.size() > 0) {
                    //??????????????????
                    if (nowTemplateIsAnim == 1) {
                        presenter.toDressUp(paths.get(0), templateId);
                    } else {
                        resultFileDispose(paths.get(0));
                    }
                }
            }
        }
    }


    /**
     * description ???????????????????????????
     * creation date: 2020/12/10
     * user : zhangtongju
     */
    private void resultFileDispose(String path) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lastChooseFilePath = path;
                String mimeType = GetPathType.getInstance().getMediaType(path);
                if (AlbumType.isImage(mimeType)) {
                    if (mOriginalPathList == null || mOriginalPathList.size() == 0) {
                        if (nowTemplateIsMattingVideo == 1) {
                            mattingImage(path);
                        } else {
                            //???????????????
                            if (imgPath.size() > lastChoosePosition) {
                                imgPath.set(lastChoosePosition, path);
                            } else {
                                imgPath.add(path);
                            }
                            nowClickMediaUi2.setImageAsset(path);
                            mTemplateViews.get(lastChoosePosition).invalidate();
                            modificationSingleThumbItem(path);

                        }
                    } else {
                        mattingImage(path);
                    }
                    chooseTemplateMusic();
                    templateThumbForMusic.findViewById(R.id.ll_choose_0).setVisibility(View.INVISIBLE);
                } else {
                    float needVideoTime = nowClickMediaUi2.getDuration() / (float) nowClickMediaUi2.getFps();
                    if (needVideoTime < 0.5) {
                        needVideoTime = 0.5f;
                    }
                    Intent intoCutVideo = new Intent(TemplateActivity.this, TemplateCutVideoActivity.class);
                    intoCutVideo.putExtra("needCropDuration", needVideoTime);
                    intoCutVideo.putExtra("videoPath", path);
                    intoCutVideo.putExtra("nowIsPhotographAlbum", nowIsPhotographAlbum);
                    intoCutVideo.putExtra("picout", 1);
                    intoCutVideo.putExtra("templateName", templateName);
                    if (nowIsPhotographAlbum) {
                        intoCutVideo.putExtra("isFrom", cutVideoTag);
                    } else {
                        intoCutVideo.putExtra("isFrom", 2);
                    }
                    startActivity(intoCutVideo);
                }
            }
        });


    }


    /**
     * description ????????????????????????
     * creation date: 2020/5/6
     * user : zhangtongju
     */
    private void mattingImage(String path) {
        boolean hasCache = nowTemplateIsAnim != 1;
        CompressionCuttingManage manage = new CompressionCuttingManage(TemplateActivity.this, templateId, hasCache, tailorPaths -> {
            if (mOriginalPathList != null && mOriginalPathList.size() != 0) {
                mOriginalPathList.set(lastChoosePosition, path);
            } else {
                //??????????????????????????????????????????????????????null
                mOriginalPathList = new ArrayList<>();
                mOriginalPathList.add(lastChoosePosition, path);
            }
            imgPath.set(lastChoosePosition, tailorPaths.get(0));
            Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
                if (nowTemplateIsMattingVideo == 1) {
                    if (nowIsChooseMatting) {
                        MediaUiModel2 mediaUi1 = (MediaUiModel2) mTemplateModel.getAssets().get(0).ui;
                        mediaUi1.setImageAsset(tailorPaths.get(0));
                        MediaUiModel2 mediaUi2 = (MediaUiModel2) mTemplateModel.getAssets().get(1).ui;
                        mediaUi2.setImageAsset(path);
                        mTemplateViews.get(lastChoosePosition).invalidate();
                        modificationSingleThumbItem(path);
                    } else {
                        MediaUiModel2 mediaUi1 = (MediaUiModel2) mTemplateModel.getAssets().get(0).ui;
                        mediaUi1.setImageAsset(path);
                        MediaUiModel2 mediaUi2 = (MediaUiModel2) mTemplateModel.getAssets().get(1).ui;
                        mediaUi2.setImageAsset(path);
                        mTemplateViews.get(lastChoosePosition).invalidate();
                        modificationSingleThumbItem(path);
                    }
                    mTemplateModel.cartoonPath = path;
                } else {
                    //??????????????????????????????id ??????????????????????????????
                    for (int i = 0; i < mTemplateModel.getAssets().size(); i++) {
                        MediaUiModel2 mediaUi2 = (MediaUiModel2) mTemplateModel.getAssets().get(i).ui;
                        if (pickGroupIndex == mediaUi2.getNowGroup()) {
                            if (pickIndex == mediaUi2.getNowIndex()) {
                                if (nowIsChooseMatting) {
                                    mediaUi2.setImageAsset(tailorPaths.get(0));
                                    modificationSingleThumbItem(tailorPaths.get(0));
                                } else {
                                    mediaUi2.setImageAsset(path);
                                    modificationSingleThumbItem(path);
                                }
                                return;
                            }
                        }
                        mTemplateViews.get(lastChoosePosition).invalidate();
                    }
                }
            });
        });
        List<String> list = new ArrayList<>();
        list.add(path);
        manage.toMatting(list);
    }


    /**
     * description ?????????????????????????????????????????????
     * creation date: 2020/4/14
     * user : zhangtongju
     */
    @Subscribe
    public void onEventMainThread(MattingVideoEnity event) {
        LogUtil.d("oom2", "lastChooseFilePath=" + lastChooseFilePath);
        nowClickMediaUi2.setPathOrigin(lastChooseFilePath);

        if (event.getTag() == cutVideoTag) {
            LogUtil.d("OOM2", "????????????onEventMainThread");
            getSingleCatVideoPath(event.getMattingPath());
        } else {
            mTemplateModel.resetUi();
            if (event.getTag() == 1) {
                //???????????????????????????????????????
                changeMaterialCallbackForVideo(event.getOriginalPath(), event.getMattingPath(), true);
            } else if (event.getTag() == 2) {
                nowTemplateIsMattingVideo = 1;
                mTemplateModel.mAssets.get(0).setNeedMatting(true);

                //????????????
                if (event.getOriginalPath() == null || !nowIsChooseMatting) {
                    LogUtil.d("OOM2", "event.getOriginalPath() == null || !nowIsChooseMatting");
                    if (event.getOriginalPath() == null) {
                        LogUtil.d("OOM2", "event.getOriginalPath()== null");
                        //????????????????????????
                        changeMaterialCallbackForVideo(null, event.getMattingPath(), false);
                        //??????????????????????????????????????????glide ???????????????????????????glide ????????????
                        presenter.getButtomIcon(event.getMattingPath());
                        mBinding.switchButton.setChecked(false);
                        changeMaterialMusic(event.getMattingPath());
                    } else {
                        LogUtil.d("OOM2", "event.getOriginalPath()???= null");
                        //?????????????????????????????????????????????
                        changeMaterialCallbackForVideo(null, event.getOriginalPath(), false);
                        //??????????????????????????????????????????glide ???????????????????????????glide ????????????
                        presenter.getButtomIcon(event.getOriginalPath());
                        changeMaterialMusic(event.getOriginalPath());
                        mBinding.switchButton.setChecked(true);
                    }
                } else {
                    LogUtil.d("OOM2", "?????????????????????");
                    //?????????????????????
                    changeMaterialCallbackForVideo(event.getOriginalPath(), event.getMattingPath(), true);
                    presenter.getButtomIcon(event.getOriginalPath());
                    changeMaterialMusic(event.getMattingPath());
                }
            }
            templateThumbForMusic.findViewById(R.id.ll_choose_0).setVisibility(View.VISIBLE);
            primitivePath = event.getPrimitivePath();
        }

        if (nowChooseMusic == 1) {
            //??????????????????
            chooseMaterialMusic();
        }

    }


    private void chooseMaterialMusic() {
        clearCheckBox();
        String path = imgPath.get(0);
        if (AlbumType.isVideo(GetPathTypeModel.getInstance().getMediaType(path))) {
            presenter.getBjMusic(primitivePath);
            changeMusic();
            cb_0.setImageResource(R.mipmap.template_btn_selected);
            nowChooseMusic = 1;
        } else {
            chooseTemplateMusic();
            ToastUtil.showToast("????????????????????????");
        }
    }


    private void changeMaterialMusic(String musicPath) {
        if (nowChooseMusic == 1) {
            presenter.getBjMusic(musicPath);
        }
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            WaitingDialog.openPragressDialog(TemplateActivity.this);
        }
    };


    private void invalidateView() {
        Observable.just(nowChoosePosition).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> new Handler().postDelayed(() -> mTemplateViews.get(integer).invalidate(), 200));
    }

    LinearLayout ll_choose_3;
    private int lastChooseCommonTabLayout;
    private ImageView cb_0;
    private ImageView cb_1;
    private ImageView cb_2;
    private ImageView cb_3;
    String[] titlesHasBj;
    private View templateThumbForMusic;

    public void initBottomLayout() {
        ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
        if (mTemplateModel.HasBj != null && mTemplateModel.HasBj) {
            LogUtil.d("OOM3", "0000");
            if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.PICTUREALBUM)) {
                titlesHasBj = new String[]{getString(R.string.template), getString(R.string.template_edit), getString(R.string.template_bj),
                        getString(R.string.template_music)};
            } else if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.FACEGIF)) {
                titlesHasBj = new String[]{getString(R.string.template), getString(R.string.template_bj)};
            } else if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.TEMPLATESPECIAL)) {
                titlesHasBj = new String[]{getString(R.string.template)};
            } else if (isToSing || nowIsGifTemplate || isSpecial) {
                LogUtil.d("OOM3", "0000+");
                titlesHasBj = new String[]{getString(R.string.template_edit), getString(R.string.template_bj)
                };
            } else {
                titlesHasBj = new String[]{getString(R.string.template_edit), getString(R.string.template_bj),
                        getString(R.string.template_music)};
            }
        } else {
            if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.PICTUREALBUM)) {
                titlesHasBj = new String[]{getString(R.string.template), getString(R.string.template_edit),
                        getString(R.string.template_music)};
            } else if (isToSing || nowIsGifTemplate || isSpecial) {
                LogUtil.d("OOM3", "0000+");
                titlesHasBj = new String[]{getString(R.string.template_edit)
                };
            } else {
                LogUtil.d("OOM3", "0000+");
                titlesHasBj = new String[]{getString(R.string.template_edit),
                        getString(R.string.template_music)};
            }
        }


        LogUtil.d("OOM3", "1111");

        for (String title : titlesHasBj) {
            mTabEntities.add(new TabEntity(title, 0, 0));
        }

        LogUtil.d("OOM3", "2222");
        mBinding.templateTablayout.setTabData(mTabEntities);
        mBinding.templateTablayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                if (position == 1) {
                    if (mTemplateModel.HasBj) {
                        chooseBj(templateItem);
                        mBinding.templateTablayout.setCurrentTab(lastChooseCommonTabLayout);
                    } else {
                        //????????????????????????????????????????????????
                        lastChooseCommonTabLayout = 2;
                        mBinding.templateViewPager.setCurrentItem(1);
                        StatisticsEventAffair.getInstance().setFlag(TemplateActivity.this, "11_yj_muscle");

                    }
                } else if (position == 2) {
                    if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.PICTUREALBUM)) {
                        mBinding.templateViewPager.setCurrentItem(2);
                    } else {
                        mBinding.templateViewPager.setCurrentItem(1);
                        StatisticsEventAffair.getInstance().setFlag(TemplateActivity.this, "11_yj_muscle");
                    }
                    lastChooseCommonTabLayout = 2;
                } else {
                    lastChooseCommonTabLayout = 0;
                    mBinding.templateViewPager.setCurrentItem(0);
                }
            }

            @Override
            public void onTabReselect(int position) {
            }
        });

        LogUtil.d("OOM3", "--------------");

        ArrayList<View> pagerList = new ArrayList<>();

        //????????????
        if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.PICTUREALBUM)) {
            View templateThumb = LayoutInflater.from(this)
                    .inflate(R.layout.view_choose_template, null);
            new ViewChooseTemplate(TemplateActivity.this, templateThumb, changeTemplatePosition, new ViewChooseTemplate.Callback() {
                @Override
                public void onItemClick(int position, String path, NewFragmentTemplateItem item) {
                    clearAllData();
                    //???????????????????????????
                    Intent intent = getIntent();
                    Bundle bundle = new Bundle();
                    String[] paths = mTemplateModel.getOriginFilePaths();
                    LogUtil.d("OOM4", "");
                    List<String> getAsset = Arrays.asList(paths);
                    ArrayList<String> arrayList = new ArrayList<>(getAsset);
//                    boolean hasDefaultBj = hasDefaultBj(paths);
                    bundle.putInt("isPicNum", 20);
                    bundle.putString("fromTo", FromToTemplate.PICTUREALBUM);
                    bundle.putInt("changeTemplatePosition", position);
                    bundle.putInt("picout", 0);
                    bundle.putInt("is_anime", 0);
                    bundle.putString("templateName", item.getTitle());
                    bundle.putString("templateId", item.getId() + "");
//                    if (hasDefaultBj) {
//                        bundle.putStringArrayList("originalPath", new ArrayList<>(originalPath));
//                        bundle.putStringArrayList("paths", new ArrayList<>(originalPath));
//                    } else {
                    bundle.putStringArrayList("originalPath", arrayList);
                    bundle.putStringArrayList("paths", arrayList);
//                    }
                    bundle.putString("templateFilePath", path);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("Message", bundle);
                    intent.putExtra("person", item);
                    startActivity(intent);
                }

                @Override
                public void isNeedToCutVideo(int position) {


                }
            });
            pagerList.add(templateThumb);
        }

        View templateItemThumb = LayoutInflater.from(this).inflate(R.layout.view_template_bg, null);
        recyclerView = templateItemThumb.findViewById(R.id.recyclerView);
        pagerList.add(recyclerView);
        initTemplateThumb(mTemplateModel.groupSize);

        templateThumbForMusic = LayoutInflater.from(this).inflate(R.layout.view_choose_music, null);
        LinearLayout ll_choose_0 = templateThumbForMusic.findViewById(R.id.ll_choose_0);
        if (nowIsPhotographAlbum) {
            ll_choose_0.setVisibility(View.GONE);
        }
        LinearLayout ll_choose_1 = templateThumbForMusic.findViewById(R.id.ll_choose_1);
        LinearLayout ll_choose_2 = templateThumbForMusic.findViewById(R.id.ll_choose_2);
        ll_choose_3 = templateThumbForMusic.findViewById(R.id.ll_choose_3);
        ll_choose_3.setVisibility(View.VISIBLE);
        LinearLayout ll_line_0 = templateThumbForMusic.findViewById(R.id.ll_line_0);
        TextView tv_add_music = templateThumbForMusic.findViewById(R.id.tv_add_music);
        tv_add_music.setOnClickListener(tvMusicListener);
        if (!mTemplateModel.HasBj) {
            ll_choose_1.setVisibility(View.GONE);
        }
        ll_choose_0.setOnClickListener(tvMusicListener);
        ll_choose_1.setOnClickListener(tvMusicListener);
        ll_choose_2.setOnClickListener(tvMusicListener);
        ll_choose_3.setOnClickListener(tvMusicListener);

        cb_0 = templateThumbForMusic.findViewById(R.id.iv_check_box_0);
        cb_1 = templateThumbForMusic.findViewById(R.id.iv_check_box_1);
        cb_2 = templateThumbForMusic.findViewById(R.id.iv_check_box_2);
        cb_3 = templateThumbForMusic.findViewById(R.id.iv_check_box_3);

        cb_2.setImageResource(R.mipmap.template_btn_selected);
        TextView tv_1 = templateThumbForMusic.findViewById(R.id.tv_1);
        tv_1.setText("????????????");
        TextView tv_2 = templateThumbForMusic.findViewById(R.id.tv_2);
        tv_2.setText("????????????");
        cb_0.setOnClickListener(tvMusicListener);
        cb_1.setOnClickListener(tvMusicListener);
        cb_2.setOnClickListener(tvMusicListener);
        cb_3.setOnClickListener(tvMusicListener);

        pagerList.add(templateThumbForMusic);

        TemplateViewPager adapter = new TemplateViewPager(pagerList);
        mBinding.templateViewPager.setAdapter(adapter);

        LogUtil.d("OOM3", "?????????????????????");
    }


    private boolean hasDefaultBj(String[] paths) {
        for (String str : paths) {
            if (str.equals(SxveConstans.default_bg_path)) {
                return true;
            }
        }

        return false;

    }


    View.OnClickListener tvMusicListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                //??????
                case R.id.ll_choose_0:
                case R.id.iv_check_box_0:
                    chooseMaterialMusic();
                    break;
                //??????
                case R.id.ll_choose_1:
                case R.id.iv_check_box_1:
                    if (TextUtils.isEmpty(mTemplateModel.getBackgroundPath())) {
                        chooseTemplateMusic();
                        ToastUtil.showToast("?????????????????????????????????");
                    } else if (!AlbumType.isVideo(GetPathType.getInstance().getPathType(mTemplateModel.getBackgroundPath()))) {
                        ToastUtil.showToast("??????????????????");
                    } else {
                        clearCheckBox();
                        cb_1.setImageResource(R.mipmap.template_btn_selected);
                        nowChooseMusic = 2;
                        presenter.getBjMusic(mTemplateModel.getBackgroundPath());
                    }
                    changeMusic();

                    break;
                //??????
                case R.id.ll_choose_2:
                case R.id.iv_check_box_2:
                    changeMusic();
                    chooseTemplateMusic();
                    break;

                //??????
                case R.id.ll_choose_3:
                case R.id.iv_check_box_3:
                    if (!TextUtils.isEmpty(downMusicPath)) {
                        clearCheckBox();
                        cb_3.setImageResource(R.mipmap.template_btn_selected);
//                        cb_3.setChecked(true);
                        changeMusic();
                        chooseDownMusic();
                    } else {
                        ToastUtil.showToast("??????????????????");
                    }

                    break;

                case R.id.tv_add_music:
                    Intent intent = new Intent(TemplateActivity.this, ChooseMusicActivity.class);
                    intent.putExtra("needDuration", needDuration * 1000);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    break;
                default:
                    break;
            }
        }
    };


    private void chooseTemplateMusic() {
        clearCheckBox();
        cb_2.setImageResource(R.mipmap.template_btn_selected);
        nowChooseMusic = 0;
        if (isPlaying) {
            mPlayer.replaceAudio(mAudio1Path);
        }
    }


    private void chooseDownMusic() {
        clearCheckBox();
        cb_3.setImageResource(R.mipmap.template_btn_selected);
        nowChooseMusic = 3;
        if (isPlaying) {
            mPlayer.replaceAudio(downMusicPath);
        }
    }


    private void changeMusic() {
        StatisticsEventAffair.getInstance().setFlag(TemplateActivity.this, "11_yj_background");
        if (mBinding.realTimePreview.getVisibility() == View.VISIBLE) {
            //??????????????????
            if (!isPlaying) {
                showPreview(false, true);
                AnimForViewShowAndHide.getInstance().show(mBinding.editViewContainer);
            }
        }
    }

    private void setBjMusic() {
        if (isPlaying) {
//            int progress=mPlayer.getDuration();
            mPlayer.replaceAudio(nowSpliteMusic);
//            presenter.playBGMMusic(nowSpliteMusic,progress);
        }
    }


    private void clearCheckBox() {
        cb_0.setImageResource(R.mipmap.template_btn_unselected);
        cb_1.setImageResource(R.mipmap.template_btn_unselected);
        cb_2.setImageResource(R.mipmap.template_btn_unselected);
        cb_3.setImageResource(R.mipmap.template_btn_unselected);
    }

    @Subscribe
    public void onEventMainThread(DownVideoPath event) {
        Observable.just(event.getPath()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(s -> {
            LogUtil.d("OOM", "???????????????????????????,?????????" + event.getPath());
            String videoBjPath = event.getPath();
            if (TextUtils.isEmpty(videoBjPath)) {
                ToastUtil.showToast("?????????????????????");
                mTemplateModel.setHasBg("", false);
            } else {
                if (AlbumType.isVideo(GetPathType.getInstance().getPathType(videoBjPath))) {
                    mTemplateModel.setHasBg(videoBjPath, true);
                    LogUtil.d("OOM", "????????????????????????" + nowChooseMusic);
                    LogUtil.d("OOM", "videoBjPath=" + videoBjPath);
                    if (nowChooseMusic == 2) {
                        //??????????????????????????????????????????????????????????????????????????????
                        presenter.getBjMusic(videoBjPath);
                    }
                } else {
                    mTemplateModel.setHasBg(videoBjPath, false);
                    nowSpliteMusic = "";
                }
            }
        });
    }


    /**
     * description ???????????????????????????
     * creation date: 2020/9/4
     * user : zhangtongju
     */
    private String downMusicPath;

    @Subscribe
    public void onEventMainThread(CutSuccess cutSuccess) {
        downMusicPath = cutSuccess.getFilePath();
        clearCheckBox();
        cb_3.setImageResource(R.mipmap.template_btn_selected);
        nowChooseMusic = 3;
//        nowSpliteMusic = downMusicPath;
        setBjMusic();
        ll_choose_3.setVisibility(View.VISIBLE);
    }


    private MediaUiModel2 nowClickMediaUi2;

    public void getNowChooseIndexMidiaUi() {
        int position = nowChoosePosition + 1;
        for (int i = 0; i < mTemplateModel.getAssets().size(); i++) {
            nowClickMediaUi2 = (MediaUiModel2) mTemplateModel.getAssets().get(i).ui;
            if (position == nowClickMediaUi2.getNowGroup()) {
                pickIndex = nowClickMediaUi2.getNowIndex();
                pickGroupIndex = nowClickMediaUi2.getNowGroup();
                LogUtil.d("oom2", "??????id--pickGroupIndex=" + pickGroupIndex);
                break;
            }
        }
    }


    public void getSingleCatVideoPath(String path) {
        LogUtil.d("OOM", "ModificationSingleThumbItempath2222=" + path);
        for (int i = 0; i < mTemplateModel.getAssets().size(); i++) {
            MediaUiModel2 mediaUi2 = (MediaUiModel2) mTemplateModel.getAssets().get(i).ui;
            LogUtil.d("OOM", "mediaUi2.getNowGroup()=pickGroupIndex=" + pickGroupIndex + "pickIndex=" + pickIndex + "mediaUi2.getNowIndex()=" + mediaUi2.getNowIndex());
            if (pickGroupIndex == mediaUi2.getNowGroup()) {
                if (pickIndex == mediaUi2.getNowIndex()) {
                    LogUtil.d("OOM", "????????????path" + path);
                    mediaUi2.setVideoPath(path, false, 0);
                    break;
                }
            }
            mTemplateViews.get(lastChoosePosition).invalidate();
        }
        modificationSingleThumbItem(path);
    }

    /**
     * description ???????????????
     * creation date: 2020/5/9
     * user : zhangtongju
     */
    public void chooseBj(NewFragmentTemplateItem templateItem) {
        Intent intent = new Intent(mContext, ChooseBackgroundTemplateActivity.class);
        intent.putExtra("templateItem", templateItem);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }


    public static Intent buildIntent(Context context, List<String> paths, List<String> originalImagePath,
                                     int isPicNum, String fromTo, int isPicOut, NewFragmentTemplateItem templateItem,
                                     String templateFilePath) {
        Intent intent = new Intent(context, TemplateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(INTENT_IMAGE_PATH, (ArrayList<String>) paths);
        bundle.putStringArrayList(INTENT_ORIGINAL_PATH, (ArrayList<String>) originalImagePath);
        bundle.putInt(INTENT_IS_PIC_NUM, isPicNum);
        bundle.putString(INTENT_FROM_TO, fromTo);
        bundle.putInt(INTENT_PIC_OUT, isPicOut);
        bundle.putInt(INTENT_IS_ANIME, templateItem.getIs_anime());
        bundle.putString(INTENT_TEMPLATE_NAME, templateItem.getTitle());
        bundle.putString(INTENT_TEMPLATE_ID, templateItem.getId() + "");
        bundle.putInt(INTENT_IS_SPECIAL, templateItem.getApi_type());
        LogUtil.d("oom22", "templateItem.getApi_type()=" + templateItem.getApi_type());
        bundle.putString(INTENT_VIDEO_TIME, templateItem.getVideotime());
        bundle.putString(INTENT_TEMPLATE_FILE_PATH, templateFilePath);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra(TEMPLATE_BUNDLE_NAME, bundle);
        intent.putExtra(TEMPLATE_ITEM_NAME, templateItem);

        return intent;
    }

}

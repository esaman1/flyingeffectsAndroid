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

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.TemplateThumbAdapter;
import com.flyingeffects.com.adapter.TemplateViewPager;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.commonlyModel.GetPathType;
import com.flyingeffects.com.enity.CutSuccess;
import com.flyingeffects.com.enity.DownVideoPath;
import com.flyingeffects.com.enity.TabEntity;
import com.flyingeffects.com.enity.TemplateThumbItem;
import com.flyingeffects.com.enity.NewFragmentTemplateItem;
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
import com.flyingeffects.com.view.EmptyControlVideo;
import com.flyingeffects.com.view.MattingVideoEnity;
import com.flyingeffects.com.view.NoSlidingViewPager;
import com.shixing.sxve.ui.AssetDelegate;
import com.shixing.sxve.ui.SxveConstans;
import com.shixing.sxve.ui.albumType;
import com.shixing.sxve.ui.model.GroupModel;
import com.shixing.sxve.ui.model.MediaUiModel;
import com.shixing.sxve.ui.model.MediaUiModel2;
import com.shixing.sxve.ui.model.TemplateModel;
import com.shixing.sxve.ui.model.TextUiModel;
import com.shixing.sxve.ui.view.TemplateView;
import com.shixing.sxve.ui.view.TextAssetEditLayout;
import com.shixing.sxve.ui.view.WaitingDialog;
import com.shixing.sxve.ui.view.WaitingDialog_progress;
import com.shixing.sxvideoengine.SXPlayerSurfaceView;
import com.shixing.sxvideoengine.SXTemplate;
import com.shixing.sxvideoengine.SXTemplatePlayer;
import com.suke.widget.SwitchButton;
import com.yanzhenjie.album.AlbumFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * 模板页面
 * 漫画和抠图比较特殊,
 */
public class TemplateActivity extends BaseActivity implements TemplateMvpView, AssetDelegate, AlbumChooseCallback {
    private static final String TAG = "TemplateActivity";

    public static final String TEMPLATE_BUNDLE_NAME = "Message";
    public static final String TEMPLATE_ITEM_NAME = "person";
    public static final String INTENT_FROM_TO = "fromTo";
    public static final String INTENT_IS_PIC_NUM = "isPicNum";
    public static final String INTENT_TEMPLATE_ID = "templateId";
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

    @BindView(R.id.switch_button)
    SwitchButton switch_button;

    @BindView(R.id.edit_view_container)
    FrameLayout mContainer;
    RecyclerView recyclerView;
    @BindView(R.id.seekBar)
    SeekBar seekBar;
    @BindView(R.id.iv_play)
    ImageView ivPlayButton;
    private SXTemplatePlayer mPlayer;
    @BindView(R.id.player_surface_view)
    SXPlayerSurfaceView mPlayerView;
    @BindView(R.id.video_player)
    EmptyControlVideo videoPlayer;

    private TemplatePresenter presenter;
    private List<String> imgPath = new ArrayList<>();
    private TemplateModel mTemplateModel;

    private File mFolder;
    private TemplateThumbAdapter templateThumbAdapter;
    private ArrayList<TemplateThumbItem> listItem = new ArrayList<>();
    private ArrayList<TemplateView> mTemplateViews;
    private String mAudio1Path;
    private static final String MUSIC_PATH = "/bj.mp3";
    private TextAssetEditLayout mTextEditLayout;

    private int nowSeekBarProgress;

    /**
     * 原图地址,如果不需要抠图，原图地址为null,有抠图的情况下，默认使用原图
     */
    private List<String> mOriginalPathList;
    /**
     * intent传递过来的模板地址
     */
    private String mTemplateFilePath;
    /**
     * 模板选择位置
     */
    private int changeTemplatePosition;

    /**
     * 底部按钮数量
     */
    private int bottomButtonCount;
    /**
     * 需要素材数量
     */
    private int needAssetsCount;
    private String templateName;

    private String fromTo;
    //影集
    private int cutVideoTag = 3;

    @BindView(R.id.Real_time_preview)
    FrameLayout real_time_preview;

    @BindView(R.id.tv_end_time)
    TextView tv_end_time;

    @BindView(R.id.tv_start_time)
    TextView tv_start_time;

    @BindView(R.id.relayout_bottom)
    LinearLayout relayout_bottom;

    @BindView(R.id.template_viewPager)
    NoSlidingViewPager viewPager;

    @BindView(R.id.template_tablayout)
    CommonTabLayout commonTabLayout;

    private String templateId;

//    private String getCartoonPath;

    private static final int REQUEST_SINGLE_MEDIA = 11;

//    private static final int REQUEST_SINGLE_MEDIA_VIDEO = 12;
    /**
     * 点击事件选择的组位置
     */
    private int pickIndex;

    /**
     * 当前点击事件选择group 的位置
     */
    private int pickGroupIndex;

    /**
     * 是否是即时播放
     */
    private boolean isRealtime = true;

    private boolean isPlaying = false;

    private int nowChoosePosition;
    private int lastChoosePosition;


    /**
     * 当前是不是动漫，1表示是动漫
     */
    private int nowTemplateIsAnim;


    /**
     * 当前素材正式视频
     */
    private int nowTemplateIsMattingVideo;

    /**
     * 当前可以选择视频
     */
    private boolean isCanChooseVideo = false;

    /**
     * 有值表示视频
     */
    private String videoTime;


    /**
     * 只针对视频抠图，然后吧第一针的封面传过去
     */
    private Bitmap videoMattingCaver;

    private boolean nowIsChooseMatting = true;

    /**
     * 0 表示不需要抠图，1表示需要抠图，无论扣不抠图，都是默认进来就抠好了图，然后在通过这个字段在改变顶部按钮选中状态
     */
    private int picout;

    private long needDuration;

    private boolean nowIsPhotographAlbum = false;

    /**
     * 只针对预览显示的文案
     */
    private WaitingDialog_progress waitingDialogProgress;


    //模板背景音乐 0表示模板音乐 1表示素材音乐  2 表示背景音乐 3表示提取音乐
    private int nowChooseMusic = 0;

    /**
     * 当前分离出来的视频音乐
     */
    private String nowSpliteMusic;

    /**
     * 这里也是原图地址，如果是扣完图视频后，这个值是指真正意义上的原图地址且包含音乐而originalPath 是拼装后的原图地址
     */
    private String primitivePath;

    private NewFragmentTemplateItem templateItem;

    /**
     * 如果是仿抖音一样的去唱歌，那么ui 界面需要修改，变成只有下一步功能
     */
    private boolean isToSing = false;
    private LoadingDialog mLoadingDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_template;
    }



    @Override
    protected void initView() {
        mContext = TemplateActivity.this;
        EventBus.getDefault().register(this);

        mLoadingDialog = buildProgressDialog();
        getLifecycle().addObserver(mLoadingDialog);
        initData();
        setOnClickListener();
        findViewById(R.id.iv_top_back).setOnClickListener(this);
        findViewById(R.id.tv_top_submit).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tv_top_submit)).setText("下一步");



        //换装的话需要的素材数量就是后台返回的素材数量
        if (nowTemplateIsAnim == 1) {
            needAssetsCount = imgPath.size();
        }

        if (nowTemplateIsAnim == 2) {
            isToSing = true;
        }

        presenter = new TemplatePresenter(this, this, fromTo, templateName);
        LogUtil.d("OOM3", "initView");

        templateItem = (NewFragmentTemplateItem) getIntent().getSerializableExtra("person");

        if (mOriginalPathList != null && mOriginalPathList.size() > 0) {
            int totalMaterial = needAssetsCount;
            if (mOriginalPathList.size() < totalMaterial) {
                //说明用户没有选完素材，那么就需要补足素材，不然会出现数组越界的情况
                for (int i = 0; i < totalMaterial; i++) {
                    if (mOriginalPathList.size() > i && !TextUtils.isEmpty(mOriginalPathList.get(i))) {
                        LogUtil.d("OOM", "正常位置");
                    } else {
                        mOriginalPathList.add(SxveConstans.default_bg_path);
                        imgPath.add(SxveConstans.default_bg_path);
                    }
                }
            }
        }

        if (templateItem.getIs_pic() != 1) {
            if (!TextUtils.isEmpty(videoTime) && !"0".equals(videoTime) && albumType.isVideo(GetPathType.getInstance().
                    getMediaType(imgPath.get(0)))) {
                nowTemplateIsMattingVideo = 1;
                //不需要抠图就不需要扣第一帧页面
                if (mOriginalPathList != null && mOriginalPathList.size() != 0) {
                    handler.sendEmptyMessage(1);
                    new Thread(() -> presenter.getMattingVideoCover(mOriginalPathList.get(0))).start();
                }
            }
        }

        if (!TextUtils.isEmpty(videoTime) && !"0".equals(videoTime)) {
            isCanChooseVideo = true;
        }

        mTextEditLayout = findViewById(R.id.text_edit_layout);

        mFolder = new File(mTemplateFilePath);
        mAudio1Path = mFolder.getPath() + MUSIC_PATH;
        FileManager manager = new FileManager();
        String dir = manager.getFileCachePath(this, "");
        mTemplateViews = new ArrayList<>();

        SxveConstans.default_bg_path = new
                File(dir, "default_bj.png").
                getPath();

        seekBar.setOnSeekBarChangeListener(seekBarListener);

        switch_button.setOnCheckedChangeListener((view, isChecked) -> {
            LogUtil.d("OOM3", "进入到了CheckedChangeListener");
            if (!isFastDoubleClick()) {
                mTemplateModel.resetUi();
                if (!isChecked) {
                    nowIsChooseMatting = false;
                    if (nowTemplateIsMattingVideo == 1 && !albumType.isImage(GetPathType.getInstance().getPathType(imgPath.get(0)))) {
                        if (mOriginalPathList != null && mOriginalPathList.size() != 0) {
                            changeMaterialCallbackForVideo(null, mOriginalPathList.get(0),
                                    false);
                        } else {
                            changeMaterialCallbackForVideo(null, imgPath.get(0), false);
                        }
                    } else {
                        StatisticsEventAffair.getInstance().setFlag(TemplateActivity.this, "1_mb_bj_Cutoutopen");
                        //修改图为裁剪后的素材
                        presenter.changeMaterial(mOriginalPathList, bottomButtonCount, needAssetsCount);
                    }
                } else {
                    chooseChecked();
                }

                if (mPlayer != null) {
                    mPlayer.pause();
                    ivPlayButton.setImageResource(R.mipmap.iv_play);
                    isPlaying = false;
                }
                showPreview(false, true);
                AnimForViewShowAndHide.getInstance().show(mContainer);
            }
        });

        if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.PICTUREALBUM)) {
            nowIsPhotographAlbum = true;
            findViewById(R.id.ll_Matting).setVisibility(View.GONE);
        } else {
            int is_pic = templateItem.getIs_pic();
            if (is_pic == 1) {
                findViewById(R.id.ll_Matting).setVisibility(View.GONE);
            }
        }

        //只是唱歌页面
        if (isToSing) {
//            relayout_bottom.setVisibility(View.GONE);
            findViewById(R.id.ll_progress).setVisibility(View.GONE);
            findViewById(R.id.ll_viewpager_container).setVisibility(View.GONE);
        }
    }

    /**
     * 数据初始化
     */
    private void initData() {
        Bundle bundle = getIntent().getBundleExtra(TEMPLATE_BUNDLE_NAME);
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
            LogUtil.d(TAG, "picout=" + picout);
            LogUtil.d(TAG, "templateName=" + templateName);
            LogUtil.d(TAG, "templateFilePath = " + mTemplateFilePath);
        }
    }

    private void setOnClickListener() {


    }


    /**
     * description ：选择状态
     * creation date: 2021/1/4
     * user : zhangtongju
     */
    private void chooseChecked() {
        nowIsChooseMatting = true;
        //选中状态
        if (nowTemplateIsMattingVideo == 1 && !albumType.isImage(GetPathType.getInstance().getPathType(imgPath.get(0)))) {
            handler.sendEmptyMessage(1);
            new Thread(() -> presenter.intoMattingVideo(imgPath.get(0), templateName)).start();
        } else {
            StatisticsEventAffair.getInstance().setFlag(TemplateActivity.this, "1_mb_bj_Cutoutoff");
            //修改为裁剪前的素材
            presenter.changeMaterial(imgPath, bottomButtonCount, needAssetsCount);
        }
    }


    private void setMattingBtnState() {
        if (picout == 0) {
            //当前是视频的情况下，且用户没有选择扣视频,上面的选中效果就取消
            switch_button.setChecked(false);
            nowIsChooseMatting = false;
        } else {
            switch_button.setChecked(true);
            nowIsChooseMatting = true;
        }
        //漫画的时候取消上面的切换按钮
        if (nowTemplateIsAnim == 1) {
            findViewById(R.id.ll_Matting).setVisibility(View.GONE);
        }
    }


//    private void test() {
//        if (originalPath != null && originalPath.size() > 0) {
//            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//            retriever.setDataSource(originalPath.get(0));
//            String sss = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT);
//            LogUtil.d("OOM2", "原视频帧数是" + sss);
//        }
//        MediaMetadataRetriever retriever2 = new MediaMetadataRetriever();
//        retriever2.setDataSource(imgPath.get(0));
//        String sss2 = retriever2.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT);
//        LogUtil.d("OOM2", "灰度图帧数是" + sss2);
//    }


    @Override
    protected void initAction() {
        //漫画逻辑和视频抠图逻辑大体差不多
//        if (nowTemplateIsMattingVideo == 1 || nowTemplateIsAnim == 1) {
//            presenter.loadTemplate(mFolder.getPath(), this, 1);
//        } else {
//            presenter.loadTemplate(mFolder.getPath(), this, 0);
//        }

        presenter.loadTemplate(mFolder.getPath(), this, nowTemplateIsAnim, nowTemplateIsMattingVideo, isToSing);

        mPlayerView.setPlayCallback(mListener);
    }

    @Override
    public void completeTemplate(TemplateModel templateModel) {
        mTemplateModel = templateModel;
        findViewById(R.id.ll_viewpager_container).setVisibility(View.VISIBLE);
        LogUtil.d("OOM3", "initBottomLayout");
        initBottomLayout();
        if (nowTemplateIsAnim == 1 || nowTemplateIsMattingVideo == 1) {
            mTemplateModel.cartoonPath = imgPath.get(0);  //设置灰度图
        }
        bottomButtonCount = templateModel.groupSize;
        int duration = mTemplateModel.getDuration();
        needDuration = (long) (duration / mTemplateModel.fps);
        tv_end_time.setText(TimeUtils.secondToTime((needDuration)));
        getNowChooseIndexMidiaUi();
        LogUtil.d("OOM3", "initTemplateViews");
        initTemplateViews(mTemplateModel);
        //设置切换按钮
        new Handler().postDelayed(this::setMattingBtnState, 500);
    }

    @Override
    public void toPreview(String path) {
        videoPlayer.setUp(path, true, "");
        videoPlayer.startPlayLogic();
        videoPlayer.setGSYVideoProgressListener((progress, secProgress, currentPosition, duration) -> seekBar.setProgress(progress));
//        videoPlayer.setVideoAllCallBack(new VideoPlayerCallbackForTemplate(isSuccess -> ));
        videoPlayer.setVideoAllCallBack(new VideoPlayerCallbackForTemplate(new VideoPlayerCallbackForTemplate.videoPlayerStopListener() {
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
    public void ChangeMaterialCallback(ArrayList<TemplateThumbItem> callbackListItem, List<String> list_all, List<String> listAssets) {
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
                ivPlayButton.setImageResource(R.mipmap.iv_play);
                isPlaying = false;
                showPreview(true, false);
            }
        } else {
            isPlaying = true;
            ivPlayButton.setImageResource(R.mipmap.pause);
            if (real_time_preview.getVisibility() == View.VISIBLE) {
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
        waitingDialogProgress.setProgress("生成中~\n" +
                "如预览卡顿\n" +
                "保存效果最佳");
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
            LogUtil.d("oom", "渲染需要的地址为" + path);
        }
        Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> new Handler().post(() -> showPreview(true, false)));

        if (mTemplateModel.HasBj && !TextUtils.isEmpty(mTemplateModel.getBackgroundPath())) {
            String[] newPaths = new String[paths.length + 1];
            System.arraycopy(paths, 0, newPaths, 0, paths.length);
            MediaUiModel2 mediaUiModel2 = (MediaUiModel2) mTemplateModel.mAssets.get(0).ui;
            if (albumType.isVideo(GetPathType.getInstance().getPathType(mTemplateModel.getBackgroundPath()))) {
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
     * description ：获得的视频封面，且扣了图片的
     * creation date: 2020/4/14
     * user : zhangtongju
     */
    @Override
    public void showMattingVideoCover(Bitmap bp, String bpPath) {
        LogUtil.d("OOM4", "封面地址为" + bpPath);
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
                                mTemplateViews.get(nowChoosePosition).invalidate(); //提示重新绘制预览图
                            }
                        });
                    }
                }
            }
        }
        videoMattingCaver = bp;
        runOnUiThread(() -> modificationSingleThumbItem(bpPath));
    }

    @Override
    public void showBottomIcon(String path) {
        LogUtil.d("OOM", "底部图的路径为" + path);
        TemplateThumbItem item1 = listItem.get(lastChoosePosition);
        item1.setPathUrl(path);
        listItem.set(lastChoosePosition, item1);
        templateThumbAdapter.notifyItemChanged(lastChoosePosition);
    }

    /**
     * description ：是否抠图按钮切换 针对于视频
     * needMatting 是否需要抠图
     * creation date: 2020/4/14
     * user : zhangtongju
     */
    @Override
    public void changeMaterialCallbackForVideo(String originalVideoPath, String path, boolean needMatting) {
        //可能之前没勾选抠图，所以originalPath 为null，这里需要null 判断
        if (needMatting) {
            LogUtil.d("OOM2", "抠图");
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
            //不需要抠图就不需要扣第一帧页面
            if (mOriginalPathList != null && mOriginalPathList.size() != 0) {
                mTemplateModel.cartoonPath = imgPath.get(0);  //设置灰度图
                LogUtil.d("OOM2", "switch_button.isChecked()=" + switch_button.isChecked());
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
            LogUtil.d("OOM4", "不抠图");
            //不需要抠图
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
     * description ：换装完成后的回调
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
        mLoadingDialog.setTitleStr("飞闪预览处理中");
        mLoadingDialog.setProgress(progress);
        mLoadingDialog.setContentStr("请勿离开页面");
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
     * description ：图片替换全部mediaUiModel2
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
        videoPlayer.onVideoPause();
        showPreview(false, true);
        isPlaying = false;
        ivPlayButton.setImageResource(R.mipmap.iv_play);
        if (mPlayer != null) {
            mPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoPlayer.onVideoResume();
    }


    private void showPreview(boolean isPreview, boolean hasAnim) {
        LogUtil.d("OOM", "showPreview=" + isPreview + "isRealtime=" + isRealtime);
        if (isPreview) {
            if (isRealtime) {
                AnimForViewShowAndHide.getInstance().show(real_time_preview);
//                real_time_preview.setVisibility(View.VISIBLE);
            } else {
//                videoPlayer.setVisibility(View.VISIBLE);
                AnimForViewShowAndHide.getInstance().show(videoPlayer);
            }
            if (hasAnim) {
                AnimForViewShowAndHide.getInstance().hide(mContainer);
            } else {
//                mContainer.setVisibility(View.GONE);
                AnimForViewShowAndHide.getInstance().hide(mContainer);

            }
            modificationThumbForRedactData(true);
        } else {
            videoPlayer.setVisibility(View.GONE);
            real_time_preview.setVisibility(View.INVISIBLE);
            mContainer.setVisibility(View.VISIBLE);
            modificationThumbForRedactData(false);
        }
    }


    @Override
    public void pickMedia(MediaUiModel model) {
        if (!DoubleClick.getInstance().isFastZDYDoubleClick(1000)) {
            pickIndex = model.getNowIndex();
            pickGroupIndex = model.getNowGroup();
            LogUtil.d("OOM", "当前的点击位置为" + pickIndex + "pickGroupIndex=" + pickGroupIndex);
            if (isToSing) {
                AlbumManager.chooseWhichAlbum(TemplateActivity.this, 1, REQUEST_SINGLE_MEDIA, this, 1, "");
            } else {
                if (isCanChooseVideo || nowIsPhotographAlbum) {
                    // 只有是否选择视频的区别
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
        mTextEditLayout.setVisibility(View.VISIBLE);
        mTextEditLayout.setupWidth(model);
    }


    /**
     * description ：如果有背景，这里需要忽略最后一个值，因为最后一个模板是背景模板，用户是不能够操作的。
     * date: ：2019/11/28 14:10
     * author: 张同举 @邮箱 jutongzhang@sina.com
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
            mContainer.addView(templateView, params);
        }
        progress = new WaitingDialog_progress(this);
        progress.openProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.d("OOM4", "显示了加载框");
                isFirstReplace(imgPath);
            }
        }).start();

    }


    /**
     * description ：第一次添加素材
     * creation date: 2020/4/8
     * param :  paths 是第一次用户选择的素材
     * user : zhangtongju
     */
    private void isFirstReplace(List<String> paths) {
        LogUtil.d("OOM4", "isFirstReplace");
        if (mTemplateViews != null && mTemplateViews.size() > 0) {
            //这里只是为了底部按钮
            List<String> list_all = new ArrayList<>();
            for (int i = 0; i < bottomButtonCount; i++) {  //填满数据，为了缩略图
                if (paths.size() > i && !TextUtils.isEmpty(paths.get(i))) {
                    list_all.add(paths.get(i)); //前面的时path ，后面的为默认的path
                } else {
                    list_all.add(SxveConstans.default_bg_path);
                }
            }
            if (nowTemplateIsAnim == 1 || nowTemplateIsMattingVideo == 1) {
                //漫画 特殊
                TemplateThumbItem templateThumbItem = new TemplateThumbItem();
                if (mOriginalPathList != null && mOriginalPathList.size() != 0) {
                    templateThumbItem.setPathUrl(mOriginalPathList.get(0));
                } else {
                    templateThumbItem.setPathUrl(imgPath.get(0));
                }
                templateThumbItem.setIsCheck(0);
                listItem.set(0, templateThumbItem);
            } else {
                for (int i = 0; i < list_all.size(); i++) {  //合成底部缩略图
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
            //这里是为了替换用户操作的页面
            List<String> listAssets = new ArrayList<>();
            for (int i = 0; i < needAssetsCount; i++) {  //填满数据，为了缩略图
                if (paths.size() > i && !TextUtils.isEmpty(paths.get(i))) {
                    if (nowTemplateIsAnim == 1 || nowTemplateIsMattingVideo == 1) {
                        //漫画或者灰度图，
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
                LogUtil.d("OOM4", "替换图片isCOMPALTE");
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
                        mTemplateViews.get(nowChoosePosition).invalidate(); //提示重新绘制预览图
                    }

                    LogUtil.d("OOM4", "关闭加载框");
                    progress.closeProgressDialog();
                    WaitingDialog.closeProgressDialog();
                }

            }));  //批量替换图片

        }
    }


    /**
     * description ：选择当前的点，里面有个mModel ，其中mModel 一定要保证是当前可见mediaModel 的mModel,否则会出现灰屏的情况
     * date: ：2019/11/28 13:58
     * author: 张同举 @邮箱 jutongzhang@sina.com
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
                        ivPlayButton.setImageResource(R.mipmap.iv_play);
                        isPlaying = false;
                    }

                    if (nowChoosePosition != lastChoosePosition) {
                        selectGroup(position);
                        modificationThumbData(lastChoosePosition, position);
                    } else {
                        if (nowIsPhotographAlbum && !DoubleClick.getInstance().isFastDoubleClick()) {
//                            MediaUiModel2 mediaUi2 = (MediaUiModel2) mTemplateModel.getAssets().get(lastChoosePosition).ui;
                            if (nowClickMediaUi2.isVideoType()) {
                                //实际需要的时长
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
                    AnimForViewShowAndHide.getInstance().show(mContainer);
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
                .setTitle("生成中...")
                .build();
        return dialog;
    }


    @Override
    @OnClick({R.id.tv_top_submit, R.id.iv_play, R.id.edit_view_container, R.id.iv_top_back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_top_submit:
                if (!DoubleClick.getInstance().isFastZDYDoubleClick(3000)) {
                    if (isToSing) {
                        MediaUiModel2 mediaUi2 = (MediaUiModel2) mTemplateModel.getAssets().get(lastChoosePosition).ui;
                        String path = mediaUi2.getSnapPath(Objects.requireNonNull(this.getExternalFilesDir("runCatch/")).getPath());
                        LogUtil.d("OOM2", "上传的图片地址为" + path);
//                        String path = CopyFileFromAssets.copyAssets(this, "test.mp4");
                        mediaUi2.GetTransFormChangeData(new MediaUiModel2.TranChangeCallback() {
                            @Override
                            public void changeBack(float TranX, float TranY, float Scale) {
                                String bjPath = mOriginalPathList.get(0);
                                if (nowIsChooseMatting) {
                                    bjPath = imgPath.get(0);
                                }
                                VideoFusionModel videoFusionModel = new VideoFusionModel(TemplateActivity.this, path, bjPath, fromTo, templateName, mediaUi2.getOriginalBitmapWidth(), mediaUi2.getOriginalBitmapHeight(), TranX, TranY, Scale);
                                videoFusionModel.uploadFileToHuawei(path, templateId);
                            }
                        });
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
                                ivPlayButton.setImageResource(R.mipmap.iv_play);
                                isPlaying = false;
                                showPreview(true, false);
                            }
                        }
                        if (nowChooseMusic != 0) {
                            if (nowChooseMusic == 3) {
                                presenter.renderVideo(mFolder.getPath(), downMusicPath, false, nowTemplateIsAnim, imgPath);
                            } else {
                                presenter.renderVideo(mFolder.getPath(), nowSpliteMusic, false, nowTemplateIsAnim, imgPath);
                            }
                        } else {
                            presenter.renderVideo(mFolder.getPath(), mAudio1Path, false, nowTemplateIsAnim, imgPath);
                        }

                        presenter.StatisticsToSave(templateId);
                    }


                }


                break;

            case R.id.iv_play:
                if (!DoubleClick.getInstance().isFastZDYDoubleClick(500)) {
                    onclickPlaying();
                }
                break;

            case R.id.edit_view_container:
                break;
            case R.id.iv_top_back:
                onBackPressed();
                break;

            default:
                break;

        }
    }

    @Override
    public void onBackPressed() {
        if (mTextEditLayout.getVisibility() == View.VISIBLE) {
            mTextEditLayout.hide();
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
                .setTitle("确定退出吗？")
                .setPositiveButton("确定")
                .setNegativeButton("取消")
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
        videoPlayer.release();
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
            LogUtil.d("OOM", "路徑為" + mSource);
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
                    seekBar.setMax(mDuration);
                    mPlayer = mPlayerView.setTemplate(template);
                    seekBar.setProgress(0);
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
            mPlayerView.post(() -> {
                LogUtil.d("OOM", "onProgressChangedFrame=" + frame);
                seekBar.setProgress(frame);
                float nowDuration = frame / mTemplateModel.fps;
                tv_start_time.setText(TimeUtils.secondToTime((long) (nowDuration)));
            });
        }

        @Override
        public void onFinish() {
            runOnUiThread(() -> {
                isPlaying = false;
                tv_start_time.setText("00:00");
                showPreview(false, true);
                ivPlayButton.setImageResource(R.mipmap.iv_play);
                seekBar.setProgress(0);
            });
        }
    };


    private String lastChooseFilePath;

    @Override
    public void resultFilePath(int tag, List<String> paths, boolean isCancel, boolean isFromCamera, ArrayList<AlbumFile> albumFileList) {
        if (!isCancel) {
            if (tag == REQUEST_SINGLE_MEDIA) {
                if (paths != null && paths.size() > 0) {
                    //重新生成换装
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
     * description ：切换素材后的回调
     * creation date: 2020/12/10
     * user : zhangtongju
     */
    private void resultFileDispose(String path) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lastChooseFilePath = path;
                String mimeType = GetPathType.getInstance().getMediaType(path);
                if (albumType.isImage(mimeType)) {
                    if (mOriginalPathList == null || mOriginalPathList.size() == 0) {
                        if (nowTemplateIsMattingVideo == 1) {
                            mattingImage(path);
                        } else {
                            //不需要抠图
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
     * description ：跳转到抠图页面
     * creation date: 2020/5/6
     * user : zhangtongju
     */
    private void mattingImage(String path) {
        boolean hasCache = nowTemplateIsAnim != 1;
        CompressionCuttingManage manage = new CompressionCuttingManage(TemplateActivity.this, templateId, hasCache, tailorPaths -> {
            if (mOriginalPathList != null && mOriginalPathList.size() != 0) {
                mOriginalPathList.set(lastChoosePosition, path);
            } else {
                //可能来自视频抠图页面，所以会出现出现null
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
                    //这里是兼容多图，通过id 来得到具体是那个位置
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
     * description ：来自抠图按钮切换或者替换素材
     * creation date: 2020/4/14
     * user : zhangtongju
     */
    @Subscribe
    public void onEventMainThread(MattingVideoEnity event) {
        LogUtil.d("oom2", "lastChooseFilePath=" + lastChooseFilePath);
        nowClickMediaUi2.setPathOrigin(lastChooseFilePath);

        if (event.getTag() == cutVideoTag) {
            LogUtil.d("OOM2", "进入到了onEventMainThread");
            getSingleCatVideoPath(event.getMattingPath());
        } else {
            mTemplateModel.resetUi();
            if (event.getTag() == 1) {
                //点击了切换按钮且没扣过视频
                changeMaterialCallbackForVideo(event.getOriginalPath(), event.getMattingPath(), true);
            } else if (event.getTag() == 2) {
                nowTemplateIsMattingVideo = 1;
                mTemplateModel.mAssets.get(0).setNeedMatting(true);

                //替换素材
                if (event.getOriginalPath() == null || !nowIsChooseMatting) {
                    LogUtil.d("OOM2", "event.getOriginalPath() == null || !nowIsChooseMatting");
                    if (event.getOriginalPath() == null) {
                        LogUtil.d("OOM2", "event.getOriginalPath()== null");
                        //用户没有选择抠图
                        changeMaterialCallbackForVideo(null, event.getMattingPath(), false);
                        //这里需要重新设置底部图，但是glide 视频路径相同。所以glide 不会刷新
                        presenter.getButtomIcon(event.getMattingPath());
                        switch_button.setChecked(false);
                        changeMaterialMusic(event.getMattingPath());
                    } else {
                        LogUtil.d("OOM2", "event.getOriginalPath()！= null");
                        //用户选择了抠图但是没有切换抠图
                        changeMaterialCallbackForVideo(null, event.getOriginalPath(), false);
                        //这里需要重新设置底部图，但是glide 视频路径相同。所以glide 不会刷新
                        presenter.getButtomIcon(event.getOriginalPath());
                        changeMaterialMusic(event.getOriginalPath());
                        switch_button.setChecked(true);
                    }
                } else {
                    LogUtil.d("OOM2", "用户选择了抠图");
                    //用户选择了抠图
                    changeMaterialCallbackForVideo(event.getOriginalPath(), event.getMattingPath(), true);
                    presenter.getButtomIcon(event.getOriginalPath());
                    changeMaterialMusic(event.getMattingPath());
//                if(!switch_button.isChecked()){
//                    new Handler().postDelayed(() -> switch_button.setChecked(true),500);
//                }
//                LogUtil.d("OOM","重新选择了抠图");
                }
            }
            templateThumbForMusic.findViewById(R.id.ll_choose_0).setVisibility(View.VISIBLE);
            primitivePath = event.getPrimitivePath();
        }

        if (nowChooseMusic == 1) {
            //重新勾选音乐

            chooseMaterialMusic();


        }

    }


    private void chooseMaterialMusic() {
        clearCheckBox();
        String path = imgPath.get(0);
        if (albumType.isVideo(GetPathTypeModel.getInstance().getMediaType(path))) {
            presenter.getBjMusic(primitivePath);
            changeMusic();
            cb_0.setImageResource(R.mipmap.template_btn_selected);
            nowChooseMusic = 1;
        } else {
            chooseTemplateMusic();
            ToastUtil.showToast("当前素材不是视频");
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
            } else {
                titlesHasBj = new String[]{getString(R.string.template_edit), getString(R.string.template_bj),
                        getString(R.string.template_music)};
            }
        } else {
            if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.PICTUREALBUM)) {
                titlesHasBj = new String[]{getString(R.string.template), getString(R.string.template_edit),
                        getString(R.string.template_music)};
            } else if (isToSing) {
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
        commonTabLayout.setTabData(mTabEntities);
        commonTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                if (position == 1) {
                    if (mTemplateModel.HasBj) {
                        presenter.chooseBj(templateItem);
                        commonTabLayout.setCurrentTab(lastChooseCommonTabLayout);
                    } else {
                        //如果不是背景模板，那么还是有音乐
                        lastChooseCommonTabLayout = 2;
                        viewPager.setCurrentItem(1);
                        StatisticsEventAffair.getInstance().setFlag(TemplateActivity.this, "11_yj_muscle");

                    }
                } else if (position == 2) {
                    if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.PICTUREALBUM)) {
                        viewPager.setCurrentItem(2);
                    } else {
                        viewPager.setCurrentItem(1);
                        StatisticsEventAffair.getInstance().setFlag(TemplateActivity.this, "11_yj_muscle");

                    }
                    lastChooseCommonTabLayout = 2;
                } else {
                    lastChooseCommonTabLayout = 0;
                    viewPager.setCurrentItem(0);
                }
            }

            @Override
            public void onTabReselect(int position) {
            }
        });

        LogUtil.d("OOM3", "--------------");

        ArrayList<View> pagerList = new ArrayList<>();


        if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.PICTUREALBUM)) {
            View templateThumb = LayoutInflater.from(this).inflate(R.layout.view_choose_template, null);
            new ViewChooseTemplate(TemplateActivity.this, templateThumb, changeTemplatePosition, new ViewChooseTemplate.Callback() {
                @Override
                public void onItemClick(int position, String path, NewFragmentTemplateItem item) {
                    clearAllData();
                    //选择模板的回调时间
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
        tv_1.setText("背景音乐");
        TextView tv_2 = templateThumbForMusic.findViewById(R.id.tv_2);
        tv_2.setText("模板音乐");
        cb_0.setOnClickListener(tvMusicListener);
        cb_1.setOnClickListener(tvMusicListener);
        cb_2.setOnClickListener(tvMusicListener);
        cb_3.setOnClickListener(tvMusicListener);
        pagerList.add(templateThumbForMusic);
        TemplateViewPager adapter = new TemplateViewPager(pagerList);
        viewPager.setAdapter(adapter);

        LogUtil.d("OOM3", "底部初始化完成");
    }


    private boolean hasDefaultBj(String[] paths) {
        for (String str : paths
        ) {
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
                //素材
                case R.id.ll_choose_0:
                case R.id.iv_check_box_0:
                    chooseMaterialMusic();
                    break;
                //背景
                case R.id.ll_choose_1:
                case R.id.iv_check_box_1:
                    if (TextUtils.isEmpty(mTemplateModel.getBackgroundPath())) {
                        chooseTemplateMusic();
                        ToastUtil.showToast("背景音乐为默认模板音乐");
                    } else if (!albumType.isVideo(GetPathType.getInstance().getPathType(mTemplateModel.getBackgroundPath()))) {
                        ToastUtil.showToast("没有背景音乐");
                    } else {
                        clearCheckBox();
                        cb_1.setImageResource(R.mipmap.template_btn_selected);
                        nowChooseMusic = 2;
                        presenter.getBjMusic(mTemplateModel.getBackgroundPath());
                    }
                    changeMusic();

                    break;
                //模板
                case R.id.ll_choose_2:
                case R.id.iv_check_box_2:
                    changeMusic();
                    chooseTemplateMusic();
                    break;

                //提取
                case R.id.ll_choose_3:
                case R.id.iv_check_box_3:
                    if (!TextUtils.isEmpty(downMusicPath)) {
                        clearCheckBox();
                        cb_3.setImageResource(R.mipmap.template_btn_selected);
//                        cb_3.setChecked(true);
                        changeMusic();
                        chooseDownMusic();
                    } else {
                        ToastUtil.showToast("沒有添加音乐");
                    }

                    break;

                case R.id.tv_add_music:
                    Intent intent = new Intent(TemplateActivity.this, ChooseMusicActivity.class);
                    intent.putExtra("needDuration", needDuration * 1000);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

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
        if (real_time_preview.getVisibility() == View.VISIBLE) {
            //预览暂停状态
            if (!isPlaying) {
                showPreview(false, true);
                AnimForViewShowAndHide.getInstance().show(mContainer);
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
            LogUtil.d("OOM", "重新选择了视频背景,地址为" + event.getPath());
            String videoBjPath = event.getPath();
            if (TextUtils.isEmpty(videoBjPath)) {
                ToastUtil.showToast("选择了默认背景");
                mTemplateModel.setHasBg("", false);
            } else {
                if (albumType.isVideo(GetPathType.getInstance().getPathType(videoBjPath))) {
                    mTemplateModel.setHasBg(videoBjPath, true);
                    LogUtil.d("OOM", "当前选择的位置为" + nowChooseMusic);
                    LogUtil.d("OOM", "videoBjPath=" + videoBjPath);
                    if (nowChooseMusic == 2) {
                        //如果本来就是选择的背景音乐，那么需要重新得到背景音乐
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
     * description ：添加音乐后的回调
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
                LogUtil.d("oom2", "刷新id--pickGroupIndex=" + pickGroupIndex);
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
                    LogUtil.d("OOM", "设置进去path" + path);
                    mediaUi2.setVideoPath(path, false, 0);
                    break;
                }
            }
            mTemplateViews.get(lastChoosePosition).invalidate();
        }
        modificationSingleThumbItem(path);
    }




    public static Intent buildIntent(Context context){
        Intent intent = new Intent(context, TemplateActivity.class);
        Bundle bundle = new Bundle();
//        bundle.putStringArrayList(INTENT_IMAGE_PATH, (ArrayList<String>) paths);
//        bundle.putInt(INTENT_IS_PIC_NUM, defaultnum);
//        bundle.putString(INTENT_FROM_TO, mOldFromTo);
//        bundle.putInt(INTENT_PIC_OUT, templateItem.getIs_picout());
//        bundle.putInt(INTENT_IS_ANIME, templateItem.getIs_anime());
//        bundle.putString(INTENT_TEMPLATE_NAME, templateItem.getTitle());
//        bundle.putString(INTENT_TEMPLATE_ID, templateItem.getId() + "");
//        bundle.putString(INTENT_VIDEO_TIME, templateItem.getVideotime());
//        bundle.putStringArrayList(INTENT_ORIGINAL_PATH, (ArrayList<String>) originalImagePath);
//        bundle.putString(INTENT_TEMPLATE_FILE_PATH, templateFilePath);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra(TEMPLATE_BUNDLE_NAME, bundle);
//        intent.putExtra(TEMPLATE_ITEM_NAME, templateItem);

        return intent;
    }

}

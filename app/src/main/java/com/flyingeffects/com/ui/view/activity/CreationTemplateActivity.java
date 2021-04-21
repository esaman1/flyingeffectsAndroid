package com.flyingeffects.com.ui.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import com.bumptech.glide.request.target.Target;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.commonlyModel.GetPathType;
import com.flyingeffects.com.commonlyModel.SaveAlbumPathModel;
import com.flyingeffects.com.constans.UiStep;
import com.flyingeffects.com.databinding.ActCreationTemplateEditBinding;
import com.flyingeffects.com.enity.ChooseVideoAddSticker;
import com.flyingeffects.com.enity.CutSuccess;
import com.flyingeffects.com.enity.DownVideoPath;
import com.flyingeffects.com.enity.VideoInfo;
import com.flyingeffects.com.manager.AdConfigs;
import com.flyingeffects.com.manager.AlbumManager;
import com.flyingeffects.com.manager.CompressionCuttingManage;
import com.flyingeffects.com.manager.DataCleanManager;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.view.CreationTemplateMvpView;
import com.flyingeffects.com.ui.model.AnimStickerModel;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.ui.model.GetPathTypeModel;
import com.flyingeffects.com.ui.model.VideoManage;
import com.flyingeffects.com.ui.presenter.CreationTemplateMvpPresenter;
import com.flyingeffects.com.ui.view.dialog.CommonMessageDialog;
import com.flyingeffects.com.ui.view.dialog.LoadingDialog;
import com.flyingeffects.com.utils.FileUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.ScreenCaptureUtil;
import com.flyingeffects.com.utils.TimeUtils;
import com.flyingeffects.com.view.StickerView;
import com.flyingeffects.com.view.drag.CreationTemplateProgressBarView;
import com.flyingeffects.com.view.drag.TemplateMaterialItemView;
import com.flyingeffects.com.view.drag.TemplateMaterialSeekBarView;
import com.flyingeffects.com.view.mine.CreateViewForAddText;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.lansosdk.videoeditor.MediaInfo;
import com.shixing.sxve.ui.albumType;
import com.shixing.sxve.ui.view.WaitingDialog;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * description ：用户创作页面,里面主要用了langSong 的工具类，对视频进行贴纸的功能
 * creation date: 2020/3/11
 * user : zhangtongju
 */


public class CreationTemplateActivity extends BaseActivity implements CreationTemplateMvpView, TemplateMaterialSeekBarView.SeekBarProgressListener {
    private static final String TAG = "CreationTemplate";

    public static final String BUNDLE_KEY = "Message";
    public static final String BUNDLE_KEY_FROM = "from";
    public static final String BUNDLE_KEY_PATHS = "paths";
    public static final String BUNDLE_KEY_VIDEO_PATH = "video_path";
    public static final String BUNDLE_KEY_ORIGINAL_PATH = "originalPath";
    public static final String BUNDLE_KEY_NEED_CUT = "isNeedCut";
    public static final String BUNDLE_KEY_TITLE = "bjTemplateTitle";
    public static final String BUNDLE_KEY_TEMPLATE_ID = "templateId";
    public static final String BUNDLE_KEY_IS_LANDSCAPE = "isLandscape";
    public static final String BUNDLE_KEY_BACKGROUND_IMAGE = "backgroundImage";

    public static final int FROM_CREATION_CODE = 0;
    public static final int FROM_DRESS_UP_BACK_CODE = 1;

    private Context mContext;

    private CreateViewForAddText mCreateViewForAddText;

    private LoadingDialog mLoadingDialog;

    private boolean nowUiIsLandscape = false;

    private boolean isEndDestroy = false;

    private int templateId;
    private boolean isIntoPause = false;
    public final static int SELECTALBUM = 0;

    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener;

    /**
     * 源视频地址
     */
    private String originalPath;
    private String imgPath;
    private CreationTemplateMvpPresenter presenter;

    /**
     * 默认背景，也是是否选择了背景的重要判断，
     */
    private String videoPath;

    /**
     * 默认图片背景，""表示绿幕
     */
    private String imageBjPath;

    /**
     * 当前预览状态，是否在播放中
     */
    private boolean isPlaying = false;

    /**
     * 是否初始化过播放器
     */
    private boolean isInitVideoLayer = false;
    private long allVideoDuration;
    private boolean isPlayComplate = false;

    /**
     * 只有背景模板才有，自定义的话这个值为""
     */
    private String title;

    /**
     * 获得背景视频音乐
     */
    private String bgmPath;
    private SimpleExoPlayer exoPlayer;
    /**
     * 背景音乐播放器
     */
    private MediaPlayer bgmPlayer;
    /**
     * 默认抠图开关
     */
    private boolean isNeedCut;
    /**
     * 是不是点击了加字的tab
     */
    private boolean isClickAddTextTag = false;
    /**
     * 素材手动拖动
     */
    boolean mSeekBarViewManualDrag = false;
    /**
     * 当前播放的进度
     */
    private long mCutStartTime;
    /**
     * 整个视频的结束时间
     */
    private long mCutEndTime;

    private long progressBarProgress;
    /**
     * 背景音乐播放的开始位置
     */
    private long musicStartTime = 0;
    /**
     * 背景音乐第一个素材播放的开始位置
     */
    private long musicStartFirstTime = 0;
    /**
     * 背景音乐第一个素材结束播放的开始位置
     */
    private long musicEndFirstTime = 0;

    /**
     * 背景音乐播放的结束位置
     */
    private long musicEndTime;

    private int musicChooseIndex = 0;

    private int initHeight = 0;//屏幕初始高度
    private int currentHeight;//调用onGlobalLayout()后，当前屏幕高度
    private int firstFlag = 0;//虚拟导航栏状态的**首次**变化情况
    private int status = 0;//虚拟导航栏状态的变化情况
    private int mFrom;

    private MediaSource mediaSource;

    private ActCreationTemplateEditBinding mBinding;

    /**
     * 换脸-换背景过来时带的背景图
     */
    private String mBackgroundImage;
    private String mFramePath;

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initView() {
        mContext = CreationTemplateActivity.this;
        mBinding = ActCreationTemplateEditBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);

        mBinding.tvTopSubmit.setText("下一步");

        EventBus.getDefault().register(this);
        mLoadingDialog = buildLoadingDialog();
        getLifecycle().addObserver(mLoadingDialog);
        LogUtil.d("OOM", "进入到创作页面");

        initBundleData();

        presenter = new CreationTemplateMvpPresenter(this, this, videoPath, mBinding.idVviewRealtimeGllayout, originalPath, null);
        LogUtil.d(TAG, "videoPath = " + videoPath);

        setOnClickListener();

        setDefaultVideoPlayerView();

        //数据收集
        presenter.statisticsDuration(videoPath, this);


        //是否显示多时间线
        seekBarViewIsShow(true);

        setProgressBarListener();

        mBinding.materialSeekBarView.setProgressListener(this);
        //初始化整体容器，获取高度
        initCreationContainer();

        setDefaultBottomVisible();
    }

    /**
     * 根据不同来源显示不同UI
     */
    private void setDefaultBottomVisible() {
        if (mFrom == FROM_DRESS_UP_BACK_CODE) {
            mBinding.tvMusic.setVisibility(View.GONE);
            mBinding.tvAnim.setVisibility(View.GONE);
            mBinding.tvFrame.setVisibility(View.VISIBLE);
            mBinding.rlBackImage.setVisibility(View.VISIBLE);
            mBinding.relativePlayerView.setVisibility(View.GONE);
            mBinding.llProgress.setVisibility(View.GONE);
            mBinding.ivChangeUi.setVisibility(View.GONE);
            mBinding.tvCurrentTime.setVisibility(View.GONE);
            mBinding.tvTotal.setVisibility(View.GONE);
            mBinding.rlSeekBar.setVisibility(View.INVISIBLE);
            mBinding.viewPager.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 初始化整体容器，获取高度
     */
    private void initCreationContainer() {
        mBinding.rlCreationContainer.post(() ->
                initHeight = mBinding.rlCreationContainer.getHeight());

        mGlobalLayoutListener = () -> {
            //如果为0，说明获取高度失败，跳出
            if (initHeight == 0) {
                return;
            }
            currentHeight = mBinding.rlCreationContainer.getHeight();
            if (initHeight > currentHeight) {
                //初始高度大于当前高度，说明虚拟导航栏由隐藏变为显示
                //此时，首次变化firstFlag设置为-1，说明首次进入时无导航栏
                status = -1;
                firstFlag = -1;
            } else if (initHeight < currentHeight) {
                //初始高度小于当前高度，说明虚拟导航栏由显示变为隐藏
                //此时，首次变化firstFlag设置为1，说明首次进入时有导航栏
                status = 1;
                firstFlag = 1;
            } else {
                //虚拟导航栏状态未改变
                //此时，status设置为0
                status = 0;
            }
            //当虚拟导航栏状态发生改变时
            if (status != 0) {
                //虚拟导航栏由隐藏变为显示，首次进入时无导航栏
                if (firstFlag == -1) {
                    //由隐藏变为显示，H需减去48dp；由显示变为隐藏，即回复初始状态，H不变
                    if (status < 0) {
                        if (mCreateViewForAddText != null) {
                            mCreateViewForAddText.setShowHeight(-1,
                                    Math.max(initHeight, currentHeight) - Math.min(initHeight, currentHeight));
                        }
                    }
                }
                //虚拟导航栏由显示变为隐藏，首次进入时有导航栏
                if (firstFlag == 1) {
                    //由显示变为隐藏，
                    if (status > 0) {
                        if (mCreateViewForAddText != null) {
                            mCreateViewForAddText.setShowHeight(1, 0);
                        }
                    }
                }
            }
            //当前高度currentHeight作为下一次变化前的初始高度initHeight
            initHeight = currentHeight;
        };

        mBinding.rlCreationContainer.getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);
    }


    /**
     * 进度条监听
     */
    private void setProgressBarListener() {
        mBinding.progressBarView.setProgressListener(new CreationTemplateProgressBarView.SeekBarProgressListener() {
            @Override
            public void progress(long progress, boolean isDrag) {
                LogUtil.d("OOM4", "mProgressBarViewProgress=" + progress);
                setgsyVideoProgress(progress);

                if (progress < mCutStartTime) {
                    progress = mCutStartTime;
                }

                if (progress > mCutEndTime) {
                    progress = mCutEndTime;
                }

                if (isDrag) {
                    mSeekBarViewManualDrag = false;
                }
                if (!mSeekBarViewManualDrag) {
                    mBinding.materialSeekBarView.dragScrollView = false;
                    mBinding.materialSeekBarView.scrollToPosition(progress);
                }
                progressBarProgress = progress;
                presenter.getNowPlayingTime(progressBarProgress, mCutEndTime);
                mBinding.tvCurrentTime.setText(TimeUtils.timeParse(progress - mCutStartTime) + "s");
            }

            @Override
            public void cutInterval(long starTime, long endTime, boolean isDirection) {
                if (starTime < mCutStartTime) {
                    mBinding.tvCurrentTime.setText(String.format("%ss", TimeUtils.timeParse(0)));
                    mCutStartTime = starTime;
                } else {
                    mCutStartTime = starTime;
                    mBinding.tvCurrentTime.setText(TimeUtils.timeParse(progressBarProgress - mCutStartTime) + "s");
                }
                mCutEndTime = endTime;

                mBinding.tvTotal.setText(TimeUtils.timeParse(mCutEndTime - mCutStartTime) + "s");
                mBinding.materialSeekBarView.setCutStartAndEndTime(starTime, endTime);
                stickerTimeLineOffset();
//                LogUtil.d("oom44", "musicStartTime=" + musicStartTime + "starTime=" + starTime + "musicEndTime=" + musicEndTime + "mCutStartTime=" + mCutStartTime);

                if (isDirection) {
                    mBinding.materialSeekBarView.scrollToPosition(starTime);
                    //--------------ztj   解决bug拖动主进度条，素材音乐没修改的情况
                    if (musicStartTime < starTime) {
//                        musicStartTime = starTime;
//                        long xx = mCutStartTime - lastmCutTime;
//                        musicEndTime = musicEndTime - xx;
                        musicStartFirstTime = starTime;
                        musicStartTime = starTime;
                        LogUtil.d("oom44", "musicStartTime=" + musicStartTime + "starTime=" + starTime + "musicEndTime=" + musicEndTime + "mCutStartTime=" + mCutStartTime);
                    }
                    //ztj  音乐向后挤 ，然后音乐就是最短位置1000+end
                    if (musicEndTime < starTime) {
                        musicEndFirstTime = musicStartTime + 1000;
                        musicEndTime = musicEndFirstTime;
                        LogUtil.d("oom44", "音乐向后挤musicEndTime=" + musicEndTime + "musicStartTime=" + musicStartTime);
                    }

                } else {
                    LogUtil.d("oom444", "xx=");
                    mBinding.materialSeekBarView.scrollToPosition(endTime);
                }

                presenter.getNowPlayingTime(progressBarProgress, mCutEndTime);
            }

            @Override
            public void onTouchEnd() {
                videoToPause();
                presenter.getNowPlayingTime(progressBarProgress, mCutEndTime);
            }
        });
    }

    /**
     * 设置默认视频背景
     */
    private void setDefaultVideoPlayerView() {
        //有视频的时候，初始化视频
        if (!TextUtils.isEmpty(videoPath)) {
            //改变默认横竖屏，需知视频宽高比
            try {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(videoPath);
                String width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                String height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                int w = Integer.parseInt(width);
                int h = Integer.parseInt(height);
                LogUtil.d(TAG, "video width = " + width + " video height = " + height);
                setPlayerViewSize(w > h);
            } catch (Exception e) {
                LogUtil.d("e", e.getMessage());
                setPlayerViewSize(false);
            }
            initExo(videoPath);
        } else {
            //todo 无视频但有背景图，处理换背景 方案待修改
            changeImageBack();
        }
        //从前一个页面设置的横竖屏判断
        if (nowUiIsLandscape) {
            new Handler().postDelayed(() -> setPlayerViewSize(nowUiIsLandscape), 500);
        }
    }

    /**
     * 将线上图片下载到本地缓存
     */
    private void downloadBackImage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = null;
                try {
                    file = Glide.with(BaseApplication.getInstance()).load(mBackgroundImage)
                            .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                imageBjPath = file.getPath();
            }
        }).start();

    }


    /**
     * 初始化页面跳转数据
     */
    private void initBundleData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(BUNDLE_KEY);
        if (bundle != null) {
            mFrom = bundle.getInt(BUNDLE_KEY_FROM);
            imgPath = bundle.getString(BUNDLE_KEY_PATHS);
            videoPath = bundle.getString(BUNDLE_KEY_VIDEO_PATH);
            originalPath = bundle.getString(BUNDLE_KEY_ORIGINAL_PATH);
            isNeedCut = bundle.getBoolean(BUNDLE_KEY_NEED_CUT);
            title = bundle.getString(BUNDLE_KEY_TITLE);
            templateId = bundle.getInt(BUNDLE_KEY_TEMPLATE_ID);
            nowUiIsLandscape = bundle.getBoolean(BUNDLE_KEY_IS_LANDSCAPE, false);
            mBackgroundImage = bundle.getString(BUNDLE_KEY_BACKGROUND_IMAGE);
            LogUtil.d("OOM2", "nowUiIsLandscape=" + nowUiIsLandscape);
        }
    }

    /**
     * 是否显示多时间线
     *
     * @param isShow
     */
    private void seekBarViewIsShow(boolean isShow) {
        if (mFrom != FROM_DRESS_UP_BACK_CODE) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mBinding.llProgress.getLayoutParams();
            if (isShow) {
                layoutParams.addRule(RelativeLayout.ABOVE, R.id.rl_seek_bar);
                mBinding.viewPager.setVisibility(View.GONE);
                mBinding.rlSeekBar.setVisibility(View.VISIBLE);
            } else {
                layoutParams.addRule(RelativeLayout.ABOVE, R.id.viewPager);
                mBinding.viewPager.setVisibility(View.VISIBLE);
                mBinding.rlSeekBar.setVisibility(View.GONE);
            }
            mBinding.llProgress.setLayoutParams(layoutParams);
        }

    }

    private LoadingDialog buildLoadingDialog() {
        return LoadingDialog.getBuilder(mContext)
                .setHasAd(true)
                .setTitle("飞闪预览处理中")
                .setMessage("请耐心等待，不要离开")
                .build();
    }


    private void initExo(String videoPath) {
        if (TextUtils.isEmpty(videoPath)) {
            return;
        }

        exoPlayer = ExoPlayerFactory.newSimpleInstance(CreationTemplateActivity.this);
        mBinding.exoPlayer.setPlayer(exoPlayer);
        //不使用控制器
        mBinding.exoPlayer.setUseController(false);
        exoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
        exoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case Player.STATE_READY:
                        break;
                    case Player.STATE_ENDED:
                        videoToStart();
                        break;
                    case Player.STATE_BUFFERING:
                    case Player.STATE_IDLE:
                    default:
                        break;
                }
            }
        });

        mediaSource = new ProgressiveMediaSource.Factory(
                new DefaultDataSourceFactory(CreationTemplateActivity.this, "exoplayer-codelab")).
                createMediaSource(Uri.fromFile(new File(videoPath)));

        exoPlayer.prepare(mediaSource, true, false);
        pauseExoPlayer();
    }

    private void videoToStart() {
        isPlayComplate = true;
        endTimer();
        isPlaying = false;
        presenter.showGifAnim(false);
        videoToPause();
        seekToVideo(mCutStartTime);
        seekToMusic(mCutStartTime);
        nowStateIsPlaying(false);
        presenter.showAllAnim(false);
    }


    private void pauseExoPlayer() {
        if (exoPlayer != null) {
            LogUtil.d("video", "videoPause");
            exoPlayer.setPlayWhenReady(false);
        }
    }

    /**
     * 开始播放
     * 背景音乐的逻辑全部放在了保存的时候去了
     * 这里只控制是否播放背景视频
     */
    private void videoPlay() {
        if (exoPlayer != null) {
            LogUtil.d("video", "play");
            if (!TextUtils.isEmpty(bgmPath)) {
//                LogUtil.d("OOM5", "musicStartTime=" + musicStartTime);
//                LogUtil.d("OOM5", "musicEndTime=" + musicEndTime);
//                if (bgmPlayer != null) {
//                    //继续播放
//                    bgmPlayer.start();
//                } else {
//                    seekTo(mCutStartTime);
//                    playBGMMusic();
//                }
                exoPlayer.setVolume(0f);
            } else {
                exoPlayer.setVolume(1f);
            }
            if (getCurrentPos() >= mCutEndTime) {
                exoPlayer.seekTo(mCutStartTime);
            } else if (getCurrentPos() < mCutStartTime) {
                exoPlayer.seekTo(mCutStartTime);
            }
            exoPlayer.setPlayWhenReady(true);
        }
        startTimer();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isIntoPause && exoPlayer != null) {
            exoPlayer.prepare(mediaSource, true, false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    pauseExoPlayer();
                    destroyTimer();
                }
            }, 200);
            isIntoPause = false;
        }
    }

    @Override
    protected void initAction() {
        presenter.initStickerView(imgPath, originalPath);
        presenter.initBottomLayout(mBinding.viewPager, getSupportFragmentManager(), mFrom);

        //设置预览界面大小
        initViewLayerRelative();
        setSwitchBtnCheckedListener();
    }

    private void setSwitchBtnCheckedListener() {
        mBinding.switchButton.setOnCheckedChangeListener((view, isChecked) -> {
            if (isChecked) {
                if (UiStep.isFromDownBj) {
                    StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "5_mb_bj_Cutoutopen");
                } else {
                    StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "6_customize_bj_Cutoutopen");
                }
            } else {
                if (UiStep.isFromDownBj) {
                    StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "5_mb_bj_Cutoutoff");
                } else {
                    StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "6_customize_bj_Cutoutoff");
                }
            }

            presenter.checkedChanged(isChecked);
        });
    }

    private void setOnClickListener() {
        mBinding.tvTopSubmit.setOnClickListener(this::onViewClicked);
        mBinding.llPlay.setOnClickListener(this::onViewClicked);
        mBinding.vAddText.ivDeleteAllText.setOnClickListener(this::onViewClicked);
        mBinding.ivAddSticker.setOnClickListener(this::onViewClicked);
        mBinding.ivTopBack.setOnClickListener(this::onViewClicked);
        mBinding.ivChangeUi.setOnClickListener(this::onViewClicked);
        mBinding.tvBackground.setOnClickListener(this::onViewClicked);
        mBinding.tvMusic.setOnClickListener(this::onViewClicked);
        mBinding.tvAnim.setOnClickListener(this::onViewClicked);
        mBinding.tvTiezhi.setOnClickListener(this::onViewClicked);
        mBinding.tvAddText.setOnClickListener(this::onViewClicked);
        mBinding.rlCreationContainer.setOnClickListener(this::onViewClicked);
        mBinding.tvFrame.setOnClickListener(this::onViewClicked);
    }

    private void onViewClicked(View view) {
        if (view == mBinding.tvTopSubmit) {
            submitCreation();
        } else if (view == mBinding.llPlay) {
            onPlayClick();
        } else if (view == mBinding.vAddText.ivDeleteAllText) {
            deleteAllText();
        } else if (view == mBinding.ivAddSticker) {
            addSticker();
        } else if (view == mBinding.ivTopBack) {
            onBackPressed();
        } else if (view == mBinding.tvMusic) {
            onClickMusicBtn();
        } else if (view == mBinding.tvAddText) {
            addText();
        } else if (view == mBinding.tvBackground) {
            onClickBackGroundBtn();
        } else if (view == mBinding.tvAnim) {
            chooseAnimBtn();
        } else if (view == mBinding.tvTiezhi) {
            chooseStickerBtn();
        } else if (view == mBinding.ivChangeUi) {
            changeLandscape();
        } else if (view == mBinding.rlCreationContainer) {
            mBinding.progressBarView.hindArrow();
        } else if (view == mBinding.tvFrame) {
            choosePhotoFrame();
        }
    }


    /**
     * 选择相框
     */
    private void choosePhotoFrame() {
        seekBarViewIsShow(false);
        presenter.chooseAnim(1);
        setTextColor(5);
        isClickAddTextTag = false;
    }

    /**
     * 横竖屏切换
     */
    private void changeLandscape() {
        //横竖屏切换
        nowUiIsLandscape = !nowUiIsLandscape;
        if (mFrom == FROM_DRESS_UP_BACK_CODE) {
            setImageBackSize(nowUiIsLandscape);
        } else {
            setPlayerViewSize(nowUiIsLandscape);
        }
    }

    /**
     * 选择贴纸
     */
    private void chooseStickerBtn() {
        seekBarViewIsShow(false);
        if (mFrom == FROM_DRESS_UP_BACK_CODE) {
            presenter.chooseAnim(2);
        } else {
            presenter.chooseAnim(0);
        }
        setTextColor(0);
        isClickAddTextTag = false;
    }

    /**
     * 选择动画
     */
    private void chooseAnimBtn() {
        seekBarViewIsShow(false);
        presenter.chooseAnim(1);
        setTextColor(1);
        isClickAddTextTag = false;
    }

    /**
     * 选择背景
     */
    private void chooseBackground() {
        Intent intent = new Intent(
                this, ChooseBackgroundTemplateActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        isClickAddTextTag = false;
    }

    /**
     * 添加文字
     */
    private void addText() {
        seekBarViewIsShow(false);
        presenter.addTextSticker();
        intoTextStyleDialog("");
        isClickAddTextTag = true;
        StatisticsEventAffair.getInstance().setFlag(this, "20_bj_text");
    }

    /**
     * 选择音乐
     */
    private void onClickMusicBtn() {
        seekBarViewIsShow(false);
        presenter.chooseAnim(2);
        setTextColor(2);
        isClickAddTextTag = false;
    }

    /**
     * 选择背景 相框页面
     */
    private void onClickBackGroundBtn() {
        if (mFrom == FROM_CREATION_CODE) {
            chooseBackground();
        } else if (mFrom == FROM_DRESS_UP_BACK_CODE) {
            chooseBackgroundDressUp();
        }
    }

    private void chooseBackgroundDressUp() {
        seekBarViewIsShow(false);
        presenter.chooseAnim(0);
        setTextColor(4);
        isClickAddTextTag = false;
    }

    /**
     * 点击添加贴纸按钮
     */
    private void addSticker() {
        if (!DoubleClick.getInstance().isFastZDYDoubleClick(1000)) {
            presenter.showAllAnim(false);
            if (isPlaying) {
                videoToPause();//添加贴纸
                isPlaying = false;
                endTimer();
                presenter.showGifAnim(false);
                nowStateIsPlaying(false);
            }
            if (UiStep.isFromDownBj) {
                StatisticsEventAffair.getInstance().setFlag(CreationTemplateActivity.this, "5_mb_bj_material");
                StatisticsEventAffair.getInstance().setFlag(CreationTemplateActivity.this, "8_material");
            } else {
                StatisticsEventAffair.getInstance().setFlag(CreationTemplateActivity.this, "6_customize_bj_material");
                StatisticsEventAffair.getInstance().setFlag(CreationTemplateActivity.this, "7_material");
            }
            //添加新的贴纸，这里的贴纸就是用户选择的贴纸
            AlbumManager.chooseAlbum(this, 1, SELECTALBUM, (tag, paths, isCancel, isFromCamera, albumFileList) -> {
                Log.d("OOM", "isCancel=" + isCancel);
                if (!isCancel) {
                    //如果是选择的视频，就需要得到封面，然后设置在matting里面去，然后吧原图设置为视频地址
                    String path = paths.get(0);
                    String pathType = GetPathTypeModel.getInstance().getMediaType(path);
                    if (albumType.isImage(pathType)) {
                        StatisticsEventAffair.getInstance().setFlag(CreationTemplateActivity.this, "7_SelectImage");
                        CompressionCuttingManage manage = new CompressionCuttingManage(CreationTemplateActivity.this, "", tailorPaths -> {
                            presenter.addNewSticker(tailorPaths.get(0), paths.get(0));
                        });
                        manage.toMatting(paths);
                    } else {
                        //贴纸选择的视频
                        intoVideoCropActivity(paths.get(0));
                        StatisticsEventAffair.getInstance().setFlag(CreationTemplateActivity.this, "7_Selectvideo");
                    }
                }
            }, "");
        }
    }

    /**
     * 删除所有文字贴纸
     */
    private void deleteAllText() {
        presenter.deleteAllTextSticker();
        if (mCreateViewForAddText != null) {
            mCreateViewForAddText.hideInputTextDialog();
        }
    }

    /**
     * 提交完成品
     */
    private void submitCreation() {
        if (mFrom == FROM_DRESS_UP_BACK_CODE) {
            presenter.keepPicture(mBinding.relativeContentAllContent);
        } else {
            DataCleanManager.deleteFilesByDirectory(getExternalFilesDir("ExtractFrame"));
            DataCleanManager.deleteFilesByDirectory(getExternalFilesDir("cacheMattingFolder"));
            if (isPlaying) {
                videoToPause();//submit
                pauseBgmMusic();
                endTimer();
            }
            if (!TextUtils.isEmpty(title)) {
                StatisticsEventAffair.getInstance().setFlag(CreationTemplateActivity.this, "5_mb_bj_save", title);
            } else {
                StatisticsEventAffair.getInstance().setFlag(CreationTemplateActivity.this, "6_customize_bj_save");
            }

            if (UiStep.isFromDownBj) {
                StatisticsEventAffair.getInstance().setFlag(CreationTemplateActivity.this, "7_Preview");
            } else {
                StatisticsEventAffair.getInstance().setFlag(CreationTemplateActivity.this, "8_Preview");
            }
            if (musicChooseIndex == 2) {
                musicEndTime = allVideoDuration;
                musicStartTime = 0;
            }
            presenter.toSaveVideo(imageBjPath, nowUiIsLandscape, percentageH, templateId, musicStartTime, musicEndTime, mCutStartTime, mCutEndTime, title);
            seekBarViewIsShow(true);
        }


    }

    /**
     * 点击播放按键时
     */
    private void onPlayClick() {
        if (!DoubleClick.getInstance().isFastZDYDoubleClick(500)) {
            if (isPlaying) {
                pauseBgmMusic();
                isIntoPause = false;
                isPlayComplate = false;
                videoToPause();//点击播放暂定
                presenter.showGifAnim(false);
                isPlaying = false;
                nowStateIsPlaying(false);
                presenter.showAllAnim(false);
            } else {
                StatisticsEventAffair.getInstance().setFlag(CreationTemplateActivity.this, " 14_preview_video_bj");
                WaitingDialog.openPragressDialog(this);
                new Thread(() -> presenter.showAllAnim(true)).start();
            }
            mSeekBarViewManualDrag = false;
        }
    }


    private void intoTextStyleDialog(String inputText) {
        if (!DoubleClick.getInstance().isFastDoubleClick()) {
            if (mCreateViewForAddText != null) {
                mCreateViewForAddText.hideInputTextDialog();
                mCreateViewForAddText = null;
            }
            mBinding.vAddText.llAddTextStyle.setVisibility(View.VISIBLE);
            mCreateViewForAddText = new CreateViewForAddText(this, mBinding.vAddText.llAddTextStyle, new CreateViewForAddText.downCallback() {
                @Override
                public void isSuccess(String path, int type, String title) {
                    presenter.changeTextStyle(path, type, title);
                }

                @Override
                public void setText(String text) {
                    presenter.ChangeTextLabe(text);
                    if (TextUtils.isEmpty(text)) {
                        if (mCreateViewForAddText != null) {
                            mCreateViewForAddText.hideInputTextDialog();
                            mCreateViewForAddText = null;
                        }
                    }
                }

                @Override
                public void hindAddTextStickerView() {
                    for (int value : LIN_ID) {
                        ((TextView) findViewById(value)).setTextColor(getResources().getColor(R.color.white));
                    }
                    seekBarViewIsShow(true);
                    mBinding.materialSeekBarView.scrollToTheBottom();
                }

                @Override
                public void setTextColor(String color0, String color1, String title) {
                    LogUtil.d("OOM4", "color0=" + color0 + "color1=" + color1);
                    presenter.ChangeTextColor(color0, color1, title);
                }

                @Override
                public void isSuccess(String textBjPath, String textFramePath, String frameTitle) {
                    LogUtil.d("OOM4", "textBjPath=" + textBjPath + "textFramePath=" + textFramePath + "frameTitle" + frameTitle);
                    presenter.ChangeTextFrame(textBjPath, textFramePath, frameTitle);
                }

                @Override
                public void isSuccess(String color0, String color1, String textFramePath, String frameTitle) {
                    LogUtil.d("OOM4", "color0=" + color0 + "color1=" + color1 + "textFramePath" + textFramePath + "frameTitle" + frameTitle);
                    presenter.ChangeTextFrame(color0, color1, textFramePath, frameTitle);
                }
            });
            mCreateViewForAddText.showBottomSheetDialog(inputText, "bj_template");
        }
    }

    private static final int[] LIN_ID = {R.id.tv_tiezhi, R.id.tv_anim, R.id.tv_music, R.id.tv_add_text, R.id.tv_background, R.id.tv_frame};

    private void setTextColor(int chooseItem) {
        for (int value : LIN_ID) {
            ((TextView) findViewById(value)).setTextColor(ContextCompat.getColor(mContext, R.color.white));
        }
        ((TextView) findViewById(LIN_ID[chooseItem])).setTextColor(Color.parseColor("#5496FF"));
    }


    private void intoVideoCropActivity(String path) {
        Intent intent = new Intent(CreationTemplateActivity.this, VideoCropActivity.class);
        intent.putExtra("videoPath", path);
        intent.putExtra("comeFrom", FromToTemplate.ISFROMEDOWNVIDEOFORADDSTICKER);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void videoToPause() {
        LogUtil.d("OOM44", "videoToPause");
        pauseExoPlayer();
        presenter.intoOnPause();
        isPlaying = false;
        endTimer();
        presenter.showGifAnim(false);
        nowStateIsPlaying(false);
    }

    private void nowStateIsPlaying(boolean isPlaying) {
        if (isPlaying) {
            mBinding.ivPlay.setImageResource(R.mipmap.pause);
        } else {
            mBinding.ivPlay.setImageResource(R.mipmap.iv_play_creation);
        }
    }


    /**
     * description ：设置预览界面大小
     * date: ：2019/11/18 20:24
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    private void initViewLayerRelative() {
        ViewGroup.LayoutParams relativeLayoutParams = mBinding.idVviewRealtimeGllayout.getLayoutParams();
        float oriRatio;
        oriRatio = 9f / 16f;
        //保证获得mContainer大小不为0
        mBinding.idVviewRealtimeGllayout.post(() -> {
            int oriHeight = mBinding.idVviewRealtimeGllayout.getHeight();
            relativeLayoutParams.width = Math.round(1f * oriHeight * oriRatio);
            relativeLayoutParams.height = oriHeight;
            mBinding.idVviewRealtimeGllayout.setLayoutParams(relativeLayoutParams);
        });

        if (!TextUtils.isEmpty(videoPath)) {
            setBJVideoPath(false);
        }
    }

    /**
     * 设置背景视频时长
     *
     * @param isModifyMaterialTimeLine 是否修改贴纸时间轴
     */
    public void setBJVideoPath(boolean isModifyMaterialTimeLine) {
        MediaInfo mediaInfo = new MediaInfo(videoPath);
        mediaInfo.prepare();
        allVideoDuration = (long) (mediaInfo.vDuration * 1000);
        mBinding.progressBarView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mBinding.progressBarView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mCutStartTime = 0;
                mCutEndTime = allVideoDuration;
                mBinding.tvTotal.setText(TimeUtils.timeParse(mCutEndTime - mCutStartTime) + "s");
                mBinding.progressBarView.addProgressBarView(allVideoDuration, videoPath);
                if (isModifyMaterialTimeLine) {
                    LogUtil.d("OOM44", "11111");
                    musicStartTime = mCutStartTime;
                    musicEndTime = mCutEndTime;
                    mBinding.materialSeekBarView.resetStartAndEndTime(mCutStartTime, mCutEndTime);
                    mBinding.materialSeekBarView.changeVideoPathViewFrameSetWidth(allVideoDuration);
                    for (int i = 0; i < mBinding.idVviewRealtimeGllayout.getChildCount(); i++) {
                        for (int j = 0; j < mBinding.materialSeekBarView.getTemplateMaterialItemViews().size(); j++) {
                            StickerView stickerView = (StickerView) mBinding.idVviewRealtimeGllayout.getChildAt(i);
                            TemplateMaterialItemView itemView = mBinding.materialSeekBarView.getTemplateMaterialItemViews().get(j);
                            if (itemView != null) {
                                if (TextUtils.equals(String.valueOf(itemView.getIdentityID()), String.valueOf(stickerView.getStickerNoIncludeAnimId()))) {
                                    stickerView.setShowStickerStartTime(itemView.getStartTime());
                                    stickerView.setShowStickerEndTime(itemView.getEndTime());
                                }
                            }
                        }
                    }
                }
            }
        });
        mediaInfo.release();
        mBinding.materialSeekBarView.setGreenScreen(false);
    }

    boolean isInitImageBj = false;

    private void showGreenBj(boolean isInitialize) {
        mBinding.llGreenBackground.setVisibility(View.VISIBLE);
        mBinding.ivGreenBackground.setVisibility(View.VISIBLE);
        if (isInitialize) {
            if (!isInitImageBj) {
                float oriRatio = 9f / 16f;
                //保证获得mContainer大小不为0
                RelativeLayout.LayoutParams RelativeLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                //如果没有选择下载视频，那么就是自定义视频入口进来，那么默认为绿布
                mBinding.ivGreenBackground.post(() -> {
                    int oriHeight = mBinding.ivGreenBackground.getHeight();
                    RelativeLayoutParams.width = Math.round(1f * oriHeight * oriRatio);
                    RelativeLayoutParams.height = oriHeight;
                    mBinding.ivGreenBackground.setLayoutParams(RelativeLayoutParams);
                });
                isInitImageBj = true;
            }
        } else {
            setPlayerViewSize(nowUiIsLandscape);
        }
        presenter.initVideoProgressView();
        mBinding.progressBarView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mBinding.progressBarView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mCutStartTime = 0;
                mCutEndTime = allVideoDuration;
                mBinding.tvTotal.setText(TimeUtils.timeParse(mCutEndTime - mCutStartTime) + "s");
                mBinding.progressBarView.addProgressBarView(allVideoDuration, "");
            }
        });
        mBinding.materialSeekBarView.setGreenScreen(true);
    }

    /**
     * description ：设置播放器尺寸,如果不设置的话会出现黑屏，因为外面嵌套了ScrollView
     * 横竖屏切换的时候例外2层都需要修改尺寸,
     * creation date: 2020/8/10
     * user : zhangtongju
     */
    private float percentageH = 0;

    private void setPlayerViewSize(boolean isLandscape) {

        videoToPause();
        //切换横竖屏
        LinearLayout.LayoutParams relativeLayoutParams = (LinearLayout.LayoutParams) mBinding.exoPlayer.getLayoutParams();
        float oriRatio = 9f / 16f;

        if (isLandscape) {
            //横屏的情况
            mBinding.scrollView.post(() -> {
                int oriWidth = mBinding.llSpace.getWidth();
                RelativeLayout.LayoutParams relativeLayoutParams2 = (RelativeLayout.LayoutParams) mBinding.scrollView.getLayoutParams();
                relativeLayoutParams2.width = oriWidth;
                relativeLayoutParams2.height = Math.round(1f * oriWidth * oriRatio);
                mBinding.scrollView.setLayoutParams(relativeLayoutParams2);
                relativeLayoutParams.width = oriWidth;
                relativeLayoutParams.height = Math.round(1f * oriWidth / oriRatio);
                mBinding.exoPlayer.setLayoutParams(relativeLayoutParams);
                //设置预览编辑界面
                mBinding.idVviewRealtimeGllayout.setLayoutParams(relativeLayoutParams2);
            });
        } else {
            //横屏模式下切换到了竖屏
            mBinding.scrollView.post(() -> {
                RelativeLayout.LayoutParams relativeLayoutParams2 = (RelativeLayout.LayoutParams) mBinding.scrollView.getLayoutParams();

                int height = mBinding.llSpace.getHeight();
                relativeLayoutParams2.height = height;
                relativeLayoutParams2.width = Math.round(1f * height * oriRatio);
                mBinding.scrollView.setLayoutParams(relativeLayoutParams2);

                relativeLayoutParams.width = Math.round(1f * height * oriRatio);
                relativeLayoutParams.height = height;
                mBinding.exoPlayer.setLayoutParams(relativeLayoutParams);
                //设置预览编辑界面
                mBinding.idVviewRealtimeGllayout.setLayoutParams(relativeLayoutParams2);
            });
        }


        if (mBinding.llGreenBackground.getVisibility() == View.VISIBLE) {
            //可见的时候需要修稿这里
            Observable.just(isLandscape).observeOn(AndroidSchedulers.mainThread()).subscribe(aBoolean -> {
                if (isLandscape) {
                    //横屏的情况
                    mBinding.ivGreenBackground.post(() -> {
                        int oriWidth = mBinding.llSpace.getWidth();

                        RelativeLayout.LayoutParams relativeLayoutParams3 = (RelativeLayout.LayoutParams) mBinding.llGreenBackground.getLayoutParams();
                        relativeLayoutParams3.width = oriWidth;
                        relativeLayoutParams3.height = Math.round(1f * oriWidth * oriRatio);
                        mBinding.llGreenBackground.setLayoutParams(relativeLayoutParams3);

                        RelativeLayout.LayoutParams relativeLayoutParams4 = (RelativeLayout.LayoutParams) mBinding.ivGreenBackground.getLayoutParams();
                        relativeLayoutParams4.width = oriWidth;
                        relativeLayoutParams4.height = Math.round(1f * oriWidth * oriRatio);
                        mBinding.ivGreenBackground.setLayoutParams(relativeLayoutParams4);
                    });
                } else {

                    mBinding.ivGreenBackground.post(() -> {
                        int oriHeight = mBinding.llSpace.getHeight();
                        RelativeLayout.LayoutParams relativeLayoutParams3 = (RelativeLayout.LayoutParams) mBinding.llGreenBackground.getLayoutParams();
                        relativeLayoutParams3.width = Math.round(1f * oriHeight * oriRatio);
                        relativeLayoutParams3.height = oriHeight;
                        mBinding.llGreenBackground.setLayoutParams(relativeLayoutParams3);
                        RelativeLayout.LayoutParams relativeLayoutParams4 = (RelativeLayout.LayoutParams) mBinding.ivGreenBackground.getLayoutParams();
                        relativeLayoutParams4.width = Math.round(1f * oriHeight * oriRatio);
                        relativeLayoutParams4.height = oriHeight;
                        mBinding.ivGreenBackground.setLayoutParams(relativeLayoutParams4);
                    });
                }
            });
        }

        mBinding.scrollView.setOnScrollListener(scrollY -> {
            int totalHeight = mBinding.scrollView.getChildAt(0).getHeight();
            percentageH = scrollY / (float) totalHeight;
            LogUtil.d("OOM3", "percentageH" + percentageH);
        });

        new Handler().postDelayed(() -> {
            presenter.setAllStickerCenter();
            if (isLandscape) {
                int height = Math.round(1f * mBinding.llSpace.getWidth() / oriRatio);
                mBinding.scrollView.scrollTo(0, height / 2 - mBinding.scrollView.getHeight() / 2);
            }
        }, 500);
    }


    private void setImageBackSize(boolean isLandscape) {

        videoToPause();
        //切换横竖屏
        ViewGroup.LayoutParams relativeLayoutParams = mBinding.ivBackImage.getLayoutParams();
        float oriRatio = 9f / 16f;
        float frameRatio = 1f;

        if (isLandscape) {
            //横屏的情况
            mBinding.svBackImage.post(() -> {
                int oriWidth = mBinding.llSpace.getWidth();
                int spaceHeight = mBinding.llSpace.getHeight();

                RelativeLayout.LayoutParams relativeLayoutParams2 = (RelativeLayout.LayoutParams) mBinding.svBackImage.getLayoutParams();
                relativeLayoutParams2.width = oriWidth;
                relativeLayoutParams2.height = Math.round(1f * oriWidth * oriRatio);
                mBinding.svBackImage.setLayoutParams(relativeLayoutParams2);

                relativeLayoutParams.width = oriWidth;
                relativeLayoutParams.height = Math.round(1f * oriWidth / oriRatio);
                mBinding.ivBackImage.setLayoutParams(relativeLayoutParams);

                //设置预览编辑界面
                mBinding.idVviewRealtimeGllayout.setLayoutParams(relativeLayoutParams2);

//                if (mBinding.ivFrameImage.getVisibility() == View.VISIBLE) {
//                    ViewGroup.LayoutParams frameLayoutParams = mBinding.ivFrameImage.getLayoutParams();
//                    frameLayoutParams.height = spaceHeight;
//                    frameLayoutParams.width = spaceHeight;
//                    mBinding.ivFrameImage.setLayoutParams(frameLayoutParams);
//                }
            });


        } else {
            //横屏模式下切换到了竖屏
            mBinding.svBackImage.post(() -> {
                RelativeLayout.LayoutParams relativeLayoutParams2 = (RelativeLayout.LayoutParams) mBinding.svBackImage.getLayoutParams();

                int height = mBinding.llSpace.getHeight();
                int width = mBinding.llSpace.getWidth();


                if (mBinding.ivFrameImage.getVisibility() == View.VISIBLE) {
                    relativeLayoutParams2.height = Math.round(1f * height / oriRatio);//height;
                    relativeLayoutParams2.width = height;//Math.round(1f * height * oriRatio);
                    mBinding.svBackImage.setLayoutParams(relativeLayoutParams2);

                    relativeLayoutParams.width = height;//Math.round(1f * height * oriRatio)
                    relativeLayoutParams.height = Math.round(1f * height / oriRatio);// height
                    mBinding.ivBackImage.setLayoutParams(relativeLayoutParams);
                    //设置预览编辑界面
                    mBinding.idVviewRealtimeGllayout.setLayoutParams(relativeLayoutParams2);
                    RelativeLayout.LayoutParams frameLayoutParams = (RelativeLayout.LayoutParams) mBinding.ivFrameImage.getLayoutParams();
                    frameLayoutParams.height = height;
                    frameLayoutParams.width = height;

                    mBinding.ivFrameImage.setLayoutParams(frameLayoutParams);
                } else {
                    relativeLayoutParams2.height = height;
                    relativeLayoutParams2.width = Math.round(1f * height * oriRatio);
                    mBinding.svBackImage.setLayoutParams(relativeLayoutParams2);

                    relativeLayoutParams.width = Math.round(1f * height * oriRatio);
                    relativeLayoutParams.height = height;
                    mBinding.ivBackImage.setLayoutParams(relativeLayoutParams);
                    //设置预览编辑界面
                    mBinding.idVviewRealtimeGllayout.setLayoutParams(relativeLayoutParams2);
                }
            });
        }


        if (mBinding.llGreenBackground.getVisibility() == View.VISIBLE) {
            //可见的时候需要修稿这里
            Observable.just(isLandscape).observeOn(AndroidSchedulers.mainThread()).subscribe(aBoolean -> {
                if (isLandscape) {
                    //横屏的情况
                    mBinding.ivGreenBackground.post(() -> {
                        int oriWidth = mBinding.llSpace.getWidth();

                        RelativeLayout.LayoutParams relativeLayoutParams3 = (RelativeLayout.LayoutParams) mBinding.llGreenBackground.getLayoutParams();
                        relativeLayoutParams3.width = oriWidth;
                        relativeLayoutParams3.height = Math.round(1f * oriWidth * oriRatio);
                        mBinding.llGreenBackground.setLayoutParams(relativeLayoutParams3);

                        RelativeLayout.LayoutParams relativeLayoutParams4 = (RelativeLayout.LayoutParams) mBinding.ivGreenBackground.getLayoutParams();
                        relativeLayoutParams4.width = oriWidth;
                        relativeLayoutParams4.height = Math.round(1f * oriWidth * oriRatio);
                        mBinding.ivGreenBackground.setLayoutParams(relativeLayoutParams4);
                    });
                } else {
                    mBinding.ivGreenBackground.post(() -> {
                        int oriHeight = mBinding.llSpace.getHeight();

                        RelativeLayout.LayoutParams relativeLayoutParams3 = (RelativeLayout.LayoutParams) mBinding.llGreenBackground.getLayoutParams();
                        relativeLayoutParams3.width = Math.round(1f * oriHeight * oriRatio);
                        relativeLayoutParams3.height = oriHeight;
                        mBinding.llGreenBackground.setLayoutParams(relativeLayoutParams3);

                        RelativeLayout.LayoutParams relativeLayoutParams4 = (RelativeLayout.LayoutParams) mBinding.ivGreenBackground.getLayoutParams();
                        relativeLayoutParams4.width = Math.round(1f * oriHeight * oriRatio);
                        relativeLayoutParams4.height = oriHeight;

                        mBinding.ivGreenBackground.setLayoutParams(relativeLayoutParams4);
                    });
                }
            });
        }

        mBinding.svBackImage.setOnScrollListener(scrollY -> {
            int totalHeight = mBinding.svBackImage.getChildAt(0).getHeight();
            int svBackImageHeight = mBinding.svBackImage.getHeight();
            percentageH = scrollY / (float) (totalHeight - svBackImageHeight);
            LogUtil.d(TAG, "svBackImageHeight" + svBackImageHeight);
            LogUtil.d(TAG, "percentageH" + percentageH);
            LogUtil.d(TAG, "totalHeight = " + totalHeight);
            LogUtil.d(TAG, "scrollY = " + scrollY);
        });

        new Handler().postDelayed(() -> {
            presenter.setAllStickerCenter();
            if (isLandscape) {
                int height = Math.round(1f * mBinding.llSpace.getWidth() / oriRatio);
                mBinding.svBackImage.scrollTo(0, height / 2 - mBinding.svBackImage.getHeight() / 2);
            }
        }, 500);
    }

    @Override
    protected void onPause() {
        videoToPause();//onPause
        isIntoPause = true;
        presenter.intoOnPause();
        super.onPause();
    }


    @Override
    public void itemClickForStickView(AnimStickerModel stickViewModel) {
        mBinding.idVviewRealtimeGllayout.addView(stickViewModel.getStickerView());
    }

    @Override
    public void hasPlayingComplete() {
    }

    @Override
    public void chooseMusicIndex(int index) {
        musicChooseIndex = index;
        LogUtil.d("OOM5", "musicChooseIndex=" + musicChooseIndex);
        if (index == 0) {
            //选中的是素材音乐
            LogUtil.d("OOM44", "555555");
            musicStartTime = musicStartFirstTime;
            LogUtil.d("OOM5", "musicEndFirstTime=" + musicEndFirstTime);
            if (musicEndFirstTime == 0) {
                musicEndTime = getFristVideoDuration();
                if (musicEndTime == 0) {
                    musicEndTime = allVideoDuration;
                    musicEndFirstTime = musicEndTime;
                }
            } else {
                musicEndTime = musicEndFirstTime;
            }
        } else {
            musicStartTime = 0;
            musicEndTime = allVideoDuration;
        }
    }


    private long getFristVideoDuration() {
        for (int i = 0; i < mBinding.idVviewRealtimeGllayout.getChildCount(); i++) {
            StickerView stickerView = (StickerView) mBinding.idVviewRealtimeGllayout.getChildAt(i);
            if (stickerView.getStickerNoIncludeAnimId() == 0) {
                String path = stickerView.getOriginalPath();
                VideoInfo videoInfo = VideoManage.getInstance().getVideoInfo(this, path);
                return videoInfo.getDuration();
            }
        }
        return 0;

    }

    @Override
    public void deleteFirstSticker() {
        new Handler().postDelayed(() -> {
            mBinding.viewPager.setCurrentItem(0);
            mBinding.tvMusic.setVisibility(View.GONE);
            setTextColor(0);
        }, 500);
    }


    /**
     * description ：贴纸点击事件，str 为如果为文字，为文字内容
     * creation date: 2020/10/15
     * user : zhangtongju
     */
    @Override
    public void stickerOnclickCallback(String str) {
        if (!TextUtils.isEmpty(str) && mCreateViewForAddText != null) {
            if (!"输入文本".equals(str)) {
                mCreateViewForAddText.setInputText(str);
            }
        }
    }

    @Override
    public void showTextDialog(String inputText) {
        intoTextStyleDialog(inputText);
    }


    @Override
    public void hideTextDialog() {
        if (mCreateViewForAddText != null) {
            mCreateViewForAddText.hideInputTextDialog();
        }
    }

    private void setgsyVideoProgress(long progress) {
        LogUtil.d("OOM", "videoProgress=" + progress);
        if (!isPlaying) {
            seekToVideo(progress);
            seekToMusic(progress);
        }
    }

    private void seekToVideo(long to) {
        if (exoPlayer != null) {
            exoPlayer.seekTo(to);
        }
    }

    private void seekToMusic(long to) {
        if (bgmPlayer != null) {
            bgmPlayer.seekTo((int) to);
        }
    }

    @Override
    public void getVideoDuration(long allVideoDuration) {
        this.allVideoDuration = allVideoDuration;
        if (musicEndFirstTime == 0) {
            musicEndTime = allVideoDuration;
            musicEndFirstTime = musicEndTime;
        }
        Log.d("OOM44", "allVideoDuration=" + allVideoDuration);
    }

    @Override
    public void needPauseVideo() {
        if (isPlaying) {
            isIntoPause = false;
            isPlayComplate = false;
            LogUtil.d("OOM44", "needPauseVideo");
            videoToPause();//interface needPauseVideo
            presenter.showGifAnim(false);
            isPlaying = false;
            nowStateIsPlaying(false);
        }
    }

    @Override
    public void getVideoCover(String path, String originalPath) {
        presenter.addNewSticker(path, originalPath);
        if (TextUtils.isEmpty(videoPath)) {
            //如果还是绿屏。那么需要刷新底部的时长
            Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer ->
                    presenter.initVideoProgressView());
        }
    }


    @Override
    public void getBgmPath(String path) {
        this.bgmPath = path;
        if (TextUtils.isEmpty(bgmPath)) {
            if (isPlaying) {
                videoToPause();
            }
        } else {
            LogUtil.d("OOM", "getBgmPath=" + path);
            if (isPlaying) {
                if (!TextUtils.isEmpty(path)) {
                    if (exoPlayer != null) {
                        exoPlayer.setVolume(0f);
                    }
                    pauseBgmMusic();
                    LogUtil.d("playBGMMusic", "getBgmPath");
                    playBGMMusic();
                    if (bgmPlayer != null) {
                        if (exoPlayer != null) {
                            bgmPlayer.seekTo((int) getCurrentPos());
                        } else {
                            bgmPlayer.seekTo((int) totalPlayTime);
                        }
                    }
                } else {
                    if (exoPlayer != null) {
                        exoPlayer.setVolume(1f);
                    }
                    pauseBgmMusic();
                }
            } else {
                videoToStart();
            }
        }

    }

    @Override
    public void hideKeyBord() {
        if (mCreateViewForAddText != null) {
            mCreateViewForAddText.hideInputTextDialog();
        }
    }

    @Override
    public void changFirstVideoSticker(String path) {
        if (TextUtils.isEmpty(videoPath) || musicChooseIndex == 0) {
            if (TextUtils.isEmpty(videoPath)) {
                //如果还是绿屏。那么需要刷新底部的时长
                Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> presenter.initVideoProgressView());
            }
            //需要重新刷新素材音乐开始时间,结束时间还是上次的
            musicStartTime = 0;
            musicStartFirstTime = 0;
            LogUtil.d("OOM44", "绿幕的情况下换了视频后结束音乐的时间为" + musicEndTime);
//            musicEndTime = getFristVideoDuration();
            musicEndFirstTime = musicEndTime;
            LogUtil.d("OOM44", "绿幕的情况下换了视频后结束音乐的时间musicEndFirstTime为" + musicEndFirstTime);
        }
    }


    /**
     * 第一次添加贴纸后修改切换按钮状态栏
     */
    @Override
    public void isFirstAddSuccess() {
        new Handler().postDelayed(() -> {
            if (!isNeedCut) {
                mBinding.switchButton.setChecked(false);
            }
        }, 1500);
    }

    @Override
    public void showCreateTemplateAnim(boolean isShow) {
        if (isShow) {
            mBinding.idVviewRealtimeGllayout.setVisibility(View.GONE);
        } else {
            mBinding.idVviewRealtimeGllayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showMusicBtn(boolean isShow) {
        if (isShow) {
            if (mFrom != FROM_DRESS_UP_BACK_CODE) {
                mBinding.tvMusic.setVisibility(View.VISIBLE);
            }
        } else {
            mBinding.tvMusic.setVisibility(View.GONE);
            mBinding.viewPager.setCurrentItem(0);
        }
        for (int value : LIN_ID) {
            ((TextView) findViewById(value)).setTextColor(ContextCompat.getColor(mContext, R.color.white));
        }
    }


    /**
     * description ：动画初始化完成，接下来就开始预览
     * creation date: 2020/6/4
     * user : zhangtongju
     */
    @Override
    public void animIsComplate() {
        LogUtil.d("OOM", "animIsComplate");
        WaitingDialog.closeProgressDialog();

        Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
            nowStateIsPlaying(true);
            if (!TextUtils.isEmpty(videoPath)) {
                //只要不是素材音乐
                LogUtil.d("playBGMMusic", "IsComplatePlay");
                seekToVideo(mCutStartTime);
                seekToMusic(mCutStartTime);
                if (isPlayComplate) {
                    videoPlay();
                    isIntoPause = false;
                } else {
                    if (isInitVideoLayer) {
                        if (!isIntoPause) {
                            videoPlay();
                        } else {
                            videoPlay();
                            isIntoPause = false;
                            isInitVideoLayer = true;
                        }
                    } else {
                        isIntoPause = false;
                        isInitVideoLayer = true;
                        videoPlay();
                    }
                }
            } else {
                //如果有背景还是播放背景音乐,如果有背景音乐且是第一次初始化
                if (!TextUtils.isEmpty(bgmPath) && musicEndTime == 0) {
                    if (bgmPlayer != null) {
                        //继续播放
                        bgmPlayer.start();
                        LogUtil.d("playBGMMusic", " bgmPlayer.start()");
                    } else {

                        seekToVideo(mCutStartTime);
                        seekToMusic(mCutStartTime);
                        LogUtil.d("playBGMMusic", "animIsComplate");
                        playBGMMusic();
                    }
                }
            }
            isPlaying = true;
            startTimer();
            presenter.showGifAnim(true);
        });

    }

    private Timer timer;
    private TimerTask task;
    private long nowTime = 5;
    //自己计算的播放时间
    private long totalPlayTime;
    private boolean isNeedPlayBjMusci = false;

    private void startTimer() {

        if (musicChooseIndex == 2) {
            musicStartTime = 0;
            musicEndTime = allVideoDuration;
        }

        isEndDestroy = false;
        LogUtil.d("OOM44", "startTimer:musicEndTime=" + musicEndTime + "musicStartTime=" + musicStartTime + "musicChooseIndex=" + musicChooseIndex);
        nowTime = 5;
        totalPlayTime = mCutStartTime;
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        timer = new Timer();

        task = new TimerTask() {
            @Override
            public void run() {
                totalPlayTime = totalPlayTime + 5;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(videoPath)) {
                            bjMusicControl();
                            if (isPlaying) {
                                long nowCurrentPos = getCurrentPos();
                                if (nowCurrentPos < 0) {
                                    nowCurrentPos = 0;
                                }
                                if (nowCurrentPos >= mCutEndTime) {
                                    exoPlayer.seekTo(mCutStartTime);
                                    videoToPause();
                                } else if (nowCurrentPos < mCutStartTime) {
                                    exoPlayer.seekTo(mCutStartTime);
                                    videoToPause();
                                }
                            }
                            if (mBinding != null) {
                                mBinding.progressBarView.scrollToPosition(getCurrentPos());
                            }
                        } else {
                            bjMusicControl();
                            //没有选择背景
                            nowTime = nowTime + 5;
                            LogUtil.d("OOM44", "nowTime==" + nowTime + "mCutEndTime=" + mCutEndTime);
                            if (nowTime >= mCutEndTime) {
                                nowTime = mCutStartTime;
                                isPlayComplate = true;
                                endTimer();
                                isPlaying = false;
                                presenter.showGifAnim(false);
                                nowStateIsPlaying(false);
                                presenter.showAllAnim(false);
                            } else if (nowTime < mCutStartTime) {
                                nowTime = mCutStartTime;
                            }
                            if (mBinding.progressBarView != null) {
                                mBinding.progressBarView.scrollToPosition(nowTime);
                            }
                        }
                    }
                });
            }
        };
        timer.schedule(task, 0, 5);
    }


    /**
     * description ：拖动后时候可以播放音乐
     * creation date: 2020/11/19
     * user : zhangtongju
     */

    private void bjMusicControl() {
        if (!isEndDestroy) {
            LogUtil.d("playBGMMusic", "bjMusicControl");
            if (!TextUtils.isEmpty(bgmPath)) {
                if (musicEndTime != 0) {
                    float needMusicStartTime = musicStartTime;
                    float needTime = totalPlayTime;
                    LogUtil.d("playBGMMusic", "totalPlayTime=" + totalPlayTime + "mCutStartTime=" + mCutStartTime + "needTime=" + needTime + "musicStartTime=" + musicStartTime + "needMusicStartTime=" + needMusicStartTime);
                    if (needTime > musicEndTime || needTime < needMusicStartTime) {
                        LogUtil.d("playBGMMusic2", "需要暂停音乐");
                        isNeedPlayBjMusci = false;
                        pauseBgmMusic();
                    } else {
                        if (!isNeedPlayBjMusci) {
                            LogUtil.d("playBGMMusic2", "播放音乐");
                            LogUtil.d("playBGMMusic2", "totalPlayTime=" + totalPlayTime + "mCutStartTime=" + mCutStartTime + "needTime=" + needTime + "musicStartTime=" + musicStartTime + "needMusicStartTime=" + needMusicStartTime);
                            playBjMusic();
                        }
                        isNeedPlayBjMusci = true;
                    }
                } else {
                    LogUtil.d("playBGMMusic", "musicEndTime=" + musicEndTime);
                    if (!isNeedPlayBjMusci) {
                        LogUtil.d("playBGMMusic", "播放音乐");
                        playBjMusic();
                    }
                    isNeedPlayBjMusci = true;
                }
            } else {
                LogUtil.d("playBGMMusic", "bgmPath==null");
            }
        }

    }


    private void playBjMusic() {
        LogUtil.d("playBGMMusic", "playBjMusic");
        if (!TextUtils.isEmpty(bgmPath)) {
            if (bgmPlayer != null) {
                bgmPlayer.start();
                seekToMusic(mCutStartTime);
            } else {
                seekToMusic(mCutStartTime);
                playBGMMusic();
            }
        }
    }


    /**
     * 获取当前进度
     */
    private long getCurrentPos() {
        return exoPlayer != null ? exoPlayer.getCurrentPosition() : 0;
    }

    /**
     * 关闭timer 和task
     */
    private void endTimer() {
        isNeedPlayBjMusci = false;
        LogUtil.d("playBGMMusic", "pauseBgmMusic---------------endTimer---------------");
        isEndDestroy = true;
        destroyTimer();
//        presenter.isEndTimer();
        if (bgmPlayer != null) {
            bgmPlayer.stop();
            bgmPlayer = null;
        }
    }

    /**
     * user :TongJu  ; email:jutongzhang@sina.com
     * time：2018/10/15
     * describe:严防内存泄露
     **/
    private void destroyTimer() {
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
    }


    /**
     * description ：重新选择了背景视频的回调
     * creation date: 2020/4/13
     * user : zhangtongju
     */
    @Subscribe
    public void onEventMainThread(DownVideoPath event) {
//        videoStop();
        Observable.just(event.getPath()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(s -> {
            if (albumType.isImage(GetPathTypeModel.getInstance().getMediaType(event.getPath()))) {
                mBinding.llGreenBackground.setVisibility(View.VISIBLE);
                mBinding.scrollView.setVisibility(View.GONE);
                presenter.setmVideoPath("");
                videoPath = "";
                showGreenBj(false);
                imageBjPath = event.getPath();
                //图片背景和绿幕背景默认都是10秒
                //循环得到最长的视频时长
                long maxVideoDuration = 0;
                for (int i = 0; i < mBinding.materialSeekBarView.getTemplateMaterialItemViews().size(); i++) {
                    if (mBinding.materialSeekBarView.getTemplateMaterialItemViews().get(i) != null) {
                        if (mBinding.materialSeekBarView.getTemplateMaterialItemViews().get(i).getDuration() >= maxVideoDuration &&
                                albumType.isVideo(GetPathType.getInstance().getPathType(mBinding.materialSeekBarView.getTemplateMaterialItemViews().get(i).resPath))) {
                            maxVideoDuration = mBinding.materialSeekBarView.getTemplateMaterialItemViews().get(i).getDuration();
                        }
                    }
                }
                if (maxVideoDuration > 0) {
                    modificationDuration(maxVideoDuration);
                } else {
                    modificationDuration(10 * 1000);
                }
                musicStartFirstTime = 0;
                LogUtil.d("OOM44", "22222");
                musicStartTime = 0;
                musicEndFirstTime = mCutEndTime;
                musicEndTime = mCutEndTime;

                new Handler().postDelayed(() ->
                        Glide.with(CreationTemplateActivity.this)
                                .load(s).into(mBinding.ivGreenBackground), 500);
            } else {
                LogUtil.d("OOM", "重新选择了视频背景,地址为" + event.getPath());
                videoPath = event.getPath();
                mBinding.llGreenBackground.setVisibility(View.GONE);
                mBinding.scrollView.setVisibility(View.VISIBLE);
                setPlayerViewSize(nowUiIsLandscape);
                initExo(videoPath);
                presenter.setmVideoPath(videoPath);
                presenter.initVideoProgressView();
                setBJVideoPath(true);
                musicStartFirstTime = 0;
                LogUtil.d("OOM44", "333333");
                musicStartTime = 0;
                musicEndFirstTime = mCutEndTime;
                musicEndTime = mCutEndTime;
            }
        });
    }


    /**
     * description ：选择视频后新增的贴纸
     * creation date: 2020/4/13
     * user : zhangtongju
     */
    @Subscribe
    public void onEventMainThread(ChooseVideoAddSticker event) {
        presenter.GetVideoCover(event.getPath());
    }


    /**
     * 释放资源
     */
    private void videoStop() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
        }

    }


    private void playBGMMusic() {
        bgmPlayer = new MediaPlayer();
        try {
            bgmPlayer.setDataSource(bgmPath);
            bgmPlayer.prepare();
            bgmPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void pauseBgmMusic() {
        LogUtil.d("playBGMMusic", "pauseBgmMusic------------------------------");
        if (bgmPlayer != null && bgmPlayer.isPlaying()) {
            bgmPlayer.pause();
        }
    }


    @Subscribe
    public void onEventMainThread(CutSuccess cutSuccess) {
        String nowChooseBjPath = cutSuccess.getFilePath();
        presenter.setAddChooseBjPath(nowChooseBjPath);
        musicChooseIndex = 2;
    }


    @Override
    public final boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mBinding.vAddText.llAddTextStyle.getVisibility() == View.VISIBLE) {
                if (mCreateViewForAddText != null) {
                    mCreateViewForAddText.hideInput();
                }
                mBinding.vAddText.llAddTextStyle.setVisibility(View.GONE);
            } else {
                onBackPressed();
            }
        }
        return true;
    }

    @Override
    public void addStickerTimeLine(String id, boolean isText, String text, StickerView stickerView) {
        mBinding.materialSeekBarView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mBinding.materialSeekBarView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                stickerView.setShowStickerStartTime(mCutStartTime);
                stickerView.setShowStickerEndTime(mCutEndTime);
                //是绿幕视频并且添加的素材是视频 遍历出所有素材的最长时长  为当前主轨道的时长
                if (albumType.isVideo(GetPathType.getInstance().getPathType(stickerView.getOriginalPath())) && TextUtils.isEmpty(videoPath)) {
                    MediaInfo mediaInfo = new MediaInfo(stickerView.getOriginalPath());
                    mediaInfo.prepare();
                    long videoDuration = (long) (mediaInfo.vDuration * 1000);
                    mediaInfo.release();
                    boolean modify = false;
                    //循环得到最长的视频时长
                    long maxVideoDuration = 0;
                    for (int i = 0; i < mBinding.materialSeekBarView.getTemplateMaterialItemViews().size(); i++) {
                        if (mBinding.materialSeekBarView.getTemplateMaterialItemViews().get(i) != null) {
                            if (mBinding.materialSeekBarView.getTemplateMaterialItemViews().get(i).getDuration() >= maxVideoDuration &&
                                    albumType.isVideo(GetPathType.getInstance().getPathType(mBinding.materialSeekBarView.getTemplateMaterialItemViews().get(i).resPath))) {
                                maxVideoDuration = mBinding.materialSeekBarView.getTemplateMaterialItemViews().get(i).getDuration();
                            }
                        }
                    }
                    if (videoDuration > maxVideoDuration) {
                        modify = true;
                        oldMaxVideoDuration = videoDuration;
                    }
                    if (modify) {
                        modificationDuration(videoDuration);
                        mBinding.materialSeekBarView.addTemplateMaterialItemView(videoDuration, TextUtils.isEmpty(stickerView.getOriginalPath()) ?
                                stickerView.getResPath() : stickerView.getOriginalPath(), mCutStartTime, mCutEndTime, isText, text, id);
                    } else if (videoDuration >= mCutEndTime) {
                        mBinding.materialSeekBarView.addTemplateMaterialItemView(maxVideoDuration, TextUtils.isEmpty(stickerView.getOriginalPath()) ?
                                stickerView.getResPath() : stickerView.getOriginalPath(), mCutStartTime, mCutEndTime, isText, text, id);
                        stickerView.setShowStickerEndTime(mCutEndTime);
                        mBinding.materialSeekBarView.setCutStartTime(mCutStartTime);
                        mBinding.materialSeekBarView.setCutEndTime(mCutEndTime);
                    } else {
                        mBinding.materialSeekBarView.addTemplateMaterialItemView(maxVideoDuration, TextUtils.isEmpty(stickerView.getOriginalPath()) ?
                                stickerView.getResPath() : stickerView.getOriginalPath(), mCutStartTime, videoDuration, isText, text, id);
                        stickerView.setShowStickerEndTime(videoDuration);
                        mBinding.materialSeekBarView.setCutStartTime(mCutStartTime);
                        mBinding.materialSeekBarView.setCutEndTime(maxVideoDuration);
                        oldMaxVideoDuration = maxVideoDuration;
                    }
                } else if (albumType.isVideo(GetPathType.getInstance().getPathType(stickerView.getOriginalPath())) && !TextUtils.isEmpty(videoPath)) {
                    MediaInfo mainMediaInfo = new MediaInfo(videoPath);
                    mainMediaInfo.prepare();
                    long videoDuration = (long) (mainMediaInfo.vDuration * 1000);
                    mainMediaInfo.release();
                    MediaInfo materialMediaInfo = new MediaInfo(stickerView.getOriginalPath());
                    materialMediaInfo.prepare();
                    long materialDuration = (long) (materialMediaInfo.vDuration * 1000);
                    materialMediaInfo.release();

                    if (mCutEndTime > materialDuration) {
                        mBinding.materialSeekBarView.addTemplateMaterialItemView(videoDuration, TextUtils.isEmpty(stickerView.getOriginalPath()) ?
                                stickerView.getResPath() : stickerView.getOriginalPath(), mCutStartTime, materialDuration, isText, text, id);
                        stickerView.setShowStickerEndTime(materialDuration);
                    } else if (materialDuration > mCutEndTime) {
                        mBinding.materialSeekBarView.addTemplateMaterialItemView(videoDuration, TextUtils.isEmpty(stickerView.getOriginalPath()) ?
                                stickerView.getResPath() : stickerView.getOriginalPath(), mCutStartTime, mCutEndTime, isText, text, id);
                        stickerView.setShowStickerEndTime(mCutEndTime);
                    } else {
                        mBinding.materialSeekBarView.addTemplateMaterialItemView(videoDuration, TextUtils.isEmpty(stickerView.getOriginalPath()) ?
                                stickerView.getResPath() : stickerView.getOriginalPath(), mCutStartTime, mCutEndTime, isText, text, id);
                    }
                    mBinding.materialSeekBarView.setCutStartTime(mCutStartTime);
                    mBinding.materialSeekBarView.setCutEndTime(mCutEndTime);
                } else {
                    //如果素材全是图片的话默认为10秒   如果素材有视频的话以最长素材视频的时长为主轨道的时长
                    long materialDuration;
                    long maxVideoDuration = 0;
                    boolean isMaxVideoDurationChange = false;
                    //裁剪了主轨道后选择图片贴纸后 要重新设置素材过来的拖动结束区域
                    boolean isAfreshSetEndCutEnd = false;
                    if (TextUtils.isEmpty(videoPath)) {
                        materialDuration = 10 * 1000;
                        for (int i = 0; i < mBinding.materialSeekBarView.getTemplateMaterialItemViews().size(); i++) {
                            if (mBinding.materialSeekBarView.getTemplateMaterialItemViews().get(i) != null) {
                                if (albumType.isVideo(GetPathType.getInstance().getPathType(mBinding.materialSeekBarView.getTemplateMaterialItemViews().get(i).resPath))) {
                                    for (int j = 0; j < mBinding.materialSeekBarView.getTemplateMaterialItemViews().size(); j++) {
                                        if (mBinding.materialSeekBarView.getTemplateMaterialItemViews().get(j) != null &&
                                                mBinding.materialSeekBarView.getTemplateMaterialItemViews().get(j).getDuration() >= maxVideoDuration &&
                                                albumType.isVideo(GetPathType.getInstance().getPathType(mBinding.materialSeekBarView.getTemplateMaterialItemViews().get(j).resPath))
                                        ) {
                                            maxVideoDuration = mBinding.materialSeekBarView.getTemplateMaterialItemViews().get(j).getDuration();
                                            maxVideoResPath = mBinding.materialSeekBarView.getTemplateMaterialItemViews().get(j).resPath;
                                        }
                                    }
                                }
                            }
                        }
                        //有视频素材  取最长的素材视频时长为主轨道的时长  走此逻辑判断
                        if (maxVideoDuration > 0) {
                            materialDuration = maxVideoDuration;
                            isMaxVideoDurationChange = true;
                        } else {
                            //全是图片素材  主轨道时长为10秒 走此逻辑判断
                            if (allVideoDuration != materialDuration) {
                                modificationDuration(materialDuration);
                            }
                            stickerView.setShowStickerEndTime(materialDuration);
                        }
                    } else {
                        materialDuration = allVideoDuration;
                        isAfreshSetEndCutEnd = true;
                    }
                    mBinding.materialSeekBarView.addTemplateMaterialItemView(materialDuration, TextUtils.isEmpty(stickerView.getOriginalPath()) ?
                            stickerView.getResPath() : stickerView.getOriginalPath(), mCutStartTime, mCutEndTime, isText, text, id);
                    if (isMaxVideoDurationChange) {
                        mBinding.materialSeekBarView.setCutStartTime(mCutStartTime);
                        allVideoDuration = materialDuration;
                        if (oldMaxVideoDuration != materialDuration) {
                            if (TextUtils.isEmpty(oldMaxVideoResPath) || !TextUtils.equals(maxVideoResPath, oldMaxVideoResPath)) {
                                mBinding.progressBarView.addProgressBarView(allVideoDuration, "");
                                oldMaxVideoResPath = maxVideoResPath;
                                if (materialDuration > mCutEndTime) {
                                    mCutEndTime = materialDuration;
                                }
                            }
                            mBinding.tvTotal.setText(TimeUtils.timeParse(mCutEndTime - mCutStartTime) + "s");

                        }
                        oldMaxVideoDuration = materialDuration;
                        mBinding.materialSeekBarView.setCutEndTime(mCutEndTime);
                        stickerView.setShowStickerEndTime(mCutEndTime);
                    }
                    if (isAfreshSetEndCutEnd) {
                        mBinding.materialSeekBarView.setCutStartTime(mCutStartTime);
                        mBinding.materialSeekBarView.setCutEndTime(mCutEndTime);
                    }
                }
                if (!TextUtils.isEmpty(id)) {
                    showTimeLineSickerArrow(id);
                }

            }
        });
    }

    long oldMaxVideoDuration = 0;
    String maxVideoResPath = "";
    String oldMaxVideoResPath = "";

    @Override
    public void updateTimeLineSickerText(String text, String id) {
        mBinding.materialSeekBarView.updateStickerViewText(text, id);
    }

    @Override
    public void deleteTimeLineSicker(String id) {
        mBinding.materialSeekBarView.deleteTemplateMaterialItemView(id);
    }

    @Override
    public void showTimeLineSickerArrow(String id) {
        if (!DoubleClick.getInstance().isFastZDYDoubleClick(500)) {
            mBinding.materialSeekBarView.isCurrentMaterialShowArrow(id);
        }
    }

    @Override
    public void modifyTimeLineSickerPath(String id, String path, StickerView stickerView) {
        if (albumType.isVideo(GetPathType.getInstance().getPathType(path)) && TextUtils.isEmpty(videoPath)) {
            //重新设置进度条的长度
            MediaInfo mediaInfo = new MediaInfo(path);
            mediaInfo.prepare();
            long videoDuration = (long) (mediaInfo.vDuration * 1000);
            mediaInfo.release();
            boolean modify = false;
            int viewCount = 0;
            for (int i = 0; i < mBinding.materialSeekBarView.getTemplateMaterialItemViews().size(); i++) {
                if (mBinding.materialSeekBarView.getTemplateMaterialItemViews().get(i) != null) {
                    viewCount++;
                    if (videoDuration > mBinding.materialSeekBarView.getTemplateMaterialItemViews().get(i).getDuration()) {
                        modify = true;
                    }
                }
            }
            if (modify || viewCount == 1) {
                modificationDuration(videoDuration);
            }
            mBinding.materialSeekBarView.modifyMaterialThumbnail(path, id, true);
        } else if (!TextUtils.isEmpty(videoPath)) {
            //背景模板
            mBinding.materialSeekBarView.modifyMaterialThumbnail(path, id, false);
            if (TextUtils.equals("0", id) && albumType.isVideo(GetPathType.getInstance().getPathType(path))) {
                MediaInfo mediaInfo = new MediaInfo(path);
                mediaInfo.prepare();
                stickerView.setShowStickerStartTime(0);
                long minDuration = Math.min((long) (mediaInfo.vDuration * 1000), mCutEndTime);
                mediaInfo.release();
                stickerView.setShowStickerEndTime(minDuration);
                musicStartTime = 0;
                musicStartFirstTime = 0;
                if (musicChooseIndex == 0) {
                    //素材音乐
                    musicEndFirstTime = minDuration;
                    musicEndTime = minDuration;
                } else {
                    //背景音乐
                    musicEndFirstTime = mCutEndTime;
                    musicEndTime = mCutEndTime;
                }
            }
            mBinding.materialSeekBarView.setCutStartTime(mCutStartTime);
            mBinding.materialSeekBarView.setCutEndTime(mCutEndTime);
        } else {
            //背景为图片或者绿幕替换素材时修改时间轴的缩略图
            mBinding.materialSeekBarView.modifyMaterialThumbnail(path, id, true);
        }

    }

    @Override
    public void stickerFragmentClose() {
        for (int value : LIN_ID) {
            ((TextView) findViewById(value)).setTextColor(ContextCompat.getColor(mContext, R.color.white));
        }
        if (isClickAddTextTag && mCreateViewForAddText != null) {
            mCreateViewForAddText.iv_down.performClick();
        } else {
            seekBarViewIsShow(true);
        }
        mBinding.materialSeekBarView.scrollToTheBottom();
    }

    @Override
    public void showLoadingDialog() {
        mLoadingDialog.show();
    }

    @Override
    public void dismissLoadingDialog() {
        mLoadingDialog.dismiss();
    }

    @Override
    public void setDialogProgress(String title, int dialogProgress, String content) {
        mLoadingDialog.setTitleStr(title);
        mLoadingDialog.setProgress(dialogProgress);
        mLoadingDialog.setContentStr(content);
    }

    @Override
    public void chooseBack(String path) {
        mBackgroundImage = path;
        changeImageBack();

    }

    private void changeImageBack() {
        setImageBackSize(false);
        if (!TextUtils.isEmpty(mBackgroundImage)) {
            Glide.with(mContext).load(mBackgroundImage)
                    .into(mBinding.ivBackImage);
            downloadBackImage();
        }
    }

    @Override
    public void chooseFrame(String path) {
        mBinding.ivFrameImage.setVisibility(View.VISIBLE);
        mFramePath = path;
        Glide.with(mContext)
                .load(path)
                .into(mBinding.ivFrameImage);

        setImageBackSize(nowUiIsLandscape);
    }

    @Override
    public void progress(long progress, boolean manualDrag) {
        mSeekBarViewManualDrag = manualDrag;
        if (manualDrag) {
            mBinding.progressBarView.scrollToPosition(progress);
        }
    }

    @Override
    public void manualDrag(boolean manualDrag) {
        mSeekBarViewManualDrag = manualDrag;
        videoToPause();
        LogUtil.d("OOM4", "progressBarProgress=" + progressBarProgress);
        presenter.getNowPlayingTime(progressBarProgress, mCutEndTime);
    }

    @Override
    public void timelineChange(long startTime, long endTime, String id) {
        LogUtil.d("playBGMMusic", "timelineChange---id=" + id);
        for (int i = 0; i < mBinding.idVviewRealtimeGllayout.getChildCount(); i++) {
            StickerView stickerView = (StickerView) mBinding.idVviewRealtimeGllayout.getChildAt(i);
            if (TextUtils.equals(id, String.valueOf(stickerView.getStickerNoIncludeAnimId()))) {
                if (!TextUtils.isEmpty(id) && "0".equals(id)) {
                    LogUtil.d("playBGMMusic", "需要改变开始时间和结束时间---musicStartFirstTime=" + startTime);
                    //需要改变开始时间和结束时间
                    musicStartFirstTime = startTime;
                    musicEndFirstTime = endTime;
                    LogUtil.d("OOM44", "444444");
                    musicStartTime = musicStartFirstTime;
                    LogUtil.d("playBGMMusic", "musicStartTime=" + musicStartTime);
                    if (musicEndFirstTime == 0) {
                        musicEndTime = allVideoDuration;
                    } else {
                        musicEndTime = musicEndFirstTime;
                    }

                    LogUtil.d("playBGMMusic", "musicEndTime=" + musicEndTime);
                }
                stickerView.setShowStickerStartTime(startTime);
                stickerView.setShowStickerEndTime(endTime);
                break;
            }
        }
        presenter.getNowPlayingTime(progressBarProgress, mCutEndTime);
    }

    @Override
    public void currentViewSelected(String id) {
        presenter.bringStickerFront(id);
    }

    @Override
    public void trackPause() {
        LogUtil.d("OOM44", "trackPause");
        videoToPause();
    }

    private void stickerTimeLineOffset() {

        for (int i = 0; i < mBinding.idVviewRealtimeGllayout.getChildCount(); i++) {
            StickerView stickerView = (StickerView) mBinding.idVviewRealtimeGllayout.getChildAt(i);

            if (stickerView.getShowStickerStartTime() > mCutStartTime && stickerView.getShowStickerEndTime() < mCutEndTime) {
                continue;
            }

            if (stickerView.getShowStickerStartTime() < mCutStartTime && stickerView.getShowStickerEndTime() > mCutEndTime) {
                stickerView.setShowStickerStartTime(mCutStartTime);
                stickerView.setShowStickerEndTime(mCutEndTime);
            }

            if (stickerView.getShowStickerStartTime() > mCutStartTime && stickerView.getShowStickerEndTime() > mCutEndTime) {
                stickerView.setShowStickerStartTime(mCutEndTime - (stickerView.getShowStickerEndTime() - stickerView.getShowStickerStartTime()));
                stickerView.setShowStickerEndTime(mCutEndTime);
            }

            if (stickerView.getShowStickerStartTime() > mCutStartTime && mCutEndTime - stickerView.getShowStickerStartTime() < 1000) {
                stickerView.setShowStickerStartTime(mCutStartTime);
            }

            if (stickerView.getShowStickerEndTime() < mCutStartTime) {
                stickerView.setShowStickerStartTime(mCutStartTime);
                stickerView.setShowStickerEndTime(mCutStartTime + 1000);
            }

            if (stickerView.getShowStickerEndTime() > mCutEndTime) {
                stickerView.setShowStickerEndTime(mCutEndTime);
                if (mCutEndTime - stickerView.getShowStickerStartTime() <= 1000) {
                    stickerView.setShowStickerStartTime(mCutEndTime - 1000);
                }
            }

            if (stickerView.getShowStickerStartTime() < mCutStartTime) {
                stickerView.setShowStickerStartTime(mCutStartTime);
                if (stickerView.getShowStickerEndTime() - mCutStartTime <= 1000) {
                    stickerView.setShowStickerEndTime(mCutStartTime + 1000);
                }
            }
        }
    }

    private void modificationDuration(long duration) {
        allVideoDuration = duration;
        mBinding.progressBarView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mBinding.progressBarView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mCutStartTime = 0;
                mCutEndTime = allVideoDuration;
                mBinding.tvTotal.setText(String.format("%ss", TimeUtils.timeParse(mCutEndTime - mCutStartTime)));
                mBinding.progressBarView.addProgressBarView(allVideoDuration, videoPath);
                mBinding.materialSeekBarView.resetStartAndEndTime(mCutStartTime, mCutEndTime);
                mBinding.materialSeekBarView.changeVideoPathViewFrameSetWidth(allVideoDuration);
                for (int i = 0; i < mBinding.idVviewRealtimeGllayout.getChildCount(); i++) {
                    for (int j = 0; j < mBinding.materialSeekBarView.getTemplateMaterialItemViews().size(); j++) {
                        StickerView stickerView = (StickerView) mBinding.idVviewRealtimeGllayout.getChildAt(i);
                        TemplateMaterialItemView itemView = mBinding.materialSeekBarView.getTemplateMaterialItemViews().get(j);
                        if (itemView != null) {
                            if (TextUtils.equals(String.valueOf(itemView.getIdentityID()), String.valueOf(stickerView.getStickerNoIncludeAnimId()))) {
                                stickerView.setShowStickerStartTime(itemView.getStartTime());
                                stickerView.setShowStickerEndTime(itemView.getEndTime());
                            }
                        }
                    }
                }
            }
        });

    }


    @Override
    public void onBackPressed() {
        StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "alert_edit_back_bj");
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
                        Runtime.getRuntime().gc();
                        finish();
                    }

                    @Override
                    public void onCancelBtnClick(CommonMessageDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .build().show();

        presenter.intoOnPause();
    }

    @Override
    public void onDestroy() {
//        presenter.onDestroy();
        destroyTimer();
        videoStop();
        if (bgmPlayer != null) {
            bgmPlayer.pause();
            bgmPlayer.release();
        }
        EventBus.getDefault().unregister(this);
        mBinding.rlCreationContainer.getViewTreeObserver().removeOnGlobalLayoutListener(mGlobalLayoutListener);
        super.onDestroy();
    }

    /**
     * 构造需要的intent
     */
    public static Intent buildIntent(Context context, int from, String imgPath, String videoPath, String originalPath,
                                     boolean isNeedCut, String title, int templateId, boolean nowUiIsLandscape, String backgroundImage) {
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_KEY_FROM, from);
        bundle.putString(BUNDLE_KEY_PATHS, imgPath);
        bundle.putString(BUNDLE_KEY_VIDEO_PATH, videoPath);
        bundle.putString(BUNDLE_KEY_ORIGINAL_PATH, originalPath);
        bundle.putBoolean(BUNDLE_KEY_NEED_CUT, isNeedCut);
        bundle.putString(BUNDLE_KEY_TITLE, title);
        bundle.putInt(BUNDLE_KEY_TEMPLATE_ID, templateId);
        bundle.putBoolean(BUNDLE_KEY_IS_LANDSCAPE, nowUiIsLandscape);
        bundle.putString(BUNDLE_KEY_BACKGROUND_IMAGE, backgroundImage);

        Intent intent = new Intent(context, CreationTemplateActivity.class);
        intent.putExtra(BUNDLE_KEY, bundle);
        return intent;
    }
}

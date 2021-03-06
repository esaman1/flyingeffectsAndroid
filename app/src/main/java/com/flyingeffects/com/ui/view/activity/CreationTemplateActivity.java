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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;

import com.bumptech.glide.request.target.Target;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.CreationBottomPagerAdapter;
import com.flyingeffects.com.adapter.TemplateGridViewAnimAdapter;
import com.flyingeffects.com.adapter.TemplateViewPager;
import com.flyingeffects.com.adapter.home_vp_frg_adapter;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.commonlyModel.GetPathType;
import com.flyingeffects.com.constans.UiStep;
import com.flyingeffects.com.databinding.ActCreationTemplateEditBinding;
import com.flyingeffects.com.entity.ChooseVideoAddSticker;
import com.flyingeffects.com.entity.CutSuccess;
import com.flyingeffects.com.entity.DownVideoPath;
import com.flyingeffects.com.entity.StickerTypeEntity;
import com.flyingeffects.com.entity.VideoInfo;
import com.flyingeffects.com.manager.AdConfigs;
import com.flyingeffects.com.manager.AlbumManager;
import com.flyingeffects.com.manager.CompressionCuttingManage;
import com.flyingeffects.com.manager.DataCleanManager;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.AlbumChooseCallback;
import com.flyingeffects.com.ui.interfaces.contract.ICreationTemplateMvpContract;
import com.flyingeffects.com.ui.model.AnimStickerModel;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.ui.model.GetPathTypeModel;
import com.flyingeffects.com.ui.model.VideoManage;
import com.flyingeffects.com.ui.presenter.CreationTemplateMvpPresenter;
import com.flyingeffects.com.ui.view.dialog.CommonMessageDialog;
import com.flyingeffects.com.ui.view.dialog.LoadingDialog;
import com.flyingeffects.com.ui.view.fragment.CreationBackListFragment;
import com.flyingeffects.com.ui.view.fragment.CreationBottomFragment;
import com.flyingeffects.com.ui.view.fragment.CreationFrameFragment;
import com.flyingeffects.com.ui.view.fragment.StickerFragment;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.ScreenCaptureUtil;
import com.flyingeffects.com.utils.TimeUtils;
import com.flyingeffects.com.view.StickerView;
import com.flyingeffects.com.view.drag.CreationTemplateProgressBarView;
import com.flyingeffects.com.view.drag.TemplateMaterialItemView;
import com.flyingeffects.com.view.drag.TemplateMaterialSeekBarView;
import com.flyingeffects.com.view.mine.CreateViewForAddText;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.lansosdk.videoeditor.MediaInfo;
import com.shixing.sxve.ui.AlbumType;
import com.shixing.sxve.ui.view.WaitingDialog;
import com.yanzhenjie.album.AlbumFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * description ?????????????????????,??????????????????langSong ?????????????????????????????????????????????
 * creation date: 2020/3/11
 * user : zhangtongju
 */


public class CreationTemplateActivity extends BaseActivity implements ICreationTemplateMvpContract.ICreationTemplateMvpView, TemplateMaterialSeekBarView.SeekBarProgressListener, CreationBackListFragment.BackChooseListener, StickerFragment.StickerListener, CreationBottomFragment.FinishListener, CreationFrameFragment.FrameChooseListener {
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
     * ???????????????
     */
    private String originalPath;
    private String imgPath;
    private CreationTemplateMvpPresenter mPresenter;

    /**
     * ????????????????????????????????????????????????????????????
     */
    private String videoPath;

    /**
     * ?????????????????????""????????????
     */
    private String imageBjPath;

    /**
     * ???????????????????????????????????????
     */
    private boolean isPlaying = false;

    /**
     * ???????????????????????????
     */
    private boolean isInitVideoLayer = false;
    private long allVideoDuration;
    private boolean isPlayComplate = false;

    /**
     * ??????????????????????????????????????????????????????""
     */
    private String title;

    /**
     * ????????????????????????
     */
    private String bgmPath;

    private SimpleExoPlayer exoPlayer;
    /**
     * ?????????????????????
     */
    private MediaPlayer bgmPlayer;
    /**
     * ??????????????????
     */
    private boolean isNeedCut;
    /**
     * ???????????????????????????tab
     */
    private boolean isClickAddTextTag = false;

    /**
     * ??????????????????
     */
    boolean mSeekBarViewManualDrag = false;

    /**
     * ?????????????????????
     */
    private long mCutStartTime;

    /**
     * ???????????????????????????
     */
    private long mCutEndTime;

    private long progressBarProgress;

    /**
     * ?????????????????????????????????
     */
    private long musicStartTime = 0;

    /**
     * ????????????????????????????????????????????????
     */
    private long musicStartFirstTime = 0;
    /**
     * ??????????????????????????????????????????????????????
     */
    private long musicEndFirstTime = 0;

    /**
     * ?????????????????????????????????
     */
    private long musicEndTime;

    private int musicChooseIndex = 0;

    private int mFrom;

    private MediaSource mediaSource;

    private ActCreationTemplateEditBinding mBinding;

    /**
     * ??????-?????????????????????????????????
     */
    private String mBackgroundImage;
    private String mBackGroundTitle;
    /**??????????????????*/
    private int initHeight = 0;
    /**??????onGlobalLayout()????????????????????????*/
    private int currentHeight;
    /**????????????????????????**??????**????????????*/
    private int firstFlag = 0;
    /**????????????????????????????????????*/
    private int status = 0;

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

        mBinding.tvTopSubmit.setText("?????????");

        EventBus.getDefault().register(this);
        mLoadingDialog = buildLoadingDialog();
        getLifecycle().addObserver(mLoadingDialog);
        LogUtil.d("OOM", "?????????????????????");

        initBundleData();

        mPresenter = new CreationTemplateMvpPresenter(this, this, videoPath, mBinding.idVviewRealtimeGllayout, originalPath, mFrom);
        LogUtil.d(TAG, "videoPath = " + videoPath);

        setOnClickListener();

        setDefaultVideoPlayerView();

        //????????????
        mPresenter.statisticsDuration(videoPath, this);

        //????????????????????????
        seekBarViewIsShow(true);

        setProgressBarListener();

        mBinding.materialSeekBarView.setProgressListener(this);

        //????????????????????????????????????
        initCreationContainer();

        setDefaultBottomVisible();
    }

    /**
     * ??????????????????????????????UI
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
            mBinding.rlSeekBar.setVisibility(View.GONE);
            mBinding.viewPager.setVisibility(View.GONE);
            mBinding.llGreenBackground.setVisibility(View.GONE);
        }
    }

    /**
     * ????????????????????????????????????
     */
    private void initCreationContainer() {
        mBinding.rlCreationContainer.post(() ->
                initHeight = mBinding.rlCreationContainer.getHeight());

        mGlobalLayoutListener = () -> {
            //?????????0????????????????????????????????????
            if (initHeight == 0) {
                return;
            }

            currentHeight = mBinding.rlCreationContainer.getHeight();
            if (initHeight > currentHeight) {
                //???????????????????????????????????????????????????????????????????????????
                //?????????????????????firstFlag?????????-1????????????????????????????????????
                status = -1;
                firstFlag = -1;
            } else if (initHeight < currentHeight) {
                //???????????????????????????????????????????????????????????????????????????
                //?????????????????????firstFlag?????????1????????????????????????????????????
                status = 1;
                firstFlag = 1;
            } else {
                //??????????????????????????????
                //?????????status?????????0
                status = 0;
            }
            //???????????????????????????????????????
            if (status != 0) {
                //??????????????????????????????????????????????????????????????????
                if (firstFlag == -1) {
                    //????????????????????????H?????????48dp???????????????????????????????????????????????????H??????
                    if (status < 0) {
                        if (mCreateViewForAddText != null) {
                            mCreateViewForAddText.setShowHeight(-1,
                                    Math.max(initHeight, currentHeight) - Math.min(initHeight, currentHeight));
                        }
                    }
                }
                //??????????????????????????????????????????????????????????????????
                if (firstFlag == 1) {
                    //????????????????????????
                    if (status > 0) {
                        if (mCreateViewForAddText != null) {
                            mCreateViewForAddText.setShowHeight(1, 0);
                        }
                    }
                }
            }
            //????????????currentHeight???????????????????????????????????????initHeight
            initHeight = currentHeight;
        };

        mBinding.rlCreationContainer.getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);
    }


    /**
     * ???????????????
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
                mPresenter.getNowPlayingTime(progressBarProgress, mCutEndTime);
                mBinding.tvCurrentTime.setText(String.format("%ss", TimeUtils.timeParse(progress - mCutStartTime)));
            }

            @Override
            public void cutInterval(long starTime, long endTime, boolean isDirection) {

                if (starTime < mCutStartTime) {
                    mBinding.tvCurrentTime.setText(String.format("%ss", TimeUtils.timeParse(0)));
                    mCutStartTime = starTime;
                } else {
                    mCutStartTime = starTime;
                    mBinding.tvCurrentTime.setText(String.format("%ss", TimeUtils.timeParse(progressBarProgress - mCutStartTime)));
                }

                mCutEndTime = endTime;

                mBinding.tvTotal.setText(String.format("%ss", TimeUtils.timeParse(mCutEndTime - mCutStartTime)));
                mBinding.materialSeekBarView.setCutStartAndEndTime(starTime, endTime);
                stickerTimeLineOffset();
//                LogUtil.d("oom44", "musicStartTime=" + musicStartTime + "starTime=" + starTime + "musicEndTime=" + musicEndTime + "mCutStartTime=" + mCutStartTime);

                if (isDirection) {
                    mBinding.materialSeekBarView.scrollToPosition(starTime);
                    //--------------ztj   ??????bug???????????????????????????????????????????????????
                    if (musicStartTime < starTime) {
//                        musicStartTime = starTime;
//                        long xx = mCutStartTime - lastmCutTime;
//                        musicEndTime = musicEndTime - xx;
                        musicStartFirstTime = starTime;
                        musicStartTime = starTime;
                        LogUtil.d("oom44", "musicStartTime=" + musicStartTime + "starTime=" + starTime + "musicEndTime=" + musicEndTime + "mCutStartTime=" + mCutStartTime);
                    }
                    //ztj  ??????????????? ?????????????????????????????????1000+end
                    if (musicEndTime < starTime) {
                        musicEndFirstTime = musicStartTime + 1000;
                        musicEndTime = musicEndFirstTime;
                        LogUtil.d("oom44", "???????????????musicEndTime=" + musicEndTime + "musicStartTime=" + musicStartTime);
                    }

                } else {
                    LogUtil.d("oom444", "xx=");
                    mBinding.materialSeekBarView.scrollToPosition(endTime);
                }

                mPresenter.getNowPlayingTime(progressBarProgress, mCutEndTime);
            }

            @Override
            public void onTouchEnd() {
                videoToPause();
                mPresenter.getNowPlayingTime(progressBarProgress, mCutEndTime);
            }
        });
    }

    /**
     * ????????????????????????
     */
    private void setDefaultVideoPlayerView() {
        //????????????????????????????????????
        if (!TextUtils.isEmpty(videoPath)) {
            //?????????????????????????????????????????????
            boolean isLandscape = getDefaultPlayerViewSize();
            setPlayerViewSize(isLandscape);
            initExo(videoPath);

        } else {
            setImageBackSize(false);
            //??????????????????????????????????????????
            changeImageBack();
        }
        //??????????????????????????????????????????
        if (nowUiIsLandscape) {
            new Handler().postDelayed(() -> setPlayerViewSize(nowUiIsLandscape), 500);
        }
    }

    /**
     * ???????????????????????????
     */
    private boolean getDefaultPlayerViewSize() {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(videoPath);
            String width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);

            int w = Integer.parseInt(width);
            int h = Integer.parseInt(height);

            LogUtil.d(TAG, "video width = " + width + " video height = " + height);
            return w > h;
        } catch (Exception e) {
            LogUtil.d("e", e.getMessage());
            return false;
        }
    }

    /**
     * ????????????????????????????????????
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
     * ???????????????????????????
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
     * ????????????????????????
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
                .setTitle("?????????????????????")
                .setMessage("??????????????????????????????")
                .build();
    }


    private void initExo(String videoPath) {
        if (TextUtils.isEmpty(videoPath)) {
            return;
        }

        exoPlayer = new SimpleExoPlayer.Builder(mContext)
                .build();
        mBinding.exoPlayer.setPlayer(exoPlayer);
        //??????????????????
        mBinding.exoPlayer.setUseController(false);
        exoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
        exoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
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
                createMediaSource(new MediaItem.Builder()
                        .setUri(Uri.fromFile(new File(videoPath))).build());

        exoPlayer.setMediaSource(mediaSource, true);
        exoPlayer.prepare();

        pauseExoPlayer();
    }

    private void videoToStart() {
        isPlayComplate = true;
        endTimer();
        isPlaying = false;
        mPresenter.showGifAnim(false);
        videoToPause();
        seekToVideo(mCutStartTime);
        seekToMusic(mCutStartTime);
        nowStateIsPlaying(false);
        mPresenter.showAllAnim(false);
    }


    private void pauseExoPlayer() {
        if (exoPlayer != null) {
            LogUtil.d("video", "videoPause");
            exoPlayer.setPlayWhenReady(false);
        }
    }

    /**
     * ????????????
     * ?????????????????????????????????????????????????????????
     * ???????????????????????????????????????
     */
    private void videoPlay() {
        if (exoPlayer != null) {
            LogUtil.d("video", "play");
            if (!TextUtils.isEmpty(bgmPath)) {
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
        mPresenter.initStickerView(imgPath, originalPath);

        initBottomLayout();

        //????????????????????????
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

            mPresenter.checkedChanged(isChecked);
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
     * ????????????
     */
    private void choosePhotoFrame() {
        seekBarViewIsShow(false);
        chooseAnim(1);
        setTextColor(5);
        isClickAddTextTag = false;
    }

    /**
     * ???????????????
     */
    private void changeLandscape() {
        //???????????????
        nowUiIsLandscape = !nowUiIsLandscape;
        if (mFrom == FROM_DRESS_UP_BACK_CODE) {
            setImageBackSize(nowUiIsLandscape);
        } else {
            setPlayerViewSize(nowUiIsLandscape);
        }
    }

    /**
     * ????????????
     */
    private void chooseStickerBtn() {
        seekBarViewIsShow(false);
        if (mFrom == FROM_DRESS_UP_BACK_CODE) {
            chooseAnim(2);
        } else {
            chooseAnim(0);
        }
        setTextColor(0);
        isClickAddTextTag = false;
    }

    /**
     * ????????????
     */
    private void chooseAnimBtn() {
        seekBarViewIsShow(false);
        chooseAnim(1);
        setTextColor(1);
        isClickAddTextTag = false;
    }

    /**
     * ????????????
     */
    private void chooseBackground() {
        Intent intent = new Intent(
                this, ChooseBackgroundTemplateActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        isClickAddTextTag = false;
    }

    /**
     * ????????????
     */
    private void addText() {
        mBinding.viewPager.setVisibility(View.GONE);
        seekBarViewIsShow(false);
        mPresenter.addTextSticker();
        intoTextStyleDialog("");
        isClickAddTextTag = true;
        if (mFrom == FROM_DRESS_UP_BACK_CODE) {
            StatisticsEventAffair.getInstance().setFlag(this, "st_bj_text");
        } else {
            StatisticsEventAffair.getInstance().setFlag(this, "20_bj_text");
        }
    }

    public void chooseAnim(int pageNum) {
        mBinding.viewPager.setCurrentItem(pageNum);
        mBinding.viewPager.setVisibility(View.VISIBLE);
    }

    /**
     * ????????????
     */
    private void onClickMusicBtn() {
        seekBarViewIsShow(false);
        chooseAnim(2);
        setTextColor(2);
        isClickAddTextTag = false;
    }

    /**
     * ???????????? ????????????
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
        chooseAnim(0);
        setTextColor(4);
        isClickAddTextTag = false;
    }

    /**
     * ????????????????????????
     */
    private void addSticker() {
        if (!DoubleClick.getInstance().isFastZDYDoubleClick(1000)) {
            mPresenter.showAllAnim(false);
            if (isPlaying) {
                videoToPause();//????????????
                isPlaying = false;
                endTimer();
                mPresenter.showGifAnim(false);
                nowStateIsPlaying(false);
            }
            if (UiStep.isFromDownBj) {
                StatisticsEventAffair.getInstance().setFlag(CreationTemplateActivity.this, "5_mb_bj_material");
                StatisticsEventAffair.getInstance().setFlag(CreationTemplateActivity.this, "8_material");
            } else {
                StatisticsEventAffair.getInstance().setFlag(CreationTemplateActivity.this, "6_customize_bj_material");
                StatisticsEventAffair.getInstance().setFlag(CreationTemplateActivity.this, "7_material");
            }
            //???????????????????????????????????????????????????????????????
            AlbumManager.chooseAlbum(this, 1, SELECTALBUM, (tag, paths, isCancel, isFromCamera, albumFileList) -> {
                Log.d("OOM", "isCancel=" + isCancel);
                if (!isCancel) {
                    //??????????????????????????????????????????????????????????????????matting????????????????????????????????????????????????
                    String path = paths.get(0);
                    String pathType = GetPathTypeModel.getInstance().getMediaType(path);
                    if (AlbumType.isImage(pathType)) {
                        StatisticsEventAffair.getInstance().setFlag(CreationTemplateActivity.this, "7_SelectImage");
                        CompressionCuttingManage manage = new CompressionCuttingManage(CreationTemplateActivity.this, "", tailorPaths -> {
                            mPresenter.addNewSticker(tailorPaths.get(0), paths.get(0));
                        });
                        manage.toMatting(paths);
                    } else {
                        //?????????????????????
                        intoVideoCropActivity(paths.get(0));
                        StatisticsEventAffair.getInstance().setFlag(CreationTemplateActivity.this, "7_Selectvideo");
                    }
                }
            }, "");
        }
    }

    /**
     * ????????????????????????
     */
    private void deleteAllText() {
        mPresenter.deleteAllTextSticker();
        if (mCreateViewForAddText != null) {
            mCreateViewForAddText.hideInputTextDialog();
        }
    }

    /**
     * ???????????????
     */
    private void submitCreation() {
        if (mFrom == FROM_DRESS_UP_BACK_CODE) {
            keepPicture();
            if (TextUtils.isEmpty(mBackGroundTitle)) {
                mBackGroundTitle = title;
            }
            StatisticsEventAffair.getInstance().setFlag(CreationTemplateActivity.this, "st_bj_save", mBackGroundTitle);
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

            mPresenter.toSaveVideo(imageBjPath, nowUiIsLandscape, percentageH, templateId, musicStartTime, musicEndTime, mCutStartTime, mCutEndTime, title);
            seekBarViewIsShow(true);
        }

    }

    /**
     * ?????????????????????
     */
    private void onPlayClick() {
        if (!DoubleClick.getInstance().isFastZDYDoubleClick(500)) {
            if (isPlaying) {
                pauseBgmMusic();
                isIntoPause = false;
                isPlayComplate = false;
                videoToPause();//??????????????????
                mPresenter.showGifAnim(false);
                isPlaying = false;
                nowStateIsPlaying(false);
                mPresenter.showAllAnim(false);
            } else {
                StatisticsEventAffair.getInstance().setFlag(CreationTemplateActivity.this, " 14_preview_video_bj");
                WaitingDialog.openPragressDialog(this);
                new Thread(() -> mPresenter.showAllAnim(true)).start();
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
                    mPresenter.changeTextStyle(path, type, title);
                }

                @Override
                public void setText(String text) {
                    mPresenter.changeTextLabe(text);
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
                    mPresenter.changeTextColor(color0, color1, title);
                }

                @Override
                public void isSuccess(String textBjPath, String textFramePath, String frameTitle) {
                    LogUtil.d("OOM4", "textBjPath=" + textBjPath + "textFramePath=" + textFramePath + "frameTitle" + frameTitle);
                    mPresenter.changeTextFrame(textBjPath, textFramePath, frameTitle);
                }

                @Override
                public void isSuccess(String color0, String color1, String textFramePath, String frameTitle) {
                    LogUtil.d("OOM4", "color0=" + color0 + "color1=" + color1 + "textFramePath" + textFramePath + "frameTitle" + frameTitle);
                    mPresenter.changeTextFrame(color0, color1, textFramePath, frameTitle);
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
        mPresenter.intoOnPause();
        isPlaying = false;
        endTimer();
        mPresenter.showGifAnim(false);
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
     * description ???????????????????????????
     * date: ???2019/11/18 20:24
     * author: ????????? @?????? jutongzhang@sina.com
     */
    private void initViewLayerRelative() {
        ViewGroup.LayoutParams relativeLayoutParams = mBinding.idVviewRealtimeGllayout.getLayoutParams();
        ViewGroup.LayoutParams relativeLayoutParams2 = mBinding.relativeContentAllContent2.getLayoutParams();

        float oriRatio;
        oriRatio = 9f / 16f;
        //????????????mContainer????????????0
        mBinding.idVviewRealtimeGllayout.post(() -> {
            int oriHeight = mBinding.idVviewRealtimeGllayout.getHeight();
            relativeLayoutParams.width = Math.round(1f * oriHeight * oriRatio);
            relativeLayoutParams.height = oriHeight;
            mBinding.idVviewRealtimeGllayout.setLayoutParams(relativeLayoutParams);
            mBinding.relativeContentAllContent2.setLayoutParams(relativeLayoutParams);
            mBinding.relativeContentAllContent2.setGravity(Gravity.CENTER_HORIZONTAL);
            mBinding.relativeContentAllContent.setGravity(Gravity.CENTER_HORIZONTAL);
            relativeLayoutParams2.width = relativeLayoutParams.width;
            relativeLayoutParams2.height = relativeLayoutParams.height;
            mBinding.idVviewRealtimeGllayout.setLayoutParams(relativeLayoutParams2);
            mBinding.llContentPatents.setGravity(Gravity.CENTER_HORIZONTAL);
        });


        if (!TextUtils.isEmpty(videoPath)) {
            setBJVideoPath(false);
        }
    }

    /**
     * ????????????????????????
     *
     * @param isModifyMaterialTimeLine ???????????????????????????
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
                //????????????mContainer????????????0
                RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                //????????????????????????????????????????????????????????????????????????????????????????????????
                mBinding.ivGreenBackground.post(() -> {
                    int oriHeight = mBinding.llSpace.getHeight();
                    LogUtil.d(TAG, "ivGreenBackground height = " + oriHeight);
                    relativeLayoutParams.width = Math.round(1f * oriHeight * oriRatio);
                    relativeLayoutParams.height = oriHeight;
                    mBinding.ivGreenBackground.setLayoutParams(relativeLayoutParams);
                });
                isInitImageBj = true;
            }
        } else {
            setPlayerViewSize(nowUiIsLandscape);
        }
        mPresenter.initVideoProgressView();

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
     * description ????????????????????????,????????????????????????????????????????????????????????????ScrollView
     * ??????????????????????????????2????????????????????????,
     * creation date: 2020/8/10
     * user : zhangtongju
     */
    private float percentageH = 0;

    private void setPlayerViewSize(boolean isLandscape) {

        videoToPause();
        //???????????????
        LinearLayout.LayoutParams relativeLayoutParams = (LinearLayout.LayoutParams) mBinding.exoPlayer.getLayoutParams();
        float oriRatio = 9f / 16f;

        if (isLandscape) {
            //???????????????
            mBinding.scrollView.post(() -> {
                int oriWidth = mBinding.llSpace.getWidth();
                RelativeLayout.LayoutParams relativeLayoutParams2 = (RelativeLayout.LayoutParams) mBinding.scrollView.getLayoutParams();
                relativeLayoutParams2.width = oriWidth;
                relativeLayoutParams2.height = Math.round(1f * oriWidth * oriRatio);
                mBinding.scrollView.setLayoutParams(relativeLayoutParams2);
                relativeLayoutParams.width = oriWidth;
                relativeLayoutParams.height = Math.round(1f * oriWidth / oriRatio);
                mBinding.exoPlayer.setLayoutParams(relativeLayoutParams);
                //????????????????????????
                mBinding.idVviewRealtimeGllayout.setLayoutParams(relativeLayoutParams2);
                RelativeLayout.LayoutParams contentParam = (RelativeLayout.LayoutParams) mBinding.relativeContentAllContent2.getLayoutParams();
                contentParam.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                contentParam.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                contentParam.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                mBinding.relativeContentAllContent2.setLayoutParams(contentParam);

            });
        } else {
            //?????????????????????????????????
            mBinding.scrollView.post(() -> {
                RelativeLayout.LayoutParams relativeLayoutParams2 = (RelativeLayout.LayoutParams) mBinding.scrollView.getLayoutParams();

                int height = mBinding.llSpace.getHeight();
                relativeLayoutParams2.height = height;
                relativeLayoutParams2.width = Math.round(1f * height * oriRatio);
                mBinding.scrollView.setLayoutParams(relativeLayoutParams2);

                relativeLayoutParams.width = Math.round(1f * height * oriRatio);
                relativeLayoutParams.height = height;
                mBinding.exoPlayer.setLayoutParams(relativeLayoutParams);
                //????????????????????????
                mBinding.idVviewRealtimeGllayout.setLayoutParams(relativeLayoutParams2);
                RelativeLayout.LayoutParams contentParam = (RelativeLayout.LayoutParams) mBinding.relativeContentAllContent2.getLayoutParams();
                contentParam.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                contentParam.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                contentParam.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                mBinding.relativeContentAllContent2.setLayoutParams(contentParam);


            });
        }


        if (mBinding.llGreenBackground.getVisibility() == View.VISIBLE) {
            //?????????????????????????????????
            Observable.just(isLandscape).observeOn(AndroidSchedulers.mainThread()).subscribe(aBoolean -> {
                if (isLandscape) {
                    //???????????????
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
            mPresenter.setAllStickerCenter();
            if (isLandscape) {
                int height = Math.round(1f * mBinding.llSpace.getWidth() / oriRatio);
                mBinding.scrollView.scrollTo(0, height / 2 - mBinding.scrollView.getHeight() / 2);
            }
        }, 500);
    }


    private void setImageBackSize(boolean isLandscape) {

        videoToPause();
        //???????????????
        ViewGroup.LayoutParams relativeLayoutParams = mBinding.ivBackImage.getLayoutParams();
        float oriRatio = 9f / 16f;
        float frameRatio = 1f;

        if (isLandscape) {
            //???????????????
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

                //????????????????????????
                mBinding.idVviewRealtimeGllayout.setLayoutParams(relativeLayoutParams2);

//                if (mBinding.ivFrameImage.getVisibility() == View.VISIBLE) {
//                    ViewGroup.LayoutParams frameLayoutParams = mBinding.ivFrameImage.getLayoutParams();
//                    frameLayoutParams.height = spaceHeight;
//                    frameLayoutParams.width = spaceHeight;
//                    mBinding.ivFrameImage.setLayoutParams(frameLayoutParams);
//                }
            });


        } else {
            //?????????????????????????????????
            mBinding.svBackImage.post(() -> {
                RelativeLayout.LayoutParams relativeLayoutParams2 = (RelativeLayout.LayoutParams) mBinding.svBackImage.getLayoutParams();

                int height = mBinding.llSpace.getHeight();
                int width = mBinding.llSpace.getWidth();

                if (mBinding.ivFrameImage.getVisibility() == View.VISIBLE) {


                    relativeLayoutParams.width = width;//Math.round(1f * height * oriRatio)
                    relativeLayoutParams.height = Math.round(1f * width / oriRatio);// height

                    mBinding.ivBackImage.setLayoutParams(relativeLayoutParams);

                    //????????????????????????

                    //RelativeLayout.LayoutParams frameLayoutParams = (RelativeLayout.LayoutParams) mBinding.ivFrameImage.getLayoutParams();

                    relativeLayoutParams2.width = width;
                    relativeLayoutParams2.height = height;

                    //mBinding.ivFrameImage.setLayoutParams(frameLayoutParams);

                    mBinding.svBackImage.setLayoutParams(relativeLayoutParams2);
                    mBinding.idVviewRealtimeGllayout.setLayoutParams(relativeLayoutParams2);
                } else {
                    relativeLayoutParams2.height = height;
                    relativeLayoutParams2.width = Math.round(1f * height * oriRatio);
                    mBinding.svBackImage.setLayoutParams(relativeLayoutParams2);

                    relativeLayoutParams.width = Math.round(1f * height * oriRatio);
                    relativeLayoutParams.height = height;
                    mBinding.ivBackImage.setLayoutParams(relativeLayoutParams);
                    //????????????????????????
                    mBinding.idVviewRealtimeGllayout.setLayoutParams(relativeLayoutParams2);
                    RelativeLayout.LayoutParams contentParam = (RelativeLayout.LayoutParams) mBinding.relativeContentAllContent2.getLayoutParams();
                    contentParam.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    contentParam.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    contentParam.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);

                    mBinding.relativeContentAllContent2.setLayoutParams(contentParam);
                }
            });
        }


        if (mBinding.llGreenBackground.getVisibility() == View.VISIBLE) {
            //?????????????????????????????????
            Observable.just(isLandscape).observeOn(AndroidSchedulers.mainThread()).subscribe(aBoolean -> {
                if (isLandscape) {
                    //???????????????
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
            mPresenter.setAllStickerCenter();
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
        mPresenter.intoOnPause();
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
            //????????????????????????
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
        if (mFrom != FROM_DRESS_UP_BACK_CODE) {
            new Handler().postDelayed(() -> {
                mBinding.viewPager.setCurrentItem(0);
                mBinding.tvMusic.setVisibility(View.GONE);
                setTextColor(0);
            }, 500);
        }
    }


    /**
     * description ????????????????????????str ????????????????????????????????????
     * creation date: 2020/10/15
     * user : zhangtongju
     */
    @Override
    public void stickerOnclickCallback(String str) {
        if (!TextUtils.isEmpty(str) && mCreateViewForAddText != null) {
            if (!"????????????".equals(str)) {
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
            mPresenter.showGifAnim(false);
            isPlaying = false;
            nowStateIsPlaying(false);
        }
    }

    @Override
    public void getVideoCover(String path, String originalPath) {
        mPresenter.addNewSticker(path, originalPath);
        if (TextUtils.isEmpty(videoPath)) {
            //??????????????????????????????????????????????????????
            Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer ->
                    mPresenter.initVideoProgressView());
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
                //??????????????????????????????????????????????????????
                Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> mPresenter.initVideoProgressView());
            }
            //??????????????????????????????????????????,???????????????????????????
            musicStartTime = 0;
            musicStartFirstTime = 0;
            LogUtil.d("OOM44", "?????????????????????????????????????????????????????????" + musicEndTime);
//            musicEndTime = getFristVideoDuration();
            musicEndFirstTime = musicEndTime;
            LogUtil.d("OOM44", "??????????????????????????????????????????????????????musicEndFirstTime???" + musicEndFirstTime);
        }
    }


    /**
     * ???????????????????????????????????????????????????
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
        if (mFrom != FROM_DRESS_UP_BACK_CODE) {
            if (isShow) {

                mBinding.tvMusic.setVisibility(View.VISIBLE);

            } else {
                mBinding.tvMusic.setVisibility(View.GONE);
                mBinding.viewPager.setCurrentItem(0);
            }

            for (int value : LIN_ID) {
                ((TextView) findViewById(value)).setTextColor(ContextCompat.getColor(mContext, R.color.white));
            }
        }
    }


    /**
     * description ???????????????????????????????????????????????????
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
                //????????????????????????
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
                //???????????????????????????????????????,?????????????????????????????????????????????
                if (!TextUtils.isEmpty(bgmPath) && musicEndTime == 0) {
                    if (bgmPlayer != null) {
                        //????????????
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
            mPresenter.showGifAnim(true);
        });

    }

    private Timer timer;
    private TimerTask task;
    private long nowTime = 5;
    //???????????????????????????
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
                creationTimerTask();
            }
        };
        timer.schedule(task, 0, 5);
    }

    private void creationTimerTask() {
        totalPlayTime = totalPlayTime + 5;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bjMusicControl();
                if (!TextUtils.isEmpty(videoPath)) {
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
                    //??????????????????
                    nowTime = nowTime + 5;
                    LogUtil.d("OOM44", "nowTime==" + nowTime + "mCutEndTime=" + mCutEndTime);

                    if (nowTime >= mCutEndTime) {
                        nowTime = mCutStartTime;
                        isPlayComplate = true;
                        endTimer();
                        isPlaying = false;
                        mPresenter.showGifAnim(false);
                        nowStateIsPlaying(false);
                        mPresenter.showAllAnim(false);
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


    /**
     * description ????????????????????????????????????
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
                        LogUtil.d("playBGMMusic2", "??????????????????");
                        isNeedPlayBjMusci = false;
                        pauseBgmMusic();
                    } else {
                        if (!isNeedPlayBjMusci) {
                            LogUtil.d("playBGMMusic2", "????????????");
                            LogUtil.d("playBGMMusic2", "totalPlayTime=" + totalPlayTime + "mCutStartTime=" + mCutStartTime + "needTime=" + needTime + "musicStartTime=" + musicStartTime + "needMusicStartTime=" + needMusicStartTime);
                            playBjMusic();
                        }
                        isNeedPlayBjMusci = true;
                    }
                } else {
                    LogUtil.d("playBGMMusic", "musicEndTime=" + musicEndTime);
                    if (!isNeedPlayBjMusci) {
                        LogUtil.d("playBGMMusic", "????????????");
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
     * ??????????????????
     */
    private long getCurrentPos() {
        return exoPlayer != null ? exoPlayer.getCurrentPosition() : 0;
    }

    /**
     * ??????timer ???task
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
     * time???2018/10/15
     * describe:??????????????????
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
     * description ???????????????????????????????????????
     * creation date: 2020/4/13
     * user : zhangtongju
     */
    @Subscribe
    public void onEventMainThread(DownVideoPath event) {
//        videoStop();
        Observable.just(event.getPath()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(s -> {
            if (AlbumType.isImage(GetPathTypeModel.getInstance().getMediaType(event.getPath()))) {
                mBinding.relativePlayerView.setVisibility(View.INVISIBLE);
                mBinding.rlBackImage.setVisibility(View.INVISIBLE);

                mPresenter.setVideoPath("");
                videoPath = "";
                showGreenBj(false);
                imageBjPath = event.getPath();

                //???????????????????????????????????????10???
                //?????????????????????????????????
                long maxVideoDuration = 0;
                for (int i = 0; i < mBinding.materialSeekBarView.getTemplateMaterialItemViews().size(); i++) {
                    if (mBinding.materialSeekBarView.getTemplateMaterialItemViews().get(i) != null) {
                        if (mBinding.materialSeekBarView.getTemplateMaterialItemViews().get(i).getDuration() >= maxVideoDuration &&
                                AlbumType.isVideo(GetPathType.getInstance().getPathType(mBinding.materialSeekBarView.getTemplateMaterialItemViews().get(i).resPath))) {
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
                LogUtil.d("OOM44", s);
                musicStartTime = 0;
                musicEndFirstTime = mCutEndTime;
                musicEndTime = mCutEndTime;

                new Handler().postDelayed(() ->
                        Glide.with(CreationTemplateActivity.this)
                                .load(s).into(mBinding.ivGreenBackground), 500);
            } else {
                LogUtil.d("OOM", "???????????????????????????,?????????" + event.getPath());
                videoPath = event.getPath();
                mBinding.llGreenBackground.setVisibility(View.GONE);
                mBinding.relativePlayerView.setVisibility(View.VISIBLE);

                setPlayerViewSize(nowUiIsLandscape);
                initExo(videoPath);
                mPresenter.setVideoPath(videoPath);
                mPresenter.initVideoProgressView();
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
     * description ?????????????????????????????????
     * creation date: 2020/4/13
     * user : zhangtongju
     */
    @Subscribe
    public void onEventMainThread(ChooseVideoAddSticker event) {
        mPresenter.getVideoCover(event.getPath());
    }


    /**
     * ????????????
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
        mPresenter.setAddChooseBjPath(nowChooseBjPath);
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
                //????????????????????????????????????????????? ????????????????????????????????????  ???????????????????????????
                if (AlbumType.isVideo(GetPathType.getInstance().getPathType(stickerView.getOriginalPath()))
                        && TextUtils.isEmpty(videoPath)) {

                    MediaInfo mediaInfo = new MediaInfo(stickerView.getOriginalPath());
                    mediaInfo.prepare();
                    long videoDuration = (long) (mediaInfo.vDuration * 1000);
                    mediaInfo.release();

                    boolean modify = false;
                    //?????????????????????????????????
                    long maxVideoDuration = 0;
                    for (int i = 0; i < mBinding.materialSeekBarView.getTemplateMaterialItemViews().size(); i++) {
                        if (mBinding.materialSeekBarView.getTemplateMaterialItemViews().get(i) != null) {
                            if (mBinding.materialSeekBarView.getTemplateMaterialItemViews().get(i).getDuration() >= maxVideoDuration &&
                                    AlbumType.isVideo(GetPathType.getInstance().getPathType(mBinding.materialSeekBarView.getTemplateMaterialItemViews().get(i).resPath))) {
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
                } else if (AlbumType.isVideo(GetPathType.getInstance().getPathType(stickerView.getOriginalPath())) && !TextUtils.isEmpty(videoPath)) {
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
                    //???????????????????????????????????????10???   ??????????????????????????????????????????????????????????????????????????????
                    long materialDuration;
                    long maxVideoDuration = 0;
                    boolean isMaxVideoDurationChange = false;
                    //?????????????????????????????????????????? ????????????????????????????????????????????????
                    boolean isAfreshSetEndCutEnd = false;
                    if (TextUtils.isEmpty(videoPath)) {
                        materialDuration = 10 * 1000;
                        for (int i = 0; i < mBinding.materialSeekBarView.getTemplateMaterialItemViews().size(); i++) {
                            if (mBinding.materialSeekBarView.getTemplateMaterialItemViews().get(i) != null) {
                                if (AlbumType.isVideo(GetPathType.getInstance().getPathType(mBinding.materialSeekBarView.getTemplateMaterialItemViews().get(i).resPath))) {
                                    for (int j = 0; j < mBinding.materialSeekBarView.getTemplateMaterialItemViews().size(); j++) {
                                        if (mBinding.materialSeekBarView.getTemplateMaterialItemViews().get(j) != null &&
                                                mBinding.materialSeekBarView.getTemplateMaterialItemViews().get(j).getDuration() >= maxVideoDuration &&
                                                AlbumType.isVideo(GetPathType.getInstance().getPathType(mBinding.materialSeekBarView.getTemplateMaterialItemViews().get(j).resPath))
                                        ) {
                                            maxVideoDuration = mBinding.materialSeekBarView.getTemplateMaterialItemViews().get(j).getDuration();
                                            maxVideoResPath = mBinding.materialSeekBarView.getTemplateMaterialItemViews().get(j).resPath;
                                        }
                                    }
                                }
                            }
                        }
                        //???????????????  ???????????????????????????????????????????????????  ??????????????????
                        if (maxVideoDuration > 0) {
                            materialDuration = maxVideoDuration;
                            isMaxVideoDurationChange = true;
                        } else {
                            //??????????????????  ??????????????????10??? ??????????????????
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
        if (AlbumType.isVideo(GetPathType.getInstance().getPathType(path)) && TextUtils.isEmpty(videoPath)) {
            //??????????????????????????????
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
            //????????????
            mBinding.materialSeekBarView.modifyMaterialThumbnail(path, id, false);
            if (TextUtils.equals("0", id) && AlbumType.isVideo(GetPathType.getInstance().getPathType(path))) {
                MediaInfo mediaInfo = new MediaInfo(path);
                mediaInfo.prepare();
                stickerView.setShowStickerStartTime(0);
                long minDuration = Math.min((long) (mediaInfo.vDuration * 1000), mCutEndTime);
                mediaInfo.release();
                stickerView.setShowStickerEndTime(minDuration);
                musicStartTime = 0;
                musicStartFirstTime = 0;
                if (musicChooseIndex == 0) {
                    //????????????
                    musicEndFirstTime = minDuration;
                    musicEndTime = minDuration;
                } else {
                    //????????????
                    musicEndFirstTime = mCutEndTime;
                    musicEndTime = mCutEndTime;
                }
            }
            mBinding.materialSeekBarView.setCutStartTime(mCutStartTime);
            mBinding.materialSeekBarView.setCutEndTime(mCutEndTime);
        } else {
            //?????????????????????????????????????????????????????????????????????
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
        if (mFrom == FROM_DRESS_UP_BACK_CODE) {
            mBinding.viewPager.setVisibility(View.GONE);
        }
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
    public void chooseBack(String title, String path) {
        if (TextUtils.isEmpty(path)) {
            upLoadLocalBack();
        } else {
            mBackGroundTitle = title;
            if (TextUtils.isEmpty(mBackGroundTitle)) {
                mBackGroundTitle = "????????????";
            }
            StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "st_bj_background", mBackGroundTitle);

            mBackgroundImage = path;
            changeImageBack();
        }

    }

    /**
     * ??????????????????
     */
    private void upLoadLocalBack() {
        AlbumManager.chooseImageAlbum(mContext, 1, 0, new AlbumChooseCallback() {
            @Override
            public void resultFilePath(int tag, List<String> paths, boolean isCancel, boolean isFromCamera, ArrayList<AlbumFile> albumFileList) {
                if (!isCancel) {
                    mBackgroundImage = paths.get(0);
                    changeImageBack();
                }
            }
        }, "");
    }


    /**
     * ?????????
     */
    private void changeImageBack() {
        if (!TextUtils.isEmpty(mBackgroundImage)) {
            Glide.with(mContext).load(mBackgroundImage)
                    .into(mBinding.ivBackImage);
            downloadBackImage();
        } else {
            showGreenBj(true);
        }
    }

    /**
     * ????????????
     *
     * @param path
     */
    @Override
    public void chooseFrame(String path) {
        mBinding.ivFrameImage.setVisibility(View.VISIBLE);

        Glide.with(mContext)
                .load(path)
                .into(mBinding.ivFrameImage);
        setImageBackSize(nowUiIsLandscape);

    }

    /**
     * ????????????
     */
    public void dismissFrame() {
        mBinding.ivFrameImage.setVisibility(View.INVISIBLE);
        setImageBackSize(nowUiIsLandscape);
    }

    //    private void test() {
//        mBinding.ivFrameImage.post(() -> {
//            getImgDisplaySize( mBinding.ivFrameImage);
//        });
//    }


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
        mPresenter.getNowPlayingTime(progressBarProgress, mCutEndTime);
    }

    @Override
    public void timelineChange(long startTime, long endTime, String id) {
        LogUtil.d("playBGMMusic", "timelineChange---id=" + id);
        for (int i = 0; i < mBinding.idVviewRealtimeGllayout.getChildCount(); i++) {
            StickerView stickerView = (StickerView) mBinding.idVviewRealtimeGllayout.getChildAt(i);
            if (TextUtils.equals(id, String.valueOf(stickerView.getStickerNoIncludeAnimId()))) {
                if (!TextUtils.isEmpty(id) && "0".equals(id)) {
                    LogUtil.d("playBGMMusic", "???????????????????????????????????????---musicStartFirstTime=" + startTime);
                    //???????????????????????????????????????
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
        mPresenter.getNowPlayingTime(progressBarProgress, mCutEndTime);
    }

    @Override
    public void currentViewSelected(String id) {
        mPresenter.bringStickerFront(id);
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
    public void dismissStickerFrame() {
        for (int i = 0; i < mBinding.idVviewRealtimeGllayout.getChildCount(); i++) {
            StickerView stickerView = (StickerView) mBinding.idVviewRealtimeGllayout.getChildAt(i);
            stickerView.disMissFrame();
        }
    }

    @Override
    public void dismissTextStickerFrame() {
        for (int i = 0; i < mBinding.idVviewRealtimeGllayout.getChildCount(); i++) {
            StickerView stickerView = (StickerView) mBinding.idVviewRealtimeGllayout.getChildAt(i);
            if (stickerView.getIsTextSticker()) {
                stickerView.disMissFrame();
            }
        }
    }

    public void buildBottomViewPager() {
        if (mFrom == CreationTemplateActivity.FROM_DRESS_UP_BACK_CODE) {
            CreationBottomPagerAdapter adapter = new CreationBottomPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, mPresenter.getFragmentList());
            mBinding.viewPager.setAdapter(adapter);
            mBinding.viewPager.setOffscreenPageLimit(3);
        } else {
            TemplateViewPager templateViewPager = new TemplateViewPager(mPresenter.getListForInitBottom());
            mBinding.viewPager.setAdapter(templateViewPager);
        }
    }

    /**
     * ?????? ?????????????????????
     */
    public void keepPicture() {
        dismissStickerFrame();

        new Handler().post(() -> {
            ScreenCaptureUtil screenCaptureUtil = new ScreenCaptureUtil(BaseApplication.getInstance());
            String textImagePath = screenCaptureUtil.getFilePath(mBinding.relativeContentAllContent2, mBinding.ivFrameImage);
            startPreviewFramePicture(textImagePath);
        });
    }

    /**
     * ??????????????????
     *
     * @param textImagePath path
     */
    private void startPreviewFramePicture(String textImagePath) {
        Intent intent = new Intent(mContext, DressUpPreviewActivity.class);
        intent.putExtra("url", textImagePath);
        intent.putExtra("template_id", templateId + "");
        intent.putExtra("localImage", textImagePath);
        intent.putExtra("isSpecial", true);
        intent.putExtra("templateTitle", "");
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "alert_edit_back_bj");

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
                        Runtime.getRuntime().gc();
                        finish();
                    }

                    @Override
                    public void onCancelBtnClick(CommonMessageDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .build().show();

        mPresenter.intoOnPause();
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
        mBinding.idVviewRealtimeGllayout.removeAllViews();
        EventBus.getDefault().unregister(this);
        mBinding.rlCreationContainer.getViewTreeObserver().removeOnGlobalLayoutListener(mGlobalLayoutListener);
        super.onDestroy();
    }

    /**
     * ???????????????intent
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

    @Override
    public void showLoading() {
        StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "load_video_post_bj");
        mLoadingDialog.show();
    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showToast(String msg) {

    }

    @Override
    public void showError() {

    }

    ImageView check_box_0;
    ImageView check_box_1;
    ImageView check_box_2;
    ImageView check_box_3;

    TextView tv_0;
    TextView tv_1;
    TextView tv_2;
    TextView tv_3;

    /**
     * ?????????????????????
     */
    public void initBottomLayout() {
        if (mFrom == CreationTemplateActivity.FROM_DRESS_UP_BACK_CODE) {
            initViewForChooseBack();
            initViewForChooseFrame();
            initViewForSticker();
        } else {
            initViewForSticker();
            initViewForChooseAnim();
            initViewForChooseMusic();
        }
        buildBottomViewPager();
    }


    /**
     * ?????????????????????view
     */
    private void initViewForChooseBack() {
        CreationBottomFragment fragment = new CreationBottomFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id", 0);
        fragment.setArguments(bundle);

        fragment.setFinishListener(this);
        fragment.setBackChooseListener(this);

        mPresenter.addFragmentList(fragment);
    }

    /**
     * ?????????????????????view
     */
    private void initViewForChooseFrame() {
        CreationBottomFragment fragment = new CreationBottomFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id", 1);
        fragment.setArguments(bundle);
        fragment.setFinishListener(this);
        fragment.setFrameChooseListener(this);
        mPresenter.addFragmentList(fragment);
    }

    ViewPager stickerViewPager;
    SlidingTabLayout stickerTab;

    /**
     * ?????????????????????view
     */
    public void initViewForSticker() {

        if (mFrom == CreationTemplateActivity.FROM_DRESS_UP_BACK_CODE) {
            CreationBottomFragment fragment = new CreationBottomFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("id", 2);
            fragment.setArguments(bundle);
            fragment.setStickerListener(this);
            fragment.setFinishListener(this);
            mPresenter.addFragmentList(fragment);
        } else {
            View templateThumbView = LayoutInflater.from(mContext).inflate(R.layout.view_template_paster, mBinding.viewPager, false);
            stickerViewPager = templateThumbView.findViewById(R.id.viewpager_sticker);

            templateThumbView.findViewById(R.id.iv_delete_sticker).setOnClickListener(v -> {
                clearSticker();
            });

            templateThumbView.findViewById(R.id.iv_down_sticker).setOnClickListener(v ->
                    stickerFragmentClose());

            stickerTab = templateThumbView.findViewById(R.id.tb_sticker);
            mPresenter.getStickerTypeList();
            mPresenter.addListForBottom(templateThumbView);
        }

    }

    TemplateGridViewAnimAdapter templateGridViewAnimAdapter;

    /**
     * ?????????????????????view
     */
    private void initViewForChooseAnim() {
        View viewForChooseAnim = LayoutInflater.from(mContext)
                .inflate(R.layout.view_create_template_anim_creation, mBinding.viewPager, false);
        GridView gridViewAnim = viewForChooseAnim.findViewById(R.id.gridView_anim);
        TextView animTab = viewForChooseAnim.findViewById(R.id.tv_name_bj_head);
        animTab.setText("??????");
        animTab.setTextSize(17);

        viewForChooseAnim.findViewById(R.id.iv_down_anim).setOnClickListener(v -> mPresenter.stickerFragmentClose());
        viewForChooseAnim.findViewById(R.id.iv_delete_anim).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.needPauseVideo();
                mPresenter.startPlayAnim(0, true, null, false);
                StatisticsEventAffair.getInstance().setFlag(mContext, "9_Animation2");
                StatisticsEventAffair.getInstance().setFlag(mContext, "9_Animation4");
            }
        });

        gridViewAnim.setOnItemClickListener((adapterView, view, i, l) -> {
            if (!DoubleClick.getInstance().isFastZDYDoubleClick(1000)) {
                mPresenter.modificationSingleAnimItemIsChecked(i);
                templateGridViewAnimAdapter.notifyDataSetChanged();
                mPresenter.needPauseVideo();
                WaitingDialog.openPragressDialog(mContext);
                mPresenter.startPlayAnim(i, false, null,  false);
            }
        });

        templateGridViewAnimAdapter = new TemplateGridViewAnimAdapter(mPresenter.getListAllAnim(), mContext);
        gridViewAnim.setAdapter(templateGridViewAnimAdapter);

        mPresenter.addListForBottom(viewForChooseAnim);
    }

    /**
     * ????????????????????????
     */
    private void initViewForChooseMusic() {
        //????????????
        View viewForChooseMusic = LayoutInflater.from(mContext).inflate(R.layout.view_choose_music, mBinding.viewPager, false);

        TextView tvAddMusic = viewForChooseMusic.findViewById(R.id.tv_add_music);
        TextView tvDownMusic = viewForChooseMusic.findViewById(R.id.iv_down_music);

        tvDownMusic.setVisibility(View.VISIBLE);
        tvDownMusic.setOnClickListener(v ->
                stickerFragmentClose());

        tvAddMusic.setOnClickListener(view -> {
            StatisticsEventAffair.getInstance().setFlag(mContext, "15_music_add");
            Intent intent = new Intent(mContext, ChooseMusicActivity.class);
            intent.putExtra("needDuration", mPresenter.getDuration());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(intent);
        });

        tv_0 = viewForChooseMusic.findViewById(R.id.tv_0);
        tv_1 = viewForChooseMusic.findViewById(R.id.tv_1);
        tv_2 = viewForChooseMusic.findViewById(R.id.tv_2);
        tv_3 = viewForChooseMusic.findViewById(R.id.tv_3);

        check_box_0 = viewForChooseMusic.findViewById(R.id.iv_check_box_0);
        check_box_1 = viewForChooseMusic.findViewById(R.id.iv_check_box_1);
        check_box_2 = viewForChooseMusic.findViewById(R.id.iv_check_box_2);
        check_box_3 = viewForChooseMusic.findViewById(R.id.iv_check_box_3);

        tv_1.setText("????????????");
        tv_2.setText("????????????");

        setOnViewClickListener();
        mPresenter.addListForBottom(viewForChooseMusic);

        mPresenter.chooseInitMusic();
    }

    @Override
    public void clearCheckBox() {
        mPresenter.setNowChooseMusicId(0);
        check_box_0.setImageResource(R.mipmap.template_btn_unselected);
        check_box_1.setImageResource(R.mipmap.template_btn_unselected);
        check_box_2.setImageResource(R.mipmap.template_btn_unselected);
        check_box_3.setImageResource(R.mipmap.template_btn_unselected);
    }

    @Override
    public void chooseCheckBox(int i) {
        switch (i) {
            case 0:
                check_box_0.setImageResource(R.mipmap.template_btn_selected);
                break;
            case 1:
                check_box_1.setImageResource(R.mipmap.template_btn_selected);
                break;
            case 2:
                check_box_2.setImageResource(R.mipmap.template_btn_selected);
                break;
            case 3:
                check_box_3.setImageResource(R.mipmap.template_btn_selected);
                break;
            default:
                break;
        }
    }

    @Override
    public void returnStickerTypeList(ArrayList<StickerTypeEntity> list) {
        List<Fragment> fragments = new ArrayList<>();
        String[] titles = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            titles[i] = list.get(i).getName();
            Bundle bundle = new Bundle();
            bundle.putInt("stickerType", list.get(i).getId());
            bundle.putInt("from", CreationTemplateActivity.FROM_CREATION_CODE);
            StickerFragment fragment = new StickerFragment();
            fragment.setStickerListener(this);
            fragment.setArguments(bundle);
            fragments.add(fragment);
        }

        home_vp_frg_adapter vp_frg_adapter = new home_vp_frg_adapter(getSupportFragmentManager(), fragments);

        stickerViewPager.setOffscreenPageLimit(list.size() - 1);
        stickerViewPager.setAdapter(vp_frg_adapter);
        stickerTab.setViewPager(stickerViewPager, titles);
    }

    private void setOnViewClickListener() {

        View.OnClickListener tvMusicListener = view -> {

            switch (view.getId()) {
                case R.id.iv_check_box_0:
                case R.id.tv_0:

                    mPresenter.chooseNowStickerMaterialMusic();
                    mPresenter.chooseMusicIndex(0);
                    break;

                case R.id.tv_1:
                case R.id.iv_check_box_1:
                    mPresenter.chooseMusicIndex(1);
                    mPresenter.chooseTemplateMusic(true);
                    break;

                case R.id.tv_2:
                case R.id.iv_check_box_2:
                    mPresenter.chooseMusicIndex(2);
                    mPresenter.setNowChooseMusicId(3);
                    mPresenter.chooseAddChooseBjPath();
                    break;

                case R.id.iv_check_box_3:
                case R.id.tv_3:
                    mPresenter.chooseMusicIndex(3);
                    clearCheckBox();
                    check_box_3.setImageResource(R.mipmap.template_btn_selected);
                    break;
                default:
                    break;
            }
        };

        tv_0.setOnClickListener(tvMusicListener);
        tv_1.setOnClickListener(tvMusicListener);
        tv_2.setOnClickListener(tvMusicListener);
        tv_3.setOnClickListener(tvMusicListener);

        check_box_0.setOnClickListener(tvMusicListener);
        check_box_1.setOnClickListener(tvMusicListener);
        check_box_2.setOnClickListener(tvMusicListener);
        check_box_3.setOnClickListener(tvMusicListener);
    }


    @Override
    public void addSticker(String stickerPath, String title) {
        mPresenter.addSticker(stickerPath, title);
    }

    @Override
    public void copyGif(String fileName, String copyName, String title) {
        mPresenter.copyGif(fileName, copyName, title);
    }

    @Override
    public void clickItemSelected(int position) {
        mPresenter.showAllAnim(false);
        mPresenter.needPauseVideo();
    }

    @Override
    public void chooseFrame(String title, String path) {
        if (TextUtils.isEmpty(path)) {
            StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "st_bj_frame", "?????????");
            clearImageFrame();
        } else {
            StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "st_bj_frame", title);
            mPresenter.chooseFrame(path);
        }
    }


    @Override
    public void onFinishClicked() {
        mPresenter.stickerFragmentClose();
    }

    @Override
    public void onClearClicked(int id) {
        if (id == 0) {

        } else if (id == 1) {
            clearImageFrame();
        } else {
            clearSticker();
        }
    }

    private void clearSticker() {
        mPresenter.stopAllAnim();
        closeAllAnim();
        mPresenter.deleteAllSticker();
        if (UiStep.isFromDownBj) {
            StatisticsEventAffair.getInstance().setFlag(mContext, " 5_mb_bj_Stickeroff");
        } else {
            StatisticsEventAffair.getInstance().setFlag(mContext, " 6_customize_bj_Stickeroff");
        }
    }

    @Override
    public void closeAllAnim() {
        //ArrayList<AllStickerData> list = new ArrayList<>();
        for (int i = 0; i < mBinding.idVviewRealtimeGllayout.getChildCount(); i++) {
            StickerView stickerView = (StickerView) mBinding.idVviewRealtimeGllayout.getChildAt(i);
            stickerView.pause();
        }
    }

    private void clearImageFrame() {
        dismissFrame();
    }
}

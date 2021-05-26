package com.flyingeffects.com.ui.view.activity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.TimelineAdapterForCutVideo;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.commonlyModel.getVideoInfo;
import com.flyingeffects.com.databinding.ActTemplateCutVideoBinding;
import com.flyingeffects.com.entity.VideoInfo;
import com.flyingeffects.com.manager.DataCleanManager;
import com.flyingeffects.com.ui.model.VideoMattingModel;
import com.flyingeffects.com.ui.model.initFaceSdkModel;
import com.flyingeffects.com.ui.model.videoCutDurationForVideoOneDo;
import com.flyingeffects.com.ui.view.dialog.LoadingDialog;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.view.MattingVideoEnity;
import com.glidebitmappool.GlideBitmapPool;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.shixing.sxve.ui.view.WaitingDialog;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;


/**
 * description ：只针对一键模板的视频裁剪页面
 * creation date: 2020/1/19
 * user : zhangtongju
 */
public class TemplateCutVideoActivity extends BaseActivity {
    private static final String TAG = "TemplateCutVideoActivity";

    private LoadingDialog mLoadingDialog;

    private TimelineAdapterForCutVideo mTimelineAdapter;

    private int mTotalWidth;

    /**
     * 整个视频时长
     */
    private int mVideoDuration;

    private float needDuration;

    /**
     * 视频源地址
     */
    private String videoPath;

    private String templateName;


    VideoInfo videoInfo;
    private int mScrollX;


    /**
     * 滑动后选择开始的位置 单位ms
     */
    private int mStartDuration;

    /**
     * 滑动后选择结束的位置,单位ms
     */
    private int mEndDuration;

    /**
     * 来自哪个页面，2来自模板想起切换视频素材  0表示预览选择视频入口页面
     */
    private int isFrom;

    private ExoPlayer exoPlayer;


    private boolean isIntoOnpause = false;


    private boolean nowActivityIsDestroy = false;


    private ActTemplateCutVideoBinding mBinding;


    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initView() {
        mBinding = ActTemplateCutVideoBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        isIntoOnpause = false;
        mLoadingDialog = buildLoadingDialog();
        getLifecycle().addObserver(mLoadingDialog);
        DataCleanManager.deleteFilesByDirectory(getExternalFilesDir("cacheMattingFolder"));
        videoPath = getIntent().getStringExtra("videoPath");
        needDuration = getIntent().getFloatExtra("needCropDuration", 1);
        templateName = getIntent().getStringExtra("templateName");
        LogUtil.d(TAG, "videoPath = " + videoPath);
        LogUtil.d(TAG, "needDuration = " + needDuration);
        isFrom = getIntent().getIntExtra("isFrom", 0);
        LogUtil.d(TAG, "isFrom = " + isFrom);
        boolean nowIsPhotographAlbum = getIntent().getBooleanExtra("nowIsPhotographAlbum", false);
        int picout = getIntent().getIntExtra("picout", 0);
        videoInfo = getVideoInfo.getInstance().getRingDuring(videoPath);
        mEndDuration = (int) (needDuration * 1000);
//        mBinding.duration.setText("模板时长 " + needDuration + "s");
        //0 表示不需要抠图，1 表示需要抠图
        if (picout == 0 || nowIsPhotographAlbum) {
            mBinding.tvKt.setVisibility(View.GONE);
            mBinding.tvNoKt.setText("下一步");
        }
        mBinding.ivClose.setOnClickListener(this::onViewClick);
        mBinding.ivCorrect.setOnClickListener(this::onViewClick);
        mBinding.tvKt.setOnClickListener(this::onViewClick);
        mBinding.tvNoKt.setOnClickListener(this::onViewClick);
        mBinding.ivBack.setOnClickListener(this::onViewClick);
    }

    private LoadingDialog buildLoadingDialog() {
        return LoadingDialog.getBuilder(this)
                .setHasAd(true)
                .setTitle("飞闪预览处理中")
                .setMessage("请耐心等待，不要离开")
                .build();
    }

    @Override
    protected void initAction() {
        initThumbList();
        mBinding.listThumb.post(() -> initSingleThumbSize(videoInfo.getVideoWidth(), videoInfo.getVideoHeight(), needDuration, videoInfo.getDuration() / (float) 1000, videoPath));
    }


    private void initExo(String videoPath, float duration) {
        mBinding.duration.setText(String.format(Locale.US, "%.1fs", duration));
        if (TextUtils.isEmpty(videoPath)) {
            return;
        }
        LogUtil.d("OOM2", "videoPath="+videoPath);
        exoPlayer = ExoPlayerFactory.newSimpleInstance(BaseApplication.getInstance(), new DefaultRenderersFactory(this), new DefaultTrackSelector(), new DefaultLoadControl());
        mBinding.exoPlayer.setPlayer(exoPlayer);
        //不使用控制器
        mBinding.exoPlayer.setUseController(false);
        exoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
        exoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case Player.STATE_READY:
                        LogUtil.d("OOM2", "STATE_READY");
                        if (!isIntoOnpause) {
                            videoPlay();
                        }
                        break;
                    case Player.STATE_ENDED:
                        LogUtil.d("OOM2", "STATE_ENDED");
                        seekTo(0);
                        break;
                    case Player.STATE_BUFFERING:
                    case Player.STATE_IDLE:
                        LogUtil.d("OOM2", "STATE_BUFFERING-STATE_IDLE");
//                        new Handler().postDelayed(() -> {
//                            if (!isIntoOnpause) {
//                                videoPlay();
//                            }
//                        },500);
                    default:
                        break;
                }
            }
        });
        MediaSource mediaSource = new ExtractorMediaSource.Factory(
                new DefaultDataSourceFactory(TemplateCutVideoActivity.this, "exoplayer-codelab")).
                createMediaSource(Uri.fromFile(new File(videoPath)));
        exoPlayer.prepare(mediaSource, true, false);
        videoPause();
        LogUtil.d("OOM2", "initEnd--");
    }

    /**
     * 开始播放
     */
    private void videoPlay() {
        if (exoPlayer != null) {
            LogUtil.d("video", "play");
            exoPlayer.setPlayWhenReady(true);
        }
        startTimer();
    }

    private void videoPause() {
        if (exoPlayer != null) {
            LogUtil.d("OOM2", "videoPause");
            exoPlayer.setPlayWhenReady(false);
            endTimer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initExo(videoPath, needDuration);
    }


    public void onViewClick(View view) {
        if (view == mBinding.ivClose || view == mBinding.ivBack) {
            this.finish();
        } else if (view == mBinding.ivCorrect) {
            File file = new File(videoPath);
            if (file.exists()) {
                WaitingDialog.openPragressDialog(this);
            } else {
                ToastUtil.showToast(getResources().getString(R.string.write_error));
            }
        } else if (view == mBinding.tvKt) {
            if (!isFastDoubleClick()) {
                videoStop();
                endTimer();
                if (!nowActivityIsDestroy) {
                    showLoadingDialog();
                }
                ktStart(true);
            }
        } else if (view == mBinding.tvNoKt) {
            if (!isFastDoubleClick()) {
                videoStop();
                endTimer();
                if (!nowActivityIsDestroy) {
                    showLoadingDialog();
                }
                ktStart(false);
            }
        }
    }


    private void showLoadingDialog() {
        mLoadingDialog.show();
    }

    private void ktStart(boolean isKt) {
        long duration = (long) (needDuration * 1000);
        new Thread(() -> videoCutDurationForVideoOneDo.getInstance().cutVideoForDrawPadAllExecute2(TemplateCutVideoActivity.this, false, duration, videoPath, mStartDuration, new videoCutDurationForVideoOneDo.isSuccess() {
            @Override
            public void progresss(int progress) {
                if (!nowActivityIsDestroy) {
                    mLoadingDialog.setTitleStr("正在裁剪中");
                    mLoadingDialog.setProgress(progress);
                }
            }

            @Override
            public void isSuccess(boolean isSuccess, String path) {
                if (isSuccess) {
                    if (isKt) {
                        gotoMattingVideo(path);
                        Observable.just(isSuccess).subscribeOn(AndroidSchedulers.mainThread()).subscribe(aBoolean -> new Handler().postDelayed(() -> {
                            if (!nowActivityIsDestroy) {
                                dismissLoadingDialog();
                            }
                        }, 500));
                    } else {
                        if (!nowActivityIsDestroy) {
                            dismissLoadingDialog();
                        }
                        EventBus.getDefault().post(new MattingVideoEnity(null, path, path, isFrom));
                        TemplateCutVideoActivity.this.finish();
                    }
                }
            }
        })).start();

    }

    private void dismissLoadingDialog() {
        mLoadingDialog.dismiss();
    }


    @Override
    protected void onPause() {
        super.onPause();
        videoStop();
        endTimer();
        isIntoOnpause = true;
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

    private VideoMattingModel videoMattingModel;

    private void gotoMattingVideo(String originalPath) {
        initFaceSdkModel.getHasLoadSdkOk(() -> Observable.just(originalPath).subscribeOn(AndroidSchedulers.mainThread()).subscribe(s -> {
            videoMattingModel = new VideoMattingModel(originalPath, TemplateCutVideoActivity.this, (isSuccess, path, noMakingPath) -> {
                EventBus.getDefault().post(new MattingVideoEnity(noMakingPath, path, originalPath, isFrom));
                TemplateCutVideoActivity.this.finish();
            });
            videoMattingModel.ToExtractFrame(templateName);
        }), this);
    }


    private void initThumbList() {
        mBinding.listThumb.setLayoutManager(new LinearLayoutManager(TemplateCutVideoActivity.this, LinearLayoutManager.HORIZONTAL, false));
        mTimelineAdapter = new TimelineAdapterForCutVideo();
        mBinding.listThumb.setAdapter(mTimelineAdapter);
        mBinding.listThumb.setHasFixedSize(true);
        mBinding.listThumb.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NotNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    float percent = (float) mScrollX / mTotalWidth;
                    mStartDuration = (int) (mVideoDuration * percent) * 1000;
                    mEndDuration = (int) (mStartDuration + (needDuration * 1000));
                    LogUtil.d("OOM", "mStartDuration=" + mStartDuration + "mEndDuration=" + mEndDuration);
                    seekTo(mStartDuration);
                    startTimer();
                }
            }

            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                mScrollX += dx;
            }
        });

    }

    private void seekTo(long to) {
        if (exoPlayer != null) {
            exoPlayer.seekTo(to);
        }
    }


    /**
     * description ：
     * creation date: 2020/1/20
     * param : height 视频高度  width 视频宽度   duration总时长  mTemplateDuration需要的时长  mVideoPath视频地址
     * user : zhangtongju
     */
    private void initSingleThumbSize(int width, int height, float mTemplateDuration, float duration, String mVideoPath) {
        this.mVideoDuration = (int) duration;
        // 需要截取的listWidth宽度
        int listWidth = mBinding.listThumb.getWidth() - mBinding.listThumb.getPaddingLeft() - mBinding.listThumb.getPaddingRight();
        int listHeight = mBinding.listThumb.getHeight();
        float scale = (float) listHeight / height;
        int thumbWidth = (int) (scale * width);
        mTimelineAdapter.setBitmapSize(thumbWidth, listHeight);
        //其中listWidth表示当前截取的大小
        int thumbCount = (int) (listWidth * (duration / mTemplateDuration / thumbWidth));
        thumbCount = Math.max(thumbCount, 0);
        //每帧所占的时间
        final int interval = (int) (duration / thumbCount * 1000);
        int[] mTimeUs = new int[thumbCount];
        for (int i = 0; i < thumbCount; i++) {
            mTimeUs[i] = i * interval;
        }
        HashMap<Integer, Bitmap> mData = new HashMap<>();
        mTimelineAdapter.setVideoUri(Uri.fromFile(new File(mVideoPath)));
        mTimelineAdapter.setData(mTimeUs, mData);
        mTotalWidth = thumbWidth * thumbCount;
    }

    /**
     * 关闭timer 和task
     */
    private void endTimer() {
        destroyTimer();
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

    private Timer timer;
    private TimerTask task;

    private void startTimer() {
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
                Observable.just(1).observeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
                    if (exoPlayer != null) {
                        exoPlayer.setPlayWhenReady(true);
                        if (getCurrentPos() >= mEndDuration) {
                            exoPlayer.seekTo(mStartDuration);
                        } else if (getCurrentPos() < mStartDuration) {
                            exoPlayer.seekTo(mStartDuration);
                        }

                    }
                });
            }
        };
        timer.schedule(task, 0, 16);
    }


    /**
     * 获取当前进度
     */
    private long getCurrentPos() {
        return exoPlayer != null ? exoPlayer.getCurrentPosition() : 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (videoMattingModel != null) {
            videoMattingModel.nowActivityIsDestroy(true);
        }
        nowActivityIsDestroy = true;
        exoPlayer = null;
        videoStop();
        GlideBitmapPool.clearMemory();
        endTimer();
    }
}

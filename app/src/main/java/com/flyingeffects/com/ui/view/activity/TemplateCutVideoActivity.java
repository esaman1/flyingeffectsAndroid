package com.flyingeffects.com.ui.view.activity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.TimelineAdapterForCutVideo;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.commonlyModel.getVideoInfo;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.VideoInfo;
import com.flyingeffects.com.manager.DataCleanManager;
import com.flyingeffects.com.ui.model.VideoMattingModel;
import com.flyingeffects.com.ui.model.videoCutDurationForVideoOneDo;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.utils.faceUtil.ConUtil;
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
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.megvii.segjni.SegJni;
import com.shixing.sxve.ui.view.WaitingDialog;
import com.shixing.sxve.ui.view.WaitingDialogProgressNowAnim;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;


/**
 * description ：只针对一键模板的视频裁剪页面
 * creation date: 2020/1/19
 * user : zhangtongju
 */
public class TemplateCutVideoActivity extends BaseActivity {

    @BindView(R.id.tv_kt)
    TextView tv_kt;

    @BindView(R.id.tv_no_kt)
    TextView tv_no_kt;

    @BindView(R.id.list_thumb)
    RecyclerView list_thumb;

    private WaitingDialogProgressNowAnim progressNowAnim;

    private TimelineAdapterForCutVideo mTimelineAdapter;


    @BindView(R.id.relative_select_duration)
    RelativeLayout relative_select_duration;

    @BindView(R.id.duration)
    TextView tv_duration;

    @BindView(R.id.view_line_progress)
    View view_line_progress;

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

    //0 表示不需要抠图，1 表示需要抠图
    private int picout;


    @BindView(R.id.exo_player)
    PlayerView playerView;

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


    private boolean nowActivityIsDestroy = false;

    @Override
    protected int getLayoutId() {
        return R.layout.act_template_cut_video;
    }

    @Override
    protected void initView() {
        progressNowAnim = new WaitingDialogProgressNowAnim(this);
        DataCleanManager.deleteFilesByDirectory(getExternalFilesDir("cacheMattingFolder"));
        videoPath = getIntent().getStringExtra("videoPath");
        needDuration = getIntent().getFloatExtra("needCropDuration", 1);
        templateName = getIntent().getStringExtra("templateName");
        isFrom = getIntent().getIntExtra("isFrom", 0);
        picout = getIntent().getIntExtra("picout", 0);
        videoInfo = getVideoInfo.getInstance().getRingDuring(videoPath);
        mEndDuration = (int) (needDuration * 1000);
        tv_duration.setText("模板时长 " + needDuration + "s");
        if (picout == 0) {
            tv_kt.setVisibility(View.GONE);
            tv_no_kt.setText("下一步");
        }
    }

    @Override
    protected void initAction() {
        initThumbList();
        list_thumb.post(() -> initSingleThumbSize(videoInfo.getVideoWidth(), videoInfo.getVideoHeight(), needDuration, videoInfo.getDuration() / (float) 1000, videoPath));

    }


    private void startVideo() {
    }


    private void initExo(String videoPath, float duration) {
        tv_duration.setText(String.format(Locale.US, "%.1fs", duration));
        if (TextUtils.isEmpty(videoPath)) {
            return;
        }
        exoPlayer = ExoPlayerFactory.newSimpleInstance(TemplateCutVideoActivity.this, new DefaultRenderersFactory(this), new DefaultTrackSelector(), new DefaultLoadControl());
        playerView.setPlayer(exoPlayer);

        //不使用控制器
        playerView.setUseController(false);
        exoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
        exoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case Player.STATE_READY:
                        videoPlay();
                        break;
                    case Player.STATE_ENDED:
                        seekTo(0);
                        break;
                    case Player.STATE_BUFFERING:
                    case Player.STATE_IDLE:
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
            LogUtil.d("video", "videoPause");
            exoPlayer.setPlayWhenReady(false);
            endTimer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initExo(videoPath, needDuration);

    }

    @OnClick({R.id.iv_close, R.id.iv_correct, R.id.tv_kt, R.id.tv_no_kt, R.id.iv_back})
    public void onMyClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
            case R.id.iv_back:
                this.finish();
                break;


            case R.id.iv_correct:

                File file = new File(videoPath);
                if (file.exists()) {
                    WaitingDialog.openPragressDialog(this);
                } else {
                    ToastUtil.showToast(getResources().getString(R.string.write_error));
                }


                break;

            case R.id.tv_kt:
            case R.id.tv_no_kt:
                videoStop();
                endTimer();
                if(!nowActivityIsDestroy){
                    progressNowAnim.openProgressDialog();
                }
                new Thread(() -> videoCutDurationForVideoOneDo.getInstance().CutVideoForDrawPadAllExecute2(TemplateCutVideoActivity.this, needDuration * 1000, videoPath, mStartDuration, new videoCutDurationForVideoOneDo.isSuccess() {
                    @Override
                    public void progresss(int progress) {
                        if (!nowActivityIsDestroy) {
                            progressNowAnim.setProgress("正在裁剪中" + progress + "%");
                        }

                    }

                    @Override
                    public void isSuccess(boolean isSuccess, String path) {
                        if (!nowActivityIsDestroy) {
                            progressNowAnim.closePragressDialog();
                        }
                        if (isSuccess) {
                            if (v.getId() == R.id.tv_kt) {
                                gotoMattingVideo(path);
                            } else {
                                EventBus.getDefault().post(new MattingVideoEnity(null, path,path, isFrom));
                                TemplateCutVideoActivity.this.finish();
                            }
                        }

                    }
                })).start();
                break;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        videoStop();
        endTimer();
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
        SegJni.nativeCreateSegHandler(this, ConUtil.getFileContent(this, R.raw.megviisegment_model), BaseConstans.THREADCOUNT);
        Observable.just(originalPath).subscribeOn(AndroidSchedulers.mainThread()).subscribe(s -> {
            videoMattingModel  = new VideoMattingModel(originalPath, TemplateCutVideoActivity.this, (isSuccess, path,noMakingPath) -> {
                EventBus.getDefault().post(new MattingVideoEnity(noMakingPath, path,originalPath, isFrom));
                TemplateCutVideoActivity.this.finish();
            });
            videoMattingModel.ToExtractFrame(templateName);
        });
    }


    private void initThumbList() {
        list_thumb.setLayoutManager(new LinearLayoutManager(TemplateCutVideoActivity.this, LinearLayoutManager.HORIZONTAL, false));
        mTimelineAdapter = new TimelineAdapterForCutVideo();
        list_thumb.setAdapter(mTimelineAdapter);
        list_thumb.setHasFixedSize(true);
        list_thumb.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        int listWidth = list_thumb.getWidth() - list_thumb.getPaddingLeft() - list_thumb.getPaddingRight();
        int listHeight = list_thumb.getHeight();
        float scale = (float) listHeight / height;
        int thumbWidth = (int) (scale * width);
        mTimelineAdapter.setBitmapSize(thumbWidth, listHeight);
        //其中listWidth表示当前截取的大小
        int thumbCount = (int) (listWidth * (duration / mTemplateDuration / thumbWidth));
//        int thumbCount = (int) (listWidth * (duration / mTemplateDuration) / thumbWidth);
        thumbCount = thumbCount > 0 ? thumbCount : 0;
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
        startVideo();
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
        nowActivityIsDestroy = true;
        if(videoMattingModel!=null){
            videoMattingModel.nowActivityIsDestroy(true);
        }
        videoStop();
        GlideBitmapPool.clearMemory();
        endTimer();
    }
}

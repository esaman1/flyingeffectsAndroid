package com.flyingeffects.com.ui.view.activity;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.TimelineAdapterForCutVideo;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.commonlyModel.getVideoInfo;
import com.flyingeffects.com.enity.VideoInfo;
import com.flyingeffects.com.enity.WxLogin;
import com.flyingeffects.com.manager.AlbumManager;
import com.flyingeffects.com.ui.interfaces.VideoPlayerCallbackForTemplate;
import com.flyingeffects.com.ui.model.VideoMattingModel;
import com.flyingeffects.com.ui.model.videoCutDurationForVideoOneDo;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.view.EmptyControlVideo;
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
import com.shixing.sxve.ui.adapter.TimelineAdapter;
import com.shixing.sxve.ui.view.WaitingDialog;
import com.shixing.sxve.ui.view.WaitingDialog_progress;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
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

    @BindView(R.id.list_thumb)
    RecyclerView list_thumb;

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


    @BindView(R.id.video_player)
    EmptyControlVideo videoPlayer;

    VideoInfo videoInfo;


    @Override
    protected int getLayoutId() {
        return R.layout.act_template_cut_video;
    }

    @Override
    protected void initView() {
        videoPath = getIntent().getStringExtra("videoPath");
        needDuration = getIntent().getFloatExtra("needCropDuration", 1);
        videoInfo = getVideoInfo.getInstance().getRingDuring(videoPath);
        videoPlayer.setUp(videoPath, true, "");
        videoPlayer.startPlayLogic();
        videoPlayer.setVideoAllCallBack(new VideoPlayerCallbackForTemplate(isSuccess -> {
            videoPlayer.startPlayLogic();
        }));
    }

    @Override
    protected void initAction() {
        initThumbList();
        list_thumb.post(() -> initSingleThumbSize(videoInfo.getVideoWidth(), videoInfo.getVideoHeight(), videoInfo.getDuration() / (float) 1000, needDuration, videoPath));
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @OnClick({R.id.iv_close, R.id.iv_correct, R.id.tv_kt})
    public void onMyClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                this.finish();
                break;


            case R.id.iv_correct:
                File file = new File(videoPath);
                if (file.exists()) {
                    WaitingDialog.openPragressDialog(this);
//                    next(videoPath,  duration, mStartDuration / 1000f, (int)videoFps);
                } else {
                    ToastUtil.showToast(getResources().getString(R.string.write_error));
//                    videoStop();
                    endTimer();
                }
                break;

            case R.id.tv_kt:
                videoPlayer.onVideoPause();
                videoCutDurationForVideoOneDo.getInstance().CutVideoForDrawPadAllExecute2(this, needDuration * 1000, videoPath, new videoCutDurationForVideoOneDo.isSuccess() {
                    @Override
                    public void progresss(int progress) {
                        LogUtil.d("OOM", "progress=" + progress);
                    }

                    @Override
                    public void isSuccess(boolean isSuccess, String path) {
                        LogUtil.d("OOM", "裁剪成功后路径 720*1280="+path);
                        if(isSuccess){

                            gotoMattingVideo(path);
                        }
                    }
                });


                break;

        }

    }


    private void gotoMattingVideo(String originalPath) {
        VideoMattingModel videoMattingModel = new VideoMattingModel(originalPath, TemplateCutVideoActivity.this, new VideoMattingModel.MattingSuccess() {
            @Override
            public void isSuccess(boolean isSuccess, String path) {
                EventBus.getDefault().post(new MattingVideoEnity(originalPath, path));
            }
        });
        videoMattingModel.newFunction();
    }


    private void next(String mVideoPath, float mTemplateDuration, float startTime, int fps) {
//        videoCut_outModel cut_outModel = new videoCut_outModel(TemplateCutVideoActivity.this);
//        Observable.just(mVideoPath).subscribeOn(Schedulers.io()).subscribe(s -> cut_outModel.Cut_outVideo(s, 0, 0, mTemplateDuration, null, startTime, fps, (path, success) -> {
//            WatingDilog.closePragressDialog();
//            EventBus.getDefault().post(new templtateCutVideoEnity(path));  //消息通知
//            this.finish();
//        }));
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
                }
            }

            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
            }
        });

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
        int thumbCount = (int) (listWidth * (duration / mTemplateDuration) / thumbWidth);
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

    }


    /**
     * description ：添加滑动效果
     * creation date: 2020/1/20
     * user : zhangtongju
     */
    int relativeSelectDurationW;

    private void seekProgress(float progress) {
        LogUtil.d("OOM", "progress=" + progress);
        if (relativeSelectDurationW == 0) {
            relativeSelectDurationW = relative_select_duration.getMeasuredWidth();
        }
        int marginProgress = (int) (progress * relativeSelectDurationW);
        setMargins(view_line_progress, marginProgress, 0, 0, 0);
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


    private int sourceVideoWidth;
    private int sourceVideoHeight;


    private void getSourceVideoWidthAndHeight(String path) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        File file = new File(path);
        if (file.exists() && file.length() > 0) {
            FileInputStream inputStream;
            try {
                inputStream = new FileInputStream(new File(path).getAbsolutePath());
                //设置数据源为该文件对象指定的绝对路径
                mmr.setDataSource(inputStream.getFD());
                if (mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION) != null) {
                    int videoRotation = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
                    String width = (videoRotation == 90 || videoRotation == 270) ? mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT) : mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                    String height = (videoRotation == 90 || videoRotation == 270) ? mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH) : mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                    if (!TextUtils.isEmpty(width)) {
                        sourceVideoWidth = Integer.parseInt(width);
                    }
                    if (!TextUtils.isEmpty(height)) {
                        sourceVideoHeight = Integer.parseInt(height);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


//    /**
//     * 获取当前进度
//     */
//    private long getCurrentPos() {
//        return exoPlayer != null ? exoPlayer.getCurrentPosition() : 0;
//    }


    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }


    private Timer timer;
    private TimerTask task;
    private long correctValue;

//    private void startTimer() {
//        if (timer != null) {
//            timer.purge();
//            timer.cancel();
//            timer = null;
//        }
//        if (task != null) {
//            task.cancel();
//            task = null;
//        }
//
//
//        if (mEndDuration - getCurrentPos() != mEndDuration) {
//            correctValue = mStartDuration;
//        } else {
//            correctValue = getCurrentPos();
//        }
//        LogUtil.d("OOM", "correctValue" + correctValue);
//
//        timer = new Timer();
//        task = new TimerTask() {
//            @Override
//            public void run() {
//                Observable.just(1).observeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
//                    if (exoPlayer != null) {
//                        exoPlayer.setPlayWhenReady(true);
//                        if (getCurrentPos() >= mEndDuration) {
//                            exoPlayer.seekTo(mStartDuration);
//                        } else if (getCurrentPos() < mStartDuration) {
//                            exoPlayer.seekTo(mStartDuration);
//                        }
//
//                        seekProgress((getCurrentPos() - correctValue) / ((float) mEndDuration - correctValue));
//                    }
//                });
//            }
//        };
//        timer.schedule(task, 0, 16);
//    }
//
//    /**
//     * 释放资源
//     */
//    private void videoStop() {
//        if (exoPlayer != null) {
//            exoPlayer.stop();
//            exoPlayer.release();
//        }
//    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        videoStop();
        GlideBitmapPool.clearMemory();
        endTimer();
    }
}

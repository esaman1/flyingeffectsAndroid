package com.flyingeffects.com.ui.model;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.VideoTimelineAdapter;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.model.VideoCropMVPCallback;
import com.flyingeffects.com.utils.FileUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.utils.screenUtil;
import com.flyingeffects.com.view.RangeSeekBarView;
import com.flyingeffects.com.view.RoundImageView;
import com.flyingeffects.com.view.VideoFrameRecycler;
import com.flyingeffects.com.view.beans.Thumb;
import com.flyingeffects.com.view.interfaces.OnProgressVideoListener;
import com.flyingeffects.com.view.interfaces.OnRangeSeekBarListener;
import com.lansosdk.LanSongFilter.LanSongBlurFilter;
import com.lansosdk.LanSongFilter.LanSongFilter;
import com.lansosdk.box.DrawPadUpdateMode;
import com.lansosdk.box.LSOVideoOption;
import com.lansosdk.box.SubLayer;
import com.lansosdk.box.VideoFrameLayer;
import com.lansosdk.box.VideoLayer;
import com.lansosdk.box.onDrawPadCompletedListener;
import com.lansosdk.videoeditor.DrawPadAllExecute2;
import com.lansosdk.videoeditor.DrawPadView2;
import com.lansosdk.videoeditor.MediaInfo;
import com.shixing.sxve.ui.view.WaitingDialog_progress;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class VideoCropMVPModel {
    private Context mContext;
    private VideoCropMVPCallback callback;
    private DrawPadView2 drawPadView;
    private static int FRAME_RATE = 30;
    private static final int DRAWPAD_WIDTH = 720;
    private static final int DRAWPAD_HEIGHT = 1280;
    private int padRealWidth;
    private int padRealHeight;
    private SubLayer mainLayer;
    private VideoLayer backgroundLayer;
    private String videoPath;
    private float vLayerW;
    private float vLayerH;
    private MediaPlayer player;
    private RangeSeekBarView mRangeSeekBarView;
    private VideoFrameRecycler mTimeLineView;
    private VideoTimelineAdapter frameAdapter;
    private RoundImageView cursor;
    private Vibrator vibrator;

    public VideoCropMVPModel(Context context, VideoCropMVPCallback callback) {
        this.mContext = context;
        this.callback = callback;
    }

    public void initDrawpad(DrawPadView2 drawPadView, String path) {
        this.drawPadView = drawPadView;
        this.videoPath = path;
        drawPadView.setUpdateMode(DrawPadUpdateMode.AUTO_FLUSH, FRAME_RATE);
        drawPadView.setOnDrawPadCompletedListener(completeListener);
        drawPadView.setDrawPadSize(DRAWPAD_WIDTH, DRAWPAD_HEIGHT, (i, i1) -> {
            padRealWidth = i;
            padRealHeight = i1;
          //  changeVideoZoom(50);
        });
        drawPadView.setOnDrawPadRecordProgressListener((drawPad, currentTimeUs) -> {
            LogUtil.d("drawPadView", "currentTimeUs=" + currentTimeUs);
        });
        drawPadView.setOnViewAvailable(drawpadView -> {
            initVideo(drawPadView);
        });
        drawPadView.setOnTouchListener(drawpadTouchListener);
    }

    @SuppressLint("ClickableViewAccessibility")
    private View.OnTouchListener drawpadTouchListener = (v, event) -> {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (isPlaying()) {
                videoPause();
            } else {
                videoResume();
            }
        }
        return true;
    };
    private onDrawPadCompletedListener completeListener = drawPad -> {
        videoPause();
        drawPad.pausePreviewDrawPad();
    };

    private void clearDrawpad() {
        if (mainLayer != null) {
            drawPadView.removeLayer(mainLayer);
            mainLayer = null;
        }
        if (backgroundLayer != null) {
            drawPadView.removeLayer(backgroundLayer);
            backgroundLayer = null;
        }
    }

    //每100毫秒更新一次进度指针
    private static final int updateCursorIntervalMs = 100;
    private float curOffset = 1;
    private float videoRatio = 1;
    private Subscription timer;

    private void initVideo(DrawPadView2 drawPadView) {
        if (drawPadView.setupDrawPad()) {
            //暂停drawpad添加层
            drawPadView.pausePreview();
            clearDrawpad();
            //添加背景
            //添加视频
            if (!videoPath.trim().isEmpty()) {
                MediaInfo videoInfo = new MediaInfo(videoPath);
                if (videoInfo.prepare()) {
                    if (padRealHeight == 0) {
                        padRealWidth = drawPadView.getDrawPadWidth();
                        padRealHeight = drawPadView.getDrawPadHeight();
                    }
                    FRAME_RATE = Math.round(videoInfo.vFrameRate);
                    videoRatio = 1f * videoInfo.getWidth() / videoInfo.getHeight();
                    vLayerW = drawPadView.getDrawPadWidth();
                    vLayerH = vLayerW / videoRatio;
//                    LanSongFilter filter = new LanSongBlurFilter();
                    backgroundLayer = drawPadView.addVideoLayer(padRealWidth, padRealHeight, null);
                    backgroundLayer.setScaledValue(padRealHeight * videoRatio, padRealHeight);
                    backgroundLayer.setPosition(drawPadView.getDrawPadWidth() * 0.5f, drawPadView.getDrawPadHeight() * 0.5f);
                    if (backgroundLayer != null) {
//                        mainLayer = backgroundLayer.addSubLayer();
//                        if (mainLayer != null) {
//                            mainLayer.setScaledValue(vLayerW, vLayerH);
//                            mainLayer.setPosition(drawPadView.getDrawPadWidth() * 0.5f, drawPadView.getDrawPadHeight() * 0.5f);
//                        }
                        player = new MediaPlayer();
                        player.setLooping(false);
                        try {
                            player.setDataSource(videoPath);
                            player.prepareAsync();
                            player.setOnCompletionListener(mp -> {
                                callback.hideCursor();
                            });
                            player.setOnPreparedListener(videoPlayer -> {
                                callback.initTrimmer();
                                if (backgroundLayer != null) {
                                    player.setSurface(new Surface(backgroundLayer.getVideoTexture()));
                                    startMedia();
                                    initTimer(updateCursorIntervalMs);
                                    //层添加完毕，启动drawpad
                                    drawPadView.resumePreview();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }


                }

            }

        }
    }

    private void initTimer(long period) {
        destroyTimer();
        timer = Observable.interval(period, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (cursor.getTranslationX() > mRangeSeekBarView.getThumbs().get(1).getPos()) {
                        seekTo(Math.round(cropStartRatio * getDuration()));
                        LogUtil.d("autoSeek", cropEndRatio + "of duration");
                    } else {
                        updateCursor(false);
                    }
                }, throwable -> {

                });
    }

    private void destroyTimer() {
        if (timer != null && !timer.isUnsubscribed()) {
            timer.unsubscribe();
            timer = null;
        }
    }

    private void startMedia() {
        try {
            if (player != null) {
                player.start();
            }
            backgroundLayer.resumeForExecute();
        } catch (IllegalStateException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 跳转到
     *
     * @param to
     */
    private void seekTo(long to) {
        try {
            player.seekTo((int) to);
            updateCursor(true);
            initTimer(updateCursorIntervalMs);
            callback.showCursor();
            if (!player.isPlaying()) {
                player.start();
            }

        } catch (NullPointerException | IllegalStateException e) {
        }
    }

    private float currentCursorStart = 0;

    private void updateCursor(boolean isSeek) {
        if (mRangeSeekBarView != null && cursor != null) {
            if (isSeek) {
                currentCursorStart = mRangeSeekBarView.getThumbs().get(0).getWidthBitmap() + mRangeSeekBarView.getThumbs().get(0).getPos();
                callback.updateCursor(currentCursorStart);
            } else {
                callback.updateCursor(currentCursorStart += curOffset);
            }
        }
    }

    /**
     * 获取总时长
     *
     * @return
     */
    public long getDuration() {
        try {
            if (player != null) {
                return player.getDuration();
            }
        } catch (NullPointerException | IllegalStateException e) {
        }
        return 0;
//        return mediaController != null ? mediaController.getDuration() : 0;
    }

    /**
     * 暂停播放
     */
    private void videoPause() {
        try {
            if (player != null) {
                player.pause();
                callback.hideCursor();
                destroyTimer();
            }
        } catch (NullPointerException | IllegalStateException e) {
        }
    }

    private void videoResume() {
        try {
            if (player != null) {
                player.start();
                callback.showCursor();
                initTimer(updateCursorIntervalMs);
            }
        } catch (NullPointerException | IllegalStateException e) {
        }
    }

    /**
     * 是否正在播放
     *
     * @return
     */
    private boolean isPlaying() {
        try {
            if (player != null) {
                return player.isPlaying();
            }
        } catch (NullPointerException | IllegalStateException e) {
        }
        return false;
    }

    private float mScale = 1f;
    private long mLastZoomTime = 0;
    public static final long ZOOM_INTERVAL = 1000L;

    public void changeVideoZoom(int progress) {
        //统计
        long nowTime = System.currentTimeMillis();
        if (nowTime - mLastZoomTime > TIME_INTERVAL) {
            mLastZoomTime = nowTime;
            statisticsEventAffair.getInstance().setFlag(mContext, "2_Titles_zoom", "手动卡点_片头缩放");
            if (62 <= progress && progress <= 63) {
                statisticsEventAffair.getInstance().setFlag(mContext, "2_Titles_zoom_dy", "手动卡点_片头缩放_抖音");
            }
        }
        if (backgroundLayer != null) {
            if (progress <= 50) {
                mScale = 0.5f + 0.01f * progress;
                mainLayer.setScaledValue(vLayerW * mScale, vLayerH * mScale);
                backgroundLayer.setPosition(drawPadView.getViewWidth() * 0.5f, drawPadView.getViewHeight() * 0.5f);
                mainLayer.setPosition(drawPadView.getViewWidth() * 0.5f, drawPadView.getViewHeight() * 0.5f);
                if (progress == 50) {
                    vibrate();
                }
            } else {
                mScale = 1f + 0.02f * (progress - 50);
                mainLayer.setScaledValue(vLayerW * mScale, vLayerH * mScale);
                backgroundLayer.setPosition(drawPadView.getViewWidth() * 0.5f, drawPadView.getViewHeight() * 0.5f);
                mainLayer.setPosition(drawPadView.getViewWidth() * 0.5f, drawPadView.getViewHeight() * 0.5f);

                if (62 <= progress && progress <= 63) {
                    vibrate();
                }
            }
        }
    }

    private long mLastVibrateTime = 0;
    public static final long TIME_INTERVAL = 1000L;

    private void vibrate() {
        if (vibrator != null && vibrator.hasVibrator()) {
            long nowTime = System.currentTimeMillis();
            if (nowTime - mLastVibrateTime > TIME_INTERVAL) {
                // do something
                mLastVibrateTime = nowTime;
                vibrator.vibrate(50);
            }
        }
    }

    private List<OnProgressVideoListener> mListeners;

    private void setUpListeners(RangeSeekBarView mRangeSeekBarView) {
        mListeners = new ArrayList<>();
        mListeners.add((time, max, scale) -> {
        });
        mRangeSeekBarView.addOnRangeSeekBarListener(new OnRangeSeekBarListener() {
            @Override
            public void onCreate(RangeSeekBarView rangeSeekBarView, int index, float value) {
                // Do nothing
            }

            @Override
            public void onSeek(RangeSeekBarView rangeSeekBarView, int index, float value) {
                onSeekThumbs(index, value);
            }

            @Override
            public void onSeekStart(RangeSeekBarView rangeSeekBarView, int index, float value) {
                // Do nothing
            }

            @Override
            public void onSeekStop(RangeSeekBarView rangeSeekBarView, int index, float value) {
                onStopSeekThumbs(index, value);
            }
        });
    }

    private void setUpMargins(RangeSeekBarView mRangeSeekBarView, VideoFrameRecycler mTimeLineView) {
        int marge = mRangeSeekBarView.getThumbs().get(0).getWidthBitmap();
        marginLeft = marge;
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mTimeLineView.getLayoutParams();
        lp.setMargins(marge, 0, marge, 0);
        mTimeLineView.setLayoutParams(lp);

    }

    private void setSeekBarPosition() {
        mStartPosition = 0;
        mEndPosition = 100;
        mRangeSeekBarView.initMaxWidth();
    }

    private boolean canScroll = false;
    private long seekbarTime = 0;
    //rangeSeekbar占总可滑动宽度的比例
    private float seekbarPercent;
    private float marginLeft;
    private boolean fullyInitiated = false;

    public void initTrimmer(RangeSeekBarView mRangeSeekBarView, VideoFrameRecycler mTimeLineView, RoundImageView progressCursor) {
        this.cursor = progressCursor;
        this.mRangeSeekBarView = mRangeSeekBarView;
        this.mTimeLineView = mTimeLineView;
        canScroll = getDuration() > VideoTimelineAdapter.FULL_SCROLL_DURATION;
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false) {
                    @Override
                    public boolean canScrollHorizontally() {
                        return canScroll;
                    }
                };
        mTimeLineView.setLayoutManager(layoutManager);
        setUpMargins(mRangeSeekBarView, mTimeLineView);
        setUpListeners(mRangeSeekBarView);
        setSeekBarPosition();
        cropStartRatio = 0f;
        cropEndRatio = 1f;
        frameAdapter = new VideoTimelineAdapter(mContext, Uri.fromFile(new File(videoPath)), () -> {
            if (canScroll) {
                mTimeLineView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            cropStartPoint = 1f * recyclerView.computeHorizontalScrollOffset() / recyclerView.computeHorizontalScrollRange();
                            LogUtil.d("scrollRange", String.valueOf(recyclerView.computeHorizontalScrollRange()));
                            LogUtil.d("scrollOffset", String.valueOf(recyclerView.computeHorizontalScrollOffset()));
                            LogUtil.d("cropStart", String.valueOf(cropStartPoint));
                            calculateCrop();
                            seekTo(Math.round(getDuration() * getCropStartRatio()));
                        }
                    }
                });
                seekbarTime = 60 * 1000;
                seekbarPercent = 1f * mTimeLineView.getWidth() / (frameAdapter.getItemWidth() * frameAdapter.getItemCount());
                float timeRatio = 1f * 60 * 1000 / getDuration();
                float adjustRatio = timeRatio / seekbarPercent;
                seekbarPercent *= adjustRatio;
                mRangeSeekBarView.setMinDistance(Math.round(getDuration() * adjustRatio));
            } else {
                seekbarTime = getDuration();
                seekbarPercent = 1f;
                mRangeSeekBarView.setMinDistance(Math.round(getDuration()));
            }
            calculateCrop();
            //初始化进度指针的位置

            //总长度除以(拖动条从左到右代表的时长)总时间（毫秒）=px/ms
            float totalDistance = mRangeSeekBarView.getThumbs().get(1).getPos() - mRangeSeekBarView.getThumbs().get(0).getPos();
            float pixelsPerMs = totalDistance / seekbarTime;
            curOffset = pixelsPerMs * updateCursorIntervalMs;
            ViewGroup.LayoutParams cursorLp = cursor.getLayoutParams();
            cursorLp.height = mRangeSeekBarView.getMeasuredHeight();
            cursor.setLayoutParams(cursorLp);
            currentCursorStart = mRangeSeekBarView.getThumbs().get(1).getWidthBitmap();
            cursor.setTranslationX(currentCursorStart);
            cursor.setTranslationY(screenUtil.dip2px(BaseApplication.getInstance(), 1));

            //初始化震动
            vibrator = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
            //初始化完成
            fullyInitiated = true;
        });
        mTimeLineView.setAdapter(frameAdapter);
        //设置帧数
        frameAdapter.getFrames(Math.round(mTimeLineView.getWidth() - 2 * marginLeft), mTimeLineView.getHeight(), canScroll);
//        setTimeVideo(0);
    }

    //裁剪起点与总时长的百分比，比如从20.5%的进度开始裁剪
    private float cropStartPoint = 0;


    private float mStartPosition = 0;
    private float mEndPosition = 100;

    private synchronized void onSeekThumbs(int index, float value) {
        switch (index) {
            case Thumb.LEFT: {
                mStartPosition = value;
                LogUtil.d("seekbar", String.valueOf(value));
                break;
            }
            case Thumb.RIGHT: {
                mEndPosition = value;
                LogUtil.d("seekbar", String.valueOf(value));
                break;
            }
        }
        calculateCrop();
        switch (index) {
            case Thumb.LEFT: {
                seekTo(Math.round(getDuration() * getCropStartRatio()));
                break;
            }
            case Thumb.RIGHT: {
                seekTo(Math.round(getDuration() * getCropEndRatio()));
                break;
            }
        }

//        setTimeFrames();
    }

    public float getCropStartRatio() {
        return cropStartRatio;
    }

    public float getCropEndRatio() {
        return cropEndRatio;
    }

    private float cropStartRatio;
    private float cropEndRatio;

    private void calculateCrop() {
        //计算各种裁剪的位置，开始结束总时长
        cropStartRatio = cropStartPoint + (mStartPosition / 100) * seekbarPercent;
        cropEndRatio = cropStartPoint + (mEndPosition / 100) * seekbarPercent;
        float durationRatio = cropEndRatio - cropStartRatio;
        long durationTimeMs = Math.round(durationRatio * getDuration());
        long startTimeMs = Math.round(cropStartRatio * getDuration());
        long endTimeMs = Math.round(cropEndRatio * getDuration());
        callback.showCropTotalTime(durationTimeMs, startTimeMs, endTimeMs);
        LogUtil.d("crop", String.format("start+%f,end+%f,duration+%f", cropStartRatio, cropEndRatio, durationRatio));
    }

    private void onStopSeekThumbs(int index, float value) {
        switch (index) {
            case Thumb.LEFT: {
                mStartPosition = value;
                LogUtil.d("seekbar", String.valueOf(value));
                break;
            }
            case Thumb.RIGHT: {
                mEndPosition = value;
                LogUtil.d("seekbar", String.valueOf(value));
                break;
            }
        }
        calculateCrop();
        seekTo(Math.round(getDuration() * getCropStartRatio()));
    }


    public void onDestroy() {
        fullyInitiated = false;
        destroyTimer();
        if (frameAdapter != null) {
            frameAdapter.destory();
        }
        if (player != null) {
            player.stop();
            player = null;
        }
        if (drawPadView != null) {
            clearDrawpad();
            drawPadView.releaseDrawPad();
            drawPadView = null;
        }
        deleteAllFilesInPath(getFrameTempPath(mContext));
    }


    private String getFrameTempPath(Context mContext) {
        File file = mContext.getExternalFilesDir("runCatch/frames/");
        if (file != null) {
            if (!file.exists()) {
                boolean success = file.mkdir();
                if (success) {
                    return file.getPath();
                }
            } else {
                return file.getAbsolutePath();
            }
        }
        if (mContext.getExternalCacheDir() != null) {
            return mContext.getExternalCacheDir().getAbsolutePath();
        } else {
            return "";
        }


    }

    private void deleteAllFilesInPath(String path) {

        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
        }

    }

    public void onPause() {
        videoPause();
        if (drawPadView != null && drawPadView.isRunning()) {
            drawPadView.pausePreview();
        }
    }

    public void onResume() {
        if (player != null && !player.isPlaying()) {
            seekTo(Math.round(getDuration() * getCropStartRatio()));
        }
        if (drawPadView != null) {
            drawPadView.resumePreview();
        }
    }

    private static final long maxCropDurationMs = 300 * 1000;
    private static final long minCropDurationMs = 2 * 1000;
    private boolean isSaving = false;
    private boolean is4kVideo = false;
    WaitingDialog_progress dialog;
    DrawPadAllExecute2 execute= null;
    public void saveVideo() {
        if (!fullyInitiated || isSaving) {
            ToastUtil.showToast("还在加载请稍等");
            return;
        }

        dialog = new WaitingDialog_progress(mContext);
        dialog.openProgressDialog();
        MediaInfo videoInfo = new MediaInfo(videoPath);
        MediaInfo.checkFile(videoPath);
        if (!videoInfo.prepare()) {
            return;
        }
        long cropDurationMs = (long) (getDuration() * (cropEndRatio - cropStartRatio));
        //裁剪时长限制
        if (cropDurationMs > maxCropDurationMs) {
            ToastUtil.showToast(mContext.getString(R.string.toast_crop_toolong));
            return;
        } else if (cropDurationMs < minCropDurationMs) {
            ToastUtil.showToast(mContext.getString(R.string.toast_crop_tooshort));
            return;
        }
        //视频尺寸限制
        if (videoInfo.vCodecWidth > 1920 || videoInfo.vCodecHeight > 1080) {
            is4kVideo = true;
//            ToastUtil.showToast("原视频尺寸过大，请重新选择");
//            return;
        }
        onPause();
        isSaving = true;

        long durationUs = getDuration() * 1000;
        videoCutDurationForVideoOneDo.getInstance().startCutDurtion(videoPath, Math.round(cropStartRatio * durationUs), Math.round(cropEndRatio * durationUs), new videoCutDurationForVideoOneDo.isSuccess() {
            @Override
            public void progresss(int progress) {
                if (dialog != null) {
                    dialog.setProgress(progress + "%");
                }
            }

            @Override
            public void isSuccess(boolean isSuccess, String path) {
            isSaving=false;
            if (path == null) {
                ToastUtil.showToast(mContext.getString(R.string.render_error));
                return;
            }
            File video = new File(path);
            if (video.exists()){
                dialog.closePragressDialog();
                String tempPath = getTempVideoPath(mContext)+video.getName();
                try {
                    FileUtil.copyFile(video, tempPath);
                    callback.finishCrop(tempPath);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                dialog.closePragressDialog();
                ToastUtil.showToast(mContext.getString(R.string.export_failure));
            }
            }
        });



//        try {
//            execute = new DrawPadAllExecute2(mContext,DRAWPAD_WIDTH,DRAWPAD_HEIGHT,(cropDurationMs*1000));
//            execute.setFrameRate(FRAME_RATE);
//            execute.setEncodeBitrate(5 * 1024 * 1024);
//            execute.setOnLanSongSDKErrorListener(message -> {
//                isSaving=false;
//                dialog.closePragressDialog();
//                LogUtil.e("execute", String.valueOf(message));
//            });
//            execute.setOnLanSongSDKProgressListener((l, i) -> {
//                if (dialog!=null){
//                    dialog.setProgress(i +"%");
//                    LogUtil.d("execute progress: ", String.valueOf(i));
//                }
//            });
//            execute.setOnLanSongSDKCompletedListener(exportPath -> {
//                isSaving=false;
//                execute.release();
//                if (exportPath == null) {
//                    ToastUtil.showToast(mContext.getString(R.string.render_error));
//                    return;
//                }
//                File video = new File(exportPath);
//                if (video.exists()){
//                    dialog.closePragressDialog();
//                    String tempPath = getTempVideoPath(mContext)+video.getName();
//                    try {
//                        FileUtil.copyFile(video, tempPath);
//                        callback.finishCrop(tempPath);
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }else {
//                    dialog.closePragressDialog();
//                    ToastUtil.showToast(mContext.getString(R.string.export_failure));
//                }
//            });
//            Observable.create((Observable.OnSubscribe<Integer>) subscriber -> {
//                if (execute!=null){
//                    try {
//                        LSOVideoOption option=new LSOVideoOption(videoPath);
//                        long durationUs=getDuration()*1000;
//                        option.setCutDurationUs(Math.round(cropStartRatio*durationUs),Math.round(cropEndRatio*durationUs));
////                        VideoFrameLayer bgLayer=execute.addVideoLayer(option,0, Long.MAX_VALUE, true, true);
////                        if (bgLayer != null) {
////                            bgLayer.setScaledValue(videoRatio*execute.getPadHeight(), execute.getPadHeight());
////                            bgLayer.setPosition(execute.getPadWidth() * 0.5f, execute.getPadHeight() * 0.5f);
////                            LanSongFilter filter = new LanSongBlurFilter();
////                            bgLayer.switchFilterTo(filter);
////                        }else {
////                            subscriber.onError(new Throwable());
////                        }
//                        VideoFrameLayer mainLayer=execute.addVideoLayer(option);
//                        if (mainLayer!=null){
//                            mainLayer.setScaledValue(DRAWPAD_WIDTH*mScale,mScale*DRAWPAD_WIDTH/videoRatio);
//                            mainLayer.setPosition(execute.getPadWidth()*0.5f,execute.getPadHeight()*0.5f);
//                        }else {
//                            subscriber.onError(new Throwable());
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                if (execute.start()) {
//                    subscriber.onNext(0);
//                } else {
//                    subscriber.onError(new Throwable());
//                }
//                subscriber.onCompleted();
//            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(aInteger -> {
//                dialog.setProgress(aInteger+"");
//            }, throwable -> {
//                if (is4kVideo){
//                    statisticsEventAffair.getInstance().setFlag(mContext,"4kVideoFail","4k视频导出崩溃");
//                }
//                isSaving=false;
//                execute.release();
//                onResume();
//                ToastUtil.showToast(mContext.getString(R.string.export_failure));
//                dialog.closePragressDialog();
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }


    private String getTempVideoPath(Context mContext) {
        try {
            File file = mContext.getExternalFilesDir("runCatch/videos/");
            if (file != null && !file.exists()) {
                boolean success = file.mkdir();
                if (success) {
                    return file.getPath();
                }
            } else {
                return file.getAbsolutePath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mContext.getExternalCacheDir().getAbsolutePath();
    }
}

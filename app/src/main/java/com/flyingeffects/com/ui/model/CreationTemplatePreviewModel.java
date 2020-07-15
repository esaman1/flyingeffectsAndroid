package com.flyingeffects.com.ui.model;

import android.app.Service;
import android.content.Context;
import android.net.Uri;
import android.os.Vibrator;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.VideoTimelineAdapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.enity.VideoInfo;
import com.flyingeffects.com.ui.interfaces.model.CreationTemplatePreviewMvpCallback;
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
import rx.subjects.PublishSubject;


public class CreationTemplatePreviewModel {
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private CreationTemplatePreviewMvpCallback callback;
    private Context mContext;
    private float marginLeft;
    private boolean canScroll = false;
    private long seekbarTime = 0;
    //rangeSeekbar占总可滑动宽度的比例
    private float seekbarPercent;
    private RoundImageView cursor;
    private RangeSeekBarView mRangeSeekBarView;
    private List<OnProgressVideoListener> mListeners;
    private float mStartPosition = 0;
    private float mEndPosition = 100;
    private float cropStartRatio;
    private float cropEndRatio;
    private VideoTimelineAdapter frameAdapter;
    //裁剪起点与总时长的百分比，比如从20.5%的进度开始裁剪
    private float cropStartPoint = 0;
    private String videoPath;
    private float curOffset = 1;
    //每100毫秒更新一次进度指针
    private static final int updateCursorIntervalMs = 100;
    private float videoRatio = 1;
    private int realityDuration;

    public CreationTemplatePreviewModel(Context context, CreationTemplatePreviewMvpCallback callback, String videoPath) {
        this.mContext = context;
        this.callback = callback;
        this.videoPath = videoPath;
        VideoInfo videoInfo = VideoManage.getInstance().getVideoInfo(context, videoPath);
        realityDuration = videoInfo.getDuration();
    }

    public long getDuration() {
        return duration;
    }

    private float currentCursorStart = 0;


    private long duration;

    public void initTrimmer(RangeSeekBarView mRangeSeekBarView, VideoFrameRecycler mTimeLineView, RoundImageView progressCursor, long duration) {
        this.cursor = progressCursor;
        this.duration = duration;
        this.mRangeSeekBarView = mRangeSeekBarView;
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
                seekbarTime = 180 * 1000;
                seekbarPercent = 1f * mTimeLineView.getWidth() / (frameAdapter.getItemWidth() * frameAdapter.getItemCount());
                float timeRatio = 1f * 180 * 1000 / getDuration();
                float adjustRatio = timeRatio / seekbarPercent;
                seekbarPercent *= adjustRatio;
                mRangeSeekBarView.setMinDistance(Math.round(getDuration() * adjustRatio));
            } else {
                seekbarTime = getDuration();
                seekbarPercent = 1f;
                mRangeSeekBarView.setMinDistance(Math.round(getDuration()));
            }
            calculateCrop();
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
        });
        mTimeLineView.setAdapter(frameAdapter);
        //设置帧数
        frameAdapter.getFrames(Math.round(mTimeLineView.getWidth() - 2 * marginLeft), mTimeLineView.getHeight(), canScroll);
//        setTimeVideo(0);
    }

    public float getCropStartRatio() {
        return cropStartRatio;
    }

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


    public void ToInitTimer() {
        initTimer(updateCursorIntervalMs);
    }

    private Subscription timer;

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

    public void destroyTimer() {
        if (timer != null && !timer.isUnsubscribed()) {
            timer.unsubscribe();
            timer = null;
        }
    }


    private void setSeekBarPosition() {
        mStartPosition = 0;
        mEndPosition = 100;
        mRangeSeekBarView.initMaxWidth();
    }

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

    public float getCropEndRatio() {
        return cropEndRatio;
    }


    private void setUpMargins(RangeSeekBarView mRangeSeekBarView, VideoFrameRecycler mTimeLineView) {
        int marge = mRangeSeekBarView.getThumbs().get(0).getWidthBitmap();
        marginLeft = marge;
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mTimeLineView.getLayoutParams();
        lp.setMargins(marge, 0, marge, 0);
        mTimeLineView.setLayoutParams(lp);

    }


    /**
     * 跳转到
     *
     * @param to
     */
    private void seekTo(long to) {
        try {
//            player.seekTo((int) to);
            updateCursor(true);
            initTimer(updateCursorIntervalMs);
            callback.showCursor();
//            if (!player.isPlaying()) {
//                player.start();
//            }

            callback.seekToPosition(to);

        } catch (NullPointerException | IllegalStateException e) {
            LogUtil.d("OOM", e.getMessage());
        }
    }


    /**
     * description ：保存在本地，如果用戶沒有裁剪，那么就不需要重新裁剪，否则就重新裁剪过
     * creation date: 2020/6/28
     * user : zhangtongju
     */
    float allDuration;
    public void toSaveVideo(boolean hasShowStimulateAd) {
        long cropDurationMs = (long) (getDuration() * (cropEndRatio - cropStartRatio));
        if (cropDurationMs == realityDuration) {
            //不需要裁剪
            callback.isSaveToAlbum(videoPath, hasShowStimulateAd);
        } else {
            allDuration=realityDuration*1000;
            float test=(cropEndRatio-cropStartRatio)*allDuration/(float)1000/(float)1000;
            ToastUtil.showToast("裁剪时长为："+test);
            WaitingDialog_progress dialog = new WaitingDialog_progress(mContext);
            dialog.openProgressDialog();
            //需要裁剪
            videoCutDurationForVideoOneDo.getInstance().startCutDurtion(videoPath, Math.round(cropStartRatio * allDuration), Math.round(cropEndRatio * allDuration), new videoCutDurationForVideoOneDo.isSuccess() {
                @Override
                public void progresss(int progress) {
                    if (progress > 100) {
                        progress = 100;
                    }
                    dialog.setProgress(progress + "%");
                }

                @Override
                public void isSuccess(boolean isSuccess, String path) {
                    if (path == null) {
                        ToastUtil.showToast(mContext.getString(R.string.render_error));
                        return;
                    }
                    dialog.closePragressDialog();
                    callback.isSaveToAlbum(path, hasShowStimulateAd);
                    LogUtil.d("OOM", "裁剪后导出的地址为111" + path);


                }
            });
        }

    }

}

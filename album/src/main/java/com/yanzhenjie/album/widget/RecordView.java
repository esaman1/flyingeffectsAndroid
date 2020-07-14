package com.yanzhenjie.album.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.yanzhenjie.album.R;
import com.yanzhenjie.album.util.PxUtils;

/**
 * 自定义拍摄按钮
 *
 * @author sjq
 */
public class RecordView extends View implements View.OnClickListener {

    private static final int PROGRESS_INTERVAL = 50;

    private Paint fillPaint;
    private Paint progressPaint;
    private Paint mProgressTrackPaint;
    private Paint mBitmapPaint;
    private final Handler mHandler;
    private int progressMaxValue;
    private final int radius;
    private final int progressWidth;
    private final int progressColor;
    private final int progressTrackColor;
    private int fillColor;
    private float progressValue = 0f;
    private boolean isRecording;
    private Bitmap mIconCapture;
    private onRecordListener mListener;

    public RecordView(Context context) {
        this(context, null);
    }

    public RecordView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RecordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AlbumRecordView, defStyleAttr, defStyleRes);
        radius = typedArray.getDimensionPixelOffset(R.styleable.AlbumRecordView_album_record_radius, 0);
        progressWidth = typedArray.getDimensionPixelOffset(R.styleable.AlbumRecordView_album_progress_width, PxUtils.dp2px(context, 3));
        progressColor = typedArray.getColor(R.styleable.AlbumRecordView_album_progress_color, Color.RED);
        progressTrackColor = typedArray.getColor(R.styleable.AlbumRecordView_album_progress_track_color, Color.RED);
        fillColor = typedArray.getColor(R.styleable.AlbumRecordView_album_fill_color, Color.WHITE);

        typedArray.recycle();

        initPaint();

        mIconCapture = BitmapFactory.decodeResource(getResources(), R.drawable.album_icon_capture);

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                progressValue = progressValue + 0.05f;
                postInvalidate();
                if (progressValue <= progressMaxValue) {
                    sendEmptyMessageDelayed(0, PROGRESS_INTERVAL);
                    mListener.onRecording(progressValue);
                } else {
                    finishRecord();
                }
            }
        };
        setOnClickListener(this);
    }

    private void initPaint() {
        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(progressColor);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(progressWidth);

        mProgressTrackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressTrackPaint.setColor(progressTrackColor);
        mProgressTrackPaint.setStyle(Paint.Style.STROKE);
        mProgressTrackPaint.setStrokeWidth(progressWidth);

        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    private void finishRecord() {
        if (mListener != null) {
            mListener.onFinish();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();

        if (isRecording) {
            fillColor = Color.parseColor("#FF3C3B");
            fillPaint.setColor(fillColor);

            canvas.drawCircle(width >> 1, height >> 1, radius, fillPaint);

            int left = progressWidth / 2;
            int top = progressWidth / 2;
            int right = width - progressWidth / 2;
            int bottom = height - progressWidth / 2;
            float sweepAngle = (progressValue * 1.0f / progressMaxValue) * 360;

            canvas.drawCircle(width >> 1, height >> 1, radius + PxUtils.dp2px(getContext(), 1), mProgressTrackPaint);
            canvas.drawArc(left, top, right, bottom, -90, sweepAngle, false, progressPaint);
        } else {
            fillColor = Color.parseColor("#5496FF");
            fillPaint.setColor(fillColor);
            canvas.drawCircle(width >> 1, height >> 1, radius, fillPaint);
        }
        canvas.drawBitmap(mIconCapture,
                (width - mIconCapture.getWidth()) >> 1,
                (height - mIconCapture.getHeight()) >> 1, mBitmapPaint);
    }

    public void setMaxDuration(int maxDuration) {
        this.progressMaxValue = maxDuration;
    }

    public void setOnRecordListener(onRecordListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onClick();
        }
    }

    public void startRecord() {
        isRecording = true;
        mHandler.sendEmptyMessage(0);
    }

    public void stopRecord() {
        mHandler.removeCallbacksAndMessages(null);
        isRecording = false;
        progressValue = 0;
        postInvalidate();
    }

    public interface onRecordListener {
        void onClick();

        void onRecording(float progress);

        void onFinish();
    }

    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
    }

}

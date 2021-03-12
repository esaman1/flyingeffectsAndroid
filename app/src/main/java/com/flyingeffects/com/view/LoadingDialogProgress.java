package com.flyingeffects.com.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.flyingeffects.com.R;
import com.flyingeffects.com.utils.PxUtils;


/**
 * 加载进度条
 * @author vidya
 *
 */
public class LoadingDialogProgress extends View {
    private static final String TAG = "LoadingDialogProgress";
    private static final int MAX_VALUE = 100;

    private final int mProgressWidth;
    private final int mProgressColor;
    private final int mProgressTrackColor;
    private final int mTextSize;
    private final int mTextColor;

    private Paint mProgressPaint;
    private Paint mProgressTrackPaint;
    private Paint mTextPaint;

    private int mProgress = 100;
    private String mProgressStr;
    private int mProgressMaxValue = MAX_VALUE;
    private float mTxtHeight;

    public LoadingDialogProgress(Context context) {
        this(context, null);
    }

    public LoadingDialogProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingDialogProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);

    }

    public LoadingDialogProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingDialogProgress, defStyleAttr, defStyleRes);
        mProgressWidth = typedArray.getDimensionPixelOffset(R.styleable.LoadingDialogProgress_progress_width, PxUtils.dp2px(context, 10));
        mProgressColor = typedArray.getColor(R.styleable.LoadingDialogProgress_progress_color, Color.BLUE);
        mProgressTrackColor = typedArray.getColor(R.styleable.LoadingDialogProgress_progress_track_color, Color.RED);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.LoadingDialogProgress_text_size, 12);
        mTextColor = typedArray.getColor(R.styleable.LoadingDialogProgress_text_color, Color.BLACK);
        typedArray.recycle();
        initPaint();
        mProgressStr = "100%";
    }

    private void initPaint() {
        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setColor(mProgressColor);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        mProgressPaint.setStrokeWidth(mProgressWidth);

        mProgressTrackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressTrackPaint.setColor(mProgressTrackColor);
        mProgressTrackPaint.setStyle(Paint.Style.STROKE);
        mProgressTrackPaint.setStrokeWidth(mProgressWidth);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);

        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        mTxtHeight = (float) Math.ceil(fm.descent - fm.ascent);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);

        int w = widthSpecSize;
        int h = heightSpecSize;

        Log.d(TAG, mTextPaint.measureText(mProgressStr) + "");

        //处理wrap_content的几种特殊情况
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            w = 200;
            h = 200;
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            //只要宽度布局参数为wrap_content， 宽度给固定值200dp(处理方式不一，按照需求来)
            w = heightSpecSize;
            //按照View处理的方法，查看View#getDefaultSize可知
            h = heightSpecSize;
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            w = widthSpecSize;
            h = widthSpecSize;
        }
        //给两个字段设置值，完成最终测量
        setMeasuredDimension(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();

        int radius = width / 2 - mProgressWidth;
        mProgressStr = mProgress + "%";
        float textWidth = mTextPaint.measureText(mProgressStr, 0, mProgressStr.length());

        float textWidthStart = (width >> 1) - textWidth / 2;
        float textHeightStart = (height >> 1) + mTxtHeight/3;

        canvas.drawText(mProgressStr, textWidthStart, textHeightStart, mTextPaint);
        int left = mProgressWidth;
        int top = mProgressWidth;
        int right = width - mProgressWidth;
        int bottom = height - mProgressWidth;
        float sweepAngle = (mProgress * 1.0f / mProgressMaxValue) * 360;

        canvas.drawCircle(width >> 1, height >> 1, radius, mProgressTrackPaint);
        canvas.drawArc(left, top, right, bottom, -90, sweepAngle, false, mProgressPaint);
    }

    public void setProgress(int progress) {
        mProgress = progress;
    }

    public void setMaxDuration(int maxDuration) {
        this.mProgressMaxValue = maxDuration;
    }

}

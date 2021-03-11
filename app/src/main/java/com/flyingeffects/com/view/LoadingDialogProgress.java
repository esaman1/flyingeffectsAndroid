package com.flyingeffects.com.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.flyingeffects.com.R;

public class LoadingDialogProgress extends View {

    public LoadingDialogProgress(Context context) {
        this(context,null);
    }

    public LoadingDialogProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadingDialogProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);

    }

    public LoadingDialogProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingProgressStyle, defStyleAttr, defStyleRes);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}

package com.shixing.sxve.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.shixing.sxve.R;
import com.shixing.sxve.ui.model.GroupModel;
import com.shixing.sxve.ui.util.Size;

public class GroupThumbView extends View {
    private GroupModel mGroup;
    private float mOverallScale;
    private Rect mRect;
    private int mRectSize;
    private Rect mStrokeRect;
    private int mStrokeSize;
    private int needY;

    public GroupThumbView(Context context) {
        this(context, null);
    }

    public GroupThumbView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GroupThumbView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        TextPaint mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.BLACK);
        float textSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                12,
                displayMetrics);
        mTextPaint.setTextSize(textSize);
        mRect = new Rect();
        mRectSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, displayMetrics);
        int mRectColor = getResources().getColor(R.color.preview_bg);
        Paint mRectPaint = new Paint();
        mRectPaint.setColor(mRectColor);
        mStrokeSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, displayMetrics);
        mStrokeRect = new Rect();
    }

    public void setAssetGroup(GroupModel group) {
        if (group != null) {
            mGroup = group;
            group.setThumbTarget(this);
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mGroup != null) {
            int height = MeasureSpec.getSize(heightMeasureSpec);
            int mWidth = MeasureSpec.getSize(widthMeasureSpec);

            Log.d("GroupThumbView","onMeasure-height="+height+",onMeasure-mWidth="+mWidth);
            Size size = mGroup.getSize();
            int width = height * size.getWidth() / size.getHeight();
            int sHeight = mWidth * size.getHeight() / size.getWidth();
            Log.d("GroupThumbView","size-height="+ size.getHeight()+",size-mWidth="+size.getWidth());
            mOverallScale = 1f * mWidth / size.getWidth();
            setMeasuredDimension(mWidth, height);
            mRect.set(width - mRectSize, 0, width, mRectSize);
            mStrokeRect.set(mStrokeSize, mStrokeSize, width - mStrokeSize, height - mStrokeSize);
            if (height - sHeight > 0) {
                needY = (height - sHeight) / 2;
            } else {
                needY = -((sHeight - height) / 2);
            }
            Log.d("GroupThumbView","needY="+needY);
        } else {
            setMeasuredDimension(0, 0);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas != null) {
            if (mGroup == null) return;
            canvas.translate(0, needY);
            canvas.save();
            canvas.drawColor(Color.parseColor("#D8D8D8"));
            canvas.scale(mOverallScale, mOverallScale);
            mGroup.draw(canvas);
            canvas.restore();
        }

    }


}

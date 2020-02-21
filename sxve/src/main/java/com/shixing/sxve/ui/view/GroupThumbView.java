package com.shixing.sxve.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
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
    private static final String TAG = "GroupThumbView";
    private GroupModel mGroup;
    private float mOverallScale;
    private TextPaint mTextPaint;
    private Rect mRect;
    private int mRectSize;
    private int mRectColor;
    private Paint mRectPaint;
    private Rect mStrokeRect;
    private int mStrokeSize;

    public GroupThumbView(Context context) {
        this(context, null);
    }

    public GroupThumbView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GroupThumbView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.BLACK);
        float textSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                12,
                displayMetrics);
        mTextPaint.setTextSize(textSize);

        mRect = new Rect();
        mRectSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, displayMetrics);
        mRectColor = getResources().getColor(R.color.sxve_primary);
        mRectPaint = new Paint();
        mRectPaint.setColor(mRectColor);

        mStrokeSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, displayMetrics);
        mStrokeRect = new Rect();
    }

    public void setAssetGroup(GroupModel group) {
        mGroup = group;
        group.setThumbTarget(this);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mGroup != null) {
            int height = MeasureSpec.getSize(heightMeasureSpec);
            Size size = mGroup.getSize();
            int width = height * size.getWidth() / size.getHeight();
            mOverallScale = (float) height / size.getHeight();
            setMeasuredDimension(width, height);

            mRect.set(width - mRectSize, 0, width, mRectSize);
            mStrokeRect.set(mStrokeSize, mStrokeSize, width - mStrokeSize, height - mStrokeSize);
        } else {
            setMeasuredDimension(0, 0);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mGroup == null) return;

        canvas.save();
        canvas.scale(mOverallScale, mOverallScale);
        mGroup.draw(canvas);
        canvas.restore();

        if (isSelected()) {
            drawStroke(canvas);
        }

        drawIndex(canvas);
    }

    private void drawIndex(Canvas canvas) {
        String indexStr = String.valueOf(mGroup.getGroupIndex());

        float width = mTextPaint.measureText(indexStr);
        float height = mTextPaint.descent() + mTextPaint.ascent();

        canvas.drawRect(mRect, mRectPaint);
        canvas.drawText(indexStr,
                mRect.left + (mRect.width() - width) / 2,
                mRect.top + (mRect.height() - height) / 2,
                mTextPaint);
    }

    private void drawStroke(Canvas canvas) {
        canvas.save();
        canvas.clipRect(mStrokeRect, Region.Op.DIFFERENCE);
        canvas.drawColor(mRectColor);
        canvas.restore();
    }
}

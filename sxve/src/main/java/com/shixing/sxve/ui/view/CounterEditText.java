package com.shixing.sxve.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatEditText;

public class CounterEditText extends AppCompatEditText {
    private static final String TAG = "CountEditText";
    private Paint countPaint;
    private String countStr = "50/50";
    private Rect bounds;
    private int paddingRight;
    private int maxLength;

    public CounterEditText(Context context) {
        super(context);
        init();
    }

    public CounterEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CounterEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paddingRight = getPaddingRight();
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        if (maxLength > 0) {
            setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});

            countStr = maxLength + "/" + maxLength;
            if (countPaint == null) {
                initCountPaint();
            }
            if (bounds == null) {
                bounds = new Rect();
            }
            countPaint.getTextBounds(countStr, 0, countStr.length(), bounds);
            setPadding(getPaddingLeft(), getPaddingTop(), paddingRight * 2 + bounds.width(), getPaddingBottom());
            countStr = "0/" + maxLength;
            invalidate();
        }
    }

    private void initCountPaint() {
        countPaint = new Paint();
        countPaint.setColor(Color.GRAY);
        float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics());
        countPaint.setTextSize(textSize);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        countStr = text.length() + "/" + maxLength;
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (maxLength > 0) {
            canvas.drawText(
                    countStr,
                    getWidth() - paddingRight - bounds.width(),
                    getHeight() + getScrollY() - getPaddingBottom(),
                    countPaint);
        }
    }
}

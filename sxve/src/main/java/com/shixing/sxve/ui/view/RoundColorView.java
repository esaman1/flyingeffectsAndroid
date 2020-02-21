package com.shixing.sxve.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.shixing.sxve.R;

public class RoundColorView extends View {
    private static final String TAG = "RoundColorView";
    private Bitmap mBitmap;
    private float mRadius;
    private Paint mPaint;
    private boolean mSelected;
    private Paint mCoverPaint;

    public RoundColorView(Context context) {
        this(context, null);
    }

    public RoundColorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundColorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.colour_xz_icon);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCoverPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCoverPaint.setColor(Color.BLACK);
        mCoverPaint.setAlpha(33);
    }

    public void setColor(int color) {
        mPaint.setColor(color);
        Log.d(TAG, "setColor: ");
        invalidate();
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //mRadius = Math.min(getWidth(), getHeight()) / 2f;
        Log.d(TAG, "onMeasure: " + mRadius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mRadius = Math.min(getWidth(), getHeight()) / 2f;
        if (mRadius == 0) return;
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, Math.min(getWidth(), getHeight()) / 2f, mPaint);
        if (mSelected) {
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, Math.min(getWidth(), getHeight()) / 2f, mCoverPaint);
            canvas.drawBitmap(mBitmap, (getWidth() - mBitmap.getWidth()) / 2f, (getHeight() - mBitmap.getHeight()) / 2f, null);
        }
    }
}

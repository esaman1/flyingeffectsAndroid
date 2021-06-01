package com.imaginstudio.imagetools.pixellab;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.core.view.ViewCompat;

/* access modifiers changed from: package-private */
/* compiled from: gradientTool_slider */
public class colorHandle extends View implements Comparable<colorHandle> {
    private int _xDelta;
    private int _yDelta;
    private Drawable backgroundCheckers;
    private int color = ViewCompat.MEASURED_STATE_MASK;
    final int handleH = dpToPixels(15);
    final int handleW = dpToPixels(26);
    private final int parentHeight;
    int parentWidth;
    private int position = 0;
    gradientTool_slider root;
    private boolean selectedHandle = true;
    OnHandleTouchListener touchListener;

    /* compiled from: gradientTool_slider */
    public interface OnHandleTouchListener {
        void onHandleDoneEvent();

        void onHandleMoveEvent();

        void onHandleTouchEvent(colorHandle colorhandle);
    }

    public int compareTo(colorHandle comparedColorHandle) {
        return this.position - comparedColorHandle.getPosition();
    }

    /* access modifiers changed from: package-private */
    public int dpToPixels(int dp) {
        return (int) TypedValue.applyDimension(1, (float) dp, getResources().getDisplayMetrics());
    }

    /* access modifiers changed from: package-private */
    public void setTouchListener(OnHandleTouchListener listener) {
        this.touchListener = listener;
    }

    colorHandle(Context context, int color2, int position2, int parentWidth2, int parentHeight2, Drawable backgroundCheckers2) {
        super(context);
        this.color = color2;
        this.position = position2;
        this.parentWidth = parentWidth2;
        this.parentHeight = parentHeight2;
        this.backgroundCheckers = backgroundCheckers2;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position2) {
        this.position = position2;
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color2) {
        this.color = color2;
        invalidate();
    }

    public void toggleSelect(boolean selected) {
        this.selectedHandle = selected;
        invalidate();
    }

    public boolean onTouchEvent(MotionEvent event) {
        int X = (int) event.getRawX();
        int Y = (int) event.getRawY();
        if (this.root == null) {
            this.root = (gradientTool_slider) getParent();
        }
        switch (event.getAction() & 255) {
            case 0:
                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) getLayoutParams();
                this._xDelta = X - lParams.leftMargin;
                this._yDelta = Y - lParams.topMargin;
                if (this.touchListener != null) {
                    this.touchListener.onHandleTouchEvent(this);
                    break;
                }
                break;
            case 1:
                if (this.touchListener != null) {
                    this.touchListener.onHandleDoneEvent();
                    break;
                }
                break;
            case 2:
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
                layoutParams.leftMargin = Math.max(Math.min(X - this._xDelta, this.parentWidth - this.handleW), 0);
                this.position = (int) ((((float) layoutParams.leftMargin) / ((float) (this.parentWidth - this.handleW))) * 1000.0f);
                layoutParams.rightMargin = -250;
                setLayoutParams(layoutParams);
                requestLayout();
                if (this.touchListener != null) {
                    this.touchListener.onHandleMoveEvent();
                }
                invalidate();
                break;
        }
        if (this.root == null) {
            return true;
        }
        this.root.invalidate();
        return true;
    }

    /* access modifiers changed from: package-private */
    public void invalidateOuterParent() {
        if (getParent().getParent() != null && (getParent().getParent() instanceof ViewGroup)) {
            ((ViewGroup) getParent().getParent()).invalidate();
        }
    }

    public void invalidate() {
        invalidateOuterParent();
        super.invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect canvasBound = new Rect();
        canvas.getClipBounds(canvasBound);
        canvasBound.inset(-dpToPixels(6), -dpToPixels(6));
        canvas.clipRect(canvasBound, Region.Op.REPLACE);
        Paint pn = new Paint(1);
        Path path = new Path();
        path.moveTo(0.0f, 0.0f);
        path.lineTo((float) getWidth(), 0.0f);
        path.lineTo((float) getWidth(), (float) (getHeight() - this.handleH));
        path.lineTo(((float) getWidth()) / 2.0f, (float) getHeight());
        path.lineTo(0.0f, (float) (getHeight() - this.handleH));
        path.close();
        if (this.selectedHandle) {
            pn.setStyle(Paint.Style.FILL_AND_STROKE);
            pn.setColor(Color.parseColor("#ff9800"));
            pn.setAlpha(255);
            pn.setStrokeWidth((float) dpToPixels(6));
            canvas.drawPath(path, pn);
        }
        pn.setStyle(Paint.Style.FILL_AND_STROKE);
        pn.setColor(-12303292);
        pn.setAlpha(255);
        pn.setStrokeWidth((float) dpToPixels(2));
        canvas.drawPath(path, pn);
        pn.setColor(-1);
        pn.setAlpha(255);
        pn.setStrokeWidth((float) dpToPixels(1));
        canvas.drawPath(path, pn);
        Rect area = new Rect(0, 0, getWidth(), getHeight() - this.handleH);
        area.inset(dpToPixels(1), dpToPixels(1));
        this.backgroundCheckers.setBounds(area);
        this.backgroundCheckers.draw(canvas);
        pn.setColor(this.color);
        canvas.drawRect(area, pn);
    }

    private int calculateContrastColor(int colorIn) {
        return ((((float) Color.red(colorIn)) * 0.299f) + (((float) Color.green(colorIn)) * 0.587f)) + (((float) Color.blue(colorIn)) * 0.114f) > 186.0f ? -12303292 : -3355444;
    }

    /* access modifiers changed from: package-private */
    public void drawStripes(Rect area, Canvas canvas, int n, int color2) {
        canvas.clipRect(new Rect(area), Region.Op.REPLACE);
        float maxDistance = (float) Math.sqrt((double) ((area.width() * area.width()) + (area.height() * area.height())));
        float cosAlpha = (float) Math.cos(0.7853981633974483d);
        float sinAlpha = (float) Math.sin(0.7853981633974483d);
        float step = ((float) area.height()) / ((float) n);
        Paint pnt = new Paint(1);
        pnt.setColor(calculateContrastColor(color2));
        pnt.setStrokeWidth(maxDistance / ((float) (n * 2)));
        for (int i = 1; i < n; i++) {
            PointF startPos = new PointF(0.0f, ((float) i) * step);
            PointF endPos = new PointF(startPos.x + (maxDistance * cosAlpha), startPos.y + (maxDistance * sinAlpha));
            canvas.drawLine(startPos.x, startPos.y, endPos.x, endPos.y, pnt);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(this.handleW, this.parentHeight);
    }
}

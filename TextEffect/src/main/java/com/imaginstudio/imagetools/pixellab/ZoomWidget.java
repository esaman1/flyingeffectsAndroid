package com.imaginstudio.imagetools.pixellab;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;


import java.util.ArrayList;
import java.util.Iterator;

import androidx.core.view.ViewCompat;

public class ZoomWidget extends View implements ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener {
    private static final float MAX_ZOOM = 5.0f;
    private static final float MIN_ZOOM = 1.0f;
    private static final String TAG = "ZoomLayout";
    private RectF bounds = null;
    private View child = null;
    private float dx = 0.0f;
    private float dy = 0.0f;
    RectF horScroll;
    RectF horScrollArea;
    private float lastScaleFactor = 0.0f;
    ArrayList<OnZoomListener> listeners = new ArrayList<>();
    private Mode mode = Mode.NONE;
    Paint pntBg;
    private float prevDx = 0.0f;
    private float prevDy = 0.0f;
    int previewQuadrant = 3;
    private float scale = MIN_ZOOM;
    ScaleGestureDetector scaleDetector;
    final float scrollBarPadding = commonFuncs.dpToPx(4);
    final float scrollBarWidth = commonFuncs.dpToPx(10);
    Paint scrollPaint;
    Paint scrollPaintBorder;
    private float startX = 0.0f;
    private float startY = 0.0f;
    private ZoomButton toNotify;
    RectF verScroll;
    RectF verScrollArea;
    boolean zoomEnabled = false;

    private enum Mode {
        NONE,
        DRAG,
        ZOOM
    }

    public interface OnZoomListener {
        void zoomChanged(float f);
    }

    public void addListener(OnZoomListener listener) {
        this.listeners.add(listener);
    }

    public boolean toggleZoom() {
        toggleZoom(!this.zoomEnabled);
        return this.zoomEnabled;
    }

    public void toggleZoom(boolean toggle) {
        boolean oldZoomToggle = this.zoomEnabled;
        this.zoomEnabled = toggle;
        if (this.zoomEnabled != oldZoomToggle) {
            if (this.toNotify != null) {
                this.toNotify.setZoomOn(this.zoomEnabled);
            }
            invalidate();
        }
    }

    public void setNotifyOnChange(ZoomButton toNotify2) {
        this.toNotify = toNotify2;
    }

    public float getZoomFactor() {
        return this.scale;
    }

    public ZoomWidget(Context context) {
        super(context);
        init(context);
    }

    public ZoomWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ZoomWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.pntBg = new Paint(1);
        this.pntBg.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.pntBg.setAlpha(100);
        this.scaleDetector = new ScaleGestureDetector(context, this);
        this.scrollPaint = new Paint(1);
        this.scrollPaintBorder = new Paint(1);
        this.scrollPaint.setColor(Color.parseColor("#a6a6a6"));
        this.scrollPaintBorder.setColor(Color.parseColor("#69222222"));
        this.scrollPaintBorder.setStyle(Paint.Style.FILL_AND_STROKE);
        this.scrollPaintBorder.setStrokeWidth(commonFuncs.dpToPx(2));
        this.verScroll = new RectF();
        this.horScroll = new RectF();
        setOnTouchListener(this);
    }

        @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (this.bounds == null) {
            this.bounds = new RectF();
        }
        if (this.horScrollArea == null) {
            this.horScrollArea = new RectF();
        }
        if (this.verScrollArea == null) {
            this.verScrollArea = new RectF();
        }
        this.bounds.set(0.0f, 0.0f, (float) w, (float) h);
        this.horScrollArea.set(0.0f, ((float) getHeight()) - this.scrollBarWidth, (float) getWidth(), (float) getHeight());
        this.horScrollArea.inset(this.scrollBarWidth + (this.scrollBarPadding * 2.0f), 0.0f);
        this.horScrollArea.offset(0.0f, -this.scrollBarPadding);
        this.verScrollArea.set(((float) getWidth()) - this.scrollBarWidth, 0.0f, (float) getWidth(), (float) getHeight());
        this.verScrollArea.inset(0.0f, this.scrollBarWidth + (this.scrollBarPadding * 2.0f));
        this.verScrollArea.offset(-this.scrollBarPadding, 0.0f);
    }

        @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.zoomEnabled && this.bounds != null) {
            drawScrollBars(canvas);
        }
    }

    /* access modifiers changed from: package-private */
    public void drawScrollBars(Canvas canvas) {
        if (this.horScrollArea != null && this.verScrollArea != null) {
            this.verScroll.set(this.verScrollArea);
            this.horScroll.set(this.horScrollArea);
            this.verScroll.bottom /= this.scale;
            this.horScroll.right /= this.scale;
            if (this.scale > MIN_ZOOM) {
                this.verScroll.offset(0.0f, this.verScrollArea.centerY() - this.verScroll.centerY());
                this.verScroll.offset(0.0f, (((this.verScrollArea.height() - this.verScroll.height()) / 2.0f) / (((((float) child().getHeight()) - (((float) child().getHeight()) / this.scale)) / 2.0f) * this.scale)) * -1.0f * this.dy);
                this.horScroll.offset(this.horScrollArea.centerX() - this.horScroll.centerX(), 0.0f);
                this.horScroll.offset((((this.horScrollArea.width() - this.horScroll.width()) / 2.0f) / (((((float) child().getWidth()) - (((float) child().getWidth()) / this.scale)) / 2.0f) * this.scale)) * -1.0f * this.dx, 0.0f);
            }
            canvas.drawRect(this.verScroll, this.scrollPaintBorder);
            canvas.drawRect(this.verScroll, this.scrollPaint);
            canvas.drawRect(this.horScroll, this.scrollPaintBorder);
            canvas.drawRect(this.horScroll, this.scrollPaint);
        }
    }

    public void setZoomLevel(float zoom) {
        this.scale = zoom;
        notifyScaleChanged();
        this.prevDx = this.dx;
        this.prevDy = this.dy;
        this.dx = 0.0f;
        this.dy = 0.0f;
        child().animate().setListener(new Animator.AnimatorListener() {
            /* class com.imaginstudio.imagetools.pixellab.ZoomWidget.AnonymousClass1 */

            public void onAnimationStart(Animator animator) {
            }

            public void onAnimationEnd(Animator animator) {
                ZoomWidget.this.invalidate();
            }

            public void onAnimationCancel(Animator animator) {
            }

            public void onAnimationRepeat(Animator animator) {
            }
        }).scaleX(this.scale).translationX(this.dx).translationY(this.dy).scaleY(this.scale).setDuration(200).setInterpolator(new AccelerateInterpolator()).start();
    }

    public void resetZoom(boolean disableAfter) {
        if (!(this.scale == MIN_ZOOM && this.dx == 0.0f && this.dy == 0.0f)) {
            this.scale = MIN_ZOOM;
            notifyScaleChanged();
            this.prevDx = this.dx;
            this.prevDy = this.dy;
            this.dx = 0.0f;
            this.dy = 0.0f;
            child().animate().setListener(new Animator.AnimatorListener() {
                /* class com.imaginstudio.imagetools.pixellab.ZoomWidget.AnonymousClass2 */

                public void onAnimationStart(Animator animator) {
                }

                public void onAnimationEnd(Animator animator) {
                    ZoomWidget.this.invalidate();
                }

                public void onAnimationCancel(Animator animator) {
                }

                public void onAnimationRepeat(Animator animator) {
                }
            }).scaleX(this.scale).scaleY(this.scale).translationX(this.dx).translationY(this.dy).setDuration(200).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        }
        if (disableAfter) {
            toggleZoom(false);
        }
    }

    private boolean setPreviewQuadrant(float x, float y) {
        int oldQuadrant = this.previewQuadrant;
        this.previewQuadrant = x > ((float) getWidth()) / 2.0f ? y > ((float) getHeight()) / 2.0f ? 1 : 4 : y > ((float) getHeight()) / 2.0f ? 2 : 3;
        if (oldQuadrant != this.previewQuadrant) {
            return true;
        }
        return false;
    }

    public boolean onScaleBegin(ScaleGestureDetector scaleDetector2) {
        Log.i(TAG, "onScaleBegin");
        return true;
    }

    public boolean onScale(ScaleGestureDetector scaleDetector2) {
        float scaleFactor = scaleDetector2.getScaleFactor();
        Log.i(TAG, "onScale" + scaleFactor);
        if (this.lastScaleFactor == 0.0f || Math.signum(scaleFactor) == Math.signum(this.lastScaleFactor)) {
            this.scale *= scaleFactor;
            this.scale = Math.max((float) MIN_ZOOM, Math.min(this.scale, (float) MAX_ZOOM));
            notifyScaleChanged();
            this.lastScaleFactor = scaleFactor;
            return true;
        }
        this.lastScaleFactor = 0.0f;
        return true;
    }

    private void notifyScaleChanged() {
        Iterator<OnZoomListener> it = this.listeners.iterator();
        while (it.hasNext()) {
            it.next().zoomChanged(this.scale);
        }
        if (this.toNotify != null) {
            this.toNotify.updateZoom(this.scale);
        }
    }

    public void onScaleEnd(ScaleGestureDetector scaleDetector2) {
        Log.i(TAG, "onScaleEnd");
    }

    private void applyScaleAndTranslation() {
        child().setScaleX(this.scale);
        child().setScaleY(this.scale);
        child().setTranslationX(this.dx);
        child().setTranslationY(this.dy);
        invalidate();
    }

    private View child() {
        return this.child;
    }

    public void setChild(View child2) {
        this.child = child2;
    }

    /* access modifiers changed from: package-private */
    public float dpToPixels(int dp) {
        return TypedValue.applyDimension(1, (float) dp, getResources().getDisplayMetrics());
    }

    /* access modifiers changed from: package-private */
    public float inRange(float val, float down, float up) {
        return Math.min(Math.max(val, down), up);
    }

    public PointF getViewOrigin() {
        return new PointF(inRange((((float) child().getWidth()) / 2.0f) - ((this.dx + (((float) getWidth()) / 2.0f)) / this.scale), 0.0f, (float) child().getWidth()), inRange((((float) child().getHeight()) / 2.0f) - ((this.dy + (((float) getHeight()) / 2.0f)) / this.scale), 0.0f, (float) child().getWidth()));
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (!this.zoomEnabled) {
            return false;
        }
        switch (motionEvent.getAction() & 255) {
            case 0:
                if (this.scale > MIN_ZOOM) {
                    this.mode = Mode.DRAG;
                    this.startX = motionEvent.getX() - this.prevDx;
                    this.startY = motionEvent.getY() - this.prevDy;
                }
                if (setPreviewQuadrant(motionEvent.getX(), motionEvent.getY())) {
                    invalidate();
                    break;
                }
                break;
            case 1:
                this.mode = Mode.NONE;
                this.prevDx = this.dx;
                this.prevDy = this.dy;
                break;
            case 2:
                if (this.mode == Mode.DRAG) {
                    this.dx = motionEvent.getX() - this.startX;
                    this.dy = motionEvent.getY() - this.startY;
                    break;
                }
                break;
            case 5:
                this.mode = Mode.ZOOM;
                if (setPreviewQuadrant(motionEvent.getX(), motionEvent.getY())) {
                    invalidate();
                    break;
                }
                break;
        }
        this.scaleDetector.onTouchEvent(motionEvent);
        if ((this.mode != Mode.DRAG || this.scale < MIN_ZOOM) && this.mode != Mode.ZOOM) {
            return true;
        }
        getParent().requestDisallowInterceptTouchEvent(true);
        float maxDx = ((((float) child().getWidth()) - (((float) child().getWidth()) / this.scale)) / 2.0f) * this.scale;
        float maxDy = ((((float) child().getHeight()) - (((float) child().getHeight()) / this.scale)) / 2.0f) * this.scale;
        this.dx = Math.min(Math.max(this.dx, -maxDx), maxDx);
        this.dy = Math.min(Math.max(this.dy, -maxDy), maxDy);
        applyScaleAndTranslation();
        return true;
    }
}

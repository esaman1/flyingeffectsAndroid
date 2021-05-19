package com.imaginstudio.imagetools.pixellab;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.imaginstudio.imagetools.R;

import androidx.core.content.ContextCompat;

public class ZoomButton extends FrameLayout implements View.OnClickListener {
    ZoomLabel label;
    int width = commonFuncs.dpToPxInt(76);
    ImageButton zoom;
    OnZoomEvent zoomListener;
    boolean zoomOn = false;

    public interface OnZoomEvent {
        void zoomReset();

        void zoomToggle(boolean z);
    }

    public void setZoomOn(boolean on) {
        boolean oldZoomOn = this.zoomOn;
        this.zoomOn = on;
        if (oldZoomOn != this.zoomOn) {
            updateButtons();
        }
    }

    public void setZoomListener(OnZoomEvent zoomListener2) {
        this.zoomListener = zoomListener2;
    }

    public void updateZoom(float zoom2) {
        this.label.setZoom(zoom2);
    }

    /* access modifiers changed from: package-private */
    public void init() {
        this.zoom = new ImageButton(getContext());
        this.zoom.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.top_bar_button1));
        LayoutParams lParams = new LayoutParams(commonFuncs.dpToPxInt(38), commonFuncs.dpToPxInt(38));
        lParams.gravity = 17;
        this.zoom.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.action_zoom));
        ((BitmapDrawable) this.zoom.getDrawable()).setAntiAlias(true);
        this.label = new ZoomLabel(getContext(), new OnClickListener() {
            /* class com.imaginstudio.imagetools.pixellab.controls.widgets.ZoomButton.AnonymousClass1 */

            public void onClick(View view) {
                if (ZoomButton.this.zoomListener != null) {
                    ZoomButton.this.zoomListener.zoomReset();
                }
            }
        });
        LayoutParams lParams1 = new LayoutParams(-1, commonFuncs.dpToPxInt(30));
        lParams1.gravity = 17;
        this.label.setVisibility(View.GONE);
        this.zoom.setOnClickListener(this);
        addView(this.label, lParams1);
        addView(this.zoom, lParams);
    }

    public ZoomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /* access modifiers changed from: package-private */
    public void updateButtons() {
        this.zoom.setSelected(this.zoomOn);
        if (this.zoomListener != null) {
            this.zoomListener.zoomToggle(this.zoomOn);
        }
        if (this.zoomOn) {
            this.zoom.animate().x(0.0f).setDuration(100).start();
            this.label.setAlpha(0.0f);
            this.label.setVisibility(View.VISIBLE);
            this.label.animate().alpha(1.0f).scaleX(1.0f).setDuration(150).setListener(null).start();
            return;
        }
        this.zoom.animate().x((((float) getWidth()) / 2.0f) - (((float) this.zoom.getWidth()) / 2.0f)).setDuration(100).start();
        this.label.animate().alpha(0.0f).scaleX(0.6f).setDuration(150).setListener(new Animator.AnimatorListener() {
            /* class com.imaginstudio.imagetools.pixellab.controls.widgets.ZoomButton.AnonymousClass2 */

            public void onAnimationStart(Animator animator) {
            }

            public void onAnimationEnd(Animator animator) {
                ZoomButton.this.label.setVisibility(View.GONE);
            }

            public void onAnimationCancel(Animator animator) {
            }

            public void onAnimationRepeat(Animator animator) {
            }
        }).start();
    }

    public void onClick(View view) {
        setZoomOn(!this.zoomOn);
    }

    /* access modifiers changed from: package-private */
    public class ZoomLabel extends View {
        RectF bounds;
        boolean isClicked = false;
        OnClickListener listener;
        Paint pntBg = new Paint(1);
        Paint pntText;
        float roundness = commonFuncs.dpToPx(5);
        RectF textArea;
        Rect textBounds = new Rect();
        String zoomText = "100%";

        public ZoomLabel(Context context, OnClickListener listener2) {
            super(context);
            this.pntBg.setColor(Color.parseColor("#152e59"));
            this.pntText = new Paint(1);
            this.pntText.setTextSize(commonFuncs.spToPx(12));
            this.pntText.setColor(-1);
            this.listener = listener2;
        }

            @Override
    protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (this.bounds != null) {
                this.pntBg.setAlpha(this.isClicked ? 170 : 255);
                canvas.drawRoundRect(this.bounds, this.roundness, this.roundness, this.pntBg);
                this.pntText.getTextBounds(this.zoomText, 0, this.zoomText.length(), this.textBounds);
                canvas.drawText(this.zoomText, this.textArea.centerX() - ((float) this.textBounds.centerX()), this.textArea.centerY() - ((float) this.textBounds.centerY()), this.pntText);
            }
        }

        public void setZoom(float zoom) {
            this.zoomText = ((int) (100.0f * zoom)) + "%";
            invalidate();
        }

            @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(ZoomButton.this.width, getMeasuredHeight());
        }

            @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            this.bounds = new RectF(commonFuncs.dpToPx(20), 0.0f, (float) w, (float) h);
            this.textArea = new RectF(commonFuncs.dpToPx(38), 0.0f, this.bounds.right, (float) h);
        }

        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getActionMasked()) {
                case 0:
                    this.isClicked = true;
                    invalidate();
                    break;
                case 1:
                case 3:
                    this.isClicked = false;
                    invalidate();
                    this.listener.onClick(this);
                    break;
            }
            return true;
        }
    }
}

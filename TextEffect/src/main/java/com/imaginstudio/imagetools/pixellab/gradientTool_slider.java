package com.imaginstudio.imagetools.pixellab;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RelativeLayout;

import com.imaginstudio.imagetools.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import androidx.core.view.PointerIconCompat;

public class gradientTool_slider extends RelativeLayout implements colorHandle.OnHandleTouchListener {
    static final int handleHDp = 15;
    static final int handleWDp = 26;
    Drawable backgroundCheckers;
    backgroundDrawableHolder bgGradient;
    private ArrayList<colorHandle> colorHandles = new ArrayList<>();
    int[] colors;
    private gradientTool_Preview gradientPreview = null;
    final int gridSquare = dpToPixels(5);
    final int handleH = dpToPixels(15);
    final int handleW = dpToPixels(26);
    colorHandle lastSelectedHandle = null;
    Paint pnGridBlack = new Paint(1);
    Paint pnGridWhite;
    float[] positions;
    private gradientTool_controls sliderControls;

    public void connectControls(gradientTool_controls sliderControls2) {
        this.sliderControls = sliderControls2;
    }

    public void gradientDoneEditing() {
        if (this.gradientPreview != null) {
            this.gradientPreview.setColors(this.colors, this.positions);
            this.gradientPreview.post(new Runnable() {
                /* class com.imaginstudio.imagetools.pixellab.controls.widgets.gradientTool_slider.AnonymousClass1 */

                public void run() {
                    gradientTool_slider.this.gradientPreview.invalidate();
                }
            });
        }
    }

    public void flipPositions() {
        if (this.lastSelectedHandle != null) {
            Iterator<colorHandle> it = this.colorHandles.iterator();
            while (it.hasNext()) {
                colorHandle aHandle = it.next();
                LayoutParams layoutParams = (LayoutParams) aHandle.getLayoutParams();
                int newPosition = 1000 - aHandle.getPosition();
                aHandle.setPosition(newPosition);
                layoutParams.leftMargin = (int) (((float) ((getWidth() - this.handleW) * newPosition)) / 1000.0f);
                layoutParams.rightMargin = -250;
                aHandle.setLayoutParams(layoutParams);
            }
            requestLayout();
            gradientChanged();
        }
    }

    public void connectPreview(gradientTool_Preview gradientPreview2) {
        this.gradientPreview = gradientPreview2;
    }

    public void initializeColors(final int[] colors2, final float[] positions2) {
        post(new Runnable() {
            /* class com.imaginstudio.imagetools.pixellab.controls.widgets.gradientTool_slider.AnonymousClass2 */

            public void run() {
                if (colors2.length == positions2.length) {
                    for (int i = 0; i < colors2.length; i++) {
                        gradientTool_slider.this.addNewColor(colors2[i], (int) (positions2[i] * 1000.0f));
                    }
                }
            }
        });
    }

    /* access modifiers changed from: package-private */
    public class backgroundDrawableHolder extends Drawable {
        int[] colors = null;
        LinearGradient linGrad = null;
        Paint pnGradient = new Paint(1);
        float[] positions = null;

        backgroundDrawableHolder() {
        }

        public void draw(Canvas canvas) {
            Rect availableArea = new Rect(0, gradientTool_slider.this.dpToPixels(2), gradientTool_slider.this.getWidth() - gradientTool_slider.this.handleW, gradientTool_slider.this.getHeight() - gradientTool_slider.this.handleH);
            Rect visibleArea = new Rect(availableArea);
            canvas.save();
            canvas.translate((float) (gradientTool_slider.this.handleW / 2), 0.0f);
            Paint strokePaint = new Paint(1);
            strokePaint.setStyle(Paint.Style.FILL);
            strokePaint.setColor(-12303292);
            canvas.drawRect(visibleArea, strokePaint);
            visibleArea.inset(gradientTool_slider.this.dpToPixels(1), gradientTool_slider.this.dpToPixels(1));
            strokePaint.setColor(-3355444);
            canvas.drawRect(visibleArea, strokePaint);
            visibleArea.inset(gradientTool_slider.this.dpToPixels(1), gradientTool_slider.this.dpToPixels(1));
            gradientTool_slider.this.backgroundCheckers.setBounds(visibleArea);
            gradientTool_slider.this.backgroundCheckers.draw(canvas);
            if (!(this.positions == null || this.colors == null)) {
                this.linGrad = new LinearGradient((float) availableArea.left, (float) availableArea.top, (float) availableArea.right, 0.0f, this.colors, this.positions, Shader.TileMode.CLAMP);
                this.pnGradient.setShader(this.linGrad);
                canvas.drawRect(visibleArea, this.pnGradient);
            }
            canvas.restore();
        }

        public void updateGradient(int[] colors2, float[] positions2) {
            this.colors = colors2;
            this.positions = positions2;
            gradientTool_slider.this.invalidate();
        }

        public void setAlpha(int i) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public int getOpacity() {
            return PixelFormat.UNKNOWN;
        }
    }

    public gradientTool_slider(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.pnGridBlack.setColor(Color.parseColor("#bdbdbd"));
        this.pnGridWhite = new Paint(1);
        this.pnGridWhite.setColor(-1);
        this.bgGradient = new backgroundDrawableHolder();
        setBackgroundDrawable(this.bgGradient);
        this.backgroundCheckers = getResources().getDrawable(R.drawable.checkered_bg);
    }

    /* access modifiers changed from: package-private */
    public void drawGrid(Rect area, Canvas canvas) {
        Paint paint;
        for (int row = 0; this.gridSquare * row < area.height(); row++) {
            for (int col = 0; this.gridSquare * col < area.width(); col++) {
                int left = col * this.gridSquare;
                int top = row * this.gridSquare;
                int right = Math.min(this.gridSquare + left, area.right);
                int bottom = Math.min(this.gridSquare + top, area.bottom);
                boolean A = col % 2 == 0;
                boolean B = row % 2 == 0;
                Rect rectDraw = new Rect(left, top, right, bottom);
                if (A == B) {
                    paint = this.pnGridBlack;
                } else {
                    paint = this.pnGridWhite;
                }
                canvas.drawRect(rectDraw, paint);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int dpToPixels(int dp) {
        return (int) TypedValue.applyDimension(1, (float) dp, getResources().getDisplayMetrics());
    }

    /* access modifiers changed from: package-private */
    public void setCurrentColorPosition(int position) {
        if (this.lastSelectedHandle != null) {
            LayoutParams layoutParams = (LayoutParams) this.lastSelectedHandle.getLayoutParams();
            this.lastSelectedHandle.setPosition(position);
            layoutParams.leftMargin = (int) (((float) ((getWidth() - this.handleW) * position)) / 1000.0f);
            layoutParams.rightMargin = -250;
            this.lastSelectedHandle.setLayoutParams(layoutParams);
            requestLayout();
        }
    }

    /* access modifiers changed from: package-private */
    public void addNewColor(int color, int position) {
        colorHandle newColor = new colorHandle(getContext(), color, position, getWidth(), getHeight(), this.backgroundCheckers);
        LayoutParams params = new LayoutParams(-2, -2);
        params.topMargin = 0;
        params.leftMargin = (int) (((float) ((getWidth() - this.handleW) * position)) / 1000.0f);
        params.rightMargin = -250;
        params.bottomMargin = -250;
        if (this.lastSelectedHandle != null) {
            this.lastSelectedHandle.toggleSelect(false);
        }
        this.lastSelectedHandle = newColor;
        newColor.setTouchListener(this);
        this.colorHandles.add(newColor);
        addView(newColor, params);
        gradientChanged();
    }

    /* access modifiers changed from: package-private */
    public void gradientColorsChanged() {
        this.colors = new int[this.colorHandles.size()];
        this.positions = new float[this.colorHandles.size()];
        int i = 0;
        Iterator<colorHandle> it = this.colorHandles.iterator();
        while (it.hasNext()) {
            colorHandle aColor = it.next();
            this.colors[i] = aColor.getColor();
            this.positions[i] = ((float) aColor.getPosition()) / 1000.0f;
            i++;
        }
        this.bgGradient.updateGradient(this.colors, this.positions);
        gradientDoneEditing();
    }

    /* access modifiers changed from: package-private */
    public void gradientChanged() {
        Collections.sort(this.colorHandles);
        this.colors = new int[this.colorHandles.size()];
        this.positions = new float[this.colorHandles.size()];
        int i = 0;
        Iterator<colorHandle> it = this.colorHandles.iterator();
        while (it.hasNext()) {
            colorHandle aColor = it.next();
            this.colors[i] = aColor.getColor();
            this.positions[i] = ((float) aColor.getPosition()) / 1000.0f;
            i++;
        }
        this.bgGradient.updateGradient(this.colors, this.positions);
        if (this.sliderControls != null) {
            this.sliderControls.selectionChanged();
        }
        gradientDoneEditing();
    }

    public void addNewColor() {
        int nextPos = 0;
        if (this.lastSelectedHandle != null) {
            int lastPos = this.lastSelectedHandle.getPosition();
            int lastColor = this.lastSelectedHandle.getColor();
            int secondColor = 0;
            boolean foundAHandle = false;
            boolean directionPositive = lastPos <= 500;
            if (directionPositive) {
                nextPos = 1000;
            }
            int tmpDist = Math.abs(nextPos - lastPos);
            int childCount = getChildCount();
            if (directionPositive) {
                for (int i = 0; i < childCount; i++) {
                    colorHandle child = (colorHandle) getChildAt(i);
                    if (child.getPosition() > lastPos && Math.abs(child.getPosition() - lastPos) <= tmpDist) {
                        nextPos = child.getPosition();
                        tmpDist = Math.abs(child.getPosition() - lastPos);
                        foundAHandle = true;
                        secondColor = child.getColor();
                    }
                }
            } else {
                for (int i2 = 0; i2 < childCount; i2++) {
                    colorHandle child2 = (colorHandle) getChildAt(i2);
                    if (child2.getPosition() < lastPos && Math.abs(child2.getPosition() - lastPos) <= tmpDist) {
                        nextPos = child2.getPosition();
                        tmpDist = Math.abs(child2.getPosition() - lastPos);
                        foundAHandle = true;
                        secondColor = child2.getColor();
                    }
                }
            }
            int currentPos = (nextPos + lastPos) / 2;
            if (foundAHandle) {
                lastColor = averageTwoColors(lastColor, secondColor);
            }
            addNewColor(lastColor, currentPos);
        }
    }

    /* access modifiers changed from: package-private */
    public int averageTwoColors(int color1, int color2) {
        return Color.argb((Color.alpha(color1) + Color.alpha(color2)) / 2, (Color.red(color1) + Color.red(color2)) / 2, (Color.green(color1) + Color.green(color2)) / 2, (Color.blue(color1) + Color.blue(color2)) / 2);
    }

    /* access modifiers changed from: package-private */
    public void selectRight() {
        if (this.lastSelectedHandle != null) {
            int tmpDistance = PointerIconCompat.TYPE_CONTEXT_MENU;
            colorHandle foundHandle = null;
            int currentPosition = this.lastSelectedHandle.getPosition();
            Iterator<colorHandle> it = this.colorHandles.iterator();
            while (it.hasNext()) {
                colorHandle aColor = it.next();
                if (aColor.getPosition() > currentPosition && Math.abs(aColor.getPosition() - currentPosition) <= tmpDistance && aColor != this.lastSelectedHandle) {
                    tmpDistance = Math.abs(aColor.getPosition() - currentPosition);
                    foundHandle = aColor;
                }
            }
            if (foundHandle != null) {
                this.lastSelectedHandle.toggleSelect(false);
                foundHandle.toggleSelect(true);
                this.lastSelectedHandle = foundHandle;
                this.lastSelectedHandle.bringToFront();
                requestLayout();
                if (this.sliderControls != null) {
                    this.sliderControls.selectionChanged();
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void selectLeft() {
        if (this.lastSelectedHandle != null) {
            int tmpDistance = PointerIconCompat.TYPE_CONTEXT_MENU;
            colorHandle foundHandle = null;
            int currentPosition = this.lastSelectedHandle.getPosition();
            Iterator<colorHandle> it = this.colorHandles.iterator();
            while (it.hasNext()) {
                colorHandle aColor = it.next();
                if (aColor.getPosition() < currentPosition && Math.abs(aColor.getPosition() - currentPosition) <= tmpDistance && aColor != this.lastSelectedHandle) {
                    tmpDistance = Math.abs(aColor.getPosition() - currentPosition);
                    foundHandle = aColor;
                }
            }
            if (foundHandle != null) {
                this.lastSelectedHandle.toggleSelect(false);
                foundHandle.toggleSelect(true);
                this.lastSelectedHandle = foundHandle;
                this.lastSelectedHandle.bringToFront();
                requestLayout();
                if (this.sliderControls != null) {
                    this.sliderControls.selectionChanged();
                }
            }
        }
    }

    @Override // com.imaginstudio.imagetools.pixellab.controls.widgets.colorHandle.OnHandleTouchListener
    public void onHandleTouchEvent(colorHandle caller) {
        if (caller != null) {
            if (this.lastSelectedHandle != null) {
                this.lastSelectedHandle.toggleSelect(false);
            }
            caller.toggleSelect(true);
            this.lastSelectedHandle = caller;
            caller.bringToFront();
            requestLayout();
            if (this.sliderControls != null) {
                this.sliderControls.selectionChanged();
            }
        }
    }

    @Override // com.imaginstudio.imagetools.pixellab.controls.widgets.colorHandle.OnHandleTouchListener
    public void onHandleMoveEvent() {
        gradientChanged();
    }

    @Override // com.imaginstudio.imagetools.pixellab.controls.widgets.colorHandle.OnHandleTouchListener
    public void onHandleDoneEvent() {
    }

    public void removeCurrentColor() {
        if (getChildCount() > 2) {
            this.colorHandles.remove(this.lastSelectedHandle);
            removeView(this.lastSelectedHandle);
            this.lastSelectedHandle = (colorHandle) getChildAt(getChildCount() - 1);
            this.lastSelectedHandle.toggleSelect(true);
            this.lastSelectedHandle.bringToFront();
            requestLayout();
            gradientChanged();
        }
    }
}

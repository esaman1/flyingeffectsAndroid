package com.imaginstudio.imagetools.pixellab;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import com.imaginstudio.imagetools.R;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.view.ViewCompat;

public class colorButton extends AppCompatImageButton {
    public static final int TYPE_COLOR = 1;
    public static final int TYPE_GRADIENT = 2;
    Drawable backgroundCheckers;
    Bitmap backgroundCheckersBitmap;
    int displayColor;
    GradientMaker.GradientFill displayGradient;
    public boolean isSquare = false;
    public colorChangeNotify mColorChangeNotify;
    String numberToViewStr = "1";
    boolean numberView = false;
    Paint pntCheckers = null;
    Paint pntText = new Paint(1);
    private boolean strokeEnabled = true;
    int strokeWidth = dpToPixels(1);
    Rect textArea = new Rect();
    private Rect textBounds = new Rect();
    int type;

    public interface colorChangeNotify {
        void colorDisplayChanged(int i);
    }

    public void disableStroke() {
        this.strokeEnabled = false;
    }

    public void setOnColorNotifyChange(colorChangeNotify mNotifier) {
        this.mColorChangeNotify = mNotifier;
    }

    public void toggleNumberView(boolean enable) {
        this.numberView = enable;
        invalidate();
    }

    public void setNumberToView(int n) {
        this.numberToViewStr = String.valueOf(n);
        invalidate();
    }

    public colorButton(Context context, int type2, int displayColor2, GradientMaker.GradientFill displayGradient2, boolean isSquare2) {
        super(context);
        this.type = type2;
        this.displayColor = displayColor2;
        this.displayGradient = displayGradient2 != null ? displayGradient2.copy() : null;
        setBackgroundDrawable(null);
        this.isSquare = isSquare2;
        if (isSquare2) {
            this.backgroundCheckers = getResources().getDrawable(R.drawable.checkered_bg);
        } else {
            this.backgroundCheckersBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.checkered);
            this.pntCheckers = new Paint(1);
            this.pntCheckers.setFilterBitmap(true);
            this.pntCheckers.setDither(true);
            this.pntCheckers.setShader(new BitmapShader(this.backgroundCheckersBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
        }
        this.pntText.setTextAlign(Paint.Align.CENTER);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Rect displayBounds = new Rect(0, 0, getWidth(), getHeight());
        Paint pnt = new Paint(1);
        pnt.setAlpha(255);
        if (!this.isSquare) {
            if (this.type == 1) {
                pnt.setColor(!this.numberView ? this.displayColor : commonFuncs.stripAlpha(this.displayColor));
            } else if (this.type == 2) {
                pnt.setShader(this.displayGradient.getShader(displayBounds));
            }
            if (!(this.backgroundCheckersBitmap == null || this.pntCheckers == null || !this.strokeEnabled)) {
                canvas.drawCircle(((float) getWidth()) / 2.0f, ((float) getHeight()) / 2.0f, (float) (displayBounds.centerX() - (dpToPixels(2) * 2)), this.pntCheckers);
            }
            Paint pntStroke = new Paint(1);
            pntStroke.setDither(true);
            pntStroke.setStyle(Paint.Style.STROKE);
            pntStroke.setStrokeWidth((float) dpToPixels(2));
            pntStroke.setColor(ViewCompat.MEASURED_STATE_MASK);
            pntStroke.setAlpha(100);
            if (this.strokeEnabled) {
                canvas.drawCircle(((float) getWidth()) / 2.0f, ((float) getHeight()) / 2.0f, (float) (displayBounds.centerX() - dpToPixels(2)), pntStroke);
            }
            canvas.drawCircle(((float) getWidth()) / 2.0f, ((float) getHeight()) / 2.0f, (float) (displayBounds.centerX() - dpToPixels(2)), pnt);
        } else {
            Rect availableArea = new Rect(displayBounds);
            Paint pntStroke2 = new Paint(1);
            pntStroke2.setColor(-7829368);
            canvas.drawRect(displayBounds, pntStroke2);
            displayBounds.inset(this.strokeWidth, this.strokeWidth);
            pntStroke2.setColor(-3355444);
            canvas.drawRect(displayBounds, pntStroke2);
            displayBounds.inset(this.strokeWidth, this.strokeWidth);
            if (this.type == 1) {
                pnt.setColor(!this.numberView ? this.displayColor : commonFuncs.stripAlpha(this.displayColor));
            } else if (this.type == 2) {
                pnt.setShader(this.displayGradient.getShader(availableArea));
            }
            this.backgroundCheckers.setBounds(displayBounds);
            this.backgroundCheckers.draw(canvas);
            canvas.drawRect(displayBounds, pnt);
        }
        if (this.numberView) {
            this.pntText.setColor(commonFuncs.getContrastColor(this.displayColor));
            this.textBounds = commonFuncs.fitTextInRect(this.numberToViewStr, this.pntText, this.textArea);
            canvas.drawText(this.numberToViewStr, ((float) getWidth()) * 0.5f, this.textArea.exactCenterY() - this.textBounds.exactCenterY(), this.pntText);
        }
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type2) {
        this.type = type2;
    }

    public GradientMaker.GradientFill getDisplayGradient() {
        return this.displayGradient;
    }

    public void setDisplayGradient(GradientMaker.GradientFill displayGradient2) {
        this.displayGradient = displayGradient2 != null ? displayGradient2.copy() : null;
    }

    public int getDisplayColor() {
        return this.displayColor;
    }

    public void setDisplayColor(int displayColor2) {
        this.displayColor = displayColor2;
        invalidate();
        if (this.mColorChangeNotify != null) {
            this.mColorChangeNotify.colorDisplayChanged(displayColor2);
        }
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.textArea = new Rect(0, 0, w, h);
        this.textArea.inset((int) (((float) this.textArea.width()) * 0.3f), (int) (((float) this.textArea.height()) * 0.3f));
    }

    /* access modifiers changed from: package-private */
    public int dpToPixels(int dp) {
        return (int) TypedValue.applyDimension(1, (float) dp, getResources().getDisplayMetrics());
    }
}

package com.imaginstudio.imagetools.pixellab.TextObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Layout;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.text.style.ScaleXSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.Pair;
import android.widget.TextView;

import com.imaginstudio.imagetools.R;
import com.imaginstudio.imagetools.pixellab.font.CustomTypefaceSpan;
import com.imaginstudio.imagetools.pixellab.functions.interval;
import com.imaginstudio.imagetools.pixellab.functions.spansIntervals;


import java.util.WeakHashMap;

import androidx.core.view.ViewCompat;

@SuppressLint({"AppCompatCustomView"})
public class CustomTextView extends TextView {
    public static final int FILL_TYPE_COLOR = 1;
    public static final int FILL_TYPE_GRADIENT = 2;
    int additionalHeightSpace;
    int additionalWidthSpace;
    float arcChord;
    float arcHeight;
    spansIntervals boldIds = new spansIntervals();
    private WeakHashMap<String, Pair<Canvas, Bitmap>> canvasStore;
    spansIntervals colorIds = new spansIntervals();
    curveMode currentCurveMode = curveMode.halfCircle;
    Point curveCenter;
    public int curveDegree = 0;
    TextPaint curvePaint;
    double curveRadian;
    float curveRadius;
    private CurveText curveText = new CurveText();
    int defaultColor = ViewCompat.MEASURED_STATE_MASK;
    public onFinishDraw drawMaskListener = null;
    EmbossMaskFilter embossFilter = null;
    spansIntervals fontsIds = new spansIntervals();
    private boolean frozen = false;
    Rect gradient_bounds = null;
    int inPaddingLeft = 0;
    int inPaddingRight = 0;
    private boolean includeEmbossInStroke = false;
    public int inner_color = ViewCompat.MEASURED_STATE_MASK;
    public float inner_dx = 0.0f;
    public float inner_dy = 0.0f;
    boolean inner_enabled;
    public float inner_r = 3.0f;
    public boolean isCurved = false;
    spansIntervals italicIds = new spansIntervals();
    private boolean justifyAlign = false;
    private int letterSpacing = 0;
    private int maxDim = 100;
    int offsetY;
    private String originalText = "";
    Path path;
    Path.Direction pathDirection;
    public int spacing = 0;
    public int strokeColor = ViewCompat.MEASURED_STATE_MASK;
    public int strokeFillType = 1;
//    public GradientMaker.GradientFill strokeGradient = new GradientMaker.GradientFill();
    public float strokeWidth = 4.0f;
    public boolean stroke_enabled;
    private Bitmap tempBitmap;
    private Canvas tempCanvas;
    float textWidthLength;
    private BitmapShader textureShader = null;
    int tmpBottom = 0;
    int tmpLeft = 0;
    int tmpRight = 0;
    int tmpTop = 0;
    public boolean tmp_drawing3DDepth = false;
    String unCurvedText;
    spansIntervals underIds = new spansIntervals();
    public double userMaxWidth = -1.0d;
    public float userTextSize = 20.0f;
    private boolean wordSpacing = false;

    /* access modifiers changed from: package-private */
    public enum curveMode {
        halfCircle,
        fullCircle,
        twoHalves
    }

    public interface onFinishDraw {
        void done(Canvas canvas);
    }

    public void setMaskFilter(EmbossMaskFilter filter) {
        if (Build.VERSION.SDK_INT >= 26) {
            this.embossFilter = filter;
        } else {
            getPaint().setMaskFilter(filter);
        }
    }

    static int maxList(float... num) {
        float max = num[0];
        for (int i = 1; i < num.length; i++) {
            if (num[i] > max) {
                max = num[i];
            }
        }
        return (int) max;
    }

    public void updatePaddings() {
        float f;
        float f2;
        float f3;
        float f4;
        float f5;
        int paddingLeft = getPaddingLeft() - this.tmpLeft;
        float[] fArr = new float[2];
        if (this.stroke_enabled) {
            f = this.strokeWidth * 0.5f;
        } else {
            f = 0.0f;
        }
        fArr[0] = f;
        fArr[1] = 5.0f;
        int finalPaddingLeft = maxList(fArr) + paddingLeft + this.inPaddingLeft;
        int paddingRight = getPaddingRight() - this.tmpRight;
        float[] fArr2 = new float[2];
        if (this.stroke_enabled) {
            f2 = this.strokeWidth * 0.5f;
        } else {
            f2 = 0.0f;
        }
        fArr2[0] = f2;
        fArr2[1] = 5.0f;
        int finalPaddingRight = maxList(fArr2) + paddingRight + this.inPaddingRight;
        int paddingTop = getPaddingTop() - this.tmpTop;
        float[] fArr3 = new float[1];
        if (this.stroke_enabled) {
            f3 = this.strokeWidth * 0.5f;
        } else {
            f3 = 0.0f;
        }
        fArr3[0] = f3;
        int finalPaddingTop = paddingTop + maxList(fArr3);
        int paddingBottom = getPaddingBottom() - this.tmpBottom;
        float[] fArr4 = new float[2];
        if (this.stroke_enabled) {
            f4 = this.strokeWidth * 0.5f;
        } else {
            f4 = 0.0f;
        }
        fArr4[0] = f4;
        fArr4[1] = negativePart((float) this.spacing);
        int finalPaddingBottom = paddingBottom + maxList(fArr4);
        this.tmpLeft = finalPaddingLeft;
        this.tmpTop = finalPaddingTop;
        this.tmpRight = finalPaddingRight;
        this.tmpBottom = finalPaddingBottom;
        if (this.inPaddingLeft > 0) {
            f5 = 1.0f;
        } else {
            f5 = 0.0f;
        }
        setShadowLayer(f5, (float) (-finalPaddingLeft), 0.0f, Color.argb(0, 0, 0, 0));
        setPadding(finalPaddingLeft, finalPaddingTop, finalPaddingRight, finalPaddingBottom);
        textUpdated();
    }

    public CustomTextView(Context context, int maxDim2) {
        super(context);
        this.maxDim = maxDim2;
        if (this.canvasStore == null) {
            this.canvasStore = new WeakHashMap<>();
        }
        setMinWidth(1);
        updateTextSize(context.getResources().getDisplayMetrics().scaledDensity * this.userTextSize);
        updateText("updateText");
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        setIncludeFontPadding(true);
    }

    /* access modifiers changed from: package-private */
    public float negativePart(float val) {
        if (val >= 0.0f) {
            return 0.0f;
        }
        return Math.abs(val);
    }

    public void setLineSpacing(float add, float mult) {
        super.setLineSpacing(add, mult);
        updatePaddings();
    }

    public int getLetterSpace() {
        return this.letterSpacing;
    }

    public boolean isWordSpacing() {
        return this.wordSpacing;
    }

    public void setWordSpacing(boolean words) {
        this.wordSpacing = words;
        updateText(this.originalText);
    }

    public void setLetterSpace(int letterSpacing2) {
        if (letterSpacing2 != 0) {
            this.justifyAlign = false;
        }
        if ((this.letterSpacing == 0 || letterSpacing2 == 0) && this.letterSpacing != letterSpacing2) {
            this.letterSpacing = letterSpacing2;
            updateText(this.originalText);
            return;
        }
        this.letterSpacing = letterSpacing2;
        applySpacing();
    }

    public void applySpacing() {
        applySpacing((SpannableString) getText());
    }

    public void applySpacing(SpannableString textReference) {
        for (ScaleXSpan span : (ScaleXSpan[]) textReference.getSpans(0, getText().length(), ScaleXSpan.class)) {
            textReference.removeSpan(span);
        }
        if (getText().length() > 1 && this.letterSpacing != 0) {
            if (this.wordSpacing) {
                float spacing2 = 1.0f + (((float) this.letterSpacing) / 10.0f);
                for (int i = 1; i < getText().length(); i++) {
                    char c = getText().charAt(i);
                    if (Character.isSpaceChar(c) || Character.isWhitespace(c)) {
                        textReference.setSpan(new ScaleXSpan(spacing2), i, i + 1, 33);
                    }
                }
            } else if (useNewLetterSpacing()) {
                setLetterSpacing(((float) this.letterSpacing) / 100.0f);
            } else {
                float spacing3 = ((float) this.letterSpacing) / 10.0f;
                for (int i2 = 1; i2 < getText().length(); i2 += 2) {
                    textReference.setSpan(new ScaleXSpan(spacing3), i2, i2 + 1, 33);
                }
            }
        }
        textUpdated();
    }

    /* access modifiers changed from: package-private */
    public void applyJustifyAlign() {
        applyJustifyAlign((SpannableString) getText());
    }

    /* access modifiers changed from: package-private */
    public void applyJustifyAlign(SpannableString textReference) {
        for (ScaleXSpan span : (ScaleXSpan[]) textReference.getSpans(0, getText().length(), ScaleXSpan.class)) {
            textReference.removeSpan(span);
        }
        if (this.justifyAlign) {
            try {
                Layout layout = getLayout();
                int start = 0;
                for (int l = 0; l < getLineCount() - 1; l++) {
                    int end = layout.getLineEnd(l);
                    int spacesNum = 0;
                    for (int i = start + 1; i < end - 1; i++) {
                        if (getText().charAt(i) == ' ') {
                            spacesNum++;
                        }
                    }
                    float spaceScaleX = 1.0f + ((((float) ((getMeasuredWidth() - getCompoundPaddingLeft()) - getCompoundPaddingRight())) - Layout.getDesiredWidth(getText(), start, end, getLayout().getPaint())) / (((float) spacesNum) * getPaint().measureText(" ")));
                    if (spaceScaleX > 1.0f) {
                        for (int i2 = start + 1; i2 < end - 1; i2++) {
                            if (getText().charAt(i2) == ' ') {
                                textReference.setSpan(new ScaleXSpan(spaceScaleX), i2, i2 + 1, 33);
                            }
                        }
                    }
                    start = end;
                }
            } catch (Exception e) {
                for (ScaleXSpan span2 : (ScaleXSpan[]) textReference.getSpans(0, getText().length(), ScaleXSpan.class)) {
                    textReference.removeSpan(span2);
                }
                e.printStackTrace();
            }
        }
        invalidate();
    }

    public void updateText(String text, int oldTextLength) {
        if (oldTextLength == -1) {
            oldTextLength = this.originalText.length();
        }
        updateEnds(oldTextLength, text.length());
        this.originalText = text;
        if (this.letterSpacing == 0 || this.wordSpacing) {
            if (useNewLetterSpacing()) {
                setLetterSpacing(0.0f);
            }
            setText(text, BufferType.SPANNABLE);
        } else if (!useNewLetterSpacing()) {
            setText(this.originalText.replaceAll(".(?=.)", "$0 "), BufferType.SPANNABLE);
        } else if (useNewLetterSpacing()) {
            setText(text, BufferType.SPANNABLE);
        }
        applySpans();
        textUpdated();
    }

    public void updateText(String text) {
        updateText(text, this.originalText.length());
    }

    /* access modifiers changed from: package-private */
    public void updateEnds(int oldEnd, int newEnd) {
        this.fontsIds.updateEnds(oldEnd, newEnd);
        this.colorIds.updateEnds(oldEnd, newEnd);
        this.boldIds.updateEnds(oldEnd, newEnd);
        this.italicIds.updateEnds(oldEnd, newEnd);
        this.underIds.updateEnds(oldEnd, newEnd);
    }

    /* access modifiers changed from: package-private */
    public void printRect(Rect rect, int i) {
        Log.d("justify", "line : " + i + " : Left : " + rect.left + ", top : " + rect.top + ", right : " + rect.right + ", bottom : " + rect.bottom);
    }

    public void applySpans() {
        SpannableString textReference = (SpannableString) getText();
        applySpacing(textReference);
        removeSpans(textReference);
        for (interval i : this.fontsIds.getIntervals()) {
            int start = Math.max(0, offsetStart(i.getStart()));
            int end = Math.min(offsetEnd(i.getStart(), i.getEnd(), start), getText().length());
            if (start < getText().length() && end >= start) {
                textReference.setSpan(new CustomTypefaceSpan(i.getFont().getTypeface()), start, end, 34);
            }
        }
        for (interval i2 : this.colorIds.getIntervals()) {
            int start2 = Math.max(0, offsetStart(i2.getStart()));
            int end2 = Math.min(offsetEnd(i2.getStart(), i2.getEnd(), start2), getText().length());
            if (start2 < getText().length() && end2 >= start2) {
                textReference.setSpan(new ForegroundColorSpan(i2.getColor()), start2, end2, 33);
            }
        }
        for (interval i3 : this.boldIds.getIntervals()) {
            int start3 = Math.max(0, offsetStart(i3.getStart()));
            int end3 = Math.min(offsetEnd(i3.getStart(), i3.getEnd(), start3), getText().length());
            if (start3 < getText().length() && end3 >= start3) {
                textReference.setSpan(new StyleSpan(1), start3, end3, 33);
            }
        }
        for (interval i4 : this.italicIds.getIntervals()) {
            int start4 = Math.max(0, offsetStart(i4.getStart()));
            int end4 = Math.min(offsetEnd(i4.getStart(), i4.getEnd(), start4), getText().length());
            if (start4 < getText().length() && end4 >= start4) {
                textReference.setSpan(new StyleSpan(2), start4, end4, 33);
            }
        }
        for (interval i5 : this.underIds.getIntervals()) {
            int start5 = Math.max(0, offsetStart(i5.getStart()));
            int end5 = Math.min(offsetEnd(i5.getStart(), i5.getEnd(), start5), getText().length());
            if (start5 < getText().length() && end5 >= start5) {
                textReference.setSpan(new UnderlineSpan(), start5, end5, 33);
            }
        }
    }

    public int offsetStart(int start) {
        return (this.letterSpacing == 0 || this.wordSpacing || useNewLetterSpacing()) ? start : (start * 2) - 1;
    }

    public int offsetEnd(int oldStart, int oldEnd, int newStart) {
        return (this.letterSpacing == 0 || this.wordSpacing || useNewLetterSpacing()) ? oldEnd : newStart + ((oldEnd - oldStart) * 2);
    }

    private void removeSpans(SpannableString textReference) {
        for (CustomTypefaceSpan span : (CustomTypefaceSpan[]) textReference.getSpans(0, getText().length(), CustomTypefaceSpan.class)) {
            textReference.removeSpan(span);
        }
        for (ForegroundColorSpan span2 : (ForegroundColorSpan[]) textReference.getSpans(0, getText().length(), ForegroundColorSpan.class)) {
            textReference.removeSpan(span2);
        }
        for (StyleSpan span3 : (StyleSpan[]) textReference.getSpans(0, getText().length(), StyleSpan.class)) {
            textReference.removeSpan(span3);
        }
        for (UnderlineSpan span4 : (UnderlineSpan[]) textReference.getSpans(0, getText().length(), UnderlineSpan.class)) {
            textReference.removeSpan(span4);
        }
    }

    public static boolean useNewLetterSpacing() {
        return Build.VERSION.SDK_INT >= 21;
    }

    public void removeColorSpans() {
        SpannableString textReference = (SpannableString) getText();
        for (ForegroundColorSpan span : (ForegroundColorSpan[]) textReference.getSpans(0, getText().length(), ForegroundColorSpan.class)) {
            textReference.removeSpan(span);
        }
    }

    public void restoreColorSpans() {
        SpannableString textReference = (SpannableString) getText();
        for (interval i : this.colorIds.getIntervals()) {
            int start = Math.max(0, offsetStart(i.getStart()));
            int end = Math.min(offsetEnd(i.getStart(), i.getEnd(), start), getText().length());
            if (start < getText().length()) {
                textReference.setSpan(new ForegroundColorSpan(i.getColor()), start, end, 33);
            }
        }
    }

    public void textUpdated() {
        if (this.isCurved) {
            setCurve(this.curveDegree);
        }
        requestLayout();
        postInvalidate();
    }

    public String returnActualText() {
        return this.originalText;
    }

    public int getTextLength() {
        if (this.originalText != null) {
            return this.originalText.length();
        }
        return 0;
    }

    public void updateTextSize(float newSize) {
        double oldSize = (double) this.userTextSize;
        setTextSize(0, newSize);
        this.userTextSize = newSize;
        if (this.userMaxWidth != -1.0d) {
            this.userMaxWidth *= ((double) newSize) / oldSize;
            setMaxWidth((int) this.userMaxWidth);
        }
        textUpdated();
    }

    public void clearStroke() {
        this.stroke_enabled = false;
        textUpdated();
    }

//    public void setStrokeFill(boolean enabled, float width, int strokeFillType2, int color, GradientMaker.GradientFill gradient) {
//        this.stroke_enabled = enabled;
//        if (this.stroke_enabled) {
//            if (width == 0.0f) {
//                width = 0.1f;
//            }
//            this.strokeWidth = width;
//            this.strokeColor = color;
//            this.strokeFillType = strokeFillType2;
//            this.strokeGradient = gradient;
//        }
//        updatePaddings();
//    }

    public void setInnerShadow(boolean enabled, float radius, float dx, float dy, int color) {
        this.inner_enabled = enabled;
        if (this.inner_enabled) {
            this.inner_r = Math.max(radius, 0.1f);
            this.inner_dx = dx;
            this.inner_dy = dy;
            this.inner_color = color;
        }
        invalidate();
    }

        @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    public void onDraw(Canvas canvas) {
        int strokeColorTmp;
        Bitmap strokeMap;
        if (this.letterSpacing == 0) {
            applyJustifyAlign();
        }
        float[] f = new float[9];
        canvas.getMatrix().getValues(f);
        float scaleX = Math.max(1.0f, f[0]);
        float scaleY = Math.max(1.0f, f[4]);
        getPaint().setAntiAlias(true);
        getPaint().setDither(true);
        if (this.stroke_enabled) {
            MaskFilter oldEmboss = getPaint().getMaskFilter();
            if (!this.includeEmbossInStroke) {
                getPaint().setMaskFilter(null);
            }
            freeze();
            removeColorSpans();
            int restoreColor = getCurrentTextColor();
            TextPaint strokePaint = new TextPaint();
            strokePaint.setAntiAlias(true);
            strokePaint.setDither(true);
            strokePaint.setFilterBitmap(true);
            TextPaint bakPaint = new TextPaint();
            bakPaint.set(getPaint());
            strokePaint.set(getPaint());
            strokePaint.setStyle(Paint.Style.STROKE);
            strokePaint.setStrokeJoin(Paint.Join.ROUND);
            strokePaint.setStrokeCap(Paint.Cap.ROUND);
            if (this.strokeFillType != 2 || this.gradient_bounds == null) {
                strokeColorTmp = this.strokeColor;
            } else {
                strokeColorTmp = ViewCompat.MEASURED_STATE_MASK;
            }
            setTextColor(strokeColorTmp);
            strokePaint.setStrokeWidth(this.strokeWidth);
            strokePaint.setColor(strokeColorTmp);
            superCanvas canvas_withNoClipping = new superCanvas();
            if (Build.VERSION.SDK_INT == 19) {
                float scaleCorrectedKK = (float) Math.min(255.0d / ((double) getTextSize()), (double) Math.max(scaleX, scaleY));
                strokeMap = Bitmap.createBitmap((int) (((float) getMeasuredWidth()) * scaleCorrectedKK), (int) (((float) getMeasuredHeight()) * scaleCorrectedKK), Bitmap.Config.ARGB_8888);
                canvas_withNoClipping.scale(scaleCorrectedKK, scaleCorrectedKK);
            } else {
                strokeMap = Bitmap.createBitmap((int) (((float) getMeasuredWidth()) * scaleX), (int) (((float) getMeasuredHeight()) * scaleY), Bitmap.Config.ARGB_8888);
                canvas_withNoClipping.scale(scaleX, scaleY);
            }
            canvas_withNoClipping.setBitmap(strokeMap);
            strokePaint.setShader(null);
            if (this.strokeFillType == 2 && this.gradient_bounds != null) {
                strokePaint.setAlpha(255);
//                strokePaint.setShader(this.strokeGradient.getShader(this.gradient_bounds));
            }
            getPaint().set(strokePaint);
            if (this.isCurved) {
                this.curvePaint = new TextPaint(getPaint());
                applyFontToCurve();
            }
            drawStuff(canvas_withNoClipping);
            Paint pn = new Paint();
            pn.setAntiAlias(true);
            pn.setDither(true);
            pn.setFilterBitmap(true);
            canvas.drawBitmap(strokeMap, (Rect) null, new RectF(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight()), pn);
            strokeMap.recycle();
            getPaint().set(bakPaint);
            setTextColor(restoreColor);
            restoreColorSpans();
            if (!this.includeEmbossInStroke) {
                getPaint().setMaskFilter(oldEmboss);
            }
            unfreeze();
            super.invalidate();
            super.requestLayout();
        }
        if (this.isCurved) {
            this.curvePaint = new TextPaint(getPaint());
            applyFontToCurve();
            applyColorToCurve();
        }
        TextPaint paintBak = new TextPaint();
        if (this.textureShader != null) {
            paintBak.set(getPaint());
            getPaint().setShader(this.textureShader);
        }
        drawStuff(canvas);
        if (this.textureShader != null) {
            getPaint().set(paintBak);
        }
        if (this.inner_enabled) {
            freeze();
            removeColorSpans();
            int restoreColor2 = getCurrentTextColor();
            TextPaint innerPaint = new TextPaint();
            innerPaint.setAntiAlias(true);
            innerPaint.setDither(true);
            innerPaint.setFilterBitmap(true);
            TextPaint bakPaint2 = new TextPaint();
            bakPaint2.set(getPaint());
            innerPaint.set(getPaint());
            innerPaint.setShader(null);
            generateTempCanvas(scaleX, scaleY);
            setTextColor(this.inner_color);
            innerPaint.setColor(this.inner_color);
            innerPaint.setShader(null);
            getPaint().set(innerPaint);
            if (this.isCurved) {
                this.curvePaint = new TextPaint(getPaint());
                applyFontToCurve();
            }
            drawStuff(this.tempCanvas);
            setTextColor(ViewCompat.MEASURED_STATE_MASK);
            innerPaint.setColor(ViewCompat.MEASURED_STATE_MASK);
            innerPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            innerPaint.setMaskFilter(new BlurMaskFilter(this.inner_r, BlurMaskFilter.Blur.NORMAL));
            this.tempCanvas.save();
            this.tempCanvas.translate(this.inner_dx, this.inner_dy);
            getPaint().set(innerPaint);
            if (this.isCurved) {
                this.curvePaint = new TextPaint(getPaint());
                applyFontToCurve();
            }
            drawStuff(this.tempCanvas);
            try {
                this.tempCanvas.restore();
            } catch (IllegalStateException e) {
            }
            Paint bitmapPaint = new Paint();
            bitmapPaint.setFilterBitmap(true);
            bitmapPaint.setDither(true);
            bitmapPaint.setAntiAlias(true);
            canvas.drawBitmap(this.tempBitmap, (Rect) null, new Rect(0, 0, getWidth(), getHeight()), bitmapPaint);
            this.tempCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
            getPaint().set(bakPaint2);
            setTextColor(restoreColor2);
            restoreColorSpans();
            unfreeze();
            invalidate();
        }
    }

        @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public void draw(Canvas canvas) {
        if (this.embossFilter != null) {
            float[] f = new float[9];
            canvas.getMatrix().getValues(f);
            Bitmap bmp = Bitmap.createBitmap((int) (((float) getWidth()) * Math.max(1.0f, f[0])), (int) (((float) getHeight()) * Math.max(1.0f, f[4])), Bitmap.Config.ARGB_8888);
            super.draw(new Canvas(bmp));
            Bitmap bmpAlpha = bmp.extractAlpha();
            Paint ptEmboss = new Paint();
            ptEmboss.setMaskFilter(this.embossFilter);
            ptEmboss.setDither(true);
            ptEmboss.setAntiAlias(true);
            ptEmboss.setFilterBitmap(true);
            ptEmboss.setShader(new BitmapShader(bmp, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
            canvas.drawBitmap(bmpAlpha, 0.0f, 0.0f, ptEmboss);
        } else {
            super.draw(canvas);
        }
        if (this.drawMaskListener != null) {
            this.drawMaskListener.done(canvas);
        }
    }

    @SuppressLint({"WrongCall"})
    private void drawStuff(Canvas canvas) {
        getPaint().clearShadowLayer();
        if (this.isCurved) {
            drawCurvedText(canvas);
        } else {
            super.onDraw(canvas);
        }
    }

    public void setIncludeEmbossInStroke(boolean enabled) {
        this.includeEmbossInStroke = enabled;
    }

    private void drawCurvedText(Canvas canvas) {
        this.curvePaint.setTextAlign(Paint.Align.CENTER);
        this.curvePaint.setShader(getPaint().getShader());
        if (this.currentCurveMode == curveMode.halfCircle) {
            canvas.drawTextOnPath(this.curveText.getSpacedText(), this.path, 0.0f, this.curveDegree < 0 ? (float) Math.abs(this.curvePaint.getFontMetricsInt().top) : 0.0f, this.curvePaint);
        } else if (this.currentCurveMode == curveMode.fullCircle) {
            canvas.drawTextOnPath(this.curveText.getSpacedText(), this.path, 0.0f, 0.0f, this.curvePaint);
        }
    }

    private void applyFontToCurve() {
        boolean bold = this.boldIds.isCoverAll(getTextLength());
        boolean italic = this.italicIds.isCoverAll(getTextLength());
        this.curvePaint.setFakeBoldText(bold);
        this.curvePaint.setTextSkewX(italic ? -0.25f : 0.0f);
        interval mainFont = this.fontsIds.getLastAttr();
        if (mainFont != null) {
            this.curvePaint.setTypeface(mainFont.getFont().getTypeface());
        }
    }

    private void applyColorToCurve() {
        if (!this.tmp_drawing3DDepth) {
            interval mainColor = this.colorIds.getLastAttr();
            if (mainColor != null) {
                this.curvePaint.setColor(mainColor.getColor());
                return;
            }
            return;
        }
        this.curvePaint.setColor(getTextColors().getDefaultColor());
    }

    public void setBlack() {
        ((SpannableString) getText()).setSpan(new ForegroundColorSpan((int) ViewCompat.MEASURED_STATE_MASK), 0, getTextLength(), 33);
    }

    public void setColorToSelection(int color, int start, int end) {
        this.colorIds.addInterval(new interval(start, end, color));
        applySpans();
    }

    public void setBoldToSelection(int start, int end) {
        this.boldIds.addInterval(new interval(start, end));
        applySpans();
    }

    public void setItalicToSelection(int start, int end) {
        this.italicIds.addInterval(new interval(start, end));
        applySpans();
    }

    public void setUnderToSelection(int start, int end) {
        this.underIds.addInterval(new interval(start, end));
        applySpans();
    }

    public void setFonts(spansIntervals fonts) {
        this.fontsIds = fonts;
        applySpans();
    }

    public void clearStyles(int start, int end) {
        this.boldIds.removeInterval(start, end);
        this.italicIds.removeInterval(start, end);
        this.underIds.removeInterval(start, end);
        applySpans();
    }

    public void setTextureShader(BitmapShader textTexture) {
        this.textureShader = textTexture;
    }

    public boolean isJustifyAlign() {
        return this.justifyAlign;
    }

    public void setJustifyAlign(boolean enabled) {
        this.justifyAlign = enabled;
        if (enabled) {
            setLetterSpace(0);
        }
    }

    /* access modifiers changed from: package-private */
    public class CurveText {
        int curveSpacing;
        String spacedText;
        String unspacedText;

        private CurveText() {
        }

        /* access modifiers changed from: package-private */
        public void setText(String str, int curveSpacing2) {
            if (!str.equals(this.unspacedText) || this.curveSpacing != curveSpacing2) {
                this.unspacedText = str;
                this.curveSpacing = curveSpacing2;
                applySpacing();
            }
        }

        /* access modifiers changed from: package-private */
        public String getSpacedText() {
            return this.spacedText;
        }

        /* access modifiers changed from: package-private */
        public void applySpacing() {
            if (this.curveSpacing > 0) {
                StringBuilder spaces = new StringBuilder();
                spaces.append("$0");
                for (int i = 0; i < this.curveSpacing / 10; i++) {
                    spaces.append(" ");
                }
                this.spacedText = this.unspacedText.replaceAll(CustomTextView.this.wordSpacing ? ".(?=\\s)" : ".(?=.)", spaces.toString());
            } else if (this.curveSpacing == 0) {
                this.spacedText = this.unspacedText;
            } else {
                this.spacedText = this.unspacedText.replaceAll("\\s", "");
            }
        }
    }

    public Typeface createFontFromPath(String path2) {
        Typeface t = Typeface.DEFAULT;
        try {
            return Typeface.createFromAsset(getContext().getAssets(), path2);
        } catch (Exception e) {
            try {
                return Typeface.createFromFile(path2);
            } catch (Exception e2) {
                return t;
            }
        }
    }

    public void setCurve(int param) {
        float f;
        this.isCurved = param != 0;
        if (useNewLetterSpacing() && !this.wordSpacing) {
            if (!this.isCurved) {
                setLetterSpacing(((float) this.letterSpacing) / 100.0f);
            } else {
                setLetterSpacing(0.0f);
            }
        }
        this.curveDegree = param;
        if (param != 0) {
            this.userMaxWidth = -1.0d;
            setMaxWidth(Integer.MAX_VALUE);
        }
        if (this.isCurved) {
            this.curvePaint = new TextPaint(getPaint());
            applyFontToCurve();
            this.curveText.setText(this.originalText, this.letterSpacing);
            this.unCurvedText = this.curveText.getSpacedText();
            Paint.FontMetricsInt fontMetricsInt = new Paint.FontMetricsInt();
            this.curvePaint.getFontMetricsInt(fontMetricsInt);
            this.path = new Path();
            this.pathDirection = Path.Direction.CW;
            this.textWidthLength = this.curvePaint.measureText(this.unCurvedText);
            if (this.currentCurveMode == curveMode.halfCircle) {
                this.curveRadian = Math.toRadians((double) Math.abs(this.curveDegree));
                this.curveRadius = (float) (((double) this.textWidthLength) / this.curveRadian);
                this.arcChord = 2.0f * this.curveRadius * ((float) Math.sin(this.curveRadian / 2.0d));
                this.arcHeight = (this.curveRadius + 0.0f) * (1.0f - ((float) Math.cos(this.curveRadian / 2.0d)));
                this.additionalWidthSpace = this.curveDegree > 0 ? -(this.curvePaint.getFontMetricsInt().top * 2) : ((-this.curvePaint.getFontMetricsInt().top) + this.curvePaint.getFontMetricsInt().bottom) * 2;
                this.additionalWidthSpace = (int) (((float) this.additionalWidthSpace) * ((float) Math.sin(this.curveRadian / 2.0d)));
                float f2 = (float) (this.curvePaint.getFontMetricsInt().bottom - this.curvePaint.getFontMetricsInt().top);
                if (this.curveDegree < 0) {
                    f = ((float) this.curvePaint.getFontMetricsInt().bottom) * (1.0f - ((float) Math.cos(this.curveRadian / 2.0d)));
                } else {
                    f = 0.0f;
                }
                this.additionalHeightSpace = (int) (f + f2);
                int matrixRotate = 90;
                this.offsetY = ((int) (this.curveRadius - ((float) fontMetricsInt.top))) + getPaddingTop();
                if (this.curveDegree < 0) {
                    this.pathDirection = Path.Direction.CCW;
                    matrixRotate = -90;
                    this.offsetY = (int) (-this.curveRadius);
                    this.offsetY = (int) (((float) this.offsetY) + ((float) (((int) this.arcHeight) + getPaddingTop())) + (((float) this.curvePaint.getFontMetricsInt().bottom) * (1.0f - ((float) Math.cos(this.curveRadian / 2.0d)))));
                }
                this.curveCenter = new Point(((int) (((this.arcChord + ((float) getPaddingRight())) + ((float) getPaddingLeft())) + ((float) this.additionalWidthSpace))) / 2, this.offsetY);
                this.path.addCircle((float) this.curveCenter.x, (float) this.curveCenter.y, this.curveRadius, this.pathDirection);
                Matrix matrix = new Matrix();
                matrix.setRotate((float) matrixRotate, (float) this.curveCenter.x, (float) this.curveCenter.y);
                this.path.transform(matrix);
            } else if (this.currentCurveMode == curveMode.fullCircle) {
            }
        }
        requestLayout();
        postInvalidate();
    }

        @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.isCurved) {
            setMeasuredDimension(((int) this.arcChord) + getPaddingLeft() + getPaddingRight() + this.additionalWidthSpace, ((int) this.arcHeight) + this.additionalHeightSpace + getPaddingTop() + getPaddingBottom());
        } else {
            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
        }
    }

    private void generateTempCanvas(float scaleX, float scaleY) {
        String key = String.format("%dx%d", Integer.valueOf(getWidth()), Integer.valueOf(getHeight()));
        Pair<Canvas, Bitmap> stored = this.canvasStore.get(key);
        if (stored != null) {
            this.tempCanvas = (Canvas) stored.first;
            this.tempBitmap = (Bitmap) stored.second;
            return;
        }
        this.tempCanvas = new Canvas();
        this.tempCanvas.scale(scaleX, scaleY);
        this.tempBitmap = Bitmap.createBitmap((int) (((float) getWidth()) * scaleX), (int) (((float) getHeight()) * scaleY), Bitmap.Config.ARGB_8888);
        this.tempCanvas.setBitmap(this.tempBitmap);
        this.canvasStore.put(key, new Pair<>(this.tempCanvas, this.tempBitmap));
    }

    public void freeze() {
        this.frozen = true;
    }

    public void unfreeze() {
        this.frozen = false;
    }

    public void requestLayout() {
        if (!this.frozen) {
            super.requestLayout();
        }
    }

    public void postInvalidate() {
        if (!this.frozen) {
            super.postInvalidate();
        }
    }

    public void postInvalidate(int left, int top, int right, int bottom) {
        if (!this.frozen) {
            super.postInvalidate(left, top, right, bottom);
        }
    }

    public void invalidate() {
        if (!this.frozen) {
            super.invalidate();
        }
    }

    public void invalidate(Rect rect) {
        if (!this.frozen) {
            super.invalidate(rect);
        }
    }

    public void invalidate(int l, int t, int r, int b) {
        if (!this.frozen) {
            super.invalidate(l, t, r, b);
        }
    }
}

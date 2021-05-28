package com.imaginstudio.imagetools.pixellab.TextObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.EmbossMaskFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.imaginstudio.imagetools.pixellab.DrawingPanelRenderer;
import com.imaginstudio.imagetools.pixellab.RectUtil;
import com.imaginstudio.imagetools.pixellab.appStateConstants;
import com.imaginstudio.imagetools.pixellab.commonFuncs;
import com.imaginstudio.imagetools.pixellab.font.customTypeface;
import com.imaginstudio.imagetools.pixellab.functions.interval;
import com.imaginstudio.imagetools.pixellab.functions.spansIntervals;
import com.imaginstudio.imagetools.pixellab.imageinfo.ImageSource;
import com.imaginstudio.imagetools.pixellab.imageinfo.displayInfo;
import com.imaginstudio.imagetools.pixellab.textContainer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.core.view.ViewCompat;

/**
 * 文字组件
 * Created by Try sven775288@gmail.com on 2021/5/18
 */
public class TextComponent extends View {

    private static final String TAG = "TextComponent";

    private displayInfo helperClass;
    public static final int FILL_TYPE_COLOR = 1;
    public static final int FILL_TYPE_GRADIENT = 2;
    public static final int threeD_TYPE_OBLIQUE = 3;
    public static final int threeD_TYPE_PERSPECTIVE = 1;
    int ADDITIONAL_SPACE_HANDLE = dpToPixels(this.ADDITIONAL_SPACE_HANDLE_DP);
    int ADDITIONAL_SPACE_HANDLE_DP = 0;
    int DRAG_ID_MAX_WIDTHER = 1;
    int DRAG_ID_WHOLE = 2;
    private int EmbossAmbient = 50;
    private int EmbossBevel = 30;
    private boolean EmbossEnabled = false;
    private int EmbossHardness = 40;
    private boolean EmbossIncludeStroke = false;
    private int EmbossIntensity = 60;
    private int EmbossLightAngle = 90;
    OnSelectEventListener SelectListener;
    public float angle = 0.0f;
    public int assigned_id;
    private float background_addHorizontalL = 0.0f;
    private float background_addHorizontalR = 0.0f;
    private float background_addVerticalB = 0.0f;
    private float background_addVerticalT = 0.0f;
    private int background_border_radius = 0;
    private int background_color = Color.argb(180, 0, 0, 0);
    private int background_fill_type = 1;
    //    GradientMaker.GradientFill background_usedGradient = new GradientMaker.GradientFill();
    public Bundle bezierMaskBundle = null;
    private boolean bezierMaskConfirmed = false;
    public boolean bezierMaskEnabled = true;
    public boolean bezierMaskIn = true;
    private ArrayList<Path> bezierMaskPath = null;
    private RectF bezierMaskRect = new RectF();
    int bigBallRadiusDp = 10;
    int bigBallRadiusPx = dpToPixels(this.bigBallRadiusDp);
    private Bitmap bitmap_3d_cache = null;
    private Bitmap bitmap_cache = null;
    private Bitmap bitmap_cache_reflection = null;
    private Bitmap bitmap_shadow_3d = null;
    private Bitmap bitmap_shadow_outer = null;
    private Bitmap bitmap_shadow_outer_reflection = null;
    int bottom_padding = 0;
    int boundingHeight = 0;
    int boundingWidth = 0;
    final float clickTolerance = commonFuncs.dpToPx(2);
    float dX;
    float dY;
    final long doubleClickDuration = 300;
    float downX = 0.0f;
    float downY = 0.0f;
    private int dragID = -1;
    int fill_type = 1;
    private boolean frozen;
    private boolean handleEnabled = true;
    boolean hasInnerShadow = false;
    private boolean hidden = false;
    private boolean isBackgroundEnabled;
    boolean isCurved = false;
    private boolean isSelected = true;
    long lastClick = 0;
    private OnTextNotifyLayers layersListener;
    int left_padding = 0;
    private boolean locked = false;
    private Matrix mMatrix;
    HandleBall maxWidther;
    private boolean need_redraw = true;
    private boolean neverEdited = true;
    float oldMaxW;
    float oldPosX;
    float oldPosY;
    private boolean outer_glow_enabled = false;
    private int outer_shadow_color = ViewCompat.MEASURED_STATE_MASK;
    private int outer_shadow_dx = 0;
    private int outer_shadow_dy = 0;
    private Boolean outer_shadow_enabled = false;
    private int outer_shadow_padding = 0;
    private float outer_shadow_radius = 10.0f;
    private Paint paint;
    Paint paintHandles = new Paint(1);
    Paint paintHandlesBorder = new Paint(1);
    Paint paintHandlesHighlight = new Paint(1);
    Paint paintSelected = new Paint(1);
    Paint paintSelectedBg = new Paint(1);
    Paint paintSelectedBorder = new Paint(1);
    float previousX;
    float previousY;
    public String reference = "0";
    public boolean reflectionEnabled = false;
    public int reflection_dy = 0;
    boolean renderMode = false;
    float renderScaleF = 1.0f;
    int right_padding = 0;
    textContainer root;
    private int shadow_3d_color = ViewCompat.MEASURED_STATE_MASK;
    private boolean shadow_3d_enabled = false;
    private int shadow_3d_expand = 10;
    private int shadow_3d_radius = 25;
    private int shadow_3d_transparency = 40;
    int smallBallRadiusDp = 7;
    int smallBallRadiusPx = dpToPixels(this.smallBallRadiusDp);
    private boolean tempDisable3DRots;
    CustomTextView textDraw;
    public int textHeight = 0;
    private int textOpacity = 100;
    private boolean textOpacityShadowInclude = false;
    private int textRotationX = 0;
    private int textRotationY = 0;
    public int textWidth = 0;
    private Bitmap textureBmb = null;
    boolean textureFlipH = false;
    boolean textureFlipV = false;
    int textureInRot = 0;
    private boolean textureMaintainAspect = false;
    RectF texturePortion = new RectF(0.0f, 0.0f, 100.0f, 100.0f);
    private int textureScale = 10;
    private ImageSource textureSrc = new ImageSource();
    int threeDDepth = 25;
    boolean threeDDepthColorAutomatic = true;
    int threeDDepthColorFill = -16776961;
    int threeDDepthDarken = 30;
    int threeDDepthFillType = 1;
    //    GradientMaker.GradientFill threeDDepthGradientFill = new GradientMaker.GradientFill();
    boolean threeDEnabled = false;
    boolean threeDLightingEnabled = true;
    int threeDLightingIntensity = 80;
    int threeDLightingShadow = 40;
    int threeDLightingSpecularHardness = 50;
    int threeDObliqueAngle = 45;
    int threeDQuality = 1;
    boolean threeDStokeInclude = true;
    int threeDViewType = 1;
    Canvas tmpCanvas = new Canvas();
    private boolean tmpHidden = false;
    int top_padding = 0;
    //    GradientMaker.GradientFill usedGradient = new GradientMaker.GradientFill();
    PointF viewCenter;
    private boolean firstInflate = true;


    public interface OnSelectEventListener {
        void onEvent_MoveMaxText(float f, float f2, float f3, boolean z, String str);

        void onEvent_SelectText(int i);

        void onEvent_doubleTapText();
    }

    public interface OnTextNotifyLayers {
        void selectionChanged(boolean z);

        void textChanged();
    }

    public void setRenderMode(boolean toggle, float scaleF) {
        this.renderMode = toggle;
        this.renderScaleF = scaleF;
        unloadBitmaps();
        this.need_redraw = true;
    }

    public void setInPadding(int left, int right) {
        this.textDraw.inPaddingLeft = left;
        this.textDraw.inPaddingRight = right;
        this.textDraw.updatePaddings();
        fullRedraw();
        requestLayout();
    }

    public int getInPaddingLeft() {
        return this.textDraw.inPaddingLeft;
    }

    public int getInPaddingRight() {
        return this.textDraw.inPaddingRight;
    }

    public void relativeScale(float factor) {
        setTextSize(getTextSize() * factor);
    }

    public void setLayersListener(OnTextNotifyLayers layersListener2) {
        this.layersListener = layersListener2;
    }

    public boolean isShadow_3d_enabled() {
        return this.shadow_3d_enabled;
    }

    public int getShadow_3d_color() {
        return this.shadow_3d_color;
    }

    public int getShadow_3d_transparency() {
        return this.shadow_3d_transparency;
    }

    public int getShadow_3d_radius() {
        return this.shadow_3d_radius;
    }

    public int getShadow_3d_expand() {
        return this.shadow_3d_expand;
    }

    public int getThreeDObliqueAngle() {
        return this.threeDObliqueAngle;
    }

    public void set3dObliqueAngle(int angle2) {
        this.threeDObliqueAngle = angle2;
        fullRedraw();
    }

    public void setShadow_3d_expand(int shadow_3d_expand2) {
        this.shadow_3d_expand = shadow_3d_expand2;
        fullRedrawUpdateParent();
    }

    public void setShadow3d(boolean shadow_3d_enabled2, int shadow_3d_color2, int shadow_3d_transparency2, int shadow_3d_radius2, int shadow_3d_expand2) {
        this.shadow_3d_enabled = shadow_3d_enabled2;
        this.shadow_3d_color = shadow_3d_color2;
        this.shadow_3d_transparency = shadow_3d_transparency2;
        this.shadow_3d_radius = shadow_3d_radius2;
        this.shadow_3d_expand = shadow_3d_expand2;
        fullRedraw();
    }

    public void setShadow_3d_enabled(boolean shadow_3d_enabled2) {
        this.shadow_3d_enabled = shadow_3d_enabled2;
        fullRedrawUpdateParent();
    }

    public void setShadow_3d_color(int shadow_3d_color2) {
        this.shadow_3d_color = shadow_3d_color2;
        invalidate();
    }

    public void setShadow_3d_transparency(int shadow_3d_transparency2) {
        this.shadow_3d_transparency = shadow_3d_transparency2;
        invalidate();
    }

    public void setShadow_3d_radius(int shadow_3d_radius2) {
        this.shadow_3d_radius = shadow_3d_radius2;
        fullRedrawUpdateParent();
    }

    public int getStrokeRadius() {
        return (int) this.textDraw.strokeWidth;
    }

    public Typeface getDominantFont() {
        interval mainFont = this.textDraw.fontsIds.getLastAttr();
        if (mainFont != null) {
            return mainFont.getFont().getTypeface();
        }
        return null;
    }

    public spansIntervals getFonts() {
        return this.textDraw.fontsIds;
    }

    public spansIntervals getColors() {
        return this.textDraw.colorIds.copy();
    }

    public spansIntervals getBolds() {
        return this.textDraw.boldIds.copy();
    }

    public spansIntervals getItalics() {
        return this.textDraw.italicIds.copy();
    }

    public spansIntervals getUnders() {
        return this.textDraw.underIds.copy();
    }

    public void setTextureScale(int scale) {
        this.textureScale = scale;
        fullRedraw();
    }

    public void setTextureMaintainAspect(boolean maintainAspect) {
        this.textureMaintainAspect = maintainAspect;
        fullRedraw();
    }

    public int getTextureScale() {
        return this.textureScale;
    }

    public boolean isTextureMaintainAspect() {
        return this.textureMaintainAspect;
    }

    public ImageSource getTextureSrc() {
        return this.textureSrc.copy();
    }

    public void setNewTextureSrc(ImageSource textureSrc2, int angle2, RectF portion, boolean flipH, boolean flipV) {
        this.textureSrc = textureSrc2;
        this.textureInRot = angle2;
        this.texturePortion = portion;
        this.textureFlipH = flipH;
        this.textureFlipV = flipV;
        this.textureScale = 10;
        this.textureMaintainAspect = false;
        this.textureBmb = null;
        fullRedraw();
    }

    @Deprecated
    public void setNewTextureSrc(String path) {
        if (path == null || path.isEmpty()) {
            this.textureSrc = new ImageSource();
        } else {
            this.textureSrc = new ImageSource(path);
        }
        this.textureBmb = null;
        fullRedraw();
    }

    public void removeTexture() {
        this.textureBmb = null;
        this.textureSrc = new ImageSource();
        fullRedraw();
    }

    public boolean getOuterGlowEnabled() {
        return this.outer_glow_enabled;
    }

    public float getZoomFactor() {
        return helperClass.getZoomFactor();
    }

    public void setTouchEventListener(OnSelectEventListener eventListener) {
        this.SelectListener = eventListener;
    }

    /* access modifiers changed from: package-private */
    public int dpToPixels(int dp) {
        return (int) TypedValue.applyDimension(1, (float) dp, getResources().getDisplayMetrics());
    }

    public void hideTemp() {
        if (!this.hidden) {
            this.tmpHidden = true;
            setHidden(true);
        }
    }

    public void unhideTemp() {
        if (this.tmpHidden) {
            setHidden(false);
            this.tmpHidden = false;
        }
    }

    public void setHidden(boolean hidden2) {
        boolean oldHidden = this.hidden;
        this.hidden = hidden2;
        if (oldHidden != this.hidden) {
            invalidate();
        }
    }

    public void setLocked(boolean locked2) {
        boolean oldLocked = this.hidden;
        this.locked = locked2;
        if (oldLocked != this.locked) {
            invalidate();
        }
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public boolean isLocked() {
        return this.locked;
    }

    /* access modifiers changed from: package-private */
    public void init(Context context) {
        this.paintSelected.setColor(-1);
        this.paintSelected.setStyle(Paint.Style.STROKE);
        this.paintSelected.setStrokeWidth(commonFuncs.dpToPx(1));

        this.paintSelectedBorder.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));
        this.paintSelectedBorder.setColor(Color.WHITE);
        this.paintSelectedBorder.setStyle(Paint.Style.STROKE);
        this.paintSelectedBorder.setStrokeWidth(commonFuncs.dpToPx(1));

        this.paintSelectedBg.setColor(Color.argb(40, 255, 255, 255));
        this.paintHandles.setColor(-1);
        this.paintHandlesBorder.setColor(Color.argb(60, 0, 0, 0));
        this.paintHandlesBorder.setStyle(Paint.Style.FILL_AND_STROKE);
        this.paintHandlesBorder.setStrokeWidth(commonFuncs.dpToPx(1));
        this.paintHandlesHighlight.setColor(Color.parseColor("#4db8f3"));
        this.textDraw = new CustomTextView(context, Math.max(helperClass.getContainerHeight(), helperClass.getContainerWidth()) * 2);
        this.textDraw.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        this.textDraw.setTypeface(Typeface.DEFAULT);
        PointF pt = new PointF(0.0f, 0.0f);
        new PointF(0.0f, 0.0f);
        new PointF(0.0f, 0.0f);
        new PointF(0.0f, 0.0f);
        this.maxWidther = new HandleBall(context, pt);
        this.mMatrix = new Matrix();
        this.paint = new Paint();

        initFrameBitmap();

    }

    @SuppressLint({"NewApi"})
    public TextComponent(Context context, int initialColor, String reference2) {
        super(context);
        init(context);
        this.reference = reference2;
        setColorFill(initialColor);
    }

    @SuppressLint({"NewApi"})
    public TextComponent(Context context, int initialColor, String reference2, displayInfo helperClass, Drawable leftTopBitmap, Drawable rightBottomBitmap) {
        super(context);
        this.helperClass = helperClass;
        this.leftTopBitmap = leftTopBitmap;
        this.rightBottomBitmap = rightBottomBitmap;
        init(context);
        this.reference = reference2;
        setColorFill(initialColor);
    }

    public TextComponent(Context context, Bundle bundle, boolean copy, String reference2) {
        super(context);
        init(context);
        if (copy || bundle.getString(appStateConstants.TEXT_REFERENCE) == null) {
            this.reference = reference2;
        } else {
            this.reference = bundle.getString(appStateConstants.TEXT_REFERENCE);
        }
        this.neverEdited = false;
        applyBundle(bundle, copy);
    }

    public int getEmbossAmbient() {
        return this.EmbossAmbient;
    }

    public boolean isEmbossIncludeStroke() {
        return this.EmbossIncludeStroke;
    }

    public int getEmbossBevel() {
        return this.EmbossBevel;
    }

    public boolean isEmbossEnabled() {
        return this.EmbossEnabled;
    }

    public int getEmbossHardness() {
        return this.EmbossHardness;
    }

    public int getEmbossIntensity() {
        return this.EmbossIntensity;
    }

    public int getEmbossLightAngle() {
        return this.EmbossLightAngle;
    }

    public void setEmboss(boolean enabled, int LightAngle, int Intensity, int Ambient, int Hardness, int Bevel) {
        this.EmbossEnabled = enabled;
        this.EmbossLightAngle = LightAngle;
        this.EmbossIntensity = Intensity;
        this.EmbossAmbient = Ambient;
        this.EmbossHardness = Hardness;
        this.EmbossBevel = Bevel;
        fullRedraw();
    }

    public void setEmbossIncludeStroke(boolean enabled) {
        this.EmbossIncludeStroke = enabled;
        fullRedraw();
    }

    public void set3dViewType(int type) {
        this.threeDViewType = type;
        fullRedraw();
    }

    public void set3dEnabled(boolean enabled) {
        this.threeDEnabled = enabled;
        fullRedraw();
    }

    public void set3dLighting(boolean LightingEnabled, int LightingAngle, int LightingIntensity, int LightingShadow, int LightingSpecular) {
        this.threeDLightingEnabled = LightingEnabled;
        this.EmbossLightAngle = LightingAngle;
        this.threeDLightingIntensity = LightingIntensity;
        this.threeDLightingShadow = LightingShadow;
        this.threeDLightingSpecularHardness = LightingSpecular;
        fullRedraw();
    }

//    public void set3dDepthColor(int DepthFillType, int DepthColorFill, GradientMaker.GradientFill DepthGradientFill, boolean DepthColorAutomatic) {
//        this.threeDDepthFillType = DepthFillType;
//        this.threeDDepthColorFill = DepthColorFill;
//        this.threeDDepthGradientFill = DepthGradientFill;
//        this.threeDDepthColorAutomatic = DepthColorAutomatic;
//        fullRedraw();
//    }

    public void set3dDepth(int Depth, int DepthDarken, int Quality, boolean StokeInclude) {
        this.threeDDepth = Depth;
        this.threeDDepthDarken = DepthDarken;
        this.threeDQuality = Quality;
        this.threeDStokeInclude = StokeInclude;
        fullRedraw();
    }

    public void setColors(spansIntervals colors) {
        if (colors != null) {
            this.fill_type = 1;
            this.textDraw.getPaint().setShader(null);
            this.textDraw.colorIds = colors;
        }
    }

    public void updateSpans(boolean sizeChange) {
        this.textDraw.applySpans();
        if (!sizeChange) {
            this.textDraw.invalidate();
            fullRedraw();
            return;
        }
        this.textDraw.textUpdated();
        requestLayout();
        fullRedraw();
    }

    public void setFonts(spansIntervals fonts) {
        if (fonts != null) {
            this.textDraw.fontsIds = fonts;
        }
    }

    public void setBolds(spansIntervals bolds) {
        if (bolds != null) {
            this.textDraw.boldIds = bolds;
        }
    }

    public void setItalics(spansIntervals italics) {
        if (italics != null) {
            this.textDraw.italicIds = italics;
        }
    }

    public void setUnders(spansIntervals unders) {
        if (unders != null) {
            this.textDraw.underIds = unders;
        }
    }

    public void setColorFill(int color) {
        setColorFill(color, 0, this.textDraw.getTextLength());
    }

    public void setColorFill(int color, int start, int end) {
        this.fill_type = 1;
        this.textDraw.getPaint().setShader(null);
        this.textDraw.setColorToSelection(color, start, end);
        this.textDraw.invalidate();
        fullRedraw();
    }

//    public void setGradientFill(GradientMaker.GradientFill gradient) {
//        this.fill_type = 2;
//        this.usedGradient = gradient != null ? gradient.copy() : null;
//        fullRedraw();
//    }

//    public GradientMaker.GradientFill getGradientFill() {
//        return this.usedGradient;
//    }

    public void setTextFont(spansIntervals fonts) {
        this.textDraw.setFonts(fonts);
        requestLayout();
        fullRedraw();
    }

    public void setTextFont(Bundle fontsBundle) {
        if (fontsBundle != null) {
            setTextFont(bundleToFontIntervals(fontsBundle));
        }
    }

    public Typeface createFontFromPath(String path) {
        Typeface t = Typeface.DEFAULT;
        try {
            return Typeface.createFromAsset(getContext().getAssets(), path);
        } catch (Exception e) {
            try {
                return Typeface.createFromFile(path);
            } catch (Exception e2) {
                return t;
            }
        }
    }

    public int getFill_type() {
        return this.fill_type;
    }

    public void setFill_type(int fill_type2) {
        this.fill_type = fill_type2;
    }

    public boolean isSpaceWords() {
        return this.textDraw.isWordSpacing();
    }

    public void setSpaceWords(boolean words) {
        this.textDraw.setWordSpacing(words);
        fullRedraw();
        requestLayout();
    }

    public void setLetterSpacing(int spacing) {
        this.textDraw.setLetterSpace(spacing);
        fullRedraw();
        requestLayout();
    }

    public int getLetterSpacing() {
        return this.textDraw.getLetterSpace();
    }

    public int getTextRotationX() {
        return this.textRotationX;
    }

    public int getTextRotationY() {
        return this.textRotationY;
    }

    public void rotate3d(int onX, int onY) {
        this.textRotationX = onX;
        this.textRotationY = onY;
        fullRedraw();
    }

    public void fullRedraw() {
        this.need_redraw = true;
        if (!this.frozen) {
            invalidate();
        }
    }

    public void freezeFullRedraw() {
        this.frozen = true;
    }

    public void UnFreezeFullRedraw() {
        this.frozen = false;
        requestLayout();
        this.textDraw.invalidate();
        fullRedraw();
    }

    private void fullRedrawUpdateParent() {
        fullRedraw();
    }

    public void setOuterShadowColor(int color) {
        this.outer_shadow_color = color;
        invalidate();
    }

    public void setOuterShadow(boolean enabled, boolean glow, float r, int dx, int dy, int color) {
        this.outer_shadow_enabled = Boolean.valueOf(enabled);
        if (enabled) {
            this.outer_glow_enabled = glow;
            this.outer_shadow_radius = r;
            this.outer_shadow_color = color;
            this.outer_shadow_dx = dx;
            this.outer_shadow_dy = dy;
            this.outer_shadow_padding = (int) (2.0d * Math.ceil((double) r));
            if (r < 1.0f) {
                this.outer_shadow_padding = 0;
            }
        }
        fullRedraw();
    }

    public float getOuter_shadow_radius() {
        return this.outer_shadow_radius;
    }

    public int getOuter_shadow_dx() {
        return this.outer_shadow_dx;
    }

    public int getOuter_shadow_dy() {
        return this.outer_shadow_dy;
    }

    public int getOuter_shadow_color() {
        return this.outer_shadow_color;
    }

    public int getOuterShadowColor() {
        return this.outer_shadow_color;
    }

    public boolean getOuterShadowEnabled() {
        return this.outer_shadow_enabled.booleanValue();
    }

    public void setInnerShadow(boolean enabled, float radius, float dx, float dy, int color) {
        this.hasInnerShadow = enabled;
        this.textDraw.setInnerShadow(enabled, radius, dx, dy, color);
        fullRedraw();
    }

    public void clearInnerShadow() {
        this.hasInnerShadow = false;
        this.textDraw.setInnerShadow(false, 0.0f, 0.0f, 0.0f, -1);
        fullRedraw();
    }

    public int getInnerColor() {
        return this.textDraw.inner_color;
    }

    public boolean getInnerEnabled() {
        return this.textDraw.inner_enabled;
    }

    public int getInnerRadius() {
        return (int) this.textDraw.inner_r;
    }

    public int getInnerDx() {
        return (int) this.textDraw.inner_dx;
    }

    public int getInnerDy() {
        return (int) this.textDraw.inner_dy;
    }

//    public void setStroke(boolean enabled, float width, int fill_type2, int color, GradientMaker.GradientFill gradient) {
//        this.textDraw.setStrokeFill(enabled, width, fill_type2, color, gradient);
//        requestLayout();
//        fullRedraw();
//    }

    public void clearStroke() {
        this.textDraw.clearStroke();
        requestLayout();
        fullRedraw();
    }

    public int getStrokeColor() {
        return this.textDraw.strokeColor;
    }

    public boolean getStrokeEnabled() {
        return this.textDraw.stroke_enabled;
    }

    public int getStrokeFillType() {
        return this.textDraw.strokeFillType;
    }

//    public GradientMaker.GradientFill getStrokeGradient() {
//        return this.textDraw.strokeGradient;
//    }

    public void setCurve(int degree) {
        this.isCurved = degree != 0;
        this.textDraw.setCurve(degree);
        fullRedraw();
        requestLayout();
    }

    public int getCurve() {
        return this.textDraw.curveDegree;
    }

    public void selectMe() {
        if (this.SelectListener != null && !this.isSelected) {
            this.SelectListener.onEvent_SelectText(this.assigned_id);
        }
    }

    /* access modifiers changed from: package-private */
    public void clicked() {
        if (System.currentTimeMillis() - this.lastClick <= 300 && this.SelectListener != null) {
            this.SelectListener.onEvent_doubleTapText();
        }
        this.lastClick = System.currentTimeMillis();
    }

    private int ACTION_TYPE_DELETE = 0;
    private int ACTION_TYPE_MOVE = 1;
    private int ACTION_TYPE_SCALE_AND_ROTATE = 2;
    private int ACTION_TYPE = 0;

    public boolean onTouchEvent(MotionEvent event) {
        if (this.locked || (isHidden() && !this.isSelected)) {
            return false;
        }
        float rx = event.getRawX();
        float ry = event.getRawY();

        float x = event.getX();
        float y = event.getY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:

                if (leftTopDstRect.contains(x, y)) {
                    Log.d(TAG, "ACTION_DOWN ACTION_TYPE_DELETE() called with");
                    ACTION_TYPE = ACTION_TYPE_DELETE;
                    if (callback != null) {
                        callback.stickerOnclick(LEFT_TOP_MODE);
                    }
                } else if (rightBottomDstRect.contains(x, y)) {
                    Log.d(TAG, "ACTION_DOWN ACTION_TYPE_SCALE_AND_ROTATE() called with");
                    ACTION_TYPE = ACTION_TYPE_SCALE_AND_ROTATE;

                } else {
                    Log.d(TAG, "ACTION_DOWN ACTION_TYPE_MOVE() called with");
                    ACTION_TYPE = ACTION_TYPE_MOVE;

                    this.downX = rx;
                    this.downY = ry;
                    this.dX = 0.0f;
                    this.dY = 0.0f;
                    this.previousX = rx;
                    this.previousY = ry;
                    if (this.SelectListener != null && !this.isSelected) {
                        this.SelectListener.onEvent_SelectText(this.assigned_id);
                    }
//                    if (!(Math.abs(this.maxWidther.getX() - event.getX()) <= ((float) getBiggerRadius()) && this.handleEnabled) || this.isCurved) {
//                        this.dragID = this.DRAG_ID_WHOLE;
//                    } else {
//                        this.dragID = this.DRAG_ID_MAX_WIDTHER;
//                    }
                    this.dragID = this.DRAG_ID_WHOLE;
                    invalidate();
                    this.oldMaxW = (float) this.textDraw.userMaxWidth;
                    this.oldPosX = getX();
                    this.oldPosY = getY();
                }

                lastX = x;
                lastY = y;

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (ACTION_TYPE == ACTION_TYPE_MOVE) {
                    if (Math.abs(this.downX - rx) <= this.clickTolerance && Math.abs(this.downY - ry) <= this.clickTolerance) {
                        clicked();
                    }
                    helperClass.motionActionUp();
                    float newPosX = getX();
                    float newPosY = getY();
                    float newMaxW = (float) this.textDraw.userMaxWidth;
                    if (!((newPosX == this.oldPosX && newPosY == this.oldPosY && newMaxW == this.oldMaxW) || this.SelectListener == null)) {
                        this.SelectListener.onEvent_MoveMaxText(this.oldPosX, this.oldPosY, this.oldMaxW, this.dragID == this.DRAG_ID_WHOLE, this.reference);
                    }
                    this.dragID = -1;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (ACTION_TYPE == ACTION_TYPE_SCALE_AND_ROTATE) {
                    Log.d(TAG, "ACTION_MOVE ACTION_TYPE_SCALE_AND_ROTATE() called with");
                    // 旋转 缩放文字操作
                    // 旋转 缩放文字操作
                    float dx = x - lastX;
                    float dy = y - lastY;
                    updateRotateAndScale(dx, dy);
                    invalidate();
                    lastX = x;
                    lastY = y;
                } else if (ACTION_TYPE == ACTION_TYPE_MOVE) {
                    Log.d(TAG, "ACTION_MOVE ACTION_TYPE_MOVE() called with");

                    if (this.dragID != this.DRAG_ID_WHOLE) {
                        if (this.dragID == this.DRAG_ID_MAX_WIDTHER) {
                            Log.d(TAG, "ACTION_MOVE ACTION_TYPE_MOVE()1 called with");
                            float difference = (float) this.textWidth;
                            setMax(((int) this.angle) == 0 ? helperClass.snapPosX(event.getX() + getX(), false) - getX() : event.getX());
                            this.textDraw.measure(0, 0);
                            setX(getX() + ((difference - ((float) this.textDraw.getMeasuredWidth())) * Math.signum(this.angle) * (this.angle / 180.0f)));
                            break;
                        }
                    } else {
                        Log.d(TAG, "ACTION_MOVE ACTION_TYPE_MOVE()2 called with");
                        this.dX = (rx - this.previousX) / getZoomFactor();
                        this.dY = (ry - this.previousY) / getZoomFactor();
                        setX(helperClass.snapPosX(getX() + this.dX, ((float) this.textWidth) * 0.5f, true));
                        setY(helperClass.snapPosY(getY() + this.dY, ((float) this.textHeight) * 0.5f, true));
                        this.previousX = rx;
                        this.previousY = ry;
                        break;
                    }
                }
                break;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public float dist(PointF p1, PointF p2) {
        return (float) Math.sqrt((double) (((p1.x - p2.x) * (p1.x - p2.x)) + ((p1.y - p2.y) * (p1.y - p2.y))));
    }

    public void setTextAlign(int textAlign) {
        this.textDraw.setGravity(textAlign);
        requestLayout();
        fullRedraw();
    }

    public void setJustify(boolean justify) {
        this.textDraw.setJustifyAlign(justify);
        requestLayout();
        fullRedraw();
    }

    public int getTextAlign() {
        return this.textDraw.getGravity();
    }

    public boolean getTextJustify() {
        return this.textDraw.isJustifyAlign();
    }

//    public void updateBackground(boolean enabled, int fill_type2, int color, GradientMaker.GradientFill gradient, float addHorizontalL, float addHorizontalR, float addVerticalT, float addVerticalB, int border_radius) {
//        this.isBackgroundEnabled = enabled;
//        if (this.isBackgroundEnabled) {
//            this.background_fill_type = fill_type2;
//            this.background_usedGradient = gradient != null ? gradient.copy() : null;
//            this.background_color = color;
//            this.background_addHorizontalL = addHorizontalL;
//            this.background_addHorizontalR = addHorizontalR;
//            this.background_addVerticalT = addVerticalT;
//            this.background_addVerticalB = addVerticalB;
//            this.background_border_radius = border_radius;
//        }
//        invalidate();
//    }

    public int getBackground_color() {
        return this.background_color;
    }

    public float getBackground_addHorizontalL() {
        return this.background_addHorizontalL;
    }

    public float getBackground_addHorizontalR() {
        return this.background_addHorizontalR;
    }

    public float getBackground_addVerticalT() {
        return this.background_addVerticalT;
    }

    public float getBackground_addVerticalB() {
        return this.background_addVerticalB;
    }

    public int getBackground_border_radius() {
        return this.background_border_radius;
    }

//    public GradientMaker.GradientFill getBackground_usedGradient() {
//        return this.background_usedGradient;
//    }

    public int getBackground_usedType() {
        return this.background_fill_type;
    }

    public boolean getBackground_enabled() {
        return this.isBackgroundEnabled;
    }

    private void restoreTextStyle(int oldColor, boolean oldStrokeEnabled) {
        if (this.fill_type == 1) {
            this.fill_type = 1;
            this.textDraw.getPaint().setShader(null);
            this.textDraw.setTextColor(oldColor);
            this.textDraw.getPaint().setColor(oldColor);
        }
        this.textDraw.stroke_enabled = oldStrokeEnabled;
        this.textDraw.restoreColorSpans();
    }

    /* access modifiers changed from: package-private */
    public void applyCamera(Canvas canvas, Camera camera) {
        camera.getMatrix(this.mMatrix);
        this.mMatrix.preTranslate(-this.viewCenter.x, -this.viewCenter.y);
        this.mMatrix.postTranslate(this.viewCenter.x, this.viewCenter.y);
        canvas.concat(this.mMatrix);
    }

    /* access modifiers changed from: package-private */
    public float getRenderScaleF() {
        if (this.renderMode) {
            return this.renderScaleF;
        }
        return 1.0f;
    }

    /* access modifiers changed from: package-private */
    public int getBiggerRadius() {
        return (int) Math.min(((float) Math.min(this.textHeight, this.textWidth)) * 0.45f, (float) this.bigBallRadiusPx);
    }

    public void tempDisable3DRotations() {
        this.tempDisable3DRots = true;
        invalidate();
    }

    public void restore3DRotations() {
        this.tempDisable3DRots = false;
        invalidate();
    }

    public void setBezierMaskEnabled(boolean enabled) {
        if (this.bezierMaskEnabled != enabled) {
            this.bezierMaskEnabled = enabled;
            fullRedraw();
        }
    }

    public boolean isBezierMaskIn() {
        return this.bezierMaskIn;
    }

    public RectF getBoundsRect() {
        return new RectF(getX(), getY(), getX() + ((float) this.textWidth), getY() + ((float) this.textHeight));
    }

    public boolean isBezierMaskEnabled() {
        return this.bezierMaskEnabled;
    }

    public void setBezierMaskIn(boolean bezierMaskIn2) {
        if (this.bezierMaskIn != bezierMaskIn2) {
            this.bezierMaskIn = bezierMaskIn2;
            fullRedraw();
        }
    }

    public void setBezierMaskPath(ArrayList<Path> paths) {
        this.bezierMaskPath = paths;
        this.bezierMaskRect.set(getX(), getY(), getX() + ((float) this.textWidth), getY() + ((float) this.textHeight));
        fullRedraw();
    }

    public void setBezierMaskBundle(Bundle bundle) {
        this.bezierMaskBundle = bundle;
    }

    private void recreateBezierMaskPath() {
        if (!(this.bezierMaskBundle == null || this.bezierMaskBundle.getStringArrayList(appStateConstants.BEZIER_CURVE_RECT) == null)) {
            this.bezierMaskPath = new ArrayList<>();
            for (String str : this.bezierMaskBundle.keySet()) {
                ArrayList<String> strList = this.bezierMaskBundle.getStringArrayList(str);
                if (appStateConstants.BEZIER_CURVE_RECT.equals(str)) {
                    this.bezierMaskRect = commonFuncs.arrToRectF(strList);
                } else {
//                    this.bezierMaskPath.add(customBezier.getPathFromStringArrayList(strList));
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.viewCenter = new PointF(((float) ((getWidth() - getPaddingLeft()) - getPaddingRight())) / 2.0f, ((float) ((getHeight() - getPaddingTop()) - getPaddingBottom())) / 2.0f);

        this.textWidth = this.textWidth > 0 ? this.textWidth : 1;
        this.textHeight = this.textHeight > 0 ? this.textHeight : 1;
        int threeDDepthPx = Math.max(1, dpToPixels(this.threeDDepth));

        this.boundingWidth = this.textWidth + this.ADDITIONAL_SPACE_HANDLE;
        this.boundingHeight = this.textHeight;
        updateHandlePos();
        this.paint.setAntiAlias(true);
        this.paint.setDither(true);
        this.paint.setStrokeJoin(Paint.Join.ROUND);
        this.paint.setStrokeWidth(1.0f);
        this.paint.setStyle(Paint.Style.FILL);
        this.paint.setColor(Color.parseColor("#40FFFFFF"));
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setColor(Color.parseColor("#007de3"));

        this.textDraw.layout(0, 0, this.textWidth + this.textDraw.getPaddingRight(), this.textHeight);
        Camera rotation3d = new Camera();
        rotation3d.rotateY((float) clipAngle(this.textRotationY));
        rotation3d.rotateX((float) clipAngle(this.textRotationX));

        Camera rotation_translation3D = new Camera();
        rotation_translation3D.rotateY((float) clipAngle(this.textRotationY));
        rotation_translation3D.rotateX((float) clipAngle(this.textRotationX));
        rotation_translation3D.translate(0.0f, 0.0f, (float) threeDDepthPx);
        canvas.save();
        canvas.scale(mScale, mScale, viewCenter.x, viewCenter.y);
        canvas.rotate(mRotateAngle, viewCenter.x, viewCenter.y);

        applyCamera(canvas, rotation3d);
        if (this.isBackgroundEnabled && !this.hidden) {
            Paint bgPaint = new Paint(1);
            bgPaint.setDither(true);
            bgPaint.setStyle(Paint.Style.FILL);
            RectF background_rect = new RectF(-this.background_addHorizontalL, -this.background_addVerticalT, ((float) this.textWidth) + this.background_addHorizontalR, ((float) this.textHeight) + this.background_addVerticalB);
            if (this.background_fill_type == 1) {
                bgPaint.setColor(this.background_color);
                if (Color.alpha(this.background_color) == 255) {
                    applyAlphaToPaint(bgPaint);
                }
            } else if (this.background_fill_type == 2) {
//                bgPaint.setShader(this.background_usedGradient.getShader(background_rect));
//                if (!this.background_usedGradient.hasAlpha()) {
//                    applyAlphaToPaint(bgPaint);
//                }
            }
            if (this.threeDEnabled) {
                canvas.restore();
                canvas.save();
                applyCamera(canvas, rotation_translation3D);
            }
//            canvas.drawRoundRect(background_rect, (float) this.background_border_radius, (float) this.background_border_radius, bgPaint);
            if (this.threeDEnabled) {
                canvas.restore();
                canvas.save();
                applyCamera(canvas, rotation3d);
            }
        }
        if (this.textureSrc.checkValid() && this.textureBmb == null) {
            loadTexture();
        }
        if (this.textureBmb == null || !this.textureSrc.checkValid()) {
            this.textDraw.setTextureShader(null);
        } else {
            BitmapShader textTexture = new BitmapShader(this.textureBmb, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            Matrix matrix = new Matrix();
            float initScaleX = ((float) this.textWidth) / ((float) this.textureBmb.getWidth());
            float initScaleY = ((float) this.textHeight) / ((float) this.textureBmb.getHeight());
            if (this.textureMaintainAspect) {
                matrix.preScale(initScaleX, initScaleY);
            } else {
                matrix.preScale(Math.min(initScaleX, initScaleY), Math.min(initScaleX, initScaleY));
            }
            matrix.postScale(((float) this.textureScale) / 10.0f, ((float) this.textureScale) / 10.0f);
            textTexture.setLocalMatrix(matrix);
            this.textDraw.setTextureShader(textTexture);
        }
        if ((this.need_redraw && this.dragID != this.DRAG_ID_WHOLE) || this.renderMode) {
            this.bitmap_cache = Bitmap.createBitmap((int) (((float) this.textWidth) * getRenderScaleF()), (int) (((float) this.textHeight) * getRenderScaleF()), Bitmap.Config.ARGB_8888);
            this.tmpCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
            this.tmpCanvas.setBitmap(this.bitmap_cache);
            this.tmpCanvas.save();
            this.tmpCanvas.scale(getRenderScaleF(), getRenderScaleF());
            this.paint.setFilterBitmap(true);
            this.paint.setDither(true);
            this.textDraw.setMaskFilter(getCurrEmbossEffect());
            this.textDraw.setIncludeEmbossInStroke(isEmbossIncludeStroke());
            this.textDraw.gradient_bounds = null;
            if (this.fill_type == 2) {
                this.textDraw.removeColorSpans();
            }
            this.textDraw.drawMaskListener = null;
            if (this.bezierMaskEnabled) {
                if (this.bezierMaskPath == null) {
                    recreateBezierMaskPath();
                }
                if (this.bezierMaskPath != null && commonFuncs.isValidRect(this.bezierMaskRect)) {
                    final Paint pntEraser = new Paint(1);
                    pntEraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                    this.textDraw.drawMaskListener = new CustomTextView.onFinishDraw() {
                        /* class com.imaginstudio.imagetools.pixellab.TextObject.TextComponent.AnonymousClass1 */

                        @Override
                        // com.imaginstudio.imagetools.pixellab.TextObject.CustomTextView.onFinishDraw
                        public void done(Canvas tmpCanvas) {
                            tmpCanvas.save();
                            tmpCanvas.scale(((float) TextComponent.this.textWidth) / TextComponent.this.bezierMaskRect.width(), ((float) TextComponent.this.textHeight) / TextComponent.this.bezierMaskRect.height());
                            tmpCanvas.translate(-TextComponent.this.bezierMaskRect.left, -TextComponent.this.bezierMaskRect.top);
                            if (TextComponent.this.bezierMaskIn) {
                                Iterator it = TextComponent.this.bezierMaskPath.iterator();
                                while (it.hasNext()) {
                                    tmpCanvas.drawPath((Path) it.next(), pntEraser);
                                }
                            } else {
                                tmpCanvas.drawPath(commonFuncs.invertPath(TextComponent.this.bezierMaskPath, TextComponent.this.bezierMaskRect.left, TextComponent.this.bezierMaskRect.top, TextComponent.this.bezierMaskRect.right, TextComponent.this.bezierMaskRect.bottom), pntEraser);
                            }
                            tmpCanvas.restore();
                        }
                    };
                }
            }
            this.textDraw.draw(this.tmpCanvas);
            if ((this.bitmap_cache != null && (this.fill_type == 2 || ((this.threeDEnabled && this.threeDDepthFillType == 2) || (this.textDraw.stroke_enabled && this.textDraw.strokeFillType == 2)))) || this.reflectionEnabled || this.shadow_3d_enabled) {
                this.top_padding = getBitmapTopPadding(this.bitmap_cache);
                this.bottom_padding = getBitmapBottomPadding(this.bitmap_cache);
            }
            Rect content_bounds = new Rect(0, 0, this.textWidth, this.textHeight);
            if (this.bitmap_cache != null && (this.fill_type == 2 || ((this.threeDEnabled && this.threeDDepthFillType == 2) || (this.textDraw.stroke_enabled && this.textDraw.strokeFillType == 2)))) {
                content_bounds = new Rect(this.textDraw.getPaddingLeft(), (int) (((float) this.top_padding) / getRenderScaleF()), ((int) (((float) this.bitmap_cache.getWidth()) / getRenderScaleF())) - this.textDraw.getPaddingRight(), (int) (((float) (this.bitmap_cache.getHeight() - this.bottom_padding)) / getRenderScaleF()));
                if (this.fill_type == 2 || this.textDraw.strokeFillType == 2) {
                    if (this.fill_type == 2) {
//                        this.textDraw.getPaint().setShader(this.usedGradient.getShader(content_bounds));
                    }
                    if (this.textDraw.strokeFillType == 2) {
                        this.textDraw.gradient_bounds = new Rect(content_bounds.left, content_bounds.top, content_bounds.right, content_bounds.bottom);
                    }
                    this.tmpCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
                    this.textDraw.draw(this.tmpCanvas);
                }
            }
            this.textDraw.setIncludeEmbossInStroke(true);
            try {
                this.tmpCanvas.restore();
            } catch (IllegalStateException e) {
            }
            if (this.threeDEnabled) {
                this.bitmap_3d_cache = Bitmap.createBitmap((int) (((float) this.textWidth) * getRenderScaleF()), (int) (((float) this.textHeight) * getRenderScaleF()), Bitmap.Config.ARGB_8888);
                Canvas tmp3dCanvas = new Canvas();
                tmp3dCanvas.scale(getRenderScaleF(), getRenderScaleF());
                tmp3dCanvas.setBitmap(this.bitmap_3d_cache);
                tmp3dCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
                int oldColor = this.textDraw.getPaint().getColor();
                boolean oldStrokeEnabled = getStrokeEnabled();
                if (!this.threeDStokeInclude) {
                    this.textDraw.stroke_enabled = false;
                }
                this.textDraw.setMaskFilter(getCurr3DEmbossEffect());
                if (!this.threeDDepthColorAutomatic) {
                    this.textDraw.removeColorSpans();
                    this.textDraw.setTextureShader(null);
                    if (this.threeDDepthFillType == 1) {
                        this.textDraw.tmp_drawing3DDepth = true;
                        this.textDraw.getPaint().setShader(null);
                        this.textDraw.setTextColor(this.threeDDepthColorFill);
                        this.textDraw.getPaint().setColor(this.threeDDepthColorFill);
                    } else if (this.threeDDepthFillType == 2) {
//                        this.textDraw.getPaint().setShader(this.threeDDepthGradientFill.getShader(content_bounds));
                    }
                }
                this.textDraw.draw(tmp3dCanvas);
                this.textDraw.tmp_drawing3DDepth = false;
                restoreTextStyle(oldColor, oldStrokeEnabled);
                this.bitmap_3d_cache = blurMask(this.bitmap_3d_cache);
            }
            this.textDraw.drawMaskListener = null;
            if (this.outer_shadow_enabled.booleanValue()) {
                this.bitmap_shadow_outer = updateOuter_Shadow(this.bitmap_cache, getRenderScaleF());
            }
            if (this.shadow_3d_enabled) {
                this.left_padding = getBitmapLeftPadding(this.bitmap_cache);
                this.right_padding = getBitmapRightPadding(this.bitmap_cache);
                this.bitmap_shadow_3d = createShadow3d((int) (((float) this.textWidth) - (((float) (this.left_padding + this.right_padding)) / getRenderScaleF())), threeDDepthPx, getRenderScaleF());
            }
            if (this.reflectionEnabled) {
                this.bitmap_cache_reflection = flipBitmap(this.bitmap_cache);
                if (this.outer_shadow_enabled.booleanValue()) {
                    this.bitmap_shadow_outer_reflection = flipBitmap(this.bitmap_shadow_outer);
                }
            }
            this.textDraw.setMaskFilter(null);
            this.need_redraw = false;
        }
        if (!this.hidden) {
            if (this.shadow_3d_enabled) {
                draw3dShadow(canvas, rotation3d, threeDDepthPx, getRenderScaleF());
            }
            if (this.threeDEnabled) {
                Paint paint3d = new Paint();
                paint3d.setAntiAlias(true);
                paint3d.setDither(true);
                paint3d.setFilterBitmap(true);
                applyAlphaToPaint(paint3d);
                paint3d.setColorFilter(new PorterDuffColorFilter(Color.argb((int) ((((float) this.threeDDepthDarken) / 100.0f) * 220.0f), 0, 0, 0), PorterDuff.Mode.SRC_ATOP));
                float stepDist = 1.0f / ((float) Math.max(this.threeDQuality, 1));
                int steps = (int) Math.ceil((double) (((float) threeDDepthPx) / stepDist));
                Rect rectDraw1 = new Rect(0, 0, this.bitmap_3d_cache.getWidth(), this.bitmap_3d_cache.getHeight());
                Rect rectDraw2 = new Rect(0, 0, this.textWidth, this.textHeight);
                if (this.threeDViewType == 1) {
                    canvas.restore();
                    if (this.outer_shadow_enabled.booleanValue()) {
                        canvas.save();
                        applyCamera(canvas, rotation_translation3D);
                        Paint pt = new Paint();
                        pt.setColorFilter(new PorterDuffColorFilter(solidColor(this.outer_shadow_color), PorterDuff.Mode.SRC_ATOP));
                        pt.setFilterBitmap(true);
                        pt.setDither(true);
                        pt.setAlpha(Color.alpha(this.outer_shadow_color));
                        if (isTextOpacityShadowInclude()) {
                            applyAlphaToPaint(pt);
                        }
                        canvas.save();
                        canvas.translate((float) this.outer_shadow_dx, (float) this.outer_shadow_dy);
                        canvas.drawBitmap(this.bitmap_shadow_outer, new Rect(0, 0, this.bitmap_shadow_outer.getWidth(), this.bitmap_shadow_outer.getHeight()), new Rect(-this.outer_shadow_padding, -this.outer_shadow_padding, this.textWidth + this.outer_shadow_padding, this.textHeight + this.outer_shadow_padding), pt);
                        canvas.restore();
                        canvas.restore();
                    }
                    for (int i = 0; i < steps; i++) {
                        canvas.save();
                        rotation_translation3D.translate(0.0f, 0.0f, -1.0f * stepDist);
                        applyCamera(canvas, rotation_translation3D);
                        canvas.drawBitmap(this.bitmap_3d_cache, rectDraw1, rectDraw2, paint3d);
                        canvas.restore();
                    }
                    canvas.save();
                    applyCamera(canvas, rotation3d);
                } else if (this.threeDViewType == 3) {
                    canvas.save();
                    PointF translationVector = new PointF(((float) Math.cos(Math.toRadians((double) this.threeDObliqueAngle))) * ((float) threeDDepthPx), -1.0f * ((float) Math.sin(Math.toRadians((double) this.threeDObliqueAngle))) * ((float) threeDDepthPx));
                    PointF smallVector = invertVector(divideVector(translationVector, steps));
                    canvas.translate(translationVector.x, translationVector.y);
                    if (this.outer_shadow_enabled.booleanValue()) {
                        Paint pt2 = new Paint();
                        pt2.setColorFilter(new PorterDuffColorFilter(solidColor(this.outer_shadow_color), PorterDuff.Mode.SRC_ATOP));
                        pt2.setFilterBitmap(true);
                        pt2.setAlpha(Color.alpha(this.outer_shadow_color));
                        if (isTextOpacityShadowInclude()) {
                            applyAlphaToPaint(pt2);
                        }
                        pt2.setDither(true);
                        canvas.save();
                        canvas.translate((float) this.outer_shadow_dx, (float) this.outer_shadow_dy);
                        canvas.drawBitmap(this.bitmap_shadow_outer, new Rect(0, 0, this.bitmap_shadow_outer.getWidth(), this.bitmap_shadow_outer.getHeight()), new Rect(-this.outer_shadow_padding, -this.outer_shadow_padding, this.textWidth + this.outer_shadow_padding, this.textHeight + this.outer_shadow_padding), pt2);
                        canvas.restore();
                    }
                    for (int i2 = 0; i2 < steps; i2++) {
                        canvas.translate(smallVector.x, smallVector.y);
                        canvas.drawBitmap(this.bitmap_3d_cache, (Rect) null, rectDraw2, paint3d);
                    }
                    canvas.restore();
                }
            }
            if (this.reflectionEnabled && !this.threeDEnabled) {
                canvas.save();
                canvas.translate(0.0f, (((float) (this.bitmap_cache.getHeight() - (this.bottom_padding * 2))) / getRenderScaleF()) + ((float) this.reflection_dy));
                if (this.outer_shadow_enabled.booleanValue()) {
                    Paint pt3 = new Paint();
                    pt3.setColorFilter(new PorterDuffColorFilter(solidColor(this.outer_shadow_color), PorterDuff.Mode.SRC_ATOP));
                    pt3.setFilterBitmap(true);
                    pt3.setDither(true);
                    pt3.setAlpha(Color.alpha(this.outer_shadow_color));
                    if (isTextOpacityShadowInclude()) {
                        applyAlphaToPaint(pt3);
                    }
                    canvas.save();
                    canvas.translate((float) this.outer_shadow_dx, (float) this.outer_shadow_dy);
                    canvas.drawBitmap(this.bitmap_shadow_outer_reflection, new Rect(0, 0, this.bitmap_shadow_outer.getWidth(), this.bitmap_shadow_outer.getHeight()), new Rect(-this.outer_shadow_padding, -this.outer_shadow_padding, this.textWidth + this.outer_shadow_padding, this.textHeight + this.outer_shadow_padding), pt3);
                    canvas.restore();
                }
                canvas.restore();
            }
            this.paint.setColorFilter(null);
            this.paint.setXfermode(null);
            this.paint.setFilterBitmap(true);
            if (this.outer_shadow_enabled.booleanValue() && !this.threeDEnabled) {
                Paint pt4 = new Paint();
                pt4.setColorFilter(new PorterDuffColorFilter(solidColor(this.outer_shadow_color), PorterDuff.Mode.SRC_ATOP));
                pt4.setAlpha(Color.alpha(this.outer_shadow_color));
                pt4.setFilterBitmap(true);
                pt4.setDither(true);
                if (isTextOpacityShadowInclude()) {
                    applyAlphaToPaint(pt4);
                }
                canvas.save();
                canvas.translate((float) this.outer_shadow_dx, (float) this.outer_shadow_dy);
                canvas.drawBitmap(this.bitmap_shadow_outer, new Rect(0, 0, this.bitmap_shadow_outer.getWidth(), this.bitmap_shadow_outer.getHeight()), new Rect(-this.outer_shadow_padding, -this.outer_shadow_padding, this.textWidth + this.outer_shadow_padding, this.textHeight + this.outer_shadow_padding), pt4);
                canvas.restore();
            }
            if (this.reflectionEnabled) {
                canvas.save();
                canvas.translate(0.0f, (((float) (this.bitmap_cache.getHeight() - (this.bottom_padding * 2))) / getRenderScaleF()) + ((float) this.reflection_dy));
                this.paint.setFilterBitmap(true);
                this.paint.setDither(true);
                applyAlphaToPaint(this.paint);
                canvas.drawBitmap(this.bitmap_cache_reflection, new Rect(0, 0, this.bitmap_cache.getWidth(), this.bitmap_cache.getHeight()), new Rect(0, 0, this.textWidth, this.textHeight), this.paint);
                resetAlphaToPaint(this.paint);
                canvas.restore();
            }
            this.paint.setFilterBitmap(true);
            this.paint.setDither(true);
            this.paint.setAntiAlias(true);
            applyAlphaToPaint(this.paint);
            canvas.drawBitmap(this.bitmap_cache, (Rect) null, new Rect(0, 0, this.textWidth, this.textHeight), this.paint);
            resetAlphaToPaint(this.paint);
        }
        canvas.restore();
        canvas.scale(mScale, mScale, viewCenter.x, viewCenter.y);
        canvas.rotate(mRotateAngle, viewCenter.x, viewCenter.y);
        if (this.isSelected && !this.renderMode) {
//            canvas.drawRect(0.0f, 0.0f, (float) this.boundingWidth, (float) this.boundingHeight, this.paintSelectedBg);
            canvas.drawRoundRect(mHelpBoxRect, 10, 10,this.paintSelectedBorder);
//            canvas.drawRect(0.0f, 0.0f, (float) this.boundingWidth, (float) this.boundingHeight, this.paintSelected);
        }
        RectUtil.scaleRect(mHelpBoxRect, mScale);
        if (this.handleEnabled && this.isSelected && !this.isCurved && !this.renderMode) {
//            canvas.drawCircle(this.maxWidther.getX(), this.maxWidther.getY(), (float) getBiggerRadius(), this.paintHandlesBorder);
//            float x = this.maxWidther.getX();
//            float y = this.maxWidther.getY();
//            float biggerRadius = (float) getBiggerRadius();
//            if (this.dragID == this.DRAG_ID_MAX_WIDTHER) {
//                paint2 = this.paintHandlesHighlight;
//            } else {
//                paint2 = this.paintHandles;
//            }
//            canvas.drawCircle(x, y, biggerRadius, paint2);

            RectF rectF = new RectF(0, 0, mMeasureWidth, mMeasureHeight);
//            rectF.offset(center.x - rectF.centerX(), center.y - rectF.centerY());
            mHelpBoxRect.set(rectF);
//
            if (leftTopBitmap != null) {
                RectUtil.rotateRect(leftTopDstRect, mHelpBoxRect.centerX(),
                        mHelpBoxRect.centerY(), mRotateAngle);
                int offsetValue = ((int) leftTopDstRect.width()) >> 1;
                leftTopDstRect.offsetTo(mHelpBoxRect.left - offsetValue,
                        mHelpBoxRect.top - offsetValue);
                leftTopBitmap.setBounds((int) leftTopDstRect.left, (int) leftTopDstRect.top, (int) leftTopDstRect.right, (int) leftTopDstRect.bottom);
                leftTopBitmap.draw(canvas);

            }

            if (rightBottomBitmap != null) {
                RectUtil.rotateRect(rightBottomDstRect, mHelpBoxRect.centerX(),
                        mHelpBoxRect.centerY(), mRotateAngle);
                int offsetValue = ((int) rightBottomDstRect.width()) >> 1;
                rightBottomDstRect.offsetTo(mHelpBoxRect.right - offsetValue,
                        mHelpBoxRect.bottom - offsetValue);
                rightBottomBitmap.setBounds((int) rightBottomDstRect.left, (int) rightBottomDstRect.top, (int) rightBottomDstRect.right, (int) rightBottomDstRect.bottom);
                rightBottomBitmap.draw(canvas);


            }
        }
    }

    /* access modifiers changed from: package-private */
    public int solidColor(int color) {
        return Color.argb(255, Color.red(color), Color.green(color), Color.blue(color));
    }

    private void loadTexture() {
        float f;
        float f2 = 1.0f;
        if (this.textureSrc.getSourceType() == ImageSource.SourceType.local_file) {
            try {
                int[] dims = commonFuncs.getDimensionsFromPath(this.textureSrc.getPath());
                float f3 = (float) dims[0];
                if (this.renderMode) {
                    f = this.texturePortion.width() / 100.0f;
                } else {
                    f = 1.0f;
                }
                dims[0] = (int) (f * f3);
                float f4 = (float) dims[1];
                if (this.renderMode) {
                    f2 = this.texturePortion.height() / 100.0f;
                }
                dims[1] = (int) (f2 * f4);
                this.textureBmb = commonFuncs.cropRotBitmap(commonFuncs.getBitmapFromPath(this.textureSrc.getPath(), commonFuncs.getInSampleSize(dims[0], dims[1], (int) (((float) this.textWidth) * getRenderScaleF()), (int) (((float) this.textHeight) * getRenderScaleF()))), this.texturePortion, this.textureInRot, this.textureFlipH, this.textureFlipV);
            } catch (Exception e) {
                this.textureBmb = null;
            }
        }
    }

    public TextPaint getUsedTextPaint() {
        return this.textDraw.getPaint();
    }

    public int getTextOpacity() {
        return this.textOpacity;
    }

    public void setTextOpacity(int opacity) {
        this.textOpacity = opacity;
        invalidate();
    }

    public boolean isTextOpacityShadowInclude() {
        return this.textOpacityShadowInclude;
    }

    public void setTextOpacityShadowInclude(boolean textOpacityShadowInclude2) {
        this.textOpacityShadowInclude = textOpacityShadowInclude2;
        invalidate();
    }

    private void applyAlphaToPaint(Paint paint2) {
        paint2.setAlpha((int) ((((float) this.textOpacity) / 100.0f) * 255.0f));
    }

    private void resetAlphaToPaint(Paint paint2) {
        paint2.setAlpha(255);
    }

    /* access modifiers changed from: package-private */
    public PointF invertVector(PointF vector) {
        return new PointF(vector.x * -1.0f, vector.y * -1.0f);
    }

    /* access modifiers changed from: package-private */
    public PointF divideVector(PointF vector, int steps) {
        return new PointF(vector.x / ((float) steps), vector.y / ((float) steps));
    }

    public int getThreeDDepth() {
        return this.threeDDepth;
    }

    public boolean isThreeDDepthColorAutomatic() {
        return this.threeDDepthColorAutomatic;
    }

    public int getThreeDDepthColorFill() {
        return this.threeDDepthColorFill;
    }

    public int getThreeDDepthDarken() {
        return this.threeDDepthDarken;
    }

    public int getThreeDDepthFillType() {
        return this.threeDDepthFillType;
    }

//    public GradientMaker.GradientFill getThreeDDepthGradientFill() {
//        return this.threeDDepthGradientFill;
//    }

    private int clipAngle(int angle2) {
        if (this.tempDisable3DRots) {
            return 0;
        }
        if (!this.threeDEnabled) {
            return angle2;
        }
        int upperBound = 90;
        if (this.threeDViewType == 3) {
            return 0;
        }
        if (this.threeDViewType != 1) {
            return angle2;
        }
        if (angle2 <= 90) {
            upperBound = angle2 < -90 ? -90 : angle2;
        }
        return upperBound;
    }

    public boolean isThreeDEnabled() {
        return this.threeDEnabled;
    }

    public boolean isThreeDLightingEnabled() {
        return this.threeDLightingEnabled;
    }

    public int getThreeDLightingSpecularHardness() {
        return this.threeDLightingSpecularHardness;
    }

    public int getThreeDLightingIntensity() {
        return this.threeDLightingIntensity;
    }

    public int getThreeDLightingShadow() {
        return this.threeDLightingShadow;
    }

    public int getThreeDQuality() {
        return this.threeDQuality;
    }

    public boolean isThreeDStokeInclude() {
        return this.threeDStokeInclude;
    }

    public int getThreeDViewType() {
        return this.threeDViewType;
    }

    private Bitmap drawWithMask(Bitmap src, Path mask, int offset, float renderScaleF2) {
        Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmOut);
        Paint ptMask = new Paint();
        ptMask.setFilterBitmap(true);
        ptMask.setDither(true);
        ptMask.setAntiAlias(true);
        ptMask.setColor(-1);
        canvas.drawBitmap(src, 0.0f, 0.0f, ptMask);
        canvas.save();
        canvas.scale(renderScaleF2, renderScaleF2);
        canvas.translate((float) offset, (float) offset);
        ptMask.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPath(mask, ptMask);
        canvas.restore();
        return bmOut;
    }

    private static int getBitmapLeftPadding(Bitmap src) {
        int w = src.getWidth();
        int h = src.getHeight();
        int[] pixels = new int[h];
        for (int hPos = 0; hPos < w; hPos++) {
            src.getPixels(pixels, 0, 1, hPos, 0, 1, h);
            for (int color : pixels) {
                if (Color.alpha(color) != 0) {
                    return hPos;
                }
            }
        }
        return -1;
    }

    private static int getBitmapRightPadding(Bitmap src) {
        int w = src.getWidth();
        int h = src.getHeight();
        int[] pixels = new int[h];
        for (int hPos = w - 1; hPos >= 0; hPos--) {
            src.getPixels(pixels, 0, 1, hPos, 0, 1, h);
            for (int color : pixels) {
                if (Color.alpha(color) != 0) {
                    return w - (hPos + 1);
                }
            }
        }
        return -1;
    }

    private int getBitmapBottomPadding(Bitmap src) {
        int w = src.getWidth();
        int h = src.getHeight();
        int[] pixels = new int[w];
        for (int vPos = h - 1; vPos >= 0; vPos--) {
            src.getPixels(pixels, 0, w, 0, vPos, w, 1);
            for (int color : pixels) {
                if (Color.alpha(color) != 0) {
                    return h - (vPos + 1);
                }
            }
        }
        return 0;
    }

    private int getBitmapTopPadding(Bitmap src) {
        int w = src.getWidth();
        int h = src.getHeight();
        int[] pixels = new int[w];
        for (int vPos = 0; vPos < h; vPos++) {
            src.getPixels(pixels, 0, w, 0, vPos, w, 1);
            for (int color : pixels) {
                if (Color.alpha(color) != 0) {
                    return vPos;
                }
            }
        }
        return 0;
    }

    private EmbossMaskFilter getCurrEmbossEffect() {
        double radLightAngle = Math.toRadians((double) this.EmbossLightAngle);
        if (!this.EmbossEnabled) {
            return null;
        }
        return new EmbossMaskFilter(new float[]{(float) (-1.0d * Math.cos(radLightAngle)), (float) Math.sin(radLightAngle), ((float) this.EmbossIntensity) / 100.0f}, ((float) this.EmbossAmbient) / 100.0f, (((float) this.EmbossHardness) * 6.0f) / 100.0f, (((float) this.EmbossBevel) * 5.0f) / 100.0f);
    }

    private EmbossMaskFilter getCurr3DEmbossEffect() {
        double radLightAngle = Math.toRadians((double) this.EmbossLightAngle);
        if (!this.threeDLightingEnabled) {
            return null;
        }
        return new EmbossMaskFilter(new float[]{(float) (-1.0d * Math.cos(radLightAngle)), (float) Math.sin(radLightAngle), ((float) this.threeDLightingIntensity) / 100.0f}, ((float) (100 - this.threeDLightingShadow)) / 100.0f, (((float) this.threeDLightingSpecularHardness) * 6.0f) / 100.0f, 2.5f);
    }

    private Bitmap flipBitmap(Bitmap src) {
        float local_reflection_top_offset = (((float) src.getHeight()) / ((float) this.bitmap_cache.getHeight())) * ((float) this.top_padding);
        float local_reflection_bottom_offset = (((float) src.getHeight()) / ((float) this.bitmap_cache.getHeight())) * ((float) this.bottom_padding);
        Bitmap fadedBitmap = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas fadeCanvas = new Canvas(fadedBitmap);
        fadeCanvas.scale(1.0f, -1.0f, (float) (src.getWidth() / 2), (float) (src.getHeight() / 2));
        fadeCanvas.drawBitmap(src, 0.0f, 0.0f, (Paint) null);
        Paint reflectionPaint = new Paint();
        reflectionPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        reflectionPaint.setShader(new LinearGradient(0.0f, local_reflection_top_offset, 0.0f, ((float) src.getHeight()) - local_reflection_bottom_offset, Color.argb(0, 255, 255, 255), Color.argb(140, 255, 255, 255), Shader.TileMode.CLAMP));
        fadeCanvas.drawRect(0.0f, local_reflection_top_offset, (float) src.getWidth(), ((float) src.getHeight()) - local_reflection_bottom_offset, reflectionPaint);
        return fadedBitmap;
    }

    private void draw3dShadow(Canvas canvas, Camera rotation3d, int threeDDepthPx, float scaleF) {
        Camera rotation_shadow3D = new Camera();
        rotation_shadow3D.rotateY((float) clipAngle(this.textRotationY));
        rotation_shadow3D.rotateX((float) (clipAngle(this.textRotationX) + 90));
        rotation_shadow3D.translate(0.0f, ((float) threeDDepthPx) - this.viewCenter.y, this.viewCenter.y - (((float) this.bottom_padding) / scaleF));
        canvas.restore();
        canvas.save();
        applyCamera(canvas, rotation_shadow3D);
        Paint pnt3dShadow = new Paint(1);
        pnt3dShadow.setColorFilter(new PorterDuffColorFilter(this.shadow_3d_color, PorterDuff.Mode.SRC_ATOP));
        pnt3dShadow.setAlpha((int) ((((float) this.shadow_3d_transparency) / 100.0f) * 255.0f));
        if (isTextOpacityShadowInclude()) {
            applyAlphaToPaint(pnt3dShadow);
        }
        pnt3dShadow.setDither(true);
        pnt3dShadow.setFilterBitmap(true);
        int additionalPadding = this.shadow_3d_radius + dpToPixels(this.shadow_3d_expand);
        canvas.save();
        canvas.translate(((float) this.left_padding) / scaleF, 0.0f);
        canvas.drawBitmap(this.bitmap_shadow_3d, (Rect) null, new RectF((float) (-additionalPadding), (float) (-additionalPadding), ((float) (this.textWidth + additionalPadding)) - (((float) (this.left_padding + this.right_padding)) / scaleF), (float) (threeDDepthPx + additionalPadding)), pnt3dShadow);
        canvas.restore();
        canvas.restore();
        canvas.save();
        applyCamera(canvas, rotation3d);
    }

    public void setReflection(boolean reflect, int dy) {
        this.reflectionEnabled = reflect;
        this.reflection_dy = dy;
        fullRedraw();
    }

    public void setMax(float max) {
        float max2;
        this.textDraw.userMaxWidth = (double) max;
        if (max <= 0.0f) {
            max2 = 2.14748365E9f;
        } else {
            max2 = Math.max(max, (float) (this.ADDITIONAL_SPACE_HANDLE + this.textDraw.getPaddingRight() + this.textDraw.getPaddingLeft()));
        }
        this.textDraw.setMaxWidth((int) max2);
        fullRedraw();
        requestLayout();
    }

    private Bitmap createShadow3d(int shadowLayer_width, int shadowLayer_height, float scaleF) {
        int expandBy = dpToPixels(this.shadow_3d_expand);
        int additionalPadding = this.shadow_3d_radius + dpToPixels(this.shadow_3d_expand);
        Bitmap bmOut = Bitmap.createBitmap((int) (((float) ((additionalPadding * 2) + shadowLayer_width)) * scaleF), (int) (((float) ((additionalPadding * 2) + shadowLayer_height)) * scaleF), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmOut);
        Paint ptBlur = new Paint();
        ptBlur.setDither(true);
        ptBlur.setAntiAlias(true);
        float blurRad = Math.max(0.1f, (float) this.shadow_3d_radius);
        RectF shadowBox = new RectF(0.0f, 0.0f, (float) ((expandBy * 2) + shadowLayer_width), (float) ((expandBy * 2) + shadowLayer_height));
        ptBlur.setMaskFilter(new BlurMaskFilter(blurRad, BlurMaskFilter.Blur.NORMAL));
        ptBlur.setColor(ViewCompat.MEASURED_STATE_MASK);
        canvas.scale(scaleF, scaleF);
        canvas.translate((float) this.shadow_3d_radius, (float) this.shadow_3d_radius);
        canvas.drawRect(shadowBox, ptBlur);
        return bmOut;
    }

    public Bitmap updateOuter_Shadow(Bitmap src, float scaleF) {
        float blurRad;
        Bitmap bmAlpha;
        if (this.outer_shadow_radius < 1.0f) {
            return src;
        }
        Bitmap bmOut = Bitmap.createBitmap((int) (((float) (this.textWidth + (this.outer_shadow_padding * 2))) * scaleF), (int) (((float) (this.textHeight + (this.outer_shadow_padding * 2))) * scaleF), Bitmap.Config.ARGB_8888);
        float glow_radius = (this.outer_shadow_radius / 2.0f) * scaleF;
        if (this.outer_glow_enabled) {
            Bitmap bmDilate = addPadding(src, (int) glow_radius);
            dilate(bmDilate, (int) glow_radius);
            bmAlpha = bmDilate.extractAlpha();
            blurRad = glow_radius;
        } else {
            blurRad = this.outer_shadow_radius * scaleF;
            bmAlpha = src.extractAlpha();
        }
        Canvas canvas = new Canvas(bmOut);
        Paint ptBlur = new Paint();
        ptBlur.setDither(true);
        ptBlur.setAntiAlias(true);
        ptBlur.setMaskFilter(new BlurMaskFilter(blurRad, BlurMaskFilter.Blur.NORMAL));
        Rect dilateBounds = new Rect(0, 0, bmAlpha.getWidth(), bmAlpha.getHeight());
        dilateBounds.offset((int) ((((float) bmOut.getWidth()) / 2.0f) - dilateBounds.exactCenterX()), (int) ((((float) bmOut.getHeight()) / 2.0f) - dilateBounds.exactCenterY()));
        canvas.drawBitmap(bmAlpha, (Rect) null, dilateBounds, ptBlur);
        return bmOut;
    }

    public Bitmap dilate(Bitmap bmOut, int rad) {
        int srcWidth = bmOut.getWidth();
        int srcHeight = bmOut.getHeight();
        int[] pixels = new int[(srcWidth * srcHeight)];
        bmOut.getPixels(pixels, 0, srcWidth, 0, 0, srcWidth, srcHeight);
        for (int y = 0; y < srcHeight; y++) {
            for (int x = 0; x < srcWidth; x++) {
                int index = (y * srcWidth) + x;
                int pixel = Color.alpha(pixels[index]);
                int startY = Math.max(y - rad, 0);
                int EndY = Math.min(y + rad, srcHeight - 1);
                for (int j = startY; j <= EndY; j++) {
                    pixel = Math.max(pixel, Color.alpha(pixels[(j * srcWidth) + x]));
                    if (pixel == 255) {
                        break;
                    }
                }
                pixels[index] = Color.argb(pixel, pixel, 255, 255);
            }
        }
        for (int y2 = 0; y2 < srcHeight; y2++) {
            for (int x2 = 0; x2 < srcWidth; x2++) {
                int index2 = (y2 * srcWidth) + x2;
                int pixel2 = Color.red(pixels[index2]);
                int startX = Math.max(x2 - rad, 0);
                int EndX = Math.min(x2 + rad, srcWidth - 1);
                for (int i = startX; i <= EndX; i++) {
                    pixel2 = Math.max(pixel2, Color.red(pixels[(y2 * srcWidth) + i]));
                    if (pixel2 == 255) {
                        break;
                    }
                }
                pixels[index2] = Color.argb(255, pixel2, pixel2, 255);
            }
        }
        for (int y3 = 0; y3 < srcHeight; y3++) {
            for (int x3 = 0; x3 < srcWidth; x3++) {
                int index3 = (y3 * srcWidth) + x3;
                pixels[index3] = Color.argb(Color.green(pixels[index3]), 255, 255, 255);
            }
        }
        bmOut.setPixels(pixels, 0, srcWidth, 0, 0, srcWidth, srcHeight);
        return bmOut;
    }

    /* access modifiers changed from: package-private */
    public Bitmap addPadding(Bitmap bmp, int padding) {
        Bitmap bmOut = Bitmap.createBitmap(bmp.getWidth() + (padding * 2), bmp.getHeight() + (padding * 2), Bitmap.Config.ARGB_8888);
        new Canvas(bmOut).drawBitmap(bmp, (Rect) null, new Rect(padding, padding, bmp.getWidth() + padding, bmp.getHeight() + padding), (Paint) null);
        return bmOut;
    }

    public Bitmap blurMask(Bitmap src) {
        Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        Paint alphaPaint = new Paint(1);
        alphaPaint.setAntiAlias(true);
        alphaPaint.setDither(true);
        Canvas canvas = new Canvas();
        canvas.setBitmap(bmOut);
        alphaPaint.setMaskFilter(new BlurMaskFilter(2.0f, BlurMaskFilter.Blur.INNER));
        canvas.drawBitmap(src.extractAlpha(), 0.0f, 0.0f, alphaPaint);
        alphaPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(src, 0.0f, 0.0f, alphaPaint);
        return bmOut;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.textDraw.measure(0, 0);
        this.textWidth = this.textDraw.getMeasuredWidth();
        this.textHeight = this.textDraw.getMeasuredHeight();
        this.boundingWidth = this.textWidth;
        this.boundingHeight = this.textHeight;
        setMeasuredDimension(this.boundingWidth + this.smallBallRadiusPx + this.ADDITIONAL_SPACE_HANDLE, this.boundingHeight);
        setPivotX(((float) this.textWidth) / 2.0f);
        setPivotY(((float) this.textHeight) / 2.0f);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (firstInflate) {
            int x = ((helperClass.getContainerWidth() - getWidth()) / 2);
            int y = ((helperClass.getContainerHeight() - getHeight()) / 2);
            setX(x);
            setY(y);
            firstInflate = false;
        }
        mMeasureWidth = getWidth();
        mMeasureHeight = getHeight();
    }

    private void updateHandlePos() {
        this.maxWidther.setX((float) (this.textWidth + this.ADDITIONAL_SPACE_HANDLE));
        this.maxWidther.setY(((float) this.textHeight) / 2.0f);
    }

    public void setTextSize(float new_size_unchecked) {
        this.textDraw.updateTextSize(new_size_unchecked > 0.0f ? new_size_unchecked : 1.0f);
        requestLayout();
        fullRedraw();
    }

    public float getTextSize() {
        return this.textDraw.userTextSize;
    }

    public void rotateText(float n_angle) {
        this.angle = n_angle % 360.0f;
        setRotation(this.angle);
    }

    public boolean isSelected() {
        return this.isSelected;
    }

    public void toggleSelect(boolean selected) {
        boolean oldSelected = this.isSelected;
        this.isSelected = selected;
        if (oldSelected != this.isSelected) {
            if (this.layersListener != null) {
                this.layersListener.selectionChanged(this.isSelected);
            }
            requestLayout();
            invalidate();
        }
    }

    public String returnText() {
        return this.textDraw.returnActualText();
    }

    public void setBold(int start, int end) {
        this.textDraw.setBoldToSelection(start, end);
        this.textDraw.textUpdated();
        requestLayout();
        fullRedraw();
    }

    public void setItalic(int start, int end) {
        this.textDraw.setItalicToSelection(start, end);
        this.textDraw.textUpdated();
        requestLayout();
        fullRedraw();
    }

    public void setUnderline(int start, int end) {
        this.textDraw.setUnderToSelection(start, end);
        this.textDraw.textUpdated();
        requestLayout();
        fullRedraw();
    }

    public void clearStyles(int start, int end) {
        this.textDraw.clearStyles(start, end);
        this.textDraw.textUpdated();
        requestLayout();
        fullRedraw();
    }

    public void setLineSpacing(int spacing) {
        this.textDraw.setLineSpacing((float) spacing, 1.0f);
        this.textDraw.spacing = spacing;
        requestLayout();
        fullRedraw();
    }

    public int getLineSpacing() {
        return this.textDraw.spacing;
    }

    /* access modifiers changed from: package-private */
    public boolean isNotVisible() {
        if (!helperClass.safetyCheck()) {
            return false;
        }
        Rect containerRect = new Rect(0, 0, helperClass.getContainerWidth(), helperClass.getContainerHeight());
        Point p1 = new Point(getLeft(), getTop());
        Point p2 = new Point(getLeft(), getBottom());
        Point p3 = new Point(getRight(), getTop());
        Point p4 = new Point(getRight(), getBottom());
        if (containerRect.contains(p1.x, p1.y) || containerRect.contains(p2.x, p2.y) || containerRect.contains(p3.x, p3.y) || containerRect.contains(p4.x, p4.y)) {
            return false;
        }
        return true;
    }

    public void visibilityCheck() {
        if (isNotVisible()) {
            makeMeVisible();
        }
    }

    private void makeMeVisible() {
        setX(0.0f);
        setY(0.0f);
    }

    public void rearrange(int w, int h, int oldw, int oldh) {
        if (h != 0 && w != 0) {
            if (oldw == 0) {
                oldw = w;
            }
            if (oldh == 0) {
                oldh = h;
            }
            float yRelative = getY() / ((float) oldh);
            setX(((float) w) * (getX() / ((float) oldw)));
            setY(((float) h) * yRelative);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.setDrawFilter(new PaintFlagsDrawFilter(1, 1));
        super.dispatchDraw(canvas);
    }

    public boolean isNeverEdited() {
        return this.neverEdited;
    }

    public void setText(String str, boolean reAdjustMax) {
        setText(str, reAdjustMax, -1);
    }

    public void setText(String str, boolean reAdjustMax, int oldTextLength) {
        int oldStrLines = countLines(this.textDraw.returnActualText());
        int displayStrLines = this.textDraw.getLineCount();
        this.textDraw.updateText(str, oldTextLength);
        this.neverEdited = false;
        if (displayStrLines == oldStrLines) {
            this.textDraw.userMaxWidth = -1.0d;
            this.textDraw.setMaxWidth(Integer.MAX_VALUE);
        }
        this.textDraw.measure(0, 0);
        this.textWidth = this.textDraw.getMeasuredWidth();
        this.textHeight = this.textDraw.getMeasuredHeight();
        if (reAdjustMax) {
            float shouldMaxAt = (((float) helperClass.getContainerWidth()) - getX()) - ((float) this.smallBallRadiusPx);
            if (((float) this.textWidth) > shouldMaxAt) {
                this.textDraw.userMaxWidth = (double) shouldMaxAt;
                this.textDraw.setMaxWidth((int) shouldMaxAt);
            }
        }
        if (this.layersListener != null) {
            this.layersListener.textChanged();
        }
        requestLayout();
        fullRedraw();
    }

    private List<Integer> rotateRectFix(int left, int top, int width, int height, int angle2) {
        List<Integer> rotatedRectFix = new ArrayList<>();
        Point center = new Point(Math.abs(width - left) / 2, Math.abs(height - top) / 2);
        Point point1 = rotatePoint(new Point(left, top), center, (double) angle2);
        Point point2 = rotatePoint(new Point(left, height), center, (double) angle2);
        Point point3 = rotatePoint(new Point(width, top), center, (double) angle2);
        Point point4 = rotatePoint(new Point(width, height), center, (double) angle2);
        int paddingLeft = Math.min(point1.x, Math.min(point2.x, Math.min(point3.x, point4.x))) * -1;
        int paddingTop = Math.min(point1.y, Math.min(point2.y, Math.min(point3.y, point4.y))) * -1;
        int newWidth = Math.abs(Math.max(point1.x, Math.max(point2.x, Math.max(point3.x, point4.x))) + paddingLeft);
        int newHeight = Math.abs(Math.max(point1.y, Math.max(point2.y, Math.max(point3.y, point4.y))) + paddingTop);
        rotatedRectFix.add(Integer.valueOf(newWidth));
        rotatedRectFix.add(Integer.valueOf(newHeight));
        rotatedRectFix.add(Integer.valueOf(paddingLeft));
        rotatedRectFix.add(Integer.valueOf(paddingTop));
        return rotatedRectFix;
    }

    /* access modifiers changed from: package-private */
    public int countLines(String str) {
        int lines = 1;
        int pos = 0;
        while (true) {
            pos = str.indexOf("\n", pos) + 1;
            if (pos == 0) {
                return lines;
            }
            lines++;
        }
    }

    private Point rotatePoint(Point pointToRotate, Point centerPoint, double angleInDegrees) {
        double angleInRadians = angleInDegrees * 0.017453292519943295d;
        double cosTheta = Math.cos(angleInRadians);
        double sinTheta = Math.sin(angleInRadians);
        Point rotated = new Point();
        rotated.x = (int) Math.floor(((((double) (pointToRotate.x - centerPoint.x)) * cosTheta) - (((double) (pointToRotate.y - centerPoint.y)) * sinTheta)) + ((double) centerPoint.x));
        rotated.y = (int) Math.floor((((double) (pointToRotate.x - centerPoint.x)) * sinTheta) + (((double) (pointToRotate.y - centerPoint.y)) * cosTheta) + ((double) centerPoint.y));
        return rotated;
    }

    public class HandleBall {
        Context mContext;
        PointF point;

        HandleBall(Context context, PointF point2) {
            this.mContext = context;
            this.point = point2;
        }

        public float getX() {
            return this.point.x;
        }

        public float getY() {
            return this.point.y;
        }

        public void setX(float x) {
            this.point.x = x;
        }

        public void setY(float y) {
            this.point.y = y;
        }
    }

    public class MaskBall {
        Context mContext;
        float percentage = 0.8f;
        PointF point;

        MaskBall(Context context, PointF point2) {
            this.mContext = context;
            this.point = point2;
        }

        public PointF getPoint() {
            return new PointF(getX(), getY());
        }

        public float getX() {
            return this.point.x;
        }

        public void setX(float x) {
            this.point.x = x;
        }

        public float getY() {
            return this.point.y;
        }

        public void setY(int y) {
            if (y < TextComponent.this.smallBallRadiusPx) {
                this.point.y = (float) TextComponent.this.smallBallRadiusPx;
            } else if (y < TextComponent.this.smallBallRadiusPx || y >= TextComponent.this.getMeasuredHeight() - TextComponent.this.smallBallRadiusPx) {
                this.point.y = (float) (TextComponent.this.getMeasuredHeight() - TextComponent.this.smallBallRadiusPx);
            } else {
                this.point.y = (float) y;
            }
        }

        /* access modifiers changed from: package-private */
        public void calculatePercent() {
            this.percentage = getY() / ((float) TextComponent.this.getMeasuredHeight());
        }

        /* access modifiers changed from: package-private */
        public float getPercentage() {
            return this.percentage;
        }

        /* access modifiers changed from: package-private */
        public void setPercentage(float percentage2) {
            this.percentage = percentage2;
        }

        /* access modifiers changed from: package-private */
        public void updateWithPercent() {
            setY((int) (((float) TextComponent.this.textHeight) * this.percentage));
        }
    }

    /* access modifiers changed from: package-private */
    public Bundle styleIntervalsToBundle(spansIntervals spansIds) {
        Bundle bundle = new Bundle();
        int j = 1;
        for (interval i : spansIds.getIntervals()) {
            Bundle bundle1 = new Bundle();
            bundle1.putInt(appStateConstants.TEXTS_SPANS_INTERVALS_START, i.getStart());
            bundle1.putInt(appStateConstants.TEXTS_SPANS_INTERVALS_END, i.getEnd());
            bundle.putBundle("interval" + j, bundle1);
            j++;
        }
        return bundle;
    }

    /* access modifiers changed from: package-private */
    public Bundle colorIntervalsToBundle(spansIntervals spansIds) {
        Bundle bundle = new Bundle();
        int j = 1;
        for (interval i : spansIds.getIntervals()) {
            Bundle bundle1 = new Bundle();
            bundle1.putInt(appStateConstants.TEXTS_SPANS_INTERVALS_START, i.getStart());
            bundle1.putInt(appStateConstants.TEXTS_SPANS_INTERVALS_END, i.getEnd());
            bundle1.putInt(appStateConstants.TEXTS_SPANS_INTERVALS_COLOR, i.getColor());
            bundle.putBundle("interval" + j, bundle1);
            j++;
        }
        return bundle;
    }

    public static Bundle fontIntervalsToBundle(spansIntervals spansIds) {
        Bundle bundle = new Bundle();
        int j = 1;
        for (interval i : spansIds.getIntervals()) {
            Bundle bundle1 = new Bundle();
            bundle1.putInt(appStateConstants.TEXTS_SPANS_INTERVALS_START, i.getStart());
            bundle1.putInt(appStateConstants.TEXTS_SPANS_INTERVALS_END, i.getEnd());
            bundle1.putString(appStateConstants.TEXTS_SPANS_INTERVALS_FONT, i.getFont().getPath());
            bundle.putBundle("interval" + j, bundle1);
            j++;
        }
        return bundle;
    }

    /* access modifiers changed from: package-private */
    public spansIntervals bundleToStyleIntervals(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        spansIntervals spans = new spansIntervals();
        for (String key : bundle.keySet()) {
            if (bundle.get(key) instanceof Bundle) {
                Bundle intervalBundle = bundle.getBundle(key);
                spans.addIntervalNoCheck(new interval(intervalBundle.getInt(appStateConstants.TEXTS_SPANS_INTERVALS_START, 0), intervalBundle.getInt(appStateConstants.TEXTS_SPANS_INTERVALS_END, 0)));
            }
        }
        return spans;
    }

    /* access modifiers changed from: package-private */
    public spansIntervals bundleToColorIntervals(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        spansIntervals spans = new spansIntervals();
        for (String key : bundle.keySet()) {
            if (bundle.get(key) instanceof Bundle) {
                Bundle intervalBundle = bundle.getBundle(key);
                spans.addIntervalNoCheck(new interval(intervalBundle.getInt(appStateConstants.TEXTS_SPANS_INTERVALS_START, 0), intervalBundle.getInt(appStateConstants.TEXTS_SPANS_INTERVALS_END, 0), intervalBundle.getInt(appStateConstants.TEXTS_SPANS_INTERVALS_COLOR, ViewCompat.MEASURED_STATE_MASK)));
            }
        }
        return spans;
    }

    /* access modifiers changed from: package-private */
    public spansIntervals bundleToFontIntervals(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        spansIntervals spans = new spansIntervals();
        for (String key : bundle.keySet()) {
            if (bundle.get(key) instanceof Bundle) {
                Bundle intervalBundle = bundle.getBundle(key);
                int start = intervalBundle.getInt(appStateConstants.TEXTS_SPANS_INTERVALS_START, 0);
                int end = intervalBundle.getInt(appStateConstants.TEXTS_SPANS_INTERVALS_END, 0);
                String fontPath = intervalBundle.getString(appStateConstants.TEXTS_SPANS_INTERVALS_FONT);
                if (fontPath == null) {
                    fontPath = "";
                }
                spans.addIntervalNoCheck(new interval(start, end, new customTypeface(fontPath, createFontFromPath(fontPath))));
            }
        }
        return spans;
    }

    public Bundle saveToBundle() {
        if (this.root == null) {
            this.root = (textContainer) getParent();
        }
        Bundle bundle = new Bundle();
        bundle.putString(appStateConstants.TEXT_REFERENCE, this.reference);
        bundle.putBoolean(appStateConstants.OBJECT_HIDDEN, this.hidden);
        bundle.putBoolean(appStateConstants.OBJECT_LOCKED, this.locked);
        bundle.putString(appStateConstants.TEXT_TEXT_STRING, returnText());
        bundle.putInt(appStateConstants.TEXT_MAX_WIDTH, (int) this.textDraw.userMaxWidth);
        bundle.putString(appStateConstants.TEXT_TEXT_SIZE, String.valueOf(getTextSize()));
        bundle.putBundle(appStateConstants.TEXT_TEXT_FONT, fontIntervalsToBundle(this.textDraw.fontsIds));
        bundle.putString(appStateConstants.TEXT_ROTATION, String.valueOf(this.angle));
        bundle.putBundle(appStateConstants.TEXT_BOLD, styleIntervalsToBundle(this.textDraw.boldIds));
        bundle.putBundle(appStateConstants.TEXT_ITALIC, styleIntervalsToBundle(this.textDraw.italicIds));
        bundle.putBundle(appStateConstants.TEXT_UNDER, styleIntervalsToBundle(this.textDraw.underIds));
        bundle.putInt(appStateConstants.TEXT_ALIGNMENT, getTextAlign());
        bundle.putInt(appStateConstants.TEXT_LETTER_SPACING, getLetterSpacing());
        bundle.putBoolean(appStateConstants.TEXT_LETTER_SPACE_WORDS, this.textDraw.isWordSpacing());
        bundle.putBoolean(appStateConstants.TEXT_JUSTIFY, getTextJustify());
        bundle.putInt(appStateConstants.TEXT_LINE_SPACING, getLineSpacing());
        bundle.putBoolean(appStateConstants.TEXT_BG_ENABLED, getBackground_enabled());
        bundle.putInt(appStateConstants.TEXT_BG_COLOR, this.background_color);
//        bundle.putString(appStateConstants.TEXT_BG_GRADIENT_V2, this.background_usedGradient.convertToStringV2());
        bundle.putInt(appStateConstants.TEXT_BG_FILL_TYPE, this.background_fill_type);
        bundle.putInt(appStateConstants.TEXT_BG_PADDING_LEFT, (int) this.background_addHorizontalL);
        bundle.putInt(appStateConstants.TEXT_BG_PADDING_RIGHT, (int) this.background_addHorizontalR);
        bundle.putInt(appStateConstants.TEXT_BG_PADDING_TOP, (int) this.background_addVerticalT);
        bundle.putInt(appStateConstants.TEXT_BG_PADDING_BOTTOM, (int) this.background_addVerticalB);
        bundle.putInt(appStateConstants.TEXT_BG_BORDER_RADIUS, this.background_border_radius);
        bundle.putBoolean(appStateConstants.TEXT_STROKE_ENABLED, getStrokeEnabled());
        bundle.putString(appStateConstants.TEXT_STROKE_WIDTH, String.valueOf(this.textDraw.strokeWidth));
        bundle.putInt(appStateConstants.TEXT_STROKE_COLOR, getStrokeColor());
        bundle.putInt(appStateConstants.TEXT_STROKE_FILL_TYPE, getStrokeFillType());
//        bundle.putString(appStateConstants.TEXT_STROKE_GRADIENT_V2, getStrokeGradient().convertToStringV2());
        bundle.putBoolean(appStateConstants.TEXT_INNER_ENABLED, getInnerEnabled());
        bundle.putString(appStateConstants.TEXT_INNER_RADIUS, String.valueOf(this.textDraw.inner_r));
        bundle.putInt(appStateConstants.TEXT_INNER_COLOR, getInnerColor());
        bundle.putString(appStateConstants.TEXT_INNER_DX, String.valueOf(this.textDraw.inner_dx));
        bundle.putString(appStateConstants.TEXT_INNER_DY, String.valueOf(this.textDraw.inner_dy));
        bundle.putBoolean(appStateConstants.TEXT_OUTER_ENABLED, getOuterShadowEnabled());
        bundle.putBoolean(appStateConstants.TEXT_OUTER_GLOW_ENABLED, getOuterGlowEnabled());
        bundle.putString(appStateConstants.TEXT_OUTER_RADIUS, String.valueOf(this.outer_shadow_radius));
        bundle.putInt(appStateConstants.TEXT_OUTER_COLOR, getOuterShadowColor());
        bundle.putInt(appStateConstants.TEXT_OUTER_DX, this.outer_shadow_dx);
        bundle.putInt(appStateConstants.TEXT_OUTER_DY, this.outer_shadow_dy);
        bundle.putInt(appStateConstants.TEXT_ROTATION_X, this.textRotationX);
        bundle.putInt(appStateConstants.TEXT_ROTATION_Y, this.textRotationY);
        bundle.putBundle(appStateConstants.TEXT_TEXT_COLOR, colorIntervalsToBundle(this.textDraw.colorIds));
        bundle.putBoolean(appStateConstants.TEXT_EMBOSS_ENABLED, isEmbossEnabled());
        bundle.putInt(appStateConstants.TEXT_EMBOSS_LIGHT_ANGLE, getEmbossLightAngle());
        bundle.putInt(appStateConstants.TEXT_EMBOSS_INTENSITY, getEmbossIntensity());
        bundle.putInt(appStateConstants.TEXT_EMBOSS_AMBIENT, getEmbossAmbient());
        bundle.putInt(appStateConstants.TEXT_EMBOSS_HARDNESS, getEmbossHardness());
        bundle.putInt(appStateConstants.TEXT_EMBOSS_BEVEL, getEmbossBevel());
        bundle.putBoolean(appStateConstants.TEXT_EMBOSS_INCLUDE_STROKE, isEmbossIncludeStroke());
        bundle.putBoolean(appStateConstants.TEXT_THREE_D_ENABLED, isThreeDEnabled());
        bundle.putInt(appStateConstants.TEXT_THREE_D_VIEW_TYPE, getThreeDViewType());
        bundle.putInt(appStateConstants.TEXT_THREE_D_DEPTH, getThreeDDepth());
        bundle.putInt(appStateConstants.TEXT_THREE_D_DEPTH_FILL_TYPE, getThreeDDepthFillType());
        bundle.putInt(appStateConstants.TEXT_THREE_D_DEPTH_COLOR_FILL, getThreeDDepthColorFill());
//        bundle.putString(appStateConstants.TEXT_THREE_D_DEPTH_GRADIENT_FILL_V2, getThreeDDepthGradientFill().convertToStringV2());
        bundle.putBoolean(appStateConstants.TEXT_THREE_D_DEPTH_COLOR_AUTOMATIC, isThreeDDepthColorAutomatic());
        bundle.putInt(appStateConstants.TEXT_THREE_D_DEPTH_DARKEN, getThreeDDepthDarken());
        bundle.putBoolean(appStateConstants.TEXT_THREE_D_STOKE_INCLUDE, isThreeDStokeInclude());
        bundle.putInt(appStateConstants.TEXT_THREE_D_OBLIQUE_ANGLE, getThreeDObliqueAngle());
        bundle.putBoolean(appStateConstants.TEXT_THREE_D_LIGHTING_ENABLED, isThreeDLightingEnabled());
        bundle.putInt(appStateConstants.TEXT_THREE_D_LIGHTING_INTENSITY, getThreeDLightingIntensity());
        bundle.putInt(appStateConstants.TEXT_THREE_D_LIGHTING_SHADOW, getThreeDLightingShadow());
        bundle.putInt(appStateConstants.TEXT_THREE_D_LIGHTING_SPECULAR_HARDNESS, getThreeDLightingSpecularHardness());
        bundle.putInt(appStateConstants.TEXT_OPACITY, getTextOpacity());
        bundle.putBoolean(appStateConstants.TEXT_OPACITY_SHADOW_INCLUDE, isTextOpacityShadowInclude());
        bundle.putBoolean(appStateConstants.TEXT_THREE_D_SHADOW_ENABLED, isShadow_3d_enabled());
        bundle.putInt(appStateConstants.TEXT_THREE_D_SHADOW_COLOR, getShadow_3d_color());
        bundle.putInt(appStateConstants.TEXT_THREE_D_SHADOW_TRANSPARENCY, getShadow_3d_transparency());
        bundle.putInt(appStateConstants.TEXT_THREE_D_SHADOW_BLUR, getShadow_3d_radius());
        bundle.putInt(appStateConstants.TEXT_THREE_D_SHADOW_EXPAND, getShadow_3d_expand());
//        bundle.putString(appStateConstants.TEXT_TEXT_GRADIENT_V2, this.usedGradient.convertToStringV2());
        bundle.putInt(appStateConstants.TEXT_TEXT_FILL_TYPE, this.fill_type);
        bundle.putInt(appStateConstants.TEXT_TEXT_CURVE, getCurve());
        bundle.putBoolean(appStateConstants.TEXT_REFLECTION_ENABLED, this.reflectionEnabled);
        bundle.putInt(appStateConstants.TEXT_REFLECTION_OFFSET, this.reflection_dy);
        bundle.putBoolean(appStateConstants.TEXT_TEXTURE_IMAGE_SRC_V2, true);
        bundle.putBundle(appStateConstants.TEXT_TEXTURE_IMAGE_SRC, this.textureSrc.toBundle());
//        bundle.putBundle(appStateConstants.TEXT_TEXTURE_IMAGE_CROP_INFO, CropInfo.makeBundle(this.texturePortion, this.textureInRot, false, this.textureFlipH, this.textureFlipV));
        bundle.putBoolean(appStateConstants.TEXT_TEXTURE_MAINTAIN_ASPECT, isTextureMaintainAspect());
        bundle.putInt(appStateConstants.TEXT_TEXTURE_SCALE, getTextureScale());
        bundle.putBoolean(appStateConstants.TEXT_BEZIER_MASK_ENABLED, this.bezierMaskEnabled);
        if (this.bezierMaskEnabled && this.bezierMaskBundle != null) {
            bundle.putBoolean(appStateConstants.TEXT_BEZIER_MASK_IN, this.bezierMaskIn);
            bundle.putBundle(appStateConstants.TEXT_BEZIER_MASK_DATA, this.bezierMaskBundle);
        }
        bundle.putInt("shapeX", (int) getX());
        bundle.putInt("shapeY", (int) getY());
        bundle.putInt("shapeOldContainerWidth", helperClass.getContainerWidth());
        bundle.putInt("shapeOldContainerHeight", helperClass.getContainerHeight());
        bundle.putInt(appStateConstants.TEXT_PADDING_LEFT, getInPaddingLeft());
        bundle.putInt(appStateConstants.TEXT_PADDING_RIGHT, getInPaddingRight());
        return bundle;
    }

    public void applyBundle(Bundle bundle, boolean copy) {
        applyBundle(bundle, copy, false);
    }

    public void applyBundle(Bundle bundle, boolean copy, boolean isStyle) {
//        GradientMaker.GradientFill importedGradientBG;
//        GradientMaker.GradientFill importedGradient;
//        GradientMaker.GradientFill importedGradientTxt;
//        GradientMaker.GradientFill importedGradient2;
        freezeFullRedraw();
        if (!copy && !isStyle) {
            this.hidden = bundle.getBoolean(appStateConstants.OBJECT_HIDDEN);
            this.locked = bundle.getBoolean(appStateConstants.OBJECT_LOCKED);
        }
        int oldContainerWidth = bundle.getInt("shapeOldContainerWidth");
        int oldContainerHeight = bundle.getInt("shapeOldContainerHeight");
        int newContainer_w = helperClass.getContainerWidth();
        float scaleX = ((float) newContainer_w) / ((float) oldContainerWidth);
        float scaleY = ((float) helperClass.getContainerHeight()) / ((float) oldContainerHeight);
        if (!copy && !isStyle) {
            setX(((float) bundle.getInt("shapeX")) * scaleX);
            setY(((float) bundle.getInt("shapeY")) * scaleY);
        }
        if (!isStyle) {
            setTextSize(Float.valueOf(bundle.getString(appStateConstants.TEXT_TEXT_SIZE)).floatValue() * scaleY);
            float newMax = ((float) bundle.getInt(appStateConstants.TEXT_MAX_WIDTH)) * scaleX;
            if (newMax <= 0.0f) {
                newMax = -1.0f;
            }
            setMax(newMax);
        }
        set3dEnabled(bundle.getBoolean(appStateConstants.TEXT_THREE_D_ENABLED, isThreeDEnabled()));
        if (isThreeDEnabled()) {
            set3dViewType(bundle.getInt(appStateConstants.TEXT_THREE_D_VIEW_TYPE, getThreeDViewType()));
            set3dDepth(bundle.getInt(appStateConstants.TEXT_THREE_D_DEPTH), bundle.getInt(appStateConstants.TEXT_THREE_D_DEPTH_DARKEN, getThreeDDepthDarken()), getThreeDQuality(), bundle.getBoolean(appStateConstants.TEXT_THREE_D_STOKE_INCLUDE, isThreeDStokeInclude()));
            String strGradient = bundle.getString(appStateConstants.TEXT_THREE_D_DEPTH_GRADIENT_FILL_V2);
            if (strGradient == null) {
//                importedGradient2 = new GradientMaker.GradientFill(bundle.getInt(appStateConstants.TEXT_THREE_D_DEPTH_GRADIENT_FILL_TYPE, getThreeDDepthGradientFill().getType()), bundle.getInt(appStateConstants.TEXT_THREE_D_DEPTH_GRADIENT_FILL_START, getThreeDDepthGradientFill().getStartColor()), bundle.getInt(appStateConstants.TEXT_THREE_D_DEPTH_GRADIENT_FILL_END, getThreeDDepthGradientFill().getEndColor()));
            } else {
//                importedGradient2 = new GradientMaker.GradientFill(strGradient);
            }
//            set3dDepthColor(bundle.getInt(appStateConstants.TEXT_THREE_D_DEPTH_FILL_TYPE, getThreeDDepthFillType()), bundle.getInt(appStateConstants.TEXT_THREE_D_DEPTH_COLOR_FILL, getThreeDDepthColorFill()), importedGradient2, bundle.getBoolean(appStateConstants.TEXT_THREE_D_DEPTH_COLOR_AUTOMATIC, isThreeDDepthColorAutomatic()));
            set3dLighting(bundle.getBoolean(appStateConstants.TEXT_THREE_D_LIGHTING_ENABLED, isThreeDLightingEnabled()), bundle.getInt(appStateConstants.TEXT_EMBOSS_LIGHT_ANGLE, getEmbossLightAngle()), bundle.getInt(appStateConstants.TEXT_THREE_D_LIGHTING_INTENSITY, getThreeDLightingIntensity()), bundle.getInt(appStateConstants.TEXT_THREE_D_LIGHTING_SHADOW, getThreeDLightingShadow()), bundle.getInt(appStateConstants.TEXT_THREE_D_LIGHTING_SPECULAR_HARDNESS, getThreeDLightingSpecularHardness()));
            int obliqueAngle = bundle.getInt(appStateConstants.TEXT_THREE_D_OBLIQUE_ANGLE, -1);
            if (obliqueAngle == -1) {
                obliqueAngle = (int) Math.toDegrees(Math.atan2((double) bundle.getInt(appStateConstants.TEXT_THREE_D_OFFSET_X, 1), (double) bundle.getInt(appStateConstants.TEXT_THREE_D_OFFSET_Y, 1)));
            }
            set3dObliqueAngle(obliqueAngle);
        }
        setEmboss(bundle.getBoolean(appStateConstants.TEXT_EMBOSS_ENABLED, isEmbossEnabled()), bundle.getInt(appStateConstants.TEXT_EMBOSS_LIGHT_ANGLE, getEmbossLightAngle()), bundle.getInt(appStateConstants.TEXT_EMBOSS_INTENSITY, getEmbossIntensity()), bundle.getInt(appStateConstants.TEXT_EMBOSS_AMBIENT, getEmbossAmbient()), bundle.getInt(appStateConstants.TEXT_EMBOSS_HARDNESS, getEmbossHardness()), bundle.getInt(appStateConstants.TEXT_EMBOSS_BEVEL, getEmbossBevel()));
        setEmbossIncludeStroke(bundle.getBoolean(appStateConstants.TEXT_EMBOSS_INCLUDE_STROKE, isEmbossIncludeStroke()));
        setTextOpacity(bundle.getInt(appStateConstants.TEXT_OPACITY, getTextOpacity()));
        setTextOpacityShadowInclude(bundle.getBoolean(appStateConstants.TEXT_OPACITY_SHADOW_INCLUDE, isTextOpacityShadowInclude()));
        if (!bundle.getBoolean(appStateConstants.TEXT_TEXTURE_IMAGE_SRC_V2)) {
            setNewTextureSrc(bundle.getString(appStateConstants.TEXT_TEXTURE_IMAGE_PATH));
        } else {
//            CropInfo cropInfo = new CropInfo(bundle.getBundle(appStateConstants.TEXT_TEXTURE_IMAGE_CROP_INFO));
//            setNewTextureSrc(new ImageSource(bundle.getBundle(appStateConstants.TEXT_TEXTURE_IMAGE_SRC)), cropInfo.getRot(), cropInfo.getPortion(), cropInfo.isFlipH(), cropInfo.isFlipV());
        }
        setTextureMaintainAspect(bundle.getBoolean(appStateConstants.TEXT_TEXTURE_MAINTAIN_ASPECT, isTextureMaintainAspect()));
        setTextureScale(bundle.getInt(appStateConstants.TEXT_TEXTURE_SCALE, getTextureScale()));
        setColors(bundleToColorIntervals(bundle.getBundle(appStateConstants.TEXT_TEXT_COLOR)));
        setFonts(bundleToFontIntervals(bundle.getBundle(appStateConstants.TEXT_TEXT_FONT)));
        setBolds(bundleToStyleIntervals(bundle.getBundle(appStateConstants.TEXT_BOLD)));
        setItalics(bundleToStyleIntervals(bundle.getBundle(appStateConstants.TEXT_ITALIC)));
        setUnders(bundleToStyleIntervals(bundle.getBundle(appStateConstants.TEXT_UNDER)));
        rotateText(Float.valueOf(bundle.getString(appStateConstants.TEXT_ROTATION)).floatValue());
        setLineSpacing(bundle.getInt(appStateConstants.TEXT_LINE_SPACING));
        setSpaceWords(bundle.getBoolean(appStateConstants.TEXT_LETTER_SPACE_WORDS));
        setTextAlign(bundle.getInt(appStateConstants.TEXT_ALIGNMENT));
        setJustify(bundle.getBoolean(appStateConstants.TEXT_JUSTIFY));
        if (!isStyle) {
            setText(bundle.getString(appStateConstants.TEXT_TEXT_STRING), false);
        } else {
            setText(returnText(), false, bundle.getString(appStateConstants.TEXT_TEXT_STRING).length());
        }
        String strGradientBG = bundle.getString(appStateConstants.TEXT_BG_GRADIENT_V2);
        if (strGradientBG == null) {
//            importedGradientBG = new GradientMaker.GradientFill(bundle.getInt(appStateConstants.TEXT_BG_GRADIENT_TYPE), bundle.getInt(appStateConstants.TEXT_BG_GRADIENT_START_COLOR), bundle.getInt(appStateConstants.TEXT_BG_GRADIENT_END_COLOR));
        } else {
//            importedGradientBG = new GradientMaker.GradientFill(strGradientBG);
        }
//        updateBackground(bundle.getBoolean(appStateConstants.TEXT_BG_ENABLED), bundle.getInt(appStateConstants.TEXT_BG_FILL_TYPE), bundle.getInt(appStateConstants.TEXT_BG_COLOR), importedGradientBG, ((float) bundle.getInt(appStateConstants.TEXT_BG_PADDING_LEFT)) * scaleX, ((float) bundle.getInt(appStateConstants.TEXT_BG_PADDING_RIGHT)) * scaleY, ((float) bundle.getInt(appStateConstants.TEXT_BG_PADDING_TOP)) * scaleY, ((float) bundle.getInt(appStateConstants.TEXT_BG_PADDING_BOTTOM)) * scaleY, bundle.getInt(appStateConstants.TEXT_BG_BORDER_RADIUS));
        String strGradient2 = bundle.getString(appStateConstants.TEXT_STROKE_GRADIENT_V2);
        if (strGradient2 == null) {
//            importedGradient = new GradientMaker.GradientFill(bundle.getInt(appStateConstants.TEXT_STROKE_GRADIENT_TYPE, getStrokeGradient().getType()), bundle.getInt(appStateConstants.TEXT_STROKE_GRADIENT_START, getStrokeGradient().getStartColor()), bundle.getInt(appStateConstants.TEXT_STROKE_GRADIENT_END, getStrokeGradient().getEndColor()));
        } else {
//            importedGradient = new GradientMaker.GradientFill(strGradient2);
        }
        setShadow3d(bundle.getBoolean(appStateConstants.TEXT_THREE_D_SHADOW_ENABLED, isShadow_3d_enabled()), bundle.getInt(appStateConstants.TEXT_THREE_D_SHADOW_COLOR, getShadow_3d_color()), bundle.getInt(appStateConstants.TEXT_THREE_D_SHADOW_TRANSPARENCY, getShadow_3d_transparency()), bundle.getInt(appStateConstants.TEXT_THREE_D_SHADOW_BLUR, getShadow_3d_radius()), bundle.getInt(appStateConstants.TEXT_THREE_D_SHADOW_EXPAND, getShadow_3d_expand()));
//        setStroke(bundle.getBoolean(appStateConstants.TEXT_STROKE_ENABLED, false), Float.valueOf(bundle.getString(appStateConstants.TEXT_STROKE_WIDTH)).floatValue(), bundle.getInt(appStateConstants.TEXT_STROKE_FILL_TYPE, getStrokeFillType()), bundle.getInt(appStateConstants.TEXT_STROKE_COLOR, getStrokeColor()), importedGradient);
        setInnerShadow(bundle.getBoolean(appStateConstants.TEXT_INNER_ENABLED), Float.valueOf(bundle.getString(appStateConstants.TEXT_INNER_RADIUS)).floatValue(), Float.valueOf(bundle.getString(appStateConstants.TEXT_INNER_DX)).floatValue(), Float.valueOf(bundle.getString(appStateConstants.TEXT_INNER_DY)).floatValue(), bundle.getInt(appStateConstants.TEXT_INNER_COLOR));
        setOuterShadow(bundle.getBoolean(appStateConstants.TEXT_OUTER_ENABLED), bundle.getBoolean(appStateConstants.TEXT_OUTER_GLOW_ENABLED), Float.valueOf(bundle.getString(appStateConstants.TEXT_OUTER_RADIUS)).floatValue(), bundle.getInt(appStateConstants.TEXT_OUTER_DX), bundle.getInt(appStateConstants.TEXT_OUTER_DY), bundle.getInt(appStateConstants.TEXT_OUTER_COLOR));
        rotate3d(bundle.getInt(appStateConstants.TEXT_ROTATION_X), bundle.getInt(appStateConstants.TEXT_ROTATION_Y));
        String strGradientTxt = bundle.getString(appStateConstants.TEXT_TEXT_GRADIENT_V2);
        if (strGradientTxt == null) {
//            importedGradientTxt = new GradientMaker.GradientFill(bundle.getInt(appStateConstants.TEXT_TEXT_GRADIENT_TYPE), bundle.getInt(appStateConstants.TEXT_TEXT_GRADIENT_START_COLOR), bundle.getInt(appStateConstants.TEXT_TEXT_GRADIENT_END_COLOR));
        } else {
//            importedGradientTxt = new GradientMaker.GradientFill(strGradientTxt);
        }
//        setGradientFill(importedGradientTxt);
        this.fill_type = bundle.getInt(appStateConstants.TEXT_TEXT_FILL_TYPE);
        setCurve(bundle.getInt(appStateConstants.TEXT_TEXT_CURVE));
        setReflection(bundle.getBoolean(appStateConstants.TEXT_REFLECTION_ENABLED), bundle.getInt(appStateConstants.TEXT_REFLECTION_OFFSET));
        this.bezierMaskEnabled = bundle.getBoolean(appStateConstants.TEXT_BEZIER_MASK_ENABLED);
        setBezierMaskIn(bundle.getBoolean(appStateConstants.TEXT_BEZIER_MASK_IN, true));
        setBezierMaskBundle(bundle.getBundle(appStateConstants.TEXT_BEZIER_MASK_DATA));
        this.bezierMaskPath = null;
        setLetterSpacing(bundle.getInt(appStateConstants.TEXT_LETTER_SPACING));
        setInPadding((int) (((float) bundle.getInt(appStateConstants.TEXT_PADDING_LEFT, 0)) * scaleX), (int) (((float) bundle.getInt(appStateConstants.TEXT_PADDING_RIGHT, 0)) * scaleX));
        UnFreezeFullRedraw();
        this.textDraw.requestLayout();
    }

    /* access modifiers changed from: package-private */
    public void unloadBitmaps() {
        this.textureBmb = null;
        this.bitmap_cache = null;
        this.bitmap_shadow_outer = null;
        this.bitmap_shadow_3d = null;
        this.bitmap_shadow_outer_reflection = null;
        this.bitmap_cache_reflection = null;
        this.bitmap_3d_cache = null;
        this.need_redraw = true;
    }

    /* access modifiers changed from: package-private */
    public void msg(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
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

    private void invalidateParent() {
        if (Build.VERSION.SDK_INT < 19 && getParent() != null) {
            ((textContainer) getParent()).invalidate();
        }
    }

    public void invalidate() {
        if (!this.frozen) {
            super.invalidate();
            invalidateParent();
        }
    }

    public void invalidate(Rect rect) {
        if (!this.frozen) {
            super.invalidate(rect);
            invalidateParent();
        }
    }

    public void invalidate(int l, int t, int r, int b) {
        if (!this.frozen) {
            super.invalidate(l, t, r, b);
            invalidateParent();
        }
    }

    public static Bitmap renderPreview(Bundle bundle, Context context, int height) {
        TextComponent text = new TextComponent(context, 0, "0");
        text.applyBundle(bundle, false, true);
        text.setTextSize((float) height);
        text.setText("SAMPLE", false);
        text.measure(0, 0);
        text.setRenderMode(true, 1.0f);
        int height2 = text.getMeasuredHeight();
        int width = text.getMeasuredWidth();
        text.layout(0, 0, width, height2);
        Bitmap bmOut = Bitmap.createBitmap(width * 2, height2 * 2, Bitmap.Config.ARGB_8888);
        Canvas offScreen = new Canvas(bmOut);
        offScreen.translate(((float) width) / 2.0f, ((float) height2) / 2.0f);
        text.draw(offScreen);
        Rect boundingBox = DrawingPanelRenderer.getAutoCropBound(bmOut);
        if (boundingBox.right == -1 || boundingBox.left == -1 || boundingBox.top == -1 || boundingBox.bottom == -1) {
            return null;
        }
        return DrawingPanelRenderer.autoCropBitmap(bmOut, boundingBox);
    }

    public PointF getViewCenter() {
        return viewCenter;
    }

    public void setViewCenter(PointF viewCenter) {
        this.viewCenter = viewCenter;
    }


    private Rect leftTopRect = new Rect();
    private Rect rightBottomRect = new Rect();

    private RectF leftTopDstRect = new RectF();
    private RectF rightBottomDstRect = new RectF();
    private RectF mHelpBoxRect = new RectF();

    private float mMeasureWidth;
    private float mMeasureHeight;

    private Drawable leftTopBitmap;
    private Drawable rightBottomBitmap;

    public float mScale = 1;
    /**
     * 旋转角度
     */
    public float mRotateAngle = 0;

    private float moveX;
    private float moveY;
    private float lastX = 0;
    private float lastY = 0;

    private StickerItemOnitemclick callback;
    private StickerItemOnDragListener dragCallback;


    /**
     * 左上角动作
     */
    public static final int LEFT_TOP_MODE = 6;
    /**
     * 右下角动作
     */
    public static final int RIGHT_BOTTOM_MODE = 9;

    public static final int STICKER_BTN_HALF_SIZE = 30;

    /**
     * 框架上按钮初始化
     */
    private void initFrameBitmap() {
        if (leftTopBitmap != null) {
            leftTopRect.set(0, 0, leftTopBitmap.getIntrinsicWidth(),
                    leftTopBitmap.getIntrinsicHeight());
            //相当于STICKER_BTN_HALF_SIZE*2 左移运算符
            leftTopDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1,
                    STICKER_BTN_HALF_SIZE << 1);
        }

        if (rightBottomBitmap != null) {
            rightBottomRect.set(0, 0, rightBottomBitmap.getIntrinsicWidth(),
                    rightBottomBitmap.getIntrinsicHeight());
            rightBottomDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1,
                    STICKER_BTN_HALF_SIZE << 1);
        }
    }

    public StickerItemOnitemclick getCallback() {
        return callback;
    }

    public void setCallback(StickerItemOnitemclick callback) {
        this.callback = callback;
    }

    /**
     * 旋转 缩放 更新
     *
     * @param dx X坐标距离
     * @param dy Y坐标距离
     */
    public void updateRotateAndScale(final float dx, final float dy) {
        float cx = mHelpBoxRect.centerX();
        float cy = mHelpBoxRect.centerY();

        float x = rightBottomDstRect.centerX();
        float y = rightBottomDstRect.centerY();

        float nx = x + dx;
        float ny = y + dy;

        float xa = x - cx;
        float ya = y - cy;

        float xb = nx - cx;
        float yb = ny - cy;

        float srcLen = (float) Math.sqrt(xa * xa + ya * ya);
        float curLen = (float) Math.sqrt(xb * xb + yb * yb);
        // 计算缩放比
        float scale = curLen / srcLen;

        mScale *= scale;

        float newWidth = mHelpBoxRect.width() * mScale;

        if (newWidth < 70) {
            mScale /= scale;
            return;
        }

        double cos = (xa * xb + ya * yb) / (srcLen * curLen);
        if (cos > 1 || cos < -1) {
            return;
        }
        float angle = (float) Math.toDegrees(Math.acos(cos));
        // 行列式计算 确定转动方向
        float calMatrix = xa * yb - xb * ya;

        int flag = calMatrix > 0 ? 1 : -1;
        angle = flag * angle;

        //+= angle;
        mRotateAngle = adjustDegree(mRotateAngle, angle);

        moveX = mHelpBoxRect.right;
        moveY = mHelpBoxRect.bottom;

        Log.d(TAG, "updateRotateAndScale() called with: mRotateAngle = [" + mRotateAngle + "]");
        Log.d(TAG, "updateRotateAndScale() called with: angle = [" + angle + "]");
//        rotateText(mRotateAngle);
    }

    /**
     * 辅助居中
     *
     * @param currentDegree 当前角度
     * @param newDegree     新角度
     * @return degree
     */
    private float adjustDegree(float currentDegree, float newDegree) {
        return currentDegree + newDegree;
    }
}

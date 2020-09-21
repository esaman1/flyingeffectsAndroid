package com.flyingeffects.com.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;

import com.bigkoo.convenientbanner.utils.ScreenUtil;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.utils.AbScreenUtils;
import com.flyingeffects.com.utils.BitmapUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.PxUtils;
import com.flyingeffects.com.view.lansongCommendView.BaseStickerView;

/**
 * 可以添加文字的stickerView 与常规的stickerView分开创建
 *
 * @author vidya
 */
public class TextStickerView extends BaseStickerView {
    private static final String TAG = "TextStickerView";
    /**
     * 高光
     */
    private static final int[] COLORS = {Color.parseColor("#00000000"), Color.parseColor("#ffffff"),
            Color.parseColor("#00000000"), Color.parseColor("#EEEEEE"), Color.parseColor("#ffffff"),
            Color.parseColor("#00000000"), Color.parseColor("#ffffff")};

    /**
     * 文字paint
     */
    private Paint mTextPaint;
    private Paint mPaintShadow;
    private Paint mPaintShadow3;
    private float mTextSize;
    private int mTextColor;
    private float paintWidth = 50;
    private float paint3Width = 40;

    /**
     * 触摸点位置
     */
    private float mTouchX;
    private float mTouchY;

    /**
     * 移动的距离
     */
    private float mMoveX;
    private float mMoveY;

    /**
     * 与输入法的连接
     */
    private TextInputConnection mTextInputConnection;

    private int measureWidth = ScreenUtil.getScreenWidth(BaseApplication.getInstance());
    private int defaultHeight = PxUtils.dp2px(BaseApplication.getInstance(), 380);

    public TextStickerView(Context context) {
        this(context, null);
    }

    public TextStickerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextStickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public TextStickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initStickerView(context, attrs);
        initTextPainter(context);
        initFrameView(context);
        //只有下面两个方法设置为true才能获取到输入的内容
        setFocusable(true);
        setFocusableInTouchMode(true);
        mTextInputConnection = new TextInputConnection(this, true, this::postInvalidate);
    }

    /**
     * 初始化边框的样式
     *
     * @param context context
     */
    private void initFrameView(Context context) {

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

        if (rightBitmap != null) {
            rightRect.set(0, 0, rightBitmap.getIntrinsicWidth(),
                    rightBitmap.getIntrinsicHeight());
            rightDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1,
                    STICKER_BTN_HALF_SIZE << 1);
        }

        if (rightCenterBitmap != null) {
            rightCenterRect.set(0, 0, rightCenterBitmap.getIntrinsicWidth(),
                    rightCenterBitmap.getIntrinsicHeight());
            rightCenterDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1,
                    STICKER_BTN_HALF_SIZE << 1);
        }

        if (leftBottomBitmap != null) {
            leftBottomRect.set(0, 0, leftBottomBitmap.getIntrinsicWidth(), leftBottomBitmap.getIntrinsicHeight());
            leftBottomDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1,
                    STICKER_BTN_HALF_SIZE << 1);
        }

        if (rightTopBitmap != null) {
            rightTopRect.set(0, 0, rightTopBitmap.getIntrinsicWidth(), rightTopBitmap.getIntrinsicHeight());
            rightTopDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1,
                    STICKER_BTN_HALF_SIZE << 1);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            measureWidth = width = widthSize;
        } else {
            width = AbScreenUtils.getAndroidScreenProperty().get(0);
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            defaultHeight = height = heightSize;
        } else {
            height = defaultHeight;
        }

        setMeasuredDimension(width, height);
//        paddingTop = getPaddingTop();
//        paddingLeft = getPaddingLeft();
//        paddingBottom = getPaddingBottom();
//        paddingRight = getPaddingRight();
    }

    /**
     * 文字相关的初始化
     */
    private void initTextPainter(Context context) {
        mTextSize = 380;
        mTextPaint = new Paint();
        mPaintShadow = new Paint();
        mPaintShadow3 = new Paint();
        mTextPaint.setColor(Color.parseColor("#000000"));
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setStrokeWidth(paintWidth);

        mPaintShadow.setColor(Color.parseColor("#000000"));
        mPaintShadow.setTextSize(mTextSize);
        mPaintShadow.setStrokeWidth(paintWidth);

        Bitmap bp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.bg_text_sticker);
        BitmapShader bitmapShader = new BitmapShader(BitmapUtil.GetBitmapForScale(bp, measureWidth / 2,
                defaultHeight / 3), Shader.TileMode.MIRROR, Shader.TileMode.MIRROR);
        mTextPaint.setShader(bitmapShader);
        mPaintShadow.setShader(bitmapShader);
        Typeface typeface = Typeface.createFromAsset(BaseApplication.getInstance().getAssets(), "ktjt.ttf");
        mTextPaint.setTypeface(typeface);
        Typeface typeface3 = Typeface.createFromAsset(BaseApplication.getInstance().getAssets(), "ktjt.ttf");
        Typeface typeface2 = Typeface.createFromAsset(BaseApplication.getInstance().getAssets(), "ktjt.ttf");
        mPaintShadow.setTypeface(typeface2);
        mPaintShadow3.setColor(Color.parseColor("#000000"));
        mPaintShadow3.setTextSize(mTextSize);
        mPaintShadow3.setStrokeWidth(paint3Width);
        RadialGradient radialGradient4 = new RadialGradient(measureWidth / (float) 4,
                defaultHeight / (float) 2, measureWidth / (float) 2, COLORS, null, Shader.TileMode.CLAMP);
        mPaintShadow3.setShader(radialGradient4);
        mPaintShadow3.setAntiAlias(true);
        mPaintShadow3.setTypeface(typeface3);
    }

    /**
     * 自定义view 基础属性初始化
     *
     * @param context context
     * @param attrs   attrs
     */
    private void initStickerView(Context context, AttributeSet attrs) {

    }

    /**
     * 让这个view具备唤起输入法的能力
     *
     * @return true 可以当作editor
     */
    @Override
    public boolean onCheckIsTextEditor() {
        return true;
    }

    /**
     * 创建与输入法的联系
     *
     * @param outAttrs 需要设置的输入法的各种类型
     * @return InputConnection
     */
    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        // outAttrs中最重要的就是:
        outAttrs.imeOptions = EditorInfo.IME_FLAG_NO_EXTRACT_UI;
        outAttrs.inputType = InputType.TYPE_NULL;
        return mTextInputConnection;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float textWidth = mTextPaint.measureText(mTextInputConnection.getNowStr());
        for (int i = 1; i < 15; i++) {
            canvas.drawText(mTextInputConnection.getNowStr(), measureWidth / (float) 2 - (i * 2) - textWidth / (float) 2, defaultHeight / (float) 2 - 10 + i, mPaintShadow);
        }
        canvas.drawText(mTextInputConnection.getNowStr(), measureWidth / (float) 2 - textWidth / (float) 2 - 10, defaultHeight / (float) 2 - 10, mPaintShadow3);
        canvas.drawText(mTextInputConnection.getNowStr(), measureWidth / (float) 2 - textWidth / (float) 2, defaultHeight / (float) 2 - 10, mTextPaint);
    }

    /**
     * 触摸监听
     *
     * @param event 事件
     * @return true 拦截事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mTouchX = event.getX();
            mTouchY = event.getY();
            popUpInputMethod();
        }
        return super.onTouchEvent(event);
    }

    /**
     * 弹出输入法
     */
    private void popUpInputMethod() {
        //InputMethodManager来控制输入法弹起和缩回。
        InputMethodManager m = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        LogUtil.d(TAG, "popUpInputMethod");
    }

}

package com.flyingeffects.com.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
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

import com.flyingeffects.com.R;
import com.flyingeffects.com.utils.LogUtil;

/**
 * 可以添加文字的stickerView 与常规的stickerView分开创建
 *
 * @author vidya
 */
public class TextStickerView extends View {
    private static final String TAG = "TextStickerView";

    // 控件的几种模式
    /**
     * 正常
     */
    public static final int IDLE_MODE = 2;
    /**
     * 移动模式
     */
    public static final int MOVE_MODE = 3;
    /**
     * 左上角动作
     */
    public static final int LEFT_TOP_MODE = 6;

    /**
     * 左下角动作
     */
    public static final int LEFT_BOTTOM_MODE = 7;
    /**
     * 右上角动作
     */
    public static final int RIGHT_TOP_MODE = 8;
    /**
     * 右下角动作
     */
    public static final int RIGHT_BOTTOM_MODE = 9;

    /**
     * 右中间动作
     */
    public static final int RIGHT_CENTER_MODE = 10;

    /**
     * 双指动作
     */
    public static final int NEW_POINTER_DOWN_MODE = 11;

    /**
     * 右侧滑动动作
     */
    public static final int RIGHT_MODE = 12;
    /**
     * 文字paint
     */
    private Paint mTextPaint;
    private float mTextSize;
    private int mTextColor;
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

    /**
     * 按钮位置
     */
    private Drawable leftTopBitmap;
    private Drawable rightTopBitmap;
    private Drawable leftBottomBitmap;
    private Drawable rightBottomBitmap;
    private Drawable rightCenterBitmap;
    private Drawable rightBitmap;
    private RectF leftTopDstRect = new RectF();
    private RectF rightBottomDstRect = new RectF();
    private RectF rightCenterDstRect = new RectF();
    private RectF rightTopDstRect = new RectF();
    private RectF leftBottomDstRect = new RectF();
    private RectF rightDstRect = new RectF();

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
        initStickerView(context,attrs);
        initTextPainter();
        //只有下面两个方法设置为true才能获取到输入的内容
        setFocusable(true);
        setFocusableInTouchMode(true);
        mTextInputConnection = new TextInputConnection(this, true, this::postInvalidate);
    }

    /**
     * 文字相关的初始化
     */
    private void initTextPainter() {
        mTextPaint = new Paint();
        mTextPaint.setTextSize(36);
        mTextPaint.setColor(Color.BLACK);
    }

    /**
     * 自定义view 基础属性初始化
     * @param context
     * @param attrs
     */
    private void initStickerView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextStickerView);
        leftTopBitmap = typedArray.getDrawable(R.styleable.TextStickerView_sv_left_top_drawable);
        leftBottomBitmap = typedArray.getDrawable(R.styleable.TextStickerView_sv_left_bottom_drawable);
        rightBitmap = typedArray.getDrawable(R.styleable.TextStickerView_sv_right_drawable);
        rightTopBitmap = typedArray.getDrawable(R.styleable.TextStickerView_sv_right_top_drawable);
        rightBottomBitmap = typedArray.getDrawable(R.styleable.TextStickerView_sv_right_bottom_drawable);


        typedArray.recycle();
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
        if (!TextUtils.isEmpty(mTextInputConnection.getNowStr())) {
            canvas.drawText(mTextInputConnection.getNowStr(), mTouchX, mTouchY, mTextPaint);
        }
        canvas.drawLine(mTouchX, mTouchY + 18, mTouchX, mTouchY - 18, mTextPaint);
    }

    /**
     * 触摸监听
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

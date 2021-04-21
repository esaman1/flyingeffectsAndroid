package com.flyingeffects.com.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.flyingeffects.com.view.lansongCommendView.PaintConstants;

public class TouchMoveImageView extends androidx.appcompat.widget.AppCompatImageView {
    private static final int NORMAL_MODE = 0;
    private static final int TOUCH_MODE = 1;
    private static final int MOVE_MODE = 2;
    private static final int ZOOM_MODE = 3;

    private int mBitmapWidth;
    private int mBitmapHeight;
    private int mMaxW;
    private int mMaxH;
    private int mMinW;
    private int mMinH;
    private int start_Top = -1;
    private int start_Left;
    private int start_Bottom;
    private int start_Right;
    private boolean isScaleAnim;
    private int mode;


    public TouchMoveImageView(@NonNull Context context) {
        this(context, null);
    }

    public TouchMoveImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TouchMoveImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (start_Top == -1) {
            start_Top = top;
            start_Left = left;
            start_Bottom = bottom;
            start_Right = right;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    /***
     * 设置显示图片
     */
    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        //获取图片宽高
        mBitmapWidth = bm.getWidth();
        mBitmapHeight = bm.getHeight();

        mMaxW = mBitmapWidth * 3;
        mMaxH = mBitmapHeight * 3;

        mMinW = mBitmapWidth / 2;
        mMinH = mBitmapHeight / 2;
    }

    /***
     * touch 事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //处理单点、多点触摸
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                onTouchDown(event);
                break;
            // 多点触摸
            case MotionEvent.ACTION_POINTER_DOWN:
                onPointerDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                onTouchMove(event);
                break;
            case MotionEvent.ACTION_UP:
                mode = NORMAL_MODE;
                break;

            // 多点松开
            case MotionEvent.ACTION_POINTER_UP:
                mode = NORMAL_MODE;
            //执行缩放还原
                if (isScaleAnim) {
                    doScaleAnim();
                }
                break;
            default:
                break;
        }

        return true;
    }

    private void doScaleAnim() {


    }

    private void onTouchMove(MotionEvent event) {

    }

    private void onPointerDown(MotionEvent event) {
            if (event.getPointerCount() == 2) {
                mode = ZOOM_MODE;
               // beforeLenght = getDistance(event);// 获取两点的距离
            }
    }

    private void onTouchDown(MotionEvent event) {


    }
}

package com.flyingeffects.com.view.lansongCommendView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.constans.UiStep;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.utils.LogUtil;

import java.lang.ref.WeakReference;


/**
 * @author vidya
 */
public class BaseStickerView extends View {

    public static final int STICKER_BTN_HALF_SIZE = 30;
    // 控件的几种模式
    /**
     * 正常
     */
    protected static final int IDLE_MODE = 2;
    /**
     * 移动模式
     */
    protected static final int MOVE_MODE = 3;
    /**
     * 左上角动作
     */
    protected static final int LEFT_TOP_MODE = 6;

    /**
     * 左下角动作
     */
    protected static final int LEFT_BOTTOM_MODE = 7;
    /**
     * 右上角动作
     */
    protected static final int RIGHT_TOP_MODE = 8;

    /**
     * 右下角动作
     */
    protected static final int RIGHT_BOTTOM_MODE = 9;

    /**
     * 右中间动作
     */
    protected static final int RIGHT_CENTER_MODE = 10;

    /**
     * 双指动作
     */
    protected static final int NEW_POINTER_DOWN_MODE = 11;

    /**
     * 右侧滑动动作
     */
    protected static final int RIGHT_MODE = 12;
    /**
     * 边框自动消息时长
     */
    private static final long AUTO_FADE_FRAME_TIMEOUT = 5000;
    /**
     * 显示边框事件ID
     */
    private static final int SHOW_FRAME = 1;
    /**
     * 消失边框事件ID
     */
    private static final int DISMISS_FRAME = 2;

    /**
     * 按钮大小
     */
    protected Rect leftTopRect = new Rect();
    protected Rect leftBottomRect = new Rect();
    protected Rect rightBottomRect = new Rect();
    protected Rect rightCenterRect = new Rect();
    protected Rect rightTopRect = new Rect();
    protected Rect rightRect = new Rect();

    /**
     * 按钮位置
     */
    protected Drawable leftTopBitmap;
    protected Drawable rightTopBitmap;
    protected Drawable leftBottomBitmap;
    protected Drawable rightBottomBitmap;
    protected Drawable rightCenterBitmap;
    protected Drawable rightBitmap;
    protected RectF leftTopDstRect = new RectF();
    protected RectF rightBottomDstRect = new RectF();
    protected RectF rightCenterDstRect = new RectF();
    protected RectF rightTopDstRect = new RectF();
    protected RectF leftBottomDstRect = new RectF();
    protected RectF rightDstRect = new RectF();

    /**
     * 是否显示边框
     */
    protected boolean mFrameShow = false;

    /**
     * 当前是否是子动画view
     */
    private boolean isFromAnim = false;

    float tempDegree = 0;
    boolean degreeTurned = false;

    /**
     * 双指
     */
    float dx0 = 0f;
    float dy0 = 0f;
    /**
     * 记录初始位置
     */
    private float lastX = 0;
    private float lastY = 0;
    /**
     * 限制右侧滑动按钮在轨道之内
     */
    private int mRightLimited = 0;
    /**
     * 右侧滑动按钮距离底部的距离百分比
     */
    private float mRightOffsetPercent = 0f;
    /**
     * 右侧滑动按钮距离底部的距离
     */
    private float mRightOffset = 0f;


    private RectF mHelpBoxRect = new RectF();

    private StickerItemOnitemclick callback;
    private StickerItemOnDragListener dragCallback;

    float autoCenterRange = 30;
    float autoCenterMoveX = 0;
    float autoCenterMoveY = 0;

    private float moveX;
    private float moveY;
    private PointF center = new PointF(0, 0);
    public float mRotateAngle = 0;
    public float mScale = 1;
    private Paint debugPaint = new Paint();
    private TextPaint textPaint = new TextPaint();
    private TextPaint whitePaint = new TextPaint();
    private TextPaint shadowPaint = new TextPaint();
    private Paint mHelpPaint = new Paint();
    //图像透明化要用到的遮罩
    private Paint mHelpDstPaint = new Paint();
    private int mCurrentMode = IDLE_MODE;

    private Bitmap mMaskBitmap;
    //是否允许辅助水平
    private boolean enableAutoAdjustDegree = true;
    //是否允许辅助居中
    private boolean enableAutoAdjustCenter = false;

    private GestureHandler mHandler = new GestureHandler(this);


    private static class GestureHandler extends Handler {

        WeakReference<BaseStickerView> mReference;

        public GestureHandler(BaseStickerView view) {
            mReference = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseStickerView view = mReference.get();
            switch (msg.what) {
                case SHOW_FRAME:
                    if (!view.mFrameShow) {
                        view.mFrameShow = true;
                        view.invalidate();
                    }
                    break;
                case DISMISS_FRAME:
                    if (view.mFrameShow) {
                        view.mFrameShow = false;
                        view.invalidate();
                    }
                    break;
                default:
                    break;
            }
        }
    }


    public BaseStickerView(Context context) {
        this(context, null);
    }

    public BaseStickerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseStickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public BaseStickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 是否向下传递事件标志 true为消耗
        super.onTouchEvent(event);
        LogUtil.d("oom", "-----------------------onTouchEvent------------------------------");
        if (!isFromAnim) {
            int pointerCount = event.getPointerCount();
            int action = event.getAction();
            float x = event.getX();
            float y = event.getY();
            LogUtil.d("event", "x =" + x);
            LogUtil.d("event", "y =" + y);
            switch (action & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    LogUtil.d("event", "ACTION_DOWN");
                    if (callback != null) {
                        callback.stickerMove();
                    }
                    mCurrentMode = adjustMode(x, y, pointerCount, false);
                    if (mCurrentMode == IDLE_MODE) {
                        return false;
                    } else if (mCurrentMode == LEFT_TOP_MODE) {
//                        if (this.isRunning) {
//                            stop();
//                        }
                        callback.stickerOnclick(LEFT_TOP_MODE);
                        return true;
                    } else if (mCurrentMode == RIGHT_TOP_MODE) {
                        callback.stickerOnclick(RIGHT_TOP_MODE);
                        return true;
                    } else if (mCurrentMode == LEFT_BOTTOM_MODE) {
                        callback.stickerOnclick(LEFT_BOTTOM_MODE);
                        return true;
                    } else if (mCurrentMode == RIGHT_CENTER_MODE) {
                        callback.stickerOnclick(RIGHT_CENTER_MODE);
                        return true;
                    } else if (mCurrentMode == RIGHT_BOTTOM_MODE) {
                        if (UiStep.isFromDownBj) {
                            statisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), " 5_mb_bj_Spin");
                        } else {
                            statisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), " 6_customize_bj_Spin");
                        }
                    }
                    lastX = x;
                    lastY = y;

                    if (!mFrameShow) {
                        mFrameShow = true;
                        invalidate();
                    }
                    mHandler.removeMessages(DISMISS_FRAME);
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    //float startRotation = getRotation(event);
                    mCurrentMode = adjustMode(x, y, pointerCount, false);
                    dx0 = event.getX(1) - event.getX(0);
                    dy0 = event.getY(1) - event.getY(0);
                    LogUtil.d("event", "ACTION_POINTER_DOWN pointerCount =" + pointerCount);
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    pointerCount = 1;
                    mCurrentMode = adjustMode(x, y, pointerCount, true);
                    LogUtil.d("event", "ACTION_POINTER_UP pointerCount =" + pointerCount);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (dragCallback != null) {
                        dragCallback.stickerDragMove();
                    }
                    if (UiStep.isFromDownBj) {
                        statisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "5_mb_bj_drag");
                    } else {
                        statisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "6_customize_bj_drag");
                    }

                    if (mCurrentMode == IDLE_MODE) {
                        return false;
                    }

                    if (mCurrentMode == MOVE_MODE) {
                        // 移动贴图
                        float dx = x - lastX;
                        float dy = y - lastY;
                        adjustCenter(dx, dy);

                        invalidate();
                        lastX = x;
                        lastY = y;
                        moveX = mHelpBoxRect.right;
                        moveY = mHelpBoxRect.bottom;

                        LogUtil.d("OOM", "moveX" + moveX);
                        LogUtil.d("OOM", "width" + getMeasuredWidth());

                        float xx = mHelpBoxRect.width();
                        float xx2 = xx / 2;
                        LogUtil.d("OOM", "xx2 ==" + xx2);
                        float aaaa = moveX - xx2;
                        LogUtil.d("OOM", "aaaa ==" + aaaa);
                        float bbb = aaaa / getMeasuredWidth();
                        LogUtil.d("OOM", "P=" + bbb);

                        float xx1 = mHelpBoxRect.height();
                        float xx21 = xx1 / 2;
                        float aaaa1 = moveY - xx21;
                        float bbb1 = aaaa1 / getMeasuredHeight();
                        LogUtil.d("OOM", "P=" + bbb1);
                    } else if (mCurrentMode == RIGHT_BOTTOM_MODE) {
                        // 旋转 缩放文字操作
                        float dx = x - lastX;
                        float dy = y - lastY;
                        updateRotateAndScale(dx, dy);
                        invalidate();
                        lastX = x;
                        lastY = y;
                    } else if (mCurrentMode == RIGHT_MODE) {
                        LogUtil.d("event", "RIGHT_MODE");
                        float dx = x - lastX;
                        float dy = y - lastY;
                        changeRightOffset(dx, dy);
                        invalidate();
                        lastX = x;
                        lastY = y;
                    } else if (mCurrentMode == NEW_POINTER_DOWN_MODE) {
                        LogUtil.d("event", "NEW_POINTER_DOWN_MODE");
                        float dx1 = event.getX(1) - event.getX(0);
                        float dy1 = event.getY(1) - event.getY(0);
                        updateRotateAndScalePointerDown(dx0, dy0, dx1, dy1);
                        invalidate();
                        dx0 = dx1;
                        dy0 = dy1;
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    if (dragCallback != null) {
                        dragCallback.stickerDragUp();
                    }
                    LogUtil.d("event", "ACTION_CANCEL");
                    if (mCurrentMode == IDLE_MODE) {
                        return false;
                    }
                    mHandler.removeMessages(DISMISS_FRAME);
                    mHandler.sendEmptyMessageDelayed(DISMISS_FRAME, AUTO_FADE_FRAME_TIMEOUT);
                    invalidate();

                    mCurrentMode = IDLE_MODE;
                    break;
                default:
                    break;

            }// end switch
            return true;
        }
        return false;
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
    }

    /**
     * 双指 旋转 缩放 更新
     *
     * @param dx0 初始x坐标差
     * @param dy0 初始y坐标差
     * @param dx1 第二次x坐标查
     * @param dy1 第二次y坐标差
     */
    public void updateRotateAndScalePointerDown(final float dx0, final float dy0,
                                                final float dx1, final float dy1) {

        float srcLen = (float) Math.sqrt(dx0 * dx0 + dy0 * dy0);
        float curLen = (float) Math.sqrt(dx1 * dx1 + dy1 * dy1);
        // 计算缩放比
        float scale = curLen / srcLen;

        mScale *= scale;

        float newWidth = mHelpBoxRect.width() * mScale;

        if (newWidth < 70) {
            mScale /= scale;
            return;
        }

        double cos = (dx0 * dx1 + dy0 * dy1) / (srcLen * curLen);
        if (cos > 1 || cos < -1) {
            return;
        }
        float angle = (float) Math.toDegrees(Math.acos(cos));
        // 行列式计算 确定转动方向
        float calMatrix = dx0 * dy1 - dx1 * dy0;

        int flag = calMatrix > 0 ? 1 : -1;
        angle = flag * angle;

        //+= angle;
        mRotateAngle = adjustDegree(mRotateAngle, angle);

        moveX = mHelpBoxRect.right;
        moveY = mHelpBoxRect.bottom;
    }


    public void setRightBottomBitmap(Drawable rightBottomBitmap) {
        if (rightBottomBitmap != null) {
            this.rightBottomBitmap = rightBottomBitmap;
            rightBottomRect.set(0, 0, rightBottomBitmap.getIntrinsicWidth(),
                    rightBottomBitmap.getIntrinsicHeight());
            rightBottomDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1,
                    STICKER_BTN_HALF_SIZE << 1);
        }
    }

    public void setRightBitmap(Drawable rightBitmap) {
        if (rightBitmap != null) {
            this.rightBitmap = rightBitmap;
            rightRect.set(0, 0, rightBitmap.getIntrinsicWidth(),
                    rightBitmap.getIntrinsicHeight());
            rightDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1,
                    STICKER_BTN_HALF_SIZE << 1);
        }
    }

    public void setRightCenterBitmap(Drawable rightCenterBitmap) {
        if (rightCenterBitmap != null) {
            this.rightCenterBitmap = rightCenterBitmap;
            rightCenterRect.set(0, 0, rightCenterBitmap.getIntrinsicWidth(),
                    rightCenterBitmap.getIntrinsicHeight());
            rightCenterDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1,
                    STICKER_BTN_HALF_SIZE << 1);
        }
    }

    public void setRightCenterBitmapForChangeIcon(Drawable rightCenterBitmap) {
        if (rightCenterBitmap != null) {
            this.rightCenterBitmap = rightCenterBitmap;
        }
    }


    public void setLeftTopBitmap(Drawable leftTopBitmap) {
        if (leftTopBitmap != null) {
            this.leftTopBitmap = leftTopBitmap;
            leftTopDstRect.set(0, 0, leftTopBitmap.getIntrinsicWidth(),
                    leftTopBitmap.getIntrinsicHeight());
            leftTopDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1,
                    STICKER_BTN_HALF_SIZE << 1);
        }
    }

    public void setLeftBottomBitmap(Drawable leftBottomBitmap) {
        if (leftBottomBitmap != null) {
            this.leftBottomBitmap = leftBottomBitmap;
            leftBottomDstRect.set(0, 0, leftBottomBitmap.getIntrinsicWidth(),
                    leftBottomBitmap.getIntrinsicHeight());
            leftBottomDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1,
                    STICKER_BTN_HALF_SIZE << 1);
        }
    }

    public void setRightTopBitmap(Drawable rightTopBitmap) {
        if (rightTopBitmap != null) {
            this.rightTopBitmap = rightTopBitmap;
            rightTopDstRect.set(0, 0, rightTopBitmap.getIntrinsicWidth(),
                    rightTopBitmap.getIntrinsicHeight());
            rightTopDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1,
                    STICKER_BTN_HALF_SIZE << 1);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawFrame(canvas);
    }

    /**
     * 绘制框架
     *
     * @param canvas 画布
     */
    private void drawFrame(Canvas canvas) {
        //显示编辑框
        if (mFrameShow && !isFromAnim) {
            // draw x and rotate button
            int offsetValue = 0;
            if (leftTopBitmap != null) {
                offsetValue = ((int) leftTopDstRect.width()) >> 1;
            } else if (rightTopBitmap != null) {
                offsetValue = ((int) rightTopDstRect.width()) >> 1;
            } else if (leftBottomBitmap != null) {
                offsetValue = ((int) leftBottomDstRect.width()) >> 1;
            } else if (rightBottomBitmap != null) {
                offsetValue = ((int) rightBottomDstRect.width()) >> 1;
            } else if (rightCenterBitmap != null) {
                offsetValue = ((int) rightCenterDstRect.width()) >> 1;
            } else if (rightBitmap != null) {
                offsetValue = ((int) rightDstRect.width()) >> 1;
            }

            leftTopDstRect.offsetTo(mHelpBoxRect.left - offsetValue,
                    mHelpBoxRect.top - offsetValue);

            rightBottomDstRect.offsetTo(mHelpBoxRect.right - offsetValue,
                    mHelpBoxRect.bottom - offsetValue);
            mRightLimited = offsetValue * 4;

            float rightOffset = mRightOffsetPercent * (mHelpBoxRect.bottom - mHelpBoxRect.top - offsetValue * 4);
            rightDstRect.offsetTo(mHelpBoxRect.right - offsetValue,
                    mHelpBoxRect.top + offsetValue + rightOffset);
            leftBottomDstRect.offsetTo(mHelpBoxRect.left - offsetValue,
                    mHelpBoxRect.bottom - offsetValue);
            rightTopDstRect.offsetTo(mHelpBoxRect.right - offsetValue,
                    mHelpBoxRect.top - offsetValue);
            float center = (mHelpBoxRect.bottom - mHelpBoxRect.top) / (float) 2;

            // 音量按键位置
            rightCenterDstRect.offsetTo(mHelpBoxRect.left - offsetValue,
                    mHelpBoxRect.top + center - offsetValue);

            RectUtil.rotateRect(leftTopDstRect, mHelpBoxRect.centerX(),
                    mHelpBoxRect.centerY(), mRotateAngle);
            RectUtil.rotateRect(rightBottomDstRect, mHelpBoxRect.centerX(),
                    mHelpBoxRect.centerY(), mRotateAngle);
            RectUtil.rotateRect(rightDstRect, mHelpBoxRect.centerX(),
                    mHelpBoxRect.centerY(), mRotateAngle);
            RectUtil.rotateRect(leftBottomDstRect, mHelpBoxRect.centerX(),
                    mHelpBoxRect.centerY(), mRotateAngle);
            RectUtil.rotateRect(rightTopDstRect, mHelpBoxRect.centerX(),
                    mHelpBoxRect.centerY(), mRotateAngle);
            RectUtil.rotateRect(rightCenterDstRect, mHelpBoxRect.centerX(),
                    mHelpBoxRect.centerY(), mRotateAngle);


            canvas.save();
            canvas.rotate(mRotateAngle, mHelpBoxRect.centerX(),
                    mHelpBoxRect.centerY());
            canvas.drawRoundRect(mHelpBoxRect, 10, 10, mHelpPaint);
            canvas.restore();

            if (leftTopBitmap != null) {
                leftTopBitmap.setBounds((int) leftTopDstRect.left, (int) leftTopDstRect.top, (int) leftTopDstRect.right, (int) leftTopDstRect.bottom);
                leftTopBitmap.draw(canvas);
            }

            if (rightBitmap != null) {
                rightBitmap.setBounds((int) rightDstRect.left, (int) rightDstRect.top, (int) rightDstRect.right, (int) rightDstRect.bottom);
                rightBitmap.draw(canvas);
            }

            if (rightBottomBitmap != null) {
                rightBottomBitmap.setBounds((int) rightBottomDstRect.left, (int) rightBottomDstRect.top, (int) rightBottomDstRect.right, (int) rightBottomDstRect.bottom);
                rightBottomBitmap.draw(canvas);
            }

            if (leftBottomBitmap != null) {
                leftBottomBitmap.setBounds((int) leftBottomDstRect.left, (int) leftBottomDstRect.top, (int) leftBottomDstRect.right, (int) leftBottomDstRect.bottom);
                leftBottomBitmap.draw(canvas);
            }

            if (rightTopBitmap != null) {
                rightTopBitmap.setBounds((int) rightTopDstRect.left, (int) rightTopDstRect.top, (int) rightTopDstRect.right, (int) rightTopDstRect.bottom);
                rightTopBitmap.draw(canvas);
            }

            if (rightCenterBitmap != null) {
                rightCenterBitmap.setBounds((int) rightCenterDstRect.left, (int) rightCenterDstRect.top, (int) rightCenterDstRect.right, (int) rightCenterDstRect.bottom);
                rightCenterBitmap.draw(canvas);
            }
        }
    }

    /**
     * 调整当前的模式
     *
     * @param x
     * @param y
     * @param pointerCount
     * @param pointerUp
     * @return
     */
    protected int adjustMode(float x, float y, int pointerCount, boolean pointerUp) {
        if (leftTopDstRect != null && leftTopDstRect.contains(x, y)) {
            return LEFT_TOP_MODE;
        } else if (leftBottomDstRect != null && leftBottomDstRect.contains(x, y)) {
            return LEFT_BOTTOM_MODE;
        } else if (rightTopDstRect != null && rightTopDstRect.contains(x, y)) {
            return RIGHT_TOP_MODE;
        } else if (rightBottomDstRect != null && rightBottomDstRect.contains(x, y)) {
            return RIGHT_BOTTOM_MODE;
        } else if (rightCenterDstRect != null && rightCenterDstRect.contains(x, y)) {
            return RIGHT_CENTER_MODE;
        } else if (rightDstRect != null && rightDstRect.contains(x, y)) {
            return RIGHT_MODE;
        } else if (isIn(mHelpBoxRect, x, y, mRotateAngle) && (pointerCount == 1) && !pointerUp) {
            return MOVE_MODE;
        } else if (pointerCount > 1) {
            return NEW_POINTER_DOWN_MODE;
        } else {
            return IDLE_MODE;
        }
    }


    public void showFrame() {
        LogUtil.d("oom", "-----------------------showFrame------------------------------");
        mHandler.sendEmptyMessage(SHOW_FRAME);
        mHandler.sendEmptyMessageDelayed(DISMISS_FRAME, AUTO_FADE_FRAME_TIMEOUT);
    }

    /**
     *
     */
    public void dismissFrame() {
        if (mFrameShow) {
            mFrameShow = false;
            invalidate();
            LogUtil.d("oom", "-----------------------dismissFrame------------------------------");
        }
    }

    public boolean isIn(RectF source, float x, float y, float angle) {
        RectF rectF = new RectF(source);
        if (angle != 0) {
            Matrix matrix = new Matrix();
            //设置旋转角度时，一定要记得设置旋转的中心，该中心与绘图时的中心点是一致的。
            matrix.setRotate(angle, rectF.centerX(), rectF.centerY());
            matrix.mapRect(rectF);
        }
        return rectF.contains(x, y);
    }

    /**
     * 辅助层中
     *
     * @param dx
     * @param dy
     */
    private void adjustCenter(float dx, float dy) {
        if (!enableAutoAdjustCenter) {
            center.offset(dx, dy);
        } else {
            //计算x軕
            if (isInCenterX(center)) {
                if (Math.abs(autoCenterMoveX) >= autoCenterRange) {
                    center.offset(autoCenterMoveX, 0);
                    Log.e("savion", "当前移动X距离已够:" + autoCenterMoveX + ",dx:" + dx);
                    autoCenterMoveX = 0;
                } else {
                    autoCenterMoveX += dx;
                }
            } else {
                boolean xWillInCenter = Math.abs((center.x + dx) - getWidth() / 2f) < autoCenterRange;
                if (xWillInCenter) {
                    autoCenterMoveX = 0;
                    center.set(getWidth() / 2f, center.y);
                } else {
                    center.offset(dx, 0);
                }
            }
            //计算Y軕
            if (isInCenterY(center)) {
                if (Math.abs(autoCenterMoveY) >= autoCenterRange) {
                    center.offset(0, autoCenterMoveY);
                    Log.e("savion", "当前移动Y距离已够:" + autoCenterMoveY + ",dy:" + dy);
                    autoCenterMoveY = 0;
                } else {
                    Log.e("savion", "当前移动Y距离不够:" + autoCenterMoveY + ",dy:" + dy);
                    autoCenterMoveY += dy;
                }
            } else {
                boolean yWillInCenter = Math.abs((center.y + dy) - getHeight() / 2f) < autoCenterRange;
                if (yWillInCenter) {
                    autoCenterMoveY = 0;
                    center.set(center.x, getHeight() / 2f);
                } else {
                    center.offset(0, dy);
                }
            }
        }
    }

    /**
     * 辅助居中
     *
     * @param currentDegree
     * @param newDegree
     * @return
     */
    private float adjustDegree(float currentDegree, float newDegree) {
        if (!enableAutoAdjustDegree) {
            return currentDegree + newDegree;
        }
        tempDegree += newDegree;
        int current = Math.round(currentDegree % 360);
        if (current < 0) {
            current = 360 + current;
        }
        if (!degreeTurned) {
            if (newDegree > 0) {
                if (current > 85 && current < 90) {
                    current = 90;
                    degreeTurned = true;
                    tempDegree = 0f;
                }
                if (current > 355 && current < 360) {
                    current = 0;
                    degreeTurned = true;
                    tempDegree = 0f;
                }
            } else {
                if (current < 95 && current > 90) {
                    current = 90;
                    degreeTurned = true;
                    tempDegree = 0f;
                }
                if (current > 0 && current < 5) {
                    current = 0;
                    degreeTurned = true;
                    tempDegree = 0f;
                }
            }
        }
        if (degreeTurned) {
            mHelpPaint.setColor(Color.RED);
            if (Math.abs(tempDegree) <= 10f) {
                return current;
            } else {
                tempDegree = 0;
                degreeTurned = false;
                return current + tempDegree;
            }

        } else {
            mHelpPaint.setColor(Color.WHITE);
        }
        return current + newDegree;
    }

    private boolean isInCenterX(PointF point) {
        if (point != null) {
            return Math.abs(point.x - getWidth() / 2f) < autoCenterRange;
        }
        return false;
    }

    private boolean isInCenterY(PointF point) {
        if (point != null) {
            return Math.abs(point.y - getHeight() / 2f) < autoCenterRange;
        }
        return false;
    }

    //右侧滑动
    private void changeRightOffset(float dx, float dy) {
        float sqrt = (float) Math.sqrt(dx * dx + dy * dy);

        if (mRotateAngle >= 90 && mRotateAngle <= 270 && dy > 0) {
            mRightOffset -= sqrt;
        } else if (mRotateAngle >= 90 && mRotateAngle <= 270 && dy < 0) {
            mRightOffset += sqrt;
        } else if (dy < 0) {
            mRightOffset -= sqrt;
        } else if (dy > 0) {
            mRightOffset += sqrt;
        }
        LogUtil.d("change", mRotateAngle + " mRotateAngle");
        LogUtil.d("change", mRightOffsetPercent + " mRightOffset1");
        if (mRightOffset < 0) {
            mRightOffset = 0;
        } else if (mRightOffset >= mHelpBoxRect.bottom - mHelpBoxRect.top - mRightLimited) {
            mRightOffset = mHelpBoxRect.bottom - mHelpBoxRect.top - mRightLimited;
        }
        mRightOffsetPercent = mRightOffset / (mHelpBoxRect.bottom - mHelpBoxRect.top - mRightLimited);
        LogUtil.d("change", mRightOffsetPercent + " mRightOffset2");
        LogUtil.d("change", mHelpBoxRect.top + " top");
        LogUtil.d("change", mHelpBoxRect.bottom + " bottom");
    }

}

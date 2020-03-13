package com.flyingeffects.com.view;

import android.app.Service;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.ui.interfaces.TickerAnimated;
import com.flyingeffects.com.utils.screenUtil;
import com.flyingeffects.com.view.lansongCommendView.RectUtil;

import java.util.List;
//com.flyingeffects.com.view.StickerView


/**
 * 文本贴图处理控件
 *
 * @author savion  区别于蓝松的StickerView
 * @date 2019/12/19
 */
public class StickerView<D extends Drawable> extends View implements TickerAnimated {
    public static final int STICKER_BTN_HALF_SIZE = 30;
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

    public int layoutX = 0;
    public int layoutY = 0;
    private PointF center = new PointF(0, 0);
    public float mRotateAngle = 0;
    public float mScale = 1;
    private Paint debugPaint = new Paint();
    private TextPaint textPaint = new TextPaint();
    private TextPaint shadowPaint = new TextPaint();
    private Paint mHelpPaint = new Paint();
    private RectF mHelpBoxRect = new RectF();
    /**
     * 删除按钮位置
     */
    private Rect leftTopRect = new Rect();
    private Rect leftBottomRect = new Rect();
    private Rect rightBottomRect = new Rect();
    /**
     * 旋转按钮位置
     */
    private Rect rightTopRect = new Rect();
    private RectF leftTopDstRect = new RectF();
    private RectF rightBottomDstRect = new RectF();
    private RectF rightTopDstRect = new RectF();
    private RectF leftBottomDstRect = new RectF();
    private Drawable leftTopBitmap;
    private Drawable rightTopBitmap;
    private Drawable leftBottomBitmap;
    private Drawable rightBottomBitmap;
    private int frameColor = Color.WHITE;
    private float frameWidth = 1;
    private int rotateLocation = RIGHT_BOTTOM_MODE;
    private int mCurrentMode = IDLE_MODE;
    //是否允许辅助水平
    private boolean enableAutoAdjustDegree = true;
    //是否允许辅助居中
    private boolean enableAutoAdjustCenter = true;
    private float lastX = 0;
    private float lastY = 0;
    //辅助线颜色
    private int guideLineColor = Color.WHITE;
    //辅助线宽度
    private float guideLineWidth = 5;
    //是否显示辅助线
    private boolean guideLineShow = true;
    //当touch时显示辅助线
    private boolean guideLineShowOntouch = false;
    private Vibrator vibrator;
    //是否显示
    private boolean frameShow = false;


    /**
     * 边框自动消息时长
     */
    private static final long AUTO_FADE_FRAME_TIMEOUT = 2000;
    /**
     * 显示边框事件ID
     */
    private static final int SHOW_FRAME = 1;
    /**
     * 消失边框事件ID
     */
    private static final int DISMISS_FRAME = 2;

    private StickerListener tickerListener;

    public StickerListener getTickerListener() {
        return tickerListener;
    }

    public void setTickerListener(StickerListener tickerListener) {
        this.tickerListener = tickerListener;
    }

    public interface StickerListener {

        void onActionEnd(StickerView stickerView, int mode, float scale, float degree, PointF center);

        void onActionStart(StickerView stickerView, int mode, float scale, float degree, PointF center);
    }

    @Override
    public void pause() {
        //暂停
        if (currentDrawable != null && currentDrawable instanceof GifDrawable) {
            ((GifDrawable) currentDrawable).stop();
        }
    }

    @Override
    public void start() {
        if (currentDrawable != null && currentDrawable instanceof GifDrawable) {
            if (!isRunning()) {
                currentDrawable.setCallback(this);
                ((GifDrawable) currentDrawable).startFromFirstFrame();
            }
            isRunning = true;
        }
    }

    @Override
    public void stop() {
        if (currentDrawable != null && currentDrawable instanceof GifDrawable) {
            pause();
            currentDrawable.setCallback(null);
            ((GifDrawable) currentDrawable).recycle();
            currentDrawable = null;
            isRunning = false;
        } else {
            currentDrawable = null;
            invalidate();
            isRunning = false;
        }
        targer = null;
        Runtime.getRuntime().gc();
    }

    private boolean isRunning = false;

    @Override
    public boolean isRunning() {
        if (currentDrawable != null && currentDrawable instanceof GifDrawable) {
            return isRunning = ((GifDrawable) currentDrawable).isRunning();
        }
        return true;
    }

    /**
     * 设置辅助线颜色
     *
     * @param guideLineColor
     */
    public void setGuideLineColor(int guideLineColor) {
        if (this.guideLineColor != guideLineColor) {
            this.guideLineColor = guideLineColor;
            if (isGuideLineShow()) {
                invalidate();
            }
        }
    }

    public float getGuideLineWidth() {
        return guideLineWidth;
    }

    public int getGuideLineColor() {
        return guideLineColor;
    }

    /**
     * 设置辅助线宽度
     *
     * @param guideLineWidth
     */
    public void setGuideLineWidth(float guideLineWidth) {
        if (this.guideLineWidth != guideLineWidth) {
            this.guideLineWidth = guideLineWidth;
            if (isGuideLineShow()) {
                invalidate();
            }
        }
    }

    /**
     * 当前是否显示辅助线
     *
     * @return
     */
    public boolean isGuideLineShow() {
        return guideLineShow;
    }

    /**
     * 设置是否显示辅助线
     *
     * @param guideLineShow
     */
    public void setGuideLineShow(boolean guideLineShow) {
        if (guideLineShow != this.guideLineShow) {
            this.guideLineShow = guideLineShow;
            invalidate();
        }
    }

    public void setFrameColor(int frameColor) {
        this.frameColor = frameColor;
    }

    public float getFrameWidth() {
        return frameWidth;
    }

    public void setFrameWidth(float frameWidth) {
        this.frameWidth = frameWidth;
    }

    public int getFrameColor() {
        return frameColor;
    }

    public void setRotateLocation(int rotateLocation) {
        this.rotateLocation = rotateLocation;
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

    public StickerView(Context context) {
        this(context, null);
    }

    public StickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        handler = new GestureHandler();
        initView(context, attrs);
    }

    public static boolean isEmpty(List list) {
        return list == null || list.size() == 0;
    }

    private void initView(Context context, AttributeSet attributeSet) {

        TypedArray mTypedArray = context.obtainStyledAttributes(attributeSet,
                R.styleable.StickerView);
        leftTopBitmap = mTypedArray.getDrawable(R.styleable.StickerView_sv_left_top_drawable);
        leftBottomBitmap = mTypedArray.getDrawable(R.styleable.StickerView_sv_left_bottom_drawable);
        rightTopBitmap = mTypedArray.getDrawable(R.styleable.StickerView_sv_right_top_drawable);
        rightBottomBitmap = mTypedArray.getDrawable(R.styleable.StickerView_sv_right_bottom_drawable);
        rotateLocation = mTypedArray.getInteger(R.styleable.StickerView_sv_contron_location, RIGHT_BOTTOM_MODE);
        guideLineColor = mTypedArray.getColor(R.styleable.StickerView_sv_guide_line_color, Color.WHITE);
        guideLineWidth = mTypedArray.getDimension(R.styleable.StickerView_sv_guide_line_width, 5f);
        enableAutoAdjustDegree = mTypedArray.getBoolean(R.styleable.StickerView_sv_auto_degree, true);
        enableAutoAdjustCenter = mTypedArray.getBoolean(R.styleable.StickerView_sv_auto_center, true);
        frameColor = mTypedArray.getColor(R.styleable.StickerView_sv_frame_color, getResources().getColor(R.color.colorAccent));
        frameWidth = mTypedArray.getDimension(R.styleable.StickerView_sv_frame_width, 1);
        mTypedArray.recycle();

        debugPaint.setColor(Color.parseColor("#000000"));
        debugPaint.setStrokeWidth(20);
        debugPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        textPaint.setTextSize(40);
        textPaint.setColor(Color.RED);
        shadowPaint.setTextSize(40);
        shadowPaint.setColor(Color.BLUE);

        if (leftTopBitmap != null) {
            leftTopRect.set(0, 0, leftTopBitmap.getIntrinsicWidth(),
                    leftTopBitmap.getIntrinsicHeight());
            leftTopDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1,
                    STICKER_BTN_HALF_SIZE << 1);
        }
        if (rightBottomBitmap != null) {
            rightBottomRect.set(0, 0, rightBottomBitmap.getIntrinsicWidth(),
                    rightBottomBitmap.getIntrinsicHeight());
            rightBottomDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1,
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

        mHelpPaint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));
        mHelpPaint.setColor(frameColor);
        mHelpPaint.setStyle(Paint.Style.STROKE);
        mHelpPaint.setStrokeWidth(screenUtil.dip2px(BaseApplication.getInstance(),frameWidth));
        vibrator = (Vibrator) getContext().getSystemService(Service.VIBRATOR_SERVICE);
    }

    private final GestureHandler handler;

    private class GestureHandler extends Handler {
        GestureHandler() {
            super();
        }

        GestureHandler(Handler handler) {
            super(handler.getLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_FRAME:
                    if (!frameShow) {
                        frameShow = true;
                        invalidate();
                    }
                    break;
                case DISMISS_FRAME:
                    if (frameShow) {
                        frameShow = false;
                        invalidate();
                    }
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable drawable) {
        super.invalidateDrawable(drawable);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawContent(canvas);
    }

    public void destory() {
        if (handler != null) {
            handler.removeMessages(SHOW_FRAME);
            handler.removeMessages(DISMISS_FRAME);
            handler.removeCallbacksAndMessages(null);
        }
        stop();
    }

    boolean hasVibrated = false;

    /**
     * 绘制基准线
     * 当中心点对准时出现 基准线
     */
    private void drawGuideLine(Canvas canvas) {
        if (isGuideLineShow() && center != null && canvas != null && guideLineShowOntouch) {
            //绘制水平辅助线
            debugPaint.setStrokeWidth(guideLineWidth);
            debugPaint.setColor(guideLineColor);
            float canvasWidth = canvas.getWidth();
            float canvasHeight = canvas.getHeight();
            float lineLength = Math.min(canvasWidth, canvasHeight) * 0.2f;
            boolean needVib = false;
            if (center.x < canvasWidth / 2f + 25 && center.x > canvasWidth / 2f - 25) {
                canvas.drawLine(canvasWidth / 2f, 0, canvasWidth / 2f, lineLength, debugPaint);
                canvas.drawLine(canvasWidth / 2f, canvasHeight, canvasWidth / 2f, canvasHeight - lineLength, debugPaint);
                needVib = true;
            }
            if (center.y < canvasHeight / 2f + 25 && center.y > canvasHeight / 2f - 25) {
                canvas.drawLine(0, canvasHeight / 2f, lineLength, canvasHeight / 2f, debugPaint);
                canvas.drawLine(canvasWidth, canvasHeight / 2f, canvasWidth - lineLength, canvasHeight / 2f, debugPaint);
                needVib = true;
            }
            if (needVib) {
                if (!hasVibrated) {
                    vibrate();
                    hasVibrated = true;
                }
            } else {
                hasVibrated = false;
            }
        }
    }

    private synchronized void vibrate() {
        Log.e("Sticker", "开始振动=====");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, 50));
        } else {
            vibrator.vibrate(50);
        }
    }

    protected StaticLayout getStaticLayout(String content, TextPaint textPaint, int drawWidth) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return StaticLayout.Builder.obtain(content, 0, content.length(), textPaint, drawWidth)
                    .setAlignment(Layout.Alignment.ALIGN_CENTER)
                    .setBreakStrategy(Layout.BREAK_STRATEGY_SIMPLE)
                    .setLineSpacing(0, 1f)
                    .setIncludePad(true)
                    .build();
        } else {
            return new StaticLayout(content, textPaint, drawWidth, Layout.Alignment.ALIGN_CENTER, 1f, 0, true);
        }
    }

    private void drawContent(Canvas canvas) {
        if (currentDrawable != null) {
           // drawGuideLine(canvas);
            RectF rectF = new RectF(0, 0, currentDrawable.getIntrinsicWidth(), currentDrawable.getIntrinsicHeight());//behavior.onProgress(canvas, center, mScale, 1f, mRotateAngle, listener.getTextCurrent(), listener.getTextDuration());
            rectF.offset(center.x - rectF.centerX(), center.y - rectF.centerY());
            canvas.save();
            canvas.scale(mScale, mScale, center.x, center.y);
            canvas.rotate(mRotateAngle, center.x, center.y);
            currentDrawable.setBounds((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
            currentDrawable.draw(canvas);
            canvas.restore();

            mHelpBoxRect.set(rectF);
            RectUtil.scaleRect(mHelpBoxRect, mScale);
            if (frameShow) {
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
                }
                leftTopDstRect.offsetTo(mHelpBoxRect.left - offsetValue,
                        mHelpBoxRect.top - offsetValue);
                rightBottomDstRect.offsetTo(mHelpBoxRect.right - offsetValue,
                        mHelpBoxRect.bottom - offsetValue);
                leftBottomDstRect.offsetTo(mHelpBoxRect.left - offsetValue,
                        mHelpBoxRect.bottom - offsetValue);
                rightTopDstRect.offsetTo(mHelpBoxRect.right - offsetValue,
                        mHelpBoxRect.top - offsetValue);

                RectUtil.rotateRect(leftTopDstRect, mHelpBoxRect.centerX(),
                        mHelpBoxRect.centerY(), mRotateAngle);
                RectUtil.rotateRect(rightBottomDstRect, mHelpBoxRect.centerX(),
                        mHelpBoxRect.centerY(), mRotateAngle);
                RectUtil.rotateRect(leftBottomDstRect, mHelpBoxRect.centerX(),
                        mHelpBoxRect.centerY(), mRotateAngle);
                RectUtil.rotateRect(rightTopDstRect, mHelpBoxRect.centerX(),
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
            }


        }
    }

    /**
     * 更新
     */
    public void update() {
        invalidate();
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private int adjustMode(float x, float y) {
        if (leftTopDstRect != null && leftTopDstRect.contains(x, y)) {
            return LEFT_TOP_MODE;
        } else if (leftBottomDstRect != null && leftBottomDstRect.contains(x, y)) {
            return LEFT_BOTTOM_MODE;
        } else if (rightTopDstRect != null && rightTopDstRect.contains(x, y)) {
            return RIGHT_TOP_MODE;
        } else if (rightBottomDstRect != null && rightBottomDstRect.contains(x, y)) {
            return RIGHT_BOTTOM_MODE;
        } else if (isIn(mHelpBoxRect, x, y, mRotateAngle)) {
            return MOVE_MODE;
        } else {
            return IDLE_MODE;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 是否向下传递事件标志 true为消耗
        super.onTouchEvent(event);
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mCurrentMode = adjustMode(x, y);
                if (mCurrentMode == IDLE_MODE) {
                    return false;
                }
                lastX = x;
                lastY = y;
                guideLineShowOntouch = true;
                if (tickerListener != null) {
                    tickerListener.onActionStart(this, mCurrentMode, mScale, mRotateAngle, center);
                }
                if (!frameShow) {
                    frameShow = true;
                    invalidate();
                }
                handler.removeMessages(DISMISS_FRAME);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mCurrentMode == IDLE_MODE) {
                    return false;
                }
                if (mCurrentMode == MOVE_MODE) {
                    // 移动贴图
                    mCurrentMode = MOVE_MODE;
                    float dx = x - lastX;
                    float dy = y - lastY;

                    layoutX += dx;
                    layoutY += dy;
                    adjustCenter(dx, dy);

                    invalidate();

                    lastX = x;
                    lastY = y;
                } else if (mCurrentMode == rotateLocation) {
                    // 旋转 缩放文字操作
                    mCurrentMode = rotateLocation;
                    float dx = x - lastX;
                    float dy = y - lastY;
                    updateRotateAndScale(dx, dy);
                    invalidate();
                    lastX = x;
                    lastY = y;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mCurrentMode == IDLE_MODE) {
                    return false;
                }
                handler.removeMessages(DISMISS_FRAME);
                handler.sendEmptyMessageDelayed(DISMISS_FRAME, AUTO_FADE_FRAME_TIMEOUT);
                guideLineShowOntouch = false;
                invalidate();
                if (tickerListener != null) {
                    tickerListener.onActionEnd(this, mCurrentMode, mScale, mRotateAngle, center);
                }
                mCurrentMode = IDLE_MODE;
                break;
            default:
                break;
        }// end switch

        return true;
    }


    public boolean isIn(RectF source, float x, float y, float angle) {
        RectF rectF = new RectF(source);
        if (angle != 0) {
            Matrix matrix = new Matrix();
            //设置旋转角度时，一定要记得设置旋转的中心，该中心与绘图时的中心点是一致的。
            matrix.setRotate(angle, rectF.centerX(), rectF.centerY());
            matrix.mapRect(rectF);
        }
        if (rectF.contains(x, y)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 消除左上角的删除按钮, 右下角的拖动图标, 和黑色的边框.
     */
    public void disappearIconBorder() {
        invalidate();
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

        mRotateAngle = adjustDegree(mRotateAngle, angle);//+= angle;
    }

    boolean degreeTurned = false;
    float tempDegree = 0;

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
            if (Math.abs(tempDegree) <= 10f) {
                return current;
            } else {
                tempDegree = 0;
                degreeTurned = false;
                return current + tempDegree;
            }
        }
        return current + newDegree;
    }

    float autoCenterRange = 30;
    float autoCenterMoveX = 0;
    float autoCenterMoveY = 0;

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

    public void resetView() {
        layoutX = getMeasuredWidth() / 2;
        layoutY = getMeasuredHeight() / 2;
        center.set(layoutX, layoutY);
        mRotateAngle = 0;
        mScale = 1;
    }

    public void setCenter(float centerx, float centery) {
        if (center == null) {
            center = new PointF(centerx, centery);
        } else {
            center.set(centerx, centery);
        }
    }

    public PointF getCenter() {
        return center;
    }

    public void setScale(float scale) {
        this.mScale = scale;
    }

    public void setDegree(float degree) {
        this.mRotateAngle = degree;
    }

    public float getScale() {
        return mScale;
    }

    public float getRotateAngle() {
        return mRotateAngle;
    }

    public StickerTarger getTarger() {
        if (targer == null) {
            targer = new StickerTarger();
        }
        return targer;
    }

    StickerTarger targer = null;

    class StickerTarger extends SimpleTarget<D> {
        boolean autoRun = false;

        public void setAutoRun(boolean autoRun) {
            this.autoRun = autoRun;
        }

        @Override
        public void onResourceReady(@NonNull D resource, @Nullable Transition<? super D> transition) {
            Log.e("Sticker", "onResourceReady:" + resource.getIntrinsicWidth() + "==" + resource.getIntrinsicHeight());
            currentDrawable = resource;
            if (autoRun) {
                start();
            }
            setScale(1f);
            setDegree(0f);
            setCenter(getWidth() / 2f, getHeight() / 2f);
            invalidate();
        }
    }

    public float getContentHeight() {
        return contentHeight;
    }

    public float getContentWidth() {
        return contentWidth;
    }

    public String getResPath() {
        return resPath;
    }

    private String resPath = null;
    private float contentWidth;
    private float contentHeight;
    D currentDrawable = null;
    final RequestOptions options = new RequestOptions().centerCrop();

    public void setImageRes(final String path, final boolean autoRun) {
        if (!TextUtils.isEmpty(path)) {
            stop();
            this.resPath = path;
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    getTarger().setAutoRun(autoRun);
                    contentWidth = (int) (getMinDisplayWidth() / 2f);
                    contentHeight = (int) (getMinDisplayWidth() / 2f);
                    RequestManager manager = Glide.with(getContext());
                    RequestBuilder builder = null;
                    if (path.endsWith(".gif")) {
                        builder = manager.asGif();
                    } else {
                        builder = manager.asDrawable();
                    }
                    options.override((int) contentWidth, (int) contentHeight);
                    builder.load(path)
                            .apply(options)
                            .into(getTarger());
                }
            });
        } else {
            //路径不存在
            Toast.makeText(getContext(), "文件不存在", Toast.LENGTH_LONG).show();
        }
    }

    private int getMinDisplayWidth() {
        return Math.min(getMeasuredWidth(), getMeasuredHeight());
    }

    private int getMaxDisplayHeight() {
        return Math.max(getMeasuredWidth(), getMeasuredHeight());
    }
}
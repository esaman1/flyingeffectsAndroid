package com.flyingeffects.com.view.histogram;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.bigkoo.convenientbanner.utils.ScreenUtil;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.utils.LogUtil;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * autour : lbing
 * date : 2018/7/30 0030 09:35
 * className :
 * version : 1.0
 * description :
 */


public class MyBarChartView extends View {

    private int barInterval;
    private int barWidth;
    //    private int top_text_size;
//    private int bottom_text_size;
    private int bar_color;
    private int bottom_line_color;
    private int top_text_color;
    private int bottom_text_color;
    //    private Paint mTopTextPaint;
//    private Paint mBottomTextPaint;
    private Paint mBarPaint;
    private Paint mBottomLinePaint;
    private ArrayList<BarData> innerData = new ArrayList<>();
    private int paddingTop;
    private int paddingLeft;
    private int paddingBottom;
    private int paddingRight;
    private int defaultHeight = dp2Px(180);
    private int bottom_view_height = dp2Px(30);
    private int top_text_height = dp2Px(30);
    private float scaleTimes = 1;
    private float lastX = 0;
    private float lastY = 0;
    private int measureWidth = 0;
    //这是最初的的位置
    private float startOriganalX = 0;
    private HorizontalScrollRunnable horizontalScrollRunnable;
    //临时滑动的距离
    private float tempLength = 0;
    private long startTime = 0;
    private boolean isFling = false;
    private float dispatchTouchX = 0;
    private float dispatchTouchY = 0;
    //是否到达边界
    private boolean isBoundary = false;
    private boolean isMove = false;

    private int frameCount;
    private float showPercentage;

    public MyBarChartView(Context context) {
        this(context, null);
    }

    public MyBarChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyBarChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.barchar_style);
        barInterval = (int) typedArray.getDimension(R.styleable.barchar_style_barInterval, dp2Px(0.2f));
        bar_color = typedArray.getColor(R.styleable.barchar_style_bar_color, Color.parseColor("#5496FF"));
        barWidth = (int) typedArray.getDimension(R.styleable.barchar_style_barWidth, dp2Px(1f));
//        top_text_size = (int) typedArray.getDimension(R.styleable.barchar_style_top_text_size, sp2Px(8));
        top_text_color = typedArray.getColor(R.styleable.barchar_style_top_text_color, Color.parseColor("#00ff00"));
//        bottom_text_size = (int) typedArray.getDimension(R.styleable.barchar_style_bottom_text_size, sp2Px(8));
        bottom_text_color = typedArray.getColor(R.styleable.barchar_style_bottom_text_color, Color.parseColor("#0000ff"));
        bottom_line_color = typedArray.getColor(R.styleable.barchar_style_bottom_line_color, Color.parseColor("#000000"));
        typedArray.recycle();
        initPaint();
    }

    private int dp2Px(float dipValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private int sp2Px(float spValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    private void initPaint() {
//        mTopTextPaint = new Paint();
//        mTopTextPaint.setTextSize(top_text_size);
//        mTopTextPaint.setColor(top_text_color);
//        mTopTextPaint.setStrokeCap(Paint.Cap.ROUND);
//        mTopTextPaint.setStyle(Paint.Style.FILL);
//        mTopTextPaint.setDither(true);
//
//        mBottomTextPaint = new Paint();
//        mBottomTextPaint.setTextSize(bottom_text_size);
//        mBottomTextPaint.setColor(bottom_text_color);
//        mBottomTextPaint.setStrokeCap(Paint.Cap.ROUND);
//        mBottomTextPaint.setStyle(Paint.Style.FILL);
//        mBottomTextPaint.setDither(true);


        mBarPaint = new Paint();
//        mBarPaint.setTextSize(top_text_size);
        mBarPaint.setColor(bar_color);
        mBarPaint.setStrokeCap(Paint.Cap.ROUND);
        mBarPaint.setStyle(Paint.Style.FILL);
        mBarPaint.setDither(true);


        mBottomLinePaint = new Paint();
//        mBottomLinePaint.setTextSize(top_text_size);
        mBottomLinePaint.setColor(bottom_line_color);
        mBottomLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mBottomLinePaint.setStyle(Paint.Style.FILL);
        mBottomLinePaint.setDither(true);
        //设置底部线的宽度
        mBottomLinePaint.setStrokeWidth(dp2Px(0.6f));


    }

    public void setBarChartData(ArrayList<BarData> innerData) {
        this.innerData.clear();
        innerData = complementData(innerData);
        this.innerData.addAll(innerData);
        scaleTimes = (float) getMaxValue() / (float) (defaultHeight - bottom_view_height - top_text_height);
        invalidate();
    }


    /**
     * description ：frameCount
     * creation date: 2020/8/26
     * user : zhangtongju
     */
    public void setBaseData(int frameCount, float showPercentage) {
        this.frameCount = frameCount;
        this.showPercentage = showPercentage;

        LogUtil.d("OOM2", "百分比为" + showPercentage);
        LogUtil.d("OOM2", "点数" + frameCount);

    }


    private int getMaxValue() {
        int defaultValue = 0;
        if (innerData.size() > 0) {
            defaultValue = innerData.get(0).getCount();

            for (BarData data : innerData
            ) {
                if (data.getCount() > defaultValue) {
                    defaultValue = data.getCount();
                }
            }

//            for (int i = 0; i < innerData.size(); i++) {
//                if (innerData.get(i).getCount() > defaultValue) {
//                    defaultValue = innerData.get(i).getCount();
//                }
//            }
        }
        return defaultValue;
    }

    //进行滑动的边界处理
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.e("TAG", "MyBarChartView===dispatchTouchEvent==" + ev.getAction());
        int dispatchCurrX = (int) ev.getX();
        int dispatchCurrY = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //父容器不拦截点击事件，子控件拦截点击事件。如果不设置为true,外层会直接拦截，从而导致motionEvent为cancle
                getParent().requestDisallowInterceptTouchEvent(true);
                dispatchTouchX = getX();
                dispatchTouchY = getY();
                break;
            case MotionEvent.ACTION_MOVE:

                float deltaX = dispatchCurrX - dispatchTouchX;
                float deltaY = dispatchCurrY - dispatchTouchY;
                if (Math.abs(deltaY) - Math.abs(deltaX) > 0) {//竖直滑动的父容器拦截事件
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                //这是向右滑动，如果是滑动到边界，那么就让父容器进行拦截
                if ((dispatchCurrX - dispatchTouchX) > 0 && startOriganalX == 0) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else if ((dispatchCurrX - dispatchTouchX) < 0 && startOriganalX == -getMoveLength()) {//这是向右滑动
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        dispatchTouchX = dispatchCurrX;
        dispatchTouchY = dispatchCurrY;
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        isBoundary = false;
        isMove = true;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                lastY = event.getY();
                startTime = System.currentTimeMillis();
                //当点击的时候，判断如果是在fling的效果的时候，就停止快速滑动
                if (isFling) {
                    removeCallbacks(horizontalScrollRunnable);
                    isFling = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float currX = event.getX();
                float currY = event.getY();
                startOriganalX += currX - lastX;

                //这是向右滑动
                if ((currX - lastX) > 0) {
                    Log.e("TAG", "向右滑动");
                    if (startOriganalX > 0) {
                        startOriganalX = 0;
                        isBoundary = true;
                    }

                } else {//这是向右滑动
                    Log.e("TAG", "向左滑动");
                    if (-startOriganalX > getMoveLength()) {
                        startOriganalX = -getMoveLength();
                        isBoundary = true;
                    }
                }
                tempLength = currX - lastX;
                //如果数据量少，根本没有充满横屏，就没必要重新绘制，
                if (measureWidth < innerData.size() * (barWidth + barInterval)) {
                    invalidate();
                }

                lastX = currX;
                lastY = currY;
                break;
            case MotionEvent.ACTION_UP:
                long endTime = System.currentTimeMillis();
                //计算猛滑动的速度，如果是大于某个值，并且数据的长度大于整个屏幕的长度，那么就允许有flIng后逐渐停止的效果
                float speed = tempLength / (endTime - startTime) * 1000;
                if (Math.abs(speed) > 100 && !isFling && measureWidth < innerData.size() * (barWidth + barInterval)) {
                    this.post(horizontalScrollRunnable = new HorizontalScrollRunnable(speed));
                }
                isMove = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                isMove = false;
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //   drawBottomLine(canvas);

//        //如果没有数据 绘制loading...
//        if (innerData.size() <= 0) {
//            drawNoDataText(canvas);
//
//        } else {
        int startX = (int) (paddingLeft + startOriganalX);
        int endY = defaultHeight - bottom_view_height;
        //jj
        int halft = defaultHeight / 2;


        for (int i = 0; i < innerData.size(); i++) {
            float barHeight = 0;
            if (scaleTimes != 0) {
                barHeight = (float) innerData.get(i).getCount() / scaleTimes;
            }
            int startY = (int) (defaultHeight - bottom_view_height - barHeight);

//                int haltEndY=startY/2;
            int haltEndY = (int) barHeight;


//                float topTextWidth = mTopTextPaint.measureText(innerData.get(i).getCount() + "");
//                float textStartX = startX + barWidth / 2 - topTextWidth / 2;
//                float textStartY = startY - 10;
//                //绘制bar上的文字
//                drawTopText(canvas, innerData.get(i).getCount() + "", textStartX, textStartY);
            //绘制bar
            //  drawBar(canvas, startX, startY, endY);

            drawBar(canvas, startX, halft - haltEndY, halft + haltEndY);
            //绘制下面的文字
//                float bottomTextWidth = mBottomTextPaint.measureText(innerData.get(i).bottomText);
//                float bottomStartX = startX + barWidth / 2 - bottomTextWidth / 2;
//                Rect rect = new Rect();
//                mBottomTextPaint.getTextBounds(innerData.get(i).getBottomText(), 0, innerData.get(i).getBottomText().length(), rect);
//                float bottomStartY = defaultHeight - bottom_view_height + 10 + rect.height();//rect.height()是获取文本的高度;
//                //绘制底部的文字
//                drawBottomText(canvas, innerData.get(i).getBottomText(), bottomStartX, bottomStartY);

            startX = startX + barWidth + barInterval;
        }
//        }

    }

    private void drawBottomLine(Canvas canvas) {
        canvas.drawLine(paddingLeft, defaultHeight - bottom_view_height, innerData.size() * (barWidth + barInterval), defaultHeight - bottom_view_height, mBottomLinePaint);
    }

    private void drawNoDataText(Canvas canvas) {
//        String text = "loading...";
//        float textWidth = mBottomTextPaint.measureText(text);
//        canvas.drawText(text, measureWidth / 2 - textWidth / 2, defaultHeight / 2 - 10, mBottomTextPaint);
    }

//    //绘制bar上的文字
//    private void drawTopText(Canvas canvas, String text, float textStartX, float textStartY) {
//        canvas.drawText(text, textStartX, textStartY, mTopTextPaint);
//    }

    //绘制bar
    private void drawBar(Canvas canvas, int startX, int startY, int endY) {
        Rect mRect = new Rect(startX, startY, startX + barWidth, endY);
        canvas.drawRect(mRect, mBarPaint);

    }

//    private void drawBottomText(Canvas canvas, String text, float bottomStartX, float bottomStartY) {
//        canvas.drawText(text, bottomStartX, bottomStartY, mBottomTextPaint);
//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = 0;
        int height = 0;

        if (widthMode == MeasureSpec.EXACTLY) {
            measureWidth = width = widthSize;
        } else {
            width = getAndroiodScreenProperty().get(0);
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            defaultHeight = height = heightSize;
        } else {
            height = defaultHeight;
        }
        setMeasuredDimension(width, height);
        paddingTop = getPaddingTop();
        paddingLeft = getPaddingLeft();
        paddingBottom = getPaddingBottom();
        paddingRight = getPaddingRight();
    }

    private ArrayList<Integer> getAndroiodScreenProperty() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;         // 屏幕宽度（像素）
        int height = dm.heightPixels;       // 屏幕高度（像素）
        float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        int screenWidth = (int) (width / density);  // 屏幕宽度(dp)
        int screenHeight = (int) (height / density);// 屏幕高度(dp)

        ArrayList<Integer> integers = new ArrayList<>();
        integers.add(screenWidth);
        integers.add(screenHeight);
        return integers;
    }

    private int getMoveLength() {
        return (barWidth + barInterval) * innerData.size() - measureWidth;
    }

    public boolean isBoundary() {
        return isBoundary;
    }

    public boolean isMove() {
        return isMove;
    }

    public static class BarData {
        private int count;
        private String bottomText;

        public BarData(int count, String bottomText) {
            this.count = count;
            this.bottomText = bottomText;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getBottomText() {
            return bottomText == null ? "" : bottomText;
        }

        public void setBottomText(String bottomText) {
            this.bottomText = bottomText;
        }
    }

    private class HorizontalScrollRunnable implements Runnable {

        private float speed = 0;

        public HorizontalScrollRunnable(float speed) {
            this.speed = speed;
        }

        @Override
        public void run() {
            if (Math.abs(speed) < 30) {
                isFling = false;
                return;
            }
            isFling = true;
            startOriganalX += speed / 15;
            speed = speed / 1.15f;
            //这是向右滑动
            if ((speed) > 0) {
                Log.e("TAG", "向右滑动");
                if (startOriganalX > 0) {
                    startOriganalX = 0;
                }

            } else {//这是向右滑动
                Log.e("TAG", "向左滑动");
                if (-startOriganalX > getMoveLength()) {
                    startOriganalX = -getMoveLength();
                }
            }
            postDelayed(this, 20);
            invalidate();
        }
    }


    /**
     * description ：计算当前在不滑动的时候需要多少个点（减去magrin），然后计算当前屏幕需要显示的百分比，如果是1以下，表示需要滑动，
     * 那么总需要的点为当前屏幕需要点除以百分比，得到总需要的点，最后在用得到的点模拟需要的点。如果是1以上，表示，表示正常情况下要少
     * 余屏幕点，不需要滑动，那么只需要得到屏幕点就可以了。
     * 添加点的逻辑，比如，100变为120  及、100/（120-100），表示多少个点就复制一次
     * creation date: 2020/8/27
     * user : zhangtongju
     */
    boolean hasMore = false;

    private ArrayList<BarData> complementData(ArrayList<BarData> innerData) {
        // 1   计算当前屏幕需要多少个点
        int screenWidth = getMeasuredWidth();
        //1个点占有位置
        int oneWidth = barWidth + barInterval;
        //大约一页能显示多少个
        float needCountF = screenWidth / (float) oneWidth;
        int needCopyPositionI = 0;
        if (frameCount > needCountF) {
            hasMore = true;
            //如果超过当前需要的点，需要滑动
            float differenceValue = frameCount - needCountF;
            LogUtil.d("OOM2","全部count="+frameCount+"一页需要的cont="+needCountF);
            float needCopyPosition;
            if(needCountF>differenceValue){
                needCopyPosition  = needCountF / differenceValue;
            }else{
                 needCopyPosition = differenceValue / needCountF;
            }
            needCopyPositionI = (int) needCopyPosition;
            LogUtil.d("OOM2","需要复制的值为"+needCopyPositionI);
        } else {
            //小于当前的点，需要添加值
            float differenceValue = needCountF - frameCount;
            float needCopyPosition = frameCount / differenceValue;
            needCopyPositionI = (int) needCopyPosition;
            hasMore = false;
        }
        ArrayList<BarData> newInnerData = new ArrayList<>();
        for (int i = 1; i <= innerData.size(); i++) {
            newInnerData.add(innerData.get(i - 1));
            if (!hasMore) {
                if (i % needCopyPositionI == 0) {
                    newInnerData.add(innerData.get(i));//多复制一次
                }
            } else {
                //太大了就多复制几次
                for (int x = 0; x < needCopyPositionI; x++) {
                    newInnerData.add(innerData.get(i - 1));
                }
            }
        }
        return newInnerData;
    }


}



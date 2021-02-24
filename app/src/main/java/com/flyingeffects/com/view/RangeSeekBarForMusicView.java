package com.flyingeffects.com.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.screenUtil;
import com.flyingeffects.com.view.beans.Thumb;
import com.flyingeffects.com.view.interfaces.OnRangeSeekBarListener;

import java.util.ArrayList;
import java.util.List;

public class RangeSeekBarForMusicView extends View {

    private static final String TAG = RangeSeekBarForMusicView.class.getSimpleName();

    private int mHeightTimeLine;
    private List<Thumb> mThumbs;
    private List<OnRangeSeekBarListener> mListeners;
    private float mMaxWidth;
    private float mThumbWidth;
    private float mThumbHeight;
    private int mViewWidth;
    private float mPixelRangeMin;
    private float mPixelRangeMax;
    private float mScaleRangeMax;
    private boolean mFirstRun;

    private final Paint mShadow = new Paint();
    private final Paint mLine = new Paint();
    //画拖动条和视频条上方的高低差
    private int offsetY = 0;
    private final Paint mBackground = new Paint();

    public RangeSeekBarForMusicView(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RangeSeekBarForMusicView(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mThumbs = Thumb.initThumbs(getResources());
        mThumbWidth = Thumb.getWidthBitmap(mThumbs);
        mThumbHeight = Thumb.getHeightBitmap(mThumbs);

        mScaleRangeMax = 100;
        mHeightTimeLine = getContext().getResources().getDimensionPixelOffset(R.dimen.frames_video_height);
//        offsetY= Math.round(DimensionUtils.dp2px(2));
        offsetY = Math.round(screenUtil.dip2px(BaseApplication.getInstance(), 2));
        setFocusable(true);
        setFocusableInTouchMode(true);

        mFirstRun = true;

        int shadowColor = ContextCompat.getColor(getContext(), R.color.shadow_color);
        mShadow.setAntiAlias(true);
        mShadow.setColor(shadowColor);
        mShadow.setAlpha(200);

        int lineColor = ContextCompat.getColor(getContext(), R.color.line_color);
        mLine.setAntiAlias(true);
        mLine.setColor(lineColor);

        int backgroundColor = ContextCompat.getColor(getContext(), R.color.white);
        mBackground.setAntiAlias(true);
        mBackground.setColor(backgroundColor);
    }

    public void initMaxWidth() {
        mMaxWidth = mThumbs.get(1).getPos() - mThumbs.get(0).getPos();
        LogUtil.d("OOM", "mMaxWidth=" + mMaxWidth);
        onSeekStop(this, 0, mThumbs.get(0).getVal());
        onSeekStop(this, 1, mThumbs.get(1).getVal());

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int minW = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        mViewWidth = resolveSizeAndState(minW, widthMeasureSpec, 1);

        int minH = getPaddingBottom() + getPaddingTop() + (int) mThumbHeight + mHeightTimeLine;
        int viewHeight = resolveSizeAndState(minH, heightMeasureSpec, 1);

        setMeasuredDimension(mViewWidth, viewHeight);

        mPixelRangeMin = 0;
        mPixelRangeMax = mViewWidth - mThumbWidth;

        if (mFirstRun) {
            for (int i = 0; i < mThumbs.size(); i++) {
                Thumb th = mThumbs.get(i);
                th.setVal(mScaleRangeMax * i);
                th.setPos(mPixelRangeMax * i);
            }
            // Fire listener callback
            onCreate(this, currentThumb, getThumbValue(currentThumb));
            mFirstRun = false;
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
//        drawBackground(canvas);
        drawShadow(canvas);
        drawThumbs(canvas);
    }

    private int currentThumb = 0;

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev) {
        final Thumb mThumb;
        final Thumb mThumb2;
        final float coordinate = ev.getX();
        final int action = ev.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                // Remember where we started
                currentThumb = getClosestThumb(coordinate);

                if (currentThumb == -1) {
                    return false;
                }

                mThumb = mThumbs.get(currentThumb);
                mThumb.setLastTouchX(coordinate);
                onSeekStart(this, currentThumb, mThumb.getVal());
                return true;
            }
            case MotionEvent.ACTION_UP: {

                if (currentThumb == -1) {
                    return false;
                }

                mThumb = mThumbs.get(currentThumb);
                onSeekStop(this, currentThumb, mThumb.getVal());
                return true;
            }

            case MotionEvent.ACTION_MOVE: {
                mThumb = mThumbs.get(currentThumb);
                mThumb2 = mThumbs.get(currentThumb == 0 ? 1 : 0);
                // Calculate the distance moved
                final float dx = coordinate - mThumb.getLastTouchX();
                final float newX = mThumb.getPos() + dx;
                if (currentThumb == 0) {
                    if ((newX + mThumb.getWidthBitmap() + minDistance) >= mThumb2.getPos()) {
                        return true;
                    }
                    if ((newX + mThumb.getWidthBitmap()) >= mThumb2.getPos()) {
                        mThumb.setPos(mThumb2.getPos() - mThumb.getWidthBitmap());
                    } else if (newX <= mPixelRangeMin) {
                        mThumb.setPos(mPixelRangeMin);
                    } else {
                        //Check if thumb is not out of max width
                        checkPositionThumb(mThumb, mThumb2, dx, true);
                        // Move the object
                        mThumb.setPos(mThumb.getPos() + dx);

                        // Remember this touch position for the next move event
                        mThumb.setLastTouchX(coordinate);
                    }

                }




                else {
                    if ((newX<=mThumb2.getPos()+mThumb2.getWidthBitmap()+minDistance)){return true;}
                    if (newX <= mThumb2.getPos() + mThumb2.getWidthBitmap()) {
                        mThumb.setPos(mThumb2.getPos() + mThumb.getWidthBitmap());
                    } else if (newX >= mPixelRangeMax) {
                        mThumb.setPos(mPixelRangeMax);
                    } else {
                        //Check if thumb is not out of max width
                        checkPositionThumb(mThumb2, mThumb, dx, false);
                        // Move the object
                        mThumb.setPos(mThumb.getPos() + dx);
                        // Remember this touch position for the next move event
                        mThumb.setLastTouchX(coordinate);
                    }
                }

                setThumbPos(currentThumb, mThumb.getPos());

                // Invalidate to request a redraw
                invalidate();
                return true;
            }
        }
        return false;
    }

    private void checkPositionThumb(@NonNull Thumb mThumbLeft, @NonNull Thumb mThumbRight, float dx, boolean isLeftMove) {
        if (isLeftMove && dx < 0) {
            if ((mThumbRight.getPos() - (mThumbLeft.getPos() + dx)) > mMaxWidth) {
                mThumbRight.setPos(mThumbLeft.getPos() + dx + mMaxWidth);
                setThumbPos(1, mThumbRight.getPos());
            }
        } else if (!isLeftMove && dx > 0) {
            if (((mThumbRight.getPos() + dx) - mThumbLeft.getPos()) > mMaxWidth) {
                mThumbLeft.setPos(mThumbRight.getPos() + dx - mMaxWidth);
                setThumbPos(0, mThumbLeft.getPos());
            }
        }
    }

    private int getUnstuckFrom(int index) {
        int unstuck = 0;
        float lastVal = mThumbs.get(index).getVal();
        for (int i = index - 1; i >= 0; i--) {
            Thumb th = mThumbs.get(i);
            if (th.getVal() != lastVal) {
                return i + 1;
            }
        }
        return unstuck;
    }

    private float pixelToScale(int index, float pixelValue) {
        float scale = (pixelValue * 100) / mPixelRangeMax;
        if (index == 0) {
            float pxThumb = (scale * mThumbWidth) / 100;
            return scale + (pxThumb * 100) / mPixelRangeMax;
        } else {
            float pxThumb = ((100 - scale) * mThumbWidth) / 100;
            return scale - (pxThumb * 100) / mPixelRangeMax;
        }
    }

    private float scaleToPixel(int index, float scaleValue) {
        float px = (scaleValue * mPixelRangeMax) / 100;
        if (index == 0) {
            float pxThumb = (scaleValue * mThumbWidth) / 100;
            return px - pxThumb;
        } else {
            float pxThumb = ((100 - scaleValue) * mThumbWidth) / 100;
            return px + pxThumb;
        }
    }

    private void calculateThumbValue(int index) {
        if (index < mThumbs.size() && !mThumbs.isEmpty()) {
            Thumb th = mThumbs.get(index);
            th.setVal(pixelToScale(index, th.getPos()));
            onSeek(this, index, th.getVal());
        }
    }

    private void calculateThumbPos(int index) {
        if (index < mThumbs.size() && !mThumbs.isEmpty()) {
            Thumb th = mThumbs.get(index);
            th.setPos(scaleToPixel(index, th.getVal()));
        }
    }

    private float getThumbValue(int index) {
        return mThumbs.get(index).getVal();
    }

    public void setThumbValue(int index, float value) {
        mThumbs.get(index).setVal(value);
        calculateThumbPos(index);
        // Tell the view we want a complete redraw
        invalidate();
    }

    private void setThumbPos(int index, float pos) {
        mThumbs.get(index).setPos(pos);
        calculateThumbValue(index);
        // Tell the view we want a complete redraw
        invalidate();
    }

    private int getClosestThumb(float coordinate) {
        int closest = -1;
        if (!mThumbs.isEmpty()) {
            for (int i = 0; i < mThumbs.size(); i++) {
                // Find thumb closest to x coordinate
                final float tcoordinate = mThumbs.get(i).getPos() + mThumbWidth;
                if (coordinate >= mThumbs.get(i).getPos() && coordinate <= tcoordinate) {
                    closest = mThumbs.get(i).getIndex();
                }
            }
        }
        return closest;
    }

    private void drawBackground(@NonNull Canvas canvas) {
        int left = Math.round(mThumbs.get(0).getPos() + mThumbWidth);
        int right = Math.round(mThumbs.get(1).getPos());
        int upperTop = -offsetY;
        int upperBottom = offsetY;
        int lowerTop = mHeightTimeLine;
        int lowerBottom = mHeightTimeLine + offsetY;
        Rect upperRect = new Rect(left, upperTop, right, upperBottom);
        Rect lowerRect = new Rect(left, lowerTop, right, lowerBottom);
        canvas.drawRect(upperRect, mBackground);
        canvas.drawRect(lowerRect, mBackground);
    }

    private void drawShadow(@NonNull Canvas canvas) {
        if (!mThumbs.isEmpty()) {

            for (Thumb th : mThumbs) {
                if (th.getIndex() == 0) {
                    final float x = th.getPos() + getPaddingLeft();
                    if (x > mPixelRangeMin) {
                        Rect mRect = new Rect((int) 0, 0, (int) (x + mThumbWidth), mHeightTimeLine + offsetY);
                        canvas.drawRect(mRect, mShadow);
                    }
                } else {
                    final float x = th.getPos() - getPaddingRight();
                    if (x < mPixelRangeMax) {
                        Rect mRect = new Rect((int) x, 0, (int) (mViewWidth - mThumbWidth), mHeightTimeLine + offsetY);
                        canvas.drawRect(mRect, mShadow);
                    }
                }
            }
        }
    }

    private void drawThumbs(@NonNull Canvas canvas) {

        if (!mThumbs.isEmpty()) {
            for (Thumb th : mThumbs) {
                if (th.getIndex() == 0) {
                    LogUtil.d("xxxx", "xxxx=" + th.getPos() + getPaddingLeft());
                    canvas.drawBitmap(th.getBitmap(), th.getPos() + getPaddingLeft(), getPaddingTop() - offsetY, null);
                } else {
                    LogUtil.d("xxxx2", "xxxx=" + th.getPos() + getPaddingLeft());
                    canvas.drawBitmap(th.getBitmap(), th.getPos() - getPaddingRight(), getPaddingTop() - offsetY, null);
                }
            }
        }
    }

    public void addOnRangeSeekBarListener(OnRangeSeekBarListener listener) {

        if (mListeners == null) {
            mListeners = new ArrayList<>();
        }

        mListeners.add(listener);
    }

    private void onCreate(RangeSeekBarForMusicView rangeSeekBarView, int index, float value) {
        if (mListeners == null) {
            return;
        }

        for (OnRangeSeekBarListener item : mListeners) {
            item.onCreate(rangeSeekBarView, index, value);
        }
    }

    private void onSeek(RangeSeekBarForMusicView rangeSeekBarView, int index, float value) {
        if (mListeners == null) {
            return;
        }

        for (OnRangeSeekBarListener item : mListeners) {
            item.onSeek(rangeSeekBarView, index, value);
        }
    }

    private void onSeekStart(RangeSeekBarForMusicView rangeSeekBarView, int index, float value) {
        if (mListeners == null) {
            return;
        }

        for (OnRangeSeekBarListener item : mListeners) {
            item.onSeekStart(rangeSeekBarView, index, value);
        }
    }

    private void onSeekStop(RangeSeekBarForMusicView rangeSeekBarView, int index, float value) {
        if (mListeners == null) {
            return;
        }

        for (OnRangeSeekBarListener item : mListeners) {
            item.onSeekStop(rangeSeekBarView, index, value);
        }
    }

    public List<Thumb> getThumbs() {
        return mThumbs;
    }

    private float minDistance = 0;

    public void setMinDistance(long duration) {
        minDistance = (1f * 2000 / duration) * mPixelRangeMax;
    }
}

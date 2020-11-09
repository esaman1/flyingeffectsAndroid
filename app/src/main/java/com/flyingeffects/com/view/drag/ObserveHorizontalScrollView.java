package com.flyingeffects.com.view.drag;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * @Author: savion
 * @Date: 2019/3/4 10:06
 * @Des:
 **/
public class ObserveHorizontalScrollView extends HorizontalScrollView {
    private OnScrollChangeListener onScrollChangeListener;
    private boolean isOnDragChanged = false;

    public void setOnScrollChangeListener(OnScrollChangeListener changeListener) {
        this.onScrollChangeListener = changeListener;
    }

    public ObserveHorizontalScrollView(Context context) {
        super(context);
    }

    public ObserveHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObserveHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onScrollChangeListener != null)
            onScrollChangeListener.onScrollChanged(l, t, oldl, oldt, isOnDragChanged);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isOnDragChanged = true;
                if (onScrollChangeListener!=null){
                    onScrollChangeListener.onTouchStart();
                }
                break;
            case MotionEvent.ACTION_UP:
                isOnDragChanged = false;
                if (onScrollChangeListener!=null){
                    onScrollChangeListener.onTouchEnd();
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);

    }

    @Override
    public void fling(int velocityX) {
        super.fling(velocityX / 1000);
    }

    public interface OnScrollChangeListener {
        void onScrollChanged(int l, int t, int oldl, int oldt, boolean onDragChanged);

        default void onTouchStart() {
        }

        default void onTouchEnd() {
        }
    }
}

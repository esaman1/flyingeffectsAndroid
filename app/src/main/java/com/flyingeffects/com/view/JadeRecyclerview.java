package com.flyingeffects.com.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

public class JadeRecyclerview extends RecyclerView {

    private ViewPager2 parentView;
    private int startY;
    private int startX;

    public JadeRecyclerview(@NonNull @NotNull Context context) {
        super(context);
    }

    public JadeRecyclerview(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public JadeRecyclerview(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) ev.getX();
                startY = (int) ev.getY();
                // 保证子View能够接收到Action_move事件
                if (getParentRecyclerview(JadeRecyclerview.this) != null) {
                    getParentRecyclerview(JadeRecyclerview.this).requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (getParentRecyclerview(JadeRecyclerview.this) != null) {
                    getParentRecyclerview(JadeRecyclerview.this).requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
//                isInter = false;
//                if (getParentRecyclerview(this) != null) {
//                    LogUtils.d("HomeVideoPlayer dispatchTouchEvent() called with: ev = [" + 4 + "]");
//                    getParentRecyclerview(this).requestDisallowInterceptTouchEvent(true);
//                }

                break;
        }
        return super.dispatchTouchEvent(ev);
    }


    public RecyclerView getParentRecyclerview(View view) {
        ViewParent parent = view.getParent();
        if (parent instanceof RecyclerView) {
            return (RecyclerView) parent;
        }
        return getParentRecyclerview((View) parent);
    }
}

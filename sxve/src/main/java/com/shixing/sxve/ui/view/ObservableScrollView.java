package com.shixing.sxve.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

import com.shixing.sxve.ui.ScrollViewListener;

import java.util.Timer;
import java.util.TimerTask;


/**
 * description 滑动事件监听
 * date: ：2019/5/17 11:36
 * author: 张同举 @邮箱 jutongzhang@sina.com
 */

public class ObservableScrollView extends HorizontalScrollView {
    private ScrollViewListener scrollViewListener = null;

    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }






    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(ObservableScrollView.this, x, y, oldx, oldy,isUser);
        }
    }



    boolean isUser=false;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN://0
                isUser=true;
                endTimer();
                break;
            case MotionEvent.ACTION_UP://1
                isUser=false;
                startTimer();

                break;
            case MotionEvent.ACTION_MOVE://2
                endTimer();
                isUser=true;
                break;
        }
        return super.onTouchEvent(ev);
    }






    /***
     * 开始自动翻页
     */
    Timer timer;
    TimerTask task;
    private void startTimer() {
            //启动新的timer之前都要确认是否关闭之前的
            endTimer();
            timer = new Timer();
            task = new TimerTask() {
                public void run() {
                    if (scrollViewListener != null&&!isUser) {
                        scrollViewListener.isCanPlay(true);
                    }
                }
            };
            timer.schedule(task, 1000);
        }



    /**
     * 关闭timer 和task
     */
    private void endTimer() {
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public void setScrollHorizontal(boolean scrollHorizontal) {
        this.scrollHorizontal = scrollHorizontal;
    }

    private boolean scrollHorizontal=true;
    @Override
    public boolean canScrollHorizontally(int direction) {
        return scrollHorizontal;
    }
}
package com.flyingeffects.com.manager;


import java.util.Timer;
import java.util.TimerTask;



/**
 * description ：通用的计时器
 * creation date: 2020/12/4
 * user : zhangtongju
 */
public class Calculagraph {

    private Callback callback;
    private Timer timer;
    private TimerTask task;
    private int maxRequestsTime;
    private int nowExecuteCount;



    /**
     * description ： second 几秒执行一次    maxRequests 最多能执行多少次  callback 执行回调
     * creation date: 2020/12/4
     * user : zhangtongju
     */
    public void startTimer(float second, int maxRequests, Callback callback) {
        this.maxRequestsTime = maxRequests;
        nowExecuteCount=0;
        this.callback = callback;
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                if (maxRequestsTime != 0) {
                    if (nowExecuteCount > maxRequests) {
                        if (callback != null) {
                            destroyTimer();
                            callback.isDone();
                        }
                    } else {
                        nowExecuteCount++;
                    }
                }
                if (callback != null) {
                    callback.isTimeUp();
                }
            }
        };
        timer.schedule(task, 0, (long) (second * 1000));
    }


    /**
     * description ： second 几秒执行一次    maxRequests 最多能执行多少次  callback 执行回调
     * creation date: 2020/12/4
     * user : zhangtongju
     */
    public void startTimer(float second, int delay,int maxRequests, Callback callback) {
        this.maxRequestsTime = maxRequests;
        nowExecuteCount=0;
        this.callback = callback;
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                if (maxRequestsTime != 0) {
                    if (nowExecuteCount > maxRequests) {
                        if (callback != null) {
                            destroyTimer();
                            callback.isDone();
                        }
                    } else {
                        nowExecuteCount++;
                    }
                }
                if (callback != null) {
                    callback.isTimeUp();
                }
            }
        };
        timer.schedule(task, delay, (long) (second * 1000));
    }

    public void destroyTimer() {
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (callback != null) {
            callback.isDone();
        }
    }


    public interface Callback {
        void isTimeUp();

        void isDone();
    }


}

package com.flyingeffects.com.manager;

import android.os.Looper;


/**
 * 线程区间判断
 */
public class ThreadJudgeManage {


    /**
     * description ：当前线程时候位于主线程
     * creation date: 2020/5/7
     * user : zhangtongju
     */
    public static  boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

}

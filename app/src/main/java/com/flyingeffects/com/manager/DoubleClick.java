package com.flyingeffects.com.manager;

public class DoubleClick {
    private static DoubleClick thisModel;

    public static DoubleClick getInstance() {

        if (thisModel == null) {
            thisModel = new DoubleClick();
        }
        return thisModel;

    }

    private long lastClickTime;

    /***
     *user: 张sir ,@time: 2017/8/14
     *description:判断事件出发时间间隔是否超过预定值,防重复点击
     */
    public boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }



}

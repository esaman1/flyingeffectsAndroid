package com.flyingeffects.com.commonlyModel;

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



    private long lastClickTime2;
    /***
     *user: 张sir ,@time: 2017/8/14
     *description:判断事件出发时间间隔是否超过预定值,防重复点击
     */
    public boolean isFastDoubleLongClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime2;
        if (0 < timeD && timeD < 3000) {
            return true;
        }
        lastClickTime2 = time;
        return false;
    }




    private long lastClickTime3;


    public boolean isFastDoubleLongClick(int intervalTime) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime3;
        if (0 < timeD && timeD < intervalTime) {
            return true;
        }
        lastClickTime3 = time;
        return false;
    }
}

package com.mobile.CloudMovie.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class screenUtil {


    /**
     * user :TongJu  ;描述：获得屏幕宽度
     * 时间：2018/6/20
     **/
    public static int getScreenWidth(Activity act) {
        DisplayMetrics m = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(m);
        android.graphics.Rect frame = new android.graphics.Rect();
        act.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return m.widthPixels;
    }

    /**
     * user :TongJu  ;描述：获得屏幕宽度
     * 时间：2018/6/20
     **/
    public static int getScreenHeight(Activity act) {
        DisplayMetrics m = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(m);
        android.graphics.Rect frame = new android.graphics.Rect();
        act.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return m.heightPixels;
    }



    /**
     * dip转px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px转dip
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
    * user :TongJu  ;描述：获得控件的高度
    * 时间：2018/7/10
    **/
    public static int getViewHeight( int padding, Context context,int width) {
        int height;
        int nowpadding = screenUtil.dip2px(context, padding);
        height = (width - nowpadding) * 9 / 16;
        return height;
    }
}

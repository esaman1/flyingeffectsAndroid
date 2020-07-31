package com.flyingeffects.com.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

import com.flyingeffects.com.base.BaseApplication;

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


    /**
     * description ：获得状态栏高度
     * creation date: 2020/7/31
     * user : zhangtongju
     */
    public static int getStatusBarHeight() {
        int statusBarHeight = getStatusBarByResId();
        if (statusBarHeight <= 0) {
            statusBarHeight = getStatusBarByReflex();
        }
        return statusBarHeight;
    }

    /**
     * 通过状态栏资源id来获取状态栏高度
     *
     * @return
     */
    private static int getStatusBarByResId() {
        int height = 0;
        //获取状态栏资源id
        int resourceId = BaseApplication.getInstance().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            try {
                height = BaseApplication.getInstance().getResources().getDimensionPixelSize(resourceId);
            } catch (Exception e) {
            }
        }
        return height;
    }


    /**
     * 通过反射获取状态栏高度
     *
     * @return
     */
    private static int getStatusBarByReflex() {
        int statusBarHeight = 0;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusBarHeight = BaseApplication.getInstance().getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }
}

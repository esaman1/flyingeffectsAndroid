package com.shixing.sxve.ui.model;


import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

/**
 * description ：进度条拖动
 * date: ：2019/5/16 18:40
 * author: 张同举 @邮箱 jutongzhang@sina.com
 */
public class PreviewProgressModel {


    public int getProgressPadding(Activity act, int marginDp) {
        int screenW = getScreenWidth(act);
        int marginLeft = dip2px(act, marginDp);
        return screenW / 2 - marginLeft;
    }


    /**
     * user :TongJu  ;描述：获得屏幕宽度
     * 时间：2018/6/20
     **/
    public int getScreenWidth(Activity act) {
        DisplayMetrics m = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(m);
        android.graphics.Rect frame = new android.graphics.Rect();
        act.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return m.widthPixels;
    }



    /**
     * description ：动态设置margin
     * date: ：2019/5/17 9:40
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    public  void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }



    /**
     * dip转px
     */
    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率PX
     * (像素)转成DP
     */
    public  int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }




}

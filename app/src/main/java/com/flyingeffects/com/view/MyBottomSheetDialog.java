package com.flyingeffects.com.view;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.bigkoo.convenientbanner.utils.ScreenUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.screenUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.nineton.ntadsdk.utils.ScreenUtils;

/**
 * 设置dialog 的总高度为固定的值
 * */
public class MyBottomSheetDialog extends BottomSheetDialog {
    public MyBottomSheetDialog(@NonNull Context context) {
        super(context);
    }

    public MyBottomSheetDialog(@NonNull Context context, int theme) {
        super(context, theme);
    }

    protected MyBottomSheetDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int screenHeight = getScreenHeight(getContext());

        if(screenHeight == 0){
            screenHeight = 1920;
        }

        Window window = getWindow();
        assert window != null;

        //设置成沉浸式
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, screenHeight);

        //需要设置最大高度 给dialog设置一个固定的高度
        //你想要减去的高度，dialog默认最大高度在状态栏下方
//        float reduceHeight = getContext().getResources().getDimension(R.dimen.qb_px_120);
        int reduceHeight = screenUtil.getStatusBarHeight();
        LogUtil.d("OOM","reduceHeight="+reduceHeight);
//        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (int) (dialogHeight - reduceHeight));
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (int) (screenHeight/3*2 - reduceHeight));
//        最后一步是必须的，否则BottomSheetDialog会显示在屏幕中间，底部会出现空白区域
        window.setGravity(Gravity.BOTTOM);
    }

    /**
     * 获取屏幕的高度
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        assert wm != null;
        wm.getDefaultDisplay().getSize(point);
        return point.y;
    }

}
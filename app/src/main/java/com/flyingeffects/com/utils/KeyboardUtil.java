package com.flyingeffects.com.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

/**
 * 作者:Administrator on 2018/10/30 15:24
 * 邮箱:jutongzhang@sina.com
 */
public class KeyboardUtil {
    /**
     * 展示输入法软键盘
     *
     * @param activity
     * @param currentFocusedView 当前获得焦点了的view
     */
    public static void showInputKeyboard(Activity activity, View currentFocusedView) {
        if (activity == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) activity.
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(currentFocusedView, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 隐藏输入法键盘
     *
     * @param activity
     */
    public static void closeInputKeyboard(Activity activity) {
        if (activity == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) activity.
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken()
                , InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public KeyboardUtil() {
    }

    private static KeyboardUtil sKeyboardUtil = new KeyboardUtil();

    public static KeyboardUtil getInstance() {
        return sKeyboardUtil;
    }

    private int lastheight = 0;

    public  void setListener(Activity activity, OnKeyboardListener listener) {
        setOnKeyboardListener(listener);
        //拿到页面的共同布局
        final View decorView = activity.getWindow().getDecorView();
        //设置最底层布局的变化监听
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //int measuredHeight = decorView.getMeasuredHeight();//拿尺寸不好用
                Rect rect = new Rect();
                //拿这个控件在屏幕上的可见区域
                decorView.getWindowVisibleDisplayFrame(rect);
                int height = rect.height();
                //第一次刚进来的时候,给上一次的可见高度赋一个初始值,
                // 然后不需要再做什么比较了,直接return即可
                if (lastheight == 0) {
                    lastheight = height;
                    return;
                }
                //当前这一次的可见高度比上一次的可见高度要小(有比较大的高度差,大于300了),
                // 认为是软键盘弹出
                if (lastheight - height > 300) {
                    //隐藏这个RoomFragment中的控件
                    if (mOnKeyboardListener != null) {
                        mOnKeyboardListener.onKeyboardShow(lastheight - height);
                    }
                }
                //当前这一次的可见高度比上一次的可见高度要大,认为是软键盘收缩
                if (height - lastheight > 300) {
                    if (mOnKeyboardListener != null) {
                        mOnKeyboardListener.onKeyboardHide(height - lastheight);
                    }
                }
                //记录下来
                lastheight = height;
            }
        });
    }


    private OnKeyboardListener mOnKeyboardListener;

    public void setOnKeyboardListener(OnKeyboardListener keyboardListener) {
        mOnKeyboardListener = keyboardListener;
    }

    public interface OnKeyboardListener {
        void onKeyboardShow(int i);

        void onKeyboardHide(int i);
    }


}

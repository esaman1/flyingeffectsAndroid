package com.flyingeffects.com.view;


import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;

import com.flyingeffects.com.utils.LogUtil;

/**
 * 输入法与view的文字传输
 *
 * @author vidya
 */
public class TextInputConnection extends BaseInputConnection {
    private static final String TAG = "TextInputConnection";
    private String mNowStr;
    private String mInputStr;
    private TextInputListener mInputListener;

    public TextInputConnection(View targetView, boolean fullEditor, TextInputListener inputListener) {
        super(targetView, fullEditor);
        mInputListener = inputListener;
        mNowStr = "输入字体";
        mInputStr = "";
    }

    @Override
    public boolean commitText(CharSequence text, int newCursorPosition) {
        //note:获取到输入的字符
        LogUtil.d(TAG, "commitText:" + text + "\t" + newCursorPosition);
        if (TextUtils.isEmpty(mNowStr)) {
            mNowStr = text.toString();
        } else {
            mInputStr = text.toString();
        }
        mInputListener.onInvalidate();
        return true;
    }

    //有文本输入，当然也有按键输入，也别注意的是有些输入法输入数字并非用commitText方法传递，而是用按键来代替，比如KeyCode_1是代表1等。note：这里我只做了删除，回车按键的处理，由于会触发动作按下和松开两次，所以在这里只做了按下的处理。
    @Override
    public boolean sendKeyEvent(KeyEvent event) {
        /** 当手指离开的按键的时候 */
        LogUtil.d(TAG, "sendKeyEvent:KeyCode=" + event.getKeyCode());
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                //删除按键
                if (mNowStr.length() > 0) {
                    mNowStr = mNowStr.substring(0, mNowStr.length() - 1);
                }
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                //回车按键
                mNowStr = mNowStr + "\n" + mInputStr;
            }
        }
        mInputListener.onInvalidate();
        return true;
    }

    //当然删除的时候也会触发
    @Override
    public boolean deleteSurroundingText(int beforeLength, int afterLength) {
        LogUtil.d(TAG, "deleteSurroundingText " + "beforeLength=" + beforeLength + " afterLength=" + afterLength);
        return true;
    }

    @Override
    public boolean finishComposingText() {
        //结束组合文本输入的时候，这个方法基本上会出现在切换输入法类型，点击回车（完成、搜索、发送、下一步）点击输入法右上角隐藏按钮会触发。
        LogUtil.d(TAG, "finishComposingText");
        return true;
    }

    public String getNowStr() {
        return mNowStr;
    }

    public String getInputStr() {
        return mInputStr;
    }

    public interface TextInputListener {
        void onInvalidate();
    }
}

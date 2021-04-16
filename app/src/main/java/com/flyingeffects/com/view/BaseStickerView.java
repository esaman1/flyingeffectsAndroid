package com.flyingeffects.com.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * 抽取框架操作的贴纸基类
 */
public class BaseStickerView extends View {

    public BaseStickerView(Context context) {
        super(context);
    }

    public BaseStickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseStickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BaseStickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}

package com.flyingeffects.com.view;


import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;
//com.flyingeffects.com.view.MarqueTextView
/**
 *自定义TextView 重写isFocused()函数，让他放回true也就是一直获取了
 *焦点效果自然也就出来了，如果这都不能解决那肯定就不是焦点问题了。
 *那就要找到问题，在想办法解决
 */
public class MarqueTextView extends AppCompatTextView {

    public MarqueTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MarqueTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueTextView(Context context) {
        super(context);
    }

    @Override

    public boolean isFocused() {
        return true;
    }
}
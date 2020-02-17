package com.mobile.flyingeffects.view;
//com.mobile.CloudMovie.view.MyGridview

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;


/**
* user :TongJu  ;描述： 不会滑动冲突的MyGridview
* 时间：2018/4/24
**/
public class MyGridview extends GridView {

    public MyGridview(Context context) {
        super(context);
    }

    public MyGridview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyGridview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
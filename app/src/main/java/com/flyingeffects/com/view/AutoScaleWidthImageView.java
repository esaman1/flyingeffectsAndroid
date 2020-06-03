package com.flyingeffects.com.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * 宽度自适应ImageView，宽度始终充满显示区域，高度成比例缩放
 */
public class AutoScaleWidthImageView extends AppCompatImageView {

    public AutoScaleWidthImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable drawable = getDrawable();
        if(drawable != null){
            int width = drawable.getMinimumWidth();
            int height = drawable.getMinimumHeight();
            float scale = (float)height/width;
            int widthMeasure = MeasureSpec.getSize(widthMeasureSpec);
            int heightMeasure = (int)(widthMeasure*scale);
            heightMeasureSpec =  MeasureSpec.makeMeasureSpec(heightMeasure, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}

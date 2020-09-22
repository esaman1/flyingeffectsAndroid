package com.flyingeffects.com.utils;

import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;

/**
 * 测量文字大小工具类
 * @author vidya
 */
public class MeasureTextUtils {
    /**
     * 返回文字宽度
     * @param paint paint
     * @param text text
     * @return 宽度float
     */
    public static float getFontWidth(Paint paint, String text) {
        return paint.measureText(text);
    }

    /**
     * @return 返回指定的文字高度
     */
    public static float getFontHeight(Paint paint) {
        FontMetrics fm = paint.getFontMetrics();
        //文字基准线的下部距离-文字基准线的上部距离 = 文字高度
        return fm.descent - fm.ascent;
    }

}

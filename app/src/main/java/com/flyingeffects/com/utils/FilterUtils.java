package com.flyingeffects.com.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.lansosdk.LanSongFilter.LanSongFilter;
import com.lansosdk.LanSongFilter.LanSongTwoInputFilter;
import com.lansosdk.box.BitmapLoader;

public class FilterUtils {

    public static LanSongFilter createBlendFilter(Context context,
                                                   Class<? extends LanSongTwoInputFilter> filterClass, Bitmap bitmap) {
        try {
            LanSongTwoInputFilter filter = filterClass.newInstance();
            //String var3 = "assets://LSResource/blend_demo.png"; //这里只是为了方便,用默认图片;
            filter.setBitmap(bitmap);
            return filter;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

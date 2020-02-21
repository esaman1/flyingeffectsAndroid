package com.shixing.sxve.ui.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.glidebitmappool.GlideBitmapFactory;
import com.glidebitmappool.GlideBitmapPool;
import com.glidebitmappool.internal.Util;

import java.io.FileDescriptor;

public class VEBitmapFactory extends GlideBitmapFactory {
    public static Bitmap decodeFileDescriptor(FileDescriptor fd, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);
        options.inSampleSize = Util.calculateInSampleSize(options, reqWidth, reqHeight);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            options.inMutable = true;
            Bitmap inBitmap = GlideBitmapPool.getBitmap(options.outWidth, options.outHeight, options.inPreferredConfig);
            if (inBitmap != null && Util.canUseForInBitmap(inBitmap, options)) {
                options.inBitmap = inBitmap;
            }
        }
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        try {
            return BitmapFactory.decodeFileDescriptor(fd, null, options);
        } catch (Exception e) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                options.inBitmap = null;
            }
            return BitmapFactory.decodeFileDescriptor(fd, null, options);
        }
    }
}

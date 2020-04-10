package com.flyingeffects.com.ui.model;

import android.graphics.Bitmap;

import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.faceUtil.ConUtil;
import com.megvii.segjni.SegJni;
import com.shixing.sxve.ui.util.PhotoBitmapUtils;


public class MattingImage {

    private int mBitmapH;
    private int mBitmapW;
    private Bitmap mOriginBitmap;

    public MattingImage() {
    }


    public void mattingImage(String path, mattingStatus callback) {
        new Thread(() -> {
            mOriginBitmap = ConUtil.getImage(path);
            mOriginBitmap = PhotoBitmapUtils.amendRotatePhoto(path, mOriginBitmap);
            if (mOriginBitmap != null) {
                mBitmapW = mOriginBitmap.getWidth();
                mBitmapH = mOriginBitmap.getHeight();
                SegJni.nativeCreateImageBuffer(mBitmapW, mBitmapH);
                byte segs[] = new byte[mBitmapH * mBitmapW];
                byte[] rgba = ConUtil.getPixelsRGBA(mOriginBitmap);
                SegJni.nativeSegImage(rgba, mBitmapW, mBitmapH, segs, false);
                SegJni.nativeReleaseImageBuffer();
                mOriginBitmap = ConUtil.setBitmapAlpha(mOriginBitmap, segs);
                callback.isSuccess(true, mOriginBitmap);
            }

        }).start();
    }


    /**
     * 单线程抠图
     */
    public void mattingImage(final Bitmap OriginBitmap, mattingStatus callback) {
        new Thread(() -> {
            mBitmapW = OriginBitmap.getWidth();
            mBitmapH = OriginBitmap.getHeight();
            SegJni.nativeCreateImageBuffer(mBitmapW, mBitmapH);
            byte segs[] = new byte[mBitmapH * mBitmapW];
            byte[] rgba = ConUtil.getPixelsRGBA(OriginBitmap);
            SegJni.nativeSegImage(rgba, mBitmapW, mBitmapH, segs, false);
            SegJni.nativeReleaseImageBuffer();
            Bitmap newBitmap = ConUtil.setBitmapAlpha(OriginBitmap, segs);
            callback.isSuccess(true, newBitmap);
        }).start();
    }


    /**
     * 多线程抠图g
     */

    private int BitmapW;
    private int BitmapH;
    private int bitmapWH[];

    public void mattingImageForMultiple(Bitmap OriginBitmap, int index, mattingStatus callback) {
        if (index == 1 && OriginBitmap != null) {
            BitmapW = OriginBitmap.getWidth();
            BitmapH = OriginBitmap.getHeight();
            SegJni.nativeCreateImageBuffer(BitmapW, BitmapH);
            bitmapWH = new int[2];
            bitmapWH[0] = BitmapW;
            bitmapWH[1] = BitmapH;
        }
        byte[] imageByte = SegJni.nativeSegCamera(getYUVByBitmap(OriginBitmap), BitmapW, BitmapH, 0, 0, 0, bitmapWH);
        if (imageByte != null) {
            Bitmap newBitmap = ConUtil.setBitmapAlpha(OriginBitmap, imageByte);
            callback.isSuccess(true, newBitmap);
        } else {
            callback.isSuccess(false, OriginBitmap);
            LogUtil.d("oom", "IMAGEBYTE==NULL");
        }
    }


    public interface mattingStatus {
        void isSuccess(boolean isSuccess, Bitmap bp);
    }


    public interface mattingStatusForMultiple {
        void isSuccess(boolean isSuccess, byte[] bytes);
    }


    /*
     * 获取位图的YUV数据
     */
    public static byte[] getYUVByBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int size = width * height;

        int pixels[] = new int[size];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        // byte[] data = convertColorToByte(pixels);
        byte[] data = rgb2YCbCr420(pixels, width, height);

        return data;
    }

    public static byte[] rgb2YCbCr420(int[] pixels, int width, int height) {
        int len = width * height;
        // yuv格式数组大小，y亮度占len长度，u,v各占len/4长度。
        byte[] yuv = new byte[len * 3 / 2];
        int y, u, v;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                // 屏蔽ARGB的透明度值
                int rgb = pixels[i * width + j] & 0x00FFFFFF;
                // 像素的颜色顺序为bgr，移位运算。
                int r = rgb & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = (rgb >> 16) & 0xFF;
                // 套用公式
                y = ((66 * r + 129 * g + 25 * b + 128) >> 8) + 16;
                u = ((-38 * r - 74 * g + 112 * b + 128) >> 8) + 128;
                v = ((112 * r - 94 * g - 18 * b + 128) >> 8) + 128;
                // rgb2yuv
                // y = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                // u = (int) (-0.147 * r - 0.289 * g + 0.437 * b);
                // v = (int) (0.615 * r - 0.515 * g - 0.1 * b);
                // RGB转换YCbCr
                // y = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                // u = (int) (-0.1687 * r - 0.3313 * g + 0.5 * b + 128);
                // if (u > 255)
                // u = 255;
                // v = (int) (0.5 * r - 0.4187 * g - 0.0813 * b + 128);
                // if (v > 255)
                // v = 255;
                // 调整
                y = y < 16 ? 16 : (y > 255 ? 255 : y);
                u = u < 0 ? 0 : (u > 255 ? 255 : u);
                v = v < 0 ? 0 : (v > 255 ? 255 : v);
                // 赋值
                yuv[i * width + j] = (byte) y;
                yuv[len + (i >> 1) * width + (j & ~1) + 0] = (byte) u;
                yuv[len + +(i >> 1) * width + (j & ~1) + 1] = (byte) v;
            }
        }
        return yuv;
    }

}

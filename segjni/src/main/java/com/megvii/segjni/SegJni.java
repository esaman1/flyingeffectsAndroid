package com.megvii.segjni;

import android.content.Context;
import android.graphics.Bitmap;

public class SegJni {
    static {
        System.loadLibrary("MegviiSegJni");
    }

    /**
     *
     * @param context
     * @param segmentModel 模型的二进制数据
     */
    public static native int nativeCreateSegHandler(Context context, byte[] segmentModel, int threadCount);

    // return seg result in orignSize (maybe include the matting functions)

    /**
     *
     * @param data  原始数据
     * @param width
     * @param height
     * @param output 返回数据 由外部分配
     * @return
     */
    public static native int nativeSegImage(byte[] data, int width, int height, byte[] output,boolean segment_flag);

    // return seg result in segSize

    /**
     *
     * @param data  yuv数据
     * @param width
     * @param height
     * @param segSize 缩图后的宽高
     * @return 缩图后的处理结果
     */

    public static native byte[] nativeSegCamera(byte[] data, int width, int height,int rotate,int radius, int fps, int []segSize);

    /**
     *
     * 释放算法句柄
     */

    public static native int nativeReleaseSegHandler();

    /**
     *
     * 原图和mask进行rgb合成
     */
    public static native int nativeBlendImage(Bitmap srcImg, byte[] alpha);

    /**
     *
     * 创建图像缓存
     */
    public static native int nativeCreateImageBuffer(int width, int height);
    /**
     *
     * 释放图像缓存
     */
    public static native int nativeReleaseImageBuffer();

    /**
     *
     * 获取版本号
     */
    public static native String nativeGetversion();
}

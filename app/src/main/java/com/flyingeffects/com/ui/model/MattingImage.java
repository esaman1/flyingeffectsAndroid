package com.flyingeffects.com.ui.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;

import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.faceUtil.ConUtil;
import com.flyingeffects.com.utils.faceUtil.SegResultHandleUtils;
import com.megvii.facepp.multi.sdk.BodySegmentApi;
import com.megvii.facepp.multi.sdk.FacePPImage;
import com.megvii.facepp.multi.sdk.utils.ImageTransformUtils;
import com.shixing.sxve.ui.util.PhotoBitmapUtils;
import com.shixing.sxve.ui.view.WaitingDialog;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class MattingImage {


    private Bitmap mOriginBitmap;


    public MattingImage() {
    }


    public void createHandle(Context context, InitSegJniStateCallback callback) {
        if (!BaseConstans.hasCreatingSegJni) {
            new Handler().postDelayed(() -> {
                WaitingDialog.openPragressDialog(context, "正在上传中...");
            }, 200);
        } else {
            WaitingDialog.closePragressDialog();
            callback.isDone(true);
        }
    }


    public interface InitSegJniStateCallback {

        void isDone(boolean isDone);
    }


    private Timer timer;
    private TimerTask task;



    private void updateDuration(float duration, Context context) {
        LogUtil.d("OOM","initMattingDuration="+duration);
        if (duration <= 10000) {
            statisticsEventAffair.getInstance().setFlag(context, "initMattingDuration", "小于10秒");
        } else if (duration <= 20000) {
            statisticsEventAffair.getInstance().setFlag(context, "initMattingDuration", "小于20秒");
        } else if (duration <= 30000) {
            statisticsEventAffair.getInstance().setFlag(context, "initMattingDuration", "小于30秒");
        } else if (duration <= 40000) {
            statisticsEventAffair.getInstance().setFlag(context, "initMattingDuration", "小于40秒");
        } else if (duration <= 50000) {
            statisticsEventAffair.getInstance().setFlag(context, "initMattingDuration", "小于50秒");
        } else if (duration <= 60000) {
            statisticsEventAffair.getInstance().setFlag(context, "initMattingDuration", "小于60秒");
        } else {
            statisticsEventAffair.getInstance().setFlag(context, "initMattingDuration", "大于1分钟");
        }
    }


    /**
     * 关闭timer 和task
     */
    private void endTimer() {
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }

    }



    /**
     * description ：普通抠图
     * creation date: 2020/10/20
     * user : zhangtongju
     */
    public void mattingImage(String path, mattingStatus callback) {
        mOriginBitmap = ConUtil.getImage(path);
        mOriginBitmap = PhotoBitmapUtils.amendRotatePhoto(path, mOriginBitmap);
        byte[] imageBgr = ImageTransformUtils.bitmap2BGR(mOriginBitmap);
        FacePPImage facePPImage = new FacePPImage.Builder()
                .setData(imageBgr)
                .setWidth(mOriginBitmap.getWidth())
                .setHeight(mOriginBitmap.getHeight())
                .setMode(FacePPImage.IMAGE_MODE_BGR)
                .setRotation(FacePPImage.FACE_UP).build();
        float[] alpha = BodySegmentApi.getInstance().bodySegment(facePPImage);//抠像
        mOriginBitmap= SegResultHandleUtils.setBitmapAlpha(mOriginBitmap, alpha);
        callback.isSuccess(true, mOriginBitmap);
    }




    /**
     * description ：自己写的自定义抠图
     * creation date: 2020/7/13
     * user : zhangtongju
     */
    public static   Bitmap mattingSingleImg(Bitmap bitmap) {
        byte[] imageBgr = ImageTransformUtils.bitmap2BGR(bitmap);
        FacePPImage facePPImage = new FacePPImage.Builder()
                .setData(imageBgr)
                .setWidth(bitmap.getWidth())
                .setHeight(bitmap.getHeight())
                .setMode(FacePPImage.IMAGE_MODE_BGR)
                .setRotation(FacePPImage.FACE_UP).build();
        float[] alpha = BodySegmentApi.getInstance().bodySegment(facePPImage);//抠像
        return SegResultHandleUtils.setBitmapAlpha(bitmap, alpha);
    }


    public static   Bitmap mattingSingleImg(Bitmap bitmap,int width,int height) {
        byte[] imageBgr = ImageTransformUtils.bitmap2BGR(bitmap);
        FacePPImage facePPImage = new FacePPImage.Builder()
                .setData(imageBgr)
                .setWidth(width)
                .setHeight(height)
                .setMode(FacePPImage.IMAGE_MODE_BGR)
                .setRotation(FacePPImage.FACE_UP).build();
        float[] alpha = BodySegmentApi.getInstance().bodySegment(facePPImage);//抠像
        return SegResultHandleUtils.setBitmapAlpha(bitmap, alpha);
    }







    /**
     * description ：返回黑白抠像
     * creation date: 2020/12/30
     * user : zhangtongju
     */
    public void mattingImageForMultiple(Bitmap OriginBitmap, mattingStatus callback) {
        LogUtil.d("OOM4","开始抠图"+System.currentTimeMillis());
        byte[] imageBgr = ImageTransformUtils.bitmap2BGR(OriginBitmap);
        FacePPImage facePPImage = new FacePPImage.Builder()
                .setData(imageBgr)
                .setWidth(OriginBitmap.getWidth())
                .setHeight(OriginBitmap.getHeight())
                .setMode(FacePPImage.IMAGE_MODE_BGR)
                .setRotation(FacePPImage.FACE_UP).build();
        float[] alpha = BodySegmentApi.getInstance().bodySegment(facePPImage);//抠像
        OriginBitmap=  SegResultHandleUtils.setBlackWhite(OriginBitmap, alpha);
        callback.isSuccess(true, OriginBitmap);
        LogUtil.d("OOM4","结束抠图"+System.currentTimeMillis());
    }



    /**
     * description ：返回正常抠像
     * creation date: 2020/12/30
     * user : zhangtongju
     */
    public void mattingImageForMultiple2(Bitmap OriginBitmap, mattingStatus callback) {
        LogUtil.d("OOM4","开始抠图"+System.currentTimeMillis());
        byte[] imageBgr = ImageTransformUtils.bitmap2BGR(OriginBitmap);
        FacePPImage facePPImage = new FacePPImage.Builder()
                .setData(imageBgr)
                .setWidth(OriginBitmap.getWidth())
                .setHeight(OriginBitmap.getHeight())
                .setMode(FacePPImage.IMAGE_MODE_BGR)
                .setRotation(FacePPImage.FACE_UP).build();
        float[] alpha = BodySegmentApi.getInstance().bodySegment(facePPImage);//抠像
        SegResultHandleUtils.setBitmapAlpha(OriginBitmap, alpha);
        callback.isSuccess(true, OriginBitmap);
        LogUtil.d("OOM4","结束抠图"+System.currentTimeMillis());
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

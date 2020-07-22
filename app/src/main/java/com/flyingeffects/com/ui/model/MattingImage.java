package com.flyingeffects.com.ui.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.flyingeffects.com.R;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.manager.SegResultHandleManage;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.view.activity.HomeMainActivity;
import com.flyingeffects.com.ui.view.activity.PreviewUpAndDownActivity;
import com.flyingeffects.com.utils.DateUtils;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.faceUtil.ConUtil;
import com.megvii.segjni.SegJni;
import com.shixing.sxve.ui.util.PhotoBitmapUtils;
import com.shixing.sxve.ui.view.WaitingDialog;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class MattingImage {

    private int mBitmapH;
    private int mBitmapW;
    private Bitmap mOriginBitmap;
    private InitSegJniStateCallback callback;

    private float nowNeedDuration;

    public MattingImage() {
    }


    public void createHandle(Context context, InitSegJniStateCallback callback) {
        if (!BaseConstans.hasCreatingSegJni) {
            this.callback = callback;
            new Handler().postDelayed(() -> {
                WaitingDialog.openPragressDialog(context, "正在上传中...");
            }, 200);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    nowNeedDuration = 1;
                    startTimer(context);
                    LogUtil.d("OOM", "开始加载模型------------------------------------------- " + DateUtils.getCurrentTime_m(System.currentTimeMillis()));
                    int aa = SegJni.nativeCreateSegHandler(context, ConUtil.getFileContent(context, R.raw.megviisegment_model), BaseConstans.THREADCOUNT);
                    LogUtil.d("OOM", "模型加载完成--------------------------------------------" + aa + DateUtils.getCurrentTime_m(System.currentTimeMillis()));
                    BaseConstans.hasCreatingSegJni = true;
                    WaitingDialog.closePragressDialog();
                }
            }).start();

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

    /***
     * 倒计时60s
     */
    private void startTimer(Context context) {

        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        timer = new Timer();
        task = new TimerTask() {
            public void run() {
                if (BaseConstans.hasCreatingSegJni) {
                    updateDuration(nowNeedDuration * 500, context);
                    endTimer();
                    WaitingDialog.closePragressDialog();
                    callback.isDone(true);
                }else{
                    nowNeedDuration++;
                }
            }
        };
        timer.schedule(task, 0, 500);
    }


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

    public void mattingImageForBitmap(Bitmap mOriginBitmap, mattingStatus callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mBitmapW = mOriginBitmap.getWidth();
                mBitmapH = mOriginBitmap.getHeight();
                SegJni.nativeCreateImageBuffer(mBitmapW, mBitmapH);
                byte segs[] = new byte[mBitmapH * mBitmapW];
                byte[] rgba = ConUtil.getPixelsRGBA(mOriginBitmap);
                SegJni.nativeSegImage(rgba, mBitmapW, mBitmapH, segs, false);
                SegJni.nativeReleaseImageBuffer();
                callback.isSuccess(true, ConUtil.setBitmapAlpha(mOriginBitmap, segs));
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


    private byte TestSegs[];

    public byte[] test(int index, int mBitmapW, int mBitmapH, byte[] rgba) {
        if (index == 0) {
            SegJni.nativeCreateImageBuffer(mBitmapW, mBitmapH);
            TestSegs = new byte[mBitmapH * mBitmapW];
        }
        SegJni.nativeSegImage(rgba, mBitmapW, mBitmapH, TestSegs, false);
        return TestSegs;
    }


    /**
     * 多线程抠图，這樣做的目的有 ，正常情况下，如果线程开启第4的时候，那么4个的时候返回第一个的数据，
     * 5,2 如果是8的时候，那么8返回第一个数据，这里的多线程抠图扣得是灰度图
     */

    private int BitmapW;
    private int BitmapH;
    private int bitmapWH[];
    private ArrayList<Bitmap> bpList = new ArrayList<>();

    public void mattingImageForMultiple(Bitmap OriginBitmap, int index, mattingStatus callback) {
        if (bpList.size() >= BaseConstans.THREADCOUNT) {
            bpList.remove(0);
            LogUtil.d("mattingImage", "队列只有" + bpList.size());
        }
        bpList.add(OriginBitmap);
        if (index == 1) {
            BitmapW = OriginBitmap.getWidth();
            BitmapH = OriginBitmap.getHeight();
            SegJni.nativeCreateImageBuffer(BitmapW, BitmapH);
            bitmapWH = new int[2];
            bitmapWH[0] = BitmapW;
            bitmapWH[1] = BitmapH;
        }

        byte[] imageByte;
        if (OriginBitmap != null) {
            LogUtil.d("mattingImage", "渲染图片地址为index=" + OriginBitmap);
            imageByte = SegJni.nativeSegCamera(getYUVByBitmap(OriginBitmap), BitmapW, BitmapH, 0, 0, 0, bitmapWH);
        } else {
            imageByte = SegJni.nativeSegCamera(getYUVByBitmap(bpList.get(0)), BitmapW, BitmapH, 0, 0, 0, bitmapWH);
        }

        if (index >= BaseConstans.THREADCOUNT - 1) {
            if (imageByte != null) {
                Bitmap newBitmap = SegResultHandleManage.setBlackWhite(bpList.get(0), imageByte);//setBlackWhite
                LogUtil.d("mattingImage", "接受源图片地址" + bpList.get(0));
                callback.isSuccess(true, newBitmap);
            } else {
                LogUtil.d("mattingImage", "不接受");
                callback.isSuccess(false, OriginBitmap);
                LogUtil.d("oom", "IMAGEBYTE==NULL");
            }
        } else {
            LogUtil.d("mattingImage", "不接受");
        }

    }


    /**
     * description ：爱字幕的单线抠图
     * creation date: 2020/7/13
     * user : zhangtongju
     */
    public static final Bitmap mattingSingleImg(Bitmap bitmap) {
        int mBitmapW = bitmap.getWidth();
        int mBitmapH = bitmap.getHeight();
        SegJni.nativeCreateImageBuffer(mBitmapW, mBitmapH);

        byte segs[] = new byte[mBitmapH * mBitmapW];
        byte[] rgba = ConUtil.getPixelsRGBA(bitmap);

        SegJni.nativeSegImage(rgba, mBitmapW, mBitmapH, segs, false);
        SegJni.nativeReleaseImageBuffer();
        return SegResultHandleManage.setBitmapAlpha(bitmap, segs);
    }


    public void mattingImageForMultipleForLucency(Bitmap OriginBitmap, int index, mattingStatus callback) {
        if (bpList.size() >= BaseConstans.THREADCOUNT) {
            bpList.remove(0);
            LogUtil.d("mattingImage", "队列只有" + bpList.size());
        }
        bpList.add(OriginBitmap);
        if (index == 1) {
            BitmapW = OriginBitmap.getWidth();
            BitmapH = OriginBitmap.getHeight();
            SegJni.nativeCreateImageBuffer(BitmapW, BitmapH);
            bitmapWH = new int[2];
            bitmapWH[0] = BitmapW;
            bitmapWH[1] = BitmapH;
        }

        byte[] imageByte;
        if (OriginBitmap != null) {
            LogUtil.d("mattingImage", "渲染图片地址为index=" + OriginBitmap);
            imageByte = SegJni.nativeSegCamera(getYUVByBitmap(OriginBitmap), BitmapW, BitmapH, 0, 0, 0, bitmapWH);
        } else {
            imageByte = SegJni.nativeSegCamera(getYUVByBitmap(bpList.get(0)), BitmapW, BitmapH, 0, 0, 0, bitmapWH);
        }

        if (index >= BaseConstans.THREADCOUNT - 1) {
            if (imageByte != null) {
                Bitmap newBitmap = SegResultHandleManage.setBitmapAlpha(bpList.get(0), imageByte);//setBlackWhite
                LogUtil.d("mattingImage", "接收源图片地址" + bpList.get(0));
                callback.isSuccess(true, newBitmap);
            } else {
                LogUtil.d("mattingImage", "不接收");
                callback.isSuccess(false, OriginBitmap);
                LogUtil.d("oom", "IMAGEBYTE==NULL");
            }
        } else {
            LogUtil.d("mattingImage", "不接受");
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

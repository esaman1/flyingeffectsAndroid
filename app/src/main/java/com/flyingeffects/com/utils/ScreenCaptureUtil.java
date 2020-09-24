package com.flyingeffects.com.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.View;

import com.flyingeffects.com.manager.BitmapManager;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.view.StickerView;
import com.opos.mobad.contentad.proto.Mat;

import java.io.File;
import java.util.ArrayList;

public class ScreenCaptureUtil {
    private static final String TAG = "ScreenCaptureUtil";
    private String mTextFolder;

    public ScreenCaptureUtil(Context context) {
        FileManager fileManager = new FileManager();
        mTextFolder = fileManager.getFileCachePath(context, "TextFolder");
    }

    public Bitmap getImagePath(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        return view.getDrawingCache();
    }

    public String getFilePath(StickerView view) {
        String path = mTextFolder + File.separator + System.currentTimeMillis() + ".png";
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bp = view.getDrawingCache();
        //Bitmap bp = loadBitmapFromView(view);
        //BitmapManager.getInstance().saveBitmapToPath(bp, path);
        //float scale = view.getScale();
        int top;
        int right;
        int left;
        int bottom;
        top = (int) (view.getMBoxTop());
        right = (int) (view.getMBoxRight());
        left = (int) (view.getMBoxLeft());
        bottom = (int) (view.getMBoxBottom());

//        if (scale < 1) {
//            top = (int) (view.getMBoxTop() + view.getMBoxTop() * (1 - scale));
//            right = (int) (view.getMBoxRight() - view.getMBoxRight() * (1 - scale));
//            left = (int) (view.getMBoxLeft() + view.getMBoxLeft() * (1 - scale));
//            bottom = (int) (view.getMBoxBottom() - view.getMBoxBottom() * (1 - scale));
//        } else if (scale > 1) {
//            top = (int) (view.getMBoxTop() - view.getMBoxTop() * scale);
//            right = (int) (view.getMBoxRight() + view.getMBoxRight() * scale);
//            left = (int) (view.getMBoxLeft() - view.getMBoxLeft() * scale);
//            bottom = (int) (view.getMBoxBottom() + view.getMBoxBottom() * scale);
//        } else {
//            top = (int) (view.getMBoxTop());
//            right = (int) (view.getMBoxRight());
//            left = (int) (view.getMBoxLeft());
//            bottom = (int) (view.getMBoxBottom());
//        }
        //需要逆时针
//        float angle = 360 - view.getRotateAngle();
//        int cx = (int) view.getCenterX();
//        int cY = (int) view.getCenterY();
//        int[] a1 = test(left, top, cx, cY, angle);
//        int[] a2 = test(left, bottom, cx, cY, angle);
//        int[] a3 = test(right, bottom, cx, cY, angle);
//        int[] a4 = test(right, top, cx, cY, angle);
//        ArrayList<int[]> list = new ArrayList<>();
//        list.add(a1);
//        list.add(a2);
//        list.add(a3);
//        list.add(a4);
//        getMineWNum(list);
//        getMaxWNum(list);
//        getMinHNum(list);
//        getMaxHNum(list);
        if (bp != null) {
            LogUtil.d(TAG, "box left = " + left);
            LogUtil.d(TAG, "box right = " + right);
            LogUtil.d(TAG, "box top = " + top);
            LogUtil.d(TAG, "box bottom = " + bottom);
            LogUtil.d(TAG, "bitmap height = " + bp.getHeight());
            LogUtil.d(TAG, "bitmap width = " + bp.getWidth());

            //获取需要截图部分的在屏幕上的坐标(view的左上角坐标）
            //int[] viewLocationArray = {minLeft, minTop};
//            int[] viewLocationArray = {left, 0};
//            view.getLocationOnScreen(viewLocationArray);
            int textLength = right - left;
            int textHeight = bottom - top;

            int bpWidth = Math.max(textLength, textHeight);
            int bpHeight = Math.max(textLength, textHeight);
            int bpTop = (int) (view.getMBoxCenterY() - bpWidth / 2);
            LogUtil.d(TAG, "textLength = " + textLength);
            LogUtil.d(TAG, "textHeight = " + textHeight);
            LogUtil.d(TAG, "bpWidth = " + bpWidth);
            LogUtil.d(TAG, "bpHeight = " + bpHeight);
            LogUtil.d(TAG, "bpTop = " + bpTop);
            if (left < 0) {
                left = 0;
            }
            if ((bpWidth + left) > bp.getWidth()) {
                if (left > 0) {
                    bpWidth = bp.getWidth() - left;
                } else {
                    bpWidth = bp.getWidth() + left;
                }
            } else if (bpWidth >= bp.getWidth()) {
                if (left > 0) {
                    bpWidth = bp.getWidth() - left;
                } else {
                    bpWidth = bp.getWidth() + left;
                }
            }

            if (bpTop < 0) {
                bpTop = 0;
            }
            if ((bpTop + bpHeight) >= bp.getHeight()) {
                bpHeight = bp.getHeight() - bpTop;
            }

            //从屏幕整张图片中截取指定区域
            Bitmap bp2 = Bitmap.createBitmap(bp, left, bpTop, bpWidth, bpHeight);

            if (bp2 != null) {
                LogUtil.d(TAG, "bp2 height = " + bp2.getHeight());
                LogUtil.d(TAG, "bp2 width = " + bp2.getWidth());
                BitmapManager.getInstance().saveBitmapToPath(bp2, path);
            }
        }
//        if (bp != null && !bp.isRecycled()) {
//            bp.recycle();
//        }
        view.setDrawingCacheEnabled(false);
        return path;
    }

    public String a(StickerView view) {
        String path = mTextFolder + File.separator + System.currentTimeMillis() + ".png";
        Bitmap bp = loadBitmapFromView(view);
        BitmapManager.getInstance().saveBitmapToPath(bp, path);
        return path;
    }

    private Bitmap loadBitmapFromView(StickerView view) {
        int w = view.getWidth();
        int h = view.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        view.draw(c);
//        Matrix matrix = new Matrix();
//        matrix.postRotate(360 - view.getRotateAngle());
//        Bitmap bp2 = Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, false);
        //c.drawBitmap(bp2, new Rect(0, 0, w, h),new Rect(0, 0,w, h), null);
        //view.layout(0, 0, w, h);

        return bmp;
    }

    int minLeft;

    private void getMineWNum(ArrayList<int[]> list) {
        for (int i = 0; i < list.size(); i++) {
            int[] a = list.get(i);
            int x = a[0];
            if (i == 0) {
                minLeft = x;
            }
            if (minLeft > x) {
                minLeft = x;
            }
        }
    }


    int MaxTop;

    private void getMaxHNum(ArrayList<int[]> list) {
        for (int i = 0; i < list.size(); i++) {
            int[] a = list.get(i);
            int x = a[1];
            if (i == 0) {
                MaxTop = x;
            }
            if (MaxTop < x) {
                MaxTop = x;
            }
        }
    }

    int minTop;

    private void getMinHNum(ArrayList<int[]> list) {
        for (int i = 0; i < list.size(); i++) {
            int[] a = list.get(i);
            int x = a[1];
            if (i == 0) {
                minTop = x;
            }
            if (minTop > x) {
                minTop = x;
            }
        }
    }

    int MaxLeft;

    private void getMaxWNum(ArrayList<int[]> list) {
        for (int i = 0; i < list.size(); i++) {
            int[] a = list.get(i);
            int x = a[0];
            if (i == 0) {
                MaxLeft = x;
            }
            if (MaxLeft < x) {
                MaxLeft = x;
            }
        }
    }

    /**
     * description ：計算位置x  计算位置y
     * creation date: 2020/9/23
     * param :
     * user : zhangtongju
     */
    private int[] test(int x, int y, int rx0, int ry0, double a) {
        int newX = (int) ((x - rx0) * Math.cos(a) - (y - ry0) * Math.sin(a) + rx0);
        int newY = (int) ((x - rx0) * Math.sin(a) + (y - ry0) * Math.cos(a) + ry0);
        int[] aa = {newX, newY};
        return aa;
    }


}

package com.flyingeffects.com.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Canvas;
import android.graphics.Color;
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

    public Bitmap GetImagePath(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        return view.getDrawingCache();
    }

    public String GetFilePath(StickerView view) {
        String path = mTextFolder + File.separator + System.currentTimeMillis() + ".png";
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bp = view.getDrawingCache();
        BitmapManager.getInstance().saveBitmapToPath(bp, path);
        int Top = (int) view.getMBoxTop();
        int Right = (int) view.getMBoxRight();
        int Left = (int) view.getMBoxLeft();
        int Bottom = (int) view.getMBoxBottom();
        float angle = view.getRotateAngle();
        int cx = (int) view.getCenterX();
        int cY = (int) view.getCenterY();
        int[] a1 = test(Left, Top, cx, cY, angle);
        int[] a2 = test(Left, Bottom, cx, cY, angle);
        int[] a3 = test(Right, Bottom, cx, cY, angle);
        int[] a4 = test(Right, Top, cx, cY, angle);
        ArrayList<int[]> list = new ArrayList<>();
        list.add(a1);
        list.add(a2);
        list.add(a3);
        list.add(a4);
        getMineWNum(list);
        getMaxWNum(list);
        getMinHNum(list);
        getMaxHNum(list);

        LogUtil.d("OOM5","minLeft="+minLeft+"minTop="+minTop+"MaxTop="+MaxTop+"MaxLeft="+MaxLeft);

        if (bp != null) {
            //获取需要截图部分的在屏幕上的坐标(view的左上角坐标）
            int[] viewLocationArray = {minLeft, minTop};
            view.getLocationOnScreen(viewLocationArray);
            //从屏幕整张图片中截取指定区域
            Bitmap bp2 = Bitmap.createBitmap(bp, minLeft, minTop, MaxLeft-minLeft, MaxTop-minTop);
            if (bp2 != null) {
                BitmapManager.getInstance().saveBitmapToPath(bp2, path);
            }
        }
        view.setDrawingCacheEnabled(false);
        return path;
    }

    private Bitmap loadBitmapFromView(View v) {
        int w = v.getWidth();
        int h = v.getHeight();
        LogUtil.d(TAG, "height = " + h);
        LogUtil.d(TAG, "width = " + w);
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        v.layout(0, 0, w, h);
        v.draw(c);
        return bmp;
    }




    int minLeft;
    private void getMineWNum(ArrayList<int[]> list) {
        for(int i=0;i<list.size();i++){
            int[] a=list.get(i);
            int x=a[0];
            if(i==0){
                minLeft=x;
            }
            if(minLeft>x){
                minLeft=x;
            }
        }
    }


    int MaxTop;
    private void getMaxHNum(ArrayList<int[]> list) {
        for(int i=0;i<list.size();i++){
            int[] a=list.get(i);
            int x=a[1];
            if(i==0){
                MaxTop=x;
            }
            if(MaxTop<x){
                MaxTop=x;
            }
        }
    }

    int minTop;

    private void getMinHNum(ArrayList<int[]> list) {
        for(int i=0;i<list.size();i++){
            int[] a=list.get(i);
            int x=a[1];
            if(i==0){
                minTop=x;
            }
            if(minTop>x){
                minTop=x;
            }
        }
    }


    int MaxLeft;
    private void getMaxWNum(ArrayList<int[]> list) {
        for(int i=0;i<list.size();i++){
            int[] a=list.get(i);
            int x=a[0];
            if(i==0){
                MaxLeft=x;
            }
            if(MaxLeft<x){
                MaxLeft=x;
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

package com.flyingeffects.com.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.flyingeffects.com.manager.BitmapManager;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.view.StickerView;

import java.io.File;

public class ScreenCaptureUtil {

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
        int width = (int) view.getmHelpBoxRectW();
        int height = (int) view.getmHelpBoxRectH();
        int X = (int) view.getMBoxCenterX()-width/2;
        int Y = (int) view.getMBoxCenterY()-height/2;
        if (bp != null) {
            //获取需要截图部分的在屏幕上的坐标(view的左上角坐标）
            int[] viewLocationArray = {X, Y};
            view.getLocationOnScreen(viewLocationArray);
            //从屏幕整张图片中截取指定区域
            Bitmap bp2 = Bitmap.createBitmap(bp, X, Y, width, height);
            if (bp2 != null) {
                BitmapManager.getInstance().saveBitmapToPath(bp2, path);
            }
        }
        view.setDrawingCacheEnabled(false);
        return path;

    }


}

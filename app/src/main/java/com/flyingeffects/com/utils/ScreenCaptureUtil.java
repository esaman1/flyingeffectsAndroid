package com.flyingeffects.com.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.flyingeffects.com.manager.BitmapManager;
import com.flyingeffects.com.manager.FileManager;

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


    public String GetFilePath(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bp = view.getDrawingCache();
        String path=mTextFolder+File.separator + System.currentTimeMillis() + ".png";
        BitmapManager.getInstance().saveBitmapToPath(bp, path);
        return path;

    }


}

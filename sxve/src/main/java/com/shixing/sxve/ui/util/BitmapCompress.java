package com.shixing.sxve.ui.util;

import android.content.Context;
import android.graphics.Bitmap;

import com.shixing.sxve.ui.view.VEBitmapFactory;

import java.io.File;
import java.io.FileInputStream;

public class BitmapCompress {




    public Bitmap getSmallBmpFromFile(String filePath, int targetW, int targetH) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(filePath);
                return VEBitmapFactory.decodeFileDescriptor(fis.getFD(), targetW, targetH);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

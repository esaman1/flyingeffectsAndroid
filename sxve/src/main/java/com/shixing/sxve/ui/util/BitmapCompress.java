package com.shixing.sxve.ui.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.File;
import java.io.FileInputStream;

public class BitmapCompress {




//    public Bitmap getSmallBmpFromFile(String filePath, int targetW, int targetH) {
//        try {
//            File file = new File(filePath);
//            if (file.exists()) {
//                FileInputStream fis = new FileInputStream(filePath);
//                return VEBitmapFactory.decodeFileDescriptor(fis.getFD(), targetW, targetH);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }



    public static Bitmap getZoomImage(String path, double newWidth, double newHeight) {
        Bitmap orgBitmap=BitmapFactory.decodeFile(path);
        if (null == orgBitmap) {
            return null;
        }
        if (orgBitmap.isRecycled()) {
            return null;
        }
        if (newWidth <= 0 || newHeight <= 0) {
            return null;
        }

        // 获取图片的宽和高
        float width = orgBitmap.getWidth();
        float height = orgBitmap.getHeight();
        // 创建操作图片的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        if(scaleWidth>scaleHeight){
            matrix.postScale(scaleHeight, scaleHeight);
        }else{
            matrix.postScale(scaleWidth, scaleWidth);
        }
        Bitmap bitmap = Bitmap.createBitmap(orgBitmap, 0, 0, (int) width, (int) height, matrix, true);
        return bitmap;
    }


    // 缩放图片
    public static Bitmap zoomImg(String path, int newWidth, int newHeight) {
        Bitmap bm=BitmapFactory.decodeFile(path);
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        if(scaleWidth>scaleHeight){
            matrix.postScale(scaleHeight, scaleHeight);
        }else{
            matrix.postScale(scaleWidth, scaleWidth);
        }
        // 得到新的图片
        return  Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
    }

}

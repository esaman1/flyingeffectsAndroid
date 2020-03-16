package com.flyingeffects.com.manager;

import android.content.Context;
import android.database.Cursor;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class BitmapManager {


    private static BitmapManager thisModel;

    public static BitmapManager getInstance() {

        if (thisModel == null) {
            thisModel = new BitmapManager();
        }
        return thisModel;

    }


    /**
     * 获得视频的旋转角度
     *
     */
    public boolean getSourceVideoDirection(String path) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();//实例化MediaMetadataRetriever对象
        File file = new File(path);//实例化File对象，文件路径为/storage/sdcard/Movies/music1.mp4
        if (file.exists() && file.length() > 0) {
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(new File(path).getAbsolutePath());
                mmr.setDataSource(inputStream.getFD());//设置数据源为该文件对象指定的绝对路径
                int videoRotation = 0;
                try {
                    videoRotation = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
                    return videoRotation != 90 && videoRotation != 270;

                } catch (Exception e) {
                    Log.d("Exception", e.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return true;
    }


    /**
     * 获得图片的选旋转角度
     */
        public  boolean getOrientation( String path) {
            ExifInterface exifInterface = null;
            try {
                exifInterface = new ExifInterface(path);
                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                return orientation != 90 && orientation != 270;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

}

package com.flyingeffects.com.ui.model;

import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

import com.flyingeffects.com.commonlyModel.getVideoInfo;
import com.flyingeffects.com.enity.VideoInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class VideoManage {

    private static VideoManage thisModel;
    public static VideoManage getInstance() {
        if (thisModel == null) {
            thisModel = new VideoManage();
        }
        return thisModel;

    }


    /**
     * description ：当前视频源是否被旋转过了，true 旋转， false  没有旋转过
     * creation date: 2020/5/14
     * user : zhangtongju
     */
    public boolean VideoIsRotation(String path) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();//实例化MediaMetadataRetriever对象
        File file = new File(path);//实例化File对象，文件路径为/storage/sdcard/Movies/music1.mp4
        if (file.exists() && file.length() > 0) {
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(new File(path).getAbsolutePath());
                mmr.setDataSource(inputStream.getFD());//设置数据源为该文件对象指定的绝对路径
                int videoRotation;
                try {
                    videoRotation = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
                    return videoRotation == 90 || videoRotation == 270;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }



    public VideoInfo getVideoInfo(String path){
        VideoInfo videoInfo = null;
        if (!TextUtils.isEmpty(path)) {
            videoInfo  = getVideoInfo.getInstance().getRingDuring(path);
        }
        return videoInfo;

    }


    public int GetVideoIsRotation(String path) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();//实例化MediaMetadataRetriever对象
        File file = new File(path);//实例化File对象，文件路径为/storage/sdcard/Movies/music1.mp4
        if (file.exists() && file.length() > 0) {
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(new File(path).getAbsolutePath());
                mmr.setDataSource(inputStream.getFD());//设置数据源为该文件对象指定的绝对路径
                int videoRotation;
                try {
                    videoRotation = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
                    return videoRotation;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }





}

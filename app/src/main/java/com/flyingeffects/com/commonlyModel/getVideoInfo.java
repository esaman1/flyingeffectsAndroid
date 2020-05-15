package com.flyingeffects.com.commonlyModel;

import android.media.MediaPlayer;

import com.flyingeffects.com.enity.VideoInfo;
import com.flyingeffects.com.utils.LogUtil;

import java.io.IOException;

public class getVideoInfo {


    private static getVideoInfo thisModel;

    public static getVideoInfo getInstance() {

        if (thisModel == null) {
            thisModel = new getVideoInfo();
        }
        return thisModel;

    }



    public VideoInfo getRingDuring(String videoPath) {
        VideoInfo info=new VideoInfo();
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            info  =new VideoInfo();
            mediaPlayer.setDataSource(videoPath);
            mediaPlayer.prepare();
            info.setDuration(mediaPlayer.getDuration());
            info.setVideoHeight(mediaPlayer.getVideoHeight());
            info.setVideoWidth(mediaPlayer.getVideoWidth());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.release();
        return info;
    }



}

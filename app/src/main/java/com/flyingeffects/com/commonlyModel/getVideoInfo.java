package com.flyingeffects.com.commonlyModel;

import android.media.MediaPlayer;

import com.flyingeffects.com.enity.VideoInfo;
import com.flyingeffects.com.utils.LogUtil;
import com.lansosdk.videoeditor.MediaInfo;

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
        MediaInfo mediaInfo = new MediaInfo(videoPath);
        try {
            info  =new VideoInfo();
            mediaPlayer.setDataSource(videoPath);
            mediaPlayer.prepare();
            info.setVideoHeight(mediaPlayer.getVideoHeight());
            info.setVideoWidth(mediaPlayer.getVideoWidth());
            mediaInfo.prepare();
            long duration=(long) (mediaInfo.vDuration*1000);
            if(duration!=0){
                info.setDuration(duration);
            }else{
                info.setDuration(mediaPlayer.getDuration());
            }
            mediaInfo.release();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.release();
        return info;
    }



}

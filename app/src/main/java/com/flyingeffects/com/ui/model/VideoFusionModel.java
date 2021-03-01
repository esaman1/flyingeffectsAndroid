package com.flyingeffects.com.ui.model;


import android.content.Context;
import android.text.TextUtils;

import com.flyingeffects.com.utils.LogUtil;
import com.lansosdk.box.LSOScaleType;
import com.lansosdk.box.LSOVideoOption;
import com.lansosdk.box.Layer;
import com.lansosdk.videoeditor.DrawPadAllExecute2;

/**
 * description ：视频融合，实现方式
 * 1:上传图片到服务器，然后得到一个完整的替换的视频
 * 2:通过蓝松，融合一个底部的图片和返回的完整视频，位置和上传的位置是同一个位置
 * creation date: 2021/3/1
 * user : zhangtongju
 */


public class VideoFusionModel {

    private static int DRAWPADWIDTH = 720;
    private static int DRAWPADHEIGHT = 1280;
    private static final int FRAME_RATE = 20;


    /**
     * 服務器返回的視頻地址
     */
    private String serversReturnPath;


    /**
     * 用戶原视频地址
     */
    private String originalPath;


    private Context context;

    private long duration;

    public void VideoFusionModel(Context context, String serversReturnPath, String originalPath, long duration) {
        this.originalPath = originalPath;
        this.context = context;
        this.duration = duration;
        this.serversReturnPath = serversReturnPath;
    }


    /**
     * description ：融合视频，通过蓝松的视频融合
     * creation date: 2021/3/1
     * user : zhangtongju
     */
    private void compoundVideo() {
        try {
            DrawPadAllExecute2   execute = new DrawPadAllExecute2(context, DRAWPADWIDTH, DRAWPADHEIGHT, duration * 1000);
            execute.setFrameRate(FRAME_RATE);

            LogUtil.d("OOM25", "时长为" + duration * 1000);
            execute.setEncodeBitrate(5 * 1024 * 1024);
            execute.setOnLanSongSDKErrorListener(message -> {
            });
            execute.setOnLanSongSDKProgressListener((l, i) -> {

            });

//            execute.addVideoLayer()
//            execute.addBitmapLayer();



            execute.start();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }




    private void setVideoLayer(String videoPath,DrawPadAllExecute2 execute){
//        LSOVideoOption option;
//        try {
//            option = new LSOVideoOption(videoPath);
//            option.setLooping(false);
//
//            Layer bgLayer = execute.addVideoLayer(option, 0, Long.MAX_VALUE, false, true);
//                float LayerWidth = bgLayer.getLayerWidth();
//                float scale = DRAWPADWIDTH / (float) LayerWidth;
//                float LayerHeight = bgLayer.getLayerHeight();
//                float needDrawHeight = LayerHeight * scale;
//                bgLayer.setScaledValue(DRAWPADWIDTH, needDrawHeight);
//                float halft = needDrawHeight / (float) 2;
//                float top = needDrawHeight * percentageH;
//                float needHeight = halft - top;
//                bgLayer.setPosition(bgLayer.getPositionX(), needHeight);
//            LogUtil.d("OOM", "主图层添加完毕");
//        } catch (Exception e) {
//            LogUtil.d("OOM", "e-------" + e.getMessage());
//            e.printStackTrace();
//        }
    }

}

package com.flyingeffects.com.ui.model;


import android.graphics.Bitmap;

import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.utils.ToastUtil;
import com.glidebitmappool.GlideBitmapPool;
import com.lansosdk.box.OnLanSongSDKErrorListener;
import com.lansosdk.videoeditor.VideoOneDo2;

/**
 * description ：视频抠图控制类
 * creation date: 2020/3/27
 * user : zhangtongju
 */
public class videoMattingModel {
    private VideoOneDo2 videoOneDo;
    private String videoPath;

    public videoMattingModel(String videoPath) {
        this.videoPath = videoPath;
        GlideBitmapPool.initialize(10 * 1024 * 1024); // 10mb max memory size
    }


    public void extractFrame() {


    }


    private void exportVideo() {
        if (videoOneDo != null && videoOneDo.isRunning()) {
            return;
        }
        try {
            videoOneDo = new VideoOneDo2(BaseApplication.getInstance(), videoPath);
            settingAndStart();
        } catch (Exception e) {
            ToastUtil.showToast("创建对象异常,可能不支持当前视频");
        }
    }


    private void settingAndStart() {
        videoOneDo.setExtractFrame(30);
        videoOneDo.setOnVideoOneDoErrorListener(new OnLanSongSDKErrorListener() {
            @Override
            public void onLanSongSDKError(int errorCode) {
                ToastUtil.showToast("VideoOneDo处理错误");
                videoOneDo.cancel();
                videoOneDo = null;
            }
        });

        showExtractFrames();


        videoOneDo.start();

    }


    private void showExtractFrames() {
        if (videoOneDo != null) {
            int cnt = 0;
            Bitmap bmp = videoOneDo.getExtractFrame();
            if (bmp != null) cnt++;
            while (bmp != null) {
                bmp = videoOneDo.getExtractFrame();
                GlideBitmapPool.putBitmap(bmp);
                if (bmp != null) cnt++;
            }

            String str = "已经读取了:" + cnt + " 帧, 代码在VideoOneDo2Activity中";
//            DemoUtil.showToast(getApplicationContext(),str);
//            startPlayDstVideo();
        }

    }

    private void readFramesAndMasking() {


        if (videoOneDo != null) {
            int cnt = 0;
            Bitmap bmp = videoOneDo.getExtractFrame();
            if (bmp != null) {
                cnt++;
            }


            String str = "已经读取了:" + cnt + " 帧, 代码在VideoOneDo2Activity中";
//            DemoUtil.showToast(getApplicationContext(),str);
//            startPlayDstVideo();
        }

    }


}









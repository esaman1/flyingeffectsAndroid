package com.flyingeffects.com.ui.model;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.enity.AllStickerData;
import com.flyingeffects.com.manager.BitmapManager;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.glidebitmappool.GlideBitmapPool;
import com.lansosdk.box.CanvasLayer;
import com.lansosdk.box.CanvasRunnable;
import com.lansosdk.box.ExtractVideoFrame;
import com.lansosdk.box.OnLanSongSDKCompletedListener;
import com.lansosdk.box.OnLanSongSDKErrorListener;
import com.lansosdk.box.OnLanSongSDKProgressListener;
import com.lansosdk.box.onExtractVideoFrameCompletedListener;
import com.lansosdk.box.onExtractVideoFrameProgressListener;
import com.lansosdk.videoeditor.DrawPadAllExecute2;
import com.lansosdk.videoeditor.MediaInfo;
import com.lansosdk.videoeditor.VideoOneDo2;

import java.io.File;

/**
 * description ：视频抠图控制类
 * creation date: 2020/3/27
 * user : zhangtongju
 */
public class VideoMattingModel {
    private ExtractVideoFrame mExtractFrame;
    private VideoOneDo2 videoOneDo;
    private String videoPath;
    /**
     * 专门用来存储face 抠像的文件夹
     */
    private String faceFolder;
    private int frameCount;
    private static final int DRAWPADWIDTH = 720;
    private static final int DRAWPADHEIGHT = 1280;
    private static final int FRAME_RATE = 30;

    public VideoMattingModel(String videoPath) {
        this.videoPath = videoPath;
        FileManager fileManager = new FileManager();
        faceFolder = fileManager.getFileCachePath(BaseApplication.getInstance(), "faceFolder");
        GlideBitmapPool.initialize(10 * 1024 * 1024); // 10mb max memory size
    }





    public void newFunction() {


        MediaInfo mInfo = new MediaInfo(videoPath);
        if (!mInfo.prepare() || !mInfo.isHaveVideo()) {
            return;
        }

        mExtractFrame = new ExtractVideoFrame(BaseApplication.getInstance(), videoPath);
        if (mInfo.vWidth * mInfo.vHeight > 960 * 540) {
            mExtractFrame.setBitmapWH(mInfo.vWidth / 2, mInfo.vHeight / 2);
        }

        int totalFrame = mInfo.vTotalFrames;
        //设置提取多少帧
        mExtractFrame.setExtractSomeFrame(totalFrame);
        /**
         * 设置处理完成监听.
         */
        mExtractFrame.setOnExtractCompletedListener(new onExtractVideoFrameCompletedListener() {

            @Override
            public void onCompleted(ExtractVideoFrame v) {


            }
        });
        /**
         * 设置处理进度监听.
         */
        mExtractFrame.setOnExtractProgressListener(new onExtractVideoFrameProgressListener() {

            /**
             * 当前帧的画面回调,, ptsUS:当前帧的时间戳,单位微秒. 拿到图片后,建议放到ArrayList中,
             * 不要直接在这里处理.
             */
            @Override
            public void onExtractBitmap(Bitmap bmp, long ptsUS) {
                frameCount++;
                String hint = " 当前是第" + frameCount + "帧" + "\n"
                        + "当前帧的时间戳是:" + String.valueOf(ptsUS) + "微秒";

//                tvProgressHint.setText(hint);
                LogUtil.d("OOM", hint);

                String fileName = faceFolder + File.separator + frameCount + ".png";
                BitmapManager.getInstance().saveBitmapToPath(bmp, fileName);
                GlideBitmapPool.putBitmap(bmp);

                // saveBitmap(bmp); //测试使用.
                // if(bmp!=null && bmp.isRecycled()){
                // bmp.recycle();
                // bmp=null;
                // }
                // if(ptsUS>15*1000*1000){ // 你可以在指定的时间段停止.
                // mExtractFrame.cancel(); //这里演示在15秒的时候停止.
                // }
            }
        });
        frameCount = 0;
        /**
         * 开始执行. 或者你可以从指定地方开始解码.
         * mExtractFrame.start(10*1000*1000);则从视频的10秒处开始提取.
         */
        mExtractFrame.start();

    }


//    public void addFrameCompoundVideo() {
//        try {
//            DrawPadAllExecute2 execute = new DrawPadAllExecute2(BaseApplication.getInstance(), DRAWPADWIDTH, DRAWPADHEIGHT, (long) (duration * 1000));
//            execute.setFrameRate(FRAME_RATE);
//            execute.setEncodeBitrate(5 * 1024 * 1024);
//            execute.setOnLanSongSDKErrorListener(message -> {
//                LogUtil.d("OOM", "错误信息为" + message);
//            });
//            execute.setOnLanSongSDKProgressListener((l, i) -> {
////                waitingProgress.setProgress(i + "%");
//                LogUtil.d("OOM","进度为");
//            });
//            execute.setOnLanSongSDKCompletedListener(exportPath -> {
//
//                //todo 需要移除全部的子图层
//                execute.release();
//                Log.d("OOM", "exportPath=" + exportPath);
//            });
//
//            CanvasLayer canvasLayer=     execute.addCanvasLayer();
//            canvasLayer.addCanvasRunnable(new CanvasRunnable() {
//                @Override
//                public void onDrawCanvas(CanvasLayer canvasLayer, Canvas canvas, long currentTime) {
//
//
//
//                }
//            });
//
//            execute.start();
//        } catch (Exception e) {
//            LogUtil.d("OOM", e.getMessage());
//            e.printStackTrace();
//        }
//
//
//    }


}
















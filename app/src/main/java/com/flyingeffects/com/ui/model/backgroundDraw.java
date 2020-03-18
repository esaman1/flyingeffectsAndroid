package com.flyingeffects.com.ui.model;


import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.flyingeffects.com.enity.AllStickerData;
import com.flyingeffects.com.utils.LogUtil;
import com.lansosdk.box.GifLayer;
import com.lansosdk.box.LSOVideoOption;
import com.lansosdk.videoeditor.DrawPadAllExecute2;
import com.shixing.sxve.ui.view.WaitingDialog_progress;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 蓝松后台绘制方法
 */
public class backgroundDraw {

    private static final int DRAWPADWIDTH = 720;
    private static final int DRAWPADHEIGHT = 1280;
    private static final int FRAME_RATE = 30;
    private DrawPadAllExecute2 execute;
    private Context context;
    private WaitingDialog_progress waitingProgress;
    private String videoPath;
    private saveCallback callback;
    private int duration;
    private String gifTest = "/storage/emulated/0/Android/data/com.tencent.mobileqq/Tencent/QQfile_recv/Comp-1(1).gif";

    public backgroundDraw(Context context, String videoPath, saveCallback callback) {
        this.context = context;
        this.videoPath = videoPath;
        this.callback = callback;
        waitingProgress = new WaitingDialog_progress(context);
        duration = getRingDuring(videoPath);
        LogUtil.d("OOM", "backgroundDrawdurationF=" + duration);
    }


    public void toSaveVideo(ArrayList<AllStickerData> list) {
        waitingProgress.openProgressDialog();
        execute = new DrawPadAllExecute2(context, DRAWPADWIDTH, DRAWPADHEIGHT, (long) (duration * 1000));
        execute.setFrameRate(FRAME_RATE);
        execute.setEncodeBitrate(5 * 1024 * 1024);
        execute.setOnLanSongSDKErrorListener(message -> {
        });
        execute.setOnLanSongSDKProgressListener((l, i) -> {
            waitingProgress.setProgress(i + "%");
        });
        execute.setOnLanSongSDKCompletedListener(exportPath -> {
            waitingProgress.closePragressDialog();
            callback.saveSuccessPath(exportPath);
            //todo 需要移除全部的子图层
            execute.release();
            Log.d("OOM", "exportPath=" + exportPath);
        });
        setLayer();
        for (AllStickerData item : list
        ) {
            addGifLayer(item);
        }
        execute.start();
    }


    private void setLayer() {
        LSOVideoOption option = null;
        try {
            option = new LSOVideoOption(videoPath);
            execute.addVideoLayer(option, 0, Long.MAX_VALUE, true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 增加一个MV图层.
     */
    private void addGifLayer(AllStickerData stickerItem) {
        LogUtil.d("OOM", "addMVLayer");
        GifLayer mvLayer = execute.addGifLayer(gifTest);
        mvLayer.setScaledToPadSize();
        if (stickerItem != null) {
            int rotate = (int) stickerItem.getRotation();
            if (rotate < 0) {
                rotate = 360 + rotate;
            }
            LogUtil.d("OOM", "rotate="+rotate);
            mvLayer.setRotate(rotate);
            mvLayer.setScale(stickerItem.getScale() / 2);
            LogUtil.d("OOM", "Scale="+stickerItem.getScale() / 2 + "");
            mvLayer.setPosition(stickerItem.getTranslationX(), mvLayer.getPositionY());
            LogUtil.d("OOM", "setPositionY=" + stickerItem.getTranslationy()+"x="+stickerItem.getTranslationX());
            mvLayer.setPosition(mvLayer.getPositionX(), stickerItem.getTranslationy());
        }
    }


    public interface saveCallback {
        void saveSuccessPath(String path);
    }


    private int getRingDuring(String videoPath) {
        int duration = 0;
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(videoPath);
            mediaPlayer.prepare();
            duration = mediaPlayer.getDuration();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.release();
        return duration;
    }

}
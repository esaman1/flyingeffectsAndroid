package com.flyingeffects.com.ui.model;


import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.flyingeffects.com.enity.StickerForParents;
import com.flyingeffects.com.manager.CopyFileFromAssets;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.view.lansongCommendView.StickerItem;
import com.flyingeffects.com.view.lansongCommendView.StickerView;
import com.lansosdk.box.LSOVideoOption;
import com.lansosdk.box.MVCacheLayer;
import com.lansosdk.videoeditor.DrawPadAllExecute2;
import com.shixing.sxve.ui.view.WaitingDialog_progress;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

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
    int duration;

    public backgroundDraw(Context context, String videoPath, saveCallback callback) {
        this.context = context;
        this.videoPath = videoPath;
        this.callback = callback;
        waitingProgress = new WaitingDialog_progress(context);
        duration = getRingDuring(videoPath);
        LogUtil.d("OOM", "backgroundDrawdurationF=" + duration);

    }


    public void toSaveVideo(StickerView stickView) {
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
        LinkedHashMap<Integer, StickerItem> linkedHashMap = stickView.getBank();
        for (int i = 1; i <= linkedHashMap.size(); i++) {
            //多个图层的情况
            StickerItem stickerItem = linkedHashMap.get(i);
            addMVLayer(stickerItem);
        }
        execute.start();
    }


    private void setLayer() {
        LSOVideoOption option = null;
        try {
            option = new LSOVideoOption(videoPath);
            execute.addVideoLayer(option, 0, Long.MAX_VALUE, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 增加一个MV图层.
     */
    private void addMVLayer(StickerItem stickerItem) {
        LogUtil.d("OOM","addMVLayer");
        String colorMVPath = CopyFileFromAssets.copyAssets(context, "mei.mp4");
        String maskMVPath = CopyFileFromAssets.copyAssets(context, "mei_b.mp4");
        MVCacheLayer mvLayer=  execute.addMVLayer(colorMVPath, maskMVPath); // <-----增加MVLayer
        if(stickerItem!=null){
            mvLayer.setRotate(stickerItem.getRoatetAngle());
            mvLayer.setScale(stickerItem.getScaleSize());
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
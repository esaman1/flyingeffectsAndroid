package com.flyingeffects.com.ui.model;


import android.content.Context;
import android.util.Log;

import com.flyingeffects.com.enity.StickerForParents;
import com.flyingeffects.com.manager.CopyFileFromAssets;
import com.lansosdk.box.LSOVideoOption;
import com.lansosdk.box.MVCacheLayer;
import com.lansosdk.videoeditor.DrawPadAllExecute2;
import com.shixing.sxve.ui.view.WaitingDialog_progress;

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

    public backgroundDraw(Context context,String videoPath,saveCallback callback){
        this.context=context;
        this.videoPath=videoPath;
        this.callback=callback;
        waitingProgress =new WaitingDialog_progress(context);
    }



    public  void toSaveVideo(long Duration,  StickerForParents stickerForParents){
        waitingProgress.openProgressDialog();
        execute = new DrawPadAllExecute2(context, DRAWPADWIDTH, DRAWPADHEIGHT, Duration*1000);
        execute.setFrameRate(FRAME_RATE);
        execute.setEncodeBitrate(5 * 1024 * 1024);
        execute.setOnLanSongSDKErrorListener(message -> {
        });
        execute.setOnLanSongSDKProgressListener((l, i) -> {
            waitingProgress.setProgress(i+"%");
        });
        execute.setOnLanSongSDKCompletedListener(exportPath -> {
            waitingProgress.closePragressDialog();
            callback.saveSuccessPath(exportPath);
            //todo 需要移除全部的子图层
            execute.release();
            Log.d("OOM", "exportPath=" + exportPath);
        });
        setLayer();
        addMVLayer(stickerForParents);
        execute.start();
    }





    private void  setLayer(){
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
    private void addMVLayer(StickerForParents stickerForParents) {
        String colorMVPath = CopyFileFromAssets.copyAssets(context, "laohu.mp4");
        String maskMVPath = CopyFileFromAssets.copyAssets(context, "mask.mp4");
        MVCacheLayer mvLayer = execute.addMVLayer(colorMVPath, maskMVPath); // <-----增加MVLayer
        mvLayer.setRotate(stickerForParents.getRoation());
        mvLayer.setScale(stickerForParents.getScale());
    }




    public interface saveCallback{
        void saveSuccessPath(String path);
    }



}

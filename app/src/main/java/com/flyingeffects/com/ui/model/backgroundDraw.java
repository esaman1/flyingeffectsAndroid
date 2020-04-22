package com.flyingeffects.com.ui.model;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;

import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.enity.AllStickerData;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.utils.LogUtil;
import com.lansosdk.box.BitmapLayer;
import com.lansosdk.box.CanvasLayer;
import com.lansosdk.box.GifLayer;
import com.lansosdk.box.LSOVideoOption;
import com.lansosdk.box.VideoFrameLayer;
import com.lansosdk.videoeditor.DrawPadAllExecute2;
import com.shixing.sxve.ui.albumType;
import com.shixing.sxve.ui.view.WaitingDialog_progress;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private int intoCanvesCount;
    private String ExtractFramegFolder;

    public backgroundDraw(Context context, String videoPath, saveCallback callback) {
        this.context = context;
        this.videoPath = videoPath;
        this.callback = callback;
        waitingProgress = new WaitingDialog_progress(context);
        duration = getRingDuring(videoPath);
        LogUtil.d("OOM", "backgroundDrawdurationF=" + duration);
        intoCanvesCount=0;
        FileManager fileManager = new FileManager();
        ExtractFramegFolder = fileManager.getFileCachePath(BaseApplication.getInstance(), "ExtractFrame");
    }

    public void toSaveVideo(ArrayList<AllStickerData> list) {
        LogUtil.d("OOM2","进入到了最后渲染");
        waitingProgress.openProgressDialog();
        try {
            execute = new DrawPadAllExecute2(context, DRAWPADWIDTH, DRAWPADHEIGHT, (long) (duration * 1000));
            execute.setFrameRate(FRAME_RATE);
            execute.setEncodeBitrate(5 * 1024 * 1024);
            execute.setOnLanSongSDKErrorListener(message -> {
                LogUtil.d("OOM2","错误信息为"+message);
            });
            execute.setOnLanSongSDKProgressListener((l, i) -> {
//                waitingProgress.setProgress(i + "%");
                waitingProgress.setProgress("正在保存中" + i + "%\n" +
                        "请勿离开页面");
            });
            execute.setOnLanSongSDKCompletedListener(exportPath -> {
                waitingProgress.closePragressDialog();
                callback.saveSuccessPath(exportPath);
                //todo 需要移除全部的子图层
                execute.release();
                Log.d("OOM", "exportPath=" + exportPath);
            });
            setMainLayer();

            for (int i=0;i<list.size();i++){
                AllStickerData item=list.get(i);
                String pathType= GetPathTypeModel.getInstance().getMediaType(item.getPath());
                if (albumType.isVideo(pathType)) {
                    intoCanvesCount++;
                    addCanversLayer(item,intoCanvesCount);
                }else{
                    if (item.getPath().endsWith(".gif")) {
                        addGifLayer(item);
                    } else {
                        addBitmapLayer(item);
                    }
                }

            }


            execute.start();
        } catch (Exception e) {
            LogUtil.d("OOM",e.getMessage());
            e.printStackTrace();
        }

    }





    private void setMainLayer() {
        LSOVideoOption option  ;
        try {
//            option = new LSOVideoOption(videoPath);
//            option.setLooping(true);
//            VideoFrameLayer bgLayer=execute.addVideoLayer(option,0, Long.MAX_VALUE, true, true);
//            bgLayer.setScaledToPadSize();
            option = new LSOVideoOption(videoPath);
            VideoFrameLayer bgLayer=  execute.addVideoLayer(option);
            bgLayer.setScaledToPadSize();
            LogUtil.d("OOM","主图层添加完毕");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 增加一个MV图层.
     */
    private void addGifLayer(AllStickerData stickerItem) {
        LogUtil.d("OOM", "addMVLayer");
        GifLayer mvLayer = execute.addGifLayer(stickerItem.getPath());
        //默认gif 的缩放位置是gif 宽度最大
        float layerScale = DRAWPADWIDTH / (float)mvLayer.getLayerWidth();
        LogUtil.d("OOM", "图层的缩放为" +layerScale+ "");
        float stickerScale = stickerItem.getScale();
        LogUtil.d("OOM", "gif+图层的缩放为" +layerScale * stickerScale+ "");
        mvLayer.setScale(layerScale * stickerScale);
        LogUtil.d("OOM", "mvLayerW=" + mvLayer.getLayerWidth() + "");
        LogUtil.d("OOM", "mvLayerpadW=" + mvLayer.getPadWidth() + "");
        int rotate = (int) stickerItem.getRotation();
        if (rotate < 0) {
            rotate = 360 + rotate;
        }
        LogUtil.d("OOM", "rotate=" + rotate);
        mvLayer.setRotate(rotate);
        LogUtil.d("OOM", "Scale=" + stickerItem.getScale() + "");
        //蓝松这边规定，0.5就是刚刚居中的位置
        float percentX = stickerItem.getTranslationX();
//        float posX = (mvLayer.getPadWidth() + mvLayer.getLayerWidth()) * percentX - mvLayer.getLayerWidth() / 2.0f;
        mvLayer.setPosition(mvLayer.getPadWidth()*percentX , mvLayer.getPositionY());

        float percentY = stickerItem.getTranslationy();
        LogUtil.d("OOM", "percentX=" + percentX + "percentY=" + percentY);
//        float posY = (mvLayer.getPadHeight() + mvLayer.getLayerHeight()) * percentY - mvLayer.getLayerHeight() / 2.0f;
//        mvLayer.setPosition(mvLayer.getPositionX(), posY);
        mvLayer.setPosition(mvLayer.getPositionX(), mvLayer.getPadHeight()*percentY);

    }


    /**
     * 增加一个图片图层.
     */
    private void addBitmapLayer(AllStickerData stickerItem) {
        LogUtil.d("OOM", "addMVLayer");
        Bitmap bp = BitmapFactory.decodeFile(stickerItem.getPath());
        BitmapLayer bpLayer = execute.addBitmapLayer(bp);

        float layerScale = DRAWPADWIDTH /(float) bpLayer.getLayerWidth();
        LogUtil.d("OOM", "图层的缩放为" +layerScale+ "");
        float stickerScale = stickerItem.getScale();
        LogUtil.d("OOM", "gif+图层的缩放为" +layerScale * stickerScale+ "");
        bpLayer.setScale(layerScale * stickerScale);
        LogUtil.d("OOM", "mvLayerW=" + bpLayer.getLayerWidth() + "");
        LogUtil.d("OOM", "mvLayerpadW=" + bpLayer.getPadWidth() + "");
        int rotate = (int) stickerItem.getRotation();
        if (rotate < 0) {
            rotate = 360 + rotate;
        }
        LogUtil.d("OOM", "rotate=" + rotate);
        bpLayer.setRotate(rotate);
        LogUtil.d("OOM", "Scale=" + stickerItem.getScale() + "");
        //蓝松这边规定，0.5就是刚刚居中的位置
        float percentX = stickerItem.getTranslationX();
//        float posX = (bpLayer.getPadWidth() + bpLayer.getLayerWidth()) * percentX - bpLayer.getLayerWidth() / 2.0f;
        bpLayer.setPosition(bpLayer.getPadWidth()*percentX , bpLayer.getPositionY());


        float percentY = stickerItem.getTranslationy();
        LogUtil.d("OOM", "percentX=" + percentX + "percentY=" + percentY);
     //   float posY = (bpLayer.getPadHeight() + bpLayer.getLayerHeight()) * percentY - bpLayer.getLayerHeight() / 2.0f;
        bpLayer.setPosition(bpLayer.getPositionX(), bpLayer.getPadHeight()*percentY);

    }





    private  void addCanversLayer(AllStickerData stickerItem,int i){
        int[] nowChooseImageIndex = {0};
        //当前进度时间
         float[] nowProgressTime={0};
        float preTime;
        LogUtil.d("OOM","开始添加CanversLayer");
        String path=ExtractFramegFolder+"/"+i;
        LogUtil.d("OOM","path"+path);
        List<File> getMattingList = FileManager.listFileSortByModifyTime(path);
        LogUtil.d("OOM","第一张图片地址为"+getMattingList.get(0).getPath());
        Bitmap bp = BitmapFactory.decodeFile(getMattingList.get(0).getPath());
        LogUtil.d("OOM","图片宽为"+bp.getWidth());
        BitmapLayer bpLayer = execute.addBitmapLayer(bp);
        float layerScale = DRAWPADWIDTH /(float) bpLayer.getLayerWidth();
        LogUtil.d("OOM", "图层的缩放为" +layerScale+ "");
        float stickerScale = stickerItem.getScale();
        LogUtil.d("OOM", "gif+图层的缩放为" +layerScale * stickerScale+ "");
        bpLayer.setScale(layerScale * stickerScale);
        LogUtil.d("OOM", "mvLayerW=" + bpLayer.getLayerWidth() + "");
        LogUtil.d("OOM", "mvLayerpadW=" + bpLayer.getPadWidth() + "");
        int rotate = (int) stickerItem.getRotation();
        if (rotate < 0) {
            rotate = 360 + rotate;
        }
        LogUtil.d("OOM", "rotate=" + rotate);
        bpLayer.setRotate(rotate);
        LogUtil.d("OOM", "Scale=" + stickerItem.getScale() + "");
        //蓝松这边规定，0.5就是刚刚居中的位置
        float percentX = stickerItem.getTranslationX();
//        float posX = (bpLayer.getPadWidth() + bpLayer.getLayerWidth()) * percentX - bpLayer.getLayerWidth() / 2.0f;
        bpLayer.setPosition(bpLayer.getPadWidth()*percentX , bpLayer.getPositionY());
        float percentY = stickerItem.getTranslationy();
        LogUtil.d("OOM", "percentX=" + percentX + "percentY=" + percentY);
        //   float posY = (bpLayer.getPadHeight() + bpLayer.getLayerHeight()) * percentY - bpLayer.getLayerHeight() / 2.0f;
        bpLayer.setPosition(bpLayer.getPositionX(), bpLayer.getPadHeight()*percentY);

        preTime = stickerItem.getDuration() *1000/ (float) getMattingList.size();
        nowProgressTime[0]=preTime;
        CanvasLayer canvasLayer = execute.addCanvasLayer();
        canvasLayer.addCanvasRunnable((canvasLayer1, canvas, currentTime) -> {
            if (currentTime >  nowProgressTime[0]) {
                //需要切换新的图了
                nowChooseImageIndex[0]++;
                if ( nowChooseImageIndex[0] < getMattingList.size()) {
                    LogUtil.d("CanvasRunnable", "addCanvasRunnable=" + preTime + "currentTime=" + currentTime + "nowChooseImageIndex=" + nowChooseImageIndex);
                    nowProgressTime[0] = preTime + nowProgressTime[0];
                    Bitmap firstBitmap1 = BitmapFactory.decodeFile(getMattingList.get( nowChooseImageIndex[0]).getPath());
                    bpLayer.switchBitmap(firstBitmap1);
                }else{
                    bpLayer.setVisibility(View.GONE);
                }
            }
        });

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
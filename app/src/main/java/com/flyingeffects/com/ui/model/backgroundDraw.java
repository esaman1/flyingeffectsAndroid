package com.flyingeffects.com.ui.model;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v4.util.SparseArrayCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.enity.AllStickerData;
import com.flyingeffects.com.manager.BitmapManager;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.view.animations.CustomMove.AnimCollect;
import com.flyingeffects.com.view.animations.CustomMove.AnimType;
import com.flyingeffects.com.view.animations.CustomMove.LayerAnimCallback;
import com.lansosdk.box.BitmapLayer;
import com.lansosdk.box.CanvasLayer;
import com.lansosdk.box.GifLayer;
import com.lansosdk.box.LSOScaleType;
import com.lansosdk.box.LSOVideoOption;
import com.lansosdk.box.Layer;
import com.lansosdk.box.SubLayer;
import com.lansosdk.box.VideoFrameLayer;
import com.lansosdk.videoeditor.DrawPadAllExecute2;
import com.shixing.sxve.ui.albumType;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 蓝松后台绘制方法
 */
public class backgroundDraw {

    private static final int DRAWPADWIDTH = 720;
    private static final int DRAWPADHEIGHT = 1280;
    private static final int FRAME_RATE = 20;
    private DrawPadAllExecute2 execute;
    private Context context;
    //    private WaitingDialog_progress waitingProgress;
    private String videoPath;
    private saveCallback callback;
    /**
     * 渲染视频时长，默认为10s
     */
    private int duration;
    private int intoCanvesCount;
    private String ExtractFramegFolder;

    private boolean noVideo = false;
    /**
     * 视频图层声音
     */
    private String videoVoice;
    private String imagePath;

    /**
     * 收集有动画的图层
     */
    private ArrayList<hasAnimLayer> hasAnimLayerList = new ArrayList<>();

    /**
     * 总时间,微秒 1=1000=1000*1000
     */
    private long totleRenderTime;

    private AnimCollect animCollect;

    /**
     * description ：后台绘制，如果videoVoice不为null,那么需要把主视频图层的声音替换为用户选择的背景声音
     * imagePath 如果videoPath没有且imagePath 有的情况，需要把绿幕背景替换为图片背景
     * creation date: 2020/4/23
     * user : zhangtongju
     */
    public backgroundDraw(Context context, String videoPath, String videoVoice, String imagePath, saveCallback callback, AnimCollect animCollect) {
        this.context = context;
        this.videoPath = videoPath;
        this.videoVoice = videoVoice;
        this.imagePath = imagePath;
        this.animCollect = animCollect;
        this.callback = callback;
//        waitingProgress = new WaitingDialog_progress(context);
        if (!TextUtils.isEmpty(videoPath)) {
            duration = getRingDuring(videoPath);
        }
        LogUtil.d("OOM", "backgroundDrawdurationF=" + duration);
        LogUtil.d("OOM", "videoVoice=" + videoVoice);
        intoCanvesCount = 0;
        FileManager fileManager = new FileManager();
        ExtractFramegFolder = fileManager.getFileCachePath(BaseApplication.getInstance(), "ExtractFrame");
    }

    public void toSaveVideo(ArrayList<AllStickerData> list, boolean isMatting) {
        //说明没得背景视频，那么渲染时长就是
        if (duration == 0) {
            for (AllStickerData data : list
            ) {
                if (duration < (int) data.getDuration()) {
                    duration = (int) data.getDuration();
                }
            }
        }
        //如果还是0,说明全是图片，就修改为10
        if (duration == 0) {
            noVideo = true;
            duration = 10000;
        }
        LogUtil.d("OOM2", "进入到了最后渲染");
//        waitingProgress.openProgressDialog();
        totleRenderTime = duration * 1000;
        try {
            execute = new DrawPadAllExecute2(context, DRAWPADWIDTH, DRAWPADHEIGHT, (long) (duration * 1000));
            execute.setFrameRate(FRAME_RATE);
            LogUtil.d("OOM2", "时长为" + FRAME_RATE);
            execute.setEncodeBitrate(5 * 1024 * 1024);
            execute.setOnLanSongSDKErrorListener(message -> {
                LogUtil.d("OOM2", "错误信息为" + message);
                callback.saveSuccessPath("", 10000);
            });
            execute.setOnLanSongSDKProgressListener((l, i) -> {

                if (noVideo) {
                    callback.saveSuccessPath("", i);
                } else {
                    float f_progress = (i / (float) 100) * 5;
                    int progress;
                    if (isMatting) {
                        progress = (int) (95 + f_progress);
                    } else {
                        progress = (int) (5 + f_progress);
                    }
                    LogUtil.d("OOM2", "progress=" + progress);
                    callback.saveSuccessPath("", progress);
                }
                LogUtil.d("OOM2", "saveSuccessPath");
//                waitingProgress.setProgress(i + "%");
//                waitingProgress.setProgress("正在保存中" + i + "%\n" +
//                        "请勿离开页面");
            });
            execute.setOnLanSongSDKCompletedListener(exportPath -> {
//                waitingProgress.closePragressDialog();
                callback.saveSuccessPath(exportPath, 100);
                //todo 需要移除全部的子图层
                execute.release();
                Log.d("OOM", "exportPath=" + exportPath);
            });
            if (!TextUtils.isEmpty(videoPath)) {
                setMainLayer();
            } else {
                if (!TextUtils.isEmpty(imagePath)) {
                    Bitmap bt_nj = BitmapManager.getInstance().getOrientationBitmap(imagePath);
                    execute.addBitmapLayer(bt_nj);
                } else {
                    execute.setBackgroundColor(Color.parseColor("#1FA400"));
                }
            }


            addMainCanversLayer(list, isMatting);


//            for (int i = 0; i < list.size(); i++) {
//                AllStickerData item = list.get(i);
//                String pathType = GetPathTypeModel.getInstance().getMediaType(item.getPath());
//                if (albumType.isVideo(pathType)) {
//                    if (isMatting) {
//                        intoCanvesCount++;
//                        addCanversLayer(item, intoCanvesCount);
//                    } else {
//                        addVideoLayer(item);
//                    }
//                } else {
//                    if (item.getPath().endsWith(".gif")) {
//                        addGifLayer(item);
//                    } else {
//                        addBitmapLayer(item);
//                    }
//                }
//            }

            if (!TextUtils.isEmpty(videoVoice)) {
                //如果有videoVoice 字段，那么需要设置在对应的主图层上面去
                execute.addAudioLayer(videoVoice, false);
            }
            execute.start();
        } catch (Exception e) {
            callback.saveSuccessPath("", 10000);
            LogUtil.d("OOM", e.getMessage());
            e.printStackTrace();
        }

    }


    private void setMainLayer() {

        LSOVideoOption option;
        try {
//            option = new LSOVideoOption(videoPath);
//            option.setLooping(true);
//            VideoFrameLayer bgLayer=execute.addVideoLayer(option,0, Long.MAX_VALUE, true, true);
//            bgLayer.setScaledToPadSize();
            option = new LSOVideoOption(videoPath);

            if (!TextUtils.isEmpty(videoVoice)) {
                option.setAudioMute();
            }
            VideoFrameLayer bgLayer = execute.addVideoLayer(option);
            bgLayer.setScaleType(LSOScaleType.VIDEO_SCALE_TYPE);
            LogUtil.d("OOM", "主图层添加完毕");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addVideoLayer(AllStickerData stickerItem) {
        LSOVideoOption option = null;
        try {
            option = new LSOVideoOption(stickerItem.getPath());
            option.setAudioMute();
            VideoFrameLayer videoLayer = execute.addVideoLayer(option);

            //默认gif 的缩放位置是gif 宽度最大
            float layerScale = DRAWPADWIDTH / (float) videoLayer.getLayerWidth();
            LogUtil.d("OOM", "图层的缩放为" + layerScale + "");
            float stickerScale = stickerItem.getScale();
            LogUtil.d("OOM", "gif+图层的缩放为" + layerScale * stickerScale + "");
            videoLayer.setScale(layerScale * stickerScale);
            LogUtil.d("OOM", "mvLayerW=" + videoLayer.getLayerWidth() + "");
            LogUtil.d("OOM", "mvLayerpadW=" + videoLayer.getPadWidth() + "");
            int rotate = (int) stickerItem.getRotation();
            if (rotate < 0) {
                rotate = 360 + rotate;
            }
            LogUtil.d("OOM", "rotate=" + rotate);
            videoLayer.setRotate(rotate);
            LogUtil.d("OOM", "Scale=" + stickerItem.getScale() + "");
            //蓝松这边规定，0.5就是刚刚居中的位置
            float percentX = stickerItem.getTranslationX();
            videoLayer.setPosition(videoLayer.getPadWidth() * percentX, videoLayer.getPositionY());
            float percentY = stickerItem.getTranslationy();
            LogUtil.d("OOM", "percentX=" + percentX + "percentY=" + percentY);
            videoLayer.setPosition(videoLayer.getPositionX(), videoLayer.getPadHeight() * percentY);

//            if (stickerItem.getChooseAnimId() != null && stickerItem.getChooseAnimId() != AnimType.NULL) {
//                hasAnimLayer animLayer = new hasAnimLayer(stickerItem.getChooseAnimId(), videoLayer);
//                int needSublayer = animCollect.getAnimNeedSubLayerCount(stickerItem.getChooseAnimId());
//                hasAnimLayerList.add(animLayer);
//
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * 增加一个MV图层.
     */
    private void addGifLayer(AllStickerData stickerItem) {
        GifLayer gifLayer = execute.addGifLayer(stickerItem.getPath());
        //默认gif 的缩放位置是gif 宽度最大
        float layerScale = DRAWPADWIDTH / (float) gifLayer.getLayerWidth();
        LogUtil.d("OOM", "图层的缩放为" + layerScale + "");
        float stickerScale = stickerItem.getScale();
        LogUtil.d("OOM", "gif+图层的缩放为" + layerScale * stickerScale + "");
        gifLayer.setScale(layerScale * stickerScale);
        LogUtil.d("OOM", "mvLayerW=" + gifLayer.getLayerWidth() + "");
        LogUtil.d("OOM", "mvLayerpadW=" + gifLayer.getPadWidth() + "");
        int rotate = (int) stickerItem.getRotation();
        if (rotate < 0) {
            rotate = 360 + rotate;
        }
        LogUtil.d("OOM", "rotate=" + rotate);
        gifLayer.setRotate(rotate);
        LogUtil.d("OOM", "Scale=" + stickerItem.getScale() + "");
        //蓝松这边规定，0.5就是刚刚居中的位置
        float percentX = stickerItem.getTranslationX();
//        float posX = (mvLayer.getPadWidth() + mvLayer.getLayerWidth()) * percentX - mvLayer.getLayerWidth() / 2.0f;
        gifLayer.setPosition(gifLayer.getPadWidth() * percentX, gifLayer.getPositionY());

        float percentY = stickerItem.getTranslationy();
        LogUtil.d("OOM", "percentX=" + percentX + "percentY=" + percentY);
//        float posY = (mvLayer.getPadHeight() + mvLayer.getLayerHeight()) * percentY - mvLayer.getLayerHeight() / 2.0f;
//        mvLayer.setPosition(mvLayer.getPositionX(), posY);
        gifLayer.setPosition(gifLayer.getPositionX(), gifLayer.getPadHeight() * percentY);



    }


    /**
     * 增加一个图片图层.
     */
    private void addBitmapLayer(AllStickerData stickerItem) {
        LogUtil.d("OOM", "addBitmapLayer");
        Bitmap bp = BitmapFactory.decodeFile(stickerItem.getPath());
        BitmapLayer bpLayer = execute.addBitmapLayer(bp);

        float layerScale = DRAWPADWIDTH / (float) bpLayer.getLayerWidth();
        LogUtil.d("OOM", "图层的缩放为" + layerScale + "");
        float stickerScale = stickerItem.getScale();
        LogUtil.d("OOM", "gif+图层的缩放为" + layerScale * stickerScale + "");
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
        bpLayer.setPosition(bpLayer.getPadWidth() * percentX, bpLayer.getPositionY());


        float percentY = stickerItem.getTranslationy();
        LogUtil.d("OOM", "percentX=" + percentX + "percentY=" + percentY);
        //   float posY = (bpLayer.getPadHeight() + bpLayer.getLayerHeight()) * percentY - bpLayer.getLayerHeight() / 2.0f;
        bpLayer.setPosition(bpLayer.getPositionX(), bpLayer.getPadHeight() * percentY);


        //todo 测试
        if (stickerItem.getChooseAnimId() != null && stickerItem.getChooseAnimId() != AnimType.NULL) {
            int needSublayer = animCollect.getAnimNeedSubLayerCount(stickerItem.getChooseAnimId());
            addBitmapSubLayer(needSublayer, bpLayer, stickerItem.getChooseAnimId());
        }

    }


    private void addBitmapSubLayer(int needSublayer, BitmapLayer layer, AnimType ChooseAnimId) {
        ArrayList<SubLayer> listForSubLayer = new ArrayList<>();{
            SubLayer subLayer = layer.addSubLayerUseMainFilter(true);
            listForSubLayer.add(subLayer);
        }
        for (int i = 0; i < needSublayer; i++) {
            SubLayer subLayer = layer.addSubLayerUseMainFilter(true);
            listForSubLayer.add(subLayer);
        }
        hasAnimLayer animLayer = new hasAnimLayer(ChooseAnimId, layer, listForSubLayer);
        hasAnimLayerList.add(animLayer);
    }


    private void addCanversLayer(AllStickerData stickerItem, int i) {
        int[] nowChooseImageIndex = {0};
        //当前进度时间
        float[] nowProgressTime = {0};
        float preTime;
        LogUtil.d("OOM", "开始添加CanversLayer");
        String path = ExtractFramegFolder + "/" + i;
        LogUtil.d("OOM", "path" + path);
        List<File> getMattingList = FileManager.listFileSortByModifyTime(path);
        LogUtil.d("OOM", "第一张图片地址为" + getMattingList.get(0).getPath());
        Bitmap bp = BitmapFactory.decodeFile(getMattingList.get(0).getPath());
        LogUtil.d("OOM", "图片宽为" + bp.getWidth());
        BitmapLayer bpLayer = execute.addBitmapLayer(bp);
        float layerScale = DRAWPADWIDTH / (float) bpLayer.getLayerWidth();
        LogUtil.d("OOM", "图层的缩放为" + layerScale + "");
        float stickerScale = stickerItem.getScale();
        LogUtil.d("OOM", "gif+图层的缩放为" + layerScale * stickerScale + "");
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
        bpLayer.setPosition(bpLayer.getPadWidth() * percentX, bpLayer.getPositionY());
        float percentY = stickerItem.getTranslationy();
        LogUtil.d("OOM", "percentX=" + percentX + "percentY=" + percentY);
        //   float posY = (bpLayer.getPadHeight() + bpLayer.getLayerHeight()) * percentY - bpLayer.getLayerHeight() / 2.0f;
        bpLayer.setPosition(bpLayer.getPositionX(), bpLayer.getPadHeight() * percentY);

        preTime = stickerItem.getDuration() * 1000 / (float) getMattingList.size();
        LogUtil.d("OOM3", "贴纸的时长为" + stickerItem.getDuration());
        LogUtil.d("OOM3", "贴纸的数量为时长为" + (float) getMattingList.size());
        LogUtil.d("OOM3", "preTime=" + preTime);

        nowProgressTime[0] = preTime;
        CanvasLayer canvasLayer = execute.addCanvasLayer();
        canvasLayer.addCanvasRunnable((canvasLayer1, canvas, currentTime) -> {
            if (currentTime > nowProgressTime[0]) {
                //需要切换新的图了
                nowChooseImageIndex[0]++;
                if (nowChooseImageIndex[0] < getMattingList.size()) {
                    LogUtil.d("CanvasRunnable", "addCanvasRunnable=" + preTime + "currentTime=" + currentTime + "nowChooseImageIndex=" + nowChooseImageIndex);
                    nowProgressTime[0] = preTime + nowProgressTime[0];
                    Bitmap firstBitmap1 = BitmapFactory.decodeFile(getMattingList.get(nowChooseImageIndex[0]).getPath());
                    bpLayer.switchBitmap(firstBitmap1);
                } else {
                    LogUtil.d("OOM", "隐藏当前图层");
                    bpLayer.switchBitmap(Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888));
                    bpLayer.setVisibility(View.GONE);
                }
            }
        });

    }


    /**
     * description ：主计时器
     * creation date: 2020/5/28
     *
     * @param list      贴纸数据列表
     * @param isMatting 是否抠图
     *                  user : zhangtongju
     */
    private void addMainCanversLayer(ArrayList<AllStickerData> list, boolean isMatting) {
        for (int i = 0; i < list.size(); i++) {
            AllStickerData item = list.get(i);
            String pathType = GetPathTypeModel.getInstance().getMediaType(item.getPath());
            if (albumType.isVideo(pathType)) {
                if (isMatting) {
                    intoCanvesCount++;
                    addCanversLayer(item, intoCanvesCount);
                } else {
                    addVideoLayer(item);
                }
            } else {
                if (item.getPath().endsWith(".gif")) {
                    addGifLayer(item);
                } else {
                    addBitmapLayer(item);
                }
            }
        }


        CanvasLayer canvasLayer = execute.addCanvasLayer();
        //思路是记录有动画的layer ，然后动态设置动画
        canvasLayer.addCanvasRunnable((canvasLayer1, canvas, currentTime) -> {
            for (int i = 0; i < hasAnimLayerList.size(); i++) {
                hasAnimLayer animLayer = hasAnimLayerList.get(i);
                float needDurationTime=animCollect.getAnimNeedSubLayerTime(animLayer.ChooseAnimId);

                LogUtil.d("OOM","当前的需要的时间"+needDurationTime*1000);
                LogUtil.d("OOM","当前的时间"+currentTime);
                float xx=currentTime%(needDurationTime*1000);
                LogUtil.d("OOM","当前的余值为"+xx);
                float xxx=xx/(needDurationTime*1000);
                LogUtil.d("OOM","当前的百分比为"+xxx);

                Layer layer = animLayer.getLayer();
                ArrayList<SubLayer> listForSubLayer = animLayer.getSublayerList();
                for (int x = 0; x < listForSubLayer.size(); x++) {
                    animCollect.startAnimForChooseAnim(animLayer.ChooseAnimId, layer, listForSubLayer, new LayerAnimCallback() {
                        @Override
                        public void translationalXY(float x, float y) {

                            LogUtil.d("translationalXY","xxx="+x+"YYY="+y);
                            layer.setPosition(x, y);


                        }

                        @Override
                        public void rotate(float angle) {

                        }

                        @Override
                        public void scale(float size) {

                        }
                    },xxx);



                }
            }
        });

    }


    public interface saveCallback {
        void saveSuccessPath(String path, int progress);
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


    class hasAnimLayer implements Serializable {


        /**
         * ChooseAnimId 动画类型  ，Layer 当前图层  sublayerList 子视图集合
         */
        public hasAnimLayer(AnimType ChooseAnimId, Layer layer, ArrayList<SubLayer> sublayerList) {
            this.layer = layer;
            this.ChooseAnimId = ChooseAnimId;
            this.sublayerList = sublayerList;
        }


        public AnimType getChooseAnimId() {
            return ChooseAnimId;
        }

        public void setChooseAnimId(AnimType chooseAnimId) {
            ChooseAnimId = chooseAnimId;
        }

        public Layer getLayer() {
            return layer;
        }

        public void setLayer(Layer layer) {
            this.layer = layer;
        }

        /**
         * 是否选择了动画，动画id值
         */
        private AnimType ChooseAnimId;


        private Layer layer;

        public ArrayList<SubLayer> getSublayerList() {
            return sublayerList;
        }

        public void setSublayerList(ArrayList<SubLayer> sublayerList) {
            this.sublayerList = sublayerList;
        }

        private ArrayList<SubLayer> sublayerList;

    }


}
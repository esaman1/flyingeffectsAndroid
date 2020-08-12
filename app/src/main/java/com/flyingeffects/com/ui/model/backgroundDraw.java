package com.flyingeffects.com.ui.model;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.enity.AllStickerData;
import com.flyingeffects.com.enity.TransplationPos;
import com.flyingeffects.com.manager.BitmapManager;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.utils.FilterUtils;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.view.animations.CustomMove.AnimCollect;
import com.flyingeffects.com.view.animations.CustomMove.AnimType;
import com.flyingeffects.com.view.animations.CustomMove.LayerAnimCallback;
import com.lansosdk.LanSongFilter.LanSongMaskBlendFilter;
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

    private static int DRAWPADWIDTH = 720;
    private static int DRAWPADHEIGHT = 1280;
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

    private boolean nowUiIsLandscape;

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

    private float percentageH;

    public void toSaveVideo(ArrayList<AllStickerData> list, boolean isMatting, boolean nowUiIsLandscape, float percentageH) {
        this.nowUiIsLandscape = nowUiIsLandscape;
        this.percentageH = percentageH;
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
        totleRenderTime = duration * 1000;
        try {
            if (nowUiIsLandscape) {
                DRAWPADWIDTH = 1280;
                DRAWPADHEIGHT = 720;
            } else {
                DRAWPADWIDTH = 720;
                DRAWPADHEIGHT = 1280;
            }
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
            //设置背景
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

    Layer bgLayer = null;
    private void setMainLayer() {
        LSOVideoOption option;
        try {
            option = new LSOVideoOption(videoPath);
            option.setLooping(true);
            if (!TextUtils.isEmpty(videoVoice)) {
                option.setAudioMute();
            }
            bgLayer = execute.addVideoLayer(option);
            if (!nowUiIsLandscape) {
                bgLayer.setScaledToPadSize();
                bgLayer.setScaleType(LSOScaleType.VIDEO_SCALE_TYPE);
            } else {
                float LayerWidth = bgLayer.getLayerWidth();
                float scale = DRAWPADWIDTH / (float)LayerWidth;
                float LayerHeight = bgLayer.getLayerHeight();
                float xxx=LayerHeight * scale;
                bgLayer.setScaledValue(DRAWPADWIDTH, xxx);



                float halft = xxx / (float) 2;
                float top = xxx * percentageH;
                float needHeight=halft-top;
                LogUtil.d("oom3", "halft=" + halft + "top=" + top +"needHeight="+needHeight);
                bgLayer.setPosition(bgLayer.getPositionX(), needHeight);
            }
            LogUtil.d("OOM", "主图层添加完毕");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addVideoLayer(AllStickerData stickerItem, int i) {
        LSOVideoOption option = null;
        try {
            option = new LSOVideoOption(stickerItem.getPath());
            option.setAudioMute();
            VideoFrameLayer videoLayer = execute.addVideoLayer(option);
            videoLayer.setId(i);
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
            videoLayer.switchFilterTo(FilterUtils.createBlendFilter(context, LanSongMaskBlendFilter.class, stickerItem.getMaskBitmap()));
            if (stickerItem.getChooseAnimId() != null && stickerItem.getChooseAnimId() != AnimType.NULL) {
                int needSublayer = animCollect.getAnimNeedSubLayerCount(stickerItem.getChooseAnimId());
                addVideoSubLayer(needSublayer, videoLayer, stickerItem.getChooseAnimId(), rotate, layerScale * stickerScale);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 增加一个MV图层.
     */
    private void addGifLayer(AllStickerData stickerItem, int id) {
        GifLayer gifLayer = execute.addGifLayer(stickerItem.getPath());
        gifLayer.setId(id);
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
        gifLayer.switchFilterTo(FilterUtils.createBlendFilter(context, LanSongMaskBlendFilter.class, stickerItem.getMaskBitmap()));
        if (stickerItem.getChooseAnimId() != null && stickerItem.getChooseAnimId() != AnimType.NULL) {
            int needSublayer = animCollect.getAnimNeedSubLayerCount(stickerItem.getChooseAnimId());
            addGifSubLayer(needSublayer, gifLayer, stickerItem.getChooseAnimId(), rotate, layerScale * stickerScale);
        }

    }


    /**
     * 增加一个图片图层.
     */
    private void addBitmapLayer(AllStickerData stickerItem, int id) {
        LogUtil.d("OOM", "addBitmapLayer");
        Bitmap bp = BitmapFactory.decodeFile(stickerItem.getPath());
        BitmapLayer bpLayer = execute.addBitmapLayer(bp);
        bpLayer.setId(id);
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
        bpLayer.switchFilterTo(FilterUtils.createBlendFilter(context, LanSongMaskBlendFilter.class, stickerItem.getMaskBitmap()));

        if (stickerItem.getChooseAnimId() != null && stickerItem.getChooseAnimId() != AnimType.NULL) {
            int needSublayer = animCollect.getAnimNeedSubLayerCount(stickerItem.getChooseAnimId());
            addBitmapSubLayer(needSublayer, bpLayer, stickerItem.getChooseAnimId(), rotate, layerScale * stickerScale);
        }
    }


    private void addBitmapSubLayer(int needSublayer, BitmapLayer layer, AnimType ChooseAnimId, float rotate, float stickerScale) {
        ArrayList<SubLayer> listForSubLayer = new ArrayList<>();
        for (int i = 0; i < needSublayer; i++) {
            SubLayer subLayer = layer.addSubLayerUseMainFilter(true);
            subLayer.setScale(stickerScale);
            subLayer.setRotate(rotate);
            listForSubLayer.add(subLayer);
        }
        hasAnimLayer animLayer = new hasAnimLayer(ChooseAnimId, layer, listForSubLayer, stickerScale);
        hasAnimLayerList.add(animLayer);
    }


//    private ArrayList<SubLayer> listForMattingSubLayer = new ArrayList<>();

    private void addMattingBitmapSubLayer(int needSublayer, BitmapLayer layer, AnimType ChooseAnimId, float rotate, float stickerScale, ArrayList<SubLayer> listForMattingSubLayer) {
        listForMattingSubLayer.clear();
        for (int i = 0; i < needSublayer; i++) {
            SubLayer subLayer = layer.addSubLayerUseMainFilter(true);
            subLayer.setScale(stickerScale);
            subLayer.setRotate(rotate);
            listForMattingSubLayer.add(subLayer);
        }

    }


    private void addVideoSubLayer(int needSublayer, VideoFrameLayer layer, AnimType ChooseAnimId, float rotate, float stickerScale) {
        ArrayList<SubLayer> listForSubLayer = new ArrayList<>();
        for (int i = 0; i < needSublayer; i++) {
            SubLayer subLayer = layer.addSubLayerUseMainFilter(true);
            subLayer.setScale(stickerScale);
            subLayer.setRotate(rotate);
            listForSubLayer.add(subLayer);
        }
        hasAnimLayer animLayer = new hasAnimLayer(ChooseAnimId, layer, listForSubLayer, stickerScale);
        hasAnimLayerList.add(animLayer);
    }


    private void addGifSubLayer(int needSublayer, GifLayer layer, AnimType ChooseAnimId, float rotate, float stickerScale) {
        ArrayList<SubLayer> listForSubLayer = new ArrayList<>();
        for (int i = 0; i < needSublayer; i++) {
            SubLayer subLayer = layer.addSubLayerUseMainFilter(true);
            subLayer.setScale(stickerScale);
            subLayer.setRotate(rotate);
            listForSubLayer.add(subLayer);
        }
        hasAnimLayer animLayer = new hasAnimLayer(ChooseAnimId, layer, listForSubLayer, stickerScale);
        hasAnimLayerList.add(animLayer);
    }


    private void addCanversLayer(AllStickerData stickerItem, int i) {
        ArrayList<SubLayer> listForMattingSubLayer = new ArrayList<>();
        float needDt = 0;
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
        bpLayer.setId(100 + i);

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
        bpLayer.switchFilterTo(FilterUtils.createBlendFilter(context, LanSongMaskBlendFilter.class, stickerItem.getMaskBitmap()));
        preTime = stickerItem.getDuration() * 1000 / (float) getMattingList.size();

        if (stickerItem.getChooseAnimId() != null && stickerItem.getChooseAnimId() != AnimType.NULL) {
            int needSublayer = animCollect.getAnimNeedSubLayerCount(stickerItem.getChooseAnimId());
            addMattingBitmapSubLayer(needSublayer, bpLayer, stickerItem.getChooseAnimId(), rotate, layerScale * stickerScale, listForMattingSubLayer);
            nowProgressTime[0] = preTime;
            float needDurationTime = animCollect.getAnimNeedSubLayerTime(stickerItem.getChooseAnimId());
            needDt = needDurationTime * 1000;
        }
        CanvasLayer canvasLayer = execute.addCanvasLayer();
        float finalNeedDt = needDt;
        canvasLayer.addCanvasRunnable((canvasLayer1, canvas, currentTime) -> {
            if (stickerItem.getChooseAnimId() != null && stickerItem.getChooseAnimId() != AnimType.NULL) {
                float percentage;
                if (stickerItem.getChooseAnimId() == AnimType.BOTTOMTOCENTER2 || stickerItem.getChooseAnimId() == AnimType.SUPERSTAR2) {
                    if (currentTime > finalNeedDt) {
                        percentage = 1;
                    } else {
                        percentage = currentTime / (finalNeedDt);
                    }
                } else {
                    //循环
                    float remainder = currentTime % (finalNeedDt);
                    percentage = remainder / (finalNeedDt);
                }

                animCollect.startAnimForChooseAnim(stickerItem.getChooseAnimId(), bpLayer, listForMattingSubLayer, new LayerAnimCallback() {
                    @Override
                    public void translationalXY(ArrayList<TransplationPos> listForTranslaptionPosition) {
                        TransplationPos transplationPos = listForTranslaptionPosition.get(0);
                        if (transplationPos.getToY() != 0) {
                            LogUtil.d("translationalXY", "yy=" + transplationPos.getToY());
                            bpLayer.setPosition(bpLayer.getPositionX(), bpLayer.getPadHeight() * transplationPos.getToY());
                        }
                        if (transplationPos.getToX() != 0) {
                            LogUtil.d("translationalXY", "xx=" + transplationPos.getToX());
                            bpLayer.setPosition(bpLayer.getPadWidth() * transplationPos.getToX(), bpLayer.getPositionY());
                        }
                        if (listForMattingSubLayer.size() > 0) {
                            for (int i = 1; i <= listForMattingSubLayer.size(); i++) {
                                TransplationPos subTransplationPos = listForTranslaptionPosition.get(i);
                                SubLayer subLayer = listForMattingSubLayer.get(i - 1);
                                subLayer.setPosition(subLayer.getPositionX(), subLayer.getPadHeight() * subTransplationPos.getToY());
                                subLayer.setPosition(subLayer.getPadWidth() * subTransplationPos.getToX(), subLayer.getPositionY());
                            }
                        }

                    }

                    @Override
                    public void rotate(ArrayList<Float> angle) {
                        float needrotate = angle.get(0);
                        bpLayer.setRotate(needrotate);
                        if (listForMattingSubLayer != null && listForMattingSubLayer.size() > 0) {
                            for (int i = 1; i <= listForMattingSubLayer.size(); i++) {
                                SubLayer subLayer = listForMattingSubLayer.get(i - 1);
                                subLayer.setScale(angle.get(i));
                            }
                        }

                    }

                    @Override
                    public void scale(ArrayList<Float> angle) {
                        float nowScale = layerScale * stickerScale;
                        bpLayer.setScale(nowScale + nowScale * angle.get(0));
                        if (listForMattingSubLayer != null && listForMattingSubLayer.size() > 0) {
                            for (int i = 1; i <= listForMattingSubLayer.size(); i++) {
                                SubLayer subLayer = listForMattingSubLayer.get(i - 1);
                                subLayer.setScale(nowScale + nowScale * angle.get(i));
                            }
                        }
                    }
                }, percentage);

            }

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
                    addVideoLayer(item, i);
                }
            } else {
                if (item.getPath().endsWith(".gif")) {
                    addGifLayer(item, i);
                } else {
                    addBitmapLayer(item, i);
                }
            }
        }

        if (hasAnimLayerList != null && hasAnimLayerList.size() > 0) {
            //这里是不包括给视频抠像的，如果视频抠像，那么需要单独的动画
            CanvasLayer canvasLayer = execute.addCanvasLayer();
            //思路是记录有动画的layer ，然后动态设置动画
            canvasLayer.addCanvasRunnable((canvasLayer1, canvas, currentTime) -> {
                for (int i = 0; i < hasAnimLayerList.size(); i++) {
                    hasAnimLayer animLayer = hasAnimLayerList.get(i);
                    float needDurationTime = animCollect.getAnimNeedSubLayerTime(animLayer.ChooseAnimId);
                    float percentage;
                    if (animLayer.ChooseAnimId == AnimType.BOTTOMTOCENTER2 || animLayer.ChooseAnimId == AnimType.SUPERSTAR2) {
                        float needDt = needDurationTime * 1000;
                        if (currentTime > needDt) {
                            percentage = 1;
                        } else {
                            percentage = currentTime / (needDt);
                        }
                    } else {
                        //循环
                        float needDt = needDurationTime * 1000;
                        float remainder = currentTime % (needDt);
                        percentage = remainder / (needDt);
                    }
                    Layer layer = animLayer.getLayer();
                    ArrayList<SubLayer> listForSubLayer = animLayer.getSublayerList();
                    animCollect.startAnimForChooseAnim(animLayer.ChooseAnimId, layer, listForSubLayer, new LayerAnimCallback() {
                        @Override
                        public void translationalXY(ArrayList<TransplationPos> listForTranslaptionPosition) {
                            TransplationPos transplationPos = listForTranslaptionPosition.get(0);
                            if (transplationPos.getToY() != 0) {
                                LogUtil.d("translationalXY", "yy=" + transplationPos.getToY());
                                layer.setPosition(layer.getPositionX(), layer.getPadHeight() * transplationPos.getToY());
                            }
                            if (transplationPos.getToX() != 0) {
                                LogUtil.d("translationalXY", "xx=" + transplationPos.getToX());
                                layer.setPosition(layer.getPadWidth() * transplationPos.getToX(), layer.getPositionY());
                            }
                            for (int i = 1; i <= listForSubLayer.size(); i++) {
                                TransplationPos subTransplationPos = listForTranslaptionPosition.get(i);
                                SubLayer subLayer = listForSubLayer.get(i - 1);
                                if (subTransplationPos.getToY() != 0) {
                                    subLayer.setPosition(subLayer.getPositionX(), subLayer.getPadHeight() * subTransplationPos.getToY());
                                }
                                if (subTransplationPos.getToX() != 0) {
                                    subLayer.setPosition(subLayer.getPadWidth() * subTransplationPos.getToX(), subLayer.getPositionY());
                                }
                            }
                        }

                        @Override
                        public void rotate(ArrayList<Float> angleList) {
                            float angle = angleList.get(0);
                            layer.setRotate(angle);
                            for (int i = 1; i <= listForSubLayer.size(); i++) {
                                float angleItem = angleList.get(i);
                                SubLayer subLayer = listForSubLayer.get(i - 1);
                                subLayer.setRotate(angleItem);
                            }
                        }

                        @Override
                        public void scale(ArrayList<Float> angle) {
                            float nowScale = animLayer.getScale();
                            layer.setScale(nowScale + nowScale * angle.get(0));
                            for (int i = 1; i <= listForSubLayer.size(); i++) {
                                SubLayer subLayer = listForSubLayer.get(i - 1);
                                subLayer.setScale(nowScale + nowScale * angle.get(i));
                            }
                        }
                    }, percentage);
                }
            });
        }
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
        public hasAnimLayer(AnimType ChooseAnimId, Layer layer, ArrayList<SubLayer> sublayerList, float scale) {
            this.layer = layer;
            this.ChooseAnimId = ChooseAnimId;
            this.sublayerList = sublayerList;
            this.scale = scale;
        }

        public float getScale() {
            return scale;
        }

        public void setScale(float scale) {
            this.scale = scale;
        }

        private float scale;


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
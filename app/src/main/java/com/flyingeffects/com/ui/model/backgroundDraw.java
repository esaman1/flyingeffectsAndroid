package com.flyingeffects.com.ui.model;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.enity.AllStickerData;
import com.flyingeffects.com.enity.TransplationPos;
import com.flyingeffects.com.manager.BitmapManager;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.utils.BitmapUtils;
import com.flyingeffects.com.utils.FilterUtils;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.ToastUtil;
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
import com.lansosdk.videoeditor.MediaInfo;
import com.shixing.sxve.ui.albumType;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

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
    private long duration;
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

    private AnimCollect animCollect;

    private boolean nowUiIsLandscape;
    private long nowCurtime;

    private long musicStartTime;
    private long musicEndTime;
    boolean isBackgroundTemplate;

    /**
     * description ：后台绘制，如果videoVoice不为null,那么需要把主视频图层的声音替换为用户选择的背景声音
     * imagePath 如果videoPath没有且imagePath 有的情况，需要把绿幕背景替换为图片背景
     * creation date: 2020/4/23
     * user : zhangtongju
     */
    public backgroundDraw(Context context, String videoPath, String videoVoice, String imagePath, long musicStartTime, long musicEndTime, long needKeepDuration, saveCallback callback, AnimCollect animCollect,boolean isBackgroundTemplate) {
        this.context = context;
        this.videoPath = videoPath;
        this.videoVoice = videoVoice;
        this.imagePath = imagePath;
        this.animCollect = animCollect;
        this.musicStartTime = musicStartTime;
        this.musicEndTime = musicEndTime;
        LogUtil.d("OOM5","musicStartTime="+musicStartTime);
        LogUtil.d("OOM5","musicEndTime="+musicEndTime);
        this.callback = callback;
        this.isBackgroundTemplate = isBackgroundTemplate;
//        waitingProgress = new WaitingDialog_progress(context);
        duration = needKeepDuration;
        if (duration == 0) {
            if (!TextUtils.isEmpty(videoPath)) {
                MediaInfo mediaInfo = new MediaInfo(videoPath);
                mediaInfo.prepare();
                duration = (long) (mediaInfo.vDuration * 1000);
                mediaInfo.release();
            }
        }
        LogUtil.d("OOM", "backgroundDrawdurationF=" + duration);
        LogUtil.d("OOM", "videoVoice=" + videoVoice);
        intoCanvesCount = 0;
        FileManager fileManager = new FileManager();
        ExtractFramegFolder = fileManager.getFileCachePath(BaseApplication.getInstance(), "ExtractFrame");
    }

    private float percentageH;
    long cutStartTime;
    long cutEndTime;

    public void setCutStartTime(long cutStartTime){
        this.cutStartTime = cutStartTime;
    }

    public void setCutEndTime(long cutEndTime){
        this.cutEndTime = cutEndTime;
    }


    public void toSaveVideo(ArrayList<AllStickerData> list, boolean isMatting, boolean nowUiIsLandscape, float percentageH) {
        nowCurtime = System.currentTimeMillis();
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
        try {
            if (nowUiIsLandscape) {
                DRAWPADWIDTH = 1280;
                DRAWPADHEIGHT = 720;
            } else {
                DRAWPADWIDTH = 720;
                DRAWPADHEIGHT = 1280;
            }
            execute = new DrawPadAllExecute2(context, DRAWPADWIDTH, DRAWPADHEIGHT, duration * 1000);
            execute.setFrameRate(FRAME_RATE);

            LogUtil.d("OOM25", "时长为" + duration * 1000);
            execute.setEncodeBitrate(5 * 1024 * 1024);
            execute.setOnLanSongSDKErrorListener(message -> {
                LogUtil.d("OOM2", "错误信息为" + message);
                callback.saveSuccessPath("", 10000);
            });
            execute.setOnLanSongSDKProgressListener((l, i) -> {

                if (noVideo) {
                    callback.saveSuccessPath("", i);
                } else {
                    float f_progress = (i / (float) 100) * 75;
                    int progress;
                    if (isMatting) {
                        progress = (int) (25 + f_progress);
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
                long time = System.currentTimeMillis() - nowCurtime;
                statisticsSaveDuration(time, context);
                //todo 需要移除全部的子图层
                execute.release();
                Log.d("OOM", "exportPath=" + exportPath);
            });
            Observable.create(new Observable.OnSubscribe<Boolean>() {
                @Override
                public void call(Subscriber<? super Boolean> subscriber) {
                    //设置背景
                    if (!TextUtils.isEmpty(videoPath)) {
                        setMainLayer();
                    } else {
                        if (!TextUtils.isEmpty(imagePath)) {
                            Bitmap bt_nj = BitmapManager.getInstance().getOrientationBitmap(imagePath);
                            BitmapUtils bpUtils = new BitmapUtils();
                            bt_nj = bpUtils.zoomImg2(bt_nj, execute.getPadWidth() / 16 * 16, execute.getPadHeight() / 16 * 16);
                            execute.addBitmapLayer(bt_nj,0, Long.MAX_VALUE);
                        } else {
                            execute.setBackgroundColor(Color.parseColor("#1FA400"));
                        }
                    }
                    if (!TextUtils.isEmpty(videoVoice)) {
                        if (musicEndTime == 0) {
                            //如果有videoVoice 字段，那么需要设置在对应的主图层上面去
                            execute.addAudioLayer(videoVoice, false);
                        } else {
                            //如果有videoVoice 字段，那么需要设置在对应的主图层上面去
                            execute.addAudioLayer(videoVoice, (musicStartTime-cutStartTime) * 1000, 0, (musicEndTime -cutStartTime) * 1000);
                        }
                    }
                    addMainCanversLayer(list, isMatting);
                    boolean started = execute.start();
                    subscriber.onNext(started);
                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
                @Override
                public void call(Boolean aBoolean) {
                    if (!aBoolean) {
                        ToastUtil.showToast("导出失败");
                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
        } catch (Exception e) {
            callback.saveSuccessPath("", 10000);
            LogUtil.d("OOM", e.getMessage());
            e.printStackTrace();
        }
    }

    private void setMainLayer() {
        LSOVideoOption option;
        try {
            option = new LSOVideoOption(videoPath);
            option.setLooping(false);
            if (!TextUtils.isEmpty(videoVoice)) {
                option.setAudioMute();
            }
            if (isBackgroundTemplate) {
                option.setCutDurationUs(cutStartTime * 1000, cutEndTime * 1000);
            }
            Layer bgLayer = execute.addVideoLayer(option,0, Long.MAX_VALUE,false,true);
            if (!nowUiIsLandscape) {
                bgLayer.setScaledToPadSize();
                bgLayer.setScaleType(LSOScaleType.VIDEO_SCALE_TYPE);
            } else {
                float LayerWidth = bgLayer.getLayerWidth();
                float scale = DRAWPADWIDTH / (float) LayerWidth;
                float LayerHeight = bgLayer.getLayerHeight();
                float needDrawHeight = LayerHeight * scale;
                bgLayer.setScaledValue(DRAWPADWIDTH, needDrawHeight);
                float halft = needDrawHeight / (float) 2;
                float top = needDrawHeight * percentageH;
                float needHeight = halft - top;
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
            long STARTTime = stickerItem.getShowStickerStartTime();
            long endTime = stickerItem.getShowStickerEndTime() * 1000;
            VideoFrameLayer videoLayer;
            if (endTime != 0) {
                if (isBackgroundTemplate) {
                    if (STARTTime <= cutStartTime) {
                        STARTTime = 0;
                    } else {
                        STARTTime = Math.max(STARTTime, cutStartTime) - Math.min(STARTTime, cutStartTime);
                    }
                    videoLayer = execute.addVideoLayer(option, STARTTime * 1000,
                            endTime >= cutEndTime * 1000 ? Long.MAX_VALUE :
                                    cutStartTime > 0 ? duration * 1000 - (cutEndTime * 1000 - endTime) : endTime, false, false);
                } else {
                    videoLayer = execute.addVideoLayer(option, 0,
                            endTime >= duration * 1000 ? Long.MAX_VALUE : endTime, false, false);
                }
            } else {
                videoLayer = execute.addVideoLayer(option);
            }

            videoLayer.setId(i);
            float layerScale;
            videoLayer.setScaleType(LSOScaleType.CROP_FILL_COMPOSITION);
            layerScale = DRAWPADWIDTH / (float) videoLayer.getLayerWidth();
            LogUtil.d("OOM", "图层的缩放为" + layerScale + "");
            float stickerScale = stickerItem.getScale();
            LogUtil.d("OOM", "gif+图层的缩放为" + layerScale * stickerScale + "");
//            videoLayer.setScale(layerScale * stickerScale);
            if (nowUiIsLandscape) {
                float LayerWidth = videoLayer.getLayerWidth();
                float scale = DRAWPADWIDTH * stickerScale / (float) LayerWidth;
                float LayerHeight = videoLayer.getLayerHeight();
                float needDrawHeight = LayerHeight * scale;
                videoLayer.setScaledValue(DRAWPADWIDTH * stickerScale, needDrawHeight);
            } else {
                videoLayer.setScale(layerScale * stickerScale);
            }

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
        long endTime = stickerItem.getShowStickerEndTime() * 1000;
        long STARTTime = stickerItem.getShowStickerStartTime();
        GifLayer gifLayer;
        if (endTime != 0) {
            if (isBackgroundTemplate) {
                if (STARTTime <= cutStartTime) {
                    STARTTime = 0;
                } else {
                    STARTTime = Math.max(STARTTime, cutStartTime) - Math.min(STARTTime, cutStartTime);
                }

                LogUtil.d("OOM44","开始增加的时间为"+STARTTime+"--消失的时间为"+ ( endTime >= cutEndTime * 1000 ? Long.MAX_VALUE : cutStartTime > 0 ? duration * 1000 - (cutEndTime * 1000 - endTime) : endTime));



                gifLayer = execute.addGifLayer(stickerItem.getPath(), STARTTime * 1000,
                        endTime >= cutEndTime * 1000 ? Long.MAX_VALUE : cutStartTime > 0 ? duration * 1000 - (cutEndTime * 1000 - endTime) : endTime);
            } else {
                gifLayer = execute.addGifLayer(stickerItem.getPath(), 0,
                        endTime >= duration * 1000 ? Long.MAX_VALUE : endTime);
            }
        } else {
            gifLayer = execute.addGifLayer(stickerItem.getPath());
        }
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
        LogUtil.d("OOM", "Path" + stickerItem.getPath());

        long endTime = stickerItem.getShowStickerEndTime();
        LogUtil.d("OOM4", "endTime" + endTime);

        long STARTTime = stickerItem.getShowStickerStartTime();
        LogUtil.d("OOM4", "STARTTime" + STARTTime);
        BitmapLayer bpLayer;
        if (endTime != 0) {
            if (isBackgroundTemplate) {
                if (STARTTime <= cutStartTime) {
                    STARTTime = 0;
                } else {
                    STARTTime = Math.max(STARTTime, cutStartTime) - Math.min(STARTTime, cutStartTime);
                }

                bpLayer = execute.addBitmapLayer(bp, STARTTime * 1000, endTime * 1000 >= cutEndTime * 1000 ? Long.MAX_VALUE :
                        cutStartTime > 0 ? duration * 1000 - (cutEndTime * 1000 - endTime * 1000) : endTime * 1000);
            } else {
                bpLayer = execute.addBitmapLayer(bp, 0, endTime * 1000 >= duration * 1000 ? Long.MAX_VALUE : endTime * 1000);
            }
        } else {
            bpLayer = execute.addBitmapLayer(bp);
        }
        bpLayer.setId(id);
        float layerScale = DRAWPADWIDTH / (float) bpLayer.getLayerWidth();
        LogUtil.d("OOM5", "图层的缩放为" + layerScale + "");

        float stickerScale = stickerItem.getScale();
        LogUtil.d("OOM5", "gif+图层的缩放为" + layerScale * stickerScale + "");
        bpLayer.setScale(layerScale * stickerScale);
        LogUtil.d("OOM5", "mvLayerH=" + bpLayer.getLayerHeight() + "");
        LogUtil.d("OOM5", "mvLayerW=" + bpLayer.getLayerWidth() + "");
        LogUtil.d("OOM5", "mvLayerpadW=" + bpLayer.getPadWidth() + "");
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


    private int imageCoverWidth;
    private int imageCoverHeight;
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
        imageCoverWidth=bp.getWidth();
        imageCoverHeight=bp.getHeight();
        bp= MattingImage.mattingSingleImg(bp,imageCoverWidth,imageCoverHeight);
        LogUtil.d("OOM", "图片宽为" + bp.getWidth());
        long STARTTime = stickerItem.getShowStickerStartTime();
        long endTime = stickerItem.getShowStickerEndTime();
        BitmapLayer bpLayer;
        LogUtil.d("OOM4", "endTime" + endTime);
        if (endTime != 0) {
            if (isBackgroundTemplate) {
                if (STARTTime <= cutStartTime) {
                    STARTTime = 0;
                } else {
                    STARTTime = Math.max(STARTTime, cutStartTime) - Math.min(STARTTime, cutStartTime);
                }

                bpLayer = execute.addBitmapLayer(bp, STARTTime * 1000, endTime * 1000 >= cutEndTime * 1000 ? Long.MAX_VALUE :
                        cutStartTime > 0 ? duration * 1000 - (cutEndTime * 1000 - endTime * 1000) : endTime * 1000);
            } else {
                bpLayer = execute.addBitmapLayer(bp, 0, endTime * 1000 >= duration * 1000 ? Long.MAX_VALUE : endTime * 1000);
            }
        } else {
            bpLayer = execute.addBitmapLayer(bp);
        }
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
        long finalSTARTTime = STARTTime * 1000;
        canvasLayer.addCanvasRunnable((canvasLayer1, canvas, currentTime) -> {
            if (finalSTARTTime != 0) {
                currentTime = currentTime - finalSTARTTime;
                if (currentTime < 0) {
                    currentTime = 0;
                }
            }
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
                        if (listForMattingSubLayer.size() > 0) {
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
                        if (listForMattingSubLayer.size() > 0) {
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
                    firstBitmap1= MattingImage.mattingSingleImg(firstBitmap1,imageCoverWidth,imageCoverHeight);
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
                    if (animLayer.ChooseAnimId == AnimType.BOTTOMTOCENTER2 || animLayer.ChooseAnimId == AnimType.SUPERSTAR2 || animLayer.ChooseAnimId == AnimType.CIRCLECLONED2 || animLayer.ChooseAnimId == AnimType.FIVEPOINTSTART2|| animLayer.ChooseAnimId == AnimType.Z|| animLayer.ChooseAnimId == AnimType.FOUNDER|| animLayer.ChooseAnimId == AnimType.SUPERLOVE) {
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

    public void statisticsSaveDuration(long duration, Context context) {
        if (duration <= 10000) {
            statisticsEventAffair.getInstance().setFlag(context, "MattingDuration", "小于10秒");
        } else if (duration <= 20000) {
            statisticsEventAffair.getInstance().setFlag(context, "MattingDuration", "小于20秒");
        } else if (duration <= 30000) {
            statisticsEventAffair.getInstance().setFlag(context, "MattingDuration", "小于30秒");
        } else if (duration <= 40000) {
            statisticsEventAffair.getInstance().setFlag(context, "MattingDuration", "小于40秒");
        } else if (duration <= 50000) {
            statisticsEventAffair.getInstance().setFlag(context, "MattingDuration", "小于50秒");
        } else if (duration <= 60000) {
            statisticsEventAffair.getInstance().setFlag(context, "MattingDuration", "小于1分钟");
        } else if (duration <= 120000) {
            statisticsEventAffair.getInstance().setFlag(context, "MattingDuration", "小于2分钟");
        } else if (duration <= 180000) {
            statisticsEventAffair.getInstance().setFlag(context, "MattingDuration", "小于3分钟");
        } else if (duration <= 240000) {
            statisticsEventAffair.getInstance().setFlag(context, "MattingDuration", "小于4分钟");
        } else if (duration <= 300000) {
            statisticsEventAffair.getInstance().setFlag(context, "MattingDuration", "小于5分钟");
        } else if (duration <= 360000) {
            statisticsEventAffair.getInstance().setFlag(context, "MattingDuration", "小于6分钟");
        } else if (duration <= 420000) {
            statisticsEventAffair.getInstance().setFlag(context, "MattingDuration", "小于7分钟");
        } else if (duration <= 480000) {
            statisticsEventAffair.getInstance().setFlag(context, "MattingDuration", "小于8分钟");
        } else if (duration <= 540000) {
            statisticsEventAffair.getInstance().setFlag(context, "MattingDuration", "小于9分钟");
        } else if (duration <= 600000) {
            statisticsEventAffair.getInstance().setFlag(context, "MattingDuration", "小于10分钟");
        } else if (duration <= 1200000) {
            statisticsEventAffair.getInstance().setFlag(context, "MattingDuration", "小于20分钟");
        } else if (duration <= 1800000) {
            statisticsEventAffair.getInstance().setFlag(context, "MattingDuration", "小于30分钟");
        } else if (duration <= 2400000) {
            statisticsEventAffair.getInstance().setFlag(context, "MattingDuration", "小于40分钟");
        } else if (duration <= 3000000) {
            statisticsEventAffair.getInstance().setFlag(context, "MattingDuration", "小于50分钟");
        } else if (duration <= 3600000) {
            statisticsEventAffair.getInstance().setFlag(context, "MattingDuration", "小于60分钟");
        } else if (duration <= 4200000) {
            statisticsEventAffair.getInstance().setFlag(context, "MattingDuration", "小于70分钟");
        } else if (duration <= 5400000) {
            statisticsEventAffair.getInstance().setFlag(context, "MattingDuration", "小于90分钟");
        } else if (duration <= 7200000) {
            statisticsEventAffair.getInstance().setFlag(context, "MattingDuration", "小于120分钟");
        } else if (duration <= 9600000) {
            statisticsEventAffair.getInstance().setFlag(context, "MattingDuration", "小于150分钟");
        } else {
            statisticsEventAffair.getInstance().setFlag(context, "MattingDuration", "大于150分钟");
        }
    }


}
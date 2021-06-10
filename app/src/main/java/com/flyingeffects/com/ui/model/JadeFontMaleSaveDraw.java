package com.flyingeffects.com.ui.model;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.flyingeffects.com.manager.BitmapManager;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.utils.BitmapUtils;
import com.flyingeffects.com.utils.FileUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.imaginstudio.imagetools.pixellab.TextObject.TextComponent;
import com.imaginstudio.imagetools.pixellab.textContainer;
import com.lansosdk.box.BitmapLayer;
import com.lansosdk.box.LSOScaleType;
import com.lansosdk.box.LSOVideoOption;
import com.lansosdk.box.Layer;
import com.lansosdk.videoeditor.DrawPadAllExecute2;

import java.io.File;
import java.io.IOException;

/**
 * description ：玉体字后台保存
 * creation date: 2021/6/9
 * user : zhangtongju
 */
public class JadeFontMaleSaveDraw {

    private final String tag = "JadeFontMaleDraw";
    private static final int FRAME_RATE = 20;
    private DrawPadAllExecute2 execute;
    private final Context context;
    private final String videoPath;
    private final int changeMusicIndex;
    private final String chooseExtractedAudioBjMusicPath;
    private final String imagePath;
    private final textContainer container;
    private final String outputPath;

    public JadeFontMaleSaveDraw(Context context, String videoPath, int changeMusicIndex, String chooseExtractedAudioBjMusicPath, String imagePath, textContainer container) {
        this.context = context;
        this.videoPath = videoPath;
        this.changeMusicIndex = changeMusicIndex;
        this.chooseExtractedAudioBjMusicPath = chooseExtractedAudioBjMusicPath;
        this.imagePath = imagePath;
        this.container = container;
        FileManager fileManager = new FileManager();
        outputPath = fileManager.getFileCachePath(context, "jadeSavePath");

    }


    public void saveVideo(long cutStartTime, long cutEndTime, boolean nowUiIsLandscape, float percentageH, jadeFontMaleSaveCallback callback) {
        int DRAWPADWIDTH = nowUiIsLandscape ? 1280 : 720;
        int DRAWPADHEIGHT = nowUiIsLandscape ? 720 : 1280;
        long duration = cutEndTime - cutStartTime;
        try {
            execute = new DrawPadAllExecute2(context, DRAWPADWIDTH, DRAWPADHEIGHT, duration * 1000);
            execute.setFrameRate(FRAME_RATE);
            execute.setEncodeBitrate(5 * 1024 * 1024);
            execute.setOnLanSongSDKErrorListener(i -> LogUtil.d(tag, "onLanSongSDKError=" + i));
            execute.setOnLanSongSDKProgressListener((l, i) -> {
                if (callback != null) {
                    callback.ProgressListener(i);
                }
                LogUtil.d(tag, "setOnLanSongSDKProgressListener=" + i);
            });
            execute.setOnLanSongSDKCompletedListener(s -> {
                String albumPath = outputPath + "/video.mp4";
                try {
                    FileUtil.copyFile(new File(s), albumPath);
                    LogUtil.d(tag, "onLanSongSDKCompleted=" + albumPath);
                    if (callback != null) {
                        callback.drawCompleted(albumPath);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.drawCompleted("");
                    }
                }
                execute.release();
            });
            if (!TextUtils.isEmpty(videoPath)) {
                try {
                    LSOVideoOption option = new LSOVideoOption(videoPath);
                    option.setLooping(false);
                    //使用视频中的音频
                    if (changeMusicIndex == -1 || changeMusicIndex == 0) {
                        option.setAudioVolume(1f);
                    } else {
                        option.setAudioMute();
                        //选择了提取音频作为视频的背景音频
                        if (!TextUtils.isEmpty(chooseExtractedAudioBjMusicPath) && changeMusicIndex == 2) {
                            execute.addAudioLayer(chooseExtractedAudioBjMusicPath, 0, 0, cutEndTime);
                        }
                    }
                    option.setCutDurationUs(cutStartTime * 1000, cutEndTime * 1000);
                    Layer bgLayer = execute.addVideoLayer(option, cutStartTime * 1000, Long.MAX_VALUE, false, true);
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Bitmap bt_nj = BitmapManager.getInstance().getOrientationBitmap(imagePath);
                bt_nj = BitmapUtils.zoomImg2(bt_nj, execute.getPadWidth() / 16 * 16, execute.getPadHeight() / 16 * 16);
                execute.addBitmapLayer(bt_nj, 0, Long.MAX_VALUE);
                if (!TextUtils.isEmpty(chooseExtractedAudioBjMusicPath) && changeMusicIndex == 2) {
                    execute.addAudioLayer(chooseExtractedAudioBjMusicPath, 0, 0, cutEndTime);
                }
            }
            addMainCanvasLayer();
            execute.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * description ：主计时器
     * creation date: 2020/5/28
     */
    private void addMainCanvasLayer() {
        for (int i = 0; i < container.getChildCount(); i++) {
            TextComponent item = (TextComponent) container.getChildAt(i);
            addBitmapLayer(item, i);
        }
    }


    /**
     * 增加一个图片图层.
     */
    private void addBitmapLayer(TextComponent textComponent, int id) {
        String path = textComponent.getTextJadePath();
        Bitmap bp = BitmapFactory.decodeFile(path);
        LogUtil.d(tag, "Path" + path);
        long endTime = textComponent.getEndTime() * 1000;
        LogUtil.d(tag, "endTime" + endTime);
        long startTime = textComponent.getStartTime() * 1000;
        LogUtil.d(tag, "startTime" + startTime + "endTime=" + endTime);
        BitmapLayer bpLayer;
        if (endTime != 0) {
            bpLayer = execute.addBitmapLayer(bp, startTime, endTime);
        } else {
            bpLayer = execute.addBitmapLayer(bp);
        }
        bpLayer.setId(id);
//        float layerScale = DRAWPADWIDTH / (float) bpLayer.getLayerWidth();
//        LogUtil.d("OOM5", "图层的缩放为" + layerScale + "");
//
//        float stickerScale = stickerItem.getScale();
//        LogUtil.d("OOM5", "gif+图层的缩放为" + layerScale * stickerScale + "");
//        bpLayer.setScale(layerScale * stickerScale);
//        LogUtil.d("OOM5", "mvLayerH=" + bpLayer.getLayerHeight() + "");
//        LogUtil.d("OOM5", "mvLayerW=" + bpLayer.getLayerWidth() + "");
//        LogUtil.d("OOM5", "mvLayerpadW=" + bpLayer.getPadWidth() + "");
//        int rotate = (int) stickerItem.getRotation();
//        if (rotate < 0) {
//            rotate = 360 + rotate;
//        }
//        LogUtil.d("OOM", "rotate=" + rotate);
//        bpLayer.setRotate(rotate);
//        LogUtil.d("OOM", "Scale=" + stickerItem.getScale() + "");
//        //蓝松这边规定，0.5就是刚刚居中的位置
//        float percentX = stickerItem.getTranslationX();
////        float posX = (bpLayer.getPadWidth() + bpLayer.getLayerWidth()) * percentX - bpLayer.getLayerWidth() / 2.0f;
//        bpLayer.setPosition(bpLayer.getPadWidth() * 0.5f, bpLayer.getPadHeight()*0.5f);
//
//        float percentY = stickerItem.getTranslationy();
//        LogUtil.d("OOM", "percentX=" + percentX + "percentY=" + percentY);
//        //   float posY = (bpLayer.getPadHeight() + bpLayer.getLayerHeight()) * percentY - bpLayer.getLayerHeight() / 2.0f;
//        bpLayer.setPosition(bpLayer.getPositionX(), bpLayer.getPadHeight() * percentY);
//        bpLayer.switchFilterTo(FilterUtils.createBlendFilter(context, LanSongMaskBlendFilter.class, stickerItem.getMaskBitmap()));
    }


    public interface jadeFontMaleSaveCallback {
        void drawCompleted(String path);

        void ProgressListener(int progress);
    }


}

package com.flyingeffects.com.ui.model;

import android.text.TextUtils;

import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.commonlyModel.getVideoInfo;
import com.flyingeffects.com.enity.AllStickerData;
import com.flyingeffects.com.enity.VideoInfo;
import com.flyingeffects.com.utils.ScreenCaptureUtil;
import com.flyingeffects.com.view.StickerView;
import com.shixing.sxve.ui.AlbumType;

import java.util.ArrayList;

public class GetAllStickerDataModel {


    private static GetAllStickerDataModel thisModel;

    //字体
    private ArrayList<String> titleStyle = new ArrayList<>();
    //效果
    private ArrayList<String> titleEffect = new ArrayList<>();

    //边框
    private ArrayList<String> titleFrame = new ArrayList<>();

    public static GetAllStickerDataModel getInstance() {
        if (thisModel == null) {
            thisModel = new GetAllStickerDataModel();
        }
        return thisModel;
    }


    public AllStickerData getStickerData(StickerView stickerView, boolean isMatting, VideoInfo videoInfo) {
        AllStickerData stickerData = new AllStickerData();
        titleEffect.clear();
        titleFrame.clear();
        titleStyle.clear();
        if (stickerView.getIsTextSticker()) {
            titleEffect.add(stickerView.getTextEffectTitle());
            titleStyle.add(stickerView.getTextStyleTitle());
            titleFrame.add(stickerView.getTextFrameTitle());
            ScreenCaptureUtil screenCaptureUtil = new ScreenCaptureUtil(BaseApplication.getInstance());
            stickerData.setBoxH((int) stickerView.getmHelpBoxRectH());
            stickerData.setBoxW((int) stickerView.getmHelpBoxRectW());
            stickerData.setScale(stickerView.getScale());
            stickerData.setMaskBitmap(stickerView.getMaskBitmap());
            stickerData.setMirrorBitmap(stickerView.getMirrorBitmap());
            stickerData.setMaterial(stickerView.getIsmaterial());
            stickerData.setTranslationX(stickerView.getTranslationX());
            stickerData.setText(true);
            stickerData.setTranslationy(stickerView.getTranslationY());
            String textImagePath = screenCaptureUtil.getFilePath(stickerView);
            stickerData.setOriginalPath(textImagePath);
            stickerData.setPath(textImagePath);

        } else {
            stickerData.setBoxH((int) stickerView.getmHelpBoxRectH());
            stickerData.setBoxW((int) stickerView.getmHelpBoxRectW());
            stickerData.setText(false);
            stickerData.setRotation(stickerView.getRotateAngle());
            stickerData.setScale(stickerView.getScale());
            stickerData.setMaskBitmap(stickerView.getMaskBitmap());
            stickerData.setMirrorBitmap(stickerView.getMirrorBitmap());
            stickerData.setMaterial(stickerView.getIsmaterial());
            stickerData.setTranslationX(stickerView.getTranslationX());
            stickerData.setTranslationy(stickerView.getTranslationY());
            if (!TextUtils.isEmpty(stickerView.getOriginalPath())) {
                String pathType = GetPathTypeModel.getInstance().getMediaType(stickerView.getOriginalPath());
                stickerData.setVideo(AlbumType.isVideo(pathType));
            }
            if (stickerView.getComeFrom()) {
                //来自相册，不是gif
                if (isMatting) {
                    stickerData.setPath(stickerView.getClipPath());
                    stickerData.setOriginalPath(stickerView.getOriginalPath());
                    VideoInfo materialVideoInfo = getVideoInfo.getInstance().getRingDuring(stickerView.getOriginalPath());
                    stickerData.setDuration(materialVideoInfo.getDuration());

                } else { //这里也会出现蓝松一样的，相同地址只有一个图层
                    stickerData.setPath(stickerView.getOriginalPath());
                    stickerData.setOriginalPath(stickerView.getOriginalPath());
                    VideoInfo materialVideoInfo = getVideoInfo.getInstance().getRingDuring(stickerView.getOriginalPath());
                    long materialDuration = materialVideoInfo.getDuration();
                    long needDuration = 0;
                    if (videoInfo != null) {
                        if (videoInfo.getDuration() < materialDuration) {
                            needDuration = videoInfo.getDuration();
                        } else {
                            needDuration = materialDuration;
                        }
                    } else {
                        needDuration = materialDuration;
                    }
                    stickerData.setDuration(needDuration);
                }
            } else {
                stickerData.setPath(stickerView.getResPath());
            }
        }

        stickerData.setShowStickerStartTime(stickerView.getShowStickerStartTime());
        stickerData.setShowStickerEndTime(stickerView.getShowStickerEndTime());
        stickerData.setChooseAnimId(stickerView.getChooseAnimId());
        return stickerData;
    }


    public ArrayList<String> GetTitleStyle() {
        return titleStyle;
    }

    public ArrayList<String> GetTitleFrame() {
        return titleFrame;
    }


    public ArrayList<String> GettitleEffect() {
        return titleEffect;
    }


}

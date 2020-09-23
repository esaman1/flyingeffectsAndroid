package com.flyingeffects.com.ui.model;

import android.text.TextUtils;

import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.commonlyModel.getVideoInfo;
import com.flyingeffects.com.enity.AllStickerData;
import com.flyingeffects.com.enity.VideoInfo;
import com.flyingeffects.com.utils.ScreenCaptureUtil;
import com.flyingeffects.com.view.StickerView;
import com.shixing.sxve.ui.albumType;

public class GetAllStickerDataModel {


    private static GetAllStickerDataModel thisModel;
    public static GetAllStickerDataModel getInstance() {
        if (thisModel == null) {
            thisModel = new GetAllStickerDataModel();
        }
        return thisModel;

    }



    public AllStickerData getStickerData(StickerView stickerView,boolean isMatting,VideoInfo videoInfo){
        AllStickerData stickerData = new AllStickerData();
        if(stickerView.getIsTextSticker()){
            ScreenCaptureUtil screenCaptureUtil=new ScreenCaptureUtil(BaseApplication.getInstance());
            stickerData.setOriginalPath(screenCaptureUtil.GetFilePath(stickerView));
            stickerData.setPath(screenCaptureUtil.GetFilePath(stickerView));
            stickerData.setBoxH((int) stickerView.getmHelpBoxRectH());
            stickerData.setBoxW((int) stickerView.getmHelpBoxRectW());
//            stickerData.setTextInterspace(screenCaptureUtil.GetTextInterspace(stickerView));
            stickerData.setScale(stickerView.getScale());
            stickerData.setMaskBitmap(stickerView.getMaskBitmap());
            stickerData.setMaterial(stickerView.getIsmaterial());
            stickerData.setTranslationX(stickerView.getTranslationX());
            stickerData.setText(true);
            stickerData.setTranslationy(stickerView.getTranslationY());
        }else{
            stickerData.setBoxH((int) stickerView.getmHelpBoxRectH());
            stickerData.setBoxW((int) stickerView.getmHelpBoxRectW());
            stickerData.setText(false);
            stickerData.setRotation(stickerView.getRotateAngle());
            stickerData.setScale(stickerView.getScale());
            stickerData.setMaskBitmap(stickerView.getMaskBitmap());
            stickerData.setMaterial(stickerView.getIsmaterial());
            stickerData.setTranslationX(stickerView.getTranslationX());
            stickerData.setTranslationy(stickerView.getTranslationY());
            if (!TextUtils.isEmpty(stickerView.getOriginalPath())) {
                String pathType = GetPathTypeModel.getInstance().getMediaType(stickerView.getOriginalPath());
                stickerData.setVideo(albumType.isVideo(pathType));
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
                    int materialDuration = materialVideoInfo.getDuration();
                    int needDuration = 0;
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
        stickerData.setChooseAnimId(stickerView.getChooseAnimId());


        return  stickerData;
    }




}

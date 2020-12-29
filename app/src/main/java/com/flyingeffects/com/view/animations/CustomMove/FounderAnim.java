package com.flyingeffects.com.view.animations.CustomMove;

import com.flyingeffects.com.enity.TransplationPos;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.view.StickerView;
import com.lansosdk.box.Layer;
import com.lansosdk.box.SubLayer;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


/**
 * description ：方正分身
 * creation date: 2020/12/29
 * user : zhangtongju
 */

public class FounderAnim extends baseAnimModel {


    private StickerView mainStickerView;


    void toChangeStickerView(StickerView mainStickerView, List<StickerView> subLayer) {
        ArrayList<StickerView> listAllSticker = new ArrayList<>();
        listAllSticker.addAll(subLayer);
        this.mainStickerView = mainStickerView;
        setRotate(mainStickerView.getRotateAngle());
        setOriginal(mainStickerView.getCenterX(), mainStickerView.getCenterY());
        float singleWidth = mainStickerView.getmHelpBoxRectW() / 2;
        float centerX = mainStickerView.getCenterX();
        float centerY = mainStickerView.getCenterY();
        float firstWidth = centerX - singleWidth * 2 - singleWidth / (float) 2;
        float firstHeight = centerY - singleWidth * 2 - singleWidth / (float) 2;
        for (int i = 0; i < subLayer.size(); i++) {
            int row = i / 6;
            int xx = i % 6;
            LogUtil.d("OOM5", "row=" + row);
            StickerView stickerView = subLayer.get(i);
            stickerView.toTranMoveXY(firstWidth + singleWidth * xx, firstHeight + singleWidth * row);
            if (i == subLayer.size() - 1) {
                mainStickerView.toTranMoveXY(firstWidth + singleWidth * xx, firstHeight + singleWidth * row);
            }
        }
        //第一个参数为总时长
        animationLinearInterpolator = new AnimationLinearInterpolator(3000, (progress, isDone) -> {
            for (int i = 0; i < subLayer.size(); i++) {
                int xx = i % 6;
                StickerView stickerView = subLayer.get(i);
                float needScale = progress * (xx + 1) * 0.5f;
                if (needScale > 0.5) {
                    needScale = 0.5f;
                }
                if (xx == 5) {
                    mainStickerView.setScale(needScale);
                }
                stickerView.setScale(needScale);
            }
        });
        animationLinearInterpolator.SetCirculation(false);
        animationLinearInterpolator.PlayAnimation();
    }


    @Override
    public void StopAnim() {
        if (animationLinearInterpolator != null) {
            animationLinearInterpolator.endTimer();
            resetAnimState(mainStickerView);
        }
    }



    //--------------------------------适配蓝松---------------------------------------


    public void initToChangeSubLayer(Layer mainLayer, @NotNull ArrayList<SubLayer> listForSubLayer, LayerAnimCallback callback, float percentage) {
        float singleWidth = mainLayer.getScaleWidth() / (float) 2;
        float centerX = mainLayer.getPositionX();
        float centerY = mainLayer.getPositionY();
        float firstWidth = centerX - singleWidth * 2 - singleWidth / (float) 2;
        float firstHeight = centerY - singleWidth * 2 - singleWidth / (float) 2;
        for (int i = 0; i < listForSubLayer.size(); i++) {
            int row = i / 6;
            int xx = i % 6;
            LogUtil.d("OOM5", "row=" + row);
            TransplationPos newTransplationPos = new TransplationPos();
            newTransplationPos.setToX((firstWidth + singleWidth * xx )/ mainLayer.getPadWidth());
            newTransplationPos.setToY((firstHeight + singleWidth * row) / mainLayer.getPadHeight());
            listForTranslaptionPosition.add(newTransplationPos);
            if (i == listForSubLayer.size() - 1) {
                listForTranslaptionPosition.add(newTransplationPos);
            }
        }
        callback.translationalXY(listForTranslaptionPosition);
        toChangeSubLayer(listForSubLayer, callback, percentage);
    }


    private ArrayList<Float> listForScale = new ArrayList<>();
    private ArrayList<TransplationPos> listForTranslaptionPosition = new ArrayList<>();

    public void toChangeSubLayer(ArrayList<SubLayer> listForSubLayer, LayerAnimCallback callback, float percentage) {
        getLansongTranslation(callback, percentage, listForSubLayer);
        LogUtil.d("translationalXY", "当前的事件为percentage=" + percentage);
    }


    void getLansongTranslation(LayerAnimCallback callback, float percentage, ArrayList<SubLayer> listForSubLayer) {
        listForScale.clear();
        AnimationLinearInterpolator animationLinearInterpolator = new AnimationLinearInterpolator(3000, (progress, isDone) -> {
            //主图层应该走的位置
            for (int i = 0; i < listForSubLayer.size(); i++) {
                SubLayer sub = listForSubLayer.get(i);
                int xx = i % 6;
                if (sub != null) {
                    float needScale = progress * (xx + 1) * 0.5f;
                    if (needScale > 0.5) {
                        needScale = 0.5f;
                    }
                    listForScale.add(needScale);
                    if (xx == 5) {
                        listForScale.add(needScale);
                    }
                }
            }
            callback.scale(listForScale);
        });
        animationLinearInterpolator.PlayAnimationNoTimer(percentage);
    }


}

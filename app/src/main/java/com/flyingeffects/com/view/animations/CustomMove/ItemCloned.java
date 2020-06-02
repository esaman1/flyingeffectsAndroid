package com.flyingeffects.com.view.animations.CustomMove;

import com.flyingeffects.com.enity.TransplationPos;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.view.StickerView;
import com.lansosdk.box.Layer;

import java.util.ArrayList;
import java.util.List;


/**
 * description ：动画，分身动画
 * creation date: 2020/5/25
 * user : zhangtongju
 */

public class ItemCloned extends baseAnimModel {


    private StickerView mainStickerView;
    private ArrayList<TransplationPos> listForTranslaptionPosition = new ArrayList<>();
    private ArrayList<Float> listForScale = new ArrayList<>();

    public void toChangeStickerView(StickerView mainStickerView, List<StickerView> subLayer) {
        this.mainStickerView = mainStickerView;
        setOriginal(mainStickerView.getCenterX(), mainStickerView.getCenterY());
        StickerView sub1 = subLayer.get(0);
        StickerView sub2 = subLayer.get(1);
        float perWidth = mainStickerView.getmHelpBoxRectW()*2;
        //第一个参数为总时长
        animationLinearInterpolator = new AnimationLinearInterpolator(2000, (progress, isDone) -> {
            float translationToX = perWidth * progress;
            sub1.toTranMoveXY(mainStickerView.getMBoxCenterX() - translationToX, mainStickerView.getMBoxCenterY());
            sub2.toTranMoveXY(mainStickerView.getMBoxCenterX() + translationToX, mainStickerView.getMBoxCenterY());
        });
        animationLinearInterpolator.PlayAnimation();
    }


    void getSubLayerData(Layer mainStickerView, LayerAnimCallback callback, float percentage) {
        toChangeSubLayer(callback, percentage);
    }


    void toChangeSubLayer(LayerAnimCallback callback, float percentage) {
        listForTranslaptionPosition.clear();
        listForScale.clear();
        AnimationLinearInterpolator animationLinearInterpolator = new AnimationLinearInterpolator(3000, (progress, isDone) -> {
            float translationToX;
            float needProgress = 1 - progress;
            TransplationPos transplationPos = new TransplationPos();
            transplationPos.setToY(0);
            if (needProgress < 0.66) {
                translationToX = (float) (needProgress + 0.33);
            } else {
                translationToX = (float) (needProgress - 0.66);
            }
            transplationPos.setToX(translationToX);
            listForTranslaptionPosition.add(transplationPos);
            listForScale.add(1 - translationToX);
            TransplationPos transplationPos2 = new TransplationPos();
            transplationPos2.setToY(0);
            if (needProgress < 0.33) {
                translationToX = (float) (needProgress + 0.66);
            } else {
                translationToX = (float) (needProgress - 0.33);
            }
            listForScale.add(1 - translationToX);
            transplationPos2.setToX(translationToX);
            listForTranslaptionPosition.add(transplationPos2);

            TransplationPos transplationPos3 = new TransplationPos();
            transplationPos3.setToY(0);
            if (needProgress < (1 - 0.99)) {
                translationToX = (float) (needProgress + 0.01);
            } else {
                translationToX = (float) (needProgress - (1 - 0.99));
            }
            listForScale.add(1 - translationToX);
            transplationPos3.setToX(translationToX);
            listForTranslaptionPosition.add(transplationPos3);
            callback.translationalXY(listForTranslaptionPosition);
            callback.scale(listForScale);


        });
        animationLinearInterpolator.PlayAnimationNoTimer(percentage);
    }


    public void StopAnim() {
        if (animationLinearInterpolator != null) {
            animationLinearInterpolator.endTimer();
            resetAnimState(mainStickerView);
        }

    }

}

package com.flyingeffects.com.view.animations.CustomMove;

import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.view.StickerView;

import java.util.List;


/**
 * description ：动画，上下摇摆
 * creation date: 2020/5/25
 * user : zhangtongju
 */

public class Rotate extends baseAnimModel {


    private StickerView mainStickerView;


    public void toChangeStickerView(StickerView mainStickerView, List<StickerView> subLayer) {
        this.mainStickerView = mainStickerView;
        setOriginal(mainStickerView.getCenterX(), mainStickerView.getCenterY());
        setRotate(mainStickerView.getRotateAngle());
        //第一个参数为总时长
        animationLinearInterpolator = new AnimationLinearInterpolator(4000, (progress, isDone) -> {
            float rotate=360*progress;
            mainStickerView.toRotate(rotate);
        });
        animationLinearInterpolator.PlayAnimation();
    }


    public void StopAnim() {


        if (animationLinearInterpolator != null) {
            animationLinearInterpolator.endTimer();
            resetAnimState(mainStickerView);
        }

    }

}

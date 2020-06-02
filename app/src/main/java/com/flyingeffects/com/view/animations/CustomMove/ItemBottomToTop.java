package com.flyingeffects.com.view.animations.CustomMove;

import com.flyingeffects.com.enity.TransplationPos;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.view.StickerView;
import com.lansosdk.box.Layer;

import java.util.ArrayList;
import java.util.List;


/**
 * description ：动画，底部到頂部
 * creation date: 2020/5/25
 * user : zhangtongju
 */

public class ItemBottomToTop extends baseAnimModel {


    private StickerView mainStickerView;
    private ArrayList<TransplationPos> listForTranslaptionPosition = new ArrayList<>();
    private ArrayList<Float> listForScale = new ArrayList<>();

    public void toChangeStickerView(StickerView mainStickerView, List<StickerView> subLayer) {
        this.mainStickerView = mainStickerView;
        setOriginal(mainStickerView.getCenterX(), mainStickerView.getCenterY());
        StickerView sub1 = subLayer.get(0);
        //(mainStickerView.getmHelpBoxRectW())  解决方法效果很突兀的情况
        float totalWidth = mainStickerView.getMeasuredWidth() + (mainStickerView.getmHelpBoxRectW());
        float totalHeight = mainStickerView.getContentHeight() + (mainStickerView.getmHelpBoxRectH());
        //view 右边的位置
        float stickerViewPosition = mainStickerView.GetHelpBoxRectRight();
        //view 右边位置的比例
        float percent = stickerViewPosition / totalWidth;
        //第一个参数为总时长
        animationLinearInterpolator = new AnimationLinearInterpolator(4000, (progress, isDone) -> {
            //拟定倒叙
            float needProgress = 1 - progress;
            //  第一个子view大约位置一半位置,显示在中间位置
            float translationToY;
            if (needProgress < 0.66) {
                translationToY = (float) (needProgress + 0.33);
                sub1.toTranMoveXY((float) (0.15 * totalWidth), totalHeight * translationToY);

            } else {
                translationToY = (float) (needProgress - 0.66);
                sub1.toTranMoveXY((float) (0.15 * totalWidth), totalHeight * translationToY);
            }

            if (needProgress < 0.33) {
                translationToY = (float) (needProgress + 0.66);
                mainStickerView.toTranMoveXY((float) (0.66 * totalWidth), totalHeight * translationToY);

            } else {
                translationToY = (float) (needProgress - 0.33);
                mainStickerView.toTranMoveXY((float) (0.66 * totalWidth), totalHeight * translationToY);
            }

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

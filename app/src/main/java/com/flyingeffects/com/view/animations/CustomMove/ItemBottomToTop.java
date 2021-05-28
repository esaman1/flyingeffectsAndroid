package com.flyingeffects.com.view.animations.CustomMove;

import com.flyingeffects.com.entity.TransplationPos;
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
    StickerView sub1;

    public void toChangeStickerView(StickerView mainStickerView, List<StickerView> subLayer) {
        this.mainStickerView = mainStickerView;
        setOriginal(mainStickerView.getCenterX(), mainStickerView.getCenterY());
        setRotate(mainStickerView.getRotateAngle());
        if(subLayer!=null&&subLayer.size()>0){
            sub1 = subLayer.get(0);
        }

        //(mainStickerView.getmHelpBoxRectW())  解决方法效果很突兀的情况
        float totalWidth = mainStickerView.getMeasuredWidth();
        float totalHeight = mainStickerView.getMeasuredHeight() + (mainStickerView.getmHelpBoxRectH());
        float halftHeifht = mainStickerView.getmHelpBoxRectH() / 2;
        float nowYP=mainStickerView.getCenterY()/totalHeight;
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
            float text = (float) Math.abs(0.8-nowYP);
            if(sub1!=null){
                if (needProgress < 0.8) {
                    translationToY = (float) (needProgress + 0.2);
                    sub1.toTranMoveXY((float) (0.25 * totalWidth), totalHeight * translationToY - halftHeifht);

                } else {
                    translationToY = (float) (needProgress - 0.8);
                    sub1.toTranMoveXY((float) (0.25 * totalWidth), totalHeight * translationToY - halftHeifht);
                }
            }
            if (needProgress < 0.4) {
                translationToY = (float) (needProgress + 0.6);
                mainStickerView.toTranMoveXY((float) (0.75 * totalWidth), totalHeight * translationToY - halftHeifht);

            } else {
                translationToY = (float) (needProgress - 0.4);
                mainStickerView.toTranMoveXY((float) (0.75 * totalWidth), totalHeight * translationToY - halftHeifht);
            }

        });
        animationLinearInterpolator.setNowDuration((int) (nowYP*4000));
        animationLinearInterpolator.PlayAnimation();
    }


    float halfHeight;
    float paddingAllHeight;
    float paddingHeight;
    float nowPositionY;
    void initSubLayerData(Layer mainLayer, LayerAnimCallback callback, float percentage) {
        halfHeight = mainLayer.getScaleHeight() / 2;
        paddingHeight = mainLayer.getPadHeight();
        paddingAllHeight = mainLayer.getPadHeight() + mainLayer.getScaleHeight();
        nowPositionY  =mainLayer.getPositionY()/paddingAllHeight;
        toChangeSubLayer(callback, percentage);

    }


    void toChangeSubLayer(LayerAnimCallback callback, float percentage) {
        listForTranslaptionPosition.clear();
        AnimationLinearInterpolator animationLinearInterpolator = new AnimationLinearInterpolator(4000, (progress, isDone) -> {
            float needProgress = 1 - progress;
            //  第一个子view大约位置一半位置,显示在中间位置
            float translationToY;
            TransplationPos transplationPos = new TransplationPos();
            transplationPos.setToX((float) 0.25);
            if (needProgress < 0.8) {
                translationToY = (float) (needProgress + 0.2);
                float toY = paddingAllHeight * translationToY - halfHeight;
                transplationPos.setToY(toY / paddingHeight);
            } else {
                translationToY = (float) (needProgress - 0.8);
                float toY = paddingAllHeight * translationToY - halfHeight;
                transplationPos.setToY(toY / paddingHeight);
            }

            listForTranslaptionPosition.add(transplationPos);
            TransplationPos transplationPos2 = new TransplationPos();
            transplationPos2.setToX((float) 0.75);
            if (needProgress < 0.4) {
                translationToY = (float) (needProgress + 0.6);
                float toY = paddingAllHeight * translationToY - halfHeight;
                transplationPos2.setToY(toY / paddingHeight);
            } else {
                translationToY = (float) (needProgress - 0.4);
                float toY = paddingAllHeight * translationToY - halfHeight;
                transplationPos2.setToY(toY / paddingHeight);
            }
            listForTranslaptionPosition.add(transplationPos2);
            callback.translationalXY(listForTranslaptionPosition);
        });

        float needPrecentage=percentage+nowPositionY;
        if(needPrecentage>1){
            needPrecentage=needPrecentage-1;
        }
        animationLinearInterpolator.PlayAnimationNoTimer(needPrecentage);
    }


    @Override
    public void StopAnim() {
        if (animationLinearInterpolator != null) {
            animationLinearInterpolator.endTimer();
            animationLinearInterpolator=null;
            resetAnimState(mainStickerView);
        }

    }

}

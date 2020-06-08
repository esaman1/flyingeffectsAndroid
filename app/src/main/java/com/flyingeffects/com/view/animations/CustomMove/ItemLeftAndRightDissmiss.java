package com.flyingeffects.com.view.animations.CustomMove;

import com.flyingeffects.com.enity.TransplationPos;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.view.StickerView;
import com.lansosdk.box.Layer;

import java.util.ArrayList;
import java.util.List;


/**
 * description ：动画，左右消失
 * creation date: 2020/5/25
 * user : zhangtongju
 */

public class ItemLeftAndRightDissmiss extends baseAnimModel {

    private  ArrayList<TransplationPos> listForTranslaptionPosition = new ArrayList<>();
    private StickerView mainStickerView;

    public void toChangeStickerView(StickerView mainStickerView, List<StickerView> subLayer) {
        this.mainStickerView = mainStickerView;
        setOriginal(mainStickerView.getCenterX(), mainStickerView.getCenterY());
        StickerView sub1 = null;
        if(subLayer!=null&&subLayer.size()>0){
            sub1 = subLayer.get(0);
        }
        float boxRectW = mainStickerView.getmHelpBoxRectW();
        float totalWidth = mainStickerView.getMeasuredWidth();
        //第一个参数为总时长
        StickerView finalSub = sub1;
        animationLinearInterpolator = new AnimationLinearInterpolator(3000, (progress, isDone) -> {
            LogUtil.d("animationLinearInterpolator", "progress=" + progress);
            //拟定倒叙
            float X = boxRectW * progress;
            mainStickerView.toTranMoveXY(X, mainStickerView.getMBoxCenterY());
            if(finalSub !=null){
                finalSub.toTranMoveXY(totalWidth - X, mainStickerView.getMBoxCenterY());
            }

        });
        animationLinearInterpolator.setInterpolatorType(1);
        animationLinearInterpolator.PlayAnimation();
    }


    private float previewPaddingWidth;
    private float previewScaleWidth;
    private float toY;

    public void initToChangeSubLayer(Layer mainLayer, LayerAnimCallback callback, float percentage) {
        previewPaddingWidth = mainLayer.getPadWidth();
        previewScaleWidth = mainLayer.getScaleWidth();
        float  layerHeight=mainLayer.getPositionY();

        float  paddingHeight=mainLayer.getPadHeight();
        toY=layerHeight/paddingHeight;
        toChangeSubLayer(callback, percentage);
    }



    public void toChangeSubLayer(LayerAnimCallback callback, float percentage) {
        listForTranslaptionPosition.clear();
        AnimationLinearInterpolator animationLinearInterpolator = new AnimationLinearInterpolator(3000, new AnimationLinearInterpolator.GetProgressCallback() {
            @Override
            public void progress(float progress, boolean isDone) {
                //拟定倒叙
                float X = previewScaleWidth * progress;
                TransplationPos transplationPos = new TransplationPos();
                transplationPos.setToY(toY);
                transplationPos.setToX(X/previewPaddingWidth);
                listForTranslaptionPosition.add(transplationPos);
                TransplationPos transplationPos2 = new TransplationPos();
                transplationPos2.setToY(toY);
                transplationPos2.setToX((previewPaddingWidth - X)/previewPaddingWidth);
                listForTranslaptionPosition.add(transplationPos2);
                callback.translationalXY(listForTranslaptionPosition);
            }
        });
        animationLinearInterpolator.setInterpolatorType(1);
        animationLinearInterpolator.PlayAnimationNoTimer(percentage);
    }


    public void StopAnim() {
        if (animationLinearInterpolator != null) {
            animationLinearInterpolator.endTimer();
            resetAnimState(mainStickerView);
        }

    }

}

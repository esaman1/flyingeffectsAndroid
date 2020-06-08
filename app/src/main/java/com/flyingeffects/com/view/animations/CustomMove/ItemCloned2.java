package com.flyingeffects.com.view.animations.CustomMove;

import com.flyingeffects.com.enity.TransplationPos;
import com.flyingeffects.com.view.StickerView;
import com.lansosdk.box.Layer;

import java.util.ArrayList;
import java.util.List;


/**
 * description ：动画，分身动画
 * creation date: 2020/5/25
 * user : zhangtongju
 */

public class ItemCloned2 extends baseAnimModel {


    private StickerView mainStickerView;
    private ArrayList<TransplationPos> listForTranslaptionPosition = new ArrayList<>();

    public void toChangeStickerView(StickerView mainStickerView, List<StickerView> subLayer) {
        this.mainStickerView = mainStickerView;
        StickerView sub1 = null;
        StickerView sub2=null;
        setOriginal(mainStickerView.getCenterX(), mainStickerView.getCenterY());
        if(subLayer!=null&&subLayer.size()==2){
            sub1= subLayer.get(0);
            sub2= subLayer.get(1);
        }

        float perWidth = mainStickerView.getmHelpBoxRectW() ;
        //第一个参数为总时长
        StickerView finalSub = sub1;
        StickerView finalSub1 = sub2;
        animationLinearInterpolator = new AnimationLinearInterpolator(2000, (progress, isDone) -> {
            float translationToX = perWidth * progress;
            if(finalSub !=null){
                finalSub.toTranMoveXY(mainStickerView.getMBoxCenterX() - translationToX, mainStickerView.getMBoxCenterY());
            }
            if(finalSub1 !=null){
                finalSub1.toTranMoveXY(mainStickerView.getMBoxCenterX() + translationToX, mainStickerView.getMBoxCenterY());
            }
        });
        animationLinearInterpolator.SetCirculation(false);
        animationLinearInterpolator.PlayAnimation();
    }

    private float previewScaleWidth2;
    private float centerX;
    private float centerY;
    private float paddingHeight;

    private float paddingWidth;

    public void initToChangeSubLayer(Layer mainLayer, LayerAnimCallback callback, float percentage) {
        previewScaleWidth2 = mainLayer.getScaleWidth();
        centerX = mainLayer.getPositionX();
        centerY=mainLayer.getPositionY();
        paddingHeight=mainLayer.getPadHeight();
        paddingWidth=mainLayer.getPadWidth();
        toChangeSubLayer(callback, percentage);
    }


    public void toChangeSubLayer(LayerAnimCallback callback, float percentage) {
        listForTranslaptionPosition.clear();
        //第一个参数为总时长
        AnimationLinearInterpolator animationLinearInterpolator = new AnimationLinearInterpolator(2000, (progress, isDone) -> {
            float translationToX = previewScaleWidth2 * progress;

            TransplationPos transplationPos0 = new TransplationPos();
            transplationPos0.setToY(centerY/paddingHeight);
            transplationPos0.setToX(centerX/paddingWidth);
            listForTranslaptionPosition.add(transplationPos0);

            TransplationPos transplationPos = new TransplationPos();
            transplationPos.setToY(centerY/paddingHeight);
            transplationPos.setToX((centerX - translationToX)/paddingWidth);
            listForTranslaptionPosition.add(transplationPos);

            TransplationPos transplationPos2 = new TransplationPos();
            transplationPos2.setToY(centerY/paddingHeight);
            transplationPos2.setToX((centerX + translationToX)/paddingWidth);
            listForTranslaptionPosition.add(transplationPos2);
            callback.translationalXY(listForTranslaptionPosition);
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

package com.flyingeffects.com.view.animations.CustomMove;

import com.flyingeffects.com.enity.TransplationPos;
import com.flyingeffects.com.view.StickerView;
import com.lansosdk.box.Layer;

import java.util.ArrayList;
import java.util.List;


/**
 * description ：动画，右到左动画
 * creation date: 2020/5/25
 * user : zhangtongju
 */

public class ItemRightToLeft extends baseAnimModel {

    private StickerView mainStickerView;
    private ArrayList<TransplationPos> listForTranslaptionPosition = new ArrayList<>();
    private ArrayList<Float> listForScale = new ArrayList<>();
    StickerView sub1;
    StickerView sub2;

    public void toChangeStickerView(StickerView mainStickerView, List<StickerView> subLayer) {
        this.mainStickerView = mainStickerView;
        if(subLayer==null||subLayer.size()==0){
            return;
        }

        setOriginal(mainStickerView.getCenterX(), mainStickerView.getCenterY());
        setScale(mainStickerView.getScale());
        setRotate(mainStickerView.getRotateAngle());
        if(subLayer.size()==2){
            sub1  = subLayer.get(0);
            sub2  = subLayer.get(1);
        }


        float totalWidth = mainStickerView.getMeasuredWidth() + (mainStickerView.getmHelpBoxRectW()*2);
        float haltfWidth=mainStickerView.getmHelpBoxRectW();
        float mScale = mainStickerView.GetHelpBoxRectScale();
        //view 右边的位置
        float stickerViewPosition = mainStickerView.GetHelpBoxRectRight();
        //view 右边位置的比例
        float percent = stickerViewPosition / totalWidth;
        //第一个参数为总时长
        animationLinearInterpolator = new AnimationLinearInterpolator(3000, (progress, isDone) -> {
            //拟定倒叙
            float needProgress = 1 - progress;
            if (isDone) {
                mainStickerView.toScale(percent, mScale, isDone);
                mainStickerView.toTranMoveX(percent * totalWidth);
            } else {
                //  第一个子view大约位置一半位置,显示在中间位置

                float translationToX;

                if(sub1!=null){
                    if (needProgress < 0.66) {
                        translationToX = (float) (needProgress + 0.33);
                        sub1.toTranMoveXY(translationToX * totalWidth-haltfWidth,mainStickerView.getMBoxCenterY());

                    } else {
                        translationToX = (float) (needProgress - 0.66);
                        sub1.toTranMoveXY(translationToX * totalWidth-haltfWidth,mainStickerView.getMBoxCenterY());
                    }
                    sub1.toScale(1 - translationToX, mScale, isDone);
                }




                if(sub2!=null){
                    if (needProgress < 0.33) {
                        translationToX = (float) (needProgress + 0.66);
                        sub2.toTranMoveXY(translationToX * totalWidth-haltfWidth,mainStickerView.getMBoxCenterY());

                    } else {
                        translationToX = (float) (needProgress - 0.33);
                        sub2.toTranMoveXY(translationToX * totalWidth-haltfWidth,mainStickerView.getMBoxCenterY());
                    }
                    sub2.toScale(1 - translationToX, mScale, isDone);
                }





                if (needProgress < (1 - 0.99)) {
                    translationToX = (float) (needProgress + 0.01);
                    mainStickerView.toTranMoveXY(translationToX * totalWidth-haltfWidth,mainStickerView.getMBoxCenterY());
                } else {
                    translationToX = (float) (needProgress - (1 - 0.99));
                    mainStickerView.toTranMoveXY(translationToX * totalWidth-haltfWidth,mainStickerView.getMBoxCenterY());
                }
                mainStickerView.toScale(1 - translationToX, mScale, isDone);
            }
        });
        animationLinearInterpolator.PlayAnimation();
    }


    private float toY;
    private float halfWidth;
    private float totalWidth;
    private float paddingWidth;
    void getSubLayerData(Layer mainLayer, LayerAnimCallback callback, float percentage) {
        toY=mainLayer.getPositionY()/mainLayer.getPadHeight();
        toChangeSubLayer(callback, percentage);
        halfWidth=mainLayer.getScaleWidth();
        paddingWidth=mainLayer.getPadWidth();
        totalWidth=paddingWidth+halfWidth*2;
    }


    void toChangeSubLayer(LayerAnimCallback callback, float percentage) {
        listForTranslaptionPosition.clear();
        listForScale.clear();
        AnimationLinearInterpolator animationLinearInterpolator = new AnimationLinearInterpolator(3000, (progress, isDone) -> {
            float translationToX;
            float toX;
            float needProgress = 1 - progress;
            TransplationPos transplationPos = new TransplationPos();
            transplationPos.setToY(toY);
            if (needProgress < 0.66) {
                translationToX = (float) (needProgress + 0.33);
                toX=totalWidth*translationToX-halfWidth;

            } else {
                translationToX = (float) (needProgress - 0.66);
                toX=totalWidth*translationToX-halfWidth;
            }
            transplationPos.setToX(toX/paddingWidth);
            listForTranslaptionPosition.add(transplationPos);
            listForScale.add(1 - translationToX);
            TransplationPos transplationPos2 = new TransplationPos();
            transplationPos2.setToY(toY);
            if (needProgress < 0.33) {
                translationToX = (float) (needProgress + 0.66);
                toX=totalWidth*translationToX-halfWidth;
            } else {
                translationToX = (float) (needProgress - 0.33);
                toX=totalWidth*translationToX-halfWidth;
            }
            listForScale.add(1 - translationToX);
            transplationPos2.setToX(toX/paddingWidth);
            listForTranslaptionPosition.add(transplationPos2);

            TransplationPos transplationPos3 = new TransplationPos();
            transplationPos3.setToY(toY);
            if (needProgress < (1 - 0.99)) {
                translationToX = (float) (needProgress + 0.01);
                toX=totalWidth*translationToX-halfWidth;
            } else {
                translationToX = (float) (needProgress - (1 - 0.99));
                toX=totalWidth*translationToX-halfWidth;
            }
            listForScale.add(1 - translationToX);
            transplationPos3.setToX(toX/paddingWidth);
            listForTranslaptionPosition.add(transplationPos3);
            callback.translationalXY(listForTranslaptionPosition);
            callback.scale(listForScale);


        });
        animationLinearInterpolator.PlayAnimationNoTimer(percentage);
    }


    @Override
    public void StopAnim() {
        if (animationLinearInterpolator != null) {
            animationLinearInterpolator.endTimer();
            resetAnimState(mainStickerView);
        }

    }

}

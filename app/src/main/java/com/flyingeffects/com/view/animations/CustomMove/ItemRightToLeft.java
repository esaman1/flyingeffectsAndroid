package com.flyingeffects.com.view.animations.CustomMove;

import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.view.StickerView;

import java.util.List;


/**
 * description ：动画，右到左动画
 * creation date: 2020/5/25
 * user : zhangtongju
 */

public class ItemRightToLeft extends baseAnimModel {


    private StickerView mainStickerView;



    public void toChangeStickerView(StickerView mainStickerView, List<StickerView> subLayer) {
        this.mainStickerView = mainStickerView;
        LogUtil.d("StartAnimModel", "subLayer大小為=" + subLayer.size());
        setOriginal(mainStickerView.getCenterX(), mainStickerView.getCenterY());
        StickerView sub1 = subLayer.get(0);
        StickerView sub2 = subLayer.get(1);
        //(mainStickerView.getmHelpBoxRectW())  解决方法效果很突兀的情况
        float totalWidth = mainStickerView.getMeasuredWidth()+(mainStickerView.getmHelpBoxRectW());
        LogUtil.d("OOM", "totalWidth=" + totalWidth);
        float mScale = mainStickerView.GetHelpBoxRectScale();
        //view 右边的位置
        float stickerViewPosition = mainStickerView.GetHelpBoxRectRight();
        //view 右边位置的比例
        float percent = stickerViewPosition / totalWidth;
        //第一个参数为总时长
        animationLinearInterpolator = new AnimationLinearInterpolator(3000, new AnimationLinearInterpolator.GetProgressCallback() {
            @Override
            public void progress(float progress, boolean isDone) {
                //拟定倒叙
                float needProgress = 1 - progress;
                if (isDone) {
                    mainStickerView.toScale(percent, mScale, isDone);
                    mainStickerView.toTranMoveX(percent * totalWidth);
                } else {
                    //  第一个子view大约位置一半位置,显示在中间位置
                    if (sub1 != null) {
                        LogUtil.d("StartAnimModel", "sub1 != null="+"subId="+sub1.getId() );
                        float translationToX;
                        if (needProgress < 0.66) {
                            translationToX = (float) (needProgress + 0.33);
                            sub1.toTranMoveX(translationToX * totalWidth);

                        } else {
                            translationToX = (float) (needProgress - 0.66);
                            sub1.toTranMoveX(translationToX * totalWidth);
                        }
                        LogUtil.d("OOM", "needToX=" + translationToX * totalWidth);
                     sub1.toScale(1 - translationToX, mScale, isDone);
                    }

                    if (sub2 != null) {
                        float translationToX;
                        if (needProgress < 0.33) {
                            translationToX = (float) (needProgress +  0.66);
                            sub2.toTranMoveX(translationToX * totalWidth);

                        } else {
                            translationToX = (float) (needProgress - 0.33);
                            sub2.toTranMoveX(translationToX * totalWidth);
                        }
                        LogUtil.d("OOM", "translationToX2=" + translationToX);
                        sub2.toScale(1 - translationToX, mScale, isDone);
                    }


                    float translationToX;
                    if (needProgress < (1 - 0.99)) {
                        translationToX = (float) (needProgress + 0.01);
                        mainStickerView.toTranMoveX(translationToX * totalWidth);
                    } else {
                        translationToX = (float) (needProgress - (1 - 0.99));
                        mainStickerView.toTranMoveX(translationToX * totalWidth);
                    }
                    LogUtil.d("OOM", "needToX=" + translationToX * totalWidth);
                mainStickerView.toScale(1 - translationToX, mScale, isDone);


                }
            }
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

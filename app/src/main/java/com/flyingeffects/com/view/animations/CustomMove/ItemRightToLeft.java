package com.flyingeffects.com.view.animations.CustomMove;

import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.view.StickerView;

import java.util.List;

public class ItemRightToLeft {

    private static ItemRightToLeft thisModel;
    AnimationLinearInterpolator animationLinearInterpolator;
    public static ItemRightToLeft getInstance() {

        if (thisModel == null) {
            thisModel = new ItemRightToLeft();
        }
        return thisModel;
    }


    public void toChangeStickerView(StickerView mainStickerView, List<StickerView> subLayer, int delay) {
        StickerView sub1 = subLayer.get(0);
        StickerView sub2 = subLayer.get(1);

        float stickerViewWidth = mainStickerView.GetHelpBoxRectWidth();
        float totalWidth = mainStickerView.getMeasuredWidth() + stickerViewWidth;
        LogUtil.d("OOM", "totalWidth=" + totalWidth);
        float mScale = mainStickerView.GetHelpBoxRectScale();
        float stickerViewPosition = mainStickerView.GetHelpBoxRectRight();
        float percent = stickerViewPosition / totalWidth;
        float percentWidth = stickerViewWidth / (float) totalWidth;
        float percentWidth2 = 1 - percentWidth;
        LogUtil.d("OOM", "即将开始的进度为" + percent);
        //第一个参数为总时长
        animationLinearInterpolator  = new AnimationLinearInterpolator(3000, new AnimationLinearInterpolator.GetProgressCallback() {
            @Override
            public void progress(float progress, boolean isDone) {
                //拟定倒叙
                float needProgress = 1 - progress;
                if (isDone) {
                    mainStickerView.toScale(percent, mScale, isDone);
                    mainStickerView.toTranMoveX(percent, totalWidth);
                } else {
                    //第一个子view大约位置一半位置
                    if (sub1 != null) {
                        float tranx;
                        if (needProgress < 0.5) {
                            tranx = (float) (needProgress + 0.5);
                            sub1.toTranMoveX(tranx, totalWidth);

                        } else {
                            tranx = (float) (needProgress - 0.5);
                            sub1.toTranMoveX(tranx, totalWidth);
                        }
                        LogUtil.d("OOM", "Tranx=" + tranx);
                    sub1.toScale(1 - tranx, mScale, isDone);
                    }


                    if (sub2 != null) {
                        float tranx;
                        if (needProgress < percentWidth) {
                            tranx = (needProgress + percentWidth2);
                            sub2.toTranMoveX(tranx, totalWidth);

                        } else {
                            tranx = (needProgress - percentWidth);
                            sub2.toTranMoveX(tranx, totalWidth);
                        }
                        LogUtil.d("OOM", "Tranx2=" + tranx);
                        sub2.toScale(1 - tranx, mScale, isDone);
                    }


                    float tranx;
                    if (needProgress < percentWidth2) {
                        tranx = (needProgress + percentWidth);
                        mainStickerView.toTranMoveX(tranx, totalWidth);

                    } else {
                        tranx = (needProgress - percentWidth2);
                        mainStickerView.toTranMoveX(tranx, totalWidth);
                    }
                    LogUtil.d("OOM", "Tranx2=" + tranx);
                    mainStickerView.toScale(1 - tranx, mScale, isDone);


                }
            }
        });
        animationLinearInterpolator.PlayAnimation();
    }


    public void StopAnim(){
        animationLinearInterpolator.endTimer();
    }

}

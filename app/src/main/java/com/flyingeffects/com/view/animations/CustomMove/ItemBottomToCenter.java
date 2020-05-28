package com.flyingeffects.com.view.animations.CustomMove;

import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.view.StickerView;

import java.util.List;


/**
 * description ：动画，右到左动画
 * creation date: 2020/5/25
 * user : zhangtongju
 */

public class ItemBottomToCenter extends baseAnimModel {


    private StickerView mainStickerView;


    public void toChangeStickerView(StickerView mainStickerView, List<StickerView> subLayer) {
        this.mainStickerView = mainStickerView;
        setOriginal(mainStickerView.getCenterX(), mainStickerView.getCenterY());

        //(mainStickerView.getmHelpBoxRectW())  解决方法效果很突兀的情况
        float totalHeight = mainStickerView.getMeasuredHeight() + (mainStickerView.getmHelpBoxRectH());
        float stickerViewPosition = mainStickerView.getMBoxCenterY();
        float stickerViewH = mainStickerView.getmHelpBoxRectH();
        //view 右边位置的比例
//        float percent = stickerViewPosition / totalHeight;
        float Difference = totalHeight - stickerViewH;

        //第一个参数为总时长
        animationLinearInterpolator = new AnimationLinearInterpolator(2000, new AnimationLinearInterpolator.GetProgressCallback() {
            @Override
            public void progress(float progress, boolean isDone) {
                float needProgress=1-progress;
                //拟定倒叙
                float Y = stickerViewPosition + Difference * needProgress;
                mainStickerView.toTranMoveY(Y);

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

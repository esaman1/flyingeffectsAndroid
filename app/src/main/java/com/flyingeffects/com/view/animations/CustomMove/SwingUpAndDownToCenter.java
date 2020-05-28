package com.flyingeffects.com.view.animations.CustomMove;

import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.view.StickerView;

import java.util.List;


/**
 * description ：动画，上下摇摆
 * creation date: 2020/5/25
 * user : zhangtongju
 */

public class SwingUpAndDownToCenter extends baseAnimModel {


    private StickerView mainStickerView;


    public void toChangeStickerView(StickerView mainStickerView, List<StickerView> subLayer) {
        this.mainStickerView = mainStickerView;
        setOriginal(mainStickerView.getCenterX(), mainStickerView.getCenterY());
        //(mainStickerView.getmHelpBoxRectW())  解决方法效果很突兀的情况
        float stickerViewPosition = mainStickerView.getMBoxCenterY();


        //view 右边位置的比例
//        float percent = stickerViewPosition / totalHeight;

        //第一个参数为总时长
        animationLinearInterpolator = new AnimationLinearInterpolator(2000, new AnimationLinearInterpolator.GetProgressCallback() {
            @Override
            public void progress(float progress, boolean isDone) {
                LogUtil.d("animationLinearInterpolator","progress="+progress);

                //拟定倒叙
                float Y =stickerViewPosition+ 50  * progress;
                mainStickerView.toTranMoveY(Y);

            }
        });
        animationLinearInterpolator.setInterpolatorType(1);
        animationLinearInterpolator.PlayAnimation();
    }


    public void StopAnim() {


        if (animationLinearInterpolator != null) {
            animationLinearInterpolator.endTimer();
            resetAnimState(mainStickerView);
        }

    }

}

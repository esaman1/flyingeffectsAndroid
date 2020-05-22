package com.flyingeffects.com.view.animations.CustomMove;

import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.view.StickerView;

public class ItemRightToLeft {

    private static ItemRightToLeft thisModel;

    public static ItemRightToLeft getInstance() {

        if (thisModel == null) {
            thisModel = new ItemRightToLeft();
        }
        return thisModel;
    }





    public  void toChangeStickerView(StickerView stickerView,int delay){
        float stickerViewWidth = stickerView.GetHelpBoxRectWidth();
        float totalWidth = stickerView.getMeasuredWidth() + stickerViewWidth;
        LogUtil.d("OOM","totalWidth="+totalWidth);
        float mScale = stickerView.GetHelpBoxRectScale();
        float percent = stickerViewWidth / totalWidth;
        LogUtil.d("OOM", "即将开始的进度为" + percent);
        AnimationLinearInterpolator animationLinearInterpolator = new AnimationLinearInterpolator(3000, (int) ((3000-delay) * percent), new AnimationLinearInterpolator.GetProgressCallback() {
            @Override
            public void progress(float progress, boolean isDone) {
                LogUtil.d("OOM","progress="+progress);
                if (isDone) {
                    stickerView.toScale(percent, mScale, isDone);
                    stickerView.toTranMoveX(percent, totalWidth);
                } else {
                    stickerView.toScale(progress, mScale, isDone);
                    stickerView.toTranMoveX(1-progress, totalWidth);
                }
            }
        });
        animationLinearInterpolator.PlayAnimation();
    }

}

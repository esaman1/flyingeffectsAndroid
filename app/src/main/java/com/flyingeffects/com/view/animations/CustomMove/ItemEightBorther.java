package com.flyingeffects.com.view.animations.CustomMove;

import android.graphics.PathMeasure;

import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.view.StickerView;

import java.util.List;


/**
 * description ：动画，花生动画，8兄弟动画  8888888
 * creation date: 2020/5/25
 * user : zhangtongju
 */

public class ItemEightBorther extends baseAnimModel{

    private AnimationLinearInterpolator animationLinearInterpolator;
    private StickerView mainStickerView;




    public void toChangeStickerView(StickerView mainStickerView, List<StickerView> subLayer) {
        this.mainStickerView=mainStickerView;
        setOriginal(mainStickerView.getCenterX(),mainStickerView.getCenterY());
        mainStickerView.drawAnimPath();
        float[] pos = new float[2];
        float[] tan = new float[2];
        PathMeasure mPathMeasure = mainStickerView.getAnimPathMeasure();
        //总长度
        float totalDistancePathMeasure = mPathMeasure.getLength();
        float perDistance = totalDistancePathMeasure / (float) 12;
        float stickerViewWidth = mainStickerView.GetHelpBoxRectWidth();
        float totalWidth = mainStickerView.getMeasuredWidth() + stickerViewWidth;
        LogUtil.d("OOM", "totalWidth=" + totalWidth);
        float stickerViewPosition = mainStickerView.GetHelpBoxRectRight();
        float percent = stickerViewPosition / totalWidth;
        LogUtil.d("OOM", "即将开始的进度为" + percent);
        //第一个参数为总时长
        animationLinearInterpolator = new AnimationLinearInterpolator(10000, (progress, isDone) -> {
            //主图层应该走的位置
            float nowDistance = totalDistancePathMeasure * progress;
            mPathMeasure.getPosTan(nowDistance, pos, tan);
            mainStickerView.toTranMoveXY(pos[0], pos[1]);
            for (int i=0;i<subLayer.size();i++){
                StickerView sub=subLayer.get(i);
                if(sub!=null){
                    float needDistance = perDistance*i + nowDistance;
                    if (needDistance > totalDistancePathMeasure) {
                        needDistance = needDistance - totalDistancePathMeasure;
                    }
                    mPathMeasure.getPosTan(needDistance, pos, tan);
                    sub.toTranMoveXY(pos[0], pos[1]);
                }
            }


        });
        animationLinearInterpolator.PlayAnimation();
    }


    @Override
    public void StopAnim() {
        if (animationLinearInterpolator != null) {
            animationLinearInterpolator.endTimer();
            resetAnimState(mainStickerView);
        }
    }
}

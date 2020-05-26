package com.flyingeffects.com.view.animations.CustomMove;

import com.flyingeffects.com.view.StickerView;

public abstract class baseAnimModel {

    private float originalX;
    private float originalY;
    public AnimationLinearInterpolator animationLinearInterpolator;
    public void setOriginal(float originalX,float originalY) {
        this.originalY = originalY;
        this.originalX = originalX;
    }


    public  abstract void StopAnim();




    /**
     * description ：动画还原
     * creation date: 2020/5/26
     * user : zhangtongju
     */
    public void resetAnimState(StickerView mainStickerView){
        if(mainStickerView!=null){
            mainStickerView.toTranMoveXY(originalX,originalY);
        }
    }






}

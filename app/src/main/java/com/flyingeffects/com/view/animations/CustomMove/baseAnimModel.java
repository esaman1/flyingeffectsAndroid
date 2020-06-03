package com.flyingeffects.com.view.animations.CustomMove;

import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.view.StickerView;

public abstract class baseAnimModel {

    private float originalX;
    private float originalY;
    private float scale;
    private float rotate;
    public AnimationLinearInterpolator animationLinearInterpolator;
    public void setOriginal(float originalX,float originalY) {
        this.originalY = originalY;
        this.originalX = originalX;
    }

    public void setScale(float scale){
        this.scale=scale;
    }

    public void setRotate(float rotate){
        this.rotate=rotate;
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
            if(scale!=0){
                mainStickerView.setScale(scale);
            }
//            if(rotate!=0){
//                mainStickerView.setRotate(rotate);
//            }
        }
    }






}

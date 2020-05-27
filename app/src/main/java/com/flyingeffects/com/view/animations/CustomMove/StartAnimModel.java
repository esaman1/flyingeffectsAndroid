package com.flyingeffects.com.view.animations.CustomMove;

import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.view.StickerView;

import java.util.List;

public class StartAnimModel {

    private AnimCollect animCollect;


    public StartAnimModel(  AnimCollect animCollect) {


        this.animCollect = animCollect;
    }


    public void ToStart(AnimType animType,StickerView mainStickerView ,List<StickerView> subLayer ) {
//        LogUtil.d("StartAnimModel","当前开始的动画id为"+mainStickerView.getId());


        for (StickerView stickerView:subLayer
             ) {
            LogUtil.d("StartAnimModel","当前开始的动画id为"+stickerView.getId());
        }

        animCollect.startAnimForChooseAnim(animType, mainStickerView, subLayer);
    }


    public void ToEnd() {
        animCollect.stopAnim();


    }


}

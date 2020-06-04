package com.flyingeffects.com.view.animations.CustomMove;

import com.flyingeffects.com.view.StickerView;

import java.util.List;

public class StartAnimModel {

    private AnimCollect animCollect;


    public StartAnimModel(  AnimCollect animCollect) {


        this.animCollect = animCollect;
    }



    public  synchronized void   ToStart(AnimType animType,StickerView mainStickerView ,List<StickerView> subLayer ) {
        animCollect.startAnimForChooseAnim(animType, mainStickerView, subLayer);
    }




}

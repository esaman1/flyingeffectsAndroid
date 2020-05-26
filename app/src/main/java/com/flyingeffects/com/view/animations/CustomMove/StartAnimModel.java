package com.flyingeffects.com.view.animations.CustomMove;

import com.flyingeffects.com.view.StickerView;

import java.util.List;

public class StartAnimModel {

    private List<StickerView> subLayer;
    private StickerView mainStickerView;
    private AnimCollect animCollect;


    public StartAnimModel(StickerView mainStickerView, List<StickerView> subLayer, AnimCollect animCollect) {
        this.subLayer = subLayer;
        this.mainStickerView = mainStickerView;
        this.animCollect = animCollect;
    }


    public void ToStart(AnimType animType) {

        animCollect.startAnimForChooseAnim(animType, mainStickerView, subLayer);
    }


    public void ToEnd() {
        animCollect.stopAnim();


    }


}

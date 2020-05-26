package com.flyingeffects.com.view.animations.CustomMove;

import com.flyingeffects.com.view.StickerView;

import java.util.List;

public class StartAnimModel {

    private List<StickerView> subLayer;
    private StickerView mainStickerView;

    public StartAnimModel(StickerView mainStickerView, List<StickerView> subLayer) {
        this.subLayer = subLayer;
        this.mainStickerView = mainStickerView;
    }



    public void ToStart(AnimType animType){
        AnimCollect.getInstance().startAnimForChooseAnim(animType,mainStickerView,subLayer);
    }


    public void ToEnd(){
        ItemRightToLeft.getInstance().StopAnim();
        ItemEightBorther.getInstance().StopAnim();


    }





}

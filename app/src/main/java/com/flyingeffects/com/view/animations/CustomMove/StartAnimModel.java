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



    public void ToStart(){
        ItemRightToLeft.getInstance().toChangeStickerView(mainStickerView,0);
        for(int i=1;i<=subLayer.size();i++){
            int delay=i*1000;
            ItemRightToLeft.getInstance().toChangeStickerView(subLayer.get(i-1),delay);
        }
    }











}

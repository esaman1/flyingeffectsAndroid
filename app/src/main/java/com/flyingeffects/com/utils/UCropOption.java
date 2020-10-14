package com.flyingeffects.com.utils;

import android.graphics.Color;

public class UCropOption {


    private static UCropOption thisModel;

    public static UCropOption getInstance() {

        if (thisModel == null) {
            thisModel = new UCropOption();
        }
        return thisModel;

    }


    public com.yalantis.ucrop.UCrop.Options getUcropOption() {
        com.yalantis.ucrop.UCrop.Options options = new com.yalantis.ucrop.UCrop.Options();
        options.setStatusBarColor(Color.parseColor("#FEE131"));
        options.setCropFrameColor(Color.parseColor("#FEE131"));
        options.setToolbarColor(Color.parseColor("#FEE131"));
        options.setCropGridColor(Color.parseColor("#FEE131"));
        options.setToolbarWidgetColor(Color.GRAY);
        options.setHideBottomControls(true);
        return  options;
    }
}

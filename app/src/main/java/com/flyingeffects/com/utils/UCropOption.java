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
        options.setStatusBarColor(Color.parseColor("#F01D1D1D"));
        options.setCropFrameColor(Color.parseColor("#F01D1D1D"));
        options.setToolbarColor(Color.parseColor("#F01D1D1D"));
        options.setCropGridColor(Color.parseColor("#F01D1D1D"));
        options.setToolbarWidgetColor(Color.WHITE);
        options.setHideBottomControls(true);
        return  options;
    }
}

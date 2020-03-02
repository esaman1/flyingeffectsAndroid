package com.flyingeffects.com.manager;

public class ColorCorrectionManager {

    private static ColorCorrectionManager instance;


    public static ColorCorrectionManager getInstance() {
        if (instance == null) {
            instance = new ColorCorrectionManager();
        }
        return instance;
    }

    public String[] colorList = {"#4792D9", "#FF7272", "#FFB753", "#796DD9", "#93F195", "#4E87D7", "#FFACAC", "#FF58DB", "#F7F0AC", "#58B3ED", "#C17DFF", "#93F195", "#C7CA53", "#FF8585"};


    public String getChooseColor(int position) {
          int colorListSize=colorList.length;
        int getPosition = position % colorListSize;
        return colorList[getPosition];
    }


}

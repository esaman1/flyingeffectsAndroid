package com.shixing.sxve.ui.util;

import android.graphics.Color;

public class ColorUtils {
    public static int parseRGBAColor(String colorString) {
        if (colorString.length() != 7 && colorString.length() != 9) {
            return Color.TRANSPARENT;
        }

        int startIndex = 1;

        String rString = colorString.substring(startIndex + 0, startIndex + 0 + 2);
        String gString = colorString.substring(startIndex + 2, startIndex + 2 + 2);
        String bString = colorString.substring(startIndex + 4, startIndex + 4 + 2);

        int r = Integer.parseInt(rString, 16);
        int g = Integer.parseInt(gString, 16);
        int b = Integer.parseInt(bString, 16);
        int a = 255;

        if (colorString.length() == 9) {
            String aString = colorString.substring(startIndex + 6, startIndex + 6 + 2);
            a = Integer.parseInt(aString, 16);
        }
        return Color.argb(a, r, g, b);
    }

    public static int argb(float alpha, float red, float green, float blue) {
        return ((int) (alpha * 255.0f + 0.5f) << 24) |
                ((int) (red   * 255.0f + 0.5f) << 16) |
                ((int) (green * 255.0f + 0.5f) <<  8) |
                (int) (blue  * 255.0f + 0.5f);
    }
}

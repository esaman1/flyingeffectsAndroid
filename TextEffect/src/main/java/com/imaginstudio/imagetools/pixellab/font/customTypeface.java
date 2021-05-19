package com.imaginstudio.imagetools.pixellab.font;

import android.graphics.Typeface;

public class customTypeface {
    String path;
    Typeface typeface = Typeface.DEFAULT;

    public customTypeface(String path2, Typeface typeface2) {
        this.path = path2;
        this.typeface = typeface2;
    }

    public String getPath() {
        return this.path;
    }

    public Typeface getTypeface() {
        return this.typeface;
    }
}

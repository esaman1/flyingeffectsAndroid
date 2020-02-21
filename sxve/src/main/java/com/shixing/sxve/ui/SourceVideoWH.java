package com.shixing.sxve.ui;

import java.io.Serializable;

public class SourceVideoWH implements Serializable {


    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    private int width;
    private int height;
}

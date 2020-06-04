package com.flyingeffects.com.enity;

import java.io.Serializable;

public class TransplationPos implements Serializable {
    public float getToX() {
        return toX;
    }

    public void setToX(float toX) {
        this.toX = toX;
    }

    public float getToY() {
        return toY;
    }

    public void setToY(float toY) {
        this.toY = toY;
    }

    private float toX;
    private float toY;


}

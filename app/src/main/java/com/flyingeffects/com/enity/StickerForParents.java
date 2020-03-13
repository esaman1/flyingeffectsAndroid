package com.flyingeffects.com.enity;


import java.io.Serializable;


/**
 * description ：标签相对于父控件的具体位置
 * creation date: 2020/3/13
 * user : zhangtongju
 */
public class StickerForParents implements Serializable {

    private float roation;

    public float getRoation() {
        return roation;
    }

    public void setRoation(float roation) {
        this.roation = roation;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public int getTranX() {
        return tranX;
    }

    public void setTranX(int tranX) {
        this.tranX = tranX;
    }

    public int getTranY() {
        return tranY;
    }

    public void setTranY(int tranY) {
        this.tranY = tranY;
    }

    private float scale;
    private int  tranX;
    private int tranY;

}

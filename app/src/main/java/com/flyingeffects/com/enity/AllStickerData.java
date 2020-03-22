package com.flyingeffects.com.enity;

import java.io.Serializable;



public class AllStickerData implements Serializable {

    private float rotation;

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    //因为默认gif是宽的一半
    public float getScale() {
        return scale/2;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }



    private float scale;
    private float translationX;

    public float getTranslationX() {
        return translationX;
    }

    public void setTranslationX(float translationX) {
        this.translationX = translationX;
    }

    public float getTranslationy() {
        return translationy;
    }

    public void setTranslationy(float translationy) {
        this.translationy = translationy;
    }

    private float translationy;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private String path;

}

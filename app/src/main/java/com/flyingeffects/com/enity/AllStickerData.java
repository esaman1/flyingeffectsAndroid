package com.flyingeffects.com.enity;

import com.flyingeffects.com.view.animations.CustomMove.AnimType;

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
        return scale / 2;
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

    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    private String originalPath;

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    private boolean isVideo = false;


    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    private long duration;


    private int BoxW;

    public int getBoxW() {
        return BoxW;
    }

    public void setBoxW(int boxW) {
        BoxW = boxW;
    }

    public int getBoxH() {
        return BoxH;
    }

    public void setBoxH(int boxH) {
        BoxH = boxH;
    }

    private int BoxH;

    public AnimType getChooseAnimId() {
        return ChooseAnimId;
    }

    public void setChooseAnimId(AnimType chooseAnimId) {
        ChooseAnimId = chooseAnimId;
    }

    /**
     * 是否选择了动画，动画id值
     */
    private AnimType ChooseAnimId;

}

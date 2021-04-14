package com.flyingeffects.com.enity;

import com.flyingeffects.com.view.animations.CustomMove.AnimType;

import android.graphics.Bitmap;

import java.io.Serializable;


public class AllStickerData implements Serializable {

    public boolean isMaterial() {
        return isMaterial;
    }

    public void setMaterial(boolean material) {
        isMaterial = material;
    }

    //true 表示素材 false 表示贴纸
    private boolean isMaterial = true;

    private float rotation;

    public Bitmap getMaskBitmap() {
        return mMaskBitmap;
    }

    public void setMaskBitmap(Bitmap mMaskBitmap) {
        this.mMaskBitmap = mMaskBitmap;
    }

    private Bitmap mMaskBitmap;

    private Bitmap mMirrorBitmap;

    public Bitmap getMirrorBitmap() {
        return mMirrorBitmap;
    }

    public void setMirrorBitmap(Bitmap mirrorBitmap) {
        mMirrorBitmap = mirrorBitmap;
    }

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

    public boolean isText() {
        return isText;
    }

    public void setText(boolean text) {
        isText = text;
    }

    private boolean isText = false;

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


    public float getTextInterspace() {
        return textInterspace;
    }

    public void setTextInterspace(float textInterspace) {
        this.textInterspace = textInterspace;
    }

    /**
     * 文字和view 的间距
     */
    private float textInterspace;


    /**
     * 贴纸开始显示时间
     */
    public long showStickerStartTime;

    public long getShowStickerStartTime() {
        return showStickerStartTime;
    }

    public void setShowStickerStartTime(long showStickerStartTime) {
        this.showStickerStartTime = showStickerStartTime;
    }

    public long getShowStickerEndTime() {
        return showStickerEndTime;
    }

    public void setShowStickerEndTime(long showStickerEndTime) {
        this.showStickerEndTime = showStickerEndTime;
    }

    /**
     * 贴纸显示结束时间
     */
    public long showStickerEndTime;

}

package com.flyingeffects.com.entity;

/**
 * @author Coca Cola
 */
public class VideoType {

    private String mPath;
    private int mPosition;
    private long mDuration;

    public VideoType(String path, int position, long duration) {
        this.mPath = path;
        this.mDuration = duration;
        this.mPosition = position;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        this.mPath = path;
    }


    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        this.mPosition = position;
    }


    public long getDuration() {
        return mDuration;
    }

    public void setDuration(long duration) {
        this.mDuration = duration;
    }

}

package com.flyingeffects.com.view.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @Author: savion
 * @Date: 2019/2/21 18:15
 * @Des:
 **/
public class VideoTrimmerFrameBean implements Parcelable {
    private String framePath;
    private int frameWidth;
    private int frameHeight;
    private int index;

    protected VideoTrimmerFrameBean(Parcel in) {
        framePath = in.readString();
        frameWidth = in.readInt();
        frameHeight = in.readInt();
        index = in.readInt();
    }

    public VideoTrimmerFrameBean() {

    }

    public static final Creator<VideoTrimmerFrameBean> CREATOR = new Creator<VideoTrimmerFrameBean>() {
        @Override
        public VideoTrimmerFrameBean createFromParcel(Parcel in) {
            return new VideoTrimmerFrameBean(in);
        }

        @Override
        public VideoTrimmerFrameBean[] newArray(int size) {
            return new VideoTrimmerFrameBean[size];
        }
    };


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getFramePath() {
        return framePath;
    }

    public void setFramePath(String framePath) {
        this.framePath = framePath;
    }

    public int getFrameWidth() {
        return frameWidth;
    }

    public void setFrameWidth(int frameWidth) {
        this.frameWidth = frameWidth;
    }

    public int getFrameHeight() {
        return frameHeight;
    }

    public void setFrameHeight(int frameHeight) {
        this.frameHeight = frameHeight;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(framePath);
        dest.writeInt(frameWidth);
        dest.writeInt(frameHeight);
        dest.writeInt(index);
    }
}

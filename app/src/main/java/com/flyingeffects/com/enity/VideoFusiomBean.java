package com.flyingeffects.com.enity;

import java.io.Serializable;

public class VideoFusiomBean implements Serializable {


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMp4() {
        return mp4;
    }

    public void setMp4(String mp4) {
        this.mp4 = mp4;
    }

    private int status;

    private String mp4;
}

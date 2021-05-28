package com.flyingeffects.com.enity;

/**
 * @author ZhouGang
 * @date 2021/5/24
 * 字幕实体
 */
public class SubtitleEntity {
    private long bg;
    private long ed;
    private String onebest;

    public long getStartTime() {
        return bg;
    }

    public void setStartTime(long startTime) {
        this.bg = startTime;
    }

    public long getEndTime() {
        return ed;
    }

    public void setEndTime(long endTime) {
        this.ed = endTime;
    }

    public String getText() {
        return onebest;
    }

    public void setText(String text) {
        this.onebest = text;
    }
}

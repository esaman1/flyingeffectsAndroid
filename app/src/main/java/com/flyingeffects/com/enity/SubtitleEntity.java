package com.flyingeffects.com.enity;

/**
 * @author ZhouGang
 * @date 2021/5/24
 * 字幕实体
 */
public class SubtitleEntity {
    private long startTime;
    private long endTime;
    private String sentences;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getText() {
        return sentences;
    }

    public void setText(String text) {
        this.sentences = text;
    }
}

package com.flyingeffects.com.enity;


import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.nineton.ntadsdk.bean.FeedAdConfigBean;

import java.io.Serializable;

/**
 * @ClassName: CommonNewsBean
 * @Author: CaoLong
 * @CreateDate: 2020/7/30 15:51
 * @Description:
 */
public class CommonNewsBean extends FeedAdConfigBean.FeedAdResultBean implements MultiItemEntity, Serializable {

    private String subTitle;
    private int eventType;
    private boolean hide;
    private boolean isVideo;
    private String readCounts;
    private String duration;


    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    @Override
    public int getEventType() {
        return eventType;
    }

    @Override
    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public boolean isHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public String getReadCounts() {
        return readCounts;
    }

    public void setReadCounts(String readCounts) {
        this.readCounts = readCounts;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public int getItemType() {
        int type = 0;
        switch (getEventType()) {
            case 0:
                type = 0;
                break;
            case BAIDU_FEED_AD_EVENT:
            case TT_FEED_AD_EVENT:
                type = 11;
                break;
            case GDT_FEED_AD_EVENT: {
                type = 12;
                break;
            }
        }
        return type;
    }

}


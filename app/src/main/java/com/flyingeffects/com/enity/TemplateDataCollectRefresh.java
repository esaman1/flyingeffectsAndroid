package com.flyingeffects.com.enity;

import java.io.Serializable;

/**
 * @author 111
 */
public class TemplateDataCollectRefresh implements Serializable {
    private boolean isSeleted;
    private int position;
    //0模板收藏  1//我上传的背景,2//收藏背景,3 模板,4, 背景
    private int from;

    public TemplateDataCollectRefresh(int position, boolean isSeleted, int from) {
        this.position = position;
        this.isSeleted = isSeleted;
        this.from = from;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public boolean isSeleted() {
        return isSeleted;
    }

    public void setSeleted(boolean seleted) {
        isSeleted = seleted;
    }

}

package com.flyingeffects.com.enity;

import java.io.Serializable;

public class templateDataZanRefresh implements Serializable {


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getZanCount() {
        return zanCount;
    }

    public void setZanCount(int zanCount) {
        this.zanCount = zanCount;
    }



    private int position;
    private int zanCount;


    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }



    //0模板收藏  1//我上传的背景,2//收藏背景,3 模板,4, 背景
    private int from;

    public boolean isSeleted() {
        return isSeleted;
    }

    public void setSeleted(boolean seleted) {
        isSeleted = seleted;
    }

    private boolean isSeleted;

    public templateDataZanRefresh(int position, int zanCount, boolean isSeleted,int from ) {
        this.position = position;
        this.zanCount = zanCount;
        this.isSeleted = isSeleted;
        this.from=from;
    }


}

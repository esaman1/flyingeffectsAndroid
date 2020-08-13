package com.flyingeffects.com.enity;

import java.io.Serializable;

public class templateDataCollectRefresh implements Serializable {


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
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

    public templateDataCollectRefresh(int position, boolean isSeleted, int from ) {
        this.position = position;
        this.isSeleted = isSeleted;
        this.from=from;
    }


}

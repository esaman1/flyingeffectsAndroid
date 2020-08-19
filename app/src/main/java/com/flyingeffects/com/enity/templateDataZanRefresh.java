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




    public boolean isSeleted() {
        return isSeleted;
    }

    public void setSeleted(boolean seleted) {
        isSeleted = seleted;
    }

    private boolean isSeleted;

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    private int templateId;



    public templateDataZanRefresh(int position, int zanCount, boolean isSeleted,int templateId ) {
        this.position = position;
        this.zanCount = zanCount;
        this.isSeleted = isSeleted;
        this.templateId=templateId;
    }


}

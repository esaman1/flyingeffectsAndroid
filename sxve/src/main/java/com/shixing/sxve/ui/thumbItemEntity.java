package com.shixing.sxve.ui;

import java.io.Serializable;

public class thumbItemEntity implements Serializable {
    private String path="";

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    private int tag=1;  //0选中，1未选中


}

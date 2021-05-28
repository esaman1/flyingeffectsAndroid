package com.flyingeffects.com.entity;

import java.io.Serializable;

public class WxLogin  implements Serializable {


    public WxLogin(String tag){
        this.tag=tag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    private String tag;
}

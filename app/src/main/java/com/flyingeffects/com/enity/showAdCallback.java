package com.flyingeffects.com.enity;

import java.io.Serializable;

public class showAdCallback implements Serializable {

    public String getIsFrom() {
        return isFrom;
    }

    public void setIsFrom(String isFrom) {
        this.isFrom = isFrom;
    }

    String isFrom;


    public  showAdCallback(String isFrom) {
        this.isFrom=isFrom;
    }
}

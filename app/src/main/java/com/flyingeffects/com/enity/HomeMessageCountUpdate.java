package com.flyingeffects.com.enity;

import java.io.Serializable;

public class HomeMessageCountUpdate implements Serializable {

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    private int state;
    public HomeMessageCountUpdate(int state){
        this.state=state;
    }

}

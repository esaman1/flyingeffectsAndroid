package com.mobile.CloudMovie.enity;

import java.io.Serializable;

public class HomeItemEnity  implements Serializable {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name="";
}

package com.flyingeffects.com.view;

import java.io.Serializable;

public class MattingVideoEnity implements Serializable {

    public MattingVideoEnity(String originalPath,String mattingPath){
        this.originalPath=originalPath;
        this.mattingPath=mattingPath;

    }


    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    public String getMattingPath() {
        return mattingPath;
    }

    public void setMattingPath(String mattingPath) {
        this.mattingPath = mattingPath;
    }

    private String originalPath;
    private String mattingPath;


}

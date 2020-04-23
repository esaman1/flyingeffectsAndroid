package com.flyingeffects.com.enity;

import java.io.Serializable;


public class CreateCutCallback implements Serializable {

    public CreateCutCallback(String coverPath, String originalPath,  boolean isNeedCut) {
        this.coverPath = coverPath;
        this.originalPath = originalPath;
        this.isNeedCut = isNeedCut;
    }



    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }



    public boolean isNeedCut() {
        return isNeedCut;
    }

    public void setNeedCut(boolean needCut) {
        isNeedCut = needCut;
    }

    private String coverPath;
    private String originalPath;
    private boolean isNeedCut;


}

package com.flyingeffects.com.enity;

public class CutSuccess {

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    private  String filePath;

    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    private String originalPath;

    public CutSuccess(String filePath,String originalPath){
        this.originalPath=originalPath;
        this.filePath=filePath;
    };



}

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


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;

    private String originalPath;

    public CutSuccess(String filePath,String originalPath,String title){
        this.originalPath=originalPath;
        this.filePath=filePath;
        this.title=title;
    };



}

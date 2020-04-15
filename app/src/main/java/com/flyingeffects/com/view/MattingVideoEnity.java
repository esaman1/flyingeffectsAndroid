package com.flyingeffects.com.view;

import java.io.Serializable;

public class MattingVideoEnity implements Serializable {



    /**
     * description ：
     * creation date: 2020/4/14
     * param : tag  界面标志 1 表示模板详情页 0表示预览页面
     * user : zhangtongju
     */
    public MattingVideoEnity(String originalPath,String mattingPath,int tag){
        this.originalPath=originalPath;
        this.mattingPath=mattingPath;
        this.tag=tag;
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


    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    /**
     * description ：界面标志，目前有3个，一个是来自预览界面 0，另一个是来自抠图开关按钮1,2来自替换素材
     * creation date: 2020/4/14
     * user : zhangtongju
     */
    private int tag;


}

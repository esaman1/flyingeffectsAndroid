package com.flyingeffects.com.enity;

import java.io.Serializable;


/**
 * description ：用户选择的素材
 * creation date: 2020/4/22
 * user : zhangtongju
 */
public class DownVideoPath implements Serializable {


    public DownVideoPath(String path){
        this.path=path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private  String path;



}

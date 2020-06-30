package com.flyingeffects.com.enity;

import java.io.Serializable;


/**
 * description ：用户选择的素材
 * creation date: 2020/4/22
 * user : zhangtongju
 */
public class SendSearchText implements Serializable {


    public SendSearchText(String text){
        this.text=text;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private  String text;



}

package com.flyingeffects.com.enity;

import java.io.Serializable;
import java.util.ArrayList;

public class uploadMaterialEvent implements Serializable {

    private ArrayList<String> uploadPathList;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    private String nickName;

    public uploadMaterialEvent(ArrayList<String> uploadPathList,String nickName) {
        this.uploadPathList = uploadPathList;
    }

    public ArrayList<String> getUploadPathList() {
        return uploadPathList;
    }

    public void setUploadPathList(ArrayList<String> uploadPathList) {
        this.uploadPathList = uploadPathList;
    }


}

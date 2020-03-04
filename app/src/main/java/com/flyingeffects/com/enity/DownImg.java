package com.flyingeffects.com.enity;

import java.io.Serializable;
import java.util.ArrayList;

public class DownImg implements Serializable {


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    private int code;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ArrayList<DownImgDataList> getData() {
        return data;
    }

    public void setData(ArrayList<DownImgDataList> data) {
        this.data = data;
    }

    private String msg;
      private String time;
      private ArrayList<DownImgDataList>data;


}

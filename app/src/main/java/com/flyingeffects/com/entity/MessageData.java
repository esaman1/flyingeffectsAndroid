package com.flyingeffects.com.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class MessageData implements Serializable {

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public ArrayList<MessageEnity> getList() {
        return list;
    }

    public void setList(ArrayList<MessageEnity> list) {
        this.list = list;
    }

    private String total;
    private ArrayList<MessageEnity>list;

}

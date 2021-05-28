package com.flyingeffects.com.entity;

import java.util.List;

public class SystemMessageDetailAllEnity {

    public List<SystemMessageDetailEnity> getList() {
        return list;
    }

    public void setList(List<SystemMessageDetailEnity> list) {
        this.list = list;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    private  List<SystemMessageDetailEnity>list;
    private int total;

}

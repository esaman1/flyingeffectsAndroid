package com.flyingeffects.com.entity;

/**
 * @author ZhouGang
 * @date 2020/10/12
 * 关键字模糊查询实体
 */
public class SearchTemplateInfoEntity {

    /**
     * id : 15.0
     * pid : 0.0
     * name : 国庆
     * create_time : 1.601192679E9
     */

    private double id;
    private double pid;
    private String name;
    private double create_time;

    public double getId() {
        return id;
    }

    public void setId(double id) {
        this.id = id;
    }

    public double getPid() {
        return pid;
    }

    public void setPid(double pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCreate_time() {
        return create_time;
    }

    public void setCreate_time(double create_time) {
        this.create_time = create_time;
    }
}

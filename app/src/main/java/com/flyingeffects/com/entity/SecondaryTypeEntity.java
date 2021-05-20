package com.flyingeffects.com.entity;

import java.io.Serializable;

/**
 * @author ZhouGang
 * @date 2020/12/3
 * 二级分类实体
 */
public class SecondaryTypeEntity implements Serializable {

    /**
     * id : 12
     * name : 汉服
     */

    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

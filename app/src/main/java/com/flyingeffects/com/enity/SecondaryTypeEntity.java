package com.flyingeffects.com.enity;

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

    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

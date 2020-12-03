package com.flyingeffects.com.enity;

import java.util.List;

/**
 * @author ZhouGang
 * @date 2020/12/3
 * 一级分类实体
 */
public class FirstLevelTypeEntity {
    private String id;
    private String name;
    private List<SecondaryTypeEntity> category;

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

    public List<SecondaryTypeEntity> getCategory() {
        return category;
    }

    public void setCategory(List<SecondaryTypeEntity> category) {
        this.category = category;
    }
}

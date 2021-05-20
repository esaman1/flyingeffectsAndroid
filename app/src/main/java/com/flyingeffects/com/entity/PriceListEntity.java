package com.flyingeffects.com.entity;


import java.math.BigDecimal;
import java.util.Collection;

/**
 * 价格列表
 */
public class PriceListEntity {
    private int id;
    private String name;
    private String old_price;
    private String price;
    private String status;
    private String info;
    private int is_discounts;
    private String desc;

    private boolean isChecked;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOld_price() {
        return old_price;
    }

    public void setOld_price(String old_price) {
        this.old_price = old_price;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getIs_discounts() {
        return is_discounts;
    }

    public void setIs_discounts(int is_discounts) {
        this.is_discounts = is_discounts;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}

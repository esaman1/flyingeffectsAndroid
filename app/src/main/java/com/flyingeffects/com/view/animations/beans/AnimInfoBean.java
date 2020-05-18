package com.flyingeffects.com.view.animations.beans;

import java.io.Serializable;


/**
 * description ：请求动画状态及是否是vip 等数据结构
 * date: ：2019/10/16 17:39
 * author: 张同举 @邮箱 jutongzhang@sina.com
 */
public class AnimInfoBean implements Serializable {
    public AnimInfoBean(String name,String key, String isVip){
        this.new_name=name;
        this.new_key=key;
        this.new_is_vip=isVip;
    }
    private String new_is_vip;
    private String new_name;
    private String new_key;
    private int id;

    public String getNew_is_vip() {
        return new_is_vip;
    }

    public void setNew_is_vip(String new_is_vip) {
        this.new_is_vip = new_is_vip;
    }

    public String getNew_name() {
        return new_name;
    }

    public void setNew_name(String new_name) {
        this.new_name = new_name;
    }

    public String getNew_key() {
        return new_key;
    }

    public void setNew_key(String new_key) {
        this.new_key = new_key;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getWeigh() {
        return weigh;
    }

    public void setWeigh(int weigh) {
        this.weigh = weigh;
    }

    private String version;
    private int weigh;
}

package com.flyingeffects.com.enity;

/**
 * @author ZhouGang
 * @date 2020/12/7
 * 贴纸分类实体
 */
public class StickerTypeEntity {

    /**
     * id : 1
     * name : 最新
     * status : 1
     * weigh : 4
     */

    private int id;
    private String name;
    private String status;
    private int weigh;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getWeigh() {
        return weigh;
    }

    public void setWeigh(int weigh) {
        this.weigh = weigh;
    }
}

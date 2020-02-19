package com.mobile.flyingeffects.enity;

import java.io.Serializable;

public class new_fag_template_item implements Serializable {
    
 private String id="";
         private String title="";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIcon() {
        return icon;
    }


    public String getRequired_material() {
        return required_material;
    }

    public void setRequired_material(String required_material) {
        this.required_material = required_material;
    }

    private String required_material;

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVip_ios() {
        return vip_ios;
    }

    public void setVip_ios(String vip_ios) {
        this.vip_ios = vip_ios;
    }

    public String getVip_android() {
        return vip_android;
    }

    public void setVip_android(String vip_android) {
        this.vip_android = vip_android;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getStatus_and() {
        return status_and;
    }

    public void setStatus_and(String status_and) {
        this.status_and = status_and;
    }

    public String getStatus_ios() {
        return status_ios;
    }

    public void setStatus_ios(String status_ios) {
        this.status_ios = status_ios;
    }

    public String getTimelen() {
        return timelen;
    }

    public void setTimelen(String timelen) {
        this.timelen = timelen;
    }

    private String thumb="";
         private String link="";
         private String enabled="";
         private String author="";
         private String icon="";
         private String material="";
         private String classification="";
         private String type="";
         private String vip_ios="";
         private String vip_android="";
         private String sort="";
         private String status_and="";
         private String status_ios="";
         private String timelen="";

    public int getIs_nav() {
        return is_nav;
    }

    public void setIs_nav(int is_nav) {
        this.is_nav = is_nav;
    }

    //0 表示外部，1表示内部
         private int is_nav=0;

    public int getIs_advices() {
        return is_advices;
    }

    public void setIs_advices(int is_advices) {
        this.is_advices = is_advices;
    }

    private int is_advices;  //1  表示有激励视频，0表示没得

    public boolean isAdvertisinga() {
        return isAdvertisinga;
    }

    public void setAdvertisinga(boolean advertisinga) {
        isAdvertisinga = advertisinga;
    }

    private boolean isAdvertisinga=false; //是否是广告

    public String getDiversion() {
        return diversion;
    }

    public void setDiversion(String diversion) {
        this.diversion = diversion;
    }

    private String diversion=""; //爱字幕广告

    public int getSet_erect() {
        return set_erect;
    }

    public void setSet_erect(int set_erect) {
        this.set_erect = set_erect;
    }

    private int set_erect;//是否允许横竖屏 1可以


    public int getIs_erect() {
        return is_erect;
    }

    public void setIs_erect(int is_erect) {
        this.is_erect = is_erect;
    }

    private int is_erect; //是否是横竖屏

    public String getPicture_num() {
        return picture_num;
    }

    public void setPicture_num(String picture_num) {
        this.picture_num = picture_num;
    }


    private String picture_num="";

    public String getVideo_num() {
        return video_num;
    }

    public void setVideo_num(String video_num) {
        this.video_num = video_num;
    }

    private String  video_num="";



    public String getCompressName() {
        return compressName;
    }


    public int getSlow_motion() {
        return slow_motion;
    }

    public void setSlow_motion(int slow_motion) {
        this.slow_motion = slow_motion;
    }

    /**
     * 1 是慢动作
     */
    private int slow_motion;

    public void setCompressName(String compressName) {
        this.compressName = compressName;
    }

    private String compressName="";

    public String getCompress() {
        return compress;
    }

    public void setCompress(String compress) {
        this.compress = compress;
    }

    private String compress="";

    public String getMaterial_info() {
        return material_info;
    }

    public void setMaterial_info(String material_info) {
        this.material_info = material_info;
    }

    private String material_info="";


}

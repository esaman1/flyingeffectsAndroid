package com.flyingeffects.com.enity;

public class DownImgDataList {

    public String getMask_url() {
        return mask_url;
    }

    public void setMask_url(String mask_url) {
        this.mask_url = mask_url;
    }

    public String getTarget_url() {
        return target_url;
    }

    public void setTarget_url(String target_url) {
        this.target_url = target_url;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    private String mask_url;
    private String target_url;
    private String request_id;

    public String getHuawei_url() {
        return huawei_url;
    }

    public void setHuawei_url(String huawei_url) {
        this.huawei_url = huawei_url;
    }

    private String huawei_url;
    private int code;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    /**
     * 1  versa、2 face++、3 baidu
     */
    private int type;


    public String getScoremap() {
        return scoremap;
    }

    public void setScoremap(String scoremap) {
        this.scoremap = scoremap;
    }

    /**
     * 百度返回来的图片编码
     */
    private String scoremap;
}

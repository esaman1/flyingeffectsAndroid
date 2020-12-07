package com.flyingeffects.com.enity;

import java.io.Serializable;

public class HumanMerageResult implements Serializable {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getResult_image() {
        return result_image;
    }

    public void setResult_image(String result_image) {
        this.result_image = result_image;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResult_time() {
        return result_time;
    }

    public void setResult_time(String result_time) {
        this.result_time = result_time;
    }

    public String getSpend_time() {
        return spend_time;
    }

    public void setSpend_time(String spend_time) {
        this.spend_time = spend_time;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public String getTemplate_url() {
        return template_url;
    }

    public void setTemplate_url(String template_url) {
        this.template_url = template_url;
    }

    public String getMerge_rate() {
        return merge_rate;
    }

    public void setMerge_rate(String merge_rate) {
        this.merge_rate = merge_rate;
    }

    public String getFeature_rate() {
        return feature_rate;
    }

    public void setFeature_rate(String feature_rate) {
        this.feature_rate = feature_rate;
    }

    private String user_id;
    private String image;
    private String result_image;
    private String create_time;
    private String status;
    private String result_time;
    private String spend_time;
    private String remark;
    private String type;
    private String channel;
    private String template_id;
    private String request_id;
    private String template_url;
    private String merge_rate;
    private String feature_rate;

}

package com.flyingeffects.com.enity;

import java.io.Serializable;

public class ConfigForTemplateList implements Serializable {
    

private String id;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    public String getCopydata() {
        return copydata;
    }

    public void setCopydata(String copydata) {
        this.copydata = copydata;
    }

    public String getKuaishouurl() {
        return kuaishouurl;
    }

    public void setKuaishouurl(String kuaishouurl) {
        this.kuaishouurl = kuaishouurl;
    }

    public String getDouyinurl() {
        return douyinurl;
    }

    public void setDouyinurl(String douyinurl) {
        this.douyinurl = douyinurl;
    }

    public String getThirdline() {
        return thirdline;
    }

    public void setThirdline(String thirdline) {
        this.thirdline = thirdline;
    }

    public String getSecondline() {
        return secondline;
    }

    public void setSecondline(String secondline) {
        this.secondline = secondline;
    }

    public String getFirstline() {
        return firstline;
    }

    public void setFirstline(String firstline) {
        this.firstline = firstline;
    }

    private String title;
        private String content;
        private String description;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private int type;
        private String copydata;
        private String kuaishouurl;
        private String douyinurl;
        private String thirdline;
        private String secondline;
        private String firstline;
}

package com.flyingeffects.com.enity;

import java.io.Serializable;

public class checkVersion implements Serializable {

    private String id;

    public String getNewversion() {
        return newversion;
    }

    public void setNewversion(String newversion) {
        this.newversion = newversion;
    }

    private String newversion;

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


    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    private String title;
    private String content;

    public String getDownloadfile() {
        return downloadfile;
    }

    public void setDownloadfile(String downloadfile) {
        this.downloadfile = downloadfile;
    }

    private String downloadfile;

    public String getIs_forceupdate() {
        return is_forceupdate;
    }

    public void setIs_forceupdate(String is_forceupdate) {
        this.is_forceupdate = is_forceupdate;
    }

    private String is_forceupdate;
    private String enabled;

    public int getIs_advertising() {
        return is_advertising;
    }

    public void setIs_advertising(int is_advertising) {
        this.is_advertising = is_advertising;
    }

    private int is_advertising;
}

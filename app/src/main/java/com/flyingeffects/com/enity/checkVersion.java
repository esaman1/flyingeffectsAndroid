package com.flyingeffects.com.enity;

import java.io.Serializable;

public class checkVersion implements Serializable {

    private String id;
    private String version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getIs_renew() {
        return is_renew;
    }

    public void setIs_renew(String is_renew) {
        this.is_renew = is_renew;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    private String title;
    private String content;
    private String path;
    private String is_renew;
    private String enabled;

    public int getIs_advertising() {
        return is_advertising;
    }

    public void setIs_advertising(int is_advertising) {
        this.is_advertising = is_advertising;
    }

    private int is_advertising;
}

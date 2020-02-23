package com.flyingeffects.com.enity;

import java.io.Serializable;

public class TemplateType implements Serializable {
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

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    private String id = "";
    private String name = "";
    private String enabled = "";


}

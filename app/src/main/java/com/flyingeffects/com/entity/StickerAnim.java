package com.flyingeffects.com.entity;

import com.flyingeffects.com.view.animations.CustomMove.AnimType;

import java.io.Serializable;

public class StickerAnim implements Serializable {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public AnimType getAnimType() {
        return animType;
    }

    public void setAnimType(AnimType animType) {
        this.animType = animType;
    }

    private AnimType animType;

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    private int icon;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    private boolean isChecked=false;
}

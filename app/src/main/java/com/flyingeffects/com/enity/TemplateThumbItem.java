package com.flyingeffects.com.enity;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class TemplateThumbItem implements Serializable {

   private String pathUrl;

    public boolean isHasText() {
        return hasText;
    }

    public void setHasText(boolean hasText) {
        this.hasText = hasText;
    }

    private boolean hasText=false;

    public Drawable getBgPath() {
        return bgPath;
    }

    public void setBgPath(Drawable bgPath) {
        this.bgPath = bgPath;
    }

    private Drawable bgPath=null;

    public String getPathUrl() {
        return pathUrl;
    }

    public void setPathUrl(String pathUrl) {
        this.pathUrl = pathUrl;
    }

    public int getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(int isCheck) {
        this.isCheck = isCheck;
    }

    public boolean isAuto() {
        return isAuto;
    }

    public void setAuto(boolean auto) {
        isAuto = auto;
    }

    private boolean isAuto=false;

    private  int isCheck;  //0选中

    public int getIsChooseAnim() {
        return isChooseAnim;
    }

    public void setIsChooseAnim(int isChooseAnim) {
        this.isChooseAnim = isChooseAnim;
    }

    private int isChooseAnim;//0 没有设置，1设置的

    public TemplateThumbItem(String pathUrl, int isCheck, boolean isAuto){
        this.isCheck=isCheck;
        this.pathUrl=pathUrl;
        this.isAuto=isAuto;
    }

    public TemplateThumbItem(){
    }
}

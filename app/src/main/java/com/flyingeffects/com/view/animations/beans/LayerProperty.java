package com.flyingeffects.com.view.animations.beans;

import android.graphics.PointF;

public class LayerProperty {

    private int id;
    private Location location;



    private PointF center=new PointF(0,0);
    //XY统一缩放
    private PointF scaleXY=new PointF(1f,1f);
    private float rotate=0f;

    private int layerW;
    private int layerH;

    //主层属性
    private LayerProperty mainProperty;
    /**
     *
     * @param id 标记
     * @param position 相对位置
     * @param mainProperty null代表这就是主层
     */
    public LayerProperty(int id, Location position, LayerProperty mainProperty){
        this.id=id;
        this.location=position;
        this.mainProperty=mainProperty;
    }

    //图层的相对位置
    public enum Location{
        UP_LEFT,UP_CENTER,UP_RIGHT,
        MIDDLE_LEFT,MIDDLE_CENTER,MIDDLE_RIGHT,
        DOWN_LEFT,DOWN_CENTER,DOWN_RIGHT
    }
    public int getId() {
        return id;
    }


    public Location getLocation() {
        return location;
    }

    public void setCenter(PointF center) {
        this.center = center;
    }

    public float getRotate() {
        if (this.location== Location.MIDDLE_CENTER){
            return this.rotate;
        }else {
            return mainProperty.rotate;
        }
    }

    public void setRotate(float rotate) {
        this.rotate = rotate;
    }

    public boolean isXMirror() {
        switch (location){
            case UP_LEFT:
            case UP_RIGHT:
            case MIDDLE_LEFT:
            case MIDDLE_RIGHT:
            case DOWN_LEFT:
            case DOWN_RIGHT:
                return true;
            default:
                return false;
        }
    }


    public boolean isYMirror() {
        switch (location){
            case UP_LEFT:
            case UP_CENTER:
            case UP_RIGHT:
            case DOWN_LEFT:
            case DOWN_CENTER:
            case DOWN_RIGHT:
                return true;
            default:
                return false;
        }
    }


    public PointF getScaleXY() {
        if (this.location== Location.MIDDLE_CENTER){
            return this.scaleXY;
        }else {
            return mainProperty.scaleXY;
        }
    }

    public void setScaleXY(PointF scaleXY) {
        this.scaleXY = scaleXY;
    }


    public void setLayerW(int layerW) {
        this.layerW = layerW;
    }

    public void setLayerH(int layerH) {
        this.layerH = layerH;
    }

    public int getLayerW() {
        if (this.location== Location.MIDDLE_CENTER){
            return layerW;
        }else {
            return mainProperty.layerW;
        }
    }

    public int getLayerH() {
        if (this.location== Location.MIDDLE_CENTER){
            return layerH;
        }else {
            return mainProperty.layerH;
        }
    }
    //点阵旋转的方法
    public PointF getCenter() {
        float oriX,oriY;
        float scaleX=getScaleXY().x!=0?getScaleXY().x:1f;
        float scaleY=getScaleXY().y!=0?getScaleXY().y:1f;
        switch (location){
            //本对象就是主图层
            default:
                return center;
            case UP_LEFT:
                oriX=mainProperty.center.x-getLayerW()*scaleX;
                oriY=mainProperty.center.y-getLayerH()*scaleY;
                break;
            case UP_CENTER:
                oriX=mainProperty.center.x;
                oriY=mainProperty.center.y-getLayerH()*scaleY;
                break;
            case UP_RIGHT:
                oriX=mainProperty.center.x+getLayerW()*scaleX;
                oriY=mainProperty.center.y-getLayerH()*scaleY;
                break;
            case MIDDLE_LEFT:
                oriX=mainProperty.center.x-getLayerW()*scaleX;
                oriY=mainProperty.center.y;
                break;
            case MIDDLE_RIGHT:
                oriX=mainProperty.center.x+getLayerW()*scaleX;
                oriY=mainProperty.center.y;
                break;
            case DOWN_LEFT:
                oriX=mainProperty.center.x-getLayerW()*scaleX;
                oriY=mainProperty.center.y+getLayerH()*scaleY;
                break;
            case DOWN_CENTER:
                oriX=mainProperty.center.x;
                oriY=mainProperty.center.y+getLayerH()*scaleY;
                break;
            case DOWN_RIGHT:
                oriX=mainProperty.center.x+getLayerW()*scaleX;
                oriY=mainProperty.center.y+getLayerH()*scaleY;
                break;
        }
        if (getRotate()!=0f){
            double radian=Math.toRadians(getRotate());
            double cosA=Math.cos(radian);
            double sinA=Math.sin(radian);
            double xMinusA=oriX-getRotationCenter().x;
            double yMinusB=oriY-getRotationCenter().y;
            float devX=(float)((xMinusA*cosA)-(yMinusB*sinA)+getRotationCenter().x);
            float devY=(float)((xMinusA*sinA)+(yMinusB)*cosA+getRotationCenter().y);
            return new PointF(devX,devY);
        }
        return new PointF(oriX,oriY);
    }
    public PointF getRotationCenter() {
        if (this.location== Location.MIDDLE_CENTER){
            return this.center;
        }else {
            return mainProperty.center;
        }
    }
}

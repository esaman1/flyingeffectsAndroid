package com.flyingeffects.com.view.animations.beans;

import java.io.Serializable;

public class AnimElement implements Serializable {
    //起始帧
    private int startFrame;
    //结束帧
    private int endFrame;
    //曲线
    private float[] curve;

    public int getStartFrame() {
        return startFrame;
    }

    public void setStartFrame(int startFrame) {
        this.startFrame = startFrame;
    }

    public int getEndFrame() {
        return endFrame;
    }

    public void setEndFrame(int endFrame) {
        this.endFrame = endFrame;
    }

    public float[] getCurve() {
        return curve;
    }

    public void setCurve(float[] curve) {
        this.curve = curve;
    }

    public float getStartVal() {
        return startVal;
    }

    public void setStartVal(float startVal) {
        this.startVal = startVal;
    }

    public float getEndVal() {
        return endVal;
    }

    public void setEndVal(float endVal) {
        this.endVal = endVal;
    }

    public AnimEnum getType() {
        return type;
    }

    public void setType(AnimEnum type) {
        this.type = type;
    }

    //起点
    private float startVal;
    //终点
    private float endVal;
    //类型
    private AnimEnum type;

    public AnimElement(){
        //默认起始帧为0
        this.startFrame=0;
        //默认结束帧为90
        this.endFrame=90;
        //默认曲线为
        this.curve=new float[]{1f,1f,1f,1f};
        //默认起点为540
        this.startVal =540;
        //默认终点为540
        this.endVal =540;
        //默认类型为
        type=AnimEnum.Zoom;
    }
    public AnimElement(int startFrame, int endFrame, float startVal, float endVal, float[] curve, AnimEnum type){
        this.startFrame=startFrame;
        this.endFrame=endFrame;
        this.startVal = startVal;
        this.endVal = endVal;
        this.curve=curve;
        this.type=type;
    }
}

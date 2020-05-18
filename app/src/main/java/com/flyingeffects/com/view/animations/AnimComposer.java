package com.flyingeffects.com.view.animations;

import android.graphics.PointF;
import android.view.animation.PathInterpolator;


import com.flyingeffects.com.view.animations.beans.AnimElement;
import com.flyingeffects.com.view.animations.beans.AnimEnum;

import java.util.ArrayList;
import java.util.List;

public class AnimComposer {
    //新剪映动画参数尺寸
    private int[] size=new int[]{1080,1080};
    //总共按90帧为结束来计算动画进度
    private int totalFrame=90;
    private List<AnimElement> xAnimList=new ArrayList<>();
    private List<AnimElement> yAnimList=new ArrayList<>();
    private List<AnimElement> zoomAnimList=new ArrayList<>();

    public List<AnimElement> getZoomYAnimList() {
        return zoomYAnimList;
    }

    public void setZoomYAnimList(List<AnimElement> zoomYAnimList) {
        this.zoomYAnimList = zoomYAnimList;
    }

    //    private List<AnimElement> zoomXAnimList=new ArrayList<>();
    private List<AnimElement> zoomYAnimList=new ArrayList<>();

    public long getMaxDurationMs() {
        return maxDurationMs;
    }

    public void setMaxDurationMs(long maxDurationMs) {
        this.maxDurationMs = maxDurationMs;
    }

    //最长持续时间,单位毫秒
    private long maxDurationMs=0;
    //当前进度，默认零
    private float progress=0f;

    public int[] getSize() {
        return size;
    }

    public void setSize(int[] size) {
        this.size = size;
    }

    public int getTotalFrame() {
        return totalFrame;
    }

    public void setTotalFrame(int totalFrame) {
        this.totalFrame = totalFrame;
    }

    public List<AnimElement> getxAnimList() {
        return xAnimList;
    }

    public void setxAnimList(List<AnimElement> xAnimList) {
        this.xAnimList = xAnimList;
    }

    public List<AnimElement> getyAnimList() {
        return yAnimList;
    }

    public void setyAnimList(List<AnimElement> yAnimList) {
        this.yAnimList = yAnimList;
    }

    public List<AnimElement> getZoomAnimList() {
        return zoomAnimList;
    }

    public void setZoomAnimList(List<AnimElement> zoomAnimList) {
        this.zoomAnimList = zoomAnimList;
    }

    public List<AnimElement> getRotateAnimList() {
        return rotateAnimList;
    }

    public void setRotateAnimList(List<AnimElement> rotateAnimList) {
        this.rotateAnimList = rotateAnimList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private List<AnimElement> rotateAnimList=new ArrayList<>();
    private String name;
    private int id;

    public AnimComposer(String name, int id){
        this.name=name;
        this.id=id;
    }

    public void add(AnimElement element){
        switch (element.getType()){
            case X:
                getxAnimList().add(element);
                break;
            case Y:
                getyAnimList().add(element);
                break;
            case Zoom:
                getZoomAnimList().add(element);
                break;
            case Rotate:
                getRotateAnimList().add(element);
                break;
            case ZoomY:
                getZoomYAnimList().add(element);
                break;
            default:
                break;
        }
    }
    public float getCurrentX(){
        if (!xAnimList.isEmpty()||progress!=0f){
            return getCurrentValue(xAnimList, AnimEnum.X);
        }
        return size[0]*0.5f;
    }
    public float getCurrentY(){
        if (!yAnimList.isEmpty()||progress!=0f){
            return getCurrentValue(yAnimList,AnimEnum.Y);
        }
        return size[1]*0.5f;
    }
    public float getCurrentZoom(){
        if (!zoomAnimList.isEmpty()||progress!=0f){
            return getCurrentValue(zoomAnimList,AnimEnum.Zoom);
        }
        return 1f;
    }
    public PointF getCurrentZoomXY(){
        if (!zoomYAnimList.isEmpty()||progress!=0f){
            return new PointF(1f,getCurrentValue(zoomYAnimList,AnimEnum.ZoomY));
        }
        return new PointF(1f,1f);
    }
    public float getCurrentRotate(){
        if (!rotateAnimList.isEmpty()||progress!=0f){
            return getCurrentValue(rotateAnimList,AnimEnum.Rotate);
        }
        return 0f;
    }
    private float getCurrentValue(List<AnimElement> animList, AnimEnum type){
        float finalValue=1f;
        AnimElement curAnim = null;
        float startProgress=0f;
        float endProgress=1f;
        for (AnimElement one:
             animList) {
            startProgress=1f*one.getStartFrame()/totalFrame;
            endProgress=1f*one.getEndFrame()/totalFrame;
            if (progress==0f||progress<0f){
                cachedValue=new float[]{0x10086,0x10086,0x10086,0x10086,0x10086,0x10086};
            }else if (progress>startProgress&&progress<endProgress){
                curAnim=one;
                break;
            }else if (progress>=1f){
                cachedValue=new float[]{0x10086,0x10086,0x10086,0x10086,0x10086,0x10086};
            }
        }
        //正确查找到当前的曲线
        if (curAnim!=null){
            //总运动距离
            float distance=curAnim.getEndVal()-curAnim.getStartVal();
            //在该单运动中的进度
            float devProg=(progress-startProgress)/(endProgress-startProgress);
            float[] curve=curAnim.getCurve();
            PathInterpolator interpolator=new PathInterpolator(curve[0],curve[1],curve[2],curve[3]);
            finalValue=curAnim.getStartVal()+interpolator.getInterpolation(devProg)*distance;
            switch (type){
                case X:
                    cachedValue[0]=finalValue;
                    break;
                case Y:
                    cachedValue[1]=finalValue;
                    break;
                case Zoom:
                    cachedValue[2]=finalValue;
                    break;
                case Rotate:
                    cachedValue[3]=finalValue;
                    break;
                case ZoomY:
                    cachedValue[5]=finalValue;
                default:
                    break;
            }
        }else {
            switch (type){
                case X:
                    finalValue=cachedValue[0]!=0x10086?cachedValue[0]:size[0]*0.5f;
                    break;
                case Y:
                    finalValue=cachedValue[1]!=0x10086?cachedValue[1]:size[1]*0.5f;
                    break;
                case Zoom:
                    finalValue=cachedValue[2]!=0x10086?cachedValue[2]:1f;
                    break;
                case Rotate:
                    finalValue=cachedValue[3]!=0x10086?cachedValue[3]:0f;
                    break;
                case ZoomY:
                    finalValue=cachedValue[5]!=0x10086?cachedValue[5]:1f;
                default:
                    break;
            }
        }
        return finalValue;
    }
    public void updateProgress(float progress){
        if (progress>=0f&&progress<=1f){
            this.progress=progress;
        }
    }

    //缓存一下上一次更新的值，0是X,1是Y,2是Zoom,3是Rotate,4是ZoomX,5是ZoomY
    private float[] cachedValue=new float[]{0x10086,0x10086,0x10086,0x10086,0x10086,0x10086};
}

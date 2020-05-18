package com.flyingeffects.com.view.animations;

import android.graphics.Matrix;
import android.graphics.PointF;


import com.flyingeffects.com.view.animations.beans.LayerProperty;
import com.flyingeffects.com.view.animations.data.AnimDataSet;
import com.flyingeffects.com.view.animations.utils.AnimUtils;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AnimContainer {
    private int padWidth;
    private int padHeight;
    private int layerWidth;
    private int layerHeight;
    private static List<AnimComposer> allAnimations= AnimDataSet.generateV2DataSet();
    private List<LayerProperty> allLayers=new ArrayList<>();

    //给图片进行计算
    private Matrix picMatrix;
    //给蓝松层进行计算
    private AnimLSback cc;
    //用于设置蓝松相关层的回调
    public interface AnimLSback {
        void state(List<LayerProperty> list);
    }

    public void setPadWidth(int padWidth) {
        this.padWidth = padWidth;
    }

    public void setPadHeight(int padHeight) {
        this.padHeight = padHeight;
    }

    public void setLayerWidth(int layerWidth) {
        this.layerWidth = layerWidth;
    }

    public void setLayerHeight(int layerHeight) {
        this.layerHeight = layerHeight;
    }

    public void setPicMatrix(Matrix picMatrix) {
        this.picMatrix = picMatrix;
    }

    public void setCc(AnimLSback cc) {
        this.cc = cc;
    }

    /**
     *
     * @param padWidth 容器宽度
     * @param padHeight 容器高度
     * @param layerWidth 蓝松层宽度
     * @param layerHeight 蓝松层高度
     * @param picMatrix 用于绘制Bitmap的matrix
     * @param callback 用于控制蓝松层属性的回调
     */
    public AnimContainer(int padWidth, int padHeight,
                         int layerWidth, int layerHeight,Matrix picMatrix, @Nullable AnimLSback callback){
        this.padWidth=padWidth;
        this.padHeight=padHeight;
        this.layerWidth=layerWidth;
        this.layerHeight=layerHeight;
        this.picMatrix=picMatrix;
        this.cc=callback;
    }

    /**
     * 用于图片动画，由于逻辑改变，已经废弃
     * @param progress 当前动画的进度
     * @param animId 动画的ID
     * @param timeDevMs 当前动画走了多少毫秒
     * @return 返回一个matrix用于canvaslayer里图片的变换
     */
    @Deprecated
    public Matrix refreshMatrix(float progress,int animId,float timeDevMs,float durationMs){
        //长宽的比例，动画师给的坐标系720*1280,中心点为(360,640)
        float defaultScale=1f;
        if (layerWidth!=0&&layerHeight!=0&&padWidth!=0&&padHeight!=0){
           defaultScale = AnimUtils.getBitmapScale(padWidth, padHeight, layerWidth, layerHeight);
        }
        picMatrix.reset();
        AnimComposer curComposer=getCurComposer(animId);
        if (curComposer!=null){
            curComposer.updateProgress(checkMaxDurationProgress(progress,timeDevMs,curComposer,durationMs));
            float curScale=curComposer.getCurrentZoom();
            PointF curScaleXY=curComposer.getCurrentZoomXY();
            //中心点X/Y的比例
            float curXRatio=curComposer.getCurrentX()/curComposer.getSize()[0];
            float curYRatio=curComposer.getCurrentY()/curComposer.getSize()[1];
            float curRotate=curComposer.getCurrentRotate();
            //左上角位置计算
            float currentX=curXRatio*padWidth;
            float currentY=curYRatio*padHeight;
            float currentScale=curScale*defaultScale;
            picMatrix.preRotate(curRotate,currentX,currentY);
            if (curScaleXY.x!=1f||curScaleXY.y!=1f){
                picMatrix.postScale(curScaleXY.x*defaultScale,curScaleXY.y*curScale*defaultScale);
            }else {
                picMatrix.postScale(curScale*defaultScale,curScale*defaultScale);
            }
            picMatrix.postTranslate(currentX-layerWidth*0.5f*currentScale,currentY-layerHeight*0.5f*currentScale);

        }
        return picMatrix;
    }

    /**
     *
     * @param progress 单个动画的进度
     * @param animId 动画的ID
     * @param timeDevMs 当前的动画已经走了多少毫秒
     * @param durationMs 用户设置的动画时长毫秒
     */
    public void refreshLSLayers(float progress, int animId, float timeDevMs,float durationMs){
        AnimComposer curComposer=getCurComposer(animId);
        if (cc!=null){
            curComposer.updateProgress(checkMaxDurationProgress(progress,timeDevMs,curComposer, durationMs));
            PointF curScaleXY=curComposer.getCurrentZoomXY();
            float curScaleX=curComposer.getCurrentZoom();
            float curScaleY=curScaleXY.y!=1f?curScaleXY.y:curScaleX;
            //中心点X/Y的比例
            float curXRatio=curComposer.getCurrentX()/curComposer.getSize()[0];
            float curYRatio=curComposer.getCurrentY()/curComposer.getSize()[1];
            float curRotate=curComposer.getCurrentRotate();

            refreshAllLayers(padWidth*curXRatio,padHeight*curYRatio,curRotate,curScaleX,curScaleY);
            cc.state(allLayers);

        }
    }

    /**
     *
     * @param mainCX 中心图层的X坐标
     * @param mainCY 中心图层的Y坐标
     * @param curRotate 中心图层的旋转角度
     * @param curScaleX  中心图层的x轴缩放比
     * @param curScaleY  中心图层的y轴缩放比
     */
    private void refreshAllLayers(float mainCX, float mainCY, float curRotate, float curScaleX, float curScaleY) {
        allLayers.clear();
        LayerProperty main=new LayerProperty(5, LayerProperty.Location.MIDDLE_CENTER,null);
        main.setRotate(curRotate);
        main.setCenter(new PointF(mainCX,mainCY));
        main.setScaleXY(new PointF(curScaleX,curScaleY));
        main.setLayerW(layerWidth);
        main.setLayerH(layerHeight);
        allLayers.add(main);
        LayerProperty tmp;
        tmp=new LayerProperty(1,LayerProperty.Location.UP_LEFT,main);
        allLayers.add(tmp);
        tmp=new LayerProperty(2,LayerProperty.Location.UP_CENTER,main);
        allLayers.add(tmp);
        tmp=new LayerProperty(3,LayerProperty.Location.UP_RIGHT,main);
        allLayers.add(tmp);
        tmp=new LayerProperty(4,LayerProperty.Location.MIDDLE_LEFT,main);
        allLayers.add(tmp);
        tmp=new LayerProperty(6,LayerProperty.Location.MIDDLE_RIGHT,main);
        allLayers.add(tmp);
        tmp=new LayerProperty(7,LayerProperty.Location.DOWN_LEFT,main);
        allLayers.add(tmp);
        tmp=new LayerProperty(8,LayerProperty.Location.DOWN_CENTER,main);
        allLayers.add(tmp);
        tmp=new LayerProperty(9,LayerProperty.Location.DOWN_RIGHT,main);
        allLayers.add(tmp);
    }

    //查找到对应的动画
    private AnimComposer getCurComposer(int animId){
        AnimComposer curComposer=null;
        for (AnimComposer composer:allAnimations){
            if (composer.getId()==animId){
                curComposer=composer;
                break;
            }
        }
        return curComposer;
    }

    //检查动画是否有时长限制

    /**
     * 部分动画有预置的最长时长的限制，用流逝的市场来决定动画的进度
     * @param progress 进度
     * @param timeDevMs 流逝时长/毫秒
     * @param curComposer 当前的动画组合
     * @param durationMs 用户打点生成的但动画时长
     * @return
     */
    private float checkMaxDurationProgress(float progress, float timeDevMs, AnimComposer curComposer, float durationMs){
        if (curComposer.getMaxDurationMs()!=0){
            if (durationMs>curComposer.getMaxDurationMs()){
                progress=timeDevMs/curComposer.getMaxDurationMs();
            }
            if (progress>1f){
                return 1f;
            }
            return progress;
        }
        return progress;
    }
}

package com.flyingeffects.com.view.animations.utils;

import android.graphics.Matrix;
import android.support.v4.util.SparseArrayCompat;
import android.view.animation.PathInterpolator;

import com.flyingeffects.com.utils.LogUtil;

import java.util.ArrayList;

import static com.flyingeffects.com.view.animations.utils.AnimUtils.ALIGN_HORIZONTAL_CENTER;
import static com.flyingeffects.com.view.animations.utils.AnimUtils.ALIGN_VERTICAL_CENTER;


@Deprecated
public class AnimTypeMatrix {
    private int padWidth;
    private int padHeight;
    private int layerWidth;
    private int layerHeight;
    private Matrix matrix;
    private static final int TEMPLATE_WIDTH=720;
    private static final int TEMPLATE_HEIGHT=1280;
    private SparseArrayCompat<float[]> animCoordinates;
    private SparseArrayCompat<ArrayList<float[]>> animCurves;
    private AnimCoordinatesCallback cc;


    public void initCoordinates(int padWidth, int padHeight, int layerWidth, int layerHeight, Matrix matrix, SparseArrayCompat<float[]> animCoordinates, SparseArrayCompat<ArrayList<float[]>> animCurves, AnimCoordinatesCallback coordinatesCallback) {



     LogUtil.d("initCoordinates","padWidth="+padWidth+"padHeight="+padHeight+"layerWidth="+layerWidth+"layerHeight="+layerHeight);

        this.padWidth = padWidth;
        this.padHeight = padHeight;
        this.layerWidth = layerWidth;
        this.layerHeight = layerHeight;
        this.matrix = matrix;
        this.animCoordinates = animCoordinates;
        this.animCurves = animCurves;
        this.cc=coordinatesCallback;
    }

    public Matrix getAnimType(float progress, String nowItemAnimChooseItem) {
        LogUtil.d("nowItemAnimChooseItem", "nowItemAnimChooseItem=" + nowItemAnimChooseItem);

        if (progress > 1f) {
            return AnimUtils.getInstance().setImgInitState(padWidth, padHeight, layerWidth, layerHeight, matrix, ALIGN_VERTICAL_CENTER, ALIGN_HORIZONTAL_CENTER);
        }
        //长宽的比例，动画师给的坐标系720*1280,中心点为(360,640)
        float xRatio = 1f * padWidth / TEMPLATE_WIDTH;
        float yRatio = 1f * padHeight / TEMPLATE_HEIGHT;
        float defaultScale = AnimUtils.getBitmapScale(padWidth, padHeight, layerWidth, layerHeight);
        matrix.reset();
        switch (nowItemAnimChooseItem) {

            case "normal_to_big":
                //@"普通放大"
                float[] cZ13 = animCoordinates.get(13);
                float currentZ13 = getInterpolatedValueForCoordinate(progress, cZ13, animCurves.get(13));
                if(cc!=null){
                    cc.scale(currentZ13,currentZ13);
                    cc.rotate(0);
                    cc.position(padWidth*0.5f,padHeight*0.5f);
                }else {
                    Matrix m13 = AnimUtils.getInstance().setImgInitState(padWidth, padHeight, layerWidth, layerHeight, matrix, ALIGN_VERTICAL_CENTER, ALIGN_HORIZONTAL_CENTER);
                    m13.postScale(currentZ13, currentZ13, padWidth * 0.5f, padHeight * 0.5f);
                    matrix = m13;
                }

                break;
            case "top_to_bottom":
                //@"从上往下"
                float[] cY2 = animCoordinates.get(2);
                float currentY2 = getInterpolatedValueForCoordinate(progress, cY2, animCurves.get(2));
                if(cc!=null){
                    cc.scale(1f,1f);
                    cc.rotate(0);
                    cc.position(padWidth*0.5f,currentY2*yRatio);

                }else {
                    Matrix mxDown = matrix;
                    mxDown.postScale(defaultScale, defaultScale);
                    mxDown.postTranslate(padWidth * 0.5f - layerWidth * 0.5f * defaultScale, currentY2 * yRatio - (layerHeight * 0.5f) * defaultScale);
                    matrix = mxDown;
                }


                break;
            case "right_to_left":
                // @"从右往左"
                float[] cX4 = animCoordinates.get(4);
                float currentX4 = getInterpolatedValueForCoordinate(progress, cX4, animCurves.get(4));
                if(cc!=null){
                    cc.scale(1f,1f);
                    cc.rotate(0);
                    cc.position(currentX4*xRatio,padHeight*0.5f);
                }else {
                    Matrix mxLeft = matrix;
                    mxLeft.postScale(defaultScale, defaultScale);
                    mxLeft.postTranslate(currentX4 * xRatio - layerWidth * 0.5f * defaultScale, padHeight * 0.5f - (layerHeight * 0.5f) * defaultScale);
                    matrix = mxLeft;
                }
                break;
            case "left_to_right":
                //@"从左往右"
                float[] cX5 = animCoordinates.get(5);
                float currentX5 = getInterpolatedValueForCoordinate(progress, cX5, animCurves.get(5));
                if(cc!=null){
                    cc.scale(1f,1f);
                    cc.rotate(0);
                    cc.position(currentX5*xRatio,padHeight*0.5f);
                }else {
                    Matrix mxRight = matrix;
                    mxRight.postScale(defaultScale, defaultScale);
                    mxRight.postTranslate(currentX5 * xRatio - layerWidth * 0.5f * defaultScale, padHeight * 0.5f - (layerHeight * 0.5f) * defaultScale);
                    matrix = mxRight;
                }

                break;
            case "bottom_to_rotate":
                //@"下弹旋转"
                float[] cR19 = animCoordinates.get(0x191);
                float currentR19 = getInterpolatedValueForCoordinate(progress, cR19, animCurves.get(0x191));
                float[] cY19 = animCoordinates.get(0x193);
                float currentY19 = getInterpolatedValueForCoordinate(progress, cY19, animCurves.get(0x193));

                if(cc!=null){
                    cc.scale(1f,1f);
                    cc.rotate(currentR19);
                    cc.position(padWidth*0.5f,currentY19 * yRatio);
                }else {
                    Matrix m19 = matrix;
                    m19.preRotate(currentR19, padWidth * 0.5f, padHeight * 0.5f);
                    m19.postScale(defaultScale, defaultScale);
                    m19.postTranslate(padWidth * 0.5f - layerWidth * 0.5f * defaultScale, currentY19 * yRatio - layerHeight * 0.5f * defaultScale);
                    matrix = m19;
                }
                break;
            case "diagonal_to_rotate":
                //@"对角旋转"
                float[] cR12 = animCoordinates.get(0x121);
                float currentR12 = getInterpolatedValueForCoordinate(progress, cR12, animCurves.get(0x121));
                //X轴位移
                float[] cX12 = animCoordinates.get(0x122);
                float currentX12 = getInterpolatedValueForCoordinate(progress, cX12, animCurves.get(0x122));
                //Y轴位移
                float[] cY12 = animCoordinates.get(0x123);
                float currentY12 = getInterpolatedValueForCoordinate(progress, cY12, animCurves.get(0x123));

                if(cc!=null){
                    cc.scale(1f,1f);
                    cc.rotate(currentR12);
                    cc.position(currentX12 * xRatio,currentY12 * yRatio);
                }else {
                    Matrix m12 = matrix;
                    m12.postScale(defaultScale, defaultScale);
                    m12.preRotate(currentR12, padWidth * 0.5f, padHeight * 0.5f);
                    m12.postTranslate(currentX12 * xRatio - layerWidth * 0.5f * defaultScale, currentY12 * yRatio - layerHeight * 0.5f * defaultScale);
                    matrix = m12;
                }
                break;
            case "top_to_bottom_swing":
                //@"上下摇摆"
                float[] cX16 = animCoordinates.get(0x162);
                float currentX16 = getInterpolatedValueForCoordinate(progress, cX16, animCurves.get(0x162));

                float[] cY16 = animCoordinates.get(0x163);
                float currentY16 = getInterpolatedValueForCoordinate(progress, cY16, animCurves.get(0x163));
                if(cc!=null){
                    cc.scale(1f,1f);
                    cc.rotate(0f);
                    cc.position(currentX16 * xRatio,currentY16 * yRatio);
                }else {
                    Matrix m16 = matrix;
                    m16.postScale(defaultScale, defaultScale);
                    m16.postTranslate(currentX16 * xRatio - layerWidth * 0.5f * defaultScale, currentY16 * yRatio - layerHeight * 0.5f * defaultScale);
                    matrix = m16;
                }
                break;
            case "rotate_to_effects":
                //@"旋转特效"
                float[] cR6 = animCoordinates.get(6);
                float currentR6 = getInterpolatedValueForCoordinate(progress, cR6, animCurves.get(6));
                if(cc!=null){
                    cc.scale(1f,1f);
                    cc.rotate(currentR6);
                    cc.position(padWidth*0.5f,padHeight*0.5f);
                }else {
                    Matrix mxShake = AnimUtils.getInstance().setImgInitState(padWidth, padHeight, layerWidth, layerHeight, matrix, ALIGN_VERTICAL_CENTER, ALIGN_HORIZONTAL_CENTER);
                    mxShake.postRotate(currentR6, padWidth * 0.5f, padHeight * 0.5f);
                    matrix = mxShake;
                }
                break;
            case "left_to_rotate":
                //@"左移旋转"
                float[] cR23 = animCoordinates.get(0x231);
                float currentR23 = getInterpolatedValueForCoordinate(progress, cR23, animCurves.get(0x231));
                float[] cX23 = animCoordinates.get(0x232);
                float currentX23 = getInterpolatedValueForCoordinate(progress, cX23, animCurves.get(0x232));

                if(cc!=null){
                    cc.scale(1f,1f);
                    cc.rotate(currentR23);
                    cc.position(currentX23 * xRatio,padHeight*0.5f);
                }else {
                    Matrix m23 = matrix;
                    m23.postScale(defaultScale, defaultScale);
                    m23.postTranslate(currentX23 * xRatio - layerWidth * 0.5f * defaultScale, padHeight * 0.5f - (layerHeight * 0.5f) * defaultScale);
                    m23.postRotate(currentR23, currentX23 * xRatio - layerWidth * 0.5f * defaultScale, padHeight * 0.5f - (layerHeight * 0.5f) * defaultScale);
                    matrix = m23;
                }
                break;
            case "centre_to_shrink":
                //@"适中缩放"
                float[] cZ17 = animCoordinates.get(17);
                float currentZ17 = getInterpolatedValueForCoordinate(progress, cZ17, animCurves.get(17));
                if(cc!=null){
                    cc.scale(currentZ17,currentZ17);
                    cc.rotate(0);
                    cc.position(padWidth*0.5f,padHeight*0.5f);
                }else {
                    Matrix m17 = AnimUtils.getInstance().setImgInitState(padWidth, padHeight, layerWidth, layerHeight, matrix, ALIGN_VERTICAL_CENTER, ALIGN_HORIZONTAL_CENTER);
                    m17.postScale(currentZ17, currentZ17, padWidth * 0.5f, padHeight * 0.5f);
                    matrix = m17;
                }

                break;
            case "effects_to_shrink":
                //@"炫酷缩小"
                float[] cZ18 = animCoordinates.get(18);
                float currentZ18 = getInterpolatedValueForCoordinate(progress, cZ18, animCurves.get(18));

                if(cc!=null){
                    cc.scale(currentZ18,currentZ18);
                    cc.rotate(0);
                    cc.position(padWidth*0.5f,padHeight*0.5f);
                }else {
                    Matrix m18 = AnimUtils.getInstance().setImgInitState(padWidth, padHeight, layerWidth, layerHeight, matrix, ALIGN_VERTICAL_CENTER, ALIGN_HORIZONTAL_CENTER);
                    m18.postScale(currentZ18, currentZ18, padWidth * 0.5f, padHeight * 0.5f);
                    matrix = m18;
                }
                break;
            case "bottom_to_top":
                //从下往上
                float[] cY3 = animCoordinates.get(3);
                float currentY3 = getInterpolatedValueForCoordinate(progress, cY3, animCurves.get(3));

                if(cc!=null){
                    cc.scale(1f,1f);
                    cc.rotate(0);
                    cc.position(padWidth*0.5f,currentY3 * yRatio);
                }else {
                    Matrix mxUp = matrix;
                    mxUp.postScale(defaultScale, defaultScale);
                    mxUp.postTranslate(padWidth * 0.5f - layerWidth * 0.5f * defaultScale, currentY3 * yRatio - (layerHeight * 0.5f) * defaultScale);
                    matrix = mxUp;
                }
                break;
            case "centre_to_rotate":
                //@"中心旋转"
                float[] cR21 = animCoordinates.get(21);
                float currentR21 = getInterpolatedValueForCoordinate(progress, cR21, animCurves.get(21));

                if(cc!=null){
                    float oX=padWidth*0.5f;
                    float oY=padHeight*0.5f;
                    float cX=padWidth*0.5f;
                    float cY=padHeight;
                    float dx= (float) (((oX-cX)*Math.cos(Math.toRadians(currentR21)))-(oY-cY)*Math.sin(Math.toRadians(currentR21))+cX);
                    float dy=(float) (((oX-cX)*Math.sin(Math.toRadians(currentR21)))+(oY-cY)*Math.cos(Math.toRadians(currentR21))+cY);
                    cc.scale(1f,1f);
                    cc.rotate(currentR21);
                    cc.position(dx,dy);
                }else {
                    Matrix m21 = AnimUtils.getInstance().setImgInitState(padWidth, padHeight, layerWidth, layerHeight, matrix, ALIGN_VERTICAL_CENTER, ALIGN_HORIZONTAL_CENTER);
                    m21.preRotate(currentR21, padWidth * 0.5f, padHeight);
                    matrix = m21;
                }
                break;
            case "centre_to_left":
                //@"中心左转"
                float[] cR20 = animCoordinates.get(20);
                float currentR20 = getInterpolatedValueForCoordinate(progress, cR20, animCurves.get(20));
                if(cc!=null){
                    float oX=padWidth*0.5f;
                    float oY=padHeight*0.5f;
                    float cX=0;
                    float cY=padHeight;
                    float dx= (float) (((oX-cX)*Math.cos(Math.toRadians(currentR20)))-(oY-cY)*Math.sin(Math.toRadians(currentR20))+cX);
                    float dy=(float) (((oX-cX)*Math.sin(Math.toRadians(currentR20)))+(oY-cY)*Math.cos(Math.toRadians(currentR20))+cY);
                    cc.scale(1f,1f);
                    cc.rotate(currentR20);
                    cc.position(dx,dy);
                }else {
                    Matrix m20 = AnimUtils.getInstance().setImgInitState(padWidth, padHeight, layerWidth, layerHeight, matrix, ALIGN_VERTICAL_CENTER, ALIGN_HORIZONTAL_CENTER);
                    m20.preRotate(currentR20, 0, padHeight);
                    matrix = m20;
                }

                break;
            case "top_to_rotate":
                //@"上弹旋转"
                float[] cR15 = animCoordinates.get(0x151);
                float currentR15 = getInterpolatedValueForCoordinate(progress, cR15, animCurves.get(0x151));
                float[] cY15 = animCoordinates.get(0x153);
                float currentY15 = getInterpolatedValueForCoordinate(progress, cY15, animCurves.get(0x153));
                if(cc!=null){
                    cc.scale(1f,1f);
                    cc.rotate(currentR15);
                    cc.position(padWidth*0.5f,currentY15*yRatio);
                }else {
                    Matrix m15 = matrix;
                    m15.preRotate(currentR15, padWidth * 0.5f, padHeight * 0.5f);
                    m15.postScale(defaultScale, defaultScale);
                    m15.postTranslate(padWidth * 0.5f - layerWidth * 0.5f * defaultScale, currentY15 * yRatio - layerHeight * 0.5f * defaultScale);
                    matrix = m15;
                }
                break;
            case "right_to_rotate":
                //@"右移旋转"
                float[] cR22 = animCoordinates.get(0x221);
                float currentR22 = getInterpolatedValueForCoordinate(progress, cR22, animCurves.get(0x221));
                float[] cX22 = animCoordinates.get(0x222);
                float currentX22 = getInterpolatedValueForCoordinate(progress, cX22, animCurves.get(0x222));
                if(cc!=null){
                    cc.scale(1f,1f);
                    cc.rotate(currentR22);
                    cc.position(currentX22,padHeight*0.5f);
                }else {
                    Matrix m22 = matrix;
                    m22.preRotate(currentR22, padWidth * 0.5f, padHeight * 0.5f);
                    m22.postScale(defaultScale, defaultScale);
                    m22.postTranslate(currentX22 * xRatio - layerWidth * 0.5f * defaultScale, padHeight * 0.5f - (layerHeight * 0.5f) * defaultScale);
                    matrix = m22;
                }

                break;
            case "normal_to_shrink":
                //@"普通缩小"
                float[] cZ14 = animCoordinates.get(14);
                float currentZ14 = getInterpolatedValueForCoordinate(progress, cZ14, animCurves.get(14));
                if(cc!=null){
                    cc.scale(currentZ14,currentZ14);
                    cc.rotate(0);
                    cc.position(padWidth*0.5f,padHeight*0.5f);
                }else {
                    Matrix m14 = AnimUtils.getInstance().setImgInitState(padWidth, padHeight, layerWidth, layerHeight, matrix, ALIGN_VERTICAL_CENTER, ALIGN_HORIZONTAL_CENTER);
                    m14.postScale(currentZ14, currentZ14, padWidth * 0.5f, padHeight * 0.5f);
                    matrix = m14;
                }
                break;
            default:  //没有动画的时候
                matrix = AnimUtils.getInstance().setImgInitState(padWidth, padHeight, layerWidth, layerHeight, matrix, ALIGN_VERTICAL_CENTER, ALIGN_HORIZONTAL_CENTER);
                break;

        }
        return matrix;

    }


    private float getInterpolatedValueForCoordinate
            (float totalProgress, float[] coordinates, ArrayList<float[]> ctrlPoints) {
        if (totalProgress < 0) {
            return 0f;
        }
        //每个区间占多少进度
        float pgsPerInterval = 1f / (coordinates.length - 1);
        //当前是第几个区间
        int curInterval = (int) (totalProgress / pgsPerInterval);
        //插值器在该区间运动到了哪个进度
        float devProgress = (totalProgress - pgsPerInterval * curInterval) * ctrlPoints.size();
        //当前的曲线
        int index = 0;
        if (ctrlPoints.size() == 1) {
            devProgress *= (coordinates.length - 1);
        } else {
            if (curInterval < coordinates.length - 1) {
                index = curInterval;
            } else {
                index = ctrlPoints.size() - 1;
            }
        }
        PathInterpolator interpolator = new PathInterpolator(ctrlPoints.get(index)[0],
                ctrlPoints.get(index)[1], ctrlPoints.get(index)[2], ctrlPoints.get(index)[3]);
        //当前的起点
        float start = coordinates[curInterval];
        //当前的总移动距离
        float distance = 0;
        if (curInterval + 1 < coordinates.length) {
            distance = coordinates[curInterval + 1] - start;
        }
        return start + distance * interpolator.getInterpolation(devProgress);
    }











    /**
     * description ：供给自定义视频数据
     * date: ：2019/12/3 16:52
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    public interface AnimCoordinatesCallback {
        void rotate(float currentR);
        void scale(float currentScaleX, float currentScaleY);
        void position(float currentX, float currentY);
    }

}

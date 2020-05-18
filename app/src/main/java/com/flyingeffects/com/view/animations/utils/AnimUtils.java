package com.flyingeffects.com.view.animations.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.v4.util.SparseArrayCompat;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;


import com.flyingeffects.com.view.animations.Ease.Ease;
import com.flyingeffects.com.view.animations.Ease.EasingInterpolator;
import com.flyingeffects.com.view.animations.Flubber.interpolators.OscillationInterpolator;

import java.util.ArrayList;


public class AnimUtils {

    public static final int ALIGN_VERTICAL_TOP = 0x100;
    public static final int ALIGN_VERTICAL_CENTER = 0x101;
    public static final int ALIGN_VERTICAL_BOTTOM = 0x102;
    public static final int ALIGN_HORIZONTAL_LEFT = 0x103;
    public static final int ALIGN_HORIZONTAL_CENTER = 0x104;
    public static final int ALIGN_HORIZONTAL_RIGHT = 0x105;
    private static AnimUtils instance;

    public static AnimUtils getInstance() {
        if (instance == null) {
            instance = new AnimUtils();
        }
        return instance;
    }

    /**
     * @param progress     当前进度
     * @param distance     总移动距离
     * @param interpolator 插值器
     * @return
     */
    public float move(float progress, int distance, Interpolator interpolator) {
        if (interpolator == null) {
            interpolator = new BounceInterpolator(0.4f);
        }
        if (progress > 1f) {
            progress = 1f;
        }
        float devDistance = distance * interpolator.getInterpolation(progress);
        return devDistance;
    }

    /**
     * @param progress     当前进度
     * @param endScale     结束的倍数
     * @param interpolator 插值器
     * @return
     */
    public float zoom(float progress, float endScale, Interpolator interpolator, boolean isReverse) {
        if (interpolator == null) {
            interpolator = new EasingInterpolator(Ease.ELASTIC_OUT);
        }

        if (progress > 1f) {
            progress = 1f;
        }
        float currentScale = endScale * interpolator.getInterpolation(progress);

        if (isReverse) {
            return endScale * (3f - 2f*interpolator.getInterpolation(progress));
        }
        return currentScale;
    }

    /**
     * @param progress
     * @param maxDegree    抖动最大度数
     * @param interpolator
     * @return
     */
    public float shake(float progress, int maxDegree, Interpolator interpolator) {
        if (interpolator == null) {
            interpolator = new OscillationInterpolator();
            ((OscillationInterpolator) interpolator).setOnlyPositive(false);
        }
        if (progress > 1f) {
            progress = 1f;
        }
        float devDegree = maxDegree * interpolator.getInterpolation(progress);
        return devDegree;
    }

    public float rotate(float progress, int startDegree, int endDegree, Interpolator interpolator) {
        if (interpolator == null) {
            interpolator = new BounceInterpolator(0.3f);
        }
        if (progress > 1f) {
            progress = 1f;
        }
        float devDegree = endDegree - startDegree;
        float currentDegree = startDegree + (devDegree * interpolator.getInterpolation(progress));
        return currentDegree;
    }



    public static Bitmap scaleBitmap(Bitmap bitmap, int sWide) {
        float whRatio = 1f * bitmap.getWidth() / bitmap.getHeight();
        float devHeight = 1f * sWide / whRatio;
        return Bitmap.createScaledBitmap(bitmap, sWide, Math.round(devHeight), false);
    }
    /**
     * @param sWide   容器宽
     * @param sHeight 容器高
     * @param imgW    图片宽度
     * @param imgH    图片高度
     * @param matrix  传进来的空Matrix
     * @param vPos    垂直方向位置 0：top 1：center 2：bottom
     * @param hPos    水平方向位置 0：left 1：center 2：right
     * @return
     */
    @Deprecated
    public Matrix setImgInitState(int sWide, int sHeight, int imgW, int imgH, Matrix matrix, int vPos, int hPos) {


        matrix = setScale(sWide, sHeight, imgW, imgH, matrix);

        switch (vPos) {
            case ALIGN_VERTICAL_TOP:
                matrix.postTranslate(0, -(imgH*nowScale));
                break;
            case ALIGN_VERTICAL_CENTER:
                matrix.postTranslate(0, sHeight * 0.5f - (imgH * 0.5f*nowScale));
                break;
            case ALIGN_VERTICAL_BOTTOM:
                matrix.postTranslate(0, sHeight);
                break;
            default:
                matrix.postTranslate(0,vPos);
                break;
        }
        switch (hPos) {
            case ALIGN_HORIZONTAL_LEFT:
                matrix.postTranslate(-(imgW*nowScale), 0);
                break;
            case ALIGN_HORIZONTAL_CENTER:
                matrix.postTranslate(sWide * 0.5f - (imgW * 0.5f*nowScale), 0);
                break;
            case ALIGN_HORIZONTAL_RIGHT:
                matrix.postTranslate(sWide, 0);
                break;
            default:
                matrix.postTranslate(hPos,0);
                break;
        }
        return matrix;
    }

    @Deprecated
    public void initAnimParams(SparseArrayCompat<float[]> animCoordinates,SparseArrayCompat<ArrayList<float[]>> animCurves){
        //1 Rotate 2 X 3 Y 4 Z 正常数字代表改动画只有一组参数
        animCoordinates.put(2, new float[]{-78, 668, 630, 643, 640});
        animCoordinates.put(3, new float[]{1358, 602, 643, 637, 640});
        animCoordinates.put(4, new float[]{1264, 348, 365, 354, 360});
        animCoordinates.put(5, new float[]{-624, 378, 352, 364, 360});
        animCoordinates.put(6, new float[]{-126f, 12f, -7f, 3f, 0f});
        animCoordinates.put(0x121, new float[]{0, -16, 6, -6, 0});
        animCoordinates.put(0x122, new float[]{686, 214, 404, 326, 360});
        animCoordinates.put(0x123, new float[]{350, 778, 602, 668, 640});
        animCoordinates.put(13, new float[]{1f, 2f});
        animCoordinates.put(14, new float[]{1f, 0.5f});
        animCoordinates.put(0x151, new float[]{0, -16, 6, -6, 0});
        animCoordinates.put(0x153, new float[]{996, 546, 666, 612, 640});
        animCoordinates.put(0x162, new float[]{256f, 303.7f, 374f, 432f, 379.7f, 297.9f, 360f});
        animCoordinates.put(0x163, new float[]{640f, 585.2f, 584f, 651.8f, 709.3f, 701.2f, 640f});
        animCoordinates.put(17, new float[]{2.14f, 0.95f, 1.11f, 1.00f});
        animCoordinates.put(18, new float[]{0.8f, 1.8f, 0.5f});
        animCoordinates.put(0x191, new float[]{0, -16, 6, -6, 0});
        animCoordinates.put(0x193, new float[]{356, 674, 522, 612, 640});
        animCoordinates.put(20, new float[]{-49, 12, -7, 3, 0});
        animCoordinates.put(21, new float[]{-37, 12, -6, 3, 0});
        animCoordinates.put(0x221, new float[]{0, -15, 11, -5, 1, 0});
        animCoordinates.put(0x222, new float[]{-624, 378, 352, 364, 360});
        animCoordinates.put(0x231, new float[]{0, -15, 11, -5, 1, 0});
        animCoordinates.put(0x232, new float[]{1324, 378, 352, 364, 360});
        animCoordinates.put(24, new float[]{0.52f, 1f, 0.741f, 0.9f, 0.8f, 0.88f});
        animCoordinates.put(25, new float[]{1.8f, 0.5f, 0.89f, 0.567f, 0.614f, 0.567f});
        animCoordinates.put(26, new float[]{-78, 750, 586, 667, 646, 656});
        animCoordinates.put(27, new float[]{-64, 17, -22, 9, 0});

        ArrayList<float[]> ctPY2 = new ArrayList<>();
        ctPY2.add(new float[]{0.78f, 0.09f, 0.80f, 0.18f});
        ctPY2.add(new float[]{0.37f, 0.16f, 0.49f, 0.99f});
        ctPY2.add(new float[]{0.51f, 1.00f, 0.65f, 0.00f});
        ctPY2.add(new float[]{0.28f, 0.15f, 0.45f, 0.97f});
        animCurves.put(2, ctPY2);
        ArrayList<float[]> ctPY3 = new ArrayList<>();
        ctPY3.add(new float[]{0.52f, 0.05f, 0.92f, 0.53f});
        ctPY3.add(new float[]{0.37f, 0.14f, 0.59f, 0.47f});
        ctPY3.add(new float[]{0.51f, 0.40f, 0.43f, 0.97f});
        ctPY3.add(new float[]{0.09f, 0.35f, 0.80f, 0.09f});
        animCurves.put(3, ctPY3);
        ArrayList<float[]> ctPX4 = new ArrayList<>();
        ctPX4.add(new float[]{0.33f, 0.00f, 0.69f, 0.20f});
        ctPX4.add(new float[]{0.17f, 0.00f, 0.83f, 1.00f});
        ctPX4.add(new float[]{0.17f, 0.00f, 0.83f, 1.00f});
        ctPX4.add(new float[]{0.17f, 0.00f, 0.83f, 1.00f});
        animCurves.put(4, ctPX4);
        ArrayList<float[]> ctPX5 = new ArrayList<>();
        ctPX5.add(new float[]{0.33f, 0.00f, 0.93f, 0.67f});
        ctPX5.add(new float[]{0.17f, 0.00f, 0.83f, 1.00f});
        ctPX5.add(new float[]{0.17f, 0.00f, 0.83f, 1.00f});
        ctPX5.add(new float[]{0.17f, 0.00f, 0.83f, 1.00f});
        animCurves.put(5, ctPX5);
        ArrayList<float[]> ctPR6 = new ArrayList<>();
        ctPR6.add(new float[]{0.33f, 0.00f, 0.78f, 0.31f});
        ctPR6.add(new float[]{0.17f, 0.00f, 0.83f, 1.00f});
        ctPR6.add(new float[]{0.17f, 0.00f, 0.83f, 1.00f});
        ctPR6.add(new float[]{0.17f, 0.00f, 0.83f, 1.00f});
        animCurves.put(6, ctPR6);
        ArrayList<float[]> ctPR12 = new ArrayList<>();
        ctPR12.add(new float[]{0.10f, 0.60f, 0.67f, 1.00f});
        ctPR12.add(new float[]{0.33f, 0.00f, 0.66f, 0.96f});
        ctPR12.add(new float[]{0.23f, 0.69f, 0.67f, 1.00f});
        ctPR12.add(new float[]{0.33f, 0.00f, 0.77f, -0.03f});
        animCurves.put(0x121, ctPR12);
        ArrayList<float[]> ctPX12 = new ArrayList<>();
        ctPX12.add(new float[]{0.26f, 0.43f, 0.67f, 1.00f});
        ctPX12.add(new float[]{0.33f, 0.00f, 0.67f, 1.00f});
        ctPX12.add(new float[]{0.33f, 0.00f, 0.67f, 1.00f});
        ctPX12.add(new float[]{0.33f, 0.00f, 0.67f, 1.00f});
        animCurves.put(0x122, ctPX12);
        ArrayList<float[]> ctPY12 = new ArrayList<>();
        ctPY12.add(new float[]{0.27f, 0.44f, 0.67f, 1.00f});
        ctPY12.add(new float[]{0.33f, 0.00f, 0.67f, 1.00f});
        ctPY12.add(new float[]{0.33f, 0.00f, 0.67f, 1.00f});
        ctPY12.add(new float[]{0.33f, 0.00f, 0.67f, 1.00f});
        animCurves.put(0x123, ctPY12);
        ArrayList<float[]> ctPZ13 = new ArrayList<>();
        ctPZ13.add(new float[]{0.38f, 0.71f, 0.67f, 1.00f});
        animCurves.put(13, ctPZ13);
        ArrayList<float[]> ctPZ14 = new ArrayList<>();
        ctPZ14.add(new float[]{0.48f, 0.07f, 0.90f, -0.02f});
        animCurves.put(14, ctPZ14);
        ArrayList<float[]> ctPR15 = new ArrayList<>();
        ctPR15.add(new float[]{0.10f, 0.60f, 0.67f, 1.00f});
        ctPR15.add(new float[]{0.33f, 0.00f, 0.66f, 0.97f});
        ctPR15.add(new float[]{0.23f, 0.57f, 0.67f, 1.00f});
        ctPR15.add(new float[]{0.33f, 0.00f, 0.77f, 0.14f});
        animCurves.put(0x151, ctPR15);
        ArrayList<float[]> ctPY15 = new ArrayList<>();
        ctPY15.add(new float[]{0.80f, 0.13f, 0.47f, 1.00f});
        ctPY15.add(new float[]{0.74f, -0.01f, 0.64f, 0.80f});
        ctPY15.add(new float[]{0.29f, -0.36f, 0.59f, 1.01f});
        ctPY15.add(new float[]{0.48f, 0.02f, 0.90f, 1.19f});
        animCurves.put(0x153, ctPY15);
        ArrayList<float[]> ctPX16 = new ArrayList<>();
        ctPX16.add(new float[]{0.17f, 0.17f, 0.83f, 0.83f});
        animCurves.put(0x162, ctPX16);
        ArrayList<float[]> ctPY16 = new ArrayList<>();
        ctPY16.add(new float[]{0.17f, 0.17f, 0.83f, 0.83f});
        animCurves.put(0x163, ctPY16);
        ArrayList<float[]> ctPZ17 = new ArrayList<>();
        ctPZ17.add(new float[]{0.39f, 0.06f, 0.77f, 0.45f});
        ctPZ17.add(new float[]{0.17f, 0.00f, 0.83f, 1.00f});
        ctPZ17.add(new float[]{0.17f, 0.00f, 0.83f, 1.00f});
        animCurves.put(17, ctPZ17);
        ArrayList<float[]> ctPZ118 = new ArrayList<>();
        ctPZ118.add(new float[]{0.40f, 0.82f, 0.65f, 1.00f});
        ctPZ118.add(new float[]{0.47f, 0.01f, 0.90f, 0.85f});
        animCurves.put(18, ctPZ118);
        ArrayList<float[]> ctPR19 = new ArrayList<>();
        ctPR19.add(new float[]{0.10f, 0.60f, 0.67f, 1.00f});
        ctPR19.add(new float[]{0.33f, 0.00f, 0.66f, 0.97f});
        ctPR19.add(new float[]{0.23f, 0.57f, 0.67f, 1.00f});
        ctPR19.add(new float[]{0.33f, 0.00f, 0.77f, 0.14f});
        animCurves.put(0x191, ctPR19);
        ArrayList<float[]> ctPY19 = new ArrayList<>();
        ctPY19.add(new float[]{0.07f, 0.17f, 0.63f, 1.01f});
        ctPY19.add(new float[]{0.53f, 0.02f, 0.50f, 1.00f});
        ctPY19.add(new float[]{0.47f, 0.00f, 0.59f, 1.00f});
        ctPY19.add(new float[]{0.48f, -0.01f, 0.90f, 0.83f});
        animCurves.put(0x193, ctPY19);
        ArrayList<float[]> ctPR20 = new ArrayList<>();
        ctPR20.add(new float[]{0.33f, 0.00f, 0.73f, 0.03f});
        ctPR20.add(new float[]{0.29f, 0.65f, 0.83f, 1.00f});
        ctPR20.add(new float[]{0.44f, 0.70f, 0.83f, 1.00f});
        ctPR20.add(new float[]{0.39f, 1.00f, 0.83f, 1.00f});
        animCurves.put(20, ctPR20);
        ArrayList<float[]> ctPR21 = new ArrayList<>();
        ctPR21.add(new float[]{0.64f, 0.21f, 0.89f, 0.52f});
        ctPR21.add(new float[]{0.36f, 0.64f, 0.60f, 0.87f});
        ctPR21.add(new float[]{0.17f, 0.00f, 0.75f, 0.01f});
        ctPR21.add(new float[]{0.18f, 0.77f, 0.55f, 0.92f});
        animCurves.put(21, ctPR21);
        ArrayList<float[]> ctPR22 = new ArrayList<>();
        ctPR22.add(new float[]{0.33f, 0.00f, 0.83f, 1.00f});
        ctPR22.add(new float[]{0.17f, 0.00f, 0.83f, 1.00f});
        ctPR22.add(new float[]{0.17f, 0.00f, 0.83f, 1.00f});
        ctPR22.add(new float[]{0.17f, 0.00f, 0.83f, 1.00f});
        ctPR22.add(new float[]{0.17f, 0.00f, 0.83f, 1.00f});
        animCurves.put(0x221, ctPR22);
        ArrayList<float[]> ctPX22 = new ArrayList<>();
        ctPX22.add(new float[]{0.33f, 0.00f, 0.83f, 1.00f});
        ctPX22.add(new float[]{0.17f, 0.00f, 0.83f, 1.00f});
        ctPX22.add(new float[]{0.17f, 0.00f, 0.83f, 1.00f});
        ctPX22.add(new float[]{0.17f, 0.00f, 0.83f, 1.00f});
        ctPX22.add(new float[]{0.17f, 0.00f, 0.83f, 1.00f});
        animCurves.put(0x222, ctPX22);
        ArrayList<float[]> ctPR23 = new ArrayList<>();
        ctPR23.add(new float[]{0.33f, 0.00f, 0.83f, 1.00f});
        ctPR23.add(new float[]{0.17f, 0.00f, 0.83f, 1.00f});
        ctPR23.add(new float[]{0.17f, 0.00f, 0.83f, 1.00f});
        ctPR23.add(new float[]{0.17f, 0.00f, 0.83f, 1.00f});
        ctPR23.add(new float[]{0.17f, 0.00f, 0.83f, 1.00f});
        animCurves.put(0x231, ctPR23);
        ArrayList<float[]> ctPX23 = new ArrayList<>();
        ctPX23.add(new float[]{0.33f, 0.00f, 0.88f, 0.85f});
        ctPX23.add(new float[]{0.17f, 0.00f, 0.83f, 1.00f});
        ctPX23.add(new float[]{0.17f, 0.00f, 0.83f, 1.00f});
        ctPX23.add(new float[]{0.17f, 0.00f, 0.83f, 1.00f});
        animCurves.put(0x232, ctPX23);
        ArrayList<float[]> ctPZ24 = new ArrayList<>();
        ctPZ24.add(new float[]{0.33f, 0, 00f, 0.67f, 1.00f});
        animCurves.put(24, ctPZ24);
        ArrayList<float[]> ctPZ25 = new ArrayList<>();
        ctPZ25.add(new float[]{0.47f, 0.01f, 0.9f, 0.85f});
        ctPZ25.add(new float[]{0.17f, 0.00f, 0.9f, 0.85f});
        ctPZ25.add(new float[]{0.17f, 0.00f, 0.9f, 0.85f});
        ctPZ25.add(new float[]{0.17f, 0.00f, 0.9f, 0.85f});
        ctPZ25.add(new float[]{0.17f, 0.00f, 0.9f, 0.85f});
        animCurves.put(25, ctPZ25);
        ArrayList<float[]> ctPY26 = new ArrayList<>();
        ctPY26.add(new float[]{0.33f, 0.00f, 0.67f, 1.00f});
        animCurves.put(26, ctPY26);
        ArrayList<float[]> ctPR27 = new ArrayList<>();
        ctPR27.add(new float[]{0.33f, 0.00f, 0.67f, 1.00f});
        animCurves.put(27, ctPR27);
    }

    /**
     * description ：图片适应布局
     * date: ：2019/8/15 19:40
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    private float nowScale;
    public Matrix setScale(int sWide, int sHeight, int imgW, int imgH, Matrix matrix) {
        float scale = 1.0f;
        //    如果图片的宽或者高大于屏幕，则缩放至屏幕的宽或者高
        if (imgW > sWide && imgH <= sHeight) {
            scale = sWide * 1.0f / imgW;
        }
        if (imgH > sHeight && imgH <= sWide) {
            scale = sHeight * 1.0f / imgH;
        }
        // 如果宽和高都大于屏幕，则让其按按比例适应屏幕大小
        if (imgW > sWide && imgH > sHeight) {
            scale = sWide * 1.0f / imgW;
        }

        // 如果宽和高都小于屏幕，则让其按按比例适应屏幕大小
        if (imgW < sWide && imgH < sHeight) {
            scale = sWide * 1.0f / imgW;
        }

        nowScale = scale;
        LogUtil.d("setScale","nowScale="+nowScale);
        matrix.postScale(scale, scale);
        return matrix;
    }
    public static float getBitmapScale(int sWide, int sHeight, int imgW, int imgH){
        float scale = 1.0f;
        //    如果图片的宽或者高大于屏幕，则缩放至屏幕的宽或者高
        if (imgW > sWide && imgH <= sHeight) {
            scale = sWide * 1.0f / imgW;
        }
        if (imgH > sHeight && imgH <= sWide) {
            scale = sHeight * 1.0f / imgH;
        }
        // 如果宽和高都大于屏幕，则让其按按比例适应屏幕大小
        if (imgW > sWide && imgH > sHeight) {
            scale = sWide * 1.0f / imgW;
        }

        // 如果宽和高都小于屏幕，则让其按按比例适应屏幕大小
        if (imgW < sWide && imgH < sHeight) {
            scale = sWide * 1.0f / imgW;
        }
        return scale;
    }
}

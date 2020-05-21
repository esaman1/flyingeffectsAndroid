package com.flyingeffects.com.view.animations.CustomMove;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.flyingeffects.com.enity.AllStickerData;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.view.animations.CancelAnimatorListenerAdapter;

/**
 * description ：从右向左运动，从小到大
 * creation date: 2020/5/21
 * user : zhangtongju
 */
public class RightToLeft {
    private int padW;
    private int padH;
    private int layerW;
    private int layerH;
    private AllStickerData stickerView;
    private float centerX;
    private float centerY;
    private int durationTime=3000;

    public RightToLeft(int padW, int padH, int layerW, int layerH,float centerX,float centerY, AllStickerData stickerView) {
        this.padW = padW;
        this.padH = padH;
        this.layerW = layerW;
        this.layerH = layerH;
        this.centerX=centerX;
        this.centerY=centerY;
        this.stickerView = stickerView;
    }


    public void startAnim(View view,AnimateCallBack callback) {
        float leftX = centerX;
        LogUtil.d("OOM", "距离右边值为" + leftX);
        // 平移 translation
        final AnimatorSet translationAnimatorSet = new AnimatorSet();
        ObjectAnimator animatorTranX = ObjectAnimator.ofFloat(view, "translationX", 0, -leftX);
        animatorTranX.setDuration(durationTime);
        DecelerateInterpolator linearInterpolator=new DecelerateInterpolator();


         float xx=  linearInterpolator.getInterpolation(0.5f);
         LogUtil.d("OOM","2.5f 的进度为"+xx);
        animatorTranX.setInterpolator(linearInterpolator);
        animatorTranX.addUpdateListener(valueAnimator -> {
            float ob = (float) valueAnimator.getAnimatedValue();
            LogUtil.d("OOM", "当前水平滑动的值为" + ob);
            if(callback!=null){
                callback.animatorTranXCallBack(ob);
            }
        });


        ObjectAnimator animatorScaleX = ObjectAnimator.ofFloat(view, "scaleX", 1, 2);
        animatorScaleX.setDuration(durationTime);
        animatorScaleX.addUpdateListener(valueAnimator -> {
            float ob = (float) valueAnimator.getAnimatedValue();
            LogUtil.d("OOM","x放大为"+ob);
            if(callback!=null){
                callback.animatorScaleXCallback(ob);
            }
        });

        ObjectAnimator animatorScaleY = ObjectAnimator.ofFloat(view, "scaleY", 1,  2);
        animatorScaleY.setDuration(durationTime);
        animatorScaleY.addUpdateListener(valueAnimator -> {
            float ob = (float) valueAnimator.getAnimatedValue();
            LogUtil.d("OOM","y放大为"+ob);
            if(callback!=null){
                callback.animatorScaleYCallback(ob);
            }
        });


        translationAnimatorSet.playTogether(
                animatorScaleX,animatorScaleY,animatorTranX
        );
        translationAnimatorSet.addListener(new CancelAnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (isCanceled()) {
                    return;
                }
                if (view == null) {
                    return;
                }
                view.setTranslationX(0);
                view.setAlpha(0.0f);
                view.setScaleX(1.0f);
                view.setScaleY(1.0f);
                view.setAlpha(1.0f);
                if(callback!=null){
                    callback.closeAnimate();
                }
            }
        });




        translationAnimatorSet.start();
    }







}

package com.flyingeffects.com.view.animations.CustomMove;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;

import com.flyingeffects.com.enity.AllStickerData;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.view.animations.CancelAnimatorListenerAdapter;
import com.flyingeffects.com.view.lansongCommendView.StickerView;

/**
 * description ：从右向左运动，且大小在变
 * creation date: 2020/5/21
 * user : zhangtongju
 */
public class RightToLeft {
    private int padW;
    private int padH;
    private int layerW;
    private int layerH;
    private AllStickerData stickerView;


    public RightToLeft(int padW, int padH, int layerW, int layerH, AllStickerData stickerView) {
        this.padW = padW;
        this.padH = padH;
        this.layerW = layerW;
        this.layerH = layerH;
        this.stickerView = stickerView;
    }


    public void startAnim(View view) {
        float FLeftX = stickerView.getTranslationX();
        float leftX = layerW * FLeftX;
        LogUtil.d("OOM", "距离右边值为" + leftX);
        // 平移 translation
        final AnimatorSet translationAnimatorSet = new AnimatorSet();
        ObjectAnimator animator22 = ObjectAnimator.ofFloat(view, "translationX", 0, -leftX);
        animator22.setDuration(2000);
        animator22.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float ob = (float) valueAnimator.getAnimatedValue();
                LogUtil.d("OOM", "当前水平滑动的值为" + ob);

            }
        });
        translationAnimatorSet.playTogether(
                animator22
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
//                startMatchTipsAnimation();
            }
        });
        translationAnimatorSet.start();


    }


}

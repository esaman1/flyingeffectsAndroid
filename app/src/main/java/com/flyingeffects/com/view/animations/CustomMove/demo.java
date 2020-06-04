package com.flyingeffects.com.view.animations.CustomMove;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;

import com.flyingeffects.com.utils.LogUtil;

public class demo {

    public void startAnim(int parentsW, View view) {

        // 平移 translation
        final AnimatorSet translationAnimatorSet = new AnimatorSet();
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", 20, 100);
        animator.setDuration(2000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float  ob = (float) valueAnimator.getAnimatedValue();
                LogUtil.d("OOM","当前水平滑动的值为"+ob);

            }
        });


        ObjectAnimator animator2 = ObjectAnimator.ofFloat(view, "translationY", 20, 100);
        animator2.setDuration(2000);
        animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Object ob = valueAnimator.getAnimatedValue();
                LogUtil.d("OOM","当前垂直滑动的值为"+ob);
            }
        });
        translationAnimatorSet.playTogether(
                animator,
                animator2
        );
        translationAnimatorSet.start();


    }
}

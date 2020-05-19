package com.flyingeffects.com.view.animations.Flubber.animation.providers;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;

import com.mobile.kadian.utils.Flubber.AnimationBody;
import com.mobile.kadian.utils.Flubber.Flubber;
import com.mobile.kadian.utils.Flubber.utils.DimensionUtils;


public class SqueezeLeft extends BaseProvider {

    public SqueezeLeft() {
        super(Flubber.Curve.SPRING);
    }

    @Override
    public Animator getAnimationFor(AnimationBody animationBody, View view) {

        final float startX = DimensionUtils.dp2px(800);
        final float endX = 0f;

        final PropertyValuesHolder translationPVH =
                PropertyValuesHolder.ofFloat(View.TRANSLATION_X, startX, endX);


        final float startScaleY = 3 * animationBody.getForce();
        final float endScaleY = 1f;

        final PropertyValuesHolder scalePVH =
                PropertyValuesHolder.ofFloat(View.SCALE_X, startScaleY, endScaleY);


        final ObjectAnimator animation =
                ObjectAnimator.ofPropertyValuesHolder(view, translationPVH, scalePVH);

        return animation;
    }
}
package com.flyingeffects.com.view.animations.Flubber.animation.providers;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;

import com.mobile.kadian.utils.Flubber.AnimationBody;
import com.mobile.kadian.utils.Flubber.Flubber;
import com.mobile.kadian.utils.Flubber.utils.DimensionUtils;

public class SlideRight extends BaseProvider {

    public SlideRight() {
        super(Flubber.Curve.SPRING);
    }

    @Override
    public Animator getAnimationFor(AnimationBody animationBody, View view) {

        final float startY = DimensionUtils.dp2px(-800);
        final float endY = 0f;

        final PropertyValuesHolder translationPVH =
                PropertyValuesHolder.ofFloat(View.TRANSLATION_X, startY, endY);

        final ObjectAnimator animation =
                ObjectAnimator.ofPropertyValuesHolder(view, translationPVH);

        return animation;
    }
}

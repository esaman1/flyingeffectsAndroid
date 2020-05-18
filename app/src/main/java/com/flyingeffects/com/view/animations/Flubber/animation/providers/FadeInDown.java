package com.flyingeffects.com.view.animations.Flubber.animation.providers;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

import com.mobile.kadian.utils.Flubber.AnimationBody;
import com.mobile.kadian.utils.Flubber.utils.DimensionUtils;


public class FadeInDown extends BaseFadeIn {

    @Override
    protected Animator getTranslation(AnimationBody animationBody, View view) {

        final float startY = -DimensionUtils.dp2px(300) * animationBody.getForce();
        final float endY = 0;

        final ObjectAnimator translateAnimation =
                ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, startY, endY);

        return translateAnimation;
    }
}

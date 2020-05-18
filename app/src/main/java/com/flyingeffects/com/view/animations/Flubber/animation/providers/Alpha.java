package com.flyingeffects.com.view.animations.Flubber.animation.providers;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

import com.flyingeffects.com.view.animations.Flubber.AnimationBody;


public class Alpha extends BaseProvider {

    @Override
    public Animator getAnimationFor(AnimationBody animationBody, View view) {

        final ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f);

        return alphaAnimation;
    }
}

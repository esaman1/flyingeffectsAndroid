package com.flyingeffects.com.view.animations.Flubber.animation.providers;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

import com.mobile.kadian.utils.Flubber.AnimationBody;


public class ScaleX extends BaseProvider {
    @Override
    public Animator getAnimationFor(AnimationBody animationBody, View view) {
        return ObjectAnimator.ofFloat(
                view,
                View.SCALE_X,
                animationBody.getStartScaleX(),
                animationBody.getEndScaleX());
    }
}

package com.flyingeffects.com.view.animations.Flubber.animation.providers;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

import com.flyingeffects.com.view.animations.Flubber.AnimationBody;


public class TranslationY extends BaseProvider {
    @Override
    public Animator getAnimationFor(AnimationBody animationBody, View view) {
        return ObjectAnimator.ofFloat(
                view,
                View.TRANSLATION_Y,
                animationBody.getStartY(),
                animationBody.getEndY());
    }
}
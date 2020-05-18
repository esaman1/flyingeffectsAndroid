package com.flyingeffects.com.view.animations.Flubber.animation.providers;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

import com.flyingeffects.com.view.animations.Flubber.AnimationBody;


public class FadeOut extends BaseProvider {

    @Override
    public Animator getAnimationFor(AnimationBody animationBody, View view) {

        final ObjectAnimator animation = ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f);

        return animation;
    }
}

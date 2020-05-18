package com.flyingeffects.com.view.animations.Flubber.animation.providers;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;

import com.flyingeffects.com.view.animations.Flubber.AnimationBody;


public class FadeOutIn extends BaseProvider {

    @Override
    public Animator getAnimationFor(AnimationBody animationBody, View view) {

        final ObjectAnimator animation = ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f);

        return animation;
    }

    @Override
    protected void setupRepeating(Animator animation, AnimationBody animationBody) {
        ((ObjectAnimator) animation).setRepeatCount(animationBody.getRepeatCount() * 2 + 1);
        ((ObjectAnimator) animation).setRepeatMode(ValueAnimator.REVERSE);
    }
}

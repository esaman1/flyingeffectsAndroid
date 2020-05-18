package com.flyingeffects.com.view.animations.Flubber.animation.providers;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.flyingeffects.com.view.animations.Flubber.AnimationBody;
import com.flyingeffects.com.view.animations.Flubber.Flubber;


public abstract class BaseFadeIn extends BaseProvider {

    public BaseFadeIn() {
        super(Flubber.Curve.SPRING);
    }

    @Override
    public Animator createAnimationFor(AnimationBody animationBody, View view) {
        initInterpolatorFor(animationBody);
        return getAnimationFor(animationBody, view);
    }

    @Override
    public Animator getAnimationFor(AnimationBody animationBody, View view) {

        final AnimatorSet animatorSet = new AnimatorSet();

        final Animator alphaAnimation = getAlpha(animationBody, view);
        final Animator translateAnimation = getTranslation(animationBody, view);

        translateAnimation.setInterpolator(getInterpolatorProvider().createInterpolatorFor(animationBody));

        animatorSet.play(translateAnimation)
                .with(alphaAnimation);

        return animatorSet;
    }

    protected abstract Animator getTranslation(AnimationBody animationBody, View view);

    protected Animator getAlpha(AnimationBody animationBody, View view) {

        final ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f);

        alphaAnimation.setInterpolator(new LinearInterpolator());

        return alphaAnimation;
    }
}

package com.flyingeffects.com.view.animations.Flubber.interpolators.providers;

import android.view.animation.Interpolator;

import com.flyingeffects.com.view.animations.Flubber.AnimationBody;
import com.flyingeffects.com.view.animations.Flubber.Flubber;
import com.flyingeffects.com.view.animations.Flubber.interpolators.SpringInterpolator;


public class Spring implements Flubber.InterpolatorProvider {
    @Override
    public Interpolator createInterpolatorFor(AnimationBody animationBody) {
        final float damping = animationBody.getDamping();
        final float velocity = animationBody.getVelocity();

        return new SpringInterpolator(damping, velocity);
    }
}

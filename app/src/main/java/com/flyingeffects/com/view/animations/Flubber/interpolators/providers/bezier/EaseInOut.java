package com.flyingeffects.com.view.animations.Flubber.interpolators.providers.bezier;

import android.support.v4.view.animation.PathInterpolatorCompat;
import android.view.animation.Interpolator;

import com.flyingeffects.com.view.animations.Flubber.AnimationBody;
import com.flyingeffects.com.view.animations.Flubber.Flubber;


public class EaseInOut implements Flubber.InterpolatorProvider {
    @Override
    public Interpolator createInterpolatorFor(AnimationBody animationBody) {
        return PathInterpolatorCompat.create(0.42f, 0.0f, 0.58f, 1.0f);
    }
}
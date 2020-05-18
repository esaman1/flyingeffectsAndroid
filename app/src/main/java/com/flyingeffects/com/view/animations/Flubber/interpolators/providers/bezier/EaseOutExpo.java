package com.flyingeffects.com.view.animations.Flubber.interpolators.providers.bezier;

import android.support.v4.view.animation.PathInterpolatorCompat;
import android.view.animation.Interpolator;

import com.flyingeffects.com.view.animations.Flubber.AnimationBody;
import com.flyingeffects.com.view.animations.Flubber.Flubber;

public class EaseOutExpo implements Flubber.InterpolatorProvider {
    @Override
    public Interpolator createInterpolatorFor(AnimationBody animationBody) {
        return PathInterpolatorCompat.create(0.19f, 1f, 0.22f, 1f);
    }
}

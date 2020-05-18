package com.flyingeffects.com.view.animations.Flubber.interpolators.providers.bezier;

import android.support.v4.view.animation.PathInterpolatorCompat;
import android.view.animation.Interpolator;

import com.flyingeffects.com.view.animations.Flubber.AnimationBody;
import com.flyingeffects.com.view.animations.Flubber.Flubber;


public class EaseOutQuad implements Flubber.InterpolatorProvider {
    @Override
    public Interpolator createInterpolatorFor(AnimationBody animationBody) {
        return PathInterpolatorCompat.create(0.25f, 0.46f, 0.45f, 0.94f);
    }
}

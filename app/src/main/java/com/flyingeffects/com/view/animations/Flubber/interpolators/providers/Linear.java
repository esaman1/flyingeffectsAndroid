package com.flyingeffects.com.view.animations.Flubber.interpolators.providers;

import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.flyingeffects.com.view.animations.Flubber.AnimationBody;
import com.flyingeffects.com.view.animations.Flubber.Flubber;

public class Linear implements Flubber.InterpolatorProvider {
    @Override
    public Interpolator createInterpolatorFor(AnimationBody animationBody) {
        return new LinearInterpolator();
    }
}

package com.flyingeffects.com.view.animations.Flubber.interpolators.providers.bezier;


import android.support.v4.view.animation.PathInterpolatorCompat;
import android.view.animation.Interpolator;

import com.flyingeffects.com.view.animations.Flubber.AnimationBody;
import com.flyingeffects.com.view.animations.Flubber.Flubber;


public class BzrSpring implements Flubber.InterpolatorProvider {
    @Override
    public Interpolator createInterpolatorFor(AnimationBody animationBody) {
        final float force = animationBody.getForce();
        return PathInterpolatorCompat.create(0.5f, 1.1f + force / 3, 1f, 1f);
    }
}

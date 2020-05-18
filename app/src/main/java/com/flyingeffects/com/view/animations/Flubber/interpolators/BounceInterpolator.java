package com.flyingeffects.com.view.animations.Flubber.interpolators;

import android.view.animation.Interpolator;

public class BounceInterpolator implements Interpolator {
    //Factor值越小，来回弹的次数越多
    private float factor;

    public BounceInterpolator(float factor) {
        this.factor = factor;
    }

    @Override
    public float getInterpolation(float input) {
        //factor = 0.4
//        pow(2, -10 * x) * sin((x - factor / 4) * (2 * PI) / factor) + 1

        return (float) (Math.pow(2, -10 * input) * Math.sin((input - factor / 4) * (2 * Math.PI) / factor) + 1);
    }
}

package com.flyingeffects.com.view.animations.Flubber.interpolators;

import android.view.animation.Interpolator;

public class OscillationInterpolator implements Interpolator {
    public void setOnlyPositive(boolean onlyPositive) {
        this.onlyPositive = onlyPositive;
    }

    private boolean onlyPositive=true;
    @Override
    public float getInterpolation(float input) {
        float output = (float) (Math.exp(-3 * input) * Math.sin(10 * Math.PI * input));
        if (!onlyPositive){
            return output;
        }
        return Math.abs(output);
    }

}

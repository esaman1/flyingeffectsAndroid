package com.flyingeffects.com.view.animations.Flubber.animation.providers;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;

import com.mobile.kadian.utils.Flubber.AnimationBody;
import com.mobile.kadian.utils.Flubber.Flubber;
import com.mobile.kadian.utils.Flubber.utils.KeyFrameUtil;


public class Morph extends BaseProvider {

    @Override
    public Animator getAnimationFor(AnimationBody animationBody, View view) {
        final float force = animationBody.getForce();

        float[] valuesX = {1f, 1.3f * force, 0.7f, 1.3f * force, 1f, 1f};
        float[] valuesY = {1f, 0.7f, 1.3f * force, 0.7f, 1f, 1f};

        final PropertyValuesHolder scaleXPVH =
                ofKeyframe(View.SCALE_X, KeyFrameUtil.getKeyFrames(Flubber.FRACTIONS, valuesX));

        final PropertyValuesHolder scaleYPVH =
                ofKeyframe(View.SCALE_Y, KeyFrameUtil.getKeyFrames(Flubber.FRACTIONS, valuesY));

        final ObjectAnimator animation =
                ObjectAnimator.ofPropertyValuesHolder(view, scaleXPVH, scaleYPVH);

        return animation;
    }
}

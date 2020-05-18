package com.flyingeffects.com.view.animations.Flubber.animation.providers;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;

import com.mobile.kadian.utils.Flubber.AnimationBody;
import com.mobile.kadian.utils.Flubber.Flubber;
import com.mobile.kadian.utils.Flubber.utils.KeyFrameUtil;


public class Swing extends BaseProvider {

    @Override
    public Animator getAnimationFor(AnimationBody animationBody, View view) {
        final float force = animationBody.getForce();

        float[] values = {
                (float) Math.toDegrees(0),
                (float) Math.toDegrees(0.3f * force),
                (float) Math.toDegrees(-0.3f * force),
                (float) Math.toDegrees(0.3f * force),
                (float) Math.toDegrees(0f),
                (float) Math.toDegrees(0f)
        };

        final PropertyValuesHolder pvhRotation =
                PropertyValuesHolder.ofKeyframe(View.ROTATION, KeyFrameUtil.getKeyFrames(Flubber.FRACTIONS, values));

        final ObjectAnimator animation =
                ObjectAnimator.ofPropertyValuesHolder(view, pvhRotation);

        return animation;
    }
}

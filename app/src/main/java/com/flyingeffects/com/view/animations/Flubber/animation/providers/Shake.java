package com.flyingeffects.com.view.animations.Flubber.animation.providers;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;

import com.mobile.kadian.utils.Flubber.AnimationBody;
import com.mobile.kadian.utils.Flubber.utils.DimensionUtils;
import com.mobile.kadian.utils.Flubber.utils.KeyFrameUtil;

import static com.mobile.kadian.utils.Flubber.Flubber.FRACTIONS;


public class Shake extends BaseProvider {

    @Override
    public Animator getAnimationFor(AnimationBody animationBody, View view) {
        final float dX = DimensionUtils.dp2px(30);
        final float force = animationBody.getForce();

        float[] translationValues = {0f, (dX * force), (-dX * force), (dX * force), 0f, 0f};
        final PropertyValuesHolder translationPVH =
                PropertyValuesHolder.ofKeyframe(View.TRANSLATION_X, KeyFrameUtil.getKeyFrames(FRACTIONS, translationValues));

        final ObjectAnimator animation =
                ObjectAnimator.ofPropertyValuesHolder(view, translationPVH);

        return animation;
    }
}

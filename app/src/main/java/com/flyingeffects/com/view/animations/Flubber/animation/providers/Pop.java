package com.flyingeffects.com.view.animations.Flubber.animation.providers;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.view.View;

import com.mobile.kadian.utils.Flubber.AnimationBody;
import com.mobile.kadian.utils.Flubber.Flubber;
import com.mobile.kadian.utils.Flubber.utils.KeyFrameUtil;


public class Pop extends BaseProvider {

    @Override
    public Animator getAnimationFor(final AnimationBody animationBody, final View view) {
        final float force = animationBody.getForce();
        final float[] values = {0f, (0.2f * force), (-0.2f * force), (0.2f * force), 0f, 0f};

        final PropertyValuesHolder scaleXPVH =
                PropertyValuesHolder.ofKeyframe(Flubber.SCALE, KeyFrameUtil.getKeyFrames(Flubber.FRACTIONS, values));

        final ValueAnimator animation = ObjectAnimator.ofPropertyValuesHolder(scaleXPVH);

        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            private int initialWidth = -1;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                if (initialWidth == -1) {
                    initialWidth = view.getWidth();
                }

                final float animScaleX = (float) animation.getAnimatedValue(Flubber.SCALE);

                final float scale = (view.getWidth() + view.getWidth() * animScaleX) / initialWidth;

                view.setScaleX(scale);
                view.setScaleY(scale);
            }
        });

        return animation;
    }
}

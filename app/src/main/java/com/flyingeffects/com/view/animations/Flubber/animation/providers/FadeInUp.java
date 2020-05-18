package com.flyingeffects.com.view.animations.Flubber.animation.providers;

import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.view.View;

import com.flyingeffects.com.view.animations.Flubber.AnimationBody;
import com.flyingeffects.com.view.animations.Flubber.utils.DimensionUtils;


public class FadeInUp extends BaseFadeIn {

    @NonNull
    @Override
    protected ObjectAnimator getTranslation(AnimationBody animationBody, View view) {

        final float startY = DimensionUtils.dp2px(300) * animationBody.getForce();
        final float endY = 0f;

        final ObjectAnimator translateAnimation =
                ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, startY, endY);

        return translateAnimation;
    }
}

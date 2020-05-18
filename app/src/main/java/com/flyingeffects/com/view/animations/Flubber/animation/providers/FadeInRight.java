package com.flyingeffects.com.view.animations.Flubber.animation.providers;

import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.view.View;

import com.flyingeffects.com.view.animations.Flubber.AnimationBody;
import com.flyingeffects.com.view.animations.Flubber.utils.DimensionUtils;

public class FadeInRight extends BaseFadeIn {

    @NonNull
    @Override
    protected ObjectAnimator getTranslation(AnimationBody animationBody, View view) {
        final float startY = -DimensionUtils.dp2px(300) * animationBody.getForce();
        final float endY = 0;

        final ObjectAnimator translateAnimation =
                ObjectAnimator.ofFloat(view, View.TRANSLATION_X, startY, endY);

        return translateAnimation;
    }

}

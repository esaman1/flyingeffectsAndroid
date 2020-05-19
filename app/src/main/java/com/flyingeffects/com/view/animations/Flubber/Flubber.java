package com.flyingeffects.com.view.animations.Flubber;

import android.animation.Animator;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Interpolator;

import com.mobile.kadian.utils.Flubber.animation.providers.Alpha;
import com.mobile.kadian.utils.Flubber.animation.providers.FadeIn;
import com.mobile.kadian.utils.Flubber.animation.providers.FadeInDown;
import com.mobile.kadian.utils.Flubber.animation.providers.FadeInLeft;
import com.mobile.kadian.utils.Flubber.animation.providers.FadeInRight;
import com.mobile.kadian.utils.Flubber.animation.providers.FadeInUp;
import com.mobile.kadian.utils.Flubber.animation.providers.FadeOut;
import com.mobile.kadian.utils.Flubber.animation.providers.FadeOutIn;
import com.mobile.kadian.utils.Flubber.animation.providers.Fall;
import com.mobile.kadian.utils.Flubber.animation.providers.Flash;
import com.mobile.kadian.utils.Flubber.animation.providers.FlipX;
import com.mobile.kadian.utils.Flubber.animation.providers.FlipY;
import com.mobile.kadian.utils.Flubber.animation.providers.Morph;
import com.mobile.kadian.utils.Flubber.animation.providers.Pop;
import com.mobile.kadian.utils.Flubber.animation.providers.Rotation;
import com.mobile.kadian.utils.Flubber.animation.providers.ScaleX;
import com.mobile.kadian.utils.Flubber.animation.providers.ScaleY;
import com.mobile.kadian.utils.Flubber.animation.providers.Shake;
import com.mobile.kadian.utils.Flubber.animation.providers.SlideDown;
import com.mobile.kadian.utils.Flubber.animation.providers.SlideLeft;
import com.mobile.kadian.utils.Flubber.animation.providers.SlideRight;
import com.mobile.kadian.utils.Flubber.animation.providers.SlideUp;
import com.mobile.kadian.utils.Flubber.animation.providers.Squeeze;
import com.mobile.kadian.utils.Flubber.animation.providers.SqueezeDown;
import com.mobile.kadian.utils.Flubber.animation.providers.SqueezeLeft;
import com.mobile.kadian.utils.Flubber.animation.providers.SqueezeRight;
import com.mobile.kadian.utils.Flubber.animation.providers.SqueezeUp;
import com.mobile.kadian.utils.Flubber.animation.providers.Swing;
import com.mobile.kadian.utils.Flubber.animation.providers.TranslationX;
import com.mobile.kadian.utils.Flubber.animation.providers.TranslationY;
import com.mobile.kadian.utils.Flubber.animation.providers.Wobble;
import com.mobile.kadian.utils.Flubber.animation.providers.ZoomIn;
import com.mobile.kadian.utils.Flubber.animation.providers.ZoomOut;
import com.mobile.kadian.utils.Flubber.interpolators.providers.Spring;
import com.mobile.kadian.utils.Flubber.interpolators.providers.bezier.BzrSpring;
import com.mobile.kadian.utils.Flubber.interpolators.providers.bezier.EaseIn;
import com.mobile.kadian.utils.Flubber.interpolators.providers.bezier.EaseInBack;
import com.mobile.kadian.utils.Flubber.interpolators.providers.bezier.EaseInCirc;
import com.mobile.kadian.utils.Flubber.interpolators.providers.bezier.EaseInCubic;
import com.mobile.kadian.utils.Flubber.interpolators.providers.bezier.EaseInExpo;
import com.mobile.kadian.utils.Flubber.interpolators.providers.bezier.EaseInOut;
import com.mobile.kadian.utils.Flubber.interpolators.providers.bezier.EaseInOutBack;
import com.mobile.kadian.utils.Flubber.interpolators.providers.bezier.EaseInOutCirc;
import com.mobile.kadian.utils.Flubber.interpolators.providers.bezier.EaseInOutCubic;
import com.mobile.kadian.utils.Flubber.interpolators.providers.bezier.EaseInOutExpo;
import com.mobile.kadian.utils.Flubber.interpolators.providers.bezier.EaseInOutQuad;
import com.mobile.kadian.utils.Flubber.interpolators.providers.bezier.EaseInOutQuart;
import com.mobile.kadian.utils.Flubber.interpolators.providers.bezier.EaseInOutQuint;
import com.mobile.kadian.utils.Flubber.interpolators.providers.bezier.EaseInOutSine;
import com.mobile.kadian.utils.Flubber.interpolators.providers.bezier.EaseInQuad;
import com.mobile.kadian.utils.Flubber.interpolators.providers.bezier.EaseInQuart;
import com.mobile.kadian.utils.Flubber.interpolators.providers.bezier.EaseInQuint;
import com.mobile.kadian.utils.Flubber.interpolators.providers.bezier.EaseInSine;
import com.mobile.kadian.utils.Flubber.interpolators.providers.bezier.EaseOut;
import com.mobile.kadian.utils.Flubber.interpolators.providers.bezier.EaseOutBack;
import com.mobile.kadian.utils.Flubber.interpolators.providers.bezier.EaseOutCirc;
import com.mobile.kadian.utils.Flubber.interpolators.providers.bezier.EaseOutCubic;
import com.mobile.kadian.utils.Flubber.interpolators.providers.bezier.EaseOutExpo;
import com.mobile.kadian.utils.Flubber.interpolators.providers.bezier.EaseOutQuad;
import com.mobile.kadian.utils.Flubber.interpolators.providers.bezier.EaseOutQuart;
import com.mobile.kadian.utils.Flubber.interpolators.providers.bezier.EaseOutQuint;
import com.mobile.kadian.utils.Flubber.interpolators.providers.bezier.EaseOutSine;
import com.mobile.kadian.utils.Flubber.interpolators.providers.bezier.Linear;

import org.jetbrains.annotations.Contract;

import java.util.HashMap;
import java.util.Map;

public class Flubber {
    public static final float[] FRACTIONS = new float[]{0f, 0.2f, 0.4f, 0.6f, 0.8f, 1f};
    public static final String SCALE = "scale";
    private static final String TAG = "Flubber";

    @Contract(" -> !null")
    public static AnimationBody.Builder with() {
        return AnimationBody.Builder.getBuilder();
    }

    public static Animator getAnimation(@NonNull final AnimationBody animationBody, View view) {
        return animationBody.getAnimation().createAnimationFor(animationBody, view);
    }

    public static enum AnimationPreset implements AnimationProvider {
        SLIDE_LEFT,
        SLIDE_RIGHT,
        SLIDE_DOWN,
        SLIDE_UP,
        SQUEEZE_LEFT,
        SQUEEZE_RIGHT,
        SQUEEZE_DOWN,
        SQUEEZE_UP,
        FADE_IN,
        FADE_OUT,
        FADE_OUT_IN,
        FADE_IN_LEFT,
        FADE_IN_RIGHT,
        FADE_IN_DOWN,
        FADE_IN_UP,
        ZOOM_IN,
        ZOOM_OUT,
        FALL,
        SHAKE,
        POP,
        FLIP_X,
        FLIP_Y,
        MORPH,
        SQUEEZE,
        FLASH,
        WOBBLE,
        SWING,
        ALPHA,
        ROTATION,
        TRANSLATION_X,
        TRANSLATION_Y,
        SCALE_X,
        SCALE_Y;

        private static Map<AnimationPreset, AnimationProvider> providers = new HashMap<>();

        static {
            providers.put(SLIDE_LEFT, new SlideLeft());
            providers.put(SLIDE_RIGHT, new SlideRight());
            providers.put(SLIDE_DOWN, new SlideDown());
            providers.put(SLIDE_UP, new SlideUp());
            providers.put(SQUEEZE_LEFT, new SqueezeLeft());
            providers.put(SQUEEZE_RIGHT, new SqueezeRight());
            providers.put(SQUEEZE_DOWN, new SqueezeDown());
            providers.put(SQUEEZE_UP, new SqueezeUp());
            providers.put(FADE_IN, new FadeIn());
            providers.put(FADE_OUT, new FadeOut());
            providers.put(FADE_OUT_IN, new FadeOutIn());
            providers.put(FADE_IN_LEFT, new FadeInLeft());
            providers.put(FADE_IN_RIGHT, new FadeInRight());
            providers.put(FADE_IN_DOWN, new FadeInDown());
            providers.put(FADE_IN_UP, new FadeInUp());
            providers.put(ZOOM_IN, new ZoomIn());
            providers.put(ZOOM_OUT, new ZoomOut());
            providers.put(FALL, new Fall());
            providers.put(SHAKE, new Shake());
            providers.put(POP, new Pop());
            providers.put(FLIP_X, new FlipX());
            providers.put(FLIP_Y, new FlipY());
            providers.put(MORPH, new Morph());
            providers.put(SQUEEZE, new Squeeze());
            providers.put(FLASH, new Flash());
            providers.put(WOBBLE, new Wobble());
            providers.put(SWING, new Swing());
            providers.put(ALPHA, new Alpha());
            providers.put(ROTATION, new Rotation());
            providers.put(TRANSLATION_X, new TranslationX());
            providers.put(TRANSLATION_Y, new TranslationY());
            providers.put(SCALE_X, new ScaleX());
            providers.put(SCALE_Y, new ScaleY());

            if (providers.keySet().size() != AnimationPreset.values().length) {
                throw new IllegalStateException("You haven't registered all providers for the animation preset.");
            }
        }

        public AnimationProvider getProvider() {
            return providers.get(this);
        }

        @Override
        public Animator createAnimationFor(AnimationBody animationBody, View view) {
            return getProvider().createAnimationFor(animationBody, view);
        }

        public static AnimationPreset valueFor(AnimationProvider animationProvider) {
            for (Map.Entry<AnimationPreset, AnimationProvider> providersEntry :
                    providers.entrySet()) {

                if (animationProvider.getClass().isAssignableFrom(providersEntry.getValue().getClass())) {
                    return providersEntry.getKey();
                }
            }

            return null;
        }
    }

    public static enum Curve implements InterpolatorProvider {
        BZR_EASE_IN,
        BZR_EASE_OUT,
        BZR_EASE_IN_OUT,
        BZR_LINEAR,
        BZR_SPRING,
        BZR_EASE_IN_SINE,
        BZR_EASE_OUT_SINE,
        BZR_EASE_IN_OUT_SINE,
        BZR_EASE_IN_QUAD,
        BZR_EASE_OUT_QUAD,
        BZR_EASE_IN_OUT_QUAD,
        BZR_EASE_IN_CUBIC,
        BZR_EASE_OUT_CUBIC,
        BZR_EASE_IN_OUT_CUBIC,
        BZR_EASE_IN_QUART,
        BZR_EASE_OUT_QUART,
        BZR_EASE_IN_OUT_QUART,
        BZR_EASE_IN_QUINT,
        BZR_EASE_OUT_QUINT,
        BZR_EASE_IN_OUT_QUINT,
        BZR_EASE_IN_EXPO,
        BZR_EASE_OUT_EXPO,
        BZR_EASE_IN_OUT_EXPO,
        BZR_EASE_IN_CIRC,
        BZR_EASE_OUT_CIRC,
        BZR_EASE_IN_OUT_CIRC,
        BZR_EASE_IN_BACK,
        BZR_EASE_OUT_BACK,
        BZR_EASE_IN_OUT_BACK,
        SPRING,
        LINEAR;

        private static Map<Curve, InterpolatorProvider> providers = new HashMap<>();

        static {
            providers.put(BZR_EASE_IN, new EaseIn());
            providers.put(BZR_EASE_OUT, new EaseOut());
            providers.put(BZR_EASE_IN_OUT, new EaseInOut());
            providers.put(BZR_LINEAR, new Linear());
            providers.put(BZR_SPRING, new BzrSpring());
            providers.put(BZR_EASE_IN_SINE, new EaseInSine());
            providers.put(BZR_EASE_OUT_SINE, new EaseOutSine());
            providers.put(BZR_EASE_IN_OUT_SINE, new EaseInOutSine());
            providers.put(BZR_EASE_IN_QUAD, new EaseInQuad());
            providers.put(BZR_EASE_OUT_QUAD, new EaseOutQuad());
            providers.put(BZR_EASE_IN_OUT_QUAD, new EaseInOutQuad());
            providers.put(BZR_EASE_IN_CUBIC, new EaseInCubic());
            providers.put(BZR_EASE_OUT_CUBIC, new EaseOutCubic());
            providers.put(BZR_EASE_IN_OUT_CUBIC, new EaseInOutCubic());
            providers.put(BZR_EASE_IN_QUART, new EaseInQuart());
            providers.put(BZR_EASE_OUT_QUART, new EaseOutQuart());
            providers.put(BZR_EASE_IN_OUT_QUART, new EaseInOutQuart());
            providers.put(BZR_EASE_IN_QUINT, new EaseInQuint());
            providers.put(BZR_EASE_OUT_QUINT, new EaseOutQuint());
            providers.put(BZR_EASE_IN_OUT_QUINT, new EaseInOutQuint());
            providers.put(BZR_EASE_IN_EXPO, new EaseInExpo());
            providers.put(BZR_EASE_OUT_EXPO, new EaseOutExpo());
            providers.put(BZR_EASE_IN_OUT_EXPO, new EaseInOutExpo());
            providers.put(BZR_EASE_IN_CIRC, new EaseInCirc());
            providers.put(BZR_EASE_OUT_CIRC, new EaseOutCirc());
            providers.put(BZR_EASE_IN_OUT_CIRC, new EaseInOutCirc());
            providers.put(BZR_EASE_IN_BACK, new EaseInBack());
            providers.put(BZR_EASE_OUT_BACK, new EaseOutBack());
            providers.put(BZR_EASE_IN_OUT_BACK, new EaseInOutBack());
            providers.put(SPRING, new Spring());
            providers.put(LINEAR, new Linear());

            if (providers.keySet().size() != Curve.values().length) {
                throw new IllegalStateException("You haven't registered all providers for all curves.");
            }
        }

        public InterpolatorProvider getProvider() {
            return providers.get(this);
        }

        @Override
        public Interpolator createInterpolatorFor(AnimationBody animationBody) {
            return getProvider().createInterpolatorFor(animationBody);
        }

        public static Curve valueFor(InterpolatorProvider interpolatorProvider) {
            for (Map.Entry<Curve, InterpolatorProvider> providersEntry :
                    providers.entrySet()) {

                if (interpolatorProvider.getClass().isAssignableFrom(providersEntry.getValue().getClass())) {
                    return providersEntry.getKey();
                }
            }

            return null;
        }
    }

    public static interface AnimationProvider {
        public Animator createAnimationFor(final AnimationBody animationBody, View view);
    }

    public static interface InterpolatorProvider {
        public Interpolator createInterpolatorFor(final AnimationBody animationBody);
    }

}
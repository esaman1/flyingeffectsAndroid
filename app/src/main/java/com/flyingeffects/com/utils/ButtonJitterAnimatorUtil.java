package com.flyingeffects.com.utils;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;

/**
 * 抖动动画工具类
 * @author ZhouGang
 * @date 2020/11/11
 */
public class ButtonJitterAnimatorUtil {
    /***
     * 抖动动画
     * @param view 需要抖动的view
     * @return 动画对象
     */
    public static ObjectAnimator jitter(View view) {

        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofKeyframe(View.SCALE_X,
                Keyframe.ofFloat(0f, 1f),
                Keyframe.ofFloat(0.1f, 1.01f),
                Keyframe.ofFloat(0.2f, 1.02f),
                Keyframe.ofFloat(0.3f, 1.03f),
                Keyframe.ofFloat(0.4f, 1.04f),
                Keyframe.ofFloat(0.5f, 1.05f),
                Keyframe.ofFloat(0.6f, 1.04f),
                Keyframe.ofFloat(0.7f, 1.03f),
                Keyframe.ofFloat(0.8f, 1.02f),
                Keyframe.ofFloat(0.9f, 1.01f),
                Keyframe.ofFloat(1f, 1f)
        );

        PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofKeyframe(View.SCALE_Y,
                Keyframe.ofFloat(0f, 1f),
                Keyframe.ofFloat(0.1f, 1.01f),
                Keyframe.ofFloat(0.2f, 1.02f),
                Keyframe.ofFloat(0.3f, 1.03f),
                Keyframe.ofFloat(0.4f, 1.04f),
                Keyframe.ofFloat(0.5f, 1.05f),
                Keyframe.ofFloat(0.6f, 1.04f),
                Keyframe.ofFloat(0.7f, 1.03f),
                Keyframe.ofFloat(0.8f, 1.02f),
                Keyframe.ofFloat(0.9f, 1.01f),
                Keyframe.ofFloat(1f, 1f)
        );

        //旋转角度
//        PropertyValuesHolder pvhRotate = PropertyValuesHolder.ofKeyframe(View.ROTATION,
//                Keyframe.ofFloat(0f, 0f),
//                Keyframe.ofFloat(0.1f, -3f * shakeFactor),
//                Keyframe.ofFloat(0.2f, -3f * shakeFactor),
//                Keyframe.ofFloat(0.3f, 3f * shakeFactor),
//                Keyframe.ofFloat(0.4f, -3f * shakeFactor),
//                Keyframe.ofFloat(0.5f, 3f * shakeFactor),
//                Keyframe.ofFloat(0.6f, -3f * shakeFactor),
//                Keyframe.ofFloat(0.7f, 3f * shakeFactor),
//                Keyframe.ofFloat(0.8f, -3f * shakeFactor),
//                Keyframe.ofFloat(0.9f, 3f * shakeFactor),
//                Keyframe.ofFloat(1f, 0)
//        );

        return ObjectAnimator.ofPropertyValuesHolder(view, pvhScaleX, pvhScaleY).setDuration(2500);

    }
}

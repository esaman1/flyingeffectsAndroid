package com.flyingeffects.com.view.animations;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

public class CancelAnimatorListenerAdapter extends AnimatorListenerAdapter {
    private boolean mCanceled = false;

    @Override
    public void onAnimationCancel(Animator animation) {
        super.onAnimationCancel(animation);
        mCanceled = true;
    }

    public boolean isCanceled() {
        return mCanceled;
    }
}
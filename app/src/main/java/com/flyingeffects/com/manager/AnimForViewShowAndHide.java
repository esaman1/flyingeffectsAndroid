package com.flyingeffects.com.manager;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class AnimForViewShowAndHide {


    private static AnimForViewShowAndHide thisModel;

    public static AnimForViewShowAndHide getInstance() {

        if (thisModel == null) {
            thisModel = new AnimForViewShowAndHide();
        }
        return thisModel;

    }


    public void hide(View view){
        AlphaAnimation    hideAnim  = new AlphaAnimation(1, 0);
        hideAnim.setDuration(500);
        view.startAnimation(hideAnim);
        hideAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    public void show(View view){
        AlphaAnimation    hideAnim  = new AlphaAnimation(0, 1);
        hideAnim.setDuration(500);
        view.startAnimation(hideAnim);
        hideAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

}

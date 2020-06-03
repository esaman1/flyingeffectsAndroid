package com.flyingeffects.com.utils;

import android.view.View;

import java.util.Calendar;

public abstract class NoDoubleClickListener implements View.OnClickListener {

    private static final int MIN_CLICK_DELAY_TIME = 100;
    private long mLastClickTime = 0;

    @Override
    public void onClick(View v) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - mLastClickTime > MIN_CLICK_DELAY_TIME) {
            mLastClickTime = currentTime;
            onNoDoubleClick(v);
        }
    }

    public abstract void onNoDoubleClick(View v);
}

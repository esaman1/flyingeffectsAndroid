package com.flyingeffects.com.ui.interfaces.view;

import android.widget.RelativeLayout;


public interface UploadMaterialMVPView {
    RelativeLayout getVideoContainer();

    void initTrimmer();

    void showTimeMs(long durationMs, long startTimeMs, long durationTimeMs);

    void updateCursor(float currentX);

    void hideCursor();

    void showCursor();

    void finishCrop(String path);

    void  getRealCutTime(float RealCutTime);
}

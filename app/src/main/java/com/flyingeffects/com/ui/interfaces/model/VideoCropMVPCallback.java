package com.flyingeffects.com.ui.interfaces.model;

public interface VideoCropMVPCallback {

    void initTrimmer();

    void showCropTotalTime(long durationMs, long startTimeMs, long endTimeMs);

    void updateCursor(float currentX);

    void hideCursor();

    void showCursor();

    void finishCrop(String path);
}

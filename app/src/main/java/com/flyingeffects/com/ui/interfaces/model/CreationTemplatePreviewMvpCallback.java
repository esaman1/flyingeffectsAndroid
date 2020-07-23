package com.flyingeffects.com.ui.interfaces.model;

import com.flyingeffects.com.enity.TemplateType;

import java.util.List;

public interface CreationTemplatePreviewMvpCallback {
    void initTrimmer();

    void showCropTotalTime(long durationMs, long startTimeMs, long endTimeMs);

    void updateCursor(float currentX);

    void hideCursor();

    void showCursor();

    void finishCrop(String path);

    void getRealCutTime(float RealCutTime);

    void seekToPosition(long position,float allDuration);

    void isSaveToAlbum(String path,boolean isAdSuccess);

}

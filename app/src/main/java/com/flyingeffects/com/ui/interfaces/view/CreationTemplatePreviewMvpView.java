package com.flyingeffects.com.ui.interfaces.view;



public interface CreationTemplatePreviewMvpView {
    void updateCursor(float currentX);

    void seekToPosition(long position);

    void isSaveToAlbum(String path,boolean isAdSuccess);
}

package com.flyingeffects.com.ui.interfaces.view;

public interface FUBeautyMvpView {

    void showCountDown(float num,int countDownStatus,float progress);

    void nowChooseRecordIsInfinite(boolean isInfinite);

    void changeFUSticker(String bundle,String name);

    void clearSticker();

    void finishAct();

}

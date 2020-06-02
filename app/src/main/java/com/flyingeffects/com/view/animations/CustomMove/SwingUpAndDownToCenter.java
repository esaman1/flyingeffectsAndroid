package com.flyingeffects.com.view.animations.CustomMove;

import com.flyingeffects.com.enity.TransplationPos;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.view.StickerView;
import com.lansosdk.box.Layer;

import java.util.ArrayList;
import java.util.List;


/**
 * description ：动画，上下摇摆
 * creation date: 2020/5/25
 * user : zhangtongju
 */

public class SwingUpAndDownToCenter extends baseAnimModel {


    private StickerView mainStickerView;


    public void toChangeStickerView(StickerView mainStickerView, List<StickerView> subLayer) {
        this.mainStickerView = mainStickerView;
        setOriginal(mainStickerView.getCenterX(), mainStickerView.getCenterY());
        float stickerViewPosition = mainStickerView.getMBoxCenterY();
        //第一个参数为总时长
        animationLinearInterpolator = new AnimationLinearInterpolator(2000, new AnimationLinearInterpolator.GetProgressCallback() {
            @Override
            public void progress(float progress, boolean isDone) {
                LogUtil.d("animationLinearInterpolator","progress="+progress);
                //拟定倒叙
                float Y =stickerViewPosition+ 50  * progress;
                mainStickerView.toTranMoveY(Y);

            }
        });
        animationLinearInterpolator.setInterpolatorType(1);
        animationLinearInterpolator.PlayAnimation();
    }


    private float previewSubPosition;
    private float previewSubPaddingHeight;
    public void initToChangeSubLayer(Layer mainLayer, LayerAnimCallback callback, float percentage){
        previewSubPosition= mainLayer.getPositionY();
        LogUtil.d("previewSubPosition","previewSubPosition="+previewSubPosition);
        previewSubPaddingHeight=mainLayer.getPadHeight();
        toChangeSubLayer(callback,percentage);
    }


    public void toChangeSubLayer( LayerAnimCallback callback, float percentage){
        AnimationLinearInterpolator animationLinearInterpolator = new AnimationLinearInterpolator(2000, new AnimationLinearInterpolator.GetProgressCallback() {
            @Override
            public void progress(float progress, boolean isDone) {
                LogUtil.d("animationLinearInterpolator","progress="+progress);
                //拟定倒叙
                float Y =previewSubPosition+ 50  * progress;
                TransplationPos transplationPos = new TransplationPos();
                transplationPos.setToY(Y/previewSubPaddingHeight);
                ArrayList<TransplationPos> list = new ArrayList<>();
                list.add(transplationPos);
                callback.translationalXY(list);
            }
        });
        animationLinearInterpolator.setInterpolatorType(1);
        animationLinearInterpolator.PlayAnimationNoTimer(percentage);
    }





    public void StopAnim() {
        if (animationLinearInterpolator != null) {
            animationLinearInterpolator.endTimer();
            resetAnimState(mainStickerView);
        }

    }

}

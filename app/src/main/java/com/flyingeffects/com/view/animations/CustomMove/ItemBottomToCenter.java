package com.flyingeffects.com.view.animations.CustomMove;

import com.flyingeffects.com.enity.TransplationPos;
import com.flyingeffects.com.view.StickerView;
import com.lansosdk.box.Layer;

import java.util.ArrayList;
import java.util.List;


/**
 * description ：动画，底部到上的动画
 * creation date: 2020/5/25
 * user : zhangtongju
 */

public class ItemBottomToCenter extends baseAnimModel {


    private StickerView mainStickerView;


    public void toChangeStickerView(StickerView mainStickerView, List<StickerView> subLayer) {
        this.mainStickerView = mainStickerView;
        setOriginal(mainStickerView.getCenterX(), mainStickerView.getCenterY());
        float totalHeight = mainStickerView.getMeasuredHeight() + (mainStickerView.getmHelpBoxRectH());
        float stickerViewPosition = mainStickerView.getMBoxCenterY();
        float stickerViewH = mainStickerView.getmHelpBoxRectH();
        //view 右边位置的比例
        float Difference = totalHeight - stickerViewH;

        //第一个参数为总时长
        animationLinearInterpolator = new AnimationLinearInterpolator(2000, new AnimationLinearInterpolator.GetProgressCallback() {
            @Override
            public void progress(float progress, boolean isDone) {
                float needProgress = 1 - progress;
                //拟定倒叙
                float Y = stickerViewPosition + Difference * needProgress;
                mainStickerView.toTranMoveY(Y);
            }
        });
        animationLinearInterpolator.PlayAnimation();
    }

   private float previewSubPosition;
    private float previewSubPaddingHeight;
    public void initToChangeSubLayer(Layer mainLayer, LayerAnimCallback callback, float percentage) {
        previewSubPosition= mainLayer.getPositionY();
        previewSubPaddingHeight=mainLayer.getPadHeight();
        toChangeSubLayer(callback,percentage);
    }



    public void toChangeSubLayer( LayerAnimCallback callback, float percentage){
        AnimationLinearInterpolator animationLinearInterpolator = new AnimationLinearInterpolator(2000, new AnimationLinearInterpolator.GetProgressCallback() {
            @Override
            public void progress(float progress, boolean isDone) {
                float needProgress = 1 - progress;
                float Y = (previewSubPosition + previewSubPaddingHeight * needProgress)/previewSubPaddingHeight;
                TransplationPos transplationPos = new TransplationPos();
                transplationPos.setToY(Y);
                ArrayList<TransplationPos> list = new ArrayList<>();
                list.add(transplationPos);
                callback.translationalXY(list);
            }
        });
        animationLinearInterpolator.PlayAnimationNoTimer(percentage);
    }


    public void StopAnim() {
        if (animationLinearInterpolator != null) {
            animationLinearInterpolator.endTimer();
            resetAnimState(mainStickerView);
        }
    }

}

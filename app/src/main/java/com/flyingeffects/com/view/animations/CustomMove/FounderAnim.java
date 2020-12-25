package com.flyingeffects.com.view.animations.CustomMove;

import android.graphics.Path;
import android.graphics.PathMeasure;

import com.flyingeffects.com.enity.TransplationPos;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.view.StickerView;
import com.lansosdk.box.Layer;
import com.lansosdk.box.SubLayer;

import java.util.ArrayList;
import java.util.List;


/**
 * description ：方正分身o
 * creation date: 2020/5/25
 * user : zhangtongju
 */

public class FounderAnim extends baseAnimModel {


    private StickerView mainStickerView;


    void toChangeStickerView(StickerView mainStickerView, List<StickerView> subLayer) {

        ArrayList<StickerView> listAllSticker = new ArrayList<>();
        listAllSticker.addAll(subLayer);
        this.mainStickerView = mainStickerView;
        setRotate(mainStickerView.getRotateAngle());
        setOriginal(mainStickerView.getCenterX(), mainStickerView.getCenterY());
        float[] pos = new float[2];
        float[] tan = new float[2];

        float frameScreenWidth = mainStickerView.getWidth();
        float needWidth = frameScreenWidth / (float) 6;
        float frameScreenHeight = mainStickerView.getHeight();
        float needStartHeight = (frameScreenHeight - frameScreenWidth) / (float) 2;
        float hafStickerH = mainStickerView.getmHelpBoxRectH() / (float) 2;
        float haltStickerW = mainStickerView.getmHelpBoxRectW() / (float) 2;
        for (int i = 0; i < subLayer.size(); i++) {
            int row = i / 6;
            int xx = i % 6;
            LogUtil.d("OOM5", "row=" + row);
            StickerView stickerView = subLayer.get(i);
            stickerView.toTranMoveXY(needWidth * xx - haltStickerW, needStartHeight + hafStickerH * row);
//            stickerView.setVisibility(View.g);
        }


        //第一个参数为总时长
        animationLinearInterpolator = new AnimationLinearInterpolator(3000, (progress, isDone) -> {
            for (int i = 0; i < subLayer.size(); i++) {
                int xx = i % 6;
                StickerView stickerView = subLayer.get(i);
                float needScale = progress * xx*0.5f;
                if (needScale > 0.5) {
                    needScale = 0.5f;
                }
                stickerView.setScale(needScale);
//            stickerView.setVisibility(View.g);
            }
        });
        animationLinearInterpolator.SetCirculation(false);
        animationLinearInterpolator.PlayAnimation();
    }


    private PathMeasure LansongPathMeasure;
    private float lansongTotalDistancePathMeasure;
    private float[] LanSongPos;
    private float[] LanSongTan;
    private Layer mainLayer;
    private float perDistance;
    private ArrayList<TransplationPos> listForTranslaptionPosition = new ArrayList<>();

    void toChangeSubLayer(Layer mainStickerView, ArrayList<SubLayer> listForSubLayer, LayerAnimCallback callback, float percentage) {
        LanSongPos = new float[2];
        LanSongTan = new float[2];
        listForTranslaptionPosition.clear();
        this.mainLayer = mainStickerView;
        LogUtil.d("OOOM", "主图层中间的位置X为" + mainStickerView.getPositionX() + ",Y的位置为" + mainStickerView.getPositionY());
        LansongPathMeasure = setPathMeasure(mainStickerView.getScaleHeight(), mainStickerView.getPositionX(), mainStickerView.getPositionY());
        //总长度
        lansongTotalDistancePathMeasure = LansongPathMeasure.getLength();
        perDistance = lansongTotalDistancePathMeasure / (float) 10;
        getLansongTranslation(callback, percentage, listForSubLayer);
        LogUtil.d("translationalXY", "当前的事件为percentage=" + percentage);
    }


    void getLansongTranslation(LayerAnimCallback callback, float percentage, ArrayList<SubLayer> listForSubLayer) {
        listForTranslaptionPosition.clear();
        AnimationLinearInterpolator animationLinearInterpolator = new AnimationLinearInterpolator(5000, (progress, isDone) -> {
            //主图层应该走的位置
            if (LansongPathMeasure != null) {
                float nowDistance = lansongTotalDistancePathMeasure * progress;
                LansongPathMeasure.getPosTan(nowDistance, LanSongPos, LanSongTan);
                //这里获得的时一个具体的值，而蓝松sdk 这边需要的时一个0-1之间的值，及0.5 表示居中
                float translateionalX = LanSongPos[0] / mainLayer.getPadWidth();
                float translateionalY = LanSongPos[1] / mainLayer.getPadHeight();
                TransplationPos transplationPos = new TransplationPos();
                transplationPos.setToX(translateionalX);
                transplationPos.setToY(translateionalY);
                listForTranslaptionPosition.add(transplationPos);
                for (int i = 0; i < listForSubLayer.size(); i++) {
                    SubLayer sub = listForSubLayer.get(i);
                    if (sub != null) {
                        float needDistance = perDistance * i + nowDistance;
                        if (needDistance > lansongTotalDistancePathMeasure) {
                            needDistance = needDistance - lansongTotalDistancePathMeasure;
                        }
                        LansongPathMeasure.getPosTan(needDistance, LanSongPos, LanSongTan);
                        TransplationPos newTransplationPos = new TransplationPos();
                        newTransplationPos.setToX(LanSongPos[0] / mainLayer.getPadWidth());
                        newTransplationPos.setToY(LanSongPos[1] / mainLayer.getPadHeight());
                        listForTranslaptionPosition.add(newTransplationPos);
                    }
                }
                callback.translationalXY(listForTranslaptionPosition);
            }
        });
        animationLinearInterpolator.PlayAnimationNoTimer(percentage);
    }


    @Override
    public void StopAnim() {
        if (animationLinearInterpolator != null) {
            animationLinearInterpolator.endTimer();
            resetAnimState(mainStickerView);
        }
    }


    /**
     * description ：路径动画
     * creation date: 2020/5/28
     * layerH 自身的高
     * user : zhangtongju
     */
    private PathMeasure setPathMeasure(float layerH, float layerCenterX, float layerCenterY) {
        float diameter = layerH / 3 * 2;
        Path mAnimPath = new Path();

        mAnimPath.addCircle(layerCenterX, layerCenterY, diameter * 2, Path.Direction.CCW);
        PathMeasure mPathMeasure = new PathMeasure();
        mPathMeasure.setPath(mAnimPath, true);
        return mPathMeasure;
    }

}

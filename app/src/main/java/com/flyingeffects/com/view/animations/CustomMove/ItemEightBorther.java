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
 * description ：动画，花生动画，8兄弟动画  8888888
 * creation date: 2020/5/25
 * user : zhangtongju
 */

public class ItemEightBorther extends baseAnimModel {

    private StickerView mainStickerView;


    void toChangeStickerView(StickerView mainStickerView, List<StickerView> subLayer) {
        this.mainStickerView = mainStickerView;
        setRotate(mainStickerView.getRotateAngle());
        setOriginal(mainStickerView.getCenterX(), mainStickerView.getCenterY());
        float[] pos = new float[2];
        float[] tan = new float[2];
        PathMeasure mPathMeasure = setPathMeasure(mainStickerView.getmHelpBoxRectH(), mainStickerView.getMBoxCenterX(), mainStickerView.getMBoxCenterY());
        float totalDistancePathMeasure = mPathMeasure.getLength();
        float perDistance = totalDistancePathMeasure / (float) 12;
        //第一个参数为总时长
        animationLinearInterpolator = new AnimationLinearInterpolator(10000, (progress, isDone) -> {
            //主图层应该走的位置
            float nowDistance = totalDistancePathMeasure * progress;
            mPathMeasure.getPosTan(nowDistance, pos, tan);
            mainStickerView.toTranMoveXY(pos[0], pos[1]);
            if(subLayer!=null){
                for (int i = 0; i < subLayer.size(); i++) {
                    StickerView sub = subLayer.get(i);
                    if (sub != null) {
                        float needDistance = perDistance * i + nowDistance;
                        if (needDistance > totalDistancePathMeasure) {
                            needDistance = needDistance - totalDistancePathMeasure;
                        }
                        mPathMeasure.getPosTan(needDistance, pos, tan);
                        sub.toTranMoveXY(pos[0], pos[1]);
                    }
                }
            }
        });
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
        LogUtil.d("OOOM","主图层中间的位置X为"+ mainStickerView.getPositionX()+",Y的位置为"+mainStickerView.getPositionY());
        LansongPathMeasure = setPathMeasure(mainStickerView.getScaleHeight(), mainStickerView.getPositionX(), mainStickerView.getPositionY());
        //总长度
        lansongTotalDistancePathMeasure = LansongPathMeasure.getLength();
        perDistance = lansongTotalDistancePathMeasure / (float) 12;
        getLansongTranslation(callback, percentage, listForSubLayer);
        LogUtil.d("translationalXY", "当前的事件为percentage=" + percentage);
    }


    void getLansongTranslation(LayerAnimCallback callback, float percentage, ArrayList<SubLayer> listForSubLayer) {
        listForTranslaptionPosition.clear();
        AnimationLinearInterpolator animationLinearInterpolator = new AnimationLinearInterpolator(10000, (progress, isDone) -> {
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
     * user : zhangtongju
     */
    private PathMeasure setPathMeasure(float layerH, float layerCenterX, float layerCenterY) {
        float diameter = layerH / 3 * 2;
        Path mAnimPath = new Path();
        mAnimPath.moveTo(layerCenterX, layerCenterY - diameter * 2);
        mAnimPath.rQuadTo(-diameter * 2, diameter, 0, diameter * 2);
        mAnimPath.rQuadTo(diameter * 2, diameter, 0, diameter * 2);
        mAnimPath.rQuadTo(-diameter * 2, -diameter, 0, -diameter * 2);
        mAnimPath.rQuadTo(diameter * 2, -diameter, 0, -diameter * 2);
        PathMeasure mPathMeasure = new PathMeasure();
        mPathMeasure.setPath(mAnimPath, true);
        return mPathMeasure;
    }


}

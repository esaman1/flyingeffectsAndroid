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
 * description ：桃心
 * <p>
 * creation date: 2020/5/25
 * user : zhangtongju
 */

public class LoveAnim extends baseAnimModel {

    private StickerView mainStickerView;
    private PathMeasure LansongPathMeasure;
    private PathMeasure LansongPathMeasure2;
    private List<StickerView> subLayer1 = new ArrayList<>();
    private List<StickerView> subLayer2 = new ArrayList<>();

    void toChangeStickerView(StickerView mainStickerView, List<StickerView> subLayer) {
        for (int i = 0; i < subLayer.size(); i++) {
            if (i < 8) {
                subLayer1.add(subLayer.get(i));
            } else {
                subLayer2.add(subLayer.get(i));
            }
        }
        this.mainStickerView = mainStickerView;
        setRotate(mainStickerView.getRotateAngle());
        setOriginal(mainStickerView.getCenterX(), mainStickerView.getCenterY());
        float[] pos = new float[2];
        float[] tan = new float[2];
        float[] pos2 = new float[2];
        float[] tan2 = new float[2];
        LansongPathMeasure = setPathMeasureOne(mainStickerView.getWidth(), mainStickerView.getHeight());
        LansongPathMeasure2 = setPathMeasureTwo(mainStickerView.getWidth(), mainStickerView.getHeight());
        float totalDistancePathMeasure = LansongPathMeasure.getLength();
        float perDistance = totalDistancePathMeasure / (float) 8;
        //第一个参数为总时长
        animationLinearInterpolator = new AnimationLinearInterpolator(2000, (progress, isDone) -> {
            float nowDistance = totalDistancePathMeasure * progress;
            LansongPathMeasure.getPosTan(nowDistance, pos, tan);
            for (int i = 0; i < subLayer1.size(); i++) {
                StickerView sub = subLayer1.get(i);
                if (sub != null) {
                    float needDistance = perDistance * i + nowDistance;
                    if (needDistance > totalDistancePathMeasure) {
                        needDistance = needDistance - totalDistancePathMeasure;
                    }
                    LansongPathMeasure.getPosTan(needDistance, pos, tan);
                    sub.toTranMoveXY(pos[0], pos[1]);
                }

                if (i == 0) {
                    mainStickerView.toTranMoveXY(pos[0], pos[1]);
                }
            }
        });
        animationLinearInterpolator.PlayAnimation();


        //第一个参数为总时长
        AnimationLinearInterpolator animationLinearInterpolator2 = new AnimationLinearInterpolator(2000, (progress, isDone) -> {
            float nowDistance = totalDistancePathMeasure * progress;
            LansongPathMeasure2.getPosTan(nowDistance, pos2, tan2);
            for (int i = 0; i < subLayer2.size(); i++) {
                StickerView sub = subLayer2.get(i);
                if (sub != null) {
                    float needDistance = perDistance * i + nowDistance;
                    if (needDistance > totalDistancePathMeasure) {
                        needDistance = needDistance - totalDistancePathMeasure;
                    }
                    LansongPathMeasure2.getPosTan(needDistance, pos2, tan2);
                    sub.toTranMoveXY(pos2[0], pos2[1]);
                }
            }
        });
        animationLinearInterpolator2.PlayAnimation();

    }


    @Override
    public void StopAnim() {
        if (animationLinearInterpolator != null) {
            animationLinearInterpolator.endTimer();
            resetAnimState(mainStickerView);
        }
    }


    //--------------------------------适配蓝松---------------------------------------


    private float lansongTotalDistancePathMeasure;
    private float[] LanSongPos;
    private float[] LanSongTan;
    private float[] LanSongPos2;
    private float[] LanSongTan2;
    private Layer mainLayer;
    private float perDistance;
    private final ArrayList<TransplationPos> listForTranslaptionPosition = new ArrayList<>();
    private final List<SubLayer> LansongSubLayer1 = new ArrayList<>();
    private final List<SubLayer> LansongSubLayer2 = new ArrayList<>();


    public void initToChangeSubLayer(Layer mainLayer, ArrayList<SubLayer> listForSubLayer, LayerAnimCallback callback, float percentage) {
        LanSongPos = new float[2];
        LanSongTan = new float[2];
        LanSongPos2 = new float[2];
        LanSongTan2 = new float[2];
        LansongSubLayer1.clear();
        LansongSubLayer2.clear();
        for (int i = 0; i < listForSubLayer.size(); i++) {
            if (i < 8) {
                LansongSubLayer1.add(listForSubLayer.get(i));
            } else {
                LansongSubLayer2.add(listForSubLayer.get(i));
            }
        }
        this.mainLayer = mainLayer;
        LansongPathMeasure = setPathMeasureOne(mainLayer.getPadWidth(), mainLayer.getPadHeight());
        LansongPathMeasure2 = setPathMeasureTwo(mainLayer.getPadWidth(), mainLayer.getPadHeight());
        lansongTotalDistancePathMeasure = LansongPathMeasure.getLength();
        perDistance = lansongTotalDistancePathMeasure / (float) 8;
        toChangeSubLayer(listForSubLayer, callback, percentage);
    }


    public void toChangeSubLayer(ArrayList<SubLayer> listForSubLayer, LayerAnimCallback callback, float percentage) {
        getLansongTranslation(callback, percentage, listForSubLayer);
        LogUtil.d("translationalXY", "当前的事件为percentage=" + percentage);
    }


    void getLansongTranslation(LayerAnimCallback callback, float percentage, ArrayList<SubLayer> listForSubLayer) {
        listForTranslaptionPosition.clear();
        AnimationLinearInterpolator animationLinearInterpolator = new AnimationLinearInterpolator(2000, (progress, isDone) -> {
            float nowDistance = lansongTotalDistancePathMeasure * progress;
            LansongPathMeasure.getPosTan(nowDistance, LanSongPos, LanSongTan);
            for (int i = 0; i < LansongSubLayer1.size(); i++) {
                SubLayer sub = LansongSubLayer1.get(i);
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
                    if (i == 0) {
                        listForTranslaptionPosition.add(newTransplationPos);
                    }
                }
            }
        });
        animationLinearInterpolator.PlayAnimationNoTimer(percentage);
        AnimationLinearInterpolator animationLinearInterpolator2 = new AnimationLinearInterpolator(2000, (progress, isDone) -> {
            float nowDistance = lansongTotalDistancePathMeasure * progress;
            LansongPathMeasure2.getPosTan(nowDistance, LanSongPos2, LanSongTan2);
            for (int i = 0; i < LansongSubLayer2.size(); i++) {
                SubLayer sub = LansongSubLayer2.get(i);
                if (sub != null) {
                    float needDistance = perDistance * i + nowDistance;
                    if (needDistance > lansongTotalDistancePathMeasure) {
                        needDistance = needDistance - lansongTotalDistancePathMeasure;
                    }
                    LansongPathMeasure2.getPosTan(needDistance, LanSongPos2, LanSongTan2);
                    TransplationPos newTransplationPos = new TransplationPos();
                    newTransplationPos.setToX(LanSongPos2[0] / mainLayer.getPadWidth());
                    newTransplationPos.setToY(LanSongPos2[1] / mainLayer.getPadHeight());
                    listForTranslaptionPosition.add(newTransplationPos);
                }
            }
            callback.translationalXY(listForTranslaptionPosition);
        });

        animationLinearInterpolator2.PlayAnimationNoTimer(percentage);

    }


    /**
     * description ：路径动画
     * creation date: 2020/5/28
     * layerH 自身的高
     * user : zhangtongju
     */
    private PathMeasure setPathMeasureOne(float width, float height) {
        Path mAnimPath = new Path();
        mAnimPath.moveTo(width / 2, height / 4);
        mAnimPath.cubicTo((width * 6) / 7, height / 9, (width * 12) / 13, (height * 2) / 5, width / 2, (height * 7) / 12);
        PathMeasure mPathMeasure = new PathMeasure();
        mPathMeasure.setPath(mAnimPath, false);
        return mPathMeasure;
    }


    private PathMeasure setPathMeasureTwo(float width, float height) {
        Path mAnimPath = new Path();
        mAnimPath.moveTo(width / 2, height / 4);
        mAnimPath.cubicTo(width / 7, height / 9, width / 13, (height * 2) / 5, width / 2, (height * 7) / 12);
        PathMeasure mPathMeasure = new PathMeasure();
        mPathMeasure.setPath(mAnimPath, false);
        return mPathMeasure;
    }
}

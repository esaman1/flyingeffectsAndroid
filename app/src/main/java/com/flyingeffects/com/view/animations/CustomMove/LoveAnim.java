package com.flyingeffects.com.view.animations.CustomMove;

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.view.View;

import com.flyingeffects.com.view.StickerView;

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

                if(i==0){
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
//                    if (i == subLayer2.size() -2) {
//                        mainStickerView.toTranMoveXY(pos2[0], tan2[1]);
//                    }

                }
            }
        });
        animationLinearInterpolator2.PlayAnimation();

    }


//    private float lansongTotalDistancePathMeasure;
//    private float[] LanSongPos;
//    private float[] LanSongTan;
//    private Layer mainLayer;
//    private float perDistance;
//    private ArrayList<TransplationPos> listForTranslaptionPosition = new ArrayList<>();
//
//    void toChangeSubLayer(Layer mainStickerView, ArrayList<SubLayer> listForSubLayer, LayerAnimCallback callback, float percentage) {
//        LanSongPos = new float[2];
//        LanSongTan = new float[2];
//        listForTranslaptionPosition.clear();
//    }
//
//
//    void getLansongTranslation(LayerAnimCallback callback, float percentage, ArrayList<SubLayer> listForSubLayer) {
//        listForTranslaptionPosition.clear();
//        AnimationLinearInterpolator animationLinearInterpolator = new AnimationLinearInterpolator(5000, (progress, isDone) -> {
//            //主图层应该走的位置
//            if (LansongPathMeasure != null) {
//                float nowDistance = lansongTotalDistancePathMeasure * progress;
//                LansongPathMeasure.getPosTan(nowDistance, LanSongPos, LanSongTan);
//                //这里获得的时一个具体的值，而蓝松sdk 这边需要的时一个0-1之间的值，及0.5 表示居中
//                float translateionalX = LanSongPos[0] / mainLayer.getPadWidth();
//                float translateionalY = LanSongPos[1] / mainLayer.getPadHeight();
//                TransplationPos transplationPos = new TransplationPos();
//                transplationPos.setToX(translateionalX);
//                transplationPos.setToY(translateionalY);
//                listForTranslaptionPosition.add(transplationPos);
//                for (int i = 0; i < listForSubLayer.size(); i++) {
//                    SubLayer sub = listForSubLayer.get(i);
//                    if (sub != null) {
//                        float needDistance = perDistance * i + nowDistance;
//                        if (needDistance > lansongTotalDistancePathMeasure) {
//                            needDistance = needDistance - lansongTotalDistancePathMeasure;
//                        }
//                        LansongPathMeasure.getPosTan(needDistance, LanSongPos, LanSongTan);
//                        TransplationPos newTransplationPos = new TransplationPos();
//                        newTransplationPos.setToX(LanSongPos[0] / mainLayer.getPadWidth());
//                        newTransplationPos.setToY(LanSongPos[1] / mainLayer.getPadHeight());
//                        listForTranslaptionPosition.add(newTransplationPos);
//                    }
//                }
//                callback.translationalXY(listForTranslaptionPosition);
//            }
//        });
//        animationLinearInterpolator.PlayAnimationNoTimer(percentage);
//    }


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
    private PathMeasure setPathMeasureOne(float width, float height) {
        Path mAnimPath = new Path();
        // 绘制心形
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

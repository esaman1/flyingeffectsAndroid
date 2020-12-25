package com.flyingeffects.com.view.animations.CustomMove;

import android.graphics.Path;
import android.graphics.PathMeasure;

import com.flyingeffects.com.enity.TransplationPos;
import com.flyingeffects.com.view.StickerView;
import com.lansosdk.box.Layer;
import com.lansosdk.box.SubLayer;

import java.util.ArrayList;
import java.util.List;


/**
 * description ：桃心
 * 0-3秒表示放大动画   3-4 拉开 4-7秒旋转 7-8秒水平回位
 * creation date: 2020/5/25
 * user : zhangtongju
 */

public class LoveAnim extends baseAnimModel {

    private StickerView mainStickerView;
    private StickerView subStickerView;
    private PathMeasure LansongPathMeasure;
    private PathMeasure LansongPathMeasure2;


    void toChangeStickerView(StickerView mainStickerView, List<StickerView> subLayer) {
        this.mainStickerView = mainStickerView;
        subStickerView = subLayer.get(0);
        float mScale = mainStickerView.GetHelpBoxRectScale();
        setRotate(mainStickerView.getRotateAngle());
        setOriginal(mainStickerView.getCenterX(), mainStickerView.getCenterY());
        LansongPathMeasure = setPathMeasureOne(mainStickerView.getmHelpBoxRectH(),mainStickerView.getCenterX(),mainStickerView.getCenterY());

        //第一个参数为总时长
        animationLinearInterpolator = new AnimationLinearInterpolator(2000, (progress, isDone) -> {
            //progress  0-1
            if (progress <= 0.35) {
                //放大动画  *4.16 意味着0-2.24可以看做0-1完整来看
                float x = progress * 2.85f;
                subStickerView.toScale(x, mScale, isDone);
                mainStickerView.toScale(x, mScale, isDone);
            }
            else {
//                //旋转
//                float needProgress = progress - 0.35f;
//                float x = needProgress * 1.53f;
//                float nowDistance = totalDistancePathMeasure * x;
//                LansongPathMeasure.getPosTan(nowDistance, pos, tan);
//                LansongPathMeasure2.getPosTan(nowDistance, pos2, tan2);
//                mainStickerView.toTranMoveXY(pos[0], pos[1]);
//                subStickerView.toTranMoveXY(pos2[0], pos2[1]);
            }

        });
        animationLinearInterpolator.SetCirculation(false);
        animationLinearInterpolator.PlayAnimation();
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
    private PathMeasure setPathMeasureOne(float halfWidth,float centerX,float centerY) {
        Path mAnimPath = new Path();
        mAnimPath.moveTo(centerX,centerY);
        mAnimPath.lineTo(centerX-halfWidth,centerY);
        mAnimPath.lineTo(centerX,centerY+halfWidth*2);
        mAnimPath.lineTo(centerX+halfWidth,centerY);
        PathMeasure mPathMeasure = new PathMeasure();
        mPathMeasure.setPath(mAnimPath, true);
        return mPathMeasure;
    }







}

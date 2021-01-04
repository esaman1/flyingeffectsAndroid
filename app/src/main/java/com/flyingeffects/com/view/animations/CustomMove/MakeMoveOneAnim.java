package com.flyingeffects.com.view.animations.CustomMove;

import android.graphics.Path;
import android.graphics.PathMeasure;

import com.flyingeffects.com.enity.TransplationPos;
import com.flyingeffects.com.view.StickerView;
import com.lansosdk.box.Layer;
import com.lansosdk.box.SubLayer;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


/**
 * description ：动起来1
 * 0-3秒表示放大动画   3-4 拉开 4-7秒旋转 7-8秒水平回位
 * creation date: 2020/5/25
 * user : zhangtongju
 */

public class MakeMoveOneAnim extends baseAnimModel {

    private StickerView mainStickerView;
    private StickerView subStickerView;

    void toChangeStickerView(StickerView mainStickerView, List<StickerView> subLayer) {

        if(subLayer==null||subLayer.size()==0){
            return;
        }

        this.mainStickerView = mainStickerView;
        subStickerView = subLayer.get(0);
        float mScale = mainStickerView.GetHelpBoxRectScale();
        setRotate(mainStickerView.getRotateAngle());
        setOriginal(mainStickerView.getCenterX(), mainStickerView.getCenterY());
        LansongPathMeasure = setPathMeasureOne(mainStickerView.getmHelpBoxRectH(), mainStickerView.getCenterX(), mainStickerView.getCenterY());
        LansongPathMeasure2 = setPathMeasureTwo(mainStickerView.getmHelpBoxRectH(), mainStickerView.getCenterX(), mainStickerView.getCenterY());
        float totalDistancePathMeasure = LansongPathMeasure.getLength();
        float[] pos = new float[2];
        float[] tan = new float[2];
        float[] pos2 = new float[2];
        float[] tan2 = new float[2];
        //第一个参数为总时长
        animationLinearInterpolator = new AnimationLinearInterpolator(8000, (progress, isDone) -> {
            //progress  0-1
            if (progress <= 0.35) {
                //放大动画  *4.16 意味着0-2.24可以看做0-1完整来看
                float x = progress * 2.85f;
                subStickerView.toScale(x, mScale, isDone);
                mainStickerView.toScale(x, mScale, isDone);
                subStickerView.toTranMoveXY(mainStickerView.getCenterX(),mainStickerView.getCenterY());
                mainStickerView.toTranMoveXY(mainStickerView.getCenterX(),mainStickerView.getCenterY());



            } else {
                //旋转
                float needProgress = progress - 0.35f;
                float x = needProgress * 1.53f;
                float nowDistance = totalDistancePathMeasure * x;
                LansongPathMeasure.getPosTan(nowDistance, pos, tan);
                LansongPathMeasure2.getPosTan(nowDistance, pos2, tan2);
                mainStickerView.toTranMoveXY(pos[0], pos[1]);
                subStickerView.toTranMoveXY(pos2[0], pos2[1]);
            }
        });
        animationLinearInterpolator.PlayAnimation();
    }


    private PathMeasure LansongPathMeasure;
    private PathMeasure LansongPathMeasure2;


    @Override
    public void StopAnim() {
        if (animationLinearInterpolator != null) {
            animationLinearInterpolator.endTimer();
            resetAnimState(mainStickerView);
        }
    }


    //--------------------------------适配蓝松---------------------------------------


    private float lansongTotalDistancePathMeasure;
    private final ArrayList<Float> listForScale = new ArrayList<>();
    private final ArrayList<TransplationPos> listForTranslaptionPosition = new ArrayList<>();
    private Layer mainLayer;
    float[] lansongPos = new float[2];
    float[] lansongTan = new float[2];
    float[] lansongPos2 = new float[2];
    float[] lansongtan2 = new float[2];


    public void initToChangeSubLayer(Layer mainLayer, @NotNull ArrayList<SubLayer> listForSubLayer, LayerAnimCallback callback, float percentage) {
        this.mainLayer = mainLayer;
        LansongPathMeasure = setPathMeasureOne(mainLayer.getScaleHeight(), mainLayer.getPositionX(), mainLayer.getPositionY());
        LansongPathMeasure2 = setPathMeasureTwo(mainLayer.getScaleHeight(), mainLayer.getPositionX(), mainLayer.getPositionY());
        lansongTotalDistancePathMeasure = LansongPathMeasure.getLength();
        toChangeSubLayer(listForSubLayer, callback, percentage);
    }


    public void toChangeSubLayer(ArrayList<SubLayer> listForSubLayer, LayerAnimCallback callback, float percentage) {
        getLansongTranslation(callback, percentage, listForSubLayer);
    }


    void getLansongTranslation(LayerAnimCallback callback, float percentage, ArrayList<SubLayer> listForSubLayer) {
        listForScale.clear();
        listForTranslaptionPosition.clear();
        int paddingW = mainLayer.getPadWidth();
        int paddingH = mainLayer.getPadHeight();
        AnimationLinearInterpolator animationLinearInterpolator = new AnimationLinearInterpolator(8000, (progress, isDone) -> {
            //主图层应该走的位置
            if (progress <= 0.35) {
                //放大动画  *4.16 意味着0-2.24可以看做0-1完整来看
                float x = progress * 2.85f*0.5f;
                listForScale.add(x);
                listForScale.add(x);
                callback.scale(listForScale);
                TransplationPos newTransplationPos = new TransplationPos();
                newTransplationPos.setToX(mainLayer.getPositionX()/paddingW);
                newTransplationPos.setToY(mainLayer.getPositionY()/paddingH);
                listForTranslaptionPosition.add(newTransplationPos);
                listForTranslaptionPosition.add(newTransplationPos);
            } else {
                float needProgress = progress - 0.35f;
                float x = needProgress * 1.53f;
                float nowDistance = lansongTotalDistancePathMeasure * x;
                LansongPathMeasure.getPosTan(nowDistance, lansongPos, lansongTan);
                LansongPathMeasure2.getPosTan(nowDistance, lansongPos2, lansongtan2);
                TransplationPos newTransplationPos = new TransplationPos();
                newTransplationPos.setToX((lansongPos[0]) / paddingW);
                newTransplationPos.setToY((lansongPos[1] / paddingH));
                TransplationPos newTransplationPos2 = new TransplationPos();
                newTransplationPos2.setToX((lansongPos2[0]) / paddingW);
                newTransplationPos2.setToY((lansongPos2[1] / paddingH));
                listForTranslaptionPosition.add(newTransplationPos);
                listForTranslaptionPosition.add(newTransplationPos2);
            }
            callback.translationalXY(listForTranslaptionPosition);
        });


        animationLinearInterpolator.PlayAnimationNoTimer(percentage);
    }


    /**
     * description ：路径动画
     * creation date: 2020/5/28
     * layerH 自身的高
     * user : zhangtongju
     */
    private PathMeasure setPathMeasureOne(float halfWidth, float centerX, float centerY) {
        Path mAnimPath = new Path();
        mAnimPath.moveTo(centerX, centerY);
        mAnimPath.lineTo(centerX - halfWidth, centerY);
        mAnimPath.lineTo(centerX, centerY + halfWidth * 2);
        mAnimPath.lineTo(centerX + halfWidth, centerY);
        mAnimPath.lineTo(centerX , centerY);
        PathMeasure mPathMeasure = new PathMeasure();
        mPathMeasure.setPath(mAnimPath, true);
        return mPathMeasure;
    }


    private PathMeasure setPathMeasureTwo(float halfWidth, float centerX, float centerY) {
        Path mAnimPath = new Path();
        mAnimPath.moveTo(centerX, centerY);
        mAnimPath.lineTo(centerX + halfWidth, centerY);
        mAnimPath.lineTo(centerX, centerY - halfWidth * 2);
        mAnimPath.lineTo(centerX - halfWidth, centerY);
        mAnimPath.lineTo(centerX , centerY);
        PathMeasure mPathMeasure = new PathMeasure();
        mPathMeasure.setPath(mAnimPath, true);
        return mPathMeasure;
    }


}

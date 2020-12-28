package com.flyingeffects.com.view.animations.CustomMove;

import android.graphics.Path;
import android.graphics.PathMeasure;

import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.view.StickerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * description ：超级桃心
 * creation date: 2020/5/25
 * user : zhangtongju
 */

public class SuperLoveAnim extends baseAnimModel {

    private StickerView mainStickerView;
    private PathMeasure LansongPathMeasure;
    private PathMeasure LansongPathMeasure2;
    private int maxHeight;
    private int maxWidth;
    private int subLayerSize;
    /**
     * 当前位置在桃心的点
     */
    private final ArrayList<float[]> pointList = new ArrayList<>();
    private final ArrayList<Integer> randomHeightList = new ArrayList<>();
    private final ArrayList<Integer> randomWidthList = new ArrayList<>();
    private final ArrayList<PathMeasure> listForMeasure = new ArrayList<>();


    void toChangeStickerView(StickerView mainStickerView, List<StickerView> subLayer) {
        this.mainStickerView = mainStickerView;
        if (subLayer != null) {
            subLayerSize = subLayer.size();
        }
        pointList.clear();
        maxWidth = mainStickerView.getWidth();
        maxHeight = mainStickerView.getHeight();
        randomPoint();
        setRotate(mainStickerView.getRotateAngle());
        setOriginal(mainStickerView.getCenterX(), mainStickerView.getCenterY());

        float[] tan = new float[2];
        float[] tan2 = new float[2];
        float[] LanSongPos = new float[2];
        float[] LanSongTan = new float[2];
        LansongPathMeasure = setPathMeasureOne(mainStickerView.getWidth(), mainStickerView.getHeight());
        LansongPathMeasure2 = setPathMeasureTwo(mainStickerView.getWidth(), mainStickerView.getHeight());
        float totalDistancePathMeasure = LansongPathMeasure.getLength();
        float perDistance = totalDistancePathMeasure / (float) 14;
        for (int i = 0; i < subLayer.size(); i++) {
            if (i < 14) {
                StickerView sub = subLayer.get(i);
                if (sub != null) {
                    float needDistance = perDistance * i;
                    if (needDistance > totalDistancePathMeasure) {
                        needDistance = needDistance - totalDistancePathMeasure;
                    }
                    float[] pos = new float[2];
                    LansongPathMeasure.getPosTan(needDistance, pos, tan);
                    sub.toTranMoveXY(pos[0], pos[1]);
                    pointList.add(pos);
                }
            } else {
                StickerView sub = subLayer.get(i);
                if (sub != null) {
                    float needDistance = perDistance * i;
                    if (needDistance > totalDistancePathMeasure) {
                        needDistance = needDistance - totalDistancePathMeasure;
                    }
                    float[] pos2 = new float[2];
                    LansongPathMeasure2.getPosTan(needDistance, pos2, tan2);
                    sub.toTranMoveXY(pos2[0], pos2[1]);
                    pointList.add(pos2);
                }
            }
        }

        for (int i = 0; i < pointList.size(); i++) {
            LogUtil.d("OOM5", "pointList.size()=" + pointList.size());
            float[] data = new float[2];
            data[0] = randomWidthList.get(i);
            data[1] = randomHeightList.get(i);
            listForMeasure.add(setPathMeasureLine(data, pointList.get(i)));
        }


        //第一个参数为总时长
        animationLinearInterpolator = new AnimationLinearInterpolator(2000, (progress, isDone) -> {
            for (int i = 0; i < listForMeasure.size(); i++) {
                PathMeasure pathMeasure = listForMeasure.get(i);
                float TotalDistance = pathMeasure.getLength();
                float nowDistance = TotalDistance * progress;
                pathMeasure.getPosTan(nowDistance, LanSongPos, LanSongTan);
                StickerView stickerView = subLayer.get(i);
                stickerView.toTranMoveXY(LanSongPos[0], LanSongPos[1]);
                if (i == listForMeasure.size() - 1) {
                    mainStickerView.toTranMoveXY(LanSongPos[0], LanSongPos[1]);
                }
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
     * description ：路径动画 ,左边桃心
     * creation date: 2020/5/28
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

    /**
     * description ：路径动画 ,右边桃心
     * creation date: 2020/5/28
     * user : zhangtongju
     */
    private PathMeasure setPathMeasureTwo(float width, float height) {
        Path mAnimPath = new Path();
        mAnimPath.moveTo(width / 2, height / 4);
        mAnimPath.cubicTo(width / 7, height / 9, width / 13, (height * 2) / 5, width / 2, (height * 7) / 12);
        PathMeasure mPathMeasure = new PathMeasure();
        mPathMeasure.setPath(mAnimPath, false);
        return mPathMeasure;
    }


    /**
     * description ：连接线，随机数和桃心的连接线
     * creation date: 2020/12/28
     * user : zhangtongju
     */
    private PathMeasure setPathMeasureLine(float[] start, float[] end) {
        Path mAnimPath = new Path();
        mAnimPath.moveTo(start[0], start[1]);
        mAnimPath.lineTo(end[0], end[1]);
        PathMeasure mPathMeasure = new PathMeasure();
        mPathMeasure.setPath(mAnimPath, false);
        return mPathMeasure;
    }


    /**
     * description ：获取到随机
     * creation date: 2020/12/28
     * user : zhangtongju
     */
    private void randomPoint() {
        Random random = new Random();
        for (int i = 0; i < subLayerSize; i++) {
            int randomHeight = random.nextInt(maxHeight);
            randomHeightList.add(randomHeight);
            int randomWidth = random.nextInt(maxWidth);
            randomWidthList.add(randomWidth);
        }
    }


}

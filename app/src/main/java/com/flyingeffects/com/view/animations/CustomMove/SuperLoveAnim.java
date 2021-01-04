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

        if(subLayer==null||subLayer.size()==0){
            return;
        }

        this.mainStickerView = mainStickerView;
            subLayerSize = subLayer.size();
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


    //--------------------------------适配蓝松---------------------------------------


    private Layer mainLayer;
    private final ArrayList<TransplationPos> listForTranslaptionPosition = new ArrayList<>();
    float[] LanSongPos = new float[2];
    float[] LanSongTan = new float[2];


    public void initToChangeSubLayer(Layer mainLayer, ArrayList<SubLayer> listForSubLayer, LayerAnimCallback callback, float percentage) {
        this.mainLayer = mainLayer;
        subLayerSize = listForSubLayer.size();
        randomHeightList.clear();
        randomWidthList.clear();
        pointList.clear();
        maxWidth = mainLayer.getPadWidth();
        maxHeight = mainLayer.getPadHeight();
        randomPoint();
        LansongPathMeasure = setPathMeasureOne(mainLayer.getPadWidth(), mainLayer.getPadHeight());
        LansongPathMeasure2 = setPathMeasureTwo(mainLayer.getPadWidth(), mainLayer.getPadHeight());
        float totalDistancePathMeasure = LansongPathMeasure.getLength();
        float perDistance = totalDistancePathMeasure / (float) 14;
        for (int i = 0; i < subLayerSize; i++) {
            if (i < 14) {
                SubLayer sub = listForSubLayer.get(i);
                if (sub != null) {
                    float needDistance = perDistance * i;
                    if (needDistance > totalDistancePathMeasure) {
                        needDistance = needDistance - totalDistancePathMeasure;
                    }
                    float[] pos = new float[2];
                    LansongPathMeasure.getPosTan(needDistance, pos, LanSongTan);
                    TransplationPos newTransplationPos = new TransplationPos();
                    newTransplationPos.setToX(pos[0] / maxWidth);
                    newTransplationPos.setToY(pos[1] / maxHeight);
                    listForTranslaptionPosition.add(newTransplationPos);
                    pointList.add(pos);
                    if (i == 0) {
                        listForTranslaptionPosition.add(newTransplationPos);
                    }
                }
            } else {
                SubLayer sub = listForSubLayer.get(i);
                if (sub != null) {
                    float needDistance = perDistance * i;
                    if (needDistance > totalDistancePathMeasure) {
                        needDistance = needDistance - totalDistancePathMeasure;
                    }
                    float[] pos2 = new float[2];
                    LansongPathMeasure2.getPosTan(needDistance, pos2, LanSongTan);
                    TransplationPos newTransplationPos = new TransplationPos();
                    newTransplationPos.setToX(pos2[0] / maxWidth);
                    newTransplationPos.setToY(pos2[1] / maxHeight);
                    listForTranslaptionPosition.add(newTransplationPos);
                    pointList.add(pos2);
                }
            }
        }
        callback.translationalXY(listForTranslaptionPosition);
        for (int i = 0; i < pointList.size(); i++) {
            LogUtil.d("OOM5", "pointList.size()=" + pointList.size());
            float[] data = new float[2];
            data[0] = randomWidthList.get(i);
            data[1] = randomHeightList.get(i);
            listForMeasure.add(setPathMeasureLine(data, pointList.get(i)));
        }
        toChangeSubLayer(listForSubLayer, callback, percentage);
    }


    public void toChangeSubLayer(ArrayList<SubLayer> listForSubLayer, LayerAnimCallback callback, float percentage) {
        getLansongTranslation(callback, percentage, listForSubLayer);
        LogUtil.d("translationalXY", "当前的事件为percentage=" + percentage);
    }


    void getLansongTranslation(LayerAnimCallback callback, float percentage, ArrayList<SubLayer> listForSubLayer) {
        listForTranslaptionPosition.clear();
        AnimationLinearInterpolator animationLinearInterpolator = new AnimationLinearInterpolator(2000, (progress, isDone) -> {
            for (int i = 0; i < listForMeasure.size(); i++) {
                PathMeasure pathMeasure = listForMeasure.get(i);
                float TotalDistance = pathMeasure.getLength();
                float nowDistance = TotalDistance * progress;
                pathMeasure.getPosTan(nowDistance, LanSongPos, LanSongTan);
                TransplationPos newTransplationPos = new TransplationPos();
                newTransplationPos.setToX(LanSongPos[0] / mainLayer.getPadWidth());
                newTransplationPos.setToY(LanSongPos[1] / mainLayer.getPadHeight());
                listForTranslaptionPosition.add(newTransplationPos);
                if (i == listForMeasure.size() - 1) {
                    listForTranslaptionPosition.add(newTransplationPos);
                }
            }
            callback.translationalXY(listForTranslaptionPosition);
        });
        animationLinearInterpolator.PlayAnimationNoTimer(percentage);
        callback.translationalXY(listForTranslaptionPosition);
    }


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

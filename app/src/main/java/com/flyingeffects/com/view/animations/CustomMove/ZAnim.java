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
 * description ：Z路径动画
 * creation date: 2020/12/23
 * user : zhangtongju
 */

public class ZAnim extends baseAnimModel {

    private StickerView mainStickerView;
    private boolean hasMainStickerAnim=false;

    void toChangeStickerView(StickerView mainStickerView, List<StickerView> subLayer) {
        ArrayList<StickerView> listAllSticker = new ArrayList<>();
        listAllSticker.addAll(subLayer);
        this.mainStickerView = mainStickerView;
        setRotate(mainStickerView.getRotateAngle());
        setOriginal(mainStickerView.getCenterX(), mainStickerView.getCenterY());
        float[] pos = new float[2];
        float[] tan = new float[2];
        PathMeasure mPathMeasure = setPathMeasure(mainStickerView.getmHelpBoxRectH(), mainStickerView.getMBoxCenterX(), mainStickerView.getMBoxCenterY());
        float totalDistancePathMeasure = mPathMeasure.getLength();
        //第一个参数为总时长
        animationLinearInterpolator = new AnimationLinearInterpolator(3000, (progress, isDone) -> {
            //主图层应该走的位置
            float nowDistance = totalDistancePathMeasure * progress;
            mPathMeasure.getPosTan(nowDistance, pos, tan);
            if(!hasMainStickerAnim){
                mainStickerView.toTranMoveXY(pos[0],  pos[1]);
                hasMainStickerAnim=true;
            }
            int x = (int) (progress * 15);
            LogUtil.d("OOM5", "x==" + x);
            if (x > 15) {
                x = 15;
            }
            int flashback = 15 - x;
            if (listAllSticker.size() > flashback) {
                StickerView subNowChoose = listAllSticker.get(flashback);
                if (subNowChoose != null) {
                    listAllSticker.remove(flashback);
                }
            }

            for (int i = 0; i < listAllSticker.size(); i++) {
                StickerView sub = listAllSticker.get(i);
                if (sub != null) {
                    sub.toTranMoveXY(pos[0], pos[1]);
                }
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
        perDistance = lansongTotalDistancePathMeasure / (float) 15;
        getLansongTranslation(callback, percentage, listForSubLayer);
        LogUtil.d("translationalXY", "当前的事件为percentage=" + percentage);
    }


    void getLansongTranslation(LayerAnimCallback callback, float percentage, ArrayList<SubLayer> listForSubLayer) {
        ArrayList<StickerView> listAllSticker = new ArrayList<>();
        listForTranslaptionPosition.clear();
        AnimationLinearInterpolator animationLinearInterpolator = new AnimationLinearInterpolator(3000, (progress, isDone) -> {
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
                callback.translationalXY(listForTranslaptionPosition);
            }
        });
        animationLinearInterpolator.SetCirculation(false);
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
        Path mAnimPath = new Path();
        drawStar(mAnimPath, layerH, layerCenterX, layerCenterY);
        PathMeasure mPathMeasure = new PathMeasure();
        mPathMeasure.setPath(mAnimPath, false);
        return mPathMeasure;
    }


    /**
     * description ：Z
     * creation date: 2020/12/23
     * <p>
     * user : zhangtongju
     */
    public void drawStar(Path cxt, float layerH, float layerCenterX, float layerCenterY) {
        //path默认开始点为（0，0），所以要先移动到第一个点上
        float diameter = layerH / 3 * 2;
        cxt.moveTo(layerCenterX - layerH, layerCenterY - (layerH * 2)); //改变接下来操作的起点位置为（x,y）
        cxt.lineTo(layerCenterX + layerH, layerCenterY - (layerH * 2));
        cxt.lineTo(layerCenterX - layerH, layerCenterY + (layerH * 2));
        cxt.lineTo(layerCenterX + layerH, layerCenterY + (layerH * 2));
//        cxt.close();//闭合path，如果path的终点和起始点不是同一个点的话，close()连接这两个点，形成一个封闭的图形
    }


}

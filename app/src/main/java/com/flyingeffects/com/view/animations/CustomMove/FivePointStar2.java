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
 * description ：五角星路径动画停
 * creation date: 2020/12/23
 * user : zhangtongju
 */

public class FivePointStar2 extends baseAnimModel {

    private StickerView mainStickerView;


    void toChangeStickerView(StickerView mainStickerView, List<StickerView> subLayer) {
        this.mainStickerView = mainStickerView;
        setRotate(mainStickerView.getRotateAngle());
        setOriginal(mainStickerView.getCenterX(), mainStickerView.getCenterY());
        float[] pos = new float[2];
        float[] tan = new float[2];
        PathMeasure mPathMeasure = setPathMeasure(mainStickerView.getmHelpBoxRectH(), mainStickerView.getMBoxCenterX(), mainStickerView.getMBoxCenterY());
        float totalDistancePathMeasure = mPathMeasure.getLength();
        float perDistance = totalDistancePathMeasure / (float) 20;
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
        perDistance = lansongTotalDistancePathMeasure / (float) 20;
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
        Path mAnimPath = new Path();
        float haltX=layerCenterX/(float)2;
        float haltY=layerCenterY/(float)2;
        drawStar(mAnimPath,layerCenterX,layerCenterY,haltX-10,haltX*2-10,0);
        PathMeasure mPathMeasure = new PathMeasure();
        mPathMeasure.setPath(mAnimPath, true);
        return mPathMeasure;
    }



    /**
     * description ：或者五角星
     * creation date: 2020/12/23
     *      *cxt:画笔
     *       *x,y:圆心坐标
     *       *r:小圆半径
     *       *R:大圆半径
     *       *rot:旋转角度
     * user : zhangtongju
     */
    public void drawStar(Path cxt, float x, float y, float r, float R, float rot){
        //path默认开始点为（0，0），所以要先移动到第一个点上
        cxt.moveTo((float) Math.cos( (18-rot)/180 * Math.PI) * R + x,(float) -Math.sin( (18-rot)/180 * Math.PI) * R + y); //改变接下来操作的起点位置为（x,y）
        for(int i = 0; i < 5; i ++){
            //R：外圆半径
            float f=(float) Math.cos( (18 + i*72 - rot)/180 * Math.PI) * R + x;//Math.cos余弦，返回值在 -1.0 到 1.0 之间；
            float f1=(float) -Math.sin( (18 + i*72 - rot)/180 * Math.PI) * R + y;//Math.sin正弦，返回值在 -1.0 到 1.0 之间；
            cxt.lineTo(f,f1 );

            //r:内圆半径
            float f2=(float) Math.cos( (54 + i*72 - rot)/180 * Math.PI) * r + x;
            float f3=(float) -Math.sin( (54 + i*72 - rot)/180 * Math.PI) * r + y;
            LogUtil.d(f+"   ,"+f1+"","      -" +f2+"   ,"+f3+"");
            cxt.lineTo(f2,f3) ;
        }
        cxt.close();//闭合path，如果path的终点和起始点不是同一个点的话，close()连接这两个点，形成一个封闭的图形
    }




}

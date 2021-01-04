package com.flyingeffects.com.view.animations.CustomMove;

import android.graphics.Path;
import android.graphics.PathMeasure;

import com.flyingeffects.com.enity.TransplationPos;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.view.StickerView;
import com.lansosdk.box.Layer;
import com.lansosdk.box.SubLayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


/**
 * description ：五角星路径动画停
 * creation date: 2020/12/23
 * user : zhangtongju
 */

public class FivePointStar2 extends baseAnimModel {

    private StickerView mainStickerView;


    void toChangeStickerView(StickerView mainStickerView, List<StickerView> subLayer) {
        if(subLayer==null||subLayer.size()==0){
            return;
        }
        ArrayList<StickerView> listAllSticker = new ArrayList<>();
        listAllSticker.addAll(subLayer);
        this.mainStickerView = mainStickerView;
        setRotate(mainStickerView.getRotateAngle());
        setOriginal(mainStickerView.getCenterX(), mainStickerView.getCenterY());
        float[] pos = new float[2];
        float[] tan = new float[2];
        PathMeasure mPathMeasure = setPathMeasure(mainStickerView.getmHelpBoxRectH(), mainStickerView.getMBoxCenterX(), mainStickerView.getMBoxCenterY());
        float totalDistancePathMeasure = mPathMeasure.getLength();
//        float perDistance = totalDistancePathMeasure / (float) 20;
        //第一个参数为总时长
        animationLinearInterpolator = new AnimationLinearInterpolator(3000, (progress, isDone) -> {
            //主图层应该走的位置
            float nowDistance = totalDistancePathMeasure * progress;
            mPathMeasure.getPosTan(nowDistance, pos, tan);
            mainStickerView.toTranMoveXY(pos[0], pos[1]);


            int x = (int) (progress * 20);
            LogUtil.d("OOM5", "x==" + x);
            if (x > 20) {
                x = 20;
            }
            int flashback = 20 - x;
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
        float haltX = layerCenterX / (float) 2;
        float haltY = layerCenterY / (float) 2;
        drawStar(mAnimPath, layerCenterX, layerCenterY, haltX - 10, haltX * 2 - 10, 0);
        PathMeasure mPathMeasure = new PathMeasure();
        mPathMeasure.setPath(mAnimPath, true);
        return mPathMeasure;
    }


    /**
     * description ：或者五角星
     * creation date: 2020/12/23
     * *cxt:画笔
     * *x,y:圆心坐标
     * *r:小圆半径
     * *R:大圆半径
     * *rot:旋转角度
     * user : zhangtongju
     */
    public void drawStar(Path cxt, float x, float y, float r, float R, float rot) {
        //path默认开始点为（0，0），所以要先移动到第一个点上
        cxt.moveTo((float) Math.cos((18 - rot) / 180 * Math.PI) * R + x, (float) -Math.sin((18 - rot) / 180 * Math.PI) * R + y); //改变接下来操作的起点位置为（x,y）
        for (int i = 0; i < 5; i++) {
            //R：外圆半径
            float f = (float) Math.cos((18 + i * 72 - rot) / 180 * Math.PI) * R + x;//Math.cos余弦，返回值在 -1.0 到 1.0 之间；
            float f1 = (float) -Math.sin((18 + i * 72 - rot) / 180 * Math.PI) * R + y;//Math.sin正弦，返回值在 -1.0 到 1.0 之间；
            cxt.lineTo(f, f1);

            //r:内圆半径
            float f2 = (float) Math.cos((54 + i * 72 - rot) / 180 * Math.PI) * r + x;
            float f3 = (float) -Math.sin((54 + i * 72 - rot) / 180 * Math.PI) * r + y;
            LogUtil.d(f + "   ," + f1 + "", "      -" + f2 + "   ," + f3 + "");
            cxt.lineTo(f2, f3);
        }
        cxt.close();//闭合path，如果path的终点和起始点不是同一个点的话，close()连接这两个点，形成一个封闭的图形
    }



    //--------------------------------适配蓝松---------------------------------------


    private PathMeasure LansongPathMeasure;
    private float lansongTotalDistancePathMeasure;
    private float[] LanSongPos;
    private float[] LanSongTan;
    private Layer mainLayer;
    private ArrayList<TransplationPos> listForTranslaptionPosition = new ArrayList<>();
    //保存不需要走的点
    private HashMap<Integer, float[]> hashMap = new HashMap<>();
//    private int LastFlashBack = 9;

    public void initToChangeSubLayer(Layer mainLayer, ArrayList<SubLayer> listForSubLayer, LayerAnimCallback callback, float percentage) {
        hashMap.clear();
        LanSongPos = new float[2];
        LanSongTan = new float[2];
        this.mainLayer = mainLayer;
        LansongPathMeasure = setPathMeasure(mainLayer.getScaleHeight(), mainLayer.getPositionX(), mainLayer.getPositionY());
        lansongTotalDistancePathMeasure = LansongPathMeasure.getLength();
        toChangeSubLayer(listForSubLayer, callback, percentage);
    }


    void toChangeSubLayer(ArrayList<SubLayer> listForSubLayer, LayerAnimCallback callback, float percentage) {
        listForTranslaptionPosition.clear();
        getLansongTranslation(callback, percentage, listForSubLayer);
    }


    void getLansongTranslation(LayerAnimCallback callback, float percentage, ArrayList<SubLayer> listForSubLayer) {
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

                ///-------
                int interProgress = (int) (progress * 20);
                if (interProgress > 20) {
                    interProgress = 20;
                }
                int flashBack = 20 - interProgress;


                for (int i = 0; i < listForSubLayer.size(); i++) {
                    SubLayer sub = listForSubLayer.get(i);
                    if (sub != null) {
                        TransplationPos newTransplationPos = new TransplationPos();
                        if (hashMap.get(i) == null) {
                            LansongPathMeasure.getPosTan(nowDistance, LanSongPos, LanSongTan);
                            float toX = LanSongPos[0] / mainLayer.getPadWidth();
                            float toY = LanSongPos[1] / mainLayer.getPadHeight();
                            newTransplationPos.setToX(toX);
                            newTransplationPos.setToY(toY);
                            listForTranslaptionPosition.add(newTransplationPos);
                            if (listForSubLayer.size() > flashBack && flashBack == i) {
                                LogUtil.d("OOM5", "保存的点为" + "flashBack=" + flashBack + "点===toX:" + toX + "点===toY：" + toY);
                                hashMap.put(flashBack, new float[]{toX, toY});
                            }
                        } else {
                            float data[] = hashMap.get(i);
                            LogUtil.d("OOM55", "得到保存的点为X" + data[0] + "YY==" + data[1]);
                            newTransplationPos.setToX(Objects.requireNonNull(data[0]));
                            newTransplationPos.setToY(Objects.requireNonNull(data[1]));
                            listForTranslaptionPosition.add(newTransplationPos);

                        }
                    }
                }
                callback.translationalXY(listForTranslaptionPosition);
            }
        });
        animationLinearInterpolator.PlayAnimationNoTimer(percentage);
    }



}

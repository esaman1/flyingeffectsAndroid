package com.flyingeffects.com.view.animations.CustomMove;

import android.graphics.Path;
import android.graphics.PathMeasure;

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

public class ItemEightBorther extends baseAnimModel{

    private AnimationLinearInterpolator animationLinearInterpolator;
    private StickerView mainStickerView;




    public void toChangeStickerView(StickerView mainStickerView, List<StickerView> subLayer) {
        this.mainStickerView=mainStickerView;
        setOriginal(mainStickerView.getCenterX(),mainStickerView.getCenterY());
        float[] pos = new float[2];
        float[] tan = new float[2];
        PathMeasure mPathMeasure =   setPathMeasure(mainStickerView.getmHelpBoxRectH(),mainStickerView.getMBoxCenterX(),mainStickerView.getMBoxCenterY());
        //总长度
        float totalDistancePathMeasure = mPathMeasure.getLength();
        float perDistance = totalDistancePathMeasure / (float) 12;
        //第一个参数为总时长
        animationLinearInterpolator = new AnimationLinearInterpolator(10000, (progress, isDone) -> {
            //主图层应该走的位置
            float nowDistance = totalDistancePathMeasure * progress;
            mPathMeasure.getPosTan(nowDistance, pos, tan);
            mainStickerView.toTranMoveXY(pos[0], pos[1]);
            for (int i=0;i<subLayer.size();i++){
                StickerView sub=subLayer.get(i);
                if(sub!=null){
                    float needDistance = perDistance*i + nowDistance;
                    if (needDistance > totalDistancePathMeasure) {
                        needDistance = needDistance - totalDistancePathMeasure;
                    }
                    mPathMeasure.getPosTan(needDistance, pos, tan);
                    sub.toTranMoveXY(pos[0], pos[1]);
                }
            }


        });
        animationLinearInterpolator.PlayAnimation();
    }


    public void toChangeSubLayer(Layer mainStickerView, ArrayList<SubLayer> listForSubLayer,LayerAnimCallback callback,float percentage) {
        float[] pos = new float[2];
        float[] tan = new float[2];
        PathMeasure mPathMeasure =   setPathMeasure(mainStickerView.getLayerHeight(),mainStickerView.getPositionX(),mainStickerView.getPositionY());
        //总长度
        float totalDistancePathMeasure = mPathMeasure.getLength();
        float perDistance = totalDistancePathMeasure / (float) 12;
        //第一个参数为总时长
        animationLinearInterpolator = new AnimationLinearInterpolator(10000, (progress, isDone) -> {
//            //主图层应该走的位置
            float nowDistance = totalDistancePathMeasure * progress;
            mPathMeasure.getPosTan(nowDistance, pos, tan);
            callback.translationalXY(pos[0], pos[1]);
//            for (int i=0;i<listForSubLayer.size();i++){
//                SubLayer sub=listForSubLayer.get(i);
//                if(sub!=null){
//                    float needDistance = perDistance*i + nowDistance;
//                    if (needDistance > totalDistancePathMeasure) {
//                        needDistance = needDistance - totalDistancePathMeasure;
//                    }
//                    mPathMeasure.getPosTan(needDistance, pos, tan);
//                    sub.toTranMoveXY(pos[0], pos[1]);
//                }
//            }


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
    public  PathMeasure setPathMeasure(float layerH, float layerCenterX,float layerCenterY){
        float diameter = layerH / 3 * 2;
        Path   mAnimPath = new Path();
        mAnimPath.moveTo(layerCenterX, layerCenterY - diameter * 2);
        mAnimPath.rQuadTo(-diameter * 2, diameter, 0, diameter * 2);
        mAnimPath.rQuadTo(diameter * 2, diameter, 0, diameter * 2);
        mAnimPath.rQuadTo(-diameter * 2, -diameter, 0, -diameter * 2);
        mAnimPath.rQuadTo(diameter * 2, -diameter, 0, -diameter * 2);
        PathMeasure  mPathMeasure = new PathMeasure();
        mPathMeasure.setPath(mAnimPath, true);
        return  mPathMeasure;
    }




}

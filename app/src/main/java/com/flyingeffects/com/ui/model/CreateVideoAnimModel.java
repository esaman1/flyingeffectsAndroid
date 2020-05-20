package com.flyingeffects.com.ui.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import com.flyingeffects.com.enity.AllStickerData;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.view.animations.AnimContainer;
import com.lansosdk.box.BitmapLayer;
import com.lansosdk.box.CanvasLayer;
import com.lansosdk.box.DrawPad;
import com.lansosdk.box.DrawPadUpdateMode;
import com.lansosdk.box.onDrawPadCompletedListener;
import com.lansosdk.videoeditor.DrawPadView2;

/**
 * description ：自定义动画集成
 * creation date: 2020/5/20
 * user : zhangtongju
 */
public class CreateVideoAnimModel {

    private static final int DRAWPADWIDTH = 720;
    private static final int DRAWPADHEIGHT = 1280;
    private static final int FRAME_RATE = 20;
    private int w_Pad;
    private int h_pad;
    private DrawPadView2 drawPadView;
    private Bitmap bitmap;
    private BitmapLayer bpForDrawSingleAnim;
    private AllStickerData stickerData;
    private CanvasLayer canvasLayer;
    private AnimContainer animContainer;
    private float totalTime =5000;
    private float LayerW;
    private  float LayerH;
    private showAnimComplete callback;


    public CreateVideoAnimModel(DrawPadView2 drawPadView) {
        this.drawPadView = drawPadView;
        animContainer = new AnimContainer(0, 0, 0, 0, null, null);
        drawPadView.setUpdateMode(DrawPadUpdateMode.AUTO_FLUSH, FRAME_RATE);
        drawPadView.setOnDrawPadCompletedListener(new DrawPadCompleted());
        drawPadView.setDrawPadSize(DRAWPADWIDTH, DRAWPADHEIGHT, (viewWidth, viewHeight) -> {
        });
    }

    private class DrawPadCompleted implements onDrawPadCompletedListener {

        @Override
        public void onCompleted(DrawPad v) {
            Log.d("onProgress", "进度100%");
            if(callback!=null){
                callback.progress(true,100);
            }
        }
    }


    public void initLayerSingleAnim(AllStickerData stickerData,showAnimComplete callback) {
        this.callback=callback;
        this.stickerData = stickerData;
        if (stickerData.isVideo()) {
            bitmap = videoAddCover.getInstance().getCoverForBitmap(stickerData.getOriginalPath());//todo 可能需要抠图地址
        } else {
            bitmap = BitmapFactory.decodeFile(stickerData.getOriginalPath());
        }
        drawPadView.releaseDrawPad();
        if (drawPadView.setupDrawPad()) {
            drawPadView.pausePreview();
            bpForDrawSingleAnim = drawPadView.addBitmapLayer(bitmap);
            w_Pad = drawPadView.getDrawPadWidth();
            h_pad = drawPadView.getDrawPadHeight();
            LayerW = bpForDrawSingleAnim.getLayerWidth();
            LayerH= bpForDrawSingleAnim.getLayerHeight();
            addBitmapLayer(stickerData, bpForDrawSingleAnim);
            canvasLayer = drawPadView.addCanvasLayer();
            canvasLayer.setScaledToPadSize();
            canvasLayer.addCanvasRunnable((canvasLayer, canvas, currentTime) -> {
                float getCurrentTime = (1f * currentTime / 1000);
                float progress = 1f * getCurrentTime / totalTime;
                startDataDrawSingleAnim(currentTime,progress);
            });
        } else {
            LogUtil.d("OOM", "Drawpad did not setup properly");
        }
        drawPadView.startPreview();
    }


    private void startDataDrawSingleAnim(float currentTime,float progress) {

        animContainer.setPadWidth(w_Pad);
        animContainer.setPadHeight(h_pad);
        animContainer.setCc(list -> {
            animContainer.setLayerWidth(Math.round(LayerW));
            animContainer.setLayerHeight(Math.round(LayerH));
            bpForDrawSingleAnim.setPosition(list.get(0).getCenter().x, list.get(0).getCenter().y);
            bpForDrawSingleAnim.setRotation(list.get(0).getRotate());
            bpForDrawSingleAnim.setScaledValue(list.get(0).getScaleXY().x * LayerW, list.get(0).getScaleXY().y * LayerH);
        });
        animContainer.setPicMatrix(new Matrix());
        animContainer.refreshLSLayers(progress, 1, currentTime, totalTime);
    }







    /**
     * 增加一个图片图层.
     */
    private void addBitmapLayer(AllStickerData stickerItem, BitmapLayer bpLayer) {
        float layerScale = DRAWPADWIDTH / (float) bpLayer.getLayerWidth();
        LogUtil.d("OOM", "图层的缩放为" + layerScale + "");
        float stickerScale = stickerItem.getScale();
        LogUtil.d("OOM", "gif+图层的缩放为" + layerScale * stickerScale + "");
        bpLayer.setScale(layerScale * stickerScale);
        LogUtil.d("OOM", "mvLayerW=" + bpLayer.getLayerWidth() + "");
        LogUtil.d("OOM", "mvLayerpadW=" + bpLayer.getPadWidth() + "");
        int rotate = (int) stickerItem.getRotation();
        if (rotate < 0) {
            rotate = 360 + rotate;
        }
        LogUtil.d("OOM", "rotate=" + rotate);
        bpLayer.setRotate(rotate);
        LogUtil.d("OOM", "Scale=" + stickerItem.getScale() + "");
        //蓝松这边规定，0.5就是刚刚居中的位置
        float percentX = stickerItem.getTranslationX();
//        float posX = (bpLayer.getPadWidth() + bpLayer.getLayerWidth()) * percentX - bpLayer.getLayerWidth() / 2.0f;
        bpLayer.setPosition(bpLayer.getPadWidth() * percentX, bpLayer.getPositionY());
        float percentY = stickerItem.getTranslationy();
        LogUtil.d("OOM", "percentX=" + percentX + "percentY=" + percentY);
        //   float posY = (bpLayer.getPadHeight() + bpLayer.getLayerHeight()) * percentY - bpLayer.getLayerHeight() / 2.0f;
        bpLayer.setPosition(bpLayer.getPositionX(), bpLayer.getPadHeight() * percentY);

    }



    public interface showAnimComplete{
        void progress(boolean isComplete,int progress);

    }


}

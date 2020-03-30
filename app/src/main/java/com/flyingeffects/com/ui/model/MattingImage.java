package com.flyingeffects.com.ui.model;

import android.graphics.Bitmap;

import com.flyingeffects.com.utils.faceUtil.ConUtil;
import com.megvii.segjni.SegJni;


public class MattingImage {

   private  int mBitmapH;
    private int mBitmapW;
    private  Bitmap mOriginBitmap;

    public MattingImage() {
    }



    public void mattingImage(String path,mattingStatus callback){
        new Thread(() -> {
            mOriginBitmap = ConUtil.getImage(path);
            mBitmapW = mOriginBitmap.getWidth();
            mBitmapH = mOriginBitmap.getHeight();
            SegJni.nativeCreateImageBuffer(mBitmapW, mBitmapH);
            byte segs[] = new byte[mBitmapH * mBitmapW];
            byte[] rgba = ConUtil.getPixelsRGBA(mOriginBitmap);
            SegJni.nativeSegImage(rgba, mBitmapW, mBitmapH, segs, false);
            SegJni.nativeReleaseImageBuffer();
            mOriginBitmap = ConUtil.setBitmapAlpha(mOriginBitmap, segs);
            callback.isSuccess(true,mOriginBitmap);
        }).start();


    }



    public interface  mattingStatus{
        void isSuccess(boolean isSuccess, Bitmap bp);
    }





}

/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flyingeffects.com.utils.record;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.record.soundfile.SoundFile;
import com.flyingeffects.com.utils.screenUtil;

import java.util.ArrayList;

/**
 * WaveformView is an Android view that displays a visual representation
 * of an audio waveform.  It retrieves the frame gains from a CheapSoundFile
 * object and recomputes the shape contour at several zoom levels.
 * <p>
 * This class doesn't handle selection or any of the touch interactions
 * directly, so it exposes a listener interface.  The class that embeds
 * this view should add itself as a listener and make the view scroll
 * and respond to other events appropriately.
 * <p>
 * WaveformView doesn't actually handle selection, but it will just display
 * the selected part of the waveform in a different color.
 */
public class WaveformViewForMove extends View {
    public interface WaveformListener {
        public void waveformTouchStart(float x);

        public void waveformTouchMove(float x);

        public void waveformTouchEnd();

        public void waveformFling(float x);

        public void waveformDraw();

        public void waveformZoomIn();

        public void waveformZoomOut();

        public void getClickedTime(ArrayList<Double> time);

    }


    // Colors
    private Paint mGridPaint;
    private Paint mSelectedLinePaint;
    private Paint mUnselectedLinePaint;
    private Paint mUnselectedBkgndLinePaint;
    private Paint mBorderLinePaint;
    private Paint mPlaybackLinePaint;
    private Paint mTimecodePaint;

    private SoundFile mSoundFile;
    private int[] mLenByZoomLevel;
    private double[][] mValuesByZoomLevel;
    private double[] mZoomFactorByZoomLevel;
    private int[] mHeightsAtThisZoomLevel;
    private int mZoomLevel;
    private int mNumZoomLevels;
    private int mSampleRate;
    private int mSamplesPerFrame;
    private int mOffset;
    private int mSelectionStart;
    private int mSelectionEnd;
    private int mPlaybackPos;
    private int halfMeasuredWidth;
    private boolean isFromDrumActivity = false;
    private float mDensity;
    private float mInitialScaleSpan;
    private WaveformListener mListener;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private boolean mInitialized;
    private int measuredWidth;
    private float zoomRatio;
    private int nowChooseModuloPoint;
    private int dp_15;
    //上下边框和绘制音频线条的高度差为布局大小的0.1倍
    private float marginScale=0.1f;
    //灰色背景Paint
    private Paint bgPaint;
    //    private int
    private Double lastChooseItem;
    ArrayList<Double> list_clickedTime = new ArrayList<>();

    public WaveformViewForMove(Context context, AttributeSet attrs) {
        super(context, attrs);
        // We don't want keys, the markers get these
        setFocusable(false);
        Resources res = getResources();
        mGridPaint = new Paint();
        mGridPaint.setAntiAlias(false);
        mGridPaint.setColor(Color.parseColor("#1C1C1C"));
        mSelectedLinePaint = new Paint();
        mSelectedLinePaint.setAntiAlias(false);
        mSelectedLinePaint.setColor(Color.parseColor("#FFFFFF"));
        mUnselectedLinePaint = new Paint();
        mUnselectedLinePaint.setAntiAlias(false);
        mUnselectedLinePaint.setColor(Color.parseColor("#DFDFDF"));
        mUnselectedBkgndLinePaint = new Paint();
        mUnselectedBkgndLinePaint.setAntiAlias(false);
        mUnselectedBkgndLinePaint.setColor(Color.parseColor("#000000"));
        mBorderLinePaint = new Paint();
        mBorderLinePaint.setAntiAlias(true);
        mBorderLinePaint.setStrokeWidth(1.5f);
        mBorderLinePaint.setPathEffect(new DashPathEffect(new float[]{3.0f, 2.0f}, 0.0f));
        mBorderLinePaint.setColor(Color.WHITE);
        mPlaybackLinePaint = new Paint();
        mPlaybackLinePaint.setAntiAlias(false);
        mPlaybackLinePaint.setColor(Color.parseColor("#FEE131"));
        mTimecodePaint = new Paint();
        mTimecodePaint.setTextSize(12);
        mTimecodePaint.setAntiAlias(true);
        mTimecodePaint.setColor(Color.parseColor("#4777EB"));
        mTimecodePaint.setShadowLayer(2, 1, 1, Color.parseColor("#8000F7"));
        bgPaint=new Paint();
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(Color.parseColor("#1C1C1C"));
        mGestureDetector = new GestureDetector(
                context,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float vx, float vy) {
                        mListener.waveformFling(vx);
                        return true;
                    }
                }
        );

        mScaleGestureDetector = new ScaleGestureDetector(
                context,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override
                    public boolean onScaleBegin(ScaleGestureDetector d) {
                        Log.v("Ringdroid", "ScaleBegin " + d.getCurrentSpanX());
                        mInitialScaleSpan = Math.abs(d.getCurrentSpanX());
                        return true;
                    }

                    @Override
                    public boolean onScale(ScaleGestureDetector d) {
                        float scale = Math.abs(d.getCurrentSpanX());
                        Log.v("Ringdroid", "Scale " + (scale - mInitialScaleSpan));
                        if (scale - mInitialScaleSpan > 40) {
                            mListener.waveformZoomIn();
                            mInitialScaleSpan = scale;
                        }
                        if (scale - mInitialScaleSpan < -40) {
                            mListener.waveformZoomOut();
                            mInitialScaleSpan = scale;
                        }
                        return true;
                    }

                    @Override
                    public void onScaleEnd(ScaleGestureDetector d) {
                        Log.v("Ringdroid", "ScaleEnd " + d.getCurrentSpanX());
                    }
                }
        );

        mSoundFile = null;
        mLenByZoomLevel = null;
        mValuesByZoomLevel = null;
        mHeightsAtThisZoomLevel = null;
        mOffset = 0;
        mPlaybackPos = -1;
        mSelectionStart = 0;
        mSelectionEnd = 0;
        mDensity = 1.0f;
        mInitialized = false;
        dp_15 = screenUtil.dip2px(context, 15);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mListener.waveformTouchStart(event.getX());
                break;
            case MotionEvent.ACTION_MOVE:
                mListener.waveformTouchMove(event.getX());
                break;
            case MotionEvent.ACTION_UP:
                mListener.waveformTouchEnd();
                break;
        }
        return true;
    }

    public boolean hasSoundFile() {
        return mSoundFile != null;
    }

    public void setSoundFile(SoundFile soundFile) {
        mSoundFile = soundFile;
        mSampleRate = mSoundFile.getSampleRate();//48000
        mSamplesPerFrame = mSoundFile.getSamplesPerFrame();//1024
        computeDoublesForAllZoomLevels();
        mHeightsAtThisZoomLevel = null;
    }

    public boolean isInitialized() {
        return mInitialized;
    }

    public int getZoomLevel() {
        return mZoomLevel;
    }

    public void setZoomLevel(int zoomLevel) {
        while (mZoomLevel > zoomLevel) {
            zoomIn();
        }
        while (mZoomLevel < zoomLevel) {
            zoomOut();
        }
    }

    public boolean canZoomIn() {
        return (mZoomLevel > 0);
    }

    public void zoomIn() {
        if (canZoomIn()) {
            mZoomLevel--;
            mSelectionStart *= 2;
            mSelectionEnd *= 2;
            mHeightsAtThisZoomLevel = null;
            int offsetCenter = mOffset + getMeasuredWidth() / 2;
            offsetCenter *= 2;
            mOffset = offsetCenter - getMeasuredWidth() / 2;
            if (mOffset < 0) {
                mOffset = 0;
            }
            invalidate();
        }
    }


    public boolean canZoomOut() {
        return (mZoomLevel < mNumZoomLevels - 1);
    }

    public void zoomOut() {
        if (canZoomOut()) {
            mZoomLevel++;
            mSelectionStart /= 2;
            mSelectionEnd /= 2;
            int offsetCenter = mOffset + getMeasuredWidth() / 2;
            offsetCenter /= 2;
            mOffset = offsetCenter - getMeasuredWidth() / 2;
            if (mOffset < 0) {
                mOffset = 0;
            }
            mHeightsAtThisZoomLevel = null;
            invalidate();
        }
    }

    public int maxPos() {  //ztj
        if (zoomRatio != 0) {
            return (int) (mLenByZoomLevel[mZoomLevel] * zoomRatio);
        } else {
            if (getMeasuredWidth() != 0) {
                zoomRatio = getMeasuredWidth() / (float) mLenByZoomLevel[mZoomLevel];
                return (int) (mLenByZoomLevel[mZoomLevel] * zoomRatio);
            }
            return mLenByZoomLevel[mZoomLevel];
        }
    }

    public int secondsToFrames(double seconds) {  //秒到帧
        return (int) (1.0 * seconds * mSampleRate / mSamplesPerFrame + 0.5);
    }

    public int secondsToPixels(double seconds) {  //秒到像素
        double z = mZoomFactorByZoomLevel[mZoomLevel];
        return (int) (z * seconds * mSampleRate / mSamplesPerFrame + 0.5);
    }

    public double pixelsToSeconds(int pixels) {   //，每一帧的时间
        double z = mZoomFactorByZoomLevel[mZoomLevel];
        return (pixels * (double) mSamplesPerFrame / (mSampleRate * z));
    }

    public int millisecsToPixels(int msecs) {  //毫秒到像素
        double z = mZoomFactorByZoomLevel[mZoomLevel];
        return (int) ((msecs * 1.0 * mSampleRate * z) /
                (1000.0 * mSamplesPerFrame) + 0.5);
    }

    public int pixelsToMillisecs(int pixels) { //像素到毫秒
        double z = mZoomFactorByZoomLevel[mZoomLevel];
        return (int) (pixels * (1000.0 * mSamplesPerFrame) /
                (mSampleRate * z) + 0.5);
    }

    public void setParameters(int start, int end, int offset) {
        mSelectionStart = start;
        mSelectionEnd = end;
        mOffset = offset;
    }

    public int getStart() {
        if (zoomRatio != 0) {
            return mSelectionStart = (int) (mSelectionStart / zoomRatio);
        }
        return mSelectionStart;
    }


    public int getNowMeasuredWidth() {


        return measuredWidth;
    }


    public int getEnd() {
        LogUtil.d("mSelectionEnd", mSelectionEnd + "");
        if (zoomRatio != 0) {
            LogUtil.d("mSelectionEnd", (mSelectionEnd / zoomRatio + ""));
            return mSelectionEnd = (int) (mSelectionEnd / zoomRatio);
        }
        return mSelectionEnd;
    }

    public int getOffset() {
        return mOffset;
    }

    public void setPlayback(int pos) {
        LogUtil.d("setPlayback", pos + "");
        mPlaybackPos = pos;


    }

    public void setListener(WaveformListener listener) {
        mListener = listener;
    }

    public void recomputeHeights(float density) {
        mHeightsAtThisZoomLevel = null;
        mDensity = density;
        mTimecodePaint.setTextSize((int) (12 * density));

        invalidate();
    }

    protected void drawWaveformLine(Canvas canvas,
                                    int x, int y0, int y1,
                                    Paint paint) {
        canvas.drawLine(x, y0, x, y1, paint);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mSoundFile == null) {
            return;
        }
        if (mHeightsAtThisZoomLevel == null) {
            computeIntsForThisZoomLevel();
        }
        zoomRatio = 1;
        measuredWidth = getMeasuredWidth();//990
        halfMeasuredWidth = measuredWidth / 2;
        int measuredHeight = getMeasuredHeight();//600
        int start = mOffset;
        int width = mHeightsAtThisZoomLevel.length - start;
        int ctr = measuredHeight / 2; //300
        if (width > measuredWidth) {
            width = measuredWidth;
        }

        //Draw Background
//        canvas.drawLine( //画边框
//                mSelectionStart - mOffset + 0.5f, 30,
//                mSelectionStart - mOffset + 0.5f, measuredHeight,
//                mBorderLinePaint);
//        canvas.drawLine(
//                mSelectionEnd - mOffset + 0.5f, 0,
//                mSelectionEnd - mOffset + 0.5f, measuredHeight - 30,
//                mBorderLinePaint);
        canvas.drawRect(mSelectionStart-mOffset+0.5f,measuredHeight*0.05f,mSelectionEnd-mOffset+0.5f,measuredHeight*0.95f,bgPaint);
        // Draw grid
        double onePixelInSecs = pixelsToSeconds(1); //1024/采样率  获得时间
        double fractionalSecs = mOffset * onePixelInSecs; //分秒数
        int i = 0;

        // Draw waveform
        for (i = 0; i < width; i++) {
            Paint paint;
            if (i * zoomRatio + start >= mSelectionStart &&
                    i * zoomRatio + start < mSelectionEnd) {
                paint = mSelectedLinePaint;
            } else {
                drawWaveformLine(canvas, (int) (i * zoomRatio), 0, measuredHeight,
                        mUnselectedBkgndLinePaint);
                paint = mUnselectedLinePaint;
            }
            for (int x=0;x<mHeightsAtThisZoomLevel.length;x++){
                if (x%5!=0){
                    mHeightsAtThisZoomLevel[x]=0;
                }
            }

            nowChooseModuloPoint = start;
            LogUtil.d("dasdasdas", "nowChooseModuloPoint=" + nowChooseModuloPoint);
            int nowChooseHeight = (nowChooseModuloPoint + i);
//                int nowChooseHeight=nowChooseModuloPoint;
            if (nowChooseHeight <0) {
                drawWaveformLine( //绘制心电图
                        canvas, (int) (i * zoomRatio),
                        0,
                        0,
                        paint);
            } else {
                if (mHeightsAtThisZoomLevel[nowChooseHeight]!=0||nowChooseHeight%5==0){
                    int y0 = ctr - mHeightsAtThisZoomLevel[nowChooseHeight];
                    if (y0 < dp_15) {
                        y0 = dp_15;
                    }
                    int y1 = ctr + 1 + mHeightsAtThisZoomLevel[nowChooseHeight];
                    if (y1 > ctr * 2 - dp_15) {
                        y1 = ctr * 2 - dp_15;
                    }
                    float length=Math.abs(y1-y0);
                    if (length<=1){
                        drawWaveformLine( //绘制心电图
                                canvas, (int) (i * zoomRatio), y0, y1, paint);
                    }else {
                        int offset=(int)(length*marginScale*0.5f);
                        drawWaveformLine(canvas,(int) (i * zoomRatio),y0+offset,y1-offset,paint);
                    }
                }
            }
            for (int num : listForPlaybackPos
            ) {
                if (i + start == num) { //绘制选中的进度条
                    Double chooseItem = onePixelInSecs * num;
                    boolean result = list_clickedTime.contains(chooseItem);
                    if (
                            !result
                    ) {

                        list_clickedTime.add(chooseItem);
                    }
                    lastChooseItem = chooseItem;
                    Path pathForTriangle = new Path();
                    pathForTriangle.moveTo(i * zoomRatio - 10, measuredHeight - 10);// 此点为多边形的起点
                    pathForTriangle.lineTo(i * zoomRatio, measuredHeight - dp_15);
                    pathForTriangle.lineTo(i * zoomRatio + 10, measuredHeight - 10);
                    pathForTriangle.close(); // 使这些点构成封闭的多边形
                    canvas.drawPath(pathForTriangle, mPlaybackLinePaint);
                }
            }

            if (i + start == mPlaybackPos) { //绘制选中的进度条
                canvas.drawLine(i, 0, i, measuredHeight, mPlaybackLinePaint);
//               canvas.drawCircle(i,measuredHeight-20,20,mPlaybackLinePaint);
            }
        }
        mListener.getClickedTime(list_clickedTime);

        // If we can see the right edge of the waveform, draw the
        // non-waveform area to the right as unselected
        for (i = width; i < measuredWidth + (measuredWidth / 2); i++) {   //画右边多出来的背景
            drawWaveformLine(canvas, (int) (i * zoomRatio), 0, measuredHeight,
                    mUnselectedBkgndLinePaint);
        }
        if (mListener != null) {
            mListener.waveformDraw();
        }
    }

    /**
     * Called once when a new sound file is added
     */
    private void computeDoublesForAllZoomLevels() {
        int numFrames = mSoundFile.getNumFrames();//515
        int[] frameGains = mSoundFile.getFrameGains();//515的数组
        double[] smoothedGains = new double[numFrames];
        if (numFrames == 1) {
            smoothedGains[0] = frameGains[0];
        } else if (numFrames == 2) {
            smoothedGains[0] = frameGains[0];
            smoothedGains[1] = frameGains[1];
        } else if (numFrames > 2) {
            smoothedGains[0] = (double) (
                    (frameGains[0] / 2.0) +
                            (frameGains[1] / 2.0));
            for (int i = 1; i < numFrames - 1; i++) {   //每一个都是左右2个值的平均值
                smoothedGains[i] = (double) (
                        (frameGains[i - 1] / 3.0) +
                                (frameGains[i] / 3.0) +
                                (frameGains[i + 1] / 3.0));
            }
            smoothedGains[numFrames - 1] = (double) (
                    (frameGains[numFrames - 2] / 2.0) +
                            (frameGains[numFrames - 1] / 2.0));
        }

        // Make sure the range is no more than 0 - 255
        double maxGain = 1.0;//1.0  遍历获得最大值 41.666
        for (int i = 0; i < numFrames; i++) {
            if (smoothedGains[i] > maxGain) {
                maxGain = smoothedGains[i];
            }
        }
        double scaleFactor = 1.0;
        if (maxGain > 255.0) {
            scaleFactor = 255 / maxGain;
        }

        // Build histogram of 256 bins and figure out the new scaled max
        maxGain = 0;
        int gainHist[] = new int[256];
        for (int i = 0; i < numFrames; i++) {
            int smoothedGain = (int) (smoothedGains[i] * scaleFactor);
            if (smoothedGain < 0) {
                smoothedGain = 0;
            }
            if (smoothedGain > 255) {
                smoothedGain = 255;
            }

            if (smoothedGain > maxGain) {
                maxGain = smoothedGain;
            }

            gainHist[smoothedGain]++; //历史值，里面重复的值出现的次数，肯定在255以内
        }

        // Re-calibrate the min to be 5%
        double minGain = 0;
        int sum = 0;
        while (minGain < 255 && sum < numFrames / 20) {
            sum += gainHist[(int) minGain];
            minGain++;  //低于柱状图高度5/1的数量，20个
        }

        // Re-calibrate the max to be 99%
        sum = 0;
        while (maxGain > 2 && sum < numFrames / 100) {  //去掉最大的5个值
            sum += gainHist[(int) maxGain];
            maxGain--;
        }

        // Compute the heights
        double[] heights = new double[numFrames]; //height 集合
        double range = maxGain - minGain;//18  38  20
        for (int i = 0; i < numFrames; i++) {
            double value = (smoothedGains[i] * scaleFactor - minGain) / range;
            if (value < 0.0) {
                value = 0.0;
            }
            if (value > 1.0) {
                value = 1.0;
            }
            heights[i] = value * value;
        }

        mNumZoomLevels = 5;  //5个缩放比例
        mLenByZoomLevel = new int[5];//放大，缩小的值
        mZoomFactorByZoomLevel = new double[5];  //放大级别
        mValuesByZoomLevel = new double[5][];//里面装了5个宽度，缩放比例的全部集合，具体值

        if (measuredWidth == 0) {
            measuredWidth = getMeasuredWidth();
        }
        float zoomValue = measuredWidth / (float) numFrames;
        if (zoomValue != 0) {

            mLenByZoomLevel[0] = measuredWidth;
            mZoomFactorByZoomLevel[0] = zoomValue;
            mValuesByZoomLevel[0] = new double[mLenByZoomLevel[0]];
            if (numFrames > 0) {
                mValuesByZoomLevel[0][0] = 0.5 * heights[0];
                mValuesByZoomLevel[0][1] = heights[0];
            }

            for (int i = 1; i < measuredWidth; i++) {
                float nowChooseItem = i / (float) measuredWidth * (float) numFrames;
                mValuesByZoomLevel[0][i] = heights[(int) nowChooseItem];
            }
        }


        // Level 1 is normal
        mLenByZoomLevel[1] = numFrames;
        mValuesByZoomLevel[1] = new double[mLenByZoomLevel[1]];
        mZoomFactorByZoomLevel[1] = 1.0;
        for (int i = 0; i < mLenByZoomLevel[1]; i++) {
            mValuesByZoomLevel[1][i] = heights[i];
        }

        // 3 more levels are each halved
        for (int j = 2; j < 5; j++) {
            mLenByZoomLevel[j] = mLenByZoomLevel[j - 1] / 2;
            mValuesByZoomLevel[j] = new double[mLenByZoomLevel[j]];
            mZoomFactorByZoomLevel[j] = mZoomFactorByZoomLevel[j - 1] / 2.0;
            for (int i = 0; i < mLenByZoomLevel[j]; i++) {
                mValuesByZoomLevel[j][i] =
                        0.5 * (mValuesByZoomLevel[j - 1][2 * i] +
                                mValuesByZoomLevel[j - 1][2 * i + 1]);
            }
        }


        int nowWidth = getMeasuredWidth();
        if (nowWidth != 0) {
            for (int i = 0; i < mLenByZoomLevel.length; i++) {
                if (nowWidth >= mLenByZoomLevel[i]) {
                    mZoomLevel = i;
                    break;
                }
            }
        } else {
            if (numFrames > 5000) {
                mZoomLevel = 3;
            } else if (numFrames > 1000) {
                mZoomLevel = 2;
            } else if (numFrames > 300) {
                mZoomLevel = 1;
            } else {
                mZoomLevel = 0;
            }
        }
        //   mZoomLevel = 1;  //永远默认为第一套
        mInitialized = true;
    }

    /**
     * Called the first time we need to draw when the zoom level has changed
     * or the screen is resized
     */
    private void computeIntsForThisZoomLevel() {
        int halfHeight = (getMeasuredHeight() / 2) - 1;  //299
        mHeightsAtThisZoomLevel = new int[mLenByZoomLevel[mZoomLevel]]; //515 2
        for (int i = 0; i < mLenByZoomLevel[mZoomLevel]; i++) {
            mHeightsAtThisZoomLevel[i] =
                    (int) (mValuesByZoomLevel[mZoomLevel][i] * halfHeight);
        }
    }


    private ArrayList<Integer> listForPlaybackPos = new ArrayList<>();

    public void addPoint() {
//        if(listForPlaybackPos!=null&&listForPlaybackPos.size()==0){
//        listForPlaybackPos.add(0);
//        }
        listForPlaybackPos.add(mPlaybackPos);
        invalidate();
    }


    public void removePoint() {
        list_clickedTime.clear();
        listForPlaybackPos.clear();
        invalidate();
    }


    public void isFromDrumActivity(boolean isFromDrumActivity) {
        this.isFromDrumActivity = isFromDrumActivity;
    }

    public boolean checkDotEligible(){
        boolean eligible=true;
        for (int i=0;i<listForPlaybackPos.size();i++){
            int temp=pixelsToMillisecs(listForPlaybackPos.get(i));
            int current=pixelsToMillisecs(mPlaybackPos);
            if (Math.abs(temp-current)<=20){
                eligible=false;
                break;
            }
        }
        return eligible;
    }

}

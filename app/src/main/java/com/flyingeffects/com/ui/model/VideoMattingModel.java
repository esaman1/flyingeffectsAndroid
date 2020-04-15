package com.flyingeffects.com.ui.model;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.commonlyModel.getVideoInfo;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.VideoInfo;
import com.flyingeffects.com.manager.BitmapManager;
import com.flyingeffects.com.manager.DataCleanManager;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.utils.FileUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.glidebitmappool.GlideBitmapPool;
import com.lansosdk.box.BitmapLayer;
import com.lansosdk.box.CanvasLayer;
import com.lansosdk.box.ExtractVideoFrame;
import com.lansosdk.box.LSOBitmapAsset;
import com.lansosdk.videoeditor.DrawPadAllExecute2;
import com.lansosdk.videoeditor.MediaInfo;
import com.megvii.segjni.SegJni;
import com.shixing.sxve.ui.view.WaitingDialogProgressNowAnim;

import java.io.File;
import java.util.List;

/**
 * description ：视频抠图控制类
 * creation date: 2020/3/27
 * user : zhangtongju
 */
public class VideoMattingModel {
    private ExtractVideoFrame mExtractFrame;
    private String videoPath;
    /**
     * 专门用来存储face 抠像的文件夹
     */
    private String faceFolder;
    /**
     * 专门用来存储已经抠图的文件夹
     */
    private String faceMattingFolder;

    /**
     * 专门用来储存已经合成视频的文件夹
     */
    private String cacheCutVideoPath;


    private int frameCount;
    private static final int DRAWPADWIDTH = 720;
    private static final int DRAWPADHEIGHT = 1280;
    private static final int FRAME_RATE = 20;
    private Context context;

    private VideoInfo videoInfo;
    private WaitingDialogProgressNowAnim dialog;


   private  MattingSuccess callback;

    public VideoMattingModel(String videoPath, Context context, MattingSuccess callback) {
        this.callback = callback;
        this.videoPath = videoPath;
        this.context=context;
        videoInfo = getVideoInfo.getInstance().getRingDuring(videoPath);
        FileManager fileManager = new FileManager();
        faceFolder = fileManager.getFileCachePath(BaseApplication.getInstance(), "faceFolder");
        faceMattingFolder = fileManager.getFileCachePath(BaseApplication.getInstance(), "faceMattingFolder");
        cacheCutVideoPath = fileManager.getFileCachePath(BaseApplication.getInstance(), "cacheMattingFolder");
        LogUtil.d("OOM", "faceMattingFolder=" + faceMattingFolder);
        dialog = new WaitingDialogProgressNowAnim(context);
        dialog.openProgressDialog();
    }



    /**
     * description ：把视频读取出全部帧来
     * creation date: 2020/4/14
     * user : zhangtongju
     */
    private int allFrame;

    public void ToExtractFrame() {

        new Thread(() -> {
            MediaInfo mInfo = new MediaInfo(videoPath);
            if (!mInfo.prepare() || !mInfo.isHaveVideo()) {
                return;
            }
            mExtractFrame = new ExtractVideoFrame(BaseApplication.getInstance(), videoPath);
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(videoPath);
            String rotation = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            if (TextUtils.isEmpty(rotation)) {
                if (mInfo.vWidth * mInfo.vHeight > 960 * 540) {
                    mExtractFrame.setBitmapWH(mInfo.vWidth / 2, mInfo.vHeight / 2);
                }
            } else {
                int iRotation = Integer.parseInt(rotation);
                if (iRotation == 90 || iRotation == 180) {
                    if (mInfo.vWidth * mInfo.vHeight > 960 * 540) {
                        mExtractFrame.setBitmapWH(mInfo.vHeight / 2, mInfo.vWidth / 2);
                    }
                } else {
                    if (mInfo.vWidth * mInfo.vHeight > 960 * 540) {
                        mExtractFrame.setBitmapWH(mInfo.vWidth / 2, mInfo.vHeight / 2);
                    }
                }
            }

            allFrame = mInfo.vTotalFrames;
            LogUtil.d("OOM2", "视频的总帧数为" + allFrame);
            //设置提取多少帧
            mExtractFrame.setExtractSomeFrame(allFrame);
            mExtractFrame.setOnExtractCompletedListener(v -> {
                for (int i = 1; i < BaseConstans.THREADCOUNT; i++) {
                    //最后需要补的帧
                    frameCount++;
                    downImageForBitmap(null, frameCount);
                }
                LogUtil.d("OOM2", "frameCount的值为" + frameCount);
                SegJni.nativeReleaseImageBuffer();
                SegJni.nativeReleaseSegHandler();
                addFrameCompoundVideoNoMatting();
            });

            //设置处理进度监听.
            //当前帧的画面回调,, ptsUS:当前帧的时间戳,单位微秒. 拿到图片后,建议放到ArrayList中,不要直接在这里处理.
            mExtractFrame.setOnExtractProgressListener((bmp, ptsUS) -> {
                frameCount++;
                new Handler().post(() -> {
                    progress = (int) ((frameCount / (float) allFrame) * 90);
                    handler.sendEmptyMessage(1);
                });
                LogUtil.d("OOM", frameCount + "帧");
                String fileName = faceFolder + File.separator + frameCount + ".png";
                BitmapManager.getInstance().saveBitmapToPath(bmp, fileName);
                downImageForBitmap(bmp, frameCount);
            });
            frameCount = 0;
            //开始执行. 或者你可以从指定地方开始解码.mExtractFrame.start(10*1000*1000);则从视频的10秒处开始提取.
            mExtractFrame.start();
        }).start();
    }


    private int nowChooseImageIndex = 0;
    private float preTime;
    //当前进度时间
    private float nowProgressTime;

    private void addFrameCompoundVideo() {
        LogUtil.d("OOM", "开始合成mask视频" );
        nowChooseImageIndex = 0;
        List<File> getMattingList = FileManager.listFileSortByModifyTime(faceMattingFolder);
        Bitmap firstBitmap = BitmapFactory.decodeFile(getMattingList.get(0).getPath());
        long AllTime = videoInfo.getDuration() * 1000;
        preTime = AllTime / (float) getMattingList.size();
        nowProgressTime = preTime;
        try {
            DrawPadAllExecute2 execute = new DrawPadAllExecute2(BaseApplication.getInstance(), DRAWPADWIDTH, DRAWPADHEIGHT, AllTime);
            execute.setFrameRate(FRAME_RATE);
            execute.setEncodeBitrate(5 * 1024 * 1024);
            execute.setOnLanSongSDKErrorListener(message -> LogUtil.d("OOM", "错误信息为" + message));
            execute.setOnLanSongSDKProgressListener((l, i) -> {
                float f_progress = (i / (float) 100) * 5;
                progress = (int) (95 + f_progress);
                handler.sendEmptyMessage(1);
            });
            execute.setOnLanSongSDKCompletedListener(exportPath -> {
                execute.removeAllLayer();
                execute.release();
                dialog.closePragressDialog();
                String albumPath = cacheCutVideoPath + "/Matting.mp4";
                try {
                    FileUtil.copyFile(new File(exportPath), albumPath);
                    DataCleanManager.deleteFilesByDirectory(context.getExternalFilesDir("faceMattingFolder"));
                    DataCleanManager.deleteFilesByDirectory(context.getExternalFilesDir("faceFolder"));
                    if (callback != null) {
                        callback.isSuccess(true, albumPath);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            LSOBitmapAsset asset = new LSOBitmapAsset(firstBitmap);
            BitmapLayer bitmapLayerForDrawBackground = execute.addBitmapLayer(asset);
            bitmapLayerForDrawBackground.setScaledToPadSize();
            CanvasLayer canvasLayer = execute.addCanvasLayer();
            canvasLayer.addCanvasRunnable((canvasLayer1, canvas, currentTime) -> {
                if (currentTime > nowProgressTime) {
                    //需要切换新的图了
                    nowChooseImageIndex++;
                    if (nowChooseImageIndex < getMattingList.size()) {
                        LogUtil.d("CanvasRunnable", "addCanvasRunnable=" + preTime + "currentTime=" + currentTime + "nowChooseImageIndex=" + nowChooseImageIndex);
                        nowProgressTime = preTime + nowProgressTime;
                        Bitmap firstBitmap1 = BitmapFactory.decodeFile(getMattingList.get(nowChooseImageIndex).getPath());
                        bitmapLayerForDrawBackground.switchBitmap(firstBitmap1);
                    }
                }
            });
            execute.start();
        } catch (Exception e) {
            LogUtil.d("OOM", e.getMessage());
            e.printStackTrace();
        }
    }


    private void addFrameCompoundVideoNoMatting() {
        LogUtil.d("OOM2", "开始合成原图" );
        List<File> getMattingList = FileManager.listFileSortByModifyTime(faceFolder);
        Bitmap firstBitmap = BitmapFactory.decodeFile(getMattingList.get(0).getPath());
        long AllTime = videoInfo.getDuration() * 1000;
        preTime = AllTime / (float) getMattingList.size();
        nowProgressTime = preTime;
        try {
            DrawPadAllExecute2 execute = new DrawPadAllExecute2(BaseApplication.getInstance(), DRAWPADWIDTH, DRAWPADHEIGHT, AllTime);
            execute.setFrameRate(FRAME_RATE);
            execute.setEncodeBitrate(5 * 1024 * 1024);
            execute.setOnLanSongSDKErrorListener(message -> LogUtil.d("OOM", "错误信息为" + message));
            execute.setOnLanSongSDKProgressListener((l, i) -> {
                float f_progress = (i / (float) 100) * 5;
                progress = (int) (90 + f_progress);
                handler.sendEmptyMessage(1);
            });
            execute.setOnLanSongSDKCompletedListener(exportPath -> {
                execute.removeAllLayer();
                execute.release();
                LogUtil.d("OOM", "合成没有原图成功");
                String albumPath = cacheCutVideoPath + "/noMatting.mp4";
                File file = new File(albumPath);
                if (file.exists()) {
                  boolean isDeleted=  file.delete();
                  LogUtil.d("OOM",""+isDeleted);
                }
                try {
                    FileUtil.copyFile(new File(exportPath), albumPath);
                    addFrameCompoundVideo();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });

            LSOBitmapAsset asset = new LSOBitmapAsset(firstBitmap);
            BitmapLayer bitmapLayerForDrawBackground = execute.addBitmapLayer(asset);
            bitmapLayerForDrawBackground.setScaledToPadSize();
            CanvasLayer canvasLayer = execute.addCanvasLayer();
            canvasLayer.addCanvasRunnable((canvasLayer1, canvas, currentTime) -> {
                if (currentTime > nowProgressTime) {
                    //需要切换新的图了
                    nowChooseImageIndex++;
                    if (nowChooseImageIndex < getMattingList.size()) {
                        LogUtil.d("CanvasRunnable", "addCanvasRunnable=" + preTime + "currentTime=" + currentTime + "nowChooseImageIndex=" + nowChooseImageIndex);
                        nowProgressTime = preTime + nowProgressTime;
                        Bitmap firstBitmap1 = BitmapFactory.decodeFile(getMattingList.get(nowChooseImageIndex).getPath());
                        bitmapLayerForDrawBackground.switchBitmap(firstBitmap1);
                    }
                }
            });
            execute.start();
        } catch (Exception e) {
            LogUtil.d("OOM", e.getMessage());
            e.printStackTrace();
        }
    }


    private MattingImage mattingImage = new MattingImage();



    private int downSuccessNum;


    private void downImageForBitmap(Bitmap OriginBitmap, int frameCount) {
        downSuccessNum++;
        String fileName = faceMattingFolder + File.separator + frameCount + ".png";
        LogUtil.d("OOM", "正在抠图" + downSuccessNum);
        mattingImage.mattingImageForMultiple(OriginBitmap, frameCount, (isSuccess, bitmap) -> {
            if (isSuccess) {
                BitmapManager.getInstance().saveBitmapToPath(bitmap, fileName, isSuccess1 -> GlideBitmapPool.putBitmap(
                        bitmap));
            } else {
                LogUtil.d("OOM", "bitmap=null");
            }

        });

        LogUtil.d("OOM", "allFrame-1=" + allFrame + "downSuccessNum=?" + downSuccessNum);
    }




    public interface MattingSuccess {
        void isSuccess(boolean isSuccess, String path);
    }


    private int progress;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dialog.setProgress(progress + "%");
        }
    };


}
















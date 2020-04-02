package com.flyingeffects.com.ui.model;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.commonlyModel.SaveAlbumPathModel;
import com.flyingeffects.com.commonlyModel.getVideoInfo;
import com.flyingeffects.com.enity.VideoInfo;
import com.flyingeffects.com.manager.BitmapManager;
import com.flyingeffects.com.manager.DataCleanManager;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.utils.FileUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.utils.faceUtil.ConUtil;
import com.glidebitmappool.GlideBitmapPool;
import com.lansosdk.box.BitmapLayer;
import com.lansosdk.box.CanvasLayer;
import com.lansosdk.box.CanvasRunnable;
import com.lansosdk.box.ExtractVideoFrame;
import com.lansosdk.box.LSOBitmapAsset;
import com.lansosdk.box.onExtractVideoFrameCompletedListener;
import com.lansosdk.box.onExtractVideoFrameProgressListener;
import com.lansosdk.videoeditor.DrawPadAllExecute2;
import com.lansosdk.videoeditor.MediaInfo;
import com.lansosdk.videoeditor.VideoOneDo2;
import com.megvii.segjni.SegJni;
import com.shixing.sxve.ui.view.WaitingDialog_progress;

import java.io.File;
import java.io.IOException;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * description ：视频抠图控制类
 * creation date: 2020/3/27
 * user : zhangtongju
 */
public class VideoMattingModel {
    private ExtractVideoFrame mExtractFrame;
    private VideoOneDo2 videoOneDo;
    private String videoPath;
    /**
     * 专门用来存储face 抠像的文件夹
     */
    private String faceFolder;
    /**
     * 专门用来存储已经抠图的文件夹
     */
    private String faceMattingFolder;
    private int frameCount;
    private static final int DRAWPADWIDTH = 720;
    private static final int DRAWPADHEIGHT = 1280;
    private static final int FRAME_RATE = 40;
    private Context context;

    private VideoInfo videoInfo;
    WaitingDialog_progress dialog;


    public VideoMattingModel(String videoPath, Context context) {
        this.videoPath = videoPath;
        this.context = context;
        videoInfo = getVideoInfo.getInstance().getRingDuring(videoPath);
        FileManager fileManager = new FileManager();
        faceFolder = fileManager.getFileCachePath(BaseApplication.getInstance(), "faceFolder");
        faceMattingFolder = fileManager.getFileCachePath(BaseApplication.getInstance(), "faceMattingFolder");


        dialog = new WaitingDialog_progress(context);
        dialog.openProgressDialog();
    }


    int allFrame;

    public void newFunction() {



        MediaInfo mInfo = new MediaInfo(videoPath);
        if (!mInfo.prepare() || !mInfo.isHaveVideo()) {
            return;
        }
        LogUtil.d("OOM", "视频的总帧数为" + allFrame);
        mExtractFrame = new ExtractVideoFrame(BaseApplication.getInstance(), videoPath);

        MediaMetadataRetriever retr = new MediaMetadataRetriever();
        retr.setDataSource(videoPath);
        String rotation = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        if (TextUtils.isEmpty(rotation)) {
            if (mInfo.vWidth * mInfo.vHeight > 960 * 540) {
                mExtractFrame.setBitmapWH(mInfo.vWidth / 2, mInfo.vHeight / 2);
            }
        } else {
            int Irotation = Integer.parseInt(rotation);
            if (Irotation == 90 || Irotation == 180) {
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
        //设置提取多少帧
        mExtractFrame.setExtractSomeFrame(allFrame);
        /**
         * 设置处理完成监听.
         */
        mExtractFrame.setOnExtractCompletedListener(new onExtractVideoFrameCompletedListener() {

            @Override
            public void onCompleted(ExtractVideoFrame v) {
                LogUtil.d("OOM", "onCompleted");
//                test();
            }
        });
        /**
         * 设置处理进度监听.
         */
        mExtractFrame.setOnExtractProgressListener(new onExtractVideoFrameProgressListener() {

            /**
             * 当前帧的画面回调,, ptsUS:当前帧的时间戳,单位微秒. 拿到图片后,建议放到ArrayList中,
             * 不要直接在这里处理.
             */
            @Override
            public void onExtractBitmap(Bitmap bmp, long ptsUS) {
                frameCount++;
                String hint = frameCount + "帧" + "\n"
                        + "s是:" + String.valueOf(ptsUS);
//                tvProgressHint.setText(hint);
                dialog.setProgress(hint);
                LogUtil.d("OOM", hint);

                //   String fileName = faceFolder + File.separator + frameCount + ".png";
                //    BitmapManager.getInstance().saveBitmapToPath(bmp, fileName);
                //todo  假如face sdk 抠图的速度和截取帧的速度大抵相同，那么就可以直接抠图，否则的话可能会造成内存回收不及时
                downImageForBitmap(bmp,frameCount);
                LogUtil.d("OOM", "bmp.width=" + bmp.getWidth() + "bmp.height=" + bmp.getHeight() + "config=" + bmp.getConfig());
//                GlideBitmapPool.putBitmap(bmp);

            }
        });
        frameCount = 0;
        /**
         * 开始执行. 或者你可以从指定地方开始解码.
         * mExtractFrame.start(10*1000*1000);则从视频的10秒处开始提取.
         */
        mExtractFrame.start();

    }


    private int nowChooseImageIndex;

    public void addFrameCompoundVideo() {
        List<File> getMattingList = FileManager.listFileSortByModifyTime(faceMattingFolder);
        Bitmap firstBitmap = BitmapFactory.decodeFile(getMattingList.get(0).getPath());

        long AllTime = videoInfo.getDuration() * 1000;
        double preTime = AllTime / getMattingList.size();
        final long[] LpreTime = {(long) preTime};

        try {
            DrawPadAllExecute2 execute = new DrawPadAllExecute2(BaseApplication.getInstance(), DRAWPADWIDTH, DRAWPADHEIGHT, AllTime);
            execute.setFrameRate(FRAME_RATE);
            execute.setEncodeBitrate(5 * 1024 * 1024);
            execute.setOnLanSongSDKErrorListener(message -> {
                LogUtil.d("OOM", "错误信息为" + message);
            });
            execute.setOnLanSongSDKProgressListener((l, i) -> {
                dialog.setProgress("最后渲染的进度为" + i);
                LogUtil.d("OOM", "进度为");
            });
            execute.setOnLanSongSDKCompletedListener(exportPath -> {
                LogUtil.d("OOM", "nowChooseImageIndex" + nowChooseImageIndex);
                dialog.closePragressDialog();
                String albumPath = SaveAlbumPathModel.getInstance().getKeepOutput();
                try {
                    FileUtil.copyFile(new File(exportPath), albumPath);
                    albumBroadcast(albumPath);
                    showKeepSuccessDialog(albumPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //todo 需要移除全部的子图层
                execute.release();
            });

            LSOBitmapAsset asset = new LSOBitmapAsset(firstBitmap);
            BitmapLayer bitmapLayerForDrawBackground = execute.addBitmapLayer(asset);
            CanvasLayer canvasLayer = execute.addCanvasLayer();
            canvasLayer.addCanvasRunnable(new CanvasRunnable() {
                @Override
                public void onDrawCanvas(CanvasLayer canvasLayer, Canvas canvas, long currentTime) {
                    if (currentTime > LpreTime[0]) {
                        LogUtil.d("OOM", "当前时间为" + currentTime + "下一个时间为" + LpreTime[0]);
                        LogUtil.d("OOM", "nowChooseImageIndex" + nowChooseImageIndex);
                        //需要切换新的图了
                        nowChooseImageIndex++;
                        if (nowChooseImageIndex < getMattingList.size()) {
                            LpreTime[0] = LpreTime[0]++;
                            Bitmap firstBitmap = BitmapFactory.decodeFile(getMattingList.get(nowChooseImageIndex).getPath());
                            bitmapLayerForDrawBackground.switchBitmap(firstBitmap);
                        }


                    }
                }
            });

            execute.start();
        } catch (Exception e) {
            LogUtil.d("OOM", e.getMessage());
            e.printStackTrace();
        }
    }

    private void showKeepSuccessDialog(String path) {
        DataCleanManager.deleteFilesByDirectory(BaseApplication.getInstance().getExternalFilesDir("faceFolder"));
        DataCleanManager.deleteFilesByDirectory(BaseApplication.getInstance().getExternalFilesDir("faceMattingFolder"));

        if (!DoubleClick.getInstance().isFastDoubleClick()) {
            AlertDialog.Builder builder = new AlertDialog.Builder( //去除黑边
                    new ContextThemeWrapper(context, R.style.Theme_Transparent));
            builder.setTitle(R.string.notification);
            builder.setMessage(context.getString(R.string.have_saved_to_sdcard) +
                    "【" + path + context.getString(R.string.folder) + "】");
            builder.setNegativeButton(context.getString(R.string.got_it), (dialog, which) -> dialog.dismiss());
            builder.setCancelable(true);
            Dialog dialog = builder.show();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    /**
     * description ：通知相册更新
     * date: ：2019/8/16 14:24
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    private void albumBroadcast(String outputFile) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(new File(outputFile)));
        context.sendBroadcast(intent);
    }


    private MattingImage mattingImage = new MattingImage();
    private List<File> getFilesAllName;

    public void test() {

        getFilesAllName = FileManager.listFileSortByModifyTime(faceFolder);
        downImageForPath(getFilesAllName.get(0).getPath());
    }


    private int downSuccessNum;

    public void downImageForPath(String path) {
        mattingImage.mattingImage(path, new MattingImage.mattingStatus() {
            @Override
            public void isSuccess(boolean isSuccess, Bitmap bp) {
                downSuccessNum++;
                if (getFilesAllName.size() == downSuccessNum) {
                    Observable.just(1).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
                        @Override
                        public void call(Integer integer) {
                            new Handler().post(() -> dialog.setProgress("完成抠图"));
                        }
                    });
                    addFrameCompoundVideo();
                } else {

                    Observable.just(1).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
                        @Override
                        public void call(Integer integer) {
                            new Handler().post(() -> dialog.setProgress("正在抠图" + downSuccessNum));
                        }
                    });

                    LogUtil.d("OOM", "正在抠图" + downSuccessNum);
                    String fileName = faceMattingFolder + File.separator + downSuccessNum + ".png";
                    BitmapManager.getInstance().saveBitmapToPath(bp, fileName);
                    GlideBitmapPool.putBitmap(bp);
                    downImageForPath(getFilesAllName.get(downSuccessNum).getPath());
                }
            }
        });
    }


    private  void downImageForBitmap(Bitmap OriginBitmap,int frameCount) {
        mattingImage.mattingImage(OriginBitmap, (isSuccess, bp1) -> {
            downSuccessNum++;
            LogUtil.d("OOM", "正在抠图" + downSuccessNum);
            String fileName = faceMattingFolder + File.separator + frameCount + ".png";
            BitmapManager.getInstance().saveBitmapToPath(bp1, fileName, isSuccess1 -> GlideBitmapPool.putBitmap(
                    bp1));
            GlideBitmapPool.putBitmap(OriginBitmap);
            if (allFrame - 1 == downSuccessNum) {
                addFrameCompoundVideo();
            }
        });
    }


}
















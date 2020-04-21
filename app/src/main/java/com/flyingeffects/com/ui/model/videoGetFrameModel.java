package com.flyingeffects.com.ui.model;


import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.manager.BitmapManager;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.utils.LogUtil;
import com.glidebitmappool.GlideBitmapPool;
import com.lansosdk.box.ExtractVideoFrame;
import com.lansosdk.videoeditor.MediaInfo;
import com.shixing.sxve.ui.view.WaitingDialogProgressNowAnim;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * description ：视频取帧控制类
 * creation date: 2020/4/20
 * user : zhangtongju
 */
public class videoGetFrameModel {

    private ExtractVideoFrame mExtractFrame;
    private Context context;
    private int allFrame;
    private int frameCount;
    private int progress;
    private String extractFrameFolder;
    private WaitingDialogProgressNowAnim dialog;
    private List<String> videoPath = new ArrayList<>();
    //    int nowExecute
    private int nowExtractVideoNum;
    private isSuccess callback;
    private FileManager fileManager;
    private String nowUseFile;

    public videoGetFrameModel(Context context, List<String> videoPath, isSuccess callback) {
        this.context = context;
        this.videoPath = videoPath;
        this.callback = callback;
        fileManager = new FileManager();
        extractFrameFolder = fileManager.getFileCachePath(BaseApplication.getInstance(), "ExtractFrame");
        dialog = new WaitingDialogProgressNowAnim(context);
        dialog.openProgressDialog();
        nowExtractVideoNum=0;
    }




    /**
     * description ： 取帧之后的文件夹，分别对应ExtractFrame里面的123。。。文件夹中，抠了多少视频，就对应对手数值
     * creation date: 2020/4/21
     * user : zhangtongju
     */
    public void ToExtractFrame(String path, String name) {
        nowExtractVideoNum++;
        nowUseFile=extractFrameFolder+"/"+nowExtractVideoNum;
        File file=new File(nowUseFile);
        if (!file.exists()) {
            //通过file的mkdirs()方法创建目录中包含却不存在的文件夹
            file.mkdirs();
        }
            new Thread(() -> {
                MediaInfo mInfo = new MediaInfo(path);
                if (!mInfo.prepare() || !mInfo.isHaveVideo()) {
                    return;
                }
                mExtractFrame = new ExtractVideoFrame(BaseApplication.getInstance(), path);
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(path);
                String rotation = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
                if (TextUtils.isEmpty(rotation)) {
                    if (mInfo.vWidth * mInfo.vHeight > 1280 * 720) {
                        mExtractFrame.setBitmapWH(mInfo.vWidth / 2, mInfo.vHeight / 2);
                    }
                } else {
                    int iRotation = Integer.parseInt(rotation);
                    if (iRotation == 90 || iRotation == 180) {
                        if (mInfo.vWidth * mInfo.vHeight > 1280 * 720) {
                            mExtractFrame.setBitmapWH(mInfo.vHeight / 2, mInfo.vWidth / 2);
                        }
                    } else {
                        if (mInfo.vWidth * mInfo.vHeight > 1280 * 720) {
                            mExtractFrame.setBitmapWH(mInfo.vWidth / 2, mInfo.vHeight / 2);
                        }
                    }
                }

                allFrame = mInfo.vTotalFrames;
                LogUtil.d("OOM2", "视频的总帧数为" + allFrame);
                //设置提取多少帧
                mExtractFrame.setExtractSomeFrame(allFrame);
                mExtractFrame.setOnExtractCompletedListener(v -> {
                    mExtractFrame.release();
                    mExtractFrame = null;
                    if (videoPath.size() == nowExtractVideoNum) {
                        //完成
                        callback.isExtractSuccess(true);
                    } else {
                        ToExtractFrame(videoPath.get(nowExtractVideoNum), nowUseFile);
                    }

                });
                //设置处理进度监听.
                //当前帧的画面回调,, ptsUS:当前帧的时间戳,单位微秒. 拿到图片后,建议放到ArrayList中,不要直接在这里处理.
                mExtractFrame.setOnExtractProgressListener((bmp, ptsUS) -> {
                    frameCount++;
                    new Handler().post(() -> {
                        progress = (int) ((frameCount / (float) allFrame) * 100);
                        handler.sendEmptyMessage(1);
                    });
                    LogUtil.d("OOM", frameCount + "帧");
                    String fileName = nowUseFile + File.separator + name + frameCount + ".png";
                    BitmapManager.getInstance().saveBitmapToPath(bmp, fileName, isSuccess -> GlideBitmapPool.putBitmap(bmp));
                });
                frameCount = 0;
                //开始执行. 或者你可以从指定地方开始解码.mExtractFrame.start(10*1000*1000);则从视频的10秒处开始提取.
                mExtractFrame.start();
            }).start();
        }

        @SuppressLint("HandlerLeak")
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                dialog.setProgress("飞闪视频抠像中" + progress + "%\n" + "请耐心等待 不要离开");
            }
        };


        interface isSuccess {

            void isExtractSuccess(boolean isSuccess);
        }

    }

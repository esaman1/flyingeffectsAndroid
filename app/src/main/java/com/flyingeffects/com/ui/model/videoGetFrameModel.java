package com.flyingeffects.com.ui.model;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.manager.BitmapManager;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.faceUtil.ConUtil;
import com.glidebitmappool.GlideBitmapPool;
import com.lansosdk.box.ExtractVideoFrame;
import com.lansosdk.videoeditor.MediaInfo;
import com.megvii.segjni.SegJni;
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
    //    private WaitingDialogProgressNowAnim dialog;
    private List<String> videoPath = new ArrayList<>();
    private int nowExtractVideoNum;
    private isSuccess callback;
    private FileManager fileManager;
    private String nowUseFile;
    /**
     * 专门用来存储已经抠图的文件夹
     */
//    private String faceMattingFolder;
    private int allVideoPathCount;
    float perAllTime;

    public videoGetFrameModel(Context context, List<String> videoPath, isSuccess callback) {
        this.context = context;
        this.videoPath = videoPath;
        this.callback = callback;
        fileManager = new FileManager();
//        faceMattingFolder = fileManager.getFileCachePath(BaseApplication.getInstance(), "faceMattingFolder");
        extractFrameFolder = fileManager.getFileCachePath(BaseApplication.getInstance(), "ExtractFrame");
//        dialog = new WaitingDialogProgressNowAnim(context);
//        dialog.openProgressDialog();
        nowExtractVideoNum = 0;
        allVideoPathCount = videoPath.size();
        perAllTime = 95 / allVideoPathCount + 1;
    }


    /**
     * description ：开始执行
     * creation date: 2020/4/21
     * user : zhangtongju
     */
    public void startExecute() {
        LogUtil.d("OOM", "要执行的次数为" + videoPath.size());
        if (videoPath.size() == nowExtractVideoNum) {
            //全部执行完成了
            LogUtil.d("OOM", "全部搞完");
//            dialog.closePragressDialog();
            if (callback != null) {
                LogUtil.d("OOM", "callback!=null");
                callback.isExtractSuccess(true, 95);
            } else {
                LogUtil.d("OOM", "callback==null");
            }
        } else {
            frameCount = 0;
            downSuccessNum = 0;
            LogUtil.d("OOM", "开始取帧第" + nowExtractVideoNum + "个视频");
            nowExtractVideoNum++;
//            SegJni.nativeCreateSegHandler(context, ConUtil.getFileContent(context, R.raw.megviisegment_model), BaseConstans.THREADCOUNT);
            ToExtractFrame(videoPath.get(nowExtractVideoNum - 1), nowExtractVideoNum + "");

        }
    }


    /**
     * description ： 取帧之后的文件夹，分别对应ExtractFrame里面的123。。。文件夹中，抠了多少视频，就对应对手数值
     * creation date: 2020/4/21
     * user : zhangtongju
     */
    public void ToExtractFrame(String path, String name) {
        nowUseFile = extractFrameFolder + "/" + name;
        File file = new File(nowUseFile);
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
                LogUtil.d("OOM2", "提取完成" + allFrame);
                for (int i = 1; i < BaseConstans.THREADCOUNT; i++) {
                    //最后需要补的帧
                    frameCount++;
                    downImageForBitmap(null, frameCount);
                }
                LogUtil.d("OOM2", "frameCount的值为" + frameCount);
//                    dialog.closePragressDialog();
                SegJni.nativeReleaseImageBuffer();
//                SegJni.nativeReleaseSegHandler();
                startExecute();
            });
            //设置处理进度监听.
            //当前帧的画面回调,, ptsUS:当前帧的时间戳,单位微秒. 拿到图片后,建议放到ArrayList中,不要直接在这里处理.
            mExtractFrame.setOnExtractProgressListener((bmp, ptsUS) -> {
                frameCount++;
                progress = (int) ((frameCount / (float) allFrame) * perAllTime);
                LogUtil.d("oom","当前抠图的进度为"+progress);
                progress = (int) (progress + perAllTime * (nowExtractVideoNum - 1));
                LogUtil.d("oom","当前抠图总的进度为"+progress);
                callback.isExtractSuccess(false, progress);

//                    new Handler().post(() -> {
//                        progress = (int) ((frameCount / (float) allFrame) * 100);
//                        handler.sendEmptyMessage(1);
//                    });
                LogUtil.d("OOM2", frameCount + "帧");
                downImageForBitmap(bmp, frameCount);
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
//                dialog.setProgress("飞闪视频抠像中" + progress + "%\n" + "请耐心等待 不要离开");
        }
    };


    private int downSuccessNum;
    private MattingImage mattingImage = new MattingImage();

    private void downImageForBitmap(Bitmap OriginBitmap, int frameCount) {
        downSuccessNum++;
        String fileName = nowUseFile + File.separator + frameCount + ".png";
        LogUtil.d("OOM2", "正在抠图" + downSuccessNum);
        mattingImage.mattingImageForMultipleForLucency(OriginBitmap, frameCount, (isSuccess, bitmap) -> {
            if (isSuccess) {
                BitmapManager.getInstance().saveBitmapToPath(bitmap, fileName, isSuccess1 -> GlideBitmapPool.putBitmap(
                        bitmap));
            } else {
                LogUtil.d("OOM2", "bitmap=null");
            }

        });

        LogUtil.d("OOM2", "allFrame-1=" + allFrame + "downSuccessNum=?" + downSuccessNum);
    }

    interface isSuccess {

        void isExtractSuccess(boolean isSuccess, int position);
    }

}

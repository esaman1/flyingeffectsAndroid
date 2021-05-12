package com.flyingeffects.com.utils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.text.TextUtils;

import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.manager.BitmapManager;
import com.flyingeffects.com.manager.FileManager;
import com.glidebitmappool.GlideBitmapPool;
import com.lansosdk.box.ExtractVideoFrame;
import com.lansosdk.videoeditor.MediaInfo;
import com.shuyu.gsyvideoplayer.utils.AnimatedGifEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


/**
 * description ：通过自己的方法，实现视频转成gif
 * creation date: 2021/5/12
 * user : zhangtongju
 */
public class VideoConvertGif {
    private ExtractVideoFrame mExtractFrame;
    private String extractFrameFolder;
    private int frameCount;

    public VideoConvertGif() {
        FileManager fileManager = new FileManager();
        extractFrameFolder = fileManager.getFileCachePath(BaseApplication.getInstance(), "ExtractFrame");
    }


    public void ToExtractFrame(String path, CreateGifCallback callback) {
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
                mExtractFrame.setBitmapWH(mInfo.vWidth / 3, mInfo.vHeight / 3);
            } else {
                int iRotation = Integer.parseInt(rotation);
                if (iRotation == 90 || iRotation == 180) {
                    mExtractFrame.setBitmapWH(mInfo.vHeight / 3, mInfo.vWidth / 3);
                } else {
                    mExtractFrame.setBitmapWH(mInfo.vWidth / 3, mInfo.vHeight / 3);
                }
            }
            int getAllFrame = mInfo.vTotalFrames;
            LogUtil.d("OOM2", "视频的总帧数为" + getAllFrame);
            float allFrameF = getAllFrame * 3 / (float) 4;
            LogUtil.d("OOM2", "提取数量为" + allFrameF);
            int allFrame = (int) allFrameF;
            //设置提取多少帧
            mExtractFrame.setExtractSomeFrame(allFrame);
            mExtractFrame.setOnExtractCompletedListener(v -> {
                createGif(callback);
            });
            mExtractFrame.setOnExtractProgressListener((bmp, ptsUS) -> {
                frameCount++;
                downImageForBitmap(bmp, frameCount);
            });
            mExtractFrame.start();
        }).start();
    }


    private void downImageForBitmap(Bitmap OriginBitmap, int frameCount) {
        String fileName = extractFrameFolder + File.separator + frameCount + ".jpg";
        BitmapManager.getInstance().saveBitmapToPathForJpg(OriginBitmap, fileName, isSuccess1 -> GlideBitmapPool.putBitmap(
                OriginBitmap));
    }

    /**
     * 生成gif图
     */
    private void createGif(CreateGifCallback callback) {
        List<File> getMattingList = FileManager.listFileSortByModifyTime(extractFrameFolder);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AnimatedGifEncoder localAnimatedGifEncoder = new AnimatedGifEncoder();
        localAnimatedGifEncoder.start(baos);//start
        localAnimatedGifEncoder.setRepeat(0);//设置生成gif的开始播放时间。0为立即开始播放
        localAnimatedGifEncoder.setDelay(0);
        for (int i = 0; i < getMattingList.size(); i++) {
            localAnimatedGifEncoder.addFrame(BitmapFactory.decodeFile(getMattingList.get(i).getPath()));
        }
        localAnimatedGifEncoder.finish();//finish
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/GIFMakerDemo");
        if (!file.exists()) file.mkdir();
        String path = Environment.getExternalStorageDirectory().getPath() + "/GIFMakerDemo/show.gif";
        LogUtil.d("OOM2", "createGif: ---->" + path);
        try {
            FileOutputStream fos = new FileOutputStream(path);
            baos.writeTo(fos);
            baos.flush();
            fos.flush();
            baos.close();
            fos.close();
            File extractFrameF = new File(extractFrameFolder);
//            if (extractFrameF.exists()) {
//               boolean isSuccess= extractFrameF.delete();
//               LogUtil.d("OOM2","删除文件夹是否成功"+isSuccess);
//            }
            if (callback != null) {
                callback.callback(true, path);
            }
        } catch (IOException e) {
            if (callback != null) {
                callback.callback(false, e.getMessage());
            }
            e.printStackTrace();
        }
    }


    /**
     * description ：gif 回调
     * creation date: 2021/5/12
     * user : zhangtongju
     */
    public interface CreateGifCallback {
        void callback(boolean isSuccess, String path);
    }


}

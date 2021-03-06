package com.flyingeffects.com.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.manager.BitmapManager;
import com.flyingeffects.com.manager.DataCleanManager;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.manager.huaweiObs;
import com.glidebitmappool.GlideBitmapPool;
import com.lansosdk.box.ExtractVideoFrame;
import com.lansosdk.videoeditor.MediaInfo;
import com.shuyu.gsyvideoplayer.utils.AnimatedGifEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;


/**
 * description ：通过自己的方法，实现视频转成gif
 * creation date: 2021/5/12
 * user : zhangtongju
 */
public class VideoConvertGif {
    private ExtractVideoFrame mExtractFrame;
    private final String extractFrameFolder;
    private final String gifCatch;
    private int frameCount;
    private final Context context;

    public VideoConvertGif(Context context) {
        this.context = context;
        FileManager fileManager = new FileManager();
        extractFrameFolder = fileManager.getFileCachePath(BaseApplication.getInstance(), "ExtractFrame");
        gifCatch = fileManager.getFileCachePath(BaseApplication.getInstance(), "GifCatch");
    }


    /**
     * description ：方式 1 通过自己拆成每帧，然后合成gif ,质量高，但是文件打
     * creation date: 2021/5/31
     * user : zhangtongju
     */
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
//            mExtractFrame.setBitmapWH(200,200);
            int allFrame = mInfo.vTotalFrames;
            //设置提取多少帧
            mExtractFrame.setExtractSomeFrame(allFrame);
            mExtractFrame.setOnExtractCompletedListener(v -> {
                try {
                    createGif(callback);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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


//    public  Bitmap getImageToChange(Bitmap mBitmap) {
//        Bitmap createBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
//        int mWidth = mBitmap.getWidth();
//        int mHeight = mBitmap.getHeight();
//        for (int i = 0; i < mHeight; i++) {
//            for (int j = 0; j < mWidth; j++) {
//                int color = mBitmap.getPixel(j, i);
//                int g = Color.green(color);
//                int r = Color.red(color);
//                int b = Color.blue(color);
//                int a = Color.alpha(color);
//                if((g>=220&&g<=255)&&(r>=220&&r<=250)&&(b>=220&&b<=255)){
//                    a = 0;
//                }
//                color = Color.argb(a, r, g, b);
//                createBitmap.setPixel(j, i, color);
//            }
//        }
//        return createBitmap;
//    }


    /**
     * 生成gif图
     */
    private void createGif(CreateGifCallback callback) throws IOException {
        List<File> getMattingList = FileManager.listFileSortByModifyTime(extractFrameFolder);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AnimatedGifEncoder localAnimatedGifEncoder = new AnimatedGifEncoder();
        localAnimatedGifEncoder.start(baos);//start
        localAnimatedGifEncoder.setRepeat(0);//设置生成gif的开始播放时间。0为立即开始播放
        localAnimatedGifEncoder.setDelay(0);
        localAnimatedGifEncoder.setFrameRate(20);
        for (int i = 0; i < getMattingList.size(); i++) {
            localAnimatedGifEncoder.addFrame(BitmapFactory.decodeFile(getMattingList.get(i).getPath()));
        }
        localAnimatedGifEncoder.finish();//finish
//        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/GIFMakerDemo");
//        if (!file.exists()) file.mkdir();
        String path = gifCatch + "/show.gif";
        String path2 = gifCatch + "/show.jpg";
        File fileFrom = new File(extractFrameFolder + File.separator + frameCount + ".jpg");
        FileUtil.copyFile(fileFrom, path2);

        LogUtil.d("OOM2", "createGif: ---->" + path);
        try {
            FileOutputStream fos = new FileOutputStream(path);
            baos.writeTo(fos);
            baos.flush();
            fos.flush();
            baos.close();
            fos.close();
            DataCleanManager.deleteFilesByDirectory(context.getExternalFilesDir("ExtractFrame"));
            uploadDressUpImage(path2, callback, path);
//            compressGif(path, path2, callback);
        } catch (IOException e) {
            if (callback != null) {
                callback.callback(false, e.getMessage(), "");
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
        void callback(boolean isSuccess, String path, String iconPath);
    }

    private String needGifPath;

    private void uploadDressUpImage(String path, CreateGifCallback callback, String orginPath) {
        new Thread(() -> {
            String type = path.substring(path.length() - 4);
            String nowTime = StringUtil.getCurrentTimeymd();
            String copyPath = "media/android/upGif/" + nowTime + "/" + System.currentTimeMillis() + type;
            needGifPath = "http://cdn.flying.flyingeffect.com/" + copyPath;
            uploadHuawei(path, copyPath, callback, orginPath);
        }).start();
    }

    /**
     * description ：上传到华为
     * creation date: 2021/4/14
     * user : zhangtongju
     */
    private void uploadHuawei(String path, String copyPath, CreateGifCallback callback, String orginPath) {
        LogUtil.d("OOM2", "needGifPath=" + needGifPath);
        huaweiObs.getInstance().uploadFileToHawei(path, copyPath, str -> Observable.just(str).subscribeOn(AndroidSchedulers.mainThread()).subscribe(s -> {
            if (callback != null) {
                callback.callback(true, orginPath, needGifPath);
            }

        }));
    }



}

package com.flyingeffects.com.ui.model;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.manager.BitmapManager;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.lansosdk.box.OnLanSongSDKCompletedListener;
import com.lansosdk.box.OnLanSongSDKErrorListener;
import com.lansosdk.box.OnLanSongSDKProgressListener;
import com.lansosdk.videoeditor.VideoOneDo2;

import static android.media.MediaMetadataRetriever.OPTION_PREVIOUS_SYNC;

public class videoAddCover {

    private VideoOneDo2 videoOneDo;
    private static videoAddCover thisModel;

    public static videoAddCover getInstance() {

        if (thisModel == null) {
            thisModel = new videoAddCover();
        }
        return thisModel;

    }


    public void addCover(String path, addCoverIsSuccess callback) {
        Bitmap bitmap = getCover(path);
        try {
            videoOneDo = new VideoOneDo2(BaseApplication.getInstance(), path);
            videoOneDo.setOnVideoOneDoErrorListener(new OnLanSongSDKErrorListener() {
                @Override
                public void onLanSongSDKError(int errorCode) {
                    ToastUtil.showToast("VideoOneDo处理错误");
                    videoOneDo.cancel();
                    videoOneDo = null;
                }
            });
            videoOneDo.setOnVideoOneDoProgressListener(new OnLanSongSDKProgressListener() {
                @Override
                public void onLanSongSDKProgress(long ptsUs, int percent) {
                    callback.progresss(percent);
                }
            });
            videoOneDo.setOnVideoOneDoCompletedListener(new OnLanSongSDKCompletedListener() {
                @Override
                public void onLanSongSDKCompleted(String dstVideo) {
                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                    videoOneDo.cancel();
                    videoOneDo.release();
                    videoOneDo = null;
                    callback.isSuccess(true, dstVideo);
                }
            });

            RequestOptions options = RequestOptions.frameOf(0);
            RequestOptions cacheOptions = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE);

//            Bitmap bmp1= BitmapFactory.decodeFile(ic);
            videoOneDo.setCoverLayer(bitmap, 0, 200 * 1000); //0.2S
            videoOneDo.start();
        } catch (Exception e) {
            e.printStackTrace();
            callback.isSuccess(false, "");
        }


    }


    public interface addCoverIsSuccess {

        void progresss(int progress);

        void isSuccess(boolean isSuccess, String path);
    }


    private Bitmap getCover(String path) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        return mmr.getFrameAtTime(2000 * 1000, OPTION_PREVIOUS_SYNC);
    }


    /**
     * description ：没压缩的封面
     * creation date: 2020/5/20
     * param :
     * user : zhangtongju
     */
    public void getCoverForPath(String path, String fileName) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        Bitmap bp = mmr.getFrameAtTime(0, OPTION_PREVIOUS_SYNC);
        BitmapManager.getInstance().saveBitmapToPath(bp, fileName);
    }


    public Bitmap getCoverForBitmap(String path) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        return mmr.getFrameAtTime(0, OPTION_PREVIOUS_SYNC);
    }


    /**
     * description ：压缩之后的封面
     * creation date: 2020/5/20
     * param :
     * user : zhangtongju
     */
    public void getCoverForPathToCompress(String path, String fileName) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        Bitmap bp = mmr.getFrameAtTime(0, OPTION_PREVIOUS_SYNC);
        bp = StringUtil.zoomImg(bp, 180, 320);
        BitmapManager.getInstance().saveBitmapToPath(bp, fileName);
    }


}

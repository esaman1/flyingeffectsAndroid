package com.flyingeffects.com.ui.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.utils.LogUtil;
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


    public void addCover(String path,addCoverIsSuccess callback ){
       Bitmap bitmap= getCover(path);
        try {
            videoOneDo=new VideoOneDo2(BaseApplication.getInstance(),path);
            videoOneDo.setOnVideoOneDoErrorListener(new OnLanSongSDKErrorListener() {
                @Override
                public void onLanSongSDKError(int errorCode) {
                    ToastUtil.showToast("VideoOneDo处理错误");
                    videoOneDo.cancel();
                    videoOneDo=null;
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
                    if(bitmap!=null&&!bitmap.isRecycled()){
                        bitmap.recycle();
                    }
                    videoOneDo.cancel();
                    videoOneDo.release();
                    videoOneDo=null;
                    callback.isSuccess(true,dstVideo);
                }
            });

            RequestOptions options = RequestOptions.frameOf(0);
            RequestOptions cacheOptions = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE);

//            Bitmap bmp1= BitmapFactory.decodeFile(ic);
            videoOneDo.setCoverLayer(bitmap,0,200*1000); //0.2S
            videoOneDo.start();
        } catch (Exception e) {
            e.printStackTrace();
            callback.isSuccess(false,"");
        }


    }


    public interface  addCoverIsSuccess{

        void progresss(int progress);

        void isSuccess(boolean isSuccess,String path);
    }



    private Bitmap getCover(String path){
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
      return mmr.getFrameAtTime( 2000*1000, OPTION_PREVIOUS_SYNC);
    }

}

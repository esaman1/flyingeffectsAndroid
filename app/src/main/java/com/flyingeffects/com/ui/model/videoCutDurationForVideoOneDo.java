package com.flyingeffects.com.ui.model;

import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.lansosdk.box.OnLanSongSDKCompletedListener;
import com.lansosdk.box.OnLanSongSDKErrorListener;
import com.lansosdk.box.OnLanSongSDKProgressListener;
import com.lansosdk.videoeditor.VideoOneDo2;

public class videoCutDurationForVideoOneDo {
    private VideoOneDo2 videoOneDo;
    private static videoCutDurationForVideoOneDo thisModel;

    public static videoCutDurationForVideoOneDo getInstance() {

        if (thisModel == null) {
            thisModel = new videoCutDurationForVideoOneDo();
        }
        return thisModel;

    }


    public void startCutDurtion(String path,long startUs,long total,isSuccess callback ){

        try {
            videoOneDo=new VideoOneDo2(BaseApplication.getInstance(),path);
            videoOneDo.setCutDuration(startUs,total);
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
                    LogUtil.d("OOM","percent="+percent);
                    LogUtil.d("OOM","ptsUs="+ptsUs);
                    callback.progresss(percent);
                }
            });
            videoOneDo.setOnVideoOneDoCompletedListener(new OnLanSongSDKCompletedListener() {
                @Override
                public void onLanSongSDKCompleted(String dstVideo) {
                    videoOneDo.cancel();
                    videoOneDo.release();
                    videoOneDo=null;
                    callback.isSuccess(true,dstVideo);
                }
            });

            videoOneDo.start();
        } catch (Exception e) {
            e.printStackTrace();
            callback.isSuccess(false,"");
        }


    }



    public interface  isSuccess{

        void progresss(int progress);

        void isSuccess(boolean isSuccess,String path);
    }
}

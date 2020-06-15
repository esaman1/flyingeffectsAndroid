package com.flyingeffects.com.ui.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.commonlyModel.getVideoInfo;
import com.flyingeffects.com.enity.VideoInfo;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.utils.FileUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.lansosdk.box.CanvasLayer;
import com.lansosdk.box.LSOScaleType;
import com.lansosdk.box.LSOVideoOption;
import com.lansosdk.box.OnLanSongSDKCompletedListener;
import com.lansosdk.box.OnLanSongSDKErrorListener;
import com.lansosdk.box.OnLanSongSDKProgressListener;
import com.lansosdk.box.VideoFrameLayer;
import com.lansosdk.videoeditor.DrawPadAllExecute2;
import com.lansosdk.videoeditor.MediaInfo;
import com.lansosdk.videoeditor.VideoOneDo2;

import java.io.File;
import java.io.IOException;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class videoCutDurationForVideoOneDo {
    private VideoOneDo2 videoOneDo;
    private static videoCutDurationForVideoOneDo thisModel;
    /**
     * 裁剪保存后的地址
     */
    private static String cacheCutVideoPath;

    public static videoCutDurationForVideoOneDo getInstance() {
        FileManager fileManager = new FileManager();
        cacheCutVideoPath = fileManager.getFileCachePath(BaseApplication.getInstance(), "zdyVideoCut");
        if (thisModel == null) {
            thisModel = new videoCutDurationForVideoOneDo();
        }
        return thisModel;
    }


    public void startCutDurtion(String path, long startUs, long total, isSuccess callback) {
        LogUtil.d("OOM", "startUs=" + startUs + "total=" + total);
        try {
            videoOneDo = new VideoOneDo2(BaseApplication.getInstance(), path);
            videoOneDo.setCutDuration(startUs, total);
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
                    videoOneDo.cancel();
                    videoOneDo.release();
                    videoOneDo = null;
                    callback.isSuccess(true, dstVideo);
                }
            });

            videoOneDo.start();
        } catch (Exception e) {
            e.printStackTrace();
            callback.isSuccess(false, "");
        }


    }


    public interface isSuccess {

        void progresss(int progress);

        void isSuccess(boolean isSuccess, String path);
    }


    /**
     * description ：DrawPadAllExecute2 的方式裁剪
     * creation date: 2020/4/10
     * param :
     * user : zhangtongju
     */
    private DrawPadAllExecute2 execute;

    public void CutVideoForDrawPadAllExecute2(Context context, float duration, String path, long startDurtion, isSuccess callback) {
        try {
            VideoInfo   videoInfo = getVideoInfo.getInstance().getRingDuring(path);
            long allDuration=videoInfo.getDuration();
            execute = new DrawPadAllExecute2(context, 720, 1280, (long) (duration * 1000));
            execute.setFrameRate(20);
            execute.setEncodeBitrate(5 * 1024 * 1024);
            execute.setOnLanSongSDKErrorListener(message -> {
                LogUtil.e("execute", String.valueOf(message));
            });
            execute.setOnLanSongSDKProgressListener((l, i) -> {
                callback.progresss(i);
            });

            execute.setOnLanSongSDKCompletedListener(exportPath -> {
                execute.removeAllLayer();
                execute.release();
                if (exportPath == null) {
                    ToastUtil.showToast(context.getString(R.string.render_error));
                    callback.isSuccess(false, "");
                    return;
                }
                File video = new File(exportPath);
                LogUtil.d("OOM","exportPath="+exportPath);
                if (video.exists()) {
                    try {
                        String savePath = cacheCutVideoPath + "/" + System.currentTimeMillis() + ".mp4";
                        File file = new File(savePath);
                        if (file.exists()) {
                            file.delete();
                        }
                        LogUtil.d("OOM","OOM="+savePath);
                        FileUtil.copyFile(video, savePath);
                        callback.isSuccess(true, savePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    callback.isSuccess(false, "");
                    ToastUtil.showToast(context.getString(R.string.export_failure));
                }
            });
            Observable.create((Observable.OnSubscribe<Integer>) subscriber -> {
                if (execute != null) {
                    try {
                        LSOVideoOption option = new LSOVideoOption(path);
                        long startDuration = startDurtion * 1000;
                        long durationUs = (long) (duration * 1000);
                        long endDuration=durationUs + startDuration;
                        option.setCutDurationUs(startDuration, endDuration);
                        final VideoFrameLayer videoLayer = execute.addVideoLayer(option);
                        videoLayer.setScaleType(LSOScaleType.VIDEO_SCALE_TYPE);

//                        CanvasLayer canvasLayer = execute.addCanvasLayer();
//                        canvasLayer.addCanvasRunnable((canvasLayer1, canvas, currentTime) -> {
//                            if (currentTime >allDuration*1000) {
//                                LogUtil.d("OOM","隐藏当前图层"+"endDuration="+endDuration+"currentTime="+currentTime);
//                                //需要切换新的图了
//                                if(videoLayer[0] !=null){
//                                    execute.removeLayer(videoLayer[0]);
//                                    videoLayer[0].setVisibility(View.GONE);
//                                    videoLayer[0] =null;
//                                }
//
//                            }
//                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }







                    if (execute.start()) {
                        subscriber.onNext(0);
                    } else {
                        subscriber.onError(new Throwable());
                    }
                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(aInteger -> {
//                dialog.setProgress(aInteger + "");
            }, throwable -> {
                execute.removeAllLayer();
                execute.release();
                callback.isSuccess(false, "");
                ToastUtil.showToast(context.getString(R.string.export_failure));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

package com.flyingeffects.com.ui.model;

import android.content.Context;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.utils.FileUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.lansosdk.box.LSOVideoOption;
import com.lansosdk.box.OnLanSongSDKCompletedListener;
import com.lansosdk.box.OnLanSongSDKErrorListener;
import com.lansosdk.box.OnLanSongSDKProgressListener;
import com.lansosdk.box.VideoFrameLayer;
import com.lansosdk.box.VideoLayer;
import com.lansosdk.videoeditor.DrawPadAllExecute2;
import com.lansosdk.videoeditor.VideoOneDo2;
import com.shixing.sxve.ui.view.WaitingDialog_progress;

import java.io.File;
import java.io.IOException;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class videoCutDurationForVideoOneDo {
    private VideoOneDo2 videoOneDo;
    private static videoCutDurationForVideoOneDo thisModel;

    public static videoCutDurationForVideoOneDo getInstance() {

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
                    LogUtil.d("OOM", "percent=" + percent);
                    LogUtil.d("OOM", "ptsUs=" + ptsUs);
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
    private boolean isSaving = false;
    DrawPadAllExecute2 execute;

    public void CutVideoForDrawPadAllExecute2(Context context, float duration, String path, long startDurtion,isSuccess callback) {
        try {
            execute = new DrawPadAllExecute2(context, 720, 1280, (long) (duration * 1000));
            execute.setFrameRate(20);
            execute.setEncodeBitrate(5 * 1024 * 1024);
            execute.setOnLanSongSDKErrorListener(message -> {
//                isSaving=false;
//                dialog.closePragressDialog();
                LogUtil.e("execute", String.valueOf(message));
            });
            execute.setOnLanSongSDKProgressListener((l, i) -> {
//                if (dialog!=null){
//                    dialog.setProgress(i +"%");
//                    LogUtil.d("execute progress: ", String.valueOf(i));
//                }
                callback.progresss(i);
            });
            execute.setOnLanSongSDKCompletedListener(exportPath -> {
                isSaving = false;
                execute.release();
                if (exportPath == null) {
                    ToastUtil.showToast(context.getString(R.string.render_error));
                    callback.isSuccess(false, "");
                    return;
                }
                File video = new File(exportPath);
                if (video.exists()) {
                    callback.isSuccess(true, exportPath);
                } else {
                    callback.isSuccess(false, "");
//                    dialog.closePragressDialog();
                    ToastUtil.showToast(context.getString(R.string.export_failure));
                }
            });
            Observable.create((Observable.OnSubscribe<Integer>) subscriber -> {
                if (execute != null) {
                    try {
                        LSOVideoOption option = new LSOVideoOption(path);
                        long startDuration=startDurtion*1000;
                        long durationUs = (long) (duration * 1000);
                        option.setCutDurationUs(startDuration, durationUs+startDuration);
                        VideoFrameLayer videoLayer = execute.addVideoLayer(option);
                        videoLayer.setScaledToPadSize();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (execute.start()) {
                    subscriber.onNext(0);
                } else {
                    subscriber.onError(new Throwable());
                }
                subscriber.onCompleted();
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(aInteger -> {
//                dialog.setProgress(aInteger + "");
            }, throwable -> {
                isSaving = false;
                execute.release();
                callback.isSuccess(false, "");
                ToastUtil.showToast(context.getString(R.string.export_failure));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

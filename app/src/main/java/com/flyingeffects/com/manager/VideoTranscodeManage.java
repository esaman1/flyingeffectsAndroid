package com.flyingeffects.com.manager;

import android.content.Context;
import android.media.MediaPlayer;

import com.flyingeffects.com.R;
import com.flyingeffects.com.utils.FileUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.lansosdk.LanSongFilter.LanSongBlurFilter;
import com.lansosdk.LanSongFilter.LanSongFilter;
import com.lansosdk.box.LSOVideoOption;
import com.lansosdk.box.VideoFrameLayer;
import com.lansosdk.videoeditor.DrawPadAllExecute2;
import com.shixing.sxve.ui.view.WaitingDialog;

import java.io.File;
import java.io.IOException;



/**
 * description ：把用户下载的视频转码
 * creation date: 2020/3/23
 * param :
 * user : zhangtongju
 */
public class VideoTranscodeManage {


    private static int FRAME_RATE = 30;
    private static final int DRAWPAD_WIDTH = 720;
    private static final int DRAWPAD_HEIGHT = 1280;


    private static VideoTranscodeManage thisModel;

    public static VideoTranscodeManage getInstance() {

        if (thisModel == null) {
            thisModel = new VideoTranscodeManage();
        }
        return thisModel;

    }


    public void tranCodeForVideo(Context context, String path,String id, videoTransCodeState callback) {
        LogUtil.d("OOM","转码的地址为"+path);
        FileManager fileManager = new FileManager();
        String mVideoFolder = fileManager.getFileCachePath(context, "downVideo");
        String format = path.substring(path.length() - 4);
        String fileName = mVideoFolder + File.separator + id + format;
        File file=new File(fileName);
        if(file.exists()){
            callback.isSuccess(true, fileName);
            return;
        }
        int duration =getRingDuring(path);
        LogUtil.d("OOM","duration="+duration*1000);
        DrawPadAllExecute2 execute = null;
        try {
            execute = new DrawPadAllExecute2(context, DRAWPAD_WIDTH, DRAWPAD_HEIGHT,duration*1000);
            execute.setFrameRate(FRAME_RATE);
            execute.setEncodeBitrate(5 * 1024 * 1024);
            execute.setOnLanSongSDKErrorListener(message -> {
                ToastUtil.showToast("转码错误编号为"+message);
                callback.isSuccess(false, "");
            });
            execute.setOnLanSongSDKProgressListener((l, i) -> {
                LogUtil.d("OOM","转码的进度为"+i+"%");
            });
            DrawPadAllExecute2 finalExecute = execute;
            execute.setOnLanSongSDKCompletedListener(exportPath -> {
                finalExecute.release();
                if (exportPath == null) {
                    ToastUtil.showToast(context.getString(R.string.render_error));
                    return;
                }
                File video = new File(exportPath);
                if (video.exists()) {
                    try {
                        FileUtil.copyFile(video, fileName);
                        callback.isSuccess(true,fileName);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            execute.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setMainLayer(String path) {
        LSOVideoOption option= null;
        try {
            option = new LSOVideoOption(path);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public interface videoTransCodeState {
        void isSuccess(boolean isSuccess, String path);
    }


    private int getRingDuring(String videoPath) {
        int duration = 0;
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(videoPath);
            mediaPlayer.prepare();
            duration = mediaPlayer.getDuration();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.release();
        return duration;
    }


}

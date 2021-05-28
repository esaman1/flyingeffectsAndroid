package com.flyingeffects.com.ui.model;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.SubtitleEntity;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.manager.huaweiObs;
import com.flyingeffects.com.manager.mediaManager;
import com.flyingeffects.com.ui.interfaces.model.JadeFontMakeMvpCallback;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.lansosdk.videoeditor.MediaInfo;
import com.shixing.sxve.ui.view.WaitingDialog;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.subjects.PublishSubject;


/**
 * @author ZhouGang
 * @date 2021/5/25
 */
public class JadeFontMakeModel {

    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();

    String videoPath;
    String soundFolder;
    JadeFontMakeMvpCallback mCallback;
    Activity context;
    /**如果videoPath不为空  -1和0代表默认使用视频中的音频 2为提取音频 如果videoPath为空 -1没有音频 2为提取音频*/
    int changeMusicIndex = -1;
    String chooseExtractedAudioBjMusicPath = "";


    public JadeFontMakeModel(Activity context,String videoPath, JadeFontMakeMvpCallback  callback) {
        this.videoPath = videoPath;
        this.mCallback = callback;
        this.context = context;
        FileManager fileManager = new FileManager();
        soundFolder = fileManager.getFileCachePath(context, "soundFolder");
    }


    public void startIdentify(boolean isVideoInAudio, String videoPath, String audioPath) {
        //先提取视频中的音频再识别
        if (isVideoInAudio) {
            getVideoVoice(videoPath, soundFolder, true);
        } else {
            //直接识别音频文件
            uploadHuaweiCloudAndIdentifySubtitle(audioPath,isVideoInAudio);
        }
    }

    /**
     * description ：视频音视频分离，获得视频的声音
     * creation date: 2020/4/23
     * user : zhangtongju
     * true isIdentify 要识别字幕   false 只提取音频
     */
    private void getVideoVoice(String videoPath, String outputPath,boolean isIdentify) {
        WaitingDialog.openPragressDialog(context);
        mediaManager manager = new mediaManager(context);

        manager.splitMp4(videoPath, new File(outputPath), (isSuccess, putPath) -> {
            WaitingDialog.closeProgressDialog();
            if (isSuccess) {
                LogUtil.d("OOM2", "分离出来的因为地址为" + outputPath);
                String audioPath = outputPath + File.separator + "bgm.mp3";
                if (isIdentify) {
                    uploadHuaweiCloudAndIdentifySubtitle(audioPath,true);
                } else {
                    mCallback.getBgmPath(audioPath);
                }
            } else {
                LogUtil.d("OOM2", "分离出来的因为地址为null" + outputPath);
                if (isIdentify) {
                    ToastUtil.showToast("提取音频失败");
                } else {
                    mCallback.getBgmPath("");
                }
            }
        });
    }

    private void uploadHuaweiCloudAndIdentifySubtitle(String audioPath,boolean isVideoInAudio){
        String type = audioPath.substring(audioPath.length() - 4);
        String nowTime = StringUtil.getCurrentTimeymd();
        String huaweiAudioPath = "media/android/audio_identify/" + nowTime + "/" + System.currentTimeMillis() + type;
        WaitingDialog.openPragressDialog(context,"识别中...");
        Log.d("OOM2", "uploadFileToHuawei" + "当前上传的地址为" + audioPath + "当前的名字为" + huaweiAudioPath);
        new Thread(() -> huaweiObs.getInstance().uploadFileToHawei(audioPath, huaweiAudioPath, new huaweiObs.Callback() {
            @Override
            public void isSuccess(String str) {
                if (!TextUtils.isEmpty(str)) {
                    String path = str.substring(str.lastIndexOf("=") + 1, str.length() - 1);
                    MediaInfo mediaInfo = new MediaInfo(audioPath);
                    mediaInfo.prepare();
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Map<String,String> map = new HashMap<>();
                            map.put("audiourl",path);
                            map.put("token", BaseConstans.GetUserToken());
                            map.put("duration", String.valueOf(mediaInfo.aDuration));
                            mediaInfo.release();
                            HttpUtil.getInstance().toSubscribe(Api.getDefault().identifySubtitle(map),
                                    new ProgressSubscriber<List<SubtitleEntity>>(context) {
                                        @Override
                                        protected void onSubError(String message) {
                                            ToastUtil.showToast(message);
                                            WaitingDialog.closeProgressDialog();
                                        }

                                        @Override
                                        protected void onSubNext(List<SubtitleEntity> data) {
                                            WaitingDialog.closeProgressDialog();
                                            if (data != null && !data.isEmpty()) {
                                                mCallback.identifySubtitle(data, isVideoInAudio,audioPath);
                                            }
                                        }
                                    }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
                        }
                    });
                }
            }
        })).start();
    }

    /**
     * 播放视频中的音频
     */
    public void chooseVideoInAudio(int index) {
        if (!TextUtils.isEmpty(videoPath)) {
            mCallback.clearCheckBox();
            mCallback.chooseCheckBox(0);
            mCallback.getBgmPath("");
            changeMusicIndex = index;
        }
    }

    /**
     * 素材中的音频
     */
    public void chooseNowStickerMaterialMusic(int index) {
//        if (nowChooseStickerView != null) {
//            if (AlbumType.isVideo(GetPathType.getInstance().getPathType(nowChooseStickerView.getOriginalPath()))) {
//                mPresenter.clearCheckBox();
//                nowChooseMusicId = 1;
//                mPresenter.chooseCheckBox(0);
//                getVideoVoice(nowChooseStickerView.getOriginalPath(), soundFolder);
//            } else {
//                ToastUtil.showToast("当前素材不是视频");
//            }
//        }
        changeMusicIndex = index;
    }

    public void setExtractedAudioBjMusicPath(String path) {
        chooseExtractedAudioBjMusicPath = path;
        extractedAudio(chooseExtractedAudioBjMusicPath,2);
    }

    /**
     * 播放提取的音频
     */
    public void extractedAudio(String audioPath,int index) {
        if(!TextUtils.isEmpty(chooseExtractedAudioBjMusicPath)){
            changeMusicIndex = index;
            mCallback.clearCheckBox();
            mCallback.chooseCheckBox(index);
            mCallback.getBgmPath(audioPath);
        }else {
            ToastUtil.showToast("没有提取音乐");
        }
    }

    public void getNowPlayingTimeViewShow(long progressBarProgress, long endTime) {

    }
}

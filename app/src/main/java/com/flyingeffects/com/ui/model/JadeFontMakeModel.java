package com.flyingeffects.com.ui.model;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.entity.SubtitleEntity;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.BitmapManager;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.manager.huaweiObs;
import com.flyingeffects.com.manager.mediaManager;
import com.flyingeffects.com.ui.interfaces.model.JadeFontMakeMvpCallback;
import com.flyingeffects.com.ui.view.activity.CreationTemplatePreviewActivity;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.imaginstudio.imagetools.pixellab.TextObject.TextComponent;
import com.imaginstudio.imagetools.pixellab.textContainer;
import com.lansosdk.videoeditor.MediaInfo;
import com.shixing.sxve.ui.view.WaitingDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import rx.subjects.PublishSubject;


/**
 * @author ZhouGang
 * @date 2021/5/25
 * 制作玉体字的model
 */
public class JadeFontMakeModel {

    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();



    String videoPath;
    String imagePath;
    String soundFolder;
    JadeFontMakeMvpCallback mCallback;
    Activity context;
    /**
     * 如果videoPath不为空  -1和0代表默认使用视频中的音频 2为提取音频 如果videoPath为空 -1没有音频 2为提取音频
     */
    int changeMusicIndex = -1;
    String chooseExtractedAudioBjMusicPath = "";


//    private int dialogProgress;
//
//    @SuppressLint("HandlerLeak")
//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            String title;
//            String content;
//            if (dialogProgress <= 25) {
//                title = "飞闪预览处理中";
//                content = "请耐心等待 不要离开";
//            } else if (dialogProgress <= 40) {
//                title = "飞闪音频添加中";
//                content = "快了，友友稍等片刻";
//            } else if (dialogProgress <= 60) {
//                title = "飞闪视频处理中";
//                content = "即将生成";
//            } else if (dialogProgress <= 80) {
//                title = "飞闪视频合成中";
//                content = "马上就好，不要离开";
//            } else {
//                title = "视频即将呈现啦";
//                content = "最后合成中，请稍后";
//            }
//            mCallback.setDialogProgress(title, dialogProgress, content);
//        }
//    };


    public JadeFontMakeModel(Activity context, String videoPath, String imagePath, JadeFontMakeMvpCallback callback) {
        this.videoPath = videoPath;
        this.imagePath = imagePath;
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
            uploadHuaweiCloudAndIdentifySubtitle(audioPath, isVideoInAudio);
        }
    }

    /**
     * description ：视频音视频分离，获得视频的声音
     * creation date: 2020/4/23
     * user : zhangtongju
     * true isIdentify 要识别字幕   false 只提取音频
     */
    private void getVideoVoice(String videoPath, String outputPath, boolean isIdentify) {
        WaitingDialog.openPragressDialog(context);
        mediaManager manager = new mediaManager(context);

        manager.splitMp4(videoPath, new File(outputPath), (isSuccess, putPath) -> {
            WaitingDialog.closeProgressDialog();
            if (isSuccess) {
                LogUtil.d("OOM2", "分离出来的因为地址为" + outputPath);
                String audioPath = outputPath + File.separator + "bgm.mp3";
                if (isIdentify) {
                    uploadHuaweiCloudAndIdentifySubtitle(audioPath, true);
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

    private void uploadHuaweiCloudAndIdentifySubtitle(String audioPath, boolean isVideoInAudio) {
        String type = audioPath.substring(audioPath.length() - 4);
        String nowTime = StringUtil.getCurrentTimeymd();
        String huaweiAudioPath = "media/android/audio_identify/" + nowTime + "/" + System.currentTimeMillis() + type;
        WaitingDialog.openPragressDialog(context, "识别中...");
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
                            Map<String, String> map = new HashMap<>();
                            map.put("audiourl", path);
                            map.put("token", BaseConstans.getUserToken());
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
                                                mCallback.identifySubtitle(data, isVideoInAudio, audioPath);
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
        extractedAudio(chooseExtractedAudioBjMusicPath, 2);
    }

    /**
     * 播放提取的音频
     */
    public void extractedAudio(String audioPath, int index) {
        if (!TextUtils.isEmpty(chooseExtractedAudioBjMusicPath)) {
            changeMusicIndex = index;
            mCallback.clearCheckBox();
            mCallback.chooseCheckBox(index);
            mCallback.getBgmPath(audioPath);
        } else {
            ToastUtil.showToast("没有提取音乐");
        }
    }

    /**
     * 控制玉体字view的展示或隐藏
     *
     * @param progress  当前播放所处的位置
     * @param totalTime 视频结束位置
     */
    public void getNowPlayingTimeViewShow(textContainer textContain, long progress, long totalTime) {
        for (int i = 0; i < textContain.getChildCount(); i++) {
            TextComponent textComponent = (TextComponent) textContain.getChildAt(i);
            if (textComponent != null) {
                long startTime = textComponent.getStartTime();
                long endTime = textComponent.getEndTime();
                LogUtil.d("OOM4", "endTime" + endTime + "startTime" + startTime + "progress=" + progress);
                if (endTime != 0) {
                    if (startTime <= progress && progress <= endTime) {
                        textComponent.setVisibility(View.VISIBLE);
                        LogUtil.d("OOM4", "setVisibility");
                    } else if (startTime <= progress && (totalTime - endTime <= 100 || (progress > totalTime && progress - totalTime <= 1))) {
                        textComponent.setVisibility(View.VISIBLE);
                    } else {
                        textComponent.setVisibility(View.INVISIBLE);
                        LogUtil.d("OOM4", "setVisibilityGONE");
                    }
                }
            }
        }
    }





    /**
     * 获得文字贴纸图片
     */
    public void GetAllTextBitPath(textContainer container) {
        WaitingDialog.openPragressDialog(context);
        for (int i = 0; i < container.getChildCount(); i++) {
            TextComponent textComponent = (TextComponent) container.getChildAt(i);
            if(textComponent!=null){
                Bitmap bp=   ViewToBitmap(textComponent);
                String path = Objects.requireNonNull(context.getExternalFilesDir("runCatch/")).getPath();
                String needPath = path + File.separator + UUID.randomUUID() + ".png";
                BitmapManager.getInstance().saveBitmapToPath(bp,needPath);
                textComponent.setTextJadePath(needPath);
            }else{

            }
        }
        JadeFontMaleSaveDraw jadeFontMaleDraw=new JadeFontMaleSaveDraw(context,videoPath,changeMusicIndex,chooseExtractedAudioBjMusicPath,imagePath, container);
        jadeFontMaleDraw.saveVideo(0, 10 * 1000, false, 0, new JadeFontMaleSaveDraw.jadeFontMaleSaveCallback() {
            @Override
            public void drawCompleted(String path) {
                WaitingDialog.closeProgressDialog();
                Intent intent = new Intent(context, CreationTemplatePreviewActivity.class);
                Bundle bundle = new Bundle();
//                bundle.putStringArrayList("titleEffect", (ArrayList<String>) GetAllStickerDataModel.getInstance().GettitleEffect());
//                bundle.putStringArrayList("titleStyle", (ArrayList<String>) GetAllStickerDataModel.getInstance().GetTitleStyle());
//                bundle.putStringArrayList("titleFrame", (ArrayList<String>) GetAllStickerDataModel.getInstance().GetTitleFrame());
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                bundle.putString("path", path);
                bundle.putBoolean("nowUiIsLandscape", false);
                bundle.putString("templateTitle", "");
                intent.putExtra("bundle", bundle);
                context.startActivity(intent);


            }

            @Override
            public void ProgressListener(int progress) {

            }
        });
    }


    /**
     * view 转成图片
     */
    public Bitmap ViewToBitmap(View v) {
        Bitmap bmp = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
     //   c.drawColor(Color.WHITE);
        v.draw(c);
        return bmp;
    }


}

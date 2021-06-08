package com.flyingeffects.com.ui.model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
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
import com.flyingeffects.com.utils.BitmapUtils;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.imaginstudio.imagetools.pixellab.TextObject.TextComponent;
import com.imaginstudio.imagetools.pixellab.textContainer;
import com.lansosdk.box.LSOScaleType;
import com.lansosdk.box.LSOVideoOption;
import com.lansosdk.box.Layer;
import com.lansosdk.box.OnLanSongSDKCompletedListener;
import com.lansosdk.box.OnLanSongSDKProgressListener;
import com.lansosdk.videoeditor.DrawPadAllExecute2;
import com.lansosdk.videoeditor.MediaInfo;
import com.shixing.sxve.ui.view.WaitingDialog;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;


/**
 * @author ZhouGang
 * @date 2021/5/25
 * 制作玉体字的model
 */
public class JadeFontMakeModel {

    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();

    private static int DRAWPADWIDTH = 720;
    private static int DRAWPADHEIGHT = 1280;
    private static final int FRAME_RATE = 20;
    private DrawPadAllExecute2 execute;

    String videoPath;
    String imagePath;
    String soundFolder;
    JadeFontMakeMvpCallback mCallback;
    Activity context;
    /**如果videoPath不为空  -1和0代表默认使用视频中的音频 2为提取音频 如果videoPath为空 -1没有音频 2为提取音频*/
    int changeMusicIndex = -1;
    String chooseExtractedAudioBjMusicPath = "";


    private int dialogProgress;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String title;
            String content;
            if (dialogProgress <= 25) {
                title = "飞闪预览处理中";
                content = "请耐心等待 不要离开";
            } else if (dialogProgress <= 40) {
                title = "飞闪音频添加中";
                content = "快了，友友稍等片刻";
            } else if (dialogProgress <= 60) {
                title = "飞闪视频处理中";
                content = "即将生成";
            } else if (dialogProgress <= 80) {
                title = "飞闪视频合成中";
                content = "马上就好，不要离开";
            } else {
                title = "视频即将呈现啦";
                content = "最后合成中，请稍后";
            }
            mCallback.setDialogProgress(title, dialogProgress, content);
        }
    };


    public JadeFontMakeModel(Activity context,String videoPath,String imagePath, JadeFontMakeMvpCallback  callback) {
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

    /**
     * 控制玉体字view的展示或隐藏
     * @param progress 当前播放所处的位置
     * @param totalTime  视频结束位置
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
                        textComponent.setVisibility(View.GONE);
                        LogUtil.d("OOM4", "setVisibilityGONE");
                    }
                }
            }
        }
    }

    public void saveVideo(long cutStartTime, long cutEndTime, boolean nowUiIsLandscape, float percentageH) {
        long duration = cutEndTime - cutStartTime;
        try {
            if (nowUiIsLandscape) {
                DRAWPADWIDTH = 1280;
                DRAWPADHEIGHT = 720;
            } else {
                DRAWPADWIDTH = 720;
                DRAWPADHEIGHT = 1280;
            }
            execute = new DrawPadAllExecute2(context, DRAWPADWIDTH, DRAWPADHEIGHT, duration * 1000);
            execute.setFrameRate(FRAME_RATE);
            execute.setEncodeBitrate(5 * 1024 * 1024);
            execute.setOnLanSongSDKErrorListener(i -> mCallback.dismissLoadingDialog());
            execute.setOnLanSongSDKProgressListener(new OnLanSongSDKProgressListener() {
                @Override
                public void onLanSongSDKProgress(long l, int i) {
                    dialogProgress = i;
                    handler.sendEmptyMessage(1);
                }
            });
            execute.setOnLanSongSDKCompletedListener(new OnLanSongSDKCompletedListener() {
                @Override
                public void onLanSongSDKCompleted(String s) {
                    //TODO  s为生成的视频的路径  然后跳转后面的导出页面
                    mCallback.dismissLoadingDialog();
                    execute.release();
                }
            });
            Observable.create(new Observable.OnSubscribe<Boolean>() {
                @Override
                public void call(Subscriber<? super Boolean> subscriber) {
                    if (!TextUtils.isEmpty(videoPath)) {
                        try {
                             LSOVideoOption option = new LSOVideoOption(videoPath);
                             option.setLooping(false);
                             //使用视频中的音频
                             if (changeMusicIndex == -1 || changeMusicIndex == 0) {
                                 option.setAudioVolume(1f);
                             } else {
                                 option.setAudioMute();
                                 //选择了提取音频作为视频的背景音频
                                 if (!TextUtils.isEmpty(chooseExtractedAudioBjMusicPath) && changeMusicIndex == 2) {
                                     execute.addAudioLayer(chooseExtractedAudioBjMusicPath, 0, 0, cutEndTime);
                                 }
                             }
                             option.setCutDurationUs(cutStartTime * 1000, cutEndTime * 1000);
                             Layer bgLayer = execute.addVideoLayer(option, cutStartTime * 1000, Long.MAX_VALUE, false, true);
                            if (!nowUiIsLandscape) {
                                bgLayer.setScaledToPadSize();
                                bgLayer.setScaleType(LSOScaleType.VIDEO_SCALE_TYPE);
                            } else {
                                float LayerWidth = bgLayer.getLayerWidth();
                                float scale = DRAWPADWIDTH / (float) LayerWidth;
                                float LayerHeight = bgLayer.getLayerHeight();
                                float needDrawHeight = LayerHeight * scale;
                                bgLayer.setScaledValue(DRAWPADWIDTH, needDrawHeight);
                                float halft = needDrawHeight / (float) 2;
                                float top = needDrawHeight * percentageH;
                                float needHeight = halft - top;
                                bgLayer.setPosition(bgLayer.getPositionX(), needHeight);
                            }
                         } catch (Exception e) {
                             e.printStackTrace();
                         }
                    } else {
                        Bitmap bt_nj = BitmapManager.getInstance().getOrientationBitmap(imagePath);
                        bt_nj = BitmapUtils.zoomImg2(bt_nj, execute.getPadWidth() / 16 * 16, execute.getPadHeight() / 16 * 16);
                        execute.addBitmapLayer(bt_nj, 0, Long.MAX_VALUE);
                        if (!TextUtils.isEmpty(chooseExtractedAudioBjMusicPath) && changeMusicIndex == 2) {
                            execute.addAudioLayer(chooseExtractedAudioBjMusicPath, 0, 0, cutEndTime);
                        }
                    }
                    addBitmapLayer();
                    boolean started = execute.start();
                    subscriber.onNext(started);
                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(aBoolean -> {
                if (!aBoolean) {
                    ToastUtil.showToast("导出失败");
                }
            }, throwable -> throwable.printStackTrace());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加字幕或者单个文本的玉体字的layer
     */
    private void addBitmapLayer() {
       //TODO 每段玉体字的展示添加逻辑
    }
}

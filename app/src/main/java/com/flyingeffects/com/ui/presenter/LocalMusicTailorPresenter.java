package com.flyingeffects.com.ui.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.flyingeffects.com.base.mvpBase.BasePresenter;
import com.flyingeffects.com.commonlyModel.GetPathType;
import com.flyingeffects.com.manager.DownloadVideoManage;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.manager.mediaManager;
import com.flyingeffects.com.ui.interfaces.model.LocalMusicTailorCallback;
import com.flyingeffects.com.ui.interfaces.view.LocalMusicTailorMvpView;
import com.flyingeffects.com.ui.model.LocalMusicTailorMvpModel;
import com.flyingeffects.com.ui.model.videoCutDurationForVideoOneDo;
import com.flyingeffects.com.utils.FileUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.utils.record.SamplePlayer;
import com.flyingeffects.com.utils.record.soundfile.SoundFile;
import com.flyingeffects.com.view.RangeSeekBarForMusicView;
import com.flyingeffects.com.view.RangeSeekBarView;
import com.flyingeffects.com.view.beans.Thumb;
import com.flyingeffects.com.view.interfaces.OnRangeSeekBarListener;
import com.shixing.sxve.ui.albumType;
import com.shixing.sxve.ui.view.WaitingDialog_progress;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * description ：逻辑
 * creation date: 2020/8/26
 * user : zhangtongju
 */
public class LocalMusicTailorPresenter extends BasePresenter implements LocalMusicTailorCallback {
    private LocalMusicTailorMvpView localMusicTailorMvpView;
    private LocalMusicTailorMvpModel localMusicTailorMvpModel;
    private WaitingDialog_progress downProgressDialog;
    private String mVideoFolder;
    private Context context;
    boolean nowMaterialIsVideo = false;
    private String soundFolder;
    private String soundCutFolder;
    private Timer timer;
    private TimerTask task;
    //视频需要裁剪时长，毫秒
    private int needDuration;
    private int nowTimerDuration;


    public LocalMusicTailorPresenter(Context context, LocalMusicTailorMvpView localMusicTailorMvpView) {
        this.localMusicTailorMvpView = localMusicTailorMvpView;
        localMusicTailorMvpModel = new LocalMusicTailorMvpModel(context, this);
        FileManager fileManager = new FileManager();
        this.context = context;
        mVideoFolder = fileManager.getFileCachePath(context, "downVideoForMusic");
        soundFolder = fileManager.getFileCachePath(context, "downSoundForMusic");
        soundCutFolder = fileManager.getFileCachePath(context, "downCutSoundForMusic");
    }


    boolean isCanSeek=true;
    public void SeekToPositionMusic(int position) {


        

        if (mPlayer != null) {
            mPlayer.start();
            mPlayer.seekTo(position);
            startTimer();
        }
    }



    public String getSoundMusicPath(){
        return  localMusicTailorMvpModel.getSoundPath();
    }

    public void setNeedDuration(int needDuration) {
        this.needDuration = needDuration;
    }


    public void pauseMusic() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
        }
    }


    public void DownPath(String path) {
        LogUtil.d("OOM2", "DownPath");
        toDownVideo(path);
    }

    public void OnDestroy() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
            mPlayer.release();
        }
    }


    /**
     * description ：下载视频
     * creation date: 2020/8/26
     * user : zhangtongju
     */
    private void toDownVideo(String path) {
        if (!TextUtils.isEmpty(path) && !path.contains("http")) {
            String mimeType = GetPathType.getInstance().getPathType(path);
            if (albumType.isVideo(mimeType)) {
                LogUtil.d("OOM", "当前的地址是本地视频地址" + path);
                toSplitMp4(path);
            } else {
                LogUtil.d("OOM", "当前的地址是本地音频地址" + path);
                String nowPath = soundCutFolder + "/audio.mp3";
                try {
                    FileUtil.copyFile(new File(path), nowPath);
                    localMusicTailorMvpModel.setSoundPath(nowPath);
                    requestSoundData(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            LogUtil.d("OOM", "当前的地址是网络地址" + path);
            String videoName;
            String mimeType = GetPathType.getInstance().getPathType(path);
            if (albumType.isVideo(mimeType)) {
                LogUtil.d("OOM", "当前的地址是网络视频音频地址" + path);
                nowMaterialIsVideo = true;
                videoName = mVideoFolder + File.separator + "downPath.mp4";
            } else {
                videoName = mVideoFolder + File.separator + "downPath.mp3";
            }
            File file = new File(videoName);
            if (file.exists()) {
                boolean tag = file.delete();
                LogUtil.d("OOM2", "删除文件" + tag);
            }
            if (downProgressDialog == null) {
                downProgressDialog = new WaitingDialog_progress(context);
                downProgressDialog.openProgressDialog();
            }
            Observable.just(path).subscribeOn(Schedulers.io()).subscribe(s -> {
                DownloadVideoManage manage = new DownloadVideoManage(isSuccess -> Observable.just(videoName).subscribeOn(AndroidSchedulers.mainThread()).subscribe(s1 -> {
                    LogUtil.d("OOM2", "s1=" + s1);
                    if (nowMaterialIsVideo) {
                        //当前是视频的情况下，需要分离出音频
                        downProgressDialog.setProgress("得到音频文件");
                        toSplitMp4(s1);
                    } else {
                        requestSoundData(s1);
                        localMusicTailorMvpModel.setSoundPath(s1);

                    }
                }));
                LogUtil.d("OOM2", "path=" + path);
                manage.DownloadVideo(path, videoName);
            });
        }
    }


    /**
     * description ：分离音视频
     * creation date: 2020/8/26
     * user : zhangtongju
     */
    private void toSplitMp4(String videoPath) {
        mediaManager manager = new mediaManager(context);
        manager.splitMp4(videoPath, new File(soundFolder), (isSuccess, putPath) -> {
            if (isSuccess) {
                LogUtil.d("OOM2", "分离出来的因为地址为" + soundFolder);
                String videoVoicePath = soundFolder + File.separator + "bgm.mp3";
                localMusicTailorMvpModel.setSoundPath(videoVoicePath);
                requestSoundData(videoVoicePath);
            } else {
                LogUtil.d("OOM2", "分离出来的因为地址为null");
                localMusicTailorMvpModel.setSoundPath("");
                if (downProgressDialog != null) {
                    downProgressDialog.closePragressDialog();
                    downProgressDialog = null;
                }
            }
        });
    }


    /**
     * description ：请求音频的波形点
     * creation date: 2020/8/26
     * user : zhangtongju
     */
    public void requestSoundData(String url) {
        String mFilename = url.replaceFirst("file://", "").replaceAll("%20", " ");
        if (!mFilename.equals("record")) {
            loadFromFile(mFilename);
        }
    }


    private File mFile;
    private SoundFile mSoundFile;
    private boolean mLoadingKeepGoing = true;
    private long mLoadingLastUpdateTime;
    private SamplePlayer mPlayer;

    private void loadFromFile(String mFilename) {
        LogUtil.d("OOM2", "loadFromFile" + "mFilename=" + mFilename);
        mFile = new File(mFilename);
        final SoundFile.ProgressListener listener =
                fractionComplete -> {
                    long now = getCurrentTime();
                    if (now - mLoadingLastUpdateTime > 100) {
                        mLoadingLastUpdateTime = now;
                        LogUtil.d("OOM2", "导入进度为" + (int) (100 * fractionComplete));
                    }
                    return mLoadingKeepGoing;
                };

        Thread mLoadSoundFileThread = new Thread() {
            @Override
            public void run() {
                try {
                    mSoundFile = SoundFile.create(mFile.getAbsolutePath(), listener);
                    if (mSoundFile == null) {
                        String name = mFile.getName().toLowerCase();
                        String[] components = name.split("\\.");
                        String err;
                        if (components.length < 2) {
                            err = "抱歉，Ringdroid不能编辑无扩展名（如.mp3、.wav）的文件";
                        } else {
                            err = "抱歉，Ringdroid尚不能编辑有类型的文件。 " +
                                    components[components.length - 1];
                        }
                        LogUtil.d("OOM2", err);
                        return;
                    }
                    mPlayer = new SamplePlayer(mSoundFile);
                    mPlayer.setOnCompletionListener(() -> localMusicTailorMvpView.onPlayerCompletion());
                    mPlayer.start();
                    startTimer();
                    localMusicTailorMvpView.initComplate();
                } catch (final Exception e) {
                    e.printStackTrace();
                    String mInfoContent = e.toString();
                    LogUtil.d("OOM2", mInfoContent);
//                    ToastUtil.showToast(mInfoContent);
                    return;
                }
                if (mLoadingKeepGoing) {
                    finishOpeningSoundFile();
                }
            }
        };
        mLoadSoundFileThread.start();
    }


    private void finishOpeningSoundFile() {
        Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
            if (downProgressDialog != null) {
                downProgressDialog.closePragressDialog();
                downProgressDialog = null;
            }
            String mCaption =
                    mSoundFile.getFiletype() + ", " +
                            mSoundFile.getSampleRate() + " Hz, " +
                            mSoundFile.getAvgBitrateKbps() + " kbps, ";
            LogUtil.d("OOM2", mCaption + "波纹点大小" + mSoundFile.getNumFrames());
            LogUtil.d("OOM2", "mSoundFile.getNumFrames()=" + mSoundFile.getNumFrames());
            localMusicTailorMvpModel.setChartData(mSoundFile.getFrameGains(), mSoundFile.getNumFrames());
            localMusicTailorMvpView.showCharView(mSoundFile.getFrameGains(), mSoundFile.getNumFrames());
        });
    }


    private long getCurrentTime() {
        return System.nanoTime() / 1000000;
    }


    /***
     * 倒计时60s
     */
    private void startTimer() {
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        };
        timer.schedule(task, 0, 500);
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    nowTimerDuration += 500;
                    if (nowTimerDuration == needDuration) {
                        nowTimerDuration = 0;
                        endTimer();
                    }
                    break;


                default:

                    break;
            }
        }
    };


    /**
     * 关闭timer 和task
     */
    private void endTimer() {
        pauseMusic();
        localMusicTailorMvpView.onPlayerCompletion();
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
    }


    /**
     * description ：保存裁剪方法
     * creation date: 2020/9/1
     * user : zhangtongju
     */
    public void toSaveCutMusic(long startTimer, long endTimer) {
        showProgressDialog();
        videoCutDurationForVideoOneDo.getInstance().cuttingAudio(context, localMusicTailorMvpModel.getSoundPath(), startTimer, endTimer, new videoCutDurationForVideoOneDo.cutAudioCallback() {
            @Override
            public void isDone(String path) {

                if (!TextUtils.isEmpty(path)) {
                    closeProgress();
                    String SoundPath = soundCutFolder + "/audio.mp3";
                    File file = new File(SoundPath);
                    if (file.exists()) {
                        file.delete();
                    }
                    try {
                        FileUtil.copyFile(new File(path), SoundPath);
                        localMusicTailorMvpView.isAudioCutDone(SoundPath,localMusicTailorMvpModel.getSoundPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                        LogUtil.d("OOM2", "复制文件报错" + e.getMessage());
                    }
                } else {
                    closeProgress();
                    ToastUtil.showToast("不支持该视频");
                }

            }

            @Override
            public void isProgress(long i) {
                Observable.just(i).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        // downProgressDialog.setProgress(aLong+"%");
                    }
                });
            }
        });
    }


    private void showProgressDialog() {
        if (downProgressDialog == null) {
            downProgressDialog = new WaitingDialog_progress(context);
            downProgressDialog.openProgressDialog();
        } else {
            downProgressDialog.openProgressDialog();
        }
    }


    private void closeProgress() {
        if (downProgressDialog != null) {
            downProgressDialog.closePragressDialog();
            downProgressDialog = null;
        }
    }




    /**
     * description ：初始化拖动条
     * creation date: 2021/2/19
     * user : zhangtongju
     */
    private RangeSeekBarForMusicView mRangeSeekBarView;
    public void InitRangeSeekBar(RangeSeekBarForMusicView mRangeSeekBarView){
        this.mRangeSeekBarView=mRangeSeekBarView;
        mRangeSeekBarView.addOnRangeSeekBarListener(new OnRangeSeekBarListener() {
            @Override
            public void onCreate(RangeSeekBarView rangeSeekBarView, int index, float value) {
                // Do nothing
            }

            @Override
            public void onSeek(RangeSeekBarView rangeSeekBarView, int index, float value) {
            }

            @Override
            public void onSeekStart(RangeSeekBarView rangeSeekBarView, int index, float value) {
                // Do nothing
            }

            @Override
            public void onSeekStop(RangeSeekBarView rangeSeekBarView, int index, float value) {
            }

            @Override
            public void onCreate(RangeSeekBarForMusicView rangeSeekBarView, int index, float value) {

            }

            @Override
            public void onSeek(RangeSeekBarForMusicView rangeSeekBarView, int index, float value) {
                if(index==Thumb.LEFT){
                    onStopSeekThumbs(index, value);
                    LogUtil.d("OOM","onSeek");
                }

            }

            @Override
            public void onSeekStart(RangeSeekBarForMusicView rangeSeekBarView, int index, float value) {

            }

            @Override
            public void onSeekStop(RangeSeekBarForMusicView rangeSeekBarView, int index, float value) {

                if(index==Thumb.LEFT){
                    onStopSeekThumbs(index, value);
                    LogUtil.d("OOM","onSeekStop");
                }
            }
        });
        setSeekBarPosition();
        mRangeSeekBarView.setMinDistance(30);
        mRangeSeekBarView.initMaxWidth();
    }


    private void setSeekBarPosition() {
        new Handler().postDelayed(() -> mRangeSeekBarView.initMaxWidth(),500);

    }

    private void onStopSeekThumbs(int index, float value) {
        float percent=value/(float)100;
        localMusicTailorMvpView.onStopSeekThumbs(percent);
    }



}

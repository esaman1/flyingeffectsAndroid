package com.flyingeffects.com.ui.view.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;

import com.flyingeffects.com.utils.timeUtils;

import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.commonlyModel.SaveAlbumPathModel;
import com.flyingeffects.com.commonlyModel.getVideoInfo;
import com.flyingeffects.com.enity.VideoInfo;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.utils.FileUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;


/**
 * description ：自定义创作预览界面，其实就是保存之后的预览页面，包含返回编辑和保存功能
 * creation date: 2020/4/20
 * user : zhangtongju
 */
public class CreationTemplatePreviewActivity extends BaseActivity {


    private ExoPlayer exoPlayer;

    @BindView(R.id.exo_player)
    PlayerView playerView;

    @BindView(R.id.seekBar)
    SeekBar seekBar;


    @BindView(R.id.tv_end_time)
    TextView tv_end_time;

    private String imagePath;
    private long mEndDuration;

    private VideoInfo videoInfo;

    @BindView(R.id.tv_start_time)
    TextView tv_start_time;

    @BindView(R.id.iv_play)
    ImageView iv_play;

    private timeUtils timeUtils;

    @Override
    protected int getLayoutId() {
        return R.layout.act_creation_template_preview;
    }

    @Override
    protected void initView() {
        imagePath = getIntent().getStringExtra("path");
        videoInfo = getVideoInfo.getInstance().getRingDuring(imagePath);
        timeUtils = new timeUtils();
        tv_end_time.setText(timeUtils.timeParse(videoInfo.getDuration()));
        exoPlayer = ExoPlayerFactory.newSimpleInstance(CreationTemplatePreviewActivity.this, new DefaultRenderersFactory(this), new DefaultTrackSelector(), new DefaultLoadControl());
        playerView.setPlayer(exoPlayer);

        //不使用控制器
        playerView.setUseController(false);
        exoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
        exoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case Player.STATE_READY:
                        mEndDuration = exoPlayer.getContentDuration();
                        videoPlay();
                        break;
                    case Player.STATE_ENDED:
                        seekTo(0);
                        break;
                    case Player.STATE_BUFFERING:
                    case Player.STATE_IDLE:
                    default:
                        break;
                }
            }
        });
        MediaSource mediaSource = new ExtractorMediaSource.Factory(
                new DefaultDataSourceFactory(CreationTemplatePreviewActivity.this, "exoplayer-codelab")).
                createMediaSource(Uri.fromFile(new File(imagePath)));
        exoPlayer.prepare(mediaSource, true, false);
        videoPause();
    }

    @Override
    protected void initAction() {

    }


    private void seekTo(long to) {
        if (exoPlayer != null) {
            exoPlayer.seekTo(to);
        }
    }

    private void videoPlay() {
        if (exoPlayer != null) {
            LogUtil.d("video", "play");
            exoPlayer.setPlayWhenReady(true);
        }
        startTimer();
    }

    private void videoPause() {
        if (exoPlayer != null) {
            LogUtil.d("video", "videoPause");
            exoPlayer.setPlayWhenReady(false);

        }
    }


    private void saveToAlbum(String path) {
        String albumPath = SaveAlbumPathModel.getInstance().getKeepOutput();
        try {
            FileUtil.copyFile(new File(path), albumPath);
            albumBroadcast(albumPath);
            showKeepSuccessDialog(albumPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * description ：通知相册更新
     * date: ：2019/8/16 14:24
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    private void albumBroadcast(String outputFile) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(new File(outputFile)));
        this.sendBroadcast(intent);
    }


    private void showKeepSuccessDialog(String path) {
        if (!DoubleClick.getInstance().isFastDoubleClick()) {
            AlertDialog.Builder builder = new AlertDialog.Builder( //去除黑边
                    new ContextThemeWrapper(CreationTemplatePreviewActivity.this, R.style.Theme_Transparent));
            builder.setTitle(R.string.notification);
            builder.setMessage("已为你保存到相册,多多分享给友友\n" + "【" + path + this.getString(R.string.folder) + "】"
            );
            builder.setNegativeButton(this.getString(R.string.got_it), (dialog, which) -> dialog.dismiss());
            builder.setCancelable(true);
            Dialog dialog = builder.show();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }


    @OnClick({R.id.tv_back, R.id.tv_save, R.id.iv_play})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                CreationTemplatePreviewActivity.this.finish();

                break;

            case R.id.tv_save:
                saveToAlbum(imagePath);
                break;

            case R.id.iv_play:
                if (isPlaying()) {
                    showIsPlay(false);
                    videoStop();
                    destroyTimer();

                } else {
                    videoPlay();
                    showIsPlay(true);
                }
                break;

        }
        super.onClick(v);
    }


    /**
     * 是否正在播放
     */
    private boolean isPlaying() {
        return exoPlayer != null &&
                exoPlayer.getPlaybackState() == Player.STATE_READY &&
                exoPlayer.getPlayWhenReady();
    }


    public void showIsPlay(boolean isPlay) {
        if (isPlay) {
            iv_play.setImageResource(R.mipmap.pause);
        } else {
            iv_play.setImageResource(R.mipmap.iv_play);
        }
    }


    /**
     * user :TongJu  ; email:jutongzhang@sina.com
     * time：2018/10/15
     * describe:严防内存泄露
     **/
    private void destroyTimer() {
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

    private Timer timer;
    private TimerTask task;

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
                Observable.just(1).observeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
                    if (exoPlayer != null) {
                        exoPlayer.setPlayWhenReady(true);
                        if (getCurrentPos() >= mEndDuration) {
                            exoPlayer.seekTo(0);
                        } else if (getCurrentPos() < 0) {
                            exoPlayer.seekTo(0);
                        }

                    }
                    float progress = getCurrentPos() / (float) mEndDuration;
                    int realPosition = (int) (progress * 100);
                    timeUtils = new timeUtils();
                    tv_start_time.setText(timeUtils.timeParse(getCurrentPos()));
                    seekBar.setProgress(realPosition);
                });
            }
        };
        timer.schedule(task, 0, 16);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        videoStop();
        destroyTimer();
    }

    /**
     * 释放资源
     */
    private void videoStop() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
        }
    }

    /**
     * 获取当前进度
     */
    private long getCurrentPos() {
        return exoPlayer != null ? exoPlayer.getCurrentPosition() : 0;

    }


}

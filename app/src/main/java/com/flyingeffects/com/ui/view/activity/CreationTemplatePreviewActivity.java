package com.flyingeffects.com.ui.view.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.util.Common;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.commonlyModel.SaveAlbumPathModel;
import com.flyingeffects.com.commonlyModel.getVideoInfo;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.constans.UiStep;
import com.flyingeffects.com.enity.VideoInfo;
import com.flyingeffects.com.enity.showAdCallback;
import com.flyingeffects.com.manager.AdConfigs;
import com.flyingeffects.com.manager.AdManager;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.manager.StimulateControlManage;
import com.flyingeffects.com.ui.interfaces.view.CreationTemplatePreviewMvpView;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.ui.model.ShowPraiseModel;
import com.flyingeffects.com.ui.presenter.CreationTemplatePreviewPresenter;
import com.flyingeffects.com.ui.view.dialog.CommonMessageDialog;
import com.flyingeffects.com.utils.FileUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.TimeUtils;
import com.flyingeffects.com.utils.record.SaveShareDialog;
import com.flyingeffects.com.view.RangeSeekBarView;
import com.flyingeffects.com.view.RoundImageView;
import com.flyingeffects.com.view.VideoFrameRecycler;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.nineton.ntadsdk.itr.VideoAdCallBack;
import com.nineton.ntadsdk.manager.VideoAdManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;


/**
 * description ：自定义创作预览界面，其实就是保存之后的预览页面，包含返回编辑和保存功能
 * creation date: 2020/4/20
 * user : zhangtongju
 */
public class CreationTemplatePreviewActivity extends BaseActivity implements CreationTemplatePreviewMvpView {

    private SimpleExoPlayer exoPlayer;

    @BindView(R.id.exo_player)
    PlayerView playerView;

    private Context mContext;

    private String imagePath;

    private MediaSource mediaSource;

    private boolean isIntoPause = false;

    private CreationTemplatePreviewPresenter Presenter;

    @BindView(R.id.timeLineBar)
    RangeSeekBarView mRangeSeekBarView;

    @BindView(R.id.timeLineView)
    VideoFrameRecycler mTimeLineView;

    @BindView(R.id.videocrop_cursor)
    RoundImageView progressCursor;

    @BindView(R.id.iv_play)
    ImageView iv_play;


    @BindView(R.id.tv_duration)
    TextView tv_duration;
    @BindView(R.id.dialog_share)
    LinearLayout dialogShare;

    boolean isIntoInitTrimmer = false;

    private boolean nowUiIsLandscape;

    private boolean isShowPreviewAd = false;

    private ArrayList<String> titleEffect;
    private ArrayList<String> titleStyle;
    private ArrayList<String> titleFrame;
    SaveShareDialog mShareDialog;
    String templateTitle;


    @Override
    protected int getLayoutId() {
        return R.layout.act_creation_template_preview;
    }

    @Override
    protected void initView() {
        mContext = CreationTemplatePreviewActivity.this;
        EventBus.getDefault().register(this);
        Bundle bundle = getIntent().getBundleExtra("bundle");
        imagePath = bundle.getString("path");
        titleEffect = bundle.getStringArrayList("titleEffect");
        LogUtil.d("OOM3", StringUtil.beanToJSONString(titleEffect));
        titleStyle = bundle.getStringArrayList("titleStyle");
        titleFrame = bundle.getStringArrayList("titleFrame");
        templateTitle = bundle.getString("templateTitle");
        if (titleFrame != null && titleFrame.size() > 0) {
            LogUtil.d("OOM3", "titleFrameSize=" + titleFrame.get(0));
        }
        nowUiIsLandscape = bundle.getBoolean("nowUiIsLandscape", false);
        Presenter = new CreationTemplatePreviewPresenter(this, this, imagePath);
        VideoInfo videoInfo = getVideoInfo.getInstance().getRingDuring(imagePath);
        LogUtil.d("OOM", "TimeUtils.timeParse(videoInfo.getDuration())=" + TimeUtils.timeParse(videoInfo.getDuration()));
        tv_duration.setText(TimeUtils.timeParse(videoInfo.getDuration()));
        initExo();
        mShareDialog = new SaveShareDialog(this, dialogShare);
    }

    @Override
    protected void initAction() {

//        showIsPlay(true);
    }


    private void seekTo(long to) {
        if (exoPlayer != null) {
            exoPlayer.seekTo(to);
        }
    }

    private void videoPlay() {
        new Handler().postDelayed(() -> {
            if (exoPlayer != null) {
                LogUtil.d("video", "play");
                exoPlayer.setPlayWhenReady(true);
            }
        }, 200);


//        startTimer();
    }

    private void videoPause() {

        if (exoPlayer != null) {
            exoPlayer.stop();
        }
        iv_play.setVisibility(View.VISIBLE);
        hideCursor();
//        destroyTimer();
    }


    private void videoPause2() {
        if (exoPlayer != null) {
            LogUtil.d("video", "videoPause");
            exoPlayer.setPlayWhenReady(false);
        }
    }


    public void hideCursor() {
//        playIcon.setVisibility(View.VISIBLE);
        progressCursor.setVisibility(View.INVISIBLE);
    }


    /**
     * description ：保存在相册，如果isAdSuccess等于false 那么就显示激励视频
     * creation date: 2020/6/28
     * user : zhangtongju
     */
    private void saveToAlbum(String path, boolean hasShowStimulateAd) {
        if (!isOndesTroy) {
            String albumPath = SaveAlbumPathModel.getInstance().getKeepOutput();
            try {
                if (!hasShowStimulateAd) {
                    if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
                        AdManager.getInstance().showCpAd(CreationTemplatePreviewActivity.this, AdConfigs.AD_SCREEN_FOR_keep);
                    }
                }
                FileUtil.copyFile(new File(path), albumPath);
                albumBroadcast(albumPath);
                showKeepSuccessDialog(albumPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
            ShowPraiseModel.keepAlbumCount();
            mShareDialog.createDialog(templateTitle);
            mShareDialog.setVideoPath(path);
        }
    }


    @Override
    @OnClick({R.id.tv_back, R.id.tv_save, R.id.rela_parent_content})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                if (UiStep.isFromDownBj) {
                    StatisticsEventAffair.getInstance().setFlag(this, "7_return");
                } else {
                    StatisticsEventAffair.getInstance().setFlag(this, "8_return");
                }

                StatisticsEventAffair.getInstance().setFlag(this, "7_return");
                finish();
                break;

            case R.id.tv_save:
                statisticsEventAffair();
                if (UiStep.isFromDownBj) {
                    StatisticsEventAffair.getInstance().setFlag(this, "7_save");
                } else {
                    StatisticsEventAffair.getInstance().setFlag(this, "8_save");
                }
                StimulateControlManage.getInstance().InitRefreshStimulate();
                if (BaseConstans.getHasAdvertising() == 1 && BaseConstans.getIncentiveVideo() && !BaseConstans.getIsNewUser() && BaseConstans.getSave_video_ad() && !BaseConstans.TemplateHasWatchingAd) {
//                    Intent intent = new Intent(CreationTemplatePreviewActivity.this, AdHintActivity.class);
//                    intent.putExtra("from", "isFormPreviewVideo");
//                    intent.putExtra("templateTitle", "");
//                    startActivity(intent);
                    showMessageDialog();
                } else {
                    videoPause();
                    Presenter.destroyTimer();
                    Presenter.toSaveVideo(false, nowUiIsLandscape);
                }
                break;
            case R.id.rela_parent_content:
                if (isPlaying()) {
                    videoPause();
                } else {
                    videoResume();
                }
                if (!isShowPreviewAd && BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
                    AdManager.getInstance().showCpAd(this, AdConfigs.AD_SCREEN_FOR_PREVIEW);
                    isShowPreviewAd = true;
                }
                break;
            default:
                break;
        }
        super.onClick(v);
    }

    private void showMessageDialog() {
        StatisticsEventAffair.getInstance().setFlag(mContext, "video_ad_alert", "");
        CommonMessageDialog.getBuilder(mContext)
                .setContentView(R.layout.dialog_common_message_ad_under)
                .setAdStatus(CommonMessageDialog.AD_STATUS_BOTTOM)
                .setAdId(AdConfigs.AD_IMAGE_DIALOG_OPEN_VIDEO)
                .setTitle("亲爱的友友")
                .setMessage("这个模板需要观看几秒广告")
                .setMessage2("「看完后就能制作飞闪视频」")
                .setPositiveButton("观看广告并制作")
                .setNegativeButton("取消")
                .setDialogBtnClickListener(new CommonMessageDialog.DialogBtnClickListener() {
                    @Override
                    public void onPositiveBtnClick(CommonMessageDialog dialog) {
                        StatisticsEventAffair.getInstance().setFlag(mContext, "bj_ad_open", "");
                        StatisticsEventAffair.getInstance().setFlag(mContext, "video_ad_alert_click_confirm");
                        EventBus.getDefault().post(new showAdCallback("isFormPreviewVideo"));
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelBtnClick(CommonMessageDialog dialog) {
                        //取消
                        StatisticsEventAffair.getInstance().setFlag(mContext, "bj_ad_cancel", "");
                        StatisticsEventAffair.getInstance().setFlag(mContext, "video_ad_alert_click_cancel");
                        dialog.dismiss();
                    }
                })
                .build().show();
    }



    private void statisticsEventAffair() {
        if (titleEffect != null && titleEffect.size() > 0) {
            for (String str : titleEffect) {
                if (!TextUtils.isEmpty(str)) {
                    StatisticsEventAffair.getInstance().setFlag(CreationTemplatePreviewActivity.this, "20_bj_text_style_save", str);
                    LogUtil.d("OOM3", "titleEffect=" + str);
                }
            }
        }


        if (titleStyle != null && titleStyle.size() > 0) {
            for (String str : titleStyle) {
                if (!TextUtils.isEmpty(str)) {
                    StatisticsEventAffair.getInstance().setFlag(CreationTemplatePreviewActivity.this, "20_bj_text_font_save", str);
                    LogUtil.d("OOM3", "titleStyle=" + str);
                }
            }
        }


        if (titleFrame != null && titleFrame.size() > 0) {
            for (String str : titleFrame) {
                if (!TextUtils.isEmpty(str)) {
                    StatisticsEventAffair.getInstance().setFlag(CreationTemplatePreviewActivity.this, "20_bj_text_border_save", str);
                    LogUtil.d("OOM3", "titleFrame=" + str);
                }
            }
        }

        if ((titleStyle != null && titleStyle.size() > 0) || (titleEffect != null && titleEffect.size() > 0)) {
            StatisticsEventAffair.getInstance().setFlag(CreationTemplatePreviewActivity.this, "20_bj_text_save_save");
        }


    }


    private void videoResume() {
        try {
            showCursor();
            //  Presenter.initTimer();
            iv_play.setVisibility(View.GONE);
            if (exoPlayer != null) {
                exoPlayer.prepare(mediaSource, false, false);
            }

        } catch (NullPointerException | IllegalStateException e) {
            LogUtil.d("OOM", e.getMessage());
        }
    }

    public void showCursor() {
        progressCursor.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isIntoPause) {
            if (exoPlayer != null) {
                if (isPlaying()) {
                    videoPause();
                } else {
                    videoResume();
                }
            } else {
                initExo();
            }
            isIntoPause = false;
        }

    }


    private void initExo() {
        exoPlayer = ExoPlayerFactory.newSimpleInstance(CreationTemplatePreviewActivity.this);
        playerView.setPlayer(exoPlayer);
        //不使用控制器
        playerView.setUseController(false);
        exoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
        exoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case Player.STATE_READY:
                        videoPlay();
                        if (!isIntoInitTrimmer) {
                            Presenter.setUpTrimmer(mRangeSeekBarView, mTimeLineView, progressCursor, exoPlayer.getDuration());
                            isIntoInitTrimmer = true;
                        }
                        Presenter.initTimer();
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

        mediaSource = new ExtractorMediaSource.Factory(
                new DefaultDataSourceFactory(CreationTemplatePreviewActivity.this, "exoplayer-codelab")).
                createMediaSource(Uri.fromFile(new File(imagePath)));
        exoPlayer.prepare(mediaSource, true, false);
        videoPause2();
    }


    private void videoOnResume() {
        if (exoPlayer != null) {
            exoPlayer.prepare(mediaSource, false, false);
        }
    }


    /**
     * 是否正在播放
     */
    private boolean isPlaying() {
        return exoPlayer != null &&
                exoPlayer.getPlaybackState() == Player.STATE_READY &&
                exoPlayer.getPlayWhenReady();
    }


//    public void showIsPlay(boolean isPlay) {
//        if (isPlay) {
//            iv_play.setImageResource(R.mipmap.pause);
//        } else {
//            iv_play.setImageResource(R.mipmap.iv_play);
//        }
//    }


    /**
     * user :TongJu  ; email:jutongzhang@sina.com
     * time：2018/10/15
     * describe:严防内存泄露
     **/
//    private void destroyTimer() {
//        if (timer != null) {
//            timer.purge();
//            timer.cancel();
//            timer = null;
//        }
//        if (task != null) {
//            task.cancel();
//            task = null;
//        }
//    }

//    private Timer timer;
//    private TimerTask task;
//
//    private void startTimer() {
//        if (timer != null) {
//            timer.purge();
//            timer.cancel();
//            timer = null;
//        }
//        if (task != null) {
//            task.cancel();
//            task = null;
//        }
//
//        timer = new Timer();
//        task = new TimerTask() {
//            @Override
//            public void run() {
//                Observable.just(1).observeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
//                    if (exoPlayer != null) {
//                        exoPlayer.setPlayWhenReady(true);
//                        if (getCurrentPos() >= mEndDuration) {
//                            exoPlayer.seekTo(0);
//                        } else if (getCurrentPos() < 0) {
//                            exoPlayer.seekTo(0);
//                        }
//
//                    }
//                    float progress = getCurrentPos() / (float) mEndDuration;
////                    int realPosition = (int) (progress * 100);
////                    TimeUtils = new TimeUtils();
////                    tv_start_time.setText(TimeUtils.timeParse(getCurrentPos()));
////                    seekBar.setProgress(realPosition);
//                });
//            }
//        };
//        timer.schedule(task, 0, 16);
//    }


    boolean isOndesTroy = false;

    @Override
    public void onDestroy() {
        super.onDestroy();
        videoStop();
        isOndesTroy = true;
//        destroyTimer();
        EventBus.getDefault().unregister(this);
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


    @Override
    protected void onPause() {
        super.onPause();
        isIntoPause = true;
        if (isPlaying()) {

//            showIsPlay(false);
            videoPause();
//            destroyTimer();
        }
    }


    @Subscribe
    public void onEventMainThread(showAdCallback event) {
        if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
            videoPause();
//            destroyTimer();
            VideoAdManager videoAdManager = new VideoAdManager();
            String adId;
            if (BaseConstans.getOddNum()) {
                adId = AdConfigs.AD_save_video;
            } else {
                adId = AdConfigs.AD_save_video2;
            }
            videoAdManager.showVideoAd(this, adId, new VideoAdCallBack() {
                @Override
                public void onVideoAdSuccess() {
                    onPause();
                    StatisticsEventAffair.getInstance().setFlag(CreationTemplatePreviewActivity.this, "video_ad_alert_request_sucess");
                    LogUtil.d("OOM", "onVideoAdSuccess");
                }

                @Override
                public void onVideoAdError(String s) {
                    StatisticsEventAffair.getInstance().setFlag(CreationTemplatePreviewActivity.this, "video_ad_alert_request_fail");
                    LogUtil.d("OOM", "onVideoAdError" + s);
                    videoPause();
                    Presenter.toSaveVideo(false, nowUiIsLandscape);
                }

                @Override
                public void onVideoAdClose() {
                    videoPause();
                    Presenter.toSaveVideo(true, nowUiIsLandscape);
                }

                @Override
                public void onRewardVerify() {
                    
                }

                @Override
                public void onVideoAdSkip() {
                    LogUtil.d("OOM", "onVideoAdSkip");
                }

                @Override
                public void onVideoAdComplete() {
                }

                @Override
                public void onVideoAdClicked() {
                    LogUtil.d("OOM", "onVideoAdClicked");
                }
            });
        } else {
            saveToAlbum(imagePath, true);
        }


    }


    /**
     * description ：更新底部游标
     * creation date: 2020/6/28
     * user : zhangtongju
     */
    @Override
    public void updateCursor(float currentX) {
        this.runOnUiThread(() -> {
            progressCursor.setTranslationX(currentX);
        });
    }

    @Override
    public void seekToPosition(long position, float allDuration) {
        LogUtil.d("OOM", "allDuration=" + allDuration);
        exoPlayer.seekTo(position);
        if (allDuration != 0) {
            tv_duration.setText(TimeUtils.timeParse((long) allDuration));
        }
    }

    @Override
    public void isSaveToAlbum(String path, boolean isAdSuccess) {
        if (!isOndesTroy) {
            saveToAlbum(path, isAdSuccess);
        }


    }
}

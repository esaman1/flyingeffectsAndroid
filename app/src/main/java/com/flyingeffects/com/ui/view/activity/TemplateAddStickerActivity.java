package com.flyingeffects.com.ui.view.activity;

import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.showAdCallback;
import com.flyingeffects.com.manager.AdConfigs;
import com.flyingeffects.com.manager.AdManager;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.view.TemplateAddStickerMvpView;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.ui.model.TemplateKeepStatistics;
import com.flyingeffects.com.ui.presenter.TemplateAddStickerMvpPresenter;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.screenUtil;
import com.flyingeffects.com.view.HorizontalListView;
import com.flyingeffects.com.view.MyScrollView;
import com.flyingeffects.com.view.mine.CreateViewForAddText;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.lansosdk.box.ViewLayerRelativeLayout;
import com.nineton.ntadsdk.itr.VideoAdCallBack;
import com.nineton.ntadsdk.manager.VideoAdManager;
import com.shixing.sxve.ui.view.WaitingDialog;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import androidx.viewpager.widget.ViewPager;

import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;


/**
 * description ：模板页面保存后添加贴纸
 * creation date: 2020/9/4
 * user : zhangtongju
 */
public class TemplateAddStickerActivity extends BaseActivity implements TemplateAddStickerMvpView {

    private TemplateAddStickerMvpPresenter presenter;
    private String videoPath;

    @BindView(R.id.exo_player)
    PlayerView playerView;

    @BindView(R.id.iv_play)
    ImageView ivPlay;

    private boolean isIntoPause = false;

    private int allVideoDuration;

    @BindView(R.id.ll_space)
    LinearLayout ll_space;

    /**
     * 是否初始化过播放器
     */
    private boolean isInitVideoLayer = false;

    private TimerTask task;

    private Timer timer;

    @BindView(R.id.scrollView)
    MyScrollView scrollView;

    @BindView(R.id.id_vview_realtime_gllayout)
    ViewLayerRelativeLayout viewLayerRelativeLayout;

    private SimpleExoPlayer exoPlayer;

//    private MediaSource mediaSource;

    private boolean isPlayComplate = false;

    /**
     * 当前预览状态，是否在播放中
     */
    private boolean isPlaying = false;

    @BindView(R.id.iv_list)
    HorizontalListView hListView;


    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.ll_add_text_style)
    LinearLayout ll_add_text_style;
    @BindView(R.id.dialog_share)
    LinearLayout dialogShare;

    private boolean isShowPreviewAd = false;

    private String mIsFrom;
    private String title;
    private String templateId;

    @Override
    protected int getLayoutId() {
        return R.layout.act_template_add_sticker;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        videoPath = getIntent().getStringExtra("videoPath");
        title = getIntent().getStringExtra("title");
        LogUtil.d("OOM", "path=" + videoPath);
        mIsFrom = getIntent().getStringExtra("IsFrom");
        templateId=getIntent().getStringExtra("templateId");
        presenter = new TemplateAddStickerMvpPresenter(this, this, ll_space, viewLayerRelativeLayout, videoPath, dialogShare, title);
        if (!TextUtils.isEmpty(videoPath)) {
            //有视频的时候，初始化视频值
            presenter.setPlayerViewSize(playerView, scrollView, viewLayerRelativeLayout);
            initExo(videoPath);
        }
        presenter.initBottomLayout(viewPager, getSupportFragmentManager());
        initViewLayerRelative();
        ((TextView) findViewById(R.id.tv_top_submit)).setText("保存");
    }


    @Override
    protected void onPause() {
        presenter.onPause();
        videoToPause();
        super.onPause();
    }

    boolean isOnDestroy = false;

    @Override
    public void onDestroy() {
        presenter.onDestroy();
        LogUtil.d("OOM", "onDestroy");
        videoStop();
        endTimer();
        isOnDestroy = true;
        EventBus.getDefault().unregister(this);
        super.onDestroy();
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
     * description ：设置预览界面大小
     * date: ：2019/11/18 20:24
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    private void initViewLayerRelative() {
        ViewGroup.LayoutParams RelativeLayoutParams = viewLayerRelativeLayout.getLayoutParams();
        float oriRatio;
        oriRatio = 9f / 16f;
        //保证获得mContainer大小不为0
        viewLayerRelativeLayout.post(() -> {
            int oriHeight = viewLayerRelativeLayout.getHeight();
            RelativeLayoutParams.width = Math.round(1f * oriHeight * oriRatio);
            RelativeLayoutParams.height = oriHeight;
            viewLayerRelativeLayout.setLayoutParams(RelativeLayoutParams);
        });

        if (!TextUtils.isEmpty(videoPath)) {
            hListView.post(() -> presenter.initVideoProgressView(hListView));
        }


    }

    @Override
    protected void initAction() {

    }

    private void initExo(String videoPath) {
        if (TextUtils.isEmpty(videoPath)) {
            return;
        }

        exoPlayer = ExoPlayerFactory.newSimpleInstance(TemplateAddStickerActivity.this);
        playerView.setPlayer(exoPlayer);
        //不使用控制器
        playerView.setUseController(false);
        exoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
        exoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case Player.STATE_READY:

                        break;
                    case Player.STATE_ENDED:
                        videoToStart();
                        break;
                    case Player.STATE_BUFFERING:
                    case Player.STATE_IDLE:
                    default:
                        break;
                }
            }
        });
        MediaSource mediaSource = new ExtractorMediaSource.Factory(
                new DefaultDataSourceFactory(TemplateAddStickerActivity.this, "exoplayer-codelab")).
                createMediaSource(Uri.fromFile(new File(videoPath)));
        exoPlayer.prepare(mediaSource, true, false);
        new Handler().postDelayed(() -> toPlay(), 200);
    }

    private void videoToStart() {
        isPlayComplate = true;
        endTimer();
        isPlaying = false;
        presenter.showGifAnim(false);
        videoPause();
        seekTo(0);
        nowStateIsPlaying(false);
        presenter.showAllAnim(false);
    }


    private void seekTo(long to) {
        if (exoPlayer != null) {
            exoPlayer.seekTo(to);
        }
    }


    private void videoToPause() {
        videoPause();
        isPlaying = false;
        endTimer();
        presenter.showGifAnim(false);
        nowStateIsPlaying(false);
    }


    private void nowStateIsPlaying(boolean isPlaying) {
        if (isPlaying) {
            ivPlay.setImageResource(R.mipmap.pause);
        } else {
            ivPlay.setImageResource(R.mipmap.iv_play_creation);
        }
    }


    private void videoPause() {
        if (exoPlayer != null) {
            LogUtil.d("video", "videoPause");
            exoPlayer.setPlayWhenReady(false);
        }
    }


    /**
     * 关闭timer 和task
     */
    private void endTimer() {
        LogUtil.d("playBGMMusic", "pauseBgmMusic---------------endTimer---------------");
        destroyTimer();
    }


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


    @Override
    public void animIsComplate() {

        WaitingDialog.closeProgressDialog();
        if (!isOnDestroy) {
            Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
                nowStateIsPlaying(true);
                if (!TextUtils.isEmpty(videoPath)) {
                    if (isPlayComplate) {
                        videoPlay();
                        isIntoPause = false;
                    } else {
                        if (isInitVideoLayer) {
                            if (!isIntoPause) {
                                videoPlay();
                            } else {
                                videoPlay();
                                isIntoPause = false;
                                isInitVideoLayer = true;
                            }
                        } else {
                            isIntoPause = false;
                            isInitVideoLayer = true;
                            videoPlay();
                        }
                    }
                }
                isPlaying = true;
                startTimer();
                presenter.showGifAnim(true);
            });
        }

    }

    @Override
    public void needPauseVideo() {
        videoToPause();
    }

    @Override
    public void getVideoDuration(int allVideoDuration, int thumbCount) {
        this.allVideoDuration = allVideoDuration;
        LogUtil.d("OOM", "allVideoDuration=" + allVideoDuration);
//        tv_total.setText(TimeUtils.timeParse(allVideoDuration) + "s");
    }


    /**
     * 开始播放
     */
    private void videoPlay() {
        if (!isOnDestroy) {
            if (exoPlayer != null) {
                LogUtil.d("video", "play");
                exoPlayer.setVolume(1f);
                exoPlayer.setPlayWhenReady(true);
            }
            startTimer();
        }
    }

    private int listWidth;
    private long nowTime = 5;
    //自己计算的播放时间
    private int totalPlayTime;

    private void startTimer() {
        totalPlayTime = 0;
        int screenWidth = screenUtil.getScreenWidth(this);
        //真实长度
        listWidth = (screenWidth - screenUtil.dip2px(this, 43)) * 2;
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
                totalPlayTime = totalPlayTime + 5;
//                Observable.just(1).observeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
                runOnUiThread(() -> {
                    if (!TextUtils.isEmpty(videoPath)) {
                        int nowDuration = (int) getCurrentPos();
                        float percent = nowDuration / (float) allVideoDuration;
                        int widthX = (int) (percent * listWidth);
                        hListView.scrollTo(widthX);
                        LogUtil.d("OOM", "percent=" + percent);
                    } else {
                        //没有选择背景
                        nowTime = nowTime + 5;
                        float percent = nowTime / (float) 10000;
                        int widthX = (int) (percent * listWidth);
                        hListView.scrollTo(widthX);
                        LogUtil.d("OOM", "percent=" + percent);
                        if (percent >= 1) {
                            nowTime = 5;
                            isPlayComplate = true;
                            endTimer();
                            isPlaying = false;
                            presenter.showGifAnim(false);
                            nowStateIsPlaying(false);
                            presenter.showAllAnim(false);
                        }
                    }
                });


//                });
            }
        };
        timer.schedule(task, 0, 5);
    }

    /**
     * 获取当前进度
     */
    private long getCurrentPos() {
        return exoPlayer != null ? exoPlayer.getCurrentPosition() : 0;
    }

    @Override
    public void setgsyVideoProgress(int progress) {
        LogUtil.d("OOM", "videoProgress=" + progress);
        if (!isPlaying) {
            seekTo(progress);
        }
    }

    @Override
    public void showTextDialog(String inputText) {
        intoTextStyleDialog(inputText);
    }

    @Override
    public void hideTextDialog() {
        if (createViewForAddText != null) {
            createViewForAddText.hideInputTextDialog();
        }

    }

    @Override
    public void hideKeyBord() {
        if (createViewForAddText != null) {
            createViewForAddText.hideInputTextDialog();
        }
    }

    @Override
    public void stickerOnclickCallback(String str) {
        if (!TextUtils.isEmpty(str) && createViewForAddText != null) {
            if (!"输入文本".equals(str)) {
                createViewForAddText.setInputText(str);
            }
        }
    }

    @Override
    public void showAdCallback() {
        if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
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
                    StatisticsEventAffair.getInstance().setFlag(TemplateAddStickerActivity.this, "video_ad_alert_request_sucess");
                    LogUtil.d("OOM", "onVideoAdSuccess");
                }

                @Override
                public void onVideoAdError(String s) {
                    StatisticsEventAffair.getInstance().setFlag(TemplateAddStickerActivity.this, "video_ad_alert_request_fail");
                    LogUtil.d("OOM", "onVideoAdError" + s);
                    presenter.alertAlbumUpdate(false);
                }

                @Override
                public void onVideoAdClose() {

                }

                @Override
                public void onRewardVerify() {
                    presenter.alertAlbumUpdate(true);
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
            presenter.alertAlbumUpdate(true);
        }
    }

    @Override
    @OnClick({R.id.tv_top_submit, R.id.ll_play, R.id.iv_top_back, R.id.tv_add_text, R.id.iv_delete_all_text})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_top_submit:
                if (isPlaying) {
                    videoToPause();
                    endTimer();
                }
                if (!TextUtils.isEmpty("IsFrom") && mIsFrom.equals(FromToTemplate.PICTUREALBUM)) {
                    LogUtil.d("OOM", "保存的模板名字为" + title);
                    StatisticsEventAffair.getInstance().setFlag(this, "21_yj_save", title);
                } else if (!TextUtils.isEmpty("IsFrom") && mIsFrom.equals(FromToTemplate.SHOOT)) {
                    StatisticsEventAffair.getInstance().setFlag(this, "12_shoot_finish_save");
                }
                presenter.toSaveVideo(0);
                TemplateKeepStatistics.getInstance().statisticsToSave(templateId,title);

                break;

            case R.id.ll_play:
                toPlay();
                break;

            case R.id.iv_delete_all_text:
                presenter.deleteAllTextSticker();
                if (createViewForAddText != null) {
                    createViewForAddText.hideInputTextDialog();
                }
                break;

            case R.id.iv_top_back:
                this.finish();
                break;


            case R.id.tv_add_text:

                presenter.addTextSticker();
                intoTextStyleDialog("");
                StatisticsEventAffair.getInstance().setFlag(this, "20_mb_text");
                break;


            default:
                break;
        }
    }







    CreateViewForAddText createViewForAddText;

    private void intoTextStyleDialog(String inputText) {
        if (!DoubleClick.getInstance().isFastDoubleClick()) {
            if (createViewForAddText != null) {
                createViewForAddText.hideInputTextDialog();
                createViewForAddText = null;
            }
            ll_add_text_style.setVisibility(View.VISIBLE);
            createViewForAddText = new CreateViewForAddText(this, ll_add_text_style, new CreateViewForAddText.downCallback() {
                @Override
                public void isSuccess(String path, int type, String title) {
                    presenter.ChangeTextStyle(path, type, title);
                }

                @Override
                public void setText(String text) {

                    presenter.ChangeTextLabe(text);
                    if (TextUtils.isEmpty(text)) {
                        if (createViewForAddText != null) {
                            createViewForAddText.hideInputTextDialog();
                            createViewForAddText = null;
                        }
                    }

                }

                @Override
                public void setTextColor(String color0, String color1, String title) {
                    presenter.ChangeTextColor(color0, color1, title);
                }

                @Override
                public void isSuccess(String textBjPath, String textFramePath, String Frametitle) {
                    presenter.ChangeTextFrame(textBjPath, textFramePath, Frametitle);
                }

                @Override
                public void isSuccess(String color0, String color1, String textFramePath, String Frametitle) {
                    presenter.ChangeTextFrame(color0, color1, textFramePath, Frametitle);
                }
            });
            createViewForAddText.showBottomSheetDialog(inputText, "OneKey_template");
        }
    }


    private void toPlay() {
        if (!DoubleClick.getInstance().isFastZDYDoubleClick(500) && !isOnDestroy) {
            if (isPlaying) {
                if (!isShowPreviewAd && BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
                    AdManager.getInstance().showCpAd(this, AdConfigs.AD_SCREEN_FOR_PREVIEW);
                    isShowPreviewAd = true;
                }
                isIntoPause = false;
                isPlayComplate = false;
                videoToPause();
                presenter.showGifAnim(false);
                isPlaying = false;
                nowStateIsPlaying(false);
                presenter.showAllAnim(false);
            } else {
                WaitingDialog.openPragressDialog(this);
                new Thread(() -> presenter.showAllAnim(true)).start();
            }
        }
    }

    @Subscribe
    public void onEventMainThread(showAdCallback event) {

    }


    @Override
    public final boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (ll_add_text_style.getVisibility() == View.VISIBLE) {
                if (createViewForAddText != null) {
                    createViewForAddText.hideInput();
                }
                ll_add_text_style.setVisibility(View.GONE);
            } else {
                finish();
            }
        }
        return true;
    }
}

package com.flyingeffects.com.ui.view.activity;

import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.showAdCallback;
import com.flyingeffects.com.manager.AdConfigs;
import com.flyingeffects.com.manager.AdManager;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.view.TemplateAddStickerMvpView;
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

    private  boolean isShowPreviewAd=false;

    @Override
    protected int getLayoutId() {
        return R.layout.act_template_add_sticker;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        videoPath=getIntent().getStringExtra("videoPath");
        presenter=new TemplateAddStickerMvpPresenter(this,this,ll_space,viewLayerRelativeLayout,videoPath);
        if (!TextUtils.isEmpty(videoPath)) {
            //有视频的时候，初始化视频值
            presenter.setPlayerViewSize(playerView,scrollView,viewLayerRelativeLayout);
            initExo(videoPath);
        }
        presenter.initBottomLayout(viewPager);
        presenter.requestStickersList();
        initViewLayerRelative();
        ((TextView)findViewById(R.id.tv_top_submit)).setText("保存");
    }


    @Override
    protected void onPause() {
        presenter.onPause();
        videoToPause();
        super.onPause();
    }

    boolean isOnDestroy=false;
    @Override
    public void onDestroy() {
        presenter.onDestroy();
        LogUtil.d("OOM","onDestroy");
        videoStop();
        endTimer();
        isOnDestroy=true;
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
        MediaSource   mediaSource = new ExtractorMediaSource.Factory(
                new DefaultDataSourceFactory(TemplateAddStickerActivity.this, "exoplayer-codelab")).
                createMediaSource(Uri.fromFile(new File(videoPath)));
        exoPlayer.prepare(mediaSource, true, false);
        new Handler().postDelayed(() -> toPlay(),200);
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

        WaitingDialog.closePragressDialog();
        if(!isOnDestroy){
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
//        tv_total.setText(timeUtils.timeParse(allVideoDuration) + "s");
    }



    /**
     * 开始播放
     */
    private void videoPlay() {
        if(!isOnDestroy){
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
    public void showTextDialog() {
        intoTextStyleDialog();
    }


    @Override
    @OnClick({R.id.tv_top_submit, R.id.ll_play, R.id.iv_top_back,R.id.tv_add_text})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_top_submit:
                if (isPlaying) {
                    videoToPause();
                    endTimer();
                }
                presenter.toSaveVideo( 0);
                break;

            case R.id.ll_play:
                toPlay();

                break;



            case R.id.iv_top_back:
                this.finish();
                break;


            case R.id.tv_add_text:
                intoTextStyleDialog();
                presenter.addTextSticker();
                break;


            default:
                break;
        }
    }


    private void intoTextStyleDialog(){
        if(!DoubleClick.getInstance().isFastDoubleClick()){
            CreateViewForAddText createViewForAddText=new CreateViewForAddText(this, new CreateViewForAddText.downCallback() {
                @Override
                public void isSuccess(String path, int type) {
                    presenter.ChangeTextStyle(path,type);
                }

                @Override
                public void setText(String text) {
                    presenter.ChangeTextLabe(text);
                }
            });
            createViewForAddText.showBottomSheetDialog();
        }
    }



    private void toPlay(){
        if (!DoubleClick.getInstance().isFastZDYDoubleClick(500)&&!isOnDestroy) {
            if (isPlaying) {
                if(!isShowPreviewAd&&BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()){
                    AdManager.getInstance().showCpAd(this, AdConfigs.AD_SCREEN_FOR_PREVIEW);
                    isShowPreviewAd=true;
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
                    statisticsEventAffair.getInstance().setFlag(TemplateAddStickerActivity.this, "video_ad_alert_request_sucess");
                    LogUtil.d("OOM", "onVideoAdSuccess");
                }

                @Override
                public void onVideoAdError(String s) {
                    statisticsEventAffair.getInstance().setFlag(TemplateAddStickerActivity.this, "video_ad_alert_request_fail");
                    LogUtil.d("OOM", "onVideoAdError" + s);
                    presenter.alertAlbumUpdate(false);
                }

                @Override
                public void onVideoAdClose() {
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


}

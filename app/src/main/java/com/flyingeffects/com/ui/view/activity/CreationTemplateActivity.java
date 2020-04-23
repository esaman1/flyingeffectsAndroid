package com.flyingeffects.com.ui.view.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.constans.UiStep;
import com.flyingeffects.com.enity.DownVideoPath;
import com.flyingeffects.com.manager.AlbumManager;
import com.flyingeffects.com.manager.CompressionCuttingManage;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.view.CreationTemplateMvpView;
import com.flyingeffects.com.ui.model.AnimStickerModel;
import com.flyingeffects.com.ui.model.GetPathTypeModel;
import com.flyingeffects.com.ui.presenter.CreationTemplateMvpPresenter;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.screenUtil;
import com.flyingeffects.com.utils.timeUtils;
import com.flyingeffects.com.view.HorizontalListView;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.lansosdk.box.ViewLayerRelativeLayout;
import com.shixing.sxve.ui.albumType;
import com.suke.widget.SwitchButton;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * description ：用户创作页面,里面主要用了langSong 的工具类，对视频进行贴纸的功能
 * creation date: 2020/3/11
 * user : zhangtongju
 */


public class CreationTemplateActivity extends BaseActivity implements CreationTemplateMvpView {

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    /**
     * 蓝松规定的容器
     */
    @BindView(R.id.id_vview_realtime_gllayout)
    ViewLayerRelativeLayout viewLayerRelativeLayout;

    private boolean isIntoPause = false;

    @BindView(R.id.iv_list)
    HorizontalListView hListView;


    @BindView(R.id.iv_play)
    ImageView ivPlay;

    @BindView(R.id.switch_button)
    SwitchButton switchButton;

    @BindView(R.id.tv_total)
    TextView tv_total;

    public final static int SELECTALBUM = 0;

    /**
     * 源视频地址
     */
    private String originalPath;
    private String imgPath;
    private CreationTemplateMvpPresenter presenter;
    /**
     * 默认背景，也是是否选择了背景的重要判断，
     */
    private String videoPath;
    /**
     * 当前预览状态，是否在播放中
     */
    private boolean isPlaying = false;
    /**
     * 是否初始化过播放器
     */
    private boolean isInitVideoLayer = false;
    private int allVideoDuration;
    private boolean isPlayComplate = false;
    /**
     * 只有背景模板才有，自定义的话这个值为""
     */
    private String title;

    @BindView(R.id.iv_green_background)
    ImageView iv_green_background;

    @BindView(R.id.ll_green_background)
    LinearLayout ll_green_background;

    /**
     * 获得背景视频音乐
     */
    private String bgmPath;

    @BindView(R.id.exo_player)
    PlayerView playerView;

    private SimpleExoPlayer exoPlayer;

    /**
     * 背景音乐播放器
     */
    private  MediaPlayer  bgmPlayer;

    @Override
    protected int getLayoutId() {
        return R.layout.act_creation_template_edit;
    }


    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        ((TextView) findViewById(R.id.tv_top_submit)).setText("预览效果");
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("Message");
        if (bundle != null) {
            imgPath = bundle.getString("paths");
            videoPath = bundle.getString("video_path");
            originalPath = bundle.getString("originalPath");
            title = bundle.getString("bjTemplateTitle");
        }
        presenter = new CreationTemplateMvpPresenter(this, this, videoPath, viewLayerRelativeLayout);
        if(!TextUtils.isEmpty(videoPath)){
            //有视频的时候，初始化视频值
            initExo(videoPath);
        }else{
            showGreenBj();
        }
        presenter.requestStickersList();
    }


    private void initExo(String videoPath) {
        if (TextUtils.isEmpty(videoPath)) {
            return;
        }


        exoPlayer = ExoPlayerFactory.newSimpleInstance(CreationTemplateActivity.this);
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
                        if(bgmPlayer!=null){
                            bgmPlayer.stop();
                            bgmPlayer=null;
                        }
                        isPlayComplate = true;
                        endTimer();
                        isPlaying = false;
                        presenter.showGifAnim(false);
                        videoPause();
                        seekTo(0);
                        nowStateIsPlaying(false);
                        break;
                    case Player.STATE_BUFFERING:
                    case Player.STATE_IDLE:
                    default:
                        break;
                }
            }
        });
        MediaSource mediaSource = new ExtractorMediaSource.Factory(
                new DefaultDataSourceFactory(CreationTemplateActivity.this, "exoplayer-codelab")).
                createMediaSource(Uri.fromFile(new File(videoPath)));

        exoPlayer.prepare(mediaSource, true, false);
        videoPause();
    }


    private void videoPause() {
        if (exoPlayer != null) {
            LogUtil.d("video", "videoPause");
            exoPlayer.setPlayWhenReady(false);
        }
    }


    /**
     * 开始播放
     */
    private void videoPlay() {
        if (exoPlayer != null) {
            LogUtil.d("video", "play");
            if(!TextUtils.isEmpty(bgmPath)){
                if(bgmPlayer!=null){
                    //继续播放
                    bgmPlayer.start();
                }else{
                    seekTo(0);
                    playBGMMusic();
                }

                exoPlayer.setVolume(0f);
            }else{
                exoPlayer.setVolume(1f);
            }

            exoPlayer.setPlayWhenReady(true);
        }
        startTimer();
    }





    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void initAction() {
        presenter.initStickerView(imgPath, originalPath);
        presenter.initBottomLayout(viewPager);
        initViewLayerRelative();
        switchButton.setOnCheckedChangeListener((view, isChecked) -> {
            if (isChecked) {
                if (UiStep.isFromDownBj) {
                    statisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "5_mb_bj_Cutoutopen");
                } else {
                    statisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "6_customize_bj_Cutoutopen");
                }
            } else {
                if (UiStep.isFromDownBj) {
                    statisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "5_mb_bj_Cutoutoff");
                } else {
                    statisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "6_customize_bj_Cutoutoff");
                }
            }
            presenter.CheckedChanged(isChecked);
        });
    }


    @OnClick({R.id.tv_top_submit, R.id.ll_play, R.id.iv_add_sticker, R.id.iv_top_back,R.id.tv_background})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_top_submit:
                if (isPlaying) {
                    videoToPause();
                    pauseBgmMusic();
                    endTimer();
                }
                if (!TextUtils.isEmpty(title)) {
                    statisticsEventAffair.getInstance().setFlag(CreationTemplateActivity.this, "5_mb_bj_save", title);
                } else {
                    statisticsEventAffair.getInstance().setFlag(CreationTemplateActivity.this, "6_customize_bj_save");
                }
                presenter.toSaveVideo();
                break;

            case R.id.ll_play:
                if (!DoubleClick.getInstance().isFastDoubleClick()) {
                    if (isPlaying) {
                        pauseBgmMusic();
                        isIntoPause = false;
                        isPlayComplate = false;
                        videoToPause();
                        presenter.showGifAnim(false);
                        isPlaying = false;
                        nowStateIsPlaying(false);
                    } else {
                        nowStateIsPlaying(true);
                        if(!TextUtils.isEmpty(videoPath)){
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
                    }
                }

                break;

            case R.id.iv_top_back:
                this.finish();
                break;

            case R.id.iv_add_sticker:
                if (isPlaying) {
                    videoToPause();
                    isPlaying = false;
                    endTimer();
                    presenter.showGifAnim(false);
                    nowStateIsPlaying(false);
                }
                if (UiStep.isFromDownBj) {
                    statisticsEventAffair.getInstance().setFlag(CreationTemplateActivity.this, "5_mb_bj_material");
                } else {
                    statisticsEventAffair.getInstance().setFlag(CreationTemplateActivity.this, "6_customize_bj_material");
                }

                //添加新的贴纸，这里的贴纸就是用户选择的贴纸
                AlbumManager.chooseAlbum(this, 1, SELECTALBUM, (tag, paths, isCancel, albumFileList) -> {
                    Log.d("OOM", "isCancel=" + isCancel);
                    if (!isCancel) {
                        //如果是选择的视频，就需要得到封面，然后设置在matting里面去，然后吧原图设置为视频地址
                        String path=paths.get(0);
                        String pathType= GetPathTypeModel.getInstance().getMediaType(path);
                        if (albumType.isImage(pathType)) {
                            CompressionCuttingManage manage = new CompressionCuttingManage(CreationTemplateActivity.this, "", tailorPaths -> {
                                presenter.addNewSticker(tailorPaths.get(0), paths.get(0));
                            });
                            manage.ToMatting(paths);
                        }else{
                            //贴纸选择的视频
                            presenter.GetVideoCover(paths.get(0));
                        }

                    }

                }, "");


            case R.id.tv_background:
                Intent intent =new Intent(this,ChooseBackgroundTemplateActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;

            default:
                break;
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

        if(!TextUtils.isEmpty(videoPath)){
            hListView.post(() -> presenter.initVideoProgressView(hListView));
        }
    }


    private void showGreenBj(){
        ll_green_background.setVisibility(View.VISIBLE);
       float oriRatio = 9f / 16f;
        //保证获得mContainer大小不为0
        LinearLayout.LayoutParams RelativeLayoutParams = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        //如果没有选择下载视频，那么就是自定义视频入口进来，那么默认为绿布
        iv_green_background.post(() -> {
            int oriHeight = iv_green_background.getHeight();
            RelativeLayoutParams.width = Math.round(1f * oriHeight * oriRatio);
            RelativeLayoutParams.height = oriHeight;
            iv_green_background.setLayoutParams(RelativeLayoutParams);
        });
      hListView.post(() -> presenter.initVideoProgressView(hListView));
    }


    @Override
    protected void onPause() {
        videoToPause();
        isIntoPause = true;
        super.onPause();
    }

    @Override
    public void onDestroy() {
        presenter.onDestroy();
        destroyTimer();
        videoStop();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void ItemClickForStickView(AnimStickerModel stickViewModel) {
        viewLayerRelativeLayout.addView(stickViewModel.getStickerView());
    }

    @Override
    public void hasPlayingComplete() {
    }

    @Override
    public void setgsyVideoProgress(int progress) {
        LogUtil.d("OOM", "videoProgress=" + progress);
        if (!isPlaying) {
            seekTo(progress);
        }
    }

    private void seekTo(long to) {
        if (exoPlayer != null) {
            exoPlayer.seekTo(to);
        }

        if(bgmPlayer!=null){
            bgmPlayer.seekTo((int) to);
        }

    }

    @Override
    public void getVideoDuration(int allVideoDuration, int thumbCount) {
        this.allVideoDuration = allVideoDuration;
        LogUtil.d("OOM", "allVideoDuration=" + allVideoDuration);
        tv_total.setText(timeUtils.timeParse(allVideoDuration) + "s");
    }

    @Override
    public void needPauseVideo() {
        if (isPlaying) {
            isIntoPause = false;
            isPlayComplate = false;
            videoToPause();
            presenter.showGifAnim(false);
            isPlaying = false;
            nowStateIsPlaying(false);
        }
    }

    @Override
    public void getVideoCover(String path,String originalPath) {
        presenter.addNewSticker(path, originalPath);
    }



    @Override
    public void getBgmPath(String path) {
        this.bgmPath=path;
    }


    private Timer timer;
    private TimerTask task;
    private int listWidth;
    private long nowTime=5;
    private void startTimer() {
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
                Observable.just(1).observeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
                    if(!TextUtils.isEmpty(videoPath)){
                        int nowDuration = (int) getCurrentPos();
                        float percent = nowDuration / (float) allVideoDuration;
                        LogUtil.d("OOM", "比例=" + percent);
                        int widthX = (int) (percent * listWidth);
                        LogUtil.d("OOM", "width=" + widthX);
                        hListView.scrollTo(widthX);
                    }else{
                        //没有选择背景
                        nowTime=nowTime+5;
                        LogUtil.d("OOM", "nowTime=" + nowTime);
                        float percent = nowTime / (float) 10000;
                        LogUtil.d("OOM", "比例=" + percent);
                        int widthX = (int) (percent * listWidth);
                        hListView.scrollTo(widthX);
                        if(percent>=1){
                            nowTime=5;
                            isPlayComplate = true;
                            endTimer();
                            isPlaying = false;
                            presenter.showGifAnim(false);
                            nowStateIsPlaying(false);
                        }
                    }
                });
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

    /**
     * 关闭timer 和task
     */
    private void endTimer() {
        destroyTimer();
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


    /**
     * description ：裁剪页面裁剪成功后返回的数据
     * creation date: 2020/4/13
     * user : zhangtongju
     */
    @Subscribe
    public void onEventMainThread(DownVideoPath event) {
        videoPath=event.getPath();
        ll_green_background.setVisibility(View.GONE);
        initExo(videoPath);
        presenter.setmVideoPath(videoPath);
        presenter.initVideoProgressView(hListView);
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






    private void playBGMMusic(){
        bgmPlayer = new MediaPlayer();
        try {
            bgmPlayer.setDataSource(bgmPath);
            bgmPlayer.prepare();
            bgmPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void pauseBgmMusic(){
        if(bgmPlayer!=null){
            bgmPlayer.pause();
        }
    }


}

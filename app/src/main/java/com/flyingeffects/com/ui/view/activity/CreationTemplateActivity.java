package com.flyingeffects.com.ui.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.constans.UiStep;
import com.flyingeffects.com.enity.ChooseVideoAddSticker;
import com.flyingeffects.com.enity.CutSuccess;
import com.flyingeffects.com.enity.DownVideoPath;
import com.flyingeffects.com.manager.AlbumManager;
import com.flyingeffects.com.manager.CompressionCuttingManage;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.view.CreationTemplateMvpView;
import com.flyingeffects.com.ui.model.AnimStickerModel;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.ui.model.GetPathTypeModel;
import com.flyingeffects.com.ui.presenter.CreationTemplateMvpPresenter;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.screenUtil;
import com.flyingeffects.com.utils.timeUtils;
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
import com.shixing.sxve.ui.albumType;
import com.shixing.sxve.ui.view.WaitingDialog;
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
     * 默认图片背景，""表示绿幕
     */
    private String imageBjPath;
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
    RelativeLayout ll_green_background;

    @BindView(R.id.scrollView)
    MyScrollView scrollView;


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
    private MediaPlayer bgmPlayer;
    /**
     * 默认抠图开关
     */
    private boolean isNeedCut;

    @BindView(R.id.relative_playerView)
    RelativeLayout relative_playerView;

    @BindView(R.id.ll_space)
    LinearLayout ll_space;

    private int templateId;

    @BindView(R.id.tv_music)
    TextView tv_music;


    @Override
    protected int getLayoutId() {
        return R.layout.act_creation_template_edit;
    }


    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        LogUtil.d("OOM", "进入到创作页面");
        ((TextView) findViewById(R.id.tv_top_submit)).setText("下一步");
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("Message");
        if (bundle != null) {
            imgPath = bundle.getString("paths");
            videoPath = bundle.getString("video_path");
            originalPath = bundle.getString("originalPath");
            isNeedCut = bundle.getBoolean("isNeedCut");
            title = bundle.getString("bjTemplateTitle");
            templateId=bundle.getInt("templateId");
        }
        presenter = new CreationTemplateMvpPresenter(this, this, videoPath, viewLayerRelativeLayout, originalPath, null);
        if (!TextUtils.isEmpty(videoPath)) {
            //有视频的时候，初始化视频值
            setPlayerViewSize(false);
            initExo(videoPath);
        } else {
            showGreenBj();
        }
        presenter.requestStickersList();
        presenter.statisticsDuration(videoPath,this);
    }

    private MediaSource mediaSource;

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
                        videoToStart();
                        break;
                    case Player.STATE_BUFFERING:
                    case Player.STATE_IDLE:
                    default:
                        break;
                }
            }
        });
        mediaSource = new ExtractorMediaSource.Factory(
                new DefaultDataSourceFactory(CreationTemplateActivity.this, "exoplayer-codelab")).
                createMediaSource(Uri.fromFile(new File(videoPath)));

        exoPlayer.prepare(mediaSource, true, false);
        videoPause();
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
            if (!TextUtils.isEmpty(bgmPath)) {
                if (bgmPlayer != null) {
                    //继续播放
                    bgmPlayer.start();
                } else {
                    seekTo(0);
                    playBGMMusic();
                }
                exoPlayer.setVolume(0f);
            } else {
                exoPlayer.setVolume(1f);
            }

            exoPlayer.setPlayWhenReady(true);
        }
        startTimer();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isIntoPause && exoPlayer != null) {
            exoPlayer.prepare(mediaSource, true, false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    videoPause();
                    destroyTimer();
                }
            }, 200);
            isIntoPause = false;
        }
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


    boolean nowUiIsLandscape = false;

    @OnClick({R.id.tv_top_submit, R.id.ll_play, R.id.iv_add_sticker, R.id.iv_top_back, R.id.iv_change_ui, R.id.tv_background,R.id.tv_music, R.id.tv_anim, R.id.tv_tiezhi,R.id.tv_add_text})
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

                if (UiStep.isFromDownBj) {
                    statisticsEventAffair.getInstance().setFlag(CreationTemplateActivity.this, "7_Preview");
                } else {
                    statisticsEventAffair.getInstance().setFlag(CreationTemplateActivity.this, "8_Preview");
                }
                presenter.toSaveVideo(imageBjPath, nowUiIsLandscape, percentageH,templateId);
                break;

            case R.id.ll_play:
                if (!DoubleClick.getInstance().isFastZDYDoubleClick(500)) {
                    if (isPlaying) {
                        pauseBgmMusic();
                        isIntoPause = false;
                        isPlayComplate = false;
                        videoToPause();
                        presenter.showGifAnim(false);
                        isPlaying = false;
                        nowStateIsPlaying(false);
                        presenter.showAllAnim(false);
                    } else {
                        statisticsEventAffair.getInstance().setFlag(CreationTemplateActivity.this, " 14_preview_video_bj");
                        WaitingDialog.openPragressDialog(this);
                        new Thread(() -> presenter.showAllAnim(true)).start();
                    }
                }

                break;


            case R.id.tv_music:
                presenter.chooseAnim(2);
                setTextColor(2);

                break;

            case R.id.tv_add_text:
                if(!DoubleClick.getInstance().isFastDoubleClick()){
                    CreateViewForAddText createViewForAddText=new CreateViewForAddText(this, new CreateViewForAddText.downCallback() {
                        @Override
                        public void isSuccess(String path, int type) {

                        }
                    });
                    createViewForAddText.showBottomSheetDialog();
                }
                break;


            case R.id.iv_top_back:
                this.finish();
                break;

            case R.id.iv_add_sticker:
                if (!DoubleClick.getInstance().isFastZDYDoubleClick(1000)) {
                    presenter.showAllAnim(false);
                    if (isPlaying) {
                        videoToPause();
                        isPlaying = false;
                        endTimer();
                        presenter.showGifAnim(false);
                        nowStateIsPlaying(false);
                    }
                    if (UiStep.isFromDownBj) {
                        statisticsEventAffair.getInstance().setFlag(CreationTemplateActivity.this, "5_mb_bj_material");
                        statisticsEventAffair.getInstance().setFlag(CreationTemplateActivity.this, "8_material");
                    } else {
                        statisticsEventAffair.getInstance().setFlag(CreationTemplateActivity.this, "6_customize_bj_material");
                        statisticsEventAffair.getInstance().setFlag(CreationTemplateActivity.this, "7_material");
                    }
                    //添加新的贴纸，这里的贴纸就是用户选择的贴纸
                    AlbumManager.chooseAlbum(this, 1, SELECTALBUM, (tag, paths, isCancel, albumFileList) -> {
                        Log.d("OOM", "isCancel=" + isCancel);
                        if (!isCancel) {
                            //如果是选择的视频，就需要得到封面，然后设置在matting里面去，然后吧原图设置为视频地址
                            String path = paths.get(0);
                            String pathType = GetPathTypeModel.getInstance().getMediaType(path);
                            if (albumType.isImage(pathType)) {
                                statisticsEventAffair.getInstance().setFlag(CreationTemplateActivity.this, "7_SelectImage");
                                CompressionCuttingManage manage = new CompressionCuttingManage(CreationTemplateActivity.this, "", tailorPaths -> {
                                    presenter.addNewSticker(tailorPaths.get(0), paths.get(0));
                                });
                                manage.ToMatting(paths);
                            } else {
                                //贴纸选择的视频
                                intoVideoCropActivity(paths.get(0));
                                statisticsEventAffair.getInstance().setFlag(CreationTemplateActivity.this, "7_Selectvideo\n");
                            }
                        }
                    }, "");
                }
                break;
            case R.id.tv_background:
                Intent intent = new Intent(
                        this, ChooseBackgroundTemplateActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;

            case R.id.tv_anim:
                presenter.chooseAnim(1);
                setTextColor(1);
                break;
            case R.id.tv_tiezhi:
                presenter.chooseAnim(0);
                setTextColor(0);
                break;

            case R.id.iv_change_ui:
                //横竖屏切换
                nowUiIsLandscape = !nowUiIsLandscape;
//                if(nowUiIsLandscape){
//                    statisticsEventAffair.getInstance().setFlag(CreationTemplateActivity.this, "13_horizontal");
//                }

                setPlayerViewSize(nowUiIsLandscape);

                break;

            default:
                break;
        }
    }


    private int[] lin_Id = {R.id.tv_tiezhi, R.id.tv_anim,R.id.tv_music,R.id.tv_add_text};

    private void setTextColor(int chooseItem) {
        for (int i = 0; i < lin_Id.length; i++) {
            ((TextView) findViewById(lin_Id[i])).setTextColor(getResources().getColor(R.color.white));
        }
        ((TextView) findViewById(lin_Id[chooseItem])).setTextColor(Color.parseColor("#5496FF"));

    }


    private void intoVideoCropActivity(String path) {
        Intent intent = new Intent(CreationTemplateActivity.this, VideoCropActivity.class);
        intent.putExtra("videoPath", path);
        intent.putExtra("comeFrom", FromToTemplate.ISFROMEDOWNVIDEOFORADDSTICKER);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
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

        if (!TextUtils.isEmpty(videoPath)) {
            hListView.post(() -> presenter.initVideoProgressView(hListView));
        }


    }


    boolean isInitImageBj = false;

    private void showGreenBj() {
        ll_green_background.setVisibility(View.VISIBLE);
        iv_green_background.setVisibility(View.VISIBLE);
        if (!isInitImageBj) {
            float oriRatio = 9f / 16f;
            //保证获得mContainer大小不为0
            RelativeLayout.LayoutParams RelativeLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            //如果没有选择下载视频，那么就是自定义视频入口进来，那么默认为绿布
            iv_green_background.post(() -> {
                int oriHeight = iv_green_background.getHeight();
                RelativeLayoutParams.width = Math.round(1f * oriHeight * oriRatio);
                RelativeLayoutParams.height = oriHeight;
                iv_green_background.setLayoutParams(RelativeLayoutParams);
            });
            isInitImageBj = true;
        }
        hListView.post(() -> presenter.initVideoProgressView(hListView));
    }


    /**
     * description ：设置播放器尺寸,如果不设置的话会出现黑屏，因为外面嵌套了ScrollView
     * 横竖屏切换的时候例外2层都需要修改尺寸,
     * creation date: 2020/8/10
     * user : zhangtongju
     */
    private int scrollViewHeight;
    private float percentageH = 0;

    private void setPlayerViewSize(boolean isLandscape) {

        videoToPause();

        LinearLayout.LayoutParams RelativeLayoutParams = (LinearLayout.LayoutParams) playerView.getLayoutParams();
        float oriRatio = 9f / 16f;
        if (isLandscape) {
            //横屏的情况
            scrollView.post(() -> {
                int oriWidth = ll_space.getWidth();
                RelativeLayout.LayoutParams RelativeLayoutParams2 = (RelativeLayout.LayoutParams) scrollView.getLayoutParams();
                RelativeLayoutParams2.width = oriWidth;
                RelativeLayoutParams2.height = Math.round(1f * oriWidth * oriRatio);
                scrollView.setLayoutParams(RelativeLayoutParams2);
                scrollViewHeight = RelativeLayoutParams2.height;
                RelativeLayoutParams.width = oriWidth;
                RelativeLayoutParams.height = Math.round(1f * oriWidth / oriRatio);
                playerView.setLayoutParams(RelativeLayoutParams);
                //设置预览编辑界面
                viewLayerRelativeLayout.setLayoutParams(RelativeLayoutParams2);
            });
        } else {
            //横屏模式下切换到了竖屏
            scrollView.post(() -> {
                RelativeLayout.LayoutParams RelativeLayoutParams2 = (RelativeLayout.LayoutParams) scrollView.getLayoutParams();
                int height = ll_space.getHeight();
                RelativeLayoutParams2.height = height;
                RelativeLayoutParams2.width = Math.round(1f * height * oriRatio);
                scrollView.setLayoutParams(RelativeLayoutParams2);
                scrollViewHeight = height;
                RelativeLayoutParams.width = Math.round(1f * height * oriRatio);
                RelativeLayoutParams.height = height;
                playerView.setLayoutParams(RelativeLayoutParams);
                //设置预览编辑界面
                viewLayerRelativeLayout.setLayoutParams(RelativeLayoutParams2);
            });
        }


        if (ll_green_background.getVisibility() == View.VISIBLE) {
            //可见的时候需要修稿这里
            if (isLandscape) {
                //横屏的情况
                iv_green_background.post(() -> {
                    int oriWidth = ll_space.getWidth();
                    RelativeLayout.LayoutParams RelativeLayoutParams3 = (RelativeLayout.LayoutParams) ll_green_background.getLayoutParams();
                    RelativeLayoutParams3.width = oriWidth;
                    RelativeLayoutParams3.height = Math.round(1f * oriWidth * oriRatio);
                    ll_green_background.setLayoutParams(RelativeLayoutParams3);
                    RelativeLayout.LayoutParams RelativeLayoutParams4 = (RelativeLayout.LayoutParams) iv_green_background.getLayoutParams();
                    RelativeLayoutParams4.width = oriWidth;
                    RelativeLayoutParams4.height = Math.round(1f * oriWidth * oriRatio);
                    iv_green_background.setLayoutParams(RelativeLayoutParams4);
                });
            } else {
                iv_green_background.post(() -> {
                    int oriHeight = ll_space.getHeight();
                    RelativeLayout.LayoutParams RelativeLayoutParams3 = (RelativeLayout.LayoutParams) ll_green_background.getLayoutParams();
                    RelativeLayoutParams3.width = Math.round(1f * oriHeight * oriRatio);
                    RelativeLayoutParams3.height = oriHeight;
                    ll_green_background.setLayoutParams(RelativeLayoutParams3);
                    RelativeLayout.LayoutParams RelativeLayoutParams4 = (RelativeLayout.LayoutParams) iv_green_background.getLayoutParams();
                    RelativeLayoutParams4.width = Math.round(1f * oriHeight * oriRatio);
                    RelativeLayoutParams4.height = oriHeight;
                    iv_green_background.setLayoutParams(RelativeLayoutParams4);
                });
            }
        }


        scrollView.setOnScrollListener(scrollY -> {
            int totalHeight = scrollView.getChildAt(0).getHeight();
            percentageH=scrollY/(float) totalHeight;
            LogUtil.d("OOM3", "percentageH" + percentageH);
        });


        new Handler().postDelayed(() -> presenter.setAllStickerCenter(), 500);
    }


    @Override
    protected void onPause() {
        videoToPause();
        isIntoPause = true;
        presenter.intoOnPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        presenter.onDestroy();
        destroyTimer();
        videoStop();
        if (bgmPlayer != null) {
            bgmPlayer.pause();
            bgmPlayer.release();
        }
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
    public void deleteFirstSticker() {
        new Handler().postDelayed(() -> {
            viewPager.setCurrentItem(0);
            tv_music.setVisibility(View.GONE);
            setTextColor(0);
        },500);
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

        if (bgmPlayer != null) {
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
    public void getVideoCover(String path, String originalPath) {
        presenter.addNewSticker(path, originalPath);
        if (TextUtils.isEmpty(videoPath)) {
            //如果还是绿屏。那么需要刷新底部的时长
            Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> presenter.initVideoProgressView(hListView));
        }
    }


    @Override
    public void getBgmPath(String path) {
        this.bgmPath = path;
        LogUtil.d("OOM", "getBgmPath=" + path);
        if (isPlaying) {
            if (!TextUtils.isEmpty(path)) {
                if (exoPlayer != null) {
                    exoPlayer.setVolume(0f);
                }
                pauseBgmMusic();
                playBGMMusic();
                if (bgmPlayer != null) {
                    if (exoPlayer != null) {
                        bgmPlayer.seekTo((int) getCurrentPos());
                    } else {
                        bgmPlayer.seekTo(totalPlayTime);
                    }
                }
            } else {
                if (exoPlayer != null) {
                    exoPlayer.setVolume(1f);
                }
                pauseBgmMusic();
            }
        } else {
            videoToStart();
        }
    }

    @Override
    public void changFirstVideoSticker(String path) {
        if (TextUtils.isEmpty(videoPath)) {
            //如果还是绿屏。那么需要刷新底部的时长
            Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> presenter.initVideoProgressView(hListView));
        }
    }


    /**
     * 第一次添加贴纸后修改切换按钮状态栏
     */
    @Override
    public void isFirstAddSuccess() {
        new Handler().postDelayed(() -> {
            if (!isNeedCut) {
                switchButton.setChecked(false);
            }
        }, 1500);
    }

    @Override
    public void showCreateTemplateAnim(boolean isShow) {
        if (isShow) {
            viewLayerRelativeLayout.setVisibility(View.GONE);
        } else {
            viewLayerRelativeLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showMusicBtn(boolean isShow) {
        if(isShow){
            tv_music.setVisibility(View.VISIBLE);
        }else{
            tv_music.setVisibility(View.GONE);
            viewPager.setCurrentItem(0);
            setTextColor(0);
        }

    }


    /**
     * description ：动画初始化完成，接下来就开始预览
     * creation date: 2020/6/4
     * user : zhangtongju
     */
    @Override
    public void animIsComplate() {
        WaitingDialog.closePragressDialog();
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
            } else {
                //如果有背景还是播放背景音乐
                if (!TextUtils.isEmpty(bgmPath)) {
                    if (bgmPlayer != null) {
                        //继续播放
                        bgmPlayer.start();
                    } else {
                        seekTo(0);
                        playBGMMusic();
                    }
                }
            }
            isPlaying = true;
            startTimer();
            presenter.showGifAnim(true);
        });

    }


    private Timer timer;
    private TimerTask task;
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


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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

    /**
     * 关闭timer 和task
     */
    private void endTimer() {
        LogUtil.d("playBGMMusic", "pauseBgmMusic---------------endTimer---------------");
        destroyTimer();
        if (bgmPlayer != null) {
            bgmPlayer.stop();
            bgmPlayer = null;
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


    /**
     * description ：重新选择了背景视频的回调
     * creation date: 2020/4/13
     * user : zhangtongju
     */
    @Subscribe
    public void onEventMainThread(DownVideoPath event) {
//        videoStop();
        Observable.just(event.getPath()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(s -> {
            if (albumType.isImage(GetPathTypeModel.getInstance().getMediaType(event.getPath()))) {
                ll_green_background.setVisibility(View.VISIBLE);
                presenter.setmVideoPath("");
                videoPath = "";
                showGreenBj();
                imageBjPath = event.getPath();
                new Handler().postDelayed(() -> Glide.with(CreationTemplateActivity.this).load(s).into(iv_green_background), 500);

            } else {
                LogUtil.d("OOM", "重新选择了视频背景,地址为" + event.getPath());
                videoPath = event.getPath();
                ll_green_background.setVisibility(View.GONE);
                setPlayerViewSize(nowUiIsLandscape);
                initExo(videoPath);
                presenter.setmVideoPath(videoPath);
                presenter.initVideoProgressView(hListView);
            }
        });
    }


    /**
     * description ：选择视频后新增的贴纸
     * creation date: 2020/4/13
     * user : zhangtongju
     */
    @Subscribe
    public void onEventMainThread(ChooseVideoAddSticker event) {
        presenter.GetVideoCover(event.getPath());
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


    private void playBGMMusic() {
        LogUtil.d("playBGMMusic", "playBGMMusic");
        bgmPlayer = new MediaPlayer();
        try {
            bgmPlayer.setDataSource(bgmPath);
            bgmPlayer.prepare();
            bgmPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void pauseBgmMusic() {
        LogUtil.d("playBGMMusic", "pauseBgmMusic------------------------------");
        if (bgmPlayer != null&&bgmPlayer.isPlaying()) {
            bgmPlayer.pause();
        }
    }


    @Subscribe
    public void onEventMainThread( CutSuccess cutSuccess) {
        String nowChooseBjPath=cutSuccess.getFilePath();
        presenter.setAddChooseBjPath(nowChooseBjPath);

    }


}

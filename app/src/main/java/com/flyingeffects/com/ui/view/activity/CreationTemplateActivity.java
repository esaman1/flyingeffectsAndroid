package com.flyingeffects.com.ui.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.TimeUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.manager.AlbumManager;
import com.flyingeffects.com.manager.CompressionCuttingManage;
import com.flyingeffects.com.ui.interfaces.VideoPlayerCallbackForTemplate;
import com.flyingeffects.com.ui.interfaces.view.CreationTemplateMvpView;
import com.flyingeffects.com.ui.model.AnimStickerModel;
import com.flyingeffects.com.ui.presenter.CreationTemplateMvpPresenter;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.screenUtil;
import com.flyingeffects.com.utils.timeUtils;
import com.flyingeffects.com.view.EmptyControlVideo;
import com.lansosdk.box.ViewLayerRelativeLayout;
import com.suke.widget.SwitchButton;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
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


    @BindView(R.id.list_thumb)
    RecyclerView list_thumb;

    @BindView(R.id.video_player)
    EmptyControlVideo videoPlayer;


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
    private String videoPath;
    /**
     * 当前预览状态，是否在播放中
     */
    private boolean isPlaying = false;
    private int allVideoDuration;
    private int thumbCount;

    @Override
    protected int getLayoutId() {
        return R.layout.act_creation_template_edit;
    }


    @Override
    protected void initView() {
        ((TextView) findViewById(R.id.tv_top_submit)).setText("保存");
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("Message");
        if (bundle != null) {
            imgPath = bundle.getString("paths");
            videoPath = bundle.getString("video_path");
            originalPath = bundle.getString("originalPath");
        }
        presenter = new CreationTemplateMvpPresenter(this, this, videoPath, viewLayerRelativeLayout);
        videoPlayer.setUp(videoPath, true, "");
        videoPlayerInit();
        videoPlayer.setVideoAllCallBack(new VideoPlayerCallbackForTemplate(isSuccess -> {
            list_thumb.scrollToPosition(0);
            endTimer();
            isPlaying = false;
            presenter.showGifAnim(false);
            videoPlayerInit();
            nowStateIsPlaying(false);
        }));
        presenter.requestStickersList();
    }


    private void videoPlayerInit() {
        videoPlayer.startPlayLogic();
        videoPlayer.onVideoPause();
        new Handler().postDelayed(() -> videoPlayer.seekTo(1000), 1000);
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
        switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                presenter.CheckedChanged(isChecked);

            }
        });
    }


    @OnClick({R.id.tv_top_submit, R.id.ll_play, R.id.iv_add_sticker,R.id.iv_top_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_top_submit:
                if (isPlaying) {
                    videoToPause();
                }
                presenter.toSaveVideo();
                break;

            case R.id.ll_play:
                if (isPlaying) {
                    videoToPause();
                } else {
                    list_thumb.scrollToPosition(0);
                    isPlaying = true;
                    startTimer();
                    videoPlayer.startPlayLogic();
                    presenter.showGifAnim(true);
                    nowStateIsPlaying(true);
                }

                break;

            case R.id.iv_top_back:
                this.finish();
                break;

            case R.id.iv_add_sticker:
                if (isPlaying) {
                    videoPlayer.onVideoPause();
                    isPlaying = false;
                    endTimer();
                    presenter.showGifAnim(false);
                    nowStateIsPlaying(false);
                }
                //添加新的贴纸，这里的贴纸就是用户选择的贴纸
                AlbumManager.chooseImageAlbum(this, 1, SELECTALBUM, (tag, paths, isCancel, albumFileList) -> {
                    CompressionCuttingManage manage = new CompressionCuttingManage(CreationTemplateActivity.this, tailorPaths -> {
                        presenter.addNewSticker(tailorPaths.get(0), paths.get(0));
                    });
                    manage.CompressImgAndCache(paths);
                }, "");
                break;

            default:
                break;

        }
    }

    private void videoToPause() {
        videoPlayer.onVideoPause();
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
            list_thumb.smoothScrollBy(0, 0);
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
        list_thumb.post(() -> presenter.initVideoProgressView(list_thumb));
    }


    @Override
    public void onDestroy() {
        presenter.onDestroy();
        destroyTimer();
        videoPlayer.onVideoPause();
        videoPlayer.release();
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
            videoPlayer.seekTo(progress);
        }
    }

    @Override
    public void getVideoDuration(int allVideoDuration, int thumbCount) {
        this.allVideoDuration = allVideoDuration;
        this.thumbCount = thumbCount;
        LogUtil.d("OOM", "allVideoDuration=" + allVideoDuration);
        tv_total.setText(timeUtils.timeParse(allVideoDuration) + "s");
    }


    private Timer timer;
    private TimerTask task;
    private int listWidth;

    private void startTimer() {
        listWidth = list_thumb.getWidth() + screenUtil.dip2px(this, 43);
        //总共需要显示的20帧
        float allShowTime = allVideoDuration / (float) 1000 * 10;
        float perScrollByX = listWidth / allShowTime;

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
//                    LogUtil.d("OOM","perScrollByX="+perScrollByX);
//                    LogUtil.d("OOM","perScrollByX--int"+(int) Math.ceil(perScrollByX));
                    //todo  perScrollByX 有误差 ，精度在小数点后面
                    //Math.ceil 四舍五入
                    list_thumb.smoothScrollBy((int) Math.ceil(perScrollByX), 0);

                });
            }
        };
        timer.schedule(task, 0, 100);
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


}

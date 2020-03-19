package com.flyingeffects.com.ui.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.manager.AlbumManager;
import com.flyingeffects.com.manager.CompressionCuttingManage;
import com.flyingeffects.com.ui.interfaces.AlbumChooseCallback;
import com.flyingeffects.com.ui.interfaces.VideoPlayerCallbackForTemplate;
import com.flyingeffects.com.ui.interfaces.view.CreationTemplateMvpView;
import com.flyingeffects.com.ui.model.AnimStickerModel;
import com.flyingeffects.com.ui.presenter.CreationTemplateMvpPresenter;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.view.EmptyControlVideo;
import com.lansosdk.box.ViewLayerRelativeLayout;
import com.yanzhenjie.album.AlbumFile;

import java.util.ArrayList;
import java.util.List;

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

    public final static int SELECTALBUM = 0;


    private List<String> imgPath = new ArrayList<>();
    private CreationTemplateMvpPresenter presenter;
    private String videoPath;


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
            imgPath = bundle.getStringArrayList("paths");
            videoPath = bundle.getString("video_path");
        }
        presenter = new CreationTemplateMvpPresenter(this, this, videoPath, viewLayerRelativeLayout);
        videoPlayer.setUp(videoPath, true, "");
        videoPlayerInit();
        videoPlayer.setVideoAllCallBack(new VideoPlayerCallbackForTemplate(isSuccess -> {
            presenter.showGifAnim(false);
            videoPlayerInit();
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
        presenter.initStickerView(imgPath.get(0));
        presenter.initBottomLayout(viewPager);
        initViewLayerRelative();
    }


    @OnClick({R.id.tv_top_submit, R.id.iv_play, R.id.iv_add_sticker})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_top_submit:
                presenter.toSaveVideo();
                break;


            case R.id.iv_play:
                videoPlayer.startPlayLogic();
                presenter.showGifAnim(true);
                break;

            case R.id.iv_add_sticker:
                //添加新的贴纸，这里的贴纸就是用户选择的贴纸
                AlbumManager.chooseImageAlbum(this, 1, SELECTALBUM, (tag, paths, isCancel, albumFileList) -> {
                    CompressionCuttingManage manage = new CompressionCuttingManage(CreationTemplateActivity.this, tailorPaths -> {
                        presenter.addNewSticker(tailorPaths.get(0));
                    });
                    manage.CompressImgAndCache(paths);
                }, "");
                break;

            default:
                break;


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
        videoPlayer.seekTo(progress);
    }


}

package com.flyingeffects.com.ui.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.ui.interfaces.view.CreationTemplateMvpView;
import com.flyingeffects.com.ui.model.AnimStickerModel;
import com.flyingeffects.com.ui.presenter.CreationTemplateMvpPresenter;
import com.lansosdk.box.ViewLayerRelativeLayout;
import com.lansosdk.videoeditor.DrawPadView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

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



    @BindView(R.id.iv_cover)
    ImageView iv_cover;


    @BindView(R.id.drawPadView)
    DrawPadView drawPadView;



    @BindView(R.id.list_thumb)
    RecyclerView list_thumb;


    private List<String> imgPath = new ArrayList<>();
    private CreationTemplateMvpPresenter presenter;
    private String coverImagePath = "http://cdn.flying.nineton.cn/admin/20200311/5e689f344ef21Comp%201%20(0-00-00-00).jpg";
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
    }


    @Override
    protected void onResume() {
        super.onResume();
        initViewLayerRelative();
    }

    @Override
    protected void initAction() {
        presenter.initStickerView(imgPath.get(0));
        presenter.initBottomLayout(viewPager);
      //  presenter.initVideoProgressView(list_thumb);
        Glide.with(this).load(coverImagePath).into(iv_cover);


    }









    @OnClick({R.id.tv_top_submit, R.id.iv_play})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_top_submit:
                presenter.toSaveVideo();
                break;


            case R.id.iv_play:
                showPreiviewView(true);
                presenter.toPrivateVideo(drawPadView);
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
        showPreiviewView(false);
    }


    /**
     * description ：预览和编辑页面切换 isShowPreViewVideo是否显示预览界面
     * creation date: 2020/3/13
     * user : zhangtongju
     */
    private void showPreiviewView(boolean isShowPreViewVideo) {
        if (isShowPreViewVideo) {
            viewLayerRelativeLayout.setVisibility(View.GONE);
            drawPadView.setVisibility(View.VISIBLE);
            iv_cover.setVisibility(View.GONE);
        } else {
            viewLayerRelativeLayout.setVisibility(View.VISIBLE);
            drawPadView.setVisibility(View.GONE);
            iv_cover.setVisibility(View.VISIBLE);
        }
    }








}

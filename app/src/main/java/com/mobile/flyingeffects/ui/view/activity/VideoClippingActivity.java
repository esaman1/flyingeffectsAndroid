package com.mobile.flyingeffects.ui.view.activity;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mobile.flyingeffects.R;
import com.mobile.flyingeffects.base.BaseActivity;
import com.mobile.flyingeffects.ui.interfaces.view.VideoClippingMvpView;
import com.mobile.flyingeffects.ui.presenter.VideoClippingMvpPresenter;

import butterknife.BindView;


/**
 * 视频，图片编辑页面 裁剪页面
 */

public class VideoClippingActivity extends BaseActivity implements VideoClippingMvpView {


    @BindView(R.id.iv_view_container)
    ImageView iv_view_container;

    VideoClippingMvpPresenter Presenter;




    @Override
    protected int getLayoutId() {
        return R.layout.act_video_clipping;
    }

    @Override
    protected void initView() {
        Presenter=new VideoClippingMvpPresenter(this,this);
        String path = getIntent().getStringExtra("path");
        Glide.with(this).load(path).into(iv_view_container);
    }

    @Override
    protected void initAction() {
       // Presenter.initBottomLayout(bottomLinear, animInfoData, filterStateList);

    }



}

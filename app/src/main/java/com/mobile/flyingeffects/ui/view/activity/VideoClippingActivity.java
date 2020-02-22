package com.mobile.flyingeffects.ui.view.activity;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mobile.flyingeffects.R;
import com.mobile.flyingeffects.base.BaseActivity;

import butterknife.BindView;


/**
 * 视频裁剪页面
 */

public class VideoClippingActivity extends BaseActivity {


    @BindView(R.id.iv_view_container)
    ImageView iv_view_container;

    @Override
    protected int getLayoutId() {
        return R.layout.act_video_clipping;
    }

    @Override
    protected void initView() {
        String path = getIntent().getStringExtra("path");
        Glide.with(this).load(path).into(iv_view_container);
    }

    @Override
    protected void initAction() {

    }
}

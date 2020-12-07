package com.flyingeffects.com.ui.view.activity;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseActivity;

import butterknife.BindView;


/**
 * description ：换装预览界面
 * creation date: 2020/12/7
 * user : zhangtongju
 */
public class DressUpPreviewActivity extends BaseActivity {

    @BindView(R.id.iv_show_content)
    ImageView iv_show_content;


    @Override
    protected int getLayoutId() {
        return R.layout.act_dress_up_preview;
    }

    @Override
    protected void initView() {
        String url=getIntent().getStringExtra("url");
        Glide.with(this).load(url).into(iv_show_content);

    }

    @Override
    protected void initAction() {

    }
}

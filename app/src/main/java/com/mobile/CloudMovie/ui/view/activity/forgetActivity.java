package com.mobile.CloudMovie.ui.view.activity;

import com.mobile.CloudMovie.R;
import com.mobile.CloudMovie.base.BaseActivity;

public class forgetActivity extends BaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.act_forget;
    }

    @Override
    protected void initView() {
        findViewById(R.id.iv_top_back).setOnClickListener(this);
    }

    @Override
    protected void initAction() {

    }
}

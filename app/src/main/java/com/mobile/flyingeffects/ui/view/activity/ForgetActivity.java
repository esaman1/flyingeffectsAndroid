package com.mobile.flyingeffects.ui.view.activity;

import com.mobile.flyingeffects.R;
import com.mobile.flyingeffects.base.BaseActivity;

public class ForgetActivity extends BaseActivity {


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

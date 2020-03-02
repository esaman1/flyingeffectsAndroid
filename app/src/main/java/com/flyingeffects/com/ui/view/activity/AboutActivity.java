package com.flyingeffects.com.ui.view.activity;

import android.content.Intent;
import android.view.View;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseActivity;

import butterknife.OnClick;

/**
 * 关于界面
 */
public class AboutActivity extends BaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.act_about;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initAction() {

    }

    @OnClick({R.id.ll_close_account})
    public void onClick(View view) {
         switch (view.getId()){
            case R.id.ll_close_account:
                break;





        }

    }
}

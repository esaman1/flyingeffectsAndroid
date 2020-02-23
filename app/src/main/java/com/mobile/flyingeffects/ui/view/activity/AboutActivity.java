package com.mobile.flyingeffects.ui.view.activity;

import android.content.Intent;
import android.view.View;

import com.mobile.flyingeffects.R;
import com.mobile.flyingeffects.base.BaseActivity;

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

    @OnClick({R.id.ll_exit_login})
    public void onClick(View view) {
         switch (view.getId()){
            case R.id.ll_exit_login:
                Intent intent=new Intent(this,LoginActivity.class);
                startActivity(intent);
                break;
        }

    }
}

package com.flyingeffects.com.ui.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.utils.ToastUtil;


/**
 * description ：举报页面
 * creation date: 2020/7/1
 * user : zhangtongju
 */

public class ReportActivity extends Activity {

    LinearLayout ll_test;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_report);
        ll_test=findViewById(R.id.ll_test);
        ll_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtil.showToast("提交成功");
                finish();
            }
        });

        LinearLayout ll_parent=findViewById(R.id.ll_parent);
        ll_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}

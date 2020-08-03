package com.flyingeffects.com.ui.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.flyingeffects.com.R;
import com.flyingeffects.com.utils.ToastUtil;

import de.greenrobot.event.EventBus;


/**
 * description ：评论页面长按事件
 * creation date: 2020/7/1
 * user : zhangtongju
 */

public class MessageLongClickActivity extends Activity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_message_long_click);
        EventBus.getDefault().register(this);
        LinearLayout    ll_report=findViewById(R.id.ll_report);
        ll_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtil.showToast("提交成功");
                finish();
            }
        });


        LinearLayout    layout_width=findViewById(R.id.ll_delete);
        layout_width.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtil.showToast("提交成功");
                finish();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

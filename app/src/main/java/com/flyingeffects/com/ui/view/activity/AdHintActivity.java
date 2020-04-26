package com.flyingeffects.com.ui.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.flyingeffects.com.R;
import com.flyingeffects.com.enity.showAdCallback;

import de.greenrobot.event.EventBus;


/**
 * description ：是否观看激励视频广告页面
 * creation date: 2020/4/26
 * user : zhangtongju
 */
public class AdHintActivity extends Activity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_ad_hint);
        findViewById(R.id.tv_cancle).setOnClickListener(listenner);
        findViewById(R.id.tv_watch_ad).setOnClickListener(listenner);

    }



    View.OnClickListener listenner= view -> {
        switch (view.getId()) {
            case R.id.tv_cancle:
                //取消
                AdHintActivity.this.finish();
                break;

            case R.id.tv_watch_ad:
                //观看广告
                EventBus.getDefault().post(new showAdCallback());
                AdHintActivity.this.finish();
                break;

        }
    };




}

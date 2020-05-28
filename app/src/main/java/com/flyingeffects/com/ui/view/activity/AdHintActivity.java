package com.flyingeffects.com.ui.view.activity;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.flyingeffects.com.R;
import com.flyingeffects.com.enity.showAdCallback;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.model.FromToTemplate;

import de.greenrobot.event.EventBus;


/**
 * description ：是否观看激励视频广告页面
 * creation date: 2020/4/26
 * user : zhangtongju
 */
public class AdHintActivity extends Activity {

    String title;
    String from ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_ad_hint);
        findViewById(R.id.tv_cancle).setOnClickListener(listenner);
        findViewById(R.id.tv_watch_ad).setOnClickListener(listenner);
        from=getIntent().getStringExtra("from");
        title=getIntent().getStringExtra("templateTitle");
    }


    View.OnClickListener listenner = view -> {
        switch (view.getId()) {
            case R.id.tv_cancle:
                //取消
                AdHintActivity.this.finish();
                if(!TextUtils.isEmpty(from)&&from.equals(FromToTemplate.ISFROMTEMPLATE)){
                    statisticsEventAffair.getInstance().setFlag(AdHintActivity.this, "mb_ad_cancel",title);
                }else{
                    statisticsEventAffair.getInstance().setFlag(AdHintActivity.this, "bj_ad_cancel",title);
                }
                break;

            case R.id.tv_watch_ad:
                //观看广告
                if(!TextUtils.isEmpty(from)&&from.equals(FromToTemplate.ISFROMTEMPLATE)){
                    statisticsEventAffair.getInstance().setFlag(AdHintActivity.this, "mb_ad_open",title);
                }else{
                    statisticsEventAffair.getInstance().setFlag(AdHintActivity.this, "bj_ad_open",title);
                }
                EventBus.getDefault().post(new showAdCallback());
                AdHintActivity.this.finish();
                break;

        }
    };


}

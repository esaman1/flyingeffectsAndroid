package com.flyingeffects.com.ui.view.activity;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.enity.showAdCallback;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.model.FromToTemplate;

import butterknife.BindView;
import de.greenrobot.event.EventBus;


/**
 * description ：是否观看激励视频广告页面
 * creation date: 2020/4/26
 * user : zhangtongju
 */
public class AdHintActivity extends Activity {

    String title;
    String from;

    TextView tv_content_1;
    ImageView iv_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_ad_hint);
        findViewById(R.id.tv_cancle).setOnClickListener(listenner);
        iv_btn=findViewById(R.id.iv_btn);
        tv_content_1=findViewById(R.id.tv_content_1);
        findViewById(R.id.tv_watch_ad).setOnClickListener(listenner);
        from = getIntent().getStringExtra("from");
        if(!TextUtils.isEmpty(from) && from.equals("isFormPreviewVideo")){
            tv_content_1.setText("「看完后就能一键保存视频」");
            iv_btn.setImageResource(R.mipmap.ad_alert_bt_keep);//ad_alert_bt_save
        }else if(!TextUtils.isEmpty(from) && from.equals("isFormDressUp")){
            tv_content_1.setText("「看完后就能一键保存图片」");
            iv_btn.setImageResource(R.mipmap.ad_alert_bt_keep);//ad_alert_bt_save
        }else{
            tv_content_1.setText("「看完后就能制作飞闪视频」");
            iv_btn.setImageResource(R.mipmap.ad_alert_bt);
        }
        title = getIntent().getStringExtra("templateTitle");
        statisticsEventAffair.getInstance().setFlag(AdHintActivity.this, "video_ad_alert", title);
    }


    View.OnClickListener listenner = view -> {
        switch (view.getId()) {
            case R.id.tv_cancle:
                //取消
                if (from.equals(FromToTemplate.ISTEMPLATE)) {
                    statisticsEventAffair.getInstance().setFlag(AdHintActivity.this, "mb_ad_cancel", title);
                } else {
                    statisticsEventAffair.getInstance().setFlag(AdHintActivity.this, "bj_ad_cancel", title);
                }
                statisticsEventAffair.getInstance().setFlag(AdHintActivity.this, "video_ad_alert_click_cancel");
                AdHintActivity.this.finish();
                break;

            case R.id.tv_watch_ad:

                if(!DoubleClick.getInstance().isFastZDYDoubleClick(2000)){
                    //观看广告
                    if (from.equals(FromToTemplate.ISTEMPLATE)) {
                        statisticsEventAffair.getInstance().setFlag(AdHintActivity.this, "mb_ad_open", title);
                    } else {
                        statisticsEventAffair.getInstance().setFlag(AdHintActivity.this, "bj_ad_open", title);
                    }
                    statisticsEventAffair.getInstance().setFlag(AdHintActivity.this, "video_ad_alert_click_confirm");
                    EventBus.getDefault().post(new showAdCallback(from));
                    AdHintActivity.this.finish();
                }


                break;

        }
    };


}

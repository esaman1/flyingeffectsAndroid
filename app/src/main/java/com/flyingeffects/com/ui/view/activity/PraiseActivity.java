package com.flyingeffects.com.ui.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.flyingeffects.com.R;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.ui.model.ShowPraiseModel;
import com.flyingeffects.com.utils.ToastUtil;

/**
 * description ：好评弹窗
 * date: ：2019/11/12 14:38
 * author: 张同举 @邮箱 jutongzhang@sina.com
 */
public class PraiseActivity extends Activity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_praise);


        StatisticsEventAffair.getInstance().setFlag(PraiseActivity.this, "16_pick 20_hp");
        findViewById(R.id.tv_0).setOnClickListener(view -> {
            StatisticsEventAffair.getInstance().setFlag(PraiseActivity.this, "20_hp_cancel1");
            ShowPraiseModel.statisticsCloseNum();
            finishAct();
        });


        findViewById(R.id.tv_1).setOnClickListener(view -> {
            ShowPraiseModel.setHasComment();
            StatisticsEventAffair.getInstance().setFlag(PraiseActivity.this, "20_hp_confirm");
            reception();
            finishAct();
        });

    }



    public void reception() {
        try {
            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            ToastUtil.showToast(getString(R.string.install_app_store_notification));
            e.printStackTrace();
        }


    }



    private void finishAct(){
        finish();
    }


    @Override
    protected void onDestroy() {
        StatisticsEventAffair.getInstance().setFlag(PraiseActivity.this, "20_hp_cancel2");
        super.onDestroy();
    }
}

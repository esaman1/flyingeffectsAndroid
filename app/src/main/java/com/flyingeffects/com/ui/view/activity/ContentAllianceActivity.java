package com.flyingeffects.com.ui.view.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.flyingeffects.com.R;
import com.flyingeffects.com.manager.AdConfigs;
import com.flyingeffects.com.utils.LogUtil;
import com.nineton.ntadsdk.itr.ContentAllianceAdCallBack;
import com.nineton.ntadsdk.manager.ContentAllianceAdManager;



/**
 * description ：快手广告
 * creation date: 2020/10/9
 * user : zhangtongju
 */

public class ContentAllianceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_alliacnce);
        LogUtil.d("OOM","ContentAllianceActivity=");
        ContentAllianceAdManager contentAllianceAdManager = new ContentAllianceAdManager();
        contentAllianceAdManager.showContentAllianceAd(this, AdConfigs.APP_kuaishou, new ContentAllianceAdCallBack() {
            @Override
            public void onContentAllianceAdShow(Fragment adFragment) {
                LogUtil.d("OOM","adFragment=");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, adFragment)
                        .commitAllowingStateLoss();
            }

            @Override
            public void onContentAllianceAdError(String error) {
                LogUtil.d("OOM","error="+error);
            }

        });
    }
}
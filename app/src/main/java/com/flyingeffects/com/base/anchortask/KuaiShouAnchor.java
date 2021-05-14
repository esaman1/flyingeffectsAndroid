package com.flyingeffects.com.base.anchortask;

import android.text.TextUtils;

import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.manager.TTAdManagerHolder;
import com.flyingeffects.com.utils.ChannelUtil;
import com.kwai.monitor.log.OAIDProxy;
import com.kwai.monitor.log.TurboAgent;
import com.kwai.monitor.log.TurboConfig;
import com.xj.anchortask.library.AnchorTask;

public class KuaiShouAnchor extends AnchorTask {

    public KuaiShouAnchor() {
        super(TaskNameConstants.INIT_KUAI_SHOU_MONITOR);
    }

    @Override
    public boolean isRunOnMainThread() {
        return true;
    }

    @Override
    public void run() {
        initKuaiShouAd();
    }

    private void initKuaiShouAd() {
        TurboAgent.init(TurboConfig.TurboConfigBuilder.create(BaseApplication.getInstance())
                .setAppId("70409")
                .setAppName("feishan")
                .setAppChannel(ChannelUtil.getChannel(BaseApplication.getInstance()))
                .setOAIDProxy(new OAIDProxy() {
                    @Override
                    public String getOAID() {
                        if (!TextUtils.isEmpty(BaseConstans.getOaid())) {
                            return BaseConstans.getOaid();
                        }
                        return null;
                    }
                })
                .setEnableDebug(BaseConstans.DEBUG)
                .build());
    }


}

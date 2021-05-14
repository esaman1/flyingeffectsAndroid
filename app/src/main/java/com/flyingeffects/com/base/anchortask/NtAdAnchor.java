package com.flyingeffects.com.base.anchortask;


import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.manager.AdConfigs;
import com.flyingeffects.com.utils.ChannelUtil;
import com.flyingeffects.com.utils.SystemUtil;
import com.nineton.ntadsdk.NTAdConfig;
import com.nineton.ntadsdk.NTAdSDK;
import com.xj.anchortask.library.AnchorTask;


public class NtAdAnchor extends AnchorTask {

    public NtAdAnchor() {
        super(TaskNameConstants.INIT_AD_SDK);
    }

    @Override
    public void run() {

        NTAdSDK.init(BaseApplication.getInstance()
                , new NTAdConfig.Builder()
                        .appName("飞闪")
                        .appVersion(SystemUtil.getVersionName(BaseApplication.getInstance()))
                        .appId("61074cddf23c0a8dd2b7e00996057e78")
                        .appChannel(ChannelUtil.getChannel(BaseApplication.getInstance()))
                        .TTAppKey(AdConfigs.APP_ID_CSJ)
                        .KSAppKey("517200002")
                        .TXYXAppKey("30097")
                        .KaiJiaAppKey("68662a49")
                        .GDTAppKey(AdConfigs.APP_ID_GDT)
                        .isDebug(false)
                        .build());
    }

    @Override
    public boolean isRunOnMainThread() {
        return true;
    }
}

package com.flyingeffects.com.base.anchortask;

import androidx.multidex.MultiDex;

import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.utils.LogUtil;
import com.lansosdk.box.OnLanSongLogOutListener;
import com.lansosdk.videoeditor.LanSoEditor;
import com.xj.anchortask.library.AnchorTask;

public class LanSongAnchor extends AnchorTask {

    public LanSongAnchor() {
        super(TaskNameConstants.INIT_LAN_SONG);
    }

    @Override
    public void run() {
        LanSoEditor.initSDK(BaseApplication.getInstance(), "jiu_LanSongSDK_android5.key");
        LanSoEditor.setSDKLogOutListener(new OnLanSongLogOutListener() {
            @Override
            public void onLogOut(int i, String s) {
                LogUtil.d("lansong", "蓝松具体错误信息为" + s);
            }
        });
    }
}

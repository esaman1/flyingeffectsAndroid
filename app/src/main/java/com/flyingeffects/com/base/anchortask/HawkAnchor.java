package com.flyingeffects.com.base.anchortask;

import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.utils.LogUtil;
import com.lansosdk.box.OnLanSongLogOutListener;
import com.lansosdk.videoeditor.LanSoEditor;
import com.orhanobut.hawk.Hawk;
import com.xj.anchortask.library.AnchorTask;

public class HawkAnchor extends AnchorTask {

    public HawkAnchor() {
        super(TaskNameConstants.INIT_HAWK);
    }

    @Override
    public void run() {
        Hawk.init(BaseApplication.getInstance()).build();
    }
}

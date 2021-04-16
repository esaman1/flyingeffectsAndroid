package com.flyingeffects.com.base.anchortask;

import com.faceunity.FURenderer;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.manager.MediaLoader;
import com.xj.anchortask.library.AnchorTask;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;

import java.util.Locale;

public class FuAnchor extends AnchorTask {

    public FuAnchor() {
        super(TaskNameConstants.INIT_FU);
    }

    @Override
    public void run() {
        FURenderer.initFURenderer(BaseApplication.getInstance());
    }
}

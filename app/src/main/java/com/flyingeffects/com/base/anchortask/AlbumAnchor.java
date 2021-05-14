package com.flyingeffects.com.base.anchortask;

import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.manager.MediaLoader;
import com.green.hand.library.EmojiManager;
import com.xj.anchortask.library.AnchorTask;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;

import java.util.Locale;

public class AlbumAnchor extends AnchorTask {

    public AlbumAnchor() {
        super(TaskNameConstants.INIT_ALBUM);
    }

    @Override
    public void run() {
        Album.initialize(AlbumConfig.newBuilder(BaseApplication.getInstance())
                .setAlbumLoader(new MediaLoader())
                .setLocale(Locale.getDefault())
                .build()
        );
    }
}

package com.flyingeffects.com.base.anchortask;

import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.manager.TTAdManagerHolder;
import com.green.hand.library.EmojiManager;
import com.xj.anchortask.library.AnchorTask;

public class EmojiAnchor extends AnchorTask {

    public EmojiAnchor() {
        super(TaskNameConstants.INIT_EMOJI);
    }

    @Override
    public void run() {
        EmojiManager.init(BaseApplication.getInstance());
    }
}

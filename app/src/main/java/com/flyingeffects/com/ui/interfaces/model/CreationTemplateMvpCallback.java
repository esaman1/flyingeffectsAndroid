package com.flyingeffects.com.ui.interfaces.model;

import com.flyingeffects.com.ui.model.AnimStickerModel;
import com.flyingeffects.com.view.StickerView;

public interface CreationTemplateMvpCallback {

    void ItemClickForStickView(AnimStickerModel stickView);

    void hasPlayingComplete();
}

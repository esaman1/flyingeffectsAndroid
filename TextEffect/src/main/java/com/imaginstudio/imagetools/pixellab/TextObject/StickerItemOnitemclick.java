package com.imaginstudio.imagetools.pixellab.TextObject;

public interface StickerItemOnitemclick {

    void stickerOnclick(int type, TextComponent textComponent);

    void stickerMove(TextComponent textComponent);

    default void stickerClickShowFrame() {
    }

}

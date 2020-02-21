package com.shixing.sxve.ui;

import com.shixing.sxve.ui.model.MediaUiModel;
import com.shixing.sxve.ui.model.TextUiModel;

public interface AssetDelegate {
    void pickMedia(MediaUiModel model);

    void editText(TextUiModel model);
}

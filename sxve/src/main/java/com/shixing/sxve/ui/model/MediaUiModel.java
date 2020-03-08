package com.shixing.sxve.ui.model;

import com.shixing.sxve.ui.AssetDelegate;
import com.shixing.sxve.ui.util.Size;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class MediaUiModel extends AssetUi {

    MediaUiModel(String folder, JSONObject ui, AssetDelegate delegate, Size size) throws JSONException {
        super(folder, ui, delegate, size);
    }

    /**
     * @return 素材时长，单位为帧
     */
    public abstract int getDuration();

    public abstract int getNowIndex();

    public abstract void setImageAsset(String path);

    public abstract void setVideoPath(String path, boolean mute, float startTime);
}

package com.imaginstudio.imagetools.pixellab.imageinfo;

import android.os.Bundle;

import com.imaginstudio.imagetools.pixellab.appStateConstants;
import com.imaginstudio.imagetools.pixellab.commonFuncs;

public class ImageSource {
    boolean assetFolder = false;
    String path = null;
    SourceType sourceType = null;

    public enum SourceType {
        local_file,
        assets
    }

    public boolean checkValid() {
        return (this.sourceType == null || this.path == null || this.path.isEmpty()) ? false : true;
    }

    /* access modifiers changed from: package-private */
    public void set(SourceType sourceType2, String path2, boolean assetFolder2) {
        this.sourceType = sourceType2;
        this.path = path2;
        this.assetFolder = assetFolder2;
    }

    public ImageSource copy() {
        ImageSource newImgSrc = new ImageSource();
        newImgSrc.set(this.sourceType, this.path, this.assetFolder);
        return newImgSrc;
    }

    public String getPath() {
        return this.path;
    }

    public boolean isAssetFolder() {
        return this.assetFolder;
    }

    public ImageSource() {
    }

    public ImageSource(String localFilePath) {
        setLocalFilePath(localFilePath);
    }

    public ImageSource(String assetsPath, boolean assetFolder2) {
        setAssetsPath(assetsPath, assetFolder2);
    }

    public SourceType getSourceType() {
        return this.sourceType;
    }

    public void setAssetsPath(String assetsPath, boolean assetFolder2) {
        this.assetFolder = assetFolder2;
        this.sourceType = SourceType.assets;
        this.path = assetsPath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.sourceType = SourceType.local_file;
        this.path = localFilePath;
    }

    public ImageSource(Bundle bundle) {
        if (bundle != null) {
            fromBundle(bundle);
        }
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(appStateConstants.IMG_SRC_ASSET_FOLDER, this.assetFolder);
        if (this.sourceType != null) {
            bundle.putString(appStateConstants.IMG_SRC_SOURCE_TYPE, this.sourceType.name());
        }
        if (this.path != null) {
            bundle.putString(appStateConstants.IMG_SRC_PATH, getPath());
        }
        return bundle;
    }

    private void fromBundle(Bundle bundle) {
        this.assetFolder = bundle.getBoolean(appStateConstants.IMG_SRC_ASSET_FOLDER);
        this.path = bundle.getString(appStateConstants.IMG_SRC_PATH);
        this.sourceType = bundle.getString(appStateConstants.IMG_SRC_SOURCE_TYPE) != null ? SourceType.valueOf(bundle.getString(appStateConstants.IMG_SRC_SOURCE_TYPE)) : null;
    }

    public boolean compare(ImageSource other) {
        return checkValid() && other.checkValid() && other.sourceType == this.sourceType && commonFuncs.compareStrings(this.path, other.path) && this.assetFolder == other.assetFolder;
    }
}

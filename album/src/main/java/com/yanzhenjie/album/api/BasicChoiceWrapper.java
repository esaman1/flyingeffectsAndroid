/*
 * Copyright 2017 Yan Zhenjie.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yanzhenjie.album.api;

import android.content.Context;
import androidx.annotation.IntRange;

import com.yanzhenjie.album.Filter;

/**
 * Created by YanZhenjie on 2017/8/16.
 */
public abstract class BasicChoiceWrapper<Returner extends BasicChoiceWrapper, Result, Cancel, Checked,view> extends BaseAlbumWrapper<Returner, Result, Cancel, Checked,view> {

    boolean mHasCamera = true;
    int mColumnCount = 2;
    long videoTime;
    String mTitle;
    String mMusicPath;
    String material_info;
    boolean isFromCreateVideo=false;
    Filter<Long> mSizeFilter;
    Filter<String> mMimeTypeFilter;

    boolean mFilterVisibility = true;

    BasicChoiceWrapper(Context context) {
        super(context);
    }

    /**
     * Turn on the camera function.
     */
    public Returner camera(boolean hasCamera) {
        this.mHasCamera = hasCamera;
        return (Returner) this;
    }

    /**
     * Sets the number of columns for the page.
     *
     * @param count the number of columns.
     */
    public Returner columnCount(@IntRange(from = 2, to = 4) int count) {
        this.mColumnCount = count;
        return (Returner) this;
    }


    public Returner setMineVideoTime( long videoTime) {
        this.videoTime = videoTime;
        return (Returner) this;
    }


    public Returner setModelTitle( String title) {
        this.mTitle = title;
        return (Returner) this;
    }

    public Returner setMusicPath( String musicPath) {
        this.mMusicPath = musicPath;
        return (Returner) this;
    }

    public Returner materialInfo(String materialInfo) {
        this.material_info = materialInfo;
        return (Returner) this;
    }


    public Returner isFromCreateVideo( boolean isFromCreateVideo) {
        this.isFromCreateVideo = isFromCreateVideo;
        return (Returner) this;
    }

    /**
     * Filter the file size.
     *
     * @param filter filter.
     */
    public Returner filterSize(Filter<Long> filter) {
        this.mSizeFilter = filter;
        return (Returner) this;
    }

    /**
     * Filter the file extension.
     *
     * @param filter filter.
     */
    public Returner filterMimeType(Filter<String> filter) {
        this.mMimeTypeFilter = filter;
        return (Returner) this;
    }

    /**
     * The visibility of the filtered file.
     *
     * @param visibility true is displayed, false is not displayed.
     */
    public Returner afterFilterVisibility(boolean visibility) {
        this.mFilterVisibility = visibility;
        return (Returner) this;
    }

}
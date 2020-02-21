package com.mobile.flyingeffects.ui.interfaces;

import com.yanzhenjie.album.AlbumFile;

import java.util.ArrayList;
import java.util.List;

public interface AlbumChooseCallback {


    void resultFilePath(int tag, List<String> paths, boolean isCancel, ArrayList<AlbumFile> albumFileList);
}

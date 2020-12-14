package com.flyingeffects.com.ui.interfaces;

import com.yanzhenjie.album.AlbumFile;

import java.util.ArrayList;
import java.util.List;

public interface AlbumChooseCallback {


    void resultFilePath(int tag, List<String> paths, boolean isCancel,boolean isFromCamera, ArrayList<AlbumFile> albumFileList);
}

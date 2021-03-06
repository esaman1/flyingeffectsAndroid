package com.flyingeffects.com.ui.interfaces.model;

import android.graphics.Bitmap;

import com.flyingeffects.com.entity.TemplateThumbItem;
import com.shixing.sxve.ui.model.TemplateModel;

import java.util.ArrayList;
import java.util.List;

public interface TemplateMvpCallback {

    void completeTemplate(TemplateModel templateModel);

    void toPreview(String path);

    void ChangeMaterialCallback(ArrayList<TemplateThumbItem> listItem, List<String> list_all,List<String> listAssets);

    void returnReplaceableFilePath(String[]paths);

    void getCartoonPath(String path);

    void showMattingVideoCover(Bitmap bitmap,String imgPath);

    void ChangeMaterialCallbackForVideo(String originalPath,String path,boolean needMatting);

    void showBottomIcon(String path);

    void getSpliteMusic(String path);

    void GetChangeDressUpData(List<String> paths);

    void setDialogProgress(int progress);

    void setDialogDismiss();

    void showProgressDialog();
}

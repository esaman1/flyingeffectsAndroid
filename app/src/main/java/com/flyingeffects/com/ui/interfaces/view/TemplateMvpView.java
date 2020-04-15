package com.flyingeffects.com.ui.interfaces.view;

import android.graphics.Bitmap;

import com.flyingeffects.com.enity.TemplateThumbItem;
import com.shixing.sxve.ui.model.TemplateModel;

import java.util.ArrayList;
import java.util.List;

public interface TemplateMvpView {

    void completeTemplate(TemplateModel templateModel);

    void toPreview(String path);

    void ChangeMaterialCallback(ArrayList<TemplateThumbItem> listItem, List<String> list_all,List<String> listAssets);

    void returnReplaceableFilePath(String[]paths);

    void getCartoonPath(String path);

    void showMattingVideoCover(Bitmap bp);
    void showBottomIcon(String path);

    void ChangeMaterialCallbackForVideo(String originalPath,String path,boolean needMatting);
}

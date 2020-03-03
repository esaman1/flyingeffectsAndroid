package com.flyingeffects.com.ui.interfaces.model;

import com.flyingeffects.com.enity.TemplateThumbItem;
import com.shixing.sxve.ui.model.TemplateModel;

import java.util.ArrayList;
import java.util.List;

public interface TemplateMvpCallback {


    void completeTemplate(TemplateModel templateModel);

    void toPreview(String path);

    void ChangeMaterialCallback(ArrayList<TemplateThumbItem> listItem, List<String> list_all);

    void returnReplaceableFilePath(String[]paths);


}

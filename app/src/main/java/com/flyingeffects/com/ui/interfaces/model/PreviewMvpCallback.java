package com.flyingeffects.com.ui.interfaces.model;

import java.util.List;

public interface PreviewMvpCallback {

   void  getCompressImgList(List<String>imgList);

   void showDownProgress(int progress);

   void getTemplateFileSuccess(String filePath);

}

package com.flyingeffects.com.ui.interfaces.model;

import com.flyingeffects.com.enity.new_fag_template_item;

import java.util.List;

public interface PreviewMvpCallback {

   void  getCompressImgList(List<String>imgList);

   void showDownProgress(int progress);

   void getTemplateFileSuccess(String filePath);

   void downVideoSuccess(String path,String imagePath);

   void collectionResult();

   void getTemplateLInfo(new_fag_template_item item);

   void hasLogin(boolean hasLogin);



   void getVideoCover(String filePath,String originalPath,String videoPath);

}

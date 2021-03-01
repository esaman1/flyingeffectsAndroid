package com.flyingeffects.com.ui.model;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;

public class GetPathTypeModel {
    private  static GetPathTypeModel thisModel;

    public static GetPathTypeModel getInstance(){

        if(thisModel==null){
            thisModel=new GetPathTypeModel();
        }
        return  thisModel;

    }

    public  String getPathType(String path) {
        String mimeType;
        String suffix = path.substring(path.lastIndexOf(".") + 1).toUpperCase();
        if ("MP4".equalsIgnoreCase(suffix) || "M4V".equalsIgnoreCase(suffix) || "3GP".equalsIgnoreCase(suffix) || "3G2".equalsIgnoreCase(suffix) || "WMV".equalsIgnoreCase(suffix) || "ASF".equalsIgnoreCase(suffix) || "AVI".equalsIgnoreCase(suffix) || "FLV".equalsIgnoreCase(suffix) || "MKV".equalsIgnoreCase(suffix) || "WEBM".equalsIgnoreCase(suffix)) {
            mimeType = "video/*";
        } else {
            mimeType = "image/*";
        }

        return mimeType;
    }






    public String getMediaType(String path){
        String  mimeType ;
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        if (!TextUtils.isEmpty(extension)) {
            mimeType= MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            if (mimeType == null) {
                mimeType = GetPathTypeModel.getInstance().getPathType(path);
            }
        } else {
            mimeType =getPathType(path);
        }
        return  mimeType;
    }

}

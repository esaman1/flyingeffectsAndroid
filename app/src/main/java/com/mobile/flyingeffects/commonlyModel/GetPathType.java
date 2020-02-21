package com.mobile.flyingeffects.commonlyModel;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;

public class GetPathType {
    private  static GetPathType  thisModel;

    public static GetPathType getInstance(){

        if(thisModel==null){
            thisModel=new GetPathType();
        }
        return  thisModel;

    }

    public  String getPathType(String path) {
        String mimeType;
        String suffix = path.substring(path.lastIndexOf(".") + 1).toUpperCase();
        if (suffix.equalsIgnoreCase("MP4") || suffix.equalsIgnoreCase("M4V") || suffix.equalsIgnoreCase("3GP") || suffix.equalsIgnoreCase("3G2") || suffix.equalsIgnoreCase("WMV") || suffix.equalsIgnoreCase("ASF") || suffix.equalsIgnoreCase("AVI") || suffix.equalsIgnoreCase("FLV") || suffix.equalsIgnoreCase("MKV") || suffix.equalsIgnoreCase("WEBM")) {
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
                mimeType = GetPathType.getInstance().getPathType(path);
            }
        } else {
            mimeType =getPathType(path);
        }
        return  mimeType;
    }

}

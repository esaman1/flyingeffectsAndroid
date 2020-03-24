package com.yanzhenjie;

import java.io.File;

public class filterSupportMedia {

    static filterSupportMedia supportMedia;

    String []strList={".wmv",".WMV",".mov",".MOV",".mpg",".MPG",".3gp",".3GP","lansongBox",".avi",".AVI",".gif"};

    public static filterSupportMedia getInstance(){
        if(supportMedia==null){
            supportMedia=new filterSupportMedia();
        }
        return  supportMedia;
    }



    /**
     * description ：相册过滤不支持的视频格式
     * date: ：2019/12/10 16:34
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
        public boolean needFilerMedia(String str){
            for (String s:strList
                 ) {
                if(str.equalsIgnoreCase(s)){
                    return  true;
                }
            }
            return  false;
    }



    public boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (f.exists() && f.length() > 0) {
                return true;
            }

        } catch (Exception e) {
            return false;
        }

        return false;
    }





    }

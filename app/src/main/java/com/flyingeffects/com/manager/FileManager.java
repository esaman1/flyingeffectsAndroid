package com.flyingeffects.com.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    public static List<String> getFilesAllName(String path) {
     //传入指定文件夹的路径
        File file = new File(path);
        File[] files = file.listFiles();
        List<String> imagePaths = new ArrayList<>();
        if(files!=null){
            for (File value : files) {
                if (checkIsImageFile(value.getPath())) {
                    imagePaths.add(value.getPath());
                }
            }
        }
        return imagePaths;
    }


    /**
     * 15  * 判断是否是照片
     * 16
     */
    public static boolean checkIsImageFile(String fName) {
        boolean isImageFile = false;
        //获取拓展名
        String fileEnd = fName.substring(fName.lastIndexOf(".") + 1,
                fName.length()).toLowerCase();
        if (fileEnd.equals("jpg") || fileEnd.equals("png") || fileEnd.equals("gif")
                || fileEnd.equals("jpeg") || fileEnd.equals("bmp")) {
            isImageFile = true;
        } else {
            isImageFile = false;
        }
        return isImageFile;
    }

}

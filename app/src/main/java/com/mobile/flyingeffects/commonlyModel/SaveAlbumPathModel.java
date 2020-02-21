package com.mobile.flyingeffects.commonlyModel;

import android.os.Environment;

import java.io.File;

public class SaveAlbumPathModel {
   private  static SaveAlbumPathModel  thisModel;

    public static SaveAlbumPathModel getInstance(){

        if(thisModel==null){
            thisModel=new SaveAlbumPathModel();
        }
        return  thisModel;

    }


    public String getKeepOutput() {
        String product = android.os.Build.MANUFACTURER; //获得手机厂商
        if (product != null && product.equals("vivo")) {
            File file_camera = new File(Environment.getExternalStorageDirectory() + "/相机");
            if (file_camera.exists()) {
                return file_camera.getPath() + File.separator + System.currentTimeMillis() + "synthetic.mp4";
            }
        }
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        File path_Camera = new File(path + "/Camera");
        if (path_Camera.exists()) {
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + "Camera" + File.separator + System.currentTimeMillis() + "synthetic.mp4";
        }
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + System.currentTimeMillis() + "synthetic.mp4";
    }
    public String getKeepOutputForImage() {
        String product = android.os.Build.MANUFACTURER; //获得手机厂商
        if (product != null && product.equals("vivo")) {
            File file_camera = new File(Environment.getExternalStorageDirectory() + "/相机");
            if (file_camera.exists()) {
                return file_camera.getPath() + File.separator + System.currentTimeMillis();
            }
        }
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        File path_Camera = new File(path + "/Camera");
        if (path_Camera.exists()) {
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + "Camera" + File.separator + System.currentTimeMillis() + "synthetic.mp4";
        }
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + System.currentTimeMillis() + "synthetic.mp4";
    }


}

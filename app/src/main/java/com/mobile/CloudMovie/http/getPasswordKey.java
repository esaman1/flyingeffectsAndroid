package com.mobile.CloudMovie.http;

public class getPasswordKey {
// getPasswordKey

    public native static String getKey();


    public native static  int getTimeKey();


    // 动态导入 so 库
    static {
        System.loadLibrary("myzxEncryption");
    }




}

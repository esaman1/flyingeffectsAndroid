package com.flyingeffects.com.manager;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.constans.BaseConstans;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;

/**
 * description ：缓存管理类
 * creation date: 2020/6/22
 * user : zhangtongju
 */
public class DiskLruCacheManage {


    public DiskLruCacheManage() {
        FileManager fileManager = new FileManager();
        String diskMattingFolder = fileManager.getFileCachePath(BaseApplication.getInstance(), "DiskMattingFolder");
        long maxSize=1024*1020*20;
        try {
            DiskLruCache.open(new File(diskMattingFolder), BaseConstans.getAppVersion(), 1, maxSize);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




}

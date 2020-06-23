package com.flyingeffects.com.manager;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * description ：缓存管理类
 * creation date: 2020/6/22
 * user : zhangtongju
 */
public class LruCacheManage {

    private  LruCache mMemoryCache;

    public LruCacheManage(){
        int maxMemory = (int) (Runtime.getRuntime().totalMemory()/1024);
        int cacheSize = maxMemory/2;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes()*value.getHeight()/1024;
            }
        };
    }


    public void putBitmap(String index,Bitmap bp){
        mMemoryCache.put(index,bp);
    }



    public  Bitmap getLruCacheBitmap(String key){
        return (Bitmap) mMemoryCache.get(key);
    }

    public int getCacheCount(){

        return  mMemoryCache.putCount();
    }




}

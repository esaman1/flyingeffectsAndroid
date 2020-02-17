package com.mobile.CloudMovie.utils;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

/**
 * 作者:Administrator on 2019/1/29 16:53
 * 邮箱:jutongzhang@sina.com
 * 内存储存图片
 */
public class LruCacheUtils {
    //申明内存缓存
    private LruCache<String, Bitmap> mLruCache;

    //在构造方法中进行初使化
    public LruCacheUtils() {
        if(mLruCache==null){
            //得到当前应用程序的内存
            int maxMemory = (int) Runtime.getRuntime().maxMemory();
            //内存缓存为当前应用程序的8分之1
            int cacheMemory = maxMemory /3;
            //进行初使化
            mLruCache = new LruCache<String, Bitmap>(cacheMemory) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getByteCount();//自定义bitmap数据大小的计算方式
                }
            };
        }
    }








    /**
     *  * 保存图片到内存缓存
     *  * @param key 图片的url
     *  * @param bitmap 图片
     *  
     */
    public void savePicToMemory(String key, Bitmap bitmap) {
        try {
            mLruCache.put(key, bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  * 通过key值得到缓存的图片
     *  * @param key 图片的url地址
     *  * @return Bitmap 或 null
     *  
     */
    public Bitmap getPicFromMemory(String key) {
        Bitmap bitmap = null;
        try {
            //通过key获取图片
            bitmap = mLruCache.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return bitmap;
    }

    /**
     *  * 清除缓存
     *  * evict:驱逐 逐出
     *  
     */
    public void clearCache() {
        if (mLruCache != null) {
            if (mLruCache.size() > 0) {
                Log.d("CacheUtils",
                        "mMemoryCache.size() " + mLruCache.size());
                mLruCache.evictAll();


                Log.d("CacheUtils", "mMemoryCache.size()" + mLruCache.size());
            }
            mLruCache = null;
        }
    }


}

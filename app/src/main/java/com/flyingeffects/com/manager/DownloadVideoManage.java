package com.flyingeffects.com.manager;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;


/**
 * description ：下载视频到本地
 * creation date: 2020/3/20
 * param :
 * user : zhangtongju
 */
public class DownloadVideoManage {

    private final String TAG = "DownloadVideoManage";
    private downloadSuccess callback;


    public DownloadVideoManage(downloadSuccess callback) {
        this.callback = callback;
    }


    public void DownloadVideo(String path, String fileName) {
        try {
            URL url = new URL(path);
            // 打开连接
            URLConnection conn = url.openConnection();
            // 打开输入流
            InputStream is = conn.getInputStream();
            // 创建字节流
            byte[] bs = new byte[1024];
            int len;
            OutputStream os = new FileOutputStream(fileName);
            // 写数据
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            // 完成后关闭流
            Log.e(TAG, "download-finish");
            os.close();
            is.close();
            callback.isSuccess(true);
            //            }
        } catch (Exception e) {
            callback.isSuccess(false);
            e.printStackTrace();
            Log.e(TAG, "e.getMessage() --- " + e.getMessage());
        }
    }


    public interface downloadSuccess {

        void isSuccess(boolean isSuccess);

    }


}

package com.flyingeffects.com.manager;

import android.content.Context;

import com.flyingeffects.com.utils.LogUtil;

import java.io.File;
import java.util.List;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;



/**
 * description ：使用鲁班压缩图片
 * creation date: 2020/5/14
 * user : zhangtongju
 */
public class CompressImgManage {
    private String mCatchFolder;
    private Context context;
    private compressCallback callback;

    public CompressImgManage(Context context, compressCallback callback) {
        this.context = context;
        this.callback = callback;
        FileManager fileManager = new FileManager();
        mCatchFolder = fileManager.getFileCachePath(context, "runCatch");
    }


    public void toCompressImg(List<String> paths) {
        Luban.with(context)
                .load(paths)
                // 传人要压缩的图片列表
//                    .ignoreBy(100)                                  // 忽略不压缩图片的大小
                .setTargetDir(mCatchFolder)                        // 设置压缩后文件存储位置
                .setCompressListener(new OnCompressListener() { //设置回调
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(File file) {
                        if (file != null) {
                            callback.isSuccess(true,file.getPath());
                        }else{
                            callback.isSuccess(false,"");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.isSuccess(false,"");
                        LogUtil.d("OOM", "onError=" + e.getMessage());
                    }
                }).launch();    //启动压缩
    }


    public interface compressCallback {
        void isSuccess(boolean b, String filePath);
    }


}

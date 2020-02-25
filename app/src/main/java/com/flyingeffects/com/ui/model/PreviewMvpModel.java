package com.flyingeffects.com.ui.model;

import android.content.Context;

import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.ui.interfaces.model.PreviewMvpCallback;

import java.io.File;
import java.util.List;

import rx.subjects.PublishSubject;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;


public class PreviewMvpModel {
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private PreviewMvpCallback callback;
    private Context context;
    private  File mCatchFolder;

    public PreviewMvpModel(Context context, PreviewMvpCallback callback) {
        this.context = context;
        this.callback = callback;
        mCatchFolder = context.getExternalFilesDir("CatchFolder/" );
    }

    public void CompressImg(List<String> paths){
        Luban.with(context)
                .load(paths)                                   // 传人要压缩的图片列表
                .ignoreBy(100)                                  // 忽略不压缩图片的大小
                .setTargetDir(mCatchFolder.getPath())                        // 设置压缩后文件存储位置
                .setCompressListener(new OnCompressListener() { //设置回调
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(File file) {


                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                }).launch();    //启动压缩


    }







}

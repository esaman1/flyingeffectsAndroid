package com.flyingeffects.com.ui.model;

import android.content.Context;
import android.os.Environment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.enity.DownImg;
import com.flyingeffects.com.enity.DownImgDataList;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.ui.interfaces.model.PreviewMvpCallback;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.updateFileUtils;
import com.google.gson.Gson;
import com.shixing.sxve.ui.view.WaitingDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;


public class PreviewMvpModel {
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private PreviewMvpCallback callback;
    private Context context;
    private String mCatchFolder;

    public PreviewMvpModel(Context context, PreviewMvpCallback callback) {
        this.context = context;
        this.callback = callback;
        mCatchFolder = getCachePath();
    }


    private String getCachePath() {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //外部存储可用
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            //外部存储不可用
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }


    public void onDestroy() {
    }

    private int nowCompressSuccessNum;

    public void CompressImg(List<String> paths) {
        int nowChoosePathNum = paths.size();
        nowCompressSuccessNum = 0;
        Luban.with(context)
                .load(paths)                                   // 传人要压缩的图片列表
                .ignoreBy(100)                                  // 忽略不压缩图片的大小
                .setTargetDir(mCatchFolder)                        // 设置压缩后文件存储位置
                .setCompressListener(new OnCompressListener() { //设置回调
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(File file) {
                        nowCompressSuccessNum++;
                        LogUtil.d("OOM", "onSuccess=" + file.getPath());
                        //全部图片压缩完成
                        if (nowCompressSuccessNum == nowChoosePathNum) {
                            allCompressPaths = FileManager.getFilesAllName(file.getParent());
                            upLoad(allCompressPaths);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.d("OOM", "onError=" + e.getMessage());
                    }
                }).launch();    //启动压缩
    }


    private List<String> allCompressPaths = new ArrayList<>();


    private List<String >test111=new ArrayList<>();
    private int downSuccessNum;
    private void downImage(String path) {

        Observable.just(path).map(new Func1<String, File>() {
            @Override
            public File call(String s) {
                File file = null;
                try {
                    file = Glide.with(context)
                            .load(s)
                            .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return file;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread()).subscribe(new Action1<File>() {
            @Override
            public void call(File file) {
                downSuccessNum++;
                test111.add(file.getPath());
                if(test111.size()==listForMatting.size()){
                        callback.getCompressImgList(test111);
                }else{
                    downImage(listForMatting.get(downSuccessNum));
                }
            }
        });

    }



    private ArrayList<String>listForMatting=new ArrayList<>();
    private void upLoad(List<String> list) {
        listForMatting.clear();
        List<File>listFile=new ArrayList<>();
        for (String str:list
             ) {
            File file=new File(str);
            listFile.add(file);
        }

        int pathNum=list.size();
        LogUtil.d("OOM","pathNum="+pathNum);
        updateFileUtils.uploadFile(listFile,"http://flying.nineton.cn/api/picture/picturesHumanList?filenum="+pathNum, new updateFileUtils.HttpCallbackListener() {
            @Override
            public void onFinish(int code, String str) {
                WaitingDialog.closePragressDialog();
                Gson gson=new Gson();
                DownImg downIng=   gson.fromJson(str, DownImg.class);
                ArrayList<DownImgDataList> data=downIng.getData();
                for (DownImgDataList item:data
                     ) {
                    listForMatting.add(item.getTarget_url());
                }
                test111.clear();
                downSuccessNum=0;
                downImage(listForMatting.get(0));
            }
        });
    }
}

package com.flyingeffects.com.ui.model;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.ui.interfaces.model.PreviewMvpCallback;
import com.flyingeffects.com.utils.LogUtil;
import com.othershe.dutil.DUtil;
import com.othershe.dutil.callback.SimpleUploadCallback;
import com.shixing.sxve.ui.view.WaitingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
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
//        DataCleanManager.cleanExternalCache();
    }

    private int nowCompressSuccessNum;

    public void CompressImg(List<String> paths) {
        WaitingDialog.openPragressDialog(context);
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
                        if (nowCompressSuccessNum == nowChoosePathNum) {
                            List<String> list = FileManager.getFilesAllName(file.getParent());
                            updateImagePath(list);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.d("OOM", "onError=" + e.getMessage());
                    }
                }).launch();    //启动压缩
    }

    private List<String> tailorList = new ArrayList<>();
    private void updateImagePath(List<String> paths) {
        tailorList.clear();








//        ExecutorService executorService = Executors.newFixedThreadPool(1); //1个线程池
//        for (String path : paths
//        ) {
//            Runnable syncRunnable = new Runnable() {
//                @Override
//                public void run() {
//                    Observable.just(path).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
//                        @Override
//                        public void call(String s) {
//                            uploadImage(new sectionalDrawing() {
//                                @Override
//                                public void isSuccess(String path) {
//                                    tailorList.add(path);
//                                    if (tailorList.size() == paths.size()) {
//                                        WaitingDialog.closePragressDialog();
//                                        callback.getCompressImgList(tailorList);
//                                    }
//                                }
//
//                                @Override
//                                public void isFail(String e) {
//                                    LogUtil.d("OOM","isFail="+e);
//                                    Toast.makeText(context, e, Toast.LENGTH_SHORT).show();
//                                }
//                            }, s);
//                        }
//                    });
//                }
//            };
//            executorService.execute(syncRunnable);
//        }



    }

    private void uploadImage(sectionalDrawing calback, String path) {
        DUtil.initFormUpload()
                .url("http://flying.nineton.cn/api/picture/pictureHuman")
                .addFile("file", "BeautyImage.jpg", new File(path))
                .fileUploadBuild()
                .upload(new SimpleUploadCallback() {
                    @Override
                    public void onStart() {
                        LogUtil.d("uploadImage", "onStart");
                        super.onStart();
                    }

                    @Override
                    public void onFinish(String response) {
                        LogUtil.d("uploadImage", response);
                        try {
                            JSONObject ob = new JSONObject(response);
                            int code = ob.getInt("code");
                            if (code == 1) {
                                JSONObject data = ob.getJSONObject("data");
                                calback.isSuccess(data.getString("target_url"));
                            } else {
                                String message = ob.getString("msg");
                                calback.isFail(message);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        super.onFinish(response);
                    }
                });

    }

    interface sectionalDrawing {

        void isSuccess(String path);

        void isFail(String e);
    }


}

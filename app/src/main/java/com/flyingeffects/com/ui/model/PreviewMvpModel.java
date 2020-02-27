package com.flyingeffects.com.ui.model;

import android.content.Context;
import android.os.Environment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.ui.interfaces.model.PreviewMvpCallback;
import com.flyingeffects.com.utils.FileUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.updateFileUtils;
import com.othershe.dutil.DUtil;
import com.othershe.dutil.callback.SimpleUploadCallback;
import com.shixing.sxve.ui.view.WaitingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
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




    private void downImage(String path) {
        Observable.just(path).map(new Func1<String, File>() {
            @Override
            public File call(String s) {
                File file = null;
                try {
                    file = Glide.with(context)
                            .load(path)
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
                try {
                    String newFilePath = getFilesPath(context);
                    FileUtil.copyFile(file, newFilePath);
                    boolean isDeleteSuccess = file.delete();
                    LogUtil.d("oom", "isDelectedSuccess=" + isDeleteSuccess);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    public String getFilesPath(Context context) {
        String filePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //外部存储可用
            File mattingPath = context.getExternalFilesDir("dynamic/" + "matting");
            if (mattingPath != null && !mattingPath.exists()) {
                mattingPath.exists();
            }
            return mattingPath.getPath();
        } else {
            //外部存储不可用
            filePath = context.getFilesDir().getPath();
        }
        return filePath;
    }


    private List<String> tailorList = new ArrayList<>();
    private int nowChoosePosition = 0;

    private void updateImagePath(List<String> paths) {
        tailorList.clear();
        uploadImage(new sectionalDrawing() {
            @Override
            public void isSuccess(String path) {
                tailorList.add(path);
//                if (tailorList.size() == paths.size()) {
//                    callback.getCompressImgList(tailorList);
//                }
            }

            @Override
            public void isFail(String e) {
            }
        }, paths.get(0), paths.get(1));
    }

    private void uploadImage(sectionalDrawing callback, String path, String path2) {
        DUtil.initFormUpload()
                .url("http://flying.nineton.cn/api/picture/picturesHumanList?filenum=3")
                .addFile("file", path, new File(path))
                .addFile("file", path2, new File(path2))
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
                                callback.isSuccess(data.getString("target_url"));
                            } else {
                                String message = ob.getString("msg");
                                callback.isFail(message);
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

    private void upLoad(List<String> list) {
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
                LogUtil.d("OOM","code="+code+"String="+str);
            }
        });



    }


}

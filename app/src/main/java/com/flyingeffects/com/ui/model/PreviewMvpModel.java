package com.flyingeffects.com.ui.model;

import android.content.Context;
import android.text.TextUtils;

import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.DownImg;
import com.flyingeffects.com.enity.DownImgDataList;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.DownImageManager;
import com.flyingeffects.com.manager.DownloadZipManager;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.manager.ZipFileHelperManager;
import com.flyingeffects.com.ui.interfaces.model.PreviewMvpCallback;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.NetworkUtils;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.utils.updateFileUtils;
import com.google.gson.Gson;
import com.shixing.sxve.ui.view.WaitingDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;


public class PreviewMvpModel {
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private PreviewMvpCallback callback;
    private Context context;
    private String mCatchFolder;
    private String mTailtoFolder;
    /**
     * 原图片地址
     */
    private List<String> localImagePaths;
    FileManager fileManager;

    public PreviewMvpModel(Context context, PreviewMvpCallback callback) {
        this.context = context;
        this.callback = callback;
        fileManager=new FileManager();
        mCatchFolder = fileManager.getCachePath(context);
        mTailtoFolder= fileManager.getFileCachePath(context,"tailor");
    }




    public void onDestroy() {
    }

    private int nowCompressSuccessNum;

    public void CompressImgAndCache(List<String> paths) {
        //todo 暂时只针对一张图片的时候
        if(paths!=null&&paths.size()==1){
            String localCacheName=paths.get(0);
            localCacheName= fileManager.getFileNameWithSuffix(localCacheName);
            File file=new File(mTailtoFolder+"/"+localCacheName);
            if(file.exists()){
                List<String>list=new ArrayList<>();list.add(file.getPath());
                callback.getCompressImgList(list);
                return;
            }
        }


        //正常压缩下载逻辑
        toCompressImg(paths);

    }


    private void toCompressImg(List<String> paths){
        if(paths!=null){
            localImagePaths=paths;
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
    }


    public void requestTemplateDetail(String templateId) {

        HashMap<String, String> params = new HashMap<>();
        params.put("template_id", templateId);
        // 启动时间
        Observable ob = Api.getDefault().templateLInfo(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<new_fag_template_item>(context) {
            @Override
            protected void _onError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(new_fag_template_item data) {

                callback.getTemplateLInfo(data);

            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);

    }


    private List<String> allCompressPaths = new ArrayList<>();


    private ArrayList<String> listForMatting = new ArrayList<>();

    private void upLoad(List<String> list) {
        WaitingDialog.openPragressDialog(context);
//        WaitingDialog.setDialogText("正在抠图中\n" +
//                "上传人物最佳~");
        listForMatting.clear();
        List<File> listFile = new ArrayList<>();
        for (String str : list
        ) {
            File file = new File(str);
            listFile.add(file);
        }

        int pathNum = list.size();
        LogUtil.d("OOM", "pathNum=" + pathNum);
        updateFileUtils.uploadFile(listFile, "http://flying.nineton.cn/api/picture/picturesHumanList?filenum=" + pathNum, new updateFileUtils.HttpCallbackListener() {
            @Override
            public void onFinish(int code, String str) {
                LogUtil.d("OOM", "uploadFileCallBack=" + str);
                WaitingDialog.closePragressDialog();
                Gson gson = new Gson();
                DownImg downIng = gson.fromJson(str, DownImg.class);
                ArrayList<DownImgDataList> data = downIng.getData();
                for (DownImgDataList item : data
                ) {
                    listForMatting.add(item.getTarget_url());
                }

                //马卡龙，这里是图片链接，下载下来的方式
                if (data.get(0).getType() == 1) {
                    DownImageManager downImageManager = new DownImageManager(BaseApplication.getInstance(), listForMatting, path -> {
                        callback.getCompressImgList(path);
                        keepTailorImageToCache(path);
                    });
                    downImageManager.downImage(listForMatting.get(0));
                } else {
                    //百度，face++ 是直接下载的图片编码
                    DownImageManager downImageManager = new DownImageManager(BaseApplication.getInstance(), listForMatting, path -> {
                        callback.getCompressImgList(path);
                        keepTailorImageToCache(path);
                    });
                    downImageManager.downImageForByte(listForMatting.get(0));
                }

            }
        });
    }

    private void keepTailorImageToCache(List<String> paths) {
        String localCacheName=localImagePaths.get(0);
        if(paths.size()==1){
            File file=new File(paths.get(0));
            FileManager manager=new FileManager();
            localCacheName= manager.getFileNameWithSuffix(localCacheName);
            if(mTailtoFolder!=null){
                File   mTailto=new File(mTailtoFolder,localCacheName);
                manager.mCopyFile(file,mTailto);
            }


        }

    }


    public void collectTemplate(String templateId) {
        HashMap<String, String> params = new HashMap<>();
        params.put("template_id", templateId);
        params.put("token", BaseConstans.GetUserToken());
        // 启动时间
        Observable ob = Api.getDefault().newCollection(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(context) {
            @Override
            protected void _onError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(Object data) {
                String str = StringUtil.beanToJSONString(data);
                LogUtil.d("OOM", "collectTemplate=" + str);
                callback.collectionResult();

            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);

    }


    public void prepareDownZip(String url, String zipPid) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            readyDown(zipPid, url);
        } else {
            ToastUtil.showToast("网络连接失败！");
        }
    }


    private File mFolder;
    private int mProgress;
    private boolean isDownZipUrl = false;

    private void readyDown(String zipPid, String downZipUrl) {
        LogUtil.d("onVideoAdError", "getPermission");
        mFolder = context.getExternalFilesDir("dynamic/" + zipPid);
        if (mFolder != null) {
            String folderPath = mFolder.getParent();
            if (!isDownZipUrl) {
                if (mFolder == null || mFolder.list().length == 0) {
                    LogUtil.d("searchActivity", "开始下载");
                    downZip(downZipUrl, folderPath);
                    mProgress = 0;
                    showMakeProgress();
                } else {
                    intoTemplateActivity(mFolder.getPath());
                }
            } else {
                ToastUtil.showToast("下载中，请稍后再试");
            }
        } else {
            ToastUtil.showToast("没找到sd卡");
        }
    }


    private void downZip(String loadUrl, String path) {
        mProgress = 0;
        if (!TextUtils.isEmpty(loadUrl)) {
            new Thread() {
                @Override
                public void run() {
                    isDownZipUrl = true;
                    try {
                        DownloadZipManager.getInstance().getFileFromServer(loadUrl, path, (progress, isSucceed, zipPath) -> {
                            if (!isSucceed) {
                                LogUtil.d("onVideoAdError", "progress=" + progress);
                                mProgress = progress;
                                showMakeProgress();
                            } else {
                                WaitingDialog.closePragressDialog();
                                LogUtil.d("onVideoAdError", "下载完成");
                                isDownZipUrl = false;
                                //可以制作了，先解压
                                File file = new File(zipPath);
                                try {
                                    ZipFileHelperManager.upZipFile(file, path, path1 -> {
                                        if (file.exists()) { //删除压缩包
                                            file.delete();
                                        }
//                                        videoPause();
                                        mProgress = 100;
                                        showMakeProgress();
                                        intoTemplateActivity(path1);
                                    });
                                } catch (IOException e) {
                                    LogUtil.d("onVideoAdError", "Exception=" + e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        });

                    } catch (Exception e) {
                        LogUtil.d("onVideoAdError", "Exception=" + e.getMessage());
                    }
                    super.run();
                }
            }.start();
        } else {
            ToastUtil.showToast("没有zip地址");
        }
    }


    private void intoTemplateActivity(String filePath) {
        callback.getTemplateFileSuccess(filePath);

    }


    private void showMakeProgress() {
        callback.showDownProgress(mProgress);
    }


}

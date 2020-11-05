package com.flyingeffects.com.commonlyModel;


import android.os.Handler;
import android.text.TextUtils;

import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.manager.DownloadZipManager;
import com.flyingeffects.com.manager.ZipFileHelperManager;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.NetworkUtils;
import com.flyingeffects.com.utils.ToastUtil;

import java.io.File;
import java.io.IOException;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * description ：模板下载模块化提出来
 * creation date: 2020/11/5
 * user : zhangtongju
 */

public class TemplateDown {

    private final DownFileCallback callback;


    public TemplateDown(DownFileCallback callback) {
        this.callback = callback;
    }


    public void prepareDownZip(String url, String zipPid) {
        if (NetworkUtils.isNetworkAvailable(BaseApplication.getInstance())) {
            readyDown(zipPid, url);
        } else {
            ToastUtil.showToast("网络连接失败！");
        }
    }


    private int mProgress;
    private boolean isDownZipUrl = false;

    private void readyDown(String zipPid, String downZipUrl) {
        LogUtil.d("onVideoAdError", "getPermission");
        File  mFolder = BaseApplication.getInstance().getExternalFilesDir("dynamic/" + zipPid);
        if (mFolder != null) {
            String folderPath = mFolder.getParent();
            if (!isDownZipUrl) {
                if (mFolder == null || mFolder.list().length == 0) {
                    LogUtil.d("searchActivity", "开始下载");
                    downZip(downZipUrl, folderPath);
                    mProgress = 0;
                    showMakeProgress();
                } else {
                    DownDone(mFolder.getPath());
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
        callback.showDownProgress(mProgress);
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
                                showMakeProgress();
                                LogUtil.d("onVideoAdError", "下载完成");
                                isDownZipUrl = false;
                                //可以制作了，先解压
                                File file = new File(zipPath);
                                try {
                                    ZipFileHelperManager.upZipFile(file, path, path1 -> {
                                        if (file.exists()) { //删除压缩包
                                            file.delete();
                                        }
                                        mProgress = 100;
                                        showMakeProgress();
                                        DownDone(path1);
                                    });
                                } catch (IOException e) {
                                    LogUtil.d("onVideoAdError", "Exception=" + e.getMessage());
                                    e.printStackTrace();

                                }
                            }
                        });

                    } catch (Exception e) {
                        isDownZipUrl = false;
                        Observable.just(e).subscribeOn(AndroidSchedulers.mainThread()).subscribe(e1 -> new Handler().post(() -> ToastUtil.showToast("下载异常，请重试")));
                        LogUtil.d("onVideoAdError", "Exception=" + e.getMessage());
                        callback.showDownProgress(100);
                    }
                    super.run();
                }
            }.start();
        } else {
            ToastUtil.showToast("没有zip地址");
        }
    }



    private void showMakeProgress() {
        if(callback!=null){
            callback.showDownProgress(mProgress);
        }
    }


    private void DownDone(String path){
        if(callback!=null){
            callback.isSuccess(path);
        }
    }


    public interface DownFileCallback {
        void isSuccess(String filePath);

        void showDownProgress(int progress);
    }


}

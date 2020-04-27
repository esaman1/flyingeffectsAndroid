package com.flyingeffects.com.ui.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.text.TextUtils;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.UserInfo;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.BitmapManager;
import com.flyingeffects.com.manager.CompressionCuttingManage;
import com.flyingeffects.com.manager.DownloadVideoManage;
import com.flyingeffects.com.manager.DownloadZipManager;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.manager.ZipFileHelperManager;
import com.flyingeffects.com.ui.interfaces.model.PreviewMvpCallback;
import com.flyingeffects.com.ui.view.activity.TemplateActivity;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.NetworkUtils;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.utils.faceUtil.ConUtil;
import com.glidebitmappool.GlideBitmapPool;
import com.glidebitmappool.internal.BitmapPool;
import com.megvii.segjni.SegJni;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;


public class PreviewMvpModel {
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private PreviewMvpCallback callback;
    private Context context;
    private String mVideoFolder;
    private FileManager fileManager;

    public PreviewMvpModel(Context context, PreviewMvpCallback callback) {
        this.context = context;
        this.callback = callback;
        fileManager = new FileManager();
        mVideoFolder = fileManager.getFileCachePath(context, "downVideo");
    }


    public void onDestroy() {
    }


    public void DownVideo(String path, String imagePath, String id) {
        String videoName = mVideoFolder + File.separator + id + "synthetic.mp4";
        File File = new File(videoName);
        if (File.exists()) {
            callback.downVideoSuccess(videoName, imagePath);
            return;
        }

        Observable.just(path).subscribeOn(Schedulers.io()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                DownloadVideoManage manage = new DownloadVideoManage(new DownloadVideoManage.downloadSuccess() {
                    @Override
                    public void isSuccess(boolean isSuccess) {
                        callback.downVideoSuccess(videoName, imagePath);
                    }
                });
                manage.DownloadVideo(path, videoName);
            }
        });

    }


    /**
     * description ：得到视频的封面
     * creation date: 2020/4/20
     * user : zhangtongju
     */
    public void GetVideoCover(String originalPath, String videoPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(originalPath);
        Bitmap mBitmap = retriever.getFrameAtTime(0);
        String fileName = mVideoFolder + File.separator + UUID.randomUUID() + ".png";
        BitmapManager.getInstance().saveBitmapToPath(mBitmap, fileName, isSuccess -> {
            CompressionCuttingManage manage = new CompressionCuttingManage(context, "", false, tailorPaths -> {
                callback.getVideoCover(tailorPaths.get(0),originalPath,videoPath);
            });
            List mattingPath=new ArrayList();
            mattingPath.add(fileName);
            manage.ToMatting(mattingPath);
            GlideBitmapPool.putBitmap(mBitmap);
        });
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


    public void requestUserInfo() {
        HashMap<String, String> params = new HashMap<>();
        params.put("token", BaseConstans.GetUserToken());
        // 启动时间
        Observable ob = Api.getDefault().getUserInfo(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<UserInfo>(context) {
            @Override
            protected void _onError(String message) {
                BaseConstans.SetUserToken("");
                callback.hasLogin(false);
            }

            @Override
            protected void _onNext(UserInfo data) {
                callback.hasLogin(true);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }


    /**
     * description ：
     * creation date: 2020/3/24
     * param : template_type 1 muban  2背景
     * user : zhangtongju
     */
    public void collectTemplate(String templateId, String title, String template_type) {
        HashMap<String, String> params = new HashMap<>();
        params.put("template_id", templateId);
        params.put("token", BaseConstans.GetUserToken());
        params.put("template_type", template_type);
        // 启动时间
        Observable ob = Api.getDefault().newCollection(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(context) {
            @Override
            protected void _onError(String message) {
//                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(Object data) {
                String str = StringUtil.beanToJSONString(data);
                LogUtil.d("OOM", "collectTemplate=" + str);
                callback.collectionResult();

            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);

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
                        isDownZipUrl = false;
                        Observable.just(e).subscribeOn(AndroidSchedulers.mainThread()).subscribe(e1 -> new Handler().post(() -> ToastUtil.showToast("下载异常，请重试")));
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

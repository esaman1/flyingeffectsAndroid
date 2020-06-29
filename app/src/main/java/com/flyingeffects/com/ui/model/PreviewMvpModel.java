package com.flyingeffects.com.ui.model;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.commonlyModel.SaveAlbumPathModel;
import com.flyingeffects.com.commonlyModel.getVideoInfo;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.UserInfo;
import com.flyingeffects.com.enity.VideoInfo;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.AdConfigs;
import com.flyingeffects.com.manager.AdManager;
import com.flyingeffects.com.manager.BitmapManager;
import com.flyingeffects.com.manager.CompressionCuttingManage;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.DownloadVideoManage;
import com.flyingeffects.com.manager.DownloadZipManager;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.manager.ZipFileHelperManager;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.model.PreviewMvpCallback;
import com.flyingeffects.com.ui.view.activity.AdHintActivity;
import com.flyingeffects.com.ui.view.activity.PreviewActivity;
import com.flyingeffects.com.utils.FileUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.NetworkUtils;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.glidebitmappool.GlideBitmapPool;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.shixing.sxve.ui.view.WaitingDialog;
import com.shixing.sxve.ui.view.WaitingDialog_progress;

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


    public void DownVideo(String path, String imagePath, String id,boolean keepAlbum) {

        String videoName = mVideoFolder + File.separator + id + "synthetic.mp4";
        File File = new File(videoName);
        if (File.exists()) {
            if(downProgressDialog!=null){
                downProgressDialog.closePragressDialog();
            }
            if(!keepAlbum){
                callback.downVideoSuccess(videoName, imagePath);
            }else{
                WaitingDialog.closePragressDialog();
                saveToAlbum(videoName);
                if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
                    AdManager.getInstance().showCpAd(context, AdConfigs.AD_SCREEN_FOR_DOWNLOAD);
                }
            }
            return;
        }
        Observable.just(path).subscribeOn(Schedulers.io()).subscribe(s -> {
            DownloadVideoManage manage = new DownloadVideoManage(isSuccess -> Observable.just(videoName).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
                @Override
                public void call(String s1) {
                    VideoInfo info= getVideoInfo.getInstance().getRingDuring(s1);
                    videoCutDurationForVideoOneDo.getInstance().CutVideoForDrawPadAllExecute2(context, info.getDuration(),videoName ,0, new videoCutDurationForVideoOneDo.isSuccess() {
                        @Override
                        public void progresss(int progress) {
                            LogUtil.d("oom","下载时候后重新裁剪进度为="+progress);
                            if(downProgressDialog!=null){
                                downProgressDialog.setProgress("下载进度为"+progress+"%");
                            }
                        }

                        @Override
                        public void isSuccess(boolean isSuccess, String path1) {
                            if(downProgressDialog!=null){
                                downProgressDialog.closePragressDialog();
                            }
                            if(!keepAlbum){
                                callback.downVideoSuccess(path1, imagePath);

                            }else{

                                WaitingDialog.closePragressDialog();
                                saveToAlbum(path1);
                                if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
                                    AdManager.getInstance().showCpAd(context, AdConfigs.AD_SCREEN_FOR_DOWNLOAD);
                                }

                            }

                        }
                    });
                }
            }));
            manage.DownloadVideo(path, videoName);
        });

    }




        private void saveToAlbum(String path) {
            String albumPath = SaveAlbumPathModel.getInstance().getKeepOutput();
            try {
                FileUtil.copyFile(new File(path), albumPath);
                albumBroadcast(albumPath);
                showKeepSuccessDialog(albumPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    /**
     * description ：通知相册更新
     * date: ：2019/8/16 14:24
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    private void albumBroadcast(String outputFile) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(new File(outputFile)));
        context.sendBroadcast(intent);
    }


    private void showKeepSuccessDialog(String path) {
        if (!DoubleClick.getInstance().isFastDoubleClick()) {
            AlertDialog.Builder builder = new AlertDialog.Builder( //去除黑边
                    new ContextThemeWrapper(context, R.style.Theme_Transparent));
            builder.setTitle(R.string.notification);
            builder.setMessage("已为你保存到相册,多多分享给友友\n" + "【" + path + context.getString(R.string.folder) + "】"
            );
            builder.setNegativeButton(context.getString(R.string.got_it), (dialog, which) -> dialog.dismiss());
            builder.setCancelable(true);
            Dialog dialog = builder.show();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    private WaitingDialog_progress downProgressDialog;
    private BottomSheetDialog   bottomSheetDialog;
    public void showBottomSheetDialog(String path,String imagePath,String id){
        bottomSheetDialog  = new BottomSheetDialog(context, R.style.gaussianDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.preview_bottom_sheet_dialog, null);
        bottomSheetDialog.setContentView(view);
       LinearLayout iv_download = view.findViewById(R.id.ll_download);
        iv_download.setOnClickListener(view12 -> {
            statisticsEventAffair.getInstance().setFlag(context, "save_back_template");
            downProgressDialog=new WaitingDialog_progress(context);
            downProgressDialog.openProgressDialog();
            DownVideo(path,imagePath,id,true);
            dismissDialog();
        });

        TextView tv_cancle=view.findViewById(R.id.tv_cancle);
        tv_cancle.setOnClickListener(view1 -> bottomSheetDialog.dismiss());
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        View parent = (View) view.getParent();     //处理高度显示完全  https://www.jianshu.com/p/38af0cf77352
        parent.setBackgroundResource(android.R.color.transparent);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
        view.measure(0, 0);
        behavior.setPeekHeight(view.getMeasuredHeight());
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) parent.getLayoutParams();
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        parent.setLayoutParams(params);
        bottomSheetDialog.show();
    }


    private void dismissDialog() {
        try {
            if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
                bottomSheetDialog.dismiss();
                bottomSheetDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
//                ToastUtil.showToast(message);
                LogUtil.d("OOM","requestTemplateDetail-error="+message);
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
                ToastUtil.showToast(message);
//                LogUtil.d("");
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

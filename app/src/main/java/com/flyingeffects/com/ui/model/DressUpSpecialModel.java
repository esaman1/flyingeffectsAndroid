package com.flyingeffects.com.ui.model;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.commonlyModel.GetPathType;
import com.flyingeffects.com.commonlyModel.getVideoInfo;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.DressUpSpecial;
import com.flyingeffects.com.enity.VideoInfo;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.BitmapManager;
import com.flyingeffects.com.manager.DownloadVideoManage;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.manager.huaweiObs;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.google.gson.Gson;
import com.shixing.sxve.ui.albumType;
import com.shixing.sxve.ui.view.WaitingDialog_progress;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import top.zibin.luban.Luban;


/**
 * description ：换装-闪图 特殊功能封装
 * 压缩-上传-合成-返回
 * creation date: 2020/12/8
 * user : zhangtongju
 */
public class DressUpSpecialModel {

    private final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private final Context context;
    private final DressUpCallback callback;
    private final String mCatchFolder;
    private final String mVideoFolder;

    public DressUpSpecialModel(Context context, DressUpCallback callback) {
        this.context = context;
        this.callback = callback;
        FileManager fileManager = new FileManager();
        mCatchFolder = fileManager.getFileCachePath(context, "runCatch");
        mVideoFolder = fileManager.getFileCachePath(context, "downVideo");
    }


    /**
     * description ：换装
     * creation date: 2020/12/3
     * user : zhangtongju
     */
    private WaitingDialog_progress progress;
    private int api_type;

    public void toDressUp(List<String> paths, int api_type) {
        this.api_type = api_type;
        new Handler().postDelayed(() -> {
            progress = new WaitingDialog_progress(context);
            progress.openProgressDialog("正在加载中...");
            new Thread(() -> toCompressImg(paths)).start();
        }, 200);
    }


    /**
     * description ：上传换装图片到华为云
     * creation date: 2020/12/4
     * user : zhangtongju
     */
    private List<String> uploadPathList = new ArrayList<>();
    private int nowUpdatePosition;
    private final List<String> huaweiPathList = new ArrayList<>();

    private void uploadFileToHuawei(String path) {
        String type = path.substring(path.length() - 4);
        String nowTime = StringUtil.getCurrentTimeymd();
        String copyName = "media/android/Gif/" + nowTime + "/" + System.currentTimeMillis() + type;
        String uploadPath = "http://cdn.flying.flyingeffect.com/" + copyName;
        Log.d("OOM3", "uploadFileToHuawei" + "当前上传的地址为" + path + "当前的名字为" + copyName);
        huaweiObs.getInstance().uploadFileToHawei(path, copyName, str -> Observable.just(str).subscribeOn(AndroidSchedulers.mainThread()).subscribe(s -> new Thread(() -> {
            nowUpdatePosition++;
            huaweiPathList.add(uploadPath);
            if (nowUpdatePosition < uploadPathList.size()) {
                LogUtil.d("OOM3", "继续上传");
                uploadFileToHuawei(uploadPathList.get(nowUpdatePosition));
            } else {
                //全部上传华为完成，开始请求结果
                LogUtil.d("OOM3", "全部上传完成");
                informServers();
            }
        }).start()));
    }


    /**
     * description ：通知后台,告诉后台已经上传了图片了
     * creation date: 2020/12/4
     * user : zhangtongju
     */
    private void informServers() {
        LogUtil.d("OOM3", "告诉后台已经上传了图片了");
        Gson gson = new Gson();
        String image = gson.toJson(huaweiPathList);
        HashMap<String, String> params = new HashMap<>();
        params.put("type", api_type + "");
        params.put("image", image);
        LogUtil.d("OOM3", "params=" + params.toString());
        Observable ob = Api.getDefault().ApiTest(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<DressUpSpecial>(context) {
            @Override
            protected void onSubError(String message) {
                LogUtil.d("OOM3", "_onError=" + message);
                ToastUtil.showToast(message);
                progress.closeProgressDialog();
            }

            @Override
            protected void onSubNext(DressUpSpecial object) {
                LogUtil.d("OOM3", "通知后台完成=" + StringUtil.beanToJSONString(object));
                keepUrlToLocal(object.getUrl());
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }


    /**
     * description ：
     * creation date: 2021/4/19
     * user : zhangtongju
     */
    private void keepUrlToLocal(String url) {
        if (!TextUtils.isEmpty(url)) {
            if (url.contains("mp4")) {
                LogUtil.d("OOM3", "下载视频=");
                downVideo(url);
            } else {
                LogUtil.d("OOM3", "下载图片=");
                downImage(url);
            }
        }
    }


    public void downVideo(String path) {
        String videoName = mVideoFolder + File.separator + "synthetic.mp4";
        File file = new File(videoName);
        if (file.exists()) {
            boolean isDelete = file.delete();
            LogUtil.d("OOM3", "isDelete=" + isDelete);
        }
        Observable.just(path).subscribeOn(Schedulers.io()).subscribe(s -> {
            DownloadVideoManage manage = new DownloadVideoManage(isSuccess -> Observable.just(videoName).subscribeOn(AndroidSchedulers.mainThread()).subscribe(s1 -> {
                VideoInfo info = getVideoInfo.getInstance().getRingDuring(s1);
                videoCutDurationForVideoOneDo.getInstance().cutVideoForDrawPadAllExecute2(context, false, info.getDuration(), videoName, 0, new videoCutDurationForVideoOneDo.isSuccess() {
                    @Override
                    public void progresss(int progress) {
                        LogUtil.d("OOM3", "下载的进度为" + progress);
                    }

                    @Override
                    public void isSuccess(boolean isSuccess1, String path1) {
                        progress.closeProgressDialog();
                        if (isSuccess1) {
                            callback.isSuccess(path1);
                        }
                    }
                });
            }));
            manage.downloadVideo(path, videoName);
        });
    }


    public interface DressUpCallback {
        void isSuccess(String url);
    }


    public void toCompressImg(List<String> paths) {


        if (
                albumType.isVideo(GetPathType.getInstance().
                        getMediaType(paths.get(0)))) {
            LogUtil.d("OOM3", "上传为视频");

            uploadPathList.add(paths.get(0));
            new Thread(() -> uploadFileToHuawei(paths.get(0))).start();

        }else{
            LogUtil.d("OOM3", "上传为图片");
            Observable.just(paths).map(strings -> {
                try {
                    return Luban.with(context).load(strings).get();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(files -> {
                huaweiPathList.clear();
                if (files != null && files.size() > 0) {
                    uploadPathList = getUploadPath(files);
                    LogUtil.d("OOM3", "luban成功压缩");
                } else {
                    uploadPathList = paths;
                    LogUtil.d("OOM3", "luban压缩失败，用原图地址");
                }
                new Thread(() -> uploadFileToHuawei(uploadPathList.get(0))).start();

            });
        }


    }


    /**
     * description ：转换文件为地址路径
     * creation date: 2021/4/19
     * user : zhangtongju
     */
    private List<String> getUploadPath(List<File> paths) {
        ArrayList<String> needUploadPath = new ArrayList<>();
        for (int i = 0; i < paths.size(); i++) {
            String path = paths.get(i).getPath();
            needUploadPath.add(path);
        }
        return needUploadPath;
    }


    /**
     * description ：下载返回来的图片地址
     * creation date: 2021/4/19
     * user : zhangtongju
     */
    private void downImage(String url) {
        Observable.just(url).map(needImagePath -> BitmapManager.getInstance().GetBitmapForHttp(needImagePath)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(bitmap -> {
            LogUtil.d("OOM3", "整合bitmap");
            String fileName = mCatchFolder + File.separator + UUID.randomUUID() + ".png";
            progress.closeProgressDialog();
            BitmapManager.getInstance().saveBitmapToPath(bitmap, fileName, isSuccess -> callback.isSuccess(fileName));
        });
    }


}

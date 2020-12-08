package com.flyingeffects.com.ui.model;

import android.content.Context;
import android.util.Log;

import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.HumanMerageResult;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.Calculagraph;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.manager.huaweiObs;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.shixing.sxve.ui.view.WaitingDialog_progress;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;


/**
 * description ：换装 功能封装
 * creation date: 2020/12/8
 * user : zhangtongju
 */
public class DressUpModel {

    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private Context context;
    private String mUploadDressUpFolder;
    private String mCatchFolder;
    private DressUpCallback callback;

    public DressUpModel(Context context, DressUpCallback callback) {
        this.callback = callback;
        this.context = context;
        FileManager fileManager = new FileManager();
        mCatchFolder = fileManager.getFileCachePath(context, "runCatch");
        mUploadDressUpFolder = fileManager.getFileCachePath(context, "DressUpFolder");
    }


    /**
     * description ：换装
     * creation date: 2020/12/3
     * user : zhangtongju
     */
    WaitingDialog_progress progress;

    public void toDressUp(String ImagePath, String templateId) {
        progress = new WaitingDialog_progress(context);
        progress.openProgressDialog();
        toCompressImg(ImagePath,templateId);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Bitmap bpResoruse = BitmapFactory.decodeFile(ImagePath);
//                Bitmap bp = BitmapUtils.compressBitmap(bpResoruse, 500);
//                String fileName = mUploadDressUpFolder + File.separator + UUID.randomUUID() + ".png";
//                LogUtil.d("OOM3", "fileName=" + fileName);
//                BitmapManager.getInstance().saveBitmapToPath(bp, fileName, new BitmapManager.saveToFileCallback() {
//                    @Override
//                    public void isSuccess(boolean isSuccess) {
//                        uploadFileToHuawei(fileName, templateId);
//                    }
//                });
//            }
//        }).start();
    }


    /**
     * description ：上传换装图片到华为云
     * creation date: 2020/12/4
     * user : zhangtongju
     */
    private void uploadFileToHuawei(String path, String template_id) {
        String type = path.substring(path.length() - 4);
        String nowTime = StringUtil.getCurrentTimeymd();
        String copyName = "media/android/dressUp/" + nowTime + "/" + System.currentTimeMillis() + type;
        String uploadPath = "http://cdn.flying.flyingeffect.com/" + copyName;
        Log.d("OOM3", "uploadFileToHuawei" + "当前上传的地址为" + path + "当前的名字为" + copyName);
        huaweiObs.getInstance().uploadFileToHawei(path, copyName, str -> Observable.just(str).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                LogUtil.d("OOM3", "上传华为云成功,地址为" + s);
                informServers(uploadPath, template_id);
                File file=new File(path);
                if(file.exists()){
                    file.delete();
                }
            }
        }));
    }


    /**
     * description ：通知后台,告诉后台已经上传了图片了
     * creation date: 2020/12/4
     * user : zhangtongju
     */
    private void informServers(String path, String template_id) {
        HashMap<String, String> params = new HashMap<>();
        params.put("image", path);
        params.put("template_id", template_id);
        Observable ob = Api.getDefault().meargeHuman(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<String>(context) {
            @Override
            protected void _onError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(String id) {
                LogUtil.d("OOM3", "informServers");
                startTimer(id);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }


    /***
     * 开启轮训
     */
    private Calculagraph calculagraph;

    private void startTimer(String id) {
        calculagraph = new Calculagraph();
        calculagraph.startTimer(3f, 5, new Calculagraph.Callback() {
            @Override
            public void isTimeUp() {
                LogUtil.d("OOM3", "开始请求融合结果");
                Observable.just(id).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        requestDressUpCallback(s);
                    }
                });
            }

            @Override
            public void isDone() {
                progress.closePragressDialog();
            }
        });
    }


    /**
     * description ：通知后台,请求换装接口
     * creation date: 2020/12/4
     * user : zhangtongju
     */
    private void requestDressUpCallback(String request_id) {
        HashMap<String, String> params = new HashMap<>();
        params.put("request_id", request_id);
        Observable ob = Api.getDefault().humanMerageResult(BaseConstans.getRequestHead(params));
        LogUtil.d("OOM3", "requestDressUpCallback的请求参数为" + StringUtil.beanToJSONString(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<HumanMerageResult>>(context) {
            @Override
            protected void _onError(String message) {
                LogUtil.d("OOM3", "message=" + message);
                ToastUtil.showToast(message);
                progress.closePragressDialog();
                if (calculagraph != null) {
                    calculagraph.destroyTimer();
                }
            }

            @Override
            protected void _onNext(List<HumanMerageResult> data) {
                if (data != null && data.size() > 0) {
                    progress.closePragressDialog();
                    String str = StringUtil.beanToJSONString(data);
                    LogUtil.d("OOM3", str);
                    if (callback != null) {
                        callback.isSuccess(data);
                        callback = null;
                    }
                    if (calculagraph != null) {
                        calculagraph.destroyTimer();
                    }
                } else {
                    LogUtil.d("OOM3", "data=null");
                }
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }


    public interface DressUpCallback {

        void isSuccess(List<HumanMerageResult>paths);

    }


    public void toCompressImg(String path, String templateId) {
        Luban.with(context)
                .load(path)
                .setTargetDir(mCatchFolder)
                .setCompressListener(new OnCompressListener() { //设置回调
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(File file) {
                        if (file != null) {
                            uploadFileToHuawei(file.getPath(), templateId);
                        }else{
                            uploadFileToHuawei(path, templateId);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        uploadFileToHuawei(path, templateId);
                    }
                }).launch();    //启动压缩
    }


}

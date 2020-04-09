package com.flyingeffects.com.ui.model;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.commonlyModel.DoubleClick;
import com.flyingeffects.com.commonlyModel.SaveAlbumPathModel;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.TemplateThumbItem;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.ui.interfaces.model.TemplateMvpCallback;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.shixing.sxve.ui.AssetDelegate;
import com.shixing.sxve.ui.SxveConstans;
import com.shixing.sxve.ui.model.TemplateModel;
import com.shixing.sxve.ui.view.WaitingDialog;
import com.shixing.sxve.ui.view.WaitingDialog_progress;
import com.shixing.sxvideoengine.SXRenderListener;
import com.shixing.sxvideoengine.SXTemplate;
import com.shixing.sxvideoengine.SXTemplateRender;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;


public class TemplateMvpModel {
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private TemplateMvpCallback callback;
    private Context context;
    private TemplateModel mTemplateModel = null;
    private File keepUunCatchPath;
    private boolean isOnDestroy;

    public TemplateMvpModel(Context context, TemplateMvpCallback callback) {
        this.context = context;
        this.callback = callback;
        keepUunCatchPath = context.getExternalFilesDir("runCatch/");
        isOnDestroy = false;
    }


    public void getReplaceableFilePath() {
        callback.returnReplaceableFilePath(mTemplateModel.getReplaceableFilePaths(Objects.requireNonNull(keepUunCatchPath.getPath())));
    }

    public void onDestroy() {
        isOnDestroy = true;
    }


    public void loadTemplate(String filePath, AssetDelegate delegate, int nowTemplateIsAnim) {
        Observable.just(filePath).map(s -> {
            try {
                mTemplateModel = new TemplateModel(filePath, delegate, context, nowTemplateIsAnim);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mTemplateModel;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(templateModel -> callback.completeTemplate(templateModel));
    }


    private String outputPathForVideoSaveToPhoto;

    public void renderVideo(String mTemplateFolder, String mAudio1Path, Boolean isPreview) {
        WaitingDialog_progress waitingDialog_progress = new WaitingDialog_progress(context);
        waitingDialog_progress.openProgressDialog();
        Observable.create((Observable.OnSubscribe<Boolean>) subscriber -> {
            String[] paths = mTemplateModel.getReplaceableFilePaths(Objects.requireNonNull(keepUunCatchPath.getPath()));
            LogUtil.d("OOM", "得到全部地址");
            SXTemplate template = new SXTemplate(mTemplateFolder, SXTemplate.TemplateUsage.kForRender); //模板对象类，需要传入模板路径和使用方式
            template.setReplaceableFilePaths(paths); //设置用户可修改的视频路径
            template.commit();
            template.setFileForAsset("black", SxveConstans.default_bg_path); //控制默认颜色
            SXTemplateRender sxTemplateRender;
            outputPathForVideoSaveToPhoto = SaveAlbumPathModel.getInstance().getKeepOutput();
            sxTemplateRender = new SXTemplateRender(template, mAudio1Path, outputPathForVideoSaveToPhoto);
            sxTemplateRender.setBitrateFactor(2f);
            sxTemplateRender.setIFrameInterval(2);
            sxTemplateRender.start();
            LogUtil.d("OOM", "开始合成");
            sxTemplateRender.setRenderListener(new SXRenderListener() {
                @Override
                public void onStart() {
                    LogUtil.d("OOM", "onStart");
                }

                @Override
                public void onUpdate(int progress) {
                    waitingDialog_progress.setProgress("正在保存中" + progress + "%\n" +
                            "请勿离开页面");
                    LogUtil.d("OOM", "progress=" + progress);
                }

                @Override
                public void onFinish(boolean success, String msg) {
                    waitingDialog_progress.closePragressDialog();
                    LogUtil.d("OOM", "onFinish+" + msg);
                    subscriber.onNext(success);
                    subscriber.onCompleted();
                }

                @Override
                public void onCancel() {
                    LogUtil.d("OOM", "onCancel");
                }
            });

        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                renderFinish(aBoolean, isPreview, outputPathForVideoSaveToPhoto);
            }
        });

    }


    public String[] getRealTimePreview() {
        return mTemplateModel.getReplaceableFilePaths(Objects.requireNonNull(keepUunCatchPath.getPath()));
    }


    private void renderFinish(boolean isSucceed, boolean isPreview, String outputPath) {
        LogUtil.d("OOM", "onFinish,success?=" + isSucceed + "MSG=" + isSucceed);
        WaitingDialog.closePragressDialog();

        if (isPreview) {
            callback.toPreview(outputPath);
        } else {
            if (isSucceed && !isOnDestroy) {
                albumBroadcast(outputPath);
                showDialog(outputPath);
            }
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


    private void showDialog(String path) {
        if (!DoubleClick.getInstance().isFastDoubleClick()) {
            LogUtil.d("showDialog", "showDialog");
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    //去除黑边
                    new ContextThemeWrapper(context, R.style.Theme_Transparent));
            builder.setTitle(context.getString(R.string.notification));
//            builder.setMessage(context.getString(R.string.have_saved_to_sdcard) +
//                    "【" + path + context.getString(R.string.folder) + "】");

            builder.setMessage("已为你保存到相册,多多分享给友友\n" + "【" + path + context.getString(R.string.folder) + "】"
            );


            builder.setNegativeButton(context.getString(R.string.got_it), (dialog, which) -> {
                dialog.dismiss();
            });
            builder.setCancelable(true);
            Dialog mDialog = builder.show();
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
        }
    }


    /**
     * todo 需要优化
     *
     * @param list
     * @param maxChooseNum
     */
    public void ChangeMaterial(List<String> list, int maxChooseNum, int needAssetsCount) {
        ArrayList<TemplateThumbItem> listItem = new ArrayList<>();
        for (int i = 0; i < maxChooseNum; i++) {
            listItem.add(new TemplateThumbItem("", 1, false));
        }
        List<String> list_all = new ArrayList<>();
        for (int i = 0; i < maxChooseNum; i++) {  //填满数据，为了缩略图
            if (list.size() > i && !TextUtils.isEmpty(list.get(i))) {
                list_all.add(list.get(i)); //前面的时path ，后面的为默认的path
            } else {
                list_all.add(SxveConstans.default_bg_path);
            }
        }
        for (int i = 0; i < list_all.size(); i++) {  //合成底部缩略图
            TemplateThumbItem templateThumbItem = new TemplateThumbItem();
            templateThumbItem.setPathUrl(list_all.get(i));
            if (i == 0) {
                templateThumbItem.setIsCheck(0);
            } else {
                templateThumbItem.setIsCheck(1);
            }
            listItem.set(i, templateThumbItem);
        }


        //这里是为了替换用户操作的页面
        List<String> listAssets = new ArrayList<>();
        for (int i = 0; i < needAssetsCount; i++) {  //填满数据，为了缩略图
            if (list.size() > i && !TextUtils.isEmpty(list.get(i))) {
                listAssets.add(list.get(i)); //前面的时path ，后面的为默认的path
            } else {
                listAssets.add(SxveConstans.default_bg_path);
            }
        }


        callback.ChangeMaterialCallback(listItem, list_all, listAssets);


    }


    /**
     * 请求漫画
     */
    private void requestCartoon(String strEditTextUsername) {
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", strEditTextUsername);
        // 启动时间
        Observable ob = Api.getDefault().toSms(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(context) {
            @Override
            protected void _onError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(Object data) {

            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);
    }

    private void downBjBitmap(String path) {
        Observable.just(path).map(s -> {
            File file1 = null;
            try {
                file1 = Glide.with(context)
                        .load(path)
                        .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return file1;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(path1 -> {
            try {
                if (path1 != null) {
                    //下载后的地址
                    callback.getCartoonPath(path1.getPath());
                } else {
                    WaitingDialog.closePragressDialog();
                    ToastUtil.showToast("请重试");
                }

            } catch (Exception e) {
                WaitingDialog.closePragressDialog();
                e.printStackTrace();
            }
        });
    }


}


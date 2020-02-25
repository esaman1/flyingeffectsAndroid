package com.flyingeffects.com.ui.model;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.ContextThemeWrapper;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.commonlyModel.DoubleClick;
import com.flyingeffects.com.commonlyModel.SaveAlbumPathModel;
import com.flyingeffects.com.ui.interfaces.model.TemplateMvpCallback;
import com.flyingeffects.com.utils.LogUtil;
import com.shixing.sxve.ui.AssetDelegate;
import com.shixing.sxve.ui.SxveConstans;
import com.shixing.sxve.ui.model.TemplateModel;
import com.shixing.sxve.ui.view.WaitingDialog;
import com.shixing.sxvideoengine.SXRenderListener;
import com.shixing.sxvideoengine.SXTemplate;
import com.shixing.sxvideoengine.SXTemplateRender;

import java.io.File;
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

    public TemplateMvpModel(Context context, TemplateMvpCallback callback) {
        this.context = context;
        this.callback = callback;
        keepUunCatchPath = context.getExternalFilesDir("runCatch/");
    }


    public void loadTemplate(String filePath, AssetDelegate delegate) {
        Observable.just(filePath).map(s -> {
            try {
                mTemplateModel = new TemplateModel(filePath, delegate, context);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mTemplateModel;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(templateModel -> callback.completeTemplate(templateModel));
    }


    private String outputPathForVideoSaveToPhoto;

    public void renderVideo(String mTemplateFolder, String mAudio1Path, Boolean isPreview) {
        WaitingDialog.openPragressDialog(context);
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
                    LogUtil.d("OOM", "progress=" + progress);
                }

                @Override
                public void onFinish(boolean success, String msg) {
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


    private void renderFinish(boolean isSucceed, boolean isPreview,String outputPath) {
        LogUtil.d("OOM", "onFinish,success?=" + isSucceed + "MSG=" + isSucceed);
        WaitingDialog.closePragressDialog();

        if (isPreview) {
            callback.toPreview(outputPath);

        } else {
            if (isSucceed) {
                //  mTemplateModel.restoreTemplate(true, "");
                showDialog(outputPath);
            }
        }
    }


    private void showDialog(String path) {
        if (!DoubleClick.getInstance().isFastDoubleClick()) {
            LogUtil.d("showDialog", "showDialog");
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    //去除黑边
                    new ContextThemeWrapper(context, R.style.Theme_Transparent));
            builder.setTitle(context.getString(R.string.notification));
            builder.setMessage(context.getString(R.string.have_saved_to_sdcard) +
                    "【" + path + context.getString(R.string.folder) + "】");
            builder.setNegativeButton(context.getString(R.string.got_it), (dialog, which) -> {
                dialog.dismiss();
            });
            builder.setCancelable(true);
            Dialog mDialog = builder.show();
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
        }
    }

}


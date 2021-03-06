package com.flyingeffects.com.ui.model;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.commonlyModel.DoubleClick;
import com.flyingeffects.com.commonlyModel.GetPathType;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.entity.TemplateThumbItem;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.BitmapManager;
import com.flyingeffects.com.manager.CompressionCuttingManage;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.manager.mediaManager;
import com.flyingeffects.com.ui.interfaces.model.TemplateMvpCallback;
import com.flyingeffects.com.ui.view.activity.MemeKeepActivity;
import com.flyingeffects.com.ui.view.activity.TemplateAddStickerActivity;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.view.MattingVideoEnity;
import com.glidebitmappool.GlideBitmapPool;
import com.orhanobut.hawk.Hawk;
import com.shixing.sxve.ui.AlbumType;
import com.shixing.sxve.ui.AssetDelegate;
import com.shixing.sxve.ui.SxveConstans;
import com.shixing.sxve.ui.model.MediaUiModel2;
import com.shixing.sxve.ui.model.TemplateModel;
import com.shixing.sxve.ui.view.WaitingDialog;
import com.shixing.sxvideoengine.SXRenderListener;
import com.shixing.sxvideoengine.SXTemplate;
import com.shixing.sxvideoengine.SXTemplateRender;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;


public class TemplateMvpModel {
    private MediaPlayer bgmPlayer;
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private TemplateMvpCallback callback;
    private Context context;
    private TemplateModel mTemplateModel = null;
    private File keepUunCatchPath;
    private boolean isOnDestroy;
    private String cacheCutVideoPath;
    private String backgroundPath;
    private String soundFolder;
    private String fromTo;
    private String saveVideoPath;
    private String templateName;
    private String templateId;
    private String templateType;


    public TemplateMvpModel(Context context, TemplateMvpCallback callback, String fromTo, String templateName, String templateId,String templateType) {
        this.context = context;
        this.callback = callback;
        this.fromTo = fromTo;
        this.templateName = templateName;
        this.templateId = templateId;
        keepUunCatchPath = context.getExternalFilesDir("runCatch/");
        FileManager fileManager = new FileManager();
        cacheCutVideoPath = fileManager.getFileCachePath(BaseApplication.getInstance(), "cacheMattingFolder");
        backgroundPath = fileManager.getFileCachePath(BaseApplication.getInstance(), "background");
        soundFolder = fileManager.getFileCachePath(context, "soundFolder");
        saveVideoPath = fileManager.getFileCachePath(context, "saveVideoPath");
        this.templateType=templateType;
        isOnDestroy = false;
    }


    public void getReplaceableFilePath() {
        callback.returnReplaceableFilePath(mTemplateModel.getReplaceableFilePaths(Objects.requireNonNull(keepUunCatchPath.getPath())));
    }


    public void statisticsToSave(String templateId) {
        HashMap<String, String> params = new HashMap<>();
        params.put("template_id", templateId);
        params.put("action_type", 2 + "");
        // ????????????
        Observable ob = Api.getDefault().saveTemplate(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(context) {
            @Override
            protected void onSubError(String message) {
            }

            @Override
            protected void onSubNext(Object data) {

            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);

    }


    /**
     * description ?????????
     * creation date: 2020/12/3
     * user : zhangtongju
     */
    public void toDressUp(String path, String templateId) {
        DressUpModel dressUpModel = new DressUpModel(context, new DressUpModel.DressUpCallback() {
            @Override
            public void isSuccess(List<String> paths) {
                callback.GetChangeDressUpData(paths);
            }
        }, true);

        dressUpModel.toDressUp(path, templateId);
    }


    public void getBjMusic(String videoPath) {
        mediaManager manager = new mediaManager(context);
        manager.splitMp4(videoPath, new File(soundFolder), new mediaManager.splitMp4Callback() {
            @Override
            public void splitSuccess(boolean isSuccess, String putPath) {
                callback.getSpliteMusic(putPath);
            }
        });
    }


    /**
     * description ????????????????????????
     * creation date: 2020/4/14
     * user : zhangtongju
     */
    public void getMattingVideoCover(String path) {
        //????????????????????????????????????????????????????????????
        if (AlbumType.isImage(GetPathType.getInstance().getPathType(path))) {
            Bitmap mattingMp = BitmapFactory.decodeFile(path);
            callback.showMattingVideoCover(mattingMp, path);
        } else {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(path);
            Bitmap bp = retriever.getFrameAtTime((long) 0);
            FileManager fileManager = new FileManager();
            String faceMattingFolder = fileManager.getFileCachePath(BaseApplication.getInstance(), "tailor");
            String savePath = faceMattingFolder + File.separator + UUID.randomUUID() + "cover.png";
            BitmapManager.getInstance().saveBitmapToPath(bp, savePath, isSuccess -> {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                       SegJni.nativeCreateSegHandler(context, ConUtil.getFileContent(context, R.raw.megviisegment_model), BaseConstans.THREADCOUNT);
                CompressionCuttingManage manage = new CompressionCuttingManage(context, "0", false, tailorPaths -> {
                    Bitmap mattingMp = BitmapFactory.decodeFile(tailorPaths.get(0));
                    if (mattingMp != null && bp != null) {
                        mattingMp = test(mattingMp, bp.getWidth(), bp.getHeight());
                        callback.showMattingVideoCover(mattingMp, tailorPaths.get(0));
                    }
                });
                List<String> list = new ArrayList<>();
                list.add(savePath);
                manage.toMatting(list);
//                    }
//                }).start();


            });
        }
    }

    public void onDestroy() {
        isOnDestroy = true;
    }


    /**
     * description ???????????????????????????????????????????????????????????????????????????
     * creation date: 2020/4/16
     * param :
     * user : zhangtongju
     */
    public Bitmap test(Bitmap bitmap, int width, int height) {
        int bmpWidth = bitmap.getWidth();
        int bmpHeight = bitmap.getHeight();
        float scaleWidth = ((float) width) / bmpWidth;
        Bitmap target = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas temp_canvas = new Canvas(target);
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleWidth);
        int tranH = (int) (bmpHeight * scaleWidth - height);
        if (tranH > 0) {
            tranH = Math.abs(tranH) / 2;
            tranH = -tranH;

        } else {
            tranH = Math.abs(tranH) / 2;
        }
        matrix.postTranslate(0, tranH);
        temp_canvas.drawBitmap(bitmap, matrix, new Paint());
        return target;
    }


    public void loadTemplate(String filePath, AssetDelegate delegate, int nowTemplateIsAnim, int nowTemplateIsMattingVideo, boolean isToSing) {
        Observable.just(filePath).map(s -> {
            try {
                mTemplateModel = new TemplateModel(filePath, delegate, context, nowTemplateIsAnim, nowTemplateIsMattingVideo, isToSing);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mTemplateModel;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(templateModel -> {
//            String path = backgroundPath + File.separator + "white .png";
//            Bitmap bitmap = Bitmap.createBitmap(templateModel.templateSize[0], templateModel.templateSize[1],
//                    Bitmap.Config.ARGB_8888);
//            bitmap.eraseColor(Color.parseColor("#FFFFFF"));//????????????
//            BitmapManager.getInstance().saveBitmapToPath(bitmap, path);
            LogUtil.d("OOM3", "completeTemplate");
            callback.completeTemplate(templateModel);
        });
    }


    //    private String outputPathForVideoSaveToPhoto;
    private String savePath;
    private boolean nowIsGifTemplate;

    public void renderVideo(String mTemplateFolder, String mAudio1Path, Boolean isPreview, int nowTemplateIsAnim, List<String> originalPath, boolean nowIsGifTemplate) {
        this.nowIsGifTemplate = nowIsGifTemplate;
        callback.showProgressDialog();
        if (FromToTemplate.PICTUREALBUM.equals(fromTo)) {
            StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "load_video_post_yj");
        } else {
            StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "load_video_post_mb");
        }
        Observable.create((Observable.OnSubscribe<Boolean>) subscriber -> {
            SXTemplate template = new SXTemplate(mTemplateFolder, SXTemplate.TemplateUsage.kForRender); //?????????????????????????????????????????????????????????
            String[] paths;
            paths = mTemplateModel.getReplaceableFilePaths(Objects.requireNonNull(keepUunCatchPath.getPath()));
            paths = repairRandomPaths.randomPaths(paths);
            if (mTemplateModel.HasBj && !TextUtils.isEmpty(mTemplateModel.getBackgroundPath())) {
                String[] newPaths = new String[paths.length + 1];
                System.arraycopy(paths, 0, newPaths, 0, paths.length);
                MediaUiModel2 mediaUiModel2 = (MediaUiModel2) mTemplateModel.mAssets.get(0).ui;
                if (AlbumType.isVideo(GetPathType.getInstance().getPathType(mTemplateModel.getBackgroundPath()))) {
                    newPaths[newPaths.length - 1] = mediaUiModel2.getpathForThisBjMatrixVideo(Objects.requireNonNull(context.getExternalFilesDir("runCatch/")).getPath(), mTemplateModel.getBackgroundPath());
                } else {
                    newPaths[newPaths.length - 1] = mediaUiModel2.getpathForThisBjMatrixImage(Objects.requireNonNull(context.getExternalFilesDir("runCatch/")).getPath(), mTemplateModel.getBackgroundPath());
                }
                template.setReplaceableFilePaths(newPaths); //????????????????????????????????????
            } else {
                template.setReplaceableFilePaths(paths); //????????????????????????????????????
            }
            template.commit();
            template.setFileForAsset("black", SxveConstans.default_bg_path); //??????????????????
            SXTemplateRender sxTemplateRender;
//            outputPathForVideoSaveToPhoto = SaveAlbumPathModel.getInstance().getKeepOutput();
            savePath = saveVideoPath + File.separator + System.currentTimeMillis() + "synthetic.mp4";
            sxTemplateRender = new SXTemplateRender(template, mAudio1Path, savePath);
            LogUtil.d("OOM", "saveVideoPath=" + savePath);
//            test()
//            sxTemplateRender.setBitrateFactor();
//            sxTemplateRender.setBitrateFactor(2f);
//            sxTemplateRender.setIFrameInterval(2);
//            int bitrate = (int) (mTemplateModel.templateSize[0] * mTemplateModel.templateSize[1] * mTemplateModel.fps - test());
//            sxTemplateRender.setBitrate(bitrate);
            sxTemplateRender.start();
            LogUtil.d("OOM", "????????????");
            sxTemplateRender.setRenderListener(new SXRenderListener() {
                @Override
                public void onStart() {
                    LogUtil.d("OOM", "onStart");
                }

                @Override
                public void onUpdate(int progress) {
                    callback.setDialogProgress(progress);

                    LogUtil.d("OOM", "progress=" + progress);
                }

                @Override
                public void onFinish(boolean success, String msg) {
                    callback.setDialogDismiss();

                    LogUtil.d("OOM", "onFinish+" + msg);
                    subscriber.onNext(success);
                    subscriber.onCompleted();
                }


                @Override
                public void onCancel() {
                    LogUtil.d("OOM", "onCancel");
                }
            });

        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(aBoolean -> renderFinish(aBoolean, isPreview, savePath));

    }


    /**
     * description ???????????????????????????????????????????????????????????????
     * creation date: 2021/4/20
     * user : zhangtongju
     */
    public void SaveSpecialTemplate(int api_type, boolean nowIsGifTemplate, int needAssetsCount, boolean isMatting) {
        String[] paths = mTemplateModel.getReplaceableFilePaths(Objects.requireNonNull(keepUunCatchPath.getPath()));
        List<String> list = Arrays.asList(paths);
        List strToList1 = new ArrayList(list);
        if (needAssetsCount == 1 && strToList1.size() > 1 && AlbumType.isImage(GetPathTypeModel.getInstance().getMediaType(paths[0]))) {
            //????????????mask??????
            strToList1.remove(1);
        } else if (AlbumType.isVideo(GetPathTypeModel.getInstance().getMediaType(paths[0])) && !isMatting) {
            strToList1.remove(1);
        }

        DressUpSpecialModel dressUpModel = new DressUpSpecialModel(context, url -> {
            LogUtil.d("oom22","templateName="+templateName);
            if (nowIsGifTemplate || url.contains("gif")) {
                Intent intent = new Intent(context, MemeKeepActivity.class);
                intent.putExtra("templateType", templateType);
                intent.putExtra("videoPath", url);
                intent.putExtra("title", templateName);
                intent.putExtra("templateId", templateId);
                intent.putExtra("IsFrom", fromTo);
                context.startActivity(intent);
            } else {
                Intent intent = new Intent(context, TemplateAddStickerActivity.class);
                intent.putExtra("videoPath", url);
                intent.putExtra("templateType", templateType);
                intent.putExtra("title", templateName);
                intent.putExtra("templateId", templateId);
                intent.putExtra("IsFrom", fromTo);
                context.startActivity(intent);
            }
        }, templateId);
        dressUpModel.toDressUp(strToList1, api_type);

    }


    public String[] getRealTimePreview() {
        return mTemplateModel.getReplaceableFilePaths(Objects.requireNonNull(keepUunCatchPath.getPath()));
    }


    private void renderFinish(boolean isSucceed, boolean isPreview, String outputPath) {
        LogUtil.d("OOM", "onFinish,success?=" + isSucceed + "outputPath=" + outputPath);

        WaitingDialog.closeProgressDialog();
        if (isPreview) {
            callback.toPreview(outputPath);
        } else {
            if (isSucceed && !isOnDestroy) {
                if (nowIsGifTemplate) {
                    LogUtil.d("oom22","templateName="+templateName);
                    Intent intent = new Intent(context, MemeKeepActivity.class);
                    intent.putExtra("templateType",templateType);
                    intent.putExtra("videoPath", outputPath);
                    intent.putExtra("title", templateName);
                    intent.putExtra("templateId", templateId);
                    intent.putExtra("IsFrom", fromTo);
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, TemplateAddStickerActivity.class);
                    intent.putExtra("videoPath", outputPath);
                    intent.putExtra("title", templateName);
                    intent.putExtra("templateId", templateId);
                    intent.putExtra("IsFrom", fromTo);
                    context.startActivity(intent);
                }


            }
        }
    }

    /**
     * description ?????????????????????
     * date: ???2019/8/16 14:24
     * author: ????????? @?????? jutongzhang@sina.com
     */
    private void albumBroadcast(String outputFile) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(new File(outputFile)));
        context.sendBroadcast(intent);
    }


    private void showDialog(String path) {
        if (!DoubleClick.getInstance().isFastDoubleClick() && !isOnDestroy) {
            ShowPraiseModel.keepAlbumCount();
            keepAlbumCount();
            LogUtil.d("showDialog", "showDialog");
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    //????????????
                    new ContextThemeWrapper(context, R.style.Theme_Transparent));
            builder.setTitle(context.getString(R.string.notification));
//            builder.setMessage(context.getString(R.string.have_saved_to_sdcard) +
//                    "???" + path + context.getString(R.string.folder) + "???");

            builder.setMessage("????????????????????????,?????????????????????\n" + "???" + path + context.getString(R.string.folder) + "???"
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

    private void keepAlbumCount() {
        int num = Hawk.get("keepAlbumNum");
        num++;
        Hawk.put("keepAlbumNum", num);
    }


    /**
     * todo ????????????
     *
     * @param list
     * @param maxChooseNum
     */
    public void changeMaterial(List<String> list, int maxChooseNum, int needAssetsCount) {
        ArrayList<TemplateThumbItem> listItem = new ArrayList<>();
        for (int i = 0; i < maxChooseNum; i++) {
            listItem.add(new TemplateThumbItem("", 1, false));
        }
        List<String> listAll = new ArrayList<>();
        for (int i = 0; i < maxChooseNum; i++) {  //??????????????????????????????
            if (list.size() > i && !TextUtils.isEmpty(list.get(i))) {
                listAll.add(list.get(i)); //????????????path ????????????????????????path
            } else {
                listAll.add(SxveConstans.default_bg_path);
            }
        }
        for (int i = 0; i < listAll.size(); i++) {  //?????????????????????
            TemplateThumbItem templateThumbItem = new TemplateThumbItem();
            templateThumbItem.setPathUrl(listAll.get(i));
            if (i == 0) {
                templateThumbItem.setIsCheck(0);
            } else {
                templateThumbItem.setIsCheck(1);
            }
            listItem.set(i, templateThumbItem);
        }


        //??????????????????????????????????????????
        List<String> listAssets = new ArrayList<>();
        for (int i = 0; i < needAssetsCount; i++) {  //??????????????????????????????
            if (list.size() > i && !TextUtils.isEmpty(list.get(i))) {
                listAssets.add(list.get(i)); //????????????path ????????????????????????path
            } else {
                listAssets.add(SxveConstans.default_bg_path);
            }
        }


        callback.ChangeMaterialCallback(listItem, listAll, listAssets);


    }


    /**
     * description ??????????????????
     * creation date: 2020/4/14
     * user : zhangtongju
     */
    public void intoMattingVideo(String path, String templatename) {

        String cacheVideoPath = cacheCutVideoPath + "/Matting.mp4";
        File file = new File(cacheVideoPath);
        if (file.exists()) {
            //?????????????????????,?????????????????????????????????????????????????????????imagePaht???????????????????????????
            callback.ChangeMaterialCallbackForVideo(path, cacheVideoPath, true);
        } else {
            //??????????????????
            gotoMattingVideo(path, templatename);
        }
    }





    public void getButtomIcon(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        Bitmap mBitmap = retriever.getFrameAtTime(0);
        String fileName = backgroundPath + File.separator + UUID.randomUUID() + "bottomIcon.png";
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        BitmapManager.getInstance().saveBitmapToPath(mBitmap, fileName, new BitmapManager.saveToFileCallback() {
            @Override
            public void isSuccess(boolean isSuccess) {
                callback.showBottomIcon(fileName);
                GlideBitmapPool.putBitmap(mBitmap);
            }
        });
    }

    private void gotoMattingVideo(String originalPath, String templatename) {
//        SegJni.nativeCreateSegHandler(context, ConUtil.getFileContent(context, R.raw.megviisegment_model), 4);
        Observable.just(originalPath).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                VideoMattingModel videoMattingModel = new VideoMattingModel(originalPath, context, new VideoMattingModel.MattingSuccess() {
                    @Override
                    public void isSuccess(boolean isSuccess, String path, String noMaskingPath) {
                        EventBus.getDefault().post(new MattingVideoEnity(noMaskingPath, path, originalPath, 1));
                    }
                });
                videoMattingModel.ToExtractFrame(templatename);
            }
        });
    }


    /**
     * ????????????
     */
    private void requestCartoon(String strEditTextUsername) {
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", strEditTextUsername);
        // ????????????
        Observable ob = Api.getDefault().toSms(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(context) {
            @Override
            protected void onSubError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void onSubNext(Object data) {

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
                    //??????????????????
                    callback.getCartoonPath(path1.getPath());
                } else {
                    WaitingDialog.closeProgressDialog();
                    ToastUtil.showToast("?????????");
                }

            } catch (Exception e) {
                WaitingDialog.closeProgressDialog();
                e.printStackTrace();
            }
        });
    }


    public void playBGMMusic(String bgmPath, int progress) {
        if (bgmPlayer == null) {
            bgmPlayer = new MediaPlayer();
            try {
                bgmPlayer.setDataSource(bgmPath);
                bgmPlayer.prepare();
                bgmPlayer.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        bgmPlayer.seekTo(progress);

    }


    public void PauseBgmMusic() {
        if (bgmPlayer != null) {
            bgmPlayer.pause();
        }
    }

    public void StopBgmMusic() {
        if (bgmPlayer != null) {
            bgmPlayer.stop();
            bgmPlayer.release();
            bgmPlayer = null;
        }
    }


}


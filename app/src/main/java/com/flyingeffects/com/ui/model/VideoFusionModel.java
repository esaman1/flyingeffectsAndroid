package com.flyingeffects.com.ui.model;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.Log;

import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.VideoFusiomBean;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.Calculagraph;
import com.flyingeffects.com.manager.DownloadVideoManage;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.manager.huaweiObs;
import com.flyingeffects.com.ui.view.activity.TemplateAddStickerActivity;
import com.flyingeffects.com.ui.view.dialog.LoadingDialog;
import com.flyingeffects.com.utils.FilterUtils;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.lansosdk.LanSongFilter.LanSongFilter;
import com.lansosdk.LanSongFilter.LanSongMaskBlendFilter;
import com.lansosdk.box.BitmapLayer;
import com.lansosdk.box.LSOScaleType;
import com.lansosdk.box.LSOVideoOption;
import com.lansosdk.box.VideoFrameLayer;
import com.lansosdk.videoeditor.DrawPadAllExecute2;
import com.lansosdk.videoeditor.MediaInfo;
import com.shixing.sxve.ui.view.WaitingDialog_progress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * description ：视频融合，实现方式
 * 1:上传图片到服务器，然后得到一个完整的替换的视频
 * 2:通过蓝松，融合一个底部的图片和返回的完整视频，位置和上传的位置是同一个位置
 * creation date: 2021/3/1
 * user : zhangtongju
 */


public class VideoFusionModel {
    private static final int MAKE_SRC_ORIENT_CODE_LEFT = 0;
    private static final int MAKE_SRC_ORIENT_CODE_RIGHT = 1;
    private static final int MAKE_SRC_ORIENT_CODE_TOP = 2;
    private static final int MAKE_SRC_ORIENT_CODE_BOTTOM = 3;

    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private int DRAWPADWIDTH;
    private int DRAWPADHEIGHT;
    private static final int FRAME_RATE = 20;
    private LoadingDialog mLoadingDialog;

    /**
     * 服務器返回的視頻地址
     */
    private String serversReturnPath;


    /**
     * 用戶原视频地址
     */
    private String originalPath;


    private Context context;

    private long duration;

    private String fromTo;

    private String title;

    private float TranXPercent;
    private float TranYPercent;
    private float ScaleTranXPercent;
    private String serverVideo;


    public VideoFusionModel(Context context, String serversReturnPath, String originalPath, String fromTo, String title, int DRAWPADWIDTH, int DRAWPADHEIGHT, float TranX, float TranY, float Scale) {
        this.originalPath = originalPath;
        this.context = context;
        this.serversReturnPath = serversReturnPath;

        this.fromTo = fromTo;
        this.title = title;
        this.DRAWPADWIDTH = DRAWPADWIDTH;
        this.DRAWPADHEIGHT = DRAWPADHEIGHT;

        this.TranXPercent = TranX;
        FileManager fileManager = new FileManager();
        serverVideo = fileManager.getFileCachePath(context, "FusionVideo");
        this.TranYPercent = TranY;
        this.ScaleTranXPercent = Scale;

    }


    /**
     * description ：融合视频，通过蓝松的视频融合
     * creation date: 2021/3/1
     * user : zhangtongju
     */
    public void compoundVideo() {

        MediaInfo mediaInfo = new MediaInfo(serversReturnPath);
        mediaInfo.prepare();
        duration = mediaInfo.getDurationUs();
        mediaInfo.release();


        try {
            DrawPadAllExecute2 execute = new DrawPadAllExecute2(context, DRAWPADWIDTH, DRAWPADHEIGHT, duration);
            execute.setFrameRate(FRAME_RATE);

            LogUtil.d("OOM2", "时长为" + duration);
            execute.setEncodeBitrate(5 * 1024 * 1024);
            execute.setOnLanSongSDKErrorListener(message -> {
            });
            execute.setOnLanSongSDKProgressListener((l, i) -> {
                mLoadingDialog.setProgress(i);
                LogUtil.d("OOM2", "Progress=" + i);
            });
            execute.setOnLanSongSDKCompletedListener(exportPath -> {
                mLoadingDialog.dismiss();
                LogUtil.d("OOM2", "exportPath=" + exportPath);
                Intent intent = new Intent(context, TemplateAddStickerActivity.class);
                intent.putExtra("videoPath", exportPath);
                intent.putExtra("title", title);
                intent.putExtra("IsFrom", fromTo);
                context.startActivity(intent);
            });
            addBitmapLayer(execute);
            setVideoLayer(execute);
            execute.start();
        } catch (Exception e) {
            mLoadingDialog.dismiss();
            e.printStackTrace();
        }
    }


    private void addBitmapLayer(DrawPadAllExecute2 execute) {
        Bitmap bp = BitmapFactory.decodeFile(originalPath);
        int size = bp.getWidth();
        float needScale = size / 256f;
        LogUtil.d("OOM3", "需要缩放比为" + needScale);
        Matrix matrix = new Matrix();
        matrix.setScale(needScale, needScale);
        bp = Bitmap.createBitmap(bp, 0, 0, bp.getWidth(),
                bp.getHeight(), matrix, true);
        BitmapLayer bpLayer = execute.addBitmapLayer(bp);
        bpLayer.setScaleType(LSOScaleType.FILL_COMPOSITION);
    }


    private void setVideoLayer(DrawPadAllExecute2 execute) {
        LSOVideoOption option;
        try {
            option = new LSOVideoOption(serversReturnPath);
            VideoFrameLayer videoLayer = execute.addVideoLayer(option);
            float LayerWidth = videoLayer.getLayerWidth();
            LogUtil.d("OOM2", "ScaleTranXPercent=" + ScaleTranXPercent);
            float scale = DRAWPADWIDTH * ScaleTranXPercent / (float) LayerWidth;
            float LayerHeight = videoLayer.getLayerHeight();
            float needDrawHeight = LayerHeight * scale;
            videoLayer.setScaledValue(DRAWPADWIDTH * ScaleTranXPercent, needDrawHeight);

            if (TranXPercent != 0) {
                videoLayer.setPosition(videoLayer.getPadWidth() * TranXPercent, videoLayer.getPositionY());
            }

            if (TranYPercent != 0) {
                videoLayer.setPosition(videoLayer.getPositionX(), videoLayer.getPadHeight() * TranYPercent);
            }

            List<LanSongFilter> list = new ArrayList<>();
            list.add(FilterUtils.createBlendFilter(context, LanSongMaskBlendFilter.class, makeSrc(DRAWPADWIDTH, DRAWPADHEIGHT, 0.1f, MAKE_SRC_ORIENT_CODE_BOTTOM)));
            list.add(FilterUtils.createBlendFilter(context, LanSongMaskBlendFilter.class, makeSrc(DRAWPADWIDTH, DRAWPADHEIGHT, 0.1f, MAKE_SRC_ORIENT_CODE_TOP)));
            list.add(FilterUtils.createBlendFilter(context, LanSongMaskBlendFilter.class, makeSrc(DRAWPADWIDTH, DRAWPADHEIGHT, 0.1f, MAKE_SRC_ORIENT_CODE_LEFT)));
            list.add(FilterUtils.createBlendFilter(context, LanSongMaskBlendFilter.class, makeSrc(DRAWPADWIDTH, DRAWPADHEIGHT, 0.1f, MAKE_SRC_ORIENT_CODE_RIGHT)));

            videoLayer.switchFilterList(list);

        } catch (Exception e) {
            LogUtil.d("OOM", "e-------" + e.getMessage());
            e.printStackTrace();
        }
    }

    private LoadingDialog buildLoadingDialog() {
        LoadingDialog dialog = LoadingDialog.getBuilder(context)
                .setHasAd(false)
                .setTitle("正在合成中...")
                .build();
        return dialog;
    }


    /**
     * description ：添加底部虚化
     * creation date: 2021/3/4
     * user : zhangtongju
     */
    private Bitmap makeSrc(int w, int h, float percent, int orientation) {
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        int graLine;
        LinearGradient gradient;
        switch (orientation) {
            case MAKE_SRC_ORIENT_CODE_BOTTOM:
                graLine = (int) (h - h * percent);
                gradient = new LinearGradient(w, graLine, w, h,
                        Color.parseColor("#ffffff"), Color.TRANSPARENT, Shader.TileMode.CLAMP);
                break;
            case MAKE_SRC_ORIENT_CODE_RIGHT:
                graLine = (int) (w - w * percent);
                gradient = new LinearGradient(graLine, 0, w, 0,
                        Color.parseColor("#ffffff"), Color.TRANSPARENT, Shader.TileMode.CLAMP);
                break;
            case MAKE_SRC_ORIENT_CODE_LEFT:
                graLine = (int) (w * percent);
                gradient = new LinearGradient(0, h, graLine, h,
                        Color.TRANSPARENT, Color.parseColor("#ffffff"), Shader.TileMode.CLAMP);
                break;
            default:
                graLine = (int) (h * percent);
                gradient = new LinearGradient(w, 0, w, graLine,
                        Color.TRANSPARENT, Color.parseColor("#ffffff"), Shader.TileMode.CLAMP);
                break;
        }

        p.setShader(gradient);
        c.drawRect(0, 0, w, h, p);
        return bm;
    }

    private Bitmap makeSrc(Bitmap bm, int w, int h, float percent, int orientation) {
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        int graLine;
        LinearGradient gradient;
        switch (orientation) {
            case MAKE_SRC_ORIENT_CODE_BOTTOM:
                graLine = (int) (h - h * percent);
                gradient = new LinearGradient(w, graLine, w, graLine + 100,
                        Color.parseColor("#ffffff"), Color.TRANSPARENT, Shader.TileMode.CLAMP);
                break;
            case MAKE_SRC_ORIENT_CODE_RIGHT:
                graLine = (int) (w - w * percent);
                gradient = new LinearGradient(graLine, h, graLine + 100, h,
                        Color.parseColor("#ffffff"), Color.TRANSPARENT, Shader.TileMode.CLAMP);
                break;
            case MAKE_SRC_ORIENT_CODE_LEFT:
                gradient = new LinearGradient(0, h, 100, h,
                        Color.TRANSPARENT, Color.parseColor("#ffffff"), Shader.TileMode.CLAMP);
                break;
            default:
                gradient = new LinearGradient(w, 0, w, 100,
                        Color.TRANSPARENT, Color.parseColor("#ffffff"), Shader.TileMode.CLAMP);
                break;
        }

        p.setShader(gradient);
        c.drawRect(0, 0, w, h, p);
        return bm;
    }

    private Bitmap createFilterBitmap(int w, int h, float percent) {
        Bitmap bitmap = makeSrc(w, h, percent, MAKE_SRC_ORIENT_CODE_LEFT);
        bitmap = makeSrc(bitmap, w, h, percent, MAKE_SRC_ORIENT_CODE_RIGHT);
        bitmap = makeSrc(bitmap, w, h, percent, MAKE_SRC_ORIENT_CODE_TOP);
        bitmap = makeSrc(bitmap, w, h, percent, MAKE_SRC_ORIENT_CODE_BOTTOM);
        return bitmap;
    }


    /**
     * description ：上传换装图片到华为云
     * creation date: 2020/12/4
     * user : zhangtongju
     */
    public void uploadFileToHuawei(String path, String template_id) {
        mLoadingDialog = buildLoadingDialog();
        mLoadingDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String type = path.substring(path.length() - 4);
                String nowTime = StringUtil.getCurrentTimeymd();
                String copyName = "media/android/Sideface/" + nowTime + "/" + System.currentTimeMillis() + type;
                String uploadPath = "http://cdn.flying.flyingeffect.com/" + copyName;
                Log.d("OOM3", "uploadFileToHuawei" + "当前上传的地址为" + path + "当前的名字为" + copyName);
                huaweiObs.getInstance().uploadFileToHawei(path, copyName, str -> Observable.just(str).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        LogUtil.d("OOM3", "上传华为云成功,地址为" + s);
                        informServers(uploadPath, template_id);
                    }
                }));
            }
        }).start();
    }


    /**
     * description ：请求结果
     * creation date: 2020/12/4
     * user : zhangtongju
     */
    private void requestDressUpCallback(String template_id) {
        HashMap<String, String> params = new HashMap<>();
        params.put("request_id", template_id);
        Observable ob = Api.getDefault().animalResult(BaseConstans.getRequestHead(params));
        LogUtil.d("OOM3", "requestDressUpCallback的请求参数为" + StringUtil.beanToJSONString(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<VideoFusiomBean>(context) {
            @Override
            protected void onSubError(String message) {
                LogUtil.d("OOM3", "请求结果=" + message);
                if (calculagraph != null) {
                    calculagraph.destroyTimer();
                }
            }

            @Override
            protected void onSubNext(VideoFusiomBean data) {
                LogUtil.d("OOM3", StringUtil.beanToJSONString(data));
                if (data.getStatus() == 2) {
                    calculagraph.destroyTimer();
                    Observable.just(data.getMp4()).observeOn(Schedulers.io()).subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            downVideoToLocal(s);
                        }
                    });


                }
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }


    /**
     * description ：通知后台,请求换装接口
     * creation date: 2020/12/4
     * user : zhangtongju
     */
    private void informServers(String path, String template_id) {
        HashMap<String, String> params = new HashMap<>();
        params.put("image", path);
        params.put("template_id", template_id);
        Observable ob = Api.getDefault().animalImage(BaseConstans.getRequestHead(params));
        LogUtil.d("OOM3", "requestDressUpCallback的请求参数为" + StringUtil.beanToJSONString(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<String>(context) {
            @Override
            protected void onSubError(String message) {
                LogUtil.d("OOM3", "通知服务器失败" + message);
                mLoadingDialog.dismiss();
            }

            @Override
            protected void onSubNext(String data) {
                LogUtil.d("OOM3", "通知服务器成功" + StringUtil.beanToJSONString(data));
                startTimer(data);

            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }


    /**
     * description ：下载视频在本地
     * creation date: 2021/3/4
     * user : zhangtongju
     */
    private void downVideoToLocal(String serverPath) {
        String path = serverVideo + "/video.mp4";
        Observable.just(serverPath).subscribeOn(Schedulers.io()).subscribe(s -> {
            DownloadVideoManage manage = new DownloadVideoManage(new DownloadVideoManage.downloadSuccess() {
                @Override
                public void isSuccess(boolean isSuccess) {
                    serversReturnPath = path;
                    mLoadingDialog.dismiss();
                    compoundVideo();
                }
            });
            manage.DownloadVideo(serverPath, path);
        });

    }


    /**
     * description ：开始轮训，几秒执行一次
     * creation date: 2021/3/2
     * user : zhangtongju
     */
    private Calculagraph calculagraph;

    private void startTimer(String Id) {
        calculagraph = new Calculagraph();
        calculagraph.startTimer(7f, 9, new Calculagraph.Callback() {
            @Override
            public void isTimeUp() {
                LogUtil.d("OOM3", "开始请求融合结果");
                Observable.just(Id).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        requestDressUpCallback(s);
                    }
                });
            }

            @Override
            public void isDone() {
            }
        });
    }


}

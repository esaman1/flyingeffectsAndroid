package com.flyingeffects.com.ui.model;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.HumanMerageResult;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.Calculagraph;
import com.flyingeffects.com.manager.huaweiObs;
import com.flyingeffects.com.ui.view.activity.TemplateAddStickerActivity;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.lansosdk.box.BitmapLayer;
import com.lansosdk.box.LSOBitmapAsset;
import com.lansosdk.box.LSOScaleType;
import com.lansosdk.box.LSOVideoOption;
import com.lansosdk.box.Layer;
import com.lansosdk.videoeditor.DrawPadAllExecute2;
import com.lansosdk.videoeditor.MediaInfo;
import com.shixing.sxve.ui.view.WaitingDialog_progress;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

/**
 * description ：视频融合，实现方式
 * 1:上传图片到服务器，然后得到一个完整的替换的视频
 * 2:通过蓝松，融合一个底部的图片和返回的完整视频，位置和上传的位置是同一个位置
 * creation date: 2021/3/1
 * user : zhangtongju
 */


public class VideoFusionModel {


    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private  int DRAWPADWIDTH ;
    private  int DRAWPADHEIGHT ;
    private static final int FRAME_RATE = 20;
    private WaitingDialog_progress progress;

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

    private Matrix matrix;

    private Matrix inverseMatrix;

    public VideoFusionModel(Context context, String serversReturnPath, String originalPath,String fromTo,String title,int DRAWPADWIDTH,int DRAWPADHEIGHT,Matrix matrix,Matrix inverseMatrix) {
        this.originalPath = originalPath;
        this.context = context;
        this.serversReturnPath = serversReturnPath;
        MediaInfo mediaInfo = new MediaInfo(serversReturnPath);
        mediaInfo.prepare();
        duration = mediaInfo.getDurationUs();
        this.fromTo=fromTo;
        this.title=title;
        this.matrix=matrix;
        this.inverseMatrix=inverseMatrix;
        this.DRAWPADWIDTH=DRAWPADWIDTH;
        this.DRAWPADHEIGHT=DRAWPADHEIGHT;
        mediaInfo.release();
    }


    /**
     * description ：融合视频，通过蓝松的视频融合
     * creation date: 2021/3/1
     * user : zhangtongju
     */
    public  void compoundVideo() {
        progress = new WaitingDialog_progress(context);
        progress.openProgressDialog("正在换装中...");

        try {
            DrawPadAllExecute2 execute = new DrawPadAllExecute2(context, DRAWPADWIDTH, DRAWPADHEIGHT, duration );
            execute.setFrameRate(FRAME_RATE);

            LogUtil.d("OOM2", "时长为" + duration );
            execute.setEncodeBitrate(5 * 1024 * 1024);
            execute.setOnLanSongSDKErrorListener(message -> {
            });
            execute.setOnLanSongSDKProgressListener((l, i) -> {
                progress.setProgress(i+"%");
                LogUtil.d("OOM2","Progress="+i);
            });
            execute.setOnLanSongSDKCompletedListener(exportPath -> {
                progress.closePragressDialog();
                LogUtil.d("OOM2","exportPath="+exportPath);
                Intent intent = new Intent(context, TemplateAddStickerActivity.class);
                intent.putExtra("videoPath", exportPath);
                intent.putExtra("title",title);
                intent.putExtra("IsFrom",fromTo);
                context.startActivity(intent);
            });
            addBitmapLayer(execute);
            setVideoLayer(execute);
            execute.start();
        } catch (Exception e) {
            progress.closePragressDialog();
            e.printStackTrace();
        }


    }




    private void addBitmapLayer(DrawPadAllExecute2 execute){
        Bitmap bitmap = Bitmap.createBitmap(DRAWPADWIDTH, DRAWPADHEIGHT, Bitmap.Config.ARGB_8888);
        Bitmap bp= BitmapFactory.decodeFile(originalPath);
        Canvas canvas = new Canvas(bitmap);
        if (bp != null) {
            canvas.drawBitmap(bp, matrix, new Paint());
        }
        execute.addBitmapLayer(bitmap);
    }


    private void setVideoLayer(DrawPadAllExecute2 execute) {


        float values []=new float[9];
        inverseMatrix.getValues(values);
        float tranx=values[2];
        float tranY=values[5];

        LogUtil.d("OOM2","tranx="+tranx);
        LogUtil.d("OOM2","tranY="+tranY);
        float scanx=values[0];
        float scany=values[4];

        LogUtil.d("OOM2","scanx="+scanx);
        LogUtil.d("OOM2","scany="+scany);

        LSOVideoOption option;
        try {
            option = new LSOVideoOption(serversReturnPath);
            option.setLooping(false);
            Layer bgLayer = execute.addVideoLayer(option, 0, Long.MAX_VALUE, false, true);
//                float LayerWidth = bgLayer.getLayerWidth();
//                float scale = DRAWPADWIDTH / (float) LayerWidth;
//                float LayerHeight = bgLayer.getLayerHeight();
                bgLayer.setScale(scanx, scany);

//            if (transplationPos.getToY() != 0) {
//                LogUtil.d("translationalXY", "yy=" + transplationPos.getToY());
//                bgLayer.setPosition(bgLayer.getPositionX(), bgLayer.getPadHeight() * tranY);
//            }




        } catch (Exception e) {
            LogUtil.d("OOM", "e-------" + e.getMessage());
            e.printStackTrace();
        }
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
//                informServers(uploadPath, template_id);
//                if (dressUpCatchCallback != null) {
//                    dressUpCatchCallback.isSuccess(uploadPath);
//                }
                requestDressUpCallback(uploadPath, template_id);
//                File file=new File(path);
//                if(file.exists()){
//                    file.delete();
//                }
            }
        }));
    }


    /**
     * description ：通知后台,请求换装接口
     * creation date: 2020/12/4
     * user : zhangtongju
     */
    private void requestDressUpCallback(String path, String template_id) {
        HashMap<String, String> params = new HashMap<>();
        params.put("image", path);
        params.put("template_id", template_id);

//        params.put("request_id", request_id);
        Observable ob = Api.getDefault().humanMerageResult(BaseConstans.getRequestHead(params));
        LogUtil.d("OOM3", "requestDressUpCallback的请求参数为" + StringUtil.beanToJSONString(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<HumanMerageResult>>(context) {
            @Override
            protected void onSubError(String message) {
//                LogUtil.d("OOM3", "message=" + message);
//                ToastUtil.showToast(message);
//                progress.closePragressDialog();
//                if (calculagraph != null) {
//                    calculagraph.destroyTimer();
//                }
//                if (callback != null) {
//                    callback.isSuccess(null);
//                }
            }

            @Override
            protected void onSubNext(List<HumanMerageResult> data) {
//                if (data != null && data.size() > 0) {
//                    String str = StringUtil.beanToJSONString(data);
//                    LogUtil.d("OOM3", "请求的结果为：" + str);
//                    GetDressUpPath(data);
////                    if (callback != null) {
////                        callback.isSuccess(data);
////                        callback = null;
////                    }
//                    if (calculagraph != null) {
//                        calculagraph.destroyTimer();
//                    }
//                } else {
//                    LogUtil.d("OOM3", "data=null");
//                }
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }


    /**
     * description ：开始轮训，几秒执行一次
     * creation date: 2021/3/2
     * user : zhangtongju
     */
    private Calculagraph calculagraph;

    private void startTimer(String id) {
        calculagraph = new Calculagraph();
        calculagraph.startTimer(5f, 5, new Calculagraph.Callback() {
            @Override
            public void isTimeUp() {
                LogUtil.d("OOM3", "开始请求融合结果");
                Observable.just(id).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
//                        requestDressUpCallback(s);
                    }
                });
            }

            @Override
            public void isDone() {
//                progress.closePragressDialog();
            }
        });
    }


}

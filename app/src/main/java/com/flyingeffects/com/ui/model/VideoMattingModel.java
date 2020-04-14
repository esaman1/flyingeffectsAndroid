package com.flyingeffects.com.ui.model;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.commonlyModel.SaveAlbumPathModel;
import com.flyingeffects.com.commonlyModel.getVideoInfo;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.VideoInfo;
import com.flyingeffects.com.manager.BitmapManager;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.utils.FileUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.glidebitmappool.GlideBitmapPool;
import com.lansosdk.box.BitmapLayer;
import com.lansosdk.box.CanvasLayer;
import com.lansosdk.box.ExtractVideoFrame;
import com.lansosdk.box.LSOBitmapAsset;
import com.lansosdk.box.LSOLog;
import com.lansosdk.box.LSOVideoOption;
import com.lansosdk.box.OnCustomFrameOutListener;
import com.lansosdk.box.onExtractVideoFrameCompletedListener;
import com.lansosdk.box.onExtractVideoFrameProgressListener;
import com.lansosdk.videoeditor.DrawPadAllExecute2;
import com.lansosdk.videoeditor.MediaInfo;
import com.lansosdk.videoeditor.VideoOneDo2;
import com.megvii.segjni.SegJni;
import com.shixing.sxve.ui.view.WaitingDialog_progress;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * description ：视频抠图控制类
 * creation date: 2020/3/27
 * user : zhangtongju
 */
public class VideoMattingModel {
    private ExtractVideoFrame mExtractFrame;
    private VideoOneDo2 videoOneDo;
    private String videoPath;
    /**
     * 专门用来存储face 抠像的文件夹
     */
    private String faceFolder;
    /**
     * 专门用来存储已经抠图的文件夹
     */
    private String faceMattingFolder;

    /**
     * 专门用来储存已经合成视频的文件夹
     */
    private String cacheCutVideoPath;


    private int frameCount;
    private static final int DRAWPADWIDTH = 720;
    private static final int DRAWPADHEIGHT = 1280;
    private static final int FRAME_RATE = 20;
    private Context context;

    private VideoInfo videoInfo;
    WaitingDialog_progress dialog;


    MattingSuccess callback;

    public VideoMattingModel(String videoPath, Context context, MattingSuccess callback) {
        this.callback = callback;
        this.videoPath = videoPath;
        this.context = context;
        videoInfo = getVideoInfo.getInstance().getRingDuring(videoPath);
        FileManager fileManager = new FileManager();
        faceFolder = fileManager.getFileCachePath(BaseApplication.getInstance(), "faceFolder");
        faceMattingFolder = fileManager.getFileCachePath(BaseApplication.getInstance(), "faceMattingFolder");
        cacheCutVideoPath = fileManager.getFileCachePath(BaseApplication.getInstance(), "cacheMattingFolder");
        LogUtil.d("OOM", "faceMattingFolder=" + faceMattingFolder);
        dialog = new WaitingDialog_progress(context);
        dialog.openProgressDialog();
    }

    public VideoMattingModel(String videoPath, Context context) {
        this.videoPath = videoPath;
        this.context = context;
        videoInfo = getVideoInfo.getInstance().getRingDuring(videoPath);
        FileManager fileManager = new FileManager();
        faceFolder = fileManager.getFileCachePath(BaseApplication.getInstance(), "faceFolder");
        cacheCutVideoPath = fileManager.getFileCachePath(BaseApplication.getInstance(), "cacheMattingFolder");
        faceMattingFolder = fileManager.getFileCachePath(BaseApplication.getInstance(), "faceMattingFolder");
        LogUtil.d("OOM", "faceMattingFolder=" + faceMattingFolder);
        dialog = new WaitingDialog_progress(context);
        dialog.openProgressDialog();
    }

    private int allFrame;

    public void newFunction() {
        new Thread(() -> {
            MediaInfo mInfo = new MediaInfo(videoPath);
            if (!mInfo.prepare() || !mInfo.isHaveVideo()) {
                return;
            }

            mExtractFrame = new ExtractVideoFrame(BaseApplication.getInstance(), videoPath);

            MediaMetadataRetriever retr = new MediaMetadataRetriever();
            retr.setDataSource(videoPath);
            String rotation = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            if (TextUtils.isEmpty(rotation)) {
                if (mInfo.vWidth * mInfo.vHeight > 960 * 540) {
                    mExtractFrame.setBitmapWH(mInfo.vWidth / 2, mInfo.vHeight / 2);
                }
            } else {
                int Irotation = Integer.parseInt(rotation);
                if (Irotation == 90 || Irotation == 180) {
                    if (mInfo.vWidth * mInfo.vHeight > 960 * 540) {
                        mExtractFrame.setBitmapWH(mInfo.vHeight / 2, mInfo.vWidth / 2);
                    }
                } else {
                    if (mInfo.vWidth * mInfo.vHeight > 960 * 540) {
                        mExtractFrame.setBitmapWH(mInfo.vWidth / 2, mInfo.vHeight / 2);
                    }
                }
            }

            allFrame = mInfo.vTotalFrames;
            LogUtil.d("OOM2", "视频的总帧数为" + allFrame);
            //设置提取多少帧
            mExtractFrame.setExtractSomeFrame(allFrame);
            /**
             * 设置处理完成监听.
             */
            mExtractFrame.setOnExtractCompletedListener(new onExtractVideoFrameCompletedListener() {

                @Override
                public void onCompleted(ExtractVideoFrame v) {
                    LogUtil.d("OOM", "onCompleted");
//                test();
                    for (int i = 1; i < BaseConstans.THREADCOUNT; i++) {
                        LogUtil.d("OOM2", "补了" + i + "帧");
                        //最后需要补的帧
                        frameCount++;
                        downImageForBitmap(null, frameCount);
                    }
                    LogUtil.d("OOM2", "frameCount的值为" + frameCount);

                    SegJni.nativeReleaseImageBuffer();
                    SegJni.nativeReleaseSegHandler();
                    addFrameCompoundVideo();
                }
            });
            /**
             * 设置处理进度监听.
             */
            mExtractFrame.setOnExtractProgressListener(new onExtractVideoFrameProgressListener() {

                /**
                 * 当前帧的画面回调,, ptsUS:当前帧的时间戳,单位微秒. 拿到图片后,建议放到ArrayList中,
                 * 不要直接在这里处理.
                 */
                @Override
                public void onExtractBitmap(Bitmap bmp, long ptsUS) {
                    frameCount++;
                    new Handler().post(() -> {
                        int progress = (int) ((frameCount / (float) allFrame) * 100);
                        dialog.setProgress("抠图进度为" + progress + "%");
                    });
                    String hint = frameCount + "帧" + "\n"
                            + "s是:" + String.valueOf(ptsUS);
                    LogUtil.d("OOM", hint);
//                    String fileName = faceFolder + File.separator + frameCount + ".png";
//                    BitmapManager.getInstance().saveBitmapToPath(bmp, fileName);
                    //todo  假如face sdk 抠图的速度和截取帧的速度大抵相同，那么就可以直接抠图，否则的话可能会造成内存回收不及时
                    downImageForBitmap(bmp, frameCount);
//                    LogUtil.d("OOM2", "正在扣"+frameCount+"帧");
//                LogUtil.d("OOM", "bmp.width=" + bmp.getWidth() + "bmp.height=" + bmp.getHeight() + "config=" + bmp.getConfig());
//                GlideBitmapPool.putBitmap(bmp);
                }
            });
            frameCount = 0;
            /**
             * 开始执行. 或者你可以从指定地方开始解码.
             * mExtractFrame.start(10*1000*1000);则从视频的10秒处开始提取.
             */
            mExtractFrame.start();
        }).start();


    }


    private int nowChooseImageIndex = 0;
    private float preTime;
    //当前进度时间
    private float nowProgressTime;
    boolean addFirstFrame = false;

    public void addFrameCompoundVideo() {
        List<File> getMattingList = FileManager.listFileSortByModifyTime(faceMattingFolder);
        LogUtil.d("OOM2", "得到所有增有" + getMattingList.size());

//        //蓝松截取帧会少一帧 //todo
//        File file=getMattingList.get(0);
//        String path=file.getParent()+"/aa.png";
//        try {
//            FileUtil.copyFile(file, path);
//            getMattingList.add(0,new File(path));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        Bitmap firstBitmap = BitmapFactory.decodeFile(getMattingList.get(0).getPath());
        LogUtil.d("OOM2", "第一针的地址为" + getMattingList.get(0).getPath());
        long AllTime = videoInfo.getDuration() * 1000;
        preTime = AllTime / (float) getMattingList.size();
        nowProgressTime = preTime;
        LogUtil.d("OOM2", "添加后数量" + getMattingList.size());
        try {
            DrawPadAllExecute2 execute = new DrawPadAllExecute2(BaseApplication.getInstance(), DRAWPADWIDTH, DRAWPADHEIGHT, AllTime);
            execute.setFrameRate(FRAME_RATE);
            execute.setEncodeBitrate(5 * 1024 * 1024);
            execute.setOnLanSongSDKErrorListener(message -> {
                LogUtil.d("OOM", "错误信息为" + message);
            });

            execute.setOnLanSongSDKProgressListener((l, i) -> {
                dialog.setProgress("最后渲染的进度为" + i);
                LogUtil.d("OOM", "进度为");
            });
            execute.setOnLanSongSDKCompletedListener(exportPath -> {
                LogUtil.d("OOM", "nowChooseImageIndex" + nowChooseImageIndex);
                dialog.closePragressDialog();
                String albumPath = cacheCutVideoPath + "/Matting.mp4";

                try {
                    FileUtil.copyFile(new File(exportPath), albumPath);
                    if (callback != null) {
                        callback.isSuccess(true, albumPath);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //todo 需要移除全部的子图层
                execute.release();
            });

            LSOBitmapAsset asset = new LSOBitmapAsset(firstBitmap);
            BitmapLayer bitmapLayerForDrawBackground = execute.addBitmapLayer(asset);
            bitmapLayerForDrawBackground.setScaledToPadSize();
            CanvasLayer canvasLayer = execute.addCanvasLayer();
            canvasLayer.addCanvasRunnable((canvasLayer1, canvas, currentTime) -> {

//                if(nowChooseImageIndex==0){
//
//                    Bitmap firstBitmap1 = BitmapFactory.decodeFile(getMattingList.get(0).getPath());
//                    bitmapLayerForDrawBackground.switchBitmap(firstBitmap1);
//                }


                if (currentTime > nowProgressTime) {
                    //需要切换新的图了
                    nowChooseImageIndex++;
                    if (nowChooseImageIndex < getMattingList.size()) {
                        LogUtil.d("CanvasRunnable", "addCanvasRunnable=" + preTime + "currentTime=" + currentTime + "nowChooseImageIndex=" + nowChooseImageIndex);
                        nowProgressTime = preTime + nowProgressTime;
                        Bitmap firstBitmap1 = BitmapFactory.decodeFile(getMattingList.get(nowChooseImageIndex).getPath());
                        bitmapLayerForDrawBackground.switchBitmap(firstBitmap1);
                    }
                }
            });

            execute.start();
        } catch (Exception e) {
            LogUtil.d("OOM", e.getMessage());
            e.printStackTrace();
        }
    }







        int nowChooseIndex;

    public void addLansongCompoundVideo(String videoPath) {


        long AllTime = videoInfo.getDuration() * 1000;
        try {
            DrawPadAllExecute2 execute = new DrawPadAllExecute2(BaseApplication.getInstance(), DRAWPADWIDTH, DRAWPADHEIGHT, AllTime);
            execute.setFrameRate(FRAME_RATE);
            execute.setEncodeBitrate(5 * 1024 * 1024);
            execute.setOnLanSongSDKErrorListener(message -> {
                LogUtil.d("OOM", "错误信息为" + message);
            });

            execute.setOnLanSongSDKProgressListener((l, i) -> {
                dialog.setProgress("最后渲染的进度为" + i);
                LogUtil.d("OOM", "进度为");
            });
            execute.setOnLanSongSDKCompletedListener(exportPath -> {
                LogUtil.d("OOM", "nowChooseImageIndex" + nowChooseImageIndex);
                dialog.closePragressDialog();
                String albumPath = cacheCutVideoPath + "/Matting.mp4";

                try {
                    FileUtil.copyFile(new File(exportPath), albumPath);
                    if (callback != null) {
                        callback.isSuccess(true, albumPath);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //todo 需要移除全部的子图层
                execute.release();
            });

//            LSOVideoOption option = new LSOVideoOption(videoPath);
//            option.setOnCustomFrameOutListener(new OnCustomFrameOutListener() {
//                @Override
//                public int onCustomFrameOut(ByteBuffer srcBuffer, ByteBuffer dstBuffer, int previewWidth, int previewHeight) {
//
//                    // srcBuffer是我们内部当前解码好的RGBA数据, dstBuffer是你们处理 后的数组,这个数据如果是灰度,则返回Mask 如果是rgba则返回RGBA,如果是错误,则返回ERROR;
//                    Log.e("LSDelete", "--------preview width: " +previewWidth + " x "+ previewHeight);
//
//
//                   arrayCopy(srcBuffer.array(), 0,dstBuffer.array(),0,dstBuffer.array().length);
//                    return OnCustomFrameOutListener.CUSTOM_FRAME_RETURN_RGBA;
//                }
//            });
//


            LSOVideoOption option = new LSOVideoOption(videoPath);
            option.setOnCustomFrameOutListener(new OnCustomFrameOutListener() {
                @Override
                public int onCustomFrameOut(ByteBuffer srcBuffer, ByteBuffer dstBuffer, int previewWidth, int previewHeight) {
                    // srcBuffer是我们内部当前解码好的RGBA数据, dstBuffer是你们处理 后的数组,这个数据如果是灰度,
                    // 则返回Mask 如果是rgba则返回RGBA,如果是错误,则返回ERROR;
                    LogUtil.d("OOM","nowChooseIndex="+nowChooseIndex);
                    byte[] bs = new byte[srcBuffer.capacity()];
                    byte[] getData = mattingImage.test(nowChooseIndex, videoInfo.getVideoWidth(), videoInfo.getVideoHeight(), bs);
                    LogUtil.d("OOM","抠图完成");
                    arrayCopy(getData, 0,dstBuffer.array(),0,dstBuffer.array().length);
//                    dstBuffer.put(getData);
                    nowChooseIndex++;
                    return OnCustomFrameOutListener.CUSTOM_FRAME_RETURN_MASK;
                }
            });
            execute.addVideoLayer(option);
            execute.start();
        } catch (Exception e) {
            LogUtil.d("OOM", e.getMessage());
            e.printStackTrace();
        }
    }



    public static void arrayCopy(Object src, int srcPos, Object dest, int destPos,int length) {
        try {
            System.arraycopy(src, srcPos, dest, destPos, length);
        }catch (IndexOutOfBoundsException e){
            LSOLog.e("memory error  IndexOutOfBoundsException ",e);
        }catch (ArrayStoreException e){
            LSOLog.e("memory error  ArrayStoreException ",e);
        }

    }


    private void showKeepSuccessDialog(String path) {
//        DataCleanManager.deleteFilesByDirectory(BaseApplication.getInstance().getExternalFilesDir("faceFolder"));
//        DataCleanManager.deleteFilesByDirectory(BaseApplication.getInstance().getExternalFilesDir("faceMattingFolder"));

        if (!DoubleClick.getInstance().isFastDoubleClick()) {
            AlertDialog.Builder builder = new AlertDialog.Builder( //去除黑边
                    new ContextThemeWrapper(context, R.style.Theme_Transparent));
            builder.setTitle(R.string.notification);
            builder.setMessage(context.getString(R.string.have_saved_to_sdcard) +
                    "【" + path + context.getString(R.string.folder) + "】");
            builder.setNegativeButton(context.getString(R.string.got_it), (dialog, which) -> dialog.dismiss());
            builder.setCancelable(true);
            Dialog dialog = builder.show();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
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


    private MattingImage mattingImage = new MattingImage();
    private List<File> getFilesAllName;

    public void test() {
        getFilesAllName = FileManager.listFileSortByModifyTime(faceFolder);
        downImageForPath(getFilesAllName.get(0).getPath());
    }


    private int downSuccessNum;

    public void downImageForPath(String path) {
        mattingImage.mattingImage(path, new MattingImage.mattingStatus() {
            @Override
            public void isSuccess(boolean isSuccess, Bitmap bp) {
                downSuccessNum++;
                if (getFilesAllName.size() == downSuccessNum) {
                    Observable.just(1).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
                        @Override
                        public void call(Integer integer) {
                            new Handler().post(() -> dialog.setProgress("完成抠图"));
                        }
                    });
                    addFrameCompoundVideo();
                } else {

                    Observable.just(1).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
                        @Override
                        public void call(Integer integer) {
                            new Handler().post(() -> dialog.setProgress("正在抠图" + downSuccessNum));
                        }
                    });

                    LogUtil.d("OOM", "正在抠图" + downSuccessNum);
                    String fileName = faceMattingFolder + File.separator + downSuccessNum + ".png";
                    BitmapManager.getInstance().saveBitmapToPath(bp, fileName);
                    GlideBitmapPool.putBitmap(bp);
                    downImageForPath(getFilesAllName.get(downSuccessNum).getPath());
                }
            }
        });
    }


    private ArrayList<Bitmap> listCatchBitmap = new ArrayList<>();

    private void downImageForBitmap(Bitmap OriginBitmap, int frameCount) {
//        mattingImage.mattingImage(OriginBitmap, (isSuccess, bp1) -> {
//            downSuccessNum++;
//            LogUtil.d("OOM", "正在抠图" + downSuccessNum);
//            String fileName = faceMattingFolder + File.separator + frameCount + ".png";
//            BitmapManager.getInstance().saveBitmapToPath(bp1, fileName, isSuccess1 -> GlideBitmapPool.putBitmap(
//                    bp1));
//            GlideBitmapPool.putBitmap(OriginBitmap);
//            if (allFrame - 1 == downSuccessNum) {
//                addFrameCompoundVideo();
//            }
//        });


        downSuccessNum++;
        String fileName = faceMattingFolder + File.separator + frameCount + ".png";
        LogUtil.d("OOM", "正在抠图" + downSuccessNum);
        mattingImage.mattingImageForMultiple(OriginBitmap, frameCount, new MattingImage.mattingStatus() {
            @Override
            public void isSuccess(boolean isSuccess, Bitmap bitmap) {
                if (isSuccess) {
                    BitmapManager.getInstance().saveBitmapToPath(bitmap, fileName, isSuccess1 -> GlideBitmapPool.putBitmap(
                            bitmap));
                } else {
                    LogUtil.d("OOM", "bitmap=null");
                }

            }

        });

        LogUtil.d("OOM", "allFrame-1=" + allFrame + "downSuccessNum=?" + downSuccessNum);
    }


    public Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }


    public Bitmap rawByteArray2RGBABitmap2(byte[] data, int width, int height) {
        int frameSize = width * height;
        int[] rgba = new int[frameSize];

        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                int y = (0xff & ((int) data[i * width + j]));
                int u = (0xff & ((int) data[frameSize + (i >> 1) * width + (j & ~1) + 0]));
                int v = (0xff & ((int) data[frameSize + (i >> 1) * width + (j & ~1) + 1]));
                y = y < 16 ? 16 : y;

                int r = Math.round(1.164f * (y - 16) + 1.596f * (v - 128));
                int g = Math.round(1.164f * (y - 16) - 0.813f * (v - 128) - 0.391f * (u - 128));
                int b = Math.round(1.164f * (y - 16) + 2.018f * (u - 128));

                r = r < 0 ? 0 : (r > 255 ? 255 : r);
                g = g < 0 ? 0 : (g > 255 ? 255 : g);
                b = b < 0 ? 0 : (b > 255 ? 255 : b);

                rgba[i * width + j] = 0xff000000 + (b << 16) + (g << 8) + r;
            }

        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmp.setPixels(rgba, 0, width, 0, 0, width, height);
        return bmp;
    }


    private void createFileWithByte(byte[] bytes, File fileName) {
        // 创建FileOutputStream对象
        FileOutputStream outputStream = null;
        // 创建BufferedOutputStream对象
        BufferedOutputStream bufferedOutputStream = null;
        try {
            // 如果文件存在则删除
            if (fileName.exists()) {
                fileName.delete();
            }
            // 获取FileOutputStream对象
            outputStream = new FileOutputStream(fileName);
            // 获取BufferedOutputStream对象
            bufferedOutputStream = new BufferedOutputStream(outputStream);
            // 往文件所在的缓冲输出流中写byte数据
            bufferedOutputStream.write(bytes);
            // 刷出缓冲输出流，该步很关键，要是不执行flush()方法，那么文件的内容是空的。
            bufferedOutputStream.flush();
        } catch (Exception e) {
            // 打印异常信息
            e.printStackTrace();
        } finally {
            // 关闭创建的流对象
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    /**
     * 将图片写入到磁盘
     *
     * @param img      图片数据流
     * @param fileName 文件保存时的名称
     */
    public static void writeImageToDisk(byte[] img, String fileName) {
        try {
            File file = new File(fileName);
            FileOutputStream fops = new FileOutputStream(file);
            fops.write(img);
            fops.flush();
            fops.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void downImageForByte(byte[] byteData, String path) {

        Observable.just(byteData).map(new Func1<byte[], String>() {
            @Override
            public String call(byte[] bytes) {
                return null;
            }
        }).subscribeOn(Schedulers.io()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                FileManager.saveBitmapToPath(convertStringToIcon(s), path, null);
            }
        });


    }

    /**
     * string转成bitmap
     *
     * @param st
     */
    public static Bitmap convertStringToIcon(String st) {
        // OutputStream out;
        Bitmap bitmap = null;
        try {
            // out = new FileOutputStream("/sdcard/aa.jpg");
            byte[] bitmapArray;
            bitmapArray = Base64.decode(st, Base64.DEFAULT);
            bitmap =
                    BitmapFactory.decodeByteArray(bitmapArray, 0,
                            bitmapArray.length);
            // bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }


    public interface MattingSuccess {
        void isSuccess(boolean isSuccess, String path);
    }


}
















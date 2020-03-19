package com.flyingeffects.com.manager;


import android.content.Context;

import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.enity.DownImg;
import com.flyingeffects.com.enity.DownImgDataList;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.updateFileUtils;
import com.google.gson.Gson;
import com.shixing.sxve.ui.view.WaitingDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * 压缩抠图一并提出来
 *
 */
public class CompressionCuttingManage {
    /**
     * 原图片地址
     */
    private List<String> localImagePaths;
    private List<String> allCompressPaths = new ArrayList<>();
    private String mCatchFolder;
    private String mTailtoFolder;
    private FileManager fileManager;
    private Context context;
    private int nowCompressSuccessNum;
    private ArrayList<String> listForMatting = new ArrayList<>();
    private imgListCallback callback;


    public CompressionCuttingManage(Context context,imgListCallback callback) {
        this.context = context;
        this.callback=callback;
        fileManager = new FileManager();
        mCatchFolder = fileManager.getCachePath(context);
        mTailtoFolder = fileManager.getFileCachePath(context, "tailor");
    }


    public void CompressImgAndCache(List<String> paths) {
        //todo 暂时只针对一张图片的时候
        if (paths != null && paths.size() == 1) {
            String localCacheName = paths.get(0);
            localCacheName = fileManager.getFileNameWithSuffix(localCacheName);
            File file = new File(mTailtoFolder + "/" + localCacheName);
            if (file.exists()) {
                List<String> list = new ArrayList<>();
                list.add(file.getPath());
                callback.imgList(list);
                return;
            }
        }

        //正常压缩下载逻辑
        toCompressImg(paths);
    }


    private void toCompressImg(List<String> paths) {
        if (paths != null) {
            localImagePaths = paths;
            int nowChoosePathNum = paths.size();
            nowCompressSuccessNum = 0;
            Luban.with(context)
                    .load(paths)                                   // 传人要压缩的图片列表
//                    .ignoreBy(100)                                  // 忽略不压缩图片的大小
                    .setTargetDir(mCatchFolder)                        // 设置压缩后文件存储位置
                    .setCompressListener(new OnCompressListener() { //设置回调
                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onSuccess(File file) {
                            nowCompressSuccessNum++;
                            LogUtil.d("OOM", "onSuccess=" + file.getPath());
                            //全部图片压缩完成
                            if (nowCompressSuccessNum == nowChoosePathNum) {
                                //todo 这里会出现一个bug ,设置了mCatchFolder ，但是裁剪后不会进入到里面去
                                if (nowChoosePathNum == 1) {
                                    allCompressPaths.clear();
                                    allCompressPaths.add(file.getPath());
                                    upLoad(allCompressPaths);
                                } else {
                                    allCompressPaths = FileManager.getFilesAllName(file.getParent());
                                    upLoad(allCompressPaths);
                                }

                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            LogUtil.d("OOM", "onError=" + e.getMessage());
                        }
                    }).launch();    //启动压缩
        }
    }

    private void upLoad(List<String> list) {
        String alert="正在抠图中"+"\n"+"上传人物最佳";
        WaitingDialog.openPragressDialog(context,alert);
        listForMatting.clear();
        List<File> listFile = new ArrayList<>();
        for (String str : list
        ) {
            File file = new File(str);
            listFile.add(file);
        }

        int pathNum = list.size();
        LogUtil.d("OOM", "pathNum=" + pathNum);
        updateFileUtils.uploadFile(listFile, "http://flying.nineton.cn/api/picture/picturesHumanList?filenum=" + pathNum, (code, str) -> {
            //   LogUtil.d("OOM", "uploadFileCallBack=" + str);
            WaitingDialog.closePragressDialog();
            Gson gson = new Gson();
            DownImg downIng = gson.fromJson(str, DownImg.class);
        if(downIng!=null&&downIng.getCode()==1){
                //成功
                ArrayList<DownImgDataList> data = downIng.getData();
                for (DownImgDataList item : data
                ) {
                    listForMatting.add(item.getTarget_url());
                }

                //马卡龙，这里是图片链接，下载下来的方式
                if (data.get(0).getType() == 1) {
                    DownImageManager downImageManager = new DownImageManager(BaseApplication.getInstance(), listForMatting, path -> {
                        callback.imgList(path);
                        keepTailorImageToCache(path);
                    });
                    downImageManager.downImage(listForMatting.get(0));
                } else {
                    //百度，face++ 是直接下载的图片编码
                    DownImageManager downImageManager = new DownImageManager(BaseApplication.getInstance(), listForMatting, path -> {
                        callback.imgList(path);
                        keepTailorImageToCache(path);
                    });
                    downImageManager.downImageForByte(listForMatting.get(0));
                }

            }else{
                //失败
                WaitingDialog.closePragressDialog();
                callback.imgList(localImagePaths);
            }
        });
    }


    /**
     * 缓存图片到本地
     * @param paths  下载后的地址列表
     */
    private void keepTailorImageToCache(List<String> paths) {
        for(int i=0;i<paths.size();i++){
            String localCacheName=localImagePaths.get(i);
            File file=new File(paths.get(i));
            FileManager manager=new FileManager();
            localCacheName= manager.getFileNameWithSuffix(localCacheName);
            if(mTailtoFolder!=null){
                File   mTailto=new File(mTailtoFolder,localCacheName);
                manager.mCopyFile(file,mTailto);
            }
        }
    }



    public interface  imgListCallback{
        void imgList(List<String>paths);
    }


}

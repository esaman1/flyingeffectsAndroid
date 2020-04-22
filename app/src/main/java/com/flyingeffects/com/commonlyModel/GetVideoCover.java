package com.flyingeffects.com.commonlyModel;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

import com.flyingeffects.com.manager.BitmapManager;
import com.flyingeffects.com.manager.CompressionCuttingManage;
import com.flyingeffects.com.manager.FileManager;
import com.glidebitmappool.GlideBitmapPool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * description ：获得视频封面，单独提出来，全部
 * creation date: 2020/4/22
 * user : zhangtongju
 */

public class GetVideoCover {

    private Context context;
    private String mVideoFolder;

    public GetVideoCover(Context context) {
        this.context = context;
        FileManager  fileManager = new FileManager();
        mVideoFolder = fileManager.getFileCachePath(context, "runCatch");
    }



    public void getCover(String originalPath,getCoverSuccess callback){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(originalPath);
        Bitmap mBitmap = retriever.getFrameAtTime(0);
        String fileName = mVideoFolder + File.separator + UUID.randomUUID() + ".png";
        BitmapManager.getInstance().saveBitmapToPath(mBitmap, fileName, isSuccess -> {
            CompressionCuttingManage manage = new CompressionCuttingManage(context, "", false, tailorPaths -> {
                callback.getCoverPath(tailorPaths.get(0));
            });
            List mattingPath=new ArrayList();
            mattingPath.add(fileName);
            manage.ToMatting(mattingPath);
            GlideBitmapPool.putBitmap(mBitmap);
        });
    }


  public   interface  getCoverSuccess{
        void getCoverPath(String path);

    }





}

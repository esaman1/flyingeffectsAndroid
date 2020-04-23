package com.flyingeffects.com.commonlyModel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import com.flyingeffects.com.manager.BitmapManager;
import com.flyingeffects.com.manager.CompressionCuttingManage;
import com.flyingeffects.com.manager.FileManager;
import com.glidebitmappool.GlideBitmapPool;
import com.shixing.sxve.ui.albumType;

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
        FileManager fileManager = new FileManager();
        mVideoFolder = fileManager.getFileCachePath(context, "runCatch");
    }



    /**
     * description ：得到视频的封面，是扣完图的封面
     * creation date: 2020/4/23
     * param :
     * user : zhangtongju
     */
    public void getCover(String originalPath, getCoverSuccess callback) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(originalPath);
        Bitmap mBitmap = retriever.getFrameAtTime(0);
        String fileName = mVideoFolder + File.separator + UUID.randomUUID() + ".png";
        BitmapManager.getInstance().saveBitmapToPath(mBitmap, fileName, isSuccess -> {
            CompressionCuttingManage manage = new CompressionCuttingManage(context, "", false, tailorPaths -> {
                callback.getCoverPath(tailorPaths.get(0));
            });
            List mattingPath = new ArrayList();
            mattingPath.add(fileName);
            manage.ToMatting(mattingPath);
            GlideBitmapPool.putBitmap(mBitmap);
        });
    }




    /**
     * description ：得到视频封面，没有抠图的封面，返回的时bitmap
     * creation date: 2020/4/23
     * param :
     * user : zhangtongju
     */
    public void getFileCoverForBitmap(String path,getCoverSuccessForBitmap callback) {
        if (albumType.isVideo(GetPathType.getInstance().getPathType(path))) {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(path);
            callback.getCoverBitmap(retriever.getFrameAtTime(0));
        } else {
            callback.getCoverBitmap(BitmapFactory.decodeFile(path));
        }
    }


    public interface getCoverSuccess {
        void getCoverPath(String path);
    }


    public interface getCoverSuccessForBitmap {
        void getCoverBitmap(Bitmap path);
    }

}

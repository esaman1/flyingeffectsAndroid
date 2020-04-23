package com.flyingeffects.com.manager;


import android.content.Context;

import com.coremedia.iso.boxes.Container;
import com.flyingeffects.com.R;
import com.flyingeffects.com.utils.ToastUtil;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;

import java.io.File;
import java.io.FileOutputStream;

/**
 * description ：媒体类管理
 * creation date: 2020/4/23
 * user : zhangtongju
 */
public class mediaManager {

    private Context context;
    public mediaManager(Context context){
        this.context=context;
    }





    /**
     * description ：分离音视频
     * creation date: 2020/4/23
     * param : mp4Path视频地址， outPath 分离出来的地址  callback 成功回调
     * user : zhangtongju
     */
    public void splitMp4(String mp4Path, File outPath ,splitMp4Callback callback){
        try {
            Movie videoMovie = MovieCreator.build(mp4Path);
//        Track videoTracks = null;// 获取视频的单纯视频部分
            Track sounTracks = null;//获取音频的不跟
            for (Track videoMovieTrack : videoMovie.getTracks()) {
//            if ("vide".equals(videoMovieTrack.getHandler())) {
//                videoTracks = videoMovieTrack;
//            }
                if ("soun".equals(videoMovieTrack.getHandler())) {
                    sounTracks = videoMovieTrack;
                }
            }

            if(sounTracks!=null){
                Movie resultMovie = new Movie();
                resultMovie.addTrack(sounTracks);// 视频部分
                Container out = new DefaultMp4Builder().build(resultMovie);
                if (!outPath.exists()) {
                    outPath.mkdirs();
                }
                File file = new File(outPath.getPath(), "bgm.mp3"); //创建文件的方法，而不是文件夹
                FileOutputStream fos = new FileOutputStream(file);
                out.writeContainer(fos.getChannel());
                fos.close();
                callback.splitSuccess(true,file.getPath());
            }else{
                callback.splitSuccess(false,"");
                ToastUtil.showToast(context.getString(R.string.no_audio_files));
            }
        }catch (Exception e) {
            callback.splitSuccess(false,"");
            ToastUtil.showToast(context.getString(R.string.no_audio_files));
        }

    }



    public interface splitMp4Callback{
        void splitSuccess(boolean isSuccess,String putPath);
    }




}

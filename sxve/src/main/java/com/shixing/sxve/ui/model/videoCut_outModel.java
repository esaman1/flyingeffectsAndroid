package com.shixing.sxve.ui.model;

import android.content.Context;
import android.graphics.Matrix;
import android.util.Log;

import com.shixing.sxvideoengine.SXCompositor;
import com.shixing.sxvideoengine.SXRenderListener;

import java.io.File;
import java.util.UUID;


/**
 * description ：视频裁剪类
 * date: ：2019/5/13 17:30
 * author: 张同举 @邮箱 jutongzhang@sina.com
 */
public class videoCut_outModel {

    private Context context;

    public videoCut_outModel(Context context) {
        this.context = context;
    }


    public void Cut_outVideo(String mVideoPath, int mTemplateWidth, int mTemplateHeight, float mTemplateDuration, Matrix matrix, float startTime,int fps, final ShowCut_outPath showCut_outPath) {
        Log.d("OOM","截取的信息是"+"mVideoPath="+mVideoPath+",mTemplateWidth="+mTemplateWidth+"mTemplateHeight="+mTemplateHeight+"mTemplateDuration="+mTemplateDuration+"startTime="+startTime+"fps="+fps);

        final String outputPath = getOutputPath();
        File file=new File(mVideoPath);
        if(file.exists()){
            SXCompositor formatter;
            if (mTemplateWidth != 0 && mTemplateHeight != 0) {
                formatter = new SXCompositor(mVideoPath, outputPath, matrix, false);  //对视频进行截取 ztj
                formatter.setWidth(mTemplateWidth);
                formatter.setHeight(mTemplateHeight);
            } else {
                formatter = new SXCompositor(mVideoPath, outputPath, matrix, false);  //对视频进行截取 ztj
            }
            formatter.setBitrate(8388608);  //相当于设置了5m的比特率   8M为清晰  计算方式为 wide*height*fps*设置的BitRateFactory
            formatter.setFrameRate(fps);
            formatter.setStartTime(startTime);
            if (mTemplateDuration < 1) {
                mTemplateDuration = 1;
            }
            formatter.setDuration(mTemplateDuration);
            formatter.setRenderListener(new SXRenderListener() {
                @Override
                public void onStart() {
                    //    Log.d("start", "onStart: .");
//                Log.d("OOM","start");
                }

                @Override
                public void onUpdate(int progress) {
                    //     Log.d("start", "progress: .");
//                Log.d("OOM","progress"+progress);
                }

                @Override
                public void onFinish(boolean success, String msg) {
                    //       Log.d("start", "onFinish: .");
                    Log.d("OOM2", "onFinish");
                    showCut_outPath.cutOutPath(outputPath, success);
                }

                @Override
                public void onCancel() {
                    //         Log.d("start", "onCancel: .");
                }
            });
            formatter.run();
        }else{
            showCut_outPath.cutOutPath(outputPath, true);
        }




    }


    private String getOutputPath() {
        return context.getExternalCacheDir() + File.separator + UUID.randomUUID() + ".mp4";
    }


    public interface ShowCut_outPath {
        void cutOutPath(String path, boolean success);
    }

}

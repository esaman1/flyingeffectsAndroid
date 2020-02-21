package com.shixing.sxve.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.util.AttributeSet;

import com.glidebitmappool.GlideBitmapPool;
import com.shixing.sxve.ui.util.VEBitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ImageViewWithCustomBg extends android.support.v7.widget.AppCompatImageView {
    private Bitmap customBg;
    private Bitmap previewBg;
    private boolean hasBackground;
    public ImageViewWithCustomBg(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public void setCustomBg(Drawable resource){
        if (resource==null){
            hasBackground=false;
            if (customBg!=null){
                GlideBitmapPool.putBitmap(customBg);
                customBg=null;
            }
        }else {
            hasBackground=true;
            if (resource instanceof ColorDrawable){
                customBg= GlideBitmapPool.getBitmap(720,1280, Bitmap.Config.RGB_565);
            }else {
                if (resource.getIntrinsicWidth()>0&&resource.getIntrinsicHeight()>0)
                customBg= drawableToBitmap(resource);
            }

        }
        invalidate();
    }

    public void setCustomBg(Bitmap resource){
        if (resource==null){
            hasBackground=false;
            if (customBg!=null){
                GlideBitmapPool.putBitmap(customBg);
                customBg=null;
            }
        }else {
            hasBackground=true;
            customBg=resource;
        }
        invalidate();
    }


    public void setImage(String path){
        if (path==null||path.isEmpty()||path.contains("clear")){
            if (previewBg!=null){
                GlideBitmapPool.putBitmap(previewBg);
                previewBg=null;
            }
        }else if (path.contains("noChange")){
            return;
        }else {
            if (path.contains("mp4")){
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                FileInputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(new File(path).getAbsolutePath());
                    retriever.setDataSource(inputStream.getFD());//设置数据源为该文件对象指定的绝对路径
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                previewBg = retriever.getFrameAtTime(); //获得第一针图片
            }else {
                previewBg= VEBitmapFactory.decodeFile(path,720,1280);
            }
        }
        invalidate();
    }


    Matrix bgMatrix;
    Matrix previewMatrix;
    @Override
    protected void onDraw(Canvas canvas) {
        if (customBg!=null&&hasBackground){
            bgMatrix=new Matrix();
            float bgScale=1f*getWidth()/customBg.getWidth();
            bgMatrix.postScale(bgScale,bgScale);
            canvas.drawBitmap(customBg,bgMatrix,null);
        }
        if (previewBg!=null){
            previewMatrix=new Matrix();
            float imgScale=1f*getWidth()/previewBg.getWidth();
            previewMatrix.postScale(imgScale,imgScale);
            float heightOffset=getHeight()*0.5f-previewBg.getHeight()*imgScale*0.5f;
            previewMatrix.postTranslate(0,heightOffset);
            canvas.drawBitmap(previewBg,previewMatrix,null);
        }
    }
    private Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }
}

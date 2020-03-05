package com.flyingeffects.com.manager;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 图片下载公共类
 */
public class DownImageManager {

    private Context context;
    private ArrayList<String> listForMatting;
    private List<String> hasDownList = new ArrayList<>();
    private int downSuccessNum;
    private keepImageToLocalState callback;
    private File keepUunCatchPath;

    public DownImageManager(Context context, ArrayList<String> listForMatting, keepImageToLocalState callback) {
        this.context = context;
        this.listForMatting = listForMatting;
        this.callback = callback;
        downSuccessNum = 0;
        hasDownList.clear();
        keepUunCatchPath = context.getExternalFilesDir("runCatch/");
    }


    public void downImage(String path) {
        Observable.just(path).map(new Func1<String, File>() {
            @Override
            public File call(String s) {
                File file = null;
                try {
                    file = Glide.with(context)
                            .load(s)
                            .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return file;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread()).subscribe(new Action1<File>() {
            @Override
            public void call(File file) {
                downSuccessNum++;
                hasDownList.add(file.getPath());
                if (hasDownList.size() == listForMatting.size()) {
                    callback.isSuccess(hasDownList);
                } else {
                    downImage(listForMatting.get(downSuccessNum));
                }
            }
        });

    }


    public void downImageForByte(String path) {
        Observable.just(path).map(new Func1<String, File>() {
            @Override
            public File call(String s) {
                File file = null;
                Bitmap bp = convertStringToIcon(s);
                String path = keepUunCatchPath + File.separator + UUID.randomUUID() + ".png";
                FileManager.saveBitmapToPath(bp, path);
                file = new File(path);
                return file;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread()).subscribe(new Action1<File>() {
            @Override
            public void call(File file) {
                downSuccessNum++;
                hasDownList.add(file.getPath());
                if (hasDownList.size() == listForMatting.size()) {
                  callback.isSuccess(hasDownList);
                } else {
                    downImageForByte(listForMatting.get(downSuccessNum));
                }
            }
        });

    }






    /**
     * string转成bitmap
     *
     * @param st
     */
    public static Bitmap convertStringToIcon(String st)
    {
        // OutputStream out;
        Bitmap bitmap = null;
        try
        {
            // out = new FileOutputStream("/sdcard/aa.jpg");
            byte[] bitmapArray;
            bitmapArray = Base64.decode(st, Base64.DEFAULT);
            bitmap =
                    BitmapFactory.decodeByteArray(bitmapArray, 0,
                            bitmapArray.length);
            // bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return bitmap;
        }
        catch (Exception e)
        {
            return null;
        }
    }


    public interface keepImageToLocalState {
        void isSuccess(List<String> path);
    }


}

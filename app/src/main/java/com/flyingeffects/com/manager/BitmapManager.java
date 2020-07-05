package com.flyingeffects.com.manager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapManager {


    private static BitmapManager thisModel;

    public static BitmapManager getInstance() {

        if (thisModel == null) {
            thisModel = new BitmapManager();
        }
        return thisModel;

    }


    /**
     * 获得视频的旋转角度
     */
    public boolean getSourceVideoDirection(String path) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();//实例化MediaMetadataRetriever对象
        File file = new File(path);//实例化File对象，文件路径为/storage/sdcard/Movies/music1.mp4
        if (file.exists() && file.length() > 0) {
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(new File(path).getAbsolutePath());
                mmr.setDataSource(inputStream.getFD());//设置数据源为该文件对象指定的绝对路径
                int videoRotation = 0;
                try {
                    videoRotation = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
                    return videoRotation != 90 && videoRotation != 270;

                } catch (Exception e) {
                    Log.d("Exception", e.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return true;
    }


    /**
     * 获得图片的选旋转角度
     */
    public boolean getOrientation(String path) {
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            return orientation != 90 && orientation != 270;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


    public Bitmap saveBitmapToPath(Bitmap bitmap, String path) {
        if (!path.endsWith(".png") && !path.endsWith(".PNG")) {
            throw new IllegalArgumentException();
        }

        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return bitmap;
    }


    public Bitmap saveBitmapToPathForJpg(Bitmap bitmap, String path) {


        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return bitmap;
    }






    public Bitmap saveBitmapToPath(Bitmap bitmap, String path, saveToFileCallback callback) {
        if (!path.endsWith(".png") && !path.endsWith(".PNG")) {
            throw new IllegalArgumentException();
        }

        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            callback.isSuccess(true);
        } catch (FileNotFoundException e) {
            callback.isSuccess(false);
            e.printStackTrace();
        } catch (IOException e) {
            callback.isSuccess(false);
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        }

        return bitmap;
    }


    public Bitmap saveBitmapToPathForJpg(Bitmap bitmap, String path, saveToFileCallback callback) {


        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
            out.flush();
            out.close();
            callback.isSuccess(true);
        } catch (FileNotFoundException e) {
            callback.isSuccess(false);
            e.printStackTrace();
        } catch (IOException e) {
            callback.isSuccess(false);
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        }

        return bitmap;
    }



    public interface saveToFileCallback {
        void isSuccess(boolean isSuccess);
    }

    /**
     * description ：获得bitmap 且修复了角度的
     * creation date: 2020/4/27
     * user : zhangtongju
     */
    public Bitmap getOrientationBitmap(String imagePath){
        Matrix mat = new Matrix();
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, new BitmapFactory.Options());
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(imagePath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    mat.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    mat.postRotate(180);
                    break;
            }
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  null;
    }



}

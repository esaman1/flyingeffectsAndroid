package com.flyingeffects.com.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;

import com.glidebitmappool.GlideBitmapPool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


/***
 * 用于打开本地图片时需要的工具
 * @author Zhangtongju
 *
 */


public class BitmapUtils {

    public static final Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            bitmap = GlideBitmapPool.getBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            drawable.draw(canvas);
        } else {
            bitmap = GlideBitmapPool.getBitmap(720, 1280, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.BLACK);
        }
        return bitmap;
    }

    /**
     * 根据资源id获得图片并压缩，返回bitmap用于显示
     */
    public static Bitmap getSmallBmpFromResource(Context context, int id, int targetW, int targetH) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), id, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, targetW, targetH);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(context.getResources(), id, options);
    }

    /**
     * 计算图片的缩放值
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }

            long totalPixels = width / inSampleSize * height / inSampleSize;

            final long totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels > totalReqPixelsCap) {
                inSampleSize *= 2;
                totalPixels /= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap getVideoThumbnail(ContentResolver cr, Uri uri) {

        Bitmap bitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inDither = false;

        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Cursor cursor = cr.query(uri, new String[]{MediaStore.Video.Media._ID}, null, null, null);


        if (cursor == null || cursor.getCount() == 0) {

            return null;

        }

        cursor.moveToFirst();

        String videoId = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID));  //image id in image table.s


        if (videoId == null) {

            return null;

        }

        cursor.close();

        long videoIdLong = Long.parseLong(videoId);

        bitmap = MediaStore.Video.Thumbnails.getThumbnail(cr, videoIdLong, MediaStore.Images.Thumbnails.MICRO_KIND, options);


        return bitmap;

    }


    /**
     * description ：以宽度为准，高度自适应
     * creation date: 2020/9/29
     * user : zhangtongju
     */
    public Bitmap zoomImg(Bitmap bitmap, int width, int height) {
        int bmpWidth = bitmap.getWidth();
        int bmpHeight = bitmap.getHeight();
        float scaleWidth = ((float) width) / bmpWidth;
        Bitmap target = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas temp_canvas = new Canvas(target);
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleWidth);
        int tranH = (int) (bmpHeight * scaleWidth - height);
        if (tranH > 0) {
            tranH = Math.abs(tranH) / 2;
            tranH = -tranH;

        } else {
            tranH = Math.abs(tranH) / 2;
        }
        matrix.postTranslate(0, tranH);
        temp_canvas.drawBitmap(bitmap, matrix, new Paint());
        return target;
    }


    /**
     * description ：以宽度，高度共同为准，填满屏幕的情况下拉伸裁剪
     * creation date: 2020/9/29
     * user : zhangtongju
     */
    public static Bitmap zoomImg2(Bitmap bitmap, int width, int height) {
        int bmpWidth = bitmap.getWidth();
        int bmpHeight = bitmap.getHeight();
        float needScale;
        float scaleWidth = ((float) width) / bmpWidth;
        float scaleHeight = ((float) height) / bmpHeight;

        needScale = Math.max(scaleWidth, scaleHeight);


        Bitmap target = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas temp_canvas = new Canvas(target);
        Matrix matrix = new Matrix();
        matrix.postScale(needScale, needScale);
        int tranH = (int) (bmpHeight * needScale - height);
        if (tranH > 0) {
            tranH = Math.abs(tranH) / 2;
            tranH = -tranH;
        } else {
            tranH = Math.abs(tranH) / 2;
        }

        int tranW = (int) (bmpWidth * needScale - width);
        if (tranW > 0) {
            tranW = Math.abs(tranW) / 2;
            tranW = -tranW;
        } else {
            tranW = Math.abs(tranW) / 2;
        }
        matrix.postTranslate(tranW, tranH);
        temp_canvas.drawBitmap(bitmap, matrix, new Paint());
        return target;
    }


    /**
     * description ：图片压缩 不能超过的大小
     * creation date: 2020/12/7
     * user : zhangtongju
     */
    public static Bitmap compressBitmap(Bitmap bitmap, long sizeLimit) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);

        // 循环判断压缩后图片是否超过限制大小
        while (baos.toByteArray().length / 1024 > sizeLimit) {
            // 清空baos
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            quality -= 10;
        }

        Bitmap newBitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()), null, null);

        return newBitmap;
    }


    public Bitmap mergeBitmap(Bitmap firstBitmap, Bitmap secondBitmap, int width, int height) {
        secondBitmap = zoomImg(secondBitmap, width, height);
        firstBitmap = zoomImg(firstBitmap, width, height);
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(firstBitmap, new Matrix(), null);
        canvas.drawBitmap(secondBitmap, 0, 0, null);
        return bitmap;
    }

    public Bitmap toHorizontalMirror(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        // 水平镜像翻转
        matrix.postScale(-1f, 1f);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

    public Bitmap toVerticalMirror(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        // 垂直镜像翻转
        matrix.postScale(1f, -1f);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

}

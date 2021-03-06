package com.flyingeffects.com.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Movie;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;

import com.flyingeffects.com.base.BaseApplication;

/**
 * @Author: savion
 * @Date: 2019/6/13 16:59
 * @Des:
 **/
public class BitmapUtil {
    public static Bitmap drawDissovleColorBitmap(int color, int viewWidth, int viewHeight, float x, float y, float rScale) {
        return drawDissovleBitmap(drawColorBitmap(color, viewWidth, viewHeight), viewWidth, viewHeight, x, y, rScale);
    }

    public static Bitmap drawColorBitmap(int color, int viewWidth, int viewHeight) {
        try {
            Bitmap bitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_4444);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(color);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    public static Bitmap drawDissovleGifBitmap(String path, int viewWidth, int viewHeight, float x, float y, float rScale) {
        return drawDissovleBitmap(drawGifFirstFrameBitmap(path, viewWidth, viewHeight), viewWidth, viewHeight, x, y, rScale);
    }

    public static Bitmap drawGifFirstFrameBitmap(String path, int width, int height) {
        Movie movie = Movie.decodeFile(path);
        Bitmap sourceBitmap = Bitmap.createBitmap(movie.width(), movie.height(), Bitmap.Config.ARGB_8888);
        Canvas movieCanvas = new Canvas(sourceBitmap);
        movieCanvas.drawColor(Color.BLACK);
        movie.setTime(0);
        movie.draw(movieCanvas, 0, 0);
        return sourceBitmap;
    }

    public static Bitmap drawDissovleBitmap(Bitmap path, int viewWidth, int viewHeight, float x, float y, float rScale) {
        if (path == null) {
            return null;
        }
        Paint paint2 = new Paint();
        Bitmap tempBitmap = Bitmap.createScaledBitmap(path, viewWidth, viewHeight, false);

        //???????????????
        int centerX = viewWidth / 2, centerY = viewHeight / 2;
        if (x >= 0 && y >= 0) {
            centerX = Math.round(x);
            centerY = Math.round(y);
        }
        int[] colors = {0x00000000, 0xffffffff};
        float[] stops = {0.6f, 1f};

        //????????????
        int radius = viewWidth / 4;
        radius *= rScale;

        //??????????????????
        RadialGradient rdg = new RadialGradient(centerX, centerY, radius, colors, stops, Shader.TileMode.MIRROR);
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
        paint.setFilterBitmap(true);
        paint.setShader(rdg);
        //?????????????????????????????????????????????
        Matrix matrix = new Matrix();
        Bitmap tempBitmap2 = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas1 = new Canvas(tempBitmap2);
        canvas1.drawBitmap(tempBitmap, matrix, paint2);
        canvas1.drawCircle(centerX, centerY, radius, paint);
        return tempBitmap2;
    }

    public static boolean recycleBitmap(Bitmap bitmap) {
        try {
            if (isBitmapEnable(bitmap)) {
                bitmap.recycle();
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isBitmapEnable(Bitmap bitmap) {
        return bitmap != null && !bitmap.isRecycled();
    }

    public static Bitmap getCurrentMaskBitmap(int maskWidth, int maskHeight, int vLayerWidth, int vLayerHeight, float vLeft, float vTop, int maskID) {
        try {
            Bitmap mask = BitmapFactory.decodeResource(BaseApplication.getInstance().getResources(), maskID);
            Bitmap scaledMask = Bitmap.createScaledBitmap(mask, maskWidth, maskHeight, false);
            mask.recycle();
            Bitmap result = Bitmap.createBitmap(vLayerWidth, vLayerHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            canvas.translate(0, 0);
            float left = vLeft;
            float top = vTop;
            canvas.drawBitmap(scaledMask, left, top, null);
            scaledMask.recycle();
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public static Bitmap getCurrentMaskBitmap(int maskID, int maskWidth, int maskHeight, int vLayerWidth, int vLayerHeight, float vLeft, float vTop, float ratio) {
        try {
            Bitmap mask = BitmapFactory.decodeResource(BaseApplication.getInstance().getResources(), maskID);
            Bitmap scaledMask = Bitmap.createScaledBitmap(mask, maskWidth, maskHeight, false);
            mask.recycle();
            Bitmap result = Bitmap.createBitmap(vLayerWidth, vLayerHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            canvas.translate(0, 0);
            canvas.rotate(ratio, (vLeft + maskWidth / 2), vTop + (maskHeight / 2));
            float left = vLeft;
            float top = vTop;
            canvas.drawBitmap(scaledMask, left, top, null);
            scaledMask.recycle();
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public static Bitmap getCurrentMaskBitmap(int maskWidth, int maskHeight, int vLayerWidth, int vLayerHeight, float vLeft, float vTop, String maskID) {
        try {
            Bitmap mask = BitmapFactory.decodeFile(maskID);
            Bitmap scaledMask = Bitmap.createScaledBitmap(mask, maskWidth, maskHeight, false);
            mask.recycle();
            Bitmap result = Bitmap.createBitmap(vLayerWidth, vLayerHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            canvas.translate(0, 0);
            float left = vLeft;
            float top = vTop;
            canvas.drawBitmap(scaledMask, left, top, null);
            scaledMask.recycle();
            return result;
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * @param context ???????????????
     * @param image   ?????????????????????
     * @return ??????????????????Bitmap
     */
    public static synchronized Bitmap blurBitmap(Context context, Bitmap image, float blurRadius) {

        // ????????????????????????????????????
        if (image == null) {
            return null;
        }
        int width = image.getWidth();
        int height = image.getHeight();
        Bitmap outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);

        // ??????RenderScript????????????
        RenderScript rs = RenderScript.create(context);
        // ???????????????????????????RenderScript???????????????
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        // ??????RenderScript???????????????VM???????????????,??????????????????Allocation?????????????????????????????????
        // ??????Allocation????????????????????????????????????,????????????copyTo()?????????????????????
        Allocation tmpIn = Allocation.createFromBitmap(rs, image);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);

        // ???????????????????????????, 25f??????????????????
        blurRadius = blurRadius > 25 ? 25f : blurRadius;
        blurRadius = blurRadius <= 0 ? 1 : blurRadius;
        blurScript.setRadius(blurRadius);
        // ??????blurScript?????????????????????
        blurScript.setInput(tmpIn);
        // ???????????????????????????????????????
        blurScript.forEach(tmpOut);

        // ??????????????????Allocation???
        tmpOut.copyTo(outputBitmap);

        rs.destroy();
        return outputBitmap;
    }

    /**
     * bitmap???????????????
     *
     * @param sourceImg ???bitmap
     * @param number    0-100
     * @return
     */
    public static Bitmap getTransparentBitmap(Bitmap sourceImg, int number) {
        int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];

        sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0, sourceImg

                .getWidth(), sourceImg.getHeight());// ???????????????ARGB???

        number = number * 255 / 100;

        for (int i = 0; i < argb.length; i++) {

            argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF);

        }

        sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(), sourceImg

                .getHeight(), Bitmap.Config.ARGB_8888);

        return sourceImg;
    }

    /**
     * ????????????????????????
     *
     * @param path
     * @param width
     * @param height
     * @return
     */
    public static Bitmap decodeAbsSizeBitmap(String path, int width, int height) {
        try {
            if (!TextUtils.isEmpty(path)) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path, options);
                int inWidth = options.outWidth;
                int inHeight = options.outHeight;
                if (inWidth > inHeight) {
                    //??????
                    float ratio = width * 1f / inWidth;
                    inWidth = width;
                    inHeight = (int) (inHeight * ratio);
                } else {
                    //??????
                    float ratio = height * 1f / inHeight;
                    inWidth = (int) (inWidth * ratio);
                    inHeight = height;
                }
                Bitmap res = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(path), inWidth, inHeight, false);
                return res;
            }
        } catch (Exception e) {
        }
        return null;
    }

    public static Bitmap decodeAutoSizeBitmap(int res) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(BaseApplication.getInstance().getResources(), res, options);
            if (options.outWidth * options.outHeight > 900f * 640f) {
                float ratio = Math.max(options.outWidth / 900f, options.outHeight / 900f);
                options.inJustDecodeBounds = false;
                options.inSampleSize = ratio > (int) ratio ? (int) (ratio + 1f) : (int) ratio;
                return BitmapFactory.decodeResource(BaseApplication.getInstance().getResources(), res, options);
            } else {
                options.inJustDecodeBounds = false;
                return BitmapFactory.decodeResource(BaseApplication.getInstance().getResources(), res, options);
            }
        } catch (Exception e) {
        }
        return null;
    }

    public static Bitmap decodeAutoSizeBitmap(String path) {
        try {
            if (!TextUtils.isEmpty(path)) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path, options);
                if (options.outWidth * options.outHeight > 900f * 640f) {
                    float ratio = Math.max(options.outWidth / 900f, options.outHeight / 900f);
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = ratio > (int) ratio ? (int) (ratio + 1f) : (int) ratio;
                    return BitmapFactory.decodeFile(path, options);
                } else {
                    options.inJustDecodeBounds = false;
                    return BitmapFactory.decodeFile(path, options);
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    public static Bitmap drawAutoSizeBitmap(int color, int width, int height) {
        if (width * height > 960 * 540) {
            float ratio = (width * 1f * height) / (960 * 1f * 540);
            return drawColorBitmap(color, (int) (width / ratio), (int) (height / ratio));
        } else {
            return drawColorBitmap(color, width, height);
        }
    }


    public static Bitmap GetBitmapForScale(Bitmap bp, int width, int height) {
        return Bitmap.createScaledBitmap(bp, width, height, true);
    }


}

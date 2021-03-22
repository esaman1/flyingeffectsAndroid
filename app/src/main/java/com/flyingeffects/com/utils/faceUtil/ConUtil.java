package com.flyingeffects.com.utils.faceUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.BitSet;
import java.util.UUID;

public class ConUtil {
    public static final int SEGMENT_THREAD_COUNT = 2; //抠像视频流检测使用线程数

    //在这边填写 API_KEY 和 API_SECRET
    public static String API_KEY = "";
    public static String API_SECRET = "";

    public static String CN_LICENSE_URL = "https://api-cn.faceplusplus.com/sdk/v3/auth";
    public static String TEST_LICENSE_URL = "http://10.104.43.63/sdk/v3/auth";
    public static String US_LICENSE_URL = "https://api-us.faceplusplus.com/sdk/v3/auth";

    public static String getUUIDString(Context mContext) {
        String KEY_UUID = "key_uuid";
        SharedUtil sharedUtil = new SharedUtil(mContext);
        String uuid = sharedUtil.getStringValueByKey(KEY_UUID);
        if (uuid != null && uuid.trim().length() != 0) {
            return uuid;
        }

        uuid = UUID.randomUUID().toString();
        uuid = Base64.encodeToString(uuid.getBytes(),
                Base64.DEFAULT);

        sharedUtil.saveStringValue(KEY_UUID, uuid);
        return uuid;
    }

    public static byte[] readAssetsData(Context context, String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName);
            int lenght = is.available();
            byte[] buffer = new byte[lenght];
            is.read(buffer);
            is.close();
            return buffer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void getAppDetailSettingIntent(Activity context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(intent);
    }

    public static String getRealPathFromURI(Context context, Uri contentURI) {
        Log.e("initData", "uri:" + contentURI.toString());
        Log.e("initData", "uri.path:" + contentURI.getPath());
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static String getVideoRealPathFromURI(Context context, Uri selectedUri) {
        Log.e("initData", "uri:" + selectedUri.toString());
        Log.e("initData", "uri.path:" + selectedUri.getPath());
        if (isQQMediaDocument(selectedUri)) {
            String path = selectedUri.getPath();
            File fileDir = Environment.getExternalStorageDirectory();
            File file = new File(fileDir, path.substring("/QQBrowser".length(), path.length()));
            return file.exists() ? file.toString() : null;
        }
        String contentPath;
        String[] columns = {MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.MIME_TYPE};
        Cursor cursor = context.getContentResolver().query(selectedUri, columns, null, null, null);
        if (cursor == null) {
            contentPath = selectedUri.getPath();
        } else {
            cursor.moveToFirst();

            int pathColumnIndex = cursor.getColumnIndex(columns[0]);
            int mimeTypeColumnIndex = cursor.getColumnIndex(columns[1]);

            contentPath = cursor.getString(pathColumnIndex);
            cursor.close();
        }

        return contentPath;

    }

    public static boolean isQQMediaDocument(Uri uri) {
        return "com.tencent.mtt.fileprovider".equals(uri.getAuthority());
    }

    public static Bitmap decodeSampledBitmapFromResource(String path,
                                                         int reqWidth, int reqHeight) {
        // 先将inJustDecodeBounds设置为true来获取图片的长宽属性
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // 计算inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // 加载压缩版图片
        options.inJustDecodeBounds = false;
        // 根据具体情况选择具体的解码方法
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 计算inSampleSize值
     *
     * @param options   用于获取原图的长宽
     * @param reqWidth  要求压缩后的图片宽度
     * @param reqHeight 要求压缩后的图片长度
     * @return 返回计算后的inSampleSize值
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 原图片的宽高
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;


            // 计算inSampleSize值
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static void saveJPG_After(Bitmap bitmap, String name) {
        File file = new File(name);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static File bitmapToFile(Context context, Bitmap bitmap, String fileNameToSave) { // File name like "image.png"
        //create a file to write bitmap data
        File file = null;
        try {
            file = new File(Environment.getExternalStorageDirectory() + File.separator + fileNameToSave);
            file.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos); // YOU can also save it in JPEG
            byte[] bitmapdata = bos.toByteArray();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return file; // it will return null
        }
    }

    @SuppressLint("NewApi")
    public static Bitmap getImage(String path) {

        try {

            Bitmap src = BitmapFactory.decodeFile(path);
            ExifInterface exif = new ExifInterface(path);
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface
                    .ORIENTATION_NORMAL);
            Matrix matrix = new Matrix();
            Log.d("GetImage Rotation", "" + rotation);
            switch (rotation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    break;
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    matrix.setScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.setRotate(180);
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    matrix.setRotate(180);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    matrix.setRotate(90);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.setRotate(90);
                    break;
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    matrix.setRotate(-90);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.setRotate(-90);
                    break;
                default:
                    break;
            }
            int hight = src.getHeight() > src.getWidth() ? src
                    .getHeight() : src.getWidth();
            float scale = 1080.0f / hight;
            if (scale < 1) {
                matrix.postScale(scale, scale);
            }
            Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
            if(dst.getConfig().ordinal()!=Bitmap.Config.ARGB_8888.ordinal()) {
                dst = dst.copy(Bitmap.Config.ARGB_8888, true);
            }
            //src.recycle();
            return dst;
        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }
    }
    public static Bitmap getImage(Context context, int mipmapId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap src = BitmapFactory.decodeResource(context.getResources(), mipmapId, options);
        Matrix matrix = new Matrix();
        int hight = src.getHeight() > src.getWidth() ? src
                .getHeight() : src.getWidth();
        float scale = 1080.0f / hight;
        if (scale < 1) {
            matrix.postScale(scale, scale);
        }
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        return dst;
    }

    public static Bitmap getImage(Context context, int mipmapId, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap src = BitmapFactory.decodeResource(context.getResources(), mipmapId, options);
        Matrix matrix = new Matrix();
        float sx = src.getWidth() * 1.0f / width;
        float sy = src.getHeight() * 1.0f / height;
//        float scale = 1080.0f / hight;
//        if (scale < 1) {
        matrix.postScale(sx, sy);
//        }
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        return dst;
    }

    public static Bitmap resizeBitmap(Bitmap src, float sx, int sy) {
        Matrix matrix = new Matrix();
        matrix.postScale(sx, sy);
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        return dst;
    }

    public static void updateAlbum(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    public static PowerManager.WakeLock wakeLock = null;

    public static void acquireWakeLock(Context context) {
        if (wakeLock == null) {
            PowerManager powerManager = (PowerManager) (context.getSystemService(Context.POWER_SERVICE));
            wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "flying:MY TAG");
            wakeLock.acquire();
        }
    }

    public static void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }
    }
}
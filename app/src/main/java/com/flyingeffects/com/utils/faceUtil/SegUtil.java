package com.flyingeffects.com.utils.faceUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Debug;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;


import com.flyingeffects.com.R;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xiejiantao on 2017/9/18.
 */

public class SegUtil {
    public static Pattern pattern = Pattern.compile(" [0-9]+");
    // 实时获取CPU当前频率（单位KHZ）
    //https://testerhome.com/topics/501
    public static String getCurCpuFreq() {

        String result = "N/A";
        try {
            FileReader fr = new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            result = text.trim();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int getProcessCpuRatelaji() {

        StringBuilder tv = new StringBuilder();
        int rate = 0;

        try {
            String Result;
            Process p;
            p = Runtime.getRuntime().exec("top -n 1");

            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((Result = br.readLine()) != null) {
                if (Result.trim().length() < 1) {
                    continue;
                } else {
                    String[] CPUusr = Result.split("%");
                    tv.append("USER:" + CPUusr[0] + "\n");
                    String[] CPUusage = CPUusr[0].split("User");
                    String[] SYSusage = CPUusr[1].split("System");
                    tv.append("CPU:" + CPUusage[1].trim() + " length:" + CPUusage[1].trim().length() + "\n");
                    tv.append("SYS:" + SYSusage[1].trim() + " length:" + SYSusage[1].trim().length() + "\n");

                    rate = Integer.parseInt(CPUusage[1].trim()) + Integer.parseInt(SYSusage[1].trim());
                    break;
                }
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(rate + "");
        return rate;
    }

    /*
    **获取当前CPU占比
     * 在实际测试中发现，有的手机会隐藏CPU状态，不会完全显示所有CPU信息，例如MX5，所有建议只做参考
     * @return
             */
    public static String getCPURateDesc() {
        String path = "/proc/stat";// 系统CPU信息文件
        long totalJiffies[] = new long[2];
        long totalIdle[] = new long[2];
        int firstCPUNum = 0;//设置这个参数，这要是防止两次读取文件获知的CPU数量不同，导致不能计算。这里统一以第一次的CPU数量为基准
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;

        for (int i = 0; i < 2; i++) {
            totalJiffies[i] = 0;
            totalIdle[i] = 0;
            try {
                fileReader = new FileReader(path);
                bufferedReader = new BufferedReader(fileReader, 8192);
                int currentCPUNum = 0;
                String str;
                while ((str = bufferedReader.readLine()) != null && (i == 0 || currentCPUNum < firstCPUNum)) {
                    if (str.toLowerCase().startsWith("cpu")) {
                        currentCPUNum++;
                        int index = 0;
                        Matcher matcher = pattern.matcher(str);
                        while (matcher.find()) {
                            try {
                                long tempJiffies = Long.parseLong(matcher.group(0).trim());
                                totalJiffies[i] += tempJiffies;
                                if (index == 3) {//空闲时间为该行第4条栏目
                                    totalIdle[i] += tempJiffies;
                                }
                                index++;
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (i == 0) {
                        firstCPUNum = currentCPUNum;
                        try {//暂停50毫秒，等待系统更新信息。
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        double rate = -1;
        if (totalJiffies[0] > 0 && totalJiffies[1] > 0 && totalJiffies[0] != totalJiffies[1]) {
            rate = 1.0 * ((totalJiffies[1] - totalIdle[1]) - (totalJiffies[0] - totalIdle[0])) / (totalJiffies[1] - totalJiffies[0]);
        }

        return String.format("cpu:%.2f", rate);
    }

     public static int getMemoryInfo() {
               Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
         Debug.getMemoryInfo(memoryInfo);
         return memoryInfo.getTotalPss()/1000;
//         return memoryInfo.dalvikPss/1000;

    }


    /**
     * 宽高对应buffer的实际宽高
     *
     * @param buffer
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getTextureTestBitmap(ByteBuffer buffer, int width, int height) {

        byte bitmapBuffer[] = buffer.array();
//   rgba   argb
        int bitmapSource[] = new int[width * height];
        for (int i = 0, j = 0; i < width * height * 4; i++, j++) {

            bitmapSource[j] = (int) (((bitmapBuffer[i++] & 0xFF) << 16)
                    | ((bitmapBuffer[i++] & 0xFF) << 8)
                    | ((bitmapBuffer[i++] & 0xFF))
                    | (bitmapBuffer[i] & 0xFF) << 24);

        }

        return Bitmap.createBitmap(bitmapSource, width, height, Config.ARGB_8888);
    }

    public static Bitmap getBitmap(Context context) {
        Resources res = context.getResources();
        Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.icon_load);
        return bmp;
    }


    public static Bitmap getBitmapWithPath(String path) {

        Bitmap bmp = decodeSampledBitmapPath(path, 720, 1280);
        return bmp;
    }


    public static Bitmap decodeSampledBitmapPath(String path,
                                                 int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }


    public static Bitmap getBitmapZoom(Context context) {

        Bitmap bmp = decodeSampledBitmapRes(context, R.drawable.icon_load, 720, 1280);
        return bmp;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }


        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapRes(Context context, int resId,
                                                int reqWidth, int reqHeight) {
        Resources res = context.getResources();
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static String getRealPathFromURI(Context context, Uri contentURI) {
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

    public static Bitmap getImageViewBitmap(ImageView iv) {
        iv.setDrawingCacheEnabled(true);
        Bitmap bitmap = iv.getDrawingCache();
        iv.setDrawingCacheEnabled(false);
        return bitmap;
    }


    public static boolean checkCameraHasPerm() {
        Camera mCamera = null;
        try {
            mCamera = Camera.open(0);
        } catch (Exception e) {

        }

        if (mCamera == null) {
            Log.i("xie", "xie camera null");
            return false;
        } else {
            Log.i("xie", "xie camera not null");
            mCamera.release();
            return true;
        }
    }

    public static void getAppDetailSettingIntent(Activity context){
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(Build.VERSION.SDK_INT >= 9){
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if(Build.VERSION.SDK_INT <= 8){
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings","com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(intent);
    }


    public static  void setbarColor(Activity context){
        //设置成白色的背景，字体颜色为黑色。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(context.getResources().getColor(android.R.color.white));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setMiuiStatusBarDarkMode(context,true);
        setMeizuStatusBarDarkIcon(context,true);

    }

    //设置成白色的背景，字体颜色为黑色。
    public static boolean setMiuiStatusBarDarkMode(Activity activity, boolean darkmode) {
        Class<? extends Window> clazz = activity.getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //设置成白色的背景，字体颜色为黑色。
    public static boolean setMeizuStatusBarDarkIcon(Activity activity, boolean dark) {
        boolean result = false;
        if (activity != null) {
            try {
                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                activity.getWindow().setAttributes(lp);
                result = true;
            } catch (Exception e) {
            }
        }
        return result;
    }


    // 保存二进制文件
    public static File getFileFromBytes(byte[] b, String outputFile) {
        BufferedOutputStream stream = null;
        File file = null;
        try {
            file = new File(outputFile);
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return file;
    }
    // 保存灰度图
    public static void saveDirGrayImage(float []imageData, int width, int height, String imgpath)
    {
        Bitmap grayBitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
        for(int i=0; i<height; i++)
        {
            for(int j=0; j<width; j++)
            {
                int value = (int)imageData[i*width+j] ;
                int color = (value << 16) | (value << 8) | value | 0xFF000000; 		// RGB_565鏍煎紡
                //int alpha = value | 0xFF000000;
                //int color = alpha | (value << 16) | (value << 8) | value;			// ARGB_8888鏍煎紡
                grayBitmap.setPixel(j, i, color);
            }
        }
        File grayImgFile = new File(imgpath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(grayImgFile);
            grayBitmap.compress(CompressFormat.JPEG, 80, fos) ;
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/*
//    * 使用Canvas合并Bitmap
//    */
//    private Bitmap mergeBitmap() {
//        // 获取ImageView上得Bitmap图片
//        Bitmap bmp1 = ((BitmapDrawable) ivBmp1.getDrawable()).getBitmap();
//        Bitmap bmp2 = ((BitmapDrawable) ivBmp2.getDrawable()).getBitmap();
//
//        // 创建空得背景bitmap
//        // 生成画布图像
//        Bitmap resultBitmap = Bitmap.createBitmap(ivBmpMerge.getWidth(),
//                ivBmpMerge.getHeight(), Bitmap.Config.RGB_565);
//        Canvas canvas = new Canvas(resultBitmap);// 使用空白图片生成canvas
//
//        // 将bmp1绘制在画布上
//        Rect srcRect = new Rect(0, 0, bmp1.getWidth(), bmp1.getHeight());// 截取bmp1中的矩形区域
//        Rect dstRect = new Rect(0, 0, ivBmpMerge.getWidth() / 2,
//                ivBmpMerge.getHeight());// bmp1在目标画布中的位置
//        canvas.drawBitmap(bmp1, srcRect, dstRect, null);
//
//        // 将bmp2绘制在画布上
//        srcRect = new Rect(0, 0, bmp2.getWidth(), bmp2.getHeight());// 截取bmp1中的矩形区域
//        dstRect = new Rect(ivBmpMerge.getWidth() / 2, 0, ivBmpMerge.getWidth(),
//                ivBmpMerge.getHeight());// bmp2在目标画布中的位置
//        canvas.drawBitmap(bmp2, srcRect, dstRect, null);
//        // 将bmp1,bmp2合并显示
//        return resultBitmap;
//    }


//    /*
//    * 使用Canvas合并Bitmap
//    */
//    private Bitmap mergeBitmapWithLogo(Bitmap srcBitmap) {
//        // 获取ImageView上得Bitmap图片
//        Bitmap bmpLogo = ((BitmapDrawable) getResources().getDrawable(R.drawable.facelogo)).getBitmap();
//
//
//        // 创建空得背景bitmap
//        // 生成画布图像
//        Bitmap resultBitmap = Bitmap.createBitmap(srcBitmap.getWidth(),
//                srcBitmap.getHeight(), Bitmap.Config.RGB_565);
//        Canvas canvas = new Canvas(resultBitmap);// 使用空白图片生成canvas
//
//        // 将bmp1绘制在画布上
//        Rect srcRect = new Rect(0, 0, srcBitmap.getWidth(), srcBitmap.getHeight());// 截取bmp1中的矩形区域
//        Rect dstRect = new Rect(0, 0, ivBmpMerge.getWidth() / 2,
//                ivBmpMerge.getHeight());// bmp1在目标画布中的位置
//        canvas.drawBitmap(bmp1, srcRect, dstRect, null);
//
//        // 将bmp2绘制在画布上
//        srcRect = new Rect(0, 0, bmp2.getWidth(), bmp2.getHeight());// 截取bmp1中的矩形区域
//        dstRect = new Rect(ivBmpMerge.getWidth() / 2, 0, ivBmpMerge.getWidth(),
//                ivBmpMerge.getHeight());// bmp2在目标画布中的位置
//        canvas.drawBitmap(bmp2, srcRect, dstRect, null);
//        // 将bmp1,bmp2合并显示
//        return resultBitmap;
//    }

//    private void showSaveDialog(){
//        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
//        builder.setTitle("保存图片");
//        //builder.setMessage("将抠像后的图片保存到本地相册");
//        String[] items={"保存图片","保存图片+性能参数","取消"};
//        builder.setItems(items, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                switch (which){
//                    case 0:
////                       String path= ConUtil.saveBitmap(mSegBitmap);
////                        Toast.makeText(ImageActivity.this,("图片保存到"+path),Toast.LENGTH_SHORT).show();
//
//                        Bitmap ivBitmap= getImageViewBitmap(ivSeg);
//                        String pathseg= ConUtil.saveBitmap(ImageActivity.this,ivBitmap);
//                        ivSeg.setDrawingCacheEnabled(false);
//                        Toast.makeText(ImageActivity.this,("图片保存到"+pathseg),Toast.LENGTH_SHORT).show();
//                        break;
//                    case 1:
//                        ivSeg.setVisibility(View.INVISIBLE);
////                       Bitmap ivBitmap= getImageViewBitmap(ivSeg);
////                        String pathseg= ConUtil.saveBitmap(ivBitmap);
////                        ivSeg.setDrawingCacheEnabled(false);
////                        Toast.makeText(ImageActivity.this,("图片保存到"+pathseg),Toast.LENGTH_SHORT).show();
//                        break;
//                    default:
//                        break;
//                }
//                mSaveDialog.dismiss();
//            }
//        });
//        mSaveDialog=builder.show();
//    }


}

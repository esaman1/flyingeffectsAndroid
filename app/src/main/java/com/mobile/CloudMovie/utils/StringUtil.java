/**
 * @Project: YasinP2P
 * @Title: StringUtil.java
 * @Package com.hoperun.utils
 * @author HRXA)Tan Xulong
 * @email tan_xulong@hoperun.com
 * @date 2015年3月5日 下午2:05:49
 * @Copyright: 润和软件2015
 * @version V1.0
 */
package com.mobile.CloudMovie.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author HRXA)Tan Xulong
 * @ClassName StringUtil
 * @date 2015年3月5日 下午2:05:49
 */
public class StringUtil {
    /**
     * 获取当前日期
     *
     * @return 当前日期
     * @Description<功能详细描述>
     * @LastModifiedDate：2013-4-28
     * @author wang_ling
     * @EditHistory：<修改内容><修改人>
     */
    public static String getCurrentDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        GregorianCalendar mCalendar = new GregorianCalendar();
        mCalendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        String dateStr = "";
        dateStr = simpleDateFormat.format(mCalendar.getTime());
        return dateStr;
    }

    /**
     * 获取当前的日期和时间
     *
     * @return 当前的日期和时间
     * @Description<功能详细描述>
     * @LastModifiedDate：<date>
     * @author wang_ling
     * @EditHistory：<修改内容><修改人>
     */
    public static String getCurrentDateAndTiem() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:MM:ss.SSS", Locale.getDefault());
        GregorianCalendar mCalendar = new GregorianCalendar();
        mCalendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        String dateStr = "";
        dateStr = simpleDateFormat.format(mCalendar.getTime());
        return dateStr;
    }

    /**
     * 获取当前时间
     *
     * @return
     * @Description<功能详细描述>
     * @LastModifiedDate：2013-5-14
     * @author du_tuo
     * @EditHistory：<修改内容><修改人>
     */
    public static String getCurrentTime() {
        Date d = new Date();
        d.setTime(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return format.format(d);
    }

    /**
     * 获取当前时间
     *
     * @return
     * @Description<功能详细描述>
     * @LastModifiedDate：2013-5-14
     * @author du_tuo
     * @EditHistory：<修改内容><修改人>
     */
    public static String getCurrentTime_hh() {
        Date d = new Date();
        d.setTime(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("ddHHmm", Locale.getDefault());
        return format.format(d);
    }


    /**
     * 获取当前秒
     *
     * @return
     * @Description<功能详细描述>
     * @LastModifiedDate：2013-5-14
     * @author du_tuo
     * @EditHistory：<修改内容><修改人>
     */
    public static String getCurrentTime_m() {
        Date d = new Date();
        d.setTime(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("ss", Locale.getDefault());
        return format.format(d);
    }

    /*
     * /** Base64工具类
     * @Description<功能详细描述>
     * @author chen_linxiang
     * @Version [版本号, 2013-4-8]
     */

    /**
     * 根据可现实字数，显示文字 <一句话功能简述>
     *
     * @param str 文字
     * @param num 可现实字数
     * @return
     * @Description<功能详细描述>
     * @LastModifiedDate：<date>
     * @author wang_bei
     * @EditHistory：<修改内容><修改人>
     */
    public static String getStringLongNum(String str, int num) {
        String ss = str;
        if (str.length() > num) {
            ss = str.substring(0, num - 2) + "…";
        }
        return ss;
    }

    // int textWidth = getTextWidth(paint, str);
    // Log.d(TAG, "textWidth=" + textWidth);

    /**
     * 获取字符串宽度像素 <一句话功能简述>
     *
     * @param paint
     * @param str
     * @return
     * @Description<功能详细描述>
     * @LastModifiedDate：2013-9-5
     * @author li_miao
     * @EditHistory：<修改内容><修改人>
     */
    public static int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;
    }

    //
    public static boolean isEmailAddress(String emailAddress) {
        String emailRegex = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        if (TextUtils.isEmpty(emailAddress))
            return false;
        else
            return emailAddress.matches(emailRegex);
    }

    /**
     * Check if string length is between min and max
     */
    public static boolean isLengthValidity(String str, int min, int max) {
        int length = str.trim().length();
        if (length >= min && length <= max) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断输入是否符合要求 6-20位 true ： 符合要求 false： 不符合要求
     */
    public static boolean isUsrName(String str, int min, int max) {
        try {
            byte[] bytes = str.getBytes("UTF-8");
            if (bytes.length == str.length()) {
                int length = str.trim().length();
                if (length >= min && length <= max) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check if string include digit and letters
     */
    public static boolean isLetterDigit(String str) {
        boolean isDigit = false;//定义一个boolean值，用来表示是否包含数字
        boolean isLetter = false;//定义一个boolean值，用来表示是否包含字母
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) { //用char包装类中的判断数字的方法判断每一个字符
                isDigit = true;
            }
            if (Character.isLetter(str.charAt(i))) { //用char包装类中的判断字母的方法判断每一个字符
                isLetter = true;
            }
        }
        String regex = "^[a-zA-Z0-9]+$";
        boolean isRight = isDigit && isLetter && str.matches(regex);
        return isRight;
    }

    /**
     * <p> 判断str字符串是否是空。 </p>
     *
     * @param str 传入的字符串
     * @return 是否为空
     */
    public static boolean isNull(String str) {
        if (str == null || "".equals(str.trim())) {
            return true;
        }
        return false;
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.length() == 0;
    }

    // 验证手机号码
    public static boolean isPhoneNumber(String str) {
        //		/*
        //		 * 170 号段为虚拟运营商专属号段 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188, 1705 联通：130、131、132、152、155、156、185、186, 1709 电信：133、153、180、189、（1349卫通）, 1700 总结起来就是第一位必定为1，第二位必定为3或5或7,8，其他位置的可以为0-9
        //		 */
        //		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        //		Pattern p1 = Pattern.compile("^(170[0,5,9])\\d{7}$");
        //		Matcher m = p.matcher(mobiles), m1 = p1.matcher(mobiles);
        //		return m.matches() || m1.matches();
        String ss = str.length() + "";
        ss = ss.trim();
        //		if(str.length() > 8 && !str.contains("-"))
        //		{
        //			return false;
        //		}
        str = str.replace("-", "");
        str = str.trim();
        if (str.length() == 8 || str.length() == 11) {
            //			Pattern p1 = null, p2 = null;
            //			Matcher m = null;
            //			boolean b = false;
            //			p1 = Pattern.compile("((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))"); // 验证带区号的
            //			p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$"); // 验证没有区号的
            //			if(str.length() > 9)
            //			{
            //				m = p1.matcher(str);
            //				b = m.matches();
            //			}
            //			else
            //			{
            //				m = p2.matcher(str);
            //				b = m.matches();
            //			}
            return true;
        } else {
            return false;
        }
    }

    //	/**
    //	 * 验证号码 手机号 固话均可
    //	 * @param phoneNumber 输入的电话号码
    //	 * @return boolean
    //	 */
    //	public static boolean isPhoneNumberValid(String phoneNumber)
    //	{
    //		boolean isValid = false;
    //		String expression = "((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
    //		CharSequence inputStr = phoneNumber;
    //		Pattern pattern = Pattern.compile(expression);
    //		Matcher matcher = pattern.matcher(inputStr);
    //		if(matcher.matches())
    //		{
    //			isValid = true;
    //		}
    //		return isValid;
    //	}

    /**
     * 只验证固定电话
     *
     * @return 验证通过返回true 只判断位数
     */
    public static boolean isPhone(String str) {
        String ss = str.length() + "";
        ss = ss.trim();
        //		if(str.length() > 8 && !str.contains("-"))
        //		{
        //			return false;
        //		}
        str = str.replace("-", "");
        str = str.trim();
        if (str.length() == 8 || str.length() == 11) {
            //			Pattern p1 = null, p2 = null;
            //			Matcher m = null;
            //			boolean b = false;
            //			p1 = Pattern.compile("((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))"); // 验证带区号的
            //			p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$"); // 验证没有区号的
            //			if(str.length() > 9)
            //			{
            //				m = p1.matcher(str);
            //				b = m.matches();
            //			}
            //			else
            //			{
            //				m = p2.matcher(str);
            //				b = m.matches();
            //			}
            return true;
        } else {
            return false;
        }
    }

    /**
     * 校验银行卡卡号
     *
     * @param cardId
     * @return
     */
    public static boolean checkBankCard(String cardId) {
        char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
        if (bit == 'N') {
            return false;
        }
        return cardId.charAt(cardId.length() - 1) == bit;
    }

    /**
     * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
     *
     * @param nonCheckCodeCardId
     * @return
     */
    public static char getBankCardCheckCode(String nonCheckCodeCardId) {
        if (nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0 || !nonCheckCodeCardId.matches("\\d+")) {
            // 如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }

    /**
     * 判断对象是否为空
     *
     * @param o
     * @return
     */
    public static boolean isEmpty(Object o) {
        if (o == null)
            return true;
        if (o instanceof String)
            return (String.valueOf(o).trim().length() == 0);
        else if (o instanceof List)
            return ((List<?>) o).isEmpty();
        else if (o instanceof Map)
            return ((Map<?, ?>) o).isEmpty();
        else
            return false;
    }


    /**
     * 时间的主动换行
     */
    public static String TimeNewLine(String str) {
        if (str != null && str.length() > 11) {
            String str_0 = str.substring(0, 10);
            String str_1 = str.substring(10, str.length());
            if (str_1 != null && !str_1.equals("")) {
                return str_0 + "\n" + str_1;
            }
            return str_0;
        } else {
            return str;
        }
    }

    /**
     * 去掉时间的时分秒
     */
    public static String CancleTime(String str) {
        if (str != null) {
            str = str.trim();
            if (str.length() > 11) {
                String str_0 = str.substring(0, 10);
                return str_0;
            } else {
                return str;
            }
        }
        return str;
    }

    /***
     * 得到时间的时分秒
     */
    public static String getHms(String str) {
        if (str != null) {
            str = str.trim();
            if (str.length() > 11) {
                String str_0 = str.substring(10, str.length());
                return str_0;
            } else {
                return str;
            }
        }
        return str;
    }

    /***
     * 去掉<p>和去掉<br/>替换成换行
     */
    public static String replaceSpecialSign(String str) {
        str = str.replace("<p>", "");
        str = str.replace("</p>", "");
        str = str.replace("<br/>", "\n");
        return str;
    }

    /***
     * 检查输入的内容是否为空
     */
    public static boolean isEdNull(EditText ed) {
        if (ed.getText().toString().trim() == null || ed.getText().toString().trim().length() == 0) {
            return true;
        }
        return false;
    }


    /**
     * 计算图片的缩放值
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 根据路径获得图片并压缩，返回bitmap用于显示
     */
    public static Bitmap getSmallBmpFromFile(String filePath, int targetW, int targetH) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, targetW, targetH);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 根据资源id获得图片并压缩，返回bitmap用于显示
     */
    public static Bitmap getSmallBmpFromResource(Context context, int id, int targetW, int targetH) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        //当inJustDecodeBounds设成true时，bitmap并不加载到内存，这样效率很高
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), id, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, targetW, targetH);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(context.getResources(), id, options);
    }


    /***
     * 根据bitmap返回缩放后的bitmap
     * @param bm  位图
     * @param newWidth   宽
     * @param newHeight 高
     * @return 返回压缩后debitmap
     */
    // 缩放图片
    public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }


    /***
     * 根据bitmap返回缩放后的bitmap
     * @param bm  位图
     * @return 返回压缩后debitmap
     */
    // 缩放图片
    public static Bitmap zoomImg_scanle(Bitmap bm, float scanle) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = scanle;
        float scaleHeight = scanle;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }


    /**
     * 保存文件
     *
     * @param bm
     * @param fileName
     * @throws IOException
     */
    public static File saveFile(Bitmap bm, String fileName) throws IOException {
        String path = "/revoeye/";
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        File myCaptureFile = new File(path + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
        return dirFile;
    }

    /***
     * 保存图片到某个路径
     *
     * @param bitName
     * @param mBitmap
     */
    public static String saveMyBitmap(String bitName, Bitmap mBitmap) {
        File f = new File("/sdcard/" + bitName + ".png");
        try {
            f.createNewFile();
        } catch (IOException e) {
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "/sdcard/" + bitName + ".png";//返回的地址路径
    }

    /***
     * 保存图片到SD卡
     */
    public static void saveBitmap2SD(String photoPath, Bitmap bitmap) {
        File f = new File(photoPath);
//        try {
//            f.createNewFile();
//        } catch (IOException e) {
//        }


        if (f.exists()) {
            f.delete();
        }
        try {
            f.createNewFile();
        } catch (IOException e) {
        }

        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * 判断当前程序是在后台运行还是在其他运行
     *
     * @param context zhangtongju 2015/8/27
     * @return true 是
     */
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                /*
                 * BACKGROUND=400 EMPTY=500 FOREGROUND=100 GONE=1000 PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
                 */
                //                Log.i(context.getPackageName(), "此appimportace ="
                //                        + appProcess.importance
                //                        + ",context.getClass().getName()="
                //                        + context.getClass().getName());
                if (appProcess.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 获取版本号
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 1000;
        }
    }

    /***
     * 把list转换成数组
     */
    public static int[] ChangeList(List list) {
        int[] inter = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            inter[i] = (int) list.get(i);
        }
        return inter;
    }

    /**
     * 把string转换成int数组
     */
    public static int[] ChangeListForString(String str) {
        int last = 0;
        int[] inter;
        if (str.contains("[")) {
            str = str.replace("[", "");
        }
        if (str.contains("]")) {
            str = str.replace("]", "");
        }
        if (str.contains(",")) {
            str = str.replace(",", "");
        }
        if (str.contains(" ")) {
            str = str.replace(" ", "");
        }
        str = str.trim();
        inter = new int[str.length()];
        for (int i = 0; i < str.length(); i++) {
            inter[i] = Integer.parseInt(str.substring(last, i + 1));
            last = i + 1;
        }
        return inter;
    }


    /**
     * 除第一个和最后一个，其他的都加密
     */
    public static String encryption(String Str) {
        String strr = Str;
        String NowStr = "*";
        if (Str != null && !Str.equals("")) {
            String ss = Str.substring(1, Str.length() - 1);
            int length_ss = ss.length();
            for (int i = 0; i < length_ss; i++) {
                NowStr = NowStr + "*";
            }
            strr = strr.replace(ss, NowStr);
            return strr;
        } else {
            return "";
        }
    }


    /**
     * dip转px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px转dip
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * 这个方法是用来去掉后台传过来的参数的单位，单独显示数字的方法
     */
    public static double cancleUnint(String str) {
        try {
            String str_canCent = str.replace("%", "");
            double float_cancleUnit = Double.parseDouble(str_canCent);
            float_cancleUnit = float_cancleUnit / 100;
            return float_cancleUnit;
        } catch (Exception e) {
            return 0;
        }
    }

    /***
     * 判断字符串里面是否有汉字,没有就返回金额名称，有就不管
     */
    public static String HasChinase(String str) {
        if (str != null && !str.equals("")) {
            try {
                byte[] bytes = str.getBytes("UTF-8");
                if (bytes.length == str.length()) {
                    return str + "元";
                } else {
                    //是汉字
                    return str;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return str;
    }

    /***
     * 判断字符串里面是否有"%"
     */
    public static String HasChinasePercent(String str) {
        if (str.equals("0")) {
            str = "0.00";
        }
        if (str.contains("%")) {
            return str;
        } else {
            return str + "%";
        }
    }


    /****
     * 把值变为Double ,取2位
     * 会四舍五入
     * data 2016/2/29
     */
    public static String ChangeDoubleToString(String str) {
        try {
            if (str != null && !str.equals("")) {
                if (str.contains("元")) {
                    str = str.replace("元", "");
                }
                double double_str = Double.parseDouble(str);
                //保留二位小数
                DecimalFormat df = new DecimalFormat("###.00");
                String str_df = df.format(double_str) + "";
                if (str_df.equals(".00")) {
                    str_df = "0.00";
                }
                return str_df;
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }


    /***
     * * 把值变为Double ,取2位
     * 不四舍五入
     */
    public static String ChangeDoubleToString_2(String str) {
        try {
            if (str != null && !str.equals("")) {
                if (str.contains("元")) {
                    str = str.replace("元", "");
                }
                double double_str = Double.parseDouble(str);
                //保留二位小数
//				DecimalFormat df = new DecimalFormat("###.00");
//				String str_df = df.format(double_str) + ""
//
// ;
                BigDecimal b = new BigDecimal(double_str);
                double f1 = b.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();//直接删除多余的小数位  11.116约=11.11
//				double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                String result = f1 + "";
                if (result.equals(".00")) {
                    result = "0.00";
                }
                return result;
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }


    /***
     * * 把值变为Double ,整数
     * 不四舍五入
     *
     * @return
     */
    public static String ChangeDoubleToString_3(String str) {
        try {
            if (str != null && !str.equals("")) {
                if (str.contains("%")) {
                    str = str.replace("%", "");
                }
                if (str.contains("元")) {
                    str = str.replace("元", "");
                }
                double double_str = Double.parseDouble(str);
                //保留二位小数
//				DecimalFormat df = new DecimalFormat("###.00");
//				String str_df = df.format(double_str) + ""
                BigDecimal b = new BigDecimal(double_str);
                double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                int inter_f1 = (int) f1;
                String result = inter_f1 + "";
                if (result.equals(".00")) {
                    result = "";
                }
                return result;
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }


    /***
     * 保存为*号
     */
    public static String Getsymbol() {
        return "***";
    }


    //将px转换为dp
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * user :TongJu  ;描述：获得版本号
     * 时间：2018/5/29
     **/
    public static String getVersion(Context context) {
        String mVersionName = null;
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            mVersionName = packageInfo.versionCode + "";

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return mVersionName;
    }


    @SuppressLint("MissingPermission")
    public static String getIMEI(Context context) {
        String imei;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imei = telephonyManager.getDeviceId();
        } catch (Exception e) {
            imei = "";
        }
        return imei;
    }


    /**
     * 把onject 转成json
     * @param bean
     * @return
     */
    public static String beanToJSONString(Object bean) {
        return new Gson().toJson(bean);

    }


    /**
     * user :TongJu  ; email:jutongzhang@sina.com
     * time：2018/9/18
     * describe:判断程序是否处于后台
     **/
    public static boolean Frontdesk(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                Log.i(context.getPackageName(), "此appimportace =" + appProcess.importance + ",context.getClass().getName()=" + context.getClass().getName());
                if (appProcess.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    LogUtil.i(context.getPackageName(), "处于后台" + appProcess.processName);
                    return true;
                } else {
                    LogUtil.i(context.getPackageName(), "处于前台" + appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }
}

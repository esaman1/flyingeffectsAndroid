/*
 * File name: NetUtils.java
 * Copyright: Copyright (c) 2006-2013 hoperun Inc, All rights reserved
 * Description: <描述>
 * Author: ma_zhicheng
 * Last modified date: 13 Sep,2013
 * Version: <版本编号>
 * Edit history: <修改内容><修改人>
 */
package com.mobile.CloudMovie.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.provider.Settings;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 *获取系统相关数据
 */
@SuppressLint("NewApi")
public class OsUtils {
	 /**
	 * 
	 * @Title: getAndroidID
	 * @Description: Settings.Secure#ANDROID_ID returns the Android ID as an
	 *               unique 64-bit hex strin
	 * @param @param context
	 * @param @return
	 * @return String
	 * @throws
	 * @link
	 *       {http://developer.android.com/reference/android/provider/Settings.
	 *       Secure
	 *       .html#ANDROID_ID}
	 */
	public static String getAndroidID(Context context) {
		String androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
		String id = androidID + Build.SERIAL;  //保险方案  设备id+序列号  ，因为有些厂商是拿不到设备id 的。序列号也不一定完全唯一
		LogUtil.d("getAndroidID","getAndroidID="+id);
		return id;
	}

	/**
	 * 
	 * 返回应用名称
	 * 
	 * @Description 获取存储文件的根路径
	 * 
	 * @param context
	 *            应用上下文
	 * @param dStr
	 *            文件名
	 * @return 文件的根路径
	 * @LastModifiedDate：2013-10-25
	 * @EditHistory：<修改内容><修改人>
	 */
	public static String getApplicationName(Context context, String dStr) {
		String packageName = "";

		try {
			PackageManager packageManager = context.getPackageManager();
			packageName = context.getApplicationInfo().loadLabel(packageManager).toString();
		}
		catch (Exception e) {
			packageName = dStr;
		}

		return packageName;
	}

	/**
	 * 
	 * 获取本地ip地址
	 * 
	 * @Description 获取本地ip地址
	 * 
	 * @return 本地ip地址
	 * @LastModifiedDate：13 Sep,2013
	 * @author ma_zhicheng
	 * @EditHistory：<修改内容><修改人>
	 */
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();

				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {

					InetAddress inetAddress = enumIpAddr.nextElement();

					if (!inetAddress.isLoopbackAddress()) {

						return inetAddress.getHostAddress().toString();
					}
				}
			}
		}
		catch (SocketException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取设备型号
	 * @Description<功能详细描述>
	 * @return
	 * @LastModifiedDate：2013-12-12
	 * @EditHistory：<修改内容><修改人>
	 */
	public static String getMODEL() {
		return Build.MODEL;
	}

	/**
	 * 
	 * 获取android版本
	 * 
	 * @Description 获取android版本
	 * 
	 * @return android版本
	 * @LastModifiedDate：13 Sep,2013
	 * @author ma_zhicheng
	 * @EditHistory：<修改内容><修改人>
	 */
	public static String getOsVersion() {
		String osVersion;

		osVersion = Build.VERSION.RELEASE;

		return osVersion;
	}

	/**
	 * 
	 * 获取存储文件的根路径
	 * 
	 * @Description 获取存储文件的根路径
	 * 
	 * @param context
	 *            应用上下文
	 * @param dStr
	 *            文件名
	 * @return 文件的根路径
	 * @LastModifiedDate：2013-10-25
	 * @EditHistory：<修改内容><修改人>
	 */
	public static String getPackagePath(Context context, String dStr) {
		String mAppName = "";

		try {
			PackageManager packageManager = context.getPackageManager();
			String packageName = context.getApplicationInfo().loadLabel(packageManager).toString();
			mAppName = "/" + packageName + "/";
		}
		catch (Exception e) {

			mAppName = dStr;
		}

		return mAppName;
	}

	public static int getSDKVersion() {
		int sdkVersion;
		try {
			sdkVersion = Integer.valueOf(Build.VERSION.SDK_INT);

		}
		catch (NumberFormatException e) {

			sdkVersion = 0;
		}
		return sdkVersion;
	}

	/**
	 * 
	 * @Title: getVersionCode
	 * @param @param context
	 * @param @return
	 * @return int
	 * @throws
	 */
	public static int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			if (null != context) {
				PackageManager packageManager = context.getPackageManager();
				String packageName = context.getPackageName();
				versionCode = packageManager.getPackageInfo(packageName, 0).versionCode;
			}
		}
		catch (NameNotFoundException e) {
		}
		return versionCode;
	}


	public static String getDeviceId(Context context){
		String androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
		String id = androidID + Build.SERIAL;
		try {
			return toMD5(id);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return id;
		}
	}




	private static String toMD5(String text) throws NoSuchAlgorithmException {
		//获取摘要器 MessageDigest
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		//通过摘要器对字符串的二进制字节数组进行hash计算
		byte[] digest = messageDigest.digest(text.getBytes());

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < digest.length; i++) {
			//循环每个字符 将计算结果转化为正整数;
			int digestInt = digest[i] & 0xff;
			//将10进制转化为较短的16进制
			String hexString = Integer.toHexString(digestInt);
			//转化结果如果是个位数会省略0,因此判断并补0
			if (hexString.length() < 2) {
				sb.append(0);
			}
			//将循环结果添加到缓冲区
			sb.append(hexString);
		}
		//返回整个结果
		return sb.toString();
	}




	/**
	 * 
	 * 获取应用的版本号
	 * 
	 * @Description 获取应用的版本号
	 * 
	 * @param context
	 *            应用上下文
	 * @return 应用的版本号
	 * @LastModifiedDate：13 Sep,2013
	 * @EditHistory：<修改内容><修改人>
	 */
	public static String getVersionName(Context context) {
		String versionName = "";
		try {
			PackageManager packageManager = context.getPackageManager();
			String packageName = context.getPackageName();
			versionName = packageManager.getPackageInfo(packageName, 0).versionName;
		}
		catch (NameNotFoundException e) {
		}
		return versionName;
	}

	/**
	 * 
	 * 隐藏键盘
	 * 
	 * @Description<功能详细描述>
	 * 
	 * @LastModifiedDate：<date>
	 * @EditHistory：<修改内容><修改人>
	 */
	public static void hideSoftKey(Activity activity, EditText et) {
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

		if (et != null) {
			imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
		}
	}

	/**
	 * 是否是三星设备
	 * 
	 * @Description<功能详细描述>
	 * 
	 * @return
	 * @LastModifiedDate：2013-12-12
	 * @EditHistory：<修改内容><修改人>
	 */
	public static boolean isSamsung() {
		String brand = Build.MANUFACTURER.toLowerCase(Locale.getDefault());
		return brand.contains("samsung");
	}

//	private static final String TAG = OsUtils.class.getSimpleName();
}

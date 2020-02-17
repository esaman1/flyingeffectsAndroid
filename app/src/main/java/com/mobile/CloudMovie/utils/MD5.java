package com.mobile.CloudMovie.utils;

/**
 * MD5工具类 <一句话功能简述>
 */
public class MD5 {
    /**
     * 获取md5
     * Description 获取md5
     * 源byte数组
     * return MD5加密字符串
     */
    private static String getMD5(byte[] source) {
        String s = null;
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        final int temp = 0xf;
        final int arraySize = 32;
        final int strLen = 16;
        final int offset = 4;
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            md.update(source);
            byte[] tmp = md.digest();
            char[] str = new char[arraySize];
            int k = 0;
            for (int i = 0; i < strLen; i++) {
                byte byte0 = tmp[i];
                str[k++] = hexDigits[byte0 >>> offset & temp];
                str[k++] = hexDigits[byte0 & temp];
            }
            s = new String(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    /***
     *user: 张sir ,@time: 2017/8/11
     *description:获取md5
     */
    public static String getMD5(String source) {
        if (source != null) {
            return getMD5(source.getBytes());
        } else {
            return null;
        }
    }


}

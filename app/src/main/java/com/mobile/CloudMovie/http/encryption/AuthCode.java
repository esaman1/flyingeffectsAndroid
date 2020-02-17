package com.mobile.CloudMovie.http.encryption;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Random;


public class AuthCode {
    public enum DiscuzAuthcodeMode {
        Encode, Decode
    };

    /**
     * 字符串切割
     */
    public static String CutString(String str, int startIndex, int length) {
        if (startIndex >= 0) {
            if (length < 0) {
                length = length * -1;
                if (startIndex - length < 0) {
                    length = startIndex;
                    startIndex = 0;
                } else {
                    startIndex = startIndex - length;
                }
            }

            if (startIndex > str.length()) {
                return "";
            }

        } else {
            if (length < 0) {
                return "";
            } else {
                if (length + startIndex > 0) {
                    length = length + startIndex;
                    startIndex = 0;
                } else {
                    return "";
                }
            }
        }

        if (str.length() - startIndex < length) {

            length = str.length() - startIndex;
        }

        return str.substring(startIndex, startIndex + length);
    }

    /**
     * 字符串切割
     */
    public static String CutString(String str, int startIndex) {
        return CutString(str, startIndex, str.length());
    }

    /**
     * 用于 RC4 处理密码
     *
     * @param pass
     * @param kLen
     * @return 字符数组
     */
    static private byte[] GetKey(byte[] pass, int kLen) {
        byte[] mBox = new byte[kLen];

        for (int i = 0; i < kLen; i++) {
            mBox[i] = (byte) i;
        }

        int j = 0;
        for (int i = 0; i < kLen; i++) {

            j = (j + (int) ((mBox[i] + 256) % 256) + pass[i % pass.length])
                    % kLen;

            byte temp = mBox[i];
            mBox[i] = mBox[j];
            mBox[j] = temp;
        }

        return mBox;
    }

    /**
     * 生成随机字符
     *
     * @return String
     */
    public static String RandomString(int lens) {
        char[] CharArray = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k',
                'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        int clens = CharArray.length;
        String sCode = "";
        Random random = new Random();
        for (int i = 0; i < lens; i++) {
            sCode += CharArray[Math.abs(random.nextInt(clens))];
        }
        return sCode;
    }

    /**
     * 生成随机字符
     *
     * @return String
     */
    public static String RandomNumber(int lens) {
        char[] CharArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        int clens = CharArray.length;
        String sCode = "";
        Random random = new Random();
        for (int i = 0; i < lens; i++) {
            int r = Math.abs(random.nextInt(clens));
            if (i == 0 && r == 0) {
                i--;
                continue;
            }
            sCode += CharArray[Math.abs(random.nextInt(clens))];
        }
        return sCode;
    }

    /**
     * 对加密方法的封装 ,可以填写过期时间
     *
     * @return
     */
    public static String authcodeEncode(String source, String key, int expiry, String charset) {
        return authcode(source, key, DiscuzAuthcodeMode.Encode, expiry, charset);
    }

    /**
     * 对加密方法的封装，不必填写过期时间
     *
     * @param source
     * @param key
     * @return String
     */
    public static String authcodeEncode(String source, String key, String charset) {
        return authcode(source, key, DiscuzAuthcodeMode.Encode, 0, charset);

    }

    /**
     * 对解密方法进行封装
     *
     * @param source
     * @param key
     * @return String
     */
    public static String authcodeDecode(String source, String key, String charset) {
        return authcode(source, key, DiscuzAuthcodeMode.Decode, 0, charset);

    }

    /**
     * 加密解密方法
     *
     * @param source
     * @param key
     * @param operation
     * @param expiry
     * @return String
     */
    private static String authcode(String source, String key,
                                   DiscuzAuthcodeMode operation, int expiry, String charset) {
        try {
            if (source == null || key == null) {
                return "";
            }

            int ckey_length = 4;
            String keya, keyb, keyc, cryptkey, result;

            keya = MD52(CutString(key, 0, 16));

            keyb = MD52(CutString(key, 16, 16));

            keyc = ckey_length > 0 ? (operation == DiscuzAuthcodeMode.Decode ? CutString(
                    source, 0, ckey_length)
                    : RandomString(ckey_length))
                    : "";

            cryptkey = keya + MD52(keya + keyc);
            if (operation == DiscuzAuthcodeMode.Decode) {
                byte[] temp;
                temp = Base64.decode(CutString(source, ckey_length));
                result = new String(RC4(temp, cryptkey), charset);
                boolean cryptTrue = CutString(result, 10, 16).equals(CutString(MD52(CutString(result, 26) + keyb), 0, 16));
                boolean notTimeOut = Integer.parseInt(CutString(result, 0, 10)) == 0 || Integer.parseInt(CutString(result, 0, 10)) - getUnixTimestamp() > 0;
                if (cryptTrue && notTimeOut) {
                    return CutString(result, 26);
                } else {
                    return "";
                }
            } else {
                if (expiry == 0) {
                    source = "0000000000" + CutString(MD52(source + keyb), 0, 16)
                            + source;
                } else {
                    source = (getUnixTimestamp() + expiry) + CutString(MD52(source + keyb), 0, 16)
                            + source;
                }
                byte[] temp = RC4(source.getBytes(charset), cryptkey);
                return keyc + Base64.encodeBytes(temp);
            }
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * RC4 原始算法
     *
     * @param input
     * @param pass
     * @return 字节数组
     */
    private static byte[] RC4(byte[] input, String pass) {
        if (input == null || pass == null) {
            return null;
        }
        byte[] output = new byte[input.length];
        byte[] mBox = GetKey(pass.getBytes(), 256);

        // 加密   
        int i = 0;
        int j = 0;

        for (int offset = 0; offset < input.length; offset++) {
            i = (i + 1) % mBox.length;
            j = (j + (int) ((mBox[i] + 256) % 256)) % mBox.length;

            byte temp = mBox[i];
            mBox[i] = mBox[j];
            mBox[j] = temp;
            byte a = input[offset];

            //byte b = mBox[(mBox[i] + mBox[j] % mBox.Length) % mBox.Length];   
            // mBox[j] 一定比 mBox.Length 小，不需要在取模   
            byte b = mBox[(toInt(mBox[i]) + toInt(mBox[j])) % mBox.length];

            output[offset] = (byte) ((int) a ^ (int) toInt(b));
        }

        return output;
    }

    /**
     * md5加密方法封装
     *
     * @param MD5
     * @return
     */
    public static String MD52(String MD5) {
        StringBuffer sb = new StringBuffer();
        String part = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] md5 = md.digest(MD5.getBytes(Charset.forName("UTF-8")));

            for (int i = 0; i < md5.length; i++) {
                part = Integer.toHexString(md5[i] & 0xFF);
                if (part.length() == 1) {
                    part = "0" + part;
                }
                sb.append(part);
            }

        } catch (NoSuchAlgorithmException ex) {
        }
        return sb.toString();
    }

    public static String MD5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte[] strTemp = s.getBytes();
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 转变为数字
     *
     * @param b
     * @return int
     */
    public static int toInt(byte b) {
        return (int) ((b + 256) % 256);
    }

    /**
     * 取得系统当前时间
     *
     * @return
     */
    public static long getUnixTimestamp() {
        Calendar cal = Calendar.getInstance();
        return cal.getTimeInMillis() / 1000;
    }

    /**
     * 测试方法
     *
     * @param args
     */
    public static void main(String[] args) {
        String test = "/asdfas/asdf.html";
        String key = "czgjj";
        String afStr = AuthCode.authcodeEncode(test, key, 30, "UTF-8");
        String deStr1 = AuthCode.authcodeDecode(afStr, key, "UTF-8");
        System.out.println("--------encode:" + afStr);
        System.out.println("--------decode:" + deStr1);
        System.out.println("MD52--------:" + MD52("vhjrQrYlTDTGwytOgGRvr0BY9uQ78GoK{\"code\":\"0013\"}vhjrQrYlTDTGwytOgGRvr0BY9uQ78GoK"));
    }

}

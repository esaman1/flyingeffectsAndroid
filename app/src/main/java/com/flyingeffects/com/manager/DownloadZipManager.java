package com.flyingeffects.com.manager;

import android.os.Environment;
import android.os.Message;

import com.flyingeffects.com.utils.LogUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class DownloadZipManager {

   static  DownloadZipManager dZip;
    public static DownloadZipManager getInstance(){
        if(dZip==null){
            dZip=new DownloadZipManager();
        }
        return  dZip;

    }


    private  HttpURLConnection conn;

    public  File getFileFromServer(String path, String filePath, getProgress progress) throws Exception {
        // 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            File needFile = new File(filePath);
            if (!needFile.exists()) {
                needFile.mkdirs();
            } //创建文件目录
            URL url = new URL(path);
            try {
                TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }};

                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
            } catch (Exception ignored) {

                LogUtil.d("onVideoAdError", "ignored="+ignored.getMessage());
            }

            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            float length = conn.getContentLength();
            InputStream is = conn.getInputStream();
//            File file_instalApk = new File(Environment.getExternalStorageDirectory() + "/myzx");
//            if (!file_instalApk.exists()) {
//                file_instalApk.mkdirs();
//            }
            File file = new File(filePath, "cache" + ".zip");
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len;
            float count = 0;

            while ((len = bis.read(buffer)) != -1) {
                Message msg = new Message();
                fos.write(buffer, 0, len);
                count += len;
                msg.arg1 = (int) (count * 100 / length);
                msg.what = 100;
                progress.downProgress(msg.arg1, false, file.getPath());
            }
            progress.downProgress(100, true, file.getPath());
            fos.close();
            bis.close();
            is.close();
            return file;
        } else {
            return null;
        }
    }

    public interface getProgress {
        void downProgress(int progress, boolean isSucceed, String zipFilePath);


    }


    public void breakConnection() {
        if(conn!=null){
            conn.disconnect();
            conn=null;
        }

    }


//    /**
//     * zip解压
//     * @param srcFile        zip源文件
//     * @param destDirPath     解压后的目标文件夹
//     * @throws RuntimeException 解压失败会抛出运行时异常
//     */
//    public static void unZip(File srcFile, String destDirPath) throws RuntimeException {
//        long start = System.currentTimeMillis();
//        // 判断源文件是否存在
//        if (!srcFile.exists()) {
//            throw new RuntimeException(srcFile.getPath() + "所指文件不存在");
//        }
//        // 开始解压
//        ZipFile zipFile = null;
//        try {
//            zipFile = new ZipFile(srcFile);
//            Enumeration<?> entries = zipFile.entries();
//            while (entries.hasMoreElements()) {
//                ZipEntry entry = (ZipEntry) entries.nextElement();
//                System.out.println("解压" + entry.getName());
//                // 如果是文件夹，就创建个文件夹
//                if (entry.isDirectory()) {
//                    String dirPath = destDirPath + "/" + entry.getName();
//                    File dir = new File(dirPath);
//                    dir.mkdirs();
//                } else {
//                    // 如果是文件，就先创建一个文件，然后用io流把内容copy过去
//                    File targetFile = new File(destDirPath + "/" + entry.getName());
//                    // 保证这个文件的父文件夹必须要存在
//                    if(!targetFile.getParentFile().exists()){
//                        targetFile.getParentFile().mkdirs();
//                    }
//                    targetFile.createNewFile();
//                    // 将压缩文件内容写入到这个文件中
//                    InputStream is = zipFile.getInputStream(entry);
//                    FileOutputStream fos = new FileOutputStream(targetFile);
//                    int len;
//                    byte[] buf = new byte[1024];
//                    while ((len = is.read(buf)) != -1) {
//                        fos.write(buf, 0, len);
//                    }
//                    // 关流顺序，先打开的后关闭
//                    fos.close();
//                    is.close();
//                }
//            }
//            long end = System.currentTimeMillis();
//            System.out.println("解压完成，耗时：" + (end - start) +" ms");
//        } catch (Exception e) {
//            throw new RuntimeException("unzip error from ZipUtils", e);
//        } finally {
//            if(zipFile != null){
//                try {
//                    zipFile.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }


}

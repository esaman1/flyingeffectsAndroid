package com.flyingeffects.com.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

public class updateFileUtils {

    public static void uploadFile(final List<File> files, final String url, final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String BOUNDARY = UUID.randomUUID().toString(); //边界标识 随机生成
                String PREFIX = "--", LINE_END = "\r\n";
                String CONTENT_TYPE = "multipart/form-data"; //内容类型
                try {
                    URL httpUrl = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
                    conn.setReadTimeout(25000);
                    conn.setConnectTimeout(25000);
                    conn.setDoInput(true); //允许输入流
                    conn.setDoOutput(true); //允许输出流
                    conn.setUseCaches(false); //不允许使用缓存
                    conn.setRequestMethod("POST"); //请求方式
                    conn.setRequestProperty("Charset", "UTF-8");
                    //设置编码
                    conn.setRequestProperty("connection", "keep-alive");
                    conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
                    //     if(files.size()!= 0) {
                    /** * 当文件不为空，把文件包装并且上传 */
                    OutputStream outputSteam = conn.getOutputStream();

                    DataOutputStream dos = new DataOutputStream(outputSteam);
                    for (int i = 0; i < files.size(); i++) {
                        StringBuffer sb = new StringBuffer();
                        sb.append(PREFIX);
                        int num=i+1;
                        sb.append(BOUNDARY);
                        sb.append(LINE_END);
                        sb.append("Content-Disposition: form-data; name=\"file"+ num + "\"; filename=\"" + files.get(i).getName() + "\"" + LINE_END);
                        //sb.append("Content-Type: application/octet-stream; charset="+ "UTF-8" +LINE_END);
//                        LogUtil.d("OOM",sb.toString());
                        sb.append(LINE_END);
                        dos.write(sb.toString().getBytes());
                        InputStream is = new FileInputStream(files.get(i));
                        byte[] bytes = new byte[1024];
                        int len = -1;
                        while ((len = is.read(bytes)) != -1) {
                            dos.write(bytes, 0, len);
                        }

                        dos.write(LINE_END.getBytes());
                        is.close();

                    }
                    byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                    dos.write(end_data);
                    dos.flush();
                    /**
                     * 获取响应码 200=成功
                     * 当响应成功，获取响应的流
                     */
                    int res = conn.getResponseCode();
//                      Log.e(TAG, "response code:"+res);
                    if (res == 200) {

                        InputStream input = conn.getInputStream();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        // 定义读取的长度
                        int len1 = 0;
                        // 定义缓冲区
                        byte buffer[] = new byte[1024];
                        // 按照缓冲区的大小，循环读取
                        while((len1 = input.read(buffer)) != -1)
                        {
                            // 根据读取的长度写入到os对象中
                            baos.write(buffer, 0, len1);
                        }
                        // 释放资源
                        input.close();
                        baos.close();
                        // 返回字符串
                        byte[] enBytes = baos.toByteArray();
                        String s = new String(enBytes);
                        listener.onFinish(res,s);

                    }else{
                        listener.onFinish(404,"");
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    listener.onFinish(404,"");
                } catch (IOException e) {
                    listener.onFinish(404,"");
                    e.printStackTrace();
                }
                return;
            }
        }).start();

    }











   public  interface  HttpCallbackListener{
       void  onFinish(int code,String  str);

    }

}

package com.flyingeffects.com.http;


import android.content.Context;
import android.util.Log;

import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.manager.SPHelper;
import com.flyingeffects.com.utils.DESUtil;
import com.flyingeffects.com.utils.RSAUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLDecoder;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by zhangtongju
 * on 2016/11/10 10:28.
 */

public class Api {
    public static ApiService SERVICE;
    private Context context;
    /**
     * 请求超时时间
     */
    private static final int DEFAULT_TIMEOUT = 10000;

    public static ApiService getDefault() {
        if (SERVICE == null) {
//            //手动创建一个OkHttpClient并设置超时时间
//            OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
//            httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
//
//
//
//
//
//            /**
//             *  拦截器
//             */
//            httpClientBuilder.addInterceptor(new Interceptor() {
//                @Override
//                public okhttp3.Response intercept(Chain chain) throws IOException {
//                    Request request = chain.request();
//
////                    Request.Builder requestBuilder = request.newBuilder();
////                    RequestBody formBody = new FormBody.Builder()
////                            .add("1","2")
////                            .build();
//
//                    HttpUrl.Builder authorizedUrlBuilder = request.url()
//                            .newBuilder()
//                            //添加统一参数 如手机唯一标识符,token等
//                            .addQueryParameter("key1","value1")
//                            .addQueryParameter("key2", "value2");
//
//                    Request newRequest = request.newBuilder()
//                            //对所有请求添加请求头
//                            .header("mobileFlag", "adfsaeefe").addHeader("type", "4")
//                            .method(request.method(), request.body())
//                            .url(authorizedUrlBuilder.build())
//                            .build();
//
////                    okhttp3.Response originalResponse = chain.proceed(request);
////                    return originalResponse.newBuilder().header("mobileFlag", "adfsaeefe").addHeader("type", "4").build();
//                    return  chain.proceed(newRequest);
//                }
//            });

//start
//            OkHttpClient sClient = new OkHttpClient();
//            SSLContext sc = null;
//            try {
//                sc = SSLContext.getInstance("SSL");
//                sc.init(null, new TrustManager[]{new X509TrustManager() {
//                    @Override
//                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
//                    }
//
//                    @Override
//                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
//
//                    }
//
//                    @Override
//                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                        return null;
//                    }
//                }}, new SecureRandom());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            HostnameVerifier hv1 = new HostnameVerifier() {
//                @Override
//                public boolean verify(String hostname, SSLSession session) {
//                    return true;
//                }
//            };
//
//            String workerClassName = "okhttp3.OkHttpClient";
//            try {
//                Class workerClass = Class.forName(workerClassName);
//                Field hostnameVerifier = workerClass.getDeclaredField("hostnameVerifier");
//                hostnameVerifier.setAccessible(true);
//                hostnameVerifier.set(sClient, hv1);
//
//                Field sslSocketFactory = workerClass.getDeclaredField("sslSocketFactory");
//                sslSocketFactory.setAccessible(true);
//                sslSocketFactory.set(sClient, sc.getSocketFactory());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }


//end


            SERVICE = new Retrofit.Builder()
//                    .client(httpClientBuilder.build())
                    .client(getUnsafeOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .baseUrl(Url.BASE_URL)
                    .build().create(ApiService.class);
        }

        return SERVICE;
    }


    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            //不验证ssl证书
            final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[]{};
                }
            }};
            // Install the all-trusting trust manager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
            okBuilder.readTimeout(20, TimeUnit.SECONDS);
            okBuilder.connectTimeout(10, TimeUnit.SECONDS);
            okBuilder.writeTimeout(20, TimeUnit.SECONDS);
            okBuilder.addInterceptor(new HttpHeadInterceptor());
            okBuilder.addInterceptor(new ResponseLogInterceptor());
            okBuilder.sslSocketFactory(sslSocketFactory);
            okBuilder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    Log.d("HttpUtils", "==come");
                    return true;
                }
            });

            return okBuilder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    static class ResponseLogInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response response = chain.proceed(chain.request());
            Log.d("ResponseLogInterceptor", "code = : " + response.code());
//            Log.d("ResponseLogInterceptor", "message = : " + response.message());
//            Log.d("ResponseLogInterceptor", "protocol = : " + response.protocol());
            if (response.body() != null && response.body().contentType() != null) {
                MediaType mediaType = response.body().contentType();
                String string = response.body().string();
                Log.d("ResponseLogInterceptor", "mediaType = : " + mediaType.toString());
                Log.d("ResponseLogInterceptor", "string = : " + string);
                ResponseBody responseBody = ResponseBody.create(mediaType, string);
                return response.newBuilder().body(responseBody).build();
            } else {
                return response;
            }
        }
    }


    //拦截器
    static class HttpHeadInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            if (!abc.isEncryption) {
                Request request = chain.request();//获取原始请求
                Request.Builder builder = request.newBuilder();//构建一个新的请求
                builder.addHeader("stoken", "EUMHJlupWwOVBeoXIdiaaH7Y2r8QaGN09sP2VrEdcjc");
                builder.addHeader("secret", "aWsJiYPsUePHZZyTcDwmNzTyaVgSxvhu");
                builder.addHeader("Content-Type", "application/json");
                return chain.proceed(builder.build());
            } else {
//                int num = (int) ((Math.random() * 9 + 1) * 10000000);
                int num = ((getPasswordKey.getTimeKey() + 1) * 10000000);
                PublicKey publicKey = RSAUtil.keyStrToPublicKey(getPasswordKey.getKey());
                String publicEncryptedResult = RSAUtil.encryptDataByPublicKey((num + "_" + getTime()).getBytes(), publicKey);
//            PrivateKey privateKey =  RSAUtil.keyStrToPrivate(abc.SX);
//            String privateDecryptedResult = RSAUtil.decryptedToStrByPrivate(publicEncryptedResult,privateKey);
                Request request = chain.request();
//            RequestBody oldRequestBody = request.body();
//            Buffer requestBuffer = new Buffer();
//            oldRequestBody.writeTo(requestBuffer);
//            String oldBodyStr = requestBuffer.readUtf8();
                String oldBodyStr = null;  //把原来的数据封装成json
                if (request.body() instanceof FormBody) {
                    // 构造新的请求表单
                    FormBody body = (FormBody) request.body();
                    JSONObject object = new JSONObject();
                    for (int i = 0; i < body.size(); i++) {
                        try {
                            object.put(body.encodedName(i), URLDecoder.decode(body.encodedValue(i), "UTF-8"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    oldBodyStr = object.toString();
                }
                String encrypt = null;
                try {
                    encrypt = DESUtil.encode(num + "", oldBodyStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
//            requestBuffer.close();
                if (request.body() instanceof FormBody) {
                    // 构造新的请求表单
                    FormBody.Builder formBuild = new FormBody.Builder();
//                FormBody body = (FormBody) request.body();
                    //将以前的参数添加
//                for (int i = 0; i < body.size(); i++) {
//                    builder2.add(body.encodedName(i), body.encodedValue(i));
//                }
                    //追加新的参数
                    formBuild.add("datas", encrypt);
                    formBuild.add("identifier", publicEncryptedResult);
                    request = request.newBuilder().post(formBuild.build()).build();//构造新的请求体
                }
                Request.Builder builder = request.newBuilder();
                //            MediaType mediaType = MediaType.parse("text/plain; charset=utf-8");
//            String newBodyStr = null;
//            try {
//                newBodyStr = DESUtil.encode("12345678", "13264147335");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            RequestBody newBody = RequestBody.create(mediaType, newBodyStr);
//            builder.post(RequestBody.create(request.body().contentType(), newBodyStr));
                SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
//                String token = spUtil.getString("token", "");
//                if (token != null && !token.equals("")) {
//                    builder.addHeader("usertoken", token);
//                }
//                builder.addHeader("platform", "android");
//                builder.addHeader("shortversion", BaseConstans.getVersionCode());
//                builder.addHeader("uuid", BaseConstans.getUuid());
//                builder.addHeader("wh", BaseConstans.wh);
//                builder.addHeader("nt", BaseConstans.getNt());
                return chain.proceed(builder.build());
            }
        }
    }


    public static String getTime() {
        long time = System.currentTimeMillis() / 1000;//获取系统时间的10位的时间戳
        return String.valueOf(time);

    }
}

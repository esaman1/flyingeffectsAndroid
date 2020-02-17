package com.mobile.CloudMovie.http;

import com.mobile.CloudMovie.enity.HttpResult;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by zhangtongju
 * on 2016/10/9 17:09.
 */

public interface ApiService {

    //登录
    @FormUrlEncoded
    @POST("/api/v1/login")
    Observable<HttpResult<Object>> toLogin(@FieldMap Map<String, String> params);

    //上传素材
    @FormUrlEncoded
    @POST("/api/v1.draft/dputin")
    Observable<HttpResult<Object>> dputin(@FieldMap Map<String, String> params);

    //请求oos
    @FormUrlEncoded
    @POST("/api/v1.record/ossgetpolicy")
    Observable<HttpResult<Object>> getOOS(@FieldMap Map<String, String> params);

    //登录
    @FormUrlEncoded
    @POST("/api/v1.sysinfo/config")
    Observable<HttpResult<Object>> toConfig(@FieldMap Map<String, String> params);













}

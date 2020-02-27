package com.flyingeffects.com.http;

import com.flyingeffects.com.enity.HttpResult;
import com.flyingeffects.com.enity.TemplateType;
import com.flyingeffects.com.enity.new_fag_template_item;

import java.util.List;
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


    @FormUrlEncoded
    @POST("/api/ems/send")
    Observable<HttpResult<Object>> toSms(@FieldMap Map<String, String> params);


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


    //模板类型
    @FormUrlEncoded
    @POST("/api/template/temcategoryList")
    Observable<HttpResult<List<TemplateType>>> getTemplateType(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("/api/template/temcategoryList")
    Observable<HttpResult<List<new_fag_template_item>>> getTemplate(@FieldMap Map<String, String> params);










}

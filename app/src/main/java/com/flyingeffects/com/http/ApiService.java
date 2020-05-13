package com.flyingeffects.com.http;

import com.flyingeffects.com.enity.Config;
import com.flyingeffects.com.enity.ConfigForTemplateList;
import com.flyingeffects.com.enity.HttpResult;
import com.flyingeffects.com.enity.StickerList;
import com.flyingeffects.com.enity.TemplateType;
import com.flyingeffects.com.enity.UserInfo;
import com.flyingeffects.com.enity.checkVersion;
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
    @POST("/api/user/mobilelogin")
    Observable<HttpResult<UserInfo>> toLogin(@FieldMap Map<String, String> params);

    //短信验证码的登录
    @FormUrlEncoded
    @POST("/api/user/login")
    Observable<HttpResult<UserInfo>> toLoginSms(@FieldMap Map<String, String> params);


    //用户自定义裁剪视频当做背景的时候，时长统计
    @FormUrlEncoded
    @POST("/api/template/userDefine")
    Observable<HttpResult<UserInfo>> userDefine(@FieldMap Map<String, String> params);



    @FormUrlEncoded
    @POST("/api/template/allconfig")
    Observable<HttpResult<List<Config>>> configList(@FieldMap Map<String, String> params);



    @FormUrlEncoded
    @POST("/api/template/homeAlertInfo")
    Observable<HttpResult<ConfigForTemplateList>> configListForTemplateList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/api/template/collectionList")
    Observable<HttpResult<List<new_fag_template_item>>> collectionList(@FieldMap Map<String, String> params);



    @FormUrlEncoded
    @POST("/api/media/uploadList")
    Observable<HttpResult<List<Object>>> uploadList(@FieldMap Map<String, String> params);



    @FormUrlEncoded
    @POST("/api/user/cancelUser")
    Observable<HttpResult<Object>> toDelete(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("/api/media/uploadTemplate")
    Observable<HttpResult<Object>> toLoadTemplate(@FieldMap Map<String, String> params);




    @FormUrlEncoded
    @POST("/api/template/keywordList")
    Observable<HttpResult<List<Object>>> keywordList(@FieldMap Map<String, String> params);




    @FormUrlEncoded
    @POST("/api/template/newCollection")
    Observable<HttpResult<Object>> newCollection(@FieldMap Map<String, String> params);



    @FormUrlEncoded
    @POST("/api/template/templateLInfo")
    Observable<HttpResult<new_fag_template_item>> templateLInfo(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("/api/sms/smsCode")
    Observable<HttpResult<Object>> toSms(@FieldMap Map<String, String> params);



    @FormUrlEncoded
    @POST("/api/user/getUserInfo")
    Observable<HttpResult<UserInfo>> getUserInfo(@FieldMap Map<String, String> params);




    //上传素材
    @FormUrlEncoded
    @POST("/api/v1.draft/dputin")
    Observable<HttpResult<Object>> dputin(@FieldMap Map<String, String> params);

    //请求oos
    @FormUrlEncoded
    @POST("/api/v1.record/ossgetpolicy")
    Observable<HttpResult<Object>> getOOS(@FieldMap Map<String, String> params);




    //模板类型
    @FormUrlEncoded
    @POST("/api/template/temcategoryList")
    Observable<HttpResult<List<TemplateType>>> getTemplateType(@FieldMap Map<String, String> params);


    //背景类型
    @FormUrlEncoded
    @POST("/api/template/backCategoryList")
    Observable<HttpResult<List<TemplateType>>> getbackCategoryType(@FieldMap Map<String, String> params);


    //贴纸列表
    @FormUrlEncoded
    @POST("/api/template/stickerslist")
    Observable<HttpResult<List<StickerList>>> getStickerslist(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("/api/template/templateList")
    Observable<HttpResult<List<new_fag_template_item>>> getTemplate(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/api/version/versionCheck")
    Observable<HttpResult<checkVersion>> checkUpdate(@FieldMap Map<String, String> params);











}

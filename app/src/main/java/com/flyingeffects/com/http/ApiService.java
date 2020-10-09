package com.flyingeffects.com.http;

import com.flyingeffects.com.enity.ChooseMusic;
import com.flyingeffects.com.enity.Config;
import com.flyingeffects.com.enity.ConfigForTemplateList;
import com.flyingeffects.com.enity.FontEnity;
import com.flyingeffects.com.enity.HttpResult;
import com.flyingeffects.com.enity.MessageData;
import com.flyingeffects.com.enity.MineCommentEnity;
import com.flyingeffects.com.enity.MineZanEnity;
import com.flyingeffects.com.enity.StickerList;
import com.flyingeffects.com.enity.TemplateType;
import com.flyingeffects.com.enity.UserInfo;
import com.flyingeffects.com.enity.checkVersion;
import com.flyingeffects.com.enity.fansEnity;
import com.flyingeffects.com.enity.messageCount;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.enity.systemessagelist;

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
    @POST("/api/message/systemessagelist")
    Observable<HttpResult<List<systemessagelist>>> systemessagelist(@FieldMap Map<String, String> params);



//    @FormUrlEncoded
//    @POST("/api/message/getUserinfo")
//    Observable<HttpResult<List<systemessagelist>>> getUserinfo(@FieldMap Map<String, String> params);



    @FormUrlEncoded
    @POST("/api/media/deleteTemplate")
    Observable<HttpResult<Object>> deleteBackground(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("/api/message/getMyProduction")
    Observable<HttpResult<List<new_fag_template_item>>> uploadList(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("/api/user/cancelUser")
    Observable<HttpResult<Object>> toDelete(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("/api/message/uploadTemplate")
    Observable<HttpResult<Object>> toLoadTemplate(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("/api/message/uploadSearchResult")
    Observable<HttpResult<Object>> uploadSearchResult(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("/api/template/keywordList")
    Observable<HttpResult<List<Object>>> keywordList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/api/message/musicKeyword")
    Observable<HttpResult<List<Object>>> musicKeyword(@FieldMap Map<String, String> params);




    @FormUrlEncoded
    @POST("/api/template/newCollection")
    Observable<HttpResult<Object>> newCollection(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/api/message/addPraise")
    Observable<HttpResult<Object>> addPraise(@FieldMap Map<String, String> params);





    @FormUrlEncoded
    @POST("/api/template/templateLInfo")
    Observable<HttpResult<new_fag_template_item>> templateLInfo(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("/api/sms/smsCode")
    Observable<HttpResult<Object>> toSms(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("/api/template/saveTemplate")
    Observable<HttpResult<Object>> saveTemplate(@FieldMap Map<String, String> params);

//    @FormUrlEncoded
//    @POST("/api/user/getUserInfo")
//    Observable<HttpResult<UserInfo>> getUserInfo(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("/api/message/templateComment")  //List<MessageEnity>
    Observable<HttpResult<MessageData>> templateComment(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("/api/message/getUserinfo")  //List<MessageEnity>
    Observable<HttpResult<UserInfo>> getOtherUserinfo(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("/api/message/addComment")
    Observable<HttpResult<Object>> addComment(@FieldMap Map<String, String> params);




    @FormUrlEncoded
    @POST("/api/message/allMessageNum")
    Observable<HttpResult<messageCount>> getAllMessageNum(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/api/message/followList")
    Observable<HttpResult<List<fansEnity>>> getFollowList(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("/api/message/followerList")
    Observable<HttpResult<List<fansEnity>>> followerList(@FieldMap Map<String, String> params);


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


//
//    //用戶的关注数或者粉丝数
//    @FormUrlEncoded
//    @POST("/api/message/followerList")
//    Observable<HttpResult<List<MyProduction>>> followerList(@FieldMap Map<String, String> params);


    //我发布的作品和我喜欢的作品
    @FormUrlEncoded
    @POST("/api/message/getMyProduction")
    Observable<HttpResult<List<new_fag_template_item>>> getMyProduction(@FieldMap Map<String, String> params);


    //请求我的评论列表
    @FormUrlEncoded
    @POST("/api/message/commentList")
    Observable<HttpResult<List<MineCommentEnity>>> commentList(@FieldMap Map<String, String> params);



    //删除我的评论列表
    @FormUrlEncoded
    @POST("/api/message/delComment")
    Observable<HttpResult<Object>> delComment(@FieldMap Map<String, String> params);

    //我的赞列表
    @FormUrlEncoded
    @POST("/api/message/praiseList")
    Observable<HttpResult<List<MineZanEnity>>> praiseList(@FieldMap Map<String, String> params);


    //关注和取消关注

    @FormUrlEncoded
    @POST("/api/message/followUser")
    Observable<HttpResult<Object>> followUser(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("/api/version/versionCheck")
    Observable<HttpResult<checkVersion>> checkUpdate(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/api/message/commentcheck")
    Observable<HttpResult<Object>> commentcheck(@FieldMap Map<String, String> params);



    @FormUrlEncoded
    @POST("/api/message/musicList")
    Observable<HttpResult<List<ChooseMusic>>> musicList(@FieldMap Map<String, String> params);



    //音乐收藏和取消收藏

    @FormUrlEncoded
    @POST("/api/message/collectMusic")
    Observable<HttpResult<Object>> collectMusic(@FieldMap Map<String, String> params);

    //收藏音乐列表
    @FormUrlEncoded
    @POST("/api/message/musicCollectionList")
    Observable<HttpResult<List<ChooseMusic>>> musicCollectionList(@FieldMap Map<String, String> params);


    //请求字体
    @FormUrlEncoded
    @POST("/api/message/fontList")
    Observable<HttpResult<List<FontEnity>>> fontList(@FieldMap Map<String, String> params);

    //收藏音乐列表
    @FormUrlEncoded
    @POST("/api/message/fontImage")
    Observable<HttpResult<List<FontEnity>>> fontImage(@FieldMap Map<String, String> params);


    //收藏音乐列表
    @FormUrlEncoded
    @POST("/api/message/systemessageinfo")
    Observable<HttpResult<Object>> systemessageinfo(@FieldMap Map<String, String> params);






}

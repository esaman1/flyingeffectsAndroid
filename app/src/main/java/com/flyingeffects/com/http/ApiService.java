package com.flyingeffects.com.http;

import com.flyingeffects.com.entity.ChooseMusic;
import com.flyingeffects.com.entity.Config;
import com.flyingeffects.com.entity.ConfigForTemplateList;
import com.flyingeffects.com.entity.DressUpSpecial;
import com.flyingeffects.com.entity.FirstLevelTypeEntity;
import com.flyingeffects.com.entity.FontColor;
import com.flyingeffects.com.entity.FontEnity;
import com.flyingeffects.com.entity.HttpResult;
import com.flyingeffects.com.entity.HumanMerageResult;
import com.flyingeffects.com.entity.ImageFrameEntity;
import com.flyingeffects.com.entity.JadeTypeFace;
import com.flyingeffects.com.entity.MessageData;
import com.flyingeffects.com.entity.MineCommentEnity;
import com.flyingeffects.com.entity.MineZanEnity;
import com.flyingeffects.com.entity.NewFragmentTemplateItem;
import com.flyingeffects.com.entity.PayEntity;
import com.flyingeffects.com.entity.PriceListEntity;
import com.flyingeffects.com.entity.SearchTemplateInfoEntity;
import com.flyingeffects.com.entity.SearchUserEntity;
import com.flyingeffects.com.entity.StickerList;
import com.flyingeffects.com.entity.StickerTypeEntity;
import com.flyingeffects.com.entity.SubtitleEntity;
import com.flyingeffects.com.entity.SystemMessageCountAllEntiy;
import com.flyingeffects.com.entity.SystemMessageDetailAllEnity;
import com.flyingeffects.com.entity.UserInfo;
import com.flyingeffects.com.entity.VideoFusiomBean;
import com.flyingeffects.com.entity.checkVersion;
import com.flyingeffects.com.entity.fansEnity;
import com.flyingeffects.com.entity.messageCount;
import com.flyingeffects.com.entity.systemessagelist;

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
    Observable<HttpResult<List<NewFragmentTemplateItem>>> collectionList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/api/message/systemessagelist")
    Observable<HttpResult<List<systemessagelist>>> systemessagelist(@FieldMap Map<String, String> params);



    @FormUrlEncoded
    @POST("/api/message/systemTotal")
    Observable<HttpResult<SystemMessageCountAllEntiy>> systemTotal(@FieldMap Map<String, String> params);




//    @FormUrlEncoded
//    @POST("/api/message/getUserinfo")
//    Observable<HttpResult<List<systemessagelist>>> getUserinfo(@FieldMap Map<String, String> params);



    @FormUrlEncoded
    @POST("/api/media/deleteTemplate")
    Observable<HttpResult<Object>> deleteBackground(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("/api/message/getMyProduction")
    Observable<HttpResult<List<NewFragmentTemplateItem>>> uploadList(@FieldMap Map<String, String> params);


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
    @POST("/api/message/uploadHumanTemplate")
    Observable<HttpResult<Object>> uploadHumanTemplate(@FieldMap Map<String, String> params);




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
    Observable<HttpResult<NewFragmentTemplateItem>> templateLInfo(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("/api/sms/smsCode")
    Observable<HttpResult<Object>> toSms(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/api/index/add_active")
    Observable<HttpResult<Object>> add_active(@FieldMap Map<String, String> params);




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
    @POST("/api/message/allMessageNumNew")
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


    //模板 背景 换脸  一二级分类类型
    @FormUrlEncoded
    @POST("/api/mearge/categoryList")
    Observable<HttpResult<List<FirstLevelTypeEntity>>> getCategoryList(@FieldMap Map<String, String> params);

    //贴纸列表
    @FormUrlEncoded
    @POST("/api/media/stickerList")
    Observable<HttpResult<List<StickerList>>> getStickerslist(@FieldMap Map<String, String> params);


    //贴纸列表2
    @FormUrlEncoded
    @POST("/api/media/camerstickerList")
    Observable<HttpResult<List<StickerList>>> camerstickerList(@FieldMap Map<String, String> params);



    @FormUrlEncoded
    @POST("/api/template/templateListNew")
    Observable<HttpResult<List<NewFragmentTemplateItem>>> getTemplate(@FieldMap Map<String, String> params);



    @FormUrlEncoded
    @POST("/api/mearge/templateList")
    Observable<HttpResult<List<NewFragmentTemplateItem>>> getMeargeTemplate(@FieldMap Map<String, String> params);


    /**
     * 素材列表
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("/api/mearge/materialList")
    Observable<HttpResult<List<NewFragmentTemplateItem>>> materialList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/api/mearge/imageBorder")
    Observable<HttpResult<List<ImageFrameEntity>>> imageBorder(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("/api/photo/photoList")
    Observable<HttpResult<List<NewFragmentTemplateItem>>> photoList(@FieldMap Map<String, String> params);

//
//    //用戶的关注数或者粉丝数
//    @FormUrlEncoded
//    @POST("/api/message/followerList")
//    Observable<HttpResult<List<MyProduction>>> followerList(@FieldMap Map<String, String> params);

    //我发布的作品和我喜欢的作品
    @FormUrlEncoded
    @POST("/api/message/getMyProduction")
    Observable<HttpResult<List<NewFragmentTemplateItem>>> getMyProduction(@FieldMap Map<String, String> params);

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


    //请求边框
    @FormUrlEncoded
    @POST("/api/message/fontBorder")
    Observable<HttpResult<List<FontEnity>>> fontBorder(@FieldMap Map<String, String> params);


    //收藏音乐列表
    @FormUrlEncoded
    @POST("/api/message/fontImage")
    Observable<HttpResult<List<FontEnity>>> fontImage(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("/api/message/systeMessageDetail")
    Observable<HttpResult<SystemMessageDetailAllEnity>> systemessageinfo(@FieldMap Map<String, String> params);

    /**上传用户皮肤*/
    @FormUrlEncoded
    @POST("/api/message/changeSkin")
    Observable<HttpResult<Object>> uploadUserSkin(@FieldMap Map<String, String> params);

    /**修改个人信息*/
    @FormUrlEncoded
    @POST("/api/user/member_edit")
    Observable<HttpResult<Object>> memberEdit(@FieldMap Map<String,String> params);

    /**模板关键字列表*/
    @FormUrlEncoded
    @POST("/api/template/keywords")
    Observable<HttpResult<List<SearchTemplateInfoEntity>>> templateKeywords(@FieldMap Map<String,String> params);

    /**模板操作行为统计*/
    @FormUrlEncoded
    @POST("/api/message/shareFriend")
    Observable<HttpResult<Object>> templateBehaviorStatistics(@FieldMap Map<String,String> params);

    /**搜索用户列表*/
    @FormUrlEncoded
    @POST("/api/template/userList")
    Observable<HttpResult<List<SearchUserEntity>>> getSearchUserList(@FieldMap Map<String,String> params);



    /**融合api*/
    @FormUrlEncoded
    @POST("/api/mearge/meargeHuman")
    Observable<HttpResult<String>> meargeHuman(@FieldMap Map<String,String> params);


    /**融合api*/
    @FormUrlEncoded
    @POST("/api/mearge/meargePicture")
    Observable<HttpResult<List<HumanMerageResult>>> humanMerageResult(@FieldMap Map<String,String> params);


    /**融合api*/
    @FormUrlEncoded
    @POST("/api/Api/query")
    Observable<HttpResult<DressUpSpecial>> Apiquery(@FieldMap Map<String,String> params);

    /**通知服务器上传成功*/
    @FormUrlEncoded
    @POST("/api/mearge/animalImage")
    Observable<HttpResult<String>> animalImage(@FieldMap Map<String,String> params);


    /**请求结果*/
    @FormUrlEncoded
    @POST("/api/mearge/animalResult")
    Observable<HttpResult<VideoFusiomBean>> animalResult(@FieldMap Map<String,String> params);




    /**贴纸分类列表*/
    @FormUrlEncoded
    @POST("/api/media/stickerCategory")
    Observable<HttpResult<List<StickerTypeEntity>>> getStickerTypeList(@FieldMap Map<String,String> params);




    /**拍摄贴纸分类列表*/
    @FormUrlEncoded
    @POST("/api/media/camerStickerCategoryList")
    Observable<HttpResult<List<StickerTypeEntity>>> camerStickerCategoryList(@FieldMap Map<String,String> params);






    /**贴纸分类列表*/
    @FormUrlEncoded
    @POST("/api/mearge/template_ids")
    Observable<HttpResult<List<String>>> template_ids(@FieldMap Map<String,String> params);


    /**统计消息*/
    @FormUrlEncoded
    @POST("/api/message/addTimes")
    Observable<HttpResult<Object>> addTimes(@FieldMap Map<String,String> params);



    /**腾讯api 联调，通过上传的图片，生成有趣的视频*/
    @FormUrlEncoded
    @POST("/api/Api/test")
    Observable<HttpResult<DressUpSpecial>> ApiTest(@FieldMap Map<String,String> params);

    /**
     * 价格列表-购买会员
     */
    @FormUrlEncoded
    @POST("/api/order/goodsList")
    Observable<HttpResult<List<PriceListEntity>>> getVipPriceList(@FieldMap Map<String, String> params);

    /**
     * 提交订单
     */
    @FormUrlEncoded
    @POST("/api/order/addorder")
    Observable<HttpResult<PayEntity>> addOrder(@FieldMap Map<String, String> params);

    /**识别音频中的字幕*/
    @FormUrlEncoded
    @POST("/api/yitu/identify")
    Observable<HttpResult<List<SubtitleEntity>>> identifySubtitle(@FieldMap Map<String,String> params);

  /**识别音频中的字幕*/
    @FormUrlEncoded
    @POST("/api/message/fontcolor")
    Observable<HttpResult<List<FontColor>>> fontColor(@FieldMap Map<String,String> params);
 @FormUrlEncoded
    @POST("/api/message/fontstylelist")
    Observable<HttpResult<List<JadeTypeFace>>> fontstylelist(@FieldMap Map<String,String> params);


}

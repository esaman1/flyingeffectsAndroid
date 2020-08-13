package com.flyingeffects.com.enity;

import com.bytedance.sdk.openadsdk.TTNativeExpressAd;

import java.io.Serializable;

public class new_fag_template_item implements Serializable {

    private String id;

    public String getId() {
        return id;
    }



    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVidoefile() {
        return vidoefile;
    }

    public void setVidoefile(String vidoefile) {
        this.vidoefile = vidoefile;
    }

    public String getTemcategory_id() {
        return temcategory_id;
    }

    public void setTemcategory_id(String temcategory_id) {
        this.temcategory_id = temcategory_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }


    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getReading() {
        return reading;
    }

    public void setReading(String reading) {
        this.reading = reading;
    }

    public String getReading2() {
        return reading2;
    }

    public void setReading2(String reading2) {
        this.reading2 = reading2;
    }


    public String getMbsearch() {
        return mbsearch;
    }

    public void setMbsearch(String mbsearch) {
        this.mbsearch = mbsearch;
    }

    public String getIos_diversion() {
        return ios_diversion;
    }

    public void setIos_diversion(String ios_diversion) {
        this.ios_diversion = ios_diversion;
    }


    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getAuth_image() {
        return auth_image;
    }

    public void setAuth_image(String auth_image) {
        this.auth_image = auth_image;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getTemplatefile() {
        return templatefile;
    }

    public void setTemplatefile(String templatefile) {
        this.templatefile = templatefile;
    }

    public String getZipid() {
        return zipid;
    }

    public void setZipid(String zipid) {
        this.zipid = zipid;
    }

    private String title;
    private String image;
    private String vidoefile;
//
//    public String getMinapp_share_title() {
//        return minapp_share_title;
//    }
//
//    public void setMinapp_share_title(String minapp_share_title) {
//        this.minapp_share_title = minapp_share_title;
//    }

//    private String minapp_share_title;
    private String temcategory_id;
    /**
     * 1需要激励视频，0 不需要激励视频
     */
    private String type;
    private String sort;

    public int getTest() {
        return test;
    }

    public void setTest(int test) {
        this.test = test;
    }

    private int test;
    private String preview;
    private String reading;
    private String reading2;


    public int getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(int template_id) {
        this.template_id = template_id;
    }

    /**
     * 我的页面收藏id
     */
    private int template_id;

    public String getVideotime() {
        return videotime;
    }

    public void setVideotime(String videotime) {
        this.videotime = videotime;
    }

    private String videotime;

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    private long create_time;
    private String mbsearch;
    private String ios_diversion;

    public int getIs_picout() {
        return is_picout;
    }

    public void setIs_picout(int is_picout) {
        this.is_picout = is_picout;
    }


    /**
     * 是否需要抠图，0不需要，1 需要
     */
    private int is_picout;

    public int getDefaultnum() {
        return defaultnum;
    }

    public void setDefaultnum(int defaultnum) {
        this.defaultnum = defaultnum;
    }

    private int defaultnum;
    private String auth;
    private String auth_image;
    private String remark;
    private String collection;
    private String templatefile;
    private String zipid;

    public int getIs_collection() {
        return is_collection;
    }

    public void setIs_collection(int is_collection) {
        this.is_collection = is_collection;
    }

    //收藏状态 0表示未收藏 1表示收藏
    private int is_collection;

    public int getIs_anime() {
        return is_anime;
    }

    public void setIs_anime(int is_anime) {
        this.is_anime = is_anime;
    }

    //1 表示是漫画，0 表示是普通类
    private int is_anime = 0;

    public TTNativeExpressAd getAd() {
        return ad;
    }

    public void setAd(TTNativeExpressAd ad) {
        this.ad = ad;
    }


    /**
     * 信息流广告
     */
    public TTNativeExpressAd ad;

    public String getOriginfile() {
        return originfile;
    }

    public void setOriginfile(String originfile) {
        this.originfile = originfile;
    }

    /**
     * 一鍵模板的封面图
     */
    public String originfile;


    public String getPraise() {
        return praise;
    }

    public void setPraise(String praise) {
        this.praise = praise;
    }

    public String getShare() {
        return share;
    }

    public void setShare(String share) {
        this.share = share;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    //赞
    public String praise;
    //分享
    public String share;
    //评论
    public String comment;

    //举报
    public String report;



    public int getIs_praise() {
        return is_praise;
    }

    public void setIs_praise(int is_praise) {
        this.is_praise = is_praise;
    }
    //是否赞过
    private int is_praise;


    public String getTemplate_type() {
        return template_type;
    }

    public void setTemplate_type(String template_type) {
        this.template_type = template_type;
    }

    private String template_type;

    public String getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(String admin_id) {
        this.admin_id = admin_id;
    }

    private String admin_id;





}

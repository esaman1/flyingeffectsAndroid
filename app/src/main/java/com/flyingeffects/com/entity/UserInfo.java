package com.flyingeffects.com.entity;

import java.io.Serializable;

public class UserInfo implements Serializable {
    public static final String USER_INFO_KEY = "UserInfo";

    private String id;
    private String token;
    private String skin;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getQq_openid() {
        return qq_openid;
    }

    public void setQq_openid(String qq_openid) {
        this.qq_openid = qq_openid;
    }

    public String getIos_openid() {
        return ios_openid;
    }

    public void setIos_openid(String ios_openid) {
        this.ios_openid = ios_openid;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }

    public int getIs_vip() {
        return is_vip;
    }

    public void setIs_vip(int is_vip) {
        this.is_vip = is_vip;
    }

    public String getVip_type() {
        return vip_type;
    }

    public void setVip_type(String vip_type) {
        this.vip_type = vip_type;
    }

    public int getVip_grade() {
        return vip_grade;
    }

    public void setVip_grade(int vip_grade) {
        this.vip_grade = vip_grade;
    }

    public long getVip_end_time() {
        return vip_end_time;
    }

    public void setVip_end_time(long vip_end_time) {
        this.vip_end_time = vip_end_time;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getIdfa() {
        return idfa;
    }

    public void setIdfa(String idfa) {
        this.idfa = idfa;
    }

    public String getReg_ip() {
        return reg_ip;
    }

    public void setReg_ip(String reg_ip) {
        this.reg_ip = reg_ip;
    }

    public String getLast_login_ip() {
        return last_login_ip;
    }

    public void setLast_login_ip(String last_login_ip) {
        this.last_login_ip = last_login_ip;
    }

    public String getLast_login_time() {
        return last_login_time;
    }

    public void setLast_login_time(String last_login_time) {
        this.last_login_time = last_login_time;
    }

    public String getLast_open_time() {
        return last_open_time;
    }

    public void setLast_open_time(String last_open_time) {
        this.last_open_time = last_open_time;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getRemark() {
        return profile;
    }

    public void setRemark(String remark) {
        this.profile = remark;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public String getIs_test() {
        return is_test;
    }

    public void setIs_test(String is_test) {
        this.is_test = is_test;
    }

    public String getIs_report() {
        return is_report;
    }

    public void setIs_report(String is_report) {
        this.is_report = is_report;
    }

    private String openid;
    private String qq_openid;
    private String ios_openid;
    private String unionid;
    private String email;
    private String mobile;
    private String nickname;
    private String photourl;
    private int is_vip;
    private String vip_type;
    private int vip_grade;
    private long vip_end_time;
    private String channel;
    private String version;
    private String platform;
    private String imei;
    private String idfa;
    private String reg_ip;
    private String last_login_ip;
    private String last_login_time;
    private String last_open_time;
    private String modified;
    private String profile;
    private String create_time;
    private String enabled;
    private String is_test;
    private String is_report;


    public String getIs_sign() {
        return is_sign;
    }

    public void setIs_sign(String is_sign) {
        this.is_sign = is_sign;
    }

    public String getIs_has_follow() {
        return is_has_follow;
    }

    public void setIs_has_follow(String is_has_follow) {
        this.is_has_follow = is_has_follow;
    }

    public String getUser_follower() {
        return user_follower;
    }

    public void setUser_follower(String user_follower) {
        this.user_follower = user_follower;
    }

    public String getUser_watch() {
        return user_watch;
    }

    public void setUser_watch(String user_watch) {
        this.user_watch = user_watch;
    }

    public String getUser_video() {
        return user_video;
    }

    public void setUser_video(String user_video) {
        this.user_video = user_video;
    }

    public String getUser_praise() {
        return user_praise;
    }

    public void setUser_praise(String user_praise) {
        this.user_praise = user_praise;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    private String is_sign;
    private String is_has_follow;
    private String user_follower;
    private String user_watch;
    private String user_video;
    private String user_praise;


}

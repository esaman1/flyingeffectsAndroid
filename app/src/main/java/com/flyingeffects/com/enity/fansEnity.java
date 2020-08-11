package com.flyingeffects.com.enity;

import java.io.Serializable;

public class fansEnity implements Serializable {







    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getIs_read() {
        return is_read;
    }

    public void setIs_read(String is_read) {
        this.is_read = is_read;
    }

    public String getTo_user_id() {
        return to_user_id;
    }

    public void setTo_user_id(String to_user_id) {
        this.to_user_id = to_user_id;
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


    private String user_id;
    private String create_time;
    private String status;
    private String update_time;
    private String is_read;
    private String to_user_id;
    private String nickname;
    private String photourl;

    public int getIs_has_follow() {
        return is_has_follow;
    }

    public void setIs_has_follow(int is_has_follow) {
        this.is_has_follow = is_has_follow;
    }


    //0表示自己还未关注，1表示已经关注了
    private int is_has_follow;


}

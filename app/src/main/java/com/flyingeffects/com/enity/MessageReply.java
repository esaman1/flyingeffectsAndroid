package com.flyingeffects.com.enity;

import java.io.Serializable;

public class MessageReply implements Serializable {

    private String id;
    private int reply_id;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getIs_read() {
        return is_read;
    }

    public void setIs_read(String is_read) {
        this.is_read = is_read;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTo_user_id() {
        return to_user_id;
    }

    public void setTo_user_id(String to_user_id) {
        this.to_user_id = to_user_id;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
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

    public String getTo_user_nickname() {
        return to_user_nickname;
    }

    public void setTo_user_nickname(String to_user_nickname) {
        this.to_user_nickname = to_user_nickname;
    }

    public String getTo_user_photourl() {
        return to_user_photourl;
    }

    public void setTo_user_photourl(String to_user_photourl) {
        this.to_user_photourl = to_user_photourl;
    }

    public boolean isReply() {
        return reply_id > 0 && reply_id != pid;
    }

    private String user_id;
    private String create_time;
    private String status;
    private String update_time;
    private String type;
    private int pid;
    private String template_id;
    private String is_read;
    private String content;
    private String to_user_id;
    private String report;
    private String nickname;
    private String photourl;
    private String to_user_nickname;
    private String to_user_photourl;


}

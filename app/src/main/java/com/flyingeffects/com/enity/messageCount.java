package com.flyingeffects.com.enity;

import java.io.Serializable;

public class messageCount implements Serializable {

    private String follow_num;

    public String getFollow_num() {
        return follow_num;
    }

    public void setFollow_num(String follow_num) {
        this.follow_num = follow_num;
    }

    public String getPraise_num() {
        return praise_num;
    }

    public void setPraise_num(String praise_num) {
        this.praise_num = praise_num;
    }

    public String getComment_num() {
        return comment_num;
    }

    public void setComment_num(String comment_num) {
        this.comment_num = comment_num;
    }

    public String getMessage_num() {
        return message_num;
    }

    public void setMessage_num(String message_num) {
        this.message_num = message_num;
    }

    public String getAll_num() {
        return all_num;
    }

    public void setAll_num(String all_num) {
        this.all_num = all_num;
    }

    private String praise_num;
    private String comment_num;
    private String message_num;
    private String all_num;
}

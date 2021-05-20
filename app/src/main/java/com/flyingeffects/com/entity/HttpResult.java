package com.flyingeffects.com.entity;

import java.io.Serializable;

/**
 * Created by zhangtongju
 * on 2016/10/10 11:44.
 * 实体的基类
 */


public class HttpResult<T> implements Serializable {

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    private int code;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private String msg;


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    private T data;

}

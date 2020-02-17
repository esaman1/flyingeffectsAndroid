package com.mobile.CloudMovie.enity;

import java.io.Serializable;

/**
 * Created by zhangtongju
 * on 2016/10/10 11:44.
 * 实体的基类
 */


public class HttpResult<T> implements Serializable {
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private int status;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private String msg;

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    private T result;










}

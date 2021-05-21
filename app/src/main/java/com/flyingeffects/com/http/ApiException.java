package com.flyingeffects.com.http;

import android.content.Intent;

import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.ui.view.activity.LoginActivity;
import com.flyingeffects.com.utils.LogUtil;

/**
 * Created by zhangtongju
 * on 2016/10/10 11:52.
 */

public class ApiException extends RuntimeException{
    private String error;
    public static final int USER_NOT_EXIST = 100;
    public static final int WRONG_PASSWORD = 101;
    private static String message;


    public ApiException(int resultCode,String error) {
        this(getApiExceptionMessage(resultCode,error,""));
    }

    public ApiException(int resultCode,String error,String actTag) {
        this(getApiExceptionMessage(resultCode,error,actTag));
    }


    public ApiException(String detailMessage) {
        super(detailMessage);
    }

    @Override
    public String getMessage() {
        return message;
    }

    /**
     * 由于服务器传递过来的错误信息直接给用户看的话，用户未必能够理解
     * 需要根据错误码对错误信息进行一个转换，在显示给用户
     * @param code
     * @return
     */
    private static String getApiExceptionMessage(int code,String error,String actTag){
        LogUtil.d("OOM","code="+code);
        switch (code) {

            case USER_NOT_EXIST:
                message = "该用户不存在";
                break;
            case WRONG_PASSWORD:
                message = "密码错误";
                break;
            case 1000:
                message = "取消dialog";
                break;
            case 201:
                message = error;
                break;
            case -1:
                LogUtil.d("OOM","接口提示要重新登录");
                intoLoginAct();
                message =error;  //用戶被刪除
                break;

            default:
                message = error;
        }
        return message;
    }




    private static void intoLoginAct(){
        if(!DoubleClick.getInstance().isFastZDYDoubleClick(3000)){
            BaseConstans.setUserToken("");
            Intent toLogin=new Intent(BaseApplication.getInstance(),LoginActivity.class);
            toLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            BaseApplication.getInstance().startActivity(toLogin);
        }
    }




}

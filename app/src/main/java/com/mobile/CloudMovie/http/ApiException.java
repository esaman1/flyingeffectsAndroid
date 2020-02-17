package com.mobile.CloudMovie.http;

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
            case 2003:
                intoLoginAct(actTag);
                message ="重新登录";  //token is invalid  和另一台设备登录
                break;

            default:
                message = error;
        }
        return message;
    }




    private static void intoLoginAct(String actTag){

    }




}

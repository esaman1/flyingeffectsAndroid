package com.flyingeffects.com.http;

import com.flyingeffects.com.constans.BaseConstans;

public class Url {

    /**
     * user :TongJu  ;描述：当前使用的地址
     * 时间：2018/6/15
     * PRODUCTION true为正式服务器地址 false为测试服务器地址
     **/

    public static final String BASE_URL = BaseConstans.PRODUCTION ?
            "http://www.flyingeffect.com" : "http://test.flyingeffect.com";

}




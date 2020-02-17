package com.mobile.CloudMovie.http;


import com.mobile.CloudMovie.http.encryption.AuthCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * user :TongJu  ; email:jutongzhang@sina.com
 * time：2018/10/17
 * describe:key
 **/
public class abc {

    public static final boolean isEncryption = false;


    public static final String API_SALT = "123456ABCDEFGHIJKL{(&#!,.&*)}MNOPQRSTUVWXYZ7890";
    public static final String API_AES_KEY = "362DA87FA3E89A95";


    public static  String sign(Map<String, String> par) {
        StringBuilder result = new StringBuilder();
        Iterator<String> keys = par.keySet().iterator();
        List<String> strings = new ArrayList<>();
        while (keys.hasNext()) {
            strings.add(keys.next());
        }
        Collections.sort(strings, (o1, o2) -> o1.compareTo(o2));
        for (int i = 0; i < strings.size(); i++) {
            String name = strings.get(i);
            String value = par.get(name);
            result.append(String.format("%s=%s&", name, value));
        }
        result.append(abc.API_SALT);
        String fResult = result.toString();
        fResult = AuthCode.MD5(fResult);
        return fResult;
    }


}

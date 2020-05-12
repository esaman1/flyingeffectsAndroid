package com.flyingeffects.com.manager;

import android.util.Log;

import com.flyingeffects.com.utils.LogUtil;
import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.HeaderResponse;

import java.io.File;
import java.io.IOException;

public class huaweiObs {


    static huaweiObs instance;

    public static huaweiObs getInstance() {
        if (instance == null) {
            instance = new huaweiObs();
        }
        return instance;
    }



    public void uploadFileToHawei(String filePath,String fileName,Callback callback ) {
        LogUtil.d("PutObject","filePath="+filePath+"fileName="+fileName);
        // 您的工程中可以只保留一个全局的ObsClient实例
// ObsClient是线程安全的，可在并发场景下使用
        ObsClient obsClient = null;
        try {
            String endPoint = "obs.cn-south-1.myhuaweicloud.com";
            String ak = "TNE4QLBWCKNT8EOKDVX8";
            String sk = "swy5ml0mP5S7HIHydGuEVdD0pwqDcJi1PxN4aZai";
            // 创建ObsClient实例
            obsClient = new ObsClient(ak, sk, endPoint);
            // 调用接口进行操作，例如上传对象，其中localfile为待上传的本地文件路径，需要指定到具体的文件名
            HeaderResponse response = obsClient.putObject("flying", fileName, new File(filePath));
            callback.isSuccess(response.toString());

        } catch (ObsException e) {
            Log.e("PutObject", "ObsException" + e.getMessage());
            Log.e("PutObject", "Response Code: " + e.getResponseCode());
            Log.e("PutObject", "Error Message: " + e.getErrorMessage());
            Log.e("PutObject", "Error Code:       " + e.getErrorCode());
            Log.e("PutObject", "Request ID:      " + e.getErrorRequestId());
            Log.e("PutObject", "Host ID:           " + e.getErrorHostId());
        } finally {
            // 关闭ObsClient实例，如果是全局ObsClient实例，可以不在每个方法调用完成后关闭
            // ObsClient在调用ObsClient.close方法关闭后不能再次使用
            if (obsClient != null) {
                try {
                    obsClient.close();
                } catch (IOException e) {
                }
            }
        }
    }


  public  interface  Callback{
        void isSuccess(String str);
    }




}

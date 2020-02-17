package com.mobile.flyingeffects.ui.model;

import android.content.Context;

import com.mobile.flyingeffects.base.ActivityLifeCycleEvent;
import com.mobile.flyingeffects.constans.BaseConstans;
import com.mobile.flyingeffects.enity.TemplateType;
import com.mobile.flyingeffects.http.Api;
import com.mobile.flyingeffects.http.HttpUtil;
import com.mobile.flyingeffects.http.ProgressSubscriber;
import com.mobile.flyingeffects.ui.interfaces.model.home_fagMvpCallback;
import com.mobile.flyingeffects.utils.ToastUtil;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;


public class home_fagMvpModel {
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private home_fagMvpCallback callback;
    private Context context;


    public home_fagMvpModel(Context context, home_fagMvpCallback callback) {
        this.context = context;
        this.callback = callback;
    }


    public void getFragmentList() {
        requestData();
    }
    ArrayList<TemplateType> data;
    //得到banner缓存数据
    private  void requestData() {
        data= Hawk.get("mainData", new ArrayList<>());
        if (data != null) {
            callback.setFragmentList(data);
            requestMainData(false); //首页杂数据
        } else {
            requestMainData(true); //首页杂数据
        }
    }




    private void requestMainData(boolean isShowDialog) {
//        ArrayList<TemplateType>data=new ArrayList<>();
//        for (int i=0;i<3;i++){
//            TemplateType type=new TemplateType();
//            type.setName("测试1");
//            data.add(type);
//        }
//        callback.setFragmentList(data);


        HashMap<String, String> params = new HashMap<>();
        Observable ob = Api.getDefault().getTemplateType(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<TemplateType>>(context) {
            @Override
            protected void _onError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(List<TemplateType> data) {

                callback.setFragmentList(data);

            }
        }, "mainData", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, true, true, isShowDialog);
    }








}

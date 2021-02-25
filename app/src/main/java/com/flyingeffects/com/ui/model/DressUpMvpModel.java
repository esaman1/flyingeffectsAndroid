package com.flyingeffects.com.ui.model;

import android.content.Context;

import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.FirstLevelTypeEntity;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.ui.interfaces.model.DressUpMvpCallback;
import com.flyingeffects.com.ui.interfaces.model.home_fagMvpCallback;
import com.flyingeffects.com.utils.ToastUtil;

import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;


public class DressUpMvpModel {
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private DressUpMvpCallback callback;
    private Context context;


    public DressUpMvpModel(Context context, DressUpMvpCallback callback) {
        this.context = context;
        this.callback = callback;
    }


    public void getFragmentList() {
        requestData();
    }

    private  void requestData() {
        requestMainData(false); //首页杂数据
    }

    private void requestMainData(boolean isShowDialog) {
        HashMap<String, String> params = new HashMap<>();
        //类型 1模板 2背景 3换脸
        params.put("type","3");
        Observable ob = Api.getDefault().getCategoryList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<FirstLevelTypeEntity>>(context) {
            @Override
            protected void onSubError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void onSubNext(List<FirstLevelTypeEntity> data) {

                callback.setFragmentList(data);

            }
        }, "mainData", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, true, true, isShowDialog);
    }
}

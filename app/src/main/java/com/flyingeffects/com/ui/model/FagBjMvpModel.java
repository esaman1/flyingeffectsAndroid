package com.flyingeffects.com.ui.model;

import android.content.Context;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.TemplateType;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.ui.interfaces.model.FagBjMvpCallback;
import com.flyingeffects.com.utils.ToastUtil;

import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;


public class FagBjMvpModel {
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private FagBjMvpCallback callback;
    private Context context;
    private BottomSheetDialog bottomSheetDialog;


    public FagBjMvpModel(Context context, FagBjMvpCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    //得到banner缓存数据
    public  void requestData() {
//        ArrayList<TemplateType> cacheTemplateData= Hawk.get("bjHeadData", new ArrayList<>());
//        if (cacheTemplateData != null) {
//            callback.setFragmentList(cacheTemplateData);
//            requestMainData(false); //首页杂数据
//        } else {
//            requestMainData(true); //首页杂数据
//        }
        requestMainData(true); //首页杂数据
    }




    private void requestMainData(boolean isShowDialog) {
        HashMap<String, String> params = new HashMap<>();
        Observable ob = Api.getDefault().getbackCategoryType(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<TemplateType>>(context) {
            @Override
            protected void _onError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(List<TemplateType> data) {

                callback.setFragmentList(data);

            }
        }, "bjHeadData", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, true, true, isShowDialog);
    }





}

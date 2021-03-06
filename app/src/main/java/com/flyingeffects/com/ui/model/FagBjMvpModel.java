package com.flyingeffects.com.ui.model;

import android.content.Context;

import com.flyingeffects.com.entity.FirstLevelTypeEntity;
import com.flyingeffects.com.entity.NewFragmentTemplateItem;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.ui.interfaces.model.FagBjMvpCallback;
import com.flyingeffects.com.utils.ToastUtil;
import com.shixing.sxve.ui.view.WaitingDialog_progress;

import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;


public class FagBjMvpModel {
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private FagBjMvpCallback callback;
    private Context context;
    private BottomSheetDialog bottomSheetDialog;
    private String mVideoFolder;
    private WaitingDialog_progress downProgressDialog;

    public FagBjMvpModel(Context context, FagBjMvpCallback callback) {
        this.context = context;
        this.callback = callback;
        FileManager fileManager = new FileManager();
        mVideoFolder = fileManager.getFileCachePath(context, "downVideo");
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
        params.put("type","2");
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
        }, "bjHeadData", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, true, true, isShowDialog);
    }



    /**
     * description ：请求影集数据
     * creation date: 2020/11/4
     * user : zhangtongju
     */
    public void requestPictureAlbumData(){
        HashMap<String, String> params = new HashMap<>();
        params.put("page",   "1");
        params.put("pageSize",  "10");
        Observable ob = Api.getDefault().photoList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<NewFragmentTemplateItem>>(context) {
            @Override
            protected void onSubError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void onSubNext(List<NewFragmentTemplateItem> data) {
                LogUtil.d("OOM",StringUtil.beanToJSONString(data));
                callback.PictureAlbum(data);

            }
        }, "FagData", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);


    }
















}

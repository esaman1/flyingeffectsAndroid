package com.mobile.flyingeffects.ui.model;

import android.content.Context;

import com.mobile.flyingeffects.base.ActivityLifeCycleEvent;
import com.mobile.flyingeffects.ui.interfaces.model.TemplateMvpCallback;
import com.shixing.sxve.ui.AssetDelegate;
import com.shixing.sxve.ui.model.TemplateModel;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;


public class TemplateMvpModel {
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private TemplateMvpCallback callback;
    private Context context;


    public TemplateMvpModel(Context context, TemplateMvpCallback callback) {
        this.context = context;
        this.callback = callback;
    }



    public void loadTemplate(String filePath, AssetDelegate delegate){
            Observable.just(filePath).map(s -> {
                TemplateModel  templateModel = null;
                try {
                    templateModel = new TemplateModel(filePath, delegate, context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return templateModel;
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(templateModel -> callback.completeTemplate(templateModel));
        }



    }


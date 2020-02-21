package com.mobile.flyingeffects.ui.model;

import android.content.Context;

import com.mobile.flyingeffects.base.ActivityLifeCycleEvent;
import com.mobile.flyingeffects.ui.interfaces.model.TemplateMvpCallback;

import rx.subjects.PublishSubject;


public class TemplateMvpModel {
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private TemplateMvpCallback callback;
    private Context context;


    public TemplateMvpModel(Context context, TemplateMvpCallback callback) {
        this.context = context;
        this.callback = callback;
    }


}

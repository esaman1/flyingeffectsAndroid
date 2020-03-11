package com.flyingeffects.com.ui.model;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.ui.interfaces.model.CreationTemplateMvpCallback;

import rx.subjects.PublishSubject;


public class CreationTemplateMvpModel {
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private CreationTemplateMvpCallback callback;
    private Context context;
    private BottomSheetDialog bottomSheetDialog;


    public CreationTemplateMvpModel(Context context, CreationTemplateMvpCallback callback) {
        this.context = context;
        this.callback = callback;
    }







}

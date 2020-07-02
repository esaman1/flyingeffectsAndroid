package com.flyingeffects.com.ui.model;

import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.ui.interfaces.model.PreviewUpAndDownMvpCallback;
import com.flyingeffects.com.ui.interfaces.model.VideoClippingMvpCallback;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import rx.subjects.PublishSubject;


public class PreviewUpAndDownMvpModel {
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private PreviewUpAndDownMvpCallback callback;
    private Context context;
    private boolean isRefresh = true;
    private int selectPage = 1;

    public PreviewUpAndDownMvpModel(Context context, PreviewUpAndDownMvpCallback callback) {
        this.context = context;
        this.callback = callback;
    }


    public void initSmartRefreshLayout(SmartRefreshLayout smartRefreshLayout){
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            isOnRefresh();
            isRefresh = true;
            refreshLayout.setEnableLoadMore(true);
            selectPage = 1;
//            requestFagData(false, true);
        });
        smartRefreshLayout.setOnLoadMoreListener(refresh -> {
            isOnLoadMore();
            isRefresh = false;
            selectPage++;
//            requestFagData(false, false);
        });
    }


    public void isOnLoadMore() {

    }

    public void isOnRefresh() {
    }









}

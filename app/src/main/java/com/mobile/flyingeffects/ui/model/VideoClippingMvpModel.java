package com.mobile.flyingeffects.ui.model;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.mobile.flyingeffects.R;
import com.mobile.flyingeffects.base.ActivityLifeCycleEvent;
import com.mobile.flyingeffects.constans.BaseConstans;
import com.mobile.flyingeffects.enity.TemplateType;
import com.mobile.flyingeffects.http.Api;
import com.mobile.flyingeffects.http.HttpUtil;
import com.mobile.flyingeffects.http.ProgressSubscriber;
import com.mobile.flyingeffects.ui.interfaces.model.VideoClippingMvpCallback;
import com.mobile.flyingeffects.utils.ToastUtil;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;


public class VideoClippingMvpModel {
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private VideoClippingMvpCallback callback;
    private Context context;
    private BottomSheetDialog bottomSheetDialog;


    public VideoClippingMvpModel(Context context, VideoClippingMvpCallback callback) {
        this.context = context;
        this.callback = callback;
    }


    public void showBottomSheetDialog() {
        if (bottomSheetDialog == null) {
            bottomSheetDialog = new BottomSheetDialog(context, R.style.gaussianDialog);
            View view = LayoutInflater.from(context).inflate(R.layout.view_bottom_sheet, null);
            bottomSheetDialog.setContentView(view);
            bottomSheetDialog.setCancelable(true);
            bottomSheetDialog.setCanceledOnTouchOutside(true);
            bottomSheetDialog.setOnDismissListener(dialog -> {

            });
//            bottomSheetDialog.setOnShowListener(dialog -> playVideo(item.getLink()));

            bottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {

                }
            });

            View parent = (View) view.getParent();     //处理高度显示完全  https://www.jianshu.com/p/38af0cf77352
            parent.setBackgroundResource(android.R.color.transparent);
            BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
            view.measure(0, 0);
            behavior.setPeekHeight(view.getMeasuredHeight());
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) parent.getLayoutParams();
            params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            parent.setLayoutParams(params);
            bottomSheetDialog.show();
        }
    }








}

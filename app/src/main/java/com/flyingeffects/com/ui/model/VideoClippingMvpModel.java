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
import com.flyingeffects.com.ui.interfaces.model.VideoClippingMvpCallback;

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

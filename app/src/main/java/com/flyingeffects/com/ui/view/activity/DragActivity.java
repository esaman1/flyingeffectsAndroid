package com.flyingeffects.com.ui.view.activity;

import android.content.Context;
import android.content.Intent;
import android.view.ViewTreeObserver;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.view.drag.CreationTemplateProgressBarView;

import butterknife.BindView;

/**
 * @author ZhouGang
 * @date 2020/11/5
 */
public class DragActivity extends BaseActivity {

    @BindView(R.id.progressBarView)
    CreationTemplateProgressBarView mProgressBarView;

    public static void startActivity(Context context){
        Intent intent = new Intent(context,DragActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_drag;
    }

    @Override
    protected void initView() {
        mProgressBarView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mProgressBarView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mProgressBarView.addProgressBarView(14000,"/storage/emulated/0/DCIM/Camera/1589971797339synthetic.mp4");
            }
        });
    }

    @Override
    protected void initAction() {

    }
}

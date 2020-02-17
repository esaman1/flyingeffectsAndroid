package com.mobile.flyingeffects.ui.view.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mobile.flyingeffects.R;
import com.mobile.flyingeffects.base.BaseFragment;
import com.mobile.flyingeffects.ui.view.activity.loginActivity;


/**
 * user :TongJu  ;描述：用戶中心
 * 时间：2018/4/24
 **/

public class frag_user_center extends BaseFragment {


    TextView tv_play_video;
    Dialog mDialog;

    @Override
    protected int getContentLayout() {
        return R.layout.fag_user_center;
    }


    @Override
    protected void initView() {

    }

    @Override
    protected void initAction() {

    }

    @Override
    protected void initData() {
        showDialog();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                //去除黑边
                new ContextThemeWrapper(getActivity(), R.style.Theme_Transparent));
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_user_center, null);
        TextView tv_login = view.findViewById(R.id.tv_login);
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), loginActivity.class);
                startActivity(intent);
            }
        });
        TextView tv_tours = view.findViewById(R.id.tv_tours);
        tv_tours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        builder.setView(view);
        builder.setCancelable(true);
        mDialog = builder.show();
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
    }










}



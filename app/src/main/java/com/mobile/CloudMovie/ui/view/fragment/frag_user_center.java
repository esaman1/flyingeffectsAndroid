package com.mobile.CloudMovie.ui.view.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mobile.CloudMovie.R;
import com.mobile.CloudMovie.base.BaseFragment;
import com.mobile.CloudMovie.ui.view.activity.loginActivity;

import butterknife.OnClick;


/**
 * user :TongJu  ;描述：用戶中心
 * 时间：2018/4/24
 **/

public class frag_user_center extends BaseFragment {


    TextView tv_play_video;
    Dialog mDialog;

    @Override
    protected int getContentLayout() {
        return R.layout.frg_4;
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


    @OnClick({R.id.ll_conversionVip,R.id.tv_Invitation,R.id.ll_watch_the_record,R.id.ll_account,R.id.ll_order,R.id.ll_pay_the_complaint})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_conversionVip:
                break;
            case R.id.tv_Invitation:
                break;
            case R.id.ll_watch_the_record:
                break;
            case R.id.ll_account:

                break;
            case R.id.ll_order:
                break;

            case R.id.ll_pay_the_complaint:
                break;

        }
    }







}



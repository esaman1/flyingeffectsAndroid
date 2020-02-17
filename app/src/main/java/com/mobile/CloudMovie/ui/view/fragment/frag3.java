package com.mobile.CloudMovie.ui.view.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.mobile.CloudMovie.R;
import com.mobile.CloudMovie.base.BaseFragment;


/**
 * user :TongJu  ;描述：支持页面
 * 时间：2018/4/24
 **/

public class frag3 extends BaseFragment  {


    TextView tv_play_video;

    @Override
    protected int getContentLayout() {
        return R.layout.frg_3;
    }


    @Override
    protected void initView() {
        tv_play_video= (TextView) findViewById(R.id.tv_play_video);
        tv_play_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    protected void initAction() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
    }






}



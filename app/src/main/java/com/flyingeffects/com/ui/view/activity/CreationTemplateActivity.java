package com.flyingeffects.com.ui.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.home_vp_frg_adapter;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.ui.interfaces.view.CreationTemplateMvpView;
import com.flyingeffects.com.ui.presenter.CreationTemplateMvpPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * description ：用户创作页面,里面主要用了langSong 的工具类，对视频进行贴纸的功能
 * creation date: 2020/3/11
 * user : zhangtongju
 */
public class CreationTemplateActivity extends BaseActivity implements CreationTemplateMvpView {


    @BindView(R.id.viewPager)
    ViewPager viewPager;
    private String fromTo;
    private List<String> imgPath = new ArrayList<>();
    private  CreationTemplateMvpPresenter presenter;
    /**
     * 原图地址,如果不需要抠图，原图地址为null
     */
    private List<String> originalPath;
    private String templateName;
    private String testVideoPath="";


    @Override
    protected int getLayoutId() {
        return R.layout.act_creation_template_edit;
    }

    @Override
    protected void initView() {
        presenter=new CreationTemplateMvpPresenter(this,this);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("Message");
        if (bundle != null) {
            fromTo = bundle.getString("fromTo");
            imgPath = bundle.getStringArrayList("paths");
            originalPath = bundle.getStringArrayList("originalPath");
            templateName = bundle.getString("templateName");
        }
        if (originalPath == null || originalPath.size() == 0) {
            //不需要抠图
            findViewById(R.id.ll_Matting).setVisibility(View.GONE);
        }
    }






    @Override
    protected void initAction() {
        presenter.initBottomLayout(viewPager);

    }

}

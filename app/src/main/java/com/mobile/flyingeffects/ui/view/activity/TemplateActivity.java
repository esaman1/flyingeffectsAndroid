package com.mobile.flyingeffects.ui.view.activity;

import android.content.Intent;
import android.os.Bundle;

import com.mobile.flyingeffects.R;
import com.mobile.flyingeffects.base.BaseActivity;
import com.mobile.flyingeffects.ui.interfaces.view.TemplateMvpView;
import com.mobile.flyingeffects.ui.presenter.TemplatePresenter;

import java.util.ArrayList;
import java.util.List;


/**
 * 模板页面
 */
public class TemplateActivity extends BaseActivity implements TemplateMvpView {

   private TemplatePresenter presenter;
   private List<String>imgPath=new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.act_template_edit;
    }

    @Override
    protected void initView() {
        presenter=new TemplatePresenter(this,this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle!=null){
            imgPath =  bundle.getStringArrayList("paths");
        }






    }

    @Override
    protected void initAction() {

    }
}

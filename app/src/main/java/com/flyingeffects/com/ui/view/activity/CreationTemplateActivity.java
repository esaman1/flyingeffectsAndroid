package com.flyingeffects.com.ui.view.activity;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.ui.interfaces.view.CreationTemplateMvpView;
import com.flyingeffects.com.ui.presenter.CreationTemplateMvpPresenter;

public class CreationTemplateActivity extends BaseActivity implements CreationTemplateMvpView {

    CreationTemplateMvpPresenter presenter;


    @Override
    protected int getLayoutId() {
        return R.layout.act_creation_template_edit;
    }

    @Override
    protected void initView() {
        presenter=new CreationTemplateMvpPresenter(this,this);
    }

    @Override
    protected void initAction() {

    }
}

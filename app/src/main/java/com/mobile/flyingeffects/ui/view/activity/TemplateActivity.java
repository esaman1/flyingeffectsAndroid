package com.mobile.flyingeffects.ui.view.activity;

import com.mobile.flyingeffects.R;
import com.mobile.flyingeffects.base.BaseActivity;
import com.mobile.flyingeffects.ui.interfaces.view.TemplateMvpView;
import com.mobile.flyingeffects.ui.presenter.TemplatePresenter;


/**
 * 模板页面
 */
public class TemplateActivity extends BaseActivity implements TemplateMvpView {

   private TemplatePresenter presenter;

    @Override
    protected int getLayoutId() {
        return R.layout.act_template_edit;
    }

    @Override
    protected void initView() {
        presenter=new TemplatePresenter(this,this);
    }

    @Override
    protected void initAction() {

    }
}

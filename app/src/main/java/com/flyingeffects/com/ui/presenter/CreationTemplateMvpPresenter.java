package com.flyingeffects.com.ui.presenter;

import android.content.Context;
import android.support.v4.view.ViewPager;

import com.flyingeffects.com.base.mvpBase.BasePresenter;
import com.flyingeffects.com.ui.interfaces.model.CreationTemplateMvpCallback;
import com.flyingeffects.com.ui.interfaces.view.CreationTemplateMvpView;
import com.flyingeffects.com.ui.model.CreationTemplateMvpModel;
import com.lansosdk.videoeditor.DrawPadView;

public class CreationTemplateMvpPresenter extends BasePresenter implements CreationTemplateMvpCallback {
    private CreationTemplateMvpView creationTemplatemvpView;
    private CreationTemplateMvpModel creationTemplatemodel;

    public CreationTemplateMvpPresenter(Context context, CreationTemplateMvpView mvp_view,String mVideoPath) {
        this.creationTemplatemvpView = mvp_view;
        creationTemplatemodel = new CreationTemplateMvpModel(context, this,mVideoPath);
    }


    public void initBottomLayout(ViewPager viewPager){
        creationTemplatemodel.initBottomLayout(viewPager);
    }


    @Override
    public void ItemClickForStickView() {
        creationTemplatemvpView.ItemClickForStickView();
    }




    public void toPrivateVideo(DrawPadView drawPadView){
        creationTemplatemodel.toPrivateVideo(drawPadView);
    }
}

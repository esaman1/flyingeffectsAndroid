package com.flyingeffects.com.ui.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.ui.interfaces.view.CreationTemplateMvpView;
import com.flyingeffects.com.ui.presenter.CreationTemplateMvpPresenter;
import com.flyingeffects.com.view.lansongCommendView.ImageTouchView;
import com.flyingeffects.com.view.lansongCommendView.StickerView;
import com.flyingeffects.com.view.lansongCommendView.TextStickerView;
import com.lansosdk.box.ViewLayerRelativeLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * description ：用户创作页面,里面主要用了langSong 的工具类，对视频进行贴纸的功能
 * creation date: 2020/3/11
 * user : zhangtongju
 */
public class CreationTemplateActivity extends BaseActivity implements CreationTemplateMvpView {

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    /**
     * 蓝松规定的容器
     */
    @BindView(R.id.id_vview_realtime_gllayout)
    ViewLayerRelativeLayout viewLayerRelativeLayout;

    @BindView(R.id.switcher)
    ImageTouchView imgeTouchView;

    @BindView(R.id.id_vview_drawimage_stickview)
    StickerView stickView;

    @BindView(R.id.id_vview_drawimage_textstickview)
    TextStickerView textStickView;

    @BindView(R.id.iv_cover)
    ImageView iv_cover;

    private List<String> imgPath = new ArrayList<>();
    private CreationTemplateMvpPresenter presenter;
    /**
     * 原图地址,如果不需要抠图，原图地址为null
     */
    private List<String> originalPath;
    private String coverImagePath = "http://cdn.flying.nineton.cn/admin/20200311/5e689f344ef21Comp%201%20(0-00-00-00).jpg";
    private String testVideoPath = "../DCIM/Camera/1583914803162synthetic.mp4";


    @Override
    protected int getLayoutId() {
        return R.layout.act_creation_template_edit;
    }

    @Override
    protected void initView() {
        ((TextView) findViewById(R.id.tv_top_submit)).setText("保存");
        presenter = new CreationTemplateMvpPresenter(this, this);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("Message");
        if (bundle != null) {
            imgPath = bundle.getStringArrayList("paths");
            originalPath = bundle.getStringArrayList("originalPath");
        }
        if (originalPath == null || originalPath.size() == 0) {
            //不需要抠图
            findViewById(R.id.ll_Matting).setVisibility(View.GONE);
        }
    }


    @Override
    protected void initAction() {
        presenter.initBottomLayout(viewPager);
        Glide.with(this).load(coverImagePath).into(iv_cover);
        FirstAddImage();
    }


    /**
     * description ：增加第一个用户抠图的stickView
     * creation date: 2020/3/11
     * user : zhangtongju
     */
    private void FirstAddImage() {
        Observable.just(imgPath.get(0)).map(BitmapFactory::decodeFile).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Bitmap>() {
            @Override
            public void call(Bitmap bitmap) {
                stickView.addBitImage(bitmap);
            }
        });
    }


    @OnClick({R.id.tv_top_submit, R.id.iv_play})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_top_submit:

                break;


            case R.id.iv_play:

                break;

                default:
                    break;


        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

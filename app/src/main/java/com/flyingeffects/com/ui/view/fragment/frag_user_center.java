package com.flyingeffects.com.ui.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.home_vp_frg_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.UserInfo;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.AlbumManager;
import com.flyingeffects.com.manager.huaweiObs;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.AlbumChooseCallback;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.ui.view.activity.AboutActivity;
import com.flyingeffects.com.ui.view.activity.EditInformationActivity;
import com.flyingeffects.com.ui.view.activity.FansActivity;
import com.flyingeffects.com.ui.view.activity.LoginActivity;
import com.flyingeffects.com.ui.view.activity.MineFocusActivity;
import com.flyingeffects.com.ui.view.activity.ZanActivity;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.orhanobut.hawk.Hawk;
import com.shixing.sxve.ui.view.WaitingDialog;
import com.yanzhenjie.album.AlbumFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;


/**
 * user :TongJu  ;描述：用戶中心
 * 时间：2018/4/24
 **/

public class frag_user_center extends BaseFragment implements AlbumChooseCallback {
    public final static int SELECTALBUMFROMUSETCENTERBJ = 1;

    private String[] titles = {"我上传的背景", "喜欢","模板收藏"};

    @BindView(R.id.viewpager)
    ViewPager viewpager;


    @BindView(R.id.tl_tabs)
    SlidingTabLayout tabLayout;

    @BindView(R.id.iv_about)
    ImageView iv_about;

    @BindView(R.id.iv_head)
    ImageView iv_head;

    @BindView(R.id.tv_id)
    TextView tv_id;

    @BindView(R.id.tv_name)
    TextView tv_name;

    @BindView(R.id.fans_count)
    TextView fans_count;

    @BindView(R.id.attention_count)
    TextView attention_count;


    @BindView(R.id.tv_video_count)
    TextView tv_video_count;
    @BindView(R.id.im_user_skin)
    ImageView imSkin;
    @BindView(R.id.tv_Introduction)
    TextView tvIntroduction;
    @BindView(R.id.im_edit)
    ImageView imEdit;


    @Override
    protected int getContentLayout() {
        return R.layout.fag_user_center;
    }


    @Override
    protected void initView() {
        iv_about.setOnClickListener(view -> {
            statisticsEventAffair.getInstance().setFlag(getActivity(), "3_help");
            Intent intent = new Intent(getActivity(), AboutActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void initAction() {
        initTabData();
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onResume() {

        if (getActivity() != null) {
            //未登陆
            if (BaseConstans.hasLogin()) {
                tv_id.setText("飞友号：" + BaseConstans.GetUserId());
                requestUserInfo();
            } else {
                Glide.with(this)
                        .load(R.mipmap.head)
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(iv_head);

                fans_count.setText("");
                attention_count.setText("");
                tv_video_count.setText("");
                tv_id.setText("未登录");
                tv_name.setVisibility(View.GONE);
            }
        }
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    private void initTabData() {
        FragmentManager manager = getChildFragmentManager();
        ArrayList<Fragment> list = new ArrayList<>();
        frag_user_upload_bj fag_1 = new frag_user_upload_bj();
        list.add(fag_1);

        fragHomePage fag_like= new fragHomePage();
        Bundle bundle1 = new Bundle();
        bundle1.putSerializable("toUserId", BaseConstans.GetUserId());
        bundle1.putSerializable("isFrom", 2);
        bundle1.putSerializable("fromTo", FromToTemplate.ISHOMEMYLIKE);
        fag_like.setArguments(bundle1);
        list.add(fag_like);



        frag_user_collect fag_0 = new frag_user_collect();
        Bundle bundle2 = new Bundle();
        bundle2.putSerializable("template_type", "1");
        fag_0.setArguments(bundle2);
        list.add(fag_0);
        home_vp_frg_adapter adapter = new home_vp_frg_adapter(manager, list);
        viewpager.setAdapter(adapter);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        tabLayout.setViewPager(viewpager, titles);
    }


    @OnClick({R.id.iv_head,R.id.fans_count,R.id.attention_count,R.id.tv_video_count,R.id.iv_Peeling,R.id.tv_edit_information})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_head:
                if (!BaseConstans.hasLogin()) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
                break;

            case R.id.fans_count:
                if(BaseConstans.hasLogin()){
                    Intent intentZan=new Intent(getActivity(), ZanActivity.class);
                    intentZan.putExtra("from",1);
                    startActivity(intentZan);
                }else{
                    ToastUtil.showToast(getActivity().getResources().getString(R.string.need_login));
                }

                break;

            case R.id.attention_count:
                if(BaseConstans.hasLogin()){
                    Intent intentFoucs=new Intent(getActivity(), MineFocusActivity.class);
                    intentFoucs.putExtra("to_user_id",BaseConstans.GetUserId());
                    startActivity(intentFoucs);
                }else{
                    ToastUtil.showToast(getActivity().getResources().getString(R.string.need_login));
                }

                break;
            case R.id.tv_video_count:
                if(BaseConstans.hasLogin()){
                    Intent intentFan=new Intent(getActivity(), FansActivity.class);
                    intentFan.putExtra("to_user_id",BaseConstans.GetUserId());
                    intentFan.putExtra("from",0);
                    startActivity(intentFan);
                }else{
                    ToastUtil.showToast(getActivity().getResources().getString(R.string.need_login));
                }
                break;
            case R.id.iv_Peeling:
                AlbumManager.chooseImageAlbum(getContext(),1,SELECTALBUMFROMUSETCENTERBJ,this,"");
                break;
            case R.id.tv_edit_information:
                Intent intent = new Intent(getActivity(), EditInformationActivity.class);
                startActivity(intent);
                break;
            default:
                break;

        }
    }


    private void requestUserInfo() {
        if (getActivity() != null) {
            HashMap<String, String> params = new HashMap<>();
            params.put("to_user_id", BaseConstans.GetUserId());
            // 启动时间
            Observable ob = Api.getDefault().getOtherUserinfo(BaseConstans.getRequestHead(params));
            HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<UserInfo>(getActivity()) {
                @Override
                protected void _onError(String message) {
                    tv_id.setText("未登录");
                    BaseConstans.SetUserToken("");
                }

                @Override
                protected void _onNext(UserInfo data) {
                    Hawk.put("UserInfo",data);
                    if (getActivity() != null) {
                        tv_id.setText("飞友号：" + data.getId());
                        if(!TextUtils.isEmpty(data.getNickname())){
                            tv_name.setText(data.getNickname());
                            tv_name.setVisibility(View.VISIBLE);
                        }else{
                            tv_name.setVisibility(View.GONE);
                        }
                        if (!TextUtils.isEmpty(data.getPhotourl())) {
                            Glide.with(getActivity())
                                    .load(data.getPhotourl())
                                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                    .into(iv_head);
                        } else {
                            Glide.with(getActivity())
                                    .load(R.mipmap.head)
                                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                    .into(iv_head);
                        }

                        fans_count.setText(data.getUser_praise());
                        attention_count.setText(data.getUser_watch());
                        tv_video_count.setText(data.getUser_follower());
                        if(TextUtils.isEmpty(data.getSkin())){
                            Glide.with(getActivity())
                                    .load(R.mipmap.home_page_bj)
                                    .into(imSkin);
                        }else {
                            Glide.with(getActivity())
                                    .load(data.getSkin())
                                    .into(imSkin);
                        }
                        if (!TextUtils.isEmpty(data.getRemark())) {
                            tvIntroduction.setText(data.getRemark());
                            imEdit.setVisibility(View.GONE);
                        }else {
                            tvIntroduction.setText("完善简介让更多的友友认识你");
                            imEdit.setVisibility(View.VISIBLE);
                        }
                    }
                    BaseConstans.SetUserId(data.getId(),data.getNickname(),data.getPhotourl());
                }
            }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
        }
    }

    @Override
    public void resultFilePath(int tag, List<String> paths, boolean isCancel, ArrayList<AlbumFile> albumFileList) {
        if (!isCancel &&  paths != null && paths.size() > 0) {
            String path = paths.get(0);
            String type = path.substring(path.length() - 4);
            String nowTime = StringUtil.getCurrentTimeymd();
            String huaweiSkinPath = "media/android/user_skin_img/" + nowTime + "/" + System.currentTimeMillis() + type;
            uploadFileToHuawei(path,huaweiSkinPath);
        }
    }


    private void uploadFileToHuawei(String videoPath, String copyName) {
        WaitingDialog.openPragressDialog(getContext());
        Log.d("OOM2", "uploadFileToHuawei" + "当前上传的地址为" + videoPath + "当前的名字为" + copyName);
        new Thread(() -> huaweiObs.getInstance().uploadFileToHawei(videoPath, copyName, new huaweiObs.Callback() {
            @Override
            public void isSuccess(String str) {
                if (!TextUtils.isEmpty(str)) {
                    String path = str.substring(str.lastIndexOf("=") + 1, str.length() - 1);
                    uploadUserSkin(path);
                }
            }
        })).start();
    }

    private void uploadUserSkin(String skinPath) {
        HashMap<String, String> params = new HashMap<>();
        params.put("skin", skinPath);
        HttpUtil.getInstance().toSubscribe(Api.getDefault().uploadUserSkin(BaseConstans.getRequestHead(params)),
                new ProgressSubscriber<Object>(getContext()) {
                    @Override
                    protected void _onError(String message) {
                        ToastUtil.showToast(message);
                    }

                    @Override
                    protected void _onNext(Object data) {
                        Glide.with(getActivity())
                                .load(skinPath)
                                .into(imSkin);
                    }
                }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);
    }
}



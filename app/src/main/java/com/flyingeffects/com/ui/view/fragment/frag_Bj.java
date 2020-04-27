package com.flyingeffects.com.ui.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.home_vp_frg_adapter;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.TemplateType;
import com.flyingeffects.com.manager.AlbumManager;
import com.flyingeffects.com.manager.CompressionCuttingManage;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.AlbumChooseCallback;
import com.flyingeffects.com.ui.interfaces.view.FagBjMvpView;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.ui.model.GetPathTypeModel;
import com.flyingeffects.com.ui.presenter.FagBjMvpPresenter;
import com.flyingeffects.com.ui.view.activity.CreationTemplateActivity;
import com.flyingeffects.com.ui.view.activity.LoginActivity;
import com.flyingeffects.com.ui.view.activity.PreviewActivity;
import com.flyingeffects.com.ui.view.activity.VideoCropActivity;
import com.shixing.sxve.ui.albumType;
import com.shixing.sxve.ui.view.WaitingDialog;
import com.yanzhenjie.album.AlbumFile;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * user :TongJu  ;描述：背景页面
 * 时间：2018/4/24
 **/

public class frag_Bj extends BaseFragment implements FagBjMvpView {

    @BindView(R.id.viewpager)
    ViewPager viewPager;


    @BindView(R.id.ll_add_child)
    LinearLayout ll_add_child;

    private FagBjMvpPresenter presenter;
    public final static int SELECTALBUM = 1;

    private ArrayList<TextView> listTv = new ArrayList<>();
    private ArrayList<View> listView = new ArrayList<>();

    @Override
    protected int getContentLayout() {
        return R.layout.fag_bj;
    }


    @Override
    protected void initView() {
        presenter = new FagBjMvpPresenter(getActivity(), this);
        presenter.requestData();
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

    private ArrayList<Fragment> list = new ArrayList<>();

    @Override
    public void setFragmentList(List<TemplateType> data) {
        if (getActivity() != null) {
            if (data != null && data.size() > 0) {
                ll_add_child.removeAllViews();
                TemplateType templateType = new TemplateType();
                templateType.setId("collect");
                templateType.setName("收藏");
                data.add(templateType);
                listView.clear();
                listTv.clear();
                list.clear();
                FragmentManager manager = getFragmentManager();
                String[] titles = new String[data.size()];
                for (int i = 0; i < data.size(); i++) {
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_bj_head, null);
                    TextView tv = view.findViewById(R.id.tv_name_bj_head);
                    View view_line = view.findViewById(R.id.view_line_head);
                    tv.setText(data.get(i).getName());
                    tv.setId(i);
                    tv.setOnClickListener(v -> showWitchBtn(v.getId()));
                    listTv.add(tv);
                    listView.add(view_line);
                    ll_add_child.addView(view);
                    titles[i] = data.get(i).getName();
                    if (i == data.size() - 1) {
                        //手动添加收藏模板
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("template_type", "2");
                        titles[i] = data.get(i).getName();
                        frag_user_collect fag_0 = new frag_user_collect();
                        fag_0.setArguments(bundle);
                        list.add(fag_0);
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("id", data.get(i).getId());
                        bundle.putSerializable("from", 1);
                        bundle.putSerializable("num", i);
                        titles[i] = data.get(i).getName();
                        fragBjItem fragment = new fragBjItem();
                        fragment.setArguments(bundle);
                        list.add(fragment);
                    }
                }
                home_vp_frg_adapter adapter = new home_vp_frg_adapter(manager, list);
                viewPager.setAdapter(adapter);
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int i, float v, int i1) {

                    }

                    @Override
                    public void onPageSelected(int i) {
                        if (i <= data.size() - 1) {
                            showWitchBtn(i);
                            statisticsEventAffair.getInstance().setFlag(getActivity(), "1_tab", titles[i]);
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int i) {

                    }
                });

                if (data.size() > 0) {
                    new Handler().postDelayed(() -> showWitchBtn(0), 500);
                }
            }
        }
    }


    private void showWitchBtn(int showWitch) {
        for (int i = 0; i < listTv.size(); i++) {
            TextView tv = listTv.get(i);
            View view = listView.get(i);
            if (i == showWitch) {
                tv.setTextSize(21);
                int width = tv.getWidth();
                view.setVisibility(View.VISIBLE);
                setViewWidth(view, width);
            } else {
                tv.setTextSize(17);
                view.setVisibility(View.INVISIBLE);
            }
        }
        viewPager.setCurrentItem(showWitch);
    }


    private void setViewWidth(View mView, int width) {
        LinearLayout.LayoutParams Params = (LinearLayout.LayoutParams) mView.getLayoutParams();
        Params.width = width;
        mView.setLayoutParams(Params);
    }


    @OnClick({R.id.iv_add, R.id.iv_cover, R.id.Toolbar})
    public void onClick(View view) {


        switch (view.getId()) {
            case R.id.iv_add:

            case R.id.Toolbar:
                if (BaseConstans.hasLogin()) {
                    toAddSticker();
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
                break;

            case R.id.iv_cover:
                toAddSticker();
                statisticsEventAffair.getInstance().setFlag(getActivity(), "7_background");
                break;

            default:
                break;
        }
    }


    private void toAddSticker(){
        statisticsEventAffair.getInstance().setFlag(getActivity(), "6_customize_bj");
        AlbumManager.chooseAlbum(getActivity(), 1, SELECTALBUM, new AlbumChooseCallback() {
            @Override
            public void resultFilePath(int tag, List<String> paths, boolean isCancel, ArrayList<AlbumFile> albumFileList) {
                if (!isCancel) {
                    if (!TextUtils.isEmpty(paths.get(0))) {
                        String pathType = GetPathTypeModel.getInstance().getMediaType(paths.get(0));
                        if (albumType.isVideo(pathType)) {
                            Intent intent = new Intent(getActivity(), VideoCropActivity.class);
                            intent.putExtra("videoPath", paths.get(0));
                            intent.putExtra("comeFrom", FromToTemplate.ISFROMEDOWNVIDEOFORADDSTICKER);

                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            compressImage(paths.get(0));

                        }
                    }
                }

            }
        }, "");
    }


    private void compressImage(String path) {
        if (getActivity() != null) {
//            WaitingDialog.openPragressDialog(getActivity(), "飞闪极速抠图中");
            CompressionCuttingManage manage = new CompressionCuttingManage(getActivity(), "", true, tailorPaths -> {
                if (getActivity() != null) {
//                    WaitingDialog.closePragressDialog();
                    Intent intent = new Intent(getActivity(), CreationTemplateActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("paths", tailorPaths.get(0));
                    bundle.putSerializable("bjTemplateTitle", "");
                    bundle.putBoolean("isNeedCut", true);
                    bundle.putString("originalPath", path);
                    bundle.putString("video_path", "");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("Message", bundle);
                    startActivity(intent);
                }
            });
            List<String> Paths = new ArrayList<>();
            Paths.add(path);
            manage.ToMatting(Paths);
        }

    }


}


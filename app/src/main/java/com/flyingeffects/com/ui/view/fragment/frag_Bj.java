package com.flyingeffects.com.ui.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.home_vp_frg_adapter2;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.RequestMessage;
import com.flyingeffects.com.enity.TemplateType;
import com.flyingeffects.com.enity.fromKuaishou;
import com.flyingeffects.com.manager.AlbumManager;
import com.flyingeffects.com.manager.CompressionCuttingManage;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.AlbumChooseCallback;
import com.flyingeffects.com.ui.interfaces.view.FagBjMvpView;
import com.flyingeffects.com.ui.model.GetPathTypeModel;
import com.flyingeffects.com.ui.model.MattingImage;
import com.flyingeffects.com.ui.presenter.FagBjMvpPresenter;
import com.flyingeffects.com.ui.view.activity.BackgroundSearchActivity;
import com.flyingeffects.com.ui.view.activity.ContentAllianceActivity;
import com.flyingeffects.com.ui.view.activity.CreationTemplateActivity;
import com.flyingeffects.com.ui.view.activity.LoginActivity;
import com.flyingeffects.com.ui.view.activity.VideoCropActivity;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.shixing.sxve.ui.albumType;
import com.yanzhenjie.album.AlbumFile;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;


/**
 * user :TongJu  ;描述：背景页面
 * 时间：2018/4/24
 **/

public class frag_Bj extends BaseFragment implements FagBjMvpView {

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.ll_add_child)
    LinearLayout ll_add_child;


    @BindView(R.id.iv_search)
    ImageView iv_search;

    private FagBjMvpPresenter presenter;
    public final static int SELECTALBUM = 1;

    private static ArrayList<TextView> listTv = new ArrayList<>();
    private static ArrayList<View> listView = new ArrayList<>();


    private List<TemplateType> data;


    private int lastViewPagerChoosePosition;

    @Override
    protected int getContentLayout() {
        return R.layout.fag_bj;
    }

    @Override
    protected void initView() {
        presenter = new FagBjMvpPresenter(getActivity(), this);
        presenter.requestData();
        EventBus.getDefault().register(this);
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
        if (data == null || data.size() == 0) {
            presenter.requestData();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    private ArrayList<Fragment> list = new ArrayList<>();
    String[] titles;

    @Override
    public void setFragmentList(List<TemplateType> data) {

        if (getActivity() != null) {
            this.data = data;
            if (data != null && data.size() > 0) {
                ll_add_child.removeAllViews();
                TemplateType templateType = new TemplateType();
                templateType.setId("collect");
                templateType.setName("收藏");
                data.add(templateType);
                listView.clear();
                listTv.clear();
                list.clear();
//                FragmentManager manager = getFragmentManager();
                titles = new String[data.size()];
                for (int i = 0; i < data.size(); i++) {
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_bj_head, null);
                    TextView tv = view.findViewById(R.id.tv_name_bj_head);
                    View view_line = view.findViewById(R.id.view_line_head);
                    tv.setText(data.get(i).getName());
                    tv.setId(i);
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TemplateType templateType1 = data.get(view.getId());
                            String str = StringUtil.beanToJSONString(templateType1);
                            LogUtil.d("OMM2", str);
                            try {
                                int id = Integer.parseInt(templateType1.getId());
                                LogUtil.d("OMM2", "id=" + id);
                                if (id != 10000) {
                                    showWitchBtn(view.getId());
                                } else {
                                    startActivity(new Intent(getActivity(), ContentAllianceActivity.class));
                                }
                            } catch (Exception e) {
                                showWitchBtn(view.getId());
                            }
                        }
                    });

                    listTv.add(tv);
                    listView.add(view_line);

                    ll_add_child.addView(view);
                    titles[i] = data.get(i).getName();
                    if (i == data.size() - 1) {
                        //手动添加收藏模板
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("template_type", "2");
                        titles[i] = data.get(i).getName();
                        frag_user_collect fragUserCollect = new frag_user_collect();
                        fragUserCollect.setArguments(bundle);
                        list.add(fragUserCollect);
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
                home_vp_frg_adapter2 adapter = new home_vp_frg_adapter2(getFragmentManager(), list);
                viewPager.setAdapter(adapter);
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int i, float v, int i1) {
                    }

                    @Override
                    public void onPageSelected(int i) {
                        if (lastViewPagerChoosePosition != i) {
                            try {
                                String position = data.get(i).getId();
                                int interPosition = Integer.parseInt(position);
                                if (interPosition == 10000) {
                                    if(!DoubleClick.getInstance().isFastZDYDoubleClick(1000)){
                                        startActivity(new Intent(getActivity(), ContentAllianceActivity.class));
                                    }
                                } else {
                                    selectedPage(i);
                                    lastViewPagerChoosePosition = i;
                                }
                            } catch (Exception e) {
                                selectedPage(i);
                                lastViewPagerChoosePosition = i;
                            }
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int i) {
                        LogUtil.d("OOM", "i=" + i);
                    }
                });

                if (data.size() > 0) {
                    new Handler().postDelayed(() -> showWitchBtn(0), 500);
                }

            }
        }
    }


    private void selectedPage(int i) {
        if (lastViewPagerChoosePosition != i) {
            if (i <= data.size() - 1) {
                showWitchBtn(i);
                statisticsEventAffair.getInstance().setFlag(getActivity(), "1_tab", titles[i]);
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
        if (titles != null) {
            statisticsEventAffair.getInstance().setFlag(getActivity(), "13_back_tab_click", titles[showWitch]);
        }
        viewPager.setCurrentItem(showWitch);
    }

    private void setViewWidth(View mView, int width) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mView.getLayoutParams();
        params.width = width;
        mView.setLayoutParams(params);
    }


    @OnClick({R.id.iv_add, R.id.iv_cover, R.id.Toolbar, R.id.relative_top, R.id.iv_search,R.id.ll_crate_photograph_album})
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
            case R.id.relative_top:
            case R.id.iv_search:
                //搜索栏目
                Intent intent = new Intent(getActivity(), BackgroundSearchActivity.class);
                intent.putExtra("isFrom", 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;

            case R.id.ll_crate_photograph_album:
                //创建影集
                toPhotographAlbum();
                break;
            default:
                break;
        }
    }


    private void toAddSticker() {
        statisticsEventAffair.getInstance().setFlag(getActivity(), "6_customize_bj");
        AlbumManager.chooseAlbum(getActivity(), 1, SELECTALBUM, new AlbumChooseCallback() {
            @Override
            public void resultFilePath(int tag, List<String> paths, boolean isCancel, ArrayList<AlbumFile> albumFileList) {
                if (!isCancel) {
                    if (!TextUtils.isEmpty(paths.get(0))) {
                        MattingImage mattingImage = new MattingImage();
                        mattingImage.createHandle(getActivity(), new MattingImage.InitSegJniStateCallback() {
                            @Override
                            public void isDone(boolean isDone) {
                                if (isDone) {
                                    String pathType = GetPathTypeModel.getInstance().getMediaType(paths.get(0));
                                    if (albumType.isVideo(pathType)) {
                                        Intent intent = new Intent(getActivity(), VideoCropActivity.class);
                                        intent.putExtra("videoPath", paths.get(0));
                                        intent.putExtra("comeFrom", "");
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    } else {
                                        compressImage(paths.get(0));
                                    }
                                }
                            }
                        });
                    }
                }
            }
        }, "");
    }



    private void toPhotographAlbum() {
        AlbumManager.chooseAlbum(getActivity(), 20, SELECTALBUM, new AlbumChooseCallback() {
            @Override
            public void resultFilePath(int tag, List<String> paths, boolean isCancel, ArrayList<AlbumFile> albumFileList) {
                if (!isCancel) {
                    if (!TextUtils.isEmpty(paths.get(0))) {
                        MattingImage mattingImage = new MattingImage();
                        mattingImage.createHandle(getActivity(), new MattingImage.InitSegJniStateCallback() {
                            @Override
                            public void isDone(boolean isDone) {
                                if (isDone) {
                                    String pathType = GetPathTypeModel.getInstance().getMediaType(paths.get(0));
                                    if (albumType.isVideo(pathType)) {
                                        Intent intent = new Intent(getActivity(), VideoCropActivity.class);
                                        intent.putExtra("videoPath", paths.get(0));
                                        intent.putExtra("comeFrom", "");
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    } else {
                                        compressImage(paths.get(0));
                                    }
                                }
                            }
                        });
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
            List<String> paths = new ArrayList<>();
            paths.add(path);
            manage.toMatting(paths);
        }
    }




    @Subscribe
    public void onEventMainThread(fromKuaishou event) {
        if(getActivity()!=null){
            showWitchBtn(lastViewPagerChoosePosition);
        }

    }

}


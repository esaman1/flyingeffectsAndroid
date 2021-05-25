package com.flyingeffects.com.ui.view.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.home_vp_frg_adapter2;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.commonlyModel.TemplateDown;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.databinding.FagBjBinding;
import com.flyingeffects.com.entity.FirstLevelTypeEntity;
import com.flyingeffects.com.entity.NewFragmentTemplateItem;
import com.flyingeffects.com.entity.SecondChoosePageListener;
import com.flyingeffects.com.entity.fromKuaishou;
import com.flyingeffects.com.manager.AlbumManager;
import com.flyingeffects.com.manager.CompressionCuttingManage;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.view.FagBjMvpView;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.ui.model.GetPathTypeModel;
import com.flyingeffects.com.ui.model.MattingImage;
import com.flyingeffects.com.ui.presenter.FagBjMvpPresenter;
import com.flyingeffects.com.ui.view.activity.ContentAllianceActivity;
import com.flyingeffects.com.ui.view.activity.CreationTemplateActivity;
import com.flyingeffects.com.ui.view.activity.LoginActivity;
import com.flyingeffects.com.ui.view.activity.TemplateActivity;
import com.flyingeffects.com.ui.view.activity.TemplateSearchActivity;
import com.flyingeffects.com.ui.view.activity.VideoCropActivity;
import com.flyingeffects.com.ui.view.dialog.LoadingDialog;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.PermissionUtil;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.shixing.sxve.ui.AlbumType;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;


/**
 * ;描述：背景页面
 * 时间：2018/4/24
 *
 * @author TongJu
 **/

public class BackgroundFragment extends BaseFragment implements FagBjMvpView, AppBarLayout.OnOffsetChangedListener {

    private static final String TAG = "BackgroundFragment";
    private FagBjMvpPresenter presenter;
    public final static int SELECTALBUM = 1;
    private List<FirstLevelTypeEntity> data;
    private int lastViewPagerChoosePosition;
    private NewFragmentTemplateItem template_item;
    private LoadingDialog mLoadingDialog;
    private FagBjBinding mBinding;

    @Override
    protected int getContentLayout() {
        return 0;
    }

    @Override
    protected void initView() {
        presenter = new FagBjMvpPresenter(getActivity(), this);
        presenter.requestData();
        mLoadingDialog = buildLoadingDialog();
        mBinding.appbar.addOnOffsetChangedListener(this);
    }

    @Override
    protected View getBindingView(LayoutInflater inflater, ViewGroup container) {
        mBinding = FagBjBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    private LoadingDialog buildLoadingDialog() {
        return LoadingDialog.getBuilder(getActivity())
                .setHasAd(false)
                .setTitle("加载中...")
                .build();
    }


    @Override
    protected void initAction() {
        mBinding.llCratePhotographAlbum.setOnClickListener(this::onViewClicked);
        mBinding.ivAdd.setOnClickListener(this::onViewClicked);
        mBinding.llClickCreateVideo2.setOnClickListener(this::onViewClicked);
        mBinding.llCratePhotographAlbum2.setOnClickListener(this::onViewClicked);
        mBinding.llClickCreateVideo.setOnClickListener(this::onViewClicked);
        mBinding.ivSearch.setOnClickListener(this::onViewClicked);
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

    private final ArrayList<Fragment> list = new ArrayList<>();

    @Override
    public void setFragmentList(List<FirstLevelTypeEntity> data) {

        if (getActivity() != null) {
            this.data = data;
            if (data != null && data.size() > 0) {
                list.clear();
                for (int i = 0; i < data.size(); i++) {
                    Bundle bundle = new Bundle();
                    if (TextUtils.equals("关注", data.get(i).getName()) || TextUtils.equals("收藏", data.get(i).getName())) {
                        bundle.putSerializable("id", data.get(i).getId());
                        bundle.putSerializable("from", 1);
                        bundle.putSerializable("num", i);
                        fragBjItem fragment = new fragBjItem();
                        fragment.setArguments(bundle);
                        list.add(fragment);
                    } else {
                        if (data.get(i).getCategory() != null && !data.get(i).getCategory().isEmpty()) {
                            Bundle bundle1 = SecondaryTypeFragment.buildArgument(data.get(i).getCategory(), SecondaryTypeFragment.BUNDLE_VALUE_TYPE_BACKGROUND, data.get(i).getId(),
                                    1, i, -1, null, data.get(i).getName());
                            SecondaryTypeFragment fragment = new SecondaryTypeFragment();
                            fragment.setArguments(bundle1);
                            list.add(fragment);
                        } else {
                            bundle.putSerializable("id", data.get(i).getId());
                            bundle.putString("tc_id", "-1");
                            bundle.putSerializable("from", 1);
                            bundle.putSerializable("num", i);
                            fragBjItem fragment = new fragBjItem();
                            fragment.setArguments(bundle);
                            list.add(fragment);
                        }
                    }
                }
                home_vp_frg_adapter2 adapter = new home_vp_frg_adapter2(getFragmentManager(), list);
                mBinding.viewpager.setAdapter(adapter);
                mBinding.viewpager.setOffscreenPageLimit(1);
                mBinding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int i, float v, int i1) {
                    }

                    @Override
                    public void onPageSelected(int i) {
                        EventBus.getDefault().post(new SecondChoosePageListener(i));
                        if (lastViewPagerChoosePosition != i) {
                            try {
                                String position = data.get(i).getId();
                                int interPosition = Integer.parseInt(position);
                                if (interPosition == 10000) {
                                    if (!DoubleClick.getInstance().isFastZDYDoubleClick(1000)) {
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
                mBinding.tlTabsBj.setupWithViewPager(mBinding.viewpager);

                for (int i = 0; i < mBinding.tlTabsBj.getTabCount(); i++) {
                    mBinding.tlTabsBj.getTabAt(i).setCustomView(R.layout.item_home_tab);
                    View view = mBinding.tlTabsBj.getTabAt(i).getCustomView();
                    AppCompatTextView tvTabText = view.findViewById(R.id.tv_tab_item_text);
                    tvTabText.setText(data.get(i).getName());
                    tvTabText.setTextColor(Color.parseColor("#797979"));
                    if (i == 0) {
                        tvTabText.setTextSize(24);
                        tvTabText.setTextColor(Color.parseColor("#ffffff"));
                    }
                }
                mBinding.tlTabsBj.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        View view = tab.getCustomView();
                        if (view != null) {
                            AppCompatTextView tvTabText = view.findViewById(R.id.tv_tab_item_text);
                            tvTabText.setTextSize(24);
                            tvTabText.setTextColor(Color.parseColor("#ffffff"));
                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        View view = tab.getCustomView();
                        if (view != null) {
                            AppCompatTextView tvTabText = view.findViewById(R.id.tv_tab_item_text);
                            tvTabText.setTextSize(16);
                            tvTabText.setTextColor(Color.parseColor("#797979"));
                        }
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });
            }
        }
    }

    public void showProgress(int progress) {
        if (getActivity() != null && mLoadingDialog != null) {
            Observable.just(progress).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> mLoadingDialog.setProgress(integer));
        }
    }


    /**
     * description ：得到图片影集
     * creation date: 2020/11/4
     * user : zhangtongju
     */
    @Override
    public void PictureAlbum(List<NewFragmentTemplateItem> data) {
        if (getActivity() != null) {
            if (data != null && data.size() > 0) {
                template_item = data.get(0);
                if (template_item != null) {
                    TemplateDown templateDown = new TemplateDown(new TemplateDown.DownFileCallback() {
                        @Override
                        public void isSuccess(String filePath) {
                            intoTemplateActivity(filePath);
                            LogUtil.d(TAG, "Background template filePath = " + filePath);
                        }

                        @Override
                        public void showDownProgress(int progress) {
                            showProgress(progress);
                        }
                    });
                    templateDown.prepareDownZip(template_item.getTemplatefile(), template_item.getZipid());
                }
            }
        }


    }

    public void intoTemplateActivity(String path) {
        if (getActivity() != null) {
            mLoadingDialog.dismiss();
            Observable.just(path).subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(s -> toPhotographAlbum(template_item, path));
        }
    }


    private void selectedPage(int i) {
        if (lastViewPagerChoosePosition != i) {
            if (i <= data.size() - 1) {
                showWitchBtn(i);
                StatisticsEventAffair.getInstance().setFlag(getActivity(), "1_tab", data.get(i).getName());
            }
        }

    }

    private void showWitchBtn(int showWitch) {
        if (data.size() != 0) {
            StatisticsEventAffair.getInstance().setFlag(getActivity(), "13_back_tab_click", data.get(showWitch).getName());
        }
        mBinding.viewpager.setCurrentItem(showWitch);
    }


    private void onViewClicked(View view) {
        if (getActivity() != null) {
            if (view == mBinding.ivSearch) {
                //搜索栏目
                StatisticsEventAffair.getInstance().setFlag(getActivity(), "20_search_bj");
                Intent intent = new Intent(getActivity(), TemplateSearchActivity.class);
                intent.putExtra("isFrom", 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            } else if (view == mBinding.llCratePhotographAlbum || view == mBinding.llCratePhotographAlbum2) {
                ActivityCompat
                        .requestPermissions(getActivity()
                                , PERMISSION_STORAGE, 1);
            } else {
                ActivityCompat
                        .requestPermissions(getActivity()
                                , PERMISSION_STORAGE, 2);
            }
        }
    }


    private void toCreateVideo() {
        if (BaseConstans.hasLogin()) {
            toAddSticker();
        } else {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (getActivity() != null) {
            if (requestCode == 1 || requestCode == 2) {
                ArrayList<String> deniedPermission = new ArrayList<>();
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    int result = grantResults[i];
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        deniedPermission.add(permission);
                    }
                }
                if (deniedPermission.isEmpty()) {
                    LogUtil.d(TAG, "requestCode = " + 1);
                    if (requestCode == 1) {
                        toMakeAlbum();
                    } else {
                        toCreateVideo();
                    }
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setMessage("读取相册必须获取存储权限，如需使用接下来的功能，请同意授权~")
                            .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                                dialog.dismiss();
                            })
                            .setPositiveButton("去授权", (dialog, which) -> {
                                PermissionUtil.gotoPermission(getActivity());
                                dialog.dismiss();
                            }).create()
                            .show();
                }
            }
        }
    }

    private void toMakeAlbum() {
        if (BaseConstans.hasLogin()) {
            StatisticsEventAffair.getInstance().setFlag(getActivity(), "21_yj_click");
            mLoadingDialog.show();
            presenter.requestPictureAlbumData();
        } else {
            Intent intentToLogin = new Intent(getActivity(), LoginActivity.class);
            intentToLogin.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intentToLogin);
        }
    }


    private void toAddSticker() {
        StatisticsEventAffair.getInstance().setFlag(getActivity(), "6_customize_bj");
        AlbumManager.chooseAlbum(getActivity(), 1, SELECTALBUM, (tag, paths, isCancel, isFromCamera, albumFileList) -> {
            if (!isCancel) {
                if (!TextUtils.isEmpty(paths.get(0))) {
                    MattingImage mattingImage = new MattingImage();
                    mattingImage.createHandle(getActivity(), isDone -> {
                        if (isDone) {
                            String pathType = GetPathTypeModel.getInstance().getMediaType(paths.get(0));
                            if (AlbumType.isVideo(pathType)) {
                                Intent intent = new Intent(getActivity(), VideoCropActivity.class);
                                intent.putExtra("videoPath", paths.get(0));
                                intent.putExtra("comeFrom", "");
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            } else {
                                compressImage(paths.get(0));
                            }
                        }
                    });
                }
            }
        }, "toAddSticker");
    }


    /**
     * 前往影集页面
     */
    private void toPhotographAlbum(NewFragmentTemplateItem item, String templateFilePath) {
        mLoadingDialog.dismiss();
        if (getActivity() != null) {
            AlbumManager.chooseAlbum(getActivity(), 20, SELECTALBUM, (tag, paths, isCancel, isFromCamera, albumFileList) -> {
                if (!isCancel) {
                    Intent intent = new Intent(getActivity(), TemplateActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("paths", (ArrayList<String>) paths);
                    LogUtil.d(TAG, "paths.size = " + paths.size());
                    bundle.putInt("isPicNum", 20);
                    bundle.putString("fromTo", FromToTemplate.PICTUREALBUM);
                    bundle.putInt("picout", 0);
                    bundle.putInt("is_anime", 0);
                    bundle.putString("templateName", item.getTitle());
                    bundle.putString("templateId", item.getId() + "");
                    bundle.putStringArrayList("originalPath", (ArrayList<String>) paths);
                    bundle.putString("templateFilePath", templateFilePath);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("Message", bundle);
                    intent.putExtra("person", item);
                    startActivity(intent);
                }
            }, "pictureAlbum");
        }
    }


    private void compressImage(String path) {
        if (getActivity() != null) {
            CompressionCuttingManage manage = new CompressionCuttingManage(getActivity(), "", true, tailorPaths -> {
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), CreationTemplateActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("paths", tailorPaths.get(0));
                    bundle.putString("bjTemplateTitle", "");
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
        if (getActivity() != null) {
            showWitchBtn(lastViewPagerChoosePosition);
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int offset = Math.abs(verticalOffset);
        int total = appBarLayout.getTotalScrollRange();
        int alphaOut = (total - offset) < 0 ? 0 : total - offset;
        float percentagef = alphaOut / (float) total;
        float percentage = percentagef * 100;
        int percent = (int) percentage;
        int topPercent = 100 - percent;
        mBinding.viewTop.tvViewMask.getBackground().setAlpha(topPercent + 30);
        if (offset <= total * 2 / 3) {
            mBinding.llExpand.setScaleY(percentagef);
            mBinding.llExpand.setVisibility(View.VISIBLE);
            mBinding.llClose.setVisibility(View.GONE);
            mBinding.llExpand.bringToFront();
        } else {
            mBinding.llExpand.setScaleY(1);
            mBinding.llExpand.setVisibility(View.GONE);
            mBinding.llClose.setVisibility(View.VISIBLE);
            mBinding.llClose.bringToFront();
        }
    }

}


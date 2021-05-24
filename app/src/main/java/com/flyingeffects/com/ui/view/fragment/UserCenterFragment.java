package com.flyingeffects.com.ui.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.home_vp_frg_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.databinding.FragmentUserCenterBinding;
import com.flyingeffects.com.entity.RequestMessage;
import com.flyingeffects.com.entity.SystemMessageCountAllEntiy;
import com.flyingeffects.com.entity.UserInfo;
import com.flyingeffects.com.entity.messageCount;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.AdConfigs;
import com.flyingeffects.com.manager.AdManager;
import com.flyingeffects.com.manager.AlbumManager;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.manager.huaweiObs;
import com.flyingeffects.com.ui.interfaces.AlbumChooseCallback;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.ui.view.activity.AboutActivity;
import com.flyingeffects.com.ui.view.activity.BuyVipActivity;
import com.flyingeffects.com.ui.view.activity.EditInformationActivity;
import com.flyingeffects.com.ui.view.activity.FansActivity;
import com.flyingeffects.com.ui.view.activity.LikeActivity;
import com.flyingeffects.com.ui.view.activity.LoginActivity;
import com.flyingeffects.com.ui.view.activity.MineFocusActivity;
import com.flyingeffects.com.ui.view.activity.SystemMessageDetailActivity;
import com.flyingeffects.com.ui.view.activity.ZanActivity;
import com.flyingeffects.com.utils.CheckVipOrAdUtils;
import com.flyingeffects.com.utils.FileUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.PermissionUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.TimeUtils;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.utils.UCropOption;
import com.google.android.material.appbar.AppBarLayout;
import com.lansosdk.videoeditor.LanSongFileUtil;
import com.nineton.ntadsdk.bean.AdInfoBean;
import com.nineton.ntadsdk.itr.ImageAdCallBack;
import com.nineton.ntadsdk.manager.ImageAdManager;
import com.orhanobut.hawk.Hawk;
import com.shixing.sxve.ui.view.WaitingDialog;
import com.yalantis.ucrop.UCrop;
import com.yanzhenjie.album.AlbumFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


/**
 * user :TongJu  ;描述：用戶中心
 * 时间：2018/4/24
 **/

public class UserCenterFragment extends BaseFragment implements AlbumChooseCallback, AppBarLayout.OnOffsetChangedListener {
    public final static int SELECTALBUMFROMUSETCENTERBJ = 1;
    private static final int CODE_PEELING = 10;

    private static final String[] TITLES = {"我上传的作品", "喜欢", "模板收藏"};

    private UCrop.Options options;
    String systemMessageId = "";
    private FragmentUserCenterBinding mBinding;

    @Override
    protected int getContentLayout() {
        return 0;
    }

    @Override
    protected View getBindingView(LayoutInflater inflater, ViewGroup container) {
        mBinding = FragmentUserCenterBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }


    @Override
    protected void initView() {
        options = UCropOption.getInstance().getUcropOption();
        setOnClickListener();
        if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
            AdManager.getInstance().showImageAd(getActivity(), AdConfigs.AD_IMAGE_message, mBinding.llAdContent);
//            loadImageAd();
        }
    }

    private void setOnClickListener() {
        mBinding.llIconZan.setOnClickListener(this::onViewClicked);
        mBinding.llComment.setOnClickListener(this::onViewClicked);
        mBinding.llPrivateMessage.setOnClickListener(this::onViewClicked);
        mBinding.vAttention.setOnClickListener(this::onViewClicked);
        mBinding.vFans.setOnClickListener(this::onViewClicked);
        mBinding.ivPeeling.setOnClickListener(this::onViewClicked);
        mBinding.tvEditInformation.setOnClickListener(this::onViewClicked);
        mBinding.tvIntroduction.setOnClickListener(this::onViewClicked);
        mBinding.tvGoLogin.setOnClickListener(this::onViewClicked);
        mBinding.ivAbout.setOnClickListener(this::onViewClicked);
        mBinding.tvVipBtn.setOnClickListener(this::onViewClicked);
    }

    private void onViewClicked(View view) {
        if (view == mBinding.llIconZan) {
            onClickZan();
        } else if (view == mBinding.llComment) {
            showComment();
        } else if (view == mBinding.llPrivateMessage) {
            showPrivateMessage();
        } else if (view == mBinding.vAttention) {
            onAttentionCount();
        } else if (view == mBinding.vFans) {
            showFansPage();
        } else if (view == mBinding.ivPeeling) {
            ActivityCompat.requestPermissions(getActivity(), PERMISSION_STORAGE, CODE_PEELING);
        } else if (view == mBinding.tvIntroduction || view == mBinding.tvEditInformation) {
            editInformation();
        } else if (view == mBinding.tvGoLogin) {
            toLogin();
        } else if (view == mBinding.ivAbout) {
            openAboutPage();
        } else if (view == mBinding.tvVipBtn) {
            startBuyVipActivity();
        }
    }


    private void startBuyVipActivity() {
        Intent intent = new Intent(getActivity(), BuyVipActivity.class);
        startActivity(intent);
    }

    private void showFansPage() {
        if (!DoubleClick.getInstance().isFastDoubleClick()) {
            if (BaseConstans.hasLogin()) {
                Intent intentFan = new Intent(getActivity(), FansActivity.class);
                intentFan.putExtra("to_user_id", BaseConstans.getUserId());
                intentFan.putExtra("from", 0);
                startActivity(intentFan);
            } else {
                ToastUtil.showToast(getString(R.string.need_login));
            }
        }
    }


    private void openAboutPage() {
        if (!DoubleClick.getInstance().isFastDoubleClick()) {
            StatisticsEventAffair.getInstance().setFlag(getActivity(), "3_help");
            Intent intent = new Intent(getActivity(), AboutActivity.class);
            startActivity(intent);
        }
    }

    private void toLogin() {
        if (!BaseConstans.hasLogin()) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
    }

    private void editInformation() {
        if (!DoubleClick.getInstance().isFastDoubleClick()) {
            StatisticsEventAffair.getInstance().setFlag(getContext(), "3_Information");
            Intent intent = new Intent(getActivity(), EditInformationActivity.class);
            startActivity(intent);
        }
    }


    private void onAttentionCount() {
        if (!DoubleClick.getInstance().isFastDoubleClick()) {
            if (BaseConstans.hasLogin()) {
                Intent intent = new Intent(getActivity(), MineFocusActivity.class);
                intent.putExtra("to_user_id", BaseConstans.getUserId());
                startActivity(intent);
            } else {
                ToastUtil.showToast(getString(R.string.need_login));
            }
        }
    }

    private void showPrivateMessage() {
        StatisticsEventAffair.getInstance().setFlag(getActivity(), "12_system");
        if (BaseConstans.hasLogin()) {
            if (!TextUtils.isEmpty(systemMessageId)) {
                Intent intent = new Intent(getActivity(), SystemMessageDetailActivity.class);
                intent.putExtra("needId", systemMessageId);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                ToastUtil.showToast("没有私信哦~");
            }
        } else {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    private void showComment() {
        if (BaseConstans.hasLogin()) {
            StatisticsEventAffair.getInstance().setFlag(getActivity(), "12_comment");
            Intent intentComment = new Intent(getActivity(), LikeActivity.class);
            intentComment.putExtra("from", 1);
            intentComment.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentComment);
        } else {
            ToastUtil.showToast(getActivity().getResources().getString(R.string.need_login));
        }
    }


    /**
     * 点赞
     */
    private void onClickZan() {
        if (!DoubleClick.getInstance().isFastDoubleClick()) {
            if (BaseConstans.hasLogin()) {
                Intent intentZan = new Intent(getActivity(), ZanActivity.class);
                intentZan.putExtra("from", 1);
                startActivity(intentZan);
            } else {
                ToastUtil.showToast(getActivity().getResources().getString(R.string.need_login));
            }
        }
    }


    @Override
    protected void initAction() {
        initTabData();
    }

    @Override
    protected void initData() {
        mBinding.appbar.addOnOffsetChangedListener(this);
    }


    @Override
    public void onResume() {
        if (getActivity() != null) {
            //未登陆
            if (BaseConstans.hasLogin()) {
                mBinding.tvId.setVisibility(View.VISIBLE);
                mBinding.tvId.setText("飞友号：" + BaseConstans.getUserId());
                mBinding.tvEditInformation.setVisibility(View.VISIBLE);
                mBinding.gVipShow.setVisibility(View.VISIBLE);
                mBinding.gLoginShow.setVisibility(View.VISIBLE);
                mBinding.gNoLoginInfo.setVisibility(View.GONE);
                requestUserInfo();
                requestMessageCount();
                requestSystemMessageCount();
            } else {
                mBinding.tvId.setVisibility(View.GONE);
                Glide.with(this)
                        .load(R.mipmap.head)
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(mBinding.ivHead);
                Glide.with(getActivity())
                        .load(R.mipmap.home_page_bj)
                        .into(mBinding.ivUserSkin);
                mBinding.tvEditInformation.setVisibility(View.GONE);
                mBinding.gVipShow.setVisibility(View.INVISIBLE);
                mBinding.gLoginShow.setVisibility(View.GONE);
                mBinding.tvAvatarVipIcon.setVisibility(View.INVISIBLE);
                mBinding.gNoLoginInfo.setVisibility(View.VISIBLE);
            }

//            if (imageAdManager != null) {
//                imageAdManager.adResume();
//            }
            AdManager.getInstance().imageAdResume();
        }
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
//        if (imageAdManager != null) {
//            imageAdManager.adPause();
//        }\
        AdManager.getInstance().imageAdPause();
    }

    private void initTabData() {
        FragmentManager manager = getChildFragmentManager();
        ArrayList<Fragment> list = new ArrayList<>();
        frag_user_upload_bj fag_1 = new frag_user_upload_bj();
        list.add(fag_1);

        fragHomePage fagLike = new fragHomePage();
        Bundle bundle1 = new Bundle();
        bundle1.putSerializable("toUserId", BaseConstans.getUserId());
        bundle1.putSerializable("isFrom", 2);
        bundle1.putSerializable("fromTo", FromToTemplate.ISHOMEMYLIKE);
        fagLike.setArguments(bundle1);
        list.add(fagLike);

        frag_user_collect fag_0 = new frag_user_collect();
        Bundle bundle2 = new Bundle();
        bundle2.putSerializable("template_type", "1");
        fag_0.setArguments(bundle2);
        list.add(fag_0);
        home_vp_frg_adapter adapter = new home_vp_frg_adapter(manager, list);
        mBinding.viewpager.setAdapter(adapter);
        mBinding.tlTabs.setViewPager(mBinding.viewpager, TITLES);
    }


    private void toPeeling() {
        if (!DoubleClick.getInstance().isFastDoubleClick()) {
            StatisticsEventAffair.getInstance().setFlag(getActivity(), "3_background");
            AlbumManager.chooseImageAlbum(getContext(), 1, SELECTALBUMFROMUSETCENTERBJ, this, "");
        }
    }

    private void requestSystemMessageCount() {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", BaseConstans.getUserId());
        Observable ob = Api.getDefault().systemTotal(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<SystemMessageCountAllEntiy>(getActivity()) {
            @Override
            protected void onSubError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void onSubNext(SystemMessageCountAllEntiy data) {
                String str = StringUtil.beanToJSONString(data);
                LogUtil.d("OOM", str);
                systemMessageId = data.getSystem_message().get(0).getId();
                if (data.getSystem_message().get(0).getTotal() == 0) {
                    mBinding.tvPrivateMessage.setVisibility(View.GONE);
                } else {
                    mBinding.tvPrivateMessage.setVisibility(View.VISIBLE);
                    mBinding.tvPrivateMessage.setText(String.valueOf(data.getSystem_message().get(0).getTotal()));
                }
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }

    private void requestUserInfo() {
        if (getActivity() != null) {
            HashMap<String, String> params = new HashMap<>();
            params.put("to_user_id", BaseConstans.getUserId());
            // 启动时间
            Observable ob = Api.getDefault().getOtherUserinfo(BaseConstans.getRequestHead(params));
            HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<UserInfo>(getActivity()) {
                @Override
                protected void onSubError(String message) {
                    mBinding.tvId.setText("未登录");
                    BaseConstans.setUserToken("");
                }

                @Override
                protected void onSubNext(UserInfo data) {
                    Hawk.put(UserInfo.USER_INFO_KEY, data);
                    LogUtil.d(TAG,"userInfo is refresh");
                    if (getActivity() != null) {
                        mBinding.tvId.setText("飞友号：" + data.getId());
                        if (!TextUtils.isEmpty(data.getNickname())) {
                            mBinding.tvName.setText(data.getNickname());
                            mBinding.tvName.setVisibility(View.VISIBLE);
                        } else {
                            mBinding.tvName.setVisibility(View.GONE);
                        }
                        if (!TextUtils.isEmpty(data.getPhotourl())) {
                            Glide.with(getActivity())
                                    .load(data.getPhotourl())
                                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                    .into(mBinding.ivHead);
                        } else {
                            Glide.with(getActivity())
                                    .load(R.mipmap.head)
                                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                    .into(mBinding.ivHead);
                        }
                        //创作的视频数量
                        mBinding.tvVideoCount.setText(data.getUser_video());
                        //我关注的数量
                        mBinding.tvAttentionCount.setText(data.getUser_watch());
                        //关注我的数量
                        mBinding.tvFansCount.setText(data.getUser_follower());
                        if (TextUtils.isEmpty(data.getSkin())) {
                            Glide.with(getActivity())
                                    .load(R.mipmap.home_page_bj)
                                    .into(mBinding.ivUserSkin);
                        } else {
                            Glide.with(getActivity())
                                    .load(data.getSkin())
                                    .into(mBinding.ivUserSkin);
                        }
                        if (!TextUtils.isEmpty(data.getRemark())) {
                            mBinding.tvIntroduction.setText(data.getRemark());
                            mBinding.tvIntroduction.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
                        } else {
                            mBinding.tvIntroduction.setText("您还没有填写简介，点击编辑资料添加");
                            mBinding.tvIntroduction.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.icon_edit, 0, 0, 0);
                        }
                    }
                    BaseConstans.setUserId(data.getId(), data.getNickname(), data.getPhotourl());
                    startVipInfo(data);
                }
            }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
        }
    }

    private static final String TAG = "UserCenterFragment";

    private void startVipInfo(UserInfo data) {
        String vipBtnStr = "";
        String vipDateStr = "";
        String vipIconStr = "";
        vipDateStr = TimeUtils.formatTheDate(data.getVip_end_time());
        Log.d(TAG, "vipEndTime = " + vipDateStr);
        if (data.getIs_vip() == CheckVipOrAdUtils.IS_VIP) {
            mBinding.tvAvatarVipIcon.setVisibility(View.VISIBLE);
            switch (data.getVip_grade()) {
                case CheckVipOrAdUtils.VIP_GRADE_MONTH:
                    vipBtnStr = "立即续费";
                    vipDateStr = TimeUtils.formatTheDate(data.getVip_end_time()) + "到期";
                    vipIconStr = "月";
                    break;
                case CheckVipOrAdUtils.VIP_GRADE_YEAR:
                    vipBtnStr = "立即续费";
                    vipDateStr = TimeUtils.formatTheDate(data.getVip_end_time()) + "到期";
                    vipIconStr = "年";
                    break;
                case CheckVipOrAdUtils.VIP_GRADE_FOREVER:
                    vipBtnStr = "会员中心";
                    vipDateStr = "永久会员";
                    vipIconStr = "永久";
                    break;
                default:
                    vipDateStr = "永久会员";
                    vipBtnStr = "会员中心";
                    vipIconStr = "永久";
                    break;
            }
        } else {
            vipDateStr = "解锁模板，无视频无广告";
            vipBtnStr = "立即开通";
            mBinding.tvAvatarVipIcon.setVisibility(View.INVISIBLE);
        }
        mBinding.tvVipTimeText.setText(vipDateStr);
        mBinding.tvVipBtn.setText(vipBtnStr);
        mBinding.tvAvatarVipIcon.setText(vipIconStr);
    }

    @Override
    public void resultFilePath(int tag, List<String> paths, boolean isCancel, boolean isFromCamera, ArrayList<AlbumFile> albumFileList) {
        if (!isCancel && paths != null && paths.size() > 0) {
            try {
                File srcFile = new File(paths.get(0));
                //中文路径无法识别问题，重命名
                File srcEngfile = new File(getContext().getExternalFilesDir("runCatch/"), "skinPath." + LanSongFileUtil.getFileSuffix(srcFile.getPath()));
                FileUtil.copyFile(srcFile, srcEngfile.getPath());
                File destFile = new File(LanSongFileUtil.createFileInBox(LanSongFileUtil.getFileSuffix(srcFile.getPath())));
                if (destFile.exists() && srcEngfile.exists()) {
                    Uri sourceUri = Uri.fromFile(srcEngfile);
                    Uri destinationUri = Uri.fromFile(destFile);
                    UCrop.of(sourceUri, destinationUri)
                            .withAspectRatio(16, 9)
                            .withMaxResultSize(1280, 720)
                            .withOptions(options)
                            .start(getActivity());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            String path = resultUri.getPath();
            String type = path.substring(path.length() - 4);
            String nowTime = StringUtil.getCurrentTimeymd();
            String huaweiSkinPath = "media/android/user_skin_img/" + nowTime + "/" + System.currentTimeMillis() + type;
            uploadFileToHuawei(path, huaweiSkinPath);
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
                    Log.d("OOM2", "isSuccess");
                    Observable.just(path).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            WaitingDialog.closeProgressDialog();
                            uploadUserSkin(s);
                        }
                    });

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
                    protected void onSubError(String message) {
                        ToastUtil.showToast(message);
                    }

                    @Override
                    protected void onSubNext(Object data) {
                        Glide.with(getActivity())
                                .load(skinPath)
                                .into(mBinding.ivUserSkin);
                    }
                }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);
    }

    private void requestMessageCount() {
        HashMap<String, String> params = new HashMap<>();
        Observable ob = Api.getDefault().getAllMessageNum(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<messageCount>(getActivity()) {
            @Override
            protected void onSubError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void onSubNext(messageCount data) {
                showMessageCount(data);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }

    private void showMessageCount(messageCount data) {
        String follow_num = data.getFollow_num();
        int followNum = Integer.parseInt(follow_num);
        if (followNum == 0) {
            mBinding.tvCommentCountAdd.setVisibility(View.GONE);
        } else {
            mBinding.tvCommentCountAdd.setVisibility(View.VISIBLE);
            mBinding.tvCommentCountAdd.setText(followNum + "");
        }
        String praise_num = data.getPraise_num();
        int praiseNum = Integer.parseInt(praise_num);
        if (praiseNum == 0) {
            mBinding.tvZan.setVisibility(View.GONE);
        } else {
            mBinding.tvZan.setVisibility(View.VISIBLE);
            mBinding.tvZan.setText(praiseNum + "");
        }
        String commentNum = data.getComment_num();
        int commentNumInt = Integer.parseInt(commentNum);
        if (commentNumInt == 0) {
            mBinding.tvCommentCount.setVisibility(View.GONE);
        } else {
            mBinding.tvCommentCount.setVisibility(View.VISIBLE);
            mBinding.tvCommentCount.setText(commentNumInt + "");
        }
    }

    @Subscribe
    public void onEventMainThread(RequestMessage event) {
        if (getActivity() != null) {
            LogUtil.d("OOM", "onEventMainThread");
            if (BaseConstans.hasLogin()) {
                requestMessageCount();
                requestSystemMessageCount();
            } else {
                mBinding.tvCommentCountAdd.setVisibility(View.GONE);
                mBinding.tvZan.setVisibility(View.GONE);
                mBinding.tvCommentCount.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int offset = Math.abs(verticalOffset);
        int total = appBarLayout.getTotalScrollRange();
        LogUtil.d("OOM2", "offset=" + offset + "total=" + total);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> deniedPermission = new ArrayList<>();
        deniedPermission.clear();
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            int result = grantResults[i];
            if (result != PackageManager.PERMISSION_GRANTED) {
                deniedPermission.add(permission);
            }
        }
        if (deniedPermission.isEmpty()) {
            if (requestCode == CODE_PEELING) {
                toPeeling();
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



package com.flyingeffects.com.ui.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.flyingeffects.com.enity.RequestMessage;
import com.flyingeffects.com.enity.SystemMessageCountAllEntiy;
import com.flyingeffects.com.enity.UserInfo;
import com.flyingeffects.com.enity.messageCount;
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
import com.flyingeffects.com.ui.view.activity.EditInformationActivity;
import com.flyingeffects.com.ui.view.activity.FansActivity;
import com.flyingeffects.com.ui.view.activity.LikeActivity;
import com.flyingeffects.com.ui.view.activity.LoginActivity;
import com.flyingeffects.com.ui.view.activity.MineFocusActivity;
import com.flyingeffects.com.ui.view.activity.SystemMessageDetailActivity;
import com.flyingeffects.com.ui.view.activity.ZanActivity;
import com.flyingeffects.com.ui.view.activity.webViewActivity;
import com.flyingeffects.com.utils.FileUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
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
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


/**
 * user :TongJu  ;描述：用戶中心
 * 时间：2018/4/24
 **/

public class frag_user_center extends BaseFragment implements AlbumChooseCallback, AppBarLayout.OnOffsetChangedListener  {
    public final static int SELECTALBUMFROMUSETCENTERBJ = 1;

    private String[] titles = {"我上传的背景", "喜欢", "模板收藏"};

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
    @BindView(R.id.tv_edit_information)
    TextView tvEditInformation;
    @BindView(R.id.tv_private_message)
    TextView mTVPrivateMessage;
    @BindView(R.id.tv_comment_count)
    TextView mTVCommentCount;
    @BindView(R.id.tv_zan)
    TextView mTVZan;
    @BindView(R.id.tv_comment_count_add)
    TextView mTVCommentCountAdd;
    @BindView(R.id.tv_go_login)
    TextView mTVGoLogin;
    @BindView(R.id.ll_info)
    LinearLayout mLLInfo;
    @BindView(R.id.ll_info_Related)
    LinearLayout mLLInfoRelated;
    @BindView(R.id.ll_no_login_info)
    LinearLayout mLLNoLoginInfo;
    @BindView(R.id.tv_top_name)
    TextView tv_top_name;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.ll_ad_content)
    LinearLayout mLLADContent;
    @BindView(R.id.ll_ad_entrance)
    LinearLayout ll_ad_entrance;

    private UCrop.Options options;
    String systemMessageId ="";


    @Override
    protected int getContentLayout() {
        return R.layout.fag_user_center;
    }


    @Override
    protected void initView() {
        options = UCropOption.getInstance().getUcropOption();
        iv_about.setOnClickListener(view -> {
            if (!DoubleClick.getInstance().isFastDoubleClick()) {
                StatisticsEventAffair.getInstance().setFlag(getActivity(), "3_help");
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
            }
        });
        if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
            AdManager.getInstance().showImageAd(getActivity(), AdConfigs.AD_IMAGE_message, mLLADContent, new AdManager.Callback() {
                @Override
                public void adClose() {
                }
            });
            loadImageAd();
        }

    }

    @Override
    protected void initAction() {
       initTabData();
    }

    @Override
    protected void initData() {
        appbar.addOnOffsetChangedListener(this);

    }

    @Override
    public void onResume() {
        if (getActivity() != null) {
            //未登陆
            if (BaseConstans.hasLogin()) {
                tv_id.setVisibility(View.VISIBLE);
                tv_id.setText("飞友号：" + BaseConstans.GetUserId());
                tvEditInformation.setVisibility(View.VISIBLE);
                mLLInfo.setVisibility(View.VISIBLE);
                mLLInfoRelated.setVisibility(View.VISIBLE);
                mTVGoLogin.setVisibility(View.GONE);
                mLLNoLoginInfo.setVisibility(View.GONE);

                requestUserInfo();
                requestMessageCount();
                requestSystemMessageCount();
            } else {
                tv_top_name.setText("未登录");
                tv_id.setVisibility(View.GONE);
                Glide.with(this)
                        .load(R.mipmap.head)
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(iv_head);
                mLLInfo.setVisibility(View.GONE);
                Glide.with(getActivity())
                        .load(R.mipmap.home_page_bj)
                        .into(imSkin);
                tvEditInformation.setVisibility(View.GONE);
                mLLInfoRelated.setVisibility(View.GONE);
                mTVGoLogin.setVisibility(View.VISIBLE);
                mLLNoLoginInfo.setVisibility(View.VISIBLE);
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

        fragHomePage fag_like = new fragHomePage();
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
        tabLayout.setViewPager(viewpager, titles);
    }


    @OnClick({R.id.ll_icon_zan, R.id.ll_comment,R.id.ll_private_message,R.id.ll_attention_count,
            R.id.ll_video_count, R.id.iv_Peeling, R.id.tv_edit_information, R.id.ll_edit_data,R.id.tv_go_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_icon_zan:
                if (!DoubleClick.getInstance().isFastDoubleClick()) {
                    if (BaseConstans.hasLogin()) {
                        Intent intentZan = new Intent(getActivity(), ZanActivity.class);
                        intentZan.putExtra("from", 1);
                        startActivity(intentZan);
                    } else {
                        ToastUtil.showToast(getActivity().getResources().getString(R.string.need_login));
                    }
                }
                break;
            case R.id.ll_comment:
                if (BaseConstans.hasLogin()) {
                    StatisticsEventAffair.getInstance().setFlag(getActivity(), "12_comment");
                    Intent intentComment = new Intent(getActivity(), LikeActivity.class);
                    intentComment.putExtra("from", 1);
                    intentComment.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentComment);
                } else {
                    ToastUtil.showToast(getActivity().getResources().getString(R.string.need_login));
                }
                break;
            case R.id.ll_private_message:
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
                break;
            case R.id.ll_attention_count:
                if (!DoubleClick.getInstance().isFastDoubleClick()) {
                    if (BaseConstans.hasLogin()) {
                        Intent intentFoucs = new Intent(getActivity(), MineFocusActivity.class);
                        intentFoucs.putExtra("to_user_id", BaseConstans.GetUserId());
                        startActivity(intentFoucs);
                    } else {
                        ToastUtil.showToast(getActivity().getResources().getString(R.string.need_login));
                    }
                }
                break;
            case R.id.ll_video_count:
                if (!DoubleClick.getInstance().isFastDoubleClick()) {
                    if (BaseConstans.hasLogin()) {
                        Intent intentFan = new Intent(getActivity(), FansActivity.class);
                        intentFan.putExtra("to_user_id", BaseConstans.GetUserId());
                        intentFan.putExtra("from", 0);
                        startActivity(intentFan);
                    } else {
                        ToastUtil.showToast(getActivity().getResources().getString(R.string.need_login));
                    }
                }
                break;
            case R.id.iv_Peeling:

                if (!DoubleClick.getInstance().isFastDoubleClick()) {
                    StatisticsEventAffair.getInstance().setFlag(getActivity(), "3_background");
                    AlbumManager.chooseImageAlbum(getContext(), 1, SELECTALBUMFROMUSETCENTERBJ, this, "");
                }
                break;
            case R.id.tv_edit_information:
            case R.id.ll_edit_data:
                if (!DoubleClick.getInstance().isFastDoubleClick()) {
                    StatisticsEventAffair.getInstance().setFlag(getContext(), "3_Information");
                    Intent intent = new Intent(getActivity(), EditInformationActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.tv_go_login:
                if (!BaseConstans.hasLogin()) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }


                break;
            default:
                break;
        }
    }

    private void requestSystemMessageCount() {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", BaseConstans.GetUserId());
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
                    mTVPrivateMessage.setVisibility(View.GONE);
                } else {
                    mTVPrivateMessage.setVisibility(View.VISIBLE);
                    mTVPrivateMessage.setText(String.valueOf(data.getSystem_message().get(0).getTotal()));
                }
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }

    private void requestUserInfo() {
        if (getActivity() != null) {
            HashMap<String, String> params = new HashMap<>();
            params.put("to_user_id", BaseConstans.GetUserId());
            // 启动时间
            Observable ob = Api.getDefault().getOtherUserinfo(BaseConstans.getRequestHead(params));
            HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<UserInfo>(getActivity()) {
                @Override
                protected void onSubError(String message) {
                    tv_id.setText("未登录");
                    BaseConstans.SetUserToken("");
                }

                @Override
                protected void onSubNext(UserInfo data) {
                    Hawk.put("UserInfo", data);
                    if (getActivity() != null) {
                        tv_id.setText("飞友号：" + data.getId());
                        if (!TextUtils.isEmpty(data.getNickname())) {
                            tv_name.setText(data.getNickname());
                            tv_name.setVisibility(View.VISIBLE);
                        } else {
                            tv_name.setVisibility(View.GONE);
                        }
                        tv_top_name.setText(data.getNickname());
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
                        //创作的视频数量
                        fans_count.setText(data.getUser_video());
                        //我关注的数量
                        attention_count.setText(data.getUser_watch());
                        //关注我的数量
                        tv_video_count.setText(data.getUser_follower());
                        if (TextUtils.isEmpty(data.getSkin())) {
                            Glide.with(getActivity())
                                    .load(R.mipmap.home_page_bj)
                                    .into(imSkin);
                        } else {
                            Glide.with(getActivity())
                                    .load(data.getSkin())
                                    .into(imSkin);
                        }
                        if (!TextUtils.isEmpty(data.getRemark())) {
                            tvIntroduction.setText(data.getRemark());
                            imEdit.setVisibility(View.GONE);
                        } else {
                            tvIntroduction.setText("您还没有填写简介，点击编辑资料添加");
                            imEdit.setVisibility(View.VISIBLE);
                        }
                    }
                    BaseConstans.SetUserId(data.getId(), data.getNickname(), data.getPhotourl());
                }
            }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
        }
    }

    @Override
    public void resultFilePath(int tag, List<String> paths, boolean isCancel,boolean isFromCamera, ArrayList<AlbumFile> albumFileList) {
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
                                .into(imSkin);
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
            mTVCommentCountAdd.setVisibility(View.GONE);

        } else {
            mTVCommentCountAdd.setVisibility(View.VISIBLE);
            mTVCommentCountAdd.setText(followNum + "");
        }
        String praise_num = data.getPraise_num();
        int praiseNum = Integer.parseInt(praise_num);
        if (praiseNum == 0) {
            mTVZan.setVisibility(View.GONE);
        } else {
            mTVZan.setVisibility(View.VISIBLE);
            mTVZan.setText(praiseNum + "");
        }
        String comment_num = data.getComment_num();
        int commentNum = Integer.parseInt(comment_num);
        if (commentNum == 0) {
            mTVCommentCount.setVisibility(View.GONE);
        } else {
            mTVCommentCount.setVisibility(View.VISIBLE);
            mTVCommentCount.setText(commentNum + "");
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
                mTVCommentCountAdd.setVisibility(View.GONE);
                mTVZan.setVisibility(View.GONE);
                mTVCommentCount.setVisibility(View.GONE);
            }
        }
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


    /**
     * 加载图片广告
     */
    private void loadImageAd() {
        ImageAdManager  imageAdManager = new ImageAdManager();
        imageAdManager.showImageAd(getActivity(), AdConfigs.APP_FUDONG, ll_ad_entrance, null, new ImageAdCallBack() {
            @Override
            public void onImageAdShow(View adView, String adId, String adPlaceId, AdInfoBean adInfoBean) {
                if (adView != null) {
                    ll_ad_entrance.removeAllViews();
                    ll_ad_entrance.addView(adView);
                }
            }

            @Override
            public void onImageAdError(String error) {
                LogUtil.e("ImageAdError = " + error);
            }

            @Override
            public void onImageAdClose() {

            }

            @Override
            public boolean onImageAdClicked(String title, String url, boolean isNtAd, boolean openURLInSystemBrowser) {
                return false;
            }
        });
    }


}



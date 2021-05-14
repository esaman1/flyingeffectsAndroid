package com.flyingeffects.com.ui.view.activity;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.databinding.ActAboutBinding;
import com.flyingeffects.com.databinding.ViewTopBinding;
import com.flyingeffects.com.manager.DataCleanManager;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.ui.view.dialog.CommonMessageDialog;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.PermissionUtil;
import com.flyingeffects.com.utils.SystemUtil;
import com.flyingeffects.com.utils.ToastUtil;


/**
 * 关于界面
 */
public class AboutActivity extends BaseActivity {


    private Context mContext;
    private ActAboutBinding mBinding;
    private ViewTopBinding mTopBinding;

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initView() {
        mContext = AboutActivity.this;

        mBinding = ActAboutBinding.inflate(getLayoutInflater());
        View rootView = mBinding.getRoot();
        mTopBinding = ViewTopBinding.bind(rootView);
        setContentView(rootView);

        mTopBinding.tvTopTitle.setText("关于");
        mBinding.tvVersionNumber.setText("飞闪版本 " + SystemUtil.getVersionName(this));

        setOnClickListener();
    }


    @Override
    protected void initAction() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BaseConstans.hasLogin()) {
            mTopBinding.tvTopSubmit.setVisibility(View.VISIBLE);
            mTopBinding.tvTopSubmit.setText("退出登录");
            mTopBinding.tvTopSubmit.setTextColor(Color.parseColor("#FE2C55"));
        } else {
            mTopBinding.tvTopSubmit.setVisibility(View.GONE);
        }
    }

    private void setOnClickListener() {
        mBinding.tvTest.setOnClickListener(this::onViewClick);
        mTopBinding.tvTopSubmit.setOnClickListener(this::onViewClick);
        mTopBinding.ivTopBack.setOnClickListener(this::onViewClick);
        mBinding.tvCloseAccount.setOnClickListener(this::onViewClick);
        mBinding.tvContactUs.setOnClickListener(this::onViewClick);
        mBinding.tvRelationUs.setOnClickListener(this::onViewClick);
        mBinding.tvPrivacyPolicy.setOnClickListener(this::onViewClick);
        mBinding.tvProtocol.setOnClickListener(this::onViewClick);
        mBinding.tvClearCache.setOnClickListener(this::onViewClick);
        mBinding.tvNotificationManagement.setOnClickListener(this::onViewClick);
        mBinding.tvVersionNumber.setOnClickListener(this::onViewClick);
    }

    private void onViewClick(View view) {
        if (view == mBinding.tvCloseAccount) {
            if (!BaseConstans.hasLogin()) {
                ToastUtil.showToast(getString(R.string.have_not_login));
            } else {
                goActivity(SignoutActivity.class);
            }
        } else if (view == mTopBinding.ivTopBack) {
            finish();
        } else if (view == mTopBinding.tvTopSubmit) {
            showDialog();
        } else if (view == mBinding.tvTest) {
            //抠图测试
//                AlbumManager.chooseVideo(this, 1, 1, (tag, paths, isCancel, albumFileList) -> {
//                    VideoMattingModel videoMattingModel=new VideoMattingModel(paths.get(0),AboutActivity.this);
//                    videoMattingModel.addLansongCompoundVideo(paths.get(0));
//                }, "");
        } else if (view == mBinding.tvContactUs) {
            StatisticsEventAffair.getInstance().setFlag(this, "3_Evaluation");
            reception();
        } else if (view == mBinding.tvRelationUs) {
            StatisticsEventAffair.getInstance().setFlag(this, "3_contact");
            contactUs();
        } else if (view == mBinding.tvPrivacyPolicy) {
            Intent intentPrivacy = new Intent(this, webViewActivity.class);
            intentPrivacy.putExtra("webUrl", BaseConstans.PRIVACYPOLICY);
            startActivity(intentPrivacy);
        } else if (view == mBinding.tvProtocol) {
            Intent intentProtocol = new Intent(this, webViewActivity.class);
            intentProtocol.putExtra("webUrl", BaseConstans.PROTOCOL);
            startActivity(intentProtocol);
        } else if (view == mBinding.tvClearCache) {
            //清除外部cache下的内容
            DataCleanManager.cleanExternalCache();
            //清理内部cache
            DataCleanManager.cleanInternalCache(BaseApplication.getInstance());
            //清理内部sdk
            DataCleanManager.cleanFiles(BaseApplication.getInstance());
            DataCleanManager.cleanExternalFile();
            ToastUtil.showToast("清理成功");
        } else if (view == mBinding.tvNotificationManagement) {
            StatisticsEventAffair.getInstance().setFlag(AboutActivity.this, "3_notifications");
            PermissionUtil.gotoPermission(AboutActivity.this);
        } else if (view == mBinding.tvVersionNumber) {
            StatisticsEventAffair.getInstance().setFlag(mContext, "3_update");
            SystemUtil.openMarket(this);
        }
    }

    private void showDialog() {
        CommonMessageDialog.getBuilder(mContext)
                .setAdStatus(CommonMessageDialog.AD_STATUS_NONE)
                .setPositiveButton("确定")
                .setNegativeButton("取消")
                .setTitle("确定退出账号登录吗？")
                .setDialogBtnClickListener(new CommonMessageDialog.DialogBtnClickListener() {
                    @Override
                    public void onPositiveBtnClick(CommonMessageDialog dialog) {
                        BaseConstans.SetUserToken("");
//                        EventBus.getDefault().post(new ExitOrLogin());
                        findViewById(R.id.tv_top_submit).setVisibility(View.GONE);
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelBtnClick(CommonMessageDialog dialog) {
                        dialog.dismiss();
                    }
                }).build().show();
    }

    private void showDeleteDialog(String id) {

    }


    public void reception() {
        try {
            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            ToastUtil.showToast(getString(R.string.install_app_store_notification));
            e.printStackTrace();
        }
    }

    public void contactUs() {
        ClipboardManager tvCopy = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        tvCopy.setPrimaryClip(ClipData.newPlainText(null, BaseConstans.getService_wxi()));
        new AlertDialog.Builder(this)
                .setTitle(R.string.notification)
                .setMessage(getString(R.string.contacts_saved_to_clipboard) +
                        BaseConstans.getService_wxi() + "\n" +
                        getString(R.string.promote_message))
                .setPositiveButton(getString(R.string.confirm), (dialog, which) -> openWx())
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void openWx() {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            ToastUtil.showToast(getString(R.string.check_login_notification));
        }
    }

}

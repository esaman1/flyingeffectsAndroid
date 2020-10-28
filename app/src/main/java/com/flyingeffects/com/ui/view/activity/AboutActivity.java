package com.flyingeffects.com.ui.view.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.manager.DataCleanManager;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.utils.PermissionUtil;
import com.flyingeffects.com.utils.SystemUtil;
import com.flyingeffects.com.utils.ToastUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 关于界面
 */
public class AboutActivity extends BaseActivity {

    @BindView(R.id.tv_version_number)
    TextView tvVersionNumber;

    @Override
    protected int getLayoutId() {
        return R.layout.act_about;
    }

    @Override
    protected void initView() {
        ((TextView) findViewById(R.id.tv_top_title)).setText("关于");
        tvVersionNumber.setText("飞闪版本 " + SystemUtil.getVersionCode(this));
    }

    @Override
    protected void initAction() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BaseConstans.hasLogin()) {
            findViewById(R.id.tv_top_submit).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.tv_top_submit)).setText("退出登录");
            ((TextView) findViewById(R.id.tv_top_submit)).setTextColor(Color.parseColor("#FE2C55"));
        }else{
            findViewById(R.id.tv_top_submit).setVisibility(View.GONE);
        }
    }

    @Override
    @OnClick({R.id.tv_test,R.id.tv_top_submit, R.id.iv_top_back, R.id.tv_close_account, R.id.tv_contact_us,R.id.tv_relation_us,
             R.id.tv_privacy_policy, R.id.tv_protocol, R.id.tv_clear_cache,R.id.tv_notification_management,R.id.tv_version_number})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_close_account:
                if (!BaseConstans.hasLogin()) {
                    ToastUtil.showToast(getString(R.string.have_not_login));
                } else {
                    goActivity(SignoutActivity.class);
                }




                break;
            case R.id.iv_top_back:
                this.finish();
                break;
            case R.id.tv_contact_us:
                statisticsEventAffair.getInstance().setFlag(this, "3_Evaluation");
                reception();

                break;
            case R.id.tv_relation_us:
                statisticsEventAffair.getInstance().setFlag(this, "3_contact");

                contactUs();
                break;
            //隐私政策
            case R.id.tv_privacy_policy:
                Intent intentPrivacy = new Intent(this, webViewActivity.class);
                intentPrivacy.putExtra("webUrl", BaseConstans.PRIVACYPOLICY);
                startActivity(intentPrivacy);

                break;
            //用户协议
            case R.id.tv_protocol:
                Intent intentProtocol = new Intent(this, webViewActivity.class);
                intentProtocol.putExtra("webUrl", BaseConstans.PROTOCOL);
                startActivity(intentProtocol);
                break;
            case R.id.tv_clear_cache:
                //清除外部cache下的内容
                DataCleanManager.cleanExternalCache();
                //清理内部cache
                DataCleanManager.cleanInternalCache(BaseApplication.getInstance());
                //清理内部sdk
                DataCleanManager.cleanFiles(BaseApplication.getInstance());
                DataCleanManager.cleanExternalFile();
                ToastUtil.showToast("清理成功");
                break;
            case R.id.tv_top_submit:
                //退出
                showDialog();
                break;

            case R.id.tv_test:
                //抠图测试
//                AlbumManager.chooseVideo(this, 1, 1, (tag, paths, isCancel, albumFileList) -> {
//                    VideoMattingModel videoMattingModel=new VideoMattingModel(paths.get(0),AboutActivity.this);
//                    videoMattingModel.addLansongCompoundVideo(paths.get(0));
//                }, "");
                break;
            case R.id.tv_notification_management:
                statisticsEventAffair.getInstance().setFlag(AboutActivity.this, "3_notifications");
                PermissionUtil.gotoPermission(AboutActivity.this);
                break;
            case R.id.tv_version_number:
                statisticsEventAffair.getInstance().setFlag(mContext, "3_update");
                SystemUtil.openMarket(this);
                break;
            default:
                break;


        }

    }



    private void showDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
                //去除黑边
                new ContextThemeWrapper(AboutActivity.this,R.style.Theme_Transparent));
        builder.setTitle(getString(R.string.notification));
        builder.setMessage(
                 "确定退出账号登录吗？");
        builder.setNegativeButton("取消", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.setPositiveButton("确定", (dialog, which) -> {
            BaseConstans.SetUserToken("");
            findViewById(R.id.tv_top_submit).setVisibility(View.GONE);
        });
        builder.setCancelable(true);
        Dialog mDialog = builder.show();
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
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

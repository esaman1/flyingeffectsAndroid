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
import android.view.View;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.utils.ToastUtil;

import butterknife.OnClick;

/**
 * 关于界面
 */
public class AboutActivity extends BaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.act_about;
    }

    @Override
    protected void initView() {
        ((TextView) findViewById(R.id.tv_top_title)).setText("关于");

    }

    @Override
    protected void initAction() {

    }


    @Override
    protected void onResume() {
        super.onResume();
        if(BaseConstans.hasLogin()){
            findViewById(R.id.tv_top_submit).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.tv_top_submit)).setText("退出登录");
            ((TextView) findViewById(R.id.tv_top_submit)).setTextColor(Color.parseColor("#FE2C55"));
        }
    }

    @OnClick({R.id.tv_top_submit,R.id.iv_top_back, R.id.ll_close_account, R.id.ll_contact_us, R.id.ll_relation_us, R.id.ll_privacy_policy, R.id.ll_protocol, R.id.ll_clear_cache})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_close_account:
                if(!BaseConstans.hasLogin()){
                    ToastUtil.showToast(getString(R.string.have_not_login));
                }else{
                    goActivity(SignoutActivity.class);
                }
                break;
            case R.id.iv_top_back:
                this.finish();
                break;
            case R.id.ll_contact_us:
                statisticsEventAffair.getInstance().setFlag(this,"3_Evaluation");
                reception();
                break;
            case R.id.ll_relation_us:
                statisticsEventAffair.getInstance().setFlag(this,"3_contact");

                contactUs();
                break;
            //隐私政策
            case R.id.ll_privacy_policy:
                Intent intentPrivacy = new Intent(this, webViewActivity.class);
                intentPrivacy.putExtra("webUrl", BaseConstans.PRIVACYPOLICY);
                startActivity(intentPrivacy);

                break;
            //用户协议
            case R.id.ll_protocol:
                Intent intentProtocol = new Intent(this, webViewActivity.class);
                intentProtocol.putExtra("webUrl", BaseConstans.PROTOCOL);
                startActivity(intentProtocol);

                break;
            case R.id.ll_clear_cache:

                break;
            case R.id.tv_top_submit:
                //退出
                BaseConstans.SetUserToken("");
                findViewById(R.id.tv_top_submit).setVisibility(View.GONE);
                break;

            default:
                break;


        }

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

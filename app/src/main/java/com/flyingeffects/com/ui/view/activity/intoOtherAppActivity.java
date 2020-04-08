package com.flyingeffects.com.ui.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.utils.ToastUtil;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class intoOtherAppActivity extends Activity {


    ImageView iv_show_bg;
    //1是微信  ，2是快手和抖音
    private int pageType;

    TextView tv_title;

    TextView tv_title_1;

    TextView tv_tencent;

    TextView tv_content;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_into_other_app);
        iv_show_bg = findViewById(R.id.iv_show_bg);
        tv_title=findViewById(R.id.tv_title);
        tv_title_1=findViewById(R.id.tv_title_1);
        tv_tencent=findViewById(R.id.tv_tencent);
        tv_content=findViewById(R.id.tv_content);



        if (BaseConstans.configList != null) {
            pageType = BaseConstans.configList.getType();
            if (pageType == 1) {
                //微信頁面
                iv_show_bg.setImageResource(R.mipmap.into_other_app_wx);
                tv_content.setText(BaseConstans.configList.getDescription());
            } else {
                //抖音和快手頁面
                iv_show_bg.setImageResource(R.mipmap.into_other_app);
                tv_content.setText(BaseConstans.configList.getThirdline());
            }

            tv_title.setText(BaseConstans.configList.getTitle());
            tv_title_1.setText(BaseConstans.configList.getContent());
            tv_tencent.setText(BaseConstans.configList.getCopydata());
        }



        ButterKnife.bind(this);
    }


    @OnClick({R.id.tv_goto_kuaishou, R.id.tv_goto_douyin, R.id.iv_close})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_goto_kuaishou:
                if(pageType==1){
                    contactUs();
                }else{
                    ClipboardManager tvCopy = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    tvCopy.setPrimaryClip(ClipData.newPlainText(null, BaseConstans.configList.getCopydata()));
                    doStartApplicationWithPackageName(this, "com.smile.gifmaker");
                }

                break;

            case R.id.tv_goto_douyin:


                if(pageType==1){
                    contactUs();
                }else{
                    ClipboardManager tvCopy_dy = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    tvCopy_dy.setPrimaryClip(ClipData.newPlainText(null, BaseConstans.configList.getCopydata()));
                    doStartApplicationWithPackageName(this, "com.ss.android.ugc.aweme");
                }
                break;

            case R.id.iv_close:
                this.finish();
                break;
        }
    }


    public void contactUs() {
        ClipboardManager tvCopy = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        tvCopy.setPrimaryClip(ClipData.newPlainText(null, BaseConstans.getService_wxi()));
        openWx();
//        new AlertDialog.Builder(this)
//                .setTitle(R.string.notification)
//                .setMessage(getString(R.string.contacts_saved_to_clipboard) +
//                        BaseConstans.configList.getCopydata() + "\n" +
//                        getString(R.string.promote_message))
//                .setPositiveButton(getString(R.string.confirm), (dialog, which) -> openWx())
//                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
//                .show();
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


    public static void doStartApplicationWithPackageName(Context context, String packagename) {

        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (packageinfo == null) {
            ToastUtil.showToast("你还未安装该应用");
            return;
        }
        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = context.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);

            intent.setComponent(cn);
            context.startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

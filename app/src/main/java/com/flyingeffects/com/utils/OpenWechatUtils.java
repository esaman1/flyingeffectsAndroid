package com.flyingeffects.com.utils;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.manager.AdConfigs;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.ui.view.dialog.CommonMessageDialog;

public class OpenWechatUtils {
    /**
     * 弹出dialog
     */
    public static void showOpenWxDialog(Context context) {
        //复制到剪贴板
        ClipboardManager tvCopy = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        tvCopy.setPrimaryClip(ClipData.newPlainText(null, BaseConstans.getService_wxi()));
        StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "alert_wechat");
        //弹出dialog
        CommonMessageDialog.getBuilder(context)
                .setContentView(R.layout.dialog_common_message)
                .setAdStatus(CommonMessageDialog.AD_STATUS_MIDDLE)
                .setAdId(AdConfigs.AD_IMAGE_WX_DIALOG)
                .setTitle(BaseConstans.configList.getTitle())
                .setMessage(BaseConstans.configList.getContent())
                .setMessage2(BaseConstans.configList.getCopydata())
                .setMessage3(BaseConstans.configList.getDescription())
                .setPositiveButton("立即打开微信获取")
                .setDialogBtnClickListener(new CommonMessageDialog.DialogBtnClickListener() {
                    @Override
                    public void onPositiveBtnClick(CommonMessageDialog dialog) {
                        openWx(context);
                    }

                    @Override
                    public void onCancelBtnClick(CommonMessageDialog dialog) {
                        dialog.dismiss();
                    }
                }).build()
                .show();
    }

    /**
     * 弹出dialog
     */
    public static void showOpenWxDialog(Context context, int adStatus, String title, String content1, String btnText) {
        //复制到剪贴板
        ClipboardManager tvCopy = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        tvCopy.setPrimaryClip(ClipData.newPlainText(null, BaseConstans.getService_wxi()));
        StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "alert_wechat");
        //弹出dialog
        CommonMessageDialog.getBuilder(context)
                .setContentView(R.layout.dialog_common_message)
                .setAdStatus(adStatus)
                .setTitle(title)
                .setMessage(content1)
                .setPositiveButton(btnText)
                .setDialogBtnClickListener(new CommonMessageDialog.DialogBtnClickListener() {
                    @Override
                    public void onPositiveBtnClick(CommonMessageDialog dialog) {
                        openWx(context);
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelBtnClick(CommonMessageDialog dialog) {
                        dialog.dismiss();
                    }

                }).build()
                .show();

    }


    private static void openWx(Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            ToastUtil.showToast(context.getString(R.string.check_login_notification));
        }
    }

}

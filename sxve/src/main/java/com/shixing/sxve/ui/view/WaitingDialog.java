package com.shixing.sxve.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shixing.sxve.R;


/**
 * user :ztj
 * data :2017-8-9
 * Description:等待框
 */
public class WaitingDialog {

    private static Dialog loadingDialog;

    /**
     * 打开Loading
     */
    static Context mContext;

    public static void openPragressDialog(Context context) {
        mContext = context;
        if (loadingDialog != null) {
            WaitingDialog.closeProgressDialog();
        }
        loadingDialog = createLoadingDialog(context, "", true);
        if (loadingDialog != null && context != null) {
            loadingDialog.show();
        }
    }

    public static void openPragressDialog(Context context, String alert) {
        if (loadingDialog != null) {
            WaitingDialog.closeProgressDialog();
        }
        loadingDialog = createLoadingDialog(context, alert, false);
        if (loadingDialog != null) {
            loadingDialog.show();
        }
    }

    private static Dialog createLoadingDialog(Context context, String alert, boolean cancelable) {
        LayoutInflater inflater = LayoutInflater.from(context);
        // 得到加载view
        View v = inflater.inflate(R.layout.waitdialog, null, false);
        if (!TextUtils.isEmpty(alert)) {
            TextView tvAlert = v.findViewById(R.id.tv_show_alert);
            tvAlert.setText(alert);
        }
        RelativeLayout layout = v.findViewById(R.id.loading);
        // 创建自定义样式dialog
        loadingDialog = new Dialog(context, R.style.loading_dialog);
        // 设置布局
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(cancelable);
        return loadingDialog;
    }


    /**
     * 关闭Loading
     */
    public static void closeProgressDialog() {
        Log.d("OOM4", "closeProgressDialog");
        if (loadingDialog != null && loadingDialog.isShowing() && mContext != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }
}

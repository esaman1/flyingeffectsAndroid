package com.shixing.sxve.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
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
    public static void openPragressDialog(Context context
    ) {
        if (loadingDialog != null) {
            WaitingDialog.closePragressDialog();
        }
        loadingDialog = createLoadingDialog(context,"");
        if (loadingDialog != null) {
            loadingDialog.show();
        }
    }

    public static void openPragressDialog(Context context,String alert
    ) {
        if (loadingDialog != null) {
            WaitingDialog.closePragressDialog();
        }
        loadingDialog = createLoadingDialog(context,alert);
        if (loadingDialog != null) {
            loadingDialog.show();
        }
    }

    private static Dialog createLoadingDialog(Context context,String alert) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.waitdialog, null, false);// 得到加载view
        if(!TextUtils.isEmpty(alert)){
            TextView tv_alert=v.findViewById(R.id.tv_show_alert);
            tv_alert.setText(alert);
        }
        RelativeLayout layout =  v.findViewById(R.id.loading);
        loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        loadingDialog.setCanceledOnTouchOutside(false);
        return loadingDialog;
    }






    /**
     * 关闭Loading
     */
    public static void closePragressDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }
}

package com.shixing.sxve.ui.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
public class WaitingDialog_progress {

    private Dialog loadingDialog;
    Context context;

    public WaitingDialog_progress(Context context) {
        this.context = context;
    }

    /**
     * 打开Loading
     */
    public void openProgressDialog(
    ) {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
        loadingDialog = createLoadingDialog(context);
        if (loadingDialog != null) {
            Activity activity = loadingDialog.getOwnerActivity();
            if (activity != null && !activity.isFinishing()) {
                loadingDialog.show();
            }
        }
    }

    private TextView tv_progress;

    private Dialog createLoadingDialog(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.waitdialog, null, false);// 得到加载view
        RelativeLayout layout = v.findViewById(R.id.loading);
        tv_progress = v.findViewById(R.id.tv_show_alert);
        loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);
        return loadingDialog;
    }

    public void setProgress(String progress) {
        if (tv_progress != null) {
            tv_progress.setText(progress);
        }
    }


    /**
     * 关闭Loading
     */
    public void closePragressDialog() {
        try {
            if (context != null) {
                if (tv_progress != null) {
                    tv_progress = null;
                }
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    Activity activity = loadingDialog.getOwnerActivity();
                    if (activity != null && !activity.isFinishing()) {
                        loadingDialog.dismiss();
                        loadingDialog = null;
                    }
                }
            }
        } catch (Exception e) {
            Log.d("OOM", "not attached to window manager");
        }
    }
}

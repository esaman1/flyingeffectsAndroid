package com.shixing.sxve.ui.view;

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
public class WaitingDialogProgressNowAnim {

    private Dialog loadingDialog;
    Context context;

    public WaitingDialogProgressNowAnim(Context context) {
        this.context = context;
    }

    /**
     * 打开Loading
     */
    public void openProgressDialog(
    ) {
        try {
            if (loadingDialog != null) {
                loadingDialog.dismiss();
                loadingDialog = null;
            }
            loadingDialog = createLoadingDialog(context);
            if (loadingDialog != null) {
                loadingDialog.show();
            }
        } catch (Exception e) {
            Log.d("oom", e.getMessage());
        }
    }

    private TextView tv_progress;

    private Dialog createLoadingDialog(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.waitdialog_no_anim, null, false);// 得到加载view
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
        if (tv_progress != null) {
            tv_progress = null;
        }
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

}



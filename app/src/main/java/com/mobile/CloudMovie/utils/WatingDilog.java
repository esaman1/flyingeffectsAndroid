package com.mobile.CloudMovie.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mobile.CloudMovie.R;


/**
 * user :ztj
 * data :2017-8-9
 * Description:等待框
 */
public class WatingDilog {

    private static Dialog loadingDialog;

    /**
     * 打开Loading
     */
    public static void openPragressDialog(Context context
    ) {
        if (loadingDialog != null) {
            WatingDilog.closePragressDialog();
        }
        loadingDialog = createLoadingDialog(context);
        if (loadingDialog != null) {
            loadingDialog.show();
        }
    }

    private static Dialog createLoadingDialog(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.waitdialog, null, false);// 得到加载view
        RelativeLayout layout =  v.findViewById(R.id.loading);
//        ImageView mImg = (ImageView) v.findViewById(R.id.waitdialog_img);
//        AnimationDrawable animationDrawable = (AnimationDrawable) mImg.getBackground();
//        animationDrawable.start();
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

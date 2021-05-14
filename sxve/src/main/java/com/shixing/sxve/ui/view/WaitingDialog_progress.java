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
public class WaitingDialog_progress {

    private Dialog mLoadingDialog;
    Context mContext;

    private TextView mTvProgress;

    public WaitingDialog_progress(Context context) {
        this.mContext = context;
    }

    /**
     * 打开Loading
     */
    public void openProgressDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
        mLoadingDialog = createLoadingDialog(mContext);
        if (mLoadingDialog != null) {
                mLoadingDialog.show();
        }
    }

    /**
     * 打开Loading
     */
    public void openProgressDialog(String title) {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
        mLoadingDialog = createLoadingDialog(mContext,title);
        if (mLoadingDialog != null) {
            mLoadingDialog.show();
        }
    }

    private Dialog createLoadingDialog(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        // 得到加载view
        View v = inflater.inflate(R.layout.waitdialog, null, false);
        RelativeLayout layout = v.findViewById(R.id.loading);
        mTvProgress = v.findViewById(R.id.tv_show_alert);
        // 创建自定义样式dialog
        mLoadingDialog = new Dialog(context, R.style.loading_dialog);
        // 设置布局
        mLoadingDialog.setContentView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.setCancelable(false);
        return mLoadingDialog;
    }

    private Dialog createLoadingDialog(Context context,String title) {
        LayoutInflater inflater = LayoutInflater.from(context);
        // 得到加载view
        View v = inflater.inflate(R.layout.waitdialog, null, false);
        RelativeLayout layout = v.findViewById(R.id.loading);
        mTvProgress = v.findViewById(R.id.tv_show_alert);
        mTvProgress.setText(title);
        // 创建自定义样式dialog
        mLoadingDialog = new Dialog(context, R.style.loading_dialog);
        // 设置布局
        mLoadingDialog.setContentView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.setCancelable(false);
        return mLoadingDialog;
    }

    public void setProgress(String progress) {
        if (mTvProgress != null) {
            mTvProgress.setText(progress);
        }
    }

    /**
     * 关闭Loading
     */
    public void closeProgressDialog() {
        try {
            if (mContext != null) {
                if (mTvProgress != null) {
                    mTvProgress = null;
                }
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                    mLoadingDialog = null;
                }
            }
        } catch (Exception e) {
            Log.d("OOM", "not attached to window manager");
        }
    }
}

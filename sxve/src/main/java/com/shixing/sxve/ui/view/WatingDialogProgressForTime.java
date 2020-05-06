package com.shixing.sxve.ui.view;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shixing.sxve.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * user :ztj
 * data :2017-8-9
 * Description:等待框
 */
public class WatingDialogProgressForTime {

    private Dialog loadingDialog;
    Context context;
    ArrayList<String[]> list = new ArrayList<>();
    public int progress;
    String[] str1 = {"飞闪预览处理中"+progress+"\n" +
            "请耐心等待 不要离开", "飞闪音频添加中"+progress+"\n" +
            "快了，友友稍等片刻", "即将呈现...", "飞闪视频处理中"+progress+"\n" +
            "视频太好看，即将预览", "人数较多...", "飞闪视频合成中"+progress+"\n" +
            "马上就好，不要离开", "视频即将呈现啦"+progress+"\n" +
            "最后合成中，请稍后"};
    private int type = 0; //0  生成预览，1正在保存

    public WatingDialogProgressForTime(Context context) {
        this.context = context;
        list.add(str1);
    }


    public void setProgress(int progress){
        this.progress=progress;
    }

    /**
     * 打开Loading
     */
    public void openProgressDialog(int type
    ) {
        this.type = type;
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
        loadingDialog = createLoadingDialog(context);
        if (loadingDialog != null) {
                loadingDialog.show();
        }
        startTimer();
    }

    private TextView tv_progress;

    private Dialog createLoadingDialog(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.waitdialog_for_time, null, false);// 得到加载view
        RelativeLayout layout = v.findViewById(R.id.loading);
        tv_progress = v.findViewById(R.id.tv_progress);
        loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);
        return loadingDialog;
    }

    public void setProgress(String progress) {
        if(tv_progress!=null){
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
        if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
                loadingDialog = null;
        }
    }


    public boolean isNull() {
        if (loadingDialog != null) {
            return true;
        } else {
            return false;
        }
    }


    private Timer timer;
    private TimerTask task;


    int getTime = 1;

    private void startTimer() {
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        timer = new Timer();
        task = new TimerTask() {
            public void run() {
                getTime++;
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        };
        timer.schedule(task, 0, 1000);
    }


    /**
     * user :TongJu  ; email:jutongzhang@sina.com
     * time：2018/10/15
     * describe:严防内存泄露
     **/
    private void destroyTimer() {
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    switch (getTime) {
                        case 1:
                            setProgress(list.get(type)[0]);
                            break;
                        case 5:
                            setProgress(list.get(type)[1]);
                            break;
                        case 11:
                            setProgress(list.get(type)[2]);
                            break;
                        case 17:
                            setProgress(list.get(type)[3]);
                            break;
                        case 31:
                            setProgress(list.get(type)[4]);
                            break;
                        case 41:
                            setProgress(list.get(type)[5]);
                            break;
                        case 56:
                            setProgress(list.get(type)[6]);
                            break;
                    }
                    break;
            }
        }
    };

}

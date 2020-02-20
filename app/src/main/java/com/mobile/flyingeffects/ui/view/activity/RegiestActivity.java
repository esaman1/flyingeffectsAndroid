package com.mobile.flyingeffects.ui.view.activity;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobile.flyingeffects.R;
import com.mobile.flyingeffects.base.BaseActivity;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

public class RegiestActivity extends BaseActivity {

    @BindView(R.id.tv_email_register)
    TextView tv_email_register;

    @BindView(R.id.tv_user_register)
    TextView tv_user_register;

    @BindView(R.id.ll_email)
    LinearLayout ll_email;

    @BindView(R.id.ed_confirm_email)
    EditText ed_confirm_email;


    @BindView(R.id.iv_verification)
    ImageView iv_verification;


    @BindView(R.id.tv_send_msm)
    TextView tv_send_msm;

    @Override
    protected int getLayoutId() {
        return R.layout.act_register;
    }

    @Override
    protected void initView() {
        findViewById(R.id.iv_top_back).setOnClickListener(this);
    }

    @Override
    protected void initAction() {

    }


    @OnClick({R.id.tv_email_register, R.id.tv_user_register,R.id.tv_send_msm})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_email_register:
                clickWhitch(0);
                ll_email.setVisibility(View.VISIBLE);
                findViewById(R.id.view_line).setVisibility(View.VISIBLE);
                iv_verification.setImageResource(R.mipmap.ic_launcher);
                ed_confirm_email.setHint("请输入您收到的验证码");
                break;

            case R.id.tv_user_register:
                clickWhitch(1);
                iv_verification.setImageResource(R.mipmap.ic_launcher);
                findViewById(R.id.view_line).setVisibility(View.GONE);
                ll_email.setVisibility(View.GONE);
                ed_confirm_email.setHint("请输入您需要注册的用户名");
                break;

            case R.id.tv_send_msm: //发送短信验证码
                if (isCanSendMsg) {
                    requestSendMsg();
                }
                break;
        }
    }


    int[] tv_title = {R.id.tv_email_register, R.id.tv_user_register};

    private void clickWhitch(int whitch) {
        for (int value : tv_title) { //隐藏全部
            ((TextView) findViewById(value)).setTextColor(getResources().getColor(R.color.dark_gray));
        }
        ((TextView) findViewById(tv_title[whitch])).setTextColor(getResources().getColor(R.color.black));
    }



    /**
     * user :TongJu  ;描述：发送短信验证码
     * 时间：2018/5/17
     **/
    private void requestSendMsg() {
//        if (username.getText().toString().equals("")) {
//            ToastUtil.showToast("请输入手机号");
//            return;
//        }
//
//        if (!StringUtil.isPhoneNumber(username.getText().toString())) {
//            ToastUtil.showToast("请输入正确的手机号");
//            return;
//        }
//
//        Map<String, String> params = new HashMap<>();
//        params.put("mobile", username.getText().toString());
//        params.put("type", "1");
//        Observable ob = Api.getDefault().getsendMsg(params);
//        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(LoginActivity.this) {
//            @Override
//            protected void _onError(String message) {
//                ToastUtil.showToast(message);
//            }
//
//            @Override
//            protected void _onNext(Object o) {
//                ToastUtil.showToast("发送成功");
//                startTimer();
//            }
//        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, true, true, true);

        startTimer();

    }

    private boolean isCanSendMsg = true;

    private Timer timer;
    private TimerTask task;
    private int total_Time = 60;

    /***
     * 倒计时60s
     */
    private void startTimer() {
        isCanSendMsg = false;
//        tv_send_msm.setBackground(getResources().getDrawable(R.drawable.strok_blue_full));
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
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        };
        timer.schedule(task, 0, 1000);
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    total_Time = total_Time - 1;
//                    tv_send_msm.setText((String.format(getResources().getString(R.string.remainTime),total_Time)));
                    tv_send_msm.setText("剩余"+total_Time+"s");
//                    tv_send_msm.setText(total_Time + "s 后重试");
                    if (total_Time == 0) {
                        total_Time = 60;
                        endTimer();
                    }
                    break;
            }
        }
    };


    /**
     * 关闭timer 和task
     */
    private void endTimer() {
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        tv_send_msm.setText("获取短信验证码");
        isCanSendMsg = true;
        tv_send_msm.setEnabled(true);
//        tv_send_msm.setBackground(getResources().getDrawable(R.drawable.strok_all_dark_full_normal));
    }


}

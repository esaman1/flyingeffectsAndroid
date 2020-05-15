package com.flyingeffects.com.http;


import android.content.Context;

import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.utils.LogUtil;
import com.shixing.sxve.ui.view.WaitingDialog;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import rx.Subscriber;

/**
 * Created by zhangtongju
 * on 2016/10/10 15:49.
 */

public abstract class ProgressSubscriber<T> extends Subscriber<T> implements ProgressCancelListener {

    private Context context;

    public ProgressSubscriber(Context context) {
        this.context = context;
    }

    @Override
    public void onCompleted() {
        if(isShowDialog){
            dismissProgressDialog();
        }
    }


    private boolean isShowDialog;
    /**
     * 显示Dialog
     */
    public void showProgressDialog( boolean isShowDialog) {
        this.isShowDialog=isShowDialog;
        WaitingDialog.openPragressDialog(context);
    }

    @Override
    public void onNext(T t) {
        _onNext(t);
    }

    /**
     * 隐藏Dialog
     */
    private void dismissProgressDialog() {
        WaitingDialog.closePragressDialog();
    }

    @Override
    public void onError(Throwable e) {
        LogUtil.d("ProgressSubscriber", e.getMessage());
        e.printStackTrace();
        if (e instanceof UnknownHostException) { //一般为未开网路的情况
            _onError("网络不可用");
        } else if (e instanceof ApiException) { //自己写的业务判断
            _onError(e.getMessage());
        } else if (e instanceof SocketTimeoutException) {
            _onError("网络连接超时");
        } else {
            if (BaseConstans.PRODUCTION) {
                LogUtil.d("error", e.getMessage());
            } else {
                _onError(e.getMessage());
            }
        }
        dismissProgressDialog();
    }


    @Override
    public void onCancelProgress() {
        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }
    }

    protected abstract void _onNext(T t);

    protected abstract void _onError(String message);
}

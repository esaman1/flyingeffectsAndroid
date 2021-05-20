package com.flyingeffects.com.ui.model;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.entity.HttpResult;
import com.flyingeffects.com.entity.PayEntity;
import com.flyingeffects.com.entity.PriceListEntity;
import com.flyingeffects.com.entity.UserInfo;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.ui.interfaces.contract.BuyVipContract;
import com.flyingeffects.com.ui.presenter.BuyVipPresenter;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.kwad.sdk.live.mode.LiveInfo;
import com.orhanobut.hawk.Hawk;
import com.xj.anchortask.library.log.LogUtils;

import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;

public class BuyVipModel implements BuyVipContract.BuyVipMvpModel {

    private static final String TAG = "BuyVipModel";
    private final BuyVipPresenter mPresenter;

    public BuyVipModel(BuyVipPresenter presenter){
        mPresenter = presenter;
    }

    @Override
    public void requestCostList() {
        HashMap<String, String> params = new HashMap<>();
        // 启动时间
        Observable<HttpResult<List<PriceListEntity>>> ob =
                Api.getDefault().getVipPriceList(BaseConstans.getRequestHead(params));

        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<PriceListEntity>>() {

            @Override
            protected void onSubError(String message) {
                LogUtils.e(TAG, message);
            }

            @Override
            protected void onSubNext(List<PriceListEntity> data) {
                String str = StringUtil.beanToJSONString(data);
                LogUtils.d(TAG, "requestCostList = " + str);
                mPresenter.returnPriceList(data);
            }

        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, PublishSubject.create(), false, true, false);
    }

    @Override
    public void requestPay(String goodsId,String tradeType) {
        HashMap<String, String> params = new HashMap<>(BaseConstans.MAP_DEFAULT_INITIAL_CAPACITY);
        params.put("token", BaseConstans.getUserId());
        params.put("goods_id", goodsId);
        params.put("trade_type", tradeType);

        // 启动时间
        Observable<HttpResult<PayEntity>> ob = Api.getDefault().addOrder(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<PayEntity>() {
            @Override
            protected void onSubError(String message) {
                LogUtils.e(TAG, message);
                ToastUtil.showToast(TAG + ": pay error " + message);
            }

            @Override
            protected void onSubNext(PayEntity data) {
                String str = StringUtil.beanToJSONString(data);
                LogUtils.d(TAG, "pay = " + str);
                mPresenter.toPay(tradeType,data);
            }

        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, PublishSubject.create(), false, true, false);
    }

    @Override
    public void requestUserInfo() {
        HashMap<String, String> params = new HashMap<>();
        params.put("to_user_id", BaseConstans.getUserId());
        // 启动时间
        Observable ob = Api.getDefault().getOtherUserinfo(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<UserInfo>() {
            @Override
            protected void onSubError(String message) {
                LogUtil.e(TAG,message);
            }

            @Override
            protected void onSubNext(UserInfo data) {
                Hawk.put(UserInfo.USER_INFO_KEY, data);
                mPresenter.returnUserInfo(data);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, PublishSubject.create(), false, true, false);

    }
}

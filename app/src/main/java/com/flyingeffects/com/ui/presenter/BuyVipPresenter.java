package com.flyingeffects.com.ui.presenter;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.PrivilegeListAdapter;
import com.flyingeffects.com.entity.PayEntity;
import com.flyingeffects.com.entity.PriceListEntity;
import com.flyingeffects.com.entity.PrivilegeEntity;
import com.flyingeffects.com.entity.UserInfo;
import com.flyingeffects.com.ui.interfaces.contract.BuyVipContract;
import com.flyingeffects.com.ui.model.BuyVipModel;
import com.flyingeffects.com.utils.CheckVipOrAdUtils;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

public class BuyVipPresenter extends BuyVipContract.BuyVipPresenter implements LifecycleObserver {
    private static final String TAG = "BuyVipPresenter";

    public static final int TRADE_TYPE_ALIPAY = 1;
    public static final int TRADE_TYPE_WECHAT = 2;

    private static final String[] PRIVILEGE_NAME = {"解锁付费模板", "无弹窗广告", "专属客服", "模板每日更新",
            "高清视频导出", "尊贵身份标识", "VIP专属模板"};

    private static final int[] PRIVILEGE_ICON = {R.mipmap.ic_vip_pri_unlock, R.mipmap.ic_vip_pri_no_ad
            , R.mipmap.ic_vip_pri_service, R.mipmap.ic_vip_pri_update, R.mipmap.ic_vip_pri_video
            , R.mipmap.ic_vip_pri, R.mipmap.ic_vip_pri_temp};

    private BuyVipContract.BuyVipMvpModel mBuyVipMvpModel;
    private List<PriceListEntity> mPriceList;
    private List<PrivilegeEntity> mPrivilegeList;
    private PrivilegeListAdapter mPrivilegeListAdapter;


    private int mCheckedPriceId;
    private int mTradeType;

    public BuyVipPresenter() {
        mBuyVipMvpModel = new BuyVipModel(this);
    }

    @Override
    public void requestPriceList() {
        if (mPriceList != null) {
            returnPriceList(mPriceList);
        } else {
            mBuyVipMvpModel.requestCostList();
        }
    }

    @Override
    public void returnPriceList(List<PriceListEntity> data) {
        mPriceList = data;
        if (isViewAttached()) {
            getView().updateCostList(data);
        }
    }


    /**
     * 调起付款应用
     *
     * @param tradeType
     * @param data      订单信息
     */
    @Override
    public void toPay(String tradeType, PayEntity data) {
        int type = Integer.parseInt(tradeType);
        PayEntity.Pay_data payData = data.getPay_data();
        if (isViewAttached()){
            if (type == TRADE_TYPE_WECHAT) {
                getView().startWeChatPay(payData);
            } else if (type == TRADE_TYPE_ALIPAY) {
                String orderInfo = data.getPay_url();
                getView().startAlipay(orderInfo);
            }
        }
    }

    @Override
    public PrivilegeListAdapter getPrivilegeAdapter() {
        mPrivilegeList = new ArrayList<>();
        for (int i = 0; i < PRIVILEGE_ICON.length; i++) {
            PrivilegeEntity entity = new PrivilegeEntity();
            entity.setName(PRIVILEGE_NAME[i]);
            entity.setResId(PRIVILEGE_ICON[i]);
            mPrivilegeList.add(entity);
        }
        mPrivilegeListAdapter = new PrivilegeListAdapter(mPrivilegeList);
        return mPrivilegeListAdapter;
    }

    @Override
    public void getUserInfo() {
        UserInfo userInfo = Hawk.get("UserInfo");
        if (userInfo != null) {
            if (isViewAttached()){
                getView().updateUserInfo(userInfo);
            }
        } else {
            mBuyVipMvpModel.requestUserInfo();
        }
    }

    @Override
    public void returnUserInfo(UserInfo data) {
        if (isViewAttached()){
            getView().updateUserInfo(data);
        }
    }

    @Override
    public void changeCheckedItem(List<PriceListEntity> data, int position) {
        for (int i = 0; i < data.size(); i++) {
            data.get(i).setChecked(false);
        }
        PriceListEntity priceListEntity = data.get(position);
        priceListEntity.setChecked(true);
        mCheckedPriceId = priceListEntity.getId();
        if (isViewAttached()){
            getView().updateOpenBtnText(priceListEntity.getPrice());
        }
    }

    @Override
    public void changeRadioChecked(int checkedId) {
        if (checkedId == R.id.rb_alipay) {
            mTradeType = TRADE_TYPE_ALIPAY;
        } else {
            mTradeType = TRADE_TYPE_WECHAT;
        }
    }

    @Override
    public void createOrder() {
        mBuyVipMvpModel.requestPay(mCheckedPriceId + "", mTradeType + "");
    }


    @Override
    public String getVipGradeText(int isVip, int vipGrade) {
        String vipGradeStr = "";
        if (isVip == CheckVipOrAdUtils.IS_VIP) {
            switch (vipGrade) {
                case CheckVipOrAdUtils.VIP_GRADE_MONTH:
                    vipGradeStr = "包月会员";
                    break;
                case CheckVipOrAdUtils.VIP_GRADE_YEAR:
                    vipGradeStr = "包年会员";
                    break;
                case CheckVipOrAdUtils.VIP_GRADE_FOREVER:
                    vipGradeStr = "永久会员";
                    if (isViewAttached()){
                        getView().hideBuyVipUi();
                    }
                    break;
                default:
                    vipGradeStr = "永久会员";
                    break;
            }
        } else {
            vipGradeStr = "暂未开通";
        }
        return vipGradeStr;
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        detachView();
    }
}

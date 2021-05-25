package com.flyingeffects.com.ui.interfaces.contract;

import com.flyingeffects.com.adapter.PrivilegeListAdapter;
import com.flyingeffects.com.base.mvpBase.BaseModel;
import com.flyingeffects.com.base.mvpBase.BasePresenter;
import com.flyingeffects.com.base.mvpBase.BaseView;
import com.flyingeffects.com.entity.PayEntity;
import com.flyingeffects.com.entity.PriceListEntity;
import com.flyingeffects.com.entity.UserInfo;

import java.util.List;

/**
 * @author vidya
 */
public interface BuyVipContract {

    abstract class BuyVipPresenter extends BasePresenter<BuyVipMvpView, BuyVipMvpModel> {

        public abstract void requestPriceList();

        public abstract void returnPriceList(List<PriceListEntity> data);

        public abstract void toPay(String tradeType, PayEntity data);

        public abstract PrivilegeListAdapter getPrivilegeAdapter();

        public abstract void getUserInfo();

        public abstract void returnUserInfo(UserInfo data);

        public abstract void changeCheckedItem(List<PriceListEntity> data, int position);

        public abstract void changeRadioChecked(int checkedId);

        public abstract void createOrder();

        public abstract String getVipGradeText(int is_vip, int vip_grade);

        public abstract void refreshUserInfo();
    }

    interface BuyVipMvpView extends BaseView {

        /**
         * 更新价格列表的数据
         */
        void updateCostList(List<PriceListEntity> data);

        /**
         * 更新用户信息
         * @param data
         */
        void updateUserInfo(UserInfo data);

        void updateOpenBtnText(String price);

        void startAlipay(String orderInfo);

        void startWeChatPay(PayEntity.Pay_data payData);

        void hideBuyVipUi();

    }

    interface BuyVipMvpModel extends BaseModel {
        /**
         * 请求价格列表
         */
        void requestCostList();

        void requestPay(String goodsId,String tradeType);

        void requestUserInfo();
    }

}

package com.flyingeffects.com.ui.view.activity;

import android.content.Context;
import android.view.View;
import android.widget.RadioGroup;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.PriceListAdapter;
import com.flyingeffects.com.adapter.PrivilegeListAdapter;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.databinding.ActivityBuyVipBinding;
import com.flyingeffects.com.databinding.ViewCommonTitleBinding;
import com.flyingeffects.com.entity.PayEntity;
import com.flyingeffects.com.entity.PriceListEntity;
import com.flyingeffects.com.entity.UserInfo;
import com.flyingeffects.com.ui.interfaces.contract.BuyVipContract;
import com.flyingeffects.com.ui.presenter.BuyVipPresenter;
import com.flyingeffects.com.ui.view.dialog.CommonMessageDialog;
import com.flyingeffects.com.utils.CheckVipOrAdUtils;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.OpenWechatUtils;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.view.decoration.PriceListDecoration;
import com.sweet.paylib.alipay.AliPayManager;
import com.sweet.paylib.wechat.WechatPay;
import com.sweet.paylib.wechat.WechatPayTools;
import com.xj.anchortask.library.log.LogUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class BuyVipActivity extends BaseActivity implements BuyVipContract.BuyVipMvpView {
    private static final String TAG = "BuyVipActivity";

    public static final int ALI_PAY_SUCCESS = 9000;
    public static final int ALI_PAY_CANCEL = 6001;

    private ActivityBuyVipBinding mBinding;
    private ViewCommonTitleBinding mTopBinding;
    private BuyVipPresenter mPresenter;
    private Context mContext;
    private PriceListAdapter mPriceListAdapter;


    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initView() {
        mContext = BuyVipActivity.this;
        mBinding = ActivityBuyVipBinding.inflate(getLayoutInflater());
        View rootView = mBinding.getRoot();
        mTopBinding = ViewCommonTitleBinding.bind(rootView);
        setContentView(rootView);

        mPresenter = new BuyVipPresenter(mContext);
        getLifecycle().addObserver(mPresenter);
        mPresenter.attachView(this);

        setOnClickListener();

        setOnCheckListener();

        initVipPrivilegeList();

        initPriceListView();
    }

    private void setOnCheckListener() {
        mBinding.radioGroup.check(R.id.rb_wx);
        mPresenter.changeRadioChecked(R.id.rb_wx);
        mBinding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mPresenter.changeRadioChecked(checkedId);
            }
        });
    }

    private void initPriceListView() {
        mPriceListAdapter = new PriceListAdapter(new ArrayList<>());
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mBinding.rvCostList.setLayoutManager(linearLayoutManager);
        mBinding.rvCostList.addItemDecoration(new PriceListDecoration());
        mBinding.rvCostList.setAdapter(mPriceListAdapter);
    }

    @Override
    protected void initAction() {
        mPresenter.getUserInfo();
        mPresenter.requestPriceList();
    }


    private void initVipPrivilegeList() {
        PrivilegeListAdapter adapter = mPresenter.getPrivilegeAdapter();
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mBinding.rvVipPrivilege.setLayoutManager(linearLayoutManager);
        mBinding.rvVipPrivilege.setAdapter(adapter);
    }

    @Override
    public void updateUserInfo(UserInfo userInfo) {
        if (userInfo != null) {
            String str = StringUtil.beanToJSONString(userInfo);
            LogUtils.d(TAG, "userInfo = " + str);
            //头像
            Glide.with(mContext)
                    .load(userInfo.getPhotourl())
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(mBinding.ivAvatar);
            mBinding.tvUserName.setText(userInfo.getNickname());
            mBinding.tvUserId.setText(MessageFormat.format("飞友号：{0}", userInfo.getId()));
            setVipGradeText(userInfo.getIs_vip(), userInfo.getVip_grade());
        }
    }

    private void setVipGradeText(int isVip, int vipGrade) {
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
                    break;
                default:
                    vipGradeStr = "永久会员";
                    break;
            }
        } else {
            vipGradeStr = "暂未开通";
        }
        mBinding.tvIsOpen.setText(vipGradeStr);
    }

    @Override
    public void updateOpenBtnText(String price) {
        mBinding.tvOpenVip.setText(MessageFormat.format("立即以{0}元开通", price));
    }

    @Override
    public void startAlipay(String orderInfo) {
        AliPayManager.aliPay(BuyVipActivity.this, orderInfo, (code, msg) -> {
            LogUtils.d(TAG, "code = " + code);
            if (code == ALI_PAY_SUCCESS) {
                ToastUtil.showToast(msg);
                finish();
                //mPresenter.requestMakeSurePay();
            } else if (code == ALI_PAY_CANCEL) {
                ToastUtil.showToast(getString(R.string.cancel_pay));
            } else {
                ToastUtil.showToast(msg);
            }
        });
    }

    @Override
    public void startWeChatPay(PayEntity.Pay_data payData) {
        WechatPayTools.wechatPayApp(mContext, payData.getAppid(), payData.getNoncestr()
                , payData.getPartnerid(), payData.getPrepayid(), payData.getTimestamp(),
                payData.getSign(), (code, msg) -> {
                    LogUtil.d(TAG, "code = " + code + " msg = " + msg);
                    if (code == WechatPay.SUCCESS_PAY) {
                        ToastUtil.showToast(msg);
                        finish();
                    } else if (code == WechatPay.CANCEL_PAY) {
                        ToastUtil.showToast(getString(R.string.cancel_pay));
                    } else {
                        ToastUtil.showToast(msg);
                    }
                });
    }

    private void setOnClickListener() {
        mTopBinding.ivBack.setOnClickListener(this::onViewClicked);
        mBinding.tvOpenVip.setOnClickListener(this::onViewClicked);
        mBinding.tvProblem.setOnClickListener(this::onViewClicked);
    }

    private void onViewClicked(View view) {
        if (view == mTopBinding.ivBack) {
            finish();
        } else if (view == mBinding.tvOpenVip) {
            mPresenter.createOrder();
        }else if (view == mBinding.tvProblem){
            showProblemDialog();
        }
    }

    private void showProblemDialog() {
        OpenWechatUtils.showOpenWxDialog(mContext,CommonMessageDialog.AD_STATUS_NONE
        ,"飞闪提示","友友您好，已经为您复制微信号，留言说明问题","打开微信");
    }


    @Override
    public void updateCostList(List<PriceListEntity> data) {
        mPresenter.changeCheckedItem(data, 0);
        mPriceListAdapter.replaceData(data);
        mPriceListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mPresenter.changeCheckedItem(data, position);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showToast(String msg) {

    }

    @Override
    public void showError() {

    }


}

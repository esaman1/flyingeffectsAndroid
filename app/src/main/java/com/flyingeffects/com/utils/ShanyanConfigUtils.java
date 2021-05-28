package com.flyingeffects.com.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.chuanglan.shanyan_sdk.tool.ShanYanUIConfig;
import com.flyingeffects.com.R;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.entity.WxLogin;

import de.greenrobot.event.EventBus;


public class ShanyanConfigUtils {


    //    //沉浸式竖屏样式
    public static ShanYanUIConfig getCJSConfig(final Context context) {
        /************************************************自定义控件**************************************************************/
        Drawable logBtnImgPath = context.getResources().getDrawable(R.drawable.login_weixin);
        Drawable backgruond = context.getResources().getDrawable(R.drawable.shanyan_demo_auth_no_bg);
        Drawable returnBg = context.getResources().getDrawable(R.mipmap.close_login);
        //loading自定义加载框
//        LayoutInflater inflater = LayoutInflater.from(context);
//        RelativeLayout view_dialog = (RelativeLayout) inflater.inflate(R.layout.shanyan_demo_dialog_layout, null);
//        RelativeLayout.LayoutParams mLayoutParams3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//        view_dialog.setLayoutParams(mLayoutParams3);
//        view_dialog.setVisibility(View.GONE);

//        //号码栏背景
//        LayoutInflater numberinflater = LayoutInflater.from(context);
//        RelativeLayout numberLayout = (RelativeLayout) numberinflater.inflate(R.layout.shanyan_demo_phobackground, null);
//        RelativeLayout.LayoutParams numberParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        numberParams.setMargins(0, 0, 0, AbScreenUtils.dp2px(context, 250));
//        numberParams.width = AbScreenUtils.getScreenWidth(context, false) - AbScreenUtils.dp2px(context, 50);
//        numberParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
//        numberParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        numberLayout.setLayoutParams(numberParams);

        LayoutInflater inflater1 = LayoutInflater.from(context);
        RelativeLayout relativeLayout = (RelativeLayout) inflater1.inflate(R.layout.view_wx_login, null);
        RelativeLayout.LayoutParams layoutParamsOther = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsOther.setMargins(0, 0, 0, AbScreenUtils.dp2px(context, 250));
        layoutParamsOther.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParamsOther.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        relativeLayout.setLayoutParams(layoutParamsOther);
        RelativeLayout relative_wx_login=relativeLayout.findViewById(R.id.relative_wx_login);
        relative_wx_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new WxLogin("wxLogin"));
            }
        });

//        otherLogin(context, relativeLayout.findViewById(R.id.relative_wx_login));
        ShanYanUIConfig uiConfig = new ShanYanUIConfig.Builder()
                .setActivityTranslateAnim("shanyan_demo_fade_in_anim", "shanyan_dmeo_fade_out_anim")
                //授权页导航栏：
                .setNavColor(Color.parseColor("#ffffff"))  //设置导航栏颜色
                .setNavText("")  //设置导航栏标题文字
                .setNavReturnBtnWidth(24)
                .setNavReturnBtnHeight(24)
                .setAuthBGImgPath(backgruond)
                .setLogoHidden(true)   //是否隐藏logo
                .setDialogDimAmount(0f)
                .setNavReturnImgPath(returnBg)
                .setFullScreen(true)
                .setNavReturnBtnOffsetX(24)
                .setNavReturnBtnOffsetY(24)
                .setStatusBarHidden(true)
                .setLogBtnOffsetY(100)
//                .setAuthNavTransparent(true)
//                .setAuthNavHidden(true)


                //授权页号码栏：
                .setNumberColor(Color.parseColor("#ffffff"))  //设置手机号码字体颜色
                .setNumFieldOffsetBottomY(250)    //设置号码栏相对于标题栏下边缘y偏移
                .setNumberSize(15)
                .setNumFieldHeight(50)
                .setNumFieldOffsetX(-155)


                //授权页登录按钮：
                .setLogBtnText("本机号码一键登录")  //设置登录按钮文字
                .setLogBtnTextColor(Color.parseColor("#333333"))   //设置登录按钮文字颜色
                .setLogBtnImgPath(logBtnImgPath)   //设置登录按钮图片
                .setLogBtnTextSize(15)
                .setLogBtnHeight(45)
                .setLogBtnOffsetBottomY(180)
                .setLogBtnWidth(AbScreenUtils.getScreenWidth(context, true) - 50)

                //授权页隐私栏：
                .setAppPrivacyOne("用户协议", BaseConstans.PROTOCOL)  //设置开发者隐私条款1名称和URL(名称，url)
                .setAppPrivacyTwo("隐私政策", BaseConstans.PRIVACYPOLICY)  //设置开发者隐私条款2名称和URL(名称，url)
                .setAppPrivacyColor(Color.parseColor("#ffffff"), Color.parseColor("#60C4FC"))    //	设置隐私条款名称颜色(基础文字颜色，协议文字颜色)
                .setPrivacyText("同意", "和", "、", "、", "并授权手机号")
                .setPrivacyOffsetBottomY(20)//设置隐私条款相对于屏幕下边缘y偏
                .setPrivacyState(false)
                .setPrivacyTextSize(10)
                .setPrivacyOffsetX(26)
                .setSloganHidden(true)
                .setShanYanSloganHidden(true)
                .setShanYanSloganTextColor(Color.parseColor("#ffffff"))
//                .addCustomView(numberLayout, false, false, null)

                .setLoadingView(null)
                // 添加自定义控件:
                .addCustomView(relativeLayout, false, false, null)
                //标题栏下划线，可以不写
                .build();
        return uiConfig;


    }


    private static void otherLogin(final Context context, RelativeLayout relativeLayout) {
        RelativeLayout weixin = relativeLayout.findViewById(R.id.relative_wx);
        weixin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new WxLogin("wxLogin"));
            }
        });
    }
}

package com.flyingeffects.com.ui.view.activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.widget.CheckBox;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;

import java.util.HashMap;

import butterknife.BindView;
import rx.Observable;

/**
 * description ：注销账号页面
 * date: ：2019/11/12 16:57
 * author: 张同举 @邮箱 jutongzhang@sina.com
 */
public class SignoutActivity extends BaseActivity {


    private boolean isAgreeProtocol = false;

    @BindView(R.id.check_box)
    CheckBox check_box;


    @BindView(R.id.bt_write_off)
    TextView bt_write_off;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_signout;
    }

    @Override
    protected void initView() {
        ((TextView) findViewById(R.id.tv_top_title)).setText(getString(R.string.string_cancel_user));
        findViewById(R.id.iv_top_back).setOnClickListener(this);
        Drawable drawable_news = getResources().getDrawable(R.drawable.signout_button_confirm);
        //当这个图片被绘制时，给他绑定一个矩形 ltrb规定这个矩形
        int radio_size = StringUtil.dip2px(this, 16);
        drawable_news.setBounds(0, 0, radio_size, radio_size);
        check_box.setCompoundDrawables(drawable_news, null, null, null);
    }

    @Override
    protected void initAction() {
        check_box.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                bt_write_off.setBackground(getResources().getDrawable(R.drawable.stroke_yellow_full_oval));
                bt_write_off.setTextColor(Color.BLACK);
            } else {
                bt_write_off.setBackground(getResources().getDrawable(R.drawable.button_signout));
                bt_write_off.setTextColor(Color.WHITE);
            }
            isAgreeProtocol = b;
        });


        bt_write_off.setOnClickListener(view -> {
            if (isAgreeProtocol) {
                requestWrite_off();
            } else {
                ToastUtil.showToast(getString(R.string.string_to_delete_alert));
            }
        });
    }


    private void requestWrite_off() {
        HashMap<String, String> params = new HashMap<>();
        Observable ob = Api.getDefault().toDelete(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(SignoutActivity.this) {
            @Override
            protected void onSubError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void onSubNext(Object data) {
                BaseConstans.setUserToken("");
                SignoutActivity.this.finish();
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);
    }


}

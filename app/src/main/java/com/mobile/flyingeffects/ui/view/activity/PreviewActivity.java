package com.mobile.flyingeffects.ui.view.activity;

import android.view.View;
import android.widget.ImageView;

import com.mobile.flyingeffects.R;
import com.mobile.flyingeffects.base.BaseActivity;
import com.mobile.flyingeffects.view.EmptyControlVideo;

import butterknife.BindView;
import butterknife.OnClick;


/***
 * 预览视频界面
 */
public class PreviewActivity extends BaseActivity {

    @BindView(R.id.video_player)
    EmptyControlVideo video_player;


    @BindView(R.id.iv_zan)
    ImageView iv_zan;

    @Override
    protected int getLayoutId() {
        return R.layout.act_preview;
    }

    @Override
    protected void initView() {
        String url = "https://res.exexm.com/cw_145225549855002";
        video_player.setUp(url, true, "");
        //过渡动画
    }


    @Override
    protected void initAction() {

    }


    @OnClick({R.id.iv_zan})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_zan:
                iv_zan.setBackgroundResource(R.mipmap.zan_selected);
                break;

            default:


                break;
        }


    }


}

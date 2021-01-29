package com.flyingeffects.com.ui.view.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.horizontalselectedviewlibrary.HorizontalselectedView;
import com.faceunity.FURenderer;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.FUBaseActivity;
import com.flyingeffects.com.enity.CutSuccess;
import com.flyingeffects.com.ui.interfaces.view.FUBeautyMvpView;
import com.flyingeffects.com.ui.presenter.FUBeautyMvpPresenter;
import com.flyingeffects.com.utils.LogUtil;

import de.greenrobot.event.Subscribe;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


/**
 * description ：美颜界面
 * creation date: 2021/1/27
 * user : zhangtongju
 */
public class FUBeautyActivity extends FUBaseActivity implements FUBeautyMvpView {

    private FUBeautyMvpPresenter presenter;
    private HorizontalselectedView horizontalselectedView;
    private LottieAnimationView lottieAnimationView;
    private TextView tv_count_down;
    private ImageView iv_count_down;


    @Override
    protected void onCreate() {
        horizontalselectedView = findViewById(R.id.horizontalselectedView);
        presenter = new FUBeautyMvpPresenter(this, this, horizontalselectedView);
        findViewById(R.id.ll_stage_property).setVisibility(View.INVISIBLE);
        findViewById(R.id.ll_album).setVisibility(View.INVISIBLE);
        findViewById(R.id.relative_choose_music).setOnClickListener(listener);
        findViewById(R.id.iv_close).setOnClickListener(listener);
        iv_count_down = findViewById(R.id.iv_count_down);
        iv_count_down.setOnClickListener(listener);
        tv_count_down = findViewById(R.id.tv_count_down_right);
        lottieAnimationView = findViewById(R.id.animation_view);
        lottieAnimationView.setOnClickListener(listener);
    }

    @Override
    protected FURenderer initFURenderer() {
        return new FURenderer
                .Builder(this)
                .maxFaces(4)
                .inputImageOrientation(mFrontCameraOrientation)
                .inputTextureType(FURenderer.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE)
                .setOnFUDebugListener(this)
                .setOnTrackingStatusChangedListener(this)
                .build();
    }

    @Override
    public void onCameraChanged(int cameraFacing, int cameraOrientation) {

    }


    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.relative_choose_music:
                    presenter.IntoChooseMusic();
                    break;

                case R.id.iv_close:
                    finish();
                    break;


                case R.id.animation_view:
                    presenter.StartCountDown();
                    break;


                case R.id.iv_count_down:
                    //倒计时功能
                    presenter.clickCountDown(iv_count_down);
                    break;

                default:

                    break;


            }
        }
    };


    /**
     * description ：选择音乐的回调
     * creation date: 2021/1/28
     * user : zhangtongju
     */
    @Subscribe
    public void onEventMainThread(CutSuccess cutSuccess) {
        String nowChooseBjPath = cutSuccess.getFilePath();
        LogUtil.d("OOM", "nowChooseBjPath=" + nowChooseBjPath);
        presenter.SetNowChooseMusic(nowChooseBjPath);
    }


    /**
     * description ：显示倒计时功能
     * creation date: 2021/1/29
     * user : zhangtongju
     */
    @Override
    public void showCountDown(int num) {
        Observable.just(num).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                if (num != 0) {
                    LogUtil.d("OOM", "返回的num=" + num);
                    tv_count_down.setText(num + "");
                } else {
                    LogUtil.d("OOM", "隐藏text");
                    tv_count_down.setVisibility(View.GONE);
                }
            }
        });

    }
}

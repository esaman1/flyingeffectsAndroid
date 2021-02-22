package com.flyingeffects.com.ui.view.activity;

import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.example.horizontalselectedviewlibrary.HorizontalselectedView;
import com.faceunity.FURenderer;
import com.faceunity.entity.Effect;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.FUBaseActivity;
import com.flyingeffects.com.enity.CutSuccess;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.ui.interfaces.view.FUBeautyMvpView;
import com.flyingeffects.com.ui.presenter.FUBeautyMvpPresenter;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.view.MarqueTextView;

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
    private LottieAnimationView animation_view_progress;
    private TextView tv_count_down;
    private ImageView iv_count_down;
    private MarqueTextView tv_chooseMusic;
    private boolean isRecording = false;
    private LinearLayout ll_stage_property;
    private ConstraintLayout constraintLayout;
    /**
     * 来自哪个界面  0  默认为主页点击+号页面   1 默认为跟随相机拍摄页面
     */
    private int isFrom;
    private RelativeLayout relative_click;
    private TextView tv_show_shoot_time;



    @Override
    protected void onCreate() {
        fuBeautyActivity=this;
        isFrom = getIntent().getIntExtra("isFrom", 0);
        long duration=getIntent().getLongExtra("duration",0);
        String musicPath=getIntent().getStringExtra("musicPath");
        String title=getIntent().getStringExtra("title");
        String videoBjPath= getIntent().getStringExtra("videoPath");
        int defaultnum=getIntent().getIntExtra("defaultnum",0);
        String TemplateFilePath=getIntent().getStringExtra("TemplateFilePath");
        String OldfromTo=getIntent().getStringExtra("OldfromTo");
        new_fag_template_item templateItem= (new_fag_template_item) getIntent().getSerializableExtra("templateItem");
        horizontalselectedView = findViewById(R.id.horizontalselectedView);
        presenter = new FUBeautyMvpPresenter(this, this, horizontalselectedView,isFrom,duration,musicPath,templateItem,TemplateFilePath,OldfromTo,defaultnum,videoBjPath);
        constraintLayout = findViewById(R.id.constraintLayout);
        findViewById(R.id.ll_album).setVisibility(View.INVISIBLE);
        findViewById(R.id.relative_choose_music).setOnClickListener(listener);
        findViewById(R.id.iv_close).setOnClickListener(listener);
        tv_chooseMusic = findViewById(R.id.tv_chooseMusic);
        relative_click=findViewById(R.id.relative_click);
        tv_show_shoot_time=findViewById(R.id.tv_show_shoot_time);
        iv_count_down = findViewById(R.id.iv_count_down);
        iv_count_down.setOnClickListener(listener);
        tv_count_down = findViewById(R.id.tv_count_down_right);
        lottieAnimationView = findViewById(R.id.animation_view);
        animation_view_progress = findViewById(R.id.animation_view_progress);
        lottieAnimationView.setOnClickListener(listener);
        animation_view_progress.setOnClickListener(listener);
        ImageView iv_rolling_over = findViewById(R.id.iv_rolling_over);
        iv_rolling_over.setOnClickListener(listener);
        ll_stage_property = findViewById(R.id.ll_stage_property);
        ll_stage_property.setOnClickListener(listener);
        if (isFrom == 1) {
            horizontalselectedView.setVisibility(View.GONE);
            ll_stage_property.setVisibility(View.INVISIBLE);
            tv_chooseMusic.setText(title);
            SetNowChooseMusic(musicPath, musicPath);
            presenter.SetNowChooseMusic(musicPath, musicPath);
        }
    }


    /**
     * description ：开启美颜
     * creation date: 2021/2/1
     * user : zhangtongju
     */
    @Override
    protected FURenderer initFURenderer() {
//        ArrayList<Effect> effects = EffectEnum.getEffectsByEffectType(1);
        return new FURenderer
                .Builder(this)
                .inputTextureType(FURenderer.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE)
//                .defaultEffect(effects.size() > 1 ? effects.get(1) : null)
                .inputImageOrientation(mFrontCameraOrientation)
                .setLoadAiHumanProcessor(false)
                .maxHumans(1)
                .setNeedFaceBeauty(true)
                .setLoadAiHandProcessor(true)
                .setOnFUDebugListener(this)
                .setOnTrackingStatusChangedListener(this)
                .setOnBundleLoadCompleteListener(null)
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

                case R.id.animation_view_progress:
                    if (isRecording) {
                        animation_view_progress.setProgress(0);
                        lottieAnimationView.setProgress(0);
                        LogUtil.d("OOM", "直接录制结束");
                        isRecording = false;
                        presenter.stopRecord();
                        stopRecording();
                        isRecordingState(false);
                    } else {
                        LogUtil.d("OOM", "开始录制");
                        isRecording = true;
                        presenter.StartCountDown();
                        tv_count_down.setVisibility(View.VISIBLE);
                        lottieAnimationView.setMaxProgress(20 / (float) 47);
                        lottieAnimationView.playAnimation();
                        isRecordingState(true);
                    }
                    break;


                case R.id.iv_count_down:
                    //倒计时功能
                    presenter.clickCountDown(iv_count_down);
                    break;


                case R.id.iv_rolling_over:
                    mCameraRenderer.switchCamera();
                    break;


                case R.id.ll_stage_property:
                    //道具
                    presenter.showBottomSheetDialog(getSupportFragmentManager(), relative_content);
                    relative_content.setVisibility(View.VISIBLE);
                    showSticker(true);
                    break;


                default:

                    break;
            }
        }
    };


    /**
     * description ：选择音乐的回调,选择音乐后时长添加默认
     * creation date: 2021/1/28
     * user : zhangtongju
     */
    @Subscribe
    public void onEventMainThread(CutSuccess cutSuccess) {
        LogUtil.d("OOM", "onEventMainThread");
        String nowChooseBjPath = cutSuccess.getFilePath();
        String nowOriginal = cutSuccess.getOriginalPath();
        String title = cutSuccess.getTitle();
        tv_chooseMusic.setText(title);
        SetNowChooseMusic(nowChooseBjPath, nowOriginal);
        presenter.SetNowChooseMusic(nowChooseBjPath, nowOriginal);
        if(isFrom==0){
            presenter.SetDefaultTime( cutSuccess.getFilePath());
            horizontalselectedView.SetChoosePosition(0);
        }


    }


    /**
     * description ：显示倒计时功能
     * creation date: 2021/1/29
     * num 当前需要显示的值 countDownStatus 0 表示是开始录屏的倒计时状态 1 表示录制视频的状态  progress 录屏状态下的百分比
     * user : zhangtongju
     */
    @Override
    public void showCountDown(int num, int countDownStatus, float progress) {
        Observable.just(num).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                if (countDownStatus == 0) {
                    //倒计时
                    if (num != 0) {
                        tv_count_down.setText(num + "");
                        presenter.setViewAnim(tv_count_down);
                    } else {
                        //开始录像
                        tv_count_down.setVisibility(View.GONE);
                        startRecordVideo();
                        startRecording();
                    }
                } else {
                    if (num > 0) {
                        //录屏倒计时
                        animation_view_progress.setProgress(progress);
                    } else {
                        isRecording = false;
                        animation_view_progress.setProgress(0);
                        lottieAnimationView.setProgress(0);
                        presenter.stopRecord();
                        stopRecording();
                        isRecordingState(false);

                    }
                }
            }
        });
    }


    /**
     * description ：当前是否选择的无限
     * creation date: 2021/2/1
     * user : zhangtongju
     */
    private boolean isInfinite;

    @Override
    public void nowChooseRecordIsInfinite(boolean isInfinite) {
        this.isInfinite = isInfinite;
        LogUtil.d("OOM", "isInfinite=" + isInfinite);
    }


    /**
     * description ：更换拍摄贴纸
     * creation date: 2021/2/4
     * user : zhangtongju
     */
    @Override
    public void changeFUSticker(String bundle, String name) {
        String bundlePath = bundle + "/" + name + ".bundle";
        LogUtil.d("OOM3", "bundlePath=" + bundlePath);
        Effect effect = new Effect(name, R.drawable.nihongdeng, bundlePath, 4, Effect.EFFECT_TYPE_STICKER, 0);
        mFURenderer.onEffectSelected(effect);
     //   dissRelative();
    }


    /**
     * description ：开始录制
     * creation date: 2021/2/1
     * user : zhangtongju
     */
    private void startRecordVideo() {
        presenter.startRecord();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.OnDestroy();
    }


    @Override
    public final boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (relative_content.getVisibility() == View.VISIBLE) {
                relative_content.setVisibility(View.GONE);
                showSticker(false);
            } else {
                finish();
            }
        }
        return true;
    }




    /**
     * description ：录屏和非录屏ui状态切换
     * creation date: 2021/2/4
     * user : zhangtongju
     */
    private void isRecordingState(boolean isRecording) {
        if (isRecording) {
            horizontalselectedView.setVisibility(View.GONE);
            constraintLayout.setVisibility(View.GONE);
            ll_stage_property.setVisibility(View.INVISIBLE);
            tv_show_shoot_time.setVisibility(View.VISIBLE);
        } else {
            horizontalselectedView.setVisibility(View.VISIBLE);
            constraintLayout.setVisibility(View.VISIBLE);
            ll_stage_property.setVisibility(View.VISIBLE);
            tv_show_shoot_time.setVisibility(View.GONE);

        }
    }





    /**
     * description ：跳转到下一页
     * creation date: 2021/2/20
     * user : zhangtongju
     */
    public void ToNextPage(String path){
        presenter.ToNextPage(path);
    }




    /**
     * description ：显示贴纸页面当前页面给的反馈
     * creation date: 2021/2/22
     * user : zhangtongju
     */
    public void showSticker(boolean isShow){
        if(isShow){
            horizontalselectedView.setVisibility(View.INVISIBLE);
            relative_click.setVisibility(View.INVISIBLE);
            ll_stage_property.setVisibility(View.INVISIBLE);
        }else{
            horizontalselectedView.setVisibility(View.VISIBLE);
            relative_click.setVisibility(View.VISIBLE);
            ll_stage_property.setVisibility(View.VISIBLE);
        }
    }


    /**
     * description ：
     * creation date: 2021/2/22
     * user : zhangtongju
     */
    public void showCountDown(int num){
        Observable.just(num).observeOn(AndroidSchedulers.mainThread()).subscribe(integer -> tv_show_shoot_time.setText(num+"秒"));
    }
}

package com.flyingeffects.com.ui.view.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.example.horizontalselectedviewlibrary.HorizontalselectedView;
import com.faceunity.FURenderer;
import com.faceunity.entity.Effect;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.base.FUBaseActivity;
import com.flyingeffects.com.enity.CreateCutCallback;
import com.flyingeffects.com.enity.CutSuccess;
import com.flyingeffects.com.enity.isIntoBackground;
import com.flyingeffects.com.enity.NewFragmentTemplateItem;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.view.FUBeautyMvpView;
import com.flyingeffects.com.ui.presenter.FUBeautyMvpPresenter;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.PermissionUtil;
import com.flyingeffects.com.view.MarqueTextView;

import java.util.ArrayList;

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
    private Context mContext;
    private FUBeautyMvpPresenter presenter;
    private HorizontalselectedView horizontalselectedView;
    private LottieAnimationView lottieAnimationView;
    private LottieAnimationView animation_view_progress;
    private TextView tv_count_down;
    private ImageView iv_count_down;
    private RelativeLayout relative_choose_music;
    private MarqueTextView tv_chooseMusic;
    private boolean isRecording = false;
    private LinearLayout ll_stage_property;
    private ConstraintLayout constraintLayout;
    private ImageView iv_close;
    private final ArrayList<String> deniedPermission = new ArrayList<>();
    /**
     * 来自哪个界面  0  默认为主页点击+号页面   1 默认为跟随相机拍摄页面
     */
    private int isFrom;
    private RelativeLayout relative_click;
    private String createDownVideoPath;
    private TextView tv_show_shoot_time;
    /**
     * 防点击，用来录屏完成后，程序处理中，防止持续点击
     */
    private boolean isCanClick = true;
    private LinearLayout ll_bottom;


    @Override
    protected void onCreate() {
        mContext = FUBeautyActivity.this;
        fuBeautyActivity = this;
        iv_close = findViewById(R.id.iv_close);
        constraintLayout = findViewById(R.id.constraintLayout);
        isFrom = getIntent().getIntExtra("isFrom", 0);
        long duration = getIntent().getLongExtra("duration", 0);
        LogUtil.d("OOM2", "duration=" + duration);
        String musicPath = getIntent().getStringExtra("musicPath");
        String title = getIntent().getStringExtra("title");
        String videoBjPath = getIntent().getStringExtra("videoPath");
        int defaultnum = getIntent().getIntExtra("defaultnum", 0);
        String TemplateFilePath = getIntent().getStringExtra("TemplateFilePath");
        String oldFromTo = getIntent().getStringExtra("OldfromTo");
        createDownVideoPath = getIntent().getStringExtra("createDownVideoPath");
        NewFragmentTemplateItem templateItem = (NewFragmentTemplateItem) getIntent().getSerializableExtra("templateItem");
        horizontalselectedView = findViewById(R.id.horizontalselectedView);
        presenter = new FUBeautyMvpPresenter(this, this, horizontalselectedView, isFrom, duration, musicPath, templateItem, TemplateFilePath, oldFromTo, defaultnum, videoBjPath);
        findViewById(R.id.ll_album).setVisibility(View.INVISIBLE);
        relative_choose_music = findViewById(R.id.relative_choose_music);
        relative_choose_music.setOnClickListener(listener);
        iv_close.setOnClickListener(listener);
        tv_chooseMusic = findViewById(R.id.tv_chooseMusic);
        ll_bottom=findViewById(R.id.ll_bottom);
        relative_click = findViewById(R.id.relative_click);
        tv_show_shoot_time = findViewById(R.id.tv_show_shoot_time);
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

        presenter.showBottomSheetDialog(getSupportFragmentManager(), relative_content);

    }


    /**
     * description ：开启美颜
     * creation date: 2021/2/1
     * user : zhangtongju
     */
    @Override
    protected FURenderer initFURenderer() {
        return new FURenderer
                .Builder(this)
                .inputTextureType(FURenderer.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE)
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

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.relative_choose_music:
                    if (isFrom == 0) {
                        StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "12_shoot_music");
                    } else {
                        StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "12_mb_Shoot_music");
                    }
                    presenter.IntoChooseMusic();
                    break;
                case R.id.iv_close:
                    finish();
                    break;
                case R.id.animation_view:
                case R.id.animation_view_progress:
                    LogUtil.d("OOM2", "isCanClick=" + isCanClick);
                    if (isCanClick) {
                        clickBtn();
                    }
                    break;
                case R.id.iv_count_down:
                    //倒计时功能
                    presenter.clickCountDown(iv_count_down, isFrom);
                    break;
                case R.id.iv_rolling_over:
                    switchCamera();
                    if (isFrom == 0) {
                        StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "12_shoot_turn");
                    } else {
                        StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "12_mb_shoot_turn");
                    }
                    break;
                case R.id.ll_stage_property:
                    //道具
                    StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "12_shoot_dj");
                    relative_content.setVisibility(View.VISIBLE);
                    showSticker(true);
                    break;
                default:
                    break;
            }
        }
    };


    private void clickBtn() {
        if (!DoubleClick.getInstance().isFastDoubleClick()) {
            if (isRecording) {
                isCanClick = false;
                LogUtil.d("OOM", "直接录制结束");
                presenter.stopRecord();
                ShowRecordingBtn(true);
                animation_view_progress.setProgress(0);
                lottieAnimationView.setProgress(0);
                isRecording = false;
                tv_count_down.setVisibility(View.GONE);
                tv_count_down.setText("");
                stopRecording();
                if (isFrom == 0) {
                    StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "12_Shoot_finish");
                } else {
                    StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "12_mb_shoot_finish");
                }
                isRecordingState(false);
            } else {
                LogUtil.d("OOM", "开始录制");
                isRecording = true;
                if (isFrom == 0) {
                    StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "12_Shoot_start");
                } else {
                    StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "12_mb_shoot_start");
                }
                presenter.StartCountDown();
                tv_count_down.setVisibility(View.VISIBLE);
                lottieAnimationView.setMaxProgress(20 / (float) 47);
                lottieAnimationView.playAnimation();
                isRecordingState(true);
            }
        }
    }


    /**
     * description ：显示底部按钮，为了解决录制时间过短，不显示的问题
     * creation date: 2021/3/18
     * user : zhangtongju
     */
    public void ShowRecordingBtn(boolean  isShow){
        if(isShow){
            ll_bottom.setVisibility(View.VISIBLE);
            horizontalselectedView.setVisibility(View.VISIBLE);
        }else{
            ll_bottom.setVisibility(View.GONE);
            horizontalselectedView.setVisibility(View.GONE);
        }

    }


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
        if (isFrom == 0) {
            LogUtil.d("OOM2", "nowChooseBjPath=" + nowChooseBjPath);
            presenter.SetDefaultTime(nowChooseBjPath);
            horizontalselectedView.setChoosePosition(0);
        }
    }


    /**
     * description ：显示倒计时功能
     * creation date: 2021/1/29
     * num 当前需要显示的值 countDownStatus 0 表示是开始录屏的倒计时状态 1 表示录制视频的状态  progress 录屏状态下的百分比
     * user : zhangtongju
     */
    @Override
    public void showCountDown(float numf, int countDownStatus, float progress) {
        int num = (int) numf;
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
                        ShowRecordingBtn(false);
                        isCanClick = false;
                        LogUtil.d("OOM22", "录屏完成，触发结束");
                        isRecording = false;
                        animation_view_progress.setProgress(0);
                        lottieAnimationView.setProgress(0);
                        presenter.stopRecord();
                        stopRecording();
                        //延迟的目的是为了防止关闭计时器后还出现在计时的情况
                        new Handler().postDelayed(() -> {
                            isRecordingState(false);
                        }, 200);
                    }
                }
            }
        });
    }


    @Override
    public void nowChooseRecordIsInfinite(boolean isInfinite) {
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
     * description ：清除全部贴纸
     * creation date: 2021/2/23
     * user : zhangtongju
     */
    @Override
    public void clearSticker() {
        Effect effectNone = new Effect("none", R.drawable.ic_delete_all, "", 1, Effect.EFFECT_TYPE_NONE, 0);
        mFURenderer.onEffectSelected(effectNone);
    }

    @Override
    public void finishAct() {
        this.finish();
    }

    /**
     * 处理权限
     *
     * @param requestCode  请求码
     * @param permissions  权限组
     * @param grantResults 授权结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            deniedPermission.clear();
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int result = grantResults[i];
                if (result != PackageManager.PERMISSION_GRANTED) {
                    deniedPermission.add(permission);
                }
            }
            if (!deniedPermission.isEmpty()) {
                new AlertDialog.Builder(this)
                        .setMessage("应用为了录制视频，必须要获取摄像，存储和录音权限，否则会导致功能异常！")
                        .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                            dialog.dismiss();
                            finish();
                        })
                        .setPositiveButton("去授权", (dialog, which) -> {
                            PermissionUtil.gotoPermission(mContext);
                            dialog.dismiss();
                            finish();
                        })
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                dialog.dismiss();
                                finish();
                            }
                        }).create()
                        .show();
            }
        }
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
        Observable.just(isRecording).observeOn(AndroidSchedulers.mainThread()).subscribe(aBoolean -> {
            if (isRecording) {
                horizontalselectedView.setVisibility(View.GONE);
                constraintLayout.setVisibility(View.GONE);
                ll_stage_property.setVisibility(View.INVISIBLE);
                tv_show_shoot_time.setVisibility(View.VISIBLE);
                relative_choose_music.setVisibility(View.GONE);
                animation_view_progress.setProgress(0);
                animation_view_progress.setVisibility(View.VISIBLE);
                tv_show_shoot_time.setVisibility(View.VISIBLE);
                iv_close.setVisibility(View.GONE);
            } else {
                if (isFrom != 1) {
                    horizontalselectedView.setVisibility(View.VISIBLE);
                    ll_stage_property.setVisibility(View.VISIBLE);
                    tv_show_shoot_time.setVisibility(View.GONE);
                }
                tv_show_shoot_time.setVisibility(View.GONE);
                tv_show_shoot_time.setText("0秒");
                constraintLayout.setVisibility(View.VISIBLE);
                animation_view_progress.setVisibility(View.INVISIBLE);
                iv_close.setVisibility(View.VISIBLE);
                relative_choose_music.setVisibility(View.VISIBLE);
            }
        });

    }


    /**
     * description ：跳转到下一页
     * creation date: 2021/2/20
     * user : zhangtongju
     */
    public void toNextPage(String path) {
        presenter.toNextPage(path);
    }

    /**
     * description ：显示贴纸页面当前页面给的反馈
     * creation date: 2021/2/22
     * user : zhangtongju
     */
    public void showSticker(boolean isShow) {
        if (isShow) {
            horizontalselectedView.setVisibility(View.INVISIBLE);
            relative_click.setVisibility(View.INVISIBLE);
            ll_stage_property.setVisibility(View.INVISIBLE);
        } else {
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
    public void showCountDown(int num) {
        Observable.just(num).observeOn(AndroidSchedulers.mainThread()).subscribe(integer -> tv_show_shoot_time.setText(num + "秒"));
    }


    @Subscribe
    public void onEventMainThread(isIntoBackground isIntoBackground) {
        LogUtil.d("OOM3", "isIntoBackground=" + isIntoBackground.isBackground());
        if (isIntoBackground.isBackground() && isRecording) {
            clickBtn();
        }
    }

    @Subscribe
    public void onEventMainThread(CreateCutCallback event) {
        LogUtil.d("OOM2", "跳转到创作页面" + "createDownVideoPath=" + createDownVideoPath);
        presenter.intoCreationTemplateActivity(event.getCoverPath(), createDownVideoPath, event.getOriginalPath(), event.isNeedCut());
    }

    /**
     * description ：可点击状态回调
     * creation date: 2021/2/26
     * user : zhangtongju
     */
    public void changeClickState() {
        isCanClick = true;
    }

}

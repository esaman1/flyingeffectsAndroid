package com.flyingeffects.com.ui.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.example.horizontalselectedviewlibrary.HorizontalselectedView;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.CreateTemplateTextEffectAdapter;
import com.flyingeffects.com.adapter.TemplateViewPager;
import com.flyingeffects.com.adapter.home_vp_frg_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.mvpBase.BasePresenter;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.constans.UiStep;
import com.flyingeffects.com.enity.StickerTypeEntity;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.model.FUBeautyMvpCallback;
import com.flyingeffects.com.ui.interfaces.view.FUBeautyMvpView;
import com.flyingeffects.com.ui.model.CreationTemplateMvpModel;
import com.flyingeffects.com.ui.model.FUBeautyMvpModel;
import com.flyingeffects.com.ui.model.TemplateAddStickerMvpModel;
import com.flyingeffects.com.ui.view.activity.ChooseMusicActivity;
import com.flyingeffects.com.ui.view.fragment.StickerFragment;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;


public class FUBeautyMvpPresenter extends BasePresenter implements FUBeautyMvpCallback {

    private FUBeautyMvpView fUBeautyMvpView;
    private FUBeautyMvpModel fUBeautyMvpmodel;
    private Context context;
    private HorizontalselectedView horizontalselectedView;
    private Timer timer;
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private TimerTask task;
//    private List<View> listForInitBottom = new ArrayList<>();

    /**
     * 当前倒计时状态 0 表示动画前倒计时 1 表示 录制倒计时
     */
    private int countDownStatus;
    /**
     * 当前选择位数
     */
    private int nowChooseCutDownNum = 0;


    public FUBeautyMvpPresenter(Context context, FUBeautyMvpView fUBeautyMvpView, HorizontalselectedView horizontalselectedView) {
        this.fUBeautyMvpView = fUBeautyMvpView;
        this.horizontalselectedView = horizontalselectedView;
        this.context = context;
        fUBeautyMvpmodel = new FUBeautyMvpModel(context, this);
        horizontalselectedView.setData(fUBeautyMvpmodel.GetTimeData());
        horizontalselectedView.setSeeSize(4);
    }

    public void SetNowChooseMusic(String musicPath, String originalPath) {
        fUBeautyMvpmodel.SetNowChooseMusic(musicPath, originalPath);
    }

    /**
     * description ：开启倒计时
     * creation date: 2021/1/28
     * user : zhangtongju
     */
    private int nowCountDownNum;
    private float nowCountDownNumF;


    public void StartCountDown() {
        countDownStatus = 0;
        if (nowChooseCutDownNum == 0) {
            nowCountDownNum = 4;
        } else if (nowChooseCutDownNum == 1) {
            nowCountDownNum = 8;
        } else {
            nowCountDownNum = 11;
        }
        startTimer();
    }

    /**
     * description ：调整到音乐选取页面
     * creation date: 2021/1/28
     * user : zhangtongju
     */
    public void IntoChooseMusic() {
        String text = horizontalselectedView.getSelectedString();
        long duration = fUBeautyMvpmodel.FetChooseDuration(text);
        Intent intent = new Intent(context, ChooseMusicActivity.class);
        LogUtil.d("OOM2", "当前需要的音乐时长为" + duration);
        intent.putExtra("needDuration", duration);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }


    /**
     * description ：点击倒计时功能
     * creation date: 2021/1/29
     * user : zhangtongju
     */
    public void clickCountDown(ImageView iv) {

        nowChooseCutDownNum++;
        if (nowChooseCutDownNum > 2) {
            nowChooseCutDownNum = 0;
        }
        if (nowChooseCutDownNum == 0) {
            iv.setImageResource(R.mipmap.cout_down_3);
        } else if (nowChooseCutDownNum == 1) {
            iv.setImageResource(R.mipmap.cout_down_7);
        } else {
            iv.setImageResource(R.mipmap.cout_down_10);
        }
    }


    /**
     * description 设置View 动画：
     * creation date: 2021/2/1
     * user : zhangtongju
     */
    public void setViewAnim(View view) {
        AnimationSet animationSet = new AnimationSet(true); //true表示共用同一个插值器
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 0.1f, 1f, 0.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        RotateAnimation rotateAnimation=new RotateAnimation(0,120,1f,1f);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);
//        animationSet.addAnimation(rotateAnimation);
        animationSet.setDuration(1000);
        //设置插值器为先加速再减速
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        //动画完成后保持位置
        animationSet.setFillAfter(false);
        view.startAnimation(animationSet);
    }


    /**
     * description ：开始录像，1 如果有音乐，就播放音乐，如果切换了下面时长，就播放原视频音乐  2 开启进度动画
     * creation date: 2021/2/1
     * user : zhangtongju
     */
    private MediaPlayer mediaPlayer;
    private int allNeedDuration;

    public void startRecord() {
        //1
        LogUtil.d("OOM", "startRecord");
        String musicCutPath = fUBeautyMvpmodel.getMusicPath();
        LogUtil.d("OOM", "musicCutPath=" + musicCutPath);
        if (!TextUtils.isEmpty(musicCutPath)) {
            initMediaPlayer(musicCutPath);
            mediaPlayer.start();
        }

        //2  开启进度动画
        String text = horizontalselectedView.getSelectedString();
        long duration = fUBeautyMvpmodel.FetChooseDuration(text);
        if (duration != 0) {
            fUBeautyMvpView.nowChooseRecordIsInfinite(false);
            countDownStatus = 1;
            nowCountDownNum = (int) (duration / 1000);
            allNeedDuration = nowCountDownNum;
            startTimer();
        } else {
            fUBeautyMvpView.nowChooseRecordIsInfinite(true);
        }
    }


    public void stopRecord() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }
    }


    private void initMediaPlayer(String path) {
        try {
            mediaPlayer = new MediaPlayer();
            File file = new File(path);
            mediaPlayer.setDataSource(file.getPath());//指定音频文件路径
            mediaPlayer.setLooping(true);//设置为循环播放
            mediaPlayer.prepare();//初始化播放器MediaPlayer
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * description ：倒计时功能
     * creation date: 2021/1/28
     * user : zhangtongju
     */
    private void startTimer() {
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        };

        if (countDownStatus == 1) {
            nowCountDownNumF = nowCountDownNum;
            timer.schedule(task, 0, 50);
        } else {
            timer.schedule(task, 0, 1000);
        }


    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (countDownStatus == 1) {
                        nowCountDownNumF = nowCountDownNumF - 0.05f;
                        LogUtil.d("OOM", "返回值为" + nowCountDownNum);
                        float progress = 0f;
                        progress = nowCountDownNumF / (float) allNeedDuration;
                        progress = 1 - progress;
                        fUBeautyMvpView.showCountDown((int) nowCountDownNumF, countDownStatus, progress);
                        if (nowCountDownNumF == 0) {
                            endTimer();
                        }
                    } else {
                        nowCountDownNum = nowCountDownNum - 1;
                        fUBeautyMvpView.showCountDown(nowCountDownNum, countDownStatus, 0);
                        if (nowCountDownNum == 0) {
                            endTimer();
                        }
                    }


                    break;


                default:

                    break;
            }
        }
    };


    /**
     * 关闭timer 和task
     */
    private void endTimer() {
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
    }


    private float countDownTotal;

    private void startProgress() {
        countDownTotal = nowCountDownNum;
        while (nowCountDownNum > 0) {
            countDownTotal = countDownTotal - 0.05f;
            float progress = countDownTotal / (float) allNeedDuration;
            progress = 1 - progress;
            Observable.just(progress).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Float>() {
                @Override
                public void call(Float aFloat) {
                    fUBeautyMvpView.showCountDown((int) countDownTotal, 1, aFloat);
                }
            });
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * description ：销毁，清除音乐播放
     * creation date: 2021/2/1
     * user : zhangtongju
     */
    public void OnDestroy() {
        stopRecord();
    }


    /**
     * description ：显示底部筛选框
     * creation date: 2021/2/2
     * user : zhangtongju
     */

    public void showBottomSheetDialog(FragmentManager fragmentManager, RelativeLayout relative_parent) {
        View  view = LayoutInflater.from(context).inflate(R.layout.view_template_paster, relative_parent, false);
        ViewPager stickerViewPager = view.findViewById(R.id.viewpager_sticker);
        view.findViewById(R.id.iv_delete_sticker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        view.findViewById(R.id.iv_down_sticker).setVisibility(View.GONE);
        SlidingTabLayout stickerTab = view.findViewById(R.id.tb_sticker);
        getStickerTypeList(fragmentManager, stickerViewPager, stickerTab);
        relative_parent.addView(view);
    }


    private void getStickerTypeList(FragmentManager fragmentManager, final ViewPager stickerViewPager, SlidingTabLayout stickerTab) {
        HashMap<String, String> params = new HashMap<>();
        Observable ob = Api.getDefault().camerStickerCategoryList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<ArrayList<StickerTypeEntity>>(context) {
            @Override
            protected void _onError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(ArrayList<StickerTypeEntity> list) {
                LogUtil.d("OOM", "123" + StringUtil.beanToJSONString(list));
                List<Fragment> fragments = new ArrayList<>();
                String[] titles = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    titles[i] = list.get(i).getName();
                    Bundle bundle = new Bundle();
                    bundle.putInt("category_id", list.get(i).getId());
                    bundle.putInt("type",1);
                    LogUtil.d("OOM2", "贴纸id为" + list.get(i).getId());
                    StickerFragment fragment = new StickerFragment();
                    fragment.setStickerListener(null);
                    fragment.setFUStickerListener(new StickerFragment.DownZipCallback() {
                        @Override
                        public void showDownProgress(int progress) {

                            LogUtil.d("OOM3","下载的进度为"+progress);
                        }

                        @Override
                        public void zipPath(String path,String title) {
                            LogUtil.d("OOM3","下载完成"+path);
                            fUBeautyMvpView.changeFUSticker(path,title);


                        }
                    });
                    fragment.setArguments(bundle);
                    fragments.add(fragment);
                }
                home_vp_frg_adapter vp_frg_adapter = new home_vp_frg_adapter(fragmentManager, fragments);
                LogUtil.d("OOM","111111");
                if (stickerViewPager!=null){

                    stickerViewPager.setOffscreenPageLimit(list.size() - 1);
                    stickerViewPager.setAdapter(vp_frg_adapter);
                }
                LogUtil.d("OOM","22222");
                stickerTab.setViewPager(stickerViewPager, titles);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);
    }


}

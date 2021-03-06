package com.flyingeffects.com.ui.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.example.horizontalselectedviewlibrary.HorizontalselectedView;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.home_vp_frg_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.base.mvpBase.BasePresenter;
import com.flyingeffects.com.commonlyModel.getVideoInfo;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.entity.ClearChooseStickerState;
import com.flyingeffects.com.entity.NewFragmentTemplateItem;
import com.flyingeffects.com.entity.StickerTypeEntity;
import com.flyingeffects.com.entity.TabEntity;
import com.flyingeffects.com.entity.VideoInfo;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.model.FUBeautyMvpCallback;
import com.flyingeffects.com.ui.interfaces.view.FUBeautyMvpView;
import com.flyingeffects.com.ui.model.FUBeautyMvpModel;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.ui.view.activity.ChooseMusicActivity;
import com.flyingeffects.com.ui.view.activity.CreationTemplateActivity;
import com.flyingeffects.com.ui.view.activity.TemplateAddStickerActivity;
import com.flyingeffects.com.ui.view.activity.TemplateCutVideoActivity;
import com.flyingeffects.com.ui.view.activity.VideoCropActivity;
import com.flyingeffects.com.ui.view.fragment.StickerFragment;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;
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
     * ????????????????????? 0 ???????????????????????? 1 ?????? ???????????????
     */
    private int countDownStatus;
    /**
     * ??????????????????
     */
    private int nowChooseCutDownNum = 0;

    /**
     * ????????????????????? 0??????????????????????????? 1 ???????????????????????????????????????
     */
    private int isFrom;
    private NewFragmentTemplateItem templateItem;
    private String TemplateFilePath;
    private String OldfromTo;
    private int defaultnum;
    private String videoBjPath;
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();


    /**
     * ?????????????????????
     */
    private long duration;

    public FUBeautyMvpPresenter(Context context, FUBeautyMvpView fUBeautyMvpView, HorizontalselectedView horizontalselectedView, int isFrom, long duration, String musicPath, NewFragmentTemplateItem templateItem, String TemplateFilePath, String OldfromTo, int defaultnum, String videoBjPath) {
        this.fUBeautyMvpView = fUBeautyMvpView;
        this.horizontalselectedView = horizontalselectedView;
        this.context = context;
        this.isFrom = isFrom;
        this.TemplateFilePath = TemplateFilePath;
        this.templateItem = templateItem;
        this.duration = duration;
        this.OldfromTo = OldfromTo;
        this.defaultnum = defaultnum;
        fUBeautyMvpmodel = new FUBeautyMvpModel(context, this, duration, musicPath, isFrom);
        horizontalselectedView.setData(fUBeautyMvpmodel.GetTimeData());
        addTabDate(false);
        //bug  ??????????????????
        horizontalselectedView.setSeeSize(4);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                horizontalselectedView.setChoosePosition(0);
            }
        }, 100);
    }


    /**
     * description ???????????????
     * creation date: 2021/2/20
     * user : zhangtongju
     */
    private void addTabDate(boolean hasDefault) {

        ArrayList<String> data = fUBeautyMvpmodel.GetTimeData();

        if (hasDefault) {
            data.add(0, "??????");
        }
        for (int i = 0; i < data.size(); i++) {
            mTabEntities.add(new TabEntity(data.get(i), 0, 0));
        }
    }

    public void SetNowChooseMusic(String musicPath, String originalPath) {
        fUBeautyMvpmodel.SetNowChooseMusic(musicPath, originalPath);
    }

    /**
     * description ??????????????????
     * creation date: 2021/1/28
     * user : zhangtongju
     */
    private float nowCountDownNum;


    public void StartCountDown() {
        nowCountDownNum = GetCountDown();
        countDownStatus = 0;
        startTimer();
    }

    public int GetCountDown() {
        int needTime = 0;
        if (nowChooseCutDownNum == 0) {
            needTime = 4;
        } else if (nowChooseCutDownNum == 1) {
            needTime = 8;
        } else {
            needTime = 11;
        }
        return needTime;
    }


    /**
     * description ??????????????????????????????
     * creation date: 2021/1/28
     * user : zhangtongju
     */
    public void IntoChooseMusic() {
        String text = horizontalselectedView.getSelectedString();
        long duration;
        if (!TextUtils.isEmpty(text) && ("??????".equals(text) || "??????".equals(text))) {
            duration = 0;
        } else {
            duration = fUBeautyMvpmodel.FetChooseDuration(text);
        }
        Intent intent = new Intent(context, ChooseMusicActivity.class);
        LogUtil.d("OOM2", "??????????????????????????????" + duration);
        intent.putExtra("needDuration", duration);
        intent.putExtra("isFromShoot", true);
        intent.putExtra(ChooseMusicActivity.IS_FROM, isFrom);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }


    /**
     * description ?????????????????????
     * creation date: 2021/2/20
     * user : zhangtongju
     */
    public void toNextPage(String path) {
        if (isFrom == 0) {
            Intent intent = new Intent(context, TemplateAddStickerActivity.class);
            intent.putExtra("videoPath", path);
            intent.putExtra("title", "????????????");
            intent.putExtra("templateType", "-1");
            intent.putExtra("IsFrom", FromToTemplate.SHOOT);
            context.startActivity(intent);
        } else {
            // ?????????????????????????????????
            StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "12_mb_Shoot_finish_next");
            String templateType = templateItem.getTemplate_type();
            if ("2".equals(templateType)) {
//                intoCreationTemplateActivity(path, videoBjPath, path, true);
                Intent intent = new Intent(context, VideoCropActivity.class);
                intent.putExtra("videoPath", path);
                intent.putExtra("comeFrom", FromToTemplate.ISCHOOSEBJ);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            } else {
                ArrayList<String> paths = new ArrayList<>();
                paths.add(path);
                String videoTime = templateItem.getVideotime();
                if (!TextUtils.isEmpty(videoTime) && !"0".equals(videoTime)) {
                    float needVideoTime = Float.parseFloat(videoTime);
                    Intent intoCutVideo = new Intent(context, TemplateCutVideoActivity.class);
                    intoCutVideo.putExtra("needCropDuration", needVideoTime);
                    intoCutVideo.putExtra("templateName", templateItem.getTitle());
                    intoCutVideo.putExtra("videoPath", paths.get(0));
                    intoCutVideo.putExtra("picout", templateItem.getIs_picout());
                    context.startActivity(intoCutVideo);
                }
            }
        }
        fUBeautyMvpView.finishAct();
    }

//    private void intoTemplateActivity(List<String> paths, String templateFilePath) {
//        Intent intent = new Intent(context, TemplateActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putStringArrayList("paths", (ArrayList<String>) paths);
//        bundle.putInt("isPicNum", defaultnum);
//        bundle.putString("fromTo", OldfromTo);
//        bundle.putInt("picout", templateItem.getIs_picout());
//        bundle.putInt("is_anime", templateItem.getIs_anime());
//        bundle.putString("templateName", templateItem.getTitle());
//        bundle.putString("templateId", templateItem.getId() + "");
//        bundle.putString("videoTime", templateItem.getVideotime());
//        bundle.putStringArrayList("originalPath", (ArrayList<String>) paths);
//        bundle.putString("templateFilePath", templateFilePath);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra("Message", bundle);
//        intent.putExtra("person", templateItem);
//        context.startActivity(intent);
//    }

    public void intoCreationTemplateActivity(String imagePath, String videoPath, String originalPath, boolean isNeedCut) {
        Intent intent = new Intent(context, CreationTemplateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("paths", imagePath);
        bundle.putSerializable("bjTemplateTitle", templateItem.getTitle());
        bundle.putString("originalPath", originalPath);
        bundle.putString("video_path", videoPath);
        boolean isLandscape = templateItem.getIsLandscape() == 1;
        bundle.putBoolean("isLandscape", isLandscape);
        bundle.putBoolean("isNeedCut", isNeedCut);
        int id = templateItem.getTemplate_id();
        if (id == 0) {
            id = templateItem.getId();
        }
        bundle.putSerializable("templateId", id);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Message", bundle);
        context.startActivity(intent);
    }


    /**
     * description ????????????????????????
     * creation date: 2021/1/29
     * user : zhangtongju
     */
    public void clickCountDown(ImageView iv, int isFrom) {
        String secondText;
        nowChooseCutDownNum++;
        if (nowChooseCutDownNum > 2) {
            nowChooseCutDownNum = 0;
        }
        if (nowChooseCutDownNum == 0) {
            iv.setImageResource(R.mipmap.cout_down_3);
            secondText = "3s";
        } else if (nowChooseCutDownNum == 1) {
            iv.setImageResource(R.mipmap.cout_down_7);
            secondText = "7s";
        } else {
            iv.setImageResource(R.mipmap.cout_down_10);
            secondText = "10s";
        }
        if (isFrom == 0) {
            StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "12_shoot_countdown", secondText);
        } else {
            StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "12_mb_shoot_countdown", secondText);
        }
    }

    /**
     * description ??????View ?????????
     * creation date: 2021/2/1
     * user : zhangtongju
     */
    public void setViewAnim(View view) {
        AnimationSet animationSet = new AnimationSet(true); //true??????????????????????????????
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 0.1f, 1f, 0.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        RotateAnimation rotateAnimation=new RotateAnimation(0,120,1f,1f);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);
//        animationSet.addAnimation(rotateAnimation);
        animationSet.setDuration(1000);
        //????????????????????????????????????
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        //???????????????????????????
        animationSet.setFillAfter(false);
        view.startAnimation(animationSet);
    }

    /**
     * description ??????????????????1 ??????????????????????????????????????????????????????????????????????????????????????????  2 ??????????????????
     * creation date: 2021/2/1
     * user : zhangtongju
     */
    private MediaPlayer mediaPlayer;
    private float allNeedDuration;

    public void startRecord() {
        //1
        LogUtil.d("OOM", "startRecord");
        String musicCutPath = fUBeautyMvpmodel.getMusicPath();
        LogUtil.d("OOM", "musicCutPath=" + musicCutPath);
        if (!TextUtils.isEmpty(musicCutPath)) {
            initMediaPlayer(musicCutPath);
            mediaPlayer.start();
        }

        //2  ??????????????????
        if (isFrom != 1) {
            String text = horizontalselectedView.getSelectedString();
            StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "12_shoot_time", text);
            long duration = fUBeautyMvpmodel.FetChooseDuration(text);
            LogUtil.d("OOM2", "duration=" + duration);
            if (duration != 0) {
                fUBeautyMvpView.nowChooseRecordIsInfinite(false);
                countDownStatus = 1;
                nowCountDownNum = (duration / (float) 1000);
                LogUtil.d("OOM2", "nowCountDownNum=" + nowCountDownNum);
                allNeedDuration = nowCountDownNum;
                startTimer();
            } else {
                fUBeautyMvpView.nowChooseRecordIsInfinite(true);
            }
        } else {
            if (duration != 0) {
                LogUtil.d("OOM2", "duration=" + duration);
                fUBeautyMvpView.nowChooseRecordIsInfinite(false);
                countDownStatus = 1;
                nowCountDownNum = (duration / (float) 1000);
                LogUtil.d("OOM2", "nowCountDownNum=" + nowCountDownNum);
                allNeedDuration = nowCountDownNum;
                startTimer();
            } else {
                fUBeautyMvpView.nowChooseRecordIsInfinite(true);
            }
        }

    }


    public void stopRecord() {
        if (mediaPlayer != null) {
//            if (mediaPlayer.isPlaying()) {
//                mediaPlayer.pause();
//            }
            mediaPlayer.release();
        }
        endTimer();
    }


    private void initMediaPlayer(String path) {
        try {
            mediaPlayer = new MediaPlayer();
            File file = new File(path);
            mediaPlayer.setDataSource(file.getPath());//????????????????????????
            mediaPlayer.setLooping(true);//?????????????????????
            mediaPlayer.prepare();//??????????????????MediaPlayer
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * description ??????????????????
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
                        nowCountDownNum = nowCountDownNum - 0.05f;
                        LogUtil.d("OOM", "nowCountDownNumF" + nowCountDownNum);
                        float progress;
                        progress = nowCountDownNum / allNeedDuration;
                        progress = 1 - progress;
                        fUBeautyMvpView.showCountDown((int) nowCountDownNum, countDownStatus, progress);
                        if (nowCountDownNum <= 0) {
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
     * ??????timer ???task
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
     * description ??????????????????????????????
     * creation date: 2021/2/1
     * user : zhangtongju
     */
    public void OnDestroy() {
        stopRecord();
    }


    /**
     * description ????????????????????????
     * creation date: 2021/2/2
     * user : zhangtongju
     */

    public void showBottomSheetDialog(FragmentManager fragmentManager, RelativeLayout relative_parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_template_paster, relative_parent, false);
        ViewPager stickerViewPager = view.findViewById(R.id.viewpager_sticker);
        view.findViewById(R.id.iv_delete_sticker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fUBeautyMvpView.clearSticker();
                EventBus.getDefault().post(new ClearChooseStickerState());
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
            protected void onSubError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void onSubNext(ArrayList<StickerTypeEntity> list) {
                LogUtil.d("OOM", "123" + StringUtil.beanToJSONString(list));
                List<Fragment> fragments = new ArrayList<>();
                String[] titles = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    titles[i] = list.get(i).getName();
                    Bundle bundle = new Bundle();
                    bundle.putInt("category_id", list.get(i).getId());
                    bundle.putInt("type", 1);
                    LogUtil.d("OOM2", "??????id???" + list.get(i).getId());
                    StickerFragment fragment = new StickerFragment();
                    fragment.setStickerListener(null);
                    fragment.setFUStickerListener(new StickerFragment.DownZipCallback() {
                        @Override
                        public void showDownProgress(int progress) {

                            LogUtil.d("OOM3", "??????????????????" + progress);
                        }

                        @Override
                        public void zipPath(String path, String title) {
                            LogUtil.d("OOM3", "????????????" + path);
                            fUBeautyMvpView.changeFUSticker(path, title);


                        }
                    });
                    fragment.setArguments(bundle);
                    fragments.add(fragment);
                }
                home_vp_frg_adapter vp_frg_adapter = new home_vp_frg_adapter(fragmentManager, fragments);
                LogUtil.d("OOM", "111111");
                if (stickerViewPager != null) {

                    stickerViewPager.setOffscreenPageLimit(list.size() - 1);
                    stickerViewPager.setAdapter(vp_frg_adapter);
                }
                LogUtil.d("OOM", "22222");
                stickerTab.setViewPager(stickerViewPager, titles);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);
    }


    /**
     * description ?????????????????????????????????????????????
     * creation date: 2021/2/22
     * user : zhangtongju
     */
    public void setDefaultTime(String musicPath) {
        VideoInfo videoInfo = getVideoInfo.getInstance().getRingDuring(musicPath);
        int duration = (int) videoInfo.getDuration();
        LogUtil.d("OOM2", "duration=" + duration);
        int[] timeDataInt = {duration, 0, 15000, 30000, 60000};
        fUBeautyMvpmodel.setTimeDataInt(timeDataInt);
        int[] timeDataInt2 = fUBeautyMvpmodel.getTimeDataInt();

        for (int value : timeDataInt2) {
            LogUtil.d("OOM2", "timeDataInt2=" + value);
        }

        ArrayList<String> list = fUBeautyMvpmodel.GetTimeData();
        if (!"??????".equals(list.get(0))) {
            list.add(0, "??????");
        }
        horizontalselectedView.setData(list);
        horizontalselectedView.setSeeSize(4);
    }


}

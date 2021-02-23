package com.flyingeffects.com.ui.presenter;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.horizontalselectedviewlibrary.HorizontalselectedView;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.CreateTemplateTextEffectAdapter;
import com.flyingeffects.com.adapter.TemplateViewPager;
import com.flyingeffects.com.adapter.home_vp_frg_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.mvpBase.BasePresenter;
import com.flyingeffects.com.commonlyModel.getVideoInfo;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.constans.UiStep;
import com.flyingeffects.com.enity.StickerTypeEntity;
import com.flyingeffects.com.enity.TabEntity;
import com.flyingeffects.com.enity.VideoInfo;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.model.FUBeautyMvpCallback;
import com.flyingeffects.com.ui.interfaces.view.FUBeautyMvpView;
import com.flyingeffects.com.ui.model.CreationTemplateMvpModel;
import com.flyingeffects.com.ui.model.FUBeautyMvpModel;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.ui.model.TemplateAddStickerMvpModel;
import com.flyingeffects.com.ui.view.activity.ChooseMusicActivity;
import com.flyingeffects.com.ui.view.activity.CreationTemplateActivity;
import com.flyingeffects.com.ui.view.activity.PreviewUpAndDownActivity;
import com.flyingeffects.com.ui.view.activity.TemplateActivity;
import com.flyingeffects.com.ui.view.activity.TemplateAddStickerActivity;
import com.flyingeffects.com.ui.view.activity.TemplateCutVideoActivity;
import com.flyingeffects.com.ui.view.activity.VideoCropActivity;
import com.flyingeffects.com.ui.view.fragment.SimpleCardFragment;
import com.flyingeffects.com.ui.view.fragment.StickerFragment;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.lansosdk.videoeditor.MediaInfo;

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

    /**
     * 来自于哪个页面 0表示首页的拍摄页面 1 表示来自于跟随音乐拍摄页面
     */
    private int isFrom;
    private new_fag_template_item templateItem;
    private String TemplateFilePath;
    private String OldfromTo;
    private int defaultnum;
    private String videoBjPath;
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();



    /**从拍摄页面进入*/
    private long duration;
    public FUBeautyMvpPresenter(Context context, FUBeautyMvpView fUBeautyMvpView, HorizontalselectedView horizontalselectedView, int isFrom, long duration, String musicPath, new_fag_template_item templateItem, String TemplateFilePath, String OldfromTo, int defaultnum, String videoBjPath) {
        this.fUBeautyMvpView = fUBeautyMvpView;
        this.horizontalselectedView = horizontalselectedView;
        this.context = context;
        this.isFrom = isFrom;
        this.TemplateFilePath = TemplateFilePath;
        this.templateItem = templateItem;
        this.duration=duration;
        this.OldfromTo = OldfromTo;
        this.defaultnum = defaultnum;
        fUBeautyMvpmodel = new FUBeautyMvpModel(context, this, duration, musicPath, isFrom);
        horizontalselectedView.setData(fUBeautyMvpmodel.GetTimeData());
        horizontalselectedView.setSeeSize(4);
        addTabDate(false);
        horizontalselectedView.SetChoosePosition(0);
    }


    /**
     * description ：添加数据
     * creation date: 2021/2/20
     * user : zhangtongju
     */
    private void addTabDate(boolean hasDefault) {

        ArrayList<String> data = fUBeautyMvpmodel.GetTimeData();

        if (hasDefault) {
            data.add(0, "默认");
        }
        for (int i = 0; i < data.size(); i++) {
            mTabEntities.add(new TabEntity(data.get(i), 0, 0));
        }
    }


    public void SetNowChooseMusic(String musicPath, String originalPath) {
        fUBeautyMvpmodel.SetNowChooseMusic(musicPath, originalPath);
    }

    /**
     * description ：开启倒计时
     * creation date: 2021/1/28
     * user : zhangtongju
     */
    private float nowCountDownNum;


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
        intent.putExtra("isFromShoot",true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }


    /**
     * description ：跳转到下一页
     * creation date: 2021/2/20
     * user : zhangtongju
     */
    public void ToNextPage(String path) {
        if (isFrom == 0) {
            Intent intent = new Intent(context, TemplateAddStickerActivity.class);
            intent.putExtra("videoPath", path);
            intent.putExtra("title", "拍摄入口");
            intent.putExtra("IsFrom", FromToTemplate.SHOOT);
            context.startActivity(intent);
        } else {
            // 这里是跟随相机拍摄页面
            String templateType = templateItem.getTemplate_type();
            if (templateType.equals("2")) {
                intoCreationTemplateActivity(path, videoBjPath, path, true);
            } else {
                ArrayList<String> paths = new ArrayList<>();
                paths.add(path);
                intoTemplateActivity(paths, TemplateFilePath);
            }
        }
    }

    private void intoTemplateActivity(List<String> paths, String templateFilePath) {
        Intent intent = new Intent(context, TemplateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("paths", (ArrayList<String>) paths);
        bundle.putInt("isPicNum", defaultnum);
        bundle.putString("fromTo", OldfromTo);
        bundle.putInt("picout", templateItem.getIs_picout());
        bundle.putInt("is_anime", templateItem.getIs_anime());
        bundle.putString("templateName", templateItem.getTitle());
        bundle.putString("templateId", templateItem.getId() + "");
        bundle.putString("videoTime", templateItem.getVideotime());
        bundle.putStringArrayList("originalPath", (ArrayList<String>) paths);
        bundle.putString("templateFilePath", templateFilePath);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Message", bundle);
        intent.putExtra("person", templateItem);
        context.startActivity(intent);
    }

    private void intoCreationTemplateActivity(String imagePath, String videoPath, String originalPath, boolean isNeedCut) {
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

        //2  开启进度动画
        
        if(isFrom!=1){
            String text = horizontalselectedView.getSelectedString();
            long duration = fUBeautyMvpmodel.FetChooseDuration(text);
            LogUtil.d("OOM2","duration="+duration);
            if (duration != 0) {
                fUBeautyMvpView.nowChooseRecordIsInfinite(false);
                countDownStatus = 1;
                nowCountDownNum = (int) (duration / 1000);
                LogUtil.d("OOM2","nowCountDownNum="+nowCountDownNum);
                allNeedDuration = nowCountDownNum;
                startTimer();
            } else {
                fUBeautyMvpView.nowChooseRecordIsInfinite(true);
            }
        }else{
            if (duration != 0) {
                LogUtil.d("OOM2","duration="+duration);
                fUBeautyMvpView.nowChooseRecordIsInfinite(false);
                countDownStatus = 1;
                nowCountDownNum =  (duration /(float) 1000);
                LogUtil.d("OOM2","nowCountDownNum="+nowCountDownNum);
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
                        float progress ;
                        progress = nowCountDownNum /allNeedDuration;
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
        View view = LayoutInflater.from(context).inflate(R.layout.view_template_paster, relative_parent, false);
        ViewPager stickerViewPager = view.findViewById(R.id.viewpager_sticker);
        view.findViewById(R.id.iv_delete_sticker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fUBeautyMvpView.ClearSticker();

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
                    bundle.putInt("type", 1);
                    LogUtil.d("OOM2", "贴纸id为" + list.get(i).getId());
                    StickerFragment fragment = new StickerFragment();
                    fragment.setStickerListener(null);
                    fragment.setFUStickerListener(new StickerFragment.DownZipCallback() {
                        @Override
                        public void showDownProgress(int progress) {

                            LogUtil.d("OOM3", "下载的进度为" + progress);
                        }

                        @Override
                        public void zipPath(String path, String title) {
                            LogUtil.d("OOM3", "下载完成" + path);
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
     * description ：重新设置数据源，添加默认效果
     * creation date: 2021/2/22
     * user : zhangtongju
     */
    public void SetDefaultTime(String musicPath) {
        VideoInfo videoInfo = getVideoInfo.getInstance().getRingDuring(musicPath);
        int duration = (int) videoInfo.getDuration();
        LogUtil.d("OOM2", "duration=" + duration);
        int[] timeDataInt = {duration, 5000, 15000, 60000, 0};
        fUBeautyMvpmodel.setTimeDataInt(timeDataInt);
        int[] timeDataInt2=fUBeautyMvpmodel.getTimeDataInt();

        for (int i=0;i<timeDataInt2.length;i++){
            LogUtil.d("OOM2", "timeDataInt2=" + timeDataInt2[i]);
        }

        ArrayList<String> list = fUBeautyMvpmodel.GetTimeData();
        if (!list.get(0).equals("默认")) {
            list.add(0, "默认");
        }
        horizontalselectedView.setData(list);
        horizontalselectedView.setSeeSize(4);

    }


}

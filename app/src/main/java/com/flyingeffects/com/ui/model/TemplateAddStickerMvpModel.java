package com.flyingeffects.com.ui.model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.TemplateGridViewAdapter;
import com.flyingeffects.com.adapter.TemplateViewPager;
import com.flyingeffects.com.adapter.home_vp_frg_adapter;
import com.flyingeffects.com.adapter.listViewForVideoThumbAdapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.commonlyModel.GetPathType;
import com.flyingeffects.com.commonlyModel.GetVideoCover;
import com.flyingeffects.com.commonlyModel.getVideoInfo;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.constans.UiStep;
import com.flyingeffects.com.entity.AllStickerData;
import com.flyingeffects.com.entity.StickerAnim;
import com.flyingeffects.com.entity.StickerTypeEntity;
import com.flyingeffects.com.entity.VideoInfo;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.AdConfigs;
import com.flyingeffects.com.manager.AdManager;
import com.flyingeffects.com.manager.AlbumManager;
import com.flyingeffects.com.manager.CompressionCuttingManage;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.manager.StimulateControlManage;
import com.flyingeffects.com.manager.mediaManager;
import com.flyingeffects.com.ui.interfaces.model.TemplateAddStickerMvpCallback;
import com.flyingeffects.com.ui.view.dialog.CommonMessageDialog;
import com.flyingeffects.com.ui.view.dialog.LoadingDialog;
import com.flyingeffects.com.ui.view.fragment.StickerFragment;
import com.flyingeffects.com.utils.CheckVipOrAdUtils;
import com.flyingeffects.com.utils.FileUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.utils.record.SaveShareDialog;
import com.flyingeffects.com.utils.screenUtil;
import com.flyingeffects.com.view.HorizontalListView;
import com.flyingeffects.com.view.StickerView;
import com.flyingeffects.com.view.animations.CustomMove.AnimCollect;
import com.flyingeffects.com.view.animations.CustomMove.AnimType;
import com.flyingeffects.com.view.animations.CustomMove.StartAnimModel;
import com.flyingeffects.com.view.lansongCommendView.StickerItemOnDragListener;
import com.flyingeffects.com.view.lansongCommendView.StickerItemOnitemclick;
import com.lansosdk.box.ViewLayerRelativeLayout;
import com.orhanobut.hawk.Hawk;
import com.shixing.sxve.ui.AlbumType;
import com.shixing.sxve.ui.view.WaitingDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.collection.SparseArrayCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;


public class TemplateAddStickerMvpModel implements StickerFragment.StickerListener {
    private StickerView nowChooseStickerView;
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private TemplateAddStickerMvpCallback callback;
    private Context context;
    private ArrayList<AnimStickerModel> listForStickerModel = new ArrayList<>();
    private SparseArrayCompat<ArrayList<StickerView>> sublayerListForBitmapLayer = new SparseArrayCompat<>();
    private ArrayList<AllStickerData> listAllSticker = new ArrayList<>();
    private ViewLayerRelativeLayout viewLayerRelativeLayout;
    private String mVideoPath;
    private VideoInfo videoInfo;
    private ArrayList<StickerView> nowChooseSubLayerAnimList = new ArrayList<>();
    private AnimCollect animCollect;
    private ArrayList<StickerAnim> listAllAnima;
    private String mGifFolder;
    private boolean isCheckedMatting = true;
    private String mImageCopyFolder;
    private Vibrator vibrator;
    private boolean isDestroy = false;
    /**
     * ??????????????????
     */
    private String videoVoicePath;
    private String soundFolder;

    private TemplateGridViewAdapter gridAdapter;
    private ViewPager viewPager;
    private BackgroundDraw backgroundDraw;
    /**
     * ??????????????????,???????????????????????????????????????
     */
    private long defaultVideoDuration = 0;
    private LoadingDialog mLoadingDialog;
    private ArrayList<StickerView> needDeleteList = new ArrayList<>();
    private int dialogProgress;
    private List<View> listForInitBottom = new ArrayList<>();
    /**
     * ???????????????????????????
     */
    private ArrayList<videoType> cutVideoPathList = new ArrayList<>();
    private HorizontalListView hListView;
    SaveShareDialog mShareDialog;
    String templateTitle;

    /***
     * originalPath  ???????????????????????????
     */
    private String originalPath;


    public TemplateAddStickerMvpModel(Activity context, TemplateAddStickerMvpCallback callback, ViewLayerRelativeLayout viewLayerRelativeLayout,
                                      String mVideoPath, LinearLayout dialogShare, String title) {
        this.context = context;
        this.callback = callback;
        this.viewLayerRelativeLayout = viewLayerRelativeLayout;
        this.mVideoPath = mVideoPath;
        this.templateTitle = title;
        mLoadingDialog = buildLoadingDialog();
        this.originalPath = mVideoPath;
        FileManager fileManager = new FileManager();
        mImageCopyFolder = fileManager.getFileCachePath(context, "imageCopy");
        videoInfo = getVideoInfo.getInstance().getRingDuring(mVideoPath);
        mGifFolder = fileManager.getFileCachePath(context, "gifFolder");
        soundFolder = fileManager.getFileCachePath(context, "soundFolder");
        mShareDialog = new SaveShareDialog(context, dialogShare);
    }

    private LoadingDialog buildLoadingDialog() {
        LoadingDialog dialog = LoadingDialog.getBuilder(context)
                .setHasAd(false)
                .setTitle("?????????...")
                .build();
        return dialog;
    }


    //    private TimelineAdapter mTimelineAdapter;
    private int mScrollX;
    private LinearLayoutManager linearLayoutManager;

    public void initVideoProgressView(HorizontalListView hListView) {
        this.hListView = hListView;
        //?????????????????????????????????
        if (videoInfo != null) {
            initSingleThumbSize(videoInfo.getVideoWidth(), videoInfo.getVideoHeight(), videoInfo.getDuration(), videoInfo.getDuration() / 2, mVideoPath);
        } else {
            getPlayVideoDuration();
            initSingleThumbSize(720, 1280, defaultVideoDuration, defaultVideoDuration / 2, "");
        }
    }


    private List<Long> perSticker = new ArrayList<>();

    private void getPlayVideoDuration() {
        defaultVideoDuration = 0;
        LogUtil.d("OOM", " viewLayerRelativeLayout.getChildCount())=" + viewLayerRelativeLayout.getChildCount());
        perSticker.clear();
        if (viewLayerRelativeLayout.getChildCount() > 0) {
            for (int i = 0; i < viewLayerRelativeLayout.getChildCount(); i++) {
                StickerView stickerView = (StickerView) viewLayerRelativeLayout.getChildAt(i);
                if (!TextUtils.isEmpty(stickerView.getOriginalPath())) {
                    if (AlbumType.isVideo(GetPathType.getInstance().getPathType(stickerView.getOriginalPath()))) {
                        VideoInfo materialVideoInfo = getVideoInfo.getInstance().getRingDuring(stickerView.getOriginalPath());
                        LogUtil.d("OOM", "materialVideoInfo.getDuration()=" + materialVideoInfo.getDuration());
                        perSticker.add(materialVideoInfo.getDuration());
                    }
                }
            }
        } else {
            //?????????????????????????????????????????????0.??????viewLayerRelativeLayout??????????????????????????????????????????????????????
            if (!TextUtils.isEmpty(originalPath)) {
                if (AlbumType.isVideo(GetPathType.getInstance().getPathType(originalPath))) {
                    VideoInfo materialVideoInfo = getVideoInfo.getInstance().getRingDuring(originalPath);
                    LogUtil.d("OOM", "materialVideoInfo.getDuration()=" + materialVideoInfo.getDuration());
                    perSticker.add(materialVideoInfo.getDuration());
                }
            }

        }
        //????????????????????????????????????????????????
        if (perSticker != null && perSticker.size() > 0) {
            if (perSticker.size() == 1) {
                defaultVideoDuration = perSticker.get(0);
            } else {
                for (long duration : perSticker
                ) {
                    if (defaultVideoDuration < duration) {
                        defaultVideoDuration = duration;
                    }
                }
            }
            LogUtil.d("OOM", "?????????????????????" + defaultVideoDuration);
        } else {
            LogUtil.d("OOM", "????????????????????????");
            defaultVideoDuration = 10 * 1000;
        }

//        callback.showRenderVideoTime(defaultVideoDuration);
    }


    private int mTotalWidth;

    private void initSingleThumbSize(int width, int height, float duration, float mTemplateDuration, String mVideoPath) {
        // ???????????????listWidth??????
        int listWidth = hListView.getWidth() - hListView.getPaddingLeft() - hListView.getPaddingRight();
        int listHeight = hListView.getHeight();
        float scale = (float) listHeight / height;
        int thumbWidth = (int) (scale * width);
        //??????listWidth???????????????????????????
        int thumbCount = (int) (listWidth * (duration / mTemplateDuration) / thumbWidth);
        thumbCount = thumbCount > 0 ? thumbCount : 0;
        //?????????????????????
        final int interval = (int) (duration / thumbCount);
        int[] mTimeUs = new int[thumbCount];
        for (int i = 0; i < thumbCount; i++) {
            mTimeUs[i] = i * interval * 1000;
        }
        mTotalWidth = thumbWidth * thumbCount;
        callback.getVideoDuration((int) duration, thumbCount);
        int dp40 = screenUtil.dip2px(context, 43);
        int screenWidth = screenUtil.getScreenWidth((Activity) context);
        listViewForVideoThumbAdapter adapter;
        if (!TextUtils.isEmpty(mVideoPath)) {
            adapter = new listViewForVideoThumbAdapter(context, mTimeUs, Uri.fromFile(new File(mVideoPath)), thumbWidth, listHeight, screenWidth / 2, screenWidth / 2 - dp40);
        } else {
            adapter = new listViewForVideoThumbAdapter(context, mTimeUs, null, thumbWidth, listHeight, screenWidth / 2, screenWidth / 2 - dp40);
        }

        hListView.setAdapter(adapter);
        int realWidth = (screenWidth - screenUtil.dip2px(context, 43)) * 2;
        LogUtil.d("OOM", "realWidth=" + realWidth);
        hListView.setOnScrollListener(mNextX -> {
            float preF = mNextX / realWidth;
            int frame = (int) (duration * preF);
            LogUtil.d("OOM", "preF=" + preF);
            LogUtil.d("OOM", "frame=" + frame);
            callback.setgsyVideoProgress(frame);
        });


    }


    public void deleteAllTextSticker() {
        toDeleteAllTextSticker();
    }

    /**
     * description ???????????????(??????????????????)
     * creation date: 2020/6/8
     * user : zhangtongju
     */
    private ArrayList<StickerView> needDeleteTextList = new ArrayList<>();

    private void toDeleteAllTextSticker() {
        needDeleteTextList.clear();
        if (listForStickerModel != null && listForStickerModel.size() > 0) {
            for (int i = 0; i < listForStickerModel.size(); i++) {
                StickerView stickerView = listForStickerModel.get(i).getStickerView();
                if (stickerView != null && stickerView.getIsTextSticker()) {
                    needDeleteTextList.add(stickerView);
                }
            }
        }

        for (StickerView stickerView : needDeleteTextList) {
            deleteStickView(stickerView);
        }

    }


    public void ChangeTextColor(String color0, String color1, String title) {
        if (nowChooseStickerView.getIsTextSticker()) {
            nowChooseStickerView.setTextPaintColor(color0, color1, title);
        }
    }

    /**
     * description ???textBjPath ?????????????????????textFramePath ????????????
     * creation date: 2020/10/23
     * user : zhangtongju
     */
    public void ChangeTextFrame(String textBjPath, String textFramePath, String Frametitle) {
        if (nowChooseStickerView.getIsTextSticker()) {
            nowChooseStickerView.changeTextFrame(textBjPath, textFramePath, Frametitle);
        }
    }

    public void ChangeTextFrame(String color0, String color1, String textFramePath, String Frametitle) {
        if (nowChooseStickerView.getIsTextSticker()) {
            nowChooseStickerView.changeTextFrame(color0, color1, textFramePath, Frametitle);
        }
    }


    public void showGifAnim(boolean isShow) {
        if (listForStickerModel != null && listForStickerModel.size() > 0) {
            for (AnimStickerModel stickerModel : listForStickerModel
            ) {
                StickerView stickerView = stickerModel.getStickerView();
                if (stickerView != null) {
                    if (isShow) {
                        stickerView.start();
                    } else {
                        stickerView.pause();
                    }
                }
            }
        }
    }


    public void onDestroy() {
        isDestroy = true;
        stopAllAnim();
        closeAllAnim();
        deleteSticker();
    }


    public void initBottomLayout(ViewPager viewPager, FragmentManager fragmentManager) {
        this.viewPager = viewPager;
        View templateThumbView = LayoutInflater.from(context).inflate(R.layout.view_template_paster, viewPager, false);
        ViewPager stickerViewPager = templateThumbView.findViewById(R.id.viewpager_sticker);
        templateThumbView.findViewById(R.id.iv_delete_sticker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAllAnim();
                closeAllAnim();
                deleteSticker();
                if (UiStep.isFromDownBj) {
                    StatisticsEventAffair.getInstance().setFlag(context, " 5_mb_bj_Stickeroff");
                } else {
                    StatisticsEventAffair.getInstance().setFlag(context, " 6_customize_bj_Stickeroff");
                }
            }
        });
        templateThumbView.findViewById(R.id.iv_down_sticker).setVisibility(View.GONE);
        SlidingTabLayout stickerTab = templateThumbView.findViewById(R.id.tb_sticker);
        getStickerTypeList(fragmentManager, stickerViewPager, stickerTab);


        listForInitBottom.add(templateThumbView);


        TemplateViewPager adapter = new TemplateViewPager(listForInitBottom);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {


            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void getStickerTypeList(FragmentManager fragmentManager, ViewPager stickerViewPager, SlidingTabLayout stickerTab) {
        HashMap<String, String> params = new HashMap<>();
        Observable ob = Api.getDefault().getStickerTypeList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<ArrayList<StickerTypeEntity>>(context) {
            @Override
            protected void onSubError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void onSubNext(ArrayList<StickerTypeEntity> list) {
                List<Fragment> fragments = new ArrayList<>();
                String[] titles = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    titles[i] = list.get(i).getName();
                    Bundle bundle = new Bundle();
                    bundle.putInt("stickerType", list.get(i).getId());
                    LogUtil.d("OOM2", "??????id???" + list.get(i).getId());
                    StickerFragment fragment = new StickerFragment();
                    fragment.setStickerListener(TemplateAddStickerMvpModel.this);
                    fragment.setArguments(bundle);
                    fragments.add(fragment);
                }
                home_vp_frg_adapter vp_frg_adapter = new home_vp_frg_adapter(fragmentManager, fragments);
                stickerViewPager.setOffscreenPageLimit(list.size() - 1);
                stickerViewPager.setAdapter(vp_frg_adapter);
                stickerTab.setViewPager(stickerViewPager, titles);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);
    }

    private void deleteSticker() {
        needDeleteList.clear();
        if (listForStickerModel != null && listForStickerModel.size() > 0) {
            for (int i = 0; i < listForStickerModel.size(); i++) {
                StickerView stickerView = listForStickerModel.get(i).getStickerView();
                if (stickerView != null && !stickerView.getComeFrom() && !stickerView.getIsTextSticker()) {
                    needDeleteList.add(stickerView);
                }
            }
        }

        for (StickerView stickerView : needDeleteList
        ) {
            deleteStickView(stickerView);
        }
    }

    /**
     * description ??????????????????????????????????????????
     * creation date: 2020/5/27
     * user : zhangtongju
     */
    private boolean hasAnim = false;
    private int hasAnimCount;
    private int previewCount;
    private int sublayerListPosition;

    public void showAllAnim(boolean isShow) {
        previewCount = 0;
        sublayerListPosition = 0;
        hasAnim = false;
        hasAnimCount = 0;
        //??????????????????

        Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
            deleteSubLayerSticker();
            stopAllAnim();
            //?????????????????????????????????
            listAllSticker.clear();
            for (int y = 0; y < viewLayerRelativeLayout.getChildCount(); y++) {
                StickerView stickerView = (StickerView) viewLayerRelativeLayout.getChildAt(y);
                listAllSticker.add(GetAllStickerDataModel.getInstance().getStickerData(stickerView, false, videoInfo));
            }
            if (isShow) {
                if (listForStickerModel != null && listForStickerModel.size() > 0) {
                    for (int i = 0; i < listForStickerModel.size(); i++) {
                        AnimStickerModel stickerModel = listForStickerModel.get(i);
                        StickerView stickerView = stickerModel.getStickerView();
                        if (stickerView != null && stickerView.getChooseAnimId() != AnimType.NULL) {
                            if (stickerView.getChooseAnimId() != null) {
                                hasAnimCount++;
                                hasAnim = true;
                                int type = animCollect.getAnimid(stickerView.getChooseAnimId());
                                startPlayAnim(type, false, stickerView, i, true);
                            }
                        }
                        if (i == listForStickerModel.size() - 1) {
                            //?????????????????????
                            if (!hasAnim) {
                                callback.animIsComplate();
                            }
                        }
                    }
                } else {
                    callback.animIsComplate();
                }
            }
        });


    }

    /**
     * description ?????????????????????
     * creation date: 2020/5/27
     * user : zhangtongju
     */
    private synchronized void stopAllAnim() {

        if (animCollect != null) {
            animCollect.stopAnim();
        }
        destroyTimer();
    }


    private Timer timer;
    private TimerTask task;
    private int totalPlayTime;

    private void startTimer(StickerView stickView) {
        totalPlayTime = 0;
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
                totalPlayTime = totalPlayTime + 500;
                if (totalPlayTime == 2000) {
                    Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
                        @Override
                        public void call(Integer integer) {
                            destroyTimer();
                            startPlayAnim(animCollect.getAnimid(stickView.getChooseAnimId()), false, null, 0, false);
                        }
                    });

                }
            }
        };
        timer.schedule(task, 0, 500);
    }

    /**
     * user :TongJu  ; email:jutongzhang@sina.com
     * time???2018/10/15
     * describe:??????????????????
     **/
    private void destroyTimer() {
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


    /**
     * description ???????????????????????????
     * creation date: 2020/5/27
     * user : zhangtongju
     */
    private synchronized void deleteSubLayerSticker() {
        if (sublayerListForBitmapLayer != null && sublayerListForBitmapLayer.size() > 0) {
            for (int i = 0; i < sublayerListForBitmapLayer.size(); i++) {
                ArrayList<StickerView> nowChooseSubLayerAnimList = sublayerListForBitmapLayer.get(i);
                //??????????????????
                if (nowChooseSubLayerAnimList != null && nowChooseSubLayerAnimList.size() > 0) {
                    for (StickerView stickerView : nowChooseSubLayerAnimList
                    ) {
                        deleteStickView(stickerView);
                    }
                }
            }
            sublayerListForBitmapLayer.clear();
        }
    }


    private void deleteStickView(StickerView stickView) {
        viewLayerRelativeLayout.removeView(stickView);
        int nowId = stickView.getId();
        if (stickView.isFirstAddSticker()) {
            if (stickView.isOpenVoice()) {
                stickView.setOpenVoice(false);
//                callback.getBgmPath("");
//                videoVoicePath = "";
            }
        }
        deletedListForSticker(nowId);
    }


    private void deletedListForSticker(int id) {
        for (int i = 0; i < listForStickerModel.size(); i++) {
            AnimStickerModel model = listForStickerModel.get(i);
            StickerView stackView = model.getStickerView();
            if (stackView.getId() == id) {
                stackView.pause();
                stackView.destory();
                listForStickerModel.remove(i);
            }
        }
    }


    /**
     * description ????????????????????? ??????????????????????????????stickver
     * creation date: 2020/5/27
     *
     * @param position          ???????????????
     * @param targetStickerView ????????????????????????null ,??????????????????????????????????????????????????????????????????????????????????????????????????????null ,
     *                          ???????????????????????????????????????????????????????????????
     * @param isFromPreview     ????????????????????????
     *                          user : zhangtongju
     */

    private synchronized void startPlayAnim(int position, boolean isClearAllAnim, StickerView targetStickerView, int intoPosition, boolean isFromPreview) {
        if (!isFromPreview) {
            stopAllAnim();
            deleteSubLayerSticker();
            sublayerListPosition = 0;
            //?????????????????????????????????
            listAllSticker.clear();
            for (int y = 0; y < viewLayerRelativeLayout.getChildCount(); y++) {
                StickerView stickerView = (StickerView) viewLayerRelativeLayout.getChildAt(y);
                listAllSticker.add(GetAllStickerDataModel.getInstance().getStickerData(stickerView, false, videoInfo));
            }
        }
        nowChooseSubLayerAnimList.clear();
        //?????????????????????
        AnimType animType = listAllAnima.get(position).getAnimType();
        //??????????????????,???????????????????????????,??????????????????????????????????????????
        if (targetStickerView == null) {
            //?????????????????????
            int nowChooseStickerPosition = viewLayerRelativeLayout.getChildCount() - 1;
            targetStickerView = (StickerView) viewLayerRelativeLayout.getChildAt(nowChooseStickerPosition);
        }

        if (targetStickerView != null) {
            if (isClearAllAnim) {
                //????????????,????????????????????????
                ToastUtil.showToast("??????????????????");
                for (int y = 0; y < viewLayerRelativeLayout.getChildCount(); y++) {
                    ((StickerView) viewLayerRelativeLayout.getChildAt(y)).setChooseAnimId(AnimType.NULL);
                }
                deleteSubLayerSticker();
                stopAllAnim();
            } else {
                if (animCollect.getAnimNeedSubLayerCount(listAllAnima.get(position).getAnimType()) > 0) {
                    for (int x = 1; x <= animCollect.getAnimNeedSubLayerCount(listAllAnima.get(position).getAnimType()); x++) {
                        //????????????????????????????????????????????????????????????????????????????????????nowChooseSubLayerAnimList?????????????????????
                        LogUtil.d("startPlayAnim", "????????????????????????id???" + targetStickerView.getId());
                        if (!TextUtils.isEmpty(targetStickerView.getClipPath())) {
                            //gif ?????????????????????
                            copyGif(targetStickerView.getClipPath(), targetStickerView.getResPath(), targetStickerView.getComeFrom(), targetStickerView, targetStickerView.getOriginalPath(), true, targetStickerView.getDownStickerTitle());
                        } else {
                            copyGif(targetStickerView.getResPath(), targetStickerView.getResPath(), targetStickerView.getComeFrom(), targetStickerView, targetStickerView.getOriginalPath(), true, targetStickerView.getDownStickerTitle());
                        }
                        if (x == animCollect.getAnimNeedSubLayerCount(listAllAnima.get(position).getAnimType())) {
                            ArrayList<StickerView> list = new ArrayList<>();
                            LogUtil.d("OOM", "sublayerListPosition" + sublayerListPosition);
                            list.addAll(nowChooseSubLayerAnimList);
                            sublayerListForBitmapLayer.put(sublayerListPosition, list);
                            StartAnimModel startAnimModel = new StartAnimModel(animCollect);
                            targetStickerView.setChooseAnimId(animType);
                            delayedToStartAnim(startAnimModel, animType, targetStickerView, sublayerListPosition, isFromPreview);
                            sublayerListPosition++;
                        }
                    }
                } else {
                    StartAnimModel startAnimModel = new StartAnimModel(animCollect);
                    targetStickerView.setChooseAnimId(animType);
                    delayedToStartAnim(startAnimModel, animType, targetStickerView, sublayerListPosition, isFromPreview);
                }
            }
        } else {
            if (!isFromPreview) {
                WaitingDialog.closeProgressDialog();
            }
        }


    }

    /**
     * description ????????????????????????????????????????????????????????????????????????
     * creation date: 2020/6/3
     * user : zhangtongju
     */
    private void delayedToStartAnim(StartAnimModel startAnimModel, AnimType animType, StickerView finalTargetStickerView, final int position, boolean isFromPreview) {

        new Handler().postDelayed(() -> {
            //?????????gif ????????????gif??????
            ArrayList<StickerView> list = null;
            finalTargetStickerView.start();
            if (sublayerListForBitmapLayer != null) {
                list = sublayerListForBitmapLayer.get(position);
                if (list != null) {
                    for (StickerView stickerView : list
                    ) {
                        stickerView.start();
                    }
                }
            }
            if (sublayerListForBitmapLayer != null) {
                previewCount++;
                startAnimModel.toStart(animType, finalTargetStickerView, sublayerListForBitmapLayer.get(position));
            } else {
                startAnimModel.toStart(animType, finalTargetStickerView, null);
            }
            if (previewCount == hasAnimCount) {
                callback.animIsComplate();
            }
            if (!isFromPreview) {
                WaitingDialog.closeProgressDialog();
            }
        }, 1500);


    }


    /**
     * description ?????????????????????  type 0 ??????????????????1?????????
     * creation date: 2020/9/21
     * user : zhangtongju
     */
    public void ChangeTextStyle(String path, int type, String title) {
        if (nowChooseStickerView.getIsTextSticker()) {
            if (type == 0) {
                nowChooseStickerView.setTextBitmapStyle(path, title);
            } else {
                nowChooseStickerView.setTextStyle(path, title);
            }
        }
    }


    /**
     * description ???????????????gif??????
     * creation date: 2020/5/22
     * param :  getResPath ???????????????path  isFromAubum ?????????????????? stickerView ????????? OriginalPath ???????????? isFromShowAnim ?????????????????????????????????
     * user : zhangtongju
     */
    private void copyGif(String getResPath, String path, boolean isFromAubum, StickerView stickerView, String OriginalPath, boolean isFromShowAnim, String title) {

        if (stickerView != null && stickerView.getIsTextSticker()) {
            addSticker("", false, false, false, "", true, stickerView, isFromShowAnim, StickerView.CODE_STICKER_TYPE_TEXT, null);
        } else {

            try {
                String copyName = null;
                if (getResPath.endsWith(".gif")) {
                    if (UiStep.isFromDownBj) {
                        StatisticsEventAffair.getInstance().setFlag(context, "5_mb_sticker_plus");
                    } else {
                        StatisticsEventAffair.getInstance().setFlag(context, "6_mb_sticker_plus");
                    }
                    copyName = mGifFolder + File.separator + System.currentTimeMillis() + "synthetic.gif";
                    String finalCopyName = copyName;
                    FileUtil.copyFile(new File(getResPath), copyName, new FileUtil.copySucceed() {
                        @Override
                        public void isSucceed() {

                            if (stickerView == null) {
                                addSticker(finalCopyName, false, false, isFromAubum, getResPath, true, null, isFromShowAnim, StickerView.CODE_STICKER_TYPE_NORMAL, title);
                            } else {
                                addSticker(finalCopyName, false, false, isFromAubum, getResPath, true, stickerView, isFromShowAnim, StickerView.CODE_STICKER_TYPE_NORMAL, stickerView.getDownStickerTitle());
                            }

                        }
                    });
                } else {
                    if (UiStep.isFromDownBj) {
                        StatisticsEventAffair.getInstance().setFlag(context, "5_mb_bj_plus one");
                    } else {
                        StatisticsEventAffair.getInstance().setFlag(context, "6_customize_bj_plus one");
                    }
                    String aa = path.substring(path.length() - 4);
                    copyName = mImageCopyFolder + File.separator + System.currentTimeMillis() + aa;
                    String finalCopyName1 = copyName;
                    FileUtil.copyFile(new File(path), copyName, new FileUtil.copySucceed() {
                        @Override
                        public void isSucceed() {
                            addSticker(getResPath, false, isFromAubum, isFromAubum, OriginalPath, true, stickerView, isFromShowAnim, StickerView.CODE_STICKER_TYPE_NORMAL, null);
                        }
                    });
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private int stickerViewID;
    private boolean isIntoDragMove = false;

    private void addSticker(String path, boolean isFirstAdd, boolean hasReplace, boolean isFromAubum, String originalPath,
                            boolean isCopy, StickerView copyStickerView, boolean isFromShowAnim, int stickerType, String title) {
        closeAllAnim();
        StickerView stickView = new StickerView(BaseApplication.getInstance(), stickerType);
        stickerViewID++;
        stickView.setId(stickerViewID);
        stickView.setOnitemClickListener(new StickerItemOnitemclick() {
            @Override
            public void stickerOnclick(int type) {
                if (type == StickerView.LEFT_TOP_MODE) {//??????
                    if (stickView.getIsTextSticker()) {
                        callback.hideTextDialog();
                    }
                    deleteStickView(stickView);

                } else if (type == StickerView.RIGHT_TOP_MODE) {
                    stickView.dismissFrame();
                    //copy
                    copyGif(stickView.getResPath(), path, stickView.getComeFrom(), stickView, stickView.getOriginalPath(), false, title);
                    if (!TextUtils.isEmpty(stickView.getOriginalPath())) {
                        if (AlbumType.isVideo(GetPathType.getInstance().getMediaType(stickView.getOriginalPath()))) {
                            if (UiStep.isFromDownBj) {
                                StatisticsEventAffair.getInstance().setFlag(context, "7_plusone");
                            } else {
                                StatisticsEventAffair.getInstance().setFlag(context, "8_plusone");
                            }
                        }
                    }
                } else if (type == StickerView.RIGHT_CENTER_MODE) {
                    showVibrator();
                    if (!stickView.isOpenVoice) {
                        //????????????
                        stickView.setOpenVoice(true);
                        stickView.setRightCenterBitmapForChangeIcon(ContextCompat.getDrawable(context, R.mipmap.sticker_open_voice));
                        getVideoVoice(stickView.getOriginalPath(), soundFolder);
                        if (UiStep.isFromDownBj) {
                            StatisticsEventAffair.getInstance().setFlag(context, "7_open");
                        } else {
                            StatisticsEventAffair.getInstance().setFlag(context, "8_open");
                        }
                    } else {
                        //????????????
                        videoVoicePath = "";
                        stickView.setOpenVoice(false);
                        stickView.setRightCenterBitmapForChangeIcon(ContextCompat.getDrawable(context, R.mipmap.sticker_close_voice));
//                        callback.getBgmPath("");
                        if (UiStep.isFromDownBj) {
                            StatisticsEventAffair.getInstance().setFlag(context, "7_turnoff");
                        } else {
                            StatisticsEventAffair.getInstance().setFlag(context, "8_turnoff");
                        }

                    }

                } else if (type == StickerView.LEFT_BOTTOM_MODE) {

                    if (!stickView.getIsTextSticker()) {
                        if (UiStep.isFromDownBj) {
                            StatisticsEventAffair.getInstance().setFlag(context, " 5_mb_bj_replace");
                        } else {
                            StatisticsEventAffair.getInstance().setFlag(context, " 6_customize_bj_replace");
                        }
                        //????????????
                        AlbumManager.chooseAlbum(context, 1, 0, (tag, paths, isCancel, isFromCamera, albumFileList) -> {
                            if (!isCancel) {
                                if (AlbumType.isVideo(GetPathType.getInstance().getPathType(paths.get(0)))) {
                                    GetVideoCover getVideoCover = new GetVideoCover(context);
                                    getVideoCover.getCover(paths.get(0), path1 -> {
                                        Observable.just(path1).subscribeOn(AndroidSchedulers.mainThread()).subscribe(s -> {
                                            stickView.setOriginalPath(paths.get(0));
                                            stickView.setClipPath(s);
                                            if (!isCheckedMatting) {
                                                stickView.changeImage(paths.get(0), false);
                                            } else {
                                                stickView.changeImage(s, false);
                                            }


                                            if (stickView.isFirstAddSticker()) {
                                                stickView.setRightCenterBitmap(ContextCompat.getDrawable(context, R.mipmap.sticker_close_voice));
//                                            callback.changFirstVideoSticker(paths.get(0));
//                                            callback.getBgmPath("");
                                            }

                                        });
                                    });
                                } else {
                                    CompressionCuttingManage manage = new CompressionCuttingManage(context, "", tailorPaths -> {
                                        Observable.just(tailorPaths.get(0)).subscribeOn(AndroidSchedulers.mainThread()).subscribe(s -> {
                                            stickView.setOriginalPath(paths.get(0));
                                            stickView.setClipPath(s);
                                            if (!isCheckedMatting) {
                                                stickView.changeImage(paths.get(0), false);
                                            } else {
                                                stickView.changeImage(s, false);
                                            }
                                        });
                                    });
                                    manage.toMatting(paths);

                                    if (stickView.isFirstAddSticker()) {
                                        if (stickView.isOpenVoice()) {
                                            stickView.setOpenVoice(false);
                                            stickView.setRightCenterBitmap(ContextCompat.getDrawable(context, R.mipmap.sticker_close_voice));
//                                        callback.getBgmPath("");
                                        }
                                    }

                                }
                            }
                        }, "");
                    } else {
                        callback.showTextDialog(nowChooseStickerView.getStickerText());
                    }

                } else if (type == StickerView.ONCLICK_MODE) {
                    if (stickView.getIsTextSticker()) {
                        callback.showTextDialog(nowChooseStickerView.getStickerText());
                    }
                }
            }

            @Override
            public void stickerMove() {
                //??????????????????
                stopAllAnim();
                closeAllAnim();
                deleteSubLayerSticker();
//                new Handler().postDelayed(() -> deleteSubLayerSticker(), 200);
                if (stickView.getParent() != null) {
                    ViewGroup vp = (ViewGroup) stickView.getParent();
                    if (vp != null) {
                        vp.removeView(stickView);
                    }
                }

                callback.needPauseVideo();
                viewLayerRelativeLayout.addView(stickView);
                stickView.start();

                nowChooseStickerView = stickView;
                callback.stickerOnclickCallback(stickView.getStickerText());

            }
        });

        stickView.setOnItemDragListener(new StickerItemOnDragListener() {
            @Override
            public void stickerDragMove() {
                isIntoDragMove = true;
                stopAllAnim();
            }

            @Override
            public void stickerDragUp() {
                if (isIntoDragMove && stickView.getChooseAnimId() != null && stickView.getChooseAnimId() != AnimType.NULL) {
                    startTimer(stickView);
                }
                isIntoDragMove = false;
//                if(stickView.isFirstAddSticker()){
//                    //??????????????????
//                    callback.showMusicBtn(true);
//                }else{
//                    callback.showMusicBtn(false);
//                }


                if (!stickView.getIsTextSticker()) {
                    callback.hideKeyBord();
                }
                nowChooseStickerView = stickView;

            }
        });
        stickView.setRightTopBitmap(ContextCompat.getDrawable(context, R.mipmap.sticker_copy));
        stickView.setLeftTopBitmap(ContextCompat.getDrawable(context, R.drawable.sticker_delete));
        stickView.setRightBottomBitmap(ContextCompat.getDrawable(context, R.mipmap.sticker_redact));
        if (stickerType != StickerView.CODE_STICKER_TYPE_TEXT) {
            stickView.setRightBitmap(ContextCompat.getDrawable(context, R.mipmap.sticker_updown));
        }

        stickView.setComeFromAlbum(isFromAubum);
        if (isFromAubum) {
            stickView.setClipPath(path);
            stickView.setOriginalPath(originalPath);
            stickView.setNowMaterialIsVideo(AlbumType
                    .isVideo(GetPathType.getInstance().getPathType(stickView.getOriginalPath())));
            stickView.setIsmaterial(true);
        } else {
            stickView.setIsmaterial(false);
        }
        if (isFirstAdd) {
            nowChooseStickerView = stickView;
            stickView.setFirstAddSticker(true);
//            if (AlbumType.isVideo(GetPathType.getInstance().getPathType(stickView.getOriginalPath()))) {
//                LogUtil.d("OOM", "mVideoPath=" + mVideoPath);
//                if (!TextUtils.isEmpty(mVideoPath)) {
//                    LogUtil.d("OOM", "??????????????????");
//                    //???????????????
//                    stickView.setRightCenterBitmap(context.getDrawable(R.mipmap.sticker_close_voice));
//                    callback.getBgmPath("");
//                    stickView.setOpenVoice(false);
//                } else {
//                    LogUtil.d("OOM", "?????????????????????");
//                    //???????????????
//                    stickView.setRightCenterBitmap(context.getDrawable(R.mipmap.sticker_open_voice));
//                    stickView.setOpenVoice(true);
//                    getVideoVoice(stickView.getOriginalPath(), soundFolder);
//                }
//            }
        }
        if (hasReplace) {
            stickView.setLeftBottomBitmap(ContextCompat.getDrawable(context, R.mipmap.sticker_change));
        }

        if (stickerType == StickerView.CODE_STICKER_TYPE_TEXT) {
            stickView.setLeftBottomBitmap(ContextCompat.getDrawable(context, R.mipmap.shader_edit));
            nowChooseStickerView = stickView;
            if (!isCopy) {
                new Handler().postDelayed(stickView::setIntoCenter, 500);
            }
        }
        if (isCopy && copyStickerView != null) {
            if (copyStickerView.getIsTextSticker()) {
                //???????????????????????????
                if (copyStickerView.getIsChooseTextBjEffect()) {
                    if (copyStickerView.getOpenThePattern()) {
                        //???????????????
                        stickView.changeTextFrame(copyStickerView.getTypefaceBitmapPath(), copyStickerView.getBjFramePath(), copyStickerView.getTextFrameTitle());
                    } else {
                        if (!TextUtils.isEmpty(copyStickerView.getTypefaceBitmapPath())) {
                            stickView.setTextBitmapStyle(copyStickerView.getTypefaceBitmapPath(), copyStickerView.getTextEffectTitle());
                        }
                    }
                } else {
                    ArrayList<String> colors = copyStickerView.getTextColors();
                    if (copyStickerView.getOpenThePattern()) {
                        nowChooseStickerView.changeTextFrame(colors.get(0), colors.get(1), copyStickerView.getTextEffectTitle());
                    } else {
                        stickView.setTextPaintColor(colors.get(0), colors.get(1), copyStickerView.getTextEffectTitle());
                    }
                }
                if (!TextUtils.isEmpty(copyStickerView.getTypefacePath())) {
                    stickView.setTextStyle(copyStickerView.getTypefacePath(), copyStickerView.getTextStyleTitle());
                }
                stickView.setStickerText(copyStickerView.getStickerText());
                stickView.setTextAngle(copyStickerView.getRotateAngle());
                stickView.setScale(copyStickerView.getCopyScale());
                stickView.setCenter(copyStickerView.getCenterXAdd30(), copyStickerView.getCenterYAdd30());
            } else {
                //?????????????????????????????????????????????item
                StickerView.isFromCopy fromCopy = new StickerView.isFromCopy();
                fromCopy.setScale(copyStickerView.getScale());
                LogUtil.d("OOM", "isCopy=Scale" + copyStickerView.getScale());
                fromCopy.setDegree(copyStickerView.getRotateAngle());
                fromCopy.setRightOffsetPercent(copyStickerView.getRightOffsetPercent());
                if (isFromShowAnim) {
                    fromCopy.setTranX(copyStickerView.getCenterX());
                    fromCopy.setTranY(copyStickerView.getCenterY());
                } else {
                    fromCopy.setTranX(copyStickerView.getCenterXAdd30());
                    fromCopy.setTranY(copyStickerView.getCenterYAdd30());
                }
                stickView.setImageRes(path, false, fromCopy);
                stickView.showFrame();

            }
        } else {
            stickView.setImageRes(path, true, null);
        }
        AnimStickerModel animStickerModel = new AnimStickerModel(context, viewLayerRelativeLayout, stickView);

        //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        if (isFromAubum && !isCheckedMatting) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    stickView.changeImage(originalPath, false);
                }
            }, 500);
        }

        if (isFromAubum && isCopy && isCheckedMatting) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    stickView.changeImage(path, false);
                }
            }, 500);
        }

        listForStickerModel.add(animStickerModel);
        if (stickView.getParent() != null) {
            ViewGroup vp = (ViewGroup) stickView.getParent();
            if (vp != null) {
                vp.removeAllViews();
            }
        }
        viewLayerRelativeLayout.addView(stickView);
//        if (isFirstAdd) {
//            callback.isFirstAddSuccess();
//        }
        if (isFromShowAnim) {
            stickView.setIsfromAnim(true);
            nowChooseSubLayerAnimList.add(stickView);
        }
    }


    /**
     * description ???????????????
     * creation date: 2020/9/21
     * user : zhangtongju
     */
    public void changeTextLabe(String text) {
        if (nowChooseStickerView != null && nowChooseStickerView.getIsTextSticker()) {
            if (TextUtils.isEmpty(text)) {
                deleteStickView(nowChooseStickerView);
            } else {
                nowChooseStickerView.setStickerText(text);
            }
        }
    }

    /**
     * description ????????????????????????????????????????????????
     * creation date: 2020/4/23
     * user : zhangtongju
     */
    private void getVideoVoice(String videoPath, String outputPath) {
        if (!isDestroy) {
            WaitingDialog.openPragressDialog(context);
//        new Thread(() -> {
            mediaManager manager = new mediaManager(context);
            manager.splitMp4(videoPath, new File(outputPath), (isSuccess, putPath) -> {
                WaitingDialog.closeProgressDialog();
                if (isSuccess) {
                    LogUtil.d("OOM2", "??????????????????????????????" + outputPath);
                    videoVoicePath = outputPath + File.separator + "bgm.mp3";
//                callback.getBgmPath(videoVoicePath);
                } else {
                    LogUtil.d("OOM2", "??????????????????????????????null" + outputPath);
//                callback.getBgmPath("");
                    videoVoicePath = "";
                }
            });
//        }).start();
        }

    }


    private void showVibrator() {
        if (vibrator.hasVibrator()) {
            //??????????????????
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(5, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(5);
            }
        }
    }

    private void closeAllAnim() {
        //ArrayList<AllStickerData> list = new ArrayList<>();
        for (int i = 0; i < viewLayerRelativeLayout.getChildCount(); i++) {
            StickerView stickerView = (StickerView) viewLayerRelativeLayout.getChildAt(i);
            stickerView.pause();
        }
    }


    public String getKeepOutput() {
        String product = android.os.Build.MANUFACTURER; //??????????????????
        if ("vivo".equals(product)) {
            File file_camera = new File(Environment.getExternalStorageDirectory() + "/??????");
            if (file_camera.exists()) {
                return file_camera.getPath() + File.separator + System.currentTimeMillis() + "synthetic.mp4";
            }
        }
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        File path_Camera = new File(path + "/Camera");
        if (path_Camera.exists()) {
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + "Camera" + File.separator + System.currentTimeMillis() + "synthetic.mp4";
        }
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + System.currentTimeMillis() + "synthetic.mp4";
    }


    private void disMissStickerFrame() {
        for (int i = 0; i < viewLayerRelativeLayout.getChildCount(); i++) {
            StickerView stickerView = (StickerView) viewLayerRelativeLayout.getChildAt(i);
            if (stickerView.getIsTextSticker()) {
                stickerView.disMissFrame();
            }
        }
    }

    /**
     * description ???????????????????????????sdk????????????????????????
     * creation date: 2020/3/12
     * user : zhangtongju
     */

    private boolean isIntoSaveVideo = false;
    private float percentageH;
    //??????????????????
    private int cutSuccessNum;
    private ArrayList<String> cutList = new ArrayList<>();

    public void toSaveVideo(float percentageH) {
        disMissStickerFrame();
        stopAllAnim();
        this.percentageH = percentageH;
        deleteSubLayerSticker();
        if (viewLayerRelativeLayout.getChildCount() == 0) {
            saveToAlbum(mVideoPath);
//            String keepPath = getKeepOutput();
//            try {
//                FileUtil.copyFile(new File(keepPath), keepPath);
//                saveToAlbum(keepPath);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }


        } else {

            new Handler().postDelayed(() -> {
                if (!isIntoSaveVideo) {
                    isIntoSaveVideo = true;
                    listAllSticker.clear();
                    cutSuccessNum = 0;
                    cutVideoPathList.clear();
                    backgroundDraw = new BackgroundDraw(context, mVideoPath, videoVoicePath, "", 0, 0, 0, new BackgroundDraw.saveCallback() {
                        @Override
                        public void saveSuccessPath(String path, int progress) {
                            if (!isDestroy) {
                                if (!TextUtils.isEmpty(path)) {
                                    mLoadingDialog.dismiss();
//                                    try {
//                                        String keepPath = getKeepOutput();
//                                        FileUtil.copyFile(new File(path), keepPath);
                                    statisticsEventAffair();
                                    saveToAlbum(path);
                                    Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> new Handler().postDelayed(() -> isIntoSaveVideo = false, 500));
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }

                                } else {
                                    if (progress == 10000) {
                                        isIntoSaveVideo = false;
                                        //????????????
                                        mLoadingDialog.dismiss();
                                    } else {
                                        dialogProgress = progress;
                                        handler.sendEmptyMessage(1);
                                    }
                                }
                            }
                        }
                    }, animCollect, false);


                    for (int i = 0; i < viewLayerRelativeLayout.getChildCount(); i++) {
                        StickerView stickerView = (StickerView) viewLayerRelativeLayout.getChildAt(i);
                        listAllSticker.add(GetAllStickerDataModel.getInstance().getStickerData(stickerView, false, videoInfo));

                        if (!TextUtils.isEmpty(stickerView.getDownStickerTitle())) {
                            StatisticsEventAffair.getInstance().setFlag(context, "11_yj_Sticker", stickerView.getDownStickerTitle());
                        }

                    }

                    if (listAllSticker.size() == 0) {
                        isIntoSaveVideo = false;
                        Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> new Handler().post(() -> Toast.makeText(context, "??????????????????", Toast.LENGTH_SHORT).show()));
                        return;
                    }

                    for (int i = 0; i < listAllSticker.size(); i++) {
                        if (defaultVideoDuration < listAllSticker.get(i).getDuration()) {
                            defaultVideoDuration = (int) listAllSticker.get(i).getDuration();
                        }
                        if (listAllSticker.get(i).isVideo()) {
                            cutVideoPathList.add(new videoType(listAllSticker.get(i).getOriginalPath(), i, listAllSticker.get(i).getDuration()));
                        }
                    }
                    if (cutVideoPathList.size() == 0) {
                        mLoadingDialog.show();
                        //?????????????????????????????????????????????
                        backgroundDraw.toSaveVideo(listAllSticker, false, false, percentageH);
                    } else {
                        mLoadingDialog.show();
                        cutList.clear();
                        if (videoInfo != null) {
                            cutVideo(cutVideoPathList.get(0), videoInfo.getDuration(), cutVideoPathList.get(0).getDuration(), false);
                        } else {
                            //???????????????????????????10???
                            cutVideo(cutVideoPathList.get(0), defaultVideoDuration, cutVideoPathList.get(0).getDuration(), false);
                        }
                    }
                }
            }, 200);
        }
    }


    private void statisticsEventAffair() {

        ArrayList<String> titleEffect = GetAllStickerDataModel.getInstance().GettitleEffect();
        ArrayList<String> titleStyle = GetAllStickerDataModel.getInstance().GetTitleStyle();
        ArrayList<String> titleFrame = GetAllStickerDataModel.getInstance().GetTitleFrame();
        if (titleEffect != null && titleEffect.size() > 0) {

            for (String str : titleEffect
            ) {
                StatisticsEventAffair.getInstance().setFlag(context, "20_mb_text_style_save", str);
                LogUtil.d("OOM3", "titleEffect=" + str);
            }
        }


        if (titleStyle != null && titleStyle.size() > 0) {

            for (String str : titleStyle
            ) {
                StatisticsEventAffair.getInstance().setFlag(context, "20_mb_text_font_save", str);
                LogUtil.d("OOM3", "titleStyle=" + str);
            }
        }


        if (titleFrame != null && titleFrame.size() > 0) {

            for (String str : titleFrame
            ) {
                StatisticsEventAffair.getInstance().setFlag(context, "20_mb_text_border_save", str);
                LogUtil.d("OOM3", "titleFrame=" + str);
            }
        }


        if ((titleStyle != null && titleStyle.size() > 0) || (titleEffect != null && titleEffect.size() > 0)) {
            StatisticsEventAffair.getInstance().setFlag(context, "20_mb_text_save_save");
        }


    }

    private void showMessageDialog() {
        StatisticsEventAffair.getInstance().setFlag(context, "video_ad_alert", "");
        CommonMessageDialog.getBuilder(context)
                .setContentView(R.layout.dialog_common_message_ad_under)
                .setAdStatus(CommonMessageDialog.AD_STATUS_BOTTOM)
                .setAdId(AdConfigs.AD_IMAGE_DIALOG_OPEN_VIDEO)
                .setTitle("???????????????")
                .setMessage("??????????????????????????????")
                .setMessage2("???????????????????????????????????????")
                .setPositiveButton("?????????????????????")
                .setNegativeButton("??????")
                .setDialogBtnClickListener(new CommonMessageDialog.DialogBtnClickListener() {
                    @Override
                    public void onPositiveBtnClick(CommonMessageDialog dialog) {
                        StatisticsEventAffair.getInstance().setFlag(context, "bj_ad_open", "");
                        StatisticsEventAffair.getInstance().setFlag(context, "video_ad_alert_click_confirm");
//                        EventBus.getDefault().post(new showAdCallback("PreviewActivity"));
                        dialog.dismiss();
                        callback.showAdCallback();
                    }

                    @Override
                    public void onCancelBtnClick(CommonMessageDialog dialog) {
                        //??????
                        StatisticsEventAffair.getInstance().setFlag(context, "bj_ad_cancel", "");
                        StatisticsEventAffair.getInstance().setFlag(context, "video_ad_alert_click_cancel");
                        dialog.dismiss();
                    }
                })
                .build().show();
    }

    /**
     * description ??????????????????
     * creation date: 2020/9/8
     * user : zhangtongju
     */
    private void saveToAlbum(String path) {
        StimulateControlManage.getInstance().InitRefreshStimulate();
        outputPathForVideoSaveToPhoto = path;
        if (!CheckVipOrAdUtils.checkIsVip() && BaseConstans.getHasAdvertising() == 1 && BaseConstans.getIncentiveVideo() &&
                !BaseConstans.getIsNewUser() && BaseConstans.getSave_video_ad() &&
                !BaseConstans.TemplateHasWatchingAd) {
            showMessageDialog();
        } else {
            try {
                if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
                    AdManager.getInstance().showCpAd(context, AdConfigs.AD_SCREEN_FOR_keep);
                }
                String keepPath = getKeepOutput();
                FileUtil.copyFile(new File(path), keepPath);
                LogUtil.d("OOM", "??????????????????" + keepPath);
                albumBroadcast(keepPath);
                showDialog(keepPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String outputPathForVideoSaveToPhoto;

    public void alertAlbumUpdate(boolean isSuccess) {
        if (!isSuccess) {
            if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
                AdManager.getInstance().showCpAd(context, AdConfigs.AD_SCREEN_FOR_keep);
            }
        }
        try {
            String keepPath = getKeepOutput();
            FileUtil.copyFile(new File(outputPathForVideoSaveToPhoto), keepPath);
            albumBroadcast(keepPath);
            showDialog(keepPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * description ?????????????????????
     * date: ???2019/8/16 14:24
     * author: ????????? @?????? jutongzhang@sina.com
     */
    private void albumBroadcast(String outputFile) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(new File(outputFile)));
        context.sendBroadcast(intent);
    }


    private void showDialog(String path) {
        if (!com.flyingeffects.com.commonlyModel.DoubleClick.getInstance().isFastDoubleClick()) {
            ShowPraiseModel.keepAlbumCount();
            keepAlbumCount();
            LogUtil.d("showDialog", "showDialog");
            mShareDialog.createDialog(templateTitle);
            mShareDialog.setVideoPath(path);
        }
    }


    private void keepAlbumCount() {
        int num = Hawk.get("keepAlbumNum");
        num++;
        Hawk.put("keepAlbumNum", num);
    }

    @Override
    public void addSticker(String stickerPath, String title) {
        addSticker(stickerPath, false, false, false, null, false, null, false, StickerView.CODE_STICKER_TYPE_NORMAL, title);
    }

    @Override
    public void copyGif(String fileName, String copyName, String title) {
        copyGif(fileName, copyName, false, null, fileName, false, title);
    }

    @Override
    public void clickItemSelected(int position) {
        showAllAnim(false);
        callback.needPauseVideo();
    }

    class videoType {

        videoType(String path, int position, long duration) {
            this.path = path;
            this.duration = duration;
            this.position = position;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        String path;

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        int position;

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        long duration;
    }

    /**
     * description ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * creation date: 2020/4/21
     * user : zhangtongju
     */
    private void cutVideo(videoType videoType, long duration, long materialDuration, boolean nowUiIsLandscape) {
        LogUtil.d("oom3", "????????????????????????" + materialDuration);
        videoCutDurationForVideoOneDo.getInstance().cutVideoForDrawPadAllExecute2(context, false, materialDuration, videoType.getPath(), 0, new videoCutDurationForVideoOneDo.isSuccess() {
            @Override
            public void progresss(int progress) {
                float positionF = progress / (float) 100;
                Log.d("OOM", "???????????????????????????" + positionF);
                float prencent = 5 / (float) (cutVideoPathList.size() + 1);
                Log.d("OOM", "???????????????" + prencent);
                int position = (int) ((int) (positionF * prencent) + cutSuccessNum * prencent);
                Log.d("OOM", "?????????????????????" + position);
                dialogProgress = position;
                handler.sendEmptyMessage(1);
            }

            @Override
            public void isSuccess(boolean isSuccess, String path) {
                LogUtil.d("OOM", "?????????????????????" + path);
                int position = videoType.getPosition();
                cutList.add(path);
                AllStickerData sticker = listAllSticker.get(position);
                statisticsAnim();
                sticker.setPath(path);
                cutSuccessNum++;
                if (cutSuccessNum == cutVideoPathList.size()) {
                    backgroundDraw.toSaveVideo(listAllSticker, false, nowUiIsLandscape, percentageH);
                } else {
                    if (videoInfo != null) {
                        cutVideo(cutVideoPathList.get(cutSuccessNum), videoInfo.getDuration(), cutVideoPathList.get(cutSuccessNum).getDuration(), nowUiIsLandscape);
                    } else {
                        cutVideo(cutVideoPathList.get(cutSuccessNum), defaultVideoDuration, cutVideoPathList.get(cutSuccessNum).getDuration(), nowUiIsLandscape);
                    }
                }
            }
        });
    }


    /**
     * description ?????????????????????
     * creation date: 2020/7/14
     * user : zhangtongju
     */
    private void statisticsAnim() {


        for (AllStickerData data : listAllSticker
        ) {
            if (data.getChooseAnimId() != null && data.getChooseAnimId() != AnimType.NULL) {

                if (data.isMaterial()) {
                    StatisticsEventAffair.getInstance().setFlag(context, "9_Animation", data.getChooseAnimId().name());
                } else {
                    StatisticsEventAffair.getInstance().setFlag(context, "9_Animation3", data.getChooseAnimId().name());
                }

            }
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String title = "???????????????";
            String content;
            if (dialogProgress <= 25) {
                content = "??????????????? ????????????";
            } else if (dialogProgress <= 40) {
                content = "???????????????????????????";
            } else if (dialogProgress <= 60) {
                content = "??????????????????????????????";
            } else if (dialogProgress <= 80) {
                content = "???????????????????????????";
            } else {
                content = "???????????????????????????";
            }
            mLoadingDialog.setTitleStr(title);
            mLoadingDialog.setContentStr(content);
            mLoadingDialog.setProgress(dialogProgress);

        }
    };


    public void addTextSticker() {
        addSticker("", false, false, false, "", false, null, false, StickerView.CODE_STICKER_TYPE_TEXT, null);
    }


}

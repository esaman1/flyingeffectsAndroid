package com.flyingeffects.com.ui.model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import androidx.collection.SparseArrayCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.TemplateGridViewAdapter;
import com.flyingeffects.com.adapter.TemplateGridViewAnimAdapter;
import com.flyingeffects.com.adapter.TemplateViewPager;
import com.flyingeffects.com.adapter.listViewForVideoThumbAdapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.commonlyModel.GetPathType;
import com.flyingeffects.com.commonlyModel.GetVideoCover;
import com.flyingeffects.com.commonlyModel.getVideoInfo;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.constans.UiStep;
import com.flyingeffects.com.enity.AllStickerData;
import com.flyingeffects.com.enity.StickerAnim;
import com.flyingeffects.com.enity.StickerList;
import com.flyingeffects.com.enity.VideoInfo;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.AdConfigs;
import com.flyingeffects.com.manager.AdManager;
import com.flyingeffects.com.manager.AlbumManager;
import com.flyingeffects.com.manager.CompressionCuttingManage;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.manager.StimulateControlManage;
import com.flyingeffects.com.manager.mediaManager;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.model.TemplateAddStickerMvpCallback;
import com.flyingeffects.com.ui.view.activity.AdHintActivity;
import com.flyingeffects.com.utils.FileUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.ToastUtil;
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
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shixing.sxve.ui.albumType;
import com.shixing.sxve.ui.view.WaitingDialog;
import com.shixing.sxve.ui.view.WaitingDialogProgressNowAnim;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static com.flyingeffects.com.manager.FileManager.saveBitmapToPath;


public class TemplateAddStickerMvpModel {
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
     * 视频默认声音
     */
    private String videoVoicePath;
    private String soundFolder;
    private int selectPage = 1;
    private int perPageCount = 20;
    private SmartRefreshLayout smartRefreshLayout;
    private boolean isRefresh = true;
    private List<StickerList> listForSticker = new ArrayList<>();
    private TemplateGridViewAdapter gridAdapter;
    private ViewPager viewPager;
    private backgroundDraw backgroundDraw;
    /**
     * 默认视频时长,如果没选择背景的时候会用到
     */
    private int defaultVideoDuration = 0;
    private WaitingDialogProgressNowAnim dialog;
    private ArrayList<StickerView> needDeleteList = new ArrayList<>();
    TemplateGridViewAnimAdapter templateGridViewAnimAdapter;
    private int dialogProgress;
    private List<View> listForInitBottom = new ArrayList<>();
    /**
     * 需要裁剪视频的集合
     */
    private ArrayList<videoType> cutVideoPathList = new ArrayList<>();
    private HorizontalListView hListView;

    /***
     * originalPath  初始化第一张的时长
     */
    private String originalPath;


    public TemplateAddStickerMvpModel(Context context, TemplateAddStickerMvpCallback callback, ViewLayerRelativeLayout viewLayerRelativeLayout, String mVideoPath) {
        this.context = context;
        this.callback = callback;
        this.viewLayerRelativeLayout = viewLayerRelativeLayout;
        this.mVideoPath = mVideoPath;
        dialog = new WaitingDialogProgressNowAnim(context);
        this.originalPath = mVideoPath;
        FileManager fileManager = new FileManager();
        mImageCopyFolder = fileManager.getFileCachePath(context, "imageCopy");
        videoInfo = getVideoInfo.getInstance().getRingDuring(mVideoPath);
        mGifFolder = fileManager.getFileCachePath(context, "gifFolder");
        soundFolder = fileManager.getFileCachePath(context, "soundFolder");
    }


    //    private TimelineAdapter mTimelineAdapter;
    private int mScrollX;
    private LinearLayoutManager linearLayoutManager;

    public void initVideoProgressView(HorizontalListView hListView) {
        this.hListView = hListView;
        //动态设置距离左边的位置
        if (videoInfo != null) {
            initSingleThumbSize(videoInfo.getVideoWidth(), videoInfo.getVideoHeight(), videoInfo.getDuration(), videoInfo.getDuration() / 2, mVideoPath);
        } else {
            getPlayVideoDuration();
            initSingleThumbSize(720, 1280, defaultVideoDuration, defaultVideoDuration / 2, "");
        }
    }


    private List<Integer> perSticker = new ArrayList<>();

    private void getPlayVideoDuration() {
        defaultVideoDuration = 0;
        LogUtil.d("OOM", " viewLayerRelativeLayout.getChildCount())=" + viewLayerRelativeLayout.getChildCount());
        perSticker.clear();
        if (viewLayerRelativeLayout.getChildCount() > 0) {
            for (int i = 0; i < viewLayerRelativeLayout.getChildCount(); i++) {
                StickerView stickerView = (StickerView) viewLayerRelativeLayout.getChildAt(i);
                if (!TextUtils.isEmpty(stickerView.getOriginalPath())) {
                    if (albumType.isVideo(GetPathType.getInstance().getPathType(stickerView.getOriginalPath()))) {
                        VideoInfo materialVideoInfo = getVideoInfo.getInstance().getRingDuring(stickerView.getOriginalPath());
                        LogUtil.d("OOM", "materialVideoInfo.getDuration()=" + materialVideoInfo.getDuration());
                        perSticker.add(materialVideoInfo.getDuration());
                    }
                }
            }
        } else {
            //只有第一次初始化的时候，可能为0.因为viewLayerRelativeLayout还没加载进入数据，所有就需要手动加上
            if (!TextUtils.isEmpty(originalPath)) {
                if (albumType.isVideo(GetPathType.getInstance().getPathType(originalPath))) {
                    VideoInfo materialVideoInfo = getVideoInfo.getInstance().getRingDuring(originalPath);
                    LogUtil.d("OOM", "materialVideoInfo.getDuration()=" + materialVideoInfo.getDuration());
                    perSticker.add(materialVideoInfo.getDuration());
                }
            }

        }
        //只有一个的情况就不需要比较大小了
        if (perSticker != null && perSticker.size() > 0) {
            if (perSticker.size() == 1) {
                defaultVideoDuration = perSticker.get(0);
            } else {
                for (int duration : perSticker
                ) {
                    if (defaultVideoDuration < duration) {
                        defaultVideoDuration = duration;
                    }
                }
            }
            LogUtil.d("OOM", "获得贴纸时长为" + defaultVideoDuration);
        } else {
            LogUtil.d("OOM", "获得贴纸时长失败");
            defaultVideoDuration = 10 * 1000;
        }

//        callback.showRenderVideoTime(defaultVideoDuration);
    }


    private int mTotalWidth;

    private void initSingleThumbSize(int width, int height, float duration, float mTemplateDuration, String mVideoPath) {
        // 需要截取的listWidth宽度
        int listWidth = hListView.getWidth() - hListView.getPaddingLeft() - hListView.getPaddingRight();
        int listHeight = hListView.getHeight();
        float scale = (float) listHeight / height;
        int thumbWidth = (int) (scale * width);
        //其中listWidth表示当前截取的大小
        int thumbCount = (int) (listWidth * (duration / mTemplateDuration) / thumbWidth);
        thumbCount = thumbCount > 0 ? thumbCount : 0;
        //每帧所占的时间
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


    public void ChangeTextColor(String color0, String color1) {
        if (nowChooseStickerView.getIsTextSticker()) {
            nowChooseStickerView.setTextPaintColor(color0, color1);
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
        deleteAllSticker();
    }


    public void requestStickersList(boolean isShowDialog) {
        HashMap<String, String> params = new HashMap<>();
        params.put("page", selectPage + "");
        params.put("pageSize", perPageCount + "");
        // 启动时间
        Observable ob = Api.getDefault().getStickerslist(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<ArrayList<StickerList>>(context) {
            @Override
            protected void _onError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(ArrayList<StickerList> list) {
                finishData();
                if (isRefresh) {
                    listForSticker.clear();
                    StickerList item1 = new StickerList();
                    item1.setClearSticker(true);
                    listForSticker.add(item1);
                }

                if (!isRefresh && list.size() < perPageCount) {  //因为可能默认只请求8条数据
                    ToastUtil.showToast(context.getResources().getString(R.string.no_more_data));
                }
                if (list.size() < perPageCount) {
                    smartRefreshLayout.setEnableLoadMore(false);
                }

                listForSticker.addAll(list);
                modificationAllData(list);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, isShowDialog);
    }


    private void modificationAllData(ArrayList<StickerList> list) {
        for (int i = 0; i < list.size(); i++) {
            String fileName = mGifFolder + File.separator + list.get(i).getId() + ".gif";
            File file = new File(fileName);
            if (file.exists()) {
                StickerList item1 = list.get(i);
                item1.setIsDownload(1);
                list.set(i, item1);
            }
        }
        gridAdapter.notifyDataSetChanged();
    }


    public void initBottomLayout(ViewPager viewPager) {
        this.viewPager = viewPager;
        View templateThumbView = LayoutInflater.from(context).inflate(R.layout.view_template_paster, viewPager, false);
        smartRefreshLayout = templateThumbView.findViewById(R.id.smart_refresh_layout);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            isRefresh = true;
            refreshLayout.setEnableLoadMore(true);
            selectPage = 1;
            requestStickersList(true);
        });
        smartRefreshLayout.setOnLoadMoreListener(refresh -> {
            isRefresh = false;
            selectPage++;
            requestStickersList(false);
        });

        GridView gridView = templateThumbView.findViewById(R.id.gridView);
        gridView.setOnItemClickListener((adapterView, view, i, l) -> {
            if (!DoubleClick.getInstance().isFastZDYDoubleClick(1000)) {

                showAllAnim(false);
                callback.needPauseVideo();
                modificationSingleItemIsChecked(i);
                if (i == 0) {
                    //删除选择的帖子
                    stopAllAnim();
                    closeAllAnim();
                    deleteAllSticker();
                    if (UiStep.isFromDownBj) {
                        statisticsEventAffair.getInstance().setFlag(context, " 5_mb_bj_Stickeroff");
                    } else {
                        statisticsEventAffair.getInstance().setFlag(context, " 6_customize_bj_Stickeroff");
                    }
                } else {
                    if (UiStep.isFromDownBj) {
                        statisticsEventAffair.getInstance().setFlag(context, " 5_mb_bj_Sticker", listForSticker.get(i).getTitle());
                    } else {
                        statisticsEventAffair.getInstance().setFlag(context, " 6_customize_bj_Sticker", listForSticker.get(i).getTitle());
                    }
                    downSticker(listForSticker.get(i).getImage(), listForSticker.get(i).getId(), i);
                }
            }

        });
        gridAdapter = new TemplateGridViewAdapter(listForSticker, context);
        gridView.setAdapter(gridAdapter);

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

    /**
     * 下载帖子功能
     *
     * @param path     下载地址
     * @param imageId  gif 保存的图片id
     * @param position 当前点击的那个item ，主要用来更新数据
     */
    private void downSticker(String path, String imageId, int position) {
        if (!isDestroy) {
            WaitingDialog.openPragressDialog(context);
            if (path.endsWith(".gif")) {
//            String finalPath = path;
                String format = path.substring(path.length() - 4);
                String fileName = mGifFolder + File.separator + imageId + format;
                File file = new File(fileName);
                if (file.exists()) {
                    //如果已经下载了，就用已经下载的，但是如果已经展示了，就不能复用，需要类似于复制功能，只针对gif
                    if (nowStickerHasChoose(imageId, path)) {
                        String copyName = mGifFolder + File.separator + System.currentTimeMillis() + format;
                        copyGif(fileName, copyName, false, null, fileName, false);
                        WaitingDialog.closePragressDialog();
                        return;
                    } else {
                        addSticker(fileName, false, false, false, null, false, null, false, false);
                        WaitingDialog.closePragressDialog();
                        return;
                    }

                }
                Observable.just(path).map(s -> {
                    File file1 = null;
                    try {
                        file1 = Glide.with(context)
                                .load(path)
                                .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                .get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return file1;
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(path1 -> {
                    try {
                        if (path1 != null) {
                            FileUtil.copyFile(path1, fileName);
                            addSticker(fileName, false, false, false, null, false, null, false, false);
                            WaitingDialog.closePragressDialog();
                            modificationSingleItem(position);
                        } else {
                            WaitingDialog.closePragressDialog();
                            ToastUtil.showToast("请重试");
                        }

                    } catch (IOException e) {
                        WaitingDialog.closePragressDialog();
                        e.printStackTrace();
                    }
                });

            } else {
                new Thread(() -> {
                    Bitmap originalBitmap = null;
                    FutureTarget<Bitmap> futureTarget =
                            Glide.with(BaseApplication.getInstance())
                                    .asBitmap()
                                    .load(path)
                                    .submit();
                    try {
                        originalBitmap = futureTarget.get();
                        Bitmap finalOriginalBitmap = originalBitmap;
                        Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
                            WaitingDialog.closePragressDialog();
                            String aa = path.substring(path.length() - 4);
                            String copyName = mGifFolder + File.separator + System.currentTimeMillis() + aa;
                            saveBitmapToPath(finalOriginalBitmap, copyName, isSucceed -> {
                                modificationSingleItem(position);
                                addSticker(copyName, false, false, false, null, false, null, false, false);
                            });
                        });
                    } catch (Exception e) {
                        LogUtil.d("oom", e.getMessage());
                    }
                    Glide.with(BaseApplication.getInstance()).clear(futureTarget);
                }).start();
            }
        }

    }


    /**
     * 当前的item 是否已经被选中上了预览页面
     */
    private boolean nowStickerHasChoose(String id, String imagePath) {
        for (int i = 0; i < viewLayerRelativeLayout.getChildCount(); i++) {
            StickerView stickerView = (StickerView) viewLayerRelativeLayout.getChildAt(i);
            String path = stickerView.getResPath();
            String format = imagePath.substring(imagePath.length() - 4);
            if (imagePath.endsWith(".gif")) {
                String copyName = mGifFolder + File.separator + id + format;
                if (path.equals(copyName)) {
                    return true;
                }
            }
        }

        return false;

    }


    private void modificationSingleAnimItemIsChecked(int position) {
        for (StickerAnim item : listAllAnima
        ) {
            item.setChecked(false);
        }
        StickerAnim item1 = listAllAnima.get(position);
        item1.setChecked(true);
        listAllAnima.set(position, item1);//修改对应的元素
        templateGridViewAnimAdapter.notifyDataSetChanged();
    }

    private void modificationSingleItem(int position) {
        StickerList item1 = listForSticker.get(position);
        item1.setIsDownload(1);
        listForSticker.set(position, item1);//修改对应的元素
        gridAdapter.notifyDataSetChanged();
    }

    private void modificationSingleItemIsChecked(int position) {
        for (StickerList item : listForSticker
        ) {
            item.setChecked(false);
        }
        StickerList item1 = listForSticker.get(position);
        item1.setChecked(true);
        listForSticker.set(position, item1);//修改对应的元素
        gridAdapter.notifyDataSetChanged();
    }

    private void deleteAllSticker() {
        needDeleteList.clear();
        if (listForStickerModel != null && listForStickerModel.size() > 0) {
            for (int i = 0; i < listForStickerModel.size(); i++) {
                StickerView stickerView = listForStickerModel.get(i).getStickerView();
                if (stickerView != null && !stickerView.getComeFrom()) {
                    needDeleteList.add(stickerView);
                }
            }
        }

        for (StickerView stickerView : needDeleteList
        ) {
            deleteStickView(stickerView);
        }
    }


    private void finishData() {
        smartRefreshLayout.finishRefresh();
        smartRefreshLayout.finishLoadMore();
    }


    /**
     * description ：显示全部动画，在预览的时候
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
        //删除动画贴纸

        Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
            deleteSubLayerSticker();
            stopAllAnim();
            //重新得到所有的贴纸列表
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
                            //最后一个的情况
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
     * description ：暂停全部动画
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
     * time：2018/10/15
     * describe:严防内存泄露
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
     * description ：删除动画的子贴纸
     * creation date: 2020/5/27
     * user : zhangtongju
     */
    private synchronized void deleteSubLayerSticker() {
        if (sublayerListForBitmapLayer != null && sublayerListForBitmapLayer.size() > 0) {
            for (int i = 0; i < sublayerListForBitmapLayer.size(); i++) {
                ArrayList<StickerView> nowChooseSubLayerAnimList = sublayerListForBitmapLayer.get(i);
                //删除动画贴纸
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
                stackView.stop();
                listForStickerModel.remove(i);
            }
        }
    }


    /**
     * description ：开始播放动画 ，如果来自预览，那么stickver
     * creation date: 2020/5/27
     *
     * @param position          动画的类型
     * @param targetStickerView 目标贴纸，如果为null ,那么目标贴纸为最上层的那个，这里的多久就是针对设置当个动画，如果不为null ,
     *                          那么动画就是针对预览页面，某个贴纸设置动画
     * @param isFromPreview     是否来自播放预览
     *                          user : zhangtongju
     */

    private synchronized void startPlayAnim(int position, boolean isClearAllAnim, StickerView targetStickerView, int intoPosition, boolean isFromPreview) {
        if (!isFromPreview) {
            stopAllAnim();
            deleteSubLayerSticker();
            sublayerListPosition = 0;
            //重新得到所有的贴纸列表
            listAllSticker.clear();
            for (int y = 0; y < viewLayerRelativeLayout.getChildCount(); y++) {
                StickerView stickerView = (StickerView) viewLayerRelativeLayout.getChildAt(y);
                listAllSticker.add(GetAllStickerDataModel.getInstance().getStickerData(stickerView, false, videoInfo));
            }
        }
        nowChooseSubLayerAnimList.clear();
        //选择的动画类型
        AnimType animType = listAllAnima.get(position).getAnimType();
        //得到目标贴纸,永远都是最顶上一个,如果有目标贴纸，就用目标贴纸
        if (targetStickerView == null) {
            //当前选中的贴纸
            int nowChooseStickerPosition = viewLayerRelativeLayout.getChildCount() - 1;
            targetStickerView = (StickerView) viewLayerRelativeLayout.getChildAt(nowChooseStickerPosition);
        }

        if (targetStickerView != null) {
            if (isClearAllAnim) {
                //贴纸还原,显示到之前的位置
                ToastUtil.showToast("清理全部动画");
                for (int y = 0; y < viewLayerRelativeLayout.getChildCount(); y++) {
                    ((StickerView) viewLayerRelativeLayout.getChildAt(y)).setChooseAnimId(AnimType.NULL);
                }
                deleteSubLayerSticker();
                stopAllAnim();
            } else {
                if (animCollect.getAnimNeedSubLayerCount(listAllAnima.get(position).getAnimType()) > 0) {
                    for (int x = 1; x <= animCollect.getAnimNeedSubLayerCount(listAllAnima.get(position).getAnimType()); x++) {
                        //通过动画属性得到需要分身的数量，然后复制出贴纸在数组里面nowChooseSubLayerAnimList，最后需要删除
                        LogUtil.d("startPlayAnim", "当前动画复制的主id为" + targetStickerView.getId());
                        if (!TextUtils.isEmpty(targetStickerView.getClipPath())) {
                            //gif 贴纸，没得抠图
                            copyGif(targetStickerView.getClipPath(), targetStickerView.getResPath(), targetStickerView.getComeFrom(), targetStickerView, targetStickerView.getOriginalPath(), true);
                        } else {
                            copyGif(targetStickerView.getResPath(), targetStickerView.getResPath(), targetStickerView.getComeFrom(), targetStickerView, targetStickerView.getOriginalPath(), true);
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
                WaitingDialog.closePragressDialog();
            }
        }


    }

    /**
     * description ：延迟开启动画，因为这里可能需要复制很多的子贴纸
     * creation date: 2020/6/3
     * user : zhangtongju
     */
    private void delayedToStartAnim(StartAnimModel startAnimModel, AnimType animType, StickerView finalTargetStickerView, final int position, boolean isFromPreview) {

        new Handler().postDelayed(() -> {
            //如果是gif 那么开启gif动画
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
                startAnimModel.ToStart(animType, finalTargetStickerView, sublayerListForBitmapLayer.get(position));
            } else {
                startAnimModel.ToStart(animType, finalTargetStickerView, null);
            }
            if (previewCount == hasAnimCount) {
                callback.animIsComplate();
            }
            if (!isFromPreview) {
                WaitingDialog.closePragressDialog();
            }
        }, 1500);


    }


    /**
     * description ：更换字体样式  type 0 是热门效果，1是字体
     * creation date: 2020/9/21
     * user : zhangtongju
     */
    public void ChangeTextStyle(String path, int type) {
        if (nowChooseStickerView.getIsTextSticker()) {
            if (type == 0) {
                nowChooseStickerView.setTextBitmapStyle(path);
            } else {
                nowChooseStickerView.setTextStyle(path);
            }
        }
    }


    /**
     * description ：复制一个gif出来
     * creation date: 2020/5/22
     * param :  getResPath 图片地址，path  isFromAubum 是否来自相册 stickerView 原贴纸 OriginalPath 原图地址 isFromShowAnim 是否是因为来自动画分身
     * user : zhangtongju
     */
    private void copyGif(String getResPath, String path, boolean isFromAubum, StickerView stickerView, String OriginalPath, boolean isFromShowAnim) {
        try {
            String copyName = null;
            if (getResPath.endsWith(".gif")) {
                if (UiStep.isFromDownBj) {
                    statisticsEventAffair.getInstance().setFlag(context, "5_mb_sticker_plus");
                } else {
                    statisticsEventAffair.getInstance().setFlag(context, "6_mb_sticker_plus");
                }
                copyName = mGifFolder + File.separator + System.currentTimeMillis() + "synthetic.gif";
                String finalCopyName = copyName;
                FileUtil.copyFile(new File(getResPath), copyName, new FileUtil.copySucceed() {
                    @Override
                    public void isSucceed() {
                        addSticker(finalCopyName, false, false, isFromAubum, getResPath, true, stickerView, isFromShowAnim, false);
                    }
                });
            } else {
                if (UiStep.isFromDownBj) {
                    statisticsEventAffair.getInstance().setFlag(context, "5_mb_bj_plus one");
                } else {
                    statisticsEventAffair.getInstance().setFlag(context, "6_customize_bj_plus one");
                }
                String aa = path.substring(path.length() - 4);
                copyName = mImageCopyFolder + File.separator + System.currentTimeMillis() + aa;
                String finalCopyName1 = copyName;
                FileUtil.copyFile(new File(path), copyName, new FileUtil.copySucceed() {
                    @Override
                    public void isSucceed() {
                        addSticker(getResPath, false, isFromAubum, isFromAubum, OriginalPath, true, stickerView, isFromShowAnim, false);
                    }
                });
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private int stickerViewID;
    private boolean isIntoDragMove = false;

    private void addSticker(String path, boolean isFirstAdd, boolean hasReplace, boolean isFromAubum, String originalPath, boolean isCopy, StickerView copyStickerView, boolean isFromShowAnim, boolean isText) {
        closeAllAnim();
        StickerView stickView = new StickerView(BaseApplication.getInstance(), isText);
        stickerViewID++;
        stickView.setId(stickerViewID);
        stickView.setOnitemClickListener(new StickerItemOnitemclick() {
            @Override
            public void stickerOnclick(int type) {
                if (type == StickerView.LEFT_TOP_MODE) {//刪除
                    deleteStickView(stickView);
                } else if (type == StickerView.RIGHT_TOP_MODE) {
                    stickView.dismissFrame();
                    //copy
                    copyGif(stickView.getResPath(), path, stickView.getComeFrom(), stickView, stickView.getOriginalPath(), false);
                    if (!TextUtils.isEmpty(stickView.getOriginalPath())) {
                        if (albumType.isVideo(GetPathType.getInstance().getMediaType(stickView.getOriginalPath()))) {
                            if (UiStep.isFromDownBj) {
                                statisticsEventAffair.getInstance().setFlag(context, "7_plusone");
                            } else {
                                statisticsEventAffair.getInstance().setFlag(context, "8_plusone");
                            }
                        }
                    }
                } else if (type == StickerView.RIGHT_CENTER_MODE) {
                    showVibrator();
                    if (!stickView.isOpenVoice) {
                        //打开声音
                        stickView.setOpenVoice(true);
                        stickView.setRightCenterBitmapForChangeIcon(ContextCompat.getDrawable(context, R.mipmap.sticker_open_voice));
                        getVideoVoice(stickView.getOriginalPath(), soundFolder);
                        if (UiStep.isFromDownBj) {
                            statisticsEventAffair.getInstance().setFlag(context, "7_open");
                        } else {
                            statisticsEventAffair.getInstance().setFlag(context, "8_open");
                        }
                    } else {
                        //关闭声音
                        videoVoicePath = "";
                        stickView.setOpenVoice(false);
                        stickView.setRightCenterBitmapForChangeIcon(ContextCompat.getDrawable(context, R.mipmap.sticker_close_voice));
//                        callback.getBgmPath("");
                        if (UiStep.isFromDownBj) {
                            statisticsEventAffair.getInstance().setFlag(context, "7_turnoff");
                        } else {
                            statisticsEventAffair.getInstance().setFlag(context, "8_turnoff");
                        }

                    }

                } else if (type == StickerView.LEFT_BOTTOM_MODE) {

                    if (!stickView.getIsTextSticker()) {
                        if (UiStep.isFromDownBj) {
                            statisticsEventAffair.getInstance().setFlag(context, " 5_mb_bj_replace");
                        } else {
                            statisticsEventAffair.getInstance().setFlag(context, " 6_customize_bj_replace");
                        }
                        //切換素材
                        AlbumManager.chooseAlbum(context, 1, 0, (tag, paths, isCancel, albumFileList) -> {
                            if (!isCancel) {
                                if (albumType.isVideo(GetPathType.getInstance().getPathType(paths.get(0)))) {
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
                                    manage.ToMatting(paths);

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
                //停止全部动画
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
//                    //显示音乐按钮
//                    callback.showMusicBtn(true);
//                }else{
//                    callback.showMusicBtn(false);
//                }
                nowChooseStickerView = stickView;


            }
        });
        stickView.setRightTopBitmap(ContextCompat.getDrawable(context, R.mipmap.sticker_copy));
        stickView.setLeftTopBitmap(ContextCompat.getDrawable(context, R.drawable.sticker_delete));
        stickView.setRightBottomBitmap(ContextCompat.getDrawable(context, R.mipmap.sticker_redact));
        stickView.setRightBitmap(ContextCompat.getDrawable(context, R.mipmap.sticker_updown));

        stickView.setIsFromStickerAnim(isFromShowAnim);
        stickView.setComeFromAlbum(isFromAubum);
        if (isFromAubum) {
            stickView.setClipPath(path);
            stickView.setOriginalPath(originalPath);
            if (albumType.isVideo(GetPathType.getInstance().getPathType(stickView.getOriginalPath()))) {
                stickView.setNowMaterialIsVideo(true);
            } else {
                stickView.setNowMaterialIsVideo(false);
            }
            stickView.setIsmaterial(true);
        } else {
            stickView.setIsmaterial(false);
        }
        if (isFirstAdd) {
            nowChooseStickerView = stickView;
            stickView.setFirstAddSticker(true);
//            if (albumType.isVideo(GetPathType.getInstance().getPathType(stickView.getOriginalPath()))) {
//                LogUtil.d("OOM", "mVideoPath=" + mVideoPath);
//                if (!TextUtils.isEmpty(mVideoPath)) {
//                    LogUtil.d("OOM", "默认是有背景");
//                    //有背景音乐
//                    stickView.setRightCenterBitmap(context.getDrawable(R.mipmap.sticker_close_voice));
//                    callback.getBgmPath("");
//                    stickView.setOpenVoice(false);
//                } else {
//                    LogUtil.d("OOM", "默认是没有背景");
//                    //无背景音乐
//                    stickView.setRightCenterBitmap(context.getDrawable(R.mipmap.sticker_open_voice));
//                    stickView.setOpenVoice(true);
//                    getVideoVoice(stickView.getOriginalPath(), soundFolder);
//                }
//            }
        }
        if (hasReplace) {
            stickView.setLeftBottomBitmap(context.getDrawable(R.mipmap.sticker_change));
        }

        if (isText) {
            stickView.setLeftBottomBitmap(ContextCompat.getDrawable(context, R.mipmap.shader_edit));
            nowChooseStickerView = stickView;
            new Handler().postDelayed(stickView::setIntoCenter, 500);
        }
        if (isCopy && copyStickerView != null) {
            //来做复制或者来自联系点击下面的item
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
        } else {
            stickView.setImageRes(path, true, null);
        }
        AnimStickerModel animStickerModel = new AnimStickerModel(context, viewLayerRelativeLayout, stickView);
        //如果关闭了原图的，并且是用户添加的，那么就关闭扣的图，不过每次都是默认抠图的
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
     * description ：更换文字
     * creation date: 2020/9/21
     * user : zhangtongju
     */
    public void ChangeTextLabe(String text) {
        if (nowChooseStickerView.getIsTextSticker()) {
            if (!TextUtils.isEmpty(text)) {
                nowChooseStickerView.setStickerText(text);
            } else {
                deleteStickView(nowChooseStickerView);
            }

        }
    }

    /**
     * description ：视频音视频分离，获得视频的声音
     * creation date: 2020/4/23
     * user : zhangtongju
     */
    private void getVideoVoice(String videoPath, String outputPath) {
        if (!isDestroy) {
            WaitingDialog.openPragressDialog(context);
//        new Thread(() -> {
            mediaManager manager = new mediaManager(context);
            manager.splitMp4(videoPath, new File(outputPath), (isSuccess, putPath) -> {
                WaitingDialog.closePragressDialog();
                if (isSuccess) {
                    LogUtil.d("OOM2", "分离出来的因为地址为" + outputPath);
                    videoVoicePath = outputPath + File.separator + "bgm.mp3";
//                callback.getBgmPath(videoVoicePath);
                } else {
                    LogUtil.d("OOM2", "分离出来的因为地址为null" + outputPath);
//                callback.getBgmPath("");
                    videoVoicePath = "";
                }
            });
//        }).start();
        }

    }


    private void showVibrator() {
        if (vibrator.hasVibrator()) {
            //设置手机振动
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
        String product = android.os.Build.MANUFACTURER; //获得手机厂商
        if (product != null && product.equals("vivo")) {
            File file_camera = new File(Environment.getExternalStorageDirectory() + "/相机");
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
     * description ：保存视频采用蓝松sdk提供的在保存功能
     * creation date: 2020/3/12
     * user : zhangtongju
     */

    private boolean isIntoSaveVideo = false;
    private float percentageH;
    //裁剪成功数量
    private int cutSuccessNum;
    private ArrayList<String> cutList = new ArrayList<>();

    public void toSaveVideo(float percentageH) {
        disMissStickerFrame();
        stopAllAnim();
        this.percentageH = percentageH;
        deleteSubLayerSticker();
        if (viewLayerRelativeLayout.getChildCount() == 0) {
            saveToAlbum(mVideoPath);
        } else {
            new Handler().postDelayed(() -> {
                if (!isIntoSaveVideo) {
                    isIntoSaveVideo = true;
                    listAllSticker.clear();
                    cutSuccessNum = 0;
                    cutVideoPathList.clear();
                    backgroundDraw = new backgroundDraw(context, mVideoPath, videoVoicePath, "", new backgroundDraw.saveCallback() {
                        @Override
                        public void saveSuccessPath(String path, int progress) {
                            if (!isDestroy) {
                                if (!TextUtils.isEmpty(path)) {
                                    dialog.closePragressDialog();
                                    String keepPath = getKeepOutput();
                                    try {
                                        FileUtil.copyFile(new File(path), keepPath);
                                        saveToAlbum(keepPath);
                                        Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> new Handler().postDelayed(() -> isIntoSaveVideo = false, 500));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    if (progress == 10000) {
                                        isIntoSaveVideo = false;
                                        //渲染失败
                                        dialog.closePragressDialog();
                                    } else {
                                        dialogProgress = progress;
                                        handler.sendEmptyMessage(1);
                                    }
                                }
                            }
                        }
                    }, animCollect);


                    for (int i = 0; i < viewLayerRelativeLayout.getChildCount(); i++) {
                        StickerView stickerView = (StickerView) viewLayerRelativeLayout.getChildAt(i);
                        listAllSticker.add(GetAllStickerDataModel.getInstance().getStickerData(stickerView, false, videoInfo));
                    }

                    if (listAllSticker.size() == 0) {
                        isIntoSaveVideo = false;
                        Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> new Handler().post(() -> Toast.makeText(context, "你未选择素材", Toast.LENGTH_SHORT).show()));
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
                        dialog.openProgressDialog();
                        //都不是视频的情况下，就直接渲染
                        backgroundDraw.toSaveVideo(listAllSticker, false, false, percentageH);
                    } else {
                        dialog.openProgressDialog();
                        cutList.clear();
                        if (videoInfo != null) {
                            cutVideo(cutVideoPathList.get(0), videoInfo.getDuration(), cutVideoPathList.get(0).getDuration(), false);
                        } else {
                            //没选择背景默认裁剪10秒
                            cutVideo(cutVideoPathList.get(0), defaultVideoDuration, cutVideoPathList.get(0).getDuration(), false);
                        }
                    }
                }
            }, 200);
        }
    }


    /**
     * description ：保存在相册
     * creation date: 2020/9/8
     * user : zhangtongju
     */
    private void saveToAlbum(String path) {
        StimulateControlManage.getInstance().InitRefreshStimulate();
        outputPathForVideoSaveToPhoto = path;
        if (BaseConstans.getHasAdvertising() == 1 && BaseConstans.getIncentiveVideo() && !BaseConstans.getIsNewUser() && BaseConstans.getSave_video_ad() && !BaseConstans.TemplateHasWatchingAd) {
            Intent intent = new Intent(context, AdHintActivity.class);
            intent.putExtra("from", "isFormPreviewVideo");
            intent.putExtra("templateTitle", "");
            context.startActivity(intent);
        } else {
            LogUtil.d("OOM", "保存的地址为" + path);
            albumBroadcast(path);
            showDialog(path);
            if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
                AdManager.getInstance().showCpAd(context, AdConfigs.AD_SCREEN_FOR_keep);
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
        albumBroadcast(outputPathForVideoSaveToPhoto);
        showDialog(outputPathForVideoSaveToPhoto);

    }


    /**
     * description ：通知相册更新
     * date: ：2019/8/16 14:24
     * author: 张同举 @邮箱 jutongzhang@sina.com
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
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    //去除黑边
                    new ContextThemeWrapper(context, R.style.Theme_Transparent));
            builder.setTitle(context.getString(R.string.notification));
//            builder.setMessage(context.getString(R.string.have_saved_to_sdcard) +
//                    "【" + path + context.getString(R.string.folder) + "】");

            builder.setMessage("已为你保存到相册,多多分享给友友\n" + "【" + path + context.getString(R.string.folder) + "】"
            );


            builder.setNegativeButton(context.getString(R.string.got_it), (dialog, which) -> {
                dialog.dismiss();
            });
            builder.setCancelable(true);
            Dialog mDialog = builder.show();
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
        }
    }


    private void keepAlbumCount() {
        int num = Hawk.get("keepAlbumNum");
        num++;
        Hawk.put("keepAlbumNum", num);
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
     * description ：裁剪视频，如果视频小于模板时长，那么就裁剪当前时长，如果视频大于当前模板时长，那么就裁剪模板时长
     * creation date: 2020/4/21
     * user : zhangtongju
     */
    private void cutVideo(videoType videoType, long duration, long materialDuration, boolean nowUiIsLandscape) {
        LogUtil.d("oom3", "需要裁剪的时长为" + materialDuration);
        videoCutDurationForVideoOneDo.getInstance().CutVideoForDrawPadAllExecute2(context, false, materialDuration, videoType.getPath(), 0, new videoCutDurationForVideoOneDo.isSuccess() {
            @Override
            public void progresss(int progress) {
                float positionF = progress / (float) 100;
                Log.d("OOM", "裁剪的进度百分比为" + positionF);
                float prencent = 5 / (float) (cutVideoPathList.size() + 1);
                Log.d("OOM", "裁剪有几份" + prencent);
                int position = (int) ((int) (positionF * prencent) + cutSuccessNum * prencent);
                Log.d("OOM", "裁剪的总进度为" + position);
                dialogProgress = position;
                handler.sendEmptyMessage(1);
            }

            @Override
            public void isSuccess(boolean isSuccess, String path) {
                LogUtil.d("OOM", "保存后的地址为" + path);
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
     * description ：统计贴纸动画
     * creation date: 2020/7/14
     * user : zhangtongju
     */
    private void statisticsAnim() {


        for (AllStickerData data : listAllSticker
        ) {
            if (data.getChooseAnimId() != null && data.getChooseAnimId() != AnimType.NULL) {

                if (data.isMaterial()) {
                    statisticsEventAffair.getInstance().setFlag(context, "9_Animation", data.getChooseAnimId().name());
                } else {
                    statisticsEventAffair.getInstance().setFlag(context, "9_Animation3", data.getChooseAnimId().name());
                }

            }
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (dialogProgress <= 25) {
                dialog.setProgress("正在保存中" + dialogProgress + "%\n" + "请耐心等待 不要离开");
            } else if (dialogProgress <= 40) {
                dialog.setProgress("正在保存中" + dialogProgress + "%\n" + "快了，友友稍等片刻");
            } else if (dialogProgress <= 60) {
                dialog.setProgress("正在保存中" + dialogProgress + "%\n" + "抠像太强大，即将生成");
            } else if (dialogProgress <= 80) {
                dialog.setProgress("正在保存中" + dialogProgress + "%\n" + "马上就好，不要离开");
            } else {
                dialog.setProgress("正在保存中" + dialogProgress + "%\n" + "最后合成中，请稍后");
            }
        }
    };


    public void addTextSticker() {
        addSticker("", false, false, false, "", false, null, false, true);
    }


}
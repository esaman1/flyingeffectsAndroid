package com.flyingeffects.com.ui.model;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.TemplateGridViewAnimAdapter;
import com.flyingeffects.com.adapter.TemplateViewPager;
import com.flyingeffects.com.adapter.home_vp_frg_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.commonlyModel.GetPathType;
import com.flyingeffects.com.commonlyModel.GetVideoCover;
import com.flyingeffects.com.commonlyModel.SaveAlbumPathModel;
import com.flyingeffects.com.commonlyModel.getVideoInfo;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.constans.UiStep;
import com.flyingeffects.com.enity.AllStickerData;
import com.flyingeffects.com.enity.FirstLevelTypeEntity;
import com.flyingeffects.com.enity.SecondaryTypeEntity;
import com.flyingeffects.com.enity.StickerAnim;
import com.flyingeffects.com.enity.StickerTypeEntity;
import com.flyingeffects.com.enity.VideoInfo;
import com.flyingeffects.com.enity.VideoType;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.AdConfigs;
import com.flyingeffects.com.manager.AdManager;
import com.flyingeffects.com.manager.AlbumManager;
import com.flyingeffects.com.manager.BitmapManager;
import com.flyingeffects.com.manager.CompressionCuttingManage;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.manager.mediaManager;
import com.flyingeffects.com.ui.interfaces.model.CreationTemplateMvpCallback;
import com.flyingeffects.com.ui.view.activity.ChooseMusicActivity;
import com.flyingeffects.com.ui.view.activity.CreationTemplateActivity;
import com.flyingeffects.com.ui.view.activity.CreationTemplatePreviewActivity;
import com.flyingeffects.com.ui.view.activity.DressUpPreviewActivity;
import com.flyingeffects.com.ui.view.fragment.CreationBackListFragment;
import com.flyingeffects.com.ui.view.fragment.CreationFrameFragment;
import com.flyingeffects.com.ui.view.fragment.StickerFragment;
import com.flyingeffects.com.utils.FileUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.ScreenCaptureUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.view.StickerView;
import com.flyingeffects.com.view.animations.CustomMove.AnimCollect;
import com.flyingeffects.com.view.animations.CustomMove.AnimType;
import com.flyingeffects.com.view.animations.CustomMove.StartAnimModel;
import com.flyingeffects.com.view.lansongCommendView.StickerItemOnDragListener;
import com.flyingeffects.com.view.lansongCommendView.StickerItemOnitemclick;
import com.glidebitmappool.GlideBitmapPool;
import com.lansosdk.box.ViewLayerRelativeLayout;
import com.shixing.sxve.ui.albumType;
import com.shixing.sxve.ui.view.WaitingDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import androidx.collection.SparseArrayCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;


/**
 * description ：使用蓝松的drawPadView来绘制页面，实现方式为一个主视频图层加上多个动态的mv图层+ 多个图片图层，最后渲染出来视频
 * creation date: 2020/3/12
 * param :
 * user : zhangtongju
 */
public class CreationTemplateMvpModel implements StickerFragment.StickerListener, CreationBackListFragment.BackChooseListener, CreationFrameFragment.FrameChooseListener {
    private static final String TAG = "CreationTemplateMvpMode";
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private CreationTemplateMvpCallback mCallback;
    private final Context mContext;
    private List<View> listForInitBottom = new ArrayList<>();
    private String mVideoPath;
    private ViewLayerRelativeLayout viewLayerRelativeLayout;
    private int nowChooseMusicId = 0;
    private ArrayList<AnimStickerModel> listForStickerModel = new ArrayList<>();
    private boolean isDestroy = false;
    private VideoInfo videoInfo;
    private String mGifFolder;
    private String soundFolder;
    private Vibrator vibrator;
    private String mImageCopyFolder;
    private boolean isCheckedMatting = true;

    View.OnClickListener tvMusicListener;

    /**
     * 当前添加的音乐路径
     */
    private String addChooseBjPath;
    /**
     * 需要裁剪视频的集合
     */
    private ArrayList<VideoType> cutVideoPathList = new ArrayList<>();
    private BackgroundDraw backgroundDraw;

    private ArrayList<AllStickerData> listAllSticker = new ArrayList<>();
    /**
     * 视频默认声音
     */
    private String videoVoicePath;
    /**
     * 是否抠图,true 抠图
     */
    private boolean isMatting = true;

    /**
     * 默认视频时长,如果没选择背景的时候会用到
     */
    private long defaultVideoDuration = 0;

    /***
     * originalPath  初始化第一张的时长
     */
    private String mOriginalPath;

    private StickerView nowChooseStickerView;

    /**
     * 所有动画集合
     */
    private ArrayList<StickerAnim> listAllAnima;
    private ArrayList<StickerView> nowChooseSubLayerAnimList = new ArrayList<>();
    private SparseArrayCompat<ArrayList<StickerView>> sublayerListForBitmapLayer = new SparseArrayCompat<>();

    private AnimCollect mAnimCollect;

    public CreationTemplateMvpModel(Context context, CreationTemplateMvpCallback callback, String mVideoPath, ViewLayerRelativeLayout viewLayerRelativeLayout, String originalPath) {
        this.mContext = context;
        this.mCallback = callback;
        this.mOriginalPath = originalPath;
        this.mVideoPath = mVideoPath;

        this.viewLayerRelativeLayout = viewLayerRelativeLayout;
        vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        if (!TextUtils.isEmpty(mVideoPath)) {
            videoInfo = getVideoInfo.getInstance().getRingDuring(mVideoPath);
        }

        FileManager fileManager = new FileManager();
        mGifFolder = fileManager.getFileCachePath(context, "gifFolder");
        soundFolder = fileManager.getFileCachePath(context, "soundFolder");
        mImageCopyFolder = fileManager.getFileCachePath(context, "imageCopy");
        mAnimCollect = new AnimCollect();
        listAllAnima = mAnimCollect.getAnimList();
    }

    /**
     * description ：更换字体样式  type 0 是热门效果，1是字体
     * creation date: 2020/9/21
     * user : zhangtongju
     */
    public void changeTextStyle(String path, int type, String title) {
        if (nowChooseStickerView.getIsTextSticker()) {
            if (type == 0) {
                nowChooseStickerView.setTextBitmapStyle(path, title);
            } else {
                nowChooseStickerView.setTextStyle(path, title);
            }
        }
    }


    public void onclickRelativeLayout() {
        stopAllAnim();
        deleteSubLayerSticker();
    }


    public void keepPicture(RelativeLayout relativeLayout) {
        for (int i = 0; i < viewLayerRelativeLayout.getChildCount(); i++) {
            StickerView stickerView = (StickerView) viewLayerRelativeLayout.getChildAt(i);
            if (stickerView.getIsTextSticker()) {
                stickerView.disMissFrame();
            }
        }
        ScreenCaptureUtil screenCaptureUtil = new ScreenCaptureUtil(BaseApplication.getInstance());
        String textImagePath = screenCaptureUtil.getFilePath(relativeLayout);
        Intent intent = new Intent(mContext, DressUpPreviewActivity.class);
        intent.putExtra("url", textImagePath);
        intent.putExtra("template_id", "");
        intent.putExtra("localImage", textImagePath);
        intent.putExtra("isSpecial", true);
        intent.putExtra("templateTitle", "");
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mContext.startActivity(intent);
    }


    private void showDialog(String path) {
        if (!com.flyingeffects.com.commonlyModel.DoubleClick.getInstance().isFastDoubleClick()) {
            ShowPraiseModel.keepAlbumCount();
            LogUtil.d("showDialog", "showDialog");
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    //去除黑边
                    new ContextThemeWrapper(mContext, R.style.Theme_Transparent));
            builder.setTitle(mContext.getString(R.string.notification));
            builder.setMessage("已为你保存到相册,多多分享给友友\n" + "【" + path + mContext.getString(R.string.folder) + "】"
            );
            builder.setNegativeButton(mContext.getString(R.string.got_it), (dialog, which) -> {
                dialog.dismiss();
            });
            builder.setCancelable(true);
            Dialog mDialog = builder.show();
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
        }
    }

    /**
     * description ：更换文字
     * creation date: 2020/9/21
     * user : zhangtongju
     */
    public void changeTextLabe(String text) {
        if (nowChooseStickerView.getIsTextSticker()) {
            if (TextUtils.isEmpty(text)) {
                deleteStickView(nowChooseStickerView, false);
            } else {
                nowChooseStickerView.setStickerText(text);
                mCallback.updateTimeLineSickerText(text, String.valueOf(nowChooseStickerView.getStickerNoIncludeAnimId()));
            }
        }
    }

    public void changeTextColor(String color0, String color1, String title) {
        if (nowChooseStickerView.getIsTextSticker()) {
            nowChooseStickerView.setTextPaintColor(color0, color1, title);
        }
    }


    /**
     * description ：textBjPath 文字背景路径，textFramePath 文字边框
     * creation date: 2020/10/23
     * user : zhangtongju
     */
    public void changeTextFrame(String textBjPath, String textFramePath, String frameTitle) {
        if (nowChooseStickerView.getIsTextSticker()) {
            nowChooseStickerView.changeTextFrame(textBjPath, textFramePath, frameTitle);
        }
    }


    public void changeTextFrame(String color0, String color1, String textFramePath, String frameTitle) {
        if (nowChooseStickerView.getIsTextSticker()) {
            nowChooseStickerView.changeTextFrame(color0, color1, textFramePath, frameTitle);
        }
    }


    private void showVibrator() {
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(5);  //设置手机振动
        }
    }

    /**
     * description ：单独设置背景视频，有可能用户第一次进来不选择背景视频
     * creation date: 2020/4/22
     * user : zhangtongju
     */
    public void setmVideoPath(String mVideoPath) {
        if (!TextUtils.isEmpty(mVideoPath)) {
            this.mVideoPath = mVideoPath;
            videoInfo = getVideoInfo.getInstance().getRingDuring(mVideoPath);
            if (TextUtils.isEmpty(videoVoicePath) || nowChooseMusicId == 0) {
                chooseTemplateMusic(true);
            }
        } else {
            if (nowChooseMusicId == 2) {
                clearCheckBox();
            }
            this.mVideoPath = null;
            videoInfo = null;
        }
    }


    public void checkedChanged(boolean isChecked) {
        this.isCheckedMatting = isChecked;
        mattingChange(isChecked);
        stopAllAnim();
        deleteSubLayerSticker();
        mCallback.needPauseVideo();
    }

    public void intoOnPause() {
        stopAllAnim();
        closeAllAnim();
        deleteSubLayerSticker();
//        new Handler().postDelayed(() -> deleteSubLayerSticker(), 200);
    }


    public void setAddChooseBjPath(String path) {
        addChooseBjPath = path;
        chooseAddChooseBjPath();
    }

    public void initStickerView(String imagePath, String originalPath) {
        new Handler().postDelayed(() -> addSticker(imagePath, true, true, true, originalPath, false, null, false, false, null), 500);
    }


    public void showGifAnim(boolean isShow) {
        if (listForStickerModel != null && listForStickerModel.size() > 0) {
            for (AnimStickerModel stickerModel : listForStickerModel) {
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


    public void chooseAnim(int pageNum) {
        mViewPager.setCurrentItem(pageNum);
        //   showAllAnim(false);
    }


    public void GetVideoCover(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        Bitmap mBitmap = retriever.getFrameAtTime(0);
        String fileName = mImageCopyFolder + File.separator + UUID.randomUUID() + ".png";
        BitmapManager.getInstance().saveBitmapToPath(mBitmap, fileName, isSuccess -> {
            CompressionCuttingManage manage = new CompressionCuttingManage(mContext, ""
                    , false, tailorPaths -> mCallback.getVideoCover(tailorPaths.get(0), path));
            List<String> mattingPath = new ArrayList<>();
            mattingPath.add(fileName);
            manage.toMatting(mattingPath);
            GlideBitmapPool.putBitmap(mBitmap);
        });
    }

    TemplateGridViewAnimAdapter templateGridViewAnimAdapter;

    private ViewPager mViewPager;

    ImageView check_box_0;
    ImageView check_box_1;
    ImageView check_box_2;
    ImageView check_box_3;

    TextView tv_0;
    TextView tv_1;
    TextView tv_2;
    TextView tv_3;

    public void initBottomLayout(ViewPager viewPager, FragmentManager fragmentManager, int from) {
        this.mViewPager = viewPager;

        if (from == CreationTemplateActivity.FROM_DRESS_UP_BACK_CODE) {
            initViewForChooseBack(fragmentManager);
            initViewForChooseFrame(fragmentManager);
            initViewForSticker(fragmentManager);

        } else {
            initViewForSticker(fragmentManager);
            initViewForChooseAnim();
            initViewForChooseMusic();
        }

        TemplateViewPager templateViewPager = new TemplateViewPager(listForInitBottom);
        viewPager.setAdapter(templateViewPager);

    }

    /**
     * 初始化选择相框view
     *
     * @param fragmentManager
     */
    private void initViewForChooseFrame(FragmentManager fragmentManager) {
        View frameView = LayoutInflater.from(mContext).inflate(R.layout.view_creation_frame, mViewPager, false);

        ViewPager frameViewPager = frameView.findViewById(R.id.view_pager);

        frameView.findViewById(R.id.iv_down_sticker).setOnClickListener(v -> mCallback.stickerFragmentClose());

        SlidingTabLayout frameTab = frameView.findViewById(R.id.tb_sticker);
        requestPhotoFrameList(frameViewPager, frameTab, fragmentManager);
        listForInitBottom.add(frameView);
    }

    /**
     * 初始化选择背景view
     *
     * @param fragmentManager
     */
    private void initViewForChooseBack(FragmentManager fragmentManager) {
        View backView = LayoutInflater.from(mContext).inflate(R.layout.view_creation_back, mViewPager, false);

        ViewPager backViewPager = backView.findViewById(R.id.view_pager);

        backView.findViewById(R.id.iv_down_sticker).setOnClickListener(v -> mCallback.stickerFragmentClose());
        SlidingTabLayout backTab = backView.findViewById(R.id.tb_sticker);
        requestBackList(backViewPager, backTab, fragmentManager);

        listForInitBottom.add(backView);
    }

    /**
     * 初始化贴纸列表view
     *
     * @param fragmentManager
     */
    private void initViewForSticker(FragmentManager fragmentManager) {
        View templateThumbView = LayoutInflater.from(mContext).inflate(R.layout.view_template_paster, mViewPager, false);
        ViewPager stickerViewPager = templateThumbView.findViewById(R.id.viewpager_sticker);
        templateThumbView.findViewById(R.id.iv_delete_sticker).setOnClickListener(v -> {
            stopAllAnim();
            closeAllAnim();
            deleteAllSticker();
            if (UiStep.isFromDownBj) {
                StatisticsEventAffair.getInstance().setFlag(mContext, " 5_mb_bj_Stickeroff");
            } else {
                StatisticsEventAffair.getInstance().setFlag(mContext, " 6_customize_bj_Stickeroff");
            }
        });

        templateThumbView.findViewById(R.id.iv_down_sticker).setOnClickListener(v -> mCallback.stickerFragmentClose());
        SlidingTabLayout stickerTab = templateThumbView.findViewById(R.id.tb_sticker);
        getStickerTypeList(fragmentManager, stickerViewPager, stickerTab);
        listForInitBottom.add(templateThumbView);
    }


    /**
     * 初始化选音乐页面
     */
    private void initViewForChooseMusic() {
        //添加音乐
        View viewForChooseMusic = LayoutInflater.from(mContext).inflate(R.layout.view_choose_music, mViewPager, false);
        TextView tvAddMusic = viewForChooseMusic.findViewById(R.id.tv_add_music);
        TextView tvDownMusic = viewForChooseMusic.findViewById(R.id.iv_down_music);

        tvDownMusic.setVisibility(View.VISIBLE);
        tvDownMusic.setOnClickListener(v -> mCallback.stickerFragmentClose());

        tvAddMusic.setOnClickListener(view -> {
            StatisticsEventAffair.getInstance().setFlag(mContext, "15_music_add");
            Intent intent = new Intent(mContext, ChooseMusicActivity.class);
            intent.putExtra("needDuration", getDuration());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(intent);
        });

        tv_0 = viewForChooseMusic.findViewById(R.id.tv_0);
        tv_1 = viewForChooseMusic.findViewById(R.id.tv_1);
        tv_2 = viewForChooseMusic.findViewById(R.id.tv_2);
        tv_3 = viewForChooseMusic.findViewById(R.id.tv_3);

        check_box_0 = viewForChooseMusic.findViewById(R.id.iv_check_box_0);
        check_box_1 = viewForChooseMusic.findViewById(R.id.iv_check_box_1);
        check_box_2 = viewForChooseMusic.findViewById(R.id.iv_check_box_2);
        check_box_3 = viewForChooseMusic.findViewById(R.id.iv_check_box_3);

        tv_1.setText("背景音乐");
        tv_2.setText("提取音乐");

        setOnViewClickListener();

        listForInitBottom.add(viewForChooseMusic);

        new Handler().postDelayed(() -> {
            if (!TextUtils.isEmpty(mVideoPath)) {
                LogUtil.d("OOM", "当前有背景");
                //模板音乐
                nowChooseMusicId = 2;
                chooseTemplateMusic(true);
                mCallback.chooseMusicIndex(1);
            } else if (albumType.isVideo(GetPathType.getInstance().getPathType(mOriginalPath))) {
                LogUtil.d("OOM", "当前素材是视频");
                nowChooseMusicId = 1;
                chooseMaterialMusic(mOriginalPath);
                mCallback.chooseMusicIndex(0);
            }
        }, 500);
    }

    /**
     * 初始化动画列表view
     */
    private void initViewForChooseAnim() {
        View viewForChooseAnim = LayoutInflater.from(mContext).inflate(R.layout.view_create_template_anim_creation, mViewPager, false);
        GridView gridViewAnim = viewForChooseAnim.findViewById(R.id.gridView_anim);
        TextView animTab = viewForChooseAnim.findViewById(R.id.tv_name_bj_head);
        animTab.setText("默认");
        animTab.setTextSize(17);

        viewForChooseAnim.findViewById(R.id.iv_down_anim).setOnClickListener(v -> mCallback.stickerFragmentClose());
        viewForChooseAnim.findViewById(R.id.iv_delete_anim).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.needPauseVideo();
                startPlayAnim(0, true, null, 0, false);
                StatisticsEventAffair.getInstance().setFlag(mContext, "9_Animation2");
                StatisticsEventAffair.getInstance().setFlag(mContext, "9_Animation4");
            }
        });

        gridViewAnim.setOnItemClickListener((adapterView, view, i, l) -> {
            if (!DoubleClick.getInstance().isFastZDYDoubleClick(1000)) {
                modificationSingleAnimItemIsChecked(i);
                mCallback.needPauseVideo();
                WaitingDialog.openPragressDialog(mContext);
                startPlayAnim(i, false, null, 0, false);
            }
        });
        templateGridViewAnimAdapter = new TemplateGridViewAnimAdapter(listAllAnima, mContext);
        gridViewAnim.setAdapter(templateGridViewAnimAdapter);

        listForInitBottom.add(viewForChooseAnim);
    }


    private void setOnViewClickListener() {
        tvMusicListener = view -> {

            switch (view.getId()) {
                case R.id.iv_check_box_0:
                case R.id.tv_0:

                    chooseMaterialMusic(nowChooseStickerView.getOriginalPath());
                    mCallback.chooseMusicIndex(0);
                    break;

                case R.id.tv_1:
                case R.id.iv_check_box_1:
                    mCallback.chooseMusicIndex(1);
                    chooseTemplateMusic(true);
                    break;

                case R.id.tv_2:
                case R.id.iv_check_box_2:
                    mCallback.chooseMusicIndex(2);
                    nowChooseMusicId = 3;
                    chooseAddChooseBjPath();
                    break;

                case R.id.iv_check_box_3:
                case R.id.tv_3:
                    mCallback.chooseMusicIndex(3);
                    clearCheckBox();
                    check_box_3.setImageResource(R.mipmap.template_btn_selected);
                    break;
                default:
                    break;
            }
        };

        tv_0.setOnClickListener(tvMusicListener);
        tv_1.setOnClickListener(tvMusicListener);
        tv_2.setOnClickListener(tvMusicListener);
        tv_3.setOnClickListener(tvMusicListener);

        check_box_0.setOnClickListener(tvMusicListener);
        check_box_1.setOnClickListener(tvMusicListener);
        check_box_2.setOnClickListener(tvMusicListener);
        check_box_3.setOnClickListener(tvMusicListener);
    }


    private void getStickerTypeList(FragmentManager fragmentManager, ViewPager stickerViewPager, SlidingTabLayout stickerTab) {
        HashMap<String, String> params = new HashMap<>();
        // 启动时间
        Observable ob = Api.getDefault().getStickerTypeList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<ArrayList<StickerTypeEntity>>(mContext) {
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
                    StickerFragment fragment = new StickerFragment();
                    fragment.setStickerListener(CreationTemplateMvpModel.this);
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

    /**
     * 请求背景
     *
     * @param backViewPager
     * @param backTab
     * @param fragmentManager
     */
    public void requestBackList(ViewPager backViewPager, SlidingTabLayout backTab, FragmentManager fragmentManager) {
        HashMap<String, String> params = new HashMap<>();
        //类型 1模板 2背景 3换脸  4 加上了最新的闪图
        params.put("type", "4");
        Observable ob = Api.getDefault().getCategoryList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<FirstLevelTypeEntity>>(mContext) {
            @Override
            protected void onSubError(String message) {
                LogUtil.e(TAG, message);
                ToastUtil.showToast("背景列表加载错误：" + message);
            }

            @Override
            protected void onSubNext(List<FirstLevelTypeEntity> data) {
                String dataStr = StringUtil.beanToJSONString(data);
                LogUtil.d(TAG, dataStr);

                List<Fragment> fragments = new ArrayList<>();

                for (int i = 0; i < data.size(); i++) {
                    if ("换背景".equals(data.get(i).getName())) {
                        List<SecondaryTypeEntity> categoryList = data.get(i).getCategory();
                        String[] titles = new String[categoryList.size()];
                        for (int j = 0; j < categoryList.size(); j++) {

                            titles[j] = categoryList.get(j).getName();
                            Bundle bundle = new Bundle();
                            bundle.putString("id", categoryList.get(j).getId());
                            CreationBackListFragment fragment = new CreationBackListFragment();
                            fragment.setBackChooseListener(CreationTemplateMvpModel.this);
                            fragment.setArguments(bundle);
                            fragments.add(fragment);
                        }
                        home_vp_frg_adapter vpFrgAdapter = new home_vp_frg_adapter(fragmentManager, fragments);
                        backViewPager.setOffscreenPageLimit(categoryList.size() - 1);
                        backViewPager.setAdapter(vpFrgAdapter);
                        backTab.setViewPager(backViewPager, titles);

                    }
                }
            }
        }, "mainData", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, true, true, false);

    }

    /**
     * 请求相框
     *
     * @param frameViewPager
     * @param backTab
     * @param fragmentManager
     */
    public void requestPhotoFrameList(ViewPager frameViewPager, SlidingTabLayout backTab, FragmentManager fragmentManager) {
        List<Fragment> fragments = new ArrayList<>();
        CreationFrameFragment fragment = new CreationFrameFragment();
        fragment.setFrameChooseListener(CreationTemplateMvpModel.this);
        String[] titles = {"相框"};
        fragments.add(fragment);
        home_vp_frg_adapter vpFrgAdapter = new home_vp_frg_adapter(fragmentManager, fragments);
        frameViewPager.setAdapter(vpFrgAdapter);
        backTab.setViewPager(frameViewPager, titles);
    }


    private long getDuration() {
        long duration = 0;
        if (!TextUtils.isEmpty(mVideoPath)) {
            duration = videoInfo.getDuration();
        } else {
            if (listAllSticker != null) {
                //说明没得背景视频，那么渲染时长就是
                for (AllStickerData data : listAllSticker) {
                    if (duration < (int) data.getDuration()) {
                        duration = (int) data.getDuration();
                    }
                }
                //如果还是0,说明全是图片，就修改为10
                if (duration == 0) {
                    duration = 10000;
                }
            }
        }
        return duration;
    }


    /**
     * description ：选择素材音乐
     * creation date: 2020/9/2
     * user : zhangtongju
     */
    private void chooseMaterialMusic(String path) {
        if (nowChooseStickerView != null) {
            if (albumType.isVideo(GetPathType.getInstance().getPathType(path))) {
                clearCheckBox();
                nowChooseMusicId = 1;
                check_box_0.setImageResource(R.mipmap.template_btn_selected);
                getVideoVoice(path, soundFolder);
            } else {
                ToastUtil.showToast("当前素材不是视频");
            }
        }
    }

    /**
     * description ：选择模板音乐
     * creation date: 2020/9/2
     * user : zhangtongju
     */
    private void chooseTemplateMusic(boolean isHint) {
        if (!TextUtils.isEmpty(mVideoPath)) {
            nowChooseMusicId = 2;
            clearCheckBox();
            check_box_1.setImageResource(R.mipmap.template_btn_selected);
            videoVoicePath = "";
            mCallback.getBgmPath("");
        } else {
            if (isHint) {
                ToastUtil.showToast("没有背景音乐");
            }
        }
    }


    private void chooseAddChooseBjPath() {
        if (!TextUtils.isEmpty(addChooseBjPath)) {
            clearCheckBox();
            check_box_2.setImageResource(R.mipmap.template_btn_selected);
            videoVoicePath = addChooseBjPath;
            mCallback.getBgmPath(addChooseBjPath);
        } else {
            ToastUtil.showToast("没有提取音乐");
        }
    }


    private void clearCheckBox() {
        nowChooseMusicId = 0;
        check_box_0.setImageResource(R.mipmap.template_btn_unselected);
        check_box_1.setImageResource(R.mipmap.template_btn_unselected);
        check_box_2.setImageResource(R.mipmap.template_btn_unselected);
        check_box_3.setImageResource(R.mipmap.template_btn_unselected);

    }


    private int previewCount;
    private int sublayerListPosition;

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
                listAllSticker.add(GetAllStickerDataModel.getInstance().getStickerData(stickerView, isMatting, videoInfo));
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
                if (mAnimCollect.getAnimNeedSubLayerCount(listAllAnima.get(position).getAnimType()) > 0) {
                    for (int x = 1; x <= mAnimCollect.getAnimNeedSubLayerCount(listAllAnima.get(position).getAnimType()); x++) {
                        //通过动画属性得到需要分身的数量，然后复制出贴纸在数组里面nowChooseSubLayerAnimList，最后需要删除
                        LogUtil.d("startPlayAnim", "当前动画复制的主id为" + targetStickerView.getId());
                        if (!TextUtils.isEmpty(targetStickerView.getClipPath())) {
                            //gif 贴纸，没得抠图
                            copyGif(targetStickerView.getClipPath(), targetStickerView.getResPath(), targetStickerView.getComeFrom(), targetStickerView, targetStickerView.getOriginalPath(), true, targetStickerView.getDownStickerTitle());
                        } else {
                            copyGif(targetStickerView.getResPath(), targetStickerView.getResPath(), targetStickerView.getComeFrom(), targetStickerView, targetStickerView.getOriginalPath(), true, targetStickerView.getDownStickerTitle());
                        }
                        if (x == mAnimCollect.getAnimNeedSubLayerCount(listAllAnima.get(position).getAnimType())) {
                            LogUtil.d("OOM", "sublayerListPosition" + sublayerListPosition);
                            ArrayList<StickerView> list = new ArrayList<>(nowChooseSubLayerAnimList);
                            sublayerListForBitmapLayer.put(sublayerListPosition, list);
                            StartAnimModel startAnimModel = new StartAnimModel(mAnimCollect);
                            targetStickerView.setChooseAnimId(animType);
                            delayedToStartAnim(startAnimModel, animType, targetStickerView, sublayerListPosition, isFromPreview);
                            sublayerListPosition++;
                        }
                    }
                } else {
                    StartAnimModel startAnimModel = new StartAnimModel(mAnimCollect);
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
     * description ：延迟开启动画，因为这里可能需要复制很多的子贴纸
     * creation date: 2020/6/3
     * user : zhangtongju
     */
    private void delayedToStartAnim(StartAnimModel startAnimModel, AnimType animType, StickerView finalTargetStickerView, final int position, boolean isFromPreview) {

        new Handler().postDelayed(() -> {
            //如果是gif 那么开启gif动画
            ArrayList<StickerView> list;
            finalTargetStickerView.start();
            if (sublayerListForBitmapLayer != null) {
                list = sublayerListForBitmapLayer.get(position);
                if (list != null) {
                    for (StickerView stickerView : list) {
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
                mCallback.animIsComplate();
            }
            if (!isFromPreview) {
                WaitingDialog.closeProgressDialog();
            }
        }, 1500);

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
                    for (StickerView stickerView : nowChooseSubLayerAnimList) {
                        deleteStickView(stickerView, true);
                    }
                }
            }
            sublayerListForBitmapLayer.clear();
        }
    }

    /**
     * description ：删除帖子(包括动画贴纸)
     * creation date: 2020/6/8
     * user : zhangtongju
     */
    private final ArrayList<StickerView> needDeleteList = new ArrayList<>();

    private void deleteAllSticker() {
        needDeleteList.clear();
        if (listForStickerModel != null && listForStickerModel.size() > 0) {
            for (int i = 0; i < listForStickerModel.size(); i++) {
                StickerView stickerView = listForStickerModel.get(i).getStickerView();
                if (stickerView != null && !stickerView.getComeFrom() && !stickerView.getIsTextSticker()) {
                    needDeleteList.add(stickerView);
                }
            }
        }

        for (StickerView stickerView : needDeleteList) {
            deleteStickView(stickerView, false);
        }

    }


    /*
     * @Author Zhangtj
     * @Date 2020/3/21
     * @Des 抠图和原图之间切换  isMatting 是否抠图
     */
    private void mattingChange(boolean isMatting) {
        this.isMatting = isMatting;
        if (listForStickerModel != null && listForStickerModel.size() > 0) {
            for (AnimStickerModel stickerModel : listForStickerModel) {
                StickerView stickerView = stickerModel.getStickerView();
                if (stickerView != null && stickerView.getComeFrom()) {
                    if (isMatting) {
                        LogUtil.d("OOM", "当前裁剪的地址为" + stickerView.getClipPath());
                        stickerView.mattingChange(stickerView.getClipPath());
                    } else {
                        stickerView.mattingChange(stickerView.getOriginalPath());
                    }
                }
            }
        }
    }


    /**
     * 当前的item 是否已经被选中上了预览页面
     *
     * @param id
     * @param imagePath
     * @return
     */
    private boolean nowStickerHasChoose(String id, String imagePath) {
        for (int i = 0; i < viewLayerRelativeLayout.getChildCount(); i++) {
            StickerView stickerView = (StickerView) viewLayerRelativeLayout.getChildAt(i);
            String path = stickerView.getResPath();
            if (!TextUtils.isEmpty(path)) {
                String format = imagePath.substring(imagePath.length() - 4);
                if (imagePath.endsWith(".gif")) {
                    String copyName = mGifFolder + File.separator + id + format;
                    if (path.equals(copyName)) {
                        return true;
                    }
                }
            }
        }
        return false;

    }


    /**
     * description ：添加一个新的sticker ,
     * creation date: 2020/3/23
     *
     * @param path         资源地址
     * @param hasReplace   是有有替换功能，目前替换功能只针对用户从相册里面选择的，
     * @param isFirstAdd    第一个贴纸
     * @param isFromAlbum  是否来自于相册选择的素材，而不是自己点击下载的，
     * @param originalPath 如果是相册选择的，没抠图的的地址，
     * @param isCopy       是否来自复制功能
     * @param isFromShowAnim   是否来自分身动画，如果是的话，不纳入最后的渲染
     * user : zhangtongju
     */

    private int stickerViewID = 0;
    /**
     * 不包含动画的id
     */
    private int stickerId = 0;
    private boolean isIntoDragMove = false;

    private void addSticker(String path, boolean isFirstAdd, boolean hasReplace, boolean isFromAlbum,
                            String originalPath, boolean isCopy, StickerView copyStickerView, boolean isFromShowAnim,
                            boolean isText, String title) {
        closeAllAnim();
        StickerView stickView = new StickerView(mContext, isText);
        stickView.setId(stickerViewID);

        setStickerOnItemClick(stickView);
        setSticerOnDraglistener(stickView);

        stickView.setRightTopBitmap(ContextCompat.getDrawable(mContext, R.mipmap.sticker_copy));
        stickView.setLeftTopBitmap(ContextCompat.getDrawable(mContext, R.drawable.sticker_delete));
        stickView.setRightBottomBitmap(ContextCompat.getDrawable(mContext, R.mipmap.sticker_redact));
        stickView.setIsFromStickerAnim(isFromShowAnim);
        stickView.setComeFromAlbum(isFromAlbum);
        if (isFromAlbum) {
            LogUtil.d("OOM2", "ClipPath=" + path);
            stickView.setClipPath(path);
            LogUtil.d("OOM2", "originalPath=" + originalPath);
            stickView.setOriginalPath(originalPath);
            stickView.setNowMaterialIsVideo(albumType.isVideo(GetPathType.getInstance()
                    .getPathType(stickView.getOriginalPath())));
            stickView.setIsmaterial(true);
        } else {
            stickView.setIsmaterial(false);
        }

        if (isFirstAdd) {
            nowChooseStickerView = stickView;
            stickView.setFirstAddSticker(true);
        }

        if (hasReplace) {
            stickView.setLeftBottomBitmap(ContextCompat.getDrawable(mContext, R.mipmap.sticker_change));
        }

        if (isText) {
            stickView.setLeftBottomBitmap(ContextCompat.getDrawable(mContext, R.mipmap.shader_edit));
            nowChooseStickerView = stickView;
            if (!isCopy) {
                new Handler().postDelayed(stickView::setIntoCenter, 500);
            }
        }

        if (title != null) {
            stickView.setDownStickerTitle(title);
        }

        if (isCopy && copyStickerView != null) {
            if (copyStickerView.getIsTextSticker()) {
                //是否是图片文字效果
                if (copyStickerView.getIsChooseTextBjEffect()) {
                    if (copyStickerView.getOpenThePattern()) {
                        //当前有边框
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
                //来做复制或者来自联系点击下面的item
                StickerView.isFromCopy fromCopy = new StickerView.isFromCopy();

                fromCopy.setScale(copyStickerView.getScale());

                LogUtil.d("OOM", "isCopy=Scale" + copyStickerView.getScale());
                fromCopy.setDegree(copyStickerView.getRotateAngle());
                fromCopy.setRightOffsetPercent(copyStickerView.getRightOffsetPercent());

                if (isFromShowAnim) {
                    if (isText) {
                        if (copyStickerView.getIsChooseTextBjEffect()) {
                            if (!TextUtils.isEmpty(copyStickerView.getTypefacePath())) {
                                stickView.setTextStyle(copyStickerView.getTypefacePath(), copyStickerView.getTextStyleTitle());
                            }
                            if (!TextUtils.isEmpty(copyStickerView.getTypefaceBitmapPath())) {
                                stickView.setTextBitmapStyle(copyStickerView.getTypefaceBitmapPath(), copyStickerView.getTextEffectTitle());
                            }
                        } else {
                            ArrayList<String> colors = copyStickerView.getTextColors();
                            stickView.setTextPaintColor(colors.get(0), colors.get(1), copyStickerView.getTextEffectTitle());
                        }
                        stickView.setStickerText(copyStickerView.getStickerText());
                        stickView.setTextAngle(copyStickerView.getRotateAngle());
                        stickView.setScale(copyStickerView.getScale());
                    } else {
                        fromCopy.setTranX(copyStickerView.getCenterX());
                        fromCopy.setTranY(copyStickerView.getCenterY());
                    }
                } else {
                    fromCopy.setTranX(copyStickerView.getCenterXAdd30());
                    fromCopy.setTranY(copyStickerView.getCenterYAdd30());
                }
                stickView.setImageRes(path, false, fromCopy);
                stickView.showFrame();
            }
            stickView.setShowStickerStartTime(copyStickerView.getShowStickerStartTime());
            stickView.setShowStickerEndTime(copyStickerView.getShowStickerEndTime());
        } else {
            stickView.setImageRes(path, true, null);
        }
        if (!isText) {
            stickView.setRightBitmap(ContextCompat.getDrawable(mContext, R.mipmap.sticker_updown));
            if (!stickView.getResPath().endsWith(".gif") && !albumType.isVideo(GetPathType.getInstance().getPathType(stickView.getOriginalPath()))) {
                stickView.setLeftBitmap(ContextCompat.getDrawable(mContext, R.mipmap.icon_pic_save));
            }
        }
        AnimStickerModel animStickerModel = new AnimStickerModel(mContext, viewLayerRelativeLayout, stickView);
        //如果关闭了原图的，并且是用户添加的，那么就关闭扣的图，不过每次都是默认抠图的
        if (isFromAlbum && !isCheckedMatting) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    stickView.changeImage(originalPath, false);
                }
            }, 500);
        }
        if (isFromAlbum && isCopy && isCheckedMatting) {
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

        if (!isFromShowAnim) {
            stickView.setStickerNoIncludeAnimId(stickerId);
            mCallback.addStickerTimeLine(String.valueOf(stickerId), isText, isText ? stickView.getStickerText() : "", stickView);
            stickerId++;
        }
        stickerViewID++;
        if (isFirstAdd) {
            mCallback.isFirstAddSuccess();
        }
        if (isFromShowAnim) {
            stickView.setIsfromAnim(true);
            nowChooseSubLayerAnimList.add(stickView);
        }
    }

    private void setSticerOnDraglistener(StickerView stickView) {

        stickView.setOnItemDragListener(new StickerItemOnDragListener() {
            @Override
            public void stickerDragMove() {
                isIntoDragMove = true;
                stopAllAnim();
            }

            @Override
            public void stickerDragUp() {
//                if (isIntoDragMove && stickView.getChooseAnimId() != null && stickView.getChooseAnimId() != AnimType.NULL) {
//                    startTimer(stickView);
//                }
                isIntoDragMove = false;
                //显示音乐按钮
                mCallback.showMusicBtn(stickView.isFirstAddSticker());
                if (!stickView.getIsTextSticker()) {
                    mCallback.hideKeyBord();
                }

                nowChooseStickerView = stickView;
            }
        });
    }

    /**
     * stickerView 的点击监听
     *
     * @param stickView
     */
    private void setStickerOnItemClick(StickerView stickView) {
        stickView.setOnitemClickListener(new StickerItemOnitemclick() {
            @Override
            public void stickerOnclick(int type) {
                if (type == StickerView.LEFT_TOP_MODE) {//刪除
                    if (stickView.isFirstAddSticker()) {
                        if (nowChooseMusicId == 1 || nowChooseMusicId == 3) {
                            mCallback.getBgmPath("");
                            videoVoicePath = "";
                            clearCheckBox();
                            chooseTemplateMusic(false);
                        }
                        mCallback.deleteFirstSticker();
                    }
                    if (stickView.getIsTextSticker()) {
                        mCallback.hineTextDialog();
                    }
                    deleteStickView(stickView, false);

                } else if (type == StickerView.RIGHT_TOP_MODE) {
                    stickView.dismissFrame();
                    //copy
                    //飞闪提供的贴纸是GIF 不支持抠像 所以抠像的情况下拿到的路径为空 这个时候择getResPath()
                    String copyStickViewPath = stickView.getClipPath() == null ? stickView.getResPath() : stickView.getClipPath();
                    copyGif(copyStickViewPath, copyStickViewPath, stickView.getComeFrom(), stickView, stickView.getOriginalPath(), false, stickView.getDownStickerTitle());
                    if (!TextUtils.isEmpty(stickView.getOriginalPath())) {
                        if (albumType.isVideo(GetPathType.getInstance().getMediaType(stickView.getOriginalPath()))) {
                            if (UiStep.isFromDownBj) {
                                StatisticsEventAffair.getInstance().setFlag(mContext, "7_plusone");
                            } else {
                                StatisticsEventAffair.getInstance().setFlag(mContext, "8_plusone");
                            }
                        }
                    }
                } else if (type == StickerView.RIGHT_CENTER_MODE) {
                    showVibrator();
                    if (!stickView.isOpenVoice) {
                        //打开声音
                        stickView.setOpenVoice(true);
                        getVideoVoice(stickView.getOriginalPath(), soundFolder);
                        if (UiStep.isFromDownBj) {
                            StatisticsEventAffair.getInstance().setFlag(mContext, "7_open");
                        } else {
                            StatisticsEventAffair.getInstance().setFlag(mContext, "8_open");
                        }
                    } else {
                        //关闭声音
                        videoVoicePath = "";
                        stickView.setOpenVoice(false);
                        mCallback.getBgmPath("");
                        if (UiStep.isFromDownBj) {
                            StatisticsEventAffair.getInstance().setFlag(mContext, "7_turnoff");
                        } else {
                            StatisticsEventAffair.getInstance().setFlag(mContext, "8_turnoff");
                        }

                    }

                } else if (type == StickerView.LEFT_BOTTOM_MODE) {

                    if (!stickView.getIsTextSticker()) {
                        if (UiStep.isFromDownBj) {
                            StatisticsEventAffair.getInstance().setFlag(mContext, " 5_mb_bj_replace");
                        } else {
                            StatisticsEventAffair.getInstance().setFlag(mContext, " 6_customize_bj_replace");
                        }
                        //切換素材
                        AlbumManager.chooseAlbum(mContext, 1, 0, (tag, paths, isCancel, isFromCamera, albumFileList) -> {
                            if (!isCancel) {
                                if (albumType.isVideo(GetPathType.getInstance().getPathType(paths.get(0)))) {
                                    GetVideoCover getVideoCover = new GetVideoCover(mContext);
                                    getVideoCover.getCover(paths.get(0), path1 -> {
                                        Observable.just(path1).subscribeOn(AndroidSchedulers.mainThread()).subscribe(s -> {
                                            stickView.setOriginalPath(paths.get(0));
                                            stickView.setClipPath(s);
                                            if (!isCheckedMatting) {
                                                stickView.changeImage(paths.get(0), false);
                                            } else {
                                                stickView.changeImage(s, false);
                                            }
                                            stickView.setLeftBitmapNoSave();
                                            if (stickView.isFirstAddSticker()) {
                                                stickView.setShowStickerStartTime(0);
                                                mCallback.changFirstVideoSticker(paths.get(0));
                                                if (TextUtils.isEmpty(mVideoPath)) {
                                                    //没得背景的情况下,重新分离出音乐来
                                                    chooseMaterialMusic(paths.get(0));
                                                } else {
//                                                    callback.getBgmPath("");
                                                    //只是分离，但是不选择素材音乐
//                                                    getVideoVoice(paths.get(0), soundFolder);
                                                    chooseMaterialMusic(paths.get(0));
                                                }

                                            }
                                            mCallback.modifyTimeLineSickerPath(String.valueOf(stickView.getStickerNoIncludeAnimId()), paths.get(0), stickView);
                                        });
                                    });
                                } else {
                                    CompressionCuttingManage manage = new CompressionCuttingManage(mContext, "", tailorPaths -> {
                                        Observable.just(tailorPaths.get(0)).subscribeOn(AndroidSchedulers.mainThread()).subscribe(s -> {
                                            stickView.setOriginalPath(paths.get(0));
                                            stickView.setClipPath(s);
                                            if (!isCheckedMatting) {
                                                stickView.changeImage(paths.get(0), false);
                                            } else {
                                                stickView.changeImage(s, false);
                                            }
                                            mCallback.getBgmPath("");
                                            if (nowChooseMusicId == 1) {

                                                if (!TextUtils.isEmpty(mVideoPath)) {
                                                    mCallback.chooseMusicIndex(1);
                                                    chooseTemplateMusic(true);
                                                } else {
                                                    clearCheckBox();

                                                }


                                            }
                                            if (!stickView.getResPath().endsWith(".gif") && !albumType.isVideo(GetPathType.getInstance().getPathType(stickView.getOriginalPath()))) {
                                                stickView.setLeftBitmap(ContextCompat.getDrawable(mContext, R.mipmap.icon_pic_save));
                                            }
                                            mCallback.modifyTimeLineSickerPath(String.valueOf(stickView.getStickerNoIncludeAnimId()), paths.get(0), stickView);
                                        });
                                    });
                                    manage.toMatting(paths);

                                    if (stickView.isFirstAddSticker()) {
                                        if (stickView.isOpenVoice()) {
                                            stickView.setOpenVoice(false);
                                            mCallback.getBgmPath("");
                                        }
                                    }

                                }
                            }
                        }, "");
                    } else {
                        mCallback.showTextDialog(nowChooseStickerView.getStickerText());
                    }
                } else if (type == StickerView.LEFT_MODE) {
                    if (!TextUtils.isEmpty(stickView.getResPath())) {
                        StatisticsEventAffair.getInstance().setFlag(mContext, "17_zdy_cutout_save");
                        //开启抠像
                        if (isMatting) {
                            if (!TextUtils.isEmpty(stickView.getClipPath())) {
                                saveToAlbum(stickView.getClipPath());
                            } else {
                                saveToAlbum(stickView.getResPath());
                            }
                        } else {
                            //没有开启抠像
                            if (albumType.isVideo(GetPathType.getInstance().getPathType(stickView.getOriginalPath()))) {
                                //素材的类型是视频 取视频的帧图进行保存
                                GetVideoCover getVideoCover = new GetVideoCover(mContext);
                                getVideoCover.getFileCoverForBitmap(stickView.getOriginalPath(), bitmap -> {
                                    String bitmapPath = FileUtil.saveBitmap(bitmap, "saveAlbum");
                                    saveToAlbum(bitmapPath);
                                });
                            } else {
                                //取原图片的路径保存
                                if (!TextUtils.isEmpty(stickView.getOriginalPath())) {
                                    saveToAlbum(stickView.getOriginalPath());
                                } else {
                                    saveToAlbum(stickView.getResPath());
                                }
                            }
                        }
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
                mCallback.needPauseVideo();
                viewLayerRelativeLayout.addView(stickView);
                stickView.start();
                nowChooseStickerView = stickView;
                mCallback.stickerOnclickCallback(stickView.getStickerText());

            }

            @Override
            public void stickerClickShowFrame() {
                mCallback.showTimeLineSickerArrow(String.valueOf(stickView.getStickerNoIncludeAnimId()));
            }

        });
    }

    /**
     * 保存到相册
     */
    private void saveToAlbum(String path) {
        String albumPath = SaveAlbumPathModel.getInstance().getKeepOutputForImage();
        try {
            FileUtil.copyFile(new File(path), albumPath);
            albumBroadcast(albumPath);
            showKeepSuccessDialog(albumPath);

            if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
                AdManager.getInstance().showCpAd(mContext, AdConfigs.AD_SCREEN_FOR_SAVE_PIC);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通知相册更新
     */
    private void albumBroadcast(String outputFile) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(new File(outputFile)));
        mContext.sendBroadcast(intent);
    }

    private void showKeepSuccessDialog(String path) {
        if (!DoubleClick.getInstance().isFastDoubleClick()) {
            ShowPraiseModel.keepAlbumCount();
            //去除黑边
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    new ContextThemeWrapper(mContext, R.style.Theme_Transparent));
            builder.setTitle(R.string.notification);
            builder.setMessage("已为你保存到相册,多多分享给友友\n" + "【" + path + mContext.getString(R.string.folder) + "】"
            );
            builder.setNegativeButton(mContext.getString(R.string.got_it), (dialog, which) -> dialog.dismiss());
            builder.setCancelable(true);
            Dialog dialog = builder.show();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    private void deleteStickView(StickerView stickView, boolean isAnimDelete) {
        viewLayerRelativeLayout.removeView(stickView);
        int nowId = stickView.getStickerNoIncludeAnimId();
        if (stickView.isFirstAddSticker()) {
            if (stickView.isOpenVoice()) {
                stickView.setOpenVoice(false);
                mCallback.getBgmPath("");
                videoVoicePath = "";
            }
        }
        if (!isAnimDelete) {
            mCallback.deleteTimeLineSicker(String.valueOf(nowId));
        }
        deletedListForSticker(stickView.getId());
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

    private void closeAllAnim() {
        //ArrayList<AllStickerData> list = new ArrayList<>();
        for (int i = 0; i < viewLayerRelativeLayout.getChildCount(); i++) {
            StickerView stickerView = (StickerView) viewLayerRelativeLayout.getChildAt(i);
            stickerView.pause();
        }
    }

    /**
     * description ：复制一个gif出来
     * creation date: 2020/5/22
     * param :  getResPath 图片地址，path  isFromAubum 是否来自相册 stickerView 原贴纸 OriginalPath 原图地址 isFromShowAnim 是否是因为来自动画分身
     * user : zhangtongju
     */
    private void copyGif(String getResPath, String path, boolean isFromAubum, StickerView stickerView, String OriginalPath, boolean isFromShowAnim, String title) {

        if (stickerView != null && stickerView.getIsTextSticker()) {
            addSticker("", false, false, false, "", true, stickerView, isFromShowAnim, true, null);
        } else {
            try {
                String copyName;
                if (getResPath.endsWith(".gif")) {
                    if (UiStep.isFromDownBj) {
                        StatisticsEventAffair.getInstance().setFlag(mContext, "5_mb_sticker_plus");
                    } else {
                        StatisticsEventAffair.getInstance().setFlag(mContext, "6_mb_sticker_plus");
                    }
                    copyName = mGifFolder + File.separator + System.currentTimeMillis() + "synthetic.gif";
                    String finalCopyName = copyName;

                    FileUtil.copyFile(new File(getResPath), copyName, new FileUtil.copySucceed() {
                        @Override
                        public void isSucceed() {
                            if (stickerView == null) {
                                addSticker(finalCopyName, false, false, isFromAubum, getResPath, true, null, isFromShowAnim, false, title);
                            } else {
                                if (stickerView != null) {
                                    addSticker(finalCopyName, false, false, isFromAubum, getResPath, true, stickerView, isFromShowAnim, false, stickerView.getDownStickerTitle());
                                } else {
                                    addSticker(finalCopyName, false, false, isFromAubum, getResPath, true, stickerView, isFromShowAnim, false, null);
                                }
                            }
                        }
                    });
                } else {
                    if (UiStep.isFromDownBj) {
                        StatisticsEventAffair.getInstance().setFlag(mContext, "5_mb_bj_plus one");
                    } else {
                        StatisticsEventAffair.getInstance().setFlag(mContext, "6_customize_bj_plus one");
                    }
                    String aa = path.substring(path.length() - 4);
                    copyName = mImageCopyFolder + File.separator + System.currentTimeMillis() + aa;
                    String finalCopyName1 = copyName;
                    FileUtil.copyFile(new File(path), copyName, new FileUtil.copySucceed() {
                        @Override
                        public void isSucceed() {
                            if (isFromShowAnim) {
                                addSticker(getResPath, false, isFromAubum, isFromAubum, OriginalPath, true, stickerView, isFromShowAnim, false, null);
                            } else {
                                addSticker(finalCopyName1, false, isFromAubum, isFromAubum, OriginalPath, true, stickerView, isFromShowAnim, false, null);
                            }
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onDestroy() {
        isDestroy = true;
        stopAllAnim();
        viewLayerRelativeLayout.removeAllViews();
    }

    public void initVideoProgressView() {
        //动态设置距离左边的位置
        if (videoInfo == null) {
            getPlayVideoDuration();
        }
    }


    private final List<Long> perSticker = new ArrayList<>();

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
            if (!TextUtils.isEmpty(mOriginalPath)) {
                if (albumType.isVideo(GetPathType.getInstance().getPathType(mOriginalPath))) {
                    VideoInfo materialVideoInfo = getVideoInfo.getInstance().getRingDuring(mOriginalPath);
                    LogUtil.d("OOM", "materialVideoInfo.getDuration()=" + materialVideoInfo.getDuration());
                    perSticker.add(materialVideoInfo.getDuration());
                }
            }

        }
        //只有一个的情况就不需要比较大小了
        if (perSticker.size() > 0) {
            if (perSticker.size() == 1) {
                defaultVideoDuration = perSticker.get(0);
            } else {
                for (long duration : perSticker) {
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
        mCallback.getVideoDuration(defaultVideoDuration);
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

    public void toSaveVideo(String imageBjPath, boolean nowUiIsLandscape, float percentageH, int templateId, long musicStartTime, long musicEndTime, long cutStartTime, long cutEndTime, String title) {
        disMissStickerFrame();
        if (templateId != 0) {
            LogUtil.d("OOM", "toSaveVideo-templateId=" + templateId);
            StatisticsToSave(templateId + "");
        }
        stopAllAnim();
        this.percentageH = percentageH;
        deleteSubLayerSticker();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isIntoSaveVideo) {
                    isIntoSaveVideo = true;
                    listAllSticker.clear();
                    cutSuccessNum = 0;
                    cutVideoPathList.clear();
                    backgroundDraw = new BackgroundDraw(mContext, mVideoPath, videoVoicePath, imageBjPath, musicStartTime, musicEndTime, cutEndTime - cutStartTime, new BackgroundDraw.saveCallback() {
                        @Override
                        public void saveSuccessPath(String path, int progress) {
                            if (!isDestroy) {
                                if (!TextUtils.isEmpty(path)) {
                                    dismissLoadingDialog();
                                    //成功后的回调
                                    Intent intent = new Intent(mContext, CreationTemplatePreviewActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putStringArrayList("titleEffect", (ArrayList<String>) GetAllStickerDataModel.getInstance().GettitleEffect());
                                    bundle.putStringArrayList("titleStyle", (ArrayList<String>) GetAllStickerDataModel.getInstance().GetTitleStyle());
                                    bundle.putStringArrayList("titleFrame", (ArrayList<String>) GetAllStickerDataModel.getInstance().GetTitleFrame());
                                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    bundle.putString("path", path);
                                    bundle.putBoolean("nowUiIsLandscape", nowUiIsLandscape);
                                    bundle.putString("templateTitle", title);
                                    intent.putExtra("bundle", bundle);
                                    mContext.startActivity(intent);

                                    Observable.just(0).subscribeOn(AndroidSchedulers.mainThread())
                                            .subscribe(integer -> new Handler().postDelayed(() ->
                                                    isIntoSaveVideo = false, 500));
                                } else {
                                    if (progress == 10000) {
                                        isIntoSaveVideo = false;
                                        //渲染失败
                                        dismissLoadingDialog();
                                    } else {
                                        dialogProgress = progress;
                                        handler.sendEmptyMessage(1);
                                    }
                                }
                            }
                        }
                    }, mAnimCollect, true);

                    backgroundDraw.setCutStartTime(cutStartTime);
                    backgroundDraw.setCutEndTime(cutEndTime);

                    for (int i = 0; i < viewLayerRelativeLayout.getChildCount(); i++) {
                        StickerView stickerView = (StickerView) viewLayerRelativeLayout.getChildAt(i);

                        if (stickerView.getIsTextSticker()) {
                            stickerView.disMissFrame();
                        }

                        if (!TextUtils.isEmpty(stickerView.getDownStickerTitle())) {
                            StatisticsEventAffair.getInstance().setFlag(mContext, "mb_bj_Sticker", stickerView.getDownStickerTitle());
                        }

                        listAllSticker.add(GetAllStickerDataModel.getInstance().getStickerData(stickerView, isMatting, videoInfo));
                    }

                    if (listAllSticker.size() == 0) {
                        isIntoSaveVideo = false;
                        Observable.just(0).subscribeOn(AndroidSchedulers.mainThread())
                                .subscribe(integer -> new Handler().post(() ->
                                        Toast.makeText(mContext, "你未选择素材", Toast.LENGTH_SHORT).show()));
                        return;
                    }

                    for (int i = 0; i < listAllSticker.size(); i++) {
                        if (defaultVideoDuration < listAllSticker.get(i).getDuration()) {
                            defaultVideoDuration = (int) listAllSticker.get(i).getDuration();
                        }
                        if (listAllSticker.get(i).isVideo()) {
                            cutVideoPathList.add(new VideoType(listAllSticker.get(i).getOriginalPath(), i, listAllSticker.get(i).getDuration()));
                        }
                    }
                    if (cutVideoPathList.size() == 0) {
                        showLoadingDialog();
                        //都不是视频的情况下，就直接渲染
                        backgroundDraw.toSaveVideo(listAllSticker, isMatting, nowUiIsLandscape, percentageH);
                    } else {
                        showLoadingDialog();
                        cutList.clear();
                        if (videoInfo != null) {
                            cutVideo(cutVideoPathList.get(0), videoInfo.getDuration(), cutVideoPathList.get(0).getDuration(), nowUiIsLandscape);
                        } else {
                            //没选择背景默认裁剪10秒
                            cutVideo(cutVideoPathList.get(0), defaultVideoDuration, cutVideoPathList.get(0).getDuration(), nowUiIsLandscape);
                        }
                    }
                }
            }
        }, 200);
    }

    private void showLoadingDialog() {
        StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "load_video_post_bj");
        mCallback.showLoadingDialog();
    }

    private void dismissLoadingDialog() {
        mCallback.dismissLoadingDialog();
    }


    public void StatisticsToSave(String templateId) {
        HashMap<String, String> params = new HashMap<>();
        params.put("template_id", templateId);
        params.put("action_type", 2 + "");
        // 启动时间
        Observable ob = Api.getDefault().saveTemplate(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(mContext) {
            @Override
            protected void onSubError(String message) {
            }

            @Override
            protected void onSubNext(Object data) {

            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);

    }


    //裁剪成功数量
    private int cutSuccessNum;
    private ArrayList<String> cutList = new ArrayList<>();


    /**
     * description ：裁剪视频，如果视频小于模板时长，那么就裁剪当前时长，如果视频大于当前模板时长，那么就裁剪模板时长
     * creation date: 2020/4/21
     * user : zhangtongju
     */
    private void cutVideo(VideoType videoType, long duration, long materialDuration, boolean nowUiIsLandscape) {
        LogUtil.d("oom3", "需要裁剪的时长为" + materialDuration);

        videoCutDurationForVideoOneDo.getInstance().cutVideoForDrawPadAllExecute2(mContext,
                false, materialDuration, videoType.getPath(), 0,
                new videoCutDurationForVideoOneDo.isSuccess() {
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
                            if (isMatting) {
                                LogUtil.d("OOM2", "裁剪完成，准备抠图");
                                //全部裁剪完成之后需要去把视频裁剪成全部帧
                                videoGetFrameModel getFrameModel = new videoGetFrameModel(mContext, cutList, (isSuccess1, progress) -> {
                                    if (isSuccess1) {
                                        backgroundDraw.toSaveVideo(listAllSticker, true, nowUiIsLandscape, percentageH);
                                    } else {
                                        //todo  临时手段
                                        if (progress <= 5) {
                                            progress = 5;
                                        }
                                        dialogProgress = progress;
                                        handler.sendEmptyMessage(1);
                                    }

                                });
                                getFrameModel.startExecute();
                            } else {
                                backgroundDraw.toSaveVideo(listAllSticker, false, nowUiIsLandscape, percentageH);
                            }
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

        for (AllStickerData data : listAllSticker) {
            if (data.getChooseAnimId() != null && data.getChooseAnimId() != AnimType.NULL) {

                if (data.isMaterial()) {
                    StatisticsEventAffair.getInstance().setFlag(mContext, "9_Animation", data.getChooseAnimId().name());
                } else {
                    StatisticsEventAffair.getInstance().setFlag(mContext, "9_Animation3", data.getChooseAnimId().name());
                }
            }
        }
    }

    private void modificationSingleAnimItemIsChecked(int position) {
        for (StickerAnim item : listAllAnima) {
            item.setChecked(false);
        }
        StickerAnim item1 = listAllAnima.get(position);
        item1.setChecked(true);
        //修改对应的元素
        listAllAnima.set(position, item1);
        templateGridViewAnimAdapter.notifyDataSetChanged();
    }


    /**
     * description ：增加一个新的
     * creation date: 2020/3/19
     * user : zhangtongju
     */
    public void addNewSticker(String path, String originalPath) {
        Observable.just(path).observeOn(AndroidSchedulers.mainThread()).subscribe(path1 -> addSticker(path1, false, true, true, originalPath, false, null, false, false, null));
    }


    /**
     * description ：视频音视频分离，获得视频的声音
     * creation date: 2020/4/23
     * user : zhangtongju
     */
    private void getVideoVoice(String videoPath, String outputPath) {
        WaitingDialog.openPragressDialog(mContext);
//        new Thread(() -> {
        mediaManager manager = new mediaManager(mContext);
        manager.splitMp4(videoPath, new File(outputPath), (isSuccess, putPath) -> {
            WaitingDialog.closeProgressDialog();
            if (isSuccess) {
                LogUtil.d("OOM2", "分离出来的因为地址为" + outputPath);
                videoVoicePath = outputPath + File.separator + "bgm.mp3";
                mCallback.getBgmPath(videoVoicePath);
            } else {
                LogUtil.d("OOM2", "分离出来的因为地址为null" + outputPath);
                mCallback.getBgmPath("");
                videoVoicePath = "";
            }
        });
//        }).start();
    }

    @Override
    public void addSticker(String stickerPath, String title) {
        addSticker(stickerPath, false, false, false, null, false, null, false, false, title);
    }

    @Override
    public void copyGif(String fileName, String copyName, String title) {
        copyGif(fileName, copyName, false, null, fileName, false, title);
    }

    @Override
    public void clickItemSelected(int position) {
        showAllAnim(false);
        mCallback.needPauseVideo();
    }

    private int dialogProgress;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String title;
            String content;
            if (dialogProgress <= 25) {
                title = "飞闪预览处理中";
                content = "请耐心等待 不要离开";
            } else if (dialogProgress <= 40) {
                title = "飞闪音频添加中";
                content = "快了，友友稍等片刻";
            } else if (dialogProgress <= 60) {
                title = "飞闪视频处理中";
                content = "抠像太强大，即将生成";
            } else if (dialogProgress <= 80) {
                title = "飞闪视频合成中";
                content = "马上就好，不要离开";
            } else {
                title = "视频即将呈现啦";
                content = "最后合成中，请稍后";
            }
            mCallback.setDialogProgress(title, dialogProgress, content);
        }
    };


    /**
     * description ：显示全部动画，在预览的时候
     * creation date: 2020/5/27
     * user : zhangtongju
     */
    private boolean hasAnim = false;
    private int hasAnimCount;

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
                listAllSticker.add(GetAllStickerDataModel.getInstance().getStickerData(stickerView, isMatting, videoInfo));
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
                                int type = mAnimCollect.getAnimid(stickerView.getChooseAnimId());
                                startPlayAnim(type, false, stickerView, i, true);
                            }
                        }
                        if (i == listForStickerModel.size() - 1) {
                            //最后一个的情况
                            if (!hasAnim) {
                                mCallback.animIsComplate();
                            }
                        }
                    }
                } else {
                    mCallback.animIsComplate();
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
        LogUtil.d("OOM4", "stopAllAnim");
        if (mAnimCollect != null) {
            mAnimCollect.stopAnim();
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
                            startPlayAnim(mAnimCollect.getAnimid(stickView.getChooseAnimId()), false, null, 0, false);
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
     * description ：设置所有帖子居中状态，这里主要是为了横竖屏切换后，所有帖子居中
     * creation date: 2020/8/10
     * user : zhangtongju
     */
    public void setAllStickerCenter() {
        for (int i = 0; i < viewLayerRelativeLayout.getChildCount(); i++) {
            StickerView stickerView = (StickerView) viewLayerRelativeLayout.getChildAt(i);
            stickerView.setIntoCenter();
//            stickerView.onresmeView();


            //目的解决复制后大小不一的情况
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isCheckedMatting) {
                        stickerView.changeImage(stickerView.getOriginalPath(), false);
                    } else {
                        stickerView.changeImage(stickerView.getClipPath(), false);
                    }
                }
            }, 500);

        }
    }


    /**
     * 数据收集
     *
     * @param path
     * @param context
     */
    public void statisticsDuration(String path, Context context) {
        long duration;
        if (!TextUtils.isEmpty(path) && albumType.isImage(GetPathType.getInstance().getPathType(path))) {
            VideoInfo videoInfo = getVideoInfo.getInstance().getRingDuring(path);
            duration = videoInfo.getDuration();
            if (duration <= 10000) {
                StatisticsEventAffair.getInstance().setFlag(context, "ChooseVideoDuration", "小于10秒");
            } else if (duration <= 20000) {
                StatisticsEventAffair.getInstance().setFlag(context, "ChooseVideoDuration", "小于20秒");
            } else if (duration <= 30000) {
                StatisticsEventAffair.getInstance().setFlag(context, "ChooseVideoDuration", "小于30秒");
            } else if (duration <= 40000) {
                StatisticsEventAffair.getInstance().setFlag(context, "ChooseVideoDuration", "小于40秒");
            } else if (duration <= 50000) {
                StatisticsEventAffair.getInstance().setFlag(context, "ChooseVideoDuration", "小于50秒");
            } else if (duration <= 60000) {
                StatisticsEventAffair.getInstance().setFlag(context, "ChooseVideoDuration", "小于60秒");
            } else if (duration <= 120000) {
                StatisticsEventAffair.getInstance().setFlag(context, "ChooseVideoDuration", "小于2分钟");
            } else if (duration <= 180000) {
                StatisticsEventAffair.getInstance().setFlag(context, "ChooseVideoDuration", "小于3分钟");
            } else if (duration <= 240000) {
                StatisticsEventAffair.getInstance().setFlag(context, "ChooseVideoDuration", "小于4分钟");
            } else if (duration <= 300000) {
                StatisticsEventAffair.getInstance().setFlag(context, "ChooseVideoDuration", "小于5分钟");
            } else if (duration <= 360000) {
                StatisticsEventAffair.getInstance().setFlag(context, "ChooseVideoDuration", "小于6分钟");
            } else if (duration <= 420000) {
                StatisticsEventAffair.getInstance().setFlag(context, "ChooseVideoDuration", "小于7分钟");
            } else if (duration <= 480000) {
                StatisticsEventAffair.getInstance().setFlag(context, "ChooseVideoDuration", "小于8分钟");
            } else if (duration <= 540000) {
                StatisticsEventAffair.getInstance().setFlag(context, "ChooseVideoDuration", "小于9分钟");
            } else {
                StatisticsEventAffair.getInstance().setFlag(context, "ChooseVideoDuration", "小于10分钟");
            }
        }
    }


    public void addTextSticker() {
        addSticker("", false, false, false, "", false, null, false, true, null);
    }


    public void deleteAllTextSticker() {
        toDeleteAllTextSticker();
    }


    /**
     * description ：删除帖子(包括动画贴纸)
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
            deleteStickView(stickerView, false);
        }
    }


    /**
     * description ：当前播放时间回调
     * creation date: 2020/11/9
     * user : zhangtongju
     */
    public void getNowPlayingTime(long progress, long totalTime) {
        if (listForStickerModel != null && listForStickerModel.size() > 0) {
            LogUtil.d("OOM4", "progress=" + progress);
            for (int i = 0; i < listForStickerModel.size(); i++) {
                AnimStickerModel model = listForStickerModel.get(i);
                StickerView stickerView = model.getStickerView();
                if (stickerView != null) {
                    long startTime = stickerView.getShowStickerStartTime();
                    long endTime = stickerView.getShowStickerEndTime();
                    LogUtil.d("OOM4", "endTime" + endTime + "startTime" + startTime + "progress=" + progress);
                    if (endTime != 0) {
                        if (startTime <= progress && progress <= endTime) {
                            stickerView.setVisibility(View.VISIBLE);
                            LogUtil.d("OOM4", "setVisibility");
                        } else if (startTime <= progress && (totalTime - endTime <= 100 || (progress > totalTime && progress - totalTime <= 1))) {
                            stickerView.setVisibility(View.VISIBLE);
                        } else {
                            stickerView.setVisibility(View.GONE);
                            LogUtil.d("OOM4", "setVisibilityGONE");
                        }
                    }
                }
            }
        }
    }


    public void isEndTimer() {
        for (int i = 0; i < listForStickerModel.size(); i++) {
            AnimStickerModel model = listForStickerModel.get(i);
            StickerView stickerView = model.getStickerView();
            if (stickerView != null) {
                stickerView.setVisibility(View.VISIBLE);
            }
        }
    }


    public void bringStickerFront(String id) {
        for (int i = 0; i < listForStickerModel.size(); i++) {
            AnimStickerModel model = listForStickerModel.get(i);
            StickerView stickerView = model.getStickerView();
            if (TextUtils.equals(id, String.valueOf(stickerView.getStickerNoIncludeAnimId()))) {
                nowChooseStickerView = stickerView;
                break;
            }
        }

        if (nowChooseStickerView != null && nowChooseStickerView.getParent() != null) {
            ViewGroup vp = (ViewGroup) nowChooseStickerView.getParent();
            if (vp != null) {
                vp.removeView(nowChooseStickerView);
            }
        }
        nowChooseStickerView.showFrame();
        viewLayerRelativeLayout.addView(nowChooseStickerView);
        mCallback.showMusicBtn(nowChooseStickerView.isFirstAddSticker());
    }

    @Override
    public void chooseBack(String path) {
        mCallback.chooseBack(path);
    }

    @Override
    public void chooseFrame(String path) {
        mCallback.chooseFrame(path);
    }
}


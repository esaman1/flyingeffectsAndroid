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
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.TemplateGridViewAnimAdapter;
import com.flyingeffects.com.adapter.TemplateViewPager;
import com.flyingeffects.com.adapter.home_vp_frg_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.commonlyModel.GetPathType;
import com.flyingeffects.com.commonlyModel.GetVideoCover;
import com.flyingeffects.com.commonlyModel.SaveAlbumPathModel;
import com.flyingeffects.com.commonlyModel.getVideoInfo;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.constans.UiStep;
import com.flyingeffects.com.enity.AllStickerData;
import com.flyingeffects.com.enity.StickerAnim;
import com.flyingeffects.com.enity.StickerTypeEntity;
import com.flyingeffects.com.enity.VideoInfo;
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
import com.flyingeffects.com.manager.mediaManager;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.model.CreationTemplateMvpCallback;
import com.flyingeffects.com.ui.view.activity.ChooseMusicActivity;
import com.flyingeffects.com.ui.view.activity.CreationTemplatePreviewActivity;
import com.flyingeffects.com.ui.view.fragment.StickerFragment;
import com.flyingeffects.com.utils.FileUtil;
import com.flyingeffects.com.utils.LogUtil;
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
import com.shixing.sxve.ui.view.WaitingDialogProgressNowAnim;

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
public class CreationTemplateMvpModel implements StickerFragment.StickerListener {
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private CreationTemplateMvpCallback callback;
    private Context context;
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

    /**
     * 当前添加的音乐路径
     */
    private String addChooseBjPath;
    /**
     * 需要裁剪视频的集合
     */
    private ArrayList<videoType> cutVideoPathList = new ArrayList<>();
    private backgroundDraw backgroundDraw;
    private WaitingDialogProgressNowAnim dialog;
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
    private String originalPath;

    private StickerView nowChooseStickerView;

    /**所有动画集合*/
    private ArrayList<StickerAnim> listAllAnima;
    private ArrayList<StickerView> nowChooseSubLayerAnimList = new ArrayList<>();
    private SparseArrayCompat<ArrayList<StickerView>> sublayerListForBitmapLayer = new SparseArrayCompat<>();

    private AnimCollect animCollect;

    public CreationTemplateMvpModel(Context context, CreationTemplateMvpCallback callback, String mVideoPath, ViewLayerRelativeLayout viewLayerRelativeLayout, String originalPath) {
        this.context = context;
        this.callback = callback;
        this.originalPath = originalPath;
        this.mVideoPath = mVideoPath;
        dialog = new WaitingDialogProgressNowAnim(context);
        this.viewLayerRelativeLayout = viewLayerRelativeLayout;
        vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        if (!TextUtils.isEmpty(mVideoPath)) {
            videoInfo = getVideoInfo.getInstance().getRingDuring(mVideoPath);
        }
        FileManager fileManager = new FileManager();
        mGifFolder = fileManager.getFileCachePath(context, "gifFolder");
        soundFolder = fileManager.getFileCachePath(context, "soundFolder");
        mImageCopyFolder = fileManager.getFileCachePath(context, "imageCopy");
        animCollect = new AnimCollect();
        listAllAnima = animCollect.getAnimList();
    }


    /**
     * description ：更换字体样式  type 0 是热门效果，1是字体
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
     * description ：更换文字
     * creation date: 2020/9/21
     * user : zhangtongju
     */
    public void ChangeTextLabe(String text) {
        if (nowChooseStickerView.getIsTextSticker()) {
            if (TextUtils.isEmpty(text)) {
                deleteStickView(nowChooseStickerView,false);
            } else {
                nowChooseStickerView.setStickerText(text);
                callback.updateTimeLineSickerText(text, String.valueOf(nowChooseStickerView.getStickerNoIncludeAnimId()));
            }
        }
    }

    public void ChangeTextColor(String color0, String color1, String title) {
        if (nowChooseStickerView.getIsTextSticker()) {
            nowChooseStickerView.setTextPaintColor(color0, color1, title);
        }
    }


    /**
     * description ：textBjPath 文字背景路径，textFramePath 文字边框
     * creation date: 2020/10/23
     * user : zhangtongju
     */
    public void ChangeTextFrame(String textBjPath, String textFramePath, String frameTitle) {
        if (nowChooseStickerView.getIsTextSticker()) {
            nowChooseStickerView.ChangeTextFrame(textBjPath, textFramePath, frameTitle);
        }
    }


    public void ChangeTextFrame(String color0, String color1, String textFramePath, String frameTitle) {
        if (nowChooseStickerView.getIsTextSticker()) {
            nowChooseStickerView.ChangeTextFrame(color0, color1, textFramePath, frameTitle);
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

            if (TextUtils.isEmpty(videoVoicePath)) {
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


    public void CheckedChanged(boolean isChecked) {
        this.isCheckedMatting = isChecked;
        MattingChange(isChecked);
        stopAllAnim();
        deleteSubLayerSticker();
        callback.needPauseVideo();
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
        viewPager.setCurrentItem(pageNum);
        //   showAllAnim(false);
    }


    public void GetVideoCover(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        Bitmap mBitmap = retriever.getFrameAtTime(0);
        String fileName = mImageCopyFolder + File.separator + UUID.randomUUID() + ".png";
        BitmapManager.getInstance().saveBitmapToPath(mBitmap, fileName, isSuccess -> {
            CompressionCuttingManage manage = new CompressionCuttingManage(context, ""
                    , false, tailorPaths -> callback.getVideoCover(tailorPaths.get(0), path));
            List<String> mattingPath = new ArrayList<>();
            mattingPath.add(fileName);
            manage.toMatting(mattingPath);
            GlideBitmapPool.putBitmap(mBitmap);
        });
    }

    TemplateGridViewAnimAdapter templateGridViewAnimAdapter;

    private ViewPager viewPager;

    ImageView check_box_0;
    ImageView check_box_1;
    ImageView check_box_2;
    ImageView check_box_3;

    TextView tv_0;
    TextView tv_1;
    TextView tv_2;
    TextView tv_3;

    public void initBottomLayout(ViewPager viewPager, FragmentManager fragmentManager) {
        this.viewPager = viewPager;
        View templateThumbView = LayoutInflater.from(context).inflate(R.layout.view_template_paster, viewPager, false);
        ViewPager stickerViewPager = templateThumbView.findViewById(R.id.viewpager_sticker);
        templateThumbView.findViewById(R.id.iv_delete_sticker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAllAnim();
                closeAllAnim();
                deleteAllSticker();
                if (UiStep.isFromDownBj) {
                    statisticsEventAffair.getInstance().setFlag(context, " 5_mb_bj_Stickeroff");
                } else {
                    statisticsEventAffair.getInstance().setFlag(context, " 6_customize_bj_Stickeroff");
                }
            }
        });
        templateThumbView.findViewById(R.id.iv_down_sticker).setOnClickListener(v -> callback.stickerFragmentClose());
        SlidingTabLayout stickerTab = templateThumbView.findViewById(R.id.tb_sticker);
        getStickerTypeList(fragmentManager, stickerViewPager, stickerTab);

        View viewForChooseAnim = LayoutInflater.from(context).inflate(R.layout.view_create_template_anim_creation, viewPager, false);
        GridView gridViewAnim = viewForChooseAnim.findViewById(R.id.gridView_anim);
        TextView animTab = viewForChooseAnim.findViewById(R.id.tv_name_bj_head);
        animTab.setText("默认");
        animTab.setTextSize(17);

        viewForChooseAnim.findViewById(R.id.iv_down_anim).setOnClickListener(v -> callback.stickerFragmentClose());
        viewForChooseAnim.findViewById(R.id.iv_delete_anim).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.needPauseVideo();
                startPlayAnim(0, true, null, 0, false);
                statisticsEventAffair.getInstance().setFlag(context, "9_Animation2");
                statisticsEventAffair.getInstance().setFlag(context, "9_Animation4");
            }
        });
        gridViewAnim.setOnItemClickListener((adapterView, view, i, l) -> {
            if (!DoubleClick.getInstance().isFastZDYDoubleClick(1000)) {
                modificationSingleAnimItemIsChecked(i);
                callback.needPauseVideo();
                WaitingDialog.openPragressDialog(context);
                startPlayAnim(i, false, null, 0, false);
            }
        });
        templateGridViewAnimAdapter = new TemplateGridViewAnimAdapter(listAllAnima, context);
        gridViewAnim.setAdapter(templateGridViewAnimAdapter);
        listForInitBottom.add(templateThumbView);
        listForInitBottom.add(viewForChooseAnim);

        //添加音乐
        View viewForChooseMusic = LayoutInflater.from(context).inflate(R.layout.view_choose_music, viewPager, false);
        TextView tv_add_music = viewForChooseMusic.findViewById(R.id.tv_add_music);
        viewForChooseMusic.findViewById(R.id.iv_down_music).setOnClickListener(v -> callback.stickerFragmentClose());
        tv_add_music.setOnClickListener(view -> {
            statisticsEventAffair.getInstance().setFlag(context, "15_music_add");
            Intent intent = new Intent(context, ChooseMusicActivity.class);
            intent.putExtra("needDuration", getDuration());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        });

        tv_0 = viewForChooseMusic.findViewById(R.id.tv_0);
        tv_1 = viewForChooseMusic.findViewById(R.id.tv_1);
        tv_2 = viewForChooseMusic.findViewById(R.id.tv_2);
        tv_1.setText("背景音乐");
        tv_3 = viewForChooseMusic.findViewById(R.id.tv_3);
        check_box_0 = viewForChooseMusic.findViewById(R.id.iv_check_box_0);
        check_box_1 = viewForChooseMusic.findViewById(R.id.iv_check_box_1);
        check_box_2 = viewForChooseMusic.findViewById(R.id.iv_check_box_2);
        check_box_3 = viewForChooseMusic.findViewById(R.id.iv_check_box_3);
        tv_2.setText("提取音乐");
        tv_0.setOnClickListener(tvMusicListener);
        tv_1.setOnClickListener(tvMusicListener);
        tv_2.setOnClickListener(tvMusicListener);
        tv_3.setOnClickListener(tvMusicListener);
        check_box_0.setOnClickListener(tvMusicListener);
        check_box_1.setOnClickListener(tvMusicListener);
        check_box_2.setOnClickListener(tvMusicListener);
        check_box_3.setOnClickListener(tvMusicListener);
        listForInitBottom.add(viewForChooseMusic);

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

        new Handler().postDelayed(() -> {
            if (!TextUtils.isEmpty(mVideoPath)) {
                LogUtil.d("OOM", "当前有背景");
                //模板音乐
                nowChooseMusicId = 2;
                chooseTemplateMusic(true);
                callback.ChooseMusicIndex(1);
            } else if (albumType.isVideo(GetPathType.getInstance().getPathType(originalPath))) {
                LogUtil.d("OOM", "当前素材是视频");
                nowChooseMusicId = 1;
                chooseMaterialMusic(originalPath);
                callback.ChooseMusicIndex(0);
            }
        }, 500);
    }


    private void getStickerTypeList(FragmentManager fragmentManager, ViewPager stickerViewPager, SlidingTabLayout stickerTab) {
        HashMap<String, String> params = new HashMap<>();
        // 启动时间
        Observable ob = Api.getDefault().getStickerTypeList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<ArrayList<StickerTypeEntity>>(context) {
            @Override
            protected void _onError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(ArrayList<StickerTypeEntity> list) {
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

    private long getDuration() {
        long duration = 0;
        if (!TextUtils.isEmpty(mVideoPath)) {
            duration = videoInfo.getDuration();
        } else {
            if (listAllSticker != null) {
                //说明没得背景视频，那么渲染时长就是
                for (AllStickerData data : listAllSticker
                ) {
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


    View.OnClickListener tvMusicListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.iv_check_box_0:
                case R.id.tv_0:

                    chooseMaterialMusic(nowChooseStickerView.getOriginalPath());
                    callback.ChooseMusicIndex(0);
                    break;

                case R.id.tv_1:
                case R.id.iv_check_box_1:
                    callback.ChooseMusicIndex(1);
                    chooseTemplateMusic(true);

                    break;

                case R.id.tv_2:
                case R.id.iv_check_box_2:
                    callback.ChooseMusicIndex(2);
                    nowChooseMusicId = 3;
                    chooseAddChooseBjPath();
                    break;

                case R.id.iv_check_box_3:
                case R.id.tv_3:
                    callback.ChooseMusicIndex(3);
                    clearCheckBox();
                    check_box_3.setImageResource(R.mipmap.template_btn_selected);
                    break;
                default:
                    break;
            }
        }
    };


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
            callback.getBgmPath("");
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
            callback.getBgmPath(addChooseBjPath);
        } else {
            ToastUtil.showToast("没有提取音乐");
        }
    }


    private void clearCheckBox() {
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
                if (animCollect.getAnimNeedSubLayerCount(listAllAnima.get(position).getAnimType()) > 0) {
                    for (int x = 1; x <= animCollect.getAnimNeedSubLayerCount(listAllAnima.get(position).getAnimType()); x++) {
                        //通过动画属性得到需要分身的数量，然后复制出贴纸在数组里面nowChooseSubLayerAnimList，最后需要删除
                        LogUtil.d("startPlayAnim", "当前动画复制的主id为" + targetStickerView.getId());
                        if (!TextUtils.isEmpty(targetStickerView.getClipPath())) {
                            //gif 贴纸，没得抠图
                            copyGif(targetStickerView.getClipPath(), targetStickerView.getResPath(), targetStickerView.getComeFrom(), targetStickerView, targetStickerView.getOriginalPath(), true, targetStickerView.getDownStickerTitle());
                        } else {
                            copyGif(targetStickerView.getResPath(), targetStickerView.getResPath(), targetStickerView.getComeFrom(), targetStickerView, targetStickerView.getOriginalPath(), true, targetStickerView.getDownStickerTitle());
                        }
                        if (x == animCollect.getAnimNeedSubLayerCount(listAllAnima.get(position).getAnimType())) {
                            LogUtil.d("OOM", "sublayerListPosition" + sublayerListPosition);
                            ArrayList<StickerView> list = new ArrayList<>(nowChooseSubLayerAnimList);
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
            ArrayList<StickerView> list;
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
                        deleteStickView(stickerView,true);
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
    private ArrayList<StickerView> needDeleteList = new ArrayList<>();

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
            deleteStickView(stickerView,false);
        }

    }


    /*
     * @Author Zhangtj
     * @Date 2020/3/21
     * @Des 抠图和原图之间切换  isMatting 是否抠图
     */
    private void MattingChange(boolean isMatting) {
        this.isMatting = isMatting;
        if (listForStickerModel != null && listForStickerModel.size() > 0) {
            for (AnimStickerModel stickerModel : listForStickerModel
            ) {
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
    /**不包含动画的id*/
    private int stickerId = 0;
    private boolean isIntoDragMove = false;

    private void addSticker(String path, boolean isFirstAdd, boolean hasReplace, boolean isFromAlbum, String originalPath, boolean isCopy, StickerView copyStickerView, boolean isFromShowAnim, boolean isText, String title) {
        closeAllAnim();
        StickerView stickView = new StickerView(context, isText);
        stickView.setId(stickerViewID);
        stickView.setOnitemClickListener(new StickerItemOnitemclick() {
            @Override
            public void stickerOnclick(int type) {
                if (type == StickerView.LEFT_TOP_MODE) {//刪除
                    if (stickView.isFirstAddSticker()) {
                        if (nowChooseMusicId == 1 || nowChooseMusicId == 3) {
                            callback.getBgmPath("");
                            videoVoicePath = "";
                            clearCheckBox();
                            chooseTemplateMusic(false);
                        }
                        callback.deleteFirstSticker();
                    }
                    if (stickView.getIsTextSticker()) {
                        callback.hineTextDialog();
                    }
                    deleteStickView(stickView,false);

                } else if (type == StickerView.RIGHT_TOP_MODE) {
                    stickView.dismissFrame();
                    //copy
//                    String copyStickViewPath = isMatting ? stickView.getClipPath() : stickView.getResPath();
                    String copyStickViewPath =  stickView.getClipPath();
                    copyGif(copyStickViewPath, copyStickViewPath, stickView.getComeFrom(), stickView, stickView.getOriginalPath(), false, stickView.getDownStickerTitle());
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
                        callback.getBgmPath("");
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
                        AlbumManager.chooseAlbum(context, 1, 0, (tag, paths, isCancel, isFromCamera, albumFileList) -> {
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
                                                callback.changFirstVideoSticker(paths.get(0));
                                                callback.getBgmPath("");
                                            }
                                            callback.modifyTimeLineSickerPath(String.valueOf(stickView.getStickerNoIncludeAnimId()), paths.get(0));
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
                                            callback.modifyTimeLineSickerPath(String.valueOf(stickView.getStickerNoIncludeAnimId()), paths.get(0));
                                        });
                                    });
                                    manage.toMatting(paths);

                                    if (stickView.isFirstAddSticker()) {
                                        if (stickView.isOpenVoice()) {
                                            stickView.setOpenVoice(false);
                                            callback.getBgmPath("");
                                        }
                                    }

                                }
                            }
                        }, "");
                    } else {
                        callback.showTextDialog(nowChooseStickerView.getStickerText());
                    }
                } else if (type == StickerView.LEFT_MODE) {
                    if (!TextUtils.isEmpty(stickView.getResPath())) {
                        statisticsEventAffair.getInstance().setFlag(context, "17_zdy_cutout_save");
                        //开启抠像
                        if (isMatting) {
                            saveToAlbum(stickView.getClipPath());
                        } else {
                            //没有开启抠像
                            if (albumType.isVideo(GetPathType.getInstance().getPathType(stickView.getOriginalPath()))) {
                                //素材的类型是视频 取视频的帧图进行保存
                                GetVideoCover getVideoCover = new GetVideoCover(context);
                                getVideoCover.getFileCoverForBitmap(stickView.getOriginalPath(), bitmap -> {
                                    String bitmapPath = FileUtil.saveBitmap(bitmap, "saveAlbum");
                                    saveToAlbum(bitmapPath);
                                });
                            } else {
                                //取原图片的路径保存
                                saveToAlbum(stickView.getOriginalPath());
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
                callback.needPauseVideo();
                viewLayerRelativeLayout.addView(stickView);
                stickView.start();
                nowChooseStickerView = stickView;
                callback.stickerOnclickCallback(stickView.getStickerText());

            }

            @Override
            public void stickerClickShowFrame() {
                callback.showTimeLineSickerArrow(String.valueOf(stickView.getStickerNoIncludeAnimId()));
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
//                if (isIntoDragMove && stickView.getChooseAnimId() != null && stickView.getChooseAnimId() != AnimType.NULL) {
//                    startTimer(stickView);
//                }
                isIntoDragMove = false;
                if (stickView.isFirstAddSticker()) {
                    //显示音乐按钮
                    callback.showMusicBtn(true);
                } else {
                    callback.showMusicBtn(false);
                }
                if (!stickView.getIsTextSticker()) {
                    callback.hideKeyBord();
                }

                nowChooseStickerView = stickView;
            }
        });
        stickView.setRightTopBitmap(ContextCompat.getDrawable(context, R.mipmap.sticker_copy));
        stickView.setLeftTopBitmap(ContextCompat.getDrawable(context, R.drawable.sticker_delete));
        stickView.setRightBottomBitmap(ContextCompat.getDrawable(context, R.mipmap.sticker_redact));
        stickView.setIsFromStickerAnim(isFromShowAnim);
        stickView.setComeFromAlbum(isFromAlbum);
        if (isFromAlbum) {
            LogUtil.d("OOM2", "ClipPath=" + path);
            stickView.setClipPath(path);
            LogUtil.d("OOM2", "originalPath=" + originalPath);
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
            stickView.setLeftBottomBitmap(ContextCompat.getDrawable(context, R.mipmap.sticker_change));
        }

        if (isText) {
            stickView.setLeftBottomBitmap(ContextCompat.getDrawable(context, R.mipmap.shader_edit));
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
                if (copyStickerView.GetIsChooseTextBjEffect()) {
                    if (copyStickerView.GetOpenThePattern()) {
                        //当前有边框
                        stickView.ChangeTextFrame(copyStickerView.getTypefaceBitmapPath(), copyStickerView.getBjFramePath(), copyStickerView.GetTextFrameTitle());
                    } else {
                        if (!TextUtils.isEmpty(copyStickerView.getTypefaceBitmapPath())) {
                            stickView.setTextBitmapStyle(copyStickerView.getTypefaceBitmapPath(), copyStickerView.GetTextEffectTitle());
                        }
                    }

                } else {
                    ArrayList<String> colors = copyStickerView.GetTextColors();
                    if (copyStickerView.GetOpenThePattern()) {
                        nowChooseStickerView.ChangeTextFrame(colors.get(0), colors.get(1), copyStickerView.GetTextEffectTitle());
                    } else {
                        stickView.setTextPaintColor(colors.get(0), colors.get(1), copyStickerView.GetTextEffectTitle());
                    }
                }
                if (!TextUtils.isEmpty(copyStickerView.getTypefacePath())) {
                    stickView.setTextStyle(copyStickerView.getTypefacePath(), copyStickerView.GetTextStyleTitle());
                }
                stickView.setStickerText(copyStickerView.getStickerText());
                stickView.SetTextAngle(copyStickerView.getRotateAngle());
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
                        if (copyStickerView.GetIsChooseTextBjEffect()) {
                            if (!TextUtils.isEmpty(copyStickerView.getTypefacePath())) {
                                stickView.setTextStyle(copyStickerView.getTypefacePath(), copyStickerView.GetTextStyleTitle());
                            }
                            if (!TextUtils.isEmpty(copyStickerView.getTypefaceBitmapPath())) {
                                stickView.setTextBitmapStyle(copyStickerView.getTypefaceBitmapPath(), copyStickerView.GetTextEffectTitle());
                            }
                        } else {
                            ArrayList<String> colors = copyStickerView.GetTextColors();
                            stickView.setTextPaintColor(colors.get(0), colors.get(1), copyStickerView.GetTextEffectTitle());
                        }
                        stickView.setStickerText(copyStickerView.getStickerText());
                        stickView.SetTextAngle(copyStickerView.getRotateAngle());
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
            stickView.setRightBitmap(ContextCompat.getDrawable(context, R.mipmap.sticker_updown));
            if (!stickView.getResPath().endsWith(".gif") && !albumType.isVideo(GetPathType.getInstance().getPathType(stickView.getOriginalPath()))) {
                stickView.setLeftBitmap(ContextCompat.getDrawable(context, R.mipmap.icon_pic_save));
            }
        }
        AnimStickerModel animStickerModel = new AnimStickerModel(context, viewLayerRelativeLayout, stickView);
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
            callback.addStickerTimeLine(String.valueOf(stickerId), isText, isText ? stickView.getStickerText() : "", stickView);
            stickerId++;
        }
        stickerViewID++;
        if (isFirstAdd) {
            callback.isFirstAddSuccess();
        }
        if (isFromShowAnim) {
            stickView.setIsfromAnim(true);
            nowChooseSubLayerAnimList.add(stickView);
        }
    }

    /**保存到相册*/
    private void saveToAlbum(String path) {
        String albumPath = SaveAlbumPathModel.getInstance().getKeepOutputForImage();
        try {
            FileUtil.copyFile(new File(path), albumPath);
            albumBroadcast(albumPath);
            showKeepSuccessDialog(albumPath);

            if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
                AdManager.getInstance().showCpAd(context, AdConfigs.AD_SCREEN_FOR_SAVE_PIC);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**通知相册更新*/
    private void albumBroadcast(String outputFile) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(new File(outputFile)));
        context.sendBroadcast(intent);
    }

    private void showKeepSuccessDialog(String path) {
        if (!DoubleClick.getInstance().isFastDoubleClick()) {
            ShowPraiseModel.keepAlbumCount();
            //去除黑边
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    new ContextThemeWrapper(context, R.style.Theme_Transparent));
            builder.setTitle(R.string.notification);
            builder.setMessage("已为你保存到相册,多多分享给友友\n" + "【" + path + context.getString(R.string.folder) + "】"
            );
            builder.setNegativeButton(context.getString(R.string.got_it), (dialog, which) -> dialog.dismiss());
            builder.setCancelable(true);
            Dialog dialog = builder.show();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    private void deleteStickView(StickerView stickView,boolean isAnimDelete) {
        viewLayerRelativeLayout.removeView(stickView);
        int nowId = stickView.getStickerNoIncludeAnimId();
        if (stickView.isFirstAddSticker()) {
            if (stickView.isOpenVoice()) {
                stickView.setOpenVoice(false);
                callback.getBgmPath("");
                videoVoicePath = "";
            }
        }
        if (!isAnimDelete) {
            callback.deleteTimeLineSicker(String.valueOf(nowId));
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
                            if (stickerView == null) {
                                addSticker(finalCopyName, false, false, isFromAubum, getResPath, true, null, isFromShowAnim, false, title);
                            } else {
                                if (stickerView != null) {
                                    addSticker(finalCopyName, false, false, isFromAubum, getResPath, true, stickerView, isFromShowAnim, false, stickerView.getDownStickerTitle());
                                }else{
                                    addSticker(finalCopyName, false, false, isFromAubum, getResPath, true, stickerView, isFromShowAnim, false, null);
                                }
                            }
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
    }

    public void initVideoProgressView() {
        //动态设置距离左边的位置
        if (videoInfo == null) {
            getPlayVideoDuration();
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
        callback.getVideoDuration(defaultVideoDuration);
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

    public void toSaveVideo(String imageBjPath, boolean nowUiIsLandscape, float percentageH, int templateId, long musicStartTime, long musicEndTime, long cutStartTime,long cutEndTime, String title) {
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
                    backgroundDraw = new backgroundDraw(context, mVideoPath, videoVoicePath, imageBjPath, musicStartTime, musicEndTime, cutEndTime - cutStartTime, new backgroundDraw.saveCallback() {
                        @Override
                        public void saveSuccessPath(String path, int progress) {
                            if (!isDestroy) {
                                if (!TextUtils.isEmpty(path)) {
                                    dialog.closePragressDialog();
                                    //成功后的回调
                                    Intent intent = new Intent(context, CreationTemplatePreviewActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putStringArrayList("titleEffect", (ArrayList<String>) GetAllStickerDataModel.getInstance().GettitleEffect());
                                    bundle.putStringArrayList("titleStyle", (ArrayList<String>) GetAllStickerDataModel.getInstance().GetTitleStyle());
                                    bundle.putStringArrayList("titleFrame", (ArrayList<String>) GetAllStickerDataModel.getInstance().GetTitleFrame());
                                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    bundle.putString("path", path);
                                    bundle.putBoolean("nowUiIsLandscape", nowUiIsLandscape);
                                    bundle.putString("templateTitle", title);
                                    intent.putExtra("bundle", bundle);
                                    context.startActivity(intent);
                                    Observable.just(0).subscribeOn(AndroidSchedulers.mainThread())
                                            .subscribe(integer -> new Handler().postDelayed(() ->
                                                    isIntoSaveVideo = false, 500));
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
                    }, animCollect,true);
                    backgroundDraw.setCutStartTime(cutStartTime);
                    backgroundDraw.setCutEndTime(cutEndTime);

                    for (int i = 0; i < viewLayerRelativeLayout.getChildCount(); i++) {
                        StickerView stickerView = (StickerView) viewLayerRelativeLayout.getChildAt(i);
                        if (stickerView.getIsTextSticker()) {
                            stickerView.disMissFrame();
                        }

                        if (!TextUtils.isEmpty(stickerView.getDownStickerTitle())) {
                            statisticsEventAffair.getInstance().setFlag(context, "mb_bj_Sticker", stickerView.getDownStickerTitle());
                        }

                        listAllSticker.add(GetAllStickerDataModel.getInstance().getStickerData(stickerView, isMatting, videoInfo));
                    }

                    if (listAllSticker.size() == 0) {
                        isIntoSaveVideo = false;
                        Observable.just(0).subscribeOn(AndroidSchedulers.mainThread())
                                .subscribe(integer -> new Handler().post(() ->
                                        Toast.makeText(context, "你未选择素材", Toast.LENGTH_SHORT).show()));
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
                        backgroundDraw.toSaveVideo(listAllSticker, isMatting, nowUiIsLandscape, percentageH);
                    } else {
                        dialog.openProgressDialog();
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


    public void StatisticsToSave(String templateId) {
        HashMap<String, String> params = new HashMap<>();
        params.put("template_id", templateId);
        params.put("action_type", 2 + "");
        // 启动时间
        Observable ob = Api.getDefault().saveTemplate(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(context) {
            @Override
            protected void _onError(String message) {
            }

            @Override
            protected void _onNext(Object data) {

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
                    if (isMatting) {
                        LogUtil.d("OOM2", "裁剪完成，准备抠图");
                        //全部裁剪完成之后需要去把视频裁剪成全部帧
                        videoGetFrameModel getFrameModel = new videoGetFrameModel(context, cutList, (isSuccess1, progress) -> {
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

    private void modificationSingleAnimItemIsChecked(int position) {
        for (StickerAnim item : listAllAnima
        ) {
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
        WaitingDialog.openPragressDialog(context);
//        new Thread(() -> {
        mediaManager manager = new mediaManager(context);
        manager.splitMp4(videoPath, new File(outputPath), (isSuccess, putPath) -> {
            WaitingDialog.closePragressDialog();
            if (isSuccess) {
                LogUtil.d("OOM2", "分离出来的因为地址为" + outputPath);
                videoVoicePath = outputPath + File.separator + "bgm.mp3";
                callback.getBgmPath(videoVoicePath);
            } else {
                LogUtil.d("OOM2", "分离出来的因为地址为null" + outputPath);
                callback.getBgmPath("");
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


    private int dialogProgress;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (dialogProgress <= 25) {
                dialog.setProgress("飞闪预览处理中" + dialogProgress + "%\n" + "请耐心等待 不要离开");
            } else if (dialogProgress <= 40) {
                dialog.setProgress("飞闪音频添加中" + dialogProgress + "%\n" + "快了，友友稍等片刻");
            } else if (dialogProgress <= 60) {
                dialog.setProgress("飞闪视频处理中" + dialogProgress + "%\n" + "抠像太强大，即将生成");
            } else if (dialogProgress <= 80) {
                dialog.setProgress("飞闪视频合成中" + dialogProgress + "%\n" + "马上就好，不要离开");
            } else {
                dialog.setProgress("视频即将呈现啦" + dialogProgress + "%\n" + "最后合成中，请稍后");
            }
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
        LogUtil.d("OOM4","stopAllAnim");
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


    public void statisticsDuration(String path, Context context) {
        long duration;
        if (!TextUtils.isEmpty(path) && albumType.isImage(GetPathType.getInstance().getPathType(path))) {
            VideoInfo videoInfo = getVideoInfo.getInstance().getRingDuring(path);
            duration = videoInfo.getDuration();
            if (duration <= 10000) {
                statisticsEventAffair.getInstance().setFlag(context, "ChooseVideoDuration", "小于10秒");
            } else if (duration <= 20000) {
                statisticsEventAffair.getInstance().setFlag(context, "ChooseVideoDuration", "小于20秒");
            } else if (duration <= 30000) {
                statisticsEventAffair.getInstance().setFlag(context, "ChooseVideoDuration", "小于30秒");
            } else if (duration <= 40000) {
                statisticsEventAffair.getInstance().setFlag(context, "ChooseVideoDuration", "小于40秒");
            } else if (duration <= 50000) {
                statisticsEventAffair.getInstance().setFlag(context, "ChooseVideoDuration", "小于50秒");
            } else if (duration <= 60000) {
                statisticsEventAffair.getInstance().setFlag(context, "ChooseVideoDuration", "小于60秒");
            } else if (duration <= 120000) {
                statisticsEventAffair.getInstance().setFlag(context, "ChooseVideoDuration", "小于2分钟");
            } else if (duration <= 180000) {
                statisticsEventAffair.getInstance().setFlag(context, "ChooseVideoDuration", "小于3分钟");
            } else if (duration <= 240000) {
                statisticsEventAffair.getInstance().setFlag(context, "ChooseVideoDuration", "小于4分钟");
            } else if (duration <= 300000) {
                statisticsEventAffair.getInstance().setFlag(context, "ChooseVideoDuration", "小于5分钟");
            } else if (duration <= 360000) {
                statisticsEventAffair.getInstance().setFlag(context, "ChooseVideoDuration", "小于6分钟");
            } else if (duration <= 420000) {
                statisticsEventAffair.getInstance().setFlag(context, "ChooseVideoDuration", "小于7分钟");
            } else if (duration <= 480000) {
                statisticsEventAffair.getInstance().setFlag(context, "ChooseVideoDuration", "小于8分钟");
            } else if (duration <= 540000) {
                statisticsEventAffair.getInstance().setFlag(context, "ChooseVideoDuration", "小于9分钟");
            } else {
                statisticsEventAffair.getInstance().setFlag(context, "ChooseVideoDuration", "小于10分钟");
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
            deleteStickView(stickerView,false);
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
                    LogUtil.d("OOM4", "endTime" + endTime+"startTime" + startTime+"progress="+progress);
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
        callback.showMusicBtn(nowChooseStickerView.isFirstAddSticker());
    }
}


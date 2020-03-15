package com.flyingeffects.com.ui.model;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.TemplateGridViewAdapter;
import com.flyingeffects.com.adapter.TemplateViewPager;
import com.flyingeffects.com.adapter.VideoTimelineAdapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.commonlyModel.SaveAlbumPathModel;
import com.flyingeffects.com.enity.StickerForParents;
import com.flyingeffects.com.manager.CopyFileFromAssets;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.ui.interfaces.model.CreationTemplateMvpCallback;
import com.flyingeffects.com.utils.FileUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.view.StickerView;
import com.flyingeffects.com.view.VideoFrameRecycler;
import com.lansosdk.box.AudioLayer;
import com.lansosdk.box.DrawPad;
import com.lansosdk.box.DrawPadUpdateMode;
import com.lansosdk.box.MVLayer;
import com.lansosdk.box.VideoLayer;
import com.lansosdk.box.ViewLayerRelativeLayout;
import com.lansosdk.box.onDrawPadProgressListener;
import com.lansosdk.box.onDrawPadSizeChangedListener;
import com.lansosdk.videoeditor.AudioEditor;
import com.lansosdk.videoeditor.AudioPadExecute;
import com.lansosdk.videoeditor.DrawPadView;
import com.lansosdk.videoeditor.LanSongFileUtil;
import com.lansosdk.videoeditor.MediaInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.subjects.PublishSubject;


/**
 * description ：使用蓝松的drawPadView来绘制页面，实现方式为一个主视频图层加上多个动态的mv图层+ 多个图片图层，最后渲染出来视频
 * creation date: 2020/3/12
 * param :
 * user : zhangtongju
 */
public class CreationTemplateMvpModel {
    private final String TAG = "CreationTemplateMvpModel";
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private CreationTemplateMvpCallback callback;
    private Context context;
    private List<View> listForInitBottom = new ArrayList<>();
    private String mVideoPath;
    private MediaPlayer mplayer;
    private DrawPadView mDrawPadView;
    private ViewLayerRelativeLayout viewLayerRelativeLayout;
    private static final int DRAWPAD_WIDTH = 720;
    private static final int DRAWPAD_HEIGHT = 1280;
    private String gifTest = "/storage/emulated/0/DCIM/Camera/IMG_20200130_142048.jpg";
    /**
     * 保存文件夹地址
     */
    private String editTmpPath = null;
    /**
     * 主视频图层
     */
    private VideoLayer mLayerMain;
    private ArrayList<MVLayer> mvLayerArrayList = new ArrayList<>();
    private String path = "";
//    private MediaInfo mInfo;
    private ArrayList<AnimStickerModel> listForStickerView = new ArrayList<>();
    private boolean isDestroy=false;


    public CreationTemplateMvpModel(Context context, CreationTemplateMvpCallback callback, String mVideoPath, ViewLayerRelativeLayout viewLayerRelativeLayout) {
        this.context = context;
        this.callback = callback;
        this.mVideoPath = mVideoPath;
        this.viewLayerRelativeLayout = viewLayerRelativeLayout;
        editTmpPath = LanSongFileUtil.newMp4PathInBox();
        mLayerMain = null;
    }

    public void initBottomLayout(ViewPager viewPager) {
        View templateThumbView = LayoutInflater.from(context).inflate(R.layout.view_template_paster, viewPager, false);
        GridView gridView = templateThumbView.findViewById(R.id.gridView);
        gridView.setOnItemClickListener((adapterView, view, i, l) -> {
            StickerView stickView = new StickerView(context);
            stickView.setLeftBottomBitmap(context.getDrawable(R.mipmap.sticker_change));
            stickView.setRightTopBitmap(context.getDrawable(R.mipmap.sticker_copy));
            stickView.setLeftTopBitmap(context.getDrawable(R.drawable.sticker_delete));
            stickView.setRightBottomBitmap(context.getDrawable(R.mipmap.sticker_redact));
            stickView.setImageRes(gifTest, false);
            AnimStickerModel animStickerModel = new AnimStickerModel(context,viewLayerRelativeLayout,stickView);
            listForStickerView.add(animStickerModel);
            callback.ItemClickForStickView(animStickerModel);
        });
        List<String> test = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            test.add("啥");
        }
        TemplateGridViewAdapter gridAdapter = new TemplateGridViewAdapter(test, context);
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


    public void onDestroy(){
        isDestroy=true;
    }


    VideoTimelineAdapter frameAdapter;
    //裁剪起点与总时长的百分比，比如从20.5%的进度开始裁剪
    private float cropStartPoint=0;
    private boolean canScroll=false;
    public void initVideoProgressView(VideoFrameRecycler mTimeLineView) {
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) {
                    @Override
                    public boolean canScrollHorizontally() {
                        return canScroll;
                    }
                };
        mTimeLineView.setLayoutManager(layoutManager);
        frameAdapter=new VideoTimelineAdapter(context, Uri.fromFile(new File(mVideoPath)),() -> {
            if (canScroll){
                mTimeLineView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        if (newState==RecyclerView.SCROLL_STATE_IDLE){
                            cropStartPoint=1f*recyclerView.computeHorizontalScrollOffset()/recyclerView.computeHorizontalScrollRange();
                            LogUtil.d("scrollRange",String.valueOf(recyclerView.computeHorizontalScrollRange()));
                            LogUtil.d("scrollOffset",String.valueOf(recyclerView.computeHorizontalScrollOffset()));
                            LogUtil.d("cropStart",String.valueOf(cropStartPoint));
//                            calculateCrop();
//                            seekTo(Math.round(getDuration()*getCropStartRatio()));
                        }
                    }
                });
//                seekbarTime=60*1000;
//                seekbarPercent=1f*mTimeLineView.getWidth()/(frameAdapter.getItemWidth()*frameAdapter.getItemCount());
//                float timeRatio=1f*60*1000/getDuration();
//                float adjustRatio=timeRatio/seekbarPercent;
//                seekbarPercent*=adjustRatio;
//                mRangeSeekBarView.setMinDistance(Math.round(getDuration()*adjustRatio));
            }else {
//                seekbarTime=getDuration();
//                seekbarPercent=1f;
//                mRangeSeekBarView.setMinDistance(Math.round(getDuration()));
            }
//            calculateCrop();
//            //初始化进度指针的位置
//
//            //总长度除以(拖动条从左到右代表的时长)总时间（毫秒）=px/ms
//            float totalDistance=mRangeSeekBarView.getThumbs().get(1).getPos()-mRangeSeekBarView.getThumbs().get(0).getPos();
//            float pixelsPerMs=totalDistance/seekbarTime;
//            curOffset=pixelsPerMs*updateCursorIntervalMs;
//            ViewGroup.LayoutParams cursorLp=cursor.getLayoutParams();
//            cursorLp.height=mRangeSeekBarView.getMeasuredHeight();
//            cursor.setLayoutParams(cursorLp);
//            currentCursorStart=mRangeSeekBarView.getThumbs().get(1).getWidthBitmap();
//            cursor.setTranslationX(currentCursorStart);
//            cursor.setTranslationY(DimensionUtils.dp2px(1));
//
//            //初始化震动




//            vibrator = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
//            //初始化完成
//            fullyInitiated=true;
            mTimeLineView.setAdapter(frameAdapter);
        });

    }




    /**
     * description ：预览视频采用蓝松sdk提供的在预览功能
     * creation date: 2020/3/12
     * user : zhangtongju
     */
    public void toPrivateVideo(DrawPadView drawPadView) {
        this.mDrawPadView = drawPadView;
        StickerForParents stickerForParents= listForStickerView.get(0).getParameterData();

        startPlayVideo(stickerForParents);
    }

    /**
     * description ：保存视频采用蓝松sdk提供的在保存功能
     * creation date: 2020/3/12
     * user : zhangtongju
     */
    public void toSaveVideo(){
        StickerForParents stickerForParents= listForStickerView.get(0).getParameterData();
        backgroundDraw backgroundDraw=new backgroundDraw(context, mVideoPath, path -> {
            String albumPath = SaveAlbumPathModel.getInstance().getKeepOutput();
            try {
                FileUtil.copyFile(new File(path), albumPath);
                albumBroadcast(albumPath);
                showKeepSuccessDialog(albumPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        MediaInfo    mInfo = new MediaInfo(mVideoPath);
        backgroundDraw.toSaveVideo(mInfo.getDurationUs(),stickerForParents);

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




    private void showKeepSuccessDialog(String path) {
        if (!isDestroy && !DoubleClick.getInstance().isFastDoubleClick()) {
            AlertDialog.Builder builder = new AlertDialog.Builder( //去除黑边
                    new ContextThemeWrapper(context, R.style.Theme_Transparent));
            builder.setTitle(R.string.notification);
            builder.setMessage(context.getString(R.string.have_saved_to_sdcard) +
                    "【" + path + context.getString(R.string.folder) + "】");
            builder.setNegativeButton(context.getString(R.string.got_it), (dialog, which) -> {
                dialog.dismiss();
            });
            builder.setCancelable(true);
            Dialog dialog = builder.show();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    /**
     * VideoLayer是外部提供画面来源,
     * 您可以用你们自己的播放器作为画面输入源,也可以用原生的MediaPlayer,只需要视频播放器可以设置surface即可.
     * 一下举例是采用MediaPlayer作为视频输入源.
     */
    private void startPlayVideo( StickerForParents stickerForParents) {
        if (mVideoPath != null) {
            mplayer = new MediaPlayer();
            try {
                mplayer.setDataSource(mVideoPath);

            } catch (IOException e) {
                e.printStackTrace();
            }
            mplayer.setOnPreparedListener(mp -> initDrawPad( stickerForParents));
            mplayer.setOnCompletionListener(mp -> stopDrawPad());
            mplayer.prepareAsync();
        } else {
            LogUtil.d(TAG, "Null Data Source\n");
        }
    }


    /**
     * Step1: 开始运行 drawPad 容器
     */
    private void initDrawPad(StickerForParents stickerForParents) {
        mDrawPadView.setUpdateMode(DrawPadUpdateMode.AUTO_FLUSH, 30);
        // 设置当前DrawPad的宽度和高度,并把宽度自动缩放到父view的宽度,然后等比例调整高度.
        mDrawPadView.setDrawPadSize(DRAWPAD_WIDTH, DRAWPAD_HEIGHT, (viewWidth, viewHeight) -> {
            // 开始DrawPad的渲染线程.
            startDrawPad(stickerForParents);
        });
    }


    /**
     * Step2: 开始运行 Drawpad线程.
     */
    private void startDrawPad(StickerForParents stickerForParents) {
        if (mDrawPadView.startDrawPad()) {
            // 增加一个主视频的 VideoLayer
            mLayerMain = mDrawPadView.addMainVideoLayer(
                    mplayer.getVideoWidth(), mplayer.getVideoHeight(), null);
            if (mLayerMain != null) {
                mplayer.setSurface(new Surface(mLayerMain.getVideoTexture()));
            }
            mplayer.start();
            addMVLayer(stickerForParents);
        }
    }


    /**
     * 增加一个MV图层.
     */
    private void addMVLayer(StickerForParents stickerForParents) {
        String colorMVPath = CopyFileFromAssets.copyAssets(context, "laohu.mp4");
        String maskMVPath = CopyFileFromAssets.copyAssets(context, "mask.mp4");
        MVLayer mvLayer = mDrawPadView.addMVLayer(colorMVPath, maskMVPath); // <-----增加MVLayer
        mvLayer.setRotate(stickerForParents.getRoation());
        mvLayer.setScale(stickerForParents.getScale());
        mvLayerArrayList.add(mvLayer);
    }


    /**
     * Step3: 停止容器,停止后,为新的视频文件增加上音频部分.
     */
    private void stopDrawPad() {
        if (mDrawPadView != null && mDrawPadView.isRunning()) {
            mDrawPadView.stopDrawPad();
//            toastStop();
            if (LanSongFileUtil.fileExist(editTmpPath)) {
                String dstPath = AudioEditor.mergeAudioNoCheck(mVideoPath, editTmpPath, true);
//                findViewById(R.id.id_mvlayer_saveplay).setVisibility(View.VISIBLE);
                LogUtil.d(TAG, "dstPath=" + dstPath);
            } else {
                LogUtil.e(TAG, " player completion, but file:" + editTmpPath
                        + " is not exist!!!");
            }

            callback.hasPlayingComplete();
        }
    }


}

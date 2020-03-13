package com.flyingeffects.com.ui.model;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.TemplateGridViewAdapter;
import com.flyingeffects.com.adapter.TemplateViewPager;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.enity.StickerForParents;
import com.flyingeffects.com.manager.CopyFileFromAssets;
import com.flyingeffects.com.ui.interfaces.model.CreationTemplateMvpCallback;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.view.StickerView;
import com.lansosdk.box.DrawPad;
import com.lansosdk.box.DrawPadUpdateMode;
import com.lansosdk.box.MVLayer;
import com.lansosdk.box.VideoLayer;
import com.lansosdk.box.ViewLayerRelativeLayout;
import com.lansosdk.box.onDrawPadProgressListener;
import com.lansosdk.box.onDrawPadSizeChangedListener;
import com.lansosdk.videoeditor.AudioEditor;
import com.lansosdk.videoeditor.DrawPadView;
import com.lansosdk.videoeditor.LanSongFileUtil;
import com.lansosdk.videoeditor.MediaInfo;

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
    private String gifTest = "/storage/emulated/0/Android/data/com.tencent.mobileqq/Tencent/QQfile_recv/Comp-1.gif";
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
    private MediaInfo mInfo;
    private ArrayList<AnimStickerModel> listForStickerView = new ArrayList<>();


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

            stickView.setImageRes(gifTest, true);
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







    /**
     * description ：预览视频采用蓝松sdk提供的在预览功能
     * creation date: 2020/3/12
     * user : zhangtongju
     */
    public void toPrivateVideo(DrawPadView drawPadView) {
        this.mDrawPadView = drawPadView;
        StickerForParents stickerForParents= listForStickerView.get(0).getParameterData();
        mInfo = new MediaInfo(mVideoPath);
        startPlayVideo(stickerForParents);
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

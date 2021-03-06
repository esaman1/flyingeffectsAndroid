package com.flyingeffects.com.ui.view.activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.TemplateViewPager;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.databinding.ActivityJadeFontMakeBinding;
import com.flyingeffects.com.entity.CutSuccess;
import com.flyingeffects.com.entity.JadeTypeFace;
import com.flyingeffects.com.entity.SubtitleEntity;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.ui.interfaces.view.JadeFontMakeMvpView;
import com.flyingeffects.com.ui.presenter.JadeFontMakePresenter;
import com.flyingeffects.com.ui.view.dialog.LoadingDialog;
import com.flyingeffects.com.ui.view.fragment.JadeAdjustFragment;
import com.flyingeffects.com.utils.KeyBoardHelper;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.TimeUtils;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.utils.record.AutoIdentifySubtitlesDialog;
import com.flyingeffects.com.utils.screenUtil;
import com.flyingeffects.com.view.drag.CreationTemplateProgressBarView;
import com.flyingeffects.com.view.drag.JakeFontMakeSeekBarView;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.imaginstudio.imagetools.pixellab.GradientMaker;
import com.imaginstudio.imagetools.pixellab.TextObject.StickerItemOnitemclick;
import com.imaginstudio.imagetools.pixellab.TextObject.TextComponent;
import com.imaginstudio.imagetools.pixellab.ZoomWidget;
import com.imaginstudio.imagetools.pixellab.font.customTypeface;
import com.imaginstudio.imagetools.pixellab.functions.interval;
import com.imaginstudio.imagetools.pixellab.functions.spansIntervals;
import com.imaginstudio.imagetools.pixellab.imageinfo.displayInfo;
import com.imaginstudio.imagetools.pixellab.textContainer;
import com.lansosdk.videoeditor.MediaInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;


/**
 * @author ZhouGang
 * @date 2021/5/21
 * ???????????????activity
 */
public class JadeFontMakeActivity extends BaseActivity implements JakeFontMakeSeekBarView.SeekBarProgressListener,
        AutoIdentifySubtitlesDialog.OnIdentifySubtitleListener, JadeFontMakeMvpView, textContainer.OnSelectionChangedListener {

    private static final String TAG = "JadeFontMakeActivity";

    ImageView check_box_0;
    ImageView check_box_1;
    ImageView check_box_2;
    ImageView check_box_3;

    TextView tv_0;
    TextView tv_1;
    TextView tv_2;
    TextView tv_3;

    ActivityJadeFontMakeBinding mBinding;
    String mVideoPath;
    String mImagePath;
    /**
     * ???????????????
     */
    private boolean nowUiIsLandscape = false;
    private boolean isPlaying = false;
    private boolean isEndDestroy = false;
    private boolean isPlayComplete = false;
    private boolean isIntoPause = false;
    /**
     * ??????????????????
     */
    boolean mSeekBarViewManualDrag = false;
    SimpleExoPlayer exoPlayer;
    /**
     * 0??????????????? 2????????????????????????
     */
    private int musicChooseIndex = 0;
    /**
     * ?????????????????????????????????
     */
    private long musicStartTime = 0;
    /**
     * ?????????????????????????????????
     */
    private long musicEndTime;
    /**
     * ?????????????????????????????????????????????
     */
    private long allVideoDuration;
    /**
     * ????????????????????????????????????
     */
    private long mCutStartTime;

    /**
     * ????????????????????????????????????
     */
    private long mCutEndTime;
    /**
     * ??????????????????????????????
     */
    private long progressBarProgress;
    /**
     * ?????????????????????
     */
    private MediaPlayer bgmPlayer;
    private MediaSource mediaSource;
    /**
     * ???????????????????????????
     */
    private boolean isInitVideoLayer = false;

    AutoIdentifySubtitlesDialog mSubtitlesDialog;

    JadeFontMakePresenter mPresenter;

    String mChangeMusicPath = "";
    /**
     * ????????????????????????
     */
    private String bgmPath;
    List<View> listForInitBottom = new ArrayList<>();
    /**
     * ?????????view???id???????????????????????????
     */
    int mJadeFontViewIndex = 0;
    LoadingDialog mLoadingDialog;


    private displayInfo helperClass;
    public FrameLayout workingArea;
    public ZoomWidget zoomWidget;
    public textContainer textContain;
    private JadeAdjustFragment jadeAdjustFragment;
    private StickerItemOnitemclick stickerItemOnitemclick;
    private FragmentManager fragmentManager;
    ObjectAnimator animatorUp;
    ObjectAnimator animatorDown;
    private KeyBoardHelper keyBoardHelper;
    private boolean isFirstAdd = true;

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initView() {
        mBinding = ActivityJadeFontMakeBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);

        mVideoPath = getIntent().getStringExtra("videoPath");
        mImagePath = getIntent().getStringExtra("imagePath");
        mPresenter = new JadeFontMakePresenter(this, this, mVideoPath, mImagePath);

        mLoadingDialog = buildLoadingDialog();
        getLifecycle().addObserver(mLoadingDialog);
        setOnClickListener();
        setDefaultVideoPlayerView();
        setProgressBarListener();
        mBinding.jakeFontSeekBarView.setProgressListener(this);
        mSubtitlesDialog = new AutoIdentifySubtitlesDialog(this);
        mSubtitlesDialog.setSubtitleListener(this);

        initJadeAdjustView();


    }


    private LoadingDialog buildLoadingDialog() {
        return LoadingDialog.getBuilder(this)
                .setHasAd(true)
                .setTitle("?????????????????????")
                .setMessage("??????????????????????????????")
                .build();
    }

    private int mWindowHeight = 0;

    private void initJadeAdjustView() {
        JadeAdjustFragment.onAdjustParamsChangeCallBack onAdjustParamsChangeCallBack = new JadeAdjustFragment.onAdjustParamsChangeCallBack() {
            //?????????????????????
            @Override
            public void onInnerColorChange(boolean enabled, float radius, float dx, float dy, int color) {
                try {
                    TextComponent currentText = JadeFontMakeActivity.this.textContain.getCurrentText();
                    if (currentText != null) {
                        currentText.setInnerShadow(enabled, radius, dx, dy, color);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onEmossChange(boolean enabled, int LightAngle, int Intensity, int Ambient, int Hardness, int Bevel) {
                try {
                    TextComponent currentText = JadeFontMakeActivity.this.textContain.getCurrentText();
                    if (currentText != null) {
                        currentText.setEmboss(enabled, LightAngle, Intensity, Ambient, Hardness, Bevel);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void on3Dchange(int Depth, int DepthDarken, int Quality, boolean StokeInclude, int obliqueAngle, int color) {
                try {
                    TextComponent currentText = JadeFontMakeActivity.this.textContain.getCurrentText();
                    if (currentText != null) {
                        currentText.set3dEnabled(true);
                        currentText.set3dViewType(3);
                        currentText.set3dDepth(((int) Depth), DepthDarken, Quality, true);
                        currentText.set3dDepthColor(1, color, null, false);
                        currentText.set3dObliqueAngle(obliqueAngle);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTextColorChange(int color, int start, int end, boolean isSimple) {
                TextComponent currentText = JadeFontMakeActivity.this.textContain.getCurrentText();
                if (currentText != null) {
                    if (isSimple) {
                        currentText.setColorFill(color);
                    } else {
                        currentText.setColorFill(color, start, end);
                    }
                }

            }

            @Override
            public void onTextColorChange(GradientMaker.GradientFill gradientFill) {
                TextComponent currentText = JadeFontMakeActivity.this.textContain.getCurrentText();
                if (currentText != null) {
                    currentText.setGradientFill(gradientFill);
                }
            }

            @Override
            public void onTypeFaceChange(String path) {
                TextComponent currentText = JadeFontMakeActivity.this.textContain.getCurrentText();
                if (currentText != null) {
                    spansIntervals spans = new spansIntervals();
                    spans.addInterval(new interval(0, currentText.returnText().length(), new customTypeface(path, createFontFromPath(path))));
                    currentText.setTextFont(spans);
                }
            }

            @Override
            public void onJadeTypeFaceChange(JadeTypeFace jadeTypeFace) {
                try {
                    TextComponent currentText = JadeFontMakeActivity.this.textContain.getCurrentText();
                    if (currentText != null) {
                        JadeTypeFace.DetailBean detail = jadeTypeFace.getDetail();
                        if (detail != null) {
                            JadeTypeFace.DetailBean.Font3DBean font_3D = detail.getFont_3D();
                            if (
                                    detail.getIn_bright() != null &&
                                            detail.getRelief() != null &&
                                            font_3D != null
                            ) {
                                currentText.setColorFill(Color.parseColor(jadeTypeFace.getColor()));

                                int fuzzy_radius = Integer.parseInt(detail.getIn_bright().getFuzzy_radius());
                                int x = Integer.parseInt(detail.getIn_bright().getHorizontal_shift());
                                int y = Integer.parseInt(detail.getIn_bright().getVertical_offset());
//                                int bright_color = Integer.parseInt(detail.getIn_bright().getBright_color());
                                int bright_color = Color.parseColor(jadeTypeFace.getIn_color());
                                boolean enabled1 = detail.getIn_bright().isEnable();
                                currentText.setInnerShadow(enabled1, fuzzy_radius, x, y, bright_color);

                                int LightAngle = Integer.parseInt(detail.getRelief().getIllumination_angle());
                                int Intensity = Integer.parseInt(detail.getRelief().getIllumination_intensity());
                                int Bevel = Integer.parseInt(detail.getRelief().getOblique_angle());
                                boolean enabled2 = detail.getRelief().isEnable();
                                currentText.setEmboss(enabled2, LightAngle, Intensity, 100, 100, Bevel);

                                int obliqueAngle = Integer.parseInt(font_3D.getAngle());
                                int Depth = Integer.parseInt(detail.getFont_3D().getDepth());
//                                int Depth = 3;
//                                int color = Integer.parseInt(detail.getFont_3D().getColor());
                                boolean enabled3 = font_3D.isEnable();
                                currentText.set3dViewType(3);
                                currentText.set3dDepth(((int) Depth), 20, 1, true);
                                currentText.set3dDepthColor(1, Color.parseColor(jadeTypeFace.getIn_color()), null, true);
                                currentText.set3dObliqueAngle(obliqueAngle);
                                currentText.set3dEnabled(enabled3);
                            }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        jadeAdjustFragment = new JadeAdjustFragment(onAdjustParamsChangeCallBack);
        jadeAdjustFragment.setOnInputChangeCallBack(new JadeAdjustFragment.OnInputChangeCallBack() {
            @Override
            public void onChange(String string) {
                if (textContain.getCurrentText() != null) {
                    TextComponent textComponent = textContain.getCurrentText();
                    int index = textComponent.isSubTitle() ? textComponent.getId() : -1;
                    int id = textComponent.getmJadeFontViewIndex();
                    mBinding.jakeFontSeekBarView.modifyMaterialOrSubtitle(
                            "", Integer.toString(id), index, textComponent.isSubTitle(), string);
                    textContain.getCurrentText().setText(string, false);
                }
            }
        });
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.jade_fragment_container, jadeAdjustFragment);
        fragmentTransaction.hide(jadeAdjustFragment);
        fragmentTransaction.commit();

        this.textContain = new textContainer(getApplicationContext());
        helperClass = new displayInfo(mBinding.idVviewRealtimeGllayout, this.zoomWidget);
        helperClass.setTextContain(this.textContain);
        mBinding.idVviewRealtimeGllayout.addView(this.textContain, new RelativeLayout.LayoutParams(-1, -1));

        this.textContain.setSelectionListener(this);
        stickerItemOnitemclick = new StickerItemOnitemclick() {
            @Override
            public void stickerOnclick(int type, TextComponent textComponent) {
                switch (type) {
                    case TextComponent.ACTION_TYPE_DELETE:
                        fragmentTransaction.hide(jadeAdjustFragment);
                        textContain.removeView(textContain.getCurrentText());
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.commit();
                        break;
                    case TextComponent.ACTION_TYPE_EDIT:
                        FragmentTransaction fragmentTransactionEdit = fragmentManager.beginTransaction();
                        fragmentTransactionEdit.show(jadeAdjustFragment);
                        fragmentTransactionEdit.commit();

                        //???????????????
                        if (jadeAdjustFragment.getInputEdit() != null) {
                            InputMethodManager imm = (InputMethodManager) JadeFontMakeActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(jadeAdjustFragment.getInputEdit(), 0);
                        }
                        break;
                }

                seekBarViewIsShow(true);

                if (textComponent.isSubTitle()) {
                    mBinding.jakeFontSeekBarView.deleteSubtitleMaterialItemView(
                            Integer.toString(textComponent.getId()), textComponent.getmJadeFontViewIndex());
                } else {
                    mBinding.jakeFontSeekBarView.deleteTemplateMaterialItemView(Integer.toString(textComponent.getmJadeFontViewIndex()));
                }
            }

            @Override
            public void stickerMove(TextComponent textComponent) {

            }
        };
        keyBoardHelper = new KeyBoardHelper(this);
        keyBoardHelper.setOnKeyBoardStatusChangeListener(new KeyBoardHelper.OnKeyBoardStatusChangeListener() {
            @Override
            public void OnKeyBoardPop(int keyBoardheight) {
                Log.d(TAG, "OnKeyBoardPop() called with: keyBoardheight = [" + keyBoardheight + "]");
//                rootView.scrollTo(0, keyBoardheight - loginViewtoBottom);
                //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                int translationY = keyBoardheight - screenUtil.dip2px(BaseApplication.getInstance(), 200);
                animatorUp = ObjectAnimator.ofFloat(mBinding.jadeFragmentContainer, "translationY", 0, -translationY);
                animatorUp.setDuration(360);
                animatorUp.setInterpolator(new AccelerateDecelerateInterpolator());
                animatorUp.start();

                int selectedTabPosition = jadeAdjustFragment.getTabLayout().getSelectedTabPosition();
                if (selectedTabPosition != 0) {
                    jadeAdjustFragment.getTabLayout().selectTab(jadeAdjustFragment.getTabLayout().getTabAt(0));
                }

            }

            @Override
            public void OnKeyBoardClose(int oldKeyBoardheight) {
                Log.d(TAG, "OnKeyBoardClose() called with: oldKeyBoardheight = [" + oldKeyBoardheight + "]");
//                rootView.scrollTo(0, 0);
                int translationY = oldKeyBoardheight - screenUtil.dip2px(BaseApplication.getInstance(), 200);
                animatorDown = ObjectAnimator.ofFloat(mBinding.jadeFragmentContainer, "translationY", -translationY, 0);
                animatorDown.setDuration(360);
                animatorDown.setInterpolator(new AccelerateDecelerateInterpolator());
                animatorDown.start();

                int selectedTabPosition = jadeAdjustFragment.getTabLayout().getSelectedTabPosition();
                if (selectedTabPosition == 0) {
                    jadeAdjustFragment.getTabLayout().selectTab(jadeAdjustFragment.getTabLayout().getTabAt(jadeAdjustFragment.getLastPosition()));
                }

            }
        });

//        mBinding.rlCreationContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                Rect rect = new Rect();
//                // ??????root????????????????????????
//                getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
//
////                int height = rect.height();
////                if (mWindowHeight == 0) {
////                    //?????????????????????????????????????????????
////                    mWindowHeight = height;
////                } else {
////                    if (mWindowHeight != height) {
////                        //????????????????????????????????????????????????
////                        int softKeyboardHeight = mWindowHeight - height;
////                        System.out.println("SoftKeyboard height = " + softKeyboardHeight);
////                    }
////                }
//
//
//                Log.d(TAG, "??????root????????????????????????() called" + rect.toString());
//                // ??????root?????????????????????????????????(?????????View?????????????????????)
//                int rootInvisibleHeight = mBinding.rlCreationContainer.getRootView().getHeight() - rect.bottom;
//                Log.d(TAG, "??????root?????????????????????????????????() called" + rootInvisibleHeight);
//                // ??????????????????????????????200??????????????????,??????????????????????????????
//                if (rootInvisibleHeight > 200) {
//                    // ???????????????
//                    int srollHeight = rootInvisibleHeight - (mBinding.rlCreationContainer.getBottom() - jadeAdjustFragment.getTabLayout().getBottom()) - screenUtil.getNavigationBarHeight();
//                    if (srollHeight > 0) {//??????????????????????????????
//                        mBinding.jadeFragmentContainer.scrollTo(0, srollHeight);
////                        mBinding.jadeFragmentContainer.animate()
////                                .translationY(-srollHeight)
////                                .setDuration(200)
////                                .start();
//                        Log.d(TAG, "????????????() called" + rootInvisibleHeight);
//
//                    }
//                } else {
//                    // ???????????????
//                    mBinding.jadeFragmentContainer.scrollTo(0, 0);
//                }
//            }
//        });
    }

    private void addJadeFont() {
        //????????????id???-1
        long endFontTime = Math.min(getCurrentPos() + 5000, allVideoDuration);
        mBinding.jakeFontSeekBarView.addTemplateMaterialItemView(mCutEndTime, "", getCurrentPos(), endFontTime, true,
                "????????????", mJadeFontViewIndex, null, -1, mBinding.progressBarView.progressTotalWidth);
        //todo ??????????????????????????????view ????????????mJadeFontViewIndex???ID  ????????????id????????????????????????????????????


        int initColor = Color.parseColor("#ffffff");
        Drawable leftTopD = ContextCompat.getDrawable(JadeFontMakeActivity.this, R.drawable.ic_jade_close);
        Drawable leftBottomD = ContextCompat.getDrawable(JadeFontMakeActivity.this, R.drawable.ic_jade_edit);
        Drawable rightBottomD = ContextCompat.getDrawable(JadeFontMakeActivity.this, R.mipmap.sticker_redact);
        TextComponent new_text = new TextComponent(JadeFontMakeActivity.this,
                initColor,
                "reference",
                helperClass,
                leftTopD,
                rightBottomD,
                leftBottomD);
        new_text.setId(-1);
        new_text.setSubTitle(false);
        new_text.setStartTime(getCurrentPos());
        new_text.setEndTime(endFontTime);
        new_text.setContent("????????????");
        new_text.setmJadeFontViewIndex(mJadeFontViewIndex);
        textContain.addNewText(new_text, stickerItemOnitemclick, helperClass);

        mJadeFontViewIndex++;
    }

    public Typeface createFontFromPath(String path) {
        Typeface t = Typeface.DEFAULT;
        try {
            return Typeface.createFromAsset(getAssets(), path);
        } catch (Exception e) {
            try {
                return Typeface.createFromFile(path);
            } catch (Exception e2) {
                return t;
            }
        }
    }

    @Override
    protected void initAction() {
        //????????????????????????
        initViewLayerRelative();
        initBottomLayout();
        EventBus.getDefault().register(this);
    }

    private void initBottomLayout() {
        initViewForChooseMusic();
        TemplateViewPager templateViewPager = new TemplateViewPager(listForInitBottom);
        mBinding.viewPager.setAdapter(templateViewPager);
    }


    private void setOnClickListener() {
        mBinding.tvTopSubmit.setOnClickListener(this::onViewClicked);
        mBinding.llPlay.setOnClickListener(this::onViewClicked);
        mBinding.ivAddSticker.setOnClickListener(this::onViewClicked);
        mBinding.ivTopBack.setOnClickListener(this::onViewClicked);
        mBinding.ivChangeUi.setOnClickListener(this::onViewClicked);
        mBinding.tvAddWord.setOnClickListener(this::onViewClicked);
        mBinding.tvIdentifySubtitles.setOnClickListener(this::onViewClicked);
        mBinding.tvChangeMusic.setOnClickListener(this::onViewClicked);
        mBinding.rlCreationContainer.setOnClickListener(this::onViewClicked);
    }

    private void onViewClicked(View view) {
        if (!DoubleClick.getInstance().isFastDoubleClick()) {
            if (view == mBinding.tvTopSubmit) {
                videoToPause();
                mPresenter.saveVideo(mCutStartTime,mCutEndTime,nowUiIsLandscape,percentageH,textContain);
            } else if (view == mBinding.llPlay) {
                onPlayClick();
            } else if (view == mBinding.ivAddSticker) {
//            addSticker();
            } else if (view == mBinding.ivTopBack) {
                onBackPressed();
            } else if (view == mBinding.tvAddWord) {
                onClickAddWordBtn();
            } else if (view == mBinding.tvIdentifySubtitles) {
                videoToPause();
                if (!TextUtils.isEmpty(mImagePath)) {
                    if (TextUtils.isEmpty(bgmPath)) {
                        ToastUtil.showToast("???????????????????????????????????????~");
                    } else {
                        mSubtitlesDialog.show();
                    }
                } else {
                    if (musicChooseIndex == 2) {
                        if (TextUtils.isEmpty(bgmPath)) {
                            ToastUtil.showToast("??????????????????????????????~");
                        } else {
                            mSubtitlesDialog.show();
                        }
                    } else {
                        mSubtitlesDialog.show();
                    }
                }
            } else if (view == mBinding.tvChangeMusic) {
                onClickMusicBtn();
            } else if (view == mBinding.ivChangeUi) {
                changeLandscape();
            } else if (view == mBinding.rlCreationContainer) {
                mBinding.progressBarView.hindArrow();
            }
        }
    }

    private static final int[] LIN_ID = {R.id.tv_add_word, R.id.tv_change_music};

    private void setTextColor(int chooseItem) {
        for (int value : LIN_ID) {
            ((TextView) findViewById(value)).setTextColor(ContextCompat.getColor(this, R.color.white));
        }
        if (chooseItem >= 0) {
            ((TextView) findViewById(LIN_ID[chooseItem])).setTextColor(Color.parseColor("#5496FF"));
        }
    }

    /**
     * ????????????????????????
     *
     * @param isShow
     */
    private void seekBarViewIsShow(boolean isShow) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mBinding.llProgress.getLayoutParams();
        if (isShow) {
            layoutParams.addRule(RelativeLayout.ABOVE, R.id.rl_seek_bar);
            mBinding.viewPager.setVisibility(View.GONE);
            mBinding.rlSeekBar.setVisibility(View.VISIBLE);
            setTextColor(-1);
        } else {
            layoutParams.addRule(RelativeLayout.ABOVE, R.id.viewPager);
            mBinding.viewPager.setVisibility(View.VISIBLE);
            mBinding.rlSeekBar.setVisibility(View.GONE);
        }
        mBinding.llProgress.setLayoutParams(layoutParams);
    }

    public void chooseTab(int pageNum) {
        mBinding.viewPager.setCurrentItem(pageNum, false);
    }

    /**
     * ????????????
     */
    private void onClickMusicBtn() {
        seekBarViewIsShow(false);
        chooseTab(1);
        setTextColor(1);
    }

    private void onClickAddWordBtn() {
        videoToPause();
        seekBarViewIsShow(true);
        setTextColor(-1);


        addJadeFont();


    }


    boolean isPlayVideoInAudio = false;

    /**
     * /**
     * ????????????????????????
     */
    private void initViewForChooseMusic() {
        //????????????
        View viewForChooseMusic = LayoutInflater.from(this).inflate(R.layout.view_choose_music, mBinding.viewPager, false);

        TextView tvAddMusic = viewForChooseMusic.findViewById(R.id.tv_add_music);
        TextView tvDownMusic = viewForChooseMusic.findViewById(R.id.iv_down_music);

        tvDownMusic.setVisibility(View.VISIBLE);
        tvDownMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBarViewIsShow(true);
            }
        });

        tvAddMusic.setOnClickListener(view -> {
            isPlayVideoInAudio = false;
            videoToPause();
            Intent intent = new Intent(this, ChooseMusicActivity.class);
            intent.putExtra("needDuration", allVideoDuration);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
        tv_0 = viewForChooseMusic.findViewById(R.id.tv_0);
        tv_1 = viewForChooseMusic.findViewById(R.id.tv_1);
        tv_2 = viewForChooseMusic.findViewById(R.id.tv_2);
        tv_3 = viewForChooseMusic.findViewById(R.id.tv_3);

        check_box_0 = viewForChooseMusic.findViewById(R.id.iv_check_box_0);
        check_box_1 = viewForChooseMusic.findViewById(R.id.iv_check_box_1);
        check_box_2 = viewForChooseMusic.findViewById(R.id.iv_check_box_2);
        check_box_3 = viewForChooseMusic.findViewById(R.id.iv_check_box_3);

        tv_0.setText("????????????");
        tv_1.setText("????????????");
        tv_2.setText("????????????");

        //???????????????????????????????????? ?????????????????????
        if (!TextUtils.isEmpty(mImagePath)) {
            tv_0.setVisibility(View.GONE);
            check_box_0.setVisibility(View.GONE);
        }
        tv_1.setVisibility(View.GONE);
        check_box_1.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(mVideoPath)) {
            isPlayVideoInAudio = true;
            chooseCheckBox(0);
        }

        View.OnClickListener tvMusicListener = view -> {
            switch (view.getId()) {
                case R.id.iv_check_box_0:
                case R.id.tv_0:
                    musicChooseIndex = 0;
                    if (!TextUtils.isEmpty(mVideoPath)) {
                        if (isPlayVideoInAudio) {
                            clearCheckBox();
                            isPlayVideoInAudio = false;
                            if (exoPlayer != null) {
                                exoPlayer.setVolume(0f);
                            }
                            getBgmPath("");
                        } else {
                            mPresenter.chooseVideoInAudio(0);
                            isPlayVideoInAudio = true;
                        }
                    } else {
                        ToastUtil.showToast("???????????????????????????");
                    }
                    break;
                case R.id.tv_1:
                case R.id.iv_check_box_1:
                    mPresenter.chooseNowStickerMaterialMusic(1);
                    break;
                case R.id.tv_2:
                case R.id.iv_check_box_2:
                    isPlayVideoInAudio = false;
                    mPresenter.extractedAudio(mChangeMusicPath, 2);
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

        listForInitBottom.add(viewForChooseMusic);
    }

    /**
     * ???????????????
     */
    private void changeLandscape() {
        //???????????????
        nowUiIsLandscape = !nowUiIsLandscape;
        if (!TextUtils.isEmpty(mImagePath)) {
            setImageBackSize(nowUiIsLandscape);
        } else {
            setPlayerViewSize(nowUiIsLandscape);
        }
    }

    /**
     * ??????????????????????????????
     */
    boolean isInitLandscape = false;

    /**
     * ????????????????????????
     */
    private void setDefaultVideoPlayerView() {
        //????????????????????????????????????
        if (!TextUtils.isEmpty(mVideoPath)) {
            //?????????????????????????????????????????????
            MediaInfo mediaInfo = new MediaInfo(mVideoPath);
            mediaInfo.prepare();
            nowUiIsLandscape = !mediaInfo.isPortVideo();
            isInitLandscape = !mediaInfo.isPortVideo();
            mediaInfo.release();
            setPlayerViewSize(nowUiIsLandscape);
            initExo(mVideoPath);
        } else {
            mBinding.rlBackImage.setVisibility(View.VISIBLE);
            mBinding.relativePlayerView.setVisibility(View.INVISIBLE);
            setImageBackSize(false);
            if (!TextUtils.isEmpty(mImagePath)) {
                Glide.with(this).load(mImagePath).into(mBinding.ivBackImage);
            }
        }
        //??????????????????????????????????????????
        if (nowUiIsLandscape) {
            new Handler().postDelayed(() -> setPlayerViewSize(nowUiIsLandscape), 500);
        }
    }

    /**
     * ???????????????
     */
    private void setProgressBarListener() {
        mBinding.progressBarView.setProgressListener(new CreationTemplateProgressBarView.SeekBarProgressListener() {
            @Override
            public void progress(long progress, boolean isDrag) {
                LogUtil.d("OOM4", "mProgressBarViewProgress=" + progress);
                setGSYVideoProgress(progress);

                if (progress < mCutStartTime) {
                    progress = mCutStartTime;
                }

                if (progress > mCutEndTime) {
                    progress = mCutEndTime;
                }

                if (isDrag) {
                    mSeekBarViewManualDrag = false;
                }

                if (!mSeekBarViewManualDrag) {
                    mBinding.jakeFontSeekBarView.dragScrollView = false;
                    mBinding.jakeFontSeekBarView.scrollToPosition(progress);
                }
                progressBarProgress = progress;
                mPresenter.getNowPlayingTimeViewShow(textContain, progressBarProgress, mCutEndTime);
                mBinding.tvCurrentTime.setText(String.format("%ss", TimeUtils.timeParse(progress - mCutStartTime)));
            }

            @Override
            public void cutInterval(long starTime, long endTime, boolean isDirection) {
                videoToPause();
                if (starTime < mCutStartTime) {
                    mBinding.tvCurrentTime.setText(String.format("%ss", TimeUtils.timeParse(0)));
                    mCutStartTime = starTime;
                } else {
                    mCutStartTime = starTime;
                    mBinding.tvCurrentTime.setText(String.format("%ss", TimeUtils.timeParse(progressBarProgress - mCutStartTime)));
                }

                mCutEndTime = endTime;

                mBinding.tvTotal.setText(String.format("%ss", TimeUtils.timeParse(mCutEndTime - mCutStartTime)));
                mBinding.jakeFontSeekBarView.setCutStartAndEndTime(starTime, endTime);
                stickerTimeLineOffset();
//                LogUtil.d("oom44", "musicStartTime=" + musicStartTime + "starTime=" + starTime + "musicEndTime=" + musicEndTime + "mCutStartTime=" + mCutStartTime);

                if (isDirection) {
                    mBinding.jakeFontSeekBarView.scrollToPosition(starTime);
                    //--------------ztj   ??????bug???????????????????????????????????????????????????
                    if (musicStartTime < starTime) {
                        musicStartTime = starTime;
                        LogUtil.d("oom44", "musicStartTime=" + musicStartTime + "starTime=" + starTime + "musicEndTime=" + musicEndTime + "mCutStartTime=" + mCutStartTime);
                    }
                    //ztj  ??????????????? ?????????????????????????????????1000+end
                    if (musicEndTime < starTime) {
                        musicEndTime = musicStartTime + 1000;
                        LogUtil.d("oom44", "???????????????musicEndTime=" + musicEndTime + "musicStartTime=" + musicStartTime);
                    }

                } else {
                    LogUtil.d("oom444", "xx=");
                    mBinding.jakeFontSeekBarView.scrollToPosition(endTime);
                }

                mPresenter.getNowPlayingTimeViewShow(textContain, progressBarProgress, mCutEndTime);
            }

            @Override
            public void onTouchEnd() {
                videoToPause();
                mPresenter.getNowPlayingTimeViewShow(textContain, progressBarProgress, mCutEndTime);
            }
        });
    }

    /**
     * ?????????????????????,????????????????????????????????????????????????????????????ScrollView
     * ??????????????????????????????2????????????????????????
     */
    private float percentageH = 0;

    private void setPlayerViewSize(boolean isLandscape) {

        videoToPause();
        //???????????????
        LinearLayout.LayoutParams relativeLayoutParams = (LinearLayout.LayoutParams) mBinding.exoPlayer.getLayoutParams();
        float oriRatio = 9f / 16f;

        if (isLandscape) {
            //???????????????
            mBinding.scrollView.post(() -> {
                int oriWidth = mBinding.llSpace.getWidth();
                RelativeLayout.LayoutParams relativeLayoutParams2 = (RelativeLayout.LayoutParams) mBinding.scrollView.getLayoutParams();
                relativeLayoutParams2.width = oriWidth;
                relativeLayoutParams2.height = Math.round(1f * oriWidth * oriRatio);
                mBinding.scrollView.setLayoutParams(relativeLayoutParams2);
                relativeLayoutParams.width = oriWidth;
                if (isInitLandscape) {
                    relativeLayoutParams.height = Math.round(1f * oriWidth * oriRatio);
                } else {
                    relativeLayoutParams.height = Math.round(1f * oriWidth / oriRatio);
                }
                mBinding.exoPlayer.setLayoutParams(relativeLayoutParams);
                //????????????????????????
                mBinding.idVviewRealtimeGllayout.setLayoutParams(relativeLayoutParams2);
                RelativeLayout.LayoutParams contentParam = (RelativeLayout.LayoutParams) mBinding.relativeContentAllContent2.getLayoutParams();
                contentParam.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                contentParam.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                contentParam.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                mBinding.relativeContentAllContent2.setLayoutParams(contentParam);

            });
        } else {
            //?????????????????????????????????
            mBinding.scrollView.post(() -> {
                RelativeLayout.LayoutParams relativeLayoutParams2 = (RelativeLayout.LayoutParams) mBinding.scrollView.getLayoutParams();

                int height = mBinding.llSpace.getHeight();
                relativeLayoutParams2.height = height;
                relativeLayoutParams2.width = Math.round(1f * height * oriRatio);
                mBinding.scrollView.setLayoutParams(relativeLayoutParams2);

                relativeLayoutParams.width = Math.round(1f * height * oriRatio);
                relativeLayoutParams.height = height;
                mBinding.exoPlayer.setLayoutParams(relativeLayoutParams);
                //????????????????????????
                mBinding.idVviewRealtimeGllayout.setLayoutParams(relativeLayoutParams2);
                RelativeLayout.LayoutParams contentParam = (RelativeLayout.LayoutParams) mBinding.relativeContentAllContent2.getLayoutParams();
                contentParam.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                contentParam.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                contentParam.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                mBinding.relativeContentAllContent2.setLayoutParams(contentParam);

            });
        }

        mBinding.scrollView.setOnScrollListener(scrollY -> {
            int totalHeight = mBinding.scrollView.getChildAt(0).getHeight();
            percentageH = scrollY / (float) totalHeight;
            LogUtil.d("OOM3", "percentageH" + percentageH);
        });

        new Handler().postDelayed(() -> {
            if (isLandscape) {
                int height = Math.round(1f * mBinding.llSpace.getWidth() / oriRatio);
                mBinding.scrollView.scrollTo(0, height / 2 - mBinding.scrollView.getHeight() / 2);
            }
        }, 500);
    }

    private void setImageBackSize(boolean isLandscape) {

        videoToPause();
        //???????????????
        ViewGroup.LayoutParams relativeLayoutParams = mBinding.ivBackImage.getLayoutParams();
        float oriRatio = 9f / 16f;

        if (isLandscape) {
            //???????????????
            mBinding.svBackImage.post(() -> {
                int oriWidth = mBinding.llSpace.getWidth();
                int spaceHeight = mBinding.llSpace.getHeight();

                RelativeLayout.LayoutParams relativeLayoutParams2 = (RelativeLayout.LayoutParams) mBinding.svBackImage.getLayoutParams();
                relativeLayoutParams2.width = oriWidth;
                relativeLayoutParams2.height = Math.round(1f * oriWidth * oriRatio);
                mBinding.svBackImage.setLayoutParams(relativeLayoutParams2);

                relativeLayoutParams.width = oriWidth;
                relativeLayoutParams.height = Math.round(1f * oriWidth / oriRatio);
                mBinding.ivBackImage.setLayoutParams(relativeLayoutParams);

                //????????????????????????
                mBinding.idVviewRealtimeGllayout.setLayoutParams(relativeLayoutParams2);
                RelativeLayout.LayoutParams contentParam = (RelativeLayout.LayoutParams) mBinding.relativeContentAllContent2.getLayoutParams();
                contentParam.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                contentParam.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                contentParam.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);

                mBinding.relativeContentAllContent2.setLayoutParams(contentParam);
            });


        } else {
            //?????????????????????????????????
            mBinding.svBackImage.post(() -> {
                RelativeLayout.LayoutParams relativeLayoutParams2 = (RelativeLayout.LayoutParams) mBinding.svBackImage.getLayoutParams();

                int height = mBinding.llSpace.getHeight();
                int width = mBinding.llSpace.getWidth();


                relativeLayoutParams2.height = height;
                relativeLayoutParams2.width = Math.round(1f * height * oriRatio);
                mBinding.svBackImage.setLayoutParams(relativeLayoutParams2);

                relativeLayoutParams.width = Math.round(1f * height * oriRatio);
                relativeLayoutParams.height = height;
                mBinding.ivBackImage.setLayoutParams(relativeLayoutParams);
                //????????????????????????
                mBinding.idVviewRealtimeGllayout.setLayoutParams(relativeLayoutParams2);
                RelativeLayout.LayoutParams contentParam = (RelativeLayout.LayoutParams) mBinding.relativeContentAllContent2.getLayoutParams();
                contentParam.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                contentParam.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                contentParam.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);

                mBinding.relativeContentAllContent2.setLayoutParams(contentParam);

            });
        }

        mBinding.svBackImage.setOnScrollListener(scrollY -> {
            int totalHeight = mBinding.svBackImage.getChildAt(0).getHeight();
            int svBackImageHeight = mBinding.svBackImage.getHeight();
            percentageH = scrollY / (float) (totalHeight - svBackImageHeight);
        });

        new Handler().postDelayed(() -> {
            if (isLandscape) {
                int height = Math.round(1f * mBinding.llSpace.getWidth() / oriRatio);
                mBinding.svBackImage.scrollTo(0, height / 2 - mBinding.svBackImage.getHeight() / 2);
            }
        }, 500);
    }

    /**
     * description ???????????????????????????
     * date: ???2019/11/18 20:24
     * author: ????????? @?????? jutongzhang@sina.com
     */
    private void initViewLayerRelative() {
        ViewGroup.LayoutParams relativeLayoutParams = mBinding.idVviewRealtimeGllayout.getLayoutParams();
        ViewGroup.LayoutParams relativeLayoutParams2 = mBinding.relativeContentAllContent2.getLayoutParams();

        float oriRatio;
        oriRatio = 9f / 16f;
        //????????????mContainer????????????0
        mBinding.idVviewRealtimeGllayout.post(() -> {
            int oriHeight = nowUiIsLandscape ? mBinding.idVviewRealtimeGllayout.getWidth() : mBinding.idVviewRealtimeGllayout.getHeight();
            relativeLayoutParams.width = Math.round(1f * oriHeight * oriRatio);
            relativeLayoutParams.height = oriHeight;
            mBinding.idVviewRealtimeGllayout.setLayoutParams(relativeLayoutParams);
            mBinding.relativeContentAllContent2.setLayoutParams(relativeLayoutParams);
            mBinding.relativeContentAllContent2.setGravity(Gravity.CENTER_HORIZONTAL);
            mBinding.relativeContentAllContent.setGravity(Gravity.CENTER_HORIZONTAL);
            relativeLayoutParams2.width = relativeLayoutParams.width;
            relativeLayoutParams2.height = relativeLayoutParams.height;
            mBinding.idVviewRealtimeGllayout.setLayoutParams(relativeLayoutParams2);
            mBinding.llContentPatents.setGravity(Gravity.CENTER_HORIZONTAL);
        });

        setVideoBJOrImageBJPath();
    }

    /**
     * ????????????????????????????????????
     */
    public void setVideoBJOrImageBJPath() {
        if (!TextUtils.isEmpty(mVideoPath)) {
            MediaInfo mediaInfo = new MediaInfo(mVideoPath);
            mediaInfo.prepare();
            allVideoDuration = (long) (mediaInfo.vDuration * 1000);
            mediaInfo.release();
        } else {
            allVideoDuration = 5 * 1000;
        }

        mBinding.progressBarView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mBinding.progressBarView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mCutStartTime = 0;
                mCutEndTime = allVideoDuration;
                musicStartTime = 0;
                musicEndTime = allVideoDuration;
                mBinding.tvTotal.setText(TimeUtils.timeParse(mCutEndTime - mCutStartTime) + "s");
                mBinding.progressBarView.addProgressBarView(allVideoDuration, !TextUtils.isEmpty(mVideoPath) ? mVideoPath : mImagePath);
            }
        });
    }

    private void initExo(String videoPath) {
        if (TextUtils.isEmpty(videoPath)) {
            return;
        }

        exoPlayer = new SimpleExoPlayer.Builder(this)
                .build();
        mBinding.exoPlayer.setPlayer(exoPlayer);
        //??????????????????
        mBinding.exoPlayer.setUseController(false);
        exoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
        exoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState) {
                    case Player.STATE_READY:
                        break;
                    case Player.STATE_ENDED:
                        videoToStart();
                        break;
                    case Player.STATE_BUFFERING:
                    case Player.STATE_IDLE:
                    default:
                        break;
                }
            }
        });

        mediaSource = new ProgressiveMediaSource.Factory(
                new DefaultDataSourceFactory(JadeFontMakeActivity.this, "exoplayer-codelab")).
                createMediaSource(new MediaItem.Builder()
                        .setUri(Uri.fromFile(new File(videoPath))).build());

        exoPlayer.setMediaSource(mediaSource, true);
        exoPlayer.prepare();

        pauseExoPlayer();
    }

    private void setGSYVideoProgress(long progress) {
        LogUtil.d("OOM", "videoProgress=" + progress);
        if (!isPlaying) {
            seekToVideo(progress);
            seekToMusic(progress);
        }
    }

    /**
     * ?????????????????????
     */
    private void onPlayClick() {
        if (!DoubleClick.getInstance().isFastZDYDoubleClick(500)) {
            if (isPlaying) {
                pauseBgmMusic();
                isIntoPause = false;
                isPlayComplete = false;
                videoToPause();//??????????????????
                isPlaying = false;
                nowStateIsPlaying(false);
            } else {
                nowStateIsPlaying(true);
                if (!TextUtils.isEmpty(mVideoPath)) {
                    seekToVideo(mCutStartTime);
                    seekToMusic(mCutStartTime);
                    if (isPlayComplete) {
                        videoPlay();
                        isIntoPause = false;
                    } else {
                        if (isInitVideoLayer) {
                            if (!isIntoPause) {
                                videoPlay();
                            } else {
                                videoPlay();
                                isIntoPause = false;
                                isInitVideoLayer = true;
                            }
                        } else {
                            isIntoPause = false;
                            isInitVideoLayer = true;
                            videoPlay();
                        }
                    }
                } else {
                    //??????????????????,?????????????????????????????????????????????
                    if (!TextUtils.isEmpty(bgmPath) && musicEndTime == 0) {
                        if (bgmPlayer != null) {
                            //????????????
                            bgmPlayer.start();
                            LogUtil.d("playBGMMusic", " bgmPlayer.start()");
                        } else {
                            seekToVideo(mCutStartTime);
                            seekToMusic(mCutStartTime);
                            LogUtil.d("playBGMMusic", "animIsComplate");
                            playBGMMusic();
                        }
                    }
                }
                isPlaying = true;
                startTimer();
            }
            mSeekBarViewManualDrag = false;
        }
    }

    private void videoToStart() {
        isPlayComplete = true;
        endTimer();
        isPlaying = false;
        videoToPause();
        seekToVideo(mCutStartTime);
        seekToMusic(mCutStartTime);
        nowStateIsPlaying(false);
    }

    private void videoToPause() {
        pauseExoPlayer();
        isPlaying = false;
        endTimer();
        nowStateIsPlaying(false);
    }

    /**
     * ????????????
     */
    private void videoStop() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
        }
    }

    /**
     * ????????????
     * ?????????????????????????????????????????????????????????
     * ???????????????????????????????????????
     */
    private void videoPlay() {
        isPlaying = true;
        isPlayComplete = false;
        if (exoPlayer != null) {
            LogUtil.d("video", "play");
            if (!TextUtils.isEmpty(bgmPath)) {
                exoPlayer.setVolume(0f);
            } else {
                if (!TextUtils.isEmpty(mVideoPath)) {
                    if (noBgMusicPlay) {
                        exoPlayer.setVolume(0f);
                    } else {
                        exoPlayer.setVolume(1f);
                    }
                } else {
                    exoPlayer.setVolume(1f);
                }
            }
            if (getCurrentPos() >= mCutEndTime) {
                exoPlayer.seekTo(mCutStartTime);
            } else if (getCurrentPos() < mCutStartTime) {
                exoPlayer.seekTo(mCutStartTime);
            }
            exoPlayer.setPlayWhenReady(true);
        }
        startTimer();
    }

    private void nowStateIsPlaying(boolean isPlaying) {
        if (isPlaying) {
            mBinding.ivPlay.setImageResource(R.mipmap.pause);
        } else {
            mBinding.ivPlay.setImageResource(R.mipmap.iv_play_creation);
        }
    }

    /**
     * ??????????????????
     */
    private long getCurrentPos() {
        return exoPlayer != null ? exoPlayer.getCurrentPosition() : 0;
    }

    private void pauseExoPlayer() {
        if (exoPlayer != null) {
            LogUtil.d("video", "videoPause");
            exoPlayer.setPlayWhenReady(false);
        }
    }

    private void seekToVideo(long to) {
        if (exoPlayer != null) {
            exoPlayer.seekTo(to);
        }
    }

    private void seekToMusic(long to) {
        if (bgmPlayer != null) {
            bgmPlayer.seekTo((int) to);
        }
    }

    private void playBGMMusic() {
        bgmPlayer = new MediaPlayer();
        try {
            bgmPlayer.setDataSource(bgmPath);
            bgmPlayer.prepare();
            bgmPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void pauseBgmMusic() {
        if (bgmPlayer != null && bgmPlayer.isPlaying()) {
            bgmPlayer.pause();
        }
    }

    /**
     * ?????????????????????????????????
     */

    private void bjMusicControl() {
        if (!isEndDestroy) {
            LogUtil.d("playBGMMusic", "bjMusicControl");
            if (!TextUtils.isEmpty(bgmPath)) {
                if (musicEndTime != 0) {
                    float needMusicStartTime = musicStartTime;
                    float needTime = totalPlayTime;
                    LogUtil.d("playBGMMusic", "totalPlayTime=" + totalPlayTime + "mCutStartTime=" + mCutStartTime + "needTime=" + needTime + "musicStartTime=" + musicStartTime + "needMusicStartTime=" + needMusicStartTime);
                    if (needTime > musicEndTime || needTime < needMusicStartTime) {
                        LogUtil.d("playBGMMusic2", "??????????????????");
                        isNeedPlayBjMusic = false;
                        pauseBgmMusic();
                    } else {
                        if (!isNeedPlayBjMusic) {
                            LogUtil.d("playBGMMusic2", "????????????");
                            LogUtil.d("playBGMMusic2", "totalPlayTime=" + totalPlayTime + "mCutStartTime=" + mCutStartTime + "needTime=" + needTime + "musicStartTime=" + musicStartTime + "needMusicStartTime=" + needMusicStartTime);
                            playBjMusic();
                        }
                        isNeedPlayBjMusic = true;
                    }
                } else {
                    LogUtil.d("playBGMMusic", "musicEndTime=" + musicEndTime);
                    if (!isNeedPlayBjMusic) {
                        LogUtil.d("playBGMMusic", "????????????");
                        playBjMusic();
                    }
                    isNeedPlayBjMusic = true;
                }
            } else {
                LogUtil.d("playBGMMusic", "bgmPath==null");
            }
        }

    }


    private void playBjMusic() {
        LogUtil.d("playBGMMusic", "playBjMusic");
        if (!TextUtils.isEmpty(bgmPath)) {
            if (bgmPlayer != null) {
                bgmPlayer.start();
                seekToMusic(mCutStartTime);
            } else {
                seekToMusic(mCutStartTime);
                playBGMMusic();
            }
        }
    }

    private Timer timer;
    private TimerTask task;
    private long nowTime = 5;
    /**
     * ???????????????????????????
     */
    private long totalPlayTime;
    private boolean isNeedPlayBjMusic = false;

    private void startTimer() {

        if (musicChooseIndex == 2) {
            musicStartTime = 0;
            musicEndTime = allVideoDuration;
        }

        isEndDestroy = false;
        nowTime = 5;
        totalPlayTime = mCutStartTime;
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
                creationTimerTask();
            }
        };
        timer.schedule(task, 0, 5);
    }

    private void creationTimerTask() {
        totalPlayTime = totalPlayTime + 5;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bjMusicControl();
                if (!TextUtils.isEmpty(mVideoPath)) {
                    if (isPlaying) {
                        long nowCurrentPos = getCurrentPos();
                        if (nowCurrentPos < 0) {
                            nowCurrentPos = 0;
                        }
                        if (nowCurrentPos >= mCutEndTime) {
                            exoPlayer.seekTo(mCutStartTime);
                            videoToPause();
                        } else if (nowCurrentPos < mCutStartTime) {
                            exoPlayer.seekTo(mCutStartTime);
                            videoToPause();
                        }
                    }
                    if (mBinding != null) {
                        mBinding.progressBarView.scrollToPosition(getCurrentPos());
                    }
                } else {
                    //????????????
                    nowTime = nowTime + 5;

                    if (nowTime >= mCutEndTime) {
                        nowTime = mCutStartTime;
                        isPlayComplete = true;
                        endTimer();
                        isPlaying = false;
                        nowStateIsPlaying(false);
                    } else if (nowTime < mCutStartTime) {
                        nowTime = mCutStartTime;
                    }

                    if (mBinding.progressBarView != null) {
                        mBinding.progressBarView.scrollToPosition(nowTime);
                    }

                }
            }
        });
    }

    /**
     * ??????timer ???task
     */
    private void endTimer() {
        isNeedPlayBjMusic = false;
        isEndDestroy = true;
        destroyTimer();
        if (bgmPlayer != null) {
            bgmPlayer.stop();
            bgmPlayer = null;
        }
    }

    /**
     * ??????????????????
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

    @Override
    public void progress(long progress, boolean manualDrag) {
        mSeekBarViewManualDrag = manualDrag;
        if (manualDrag) {
            mBinding.progressBarView.scrollToPosition(progress);
        }
    }

    @Override
    public void manualDrag(boolean manualDrag) {
        mSeekBarViewManualDrag = manualDrag;
        videoToPause();
        mPresenter.getNowPlayingTimeViewShow(textContain, progressBarProgress, mCutEndTime);
    }

    @Override
    public void timelineChange(long startTime, long endTime, String id, int listId, boolean isSubtitle) {
        for (int i = 0; i < textContain.getChildCount(); i++) {
            TextComponent textComponent = (TextComponent) textContain.getChildAt(i);
            if (textComponent != null) {
                if (isSubtitle) {
                    if (TextUtils.equals(id, String.valueOf(textComponent.getId())) &&
                            TextUtils.equals(String.valueOf(listId), String.valueOf(textComponent.getmJadeFontViewIndex()))) {
                        textComponent.setStartTime(startTime);
                        textComponent.setEndTime(endTime);
                        break;
                    }
                } else {
                    if (TextUtils.equals(id, String.valueOf(textComponent.getmJadeFontViewIndex()))) {
                        textComponent.setStartTime(startTime);
                        textComponent.setEndTime(endTime);
                        break;
                    }
                }
            }
        }
        mPresenter.getNowPlayingTimeViewShow(textContain, progressBarProgress, mCutEndTime);
    }

    @Override
    public void currentViewSelected(String id) {

    }

    @Override
    public void trackPause() {
        videoToPause();
    }

    @Override
    public void startIdentifySubtitle() {
        videoToPause();
        if (musicChooseIndex == 0) {
            mPresenter.startIdentify(true, mVideoPath, "");
        }
        if (musicChooseIndex == 2) {
            mPresenter.startIdentify(false, "", bgmPath);
        }
    }


    @Override
    public void identifySubtitle(List<SubtitleEntity> subtitles, boolean isVideoInAudio, String audioPath) {
        if (mBinding.jakeFontSeekBarView.subtitleIndex != -1) {
            //????????????????????????  ?????????????????????????????????????????????view
            mBinding.jakeFontSeekBarView.deleteSubtitleView(mBinding.jakeFontSeekBarView.subtitleIndex);
        }
        mBinding.jakeFontSeekBarView.addTemplateMaterialItemView(allVideoDuration, "", 0, 0, false,
                "", -1, subtitles, mJadeFontViewIndex, mBinding.progressBarView.progressTotalWidth);
        mBinding.jakeFontSeekBarView.setCutEndTime(mCutEndTime);
        mBinding.jakeFontSeekBarView.scrollToTheBottom();
        //????????????id???0???size-1
        for (int i = 0; i < subtitles.size(); i++) {
            SubtitleEntity subtitleEntity = subtitles.get(i);

            int initColor = Color.parseColor("#252B3B");
            Drawable leftTopD = ContextCompat.getDrawable(JadeFontMakeActivity.this, R.drawable.ic_jade_close);
            Drawable rightBottomD = ContextCompat.getDrawable(JadeFontMakeActivity.this, R.mipmap.sticker_redact);
            Drawable leftBottomD = ContextCompat.getDrawable(JadeFontMakeActivity.this, R.drawable.ic_jade_edit);

            TextComponent new_text = new TextComponent(JadeFontMakeActivity.this,
                    initColor,
                    "reference",
                    helperClass,
                    leftTopD,
                    rightBottomD,
                    leftBottomD
            );
            new_text.setId(i);
            new_text.setSubTitle(true);
            new_text.setStartTime(subtitleEntity.getStartTime());
            new_text.setEndTime(subtitleEntity.getEndTime());
            new_text.setContent(subtitleEntity.getText());
            new_text.setmJadeFontViewIndex(mJadeFontViewIndex);
            new_text.setVisibility(View.GONE);
            textContain.addNewText(new_text, stickerItemOnitemclick, helperClass);
        }
        mJadeFontViewIndex++;
    }

    @Override
    public void getBgmPath(String bgmPath) {
        this.bgmPath = bgmPath;
        if (TextUtils.isEmpty(bgmPath)) {
            if (isPlaying) {
                videoToPause();
            }
        } else {
            if (isPlaying) {
                if (!TextUtils.isEmpty(bgmPath)) {
                    if (exoPlayer != null) {
                        exoPlayer.setVolume(0f);
                    }
                    pauseBgmMusic();
                    playBGMMusic();
                    if (bgmPlayer != null) {
                        if (exoPlayer != null) {
                            bgmPlayer.seekTo((int) getCurrentPos());
                        } else {
                            bgmPlayer.seekTo((int) totalPlayTime);
                        }
                    }
                } else {
                    if (exoPlayer != null) {
                        exoPlayer.setVolume(1f);
                    }
                    pauseBgmMusic();
                }
            } else {
                videoToStart();
            }
        }
    }

    boolean noBgMusicPlay = false;

    @Override
    public void clearCheckBox() {
        noBgMusicPlay = true;
        check_box_0.setImageResource(R.mipmap.template_btn_unselected);
        check_box_1.setImageResource(R.mipmap.template_btn_unselected);
        check_box_2.setImageResource(R.mipmap.template_btn_unselected);
        check_box_3.setImageResource(R.mipmap.template_btn_unselected);
    }

    @Override
    public void chooseCheckBox(int i) {
        switch (i) {
            case 0:
                noBgMusicPlay = false;
                check_box_0.setImageResource(R.mipmap.template_btn_selected);
                break;
            case 1:
                check_box_1.setImageResource(R.mipmap.template_btn_selected);
                break;
            case 2:
                check_box_2.setImageResource(R.mipmap.template_btn_selected);
                break;
            case 3:
                check_box_3.setImageResource(R.mipmap.template_btn_selected);
                break;
            default:
                break;
        }
    }

    @Override
    public void showLoadingDialog() {
        mLoadingDialog.show();
    }

    @Override
    public void setDialogProgress(String title, int dialogProgress, String content) {
        mLoadingDialog.setTitleStr(title);
        mLoadingDialog.setProgress(dialogProgress);
        mLoadingDialog.setContentStr(content);
    }

    @Override
    public void dismissLoadingDialog() {
        mLoadingDialog.dismiss();
    }

    @Subscribe
    public void onEventMainThread(CutSuccess cutSuccess) {
        mChangeMusicPath = cutSuccess.getFilePath();
        mPresenter.setExtractedAudioBjMusicPath(mChangeMusicPath);
        musicChooseIndex = 2;
    }

    @Override
    public void objectTouch() {

    }

    @Override
    public void onObjectZChanged(String str, int i) {

    }

    @Override
    public void onShapeCreate(String str) {

    }

    @Override
    public void onShapeDelete(Bundle bundle, int i, String str) {

    }

    @Override
    public void onShapeMoveResize(float f, float f2, float f3, float f4, boolean z, String str) {

    }

    @Override
    public void onShapeSelectionChanged(boolean z, int i) {

    }

    @Override
    public void onTextCreate(String str) {

    }

    @Override
    public void onTextDelete(Bundle bundle, int i, String str) {
        Log.d(TAG, "onTextDelete() called with: bundle = [" + bundle + "], i = [" + i + "], str = [" + str + "]");
    }

    @Override
    public void onTextDoubleTap() {

    }

    @Override
    public void onTextMove(float f, float f2, float f3, boolean z, String str) {

    }

    @Override
    public void onTextSelectionChanged(boolean z) {

        Log.d(TAG, "onTextSelectionChanged() called with: z = [" + z + "]");
        if (z) {
//            if (isFirstAdd) {
//                isFirstAdd = false;
//                return;
//            }
//            FragmentTransaction fragmentTransactionEdit = fragmentManager.beginTransaction();
//            fragmentTransactionEdit.show(jadeAdjustFragment);
//            fragmentTransactionEdit.commit();
        } else {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.hide(jadeAdjustFragment);
            fragmentTransaction.commit();
        }
    }

    private void stickerTimeLineOffset() {

        for (int i = 0; i < textContain.getChildCount(); i++) {

            TextComponent textComponent = (TextComponent) textContain.getChildAt(i);

            if (!textComponent.isSubTitle()) {
                if (textComponent.getStartTime() > mCutStartTime && textComponent.getEndTime() < mCutEndTime) {
                    continue;
                }

                if (textComponent.getStartTime() < mCutStartTime && textComponent.getEndTime() > mCutEndTime) {
                    textComponent.setStartTime(mCutStartTime);
                    textComponent.setEndTime(mCutEndTime);
                }

                if (textComponent.getStartTime() > mCutStartTime && textComponent.getEndTime() > mCutEndTime) {
                    textComponent.setStartTime(mCutEndTime - (textComponent.getEndTime() - textComponent.getStartTime()));
                    textComponent.setEndTime(mCutEndTime);
                }

                if (textComponent.getStartTime() > mCutStartTime && mCutEndTime - textComponent.getStartTime() < 1000) {
                    textComponent.setStartTime(mCutStartTime);
                }

                if (textComponent.getEndTime() < mCutStartTime) {
                    textComponent.setStartTime(mCutStartTime);
                    textComponent.setEndTime(mCutStartTime + 1000);
                }

                if (textComponent.getEndTime() > mCutEndTime) {
                    textComponent.setEndTime(mCutEndTime);
                    if (mCutEndTime - textComponent.getStartTime() <= 1000) {
                        textComponent.setStartTime(mCutEndTime - 1000);
                    }
                }

                if (textComponent.getStartTime() < mCutStartTime) {
                    textComponent.setStartTime(mCutStartTime);
                    if (textComponent.getEndTime() - mCutStartTime <= 1000) {
                        textComponent.setEndTime(mCutStartTime + 1000);
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isIntoPause && exoPlayer != null) {
            exoPlayer.prepare(mediaSource, true, false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    pauseExoPlayer();
                    destroyTimer();
                }
            }, 200);
            isIntoPause = false;
        }
    }

    @Override
    protected void onPause() {
        videoToPause();
        isIntoPause = true;
        super.onPause();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        destroyTimer();
        videoStop();
        if (bgmPlayer != null) {
            bgmPlayer.pause();
            bgmPlayer.release();
        }
        super.onDestroy();
    }

}

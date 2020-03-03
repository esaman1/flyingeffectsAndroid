package com.flyingeffects.com.ui.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.TemplateThumbAdapter;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.enity.TemplateThumbItem;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.VideoPlayerCallbackForTemplate;
import com.flyingeffects.com.ui.interfaces.view.TemplateMvpView;
import com.flyingeffects.com.ui.presenter.TemplatePresenter;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.view.EmptyControlVideo;
import com.shixing.sxve.ui.AssetDelegate;
import com.shixing.sxve.ui.SxveConstans;
import com.shixing.sxve.ui.model.GroupModel;
import com.shixing.sxve.ui.model.MediaUiModel;
import com.shixing.sxve.ui.model.TemplateModel;
import com.shixing.sxve.ui.model.TextUiModel;
import com.shixing.sxve.ui.view.TemplateView;
import com.shixing.sxve.ui.view.TextAssetEditLayout;
import com.shixing.sxve.ui.view.WaitingDialog;
import com.suke.widget.SwitchButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * 模板页面
 */
public class TemplateActivity extends BaseActivity implements TemplateMvpView, AssetDelegate {

    @BindView(R.id.switch_button)
    SwitchButton switch_button;
    @BindView(R.id.edit_view_container)
    FrameLayout mContainer;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.seekBar)
    SeekBar seekBar;
    private TemplatePresenter presenter;
    private List<String> imgPath = new ArrayList<>();
    private TemplateModel mTemplateModel;
    private File mFolder;
    private TemplateThumbAdapter templateThumbAdapter;
    private ArrayList<TemplateThumbItem> listItem = new ArrayList<>();
    private ArrayList<TemplateView> mTemplateViews;
    private int nowChooseIndex = 0;
    private int lastPosition;
    private String mAudio1Path;
    private static final String MUSIC_PATH = "/bj.mp3";
    private TextAssetEditLayout mTextEditLayout;
    @BindView(R.id.video_player)
    EmptyControlVideo videoPlayer;
    /**
     * 原图地址,如果不需要抠图，原图地址为null
     */
    private List<String> originalPath;
    private String templateFilePath;
    /**
     * 需要素材数量
     */
    private int defaultNum;
    private String templateName;
    private String fromTo;



    @Override
    protected int getLayoutId() {
        return R.layout.act_template_edit;
    }

    @Override
    protected void initView() {

        findViewById(R.id.tv_top_submit).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tv_top_submit)).setText("保存");
        presenter = new TemplatePresenter(this, this);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("Message");
        if (bundle != null) {
            fromTo=bundle.getString("fromTo");
            defaultNum=bundle.getInt("isPicNum");
            templateFilePath=bundle.getString("templateFilePath");
            imgPath = bundle.getStringArrayList("paths");
            originalPath= bundle.getStringArrayList("originalPath");
            templateName=bundle.getString("templateName");
        }
        if(originalPath==null){
            //不需要抠图
            findViewById(R.id.ll_Matting).setVisibility(View.GONE);
        }
        mTextEditLayout = findViewById(R.id.text_edit_layout);
//        mFolder = getExternalFilesDir("dynamic/" + "test");//zs2002202bg  ///aaa  ///test
        mFolder=new File(templateFilePath);
        mAudio1Path = mFolder.getPath() + MUSIC_PATH;
        File dir = getExternalFilesDir("");
        mTemplateViews = new ArrayList<>();
        for (int i = 0; i < defaultNum; i++) {
            listItem.add(new TemplateThumbItem("", 1, false));
        }
        SxveConstans.default_bg_path = new File(dir, "default_bj.png").getPath();
        seekBar.setOnSeekBarChangeListener(seekBarListener);
        switch_button.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if(isChecked){
                    //修改图为裁剪后的素材
                    presenter.ChangeMaterial(originalPath,defaultNum);
                }else{
                    statisticsEventAffair.getInstance().setFlag(TemplateActivity.this,"1_mb_bj_Cutoutoff");
                    //修改为裁剪前的素材
                    presenter.ChangeMaterial(imgPath,defaultNum);
                }
            }
        });
    }




    @Override
    protected void initAction() {
        initTemplateThumb();
        presenter.loadTemplate(mFolder.getPath(), this);
    }

    @Override
    public void completeTemplate(TemplateModel templateModel) {
        mTemplateModel = templateModel;
        initTemplateViews(mTemplateModel);  //初始化templateView 等数据
    }

    @Override
    public void toPreview(String path) {
        videoPlayer.setUp(path, true, "");
        videoPlayer.startPlayLogic();
        videoPlayer.setGSYVideoProgressListener((progress, secProgress, currentPosition, duration) -> seekBar.setProgress(progress));
        videoPlayer.setVideoAllCallBack(new VideoPlayerCallbackForTemplate(isSuccess -> {
            showPreview(false);
        }));
        showPreview(true);
    }

    @Override
    public void ChangeMaterialCallback(ArrayList<TemplateThumbItem> callbackListItem, List<String> list_all) {
        listItem.clear();
        listItem.addAll(callbackListItem);
        templateThumbAdapter.notifyDataSetChanged();
        mTemplateModel.setReplaceAllMaterial(list_all);
        mTemplateViews.get(nowChooseIndex).invalidate();
    }


    @Override
    protected void onPause() {
        super.onPause();
        videoPlayer.onVideoPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoPlayer.onVideoResume();
    }


    private void showPreview(boolean isPreview){
        if(isPreview){
            mContainer.setVisibility(View.INVISIBLE);
            videoPlayer.setVisibility(View.VISIBLE);
        }else{
            videoPlayer.setVisibility(View.INVISIBLE);
            mContainer.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void pickMedia(MediaUiModel model) {

    }

    @Override
    public void editText(TextUiModel model) {
        mTextEditLayout.setVisibility(View.VISIBLE);
        mTextEditLayout.setupWidth(model);
    }


    /**
     * description ：如果有背景，这里需要忽略最后一个值，因为最后一个模板是背景模板，用户是不能够操作的。
     * date: ：2019/11/28 14:10
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    private void initTemplateViews(TemplateModel templateModel) {
        for (int i = 1; i <= templateModel.groupSize; i++) {
            TemplateView templateView = new TemplateView(TemplateActivity.this);
            templateView.setBackgroundColor(Color.BLACK);
            templateView.setVisibility(i == 1 ? View.VISIBLE : View.GONE);
            GroupModel groupModel = templateModel.groups.get(i);
            templateView.setAssetGroup(groupModel);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            params.gravity = Gravity.CENTER;
            mTemplateViews.add(templateView);
            mContainer.addView(templateView, params);
        }
        isFirstReplace(imgPath);
    }


    private void isFirstReplace(List<String> paths) {
        if (mTemplateViews != null && mTemplateViews.size() > 0) {
            List<String> list_all = new ArrayList<>();
            for (int i = 0; i < defaultNum; i++) {  //填满数据，为了缩略图
                if (paths.size() > i && !TextUtils.isEmpty(paths.get(i))) {
                    list_all.add(paths.get(i)); //前面的时path ，后面的为默认的path
                } else {
                    list_all.add(SxveConstans.default_bg_path);
                }
            }
            for (int i = 0; i < list_all.size(); i++) {  //合成底部缩略图
                TemplateThumbItem templateThumbItem = new TemplateThumbItem();
                templateThumbItem.setPathUrl(list_all.get(i));
                if (i == 0) {
                    templateThumbItem.setIsCheck(0);
                } else {
                    templateThumbItem.setIsCheck(1);
                }
                listItem.set(i, templateThumbItem);
            }
            templateThumbAdapter.notifyDataSetChanged();
            WaitingDialog.openPragressDialog(this);
            new Thread(() -> {
                mTemplateModel.setReplaceAllFiles(list_all, TemplateActivity.this, complete -> TemplateActivity.this.runOnUiThread(() -> {
                    WaitingDialog.closePragressDialog();
                    selectGroup(0);
                    nowChooseIndex = 0;
                    templateThumbAdapter.notifyDataSetChanged();
                    if (mTemplateViews != null && mTemplateViews.size() > 0) {
                        mTemplateViews.get(nowChooseIndex).invalidate(); //提示重新绘制预览图
                    }
                }), "FIRST_MEDIA");  //批量替换图片
            }).start();
        }
    }


    /**
     * description ：选择当前的点，里面有个mModel ，其中mModel 一定要保证是当前可见mediaModel 的mModel,否则会出现灰屏的情况
     * date: ：2019/11/28 13:58
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    public void selectGroup(final int index) {
        if (mTemplateViews != null && mTemplateViews.size() > 0) {

            try{
                TemplateView nowChooseTemplateView = mTemplateViews.get(index);
                nowChooseTemplateView.setVisibility(View.VISIBLE);
//            nowChooseTemplateView.isViewVisible(true);
                nowChooseTemplateView.invalidate();
                rx.Observable.from(mTemplateViews).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(templateView -> {
                    LogUtil.d("OOM", "selectGroup");
                    if (templateView != nowChooseTemplateView && templateView.getVisibility() != View.GONE) {
                        templateView.setVisibility(View.GONE);
//                    templateView.isViewVisible(false);
                    }
                });
            }catch (Exception e){
                LogUtil.d("Exception",e.getMessage());
            }


        }
    }


    public void initTemplateThumb() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        templateThumbAdapter = new TemplateThumbAdapter(R.layout.item_group_thumb, listItem, TemplateActivity.this);
        templateThumbAdapter.setOnItemClickListener((adapter, view, position) -> {
            modificationThumbData(lastPosition, position);
            selectGroup(position);
            lastPosition = position;
        });
        recyclerView.setAdapter(templateThumbAdapter);
    }


    private void modificationThumbData(int lastPosition, int position) {
        if (lastPosition != position) {
            TemplateThumbItem item1 = listItem.get(position);
            item1.setIsCheck(0);
            listItem.set(position, item1);
            TemplateThumbItem item2 = listItem.get(lastPosition);
            item2.setIsCheck(1);
            listItem.set(lastPosition, item2);
            templateThumbAdapter.notifyItemChanged(position); //更新上一个
            templateThumbAdapter.notifyItemChanged(lastPosition);
        } else {
            Intent intent = new Intent(this, VideoClippingActivity.class);
            intent.putExtra("path", listItem.get(position).getPathUrl());
            startActivity(intent);
        }
    }


    @OnClick({R.id.tv_top_submit, R.id.iv_play})
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_top_submit:

                if(!TextUtils.isEmpty(fromTo)&&fromTo.equals("search")){
                    statisticsEventAffair.getInstance().setFlag(TemplateActivity.this,"4_search_save",templateName);
                }
                statisticsEventAffair.getInstance().setFlag(TemplateActivity.this,"1_mb_bj_save",templateName);
                presenter.renderVideo(mFolder.getPath(), mAudio1Path,false);
                break;

            case R.id.iv_play:
                presenter.renderVideo(mFolder.getPath(), mAudio1Path,true);
                break;

            default:
                break;

        }
        super.onClick(v);
    }

    @Override
    public void onBackPressed() {
        if (mTextEditLayout.getVisibility() == View.VISIBLE) {
            mTextEditLayout.hide();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        videoPlayer.release();
    }


    SeekBar.OnSeekBarChangeListener seekBarListener =new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int nowProgress, boolean fromUser) {
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

}

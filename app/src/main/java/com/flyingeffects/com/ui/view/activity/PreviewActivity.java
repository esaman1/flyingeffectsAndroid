package com.flyingeffects.com.ui.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.manager.AlbumManager;
import com.flyingeffects.com.manager.DataCleanManager;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.AlbumChooseCallback;
import com.flyingeffects.com.ui.interfaces.VideoPlayerCallbackForTemplate;
import com.flyingeffects.com.ui.interfaces.view.PreviewMvpView;
import com.flyingeffects.com.ui.presenter.PreviewMvpPresenter;
import com.flyingeffects.com.view.EmptyControlVideo;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.yanzhenjie.album.AlbumFile;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


/***
 * 预览视频界面
 * 开始制作
 */
public class PreviewActivity extends BaseActivity implements AlbumChooseCallback, PreviewMvpView {

    @BindView(R.id.video_player)
    EmptyControlVideo videoPlayer;

    @BindView(R.id.tv_make)
    TextView tv_make;

    @BindView(R.id.iv_zan)
    ImageView iv_zan;

    @BindView(R.id.iv_writer)
    ImageView iv_writer;

    @BindView(R.id.tv_writer_name)
    TextView tv_writer_name;

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.tv_describe)
    TextView tv_describe;

    @BindView(R.id.relative_show_cover)
    RelativeLayout relative_show_cover;

    @BindView(R.id.iv_show_cover)
    ImageView iv_show_cover;

    @BindView(R.id.iv_play)
    ImageView iv_play;

    PreviewMvpPresenter Presenter;

    new_fag_template_item templateItem;


    private List<String>originalImagePath=new ArrayList<>();

    /**
     * 模板下载地址
     */
    private String TemplateFilePath;
    /**
     * 素材数量
     */
    private int defaultnum;

    /**
     * 是否需要抠图
     */
    private int is_picout;

    public final static int SELECTALBUM = 0;

    /**
     * 来着来个页面
     */
    private String fromTo;


    @Override
    protected int getLayoutId() {
        return R.layout.act_preview;
    }

    @Override
    protected void initView() {
        templateItem = (new_fag_template_item) getIntent().getSerializableExtra("person");
        fromTo=getIntent().getStringExtra("fromTo");
        defaultnum=templateItem.getDefaultnum();
        is_picout=templateItem.getIs_picout();
        Presenter = new PreviewMvpPresenter(this, this);
        Glide.with(this).load(templateItem.getImage()).into(iv_show_cover);
        videoPlayer.setUp(templateItem.getVidoefile(), true, "");
        videoPlayer.startPlayLogic();
        videoPlayer.setVideoAllCallBack(new VideoPlayerCallbackForTemplate(isSuccess -> {
            VideoPlaybackCompleted(true);
        }));
        Glide.with(this)
                .load(templateItem.getAuth_image())
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(iv_writer);
        tv_writer_name.setText(templateItem.getAuth());
        tv_title.setText(templateItem.getTitle());
        tv_describe.setText(templateItem.getMbsearch());
    }


    @Override
    protected void initAction() {
        DataCleanManager.cleanExternalCache();
        DataCleanManager.cleanInternalCache(BaseApplication.getInstance());
    }


    @OnClick({R.id.iv_zan, R.id.tv_make, R.id.iv_play})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_zan:
                Presenter.collectTemplate(templateItem.getId());
                iv_zan.setImageResource(R.mipmap.zan_selected);
                break;
            case R.id.tv_make:
                if(!TextUtils.isEmpty(fromTo)&&fromTo.equals("search")){
                    statisticsEventAffair.getInstance().setFlag(PreviewActivity.this,"4_search_make",templateItem.getTitle());
                }
                videoPlayer.onVideoPause();
                VideoPlaybackCompleted(true);
                Presenter.downZip(templateItem.getTemplatefile(), templateItem.getZipid());
                break;
            case R.id.iv_play:
                VideoPlaybackCompleted(false);
                videoPlayer.startPlayLogic();
                break;

            default:
                break;
        }


    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Presenter.onDestroy();
        videoPlayer.release();
    }


    @Override
    public void onBackPressed() {
        //释放所有
        videoPlayer.setVideoAllCallBack(null);
        GSYVideoManager.releaseAllVideos();
        this.finish();
    }


    @Override
    public void resultFilePath(int tag, List<String> paths, boolean isCancel, ArrayList<AlbumFile> albumFileList) {
        if (!isCancel) {
            if (SELECTALBUM == 0) {
                //如果不需要抠图
                if(is_picout==0){
                    intoTemplateActivity(paths,TemplateFilePath);
                    originalImagePath=null;
                }else{//需要抠图
                    originalImagePath=paths;
                    Presenter.CompressImg(paths);
                }

            }
        }
    }


    private void intoTemplateActivity(List<String> paths, String templateFilePath) {
        Intent intent = new Intent(this, TemplateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("paths", (ArrayList<String>) paths);
        bundle.putInt("isPicNum",defaultnum);
        bundle.putString("fromTo",fromTo);
        bundle.putString("templateName",templateItem.getTitle());
        bundle.putStringArrayList("originalPath", (ArrayList<String>) originalImagePath);
        bundle.putString("templateFilePath", templateFilePath);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Message", bundle);
        startActivity(intent);
        this.finish();
    }


    @Override
    public void getCompressImgList(List<String> imgList) {
        intoTemplateActivity(imgList, TemplateFilePath);
    }


    @Override
    public void showDownProgress(int progress) {
        Observable.just(progress).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                tv_make.setText("下载" + progress + "%");
            }
        });
    }

    @Override
    public void getTemplateFileSuccess(String TemplateFilePath) {
        //file 文件下载成功
        this.TemplateFilePath = TemplateFilePath;
        AlbumManager.chooseImageAlbum(this, defaultnum, SELECTALBUM, this, "");
    }


    private void VideoPlaybackCompleted(boolean isComplete) {
        if (isComplete) {
            relative_show_cover.setVisibility(View.VISIBLE);
            videoPlayer.setVisibility(View.INVISIBLE);
        } else {
            relative_show_cover.setVisibility(View.INVISIBLE);
            videoPlayer.setVisibility(View.VISIBLE);
        }
    }

}

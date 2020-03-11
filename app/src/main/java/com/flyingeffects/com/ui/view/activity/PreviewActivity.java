package com.flyingeffects.com.ui.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.manager.AlbumManager;
import com.flyingeffects.com.manager.DataCleanManager;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.AlbumChooseCallback;
import com.flyingeffects.com.ui.interfaces.VideoPlayerCallbackForTemplate;
import com.flyingeffects.com.ui.interfaces.view.PreviewMvpView;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.ui.presenter.PreviewMvpPresenter;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.view.EmptyControlVideo;
import com.flyingeffects.com.view.MarqueTextView;
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
    private boolean ondestroy;

    @BindView(R.id.video_player)
    EmptyControlVideo videoPlayer;

    @BindView(R.id.tv_make)
    TextView tv_make;

    @BindView(R.id.iv_zan)
    ImageView iv_zan;

    @BindView(R.id.iv_writer)
    ImageView iv_writer;

    @BindView(R.id.iv_video_play)
    ImageView iv_video_play;

    @BindView(R.id.tv_writer_name)
    TextView tv_writer_name;

    @BindView(R.id.tv_title)
    MarqueTextView tv_title;

    @BindView(R.id.tv_describe)
    MarqueTextView tv_describe;

    @BindView(R.id.iv_show_cover)
    ImageView iv_show_cover;


    PreviewMvpPresenter Presenter;

    new_fag_template_item templateItem;


    private List<String> originalImagePath = new ArrayList<>();

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
    public final static int SELECTALBUMFROMBJ= 1;

    private boolean isPlayComplate=false;

    /**
     * 来着来个页面
     */
    private String fromTo;

    private int nowCollectType;

    /**
     * 0 表示来做模板，1表示来自背景
     */


    @Override
    protected int getLayoutId() {
        return R.layout.act_preview;
    }

    @Override
    protected void initView() {
        ondestroy = false;
        templateItem = (new_fag_template_item) getIntent().getSerializableExtra("person");
        fromTo = getIntent().getStringExtra("fromTo");
        defaultnum = templateItem.getDefaultnum();
        is_picout = templateItem.getIs_picout();
        nowCollectType = templateItem.getIs_collection();
        if (nowCollectType == 1) {
            iv_zan.setImageResource(R.mipmap.zan_selected);
        }
        Presenter = new PreviewMvpPresenter(this, this);
        Glide.with(this).load(templateItem.getImage()).into(iv_show_cover);
        videoPlayer.setUp(templateItem.getVidoefile(), true, "");
        videoPlayer.startPlayLogic();
        videoPlayer.setVideoAllCallBack(new VideoPlayerCallbackForTemplate(isSuccess -> {
            VideoPlaybackCompleted(true,true);
            isPlayComplate=true;
        }));
        Glide.with(this)
                .load(templateItem.getAuth_image())
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(iv_writer);
        tv_writer_name.setText(templateItem.getAuth());
        tv_title.setText(templateItem.getTitle());
        tv_describe.setText("友友们    " + "上传" + templateItem.getDefaultnum() + "张照片即可制作");
        Presenter.requestTemplateDetail(templateItem.getId());

    }


    @Override
    protected void initAction() {
        DataCleanManager.cleanExternalCache();
        DataCleanManager.cleanInternalCache(BaseApplication.getInstance());
    }


    @OnClick({R.id.iv_zan, R.id.tv_make, R.id.iv_video_play, R.id.iv_top_back, R.id.iv_click})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_zan:

                Presenter.collectTemplate(templateItem.getId(),templateItem.getTitle());
                break;
            case R.id.tv_make:
                if (!DoubleClick.getInstance().isFastZDYDoubleClick(3000)) {
                    if(BaseConstans.hasLogin()){
                        //登录可能被挤下去，所以这里加个用户信息刷新请求
                        Presenter.requestUserInfo();
                    }else{
                        Intent intent =new Intent(PreviewActivity.this,LoginActivity.class);
                        startActivity(intent);
                    }
                }
                break;

            case R.id.iv_top_back:
                PreviewActivity.this.finish();
                break;

            case R.id.iv_video_play:
                videoPlayer.setVisibility(View.VISIBLE);
                iv_video_play.setVisibility(View.GONE);
                iv_show_cover.setVisibility(View.GONE);
                videoPlayer.startPlayLogic();
                break;

            case R.id.iv_click:
                if(iv_video_play.getVisibility()==View.VISIBLE){
                    if(isPlayComplate){
                        videoPlayer.startPlayLogic();
                        isPlayComplate=false;
                    }else{
                        videoPlayer.onVideoResume(false);
                    }
                  VideoPlaybackCompleted(false,false);
                }else{
                    videoPlayer.onVideoPause();
               VideoPlaybackCompleted(true,false);
                }
                break;

            default:
                break;
        }
    }


    @Override
    protected void onPause() {
        videoPlayer.onVideoPause();
        iv_video_play.setVisibility(View.VISIBLE);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onDestroy() {
        ondestroy = true;
        Presenter.onDestroy();
        videoPlayer.release();
        super.onDestroy();
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
        if (!isCancel && !ondestroy) {
                //如果不需要抠图
                if (is_picout == 0) {
                    intoTemplateActivity(paths, TemplateFilePath);
                    originalImagePath = null;
                } else {//需要抠图
                    originalImagePath = paths;
                    Presenter.CompressImg(paths);
                }
            }
    }


    private void intoTemplateActivity(List<String> paths, String templateFilePath) {
        Intent intent = new Intent(this, TemplateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("paths", (ArrayList<String>) paths);
        bundle.putInt("isPicNum", defaultnum);
        bundle.putString("fromTo", fromTo);
        bundle.putString("templateName", templateItem.getTitle());
        bundle.putStringArrayList("originalPath", (ArrayList<String>) originalImagePath);
        bundle.putString("templateFilePath", templateFilePath);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Message", bundle);
        startActivity(intent);
    }





    @Override
    public void getCompressImgList(List<String> imgList) {
        if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.ISFROMBJ)) {
            Intent intent=new Intent(PreviewActivity.this,CreationTemplateActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("paths", (ArrayList<String>) imgList);
            bundle.putString("coverPath", templateItem.getImage());
            bundle.putString("fromTo", fromTo);
            bundle.putString("templateName", templateItem.getTitle());
            bundle.putStringArrayList("originalPath", (ArrayList<String>) originalImagePath);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("Message", bundle);
            startActivity(intent);
        }else{
            intoTemplateActivity(imgList, TemplateFilePath);
        }

    }


    @Override
    public void showDownProgress(int progress) {
        Observable.just(progress).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                if (progress == 100) {
                    tv_make.setText("马上制作");
                } else {
                    tv_make.setText("下载" + progress + "%");
                }

            }
        });
    }

    @Override
    public void getTemplateFileSuccess(String TemplateFilePath) {
        if (!ondestroy) {
            //file 文件下载成功
            this.TemplateFilePath = TemplateFilePath;
            AlbumManager.chooseImageAlbum(this, defaultnum, SELECTALBUM, this, "");
        }

    }

    @Override
    public void collectionResult() {
        if (nowCollectType == 0) {
            statisticsEventAffair.getInstance().setFlag(PreviewActivity.this, "1_mb_keep_cancel", templateItem.getTitle());
            nowCollectType = 1;
            ToastUtil.showToast(getString(R.string.template_collect_success));
        } else {
            statisticsEventAffair.getInstance().setFlag(PreviewActivity.this, "1_mb_keep", templateItem.getTitle());
            nowCollectType = 0;
            ToastUtil.showToast(getString(R.string.template_cancel_success));
        }
        showCollectState(nowCollectType == 0);
    }


    private void showCollectState(boolean unSelected) {
        if (unSelected) {
            iv_zan.setImageResource(R.mipmap.zan);
            nowCollectType = 0;
        } else {
            iv_zan.setImageResource(R.mipmap.zan_selected);
            nowCollectType = 1;
        }
    }


    /**
     * 只用来更新保存状态
     */
    @Override
    public void getTemplateLInfo(new_fag_template_item item) {
        Glide.with(PreviewActivity.this).load(item.getImage()).into(iv_show_cover);
        showCollectState(item.getIs_collection() == 0);
    }

    @Override
    public void hasLogin(boolean hasLogin) {

        if(!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.ISFROMBJ)){
            //来做背景页面
            AlbumManager.chooseImageAlbum(this, 1, SELECTALBUMFROMBJ, this, "");
        }else{
            if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.ISFROMSEARCH)) {
                statisticsEventAffair.getInstance().setFlag(PreviewActivity.this, "4_search_make", templateItem.getTitle());
            }
            statisticsEventAffair.getInstance().setFlag(PreviewActivity.this, "mb_make", templateItem.getTitle());
            videoPlayer.onVideoPause();
            VideoPlaybackCompleted(true,true);
            Presenter.downZip(templateItem.getTemplatefile(), templateItem.getZipid());
        }


    }


    private void VideoPlaybackCompleted(boolean isComplete,Boolean isShowCover) {
        if (isComplete) {
            iv_video_play.setVisibility(View.VISIBLE);
            if(isShowCover){
                iv_show_cover.setVisibility(View.VISIBLE);
                videoPlayer.setVisibility(View.INVISIBLE);
            }else{
                videoPlayer.setVisibility(View.VISIBLE);
            }
        } else {
            iv_show_cover.setVisibility(View.INVISIBLE);
            videoPlayer.setVisibility(View.VISIBLE);
            iv_video_play.setVisibility(View.GONE);
        }
    }

}

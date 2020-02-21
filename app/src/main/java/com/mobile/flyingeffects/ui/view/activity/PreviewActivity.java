package com.mobile.flyingeffects.ui.view.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.transition.Transition;
import android.view.View;
import android.widget.ImageView;

import com.mobile.flyingeffects.R;
import com.mobile.flyingeffects.base.BaseActivity;
import com.mobile.flyingeffects.manager.AlbumManager;
import com.mobile.flyingeffects.ui.interfaces.AlbumChooseCallback;
import com.mobile.flyingeffects.ui.interfaces.OnTransitionListener;
import com.mobile.flyingeffects.view.EmptyControlVideo;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.yanzhenjie.album.AlbumFile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/***
 * 预览视频界面
 * 开始制作
 */
public class PreviewActivity extends BaseActivity implements AlbumChooseCallback {


    public final static String IMG_TRANSITION = "IMG_TRANSITION";
    public final static String TRANSITION = "TRANSITION";
    public final static int SELECTALBUM=0;

    OrientationUtils orientationUtils;

    private boolean isTransition;

    private Transition transition;


    @BindView(R.id.video_player)
    EmptyControlVideo videoPlayer;


    @BindView(R.id.iv_zan)
    ImageView iv_zan;

    @Override
    protected int getLayoutId() {
        return R.layout.act_preview;
    }

    @Override
    protected void initView() {
        String url = "https://res.exexm.com/cw_145225549855002";
        videoPlayer.setUp(url, true, "");
        //过渡动画
        initTransition();
    }


    @Override
    protected void initAction() {

    }


    @OnClick({R.id.iv_zan,R.id.tv_make})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_zan:
                iv_zan.setImageResource(R.mipmap.zan_selected);
                break;
            case R.id.tv_make:
                videoPlayer.onVideoPause();
                AlbumManager.chooseAlbum(this,7,SELECTALBUM,this,"");
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



    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onDestroy() {
        super.onDestroy();
         videoPlayer.release();
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }



    @Override
    public void onBackPressed() {
        //释放所有
        videoPlayer.setVideoAllCallBack(null);
        GSYVideoManager.releaseAllVideos();
        if (isTransition && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.onBackPressed();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                }
            }, 500);
        }
    }


    private void initTransition() {
        if (isTransition && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
            ViewCompat.setTransitionName(videoPlayer, IMG_TRANSITION);
            addTransitionListener();
            startPostponedEnterTransition();
        } else {
            videoPlayer.startPlayLogic();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean addTransitionListener() {
        transition = getWindow().getSharedElementEnterTransition();
        if (transition != null) {
            transition.addListener(new OnTransitionListener(){
                @Override
                public void onTransitionEnd(Transition transition) {
                    super.onTransitionEnd(transition);
                    videoPlayer.startPlayLogic();
                    transition.removeListener(this);
                }
            });
            return true;
        }
        return false;
    }


    @Override
    public void resultFilePath(int tag, List<String> paths, boolean isCancel, ArrayList<AlbumFile> albumFileList) {
        if(!isCancel){
            if(SELECTALBUM==0){
                Intent intent=new Intent(this,TemplateActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("paths", (ArrayList<String>) paths);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    }
}

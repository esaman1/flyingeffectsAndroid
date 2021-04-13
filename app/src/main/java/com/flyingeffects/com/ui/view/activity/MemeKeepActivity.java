package com.flyingeffects.com.ui.view.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.databinding.ActMemeKeepBinding;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.utils.FileUtil;
import com.lansosdk.videoeditor.VideoEditor;
import com.shixing.sxve.ui.view.WaitingDialog;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.media.UMEmoji;
import com.umeng.socialize.media.UMImage;

import java.io.File;
import java.io.IOException;


/**
 * description ：表情包保存页面
 * creation date: 2021/4/9
 * user : zhangtongju
 */
public class MemeKeepActivity extends BaseActivity {

    private ActMemeKeepBinding mBinding;
    private String mGifFolder;
    private String videoPath;
    private ImageView imageView;

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initView() {
        ActMemeKeepBinding mBinding = ActMemeKeepBinding.inflate(getLayoutInflater());
        View rootView = mBinding.getRoot();
        videoPath = getIntent().getStringExtra("videoPath");
        setContentView(rootView);
        FileManager fileManager = new FileManager();
        mGifFolder = fileManager.getFileCachePath(this, "gifFolder");
        mBinding.llSendFriends.setOnClickListener(this::onViewClick);
        imageView = mBinding.videoItemPlayer;
        WaitingDialog.openPragressDialog(this);
    }

    @Override
    protected void initAction() {
        VideoEditor videoEditor = new VideoEditor();
        String str = videoEditor.executeConvertVideoToGif(videoPath, 10, 720, 1280, 1f);
        File gif = new File(mGifFolder + "/keep.gif");
        if (gif.exists()) {
            gif.delete();
        }
        WaitingDialog.closeProgressDialog();
        File file = new File(str);
        if (file.exists()) {
            try {
                FileUtil.copyFile(file, mGifFolder + "/keep.gif");
                Glide.with(this).load(mGifFolder + "/keep.gif").into(imageView);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void onViewClick(View view) {
        UMEmoji emoji = new UMEmoji(MemeKeepActivity.this, new File(mGifFolder + "/a.gif"));
        emoji.setThumb(new UMImage(MemeKeepActivity.this, R.mipmap.logo));
        new ShareAction(MemeKeepActivity.this)
                .withMedia(emoji).share();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

}

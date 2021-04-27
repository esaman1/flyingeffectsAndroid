package com.flyingeffects.com.ui.view.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.commonlyModel.SaveAlbumPathModel;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.databinding.ActMemeKeepBinding;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.manager.huaweiObs;
import com.flyingeffects.com.ui.model.ShowPraiseModel;
import com.flyingeffects.com.ui.model.TemplateKeepStatistics;
import com.flyingeffects.com.utils.FileUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.utils.record.SaveShareDialog;
import com.lansosdk.videoeditor.MediaInfo;
import com.lansosdk.videoeditor.VideoEditor;
import com.orhanobut.hawk.Hawk;
import com.shixing.sxve.ui.view.WaitingDialog;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMEmoji;
import com.umeng.socialize.media.UMImage;

import java.io.File;
import java.io.IOException;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


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
    private MediaInfo mediaInfo;
    SaveShareDialog mShareDialog;
    private LinearLayout dialogShare;
    private String title;
    private String templateId;

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initView() {
        mBinding = ActMemeKeepBinding.inflate(getLayoutInflater());
        View rootView = mBinding.getRoot();
        videoPath = getIntent().getStringExtra("videoPath");
        templateId=getIntent().getStringExtra("templateId");
        title=getIntent().getStringExtra(title);
        mediaInfo = new MediaInfo(videoPath);
        mediaInfo.prepare();
        setContentView(rootView);
        FileManager fileManager = new FileManager();
        mGifFolder = fileManager.getFileCachePath(this, "gifFolder");
        mBinding.llSendFriends.setOnClickListener(this::onViewClick);
        imageView = mBinding.videoItemPlayer;
        mBinding.llCourse.setOnClickListener(this::onViewClick);
        mBinding.ivBack.setOnClickListener(this::onViewClick);
        mBinding.llKeep.setOnClickListener(this::onViewClick);
        WaitingDialog.openPragressDialog(this);
        dialogShare=findViewById(R.id.dialog_share);
        mShareDialog = new SaveShareDialog(this, dialogShare);
    }

    @Override
    protected void initAction() {
        VideoEditor videoEditor = new VideoEditor();
        String str = videoEditor.executeConvertVideoToGif(videoPath, 5, mediaInfo.getWidth() / 2, mediaInfo.getHeight() / 2, 1f);
        mediaInfo.release();
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
        Glide.with(this).load(BaseConstans.getGifCourse()).into(mBinding.ivShowCourse);
    }


    public void onViewClick(View view) {
        if (view == mBinding.llSendFriends) {
            uploadDressUpImage(mGifFolder + "/keep.gif");
        } else if (view == mBinding.ivBack) {
            finish();
        } else if (view == mBinding.llKeep) {
            TemplateKeepStatistics.getInstance().statisticsToSave(templateId);
            StatisticsEventAffair.getInstance().setFlag(MemeKeepActivity.this, "st_bqb_save", title);
            //保存到本地
            String keepPath = SaveAlbumPathModel.getInstance().getKeepOutputForGif();
            try {
                FileUtil.copyFile(new File(mGifFolder + "/keep.gif"), keepPath);
                albumBroadcast(keepPath);
                showDialog(keepPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }







    /**
     * description ：通知相册更新
     * date: ：2019/8/16 14:24
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    private void albumBroadcast(String outputFile) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(new File(outputFile)));
        sendBroadcast(intent);
    }


    private UMShareListener shareListener = new UMShareListener() {
        /**
         * @descrption 分享开始的回调
         * @param platform 平台类型
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {
        }

        /**
         * @descrption 分享成功的回调
         * @param platform 平台类型
         */
        @Override
        public void onResult(SHARE_MEDIA platform) {
            ToastUtil.showToast("分享成功");
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            String string = t.getMessage();
            ToastUtil.showToast("分享失败");
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
        }
    };


    /**
     * description ：换装接口对接
     * creation date: 2020/12/7
     * user : zhangtongju
     */
    private String needGifPath;

    private void uploadDressUpImage(String path) {
        WaitingDialog.openPragressDialog(this);
        new Thread(() -> {
            String type = path.substring(path.length() - 4);
            String nowTime = StringUtil.getCurrentTimeymd();
            String copyPath = "media/android/upGif/" + nowTime + "/" + System.currentTimeMillis() + type;
            needGifPath = "http://cdn.flying.flyingeffect.com/" + copyPath;
            uploadHuawei(path, copyPath);
        }).start();
    }


    /**
     * description ：上传到华为
     * creation date: 2021/4/14
     * user : zhangtongju
     */
    private void uploadHuawei(String path, String copyPath) {
        LogUtil.d("OOM2", "needGifPath=" + needGifPath);
        huaweiObs.getInstance().uploadFileToHawei(path, copyPath, new huaweiObs.Callback() {
            @Override
            public void isSuccess(String str) {
                Observable.just(str).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        WaitingDialog.closeProgressDialog();
                        shareToWx();
                    }
                });
            }
        });
    }


    /**
     * description ：分享到微信
     * creation date: 2021/4/14
     * user : zhangtongju
     */
    private void shareToWx() {
        Observable.just(needGifPath).observeOn(AndroidSchedulers.mainThread()).subscribe(s -> {
            new Handler().post(() -> {
                UMEmoji emoji = new UMEmoji(MemeKeepActivity.this, s);
                emoji.setThumb(new UMImage(MemeKeepActivity.this, R.mipmap.logo));
                new ShareAction(MemeKeepActivity.this)
                        .withMedia(emoji).setPlatform(SHARE_MEDIA.WEIXIN)
                        .setCallback(shareListener).share();
            });
        });
    }


//    private void showDialog(String path) {
//        if (!com.flyingeffects.com.commonlyModel.DoubleClick.getInstance().isFastDoubleClick()) {
//            ShowPraiseModel.keepAlbumCount();
//            keepAlbumCount();
//            LogUtil.d("showDialog", "showDialog");
//            mShareDialog.createDialog("已保存");
//            mShareDialog.setVideoPath(path);
//        }
//    }


    private void showDialog(String path) {
        if (!com.flyingeffects.com.commonlyModel.DoubleClick.getInstance().isFastDoubleClick()) {
            ShowPraiseModel.keepAlbumCount();
            keepAlbumCount();
            LogUtil.d("showDialog", "showDialog");
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    //去除黑边
                    new ContextThemeWrapper(this, R.style.Theme_Transparent));
            builder.setTitle(this.getString(R.string.notification));
//            builder.setMessage(context.getString(R.string.have_saved_to_sdcard) +
//                    "【" + path + context.getString(R.string.folder) + "】");

            builder.setMessage("已为你保存到相册,多多分享给友友\n" + "【" + path + this.getString(R.string.folder) + "】"
            );


            builder.setNegativeButton(this.getString(R.string.got_it), (dialog, which) -> {
                dialog.dismiss();
            });
            builder.setCancelable(true);
            Dialog mDialog = builder.show();
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
        }
    }


    private void keepAlbumCount() {
        int num = Hawk.get("keepAlbumNum");
        num++;
        Hawk.put("keepAlbumNum", num);
    }


}

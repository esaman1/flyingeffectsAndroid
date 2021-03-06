package com.flyingeffects.com.ui.view.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
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
import com.flyingeffects.com.manager.AdConfigs;
import com.flyingeffects.com.manager.AdManager;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.manager.huaweiObs;
import com.flyingeffects.com.ui.model.GetPathTypeModel;
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
import com.shixing.sxve.ui.AlbumType;
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
import rx.schedulers.Schedulers;


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
    private String templateType;
    /**
     * 0  视频 1 gif
     */
    private int Typematerial = 0;

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initView() {
        mBinding = ActMemeKeepBinding.inflate(getLayoutInflater());
        View rootView = mBinding.getRoot();
        videoPath = getIntent().getStringExtra("videoPath");
        templateId = getIntent().getStringExtra("templateId");
        templateType = getIntent().getStringExtra("templateType");
        title = getIntent().getStringExtra("title");
        LogUtil.d("oom22", "title=" + title);
        if (AlbumType.isVideo(GetPathTypeModel.getInstance().getMediaType(videoPath))) {
            Typematerial = 0;
            mediaInfo = new MediaInfo(videoPath);
            mediaInfo.prepare();
        } else {
            Typematerial = 1;
        }
        setContentView(rootView);
        FileManager fileManager = new FileManager();
        mGifFolder = fileManager.getFileCachePath(this, "gifFolder");
        mBinding.llSendFriends.setOnClickListener(this::onViewClick);
        imageView = mBinding.videoItemPlayer;
        mBinding.llCourse.setOnClickListener(this::onViewClick);
        mBinding.ivBack.setOnClickListener(this::onViewClick);
        mBinding.llKeep.setOnClickListener(this::onViewClick);
        WaitingDialog.openPragressDialog(this);
        dialogShare = findViewById(R.id.dialog_share);
        mShareDialog = new SaveShareDialog(this, dialogShare);
    }


    private String keepGifName;
    private String logoPath;

    @Override
    protected void initAction() {
        if (Typematerial == 0) {
//            VideoConvertGif videoConvertGif = new VideoConvertGif(this);
//            videoConvertGif.ToExtractFrame(videoPath, new VideoConvertGif.CreateGifCallback() {
//                @Override
//                public void callback(boolean isSuccess, String path, String icon) {
//
//                    Observable.just(isSuccess).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
//                        @Override
//                        public void call(Boolean aBoolean) {
//                            if (aBoolean) {
//                                logoPath = icon;
//                                showGif(path);
//                            } else {
//                                ToastUtil.showToast(path);
//                            }
//                        }
//                    });
//
//                }
//            });
            WaitingDialog.openPragressDialog(this);
            Observable.just(videoPath).map(s -> {
                VideoEditor videoEditor = new VideoEditor();
                return  logoPath = videoEditor.executeConvertVideoToGifHasPalette(videoPath, videoEditor.executeConvertVideoToGlobalPalette(videoPath));
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
                @Override
                public void call(String s) {
                    showGif(logoPath);
                    LogUtil.d("OOM2", "logoPath=" + logoPath);
                }
            });
        } else {
            showGif(videoPath);
        }
    }


    private void showGif(String gifPath) {
        WaitingDialog.closeProgressDialog();
        if (!TextUtils.isEmpty(gifPath)) {
            File file = new File(gifPath);
            keepGifName = mGifFolder + File.separator + System.currentTimeMillis() + "keep.gif";
            if (file.exists()) {
                try {
                    FileUtil.copyFile(file, keepGifName);
                    Glide.with(this).load(keepGifName).into(imageView);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Glide.with(this).load(BaseConstans.getGifCourse()).into(mBinding.ivShowCourse);
        } else {
            ToastUtil.showToast("合成失败");
            finish();
        }
    }


    public void onViewClick(View view) {
        if (view == mBinding.llSendFriends) {
            StatisticsEventAffair.getInstance().setFlag(MemeKeepActivity.this, "st_bqb_wechat", title);
            StatisticsEventAffair.getInstance().setFlag(MemeKeepActivity.this, "st_bqb_wechat1");
            uploadDressUpImage(keepGifName);
        } else if (view == mBinding.ivBack) {
            finish();
        } else if (view == mBinding.llKeep) {
            LogUtil.d("OOM22", "埋点st_bqb_save" + title);
            TemplateKeepStatistics.getInstance().statisticsToSave(templateId, title, templateType);
            StatisticsEventAffair.getInstance().setFlag(MemeKeepActivity.this, "st_bqb_save", title);
            StatisticsEventAffair.getInstance().setFlag(MemeKeepActivity.this, "st_bqb_save1");
            //保存到本地
            String keepPath = SaveAlbumPathModel.getInstance().getKeepOutputForGif();
            try {
                FileUtil.copyFile(new File(keepGifName), keepPath);
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
                String ss = "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2483784393,127887195&fm=26&gp=0.jpg";
                if (!TextUtils.isEmpty(logoPath)) {
                    emoji.setThumb(new UMImage(MemeKeepActivity.this, logoPath));
                } else {
                    emoji.setThumb(new UMImage(MemeKeepActivity.this, R.mipmap.logo));
                }
                new ShareAction(MemeKeepActivity.this)
                        .withMedia(emoji).setPlatform(SHARE_MEDIA.WEIXIN)
                        .setCallback(shareListener).share();
            });
        });
    }


    private void showDialog(String path) {
        if (!com.flyingeffects.com.commonlyModel.DoubleClick.getInstance().isFastDoubleClick()) {

            if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
                AdManager.getInstance().showCpAd(MemeKeepActivity.this, AdConfigs.AD_SCREEN_FOR_keep);
            }

            ShowPraiseModel.keepAlbumCount();
            keepAlbumCount();
            LogUtil.d("showDialog", "showDialog");
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    new ContextThemeWrapper(this, R.style.Theme_Transparent));
            builder.setTitle(this.getString(R.string.notification));
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

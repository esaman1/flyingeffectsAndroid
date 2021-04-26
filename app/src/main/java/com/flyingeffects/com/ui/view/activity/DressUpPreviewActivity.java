package com.flyingeffects.com.ui.view.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.AdConfigs;
import com.flyingeffects.com.manager.AdManager;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.ui.model.DressUpModel;
import com.flyingeffects.com.ui.view.dialog.CommonMessageDialog;
import com.flyingeffects.com.utils.FileUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.nineton.ntadsdk.itr.VideoAdCallBack;
import com.nineton.ntadsdk.manager.VideoAdManager;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


/**
 * description ：换装预览界面
 * creation date: 2020/12/7
 *
 * @author : zhangtongju
 */
public class DressUpPreviewActivity extends BaseActivity {


    @BindView(R.id.iv_show_content)
    ImageView iv_show_content;

    @BindView(R.id.dress_up_next)
    ImageView dress_up_next;

    @BindView(R.id.tv_top_title)
    TextView tv_top_title;

    @BindView(R.id.iv_back)
    ImageView iv_back;

    private ArrayList<String> listForKeep = new ArrayList<>();
    private int nowChooseIndex;
    private String template_id;
    private List<String> TemplateIdList = new ArrayList<>();
    private String localImage;
    private String templateTitle;
    /**
     * 不是换脸，而是其他特殊类型页面
     */
    private boolean isSpecial;
    /**
     * 换装切换次数
     */
    int dressupSwitchNumber = 0;
    private Context mContext;

    @Override
    protected int getLayoutId() {
        return R.layout.act_dress_up_preview_new;
    }

    @Override
    protected void initView() {
        mContext = DressUpPreviewActivity.this;
        LogUtil.d("OOM3", "换装页面initView");
        String urlPath = getIntent().getStringExtra("url");
        template_id = getIntent().getStringExtra("template_id");
        localImage = getIntent().getStringExtra("localImage");
        isSpecial = getIntent().getBooleanExtra("isSpecial", false);
        templateTitle = getIntent().getStringExtra("templateTitle");
        tv_top_title.setText("上传清晰正脸照片最佳");
        showAndSaveImage(urlPath);
        requestAllTemplateId();
    }

    @Override
    protected void initAction() {
        if (isSpecial) {
            findViewById(R.id.dress_up_next).setVisibility(View.GONE);
        }
    }


    @Override
    @OnClick({R.id.dress_up_next, R.id.iv_back, R.id.keep_to_album, R.id.share, R.id.iv_top_back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dress_up_next:
                if (!DoubleClick.getInstance().isFastZDYDoubleClick(1000)) {
                    LogUtil.d("nowShowPosition", "nowShowPosition=" + nowShowPosition);
                    if (nowShowPosition != 0 && nowShowPosition % BaseConstans.getDressupIntervalsNumber() == 0 &&
                            BaseConstans.getIncentiveVideo() && BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
                        VideoAdManager videoAdManager = new VideoAdManager();
                        videoAdManager.showVideoAd(this, AdConfigs.AD_DRESSUP_SCREEN_VIDEO, new VideoAdCallBack() {
                            @Override
                            public void onVideoAdSuccess() {
//                                StatisticsEventAffair.getInstance().setFlag(DressUpPreviewActivity.this, "video_ad_alert_request_sucess");
                                LogUtil.d("OOM4", "onVideoAdSuccess");
                            }

                            @Override
                            public void onVideoAdError(String s) {
//                                StatisticsEventAffair.getInstance().setFlag(DressUpPreviewActivity.this, "video_ad_alert_request_fail");
                                LogUtil.d("OOM4", "onVideoAdError" + s);
                                showDressUp(true);
                                iv_back.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onVideoAdClose() {
                                LogUtil.d("OOM4", "onVideoAdClose");
                                showDressUp(true);
                                iv_back.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onRewardVerify() {
                            }

                            @Override
                            public void onVideoAdSkip() {
                                LogUtil.d("OOM4", "onVideoAdSkip");
                            }

                            @Override
                            public void onVideoAdComplete() {
                                LogUtil.d("OOM4", "onVideoAdComplete");
                            }

                            @Override
                            public void onVideoAdClicked() {
                                LogUtil.d("OOM4", "onVideoAdClicked");
                            }
                        });
                    } else {
                        showDressUp(true);
                        iv_back.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case R.id.iv_back:
                if (!DoubleClick.getInstance().isFastDoubleClick()) {
                    if (nowChooseIndex >= 1) {
                        showDressUp(false);
                        iv_back.setVisibility(View.VISIBLE);
                    } else {
                        iv_back.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.keep_to_album:
                alertAlbumUpdate(false);
                StatisticsToSave(template_id);

                if(isSpecial){
                    StatisticsEventAffair.getInstance().setFlag(this, "st_ft_save", templateTitle);
                }else{
                    StatisticsEventAffair.getInstance().setFlag(this, "21_face_save", templateTitle);
                }


                break;
            case R.id.share:
                share(listForKeep.get(nowChooseIndex));
                break;
            case R.id.iv_top_back:
                onBackPressed();
                break;
            default:
                break;
        }

    }

    private void share(String downPath) {
        UMImage image = new UMImage(this, new File(downPath));
        //推荐使用网络图片和资源文件的方式，平台兼容性更高。 对于微信QQ的那个平台，分享的图片需要设置缩略图，缩略图的设置规则为：
        UMImage thumb = new UMImage(DressUpPreviewActivity.this, new File(downPath));
        image.setThumb(thumb);
        //分享图片
        new ShareAction(DressUpPreviewActivity.this).withText(templateTitle)
                .withMedia(image)
                .setPlatform(SHARE_MEDIA.WEIXIN)
                .setCallback(shareListener).share();

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        showBackDialog();
    }

    private void showBackDialog() {
        StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "alert_edit_back_face");
        CommonMessageDialog.getBuilder(mContext)
                .setAdStatus(CommonMessageDialog.AD_STATUS_MIDDLE)
                .setAdId(AdConfigs.AD_IMAGE_EXIT)
                .setTitle("确定退出吗？")
                .setPositiveButton("确定")
                .setNegativeButton("取消")
                .setDialogBtnClickListener(new CommonMessageDialog.DialogBtnClickListener() {
                    @Override
                    public void onPositiveBtnClick(CommonMessageDialog dialog) {
                        dialog.dismiss();
                        finish();
                    }

                    @Override
                    public void onCancelBtnClick(CommonMessageDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .build().show();
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
//            ToastUtil.showToast("分享成功");
            if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
                AdManager.getInstance().showCpAd(DressUpPreviewActivity.this, AdConfigs.AD_SCREEN_FOR_DRESSUP_SAVE_OR_SHARE);
            }
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            LogUtil.d("OOM", "友盟错误日志" + t.getMessage());
            String string = t.getMessage();
            String str = string.substring(string.lastIndexOf("："));
            Toast.makeText(mContext, "失败" + str, Toast.LENGTH_LONG).show();
            if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
                AdManager.getInstance().showCpAd(DressUpPreviewActivity.this, AdConfigs.AD_SCREEN_FOR_DRESSUP_SAVE_OR_SHARE);
            }
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            ToastUtil.showToast("取消了");
            if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
                AdManager.getInstance().showCpAd(DressUpPreviewActivity.this, AdConfigs.AD_SCREEN_FOR_DRESSUP_SAVE_OR_SHARE);
            }
        }
    };

    private int nowShowPosition;

    private void showDressUp(boolean isNext) {
        LogUtil.d("OOM3", "nowChooseIndex=" + nowChooseIndex);

        if (isNext) {
            nowShowPosition = nowChooseIndex + 1;
        } else {
            nowShowPosition = nowChooseIndex - 1;
        }

        if (nowShowPosition == 0) {
            LogUtil.d("OOM3", "隐藏");
            Observable.just(0).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
                @Override
                public void call(Integer integer) {
                    iv_back.setVisibility(View.GONE);
                }
            });

        }
        if (listForKeep.size() > nowShowPosition) {
            LogUtil.d("OOM3", "有过缓存");
            //有过缓存
            String needShowPath = listForKeep.get(nowShowPosition);
            nowChooseIndex = nowShowPosition;
            Glide.with(this).load(needShowPath).apply(new RequestOptions().placeholder(R.mipmap.placeholder)).into(iv_show_content);
        } else {
            LogUtil.d("OOM3", "没得缓存");
            //没有缓存
            if (TemplateIdList.size() > nowShowPosition) {
                String id = TemplateIdList.get(nowShowPosition);
                toNextDressUp(id);
                dressupSwitchNumber++;
            } else {
                ToastUtil.showToast("没有更多换装了");
                LogUtil.d("OOM3", "没有更多换装了");
            }
        }
    }


    private String keepUploadPath;

    /**
     * description ：请求下一条数据
     * creation date: 2020/12/8
     * user : zhangtongju
     */
    private void toNextDressUp(String templateId) {
        DressUpModel dressUpModel = new DressUpModel(this, new DressUpModel.DressUpCallback() {

            @Override
            public void isSuccess(List<String> paths) {
                if (paths != null && paths.size() > 0) {
                    showAndSaveImage(paths.get(0));
                    nowChooseIndex = nowChooseIndex + 1;
                } else {
                    TemplateIdList.remove(nowChooseIndex);
                    showDressUp(true);
                }
            }
        }, false);
        if (!TextUtils.isEmpty(keepUploadPath)) {
            dressUpModel.requestDressUp(keepUploadPath, templateId);
        } else {
            dressUpModel.toDressUp(localImage, templateId, new DressUpModel.DressUpCatchCallback() {
                @Override
                public void isSuccess(String uploadPath) {
                    keepUploadPath = uploadPath;
                }
            });
        }
    }


    /**
     * description ：显示和保存图片
     * creation date: 2020/12/8
     * user : zhangtongju
     */
    private void showAndSaveImage(String url) {
        Glide.with(this).load(url).into(iv_show_content);
        listForKeep.add(url);
        LogUtil.d("OOM3", "showAndSaveImage");
    }


    /**
     * description ：请求全部templateId
     * creation date: 2020/12/8
     * user : zhangtongju
     */
    private void requestAllTemplateId() {
        HashMap<String, String> params = new HashMap<>();
        params.put("template_id", template_id);
        Observable ob = Api.getDefault().template_ids(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<String>>(DressUpPreviewActivity.this) {
            @Override
            protected void onSubError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void onSubNext(List<String> data) {
                String str = StringUtil.beanToJSONString(data);
                LogUtil.d("OOM3", "请求的template数据为" + str);
                TemplateIdList = data;
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);
    }


    /**
     * description ：保存图片在相册
     * creation date: 2020/12/8
     * user : zhangtongju
     */
    private void keepImageToAlbum(String path) {
        String path2 = getKeepOutput();
        try {
            FileUtil.copyFile(new File(path), path2, new FileUtil.copySucceed() {

                @Override
                public void isSucceed() {
                    showKeepSuccessDialog(path2);
                    albumBroadcast(path2);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showKeepSuccessDialog(String path) {
        if (!DoubleClick.getInstance().isFastDoubleClick()) {
            AlertDialog.Builder builder = new AlertDialog.Builder( //去除黑边
                    new ContextThemeWrapper(this, R.style.Theme_Transparent));
            builder.setTitle(R.string.notification);
            builder.setMessage("已为你保存到相册,多多分享给友友\n" + "【" + path + getString(R.string.folder) + "】");
            builder.setNegativeButton(getString(R.string.got_it), (dialog, which) ->
                    dialog.dismiss());
            builder.setCancelable(true);
            Dialog dialog = builder.show();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }


    public String getKeepOutput() {
        String product = android.os.Build.MANUFACTURER; //获得手机厂商
        if ("vivo".equals(product)) {
            File fileCamera = new File(Environment.getExternalStorageDirectory() + "/相机");
            if (fileCamera.exists()) {
                return fileCamera.getPath() + File.separator + System.currentTimeMillis() + "synthetic.png";
            }
        }
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        File pathCamera = new File(path + "/Camera");
        if (pathCamera.exists()) {
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + "Camera" + File.separator + System.currentTimeMillis() + "synthetic.png";
        }
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + System.currentTimeMillis() + "synthetic.png";
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

    public void alertAlbumUpdate(boolean isSuccess) {
        if (!isSuccess) {
            if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
                AdManager.getInstance().showCpAd(this, AdConfigs.AD_SCREEN_FOR_DRESSUP_SAVE_OR_SHARE);
            }
        }
        if (listForKeep.size() > nowChooseIndex) {
            String path = listForKeep.get(nowChooseIndex);
            keepImageToAlbum(path);
        }
    }

    public void StatisticsToSave(String templateId) {
        HashMap<String, String> params = new HashMap<>();
        params.put("template_id", templateId);
        params.put("template_type", "3");
        // 启动时间
        Observable ob = Api.getDefault().saveTemplate(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(this) {
                    @Override
                    protected void onSubError(String message) {
                    }

                    @Override
                    protected void onSubNext(Object data) {

                    }
                }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject,
                false, true, false);

    }

}

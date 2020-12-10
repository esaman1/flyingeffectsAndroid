package com.flyingeffects.com.ui.view.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.HumanMerageResult;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.BitmapManager;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.ui.model.DressUpModel;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.shixing.sxve.ui.view.WaitingDialog;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * description ：换装预览界面
 * creation date: 2020/12/7
 * user : zhangtongju
 */
public class DressUpPreviewActivity extends BaseActivity {


    @BindView(R.id.iv_show_content)
    ImageView iv_show_content;

    @BindView(R.id.dress_up_next)
    ImageView dress_up_next;


    @BindView(R.id.iv_back)
    ImageView iv_back;

    private ArrayList<String> listForKeep = new ArrayList<>();
    private int nowChooseIndex;
    private String template_id;
    private List<String> TemplateIdList = new ArrayList<>();
    private String localImage;
    private String mUploadDressUpFolder;

    @Override
    protected int getLayoutId() {
        return R.layout.act_dress_up_preview_new;
    }

    @Override
    protected void initView() {
        String urlPath = getIntent().getStringExtra("url");
        template_id = getIntent().getStringExtra("template_id");
        localImage = getIntent().getStringExtra("localImage");
        findViewById(R.id.iv_top_back).setOnClickListener(this);
        showAndSaveImage(urlPath);
        requestAllTemplateId();
        FileManager fileManager = new FileManager();
        mUploadDressUpFolder = fileManager.getFileCachePath(this, "DressUpFolder");
    }

    @Override
    protected void initAction() {


    }


    @OnClick({R.id.dress_up_next, R.id.iv_back, R.id.keep_to_album, R.id.share})
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.dress_up_next:
                if (!DoubleClick.getInstance().isFastDoubleClick()) {
                    nowChooseIndex++;
                    showDressUp();
                    iv_back.setVisibility(View.VISIBLE);
                }

                break;


            case R.id.iv_back:
                if (!DoubleClick.getInstance().isFastDoubleClick()) {
                    if (nowChooseIndex >= 1) {
                        nowChooseIndex--;
                        showDressUp();
                        iv_back.setVisibility(View.VISIBLE);
                    } else {
                        iv_back.setVisibility(View.GONE);
                    }
                }

                break;

            case R.id.keep_to_album:
                String path = listForKeep.get(nowChooseIndex);
                keepImageToAlbum(path);
                break;

            case R.id.share:
                share(listForKeep.get(nowChooseIndex));
                break;
        }
        super.onClick(v);

    }


    public static Bitmap GetLocalOrNetBitmap(String url) {
        Bitmap bitmap = null;
        InputStream in = null;
        BufferedOutputStream out = null;
        try {
            in = new BufferedInputStream(new URL(url).openStream(), 1024);
            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, 1024);
            copy(in, out);
            out.flush();
            byte[] data = dataStream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            data = null;
            return bitmap;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private static void copy(InputStream in, OutputStream out)
            throws IOException {
        byte[] b = new byte[1024];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }


    private void share(String downPath) {
        WaitingDialog.openPragressDialog(this);
//
//        UMImage image = new UMImage(DressUpPreviewActivity.this, downPath);//网络图片
//        //推荐使用网络图片和资源文件的方式，平台兼容性更高。 对于微信QQ的那个平台，分享的图片需要设置缩略图，缩略图的设置规则为：
//        UMImage thumb =  new UMImage(DressUpPreviewActivity.this, R.mipmap.logo);
//        image.setThumb(thumb);
//
//        new ShareAction(DressUpPreviewActivity.this).withText("飞闪换装，你也来试下吧")//分享内容
//                .withMedia(image)//分享图片
//                .setPlatform(SHARE_MEDIA.WEIXIN)
//                .setCallback(shareListener).share();



        Observable.just(downPath).map(new Func1<String, Bitmap>() {
            @Override
            public Bitmap call(String s) {
                return GetLocalOrNetBitmap(downPath);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Bitmap>() {
            @Override
            public void call(Bitmap bitmap) {
                WaitingDialog.closePragressDialog();
                UMImage umImage = new UMImage(DressUpPreviewActivity.this, bitmap);
                umImage.setTitle("飞闪换装，你也来试下吧");
                UMImage thumb = new UMImage(DressUpPreviewActivity.this, R.mipmap.logo_circle);
                umImage.setThumb(thumb);
                new ShareAction(DressUpPreviewActivity.this)
                        .withMedia(umImage)
                        .setPlatform(SHARE_MEDIA.WEIXIN)
                        .setCallback(shareListener).share();
            }
        });

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
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            LogUtil.d("OOM","友盟错误日志"+t.getMessage());
            ToastUtil.showToast("失败");
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            ToastUtil.showToast("取消了");
        }
    };


    private void showDressUp() {
        LogUtil.d("OOM3", "nowChooseIndex=" + nowChooseIndex);


        if (listForKeep.size() - 1 >= nowChooseIndex) {
            String needShowPath = listForKeep.get(nowChooseIndex);
            Glide.with(this).load(needShowPath).apply(new RequestOptions().placeholder(R.mipmap.placeholder)).into(iv_show_content);
        } else {
            if (TemplateIdList.size() >= nowChooseIndex) {
                String id = TemplateIdList.get(nowChooseIndex);
                ToNextDressUp(id);
            } else {
                ToastUtil.showToast("没有更多换装了");
            }
        }
    }


    /**
     * description ：请求下一条数据
     * creation date: 2020/12/8
     * user : zhangtongju
     */
    private void ToNextDressUp(String templateId) {
        DressUpModel dressUpModel = new DressUpModel(this, new DressUpModel.DressUpCallback() {
            @Override
            public void isSuccess(List<String> paths) {
                showAndSaveImage(paths.get(0));
            }
        });
        dressUpModel.toDressUp(localImage, templateId);
    }


    /**
     * description ：显示和保存图片
     * creation date: 2020/12/8
     * user : zhangtongju
     */
    private void showAndSaveImage(String url) {
        Glide.with(this).load(url).into(iv_show_content);
        listForKeep.add(url);
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
            protected void _onError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(List<String> data) {
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
    private void keepImageToAlbum(String url) {
        Observable.just(url).map(new Func1<String, Bitmap>() {
            @Override
            public Bitmap call(String s) {
                return BitmapManager.getInstance().GetBitmapForHttp(url);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Bitmap>() {
            @Override
            public void call(Bitmap bitmap) {
                String path = getKeepOutput();
                BitmapManager.getInstance().saveBitmapToPath(bitmap, path, new BitmapManager.saveToFileCallback() {
                    @Override
                    public void isSuccess(boolean isSuccess) {
                        albumBroadcast(path);
                        showKeepSuccessDialog(path);
                    }
                });
            }
        });
    }

    private void showKeepSuccessDialog(String path) {
        if (!DoubleClick.getInstance().isFastDoubleClick()) {
            AlertDialog.Builder builder = new AlertDialog.Builder( //去除黑边
                    new ContextThemeWrapper(this, R.style.Theme_Transparent));
            builder.setTitle(R.string.notification);
            builder.setMessage("已为你保存到相册,多多分享给友友\n" + "【" + path + getString(R.string.folder) + "】");
            builder.setNegativeButton(getString(R.string.got_it), (dialog, which) -> dialog.dismiss());
            builder.setCancelable(true);
            Dialog dialog = builder.show();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }


    public String getKeepOutput() {
        String product = android.os.Build.MANUFACTURER; //获得手机厂商
        if (product != null && product.equals("vivo")) {
            File file_camera = new File(Environment.getExternalStorageDirectory() + "/相机");
            if (file_camera.exists()) {
                return file_camera.getPath() + File.separator + System.currentTimeMillis() + "synthetic.png";
            }
        }
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        File path_Camera = new File(path + "/Camera");
        if (path_Camera.exists()) {
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


}

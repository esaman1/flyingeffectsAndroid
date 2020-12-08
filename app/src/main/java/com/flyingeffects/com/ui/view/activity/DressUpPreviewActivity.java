package com.flyingeffects.com.ui.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.BitmapManager;
import com.flyingeffects.com.ui.model.DressUpModel;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;


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

    private ArrayList<String> listForKeep = new ArrayList<>();
    private int nowChooseIndex;
    private String template_id;
    private List<String> TemplateIdList = new ArrayList<>();
    private String localImage;

    @Override
    protected int getLayoutId() {
        return R.layout.act_dress_up_preview_new;
    }

    @Override
    protected void initView() {
        String urlPath = getIntent().getStringExtra("url");
        template_id = getIntent().getStringExtra("template_id");
        localImage = getIntent().getStringExtra("localImage");
        showAndSaveImage(urlPath);
        requestAllTemplateId();
    }

    @Override
    protected void initAction() {


    }


    @OnClick({R.id.dress_up_next, R.id.iv_back, R.id.keep_to_album})
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.dress_up_next:
                nowChooseIndex++;
                showDressUp();
                break;


            case R.id.iv_back:
                nowChooseIndex--;
                showDressUp();
                break;

            case R.id.keep_to_album:
                String path = listForKeep.get(nowChooseIndex);
                keepImageToAlbum(path);

                break;
        }
        super.onClick(v);

    }


    private void showDressUp() {
        if (listForKeep.size() - 1 <= nowChooseIndex) {
            if (TemplateIdList.size() >= nowChooseIndex) {
                String id = TemplateIdList.get(nowChooseIndex);
                ToNextDressUp(id);
            } else {
                ToastUtil.showToast("没有更多换装了");
            }
        } else {
            String needShowPath = TemplateIdList.get(nowChooseIndex);
            Glide.with(this).load(needShowPath).into(iv_show_content);
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
            public void isSuccess(String url) {
                showAndSaveImage(url);
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
        Bitmap bp = BitmapManager.getInstance().GetBitmapForHttp(url);
        String path = getKeepOutput();
        BitmapManager.getInstance().saveBitmapToPath(bp, path, new BitmapManager.saveToFileCallback() {
            @Override
            public void isSuccess(boolean isSuccess) {
                albumBroadcast(path);
            }
        });
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

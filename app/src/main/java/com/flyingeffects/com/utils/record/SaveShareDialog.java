package com.flyingeffects.com.utils.record;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import com.bytedance.sdk.open.aweme.DYOpenConstants;
import com.bytedance.sdk.open.aweme.api.TiktokOpenApi;
import com.bytedance.sdk.open.aweme.base.DYMediaContent;
import com.bytedance.sdk.open.aweme.base.DYVideoObject;
import com.bytedance.sdk.open.aweme.impl.TikTokOpenApiFactory;
import com.bytedance.sdk.open.aweme.share.Share;
import com.flyingeffects.com.R;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.view.activity.UploadMaterialActivity;
import com.flyingeffects.com.utils.ToastUtil;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMMin;

import java.util.ArrayList;

import androidx.annotation.NonNull;

/**
 * @author ZhouGang
 * @date 2020/12/2
 * 保存视频时底部弹出对话框
 */
public class SaveShareDialog extends Dialog {
    ArrayList<String> videoPaths = new ArrayList<>();
    TiktokOpenApi bdOpenApi;
    Context mContext;

    public SaveShareDialog(@NonNull Context context) {
        super(context,R.style.BottomDialog_Animation);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_save_share);
        getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
        bdOpenApi = TikTokOpenApiFactory.create(mContext);

        findViewById(R.id.bt_publish_Douyin).setOnClickListener(v -> {
            Share.Request request = new Share.Request();
            DYVideoObject videoObject = new DYVideoObject();
            videoObject.mVideoPaths = videoPaths;
            DYMediaContent content = new DYMediaContent();
            content.mMediaObject = videoObject;
            request.mMediaContent = content;
            request.mState = "ss";
            request.mTargetApp = DYOpenConstants.TARGET_APP.AWEME;
            request.callerLocalEntry = "com.flyingeffects.com.bdopen.BdEntryActivity";
            boolean isShared = bdOpenApi.share(request);
            if (!isShared) {
                ToastUtil.showToast("未分享成功，可能是未安装抖音，请安装后重试");
            } else {
                dismiss();
            }

            statisticsEventAffair.getInstance().setFlag(mContext, "21_save_douying");
        });
        findViewById(R.id.bt_share_wx).setOnClickListener(v -> {
            //分享小程序
            UMImage image = new UMImage(mContext, "http://cdn.flying.flyingeffect.com/admin/20200702/5efd9be4f075bshare.png");
            String url = "pages/background/background?path=detail&from_path=app&id=5";
            UMMin umMin = new UMMin(url);
            umMin.setPath(url);
            umMin.setThumb(image);
            umMin.setUserName("gh_4161ca2837f7");
            umMin.setTitle(BaseConstans.getminapp_share_title() + "多人分身制作【教程】");
            new ShareAction((Activity) mContext)
                    .withMedia(umMin)
                    .setPlatform(SHARE_MEDIA.WEIXIN)
                    .setCallback(shareListener).share();
        });
        findViewById(R.id.bt_upload_feishan).setOnClickListener(v -> {
            statisticsEventAffair.getInstance().setFlag(mContext, "21_save_flyingfighting");

            Intent intent = new Intent(mContext, UploadMaterialActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("videoPath", videoPaths.get(0));
            mContext.startActivity(intent);
            dismiss();
        });
        findViewById(R.id.bt_cancel).setOnClickListener(v -> dismiss());
    }

    public void setVideoPath(String videoPath) {
        videoPaths.clear();
        videoPaths.add(videoPath);
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
            statisticsEventAffair.getInstance().setFlag(mContext, "21_save_wechat");
            dismiss();
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(mContext, "失败" + t.getMessage(), Toast.LENGTH_LONG).show();
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(mContext, "取消了", Toast.LENGTH_LONG).show();
        }
    };
}

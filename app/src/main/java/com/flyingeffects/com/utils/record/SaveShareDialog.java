package com.flyingeffects.com.utils.record;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bytedance.sdk.open.aweme.base.MediaContent;
import com.bytedance.sdk.open.aweme.base.VideoObject;
import com.bytedance.sdk.open.aweme.share.Share;
import com.bytedance.sdk.open.douyin.DouYinOpenApiFactory;
import com.bytedance.sdk.open.douyin.api.DouYinOpenApi;
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
public class SaveShareDialog  {
    ArrayList<String> videoPaths = new ArrayList<>();
    DouYinOpenApi bdOpenApi;
    Activity mContext;
    LinearLayout dialogShare;

    public SaveShareDialog(@NonNull Activity context, LinearLayout dialogShare) {
        mContext = context;
        this.dialogShare = dialogShare;
        bdOpenApi = DouYinOpenApiFactory.create(mContext);
    }

    public void createDialog(String topic) {
        dialogShare.setVisibility(View.VISIBLE);
        dialogShare.findViewById(R.id.bt_publish_Douyin).setOnClickListener(v -> {
            Share.Request request = new Share.Request();
            VideoObject videoObject = new VideoObject ();
            videoObject.mVideoPaths = videoPaths;
            MediaContent content = new MediaContent();
            content.mMediaObject = videoObject;
            request.mMediaContent = content;
            ArrayList<String> hashTags = new ArrayList<>();
            request.mHashTagList =hashTags;
            if (TextUtils.isEmpty(topic)) {
                hashTags.add(BaseConstans.getDouyingTopic());
            } else {
                hashTags.add(topic);
            }
            request.mState = "ss";
            request.callerLocalEntry = "com.flyingeffects.com.douyinapi.DouYinEntryActivity";
            boolean isShared = bdOpenApi.share(request);
            if (!isShared) {
                ToastUtil.showToast("未分享成功，可能是未安装抖音，请安装后重试");
            } else {
                dialogShare.setVisibility(View.GONE);
            }

            statisticsEventAffair.getInstance().setFlag(mContext, "21_save_douying");
        });
        dialogShare.findViewById(R.id.bt_share_wx).setOnClickListener(v -> {
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
        dialogShare.findViewById(R.id.bt_upload_feishan).setOnClickListener(v -> {
            statisticsEventAffair.getInstance().setFlag(mContext, "21_save_flyingfighting");

            Intent intent = new Intent(mContext, UploadMaterialActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("videoPath", videoPaths.get(0));
            mContext.startActivity(intent);
            dialogShare.setVisibility(View.GONE);
        });
        dialogShare.findViewById(R.id.bt_cancel).setOnClickListener(v -> dialogShare.setVisibility(View.GONE));
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
            dialogShare.setVisibility(View.GONE);
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

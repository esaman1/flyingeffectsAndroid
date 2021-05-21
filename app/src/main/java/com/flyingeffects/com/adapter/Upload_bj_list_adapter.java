package com.flyingeffects.com.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.BuildConfig;
import com.flyingeffects.com.R;
import com.flyingeffects.com.entity.NewFragmentTemplateItem;
import com.flyingeffects.com.manager.AlbumManager;
import com.flyingeffects.com.manager.GlideRoundTransform;
import com.flyingeffects.com.ui.view.activity.UploadMaterialActivity;
import com.flyingeffects.com.utils.PermissionUtil;

import java.util.List;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;


/**
 * user :TongJu  ; email:jutongzhang@sina.com
 * time：2019/1/25
 * describe:我上传的背景
 **/
public class Upload_bj_list_adapter extends BaseQuickAdapter<NewFragmentTemplateItem, BaseViewHolder> {

    private Context context;
    public final static String TAG = "MainRecyclerAdapter";
    UploadBackgroundListener mListener;

    public Upload_bj_list_adapter(int layoutResId, @Nullable List<NewFragmentTemplateItem> allData, Context context, UploadBackgroundListener listener) {
        super(layoutResId, allData);
        this.context = context;
        mListener = listener;
    }


    @Override
    protected void convert(final BaseViewHolder helper, final NewFragmentTemplateItem item) {
        int offset = helper.getLayoutPosition();
        ImageView blackLucency = helper.getView(R.id.black_lucency);
        ImageView ivCover = helper.getView(R.id.iv_cover);
        AppCompatImageView ivDelete = helper.getView(R.id.iv_delete);
        TextView tvAudit = helper.getView(R.id.tv_audit);
        TextView tvPlayNum = helper.getView(R.id.tv_play_num);
        ivDelete.setOnClickListener(v -> {
            mListener.onDelete(item.getId()+"");
        });
        if (item.getTest() == 0) {
            tvPlayNum.setVisibility(View.VISIBLE);
            tvPlayNum.setText(item.getPreview());
            blackLucency.setVisibility(View.GONE);
            tvAudit.setVisibility(View.GONE);
            //审核成功
            Glide.with(context)
                    .load(item.getImage())
                    .apply(bitmapTransform(new GlideRoundTransform(context, 5)))
                    .into(ivCover);
        } else {
            tvPlayNum.setVisibility(View.GONE);
            blackLucency.setVisibility(View.VISIBLE);
            tvAudit.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(item.getImage())
                    .apply(bitmapTransform(new GlideRoundTransform(context, 5)))
                    .into(ivCover);

//            Glide.with(context)
//                    .load(item.getImage())
//                    .apply(bitmapTransform(new BlurTransformation(context 25, 4)))
//                    .into(ivCover);
            if (item.getTest() == 1) {
                //审核中
                GradientDrawable view_ground = (GradientDrawable) tvAudit.getBackground(); //获取控件的背
                view_ground.setStroke(2, Color.parseColor("#FEE131"));
                tvAudit.setTextColor(Color.parseColor("#FEE131"));
                tvAudit.setText("审核中");
            } else {
                //未通过
                GradientDrawable view_ground = (GradientDrawable) tvAudit.getBackground(); //获取控件的背
                view_ground.setStroke(2, Color.parseColor("#FF7272"));
                tvAudit.setTextColor(Color.parseColor("#FF7272"));
                tvAudit.setText("未通过：" + item.getRemark());
            }
        }
        ImageView ivUpload = helper.getView(R.id.iv_upload);
//        TextView tv_name = helper.getView(R.id.tv_name);
//        tv_name.setText(item.getAuth());
        if (offset == 0) {
            ivDelete.setVisibility(View.GONE);
            ivCover.setVisibility(View.GONE);
            ivUpload.setVisibility(View.VISIBLE);
            tvPlayNum.setVisibility(View.GONE);
//            tv_name.setVisibility(View.GONE);
            ivUpload.setOnClickListener(v -> {
                PackageManager pm = mContext.getPackageManager();
                if (pm.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, BuildConfig.APPLICATION_ID)
                        == PackageManager.PERMISSION_GRANTED) {
                    uploadVideo(item);
                }else {
                    new AlertDialog.Builder(mContext)
                            .setMessage("读取相册必须获取存储权限，如需使用接下来的功能，请同意授权~")
                            .setNegativeButton("取消", (dialog, which) -> {
                                dialog.dismiss();
                            })
                            .setPositiveButton("去授权", (dialog, which) -> {
                                PermissionUtil.gotoPermission(mContext);
                                dialog.dismiss();
                            }).create()
                            .show();
                }

            });
        } else {
            ivDelete.setVisibility(View.VISIBLE);
            ivUpload.setVisibility(View.GONE);
//            tv_name.setVisibility(View.VISIBLE);
            ivCover.setVisibility(View.VISIBLE);
        }
    }

    private void uploadVideo(NewFragmentTemplateItem item) {
        AlbumManager.chooseVideo((Activity) context, 1, 1, (tag, paths, isCancel, isFromCamera, albumFileList) -> {
            if (!isCancel) {
                Intent intent = new Intent(context, UploadMaterialActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("videoPath", paths.get(0));
                context.startActivity(intent);
            }
        }, "");
    }

    public interface UploadBackgroundListener{
        void onDelete(String id);
    }


}










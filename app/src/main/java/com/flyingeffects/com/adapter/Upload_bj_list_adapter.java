package com.flyingeffects.com.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.Group;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.R;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.manager.AlbumManager;
import com.flyingeffects.com.manager.GlideRoundTransform;
import com.flyingeffects.com.ui.view.activity.UploadMaterialActivity;

import java.util.List;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;


/**
 * user :TongJu  ; email:jutongzhang@sina.com
 * time：2019/1/25
 * describe:我上传的背景
 **/
public class Upload_bj_list_adapter extends BaseQuickAdapter<new_fag_template_item, BaseViewHolder> {

    private Context context;
    public final static String TAG = "main_recycler_adapter";
    UploadBackgroundListener mListener;

    public Upload_bj_list_adapter(int layoutResId, @Nullable List<new_fag_template_item> allData, Context context,UploadBackgroundListener listener) {
        super(layoutResId, allData);
        this.context = context;
        mListener = listener;
    }


    @Override
    protected void convert(final BaseViewHolder helper, final new_fag_template_item item) {
        int offset = helper.getLayoutPosition();
        ImageView black_lucency = helper.getView(R.id.black_lucency);
        ImageView iv_cover = helper.getView(R.id.iv_cover);
        AppCompatImageView ivDelete = helper.getView(R.id.iv_delete);
        TextView tv_audit = helper.getView(R.id.tv_audit);
        TextView tv_play_num = helper.getView(R.id.tv_play_num);
        ivDelete.setOnClickListener(v -> {
            mListener.onDelete(item.getId());
        });
        if (item.getTest() == 0) {
            tv_play_num.setVisibility(View.VISIBLE);
            tv_play_num.setText(item.getPreview());
            black_lucency.setVisibility(View.GONE);
            tv_audit.setVisibility(View.GONE);
            //审核成功
            Glide.with(context)
                    .load(item.getImage())
                    .apply(bitmapTransform(new GlideRoundTransform(context, 5)))
                    .into(iv_cover);
        } else {
            tv_play_num.setVisibility(View.GONE);
            black_lucency.setVisibility(View.VISIBLE);
            tv_audit.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(item.getImage())
                    .apply(bitmapTransform(new GlideRoundTransform(context, 5)))
                    .into(iv_cover);


//            Glide.with(context)
//                    .load(item.getImage())
//                    .apply(bitmapTransform(new BlurTransformation(context 25, 4)))
//                    .into(iv_cover);
            if (item.getTest() == 1) {
                //审核中
                GradientDrawable view_ground = (GradientDrawable) tv_audit.getBackground(); //获取控件的背
                view_ground.setStroke(2, Color.parseColor("#FEE131"));
                tv_audit.setTextColor(Color.parseColor("#FEE131"));
                tv_audit.setText("审核中");
            } else {
                //未通过
                GradientDrawable view_ground = (GradientDrawable) tv_audit.getBackground(); //获取控件的背
                view_ground.setStroke(2, Color.parseColor("#FF7272"));
                tv_audit.setTextColor(Color.parseColor("#FF7272"));
                tv_audit.setText("未通过：" + item.getRemark());
            }
        }
        ImageView ivUpload = helper.getView(R.id.iv_upload);
//        TextView tv_name = helper.getView(R.id.tv_name);
//        tv_name.setText(item.getAuth());
        if (offset == 0) {
            ivDelete.setVisibility(View.GONE);
            iv_cover.setVisibility(View.GONE);
            ivUpload.setVisibility(View.VISIBLE);
            tv_play_num.setVisibility(View.GONE);
//            tv_name.setVisibility(View.GONE);
            ivUpload.setOnClickListener(v -> {
                uploadVideo(item);
            });
        } else {
            ivDelete.setVisibility(View.VISIBLE);
            ivUpload.setVisibility(View.GONE);
//            tv_name.setVisibility(View.VISIBLE);
            iv_cover.setVisibility(View.VISIBLE);
        }
    }


    private void uploadVideo(new_fag_template_item item) {
        AlbumManager.chooseVideo((Activity) context, 1, 1, (tag, paths, isCancel, albumFileList) -> {
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










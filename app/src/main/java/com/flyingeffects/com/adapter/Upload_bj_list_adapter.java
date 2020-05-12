package com.flyingeffects.com.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.R;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.manager.AlbumManager;
import com.flyingeffects.com.manager.GlideRoundTransform;
import com.flyingeffects.com.ui.view.activity.UploadMaterialActivity;

import java.util.List;


/**
 * user :TongJu  ; email:jutongzhang@sina.com
 * time：2019/1/25
 * describe:我上传的背景
 **/
public class Upload_bj_list_adapter extends BaseQuickAdapter<new_fag_template_item, BaseViewHolder> {

    private Context context;
    public final static String TAG = "main_recycler_adapter";

    public Upload_bj_list_adapter(int layoutResId, @Nullable List<new_fag_template_item> allData, Context context) {
        super(layoutResId, allData);
        this.context = context;
    }


    @Override
    protected void convert(final BaseViewHolder helper, final new_fag_template_item item) {
        int offset = helper.getLayoutPosition();
        Glide.with(context)
                .load(item.getImage())
                .apply(RequestOptions.bitmapTransform(new GlideRoundTransform(context, 5)))
                .into((ImageView) helper.getView(R.id.iv_cover));
        ImageView iv_show_author = helper.getView(R.id.iv_show_author);
        RelativeLayout ll_relative_1 = helper.getView(R.id.ll_relative_1);
        RelativeLayout ll_relative_0 = helper.getView(R.id.ll_relative_0);
        TextView tv_name = helper.getView(R.id.tv_name);
        tv_name.setText(item.getTitle());
        if (offset == 0) {
            ll_relative_1.setVisibility(View.GONE);
            ll_relative_0.setVisibility(View.VISIBLE);
            ll_relative_0.setOnClickListener(v -> {
                uploadVideo();

            });
        } else {
            ll_relative_0.setVisibility(View.GONE);
            ll_relative_1.setVisibility(View.VISIBLE);
        }
        //背景
        iv_show_author.setVisibility(View.VISIBLE);
        tv_name.setVisibility(View.GONE);

    }


    private void uploadVideo() {
        AlbumManager.chooseVideo((Activity) context, 1, 1, (tag, paths, isCancel, albumFileList) -> {
            Intent intent = new Intent(context, UploadMaterialActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("videoPath", paths.get(0));
            context.startActivity(intent);
        }, "");

    }


}









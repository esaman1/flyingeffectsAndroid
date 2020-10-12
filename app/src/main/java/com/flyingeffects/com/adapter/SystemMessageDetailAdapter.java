package com.flyingeffects.com.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.TimeUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.R;
import com.flyingeffects.com.enity.SystemMessageDetailEnity;
import com.flyingeffects.com.utils.timeUtils;
import com.google.android.exoplayer2.C;

import java.util.List;


/**
 * user :TongJu  ; email:jutongzhang@sina.com
 * time：2020/7/29
 * describe:消息页面
 **/
public class SystemMessageDetailAdapter extends BaseQuickAdapter<SystemMessageDetailEnity, BaseViewHolder> {

    private Context context;
    //1是消息页面的赞
    int isFrom;
    public final static String TAG = "main_recycler_adapter";

    public SystemMessageDetailAdapter(int layoutResId, @Nullable List<SystemMessageDetailEnity> data, Context context) {
        super(layoutResId, data);
        this.context = context;
    }


    @Override
    protected void convert(final BaseViewHolder helper, final SystemMessageDetailEnity item) {
//        int offset = helper.getLayoutPosition();

        //文字消息
        LinearLayout ll_text_message = helper.getView(R.id.ll_text_message);

        //文字消息内容
        TextView tv_message_content = helper.getView(R.id.tv_message_content);

        TextView tv_template_content = helper.getView(R.id.tv_template_content);

        //模板消息
        LinearLayout ll_template_message = helper.getView(R.id.ll_template_message);


        //模板图片
        ImageView iv_cover = helper.getView(R.id.iv_cover);
//        setLayoutParams(iv_cover);
        helper.addOnClickListener(R.id.tv_make);
        TextView tv_time = helper.getView(R.id.tv_time);
        int type = item.getType();
        if (type == 1) {
            //文字消息
            ll_text_message.setVisibility(View.VISIBLE);
            ll_template_message.setVisibility(View.GONE);
            tv_message_content.setText(item.getContent());
        } else if (type == 2) {
            //模板消息
            ll_template_message.setVisibility(View.VISIBLE);
            ll_text_message.setVisibility(View.GONE);
            Glide.with(context)
                    .load(item.getImage()).into(iv_cover);
            tv_template_content.setText(item.getContent());
        }
        String createTime = item.getCreate_time();
        long time = Long.parseLong(createTime);
        tv_time.setText(timeUtils.GetSystemMessageTime(time));
    }


    private void setLayoutParams(ImageView imageView) {
        float oriRatio = 9f / 16f;
        LinearLayout.LayoutParams RelativeLayoutParams2 = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        int width = imageView.getWidth();
        RelativeLayoutParams2.width = width;
        RelativeLayoutParams2.width = Math.round(1f * width * oriRatio);
        imageView.setLayoutParams(RelativeLayoutParams2);

    }


}










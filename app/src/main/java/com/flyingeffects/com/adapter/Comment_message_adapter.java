package com.flyingeffects.com.adapter;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.R;
import com.flyingeffects.com.enity.MessageData;
import com.flyingeffects.com.enity.MessageEnity;
import com.flyingeffects.com.enity.systemessagelist;

import java.util.List;


/**
 * user :TongJu  ; 预览视频详情评论页面,回复的话打算设置5个已回复
 * time：2020/7/29
 * describe:消息页面
 **/
public class Comment_message_adapter extends BaseItemDraggableAdapter<MessageEnity, BaseViewHolder> {

    private Context context;
    public final static String TAG = "main_recycler_adapter";
    private ListView listView;
    private Comment_message_item_adapter adapter;
    private LinearLayout ll_more_comment;
    private CommentOnItemClick callback;

    public Comment_message_adapter(int layoutResId, List<MessageEnity> data, Context context,CommentOnItemClick callback) {
        super(layoutResId, data);
        this.context = context;
        this.callback=callback;
    }


    @Override
    protected void convert(final BaseViewHolder helper, final MessageEnity item) {
//        int offset = helper.getLayoutPosition();
        ImageView iv_comment_head = helper.getView(R.id.iv_comment_head);
        //主层用户头像
        Glide.with(context)
                .load(item.getPhotourl())
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(iv_comment_head);
        helper.setText(R.id.tv_user_id, item.getUser_id());
        helper.setText(R.id.tv_content, item.getContent());
        listView = helper.getView(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                callback.clickPosition(i,item.getId());
            }
        });
        ll_more_comment=helper.getView(R.id.ll_more_comment);
//        //显示第一个预览评论
        if (item.getReply() != null && item.getReply().size() > 0) {
            listView.setVisibility(View.VISIBLE);
            adapter = new Comment_message_item_adapter(item.getReply(), context);
            listView.setAdapter(adapter);
            if(item.getReply().size()>=10){
                ll_more_comment.setVisibility(View.VISIBLE);
            }else{
                ll_more_comment.setVisibility(View.GONE);
            }
        }else{
            listView.setVisibility(View.GONE);
        }
    }




    public interface  CommentOnItemClick{
        void clickPosition(int position,String message_id);
    }


}










package com.flyingeffects.com.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.R;
import com.flyingeffects.com.enity.MessageEnity;
import com.flyingeffects.com.ui.view.activity.MessageLongClickActivity;
import com.flyingeffects.com.view.MyListView;

import java.util.List;


/**
 * user :TongJu  ; 预览视频详情评论页面,回复的话打算设置5个已回复
 * time：2020/7/29
 * describe:消息页面
 **/
public class Comment_message_adapter extends BaseQuickAdapter<MessageEnity, BaseViewHolder> {

    private Context context;
    public final static String TAG = "main_recycler_adapter";
    private Comment_message_item_adapter adapter;
    private LinearLayout ll_more_comment;
    private CommentOnItemClick callback;
    private click2Comment clickCommentCallback;

    public Comment_message_adapter(int layoutResId, List<MessageEnity> data, Context context, CommentOnItemClick callback, click2Comment clickCommentCallback) {
        super(layoutResId, data);
        this.context = context;
        this.callback = callback;
        this.clickCommentCallback = clickCommentCallback;
    }


    @Override
    protected void convert(final BaseViewHolder helper, final MessageEnity item) {
        int offset = helper.getLayoutPosition();
        ImageView iv_comment_head = helper.getView(R.id.iv_comment_head);
        MyListView listView = helper.getView(R.id.listView);
        //主层用户头像
        Glide.with(context)
                .load(item.getPhotourl())
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(iv_comment_head);
        helper.addOnClickListener(R.id.ll_parent);
        helper.addOnLongClickListener(R.id.ll_parent);
        helper.addOnClickListener(R.id.iv_comment_head);
        helper.setText(R.id.tv_content, item.getContent());
        helper.setText(R.id.tv_user_id, item.getNickname());
        ll_more_comment = helper.getView(R.id.ll_more_comment);
//        //显示第一个预览评论
//        if (item.getReply() != null && item.getReply().size() > 0) {
//            ll_more_comment.setVisibility(View.VISIBLE);
//            ll_more_comment.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    clickCommentCallback.click(offset);
//                }
//            });
//        } else {
//            ll_more_comment.setVisibility(View.GONE);
//        }

        if (item.isOpenComment()) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            callback.clickPosition(i, item.getId());


                }
            });

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//                    callback.longClickPosition(i, item.getId());
                    Intent intent = new Intent(context, MessageLongClickActivity.class);
                    intent.putExtra("user_id", item.getReply().get(i).getUser_id());
                    intent.putExtra("message_id", item.getReply().get(i).getId());
                    intent.putExtra("templateId", item.getReply().get(i).getTemplate_id());
                    intent.putExtra("position", i);
                    intent.putExtra("isFirstComment", false);
                     context.startActivity(intent);
                    return false;
                }
            });



            adapter = new Comment_message_item_adapter(item.getReply(), context);
            listView.setAdapter(adapter);
            adapter.setCommentListener(new Comment_message_item_adapter.OnItemCommentListener() {
                @Override
                public void clickComment(String id) {
                    callback.clickItemComment(id);
                }
            });
            if (item.getReply().size() >= 10) {
                ll_more_comment.setVisibility(View.VISIBLE);
            } else {
                ll_more_comment.setVisibility(View.GONE);
            }
            listView.setVisibility(View.VISIBLE);
        }else{
            listView.setVisibility(View.GONE);
        }
    }


    public interface CommentOnItemClick {
        void clickPosition(int position, String message_id);
        void clickItemComment(String id);
    }

    public interface click2Comment {

        void click(int position);
    }


}










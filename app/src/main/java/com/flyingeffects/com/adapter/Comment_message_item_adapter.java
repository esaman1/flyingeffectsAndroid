package com.flyingeffects.com.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.flyingeffects.com.R;
import com.flyingeffects.com.entity.MessageReply;
import com.flyingeffects.com.ui.view.activity.UserHomepageActivity;

import java.util.ArrayList;

/**
 * user :TongJu  ;描述：评论的查看更多
 * 时间：2018/5/3
 **/
public class Comment_message_item_adapter extends BaseAdapter {


    private ArrayList<MessageReply> SearchList;
    private Context context;


    public Comment_message_item_adapter(ArrayList<MessageReply> SearchList, Context context) {
        this.SearchList = SearchList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return SearchList.size();
    }

    @Override
    public Object getItem(int i) {
        return SearchList.get(i);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHold holder;
        if (view == null) {
            holder = new ViewHold();
            view = LayoutInflater.from(context).inflate(R.layout.item_comment_message, parent, false);
            holder.iv_comment_head_0 = view.findViewById(R.id.iv_comment_head_0);
            holder.tv_content_1 = view.findViewById(R.id.tv_content_1);
            holder.tv_user_0=view.findViewById(R.id.tv_user_0);
            holder.llComment = view.findViewById(R.id.ll_item_comment);
            view.setTag(holder);
        } else {
            holder = (ViewHold) view.getTag();
        }
        MessageReply data = SearchList.get(position);



        Glide.with(context)
                .load(data.getPhotourl())
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(holder.iv_comment_head_0);

        holder.iv_comment_head_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //进入到用户主页
                Intent intent = new Intent(context, UserHomepageActivity.class);
                intent.putExtra("toUserId",  data.getUser_id());
                context. startActivity(intent);
            }
        });

        holder.llComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commentListener != null) {
                    commentListener.clickComment(SearchList.get(position).getId(),SearchList.get(position).getNickname());
                }
            }
        });
        holder.llComment.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (commentListener != null) {
                    commentListener.clickLongDelete(position);
                }
                return false;
            }
        });

        holder.tv_content_1.setText(data.getContent());

        if (SearchList.get(position).isReply()) {
            holder.tv_user_0.setText(SearchList.get(position).getNickname() + " 回复 " + SearchList.get(position).getTo_user_nickname());
        } else {
            holder.tv_user_0.setText(SearchList.get(position).getNickname());
        }
        return view;
    }




    class ViewHold {
        ImageView  iv_comment_head_0;
        TextView tv_user_0;
        TextView tv_content_1;
        LinearLayout llComment;
    }

    public interface OnItemCommentListener{
        void clickComment(String id,String nickName);
        void clickLongDelete(int position);
    }

    OnItemCommentListener commentListener;

    public void setCommentListener(OnItemCommentListener commentListener) {
        this.commentListener = commentListener;
    }
}

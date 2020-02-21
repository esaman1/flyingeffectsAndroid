package com.shixing.sxve.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shixing.sxve.R;
import com.shixing.sxve.ui.model.TemplateModel;
import com.shixing.sxve.ui.view.GroupThumbView;

import java.util.ArrayList;
import java.util.List;

public class GroupThumbAdapter extends RecyclerView.Adapter<GroupThumbAdapter.GroupThumbHolder> {
    private TemplateModel mTemplateModel;
    private int mSelectedItem;
    private OnItemSelectedListener mOnItemSelectedListener;
    private Context context;

    private ArrayList<Integer>list=new ArrayList<>();  //选中状态标志栏
    private List<String> thumbPaths;
    private boolean isAuto=false;
    public GroupThumbAdapter(Context context, List<String> thumbPaths) {
        this.thumbPaths=thumbPaths;
        this.context=context;
    }


    public GroupThumbAdapter(TemplateModel templateModel, Context context) {
        mTemplateModel = templateModel;


        this.context=context;
    }

    public GroupThumbAdapter(TemplateModel templateModel) {
        mTemplateModel = templateModel;
    }

    @NonNull
    @Override
    public GroupThumbHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_group_thumb,parent,false);
        GroupThumbView groupThumbView = new GroupThumbView(parent.getContext());
        groupThumbView.setBackgroundColor(Color.BLACK);
        return new GroupThumbHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupThumbHolder holder, int position) {
        holder.groupThumbView.setAssetGroup(mTemplateModel.groups.get(position + 1));
        holder.groupThumbView.setSelected(position == mSelectedItem);
        if(position<thumbPaths.size()){
            Glide.with(context).load(thumbPaths.get(position)).into(holder.iv_show_un_select);
        }else{
            holder.iv_show_un_select.setImageResource(R.drawable.ic_launcher);
        }
         holder.tv_num.setText(position + 1+"");
         if(list.get(position)==0){  //选中状态
             holder.iv_select.setVisibility(View.VISIBLE);
             if(isAuto){
                 holder.groupThumbView.setVisibility(View.GONE);
                 holder.iv_show_un_select.setVisibility(View.VISIBLE);
             }else{
                 holder.groupThumbView.setVisibility(View.VISIBLE);
                 holder.iv_show_un_select.setVisibility(View.GONE);
             }
         }else{
             holder.iv_select.setVisibility(View.GONE);
             holder.groupThumbView.setVisibility(View.GONE);
             holder.iv_show_un_select.setVisibility(View.VISIBLE);
         }
    }

    @Override
    public int getItemCount() {
        return mTemplateModel == null ? 0 : mTemplateModel.groupSize;
    }

    public void setTemplateModel(TemplateModel templateModel) {
        for (int i=0;i<templateModel.groupSize;i++){
            if(i==0){
                list.add(0) ;
            }else{
                list.add(1) ;
            }

    }
        mTemplateModel = templateModel;
        notifyDataSetChanged();
    }

   public  class GroupThumbHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


       private  GroupThumbView groupThumbView;
       private  TextView tv_num;
       private RelativeLayout rela_parent;
       private  ImageView iv_select;
       private ImageView iv_show_un_select;

       private GroupThumbHolder(View itemView) {
            super(itemView);
            groupThumbView=itemView.findViewById(R.id.GroupThumbView);
            rela_parent=itemView.findViewById(R.id.rela_parent);
            tv_num=itemView.findViewById(R.id.tv_num);
            rela_parent.setOnClickListener(this);
            iv_show_un_select=itemView.findViewById(R.id.iv_show_un_select);
            iv_select=itemView.findViewById(R.id.iv_select);
        }



        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != mSelectedItem) {
                isAuto=false;
                int lastItem = mSelectedItem;
                mSelectedItem = position;
                modificationDate(lastItem,position);
//                notifyItemChanged(lastItem);
//                notifyItemChanged(mSelectedItem);
                if (mOnItemSelectedListener != null) {
                    mOnItemSelectedListener.onItemSelected(mSelectedItem);
                }
            }
        }
    }



    private void modificationDate(int lastItem,int selectedItem){
        if (list.get(lastItem)!=null&&list.get(lastItem) == 0) {
            list.set(lastItem, 1);//修改对应的元素
        }

        if(list.get(selectedItem)!=null){
            list.set(selectedItem, 0);
        }
    }

    public interface OnItemSelectedListener{
        void onItemSelected(int index);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        mOnItemSelectedListener = onItemSelectedListener;
    }


    public void autoSelect(final int position){
        if (position != mSelectedItem) {
            isAuto=true;
            int lastItem = mSelectedItem;
            mSelectedItem = position;
            modificationDate(lastItem,position);
            notifyItemChanged(lastItem);
            notifyItemChanged(mSelectedItem);
        }

    }




    public int dip2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


}

package com.shixing.sxve.ui.adapter;

import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.ViewGroup;

import com.shixing.sxve.ui.model.TemplateModel;
import com.shixing.sxve.ui.view.GroupThumbView;

public class GroupThumbAdapter extends RecyclerView.Adapter<GroupThumbAdapter.GroupThumbHolder> {
    private TemplateModel mTemplateModel;
    private int mSelectedItem;
    private OnItemSelectedListener mOnItemSelectedListener;

    public GroupThumbAdapter() {
    }

    public GroupThumbAdapter(TemplateModel templateModel) {
        mTemplateModel = templateModel;
    }

    @NonNull
    @Override
    public GroupThumbHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GroupThumbView groupThumbView = new GroupThumbView(parent.getContext());
        groupThumbView.setBackgroundColor(Color.BLACK);
        return new GroupThumbHolder(groupThumbView);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupThumbHolder holder, int position) {
        GroupThumbView thumbView = (GroupThumbView) holder.itemView;
        thumbView.setAssetGroup(mTemplateModel.groups.get(position + 1));
        thumbView.setSelected(position == mSelectedItem);
    }

    @Override
    public int getItemCount() {
        return mTemplateModel == null ? 0 : mTemplateModel.groupSize;
    }

    public void setTemplateModel(TemplateModel templateModel) {
        mTemplateModel = templateModel;
        notifyDataSetChanged();
    }

    class GroupThumbHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public GroupThumbHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != mSelectedItem) {
                int lastItem = mSelectedItem;
                mSelectedItem = position;
                notifyItemChanged(lastItem);
                notifyItemChanged(mSelectedItem);

                if (mOnItemSelectedListener != null) {
                    mOnItemSelectedListener.onItemSelected(mSelectedItem);
                }
            }
        }
    }

    public interface OnItemSelectedListener{
        void onItemSelected(int index);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        mOnItemSelectedListener = onItemSelectedListener;
    }
}

package com.shixing.sxve.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shixing.sxve.R;
import com.shixing.sxve.ui.view.RoundColorView;

import java.util.ArrayList;
import java.util.List;

public class ColorListAdapter extends RecyclerView.Adapter<ColorListAdapter.ColorListHolder> {
    private int mSelected;
    private List<Integer> colors;

    @NonNull
    @Override
    public ColorListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ColorListHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.sxve_item_color, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ColorListHolder holder, int position) {
        RoundColorView itemView = (RoundColorView) holder.itemView;
        itemView.setColor(colors.get(position));
        itemView.setSelected(position == mSelected);
    }

    @Override
    public int getItemCount() {
        return colors == null ? 0 : colors.size();
    }

    public void setData(ArrayList<Integer> colors) {
        this.colors = colors;
        notifyDataSetChanged();
    }

    class ColorListHolder extends RecyclerView.ViewHolder {
        public ColorListHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSelected != getAdapterPosition()) {
                        int last = mSelected;
                        mSelected = getAdapterPosition();
                        notifyItemChanged(last);
                        notifyItemChanged(mSelected);

                        if (mOnColorSelectedListener != null) {
                            mOnColorSelectedListener.onColorSelected(colors.get(mSelected));
                        }
                    }
                }
            });
        }
    }

    public interface OnColorSelectedListener {
        void onColorSelected(int color);
    }

    private OnColorSelectedListener mOnColorSelectedListener;

    public void setOnColorSelectedListener(OnColorSelectedListener onColorSelectedListener) {
        mOnColorSelectedListener = onColorSelectedListener;
    }
}

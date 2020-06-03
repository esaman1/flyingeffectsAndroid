package com.shixing.sxve.ui.adapter;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shixing.sxve.R;
import com.shixing.sxve.ui.model.FontModel;

import java.util.ArrayList;
import java.util.List;

public class FontListAdapter extends RecyclerView.Adapter<FontListAdapter.FontListHolder> {

    private List<FontModel> mFontModels;
    private int mSelectedItem;

    @NonNull
    @Override
    public FontListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new FontListHolder(inflater.inflate(R.layout.sxve_item_font, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FontListHolder holder, int position) {
        FontModel font = mFontModels.get(position);
        Resources resources = holder.itemView.getContext().getResources();
        holder.mFontName.setText(font.name);
        holder.mFontName.setTypeface(font.typeface);

        if (mSelectedItem == position) {
            holder.mIcon.setImageResource(R.drawable.font_xz_icon);
            holder.itemView.setBackgroundColor(resources.getColor(R.color.sxve_window_background));
            holder.mFontName.setTextColor(resources.getColor(R.color.sxve_primary));
        } else {
            holder.mIcon.setImageResource(R.drawable.font_wxz_icon);
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            holder.mFontName.setTextColor(resources.getColor(R.color.sxve_white));
        }
    }

    @Override
    public int getItemCount() {
        return mFontModels == null ? 0 : mFontModels.size();
    }

    public void setData(ArrayList<FontModel> fontModels) {
        mFontModels = fontModels;
        notifyDataSetChanged();
    }

    class FontListHolder extends RecyclerView.ViewHolder {

        private final TextView mFontName;
        private final ImageView mIcon;

        public FontListHolder(View itemView) {
            super(itemView);
            mFontName = itemView.findViewById(R.id.font_name);
            mIcon = itemView.findViewById(R.id.select_icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSelectedItem != getAdapterPosition()) {
                        int lastItem = mSelectedItem;
                        mSelectedItem = getAdapterPosition();
                        notifyItemChanged(lastItem);
                        notifyItemChanged(mSelectedItem);

                        if (mOnFontSelectedListener != null) {
                            mOnFontSelectedListener.onFontSelected(mFontModels.get(mSelectedItem).typeface);
                        }
                    }
                }
            });
        }
    }

    public interface OnFontSelectedListener {
        void onFontSelected(Typeface typeface);
    }

    private OnFontSelectedListener mOnFontSelectedListener;

    public void setOnFontSelectedListener(OnFontSelectedListener onFontSelectedListener) {
        mOnFontSelectedListener = onFontSelectedListener;
    }
}

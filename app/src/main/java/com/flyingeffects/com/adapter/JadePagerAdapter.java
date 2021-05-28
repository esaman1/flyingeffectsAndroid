package com.flyingeffects.com.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flyingeffects.com.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class JadePagerAdapter extends RecyclerView.Adapter<JadePagerAdapter.ColorHolder> {

    private String[] stringArray;

    public JadePagerAdapter(String[] stringArray) {
        this.stringArray = stringArray;
    }

    @NonNull
    @Override
    public ColorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ColorHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_jade_color, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ColorHolder holder, int position) {
//        holder.textView.setText(mDatas.get(position));
    }

    @Override
    public int getItemCount() {
        return stringArray.length;
    }

    public static class ColorHolder extends RecyclerView.ViewHolder {

//        public TextView textView;

        public ColorHolder(@NonNull View itemView) {
            super(itemView);
//            textView = itemView.findViewById(R.id.tv_content);
        }
    }
}
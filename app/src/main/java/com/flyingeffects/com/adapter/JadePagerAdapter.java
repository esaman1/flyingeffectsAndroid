package com.flyingeffects.com.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.R;
import com.flyingeffects.com.enity.FontColor;
import com.flyingeffects.com.ui.view.activity.MainActivity2;
import com.flyingeffects.com.ui.view.fragment.JadeAdjustFragment;
import com.google.android.material.slider.Slider;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class JadePagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int EMPTY = 0;
    public static final int TYPE_JADE_FONT = 1;
    public static final int TYPE_COLOR = 2;
    public static final int TYPE_TYPE_FACE = 3;
    public static final int TYPE_INNER = 4;
    public static final int TYPE_EMOSS = 5;
    public static final int TYPE_3D = 6;

    private String[] stringArray;

    private List<FontColor> innerSimpleColors = new ArrayList<>();

    private JadeAdjustFragment.onAdjustParamsChangeCallBack onAdjustParamsChangeCallBack;
    private FontColor selectedInnerSimpleColor;

    public JadePagerAdapter(String[] stringArray) {
        this.stringArray = stringArray;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case EMPTY:
            default:
                return new Empty(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_jade_empty, parent, false));
            case TYPE_JADE_FONT:
                return new JadeFontHOlder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_jade_font, parent, false));
            case TYPE_COLOR:
                return new ColorHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_jade_color, parent, false));
            case TYPE_TYPE_FACE:
                return new TypeFaceHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_jade_type_face, parent, false));
            case TYPE_INNER:
                return new InnerHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_jade_inner, parent, false));
            case TYPE_EMOSS:
                return new EmossHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_jade_emoss, parent, false));
            case TYPE_3D:
                return new ThreeDHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_jade_ddd, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        holder.textView.setText(mDatas.get(position));
        if (holder instanceof InnerHolder) {
            bindInnerHolder((InnerHolder) holder);
        }
    }

    private void bindInnerHolder(InnerHolder holder) {
        holder.forbidden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAdjustParamsChangeCallBack != null
                        &&selectedInnerSimpleColor!=null) {
                    float radius = holder.inner_radius.getValue();
                    float dx = holder.inner_dx.getValue();
                    float dy = holder.inner_dy.getValue();
                    int color = Color.parseColor(selectedInnerSimpleColor.getColor());
                    onAdjustParamsChangeCallBack.onInnerColorChange(false, radius, dx, dy, color);
                }
            }
        });
        holder.color.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        BaseQuickAdapter<FontColor, BaseViewHolder> adapter = new BaseQuickAdapter<FontColor, BaseViewHolder>(R.layout.view_jede_color_item, innerSimpleColors) {

            @Override
            protected void convert(BaseViewHolder helper, FontColor item) {
                Glide.with(helper.getView(R.id.iv))
                        .load(item.getIcon_image())
                        .centerCrop()
                        .into((ImageView) helper.getView(R.id.iv));
                helper.addOnClickListener(R.id.iv);
            }
        };
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (innerSimpleColors==null) {
                    return;
                }

                selectedInnerSimpleColor = innerSimpleColors.get(position);
                actionInner(holder);
            }
        });

        holder.inner_dx.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float progress, boolean fromUser) {
                actionInner(holder);
            }
        });
        holder.inner_dy.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float progress, boolean fromUser) {
                actionInner(holder);
            }
        });
        holder.inner_radius.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float progress, boolean fromUser) {
                actionInner(holder);
            }
        });
        holder.color.setAdapter(adapter);
    }

    private void actionInner(InnerHolder holder) {
        if (onAdjustParamsChangeCallBack != null
        &&selectedInnerSimpleColor!=null) {
            float radius = holder.inner_radius.getValue();
            float dx = holder.inner_dx.getValue();
            float dy = holder.inner_dy.getValue();
            int color = Color.parseColor(selectedInnerSimpleColor.getColor());
            onAdjustParamsChangeCallBack.onInnerColorChange(true, radius, dx, dy, color);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return stringArray.length;
    }

    public static class JadeFontHOlder extends RecyclerView.ViewHolder {

//        public TextView textView;

        public JadeFontHOlder(@NonNull View itemView) {
            super(itemView);
//            textView = itemView.findViewById(R.id.tv_content);
        }
    }

    public static class ColorHolder extends RecyclerView.ViewHolder {

//        public TextView textView;

        public ColorHolder(@NonNull View itemView) {
            super(itemView);
//            textView = itemView.findViewById(R.id.tv_content);
        }
    }

    public static class TypeFaceHolder extends RecyclerView.ViewHolder {

//        public TextView textView;

        public TypeFaceHolder(@NonNull View itemView) {
            super(itemView);
//            textView = itemView.findViewById(R.id.tv_content);
        }
    }

    public static class InnerHolder extends RecyclerView.ViewHolder {

        public Slider inner_radius;
        public Slider inner_dx;
        public Slider inner_dy;
        public ImageView forbidden;
        public RecyclerView color;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
//            textView = itemView.findViewById(R.id.tv_content);
            inner_radius = itemView.findViewById(R.id.inner_radius);
            inner_dx = itemView.findViewById(R.id.inner_dx);
            inner_dy = itemView.findViewById(R.id.inner_dy);
            forbidden = itemView.findViewById(R.id.forbidden);
            color = itemView.findViewById(R.id.color);
        }
    }

    public static class EmossHolder extends RecyclerView.ViewHolder {

//        public TextView textView;

        public EmossHolder(@NonNull View itemView) {
            super(itemView);
//            textView = itemView.findViewById(R.id.tv_content);
        }
    }

    public static class ThreeDHolder extends RecyclerView.ViewHolder {

//        public TextView textView;

        public ThreeDHolder(@NonNull View itemView) {
            super(itemView);
//            textView = itemView.findViewById(R.id.tv_content);
        }
    }

    public static class Empty extends RecyclerView.ViewHolder {

//        public TextView textView;

        public Empty(@NonNull View itemView) {
            super(itemView);
//            textView = itemView.findViewById(R.id.tv_content);
        }
    }

    public List<FontColor> getInnerSimpleColors() {
        return innerSimpleColors;
    }

    public void setInnerSimpleColors(List<FontColor> innerSimpleColors) {
        this.innerSimpleColors = innerSimpleColors;
    }

    public JadeAdjustFragment.onAdjustParamsChangeCallBack getOnAdjustParamsChangeCallBack() {
        return onAdjustParamsChangeCallBack;
    }

    public void setOnAdjustParamsChangeCallBack(JadeAdjustFragment.onAdjustParamsChangeCallBack onAdjustParamsChangeCallBack) {
        this.onAdjustParamsChangeCallBack = onAdjustParamsChangeCallBack;
    }
}
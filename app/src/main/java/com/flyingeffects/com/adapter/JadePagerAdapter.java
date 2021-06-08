package com.flyingeffects.com.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.enity.FontColor;
import com.flyingeffects.com.enity.FontEnity;
import com.flyingeffects.com.enity.JadeTypeFace;
import com.flyingeffects.com.ui.GridSpacingItemDecoration;
import com.flyingeffects.com.ui.view.fragment.JadeAdjustFragment;
import com.flyingeffects.com.utils.PxUtils;
import com.google.android.material.slider.Slider;
import com.imaginstudio.imagetools.pixellab.GradientMaker;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.imaginstudio.imagetools.pixellab.appDefault.HOR;
import static com.imaginstudio.imagetools.pixellab.appDefault.RAD;
import static com.imaginstudio.imagetools.pixellab.appDefault.VER;

public class JadePagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int EMPTY = 0;
    public static final int TYPE_JADE_FONT = 1;
    public static final int TYPE_COLOR = 2;
    public static final int TYPE_TYPE_FACE = 3;
    public static final int TYPE_INNER = 4;
    public static final int TYPE_EMOSS = 5;
    public static final int TYPE_3D = 6;
    private final Context content;

    private int currentGradientType = HOR;

    private String[] stringArray;

    private JadeAdjustFragment.onAdjustParamsChangeCallBack onAdjustParamsChangeCallBack;

    private List<FontColor> simpleColors = new ArrayList<>();
    private List<FontColor> gradientColors = new ArrayList<>();

    private List<JadeTypeFace> jadeTypeFaces = new ArrayList<>();
    private List<JadeTypeFace> localJadeTypeFaces = new ArrayList<>();


    private FontColor selectedInnerSimpleColor;
    private FontColor selected3DSimpleColor;
    private FontColor selectedOverlaySimpleColor;
    private FontColor selectedGradientColor;
    private JadeTypeFace selectedJadeTypeFace;


    private List<FontEnity> fontEnityList = new ArrayList<>();

    private static final String TAG = "JadePagerAdapter";

    public JadePagerAdapter(String[] stringArray, Context content) {
        this.content = content;
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
        if (holder instanceof InnerHolder) {
            bindInnerHolder((InnerHolder) holder);
        } else if (holder instanceof EmossHolder) {
            bindEmossHolder((EmossHolder) holder);
        } else if (holder instanceof ThreeDHolder) {
            bind3DHolder((ThreeDHolder) holder);
        } else if (holder instanceof ColorHolder) {
            bindColorHolder((ColorHolder) holder);
        } else if (holder instanceof JadeFontHOlder) {
            bindJadeFontHolder(((JadeFontHOlder) holder));
        } else if (holder instanceof TypeFaceHolder) {
            bindTypeFaceHolder((TypeFaceHolder) holder);
        }
    }

    private void bindTypeFaceHolder(TypeFaceHolder holder) {
        holder.rv.setLayoutManager(new GridLayoutManager(holder.itemView.getContext(), 4));
        holder.rv.addItemDecoration(new GridSpacingItemDecoration(4, PxUtils.dp2px(BaseApplication.getInstance(), 8), false, 0));
        BaseQuickAdapter<FontEnity, BaseViewHolder> adapter = new BaseQuickAdapter<FontEnity, BaseViewHolder>(R.layout.view_jade_other_type_face_item, fontEnityList) {
            @Override
            protected void convert(BaseViewHolder helper, FontEnity item) {
                Glide.with(helper.getView(R.id.iv))
                        .load(item.getIcon_image())
                        .into((ImageView) helper.getView(R.id.iv));
                if (item.isSelected()) {
                    helper.getView(R.id.frame).setVisibility(View.VISIBLE);
                } else {
                    helper.getView(R.id.frame).setVisibility(View.GONE);
                }
                helper.addOnClickListener(R.id.iv);
            }
        };
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                for (FontEnity fontEnity : fontEnityList) {
                    fontEnity.setSelected(false);
                }
                if (fontEnityList != null) {
                    FontEnity clickItem = fontEnityList.get(position);
                    clickItem.setSelected(true);
//                if (simpleColors == null) {
//                    return;
//                }
//
//                selectedInnerSimpleColor = simpleColors.get(position);
//                actionInner(holder);
                    if (onTypeFaceClick != null) {
                        onTypeFaceClick.onClick(clickItem);
                    }
                    adapter.notifyDataSetChanged();
                }

            }
        });
        holder.rv.setAdapter(adapter);
    }

    private void bindJadeFontHolder(JadeFontHOlder holder) {
        holder.forbidden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedOverlaySimpleColor = null;
                if (onAdjustParamsChangeCallBack != null) {
                    int color = Color.parseColor("#ffffff");
//                    onAdjustParamsChangeCallBack.onTextColorChange(color, 0, 0, true);
                }
            }
        });

        ((RadioButton) holder.group.getChildAt(0)).setText("默认样式");
        ((RadioButton) holder.group.getChildAt(1)).setText("我的样式");
        holder.group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.choose1:
                        holder.default_rv.setVisibility(View.VISIBLE);
                        holder.my_rv.setVisibility(View.GONE);
                        break;
                    case R.id.choose2:
                        holder.default_rv.setVisibility(View.GONE);
                        holder.my_rv.setVisibility(View.VISIBLE);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + checkedId);
                }
            }
        });


        holder.default_rv.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        BaseQuickAdapter<JadeTypeFace, BaseViewHolder> adapter = new BaseQuickAdapter<JadeTypeFace, BaseViewHolder>(R.layout.view_jade_jade_type_face_item, jadeTypeFaces) {

            @Override
            protected void convert(BaseViewHolder helper, JadeTypeFace item) {
                Glide.with(helper.getView(R.id.iv))
                        .load(item.getIcon_image())
                        .centerCrop()
                        .into((ImageView) helper.getView(R.id.iv));
                if (item.isSelected()) {
                    helper.getView(R.id.frame).setVisibility(View.VISIBLE);
                } else {
                    helper.getView(R.id.frame).setVisibility(View.GONE);
                }
                helper.addOnClickListener(R.id.root);
                helper.addOnClickListener(R.id.iv);
            }
        };
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Log.d(TAG, "onItemChildClick() called with: adapter = [" + adapter + "], view = [" + view + "], position = [" + position + "]");
                if (jadeTypeFaces != null) {
                    selectedJadeTypeFace = jadeTypeFaces.get(position);
                    for (JadeTypeFace fontEnity : jadeTypeFaces) {
                        fontEnity.setSelected(false);
                    }
                    selectedJadeTypeFace = jadeTypeFaces.get(position);
                    selectedJadeTypeFace.setSelected(true);
                    adapter.notifyDataSetChanged();
                    if (onAdjustParamsChangeCallBack != null) {
                        onAdjustParamsChangeCallBack.onJadeTypeFaceChange(selectedJadeTypeFace);
                    }
                }
            }
        });
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Log.d(TAG, "onItemClick() called with: adapter = [" + adapter + "], view = [" + view + "], position = [" + position + "]");
            }
        });
        holder.default_rv.setAdapter(adapter);


//        holder.my_rv.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
//        BaseQuickAdapter<JadeTypeFace, BaseViewHolder> adapterMy = new BaseQuickAdapter<JadeTypeFace, BaseViewHolder>(R.layout.view_jade_jade_type_face_item, localJadeTypeFaces) {
//
//            @Override
//            protected void convert(BaseViewHolder helper, JadeTypeFace item) {
//                Glide.with(helper.getView(R.id.iv))
//                        .load(item.getIcon_image())
//                        .centerCrop()
//                        .into((ImageView) helper.getView(R.id.iv));
//                helper.addOnClickListener(R.id.iv);
//            }
//        };
//        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
//            @Override
//            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
////                selectedJadeTypeFace = localJadeTypeFaces.get(position);
////                if (onAdjustParamsChangeCallBack != null) {
////                    int color = Color.parseColor(selectedOverlaySimpleColor.getColor());
////                    onAdjustParamsChangeCallBack.onTextColorChange(color, 0, 0, true);
////                }
//            }
//        });
//        holder.my_rv.setAdapter(adapterMy);

    }

    private void bindColorHolder(ColorHolder holder) {
        holder.forbidden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedOverlaySimpleColor = null;
                if (onAdjustParamsChangeCallBack != null) {
                    int color = Color.parseColor("#ffffff");
                    onAdjustParamsChangeCallBack.onTextColorChange(color, 0, 0, true);
                }
            }
        });

        ((RadioButton) holder.group.getChildAt(0)).setText("颜色");
        ((RadioButton) holder.group.getChildAt(1)).setText("渐变");
        holder.group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.choose1:
                        holder.simple_form.setVisibility(View.VISIBLE);
                        holder.gradual_form.setVisibility(View.GONE);
                        break;
                    case R.id.choose2:
                        holder.simple_form.setVisibility(View.GONE);
                        holder.gradual_form.setVisibility(View.VISIBLE);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + checkedId);
                }
            }
        });

        holder.radio_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (selectedGradientColor != null) {
                    switch (checkedId) {
                        case R.id.type1:
                            currentGradientType = HOR;
                            @NotNull int[] gradientColor = getGradientColor(selectedGradientColor);
                            GradientMaker.GradientFill gradientFill = new GradientMaker.GradientFill(HOR, gradientColor[0], gradientColor[1]);
                            if (onAdjustParamsChangeCallBack != null) {
                                onAdjustParamsChangeCallBack.onTextColorChange(gradientFill);
                            }
                            break;
                        case R.id.type2:
                            currentGradientType = RAD;
                            @NotNull int[] gradientColor1 = getGradientColor(selectedGradientColor);
                            GradientMaker.GradientFill gradientFill1 = new GradientMaker.GradientFill(RAD, gradientColor1[0], gradientColor1[1]);
                            if (onAdjustParamsChangeCallBack != null) {
                                onAdjustParamsChangeCallBack.onTextColorChange(gradientFill1);
                            }
                            break;
                        case R.id.type3:
                            currentGradientType = VER;
                            @NotNull int[] gradientColor2 = getGradientColor(selectedGradientColor);
                            GradientMaker.GradientFill gradientFill2 = new GradientMaker.GradientFill(VER, gradientColor2[0], gradientColor2[1]);
                            if (onAdjustParamsChangeCallBack != null) {
                                onAdjustParamsChangeCallBack.onTextColorChange(gradientFill2);
                            }
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + checkedId);
                    }
                }

            }
        });

        holder.color.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        BaseQuickAdapter<FontColor, BaseViewHolder> adapter = new BaseQuickAdapter<FontColor, BaseViewHolder>(R.layout.view_jede_color_item, simpleColors) {

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
                if (simpleColors != null) {
                    selectedOverlaySimpleColor = simpleColors.get(position);
                    if (onAdjustParamsChangeCallBack != null) {
                        int color = Color.parseColor(selectedOverlaySimpleColor.getColor());
                        onAdjustParamsChangeCallBack.onTextColorChange(color, 0, 0, true);
                    }
                }

            }
        });
        holder.color.setAdapter(adapter);

        holder.gradualColorRv.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        BaseQuickAdapter<FontColor, BaseViewHolder> gradualColorRvAdapter = new BaseQuickAdapter<FontColor, BaseViewHolder>(R.layout.view_jede_color_item, gradientColors) {

            @Override
            protected void convert(BaseViewHolder helper, FontColor item) {

                if (!TextUtils.isEmpty(item.getColor())) {
                    int[] colors = getGradientColor(item);
                    GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
                    drawable.setCornerRadius(2);
                    drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
                    ((ImageView) helper.getView(R.id.iv)).setBackground(drawable);
                }
                helper.addOnClickListener(R.id.iv);
            }
        };
        gradualColorRvAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (gradientColors != null) {
                    selectedGradientColor = gradientColors.get(position);
                    @NotNull int[] gradientColor = getGradientColor(selectedGradientColor);
                    GradientMaker.GradientFill gradientFill = new GradientMaker.GradientFill(currentGradientType, gradientColor[0], gradientColor[1]);
                    if (onAdjustParamsChangeCallBack != null) {
                        onAdjustParamsChangeCallBack.onTextColorChange(gradientFill);
                    }
                }
            }
        });
        holder.gradualColorRv.setAdapter(gradualColorRvAdapter);


    }

    @NotNull
    private int[] getGradientColor(FontColor item) {
        int[] colors = new int[4];
        String[] split = item.getColor().split(",");
        for (int i = 0; i < split.length; i++) {
            colors[i] = Color.parseColor(split[i]);
        }
        return colors;
    }

    private void bind3DHolder(ThreeDHolder holder) {
        holder.forbidden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected3DSimpleColor = null;
                action3DAction(holder);
            }
        });
        holder.color.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        BaseQuickAdapter<FontColor, BaseViewHolder> adapter = new BaseQuickAdapter<FontColor, BaseViewHolder>(R.layout.view_jede_color_item, simpleColors) {

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
                if (simpleColors != null) {

                    selected3DSimpleColor = simpleColors.get(position);
                    action3DAction(holder);
                }
            }
        });
        holder.color.setAdapter(adapter);

        holder.ddd_arc.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float progress, boolean fromUser) {
                action3DAction(holder);
            }
        });
        holder.ddd_deep.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float progress, boolean fromUser) {
                action3DAction(holder);
            }
        });
    }

    private void action3DAction(ThreeDHolder holder) {
        if (onAdjustParamsChangeCallBack != null) {
//                    obliqueAngle = (int) Math.toDegrees(Math.atan2((double) bundle.getInt("shapeThreeDOffsetX", 1), (double) bundle.getInt("shapeThreeDOffsetY", 1)));
            int ddd_arc = (int) holder.ddd_arc.getValue();
            int ddd_deep = (int) holder.ddd_deep.getValue();
            int color;
            if (selected3DSimpleColor == null) {
                color = Color.parseColor("#ffffff");
            } else {
                color = Color.parseColor(selected3DSimpleColor.getColor());
            }
            onAdjustParamsChangeCallBack.on3Dchange(ddd_deep, 0, 1, true, ddd_arc, color);
        }
    }

    private void bindEmossHolder(EmossHolder holder) {
        holder.emoss_arc.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float progress, boolean fromUser) {
                actionEmoss(holder);
            }
        });
        holder.emoss_qiangdu.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float progress, boolean fromUser) {
                actionEmoss(holder);
            }
        });
        holder.emoss_xieijiao.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float progress, boolean fromUser) {
                actionEmoss(holder);
            }
        });
    }

    private void bindInnerHolder(InnerHolder holder) {
        holder.forbidden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAdjustParamsChangeCallBack != null
                        && selectedInnerSimpleColor != null) {
                    float radius = holder.inner_radius.getValue();
                    float dx = holder.inner_dx.getValue();
                    float dy = holder.inner_dy.getValue();
                    int color = Color.parseColor(selectedInnerSimpleColor.getColor());
                    onAdjustParamsChangeCallBack.onInnerColorChange(false, radius, dx, dy, color);
                }
            }
        });
        holder.color.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        BaseQuickAdapter<FontColor, BaseViewHolder> adapter = new BaseQuickAdapter<FontColor, BaseViewHolder>(R.layout.view_jede_color_item, simpleColors) {

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
                if (simpleColors == null) {
                    return;
                }

                selectedInnerSimpleColor = simpleColors.get(position);
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
                && selectedInnerSimpleColor != null) {
            float radius = holder.inner_radius.getValue();
            float dx = holder.inner_dx.getValue();
            float dy = holder.inner_dy.getValue();
            int color = Color.parseColor(selectedInnerSimpleColor.getColor());
            onAdjustParamsChangeCallBack.onInnerColorChange(true, radius, dx, dy, color);
        }
    }

    private void actionEmoss(EmossHolder holder) {
        if (onAdjustParamsChangeCallBack != null) {
            int emoss_arc = (int) holder.emoss_arc.getValue();
            int emoss_qiangdu = (int) holder.emoss_qiangdu.getValue();
            int emoss_xieijiao = (int) holder.emoss_xieijiao.getValue();
            onAdjustParamsChangeCallBack.onEmossChange(true, emoss_arc, emoss_qiangdu, 100, 100, emoss_xieijiao);
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
        public ImageView forbidden;
        public RadioGroup group;
        public RecyclerView default_rv;
        public RecyclerView my_rv;

        public JadeFontHOlder(@NonNull View itemView) {
            super(itemView);
            forbidden = itemView.findViewById(R.id.forbidden);
            group = itemView.findViewById(R.id.group);
            default_rv = itemView.findViewById(R.id.default_rv);
            my_rv = itemView.findViewById(R.id.my_rv);
        }
    }

    public static class ColorHolder extends RecyclerView.ViewHolder {
        public ImageView forbidden;
        public RadioGroup group;
        public RadioGroup radio_type;
        public LinearLayout simple_form;
        public LinearLayout gradual_form;
        public RecyclerView color;
        public RecyclerView gradualColorRv;

        public ColorHolder(@NonNull View itemView) {
            super(itemView);
            forbidden = itemView.findViewById(R.id.forbidden);
            group = itemView.findViewById(R.id.group);
            radio_type = itemView.findViewById(R.id.radio_type);
            simple_form = itemView.findViewById(R.id.simple_form);
            gradual_form = itemView.findViewById(R.id.gradual_form);
            gradualColorRv = itemView.findViewById(R.id.gradual_rv);
            color = itemView.findViewById(R.id.color);
        }
    }

    public static class TypeFaceHolder extends RecyclerView.ViewHolder {

        RecyclerView rv;
//        ImageView iv;
//        ImageView download;
//        ProgressBar progress;
//        FrameLayout frame;

        public TypeFaceHolder(@NonNull View itemView) {
            super(itemView);
            rv = itemView.findViewById(R.id.rv);
//            iv = itemView.findViewById(R.id.iv);
//            download = itemView.findViewById(R.id.download);
//            progress = itemView.findViewById(R.id.progress);
//            frame = itemView.findViewById(R.id.frame);
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
        public Slider emoss_arc;
        public Slider emoss_qiangdu;
        public Slider emoss_xieijiao;

        public EmossHolder(@NonNull View itemView) {
            super(itemView);
            emoss_arc = itemView.findViewById(R.id.emoss_arc);
            emoss_qiangdu = itemView.findViewById(R.id.emoss_qiangdu);
            emoss_xieijiao = itemView.findViewById(R.id.emoss_xieijiao);
        }
    }

    public static class ThreeDHolder extends RecyclerView.ViewHolder {

        public ImageView forbidden;
        public RecyclerView color;
        public Slider ddd_arc;
        public Slider ddd_deep;

        public ThreeDHolder(@NonNull View itemView) {
            super(itemView);
            forbidden = itemView.findViewById(R.id.forbidden);
            color = itemView.findViewById(R.id.color);
            ddd_arc = itemView.findViewById(R.id.ddd_arc);
            ddd_deep = itemView.findViewById(R.id.ddd_deep);
        }
    }

    public static class Empty extends RecyclerView.ViewHolder {

//        public TextView textView;

        public Empty(@NonNull View itemView) {
            super(itemView);
//            textView = itemView.findViewById(R.id.tv_content);
        }
    }

    public List<FontColor> getSimpleColors() {
        return simpleColors;
    }

    public void setSimpleColors(List<FontColor> simpleColors) {
        this.simpleColors = simpleColors;
    }

    public JadeAdjustFragment.onAdjustParamsChangeCallBack getOnAdjustParamsChangeCallBack() {
        return onAdjustParamsChangeCallBack;
    }

    public void setOnAdjustParamsChangeCallBack(JadeAdjustFragment.onAdjustParamsChangeCallBack onAdjustParamsChangeCallBack) {
        this.onAdjustParamsChangeCallBack = onAdjustParamsChangeCallBack;
    }

    public List<FontColor> getGradientColors() {
        return gradientColors;
    }

    public void setGradientColors(List<FontColor> gradientColors) {
        this.gradientColors = gradientColors;
    }

    public List<FontEnity> getFontEnityList() {
        return fontEnityList;
    }

    public void setFontEnityList(List<FontEnity> fontEnityList) {
        this.fontEnityList = fontEnityList;
    }

    private onTypeFaceClick onTypeFaceClick;

    public JadePagerAdapter.onTypeFaceClick getOnTypeFaceClick() {
        return onTypeFaceClick;
    }

    public void setOnTypeFaceClick(JadePagerAdapter.onTypeFaceClick onTypeFaceClick) {
        this.onTypeFaceClick = onTypeFaceClick;
    }

    public interface onTypeFaceClick {
        void onClick(FontEnity fontEnity);
    }

    public List<JadeTypeFace> getJadeTypeFaces() {
        return jadeTypeFaces;
    }

    public void setJadeTypeFaces(List<JadeTypeFace> jadeTypeFaces) {
        this.jadeTypeFaces = jadeTypeFaces;
    }

    public List<JadeTypeFace> getLocalJadeTypeFaces() {
        return localJadeTypeFaces;
    }

    public void setLocalJadeTypeFaces(List<JadeTypeFace> localJadeTypeFaces) {
        this.localJadeTypeFaces = localJadeTypeFaces;
    }
}
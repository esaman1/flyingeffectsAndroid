package com.imaginstudio.imagetools.pixellab;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.imaginstudio.imagetools.R;

import info.hoang8f.android.segmented.SegmentedGroup;
import java.util.ArrayList;
import java.util.Iterator;

public class unifiedColor_selector extends LinearLayout implements View.OnClickListener {
    public unifiedColor_selector(Context context) {
        super(context);
    }

    @Override
    public void onClick(View v) {

    }
//    private static final String LAST_USED_COLOR = "last_colors";
//    private static final String LAST_USED_GRADIENT = "last_gradients";
//    public static int TYPE_COLOR = 1;
//    public static int TYPE_GRADIENT = 2;
//    boolean colorsAdded = false;
//    int currColor;
//    GradientMaker.GradientFill currGradient;
//    int fillType;
//    boolean gradientsAdded = false;
//    private boolean isAutomatic = false;
//    onColorChangeListener mColorListener;
//    private boolean showAutomatic = false;
//
//    public interface onColorChangeListener {
//        void colorChanged(int i, int i2, GradientMaker.GradientFill gradientFill);
//    }
//
//    public void setOnColorChangeListener(onColorChangeListener listener) {
//        this.mColorListener = listener;
//    }
//
//    public void onClick(View view) {
//        colorButton clickedColor = (colorButton) view;
//        this.fillType = clickedColor.getType();
//        if (this.fillType == TYPE_COLOR) {
//            this.currColor = clickedColor.getDisplayColor();
//        } else if (this.fillType == TYPE_GRADIENT) {
//            this.currGradient = clickedColor.getDisplayGradient() != null ? clickedColor.getDisplayGradient().copy() : null;
//        }
//        reportColorChange();
//    }
//
//    public boolean isAutomatic() {
//        return this.isAutomatic;
//    }
//
//    public void setIsAutomatic(boolean isAutomatic2) {
//        this.isAutomatic = isAutomatic2;
////        RadioButton autoR = (RadioButton) findViewById(R.id.autoRadioButton);
////        if (isAutomatic2) {
////            autoR.setChecked(true);
////        }
//    }
//
//    public void setShowAutomatic(boolean show, boolean auto) {
//        this.showAutomatic = show;
//        RadioButton autoR = (RadioButton) findViewById(R.id.autoRadioButton);
//        if (auto) {
//            autoR.setChecked(true);
//        }
//        autoR.setVisibility(0);
//        this.isAutomatic = auto;
//    }
//
//    public unifiedColor_selector(Context context, boolean hasBoth, int type, int initColor, GradientMaker.GradientFill initGradient) {
//        super(context);
//        inflate(getContext(), R.layout.color_selector, this);
//        this.fillType = type;
//        this.currColor = initColor;
//        this.currGradient = initGradient != null ? initGradient.copy() : null;
//        SegmentedGroup fillTypeChoice = (SegmentedGroup) findViewById(R.id.segmented2);
//        fillTypeChoice.setTintColor(Color.parseColor("#e91e63"), -1);
//        fillTypeChoice.setLayerType(1, null);
//        final LayoutParams lpa = new LayoutParams(dpToPixels(40), dpToPixels(40));
//        lpa.gravity = 17;
//        lpa.rightMargin = dpToPixels(10);
//        lpa.leftMargin = dpToPixels(10);
//        final LinearLayout colorList = (LinearLayout) findViewById(R.id.default_color_list);
//        final LinearLayout colorListLast = (LinearLayout) findViewById(R.id.last_color_list);
//        final LinearLayout gradientsList = (LinearLayout) findViewById(R.id.default_gradient_list);
//        final LinearLayout gradientsListLast = (LinearLayout) findViewById(R.id.last_gradient_list);
//        final LinearLayout colorListHolder = (LinearLayout) findViewById(R.id.colors_panel);
//        final LinearLayout gradientsListHolder = (LinearLayout) findViewById(R.id.gradients_panel);
//        RadioButton radioButton = (RadioButton) findViewById(R.id.colorRadioButton);
//        RadioButton gradientR = (RadioButton) findViewById(R.id.gradientRadioButton);
//        fillTypeChoice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            /* class com.imaginstudio.imagetools.pixellab.controls.widgets.unifiedColor_selector.AnonymousClass1 */
//
//            public void onCheckedChanged(RadioGroup radioGroup, int i) {
//                switch (i) {
//                    case R.id.autoRadioButton /*{ENCODED_INT: 2131230804}*/:
//                        colorListHolder.setVisibility(8);
//                        gradientsListHolder.setVisibility(8);
//                        unifiedColor_selector.this.setIsAutomatic(true);
//                        unifiedColor_selector.this.reportColorChange();
//                        return;
//                    case R.id.colorRadioButton /*{ENCODED_INT: 2131230853}*/:
//                        gradientsListHolder.setVisibility(8);
//                        colorListHolder.setVisibility(0);
//                        if (!unifiedColor_selector.this.colorsAdded) {
//                            unifiedColor_selector.this.insertDefaultColors(colorList, lpa);
//                        }
//                        unifiedColor_selector.this.insertLastUsedColors(colorListLast, lpa);
//                        unifiedColor_selector.this.fillType = unifiedColor_selector.TYPE_COLOR;
//                        unifiedColor_selector.this.setIsAutomatic(false);
//                        return;
//                    case R.id.gradientRadioButton /*{ENCODED_INT: 2131230933}*/:
//                        colorListHolder.setVisibility(8);
//                        gradientsListHolder.setVisibility(0);
//                        if (!unifiedColor_selector.this.gradientsAdded) {
//                            unifiedColor_selector.this.insertDefaultGradients(gradientsList, lpa);
//                        }
//                        unifiedColor_selector.this.insertLastUsedGradients(gradientsListLast, lpa);
//                        unifiedColor_selector.this.fillType = unifiedColor_selector.TYPE_GRADIENT;
//                        unifiedColor_selector.this.setIsAutomatic(false);
//                        return;
//                    default:
//                        return;
//                }
//            }
//        });
//        ImageButton addColor = (ImageButton) findViewById(R.id.custom_color);
//        ImageButton addGradient = (ImageButton) findViewById(R.id.custom_gradient);
//        addColor.setColorFilter(appDefault.blueHighlightColor);
//        addGradient.setColorFilter(appDefault.blueHighlightColor);
//        addColor.setOnClickListener(new OnClickListener() {
//            /* class com.imaginstudio.imagetools.pixellab.controls.widgets.unifiedColor_selector.AnonymousClass2 */
//
//            public void onClick(View view) {
//                new colorPickerDialog(unifiedColor_selector.this.getContext(), unifiedColor_selector.this.currColor, new colorPickerDialog.OnColorSelectedListener() {
//                    /* class com.imaginstudio.imagetools.pixellab.controls.widgets.unifiedColor_selector.AnonymousClass2.AnonymousClass1 */
//
//                    @Override // com.imaginstudio.imagetools.pixellab.colorPickerDialog.OnColorSelectedListener
//                    public void onColorSelected(int color) {
//                        unifiedColor_selector.this.fillType = unifiedColor_selector.TYPE_COLOR;
//                        unifiedColor_selector.this.currColor = color;
//                        unifiedColor_selector.this.reportColorChange();
//                        TinyDB tinydb = new TinyDB(unifiedColor_selector.this.getContext());
//                        ArrayList<Integer> last_used = tinydb.getListInt(unifiedColor_selector.LAST_USED_COLOR);
//                        last_used.remove(Integer.valueOf(color));
//                        last_used.add(0, Integer.valueOf(color));
//                        while (last_used.size() > 5) {
//                            last_used.remove(last_used.size() - 1);
//                        }
//                        tinydb.putListInt(unifiedColor_selector.LAST_USED_COLOR, last_used);
//                        unifiedColor_selector.this.insertLastUsedColors(colorListLast, lpa);
//                    }
//                }).show();
//            }
//        });
//        addGradient.setOnClickListener(new OnClickListener() {
//            /* class com.imaginstudio.imagetools.pixellab.controls.widgets.unifiedColor_selector.AnonymousClass3 */
//
//            public void onClick(View view) {
//                new GradientMaker(unifiedColor_selector.this.getContext(), unifiedColor_selector.this.currGradient, new GradientMaker.OnGradientSelectedListener() {
//                    /* class com.imaginstudio.imagetools.pixellab.controls.widgets.unifiedColor_selector.AnonymousClass3.AnonymousClass1 */
//
//                    @Override // com.imaginstudio.imagetools.pixellab.GradientMaker.OnGradientSelectedListener
//                    public void onGradientSelected(GradientMaker.GradientFill gradient) {
//                        unifiedColor_selector.this.fillType = unifiedColor_selector.TYPE_GRADIENT;
//                        unifiedColor_selector.this.currGradient = gradient != null ? gradient.copy() : null;
//                        unifiedColor_selector.this.reportColorChange();
//                        TinyDB tinydb = new TinyDB(unifiedColor_selector.this.getContext());
//                        ArrayList<String> last_used = tinydb.getList(unifiedColor_selector.LAST_USED_GRADIENT);
//                        String GradientString = gradient.convertToStringV2();
//                        last_used.remove(GradientString);
//                        last_used.add(0, GradientString);
//                        while (last_used.size() > 5) {
//                            last_used.remove(last_used.size() - 1);
//                        }
//                        tinydb.putList(unifiedColor_selector.LAST_USED_GRADIENT, last_used);
//                        unifiedColor_selector.this.insertLastUsedGradients(gradientsListLast, lpa);
//                    }
//                }).show();
//            }
//        });
//        if (!hasBoth || type == TYPE_COLOR) {
//            gradientsListHolder.setVisibility(8);
//            insertDefaultColors(colorList, lpa);
//            insertLastUsedColors(colorListLast, lpa);
//        } else {
//            colorListHolder.setVisibility(8);
//            insertDefaultGradients(gradientsList, lpa);
//            insertLastUsedGradients(gradientsListLast, lpa);
//            gradientR.setChecked(true);
//        }
//        if (!hasBoth) {
//            ((LinearLayout) findViewById(R.id.segment_holder)).setVisibility(8);
//        }
//    }
//
//    /* access modifiers changed from: package-private */
//    public int dpToPixels(int dp) {
//        return (int) TypedValue.applyDimension(1, (float) dp, getResources().getDisplayMetrics());
//    }
//
//    public void setParamsQuietly(int type, int color, GradientMaker.GradientFill gradient) {
//        this.currColor = color;
//        this.currGradient = gradient != null ? gradient.copy() : null;
//        if (type == TYPE_COLOR) {
//            ((RadioButton) findViewById(R.id.colorRadioButton)).setChecked(true);
//        } else {
//            ((RadioButton) findViewById(R.id.gradientRadioButton)).setChecked(true);
//        }
//    }
//
//    public int getFillType() {
//        return this.fillType;
//    }
//
//    public int getCurrColor() {
//        return this.currColor;
//    }
//
//    public GradientMaker.GradientFill getCurrGradient() {
//        return this.currGradient;
//    }
//
//    public void insertDefaultColors(LinearLayout insertTo, LayoutParams lpa) {
//        Iterator<String> it = appDefault.defaultColors.iterator();
//        while (it.hasNext()) {
//            int color = -1;
//            try {
//                color = Color.parseColor(it.next());
//            } catch (IllegalArgumentException e) {
//            }
//            colorButton insertButton = new colorButton(getContext(), 1, color, null, false);
//            insertButton.setOnClickListener(this);
//            insertTo.addView(insertButton, lpa);
//        }
//        this.colorsAdded = true;
//    }
//
//    public void insertLastUsedColors(LinearLayout insertTo, LayoutParams lpa) {
//        insertTo.removeAllViews();
//        ArrayList<Integer> last_used = new TinyDB(getContext()).getListInt(LAST_USED_COLOR);
//        if (!last_used.isEmpty()) {
//            Iterator<Integer> it = last_used.iterator();
//            while (it.hasNext()) {
//                colorButton insertButton = new colorButton(getContext(), 1, it.next().intValue(), null, false);
//                insertButton.setOnClickListener(this);
//                insertTo.addView(insertButton, lpa);
//            }
//        }
//    }
//
//    public void insertLastUsedGradients(LinearLayout insertTo, LayoutParams lpa) {
//        insertTo.removeAllViews();
//        ArrayList<String> last_used = new TinyDB(getContext()).getList(LAST_USED_GRADIENT);
//        if (!last_used.isEmpty()) {
//            Iterator<String> it = last_used.iterator();
//            while (it.hasNext()) {
//                colorButton insertButton = new colorButton(getContext(), 2, 0, new GradientMaker.GradientFill(it.next()), false);
//                insertButton.setOnClickListener(this);
//                insertTo.addView(insertButton, lpa);
//            }
//        }
//    }
//
//    public void reportColorChange() {
//        if (this.mColorListener != null) {
//            this.mColorListener.colorChanged(this.fillType, this.currColor, this.currGradient);
//        }
//    }
//
//    public void insertDefaultGradients(LinearLayout insertTo, LayoutParams lpa) {
//        Iterator<GradientMaker.GradientFill> it = appDefault.defaultGradients.iterator();
//        while (it.hasNext()) {
//            GradientMaker.GradientFill gradient = it.next();
//            colorButton insertButton = new colorButton(getContext(), 2, 0, gradient != null ? gradient.copy() : null, false);
//            insertButton.setOnClickListener(this);
//            insertTo.addView(insertButton, lpa);
//        }
//        this.gradientsAdded = true;
//    }
}

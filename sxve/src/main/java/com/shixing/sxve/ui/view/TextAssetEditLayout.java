package com.shixing.sxve.ui.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.shixing.sxve.R;
import com.shixing.sxve.ui.adapter.ColorListAdapter;
import com.shixing.sxve.ui.adapter.FontListAdapter;
import com.shixing.sxve.ui.model.FontModel;
import com.shixing.sxve.ui.model.TextUiModel;
import com.shixing.sxve.ui.util.GroupThumbDecoration;

import java.util.ArrayList;

public class TextAssetEditLayout extends FrameLayout implements RadioGroup.OnCheckedChangeListener {
    private static final String TAG = "TestAssetEditLayout";
    private View editLayout;
    private View fontList;
    private View colorLayout;
    private Activity mActivity;
    private CounterEditText mEditText;
    private InputMethodManager mInputMethodManager;
    private TextUiModel mModel;
    private RadioGroup mRadioGroup;

    public TextAssetEditLayout(Context context) {
        this(context, null);
    }

    public TextAssetEditLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextAssetEditLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mActivity = ((Activity) context);

        LayoutInflater.from(context).inflate(R.layout.sxve_text_asset_edit, this, true);

        initRadioGroup();

        initEdit();

        initFontList();

        initColorList();

        mInputMethodManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

        mActivity.findViewById(android.R.id.content).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect outRect = new Rect();
                mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
                int invisibleHeight = getScreenHeight() - outRect.bottom;
                if (invisibleHeight > 200) {
                    WindowManager.LayoutParams attributes = mActivity.getWindow().getAttributes();
                    attributes.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING;
                    mActivity.getWindow().setAttributes(attributes);

                    View placeHolder = findViewById(R.id.placeholder);
                    ViewGroup.LayoutParams layoutParams = placeHolder.getLayoutParams();
                    layoutParams.height = invisibleHeight;
                    placeHolder.setLayoutParams(layoutParams);

                    mActivity.findViewById(android.R.id.content).getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });

        findViewById(R.id.confirm).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });

        findViewById(R.id.edit_panel).setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        hide();
        return true;
    }

    public void hide() {
        mRadioGroup.check(R.id.rb_text_content);
        hideInputMethod();
        setVisibility(GONE);
    }

    private void initEdit() {
        mEditText = findViewById(R.id.edit_text);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mModel!=null){
                    mModel.setText(s.toString());
                }

            }
        });
    }

    private void initRadioGroup() {
        mRadioGroup = this.findViewById(R.id.rg_text_edit);
        mRadioGroup.setOnCheckedChangeListener(this);
        editLayout = findViewById(R.id.edit_layout);
        fontList = findViewById(R.id.font_list);
        colorLayout = findViewById(R.id.style_layout);
    }

    private void initFontList() {
        RecyclerView fontList = findViewById(R.id.font_list);
        FontListAdapter fontListAdapter = new FontListAdapter();
        fontList.setLayoutManager(new LinearLayoutManager(getContext()));
        fontList.setAdapter(fontListAdapter);

        ArrayList<FontModel> fontModels = new ArrayList<>();
        fontModels.add(new FontModel("系统字体", Typeface.defaultFromStyle(Typeface.NORMAL)));
        fontModels.add(new FontModel("加粗", Typeface.defaultFromStyle(Typeface.BOLD)));
        fontModels.add(new FontModel("斜体", Typeface.defaultFromStyle(Typeface.ITALIC)));
        fontModels.add(new FontModel("斜体加粗", Typeface.defaultFromStyle(Typeface.BOLD_ITALIC)));
        fontListAdapter.setData(fontModels);

        fontListAdapter.setOnFontSelectedListener(new FontListAdapter.OnFontSelectedListener() {
            @Override
            public void onFontSelected(Typeface typeface) {
                mModel.setTypeface(typeface);
            }
        });
    }

    private void initColorList() {
        RecyclerView textColorList = findViewById(R.id.text_color_list);
        ColorListAdapter colorListAdapter = new ColorListAdapter();
        textColorList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        textColorList.addItemDecoration(new GroupThumbDecoration());
        textColorList.setAdapter(colorListAdapter);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#ffffff"));
        colors.add(Color.parseColor("#1e1e1e"));
        colors.add(Color.parseColor("#7cccff"));
        colors.add(Color.parseColor("#83e9a2"));
        colors.add(Color.parseColor("#ffe684"));
        colors.add(Color.parseColor("#ff7c62"));
        colors.add(Color.parseColor("#ffb7d6"));
        colorListAdapter.setData(colors);

        colorListAdapter.setOnColorSelectedListener(new ColorListAdapter.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                mModel.setTextColor(color);
            }
        });

        RecyclerView strokeColorList = findViewById(R.id.text_stroke_list);
        ColorListAdapter strokeColorListAdapter = new ColorListAdapter();
        strokeColorList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        strokeColorList.addItemDecoration(new GroupThumbDecoration());
        strokeColorList.setAdapter(strokeColorListAdapter);
        strokeColorListAdapter.setData(colors);
        strokeColorListAdapter.setOnColorSelectedListener(new ColorListAdapter.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                mModel.setStrokeColor(color);
            }
        });

        SeekBar sb_stroke_width = findViewById(R.id.sb_stroke_width);
        sb_stroke_width.setMax(20);
        sb_stroke_width.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mModel.setStrokeWidth(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        hideAll();
        if (checkedId == R.id.rb_text_content) {
            showEditLayout();
        } else if (checkedId == R.id.rb_text_font) {
            showFontLayout();
        } else if (checkedId == R.id.rb_text_color) {
            showColorLayout();
        }
    }

    private void hideAll() {
        editLayout.setVisibility(INVISIBLE);
        fontList.setVisibility(INVISIBLE);
        colorLayout.setVisibility(INVISIBLE);
    }

    private void showEditLayout() {
        editLayout.setVisibility(VISIBLE);
        showInputMethod();
    }

    private void showFontLayout() {
        hideInputMethod();
        fontList.setVisibility(VISIBLE);
    }

    private void showColorLayout() {
        hideInputMethod();
        colorLayout.setVisibility(VISIBLE);
    }

    private void showInputMethod() {
        mEditText.requestFocus();
        mInputMethodManager.showSoftInput(mEditText, 0);
    }

    private void hideInputMethod() {
        mInputMethodManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
        //mEditText.clearFocus();
    }

    private int getScreenHeight() {
        DisplayMetrics outMetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    public void setupWidth(TextUiModel model) {
        mModel = model;
        mEditText.setMaxLength(model.getMax());
        mEditText.setText(model.getText());
        showInputMethod();
    }
}

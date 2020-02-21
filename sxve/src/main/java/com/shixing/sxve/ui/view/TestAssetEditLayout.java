package com.shixing.sxve.ui.view;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.shixing.sxve.R;
import com.shixing.sxve.ui.model.TextUiModel;

public class TestAssetEditLayout extends FrameLayout implements RadioGroup.OnCheckedChangeListener {
//    private static final String TAG = "TestAssetEditLayout";
//    private View editLayout;
//    private View fontList;
//    private View colorLayout;
//    private Activity mActivity;
    private CounterEditText mEditText;
    private InputMethodManager mInputMethodManager;
    private TextUiModel mModel;
//    private RadioGroup mRadioGroup;

    public TestAssetEditLayout(Context context) {
        this(context, null);
    }

    public TestAssetEditLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestAssetEditLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Activity  mActivity = ((Activity) context);

        LayoutInflater.from(context).inflate(R.layout.sxve_text_asset_edit, this, true);


        initEdit();
//        initRadioGroup();
//        initFontList();
//        initColorList();
//        initStrokeColorList();
//        initStrokeWidthSeekbar();

        mInputMethodManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

        findViewById(R.id.confirm).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideInputMethod();
                setVisibility(INVISIBLE);
            }
        });

        findViewById(R.id.edit_panel).setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setupWidth(mModel);
                showEditLayout();
                showInputMethod();
                invalidate();
                return true;
            }
        });
    }

//    private void initStrokeWidthSeekbar() {
//        SeekBar seekBar=this.findViewById(R.id.seekbar_stroke_width);
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                mModel.setStrokeWidth(progress);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//    }

//    private void initStrokeColorList() {
//        RecyclerView strokeColorList=this.findViewById(R.id.rv_stroke_list);
//        ColorListAdapter colorListAdapter = new ColorListAdapter();
//        strokeColorList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//        strokeColorList.addItemDecoration(new GroupThumbDecoration());
//        strokeColorList.setAdapter(colorListAdapter);
//        ArrayList<Integer> colors = new ArrayList<>();
//        colors.add(Color.parseColor("#ffffff"));
//        colors.add(Color.parseColor("#1e1e1e"));
//        colors.add(Color.parseColor("#7cccff"));
//        colors.add(Color.parseColor("#83e9a2"));
//        colors.add(Color.parseColor("#ffe684"));
//        colors.add(Color.parseColor("#ff7c62"));
//        colors.add(Color.parseColor("#ffb7d6"));
//        colorListAdapter.setData(colors);
//        colorListAdapter.setOnColorSelectedListener(new ColorListAdapter.OnColorSelectedListener() {
//            @Override
//            public void onColorSelected(int color) {
//                mModel.setStrokeColor(color);
//            }
//        });
//    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        return false;
//    }

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
                    mModel.setTextChanged(true);
                    mModel.setText(s.toString().trim());
                }

            }
        });
    }

//    private void initRadioGroup() {
////        mRadioGroup = this.findViewById(R.id.rg_text_edit);
////        mRadioGroup.setOnCheckedChangeListener(this);
//        fontList = findViewById(R.id.font_list);
//        colorLayout = findViewById(R.id.style_layout);
//    }
//
//    private void initFontList() {
//        RecyclerView fontList = findViewById(R.id.font_list);
//        FontListAdapter fontListAdapter = new FontListAdapter();
//        fontList.setLayoutManager(new LinearLayoutManager(getContext()));
//        fontList.setAdapter(fontListAdapter);
//
//        ArrayList<FontModel> fontModels = new ArrayList<>();
//        fontModels.add(new FontModel("系统字体", Typeface.defaultFromStyle(Typeface.NORMAL)));
//        fontModels.add(new FontModel("加粗", Typeface.defaultFromStyle(Typeface.BOLD)));
//        fontModels.add(new FontModel("斜体", Typeface.defaultFromStyle(Typeface.ITALIC)));
//        fontModels.add(new FontModel("斜体加粗", Typeface.defaultFromStyle(Typeface.BOLD_ITALIC)));
//        fontListAdapter.setData(fontModels);
//
//        fontListAdapter.setOnFontSelectedListener(new FontListAdapter.OnFontSelectedListener() {
//            @Override
//            public void onFontSelected(Typeface typeface) {
//                int style=typeface.getStyle();
//                mModel.setTypeface(Typeface.create(mModel.getTypeface(),style));
//            }
//        });
//    }
//
//    private void initColorList() {
//        RecyclerView textColorList = findViewById(R.id.text_color_list);
//        ColorListAdapter colorListAdapter = new ColorListAdapter();
//        textColorList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//        textColorList.addItemDecoration(new GroupThumbDecoration());
//        textColorList.setAdapter(colorListAdapter);
//
//        ArrayList<Integer> colors = new ArrayList<>();
//        colors.add(Color.parseColor("#ffffff"));
//        colors.add(Color.parseColor("#1e1e1e"));
//        colors.add(Color.parseColor("#7cccff"));
//        colors.add(Color.parseColor("#83e9a2"));
//        colors.add(Color.parseColor("#ffe684"));
//        colors.add(Color.parseColor("#ff7c62"));
//        colors.add(Color.parseColor("#ffb7d6"));
//        colorListAdapter.setData(colors);
//
//        colorListAdapter.setOnColorSelectedListener(new ColorListAdapter.OnColorSelectedListener() {
//            @Override
//            public void onColorSelected(int color) {
//                mModel.setTextColor(color);
//            }
//        });
//    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
//        if (checkedId == R.id.rb_text_content) {
//            showEditLayout();
//        } else if (checkedId == R.id.rb_text_font) {
//            showFontLayout();
//        } else if (checkedId == R.id.rb_text_color) {
//            showColorLayout();
//        }
    }


    private void showEditLayout() {
//        editLayout.setVisibility(VISIBLE);
        showInputMethod();
    }

//    private void showFontLayout() {
//        hideInputMethod();
//        fontList.setVisibility(VISIBLE);
//    }
//
//    private void showColorLayout() {
//        hideInputMethod();
//        colorLayout.setVisibility(VISIBLE);
//    }

    private void showInputMethod() {
        mEditText.requestFocus();
        mInputMethodManager.showSoftInput(mEditText, 0);
    }

    private void hideInputMethod() {
        mInputMethodManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
        //mEditText.clearFocus();
    }

    public void setupWidth(TextUiModel model) {
        mModel = model;
        mEditText.setMaxLength(model.getMax());
        mEditText.setText(model.getText());
        showInputMethod();
    }
//    private int getScreenHeight() {
//        DisplayMetrics outMetrics = new DisplayMetrics();
//        mActivity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
//        return outMetrics.heightPixels;
//    }
}

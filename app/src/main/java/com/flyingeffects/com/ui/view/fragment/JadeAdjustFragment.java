package com.flyingeffects.com.ui.view.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.JadePagerAdapter;
import com.flyingeffects.com.databinding.FragmentJadeAdjustBinding;
import com.flyingeffects.com.utils.screenUtil;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

/**
 * 玉体字调节fragment
 * Created by Try sven775288@gmail.com on 2021/5/26
 */
public class JadeAdjustFragment extends Fragment {

    private static final String TAG = "JadeAdjustFragment";

    private FragmentJadeAdjustBinding binding;
    private String[] stringArray;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.TransBottomSheetDialogStyle);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_jade_adjust, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        stringArray = getResources().getStringArray(R.array.jade_option);
        initTabLayoutWithViewPager();
        initInputEditText();
    }

    private void initInputEditText() {
        binding.input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (OnInputChangeCallBack != null) {
                    OnInputChangeCallBack.onChange(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initTabLayoutWithViewPager() {

        binding.tlTabsBj.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                if (view != null) {
                    AppCompatTextView tvTabText = view.findViewById(R.id.tv_tab_item_text);
                    tvTabText.setTextSize(24);
                    tvTabText.setTextColor(Color.parseColor("#ffffff"));
                }
                if (tab.getPosition() == 0) {
                    showSoftInput(binding.input);
                } else {
                    hideSoftInput();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                if (view != null) {
                    AppCompatTextView tvTabText = view.findViewById(R.id.tv_tab_item_text);
                    tvTabText.setTextSize(16);
                    tvTabText.setTextColor(Color.parseColor("#797979"));
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        JadePagerAdapter jadePagerAdapter = new JadePagerAdapter(stringArray);
        binding.viewpager.setAdapter(jadePagerAdapter);
        new TabLayoutMediator(binding.tlTabsBj, binding.viewpager, true, true, (tab, position) -> {
            LinearLayout customView = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.item_home_tab, null);
            AppCompatTextView tvTabText = customView.findViewById(R.id.tv_tab_item_text);
            tvTabText.setText(stringArray[position]);
            tvTabText.setTextColor(Color.parseColor("#797979"));
            if (position == 0) {
                tvTabText.setTextSize(24);
                tvTabText.setTextColor(Color.parseColor("#ffffff"));
                showSoftInput(binding.input);
            }
            tab.setCustomView(customView);
        }).attach();
    }

    private void showSoftInput(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideSoftInput() {
        binding.input.clearFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.input.getWindowToken(), 0);
    }

    public interface OnInputChangeCallBack {
        void onChange(String string);
    }

    private OnInputChangeCallBack OnInputChangeCallBack;

    public JadeAdjustFragment.OnInputChangeCallBack getOnInputChangeCallBack() {
        return OnInputChangeCallBack;
    }

    public void setOnInputChangeCallBack(JadeAdjustFragment.OnInputChangeCallBack onInputChangeCallBack) {
        OnInputChangeCallBack = onInputChangeCallBack;
    }
}
package com.flyingeffects.com.ui.view.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.main_recycler_adapter;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.view.AutoNewLineLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * user :TongJu  ;描述：搜索页面
 * 时间：2018/4/24
 **/

public class frag_search extends BaseFragment {


    @BindView(R.id.AutoNewLineLayout)
    AutoNewLineLayout autoNewLineLayout;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView ;


    @BindView(R.id.ed_search)
    EditText ed_text;

    private List<new_fag_template_item> allData = new ArrayList<>();

    @BindView(R.id.smart_refresh_layout)
    SmartRefreshLayout smartRefreshLayout;

    private StaggeredGridLayoutManager layoutManager;
    private main_recycler_adapter adapter;


    @Override
    protected int getContentLayout() {
        return R.layout.fag_search;
    }


    @Override
    protected void initView() {
        //键盘的搜索按钮
        ed_text.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) { //键盘的搜索按钮
                String text = ed_text.getText().toString().trim();
                if (!text.equals("")) {
                    smartRefreshLayout.setVisibility(View.VISIBLE);
                }
                return true;
            }
            return false;
        });

        ed_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    smartRefreshLayout.setVisibility(View.GONE);
                } else {
                    smartRefreshLayout.setVisibility(View.VISIBLE);
                }
                ed_text.setSelection(s.length());
            }
        });
        showSoftInputFromWindow(ed_text);
    }


    @Override
    protected void initAction() {
        test();
    }

    @Override
    protected void initData() {
        initRecycler();
        initSmartRefreshLayout();
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    private void test() {
        for (int i = 0; i < 20; i++) {
            TextView tv = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.textview_recommend, null);
            tv.setText("飞天特效" + i * 110);
            tv.setTextColor(Color.parseColor("#FF0000"));
            GradientDrawable view_ground = (GradientDrawable) tv.getBackground(); //获取控件的背
            view_ground.setStroke(2, Color.parseColor("#FF0000"));
            autoNewLineLayout.addView(tv);
        }


    }


    private void initSmartRefreshLayout() {
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
        });
        smartRefreshLayout.setOnLoadMoreListener(refresh -> {
        });
    }



    private void initRecycler() {

        for (int i=0;i<10;i++){
            new_fag_template_item item=new new_fag_template_item();
            item.setTitle("123");
            allData.add(item);
        }

        adapter = new main_recycler_adapter(R.layout.list_main_item, allData, getActivity(), null, 0);
        layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter, view, position) -> {

        });
    }

    public void showSoftInputFromWindow(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(editText, 0);
    }

}



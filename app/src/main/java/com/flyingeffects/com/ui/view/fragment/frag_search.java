package com.flyingeffects.com.ui.view.fragment;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.home_vp_frg_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.SearchKeyWord;
import com.flyingeffects.com.enity.SendSearchText;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.ColorCorrectionManager;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.view.WarpLinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import de.greenrobot.event.EventBus;
import rx.Observable;


/**
 * user :TongJu  ;描述：搜索页面
 * 时间：2018/4/24
 **/

public class frag_search extends BaseFragment {


    @BindView(R.id.AutoNewLineLayout)
    WarpLinearLayout autoNewLineLayout;


    @BindView(R.id.ed_search)
    EditText ed_text;


    @BindView(R.id.iv_delete)
    ImageView iv_delete;

    @BindView(R.id.tv_youyou)
    TextView tv_youyou;


    @BindView(R.id.ll_add_child)
    LinearLayout ll_add_child;


    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.horizontal_scrollView)
    HorizontalScrollView horizontalScrollView;

    private ArrayList<TextView> listTv = new ArrayList<>();
    private ArrayList<View> listView = new ArrayList<>();
    private ArrayList<SearchKeyWord> listSearchKey = new ArrayList<>();
    private String nowShowText;
    private FragmentManager manager;


    @Override
    protected int getContentLayout() {
        return R.layout.fag_search;
    }


    @Override
    protected void initView() {
        //键盘的搜索按钮
        ed_text.setOnEditorActionListener((v, actionId, event) -> {

            LogUtil.d("OOM","setOnEditorActionListener");
            if (actionId == EditorInfo.IME_ACTION_SEARCH) { //键盘的搜索按钮
                nowShowText = ed_text.getText().toString().trim();
                if (!nowShowText.equals("")) {
                    EventBus.getDefault().post(new SendSearchText(nowShowText));
                    hideResultView(false);
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
                    hideResultView(false);
                    iv_delete.setVisibility(View.GONE);
                } else {
                    iv_delete.setVisibility(View.VISIBLE);
                }
            }
        });
        iv_delete.setOnClickListener(view -> {
            ed_text.setText("");
        });


        hideResultView(true);
    }


    private void hideResultView(boolean isHide) {
        if (isHide) {
            viewPager.setVisibility(View.INVISIBLE);
            horizontalScrollView.setVisibility(View.GONE);
        } else {
            viewPager.setVisibility(View.VISIBLE);
            horizontalScrollView.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initAction() {

    }

    @Override
    protected void initData() {
    }


    @Override
    public void onResume() {
        super.onResume();
        LogUtil.d("OOM","onResume");
        requestKeywordList();
        hideResultView(true);
        listTv.clear();
        listView.clear();
        showHeadTitle();
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    private void setKeyWordList(ArrayList<SearchKeyWord> listSearchKey) {
        autoNewLineLayout.removeAllViews();
        for (int i = 0; i < listSearchKey.size(); i++) {
            String nowChooseColor = ColorCorrectionManager.getInstance().getChooseColor(i);
            TextView tv = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.textview_recommend, null);
            tv.setText(listSearchKey.get(i).getName());
            tv.setTextColor(Color.parseColor(nowChooseColor));
            int finalI = i;
            tv.setOnClickListener(view -> {
                if (!DoubleClick.getInstance().isFastDoubleClick()) {
                    if (listSearchKey.size() >= finalI + 1) {
                        statisticsEventAffair.getInstance().setFlag(getActivity(), "4_recommend", listSearchKey.get(finalI).getName());
                        nowShowText = listSearchKey.get(finalI).getName();
                        ed_text.setText(nowShowText);
                        hideResultView(false);
//                        setResultMargin();
                        EventBus.getDefault().post(new SendSearchText(nowShowText));
                    }
                }
            });
            GradientDrawable view_ground = (GradientDrawable) tv.getBackground(); //获取控件的背
            view_ground.setStroke(2, Color.parseColor(nowChooseColor));
            autoNewLineLayout.addView(tv);
        }

    }


    /**
     * 请求友友推荐
     */
    private void requestKeywordList() {
        listSearchKey.clear();
        HashMap<String, String> params = new HashMap<>();
        Observable ob = Api.getDefault().keywordList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(getActivity()) {
            @Override
            protected void _onError(String message) {
            }

            @Override
            protected void _onNext(Object data) {
                String str = StringUtil.beanToJSONString(data);
                try {
                    JSONArray array = new JSONArray(str);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject ob = array.getJSONObject(i);
                        SearchKeyWord key = new SearchKeyWord();
                        key.setColor(ob.getString("color"));
                        key.setName(ob.getString("name"));
                        key.setID(ob.getString("ID"));
                        key.setWeigh(ob.getString("weigh"));
                        key.setCreate_time(ob.getString("create_time"));
                        listSearchKey.add(key);
                    }
                    setKeyWordList(listSearchKey);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LogUtil.d("OOM", str);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }


    private void showHeadTitle() {
        ll_add_child.removeAllViews();
        String[] titles = {"模板", "背景"};
        for (int i = 0; i < titles.length; i++) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_bj_head, null);
            TextView tv = view.findViewById(R.id.tv_name_bj_head);
            View view_line = view.findViewById(R.id.view_line_head);
            tv.setText(titles[i]);
            tv.setId(i);
            tv.setOnClickListener(v -> showWitchBtn(v.getId()));
            listTv.add(tv);
            listView.add(view_line);
            ll_add_child.addView(view);
            setViewpager();
        }
    }

    private void setViewpager() {
        ArrayList<Fragment> list = new ArrayList<>();
        Bundle bundle = new Bundle();
        bundle.putSerializable("from", 0);
        fragBjSearch fragment = new fragBjSearch();
        fragment.setArguments(bundle);
        list.add(fragment);
        Bundle bundle2 = new Bundle();
        bundle2.putSerializable("from", 1);
        fragBjSearch fragment2 = new fragBjSearch();
        fragment2.setArguments(bundle2);
        list.add(fragment2);
//        if (manager == null) {
//            manager = getChildFragmentManager();
//        }
        home_vp_frg_adapter adapter = new home_vp_frg_adapter( getChildFragmentManager(), list);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                showWitchBtn(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        new Handler().postDelayed(() -> showWitchBtn(0), 500);
    }


    private void showWitchBtn(int showWitch) {
        for (int i = 0; i < listTv.size(); i++) {
            TextView tv = listTv.get(i);
            View view = listView.get(i);
            if (i == showWitch) {
                tv.setTextSize(21);
                view.setVisibility(View.VISIBLE);
            } else {
                tv.setTextSize(17);
                view.setVisibility(View.INVISIBLE);
            }
        }
        viewPager.setCurrentItem(showWitch);
    }





}



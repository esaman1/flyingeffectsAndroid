package com.flyingeffects.com.ui.view.activity;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.SearchTemplateItemAdapter;
import com.flyingeffects.com.adapter.home_vp_frg_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.SearchKeyWord;
import com.flyingeffects.com.enity.SearchTemplateInfoEntity;
import com.flyingeffects.com.enity.SendSearchText;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.AdConfigs;
import com.flyingeffects.com.manager.AdManager;
import com.flyingeffects.com.manager.ColorCorrectionManager;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.view.fragment.FragmentUser;
import com.flyingeffects.com.ui.view.fragment.fragBjSearch;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.view.WarpLinearLayout;
import com.google.android.material.appbar.AppBarLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import de.greenrobot.event.EventBus;
import rx.Observable;

/**
 * @author ZhouGang
 * @date 2020/10/12
 * 背景、一键模板、换装搜索
 */
public class TemplateSearchActivity extends BaseActivity {

    @BindView(R.id.AutoNewLineLayout)
    WarpLinearLayout autoNewLineLayout;
    @BindView(R.id.ed_search)
    EditText ed_text;
    @BindView(R.id.tl_tabs_search)
    SlidingTabLayout tabSearch;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.rc_search)
    RecyclerView rcSearch;
    @BindView(R.id.main_content)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.ll_ad_content)
    LinearLayout ll_ad_content;
    @BindView(R.id.tv_search)
    TextView tv_search;
    @BindView(R.id.iv_delete)
    ImageView mIvDelete;

    private ArrayList<Fragment> list = new ArrayList<>();;
    private ArrayList<SearchKeyWord> listSearchKey = new ArrayList<>();
    private String nowShowText;

    SearchTemplateItemAdapter  searchTemplateItemAdapter;

    /**0表示 背景过来，1表示 模板进来 3表示换脸进来*/
    private int isFrom;

    @Override
    protected int getLayoutId() {
        return R.layout.act_template_search;
    }

    @Override
    protected void initView() {

        statisticsEventAffair.getInstance().setFlag(TemplateSearchActivity.this, "14_go_to_search");
        isFrom = getIntent().getIntExtra("isFrom", 0);


        searchTemplateItemAdapter = new SearchTemplateItemAdapter(R.layout.item_search_template_mohu);
        rcSearch.setAdapter(searchTemplateItemAdapter);
        //键盘的搜索按钮
        ed_text.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) { //键盘的搜索按钮
                if(!TextUtils.isEmpty(ed_text.getText().toString().trim())){
                    toTemplate(ed_text.getText().toString().trim());
                    rcSearch.setVisibility(View.GONE);
                    coordinatorLayout.setVisibility(View.VISIBLE);
                }
                return true;
            }
            return false;
        });
        mIvDelete.setOnClickListener(view -> {
            rcSearch.setVisibility(View.GONE);
            coordinatorLayout.setVisibility(View.VISIBLE);
            ed_text.setText("");
            hideResultView(true);
            cancelFocus();
            hideKeyboard();
            EventBus.getDefault().post(new SendSearchText(""));
            viewPager.setCurrentItem(0);
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
                    rcSearch.setVisibility(View.GONE);
                    mIvDelete.setVisibility(View.GONE);
                    coordinatorLayout.setVisibility(View.VISIBLE);
                    appbar.setExpanded(true);
                    hideResultView(true);
                } else if (!keywordQueryItemClickTag) {
                    rcSearch.setVisibility(View.VISIBLE);
                    coordinatorLayout.setVisibility(View.GONE);
                    requestServerTemplateFuzzyQuery(ed_text.getText().toString().trim());
                    mIvDelete.setVisibility(View.VISIBLE);
                }

            }
        });
        ed_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !TextUtils.isEmpty(ed_text.getText().toString())) {
                    if(!keywordQueryItemClickTag){
                        rcSearch.setVisibility(View.VISIBLE);
                        coordinatorLayout.setVisibility(View.GONE);
                        requestServerTemplateFuzzyQuery(ed_text.getText().toString().trim());
                    }
                    mIvDelete.setVisibility(View.VISIBLE);
                    keywordQueryItemClickTag = false;
                }
            }
        });
        hideResultView(true);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if( BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()){
            AdManager.getInstance().showImageAd(this, AdConfigs.AD_IMAGE, ll_ad_content, new AdManager.Callback() {
                @Override
                public void adClose() {

                }
            });
        }
        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(ed_text.getText().toString().trim())){
                    statisticsEventAffair.getInstance().setFlag(TemplateSearchActivity.this, "4_search_button");
                    statisticsEventAffair.getInstance().setFlag(TemplateSearchActivity.this, "4_search_new",ed_text.getText().toString());

                    toTemplate(ed_text.getText().toString().trim());
                    rcSearch.setVisibility(View.GONE);
                    coordinatorLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    /**关键字模糊查询*/
    private void requestServerTemplateFuzzyQuery(String keywords) {
        HashMap<String, String> params = new HashMap<>();
        params.put("keywords", keywords);
        // 1模板2背景3换装
        if (isFrom == 0) {
            params.put("template_type", "2");
        } else if (isFrom == 1) {
            params.put("template_type", "1");
        } else {
            params.put("template_type", "3");
        }
        HttpUtil.getInstance().toSubscribe(Api.getDefault().templateKeywords(BaseConstans.getRequestHead(params)),
                new ProgressSubscriber<List<SearchTemplateInfoEntity>>(TemplateSearchActivity.this) {
                    @Override
                    protected void onSubError(String message) {
                        ToastUtil.showToast(message);
                        rcSearch.setVisibility(View.GONE);
                        coordinatorLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    protected void onSubNext(List<SearchTemplateInfoEntity> datas) {
                        LogUtil.d("OOM", "模板模糊查询" + StringUtil.beanToJSONString(datas));
                        SearchTemplateInfoEntity infoEntity = new SearchTemplateInfoEntity();
                        infoEntity.setName(keywords);
                        datas.add(0, infoEntity);
                        searchTemplateItemAdapter.setInquireWordColor(keywords);
                        searchTemplateItemAdapter.setNewData(datas);
                    }
                }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
        searchTemplateItemAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String itemContent = searchTemplateItemAdapter.getData().get(position).getName();
                toTemplate(itemContent);
                rcSearch.setVisibility(View.GONE);
                coordinatorLayout.setVisibility(View.VISIBLE);
                keywordQueryItemClickTag = true;
                ed_text.setText(itemContent);
                if (position != 0) {
                    statisticsEventAffair.getInstance().setFlag(TemplateSearchActivity.this, "4_search_query", itemContent);
                } else {
                    statisticsEventAffair.getInstance().setFlag(TemplateSearchActivity.this, "4_search_query_first");
                }
            }
        });
    }

    boolean keywordQueryItemClickTag = false;

    private void toTemplate(String content) {
        nowShowText = content;
        if (!"".equals(nowShowText)) {
            cancelFocus();
            statisticsEventAffair.getInstance().setFlag(TemplateSearchActivity.this, "10_searchfor", nowShowText);
            EventBus.getDefault().post(new SendSearchText(nowShowText));
            hideResultView(false);
            ll_ad_content.setVisibility(View.GONE);
        }
    }


    private void hideResultView(boolean isHide) {
        if (isHide) {
            viewPager.setVisibility(View.INVISIBLE);
            tabSearch.setVisibility(View.INVISIBLE);
        } else {
            viewPager.setVisibility(View.VISIBLE);
            tabSearch.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initAction() {
        showHeadTitle();
        requestKeywordList();
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    private void setKeyWordList(ArrayList<SearchKeyWord> listSearchKey) {
        autoNewLineLayout.removeAllViews();
        for (int i = 0; i < listSearchKey.size(); i++) {
            String nowChooseColor = ColorCorrectionManager.getInstance().getChooseColor(i);
            TextView tv = (TextView) LayoutInflater.from(TemplateSearchActivity.this).inflate(R.layout.textview_recommend, null);
            tv.setText(listSearchKey.get(i).getName());
            tv.setTextColor(Color.parseColor(nowChooseColor));
            int finalI = i;
            tv.setOnClickListener(view -> {
                if (!DoubleClick.getInstance().isFastDoubleClick()) {
                    if (listSearchKey.size() >= finalI + 1) {
                        statisticsEventAffair.getInstance().setFlag(TemplateSearchActivity.this, "4_recommend", listSearchKey.get(finalI).getName());
                        nowShowText = listSearchKey.get(finalI).getName();
                        keywordQueryItemClickTag = true;
                        ed_text.setText(nowShowText);
                        rcSearch.setVisibility(View.GONE);
                        coordinatorLayout.setVisibility(View.VISIBLE);
                        toTemplate(nowShowText);
                        ll_ad_content.setVisibility(View.GONE);
                        statisticsEventAffair.getInstance().setFlag(TemplateSearchActivity.this, "10_searchfor", nowShowText);
                        EventBus.getDefault().post(new SendSearchText(nowShowText));

                    }
                }
            });
            //获取控件的背
            GradientDrawable view_ground = (GradientDrawable) tv.getBackground();
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
        //isFrom为0 表示背景
        if (isFrom == 0) {
            params.put("template_type", "2");
        } else if (isFrom == 3) {
            //isFrom为3 表示换装
            params.put("template_type", "3");
        } else {
            //isFrom为1 表示模板
            params.put("template_type", "1");
        }
        // 启动时间
        Observable ob = Api.getDefault().keywordList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(TemplateSearchActivity.this) {
            @Override
            protected void onSubError(String message) {
            }

            @Override
            protected void onSubNext(Object data) {
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
        String[] titles;
        if (isFrom == 0) {
            titles = new String[]{"背景", "模板", "换装", "用户"};
        } else if (isFrom == 3) {
            titles = new String[]{"换装", "背景", "模板", "用户"};
        } else {
            titles = new String[]{"模板", "背景", "换装", "用户"};
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("from", 1);
        fragBjSearch fragment = new fragBjSearch();
        fragment.setArguments(bundle);

        Bundle bundle2 = new Bundle();
        bundle2.putSerializable("from", 0);
        fragBjSearch fragment2 = new fragBjSearch();
        fragment2.setArguments(bundle2);

        Bundle bundle3 = new Bundle();
        bundle3.putSerializable("from", 3);
        fragBjSearch fragment3 = new fragBjSearch();
        fragment3.setArguments(bundle3);

        if (isFrom == 0) {
            list.add(fragment);
            list.add(fragment2);
            list.add(fragment3);
            list.add(new FragmentUser());
        } else if (isFrom == 1) {
            list.add(fragment2);
            list.add(fragment);
            list.add(fragment3);
            list.add(new FragmentUser());
        } else if (isFrom == 3) {
            list.add(fragment3);
            list.add(fragment);
            list.add(fragment2);
            list.add(new FragmentUser());
        }
        home_vp_frg_adapter adapter = new home_vp_frg_adapter(getSupportFragmentManager(), list);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        tabSearch.setViewPager(viewPager, titles);
    }

    private void cancelFocus() {
        ed_text.setFocusable(true);
        ed_text.setFocusableInTouchMode(true);
        ed_text.requestFocus();
        //失去焦点
        ed_text.clearFocus();
    }

}

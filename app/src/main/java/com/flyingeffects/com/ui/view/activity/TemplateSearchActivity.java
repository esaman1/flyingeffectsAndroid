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
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.SearchTemplateItemAdapter;
import com.flyingeffects.com.adapter.home_vp_frg_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.databinding.ActTemplateSearchBinding;
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
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.ui.view.fragment.FragmentUser;
import com.flyingeffects.com.ui.view.fragment.fragBjSearch;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.fragment.app.Fragment;

import butterknife.BindView;
import de.greenrobot.event.EventBus;
import rx.Observable;

/**
 * @author ZhouGang
 * @date 2020/10/12
 * 背景、一键模板、换装搜索
 */
public class TemplateSearchActivity extends BaseActivity {


    private ArrayList<Fragment> list = new ArrayList<>();
    private ArrayList<SearchKeyWord> listSearchKey = new ArrayList<>();
    private String nowShowText;

    SearchTemplateItemAdapter searchTemplateItemAdapter;

    /**
     * 0表示 背景过来，1表示 模板进来 3表示换脸进来
     */
    private int isFrom;
    private ActTemplateSearchBinding mBinding;

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initView() {

        mBinding = ActTemplateSearchBinding.inflate(getLayoutInflater());
        View rootView = mBinding.getRoot();
        setContentView(rootView);

        StatisticsEventAffair.getInstance().setFlag(TemplateSearchActivity.this, "14_go_to_search");

        isFrom = getIntent().getIntExtra("isFrom", 0);

        searchTemplateItemAdapter = new SearchTemplateItemAdapter(R.layout.item_search_template_mohu);
        mBinding.rcSearch.setAdapter(searchTemplateItemAdapter);
        //键盘的搜索按钮
        mBinding.edSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) { //键盘的搜索按钮
                if (!TextUtils.isEmpty(mBinding.edSearch.getText().toString().trim())) {
                    toTemplate(mBinding.edSearch.getText().toString().trim());
                    mBinding.rcSearch.setVisibility(View.GONE);
                    mBinding.mainContent.setVisibility(View.VISIBLE);
                }
                return true;
            }
            return false;
        });
        mBinding.ivDelete.setOnClickListener(view -> {
            mBinding.rcSearch.setVisibility(View.GONE);
            mBinding.mainContent.setVisibility(View.VISIBLE);
            mBinding.edSearch.setText("");
            hideResultView(true);
            cancelFocus();
            hideKeyboard();
            EventBus.getDefault().post(new SendSearchText(""));
            mBinding.viewpager.setCurrentItem(0);
        });

        mBinding.edSearch.addTextChangedListener(new TextWatcher() {
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
                    mBinding.rcSearch.setVisibility(View.GONE);
                    mBinding.ivDelete.setVisibility(View.GONE);
                    mBinding.mainContent.setVisibility(View.VISIBLE);
                    mBinding.appbar.setExpanded(true);
                    hideResultView(true);
                } else if (!keywordQueryItemClickTag) {
                    mBinding.rcSearch.setVisibility(View.VISIBLE);
                    mBinding.mainContent.setVisibility(View.GONE);
                    requestServerTemplateFuzzyQuery(mBinding.edSearch.getText().toString().trim());
                    mBinding.ivDelete.setVisibility(View.VISIBLE);
                }

            }
        });

        mBinding.edSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !TextUtils.isEmpty(mBinding.edSearch.getText().toString())) {
                    if (!keywordQueryItemClickTag) {
                        mBinding.rcSearch.setVisibility(View.VISIBLE);
                        mBinding.mainContent.setVisibility(View.GONE);
                        requestServerTemplateFuzzyQuery(mBinding.edSearch.getText().toString().trim());
                    }
                    mBinding.ivDelete.setVisibility(View.VISIBLE);
                    keywordQueryItemClickTag = false;
                }
            }
        });

        hideResultView(true);

        mBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
            AdManager.getInstance().showImageAd(this, AdConfigs.AD_IMAGE, mBinding.llAdContent);
        }

        mBinding.tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mBinding.edSearch.getText().toString().trim())) {
                    StatisticsEventAffair.getInstance().setFlag(TemplateSearchActivity.this, "4_search_button");
                    StatisticsEventAffair.getInstance().setFlag(TemplateSearchActivity.this, "4_search_new", mBinding.edSearch.getText().toString());

                    toTemplate(mBinding.edSearch.getText().toString().trim());
                    mBinding.rcSearch.setVisibility(View.GONE);
                    mBinding.mainContent.setVisibility(View.VISIBLE);
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

    /**
     * 关键字模糊查询
     */
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
                        mBinding.rcSearch.setVisibility(View.GONE);
                        mBinding.mainContent.setVisibility(View.VISIBLE);
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
                mBinding.rcSearch.setVisibility(View.GONE);
                mBinding.mainContent.setVisibility(View.VISIBLE);
                keywordQueryItemClickTag = true;
                mBinding.edSearch.setText(itemContent);
                if (position != 0) {
                    StatisticsEventAffair.getInstance().setFlag(TemplateSearchActivity.this, "4_search_query", itemContent);
                } else {
                    StatisticsEventAffair.getInstance().setFlag(TemplateSearchActivity.this, "4_search_query_first");
                }
            }
        });
    }

    boolean keywordQueryItemClickTag = false;

    private void toTemplate(String content) {
        nowShowText = content;
        if (!"".equals(nowShowText)) {
            cancelFocus();
            StatisticsEventAffair.getInstance().setFlag(TemplateSearchActivity.this, "10_searchfor", nowShowText);
            EventBus.getDefault().post(new SendSearchText(nowShowText));
            hideResultView(false);
            mBinding.llAdContent.setVisibility(View.GONE);
        }
    }


    private void hideResultView(boolean isHide) {
        if (isHide) {
            mBinding.viewpager.setVisibility(View.INVISIBLE);
            mBinding.tlTabsSearch.setVisibility(View.INVISIBLE);
        } else {
            mBinding.viewpager.setVisibility(View.VISIBLE);
            mBinding.tlTabsSearch.setVisibility(View.VISIBLE);
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
        mBinding.AutoNewLineLayout.removeAllViews();
        for (int i = 0; i < listSearchKey.size(); i++) {
            String nowChooseColor = ColorCorrectionManager.getInstance().getChooseColor(i);
            TextView tv = (TextView) LayoutInflater.from(TemplateSearchActivity.this).inflate(R.layout.textview_recommend, null);
            tv.setText(listSearchKey.get(i).getName());
            tv.setTextColor(Color.parseColor(nowChooseColor));
            int finalI = i;
            tv.setOnClickListener(view -> {
                if (!DoubleClick.getInstance().isFastDoubleClick()) {
                    if (listSearchKey.size() >= finalI + 1) {
                        StatisticsEventAffair.getInstance().setFlag(TemplateSearchActivity.this, "4_recommend", listSearchKey.get(finalI).getName());
                        nowShowText = listSearchKey.get(finalI).getName();
                        keywordQueryItemClickTag = true;
                        mBinding.edSearch.setText(nowShowText);
                        mBinding.rcSearch.setVisibility(View.GONE);
                        mBinding.mainContent.setVisibility(View.VISIBLE);
                        toTemplate(nowShowText);
                        mBinding.llAdContent.setVisibility(View.GONE);
                        StatisticsEventAffair.getInstance().setFlag(TemplateSearchActivity.this, "10_searchfor", nowShowText);
                        EventBus.getDefault().post(new SendSearchText(nowShowText));

                    }
                }
            });
            //获取控件的背
            GradientDrawable viewGround = (GradientDrawable) tv.getBackground();
            viewGround.setStroke(2, Color.parseColor(nowChooseColor));
            mBinding.AutoNewLineLayout.addView(tv);
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
            titles = new String[]{"背景", "模板", "闪图", "用户"};
        } else if (isFrom == 3) {
            titles = new String[]{"闪图", "背景", "模板", "用户"};
        } else {
            titles = new String[]{"模板", "背景", "闪图", "用户"};
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
        mBinding.viewpager.setAdapter(adapter);
        mBinding.viewpager.setOffscreenPageLimit(3);
        mBinding.tlTabsSearch.setViewPager(mBinding.viewpager, titles);
    }

    private void cancelFocus() {
        mBinding.edSearch.setFocusable(true);
        mBinding.edSearch.setFocusableInTouchMode(true);
        mBinding.edSearch.requestFocus();
        //失去焦点
        mBinding.edSearch.clearFocus();
    }

}

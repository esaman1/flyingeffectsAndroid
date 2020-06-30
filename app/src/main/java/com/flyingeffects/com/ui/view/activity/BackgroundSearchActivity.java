package com.flyingeffects.com.ui.view.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.main_recycler_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.SearchKeyWord;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.ColorCorrectionManager;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.screenUtil;
import com.flyingeffects.com.view.WarpLinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import rx.Observable;

public class BackgroundSearchActivity extends BaseActivity {


    @BindView(R.id.AutoNewLineLayout)
    WarpLinearLayout autoNewLineLayout;

//    @BindView(R.id.recyclerView)
//    RecyclerView recyclerView;

    @BindView(R.id.ed_search)
    EditText ed_text;

    @BindView(R.id.ll_showResult)
    LinearLayout ll_showResult;

    @BindView(R.id.iv_delete)
    ImageView iv_delete;

    @BindView(R.id.tv_youyou)
    TextView tv_youyou;


    @BindView(R.id.ll_add_child)
    LinearLayout ll_add_child;


//    @BindView(R.id.smart_refresh_layout)
//    SmartRefreshLayout smartRefreshLayout;

    private List<new_fag_template_item> allData = new ArrayList<>();
    private main_recycler_adapter adapter;
    private ArrayList<SearchKeyWord> listSearchKey = new ArrayList<>();
    private ArrayList<TextView> ListForTv = new ArrayList<>();
    private int perPageCount = 10;
    private String nowShowText;


    @Override
    protected int getLayoutId() {
        return R.layout.act_background_search;
    }

    @Override
    protected void initView() {

//        ed_text.setOnClickListener(view -> statisticsEventAffair.getInstance().setFlag(getActivity(), "4_click"));
        //键盘的搜索按钮
        ed_text.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) { //键盘的搜索按钮
                nowShowText = ed_text.getText().toString().trim();
                if (!nowShowText.equals("")) {
//                    selectPage=1;
                    statisticsEventAffair.getInstance().setFlag(BackgroundSearchActivity.this, "4_search", nowShowText);
//                    requestFagData(nowShowText, true);
                    ll_showResult.setVisibility(View.VISIBLE);
                    setResultMargin();
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
                    ll_showResult.setVisibility(View.GONE);
                    iv_delete.setVisibility(View.GONE);
                } else {
                    iv_delete.setVisibility(View.VISIBLE);
                }
//                ed_text.setSelection(s.length());
            }
        });
        iv_delete.setOnClickListener(view -> {
            ed_text.setText("");
            ll_showResult.setVisibility(View.GONE);
        });
        //   showSoftInputFromWindow(ed_text);
    }


    @Override
    protected void initAction() {
//        initRecycler();
//        initSmartRefreshLayout();
    }



    @Override
    public void onResume() {
        super.onResume();
        requestKeywordList();
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    private void setKeyWordList(ArrayList<SearchKeyWord> listSearchKey) {
        ListForTv.clear();
        autoNewLineLayout.removeAllViews();
        for (int i = 0; i < listSearchKey.size(); i++) {
            String nowChooseColor = ColorCorrectionManager.getInstance().getChooseColor(i);
            TextView tv = (TextView) LayoutInflater.from(BackgroundSearchActivity.this).inflate(R.layout.textview_recommend, null);
            tv.setText(listSearchKey.get(i).getName());
            tv.setTextColor(Color.parseColor(nowChooseColor));
            int finalI = i;
            tv.setOnClickListener(view -> {
                if (!DoubleClick.getInstance().isFastDoubleClick()) {
                        if (listSearchKey.size() >= finalI + 1) {
//                            selectPage=1;
                            statisticsEventAffair.getInstance().setFlag(BackgroundSearchActivity.this, "4_recommend", listSearchKey.get(finalI).getName());
                            nowShowText = listSearchKey.get(finalI).getName();
                            ed_text.setText(nowShowText);
//                            requestFagData(nowShowText, true);
                            ll_showResult.setVisibility(View.VISIBLE);
                            setResultMargin();
                        }
                }
            });
            GradientDrawable view_ground = (GradientDrawable) tv.getBackground(); //获取控件的背
            view_ground.setStroke(2, Color.parseColor(nowChooseColor));
            autoNewLineLayout.addView(tv);
            ListForTv.add(tv);
        }

    }


    private void setResultMargin() {
        try {
            int tv_height = tv_youyou.getHeight() + ListForTv.get(0).getHeight() * 2;
            int marginTop = tv_height + screenUtil.dip2px(BackgroundSearchActivity.this, 116);
            int dp20 = screenUtil.dip2px(BackgroundSearchActivity.this, 20);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(dp20, marginTop, dp20, 0);//4个参数按顺序分别是左上右下
            ll_showResult.setLayoutParams(layoutParams);
        } catch (Exception e) {
            ll_showResult.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }


//    private void initRecycler() {
//        adapter = new main_recycler_adapter(R.layout.list_main_item, allData,BackgroundSearchActivity.this, 2);
//        StaggeredGridLayoutManager layoutManager =
//                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setAdapter(adapter);
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                ed_text.clearFocus();
//            }
//        });
//
//        adapter.setOnItemClickListener((adapter, view, position) -> {
//            if (!DoubleClick.getInstance().isFastDoubleClick()) {
//                statisticsEventAffair.getInstance().setFlag(BackgroundSearchActivity.this, "4_search_click", allData.get(position).getTitle());
//                Intent intent = new Intent(BackgroundSearchActivity.this, PreviewActivity.class);
//                intent.putExtra("fromTo", FromToTemplate.ISFROMSEARCH);
//                intent.putExtra("person", allData.get(position));//直接存入被序列化的对象实例
//                startActivity(intent);
//            }
//        });
//    }

    public void showSoftInputFromWindow(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(editText, 0);
    }





    /**
     * 请求友友推荐
     */
    private void requestKeywordList() {
        listSearchKey.clear();
        HashMap<String, String> params = new HashMap<>();
        //2 表示背景
        params.put("template_type","2");
        // 启动时间
        Observable ob = Api.getDefault().keywordList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(BackgroundSearchActivity.this) {
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


//    private void requestFagData(String name, boolean isShowDialog) {
//        HashMap<String, String> params = new HashMap<>();
//        params.put("search", name);
//        params.put("page", selectPage + "");
//        params.put("pageSize", perPageCount + "");
//        Observable ob = Api.getDefault().getTemplate(BaseConstans.getRequestHead(params));
//        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<new_fag_template_item>>(getActivity()) {
//            @Override
//            protected void _onError(String message) {
//                finishData();
//                ToastUtil.showToast(message);
//            }
//
//            @Override
//            protected void _onNext(List<new_fag_template_item> data) {
//                finishData();
//                if (isRefresh) {
//                    allData.clear();
//                }
//
//                if (isRefresh && data.size() == 0) {
//                    ToastUtil.showToast("没有查询到输入内容，换个关键词试试");
//                    statisticsEventAffair.getInstance().setFlag(BackgroundSearchActivity.this, "4_search_none", name);
//                }
//                if (!isRefresh && data.size() < perPageCount) {  //因为可能默认只请求8条数据
//                    ToastUtil.showToast(getResources().getString(R.string.no_more_data));
//                }
//                if (data.size() < perPageCount) {
//                    smartRefreshLayout.setEnableLoadMore(false);
//                }
//                allData.addAll(data);
//                adapter.notifyDataSetChanged();
//
//
//            }
//        }, "FagData", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, isShowDialog);
//    }
//
//    private void finishData() {
//        smartRefreshLayout.finishRefresh();
//        smartRefreshLayout.finishLoadMore();
//    }
//
//
//    private int selectPage = 1;
//    private boolean isRefresh = true;
//
//    public void initSmartRefreshLayout() {
//        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
////            isOnRefresh();
//            isRefresh = true;
//            refreshLayout.setEnableLoadMore(true);
//            selectPage = 1;
//            requestFagData(nowShowText, true);
//        });
//        smartRefreshLayout.setOnLoadMoreListener(refresh -> {
////            isOnLoadMore();
//            isRefresh = false;
//            selectPage++;
//            requestFagData(nowShowText, false);
//        });
//    }

}

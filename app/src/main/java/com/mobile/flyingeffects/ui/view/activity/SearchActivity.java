package com.mobile.flyingeffects.ui.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.flyco.tablayout.SlidingTabLayout;
import com.mobile.flyingeffects.R;
import com.mobile.flyingeffects.adapter.home_vp_frg_adapter;
import com.mobile.flyingeffects.adapter.searchAdapter;
import com.mobile.flyingeffects.base.BaseActivity;
import com.mobile.flyingeffects.enity.hotSearch;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SearchActivity extends BaseActivity {

    @BindView(R.id.ed_text)
    EditText ed_text;


    @BindView(R.id.listView)
    ListView listView;

    @BindView(R.id.ll_showResult)
    LinearLayout ll_showResult;


    @BindView(R.id.tl_tabs)
    SlidingTabLayout tl_tabs;

    @BindView(R.id.viewpager)
    ViewPager viewpager;


    @BindView(R.id.ll_showNoSearch)
    LinearLayout ll_showNoSearch;




    @Override
    protected int getLayoutId() {
        return R.layout.act_search;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initAction() {
        //键盘的搜索按钮
        ed_text.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) { //键盘的搜索按钮
                String text = ed_text.getText().toString().trim();
                if (!text.equals("")) {
                    addCache(text);
//                    presenter.requestSearch(text);
                    hideSoftInputFromWindow(ed_text);
                    showResultPage();
                }
                return true;
            }
            return false;
        });

        showHot();

    }


    public void hideSoftInputFromWindow(EditText editText) {
        InputMethodManager inputManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }


    private void addCache(String text) {
        List<String> list = Hawk.get("searchCache");
        if (list != null) {
            removeExcessCache(list, 2);
            list.add(text);
            Hawk.delete("searchCache");
            Hawk.put("searchCache", list); // Returns the result as boolean
        } else {
            List<String> list_new = new ArrayList<>();
            list_new.add(text);
            Hawk.delete("searchCache");
            Hawk.put("searchCache", list_new); // Returns the result as boolean
        }
    }




    private void removeExcessCache(List<String> list_new, int showItemCount) {
        if (list_new.size() > showItemCount) {
            for (int i = 0; i < list_new.size() - showItemCount; i++) {
                list_new.remove(i);
            }
        }
    }




    private void showHot(){

        List<hotSearch> SearchList=new ArrayList<>();

        for (int i=0;i<10;i++){
            hotSearch data=new hotSearch();
            data.setId(i+"");
            data.setSort(i+"");
            data.setTitle("dasdsa");
            SearchList.add(data);
        }


        searchAdapter adapter = new searchAdapter(SearchList, this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {

        });
    }



    private void isShowResultPage(boolean isShow){
        if(isShow){
            ll_showResult.setVisibility(View.VISIBLE);
            ll_showNoSearch.setVisibility(View.GONE);
        }else{
            ll_showResult.setVisibility(View.GONE);
            ll_showNoSearch.setVisibility(View.VISIBLE);
        }

    }


    /**
     * 显示搜索结果页面
     */
    private void showResultPage(){
        isShowResultPage(true);
        ArrayList<Fragment> list = new ArrayList<>();
        FragmentManager manager = getSupportFragmentManager();
        String[] titles ={"长片","短片"};
        for (int i = 0; i < titles.length; i++) {
//            Bundle bundle = new Bundle();
////                bundle.putSerializable("id", data.get(i).getId());
//            bundle.putSerializable("num", i);
////            titles[i] = data.get(i).getName();
//            frag_searchResult fragment = new frag_searchResult();
//            fragment.setArguments(bundle);
//            list.add(fragment);
        }
        home_vp_frg_adapter adapter = new home_vp_frg_adapter(manager, list);
        viewpager.setAdapter(adapter);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
//                if (i <= data.size() - 1) {
//                        statisticsEventAffair.getInstance().setFlag(getActivity(), "1_tab", titles[i]);
//                        EventBus.getDefault().post(new viewPagerSelected(i));  //消息通知
//                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        tl_tabs.setViewPager(viewpager, titles);

    }





}

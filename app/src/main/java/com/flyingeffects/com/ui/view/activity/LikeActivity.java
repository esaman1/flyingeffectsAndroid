package com.flyingeffects.com.ui.view.activity;

import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.Like_adapter;
import com.flyingeffects.com.adapter.main_recycler_adapter;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.enity.systemessagelist;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * description ：点赞页面
 * creation date: 2020/7/29
 * user : zhangtongju
 */
public class LikeActivity extends BaseActivity {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private Like_adapter adapter;
    private List<systemessagelist> systemessagelists=new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.act_like;
    }

    @Override
    protected void initView() {
        ((TextView) findViewById(R.id.tv_top_title)).setText("赞");
        findViewById(R.id.iv_top_back).setOnClickListener(this);
    }

    @Override
    protected void initAction() {
        systemessagelist AA=new  systemessagelist();
        AA.setContent("1231312");

        systemessagelist AA1=new  systemessagelist();
        AA1.setContent("1231312");


        systemessagelist AA2=new  systemessagelist();
        AA2.setContent("1231312");

        systemessagelist AA3=new  systemessagelist();
        AA3.setContent("1231312");


        systemessagelists.add(AA);
        systemessagelists.add(AA1);
        systemessagelists.add(AA2);
        systemessagelists.add(AA3);

        adapter = new Like_adapter(R.layout.list_like_item, systemessagelists, this);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }







}

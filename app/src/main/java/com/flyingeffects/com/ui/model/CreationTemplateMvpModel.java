package com.flyingeffects.com.ui.model;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.TemplateGridViewAdapter;
import com.flyingeffects.com.adapter.TemplateViewPager;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.ui.interfaces.model.CreationTemplateMvpCallback;

import java.util.ArrayList;
import java.util.List;

import rx.subjects.PublishSubject;


public class CreationTemplateMvpModel {
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private CreationTemplateMvpCallback callback;
    private Context context;
    private List<View>listForInitBottom=new ArrayList<>();

    public CreationTemplateMvpModel(Context context, CreationTemplateMvpCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    public void initBottomLayout(ViewPager viewPager){
        View templateThumbView = LayoutInflater.from(context).inflate(R.layout.view_template_paster, viewPager,false);
        GridView gridView=templateThumbView.findViewById(R.id.gridView);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                callback.ItemClickForStickView();
            }
        });
        List<String>test=new ArrayList<>();
        for(int i=0;i<14;i++){
            test.add("啥");
        }
        TemplateGridViewAdapter gridAdapter=new TemplateGridViewAdapter(test,context);
        gridView.setAdapter(gridAdapter);
        listForInitBottom.add(templateThumbView);
        TemplateViewPager adapter = new TemplateViewPager(listForInitBottom);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }









}

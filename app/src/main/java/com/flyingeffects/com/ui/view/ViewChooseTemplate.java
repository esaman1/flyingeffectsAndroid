package com.flyingeffects.com.ui.view;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.ChooseTemplateAdapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;

public class ViewChooseTemplate {
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private  RecyclerView recyclerView;
    private Context context;
    private   ChooseTemplateAdapter templateThumbAdapter;
    private List<new_fag_template_item>list=new ArrayList<>();

    public ViewChooseTemplate(Context context,View templateThumb) {
        this.context = context;
        initAllView(templateThumb);
        requestPictureAlbumData();
    }



    public void initAllView(View templateThumb){
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView=templateThumb.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        templateThumbAdapter= new ChooseTemplateAdapter(R.layout.item_choose_template, list, context);
        templateThumbAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (!DoubleClick.getInstance().isFastZDYDoubleClick(1000)) {

            }
        });
        recyclerView.setAdapter(templateThumbAdapter);
    }




    public void requestPictureAlbumData(){
        HashMap<String, String> params = new HashMap<>();
        params.put("page",   "1");
        params.put("pageSize",  "10");
        Observable ob = Api.getDefault().photoList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<new_fag_template_item>>(context) {
            @Override
            protected void _onError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(List<new_fag_template_item> data) {
                list.clear();
                list.addAll(data);
                templateThumbAdapter.notifyDataSetChanged();
            }
        }, "FagData", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);


    }




}

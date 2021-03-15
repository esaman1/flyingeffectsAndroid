package com.flyingeffects.com.ui.view;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.ChooseTemplateAdapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.commonlyModel.TemplateDown;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.shixing.sxve.ui.view.WaitingDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

public class ViewChooseTemplate {
    public Callback callback;
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private RecyclerView recyclerView;
    private Context context;
    private ChooseTemplateAdapter templateThumbAdapter;
    private List<new_fag_template_item> list = new ArrayList<>();
    private int changeTemplatePosition;

    public ViewChooseTemplate(Context context, View templateThumb,int changeTemplatePosition, Callback callback) {
        this.context = context;
        this.callback = callback;
        this.changeTemplatePosition=changeTemplatePosition;
        initAllView(templateThumb);
        requestPictureAlbumData();
    }


    public void initAllView(View templateThumb) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView = templateThumb.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        templateThumbAdapter = new ChooseTemplateAdapter(R.layout.item_choose_template, list, context);


        templateThumbAdapter.setOnItemClickListener((adapter, view, position) -> {
            if(!DoubleClick.getInstance().isFastZDYDoubleClick(1000)&&context!=null){
                WaitingDialog.openPragressDialog(context);
                new_fag_template_item items=list.get(position);
                TemplateDown templateDown=new TemplateDown(new TemplateDown.DownFileCallback() {
                    @Override
                    public void isSuccess(String filePath) {
                        Observable.just(filePath).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                WaitingDialog.closeProgressDialog();
                                StatisticsEventAffair.getInstance().setFlag(context, "21_yj_mb_click",list.get(position).getTitle());
                                if(callback!=null){
                                    callback.onItemClick(position,filePath,items);
                                }
                            }
                        });
                    }

                    @Override
                    public void showDownProgress(int progress) {
                    }
                });
                templateDown.prepareDownZip(items.getTemplatefile(), items.getZipid());
            }
        });

        recyclerView.setAdapter(templateThumbAdapter);
    }


    public void requestPictureAlbumData() {
        HashMap<String, String> params = new HashMap<>();
        params.put("page", "1");
        params.put("pageSize", "10");
        Observable ob = Api.getDefault().photoList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<new_fag_template_item>>(context) {
            @Override
            protected void onSubError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void onSubNext(List<new_fag_template_item> data) {
                String test= StringUtil.beanToJSONString(data);
                LogUtil.d("OOM2",test);
                list.clear();
                list.addAll(data);
                new_fag_template_item items=list.get(changeTemplatePosition);
                items.setCheckItem(true);
                templateThumbAdapter.notifyDataSetChanged();
            }
        }, "FagData", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);


    }


    public interface Callback {

        void onItemClick(int position,String  filePath, new_fag_template_item item);

        void isNeedToCutVideo(int position);

    }


}

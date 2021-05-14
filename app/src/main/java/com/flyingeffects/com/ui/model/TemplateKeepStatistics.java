package com.flyingeffects.com.ui.model;

import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.ui.view.activity.PreviewUpAndDownActivity;

import java.util.HashMap;

import rx.Observable;
import rx.subjects.PublishSubject;

public class TemplateKeepStatistics {


    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private static TemplateKeepStatistics thisModel;
    public static TemplateKeepStatistics getInstance() {
        if (thisModel == null) {
            thisModel = new TemplateKeepStatistics();
        }
        return thisModel;
    }




    public void statisticsToSave(String templateId,String title,String templateType) {
        StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "all_save_type_num",templateType);
        HashMap<String, String> params = new HashMap<>();
        params.put("template_id", templateId);
        params.put("action_type", 2 + "");
        // 启动时间
        Observable ob = Api.getDefault().saveTemplate(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(BaseApplication.getInstance()) {
            @Override
            protected void onSubError(String message) {
            }

            @Override
            protected void onSubNext(Object data) {

            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);

    }


}

package com.flyingeffects.com.ui.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.entity.DeleteMessage;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.utils.ToastUtil;

import java.util.HashMap;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.subjects.PublishSubject;


/**
 * description ：评论页面长按事件
 * creation date: 2020/7/1
 * user : zhangtongju
 */

public class MessageLongClickActivity extends Activity {

    private int position;
    private String user_id;
    private String message_id;
    private String templateId;
    private boolean isFirstComment;
    private LinearLayout ll_parent;
    LinearLayout ll_delete;

    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_message_long_click);
        ll_delete = findViewById(R.id.ll_delete);
        ll_parent=findViewById(R.id.ll_parent);
        ll_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        isFirstComment = getIntent().getBooleanExtra("isFirstComment", false);
        user_id = getIntent().getStringExtra("user_id");
        message_id = getIntent().getStringExtra("message_id");
        templateId = getIntent().getStringExtra("templateId");
        position = getIntent().getIntExtra("position", 0);
        if (!user_id.equals(BaseConstans.getUserId())) {
            //不是自己的评论
            ll_delete.setVisibility(View.GONE);
        }


        LinearLayout ll_report = findViewById(R.id.ll_report);
        ll_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtil.showToast("提交成功");
                finish();
            }
        });


        LinearLayout layout_width = findViewById(R.id.ll_delete);
        layout_width.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteComment();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /**
     * description ：删除消息
     * creation date: 2020/8/11
     * user : zhangtongju
     */
    private void deleteComment() {
        HashMap<String, String> params = new HashMap<>();
        params.put("message_id", message_id);
        params.put("template_id", templateId);
        // 启动时间
        Observable ob = Api.getDefault().delComment(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(this) {
            @Override
            protected void onSubError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void onSubNext(Object data) {
                EventBus.getDefault().post(new DeleteMessage(position, isFirstComment));
                finish();
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }


}

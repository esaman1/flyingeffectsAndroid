package com.flyingeffects.com.ui.view.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.Frag_message_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.RequestMessage;
import com.flyingeffects.com.enity.SystemMessageCountAllEntiy;
import com.flyingeffects.com.enity.messageCount;
import com.flyingeffects.com.enity.systemessagelist;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.AdConfigs;
import com.flyingeffects.com.manager.AdManager;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.view.activity.FansActivity;
import com.flyingeffects.com.ui.view.activity.LikeActivity;
import com.flyingeffects.com.ui.view.activity.SystemMessageDetailActivity;
import com.flyingeffects.com.ui.view.activity.ZanActivity;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import rx.Observable;


/**
 * description ：消息页面
 * creation date: 2020/7/28
 * user : zhangtongju
 */
public class frag_message extends BaseFragment {


    @BindView(R.id.swipeMenuListView)
    ListView swipeMenuListView;

    @BindView(R.id.tv_follow)
    TextView tv_follow;

    @BindView(R.id.tv_zan)
    TextView tv_zan;

    @BindView(R.id.tv_comment_count)
    TextView tv_comment_count;


    @BindView(R.id.ll_comment)
    LinearLayout ll_comment;


    @BindView(R.id.ll_ad_content)
    LinearLayout ll_ad_content;


    @Override
    protected int getContentLayout() {
        return R.layout.fag_message;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
            AdManager.getInstance().showImageAd(getActivity(), AdConfigs.AD_IMAGE_message, ll_ad_content, new AdManager.Callback() {
                @Override
                public void adClose() {
                }
            });
        }
    }

    @Override
    protected void initAction() {

    }

    @Override
    protected void initData() {

    }


    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
//            requestSystemMessage();
            if (BaseConstans.hasLogin()) {
                requestMessageCount();
                requestSystemMessageCount();
            } else {
                tv_follow.setVisibility(View.GONE);
                tv_zan.setVisibility(View.GONE);
                tv_comment_count.setVisibility(View.GONE);
            }

        }

    }

//    /**
//     * description ：请求系统消息
//     * creation date: 2020/7/28
//     * user : zhangtongju
//     */
//    private void requestSystemMessage() {
//        Observable ob = Api.getDefault().systemessagelist(BaseConstans.getRequestHead(new HashMap<>()));
//        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<systemessagelist>>(getActivity()) {
//            @Override
//            protected void _onError(String message) {
//                ToastUtil.showToast(message);
//            }
//
//            @Override
//            protected void _onNext(List<systemessagelist> data) {
//                initRecyclerView(data);
//            }
//        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
//    }







    /**
     * description ：请求粉丝数，赞和评论数量
     * creation date: 2020/7/29
     * user : zhangtongju
     */
    private void requestMessageCount() {
        HashMap<String, String> params = new HashMap<>();
        Observable ob = Api.getDefault().getAllMessageNum(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<messageCount>(getActivity()) {
            @Override
            protected void _onError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(messageCount data) {
                showMessageCount(data);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }


    private void requestSystemMessageCount() {
        HashMap<String, String> params = new HashMap<>();
        Observable ob = Api.getDefault().systemTotal(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<SystemMessageCountAllEntiy>(getActivity()) {
            @Override
            protected void _onError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(SystemMessageCountAllEntiy data) {
                String str=StringUtil.beanToJSONString(data);
                LogUtil.d("OOM",str);
                ArrayList<systemessagelist> list=data.getSystem_message();
                initRecyclerView(list);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }




    private void showMessageCount(messageCount data) {
        String follow_num = data.getFollow_num();
        int followNum = Integer.parseInt(follow_num);
        if (followNum == 0) {
            tv_follow.setVisibility(View.GONE);

        } else {
            tv_follow.setVisibility(View.VISIBLE);
            tv_follow.setText(followNum + "");
        }
        String praise_num = data.getPraise_num();
        int praiseNum = Integer.parseInt(praise_num);
        if (praiseNum == 0) {
            tv_zan.setVisibility(View.GONE);
        } else {
            tv_zan.setVisibility(View.VISIBLE);
            tv_zan.setText(praiseNum + "");
        }
        String comment_num = data.getComment_num();
        int commentNum = Integer.parseInt(comment_num);
        if (commentNum == 0) {
            tv_comment_count.setVisibility(View.GONE);
        } else {
            tv_comment_count.setVisibility(View.VISIBLE);
            tv_comment_count.setText(commentNum + "");
        }
    }


    private void initRecyclerView(List<systemessagelist> systemessagelists) {

        Frag_message_adapter adapter = new Frag_message_adapter(systemessagelists, getActivity());
        swipeMenuListView.setAdapter(adapter);
        swipeMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                statisticsEventAffair.getInstance().setFlag(getActivity(), "12_system");
                Intent intent = new Intent(getActivity(), SystemMessageDetailActivity.class);
                intent.putExtra("needId",systemessagelists.get(i).getId());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }


    @OnClick({R.id.iv_icon_fans, R.id.iv_icon_zan, R.id.ll_comment})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.iv_icon_fans:

                if (BaseConstans.hasLogin()) {

                    statisticsEventAffair.getInstance().setFlag(getActivity(), "12_Fans");
                    Intent intentFan = new Intent(getActivity(), FansActivity.class);
                    intentFan.putExtra("to_user_id", BaseConstans.GetUserId());
                    intentFan.putExtra("from", 1);
                    startActivity(intentFan);
                } else {
                    ToastUtil.showToast(getActivity().getResources().getString(R.string.need_login));
                }


                break;

            case R.id.iv_icon_zan:
                if (BaseConstans.hasLogin()) {
                    statisticsEventAffair.getInstance().setFlag(getActivity(), "12_awesome");
                    Intent intentZan = new Intent(getActivity(), ZanActivity.class);
                    intentZan.putExtra("from", 1);
                    startActivity(intentZan);
                } else {
                    ToastUtil.showToast(getActivity().getResources().getString(R.string.need_login));
                }

                break;

            case R.id.ll_comment:
                if (BaseConstans.hasLogin()) {
                    statisticsEventAffair.getInstance().setFlag(getActivity(), "12_comment");
                    Intent intentComment = new Intent(getActivity(), LikeActivity.class);
                    intentComment.putExtra("from", 1);
                    startActivity(intentComment);
                } else {
                    ToastUtil.showToast(getActivity().getResources().getString(R.string.need_login));
                }
                break;

            default:
                break;
        }
    }





    @Subscribe
    public void onEventMainThread(RequestMessage event) {
        if (getActivity() != null) {
            LogUtil.d("OOM","onEventMainThread");
            if (BaseConstans.hasLogin()) {
                requestMessageCount();
                requestSystemMessageCount();
            } else {
                tv_follow.setVisibility(View.GONE);
                tv_zan.setVisibility(View.GONE);
                tv_comment_count.setVisibility(View.GONE);
            }

        }
    }


}

package com.flyingeffects.com.ui.view.fragment;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.Fans_adapter;
import com.flyingeffects.com.adapter.Frag_message_adapter;
import com.flyingeffects.com.adapter.System_message_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.messageCount;
import com.flyingeffects.com.enity.systemessagelist;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.ui.view.activity.FansActivity;
import com.flyingeffects.com.ui.view.activity.LikeActivity;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.ToastUtil;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;

import static com.scwang.smartrefresh.layout.util.SmartUtil.dp2px;


/**
 * description ：消息页面
 * creation date: 2020/7/28
 * user : zhangtongju
 */
public class frag_message extends BaseFragment {


    @BindView(R.id.swipeMenuListView)
    SwipeMenuListView swipeMenuListView;

    @BindView(R.id.tv_follow)
    TextView tv_follow;

    @BindView(R.id.tv_zan)
    TextView tv_zan;

    @BindView(R.id.tv_comment_count)
    TextView tv_comment_count;


    @Override
    protected int getContentLayout() {
        return R.layout.fag_message;
    }

    @Override
    protected void initView() {
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
            requestSystemMessage();
            if (BaseConstans.hasLogin()) {
                requestMessageCount();
            }

        }

    }

    /**
     * description ：请求系统消息
     * creation date: 2020/7/28
     * user : zhangtongju
     */
    private void requestSystemMessage() {
        Observable ob = Api.getDefault().systemessagelist(BaseConstans.getRequestHead(new HashMap<>()));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<systemessagelist>>(getActivity()) {
            @Override
            protected void _onError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(List<systemessagelist> data) {
                initRecyclerView(data);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }


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


    private void showMessageCount(messageCount data) {
        String follow_num = data.getFollow_num();
        int followNum = Integer.parseInt(follow_num);
        if (followNum == 0) {
            tv_follow.setVisibility(View.GONE);

        } else {
            tv_follow.setVisibility(View.VISIBLE);
            tv_follow.setText(followNum);
        }
        String praise_num = data.getPraise_num();
        int praiseNum = Integer.parseInt(praise_num);
        if (praiseNum == 0) {
            tv_zan.setVisibility(View.GONE);
        } else {
            tv_zan.setVisibility(View.VISIBLE);
            tv_zan.setText(praiseNum);
        }
        String comment_num = data.getComment_num();
        int commentNum = Integer.parseInt(comment_num);
        if (commentNum == 0) {
            tv_comment_count.setVisibility(View.GONE);
        } else {
            tv_comment_count.setVisibility(View.VISIBLE);
            tv_comment_count.setText(commentNum);
        }
    }


    private void initRecyclerView(List<systemessagelist> systemessagelists) {


        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getActivity());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.parseColor("#302F2F")));
                // set item width
                openItem.setWidth(dp2px(90));
                // set item title
                openItem.setTitle("标记未读");
                // set item title fontsize


                openItem.setTitleSize(14);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);



                SwipeMenuItem openItem2 = new SwipeMenuItem(
                        getActivity());
                // set item background
                openItem2.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                openItem2.setWidth(dp2px(90));
                // set item title
                openItem2.setTitle("刪除");
                // set item title fontsize
                openItem2.setTitleSize(14);
                // set item title font color
                openItem2.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem2);


//                // create "delete" item
//                SwipeMenuItem deleteItem = new SwipeMenuItem(
//                        getContext());
//                // set item background
//                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
//                        0x3F, 0x25)));
//                // set item width
//                deleteItem.setWidth(dp2px(90));
//                openItem.setTitleSize(18);
//                // set item title font color
//                openItem.setTitleColor(Color.WHITE);
//                deleteItem.setTitle("删除");
//                // set a icon
////                deleteItem.setIcon(R.drawable.ic_delete);
//                // add to menu
//                menu.addMenuItem(deleteItem);
            }
        };

// set creator
        swipeMenuListView.setMenuCreator(creator);
        Frag_message_adapter adapter=new Frag_message_adapter(systemessagelists,getActivity());
        swipeMenuListView.setAdapter(adapter);

//        LinearLayoutManager linearLayoutManager =
//                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerView.setHasFixedSize(true);
//        System_message_adapter adapter = new System_message_adapter(R.layout.item_system_message, systemessagelists, getActivity());
//        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(adapter);
//        ItemTouchHelper itemTouchHelper=new ItemTouchHelper(itemDragAndSwipeCallback);
//        itemTouchHelper.onChildViewAttachedToWindow();
//        itemTouchHelper.attachToRecyclerView(recyclerView);
//        adapter.enableSwipeItem();
//        adapter.setOnItemSwipeListener(new OnItemSwipeListener() {
//            @Override
//            public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int i) {
//
//            }
//
//            @Override
//            public void clearView(RecyclerView.ViewHolder viewHolder, int i) {
//
//            }
//
//            @Override
//            public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int i) {
//
//            }
//
//            @Override
//            public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float v, float v1, boolean b) {
//
//            }
//        });
//        recyclerView.setAdapter(adapter);
    }


    @OnClick({R.id.iv_icon_fans, R.id.iv_icon_zan, R.id.iv_icon_comment})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.iv_icon_fans:
                LogUtil.d("OOM","tv_follow");

                Intent intentFan=new Intent(getActivity(),FansActivity.class);
                startActivity(intentFan);

                break;

            case R.id.iv_icon_zan:
                Intent intentZan=new Intent(getActivity(),LikeActivity.class);
                startActivity(intentZan);

                break;

            case R.id.iv_icon_comment:
                Intent intentComment=new Intent(getActivity(),LikeActivity.class);
                startActivity(intentComment);
                break;

            default:
                break;
        }


    }


}

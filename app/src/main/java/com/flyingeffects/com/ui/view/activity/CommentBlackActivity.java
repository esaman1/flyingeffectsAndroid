package com.flyingeffects.com.ui.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.utils.ScreenUtil;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.Comment_message_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.DeleteMessage;
import com.flyingeffects.com.enity.MessageData;
import com.flyingeffects.com.enity.MessageEnity;
import com.flyingeffects.com.enity.MessageReply;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.utils.record.CommentInputDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import rx.Observable;
import rx.subjects.PublishSubject;


/**
 * description ：评论回复页面
 * creation date: 2020/8/24
 * user : zhangtongju
 */
public class CommentBlackActivity extends Activity {


    /**
     * 顶部向下偏移量
     */
    private RecyclerView recyclerViewComment;
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private TextView no_comment;
    private String nowTemplateId;
    private String templateTitle;
    private String templateType;
    //2级回复id ,如果这个id 不为""，那么表示一级回复，否则表示二级回复
    private String message_id;
    private int lastOpenCommentPosition;
    private Comment_message_adapter adapter;
    private int nowFirstOpenClickPosition;


    private TextView tv_comment_count;

    private ImageView iv_cancle;

    private SmartRefreshLayout smartRefreshLayout;

    private boolean isRefresh = true;

    private ArrayList<MessageEnity> allDataList = new ArrayList<>();

    private int selectPage = 1;
    private int perPageCount = 10;

    private RelativeLayout rela_parent;

    private RelativeLayout rela_content;
    private LinearLayout llComment;

    CommentInputDialog commentInputDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_comment_black);
//        rela_content=findViewById(R.id.rela_content);
//        rela_content.setOnClickListener(view -> finish());
        int height = (int) (ScreenUtil.getScreenHeight(this) * 0.7f);
        rela_parent=findViewById(R.id.rela_parent);
        RelativeLayout.LayoutParams RelativeLayoutParams = (RelativeLayout.LayoutParams) rela_parent.getLayoutParams();
        //如果没有选择下载视频，那么就是自定义视频入口进来，那么默认为绿布
        rela_parent.post(() -> {
            RelativeLayoutParams.height = height;
            rela_parent.setLayoutParams(RelativeLayoutParams);
        });
        EventBus.getDefault().register(this);
        nowTemplateId = getIntent().getStringExtra("templateId");
        templateTitle = getIntent().getStringExtra("templateTitle");
        templateType = getIntent().getStringExtra("templateType");
        recyclerViewComment = findViewById(R.id.recyclerView);
        tv_comment_count = findViewById(R.id.tv_comment_count);
        iv_cancle = findViewById(R.id.iv_cancle);
        iv_cancle.setOnClickListener(view -> CommentBlackActivity.this.finish());
        no_comment = findViewById(R.id.no_comment);
        llComment = findViewById(R.id.ll_comment);
        smartRefreshLayout = findViewById(R.id.smart_refresh_layout_bj);
        llComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Window window = commentInputDialog.getWindow();
                commentInputDialog.show();
                window.getDecorView().setPadding(0, 0, 0, 0); //消除边距
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;   //设置宽度充满屏幕
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(lp);
                window.setGravity(Gravity.BOTTOM);
                llComment.setVisibility(View.GONE);
                commentInputDialog.showSoftInputFromWindow();
            }
        });

        commentInputDialog = new CommentInputDialog(this,nowTemplateId,templateType,templateTitle);
        commentInputDialog.setCommentSuccessListener(new CommentInputDialog.OnCommentSuccessListener() {
            @Override
            public void commentSuccess() {
                requestComment();
            }

            @Override
            public void closeComment() {
                llComment.setVisibility(View.VISIBLE);
            }
        });

        initSmartRefreshLayout();
        initRecyclerView();
        initAction();
    }

    private void initAction() {
        requestComment();
    }

    /**
     * description ：请求评论列表
     * creation date: 2020/7/30
     * user : zhangtongju
     */
    private void requestComment() {
        HashMap<String, String> params = new HashMap<>();
        params.put("template_id", nowTemplateId);
        params.put("page", selectPage + "");
        params.put("pageSize", perPageCount + "");
        // 启动时间
        Observable ob = Api.getDefault().templateComment(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<MessageData>(this) {
            @Override
            protected void _onError(String message) {
                finishData();
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(MessageData data) {
                finishData();
                LogUtil.d("OOM", "评论列表数据" + StringUtil.beanToJSONString(data));
                ArrayList<MessageEnity> dataList = data.getList();
                if (dataList != null) {
                    if (isRefresh && dataList.size() == 0) {
                        showNoData(true, data.getTotal());
                    } else {
                        showNoData(false, data.getTotal());
                    }
                    if (isRefresh) {
                        allDataList.clear();
                    }
                    if (!isRefresh && dataList.size() < perPageCount) {  //因为可能默认只请求8条数据
                        ToastUtil.showToast(getResources().getString(R.string.no_more_data));
                    }
                    if (dataList.size() < perPageCount) {
                        smartRefreshLayout.setEnableLoadMore(false);
                    }
                    allDataList.addAll(dataList);
                    adapter.notifyDataSetChanged();
                }
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }

    public void showNoData(boolean isShowNoData, String total) {
        if (isShowNoData) {
            no_comment.setVisibility(View.VISIBLE);
        } else {
            tv_comment_count.setText(total + "条评论");
            no_comment.setVisibility(View.GONE);
        }
    }

    private void finishData() {
        smartRefreshLayout.finishRefresh();
        smartRefreshLayout.finishLoadMore();
    }

    public void initSmartRefreshLayout() {
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            isRefresh = true;
            refreshLayout.setEnableLoadMore(true);
            selectPage = 1;
            requestComment();
        });
        smartRefreshLayout.setOnLoadMoreListener(refresh -> {
            isRefresh = false;
            selectPage++;
            requestComment();
        });
        smartRefreshLayout.setEnableRefresh(false);
    }


    /**
     * description ：初始化RecyclerView
     * creation date: 2020/7/31
     * user : zhangtongju
     */
    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewComment.setLayoutManager(linearLayoutManager);
        recyclerViewComment.setHasFixedSize(true);
        adapter = new Comment_message_adapter(R.layout.item_comment_preview, allDataList, this, (position, id) -> {
            message_id = id;
        }, position -> {
            //点击了展开更多
            nowFirstOpenClickPosition = position;
            updateDataComment(position);
        }
        );


        adapter.setOnItemChildLongClickListener((adapter, view, position) -> {
            nowFirstOpenClickPosition = position;
            Intent intent = new Intent(CommentBlackActivity.this, MessageLongClickActivity.class);
            intent.putExtra("user_id", allDataList.get(position).getUser_id());
            intent.putExtra("message_id", allDataList.get(position).getId());
            intent.putExtra("templateId", allDataList.get(position).getTemplate_id());
            intent.putExtra("position", position);
            intent.putExtra("isFirstComment", true);
            startActivity(intent);
            return false;
        });

        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.iv_comment_head:
                    //进入到用户主页
                    Intent intent = new Intent(CommentBlackActivity.this, UserHomepageActivity.class);
                    intent.putExtra("toUserId", allDataList.get(position).getUser_id());
                    startActivity(intent);
                    break;


                case R.id.ll_parent:
                    LogUtil.d("OOM", "onItemClick");
                    message_id = allDataList.get(position).getId();
                    commentInputDialog.setMessage_id(message_id);
                    break;


                default:

                    break;
            }
        });
        recyclerViewComment.setAdapter(adapter);
    }

    private void updateDataComment(int position) {
        allDataList.get(position).isOpenComment();
        MessageEnity item1 = allDataList.get(position);
        item1.setOpenComment(true);
        allDataList.set(position, item1);
        if (lastOpenCommentPosition != position) {
            MessageEnity item2 = allDataList.get(lastOpenCommentPosition);
            item2.setOpenComment(false);
            allDataList.set(lastOpenCommentPosition, item2);
            adapter.notifyItemChanged(lastOpenCommentPosition);
        }
        adapter.notifyItemChanged(position);
        lastOpenCommentPosition = position;
    }

    @Subscribe
    public void onEventMainThread(DeleteMessage event) {
        LogUtil.d("OOM", "删除私信");
        int deletePosition = event.getPosition();
        boolean isFirstComment = event.isFirstComment();
        if (allDataList != null) {
            if (isFirstComment && allDataList.size() > deletePosition) {
                allDataList.remove(deletePosition);
            } else {
                MessageEnity messageEnity = allDataList.get(nowFirstOpenClickPosition);
                ArrayList<MessageReply> reply = messageEnity.getReply();
                if (reply != null && reply.size() > deletePosition) {
                    reply.remove(deletePosition);
                }
            }
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.activity_anim_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

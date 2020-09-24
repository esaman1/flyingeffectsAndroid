package com.flyingeffects.com.ui.view.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.Comment_message_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.DeleteMessage;
import com.flyingeffects.com.enity.MessageData;
import com.flyingeffects.com.enity.MessageEnity;
import com.flyingeffects.com.enity.MessageReply;
import com.flyingeffects.com.enity.ReplayMessageEvent;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.utils.KeyboardUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.utils.keyBordUtils;
import com.flyingeffects.com.utils.record.CommentInputDialog;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.green.hand.library.widget.EmojiBoard;
import com.green.hand.library.widget.EmojiEdittext;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import rx.Observable;
import rx.subjects.PublishSubject;

public class BaseFullBottomSheetFragment extends BottomSheetDialogFragment {


    /**
     * 顶部向下偏移量
     */
    private int topOffset = 0;
    private BottomSheetBehavior<FrameLayout> behavior;
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
    CoordinatorLayout coordinator;
    private int nowFirstOpenClickPosition;


    private TextView tv_comment_count;

    private ImageView iv_cancle;

    private SmartRefreshLayout smartRefreshLayout;
    private LinearLayout llComment;

    private boolean isRefresh = true;

    private ArrayList<MessageEnity> allDataList=new ArrayList<>();

    private int selectPage = 1;
    private int perPageCount = 10;
    CommentInputDialog commentInputDialog;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getContext() == null) {
            return super.onCreateDialog(savedInstanceState);
        }
        return new BottomSheetDialog(getContext(), R.style.TransparentBottomSheetStyle);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_fragment, container, false);
        recyclerViewComment = view.findViewById(R.id.recyclerView);
        llComment = view.findViewById(R.id.ll_comment);
        tv_comment_count = view.findViewById(R.id.tv_comment_count);
        iv_cancle = view.findViewById(R.id.iv_cancle);
        iv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CloseDialog();
            }
        });
        EventBus.getDefault().register(this);

        smartRefreshLayout=view.findViewById(R.id.smart_refresh_layout_bj);

        coordinator = view.findViewById(R.id.coordinator);
        initSmartRefreshLayout();
        initRecyclerView();
        no_comment = view.findViewById(R.id.no_comment);
        initAction();

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

        commentInputDialog = new CommentInputDialog(getActivity(),nowTemplateId,templateType,templateTitle);
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

        return view;
    }


    private void initAction(){
        requestComment();
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

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        // 设置软键盘不自动弹出
//        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        FrameLayout bottomSheet = dialog.getDelegate().findViewById(R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomSheet.getLayoutParams();
            layoutParams.height = getHeight();
            behavior = BottomSheetBehavior.from(bottomSheet);
            // 初始为展开状态
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
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
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<MessageData>(getActivity()) {
            @Override
            protected void _onError(String message) {
                finishData();
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(MessageData data) {
                finishData();
                LogUtil.d("OOM", "评论列表数据" + StringUtil.beanToJSONString(data));
                ArrayList<MessageEnity> dataList   = data.getList();
                if(dataList!=null){
                    if (isRefresh && dataList.size() == 0) {
                        showNoData(true,data.getTotal());
                    } else {
                        showNoData(false,data.getTotal());
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


    private void finishData() {
        smartRefreshLayout.finishRefresh();
        smartRefreshLayout.finishLoadMore();
    }


    public void showNoData(boolean isShowNoData,String total) {
        if (isShowNoData) {
            no_comment.setVisibility(View.VISIBLE);
        } else {
            tv_comment_count.setText(total+"条评论");
            no_comment.setVisibility(View.GONE);
        }
    }

    /**
     * description ：初始化RecyclerView
     * creation date: 2020/7/31
     * user : zhangtongju
     */
    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewComment.setLayoutManager(linearLayoutManager);
        recyclerViewComment.setHasFixedSize(true);
        adapter = new Comment_message_adapter(R.layout.item_comment_preview, allDataList, getActivity(), new Comment_message_adapter.CommentOnItemClick() {
            @Override
            public void clickPosition(int position, String id) {
                message_id = id;
                commentInputDialog.setMessage_id(message_id);
            }

        }, new Comment_message_adapter.click2Comment() {
            @Override
            public void click(int position) {
                //点击了展开更多
                nowFirstOpenClickPosition = position;
                updateDataComment(position);
            }
        }
        );


        adapter.setOnItemChildLongClickListener(new BaseQuickAdapter.OnItemChildLongClickListener() {
            @Override
            public boolean onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {
                nowFirstOpenClickPosition = position;
                Intent intent = new Intent(getActivity(), MessageLongClickActivity.class);
                intent.putExtra("user_id", allDataList.get(position).getUser_id());
                intent.putExtra("message_id",allDataList.get(position).getId());
                intent.putExtra("templateId", allDataList.get(position).getTemplate_id());
                intent.putExtra("position", position);
                intent.putExtra("isFirstComment", true);
                startActivity(intent);
                return false;
            }
        });

        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.iv_comment_head:
                        //进入到用户主页
                        Intent intent = new Intent(getActivity(), UserHomepageActivity.class);
                        intent.putExtra("toUserId",allDataList.get(position).getUser_id());
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
            }
        });
        recyclerViewComment.setAdapter(adapter);
    }

    /**
     * description ：关闭弹框
     * creation date: 2020/8/3
     * user : zhangtongju
     */
    public void CloseDialog() {
        if (behavior != null) {
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }


    /**
     * 获取屏幕高度
     *
     * @return height
     */
    private int getHeight() {
        int height = 1920;
        if (getContext() != null) {
            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            Point point = new Point();
            if (wm != null) {
                // 使用Point已经减去了状态栏高度
                wm.getDefaultDisplay().getSize(point);
                height = point.y - getTopOffset();
            }
        }
        return height;
    }

    public int getTopOffset() {
        return topOffset;
    }

    public void setTopOffset(int topOffset) {
        this.topOffset = topOffset;
    }


    public void setNowTemplateId(String nowTemplateId) {
        this.nowTemplateId = nowTemplateId;
        requestComment();
    }

    public void setNowTemplateTitle(String templateTitle) {
        this.templateTitle = templateTitle;
    }

    public void setNowTemplateType(String templateType) {
        this.templateType = templateType;
    }


    public BottomSheetBehavior<FrameLayout> getBehavior() {
        return behavior;
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
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new ReplayMessageEvent());
        EventBus.getDefault().unregister(this);
    }
}

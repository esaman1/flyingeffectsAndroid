package com.flyingeffects.com.ui.view.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.flyingeffects.com.enity.MessageData;
import com.flyingeffects.com.enity.MessageEnity;
import com.flyingeffects.com.enity.TemplateThumbItem;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.HashMap;

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
    private EditText ed_search;
    private TextView no_comment;
    private String nowTemplateId;
    //2级回复id ,如果这个id 不为""，那么表示一级回复，否则表示二级回复
    private String message_id;
    private int lastOpenCommentPosition;

    private ArrayList<MessageEnity> messageEnityList = new ArrayList<>();
    private Comment_message_adapter adapter;

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
        ed_search = view.findViewById(R.id.ed_search);
        ed_search.setOnEditorActionListener((v, actionId, event) -> {
            LogUtil.d("OOM", "setOnEditorActionListener");
            if (actionId == EditorInfo.IME_ACTION_SEND) { //键盘的搜索按钮
                String reply = ed_search.getText().toString().trim();
                if (!reply.equals("")) {
                    if (!TextUtils.isEmpty(message_id)) {
                        replyMessage(reply, "2", message_id);
                    } else {
                        replyMessage(reply, "1", "0");
                    }

                    cancelFocus();
                }
                return true;
            }
            return false;
        });
        ed_search.setOnTouchListener((v, event) -> {
            message_id = "";
            hideShowKeyboard(true);
            return true;
        });


        no_comment = view.findViewById(R.id.no_comment);
        return view;

    }

    private void cancelFocus() {
        if (ed_search != null && ed_search.hasFocus()) {
            ed_search.setText("");
            ed_search.setFocusable(true);
            ed_search.setFocusableInTouchMode(true);
            ed_search.requestFocus();
            ed_search.clearFocus();//失去焦点
        }
    }


    /**
     * description ：回复消息
     * type 1表示一级评论，2 表示二级回复
     * creation date: 2020/7/30
     * user : zhangtongju
     */
    private void replyMessage(String content, String type, String message_id) {
        HashMap<String, String> params = new HashMap<>();
        params.put("template_id", nowTemplateId);
        params.put("content", content);
        params.put("message_id", message_id);
        params.put("type", type);
        // 启动时间
        Observable ob = Api.getDefault().addComment(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(getActivity()) {
            @Override
            protected void _onError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(Object data) {
                String aa = StringUtil.beanToJSONString(data);
                LogUtil.d("OOM", aa);
                cancelFocus();
                hideShowKeyboard(false);
                requestComment();
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }


    @Override
    public void onStart() {
        super.onStart();
        // 设置软键盘不自动弹出
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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
        // 启动时间
        Observable ob = Api.getDefault().templateComment(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<MessageData>(getActivity()) {
            @Override
            protected void _onError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(MessageData data) {
                messageEnityList = data.getList();
                initRecyclerView(data);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }


    /**
     * description ：初始化RecyclerView
     * creation date: 2020/7/31
     * user : zhangtongju
     */

    private void initRecyclerView(MessageData data) {
        if (data.getList() == null || data.getList().size() == 0) {
            no_comment.setVisibility(View.VISIBLE);
        } else {
            no_comment.setVisibility(View.GONE);
            LinearLayoutManager linearLayoutManager =
                    new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            recyclerViewComment.setLayoutManager(linearLayoutManager);
            recyclerViewComment.setHasFixedSize(true);
            adapter = new Comment_message_adapter(R.layout.item_comment_preview, data.getList(), getActivity(), new Comment_message_adapter.CommentOnItemClick() {
                @Override
                public void clickPosition(int position, String id) {
                    hideShowKeyboard(true);
                    message_id = id;
                }
            }, new Comment_message_adapter.click2Comment() {
                @Override
                public void click(int position) {
                    updateDataComment(position);
                }
            });

            adapter.setOnItemLongClickListener((adapter1, view, position) -> {
                Intent MessageLongClickActivity = new Intent(getActivity(), MessageLongClickActivity.class);
                startActivity(MessageLongClickActivity);
                return false;
            });

            adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    LogUtil.d("OOM", "onItemClick");
                    showSoftInputFromWindow(ed_search);
                    ed_search.setHint("@" + data.getList().get(position).getUser_id());
                    message_id = data.getList().get(position).getId();
                }
            });

            recyclerViewComment.setAdapter(adapter);
        }
    }


    /**
     * description ：显示或影藏键盘
     * creation date: 2020/7/31
     * user : zhangtongju
     */
    public void hideShowKeyboard(boolean isOpen) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE); //得到InputMethodManager的实例
        if (isOpen) {
            ed_search.requestFocus();
            imm.showSoftInput(ed_search, InputMethodManager.SHOW_IMPLICIT);
        } else {
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);//关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
        }
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


    public BottomSheetBehavior<FrameLayout> getBehavior() {
        return behavior;
    }


    private void showSoftInputFromWindow(EditText editText) {
        editText.requestFocus();
        hideShowKeyboard(true);
    }


    private void updateDataComment(int position) {
        messageEnityList.get(position).isOpenComment();
        MessageEnity item1 = messageEnityList.get(position);
        item1.setOpenComment(true);
        messageEnityList.set(position, item1);
        MessageEnity item2 = messageEnityList.get(lastOpenCommentPosition);
        item2.setOpenComment(false);
        messageEnityList.set(lastOpenCommentPosition, item2);
        adapter.notifyItemChanged(position);
        adapter.notifyItemChanged(lastOpenCommentPosition);
    }


}

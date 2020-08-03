package com.flyingeffects.com.ui.view.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.Comment_message_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.MessageData;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.HashMap;

import rx.Observable;
import rx.subjects.PublishSubject;

public class BaseFullBottomSheetFragment extends BottomSheetDialogFragment {
    /**
     * 顶部向下偏移量
     */
    private int topOffset = 0;
    private BottomSheetBehavior<FrameLayout> behavior;
    private RecyclerView recyclerViewComment ;
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private EditText ed_search;
    private TextView no_comment;
    private String nowTemplateId;

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
        View view=inflater.inflate(R.layout.bottom_sheet_fragment, container, false);
        recyclerViewComment=view.findViewById(R.id.recyclerView);
        ed_search=view.findViewById(R.id.ed_search);
        no_comment=view.findViewById(R.id.no_comment);
        return  view;

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
                String str= StringUtil.beanToJSONString(data);
                LogUtil.d("OOM","requestComment="+str);
                initRecyclerView(data);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }


    /**
     * description ：
     * creation date: 2020/7/31
     * user : zhangtongju
     */
    private  String message_id;
    private void initRecyclerView(MessageData data) {
        if (data.getList() == null || data.getList().size() == 0) {
            no_comment.setVisibility(View.VISIBLE);
        } else {
            no_comment.setVisibility(View.GONE);
            LinearLayoutManager linearLayoutManager =
                    new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            recyclerViewComment.setLayoutManager(linearLayoutManager);
            recyclerViewComment.setHasFixedSize(true);
            Comment_message_adapter adapter = new Comment_message_adapter(R.layout.item_comment_preview, data.getList(), getActivity(), new Comment_message_adapter.CommentOnItemClick() {
                @Override
                public void clickPosition(int position,String id) {
                    hideShowKeyboard();
                    message_id=id;
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
    public void hideShowKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE); //得到InputMethodManager的实例
        if (imm.isActive()) {//如果开启
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);//关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
        }else{
            ed_search.requestFocus();
            imm.showSoftInput(ed_search, InputMethodManager.SHOW_IMPLICIT);
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


    public void setNowTemplateId(String nowTemplateId){
        this.nowTemplateId=nowTemplateId;
        requestComment();
    }


    public BottomSheetBehavior<FrameLayout> getBehavior() {
        return behavior;
    }
}

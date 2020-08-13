package com.flyingeffects.com.ui.view.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
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
import com.flyingeffects.com.enity.showAdCallback;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.utils.KeyboardUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.utils.keyBordUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.green.hand.library.widget.EmojiBoard;
import com.green.hand.library.widget.EmojiEdittext;

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
    private EmojiEdittext ed_search;
    private TextView no_comment;
    private String nowTemplateId;
    //2级回复id ,如果这个id 不为""，那么表示一级回复，否则表示二级回复
    private String message_id;
    private int lastOpenCommentPosition;
    private ArrayList<MessageEnity> messageEnityList = new ArrayList<>();
    private Comment_message_adapter adapter;
    CoordinatorLayout coordinator;
    private int nowFirstOpenClickPosition;

    //键盘输入框
    private EmojiBoard emojiBoard;

    private TextView tv_sent;

    private TextView tv_comment_count;

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
        ed_search = view.findViewById(R.id.emojicon_edit_text);
        tv_comment_count=view.findViewById(R.id.tv_comment_count);

        EventBus.getDefault().register(this);
        ed_search.setOnTouchListener((v, event) -> {
            KeyboardUtil.showInputKeyboard(getActivity(), ed_search);
            hideEmoJiBoard();
            return false;
        });
        ImageView iv_show_emoj = view.findViewById(R.id.iv_show_emoj);
        emojiBoard = view.findViewById(R.id.input_emoji_board);
        tv_sent = view.findViewById(R.id.tv_sent);
        //表情框点击事件
        emojiBoard.setItemClickListener(code -> {
            if (code.equals("/DEL")) {//删除图标
                ed_search.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
            } else {//插入表情
                ed_search.getText().insert(ed_search.getSelectionStart(), code);
            }
        });
        coordinator = view.findViewById(R.id.coordinator);
        tv_sent.setOnClickListener(listener);
        iv_show_emoj.setOnClickListener(view1 -> {
            LogUtil.d("OOM", "关闭");
            keyBordUtils.HideKeyboard(view);
            showEmojiBoard();
        });
        no_comment = view.findViewById(R.id.no_comment);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
//        requestComment();
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_sent:

                    if(BaseConstans.hasLogin()){
                        String reply = ed_search.getText().toString().trim();
                        if (!reply.equals("")) {
                            if (!TextUtils.isEmpty(message_id)) {
                                replyMessage(reply, "2", message_id);
                            } else {
                                replyMessage(reply, "1", "0");
                            }
                            cancelFocus();
                        }
                    }else{
                        ToastUtil.showToast("请先登录");
                    }


                    break;


                default:
                    break;
            }
        }
    };


    /**
     * description ：去掉焦点
     * creation date: 2020/8/7
     * user : zhangtongju
     */
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
                LogUtil.d("OOM","评论列表数据"+StringUtil.beanToJSONString(data));
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
            adapter = new Comment_message_adapter(R.layout.item_comment_preview, messageEnityList, getActivity(), new Comment_message_adapter.CommentOnItemClick() {
                @Override
                public void clickPosition(int position, String id) {
                    hideShowKeyboard(true);
                    message_id = id;
                }

            }, new Comment_message_adapter.click2Comment() {
                @Override
                public void click(int position) {
                    //点击了展开更多
                    nowFirstOpenClickPosition=position;
                    updateDataComment(position);
                }
            });


            adapter.setOnItemChildLongClickListener(new BaseQuickAdapter.OnItemChildLongClickListener() {
                @Override
                public boolean onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {
                    nowFirstOpenClickPosition=position;
                    Intent intent = new Intent(getActivity(), MessageLongClickActivity.class);
                    intent.putExtra("user_id", data.getList().get(position).getUser_id());
                    intent.putExtra("message_id", data.getList().get(position).getId());
                    intent.putExtra("templateId", data.getList().get(position).getTemplate_id());
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
                            intent.putExtra("toUserId", data.getList().get(position).getUser_id());
                            startActivity(intent);
                            break;


                        case R.id.ll_parent:
                            LogUtil.d("OOM", "onItemClick");
                            showSoftInputFromWindow(ed_search);
                            ed_search.setHint("@" + data.getList().get(position).getUser_id());
                            message_id = data.getList().get(position).getId();
                            break;


                        default:

                            break;
                    }
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
        if (isOpen) {
            keyBordUtils.showSoftInput(getActivity(), ed_search);
        } else {
            keyBordUtils.closeKeybord(Objects.requireNonNull(getActivity()));
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
        adapter.notifyItemChanged(lastOpenCommentPosition);
        adapter.notifyItemChanged(position);
        lastOpenCommentPosition = position;
    }





    /**
     * 展开or隐藏表情框
     */
    public void showEmojiBoard() {
        ed_search.setSelected(emojiBoard.getVisibility() == View.GONE);//设置图片选中效果
        emojiBoard.showBoard();//是否显示表情框
    }


    public void hideEmoJiBoard() {
        emojiBoard.setVisibility(View.GONE);
    }


    @Subscribe
    public void onEventMainThread(DeleteMessage event) {
        LogUtil.d("OOM","删除私信");
        int deletePosition = event.getPosition();
        boolean isFirstComment=event.isFirstComment();
        if(messageEnityList!=null){
            if(isFirstComment&&messageEnityList.size()>deletePosition){
                messageEnityList.remove(deletePosition);
            }else{
                MessageEnity messageEnity=messageEnityList.get(nowFirstOpenClickPosition);
                ArrayList<MessageReply> reply=messageEnity.getReply();
                if(reply!=null&&reply.size()>deletePosition){
                    reply.remove(deletePosition);
                }
            }
            adapter.notifyDataSetChanged();
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

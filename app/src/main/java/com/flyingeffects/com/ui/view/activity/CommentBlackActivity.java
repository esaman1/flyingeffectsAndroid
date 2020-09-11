package com.flyingeffects.com.ui.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.utils.KeyboardUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.utils.keyBordUtils;
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
    private EmojiEdittext ed_search;
    private TextView no_comment;
    private String nowTemplateId;
    private String templateTitle;
    private String templateType;
    //2级回复id ,如果这个id 不为""，那么表示一级回复，否则表示二级回复
    private String message_id;
    private int lastOpenCommentPosition;
    private Comment_message_adapter adapter;
    private int nowFirstOpenClickPosition;

    //键盘输入框
    private EmojiBoard emojiBoard;

    private TextView tv_sent;

    private TextView tv_comment_count;

    private ImageView iv_cancle;

    private SmartRefreshLayout smartRefreshLayout;

    private boolean isRefresh = true;

    private ArrayList<MessageEnity> allDataList = new ArrayList<>();

    private int selectPage = 1;
    private int perPageCount = 10;

    private RelativeLayout rela_parent;

    private RelativeLayout rela_content;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_comment_black);
        rela_content=findViewById(R.id.rela_content);
        rela_content.setOnClickListener(view -> finish());
        int height = ScreenUtil.getScreenHeight(this) / 2;
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
        ed_search = findViewById(R.id.emojicon_edit_text);
        tv_comment_count = findViewById(R.id.tv_comment_count);
        iv_cancle = findViewById(R.id.iv_cancle);
        iv_cancle.setOnClickListener(view -> CommentBlackActivity.this.finish());
        no_comment = findViewById(R.id.no_comment);
        ImageView iv_show_emoj = findViewById(R.id.iv_show_emoj);
        emojiBoard = findViewById(R.id.input_emoji_board);
        tv_sent = findViewById(R.id.tv_sent);
        smartRefreshLayout = findViewById(R.id.smart_refresh_layout_bj);
        ed_search.setOnTouchListener((v, event) -> {
            KeyboardUtil.showInputKeyboard(this, ed_search);
            hideEmoJiBoard();
            return false;
        });


        ed_search.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                ed_search.getWindowVisibleDisplayFrame(r);
                int screenHeight = ed_search.getRootView().getHeight();
                int heightDifference = screenHeight - (r.bottom);
                if (heightDifference > 200) {
                    //软键盘显示
                    LogUtil.e("TAG", "mIsSoftKeyboardShowing 显示");
                } else {
                    //软键盘隐藏
                    ed_search.setHint("有爱评论，说点儿好听的~");
                    message_id="";
                }
            }
        });


        //表情框点击事件
        emojiBoard.setItemClickListener(code -> {
            if (code.equals("/DEL")) {//删除图标
                ed_search.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
            } else {//插入表情
                ed_search.getText().insert(ed_search.getSelectionStart(), code);
            }
        });



        tv_sent.setOnClickListener(listener);
        iv_show_emoj.setOnClickListener(view1 -> {
            LogUtil.d("OOM", "关闭");
            keyBordUtils.closeKeybord(this);
            showEmojiBoard();
        });
        initSmartRefreshLayout();
        initRecyclerView();
        initAction();
    }


    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_sent:

                    if (BaseConstans.hasLogin()) {
                        String reply = ed_search.getText().toString().trim();
                        if (!reply.equals("")) {
                            if (!TextUtils.isEmpty(message_id)) {
                                replyMessage(reply, "2", message_id);
                            } else {
                                replyMessage(reply, "1", "0");
                            }
                            cancelFocus();
                        }
                    } else {
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
        if (type.equals("1")) {
            if (templateType.equals("1")) {
                statisticsEventAffair.getInstance().setFlag(CommentBlackActivity.this, " 12_amount", templateTitle);
            } else {
                statisticsEventAffair.getInstance().setFlag(CommentBlackActivity.this, " 13_amount", templateTitle);
            }
        } else {

            if (templateType.equals("1")) {
                statisticsEventAffair.getInstance().setFlag(CommentBlackActivity.this, " 12_Reply", templateTitle);
            } else {
                statisticsEventAffair.getInstance().setFlag(CommentBlackActivity.this, " 13_Reply", templateTitle);
            }


        }


        HashMap<String, String> params = new HashMap<>();
        params.put("template_id", nowTemplateId);
        params.put("content", content);
        params.put("message_id", message_id);
        params.put("type", type);
        // 启动时间
        Observable ob = Api.getDefault().addComment(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(CommentBlackActivity.this) {
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


    /**
     * 展开or隐藏表情框
     */
    public void showEmojiBoard() {
        ed_search.setSelected(emojiBoard.getVisibility() == View.GONE);//设置图片选中效果
        emojiBoard.showBoard();//是否显示表情框
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


    public void hideEmoJiBoard() {
        emojiBoard.setVisibility(View.GONE);
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
            hideShowKeyboard(true);
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
                    showSoftInputFromWindow(ed_search);
                    ed_search.setHint("@" + allDataList.get(position).getUser_id());
                    message_id = allDataList.get(position).getId();
                    break;


                default:

                    break;
            }
        });
        recyclerViewComment.setAdapter(adapter);
    }


    private void showSoftInputFromWindow(EditText editText) {
        editText.requestFocus();
        hideShowKeyboard(true);
    }


    /**
     * description ：显示或影藏键盘
     * creation date: 2020/7/31
     * user : zhangtongju
     */
    public void hideShowKeyboard(boolean isOpen) {
        if (isOpen) {
            keyBordUtils.showSoftInput(this, ed_search);
        } else {
            keyBordUtils.closeKeybord(Objects.requireNonNull(this));
        }
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
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

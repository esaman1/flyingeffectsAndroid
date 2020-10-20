package com.flyingeffects.com.utils.record;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.constans.BaseConstans;
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

import java.util.HashMap;

import androidx.annotation.NonNull;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * @author ZhouGang
 * @date 2020/9/21
 * 评论输入的对话框
 */
public class CommentInputDialog extends Dialog {
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();

    EmojiEdittext ed_search;
    EmojiBoard emojiBoard;
    ImageView iv_show_emoj;
    TextView tv_sent;


    String nowTemplateId, templateType, templateTitle;
    String message_id;
    Activity activity;
    OnCommentSuccessListener commentSuccessListener;
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

    public CommentInputDialog(@NonNull Activity context, String nowTemplateId, String templateType, String templateTitle) {
        super(context, R.style.style_dialog);
        activity = context;
        this.nowTemplateId = nowTemplateId;
        this.templateType = templateType;
        this.templateTitle = templateTitle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_comment_input);
        ed_search = findViewById(R.id.emojicon_edit_text);
        emojiBoard = findViewById(R.id.input_emoji_board);
        iv_show_emoj = findViewById(R.id.iv_show_emoj);
        tv_sent = findViewById(R.id.tv_sent);
        iv_show_emoj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyBordUtils.closeKeybord(activity);
                showEmojiBoard();
            }
        });
        tv_sent.setOnClickListener(listener);
        ed_search.setOnTouchListener((v, event) -> {
            KeyboardUtil.showInputKeyboard(activity, ed_search);
            hideEmoJiBoard();
            return false;
        });
        emojiBoard.setItemClickListener(code -> {
            if (code.equals("/DEL")) {//删除图标
                ed_search.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
            } else {//插入表情
                ed_search.getText().insert(ed_search.getSelectionStart(), code);
            }
        });
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public void setEdittextHint(String id) {
        if (ed_search != null) {
            ed_search.setHint("@" + id);
        }
    }

    public void showSoftInputFromWindow() {
        ed_search.requestFocus();
        hideShowKeyboard(true);
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
                statisticsEventAffair.getInstance().setFlag(activity, " 12_amount", templateTitle);
            } else {
                statisticsEventAffair.getInstance().setFlag(activity, " 13_amount", templateTitle);
            }
        } else {

            if (templateType.equals("1")) {
                statisticsEventAffair.getInstance().setFlag(activity, " 12_Reply", templateTitle);
            } else {
                statisticsEventAffair.getInstance().setFlag(activity, " 13_Reply", templateTitle);
            }


        }


        HashMap<String, String> params = new HashMap<>();
        params.put("template_id", nowTemplateId);
        params.put("content", content);
        params.put("message_id", message_id);
        params.put("type", type);
        // 启动时间
        Observable ob = Api.getDefault().addComment(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(activity) {
            @Override
            protected void _onError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(Object data) {
                String aa = StringUtil.beanToJSONString(data);
                LogUtil.d("OOM", aa);
                cancelFocus();
                ToastUtil.showToast("评论成功");
                //评论成功后刷新评论列表
                keyBordUtils.HideKeyboard(ed_search);
                if (commentSuccessListener != null) {
                    commentSuccessListener.commentSuccess(Integer.parseInt(type));
                    dismiss();
                }
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }

    /**
     * 展开or隐藏表情框
     */
    public void showEmojiBoard() {
        //设置图片选中效果
        ed_search.setSelected(emojiBoard.getVisibility() == View.GONE);
        //是否显示表情框
        emojiBoard.showBoard();
    }

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
            //失去焦点
            ed_search.clearFocus();
        }
    }

    /**
     * description ：显示或影藏键盘
     * creation date: 2020/7/31
     * user : zhangtongju
     */
    public void hideShowKeyboard(boolean isOpen) {
        if (isOpen) {
            keyBordUtils.showSoftInput(activity, ed_search);
        } else {
            keyBordUtils.closeKeybord(activity);
        }
    }

    public void hideEmoJiBoard() {
        emojiBoard.setVisibility(View.GONE);
    }

    @Override
    public void cancel() {
        keyBordUtils.HideKeyboard(ed_search);
        super.cancel();
        if (commentSuccessListener != null) {
            commentSuccessListener.closeComment();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (commentSuccessListener != null) {
            commentSuccessListener.closeComment();
        }
    }

    public void setCommentSuccessListener(OnCommentSuccessListener commentSuccessListener) {
        this.commentSuccessListener = commentSuccessListener;
    }

    public interface OnCommentSuccessListener {
        /**
         * 评论成功
         */
        void commentSuccess(int type);

        /**
         * 关闭评论弹框
         */
        void closeComment();
    }
}

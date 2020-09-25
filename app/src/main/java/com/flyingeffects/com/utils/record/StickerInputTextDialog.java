package com.flyingeffects.com.utils.record;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.utils.keyBordUtils;

import androidx.annotation.NonNull;

/**
 * @author ZhouGang
 * @date 2020/9/24
 * 输入文字贴纸的dialog
 */
public class StickerInputTextDialog extends Dialog {
    EditText editText;
    TextView tv_complete;
    String inputText;

    OnInputTextListener onInputTextListener;
    Context context;

    public StickerInputTextDialog(@NonNull Context context) {
        super(context, R.style.style_dialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置显示在底部
        getWindow().setGravity(Gravity.BOTTOM);
        setContentView(R.layout.dialog_sticker_input_text);
        editText = findViewById(R.id.edit_text);
        if (!TextUtils.isEmpty(inputText)) {
            editText.setText(inputText);
        }else {
            editText.setText("");
        }
        keyBordUtils.showSoftInput(context,editText);
        tv_complete = findViewById(R.id.tv_complete);
        tv_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onInputTextListener != null) {
                    onInputTextListener.inputText(editText.getText().toString());
                }
                keyBordUtils.HideKeyboard(editText);
            }
        });
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                keyBordUtils.showSoftInput(context,editText);
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (onInputTextListener != null) {
                    onInputTextListener.afterTextChanged(editText.getText().toString());
                }
            }
        });
    }

    public void setInputText(String text) {
        if (editText != null) {
            if (!TextUtils.isEmpty(text)) {
                editText.setText(text);
            } else {
                editText.setText("");
            }
        }
        this.inputText = text;
    }

    public void setOnInputTextListener(OnInputTextListener onInputTextListener) {
        this.onInputTextListener = onInputTextListener;
    }

    @Override
    public void cancel() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        keyBordUtils.HideKeyboard(editText);
    }

    @Override
    public void dismiss() {
        keyBordUtils.HideKeyboard(editText);
        super.dismiss();
    }

    public interface OnInputTextListener {
        void inputText(String inputText);

        void afterTextChanged(String string);
    }
}

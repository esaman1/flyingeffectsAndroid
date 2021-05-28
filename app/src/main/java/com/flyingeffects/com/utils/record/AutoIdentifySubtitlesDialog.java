package com.flyingeffects.com.utils.record;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.flyingeffects.com.R;
import com.flyingeffects.com.utils.screenUtil;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;

/**
 * @author ZhouGang
 * @date 2021/5/25
 * 自动识别字幕的dialog
 */
public class AutoIdentifySubtitlesDialog extends Dialog {
    Activity mActivity;

    public AutoIdentifySubtitlesDialog(@NonNull @NotNull Activity context) {
        super(context, R.style.style_dialog);
        this.mActivity = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_identify_subtitle);
        setCancelable(false);
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        //设置宽度充满屏幕
        lp.width = (int) (screenUtil.getScreenWidth(mActivity) * 0.8);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        window.setGravity(Gravity.CENTER);

        findViewById(R.id.tv_start_identify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSubtitleListener != null) {
                    mSubtitleListener.startIdentifySubtitle();
                }
                dismiss();
            }
        });
        findViewById(R.id.tv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public interface OnIdentifySubtitleListener {

        void startIdentifySubtitle();

    }

    OnIdentifySubtitleListener mSubtitleListener;

    public void setSubtitleListener(OnIdentifySubtitleListener subtitleListener) {
        mSubtitleListener = subtitleListener;
    }
}

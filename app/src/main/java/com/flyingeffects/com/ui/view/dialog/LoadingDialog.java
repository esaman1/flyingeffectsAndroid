package com.flyingeffects.com.ui.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.flyingeffects.com.R;
import com.flyingeffects.com.manager.AdConfigs;
import com.flyingeffects.com.manager.AdManager;
import com.flyingeffects.com.view.LoadingDialogProgress;


public class LoadingDialog extends Dialog implements LifecycleObserver {

    protected LoadingDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected LoadingDialog(@NonNull Context context) {
        this(context, R.style.CustomDialog);
    }

    public static LoadingDialog.Builder getBuilder(Context context) {
        return new LoadingDialog.Builder(context);
    }

    public static class Builder {
        private Context mContext;
        private String mTitle;
        private String mMessage;
        private View mView;
        private boolean mHasAd;

        private DialogDismissListener mDialogDismissListener;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setMessage(String message) {
            mMessage = message;
            return this;
        }

        /**
         * Set the Dialog message from resource
         *
         * @param
         * @return
         */
        public Builder setMessage(int message) {
            mMessage = (String) mContext.getText(message);
            return this;
        }


        /**
         * Set the Dialog title from resource
         *
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            mTitle = (String) mContext.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         *
         * @param title
         * @return
         */
        public Builder setTitle(String title) {
            mTitle = title;
            return this;
        }

        public Builder setHasAd(boolean hasAd) {
            mHasAd = hasAd;
            return this;
        }

        public Builder setContentView(View v) {
            mView = v;
            return this;
        }

        public Builder setContentView(@LayoutRes int layoutInt) {
            mView = LayoutInflater.from(mContext).inflate(layoutInt, null);
            return this;
        }

        public Builder setDialogDismissListener(DialogDismissListener listener) {
            mDialogDismissListener = listener;
            return this;
        }


        public LoadingDialog build() {
            // instantiate the dialog with the custom Theme
            final LoadingDialog dialog = new LoadingDialog(mContext);

            if (mView == null) {
                mView = LayoutInflater.from(mContext)
                        .inflate(R.layout.dialog_loading, null);
            }
            dialog.setContentView(mView);

            AppCompatTextView tvTitle = mView.findViewById(R.id.tv_dialog_title);
            AppCompatTextView tvContent = mView.findViewById(R.id.tv_content_1);
            LinearLayout llContainer = mView.findViewById(R.id.ll_ad_container);

            if (!TextUtils.isEmpty(mTitle)) {
                tvTitle.setText(mTitle);
            } else {
                tvTitle.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(mMessage)) {
                tvContent.setText(mMessage);
            } else {
                tvContent.setVisibility(View.GONE);
            }

            dialog.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (mDialogDismissListener != null) {
                        mDialogDismissListener.onDismiss();
                    }
                    if (llContainer != null) {
                        AdManager.getInstance().imageAdClose(llContainer);
                    }
                }
            });


            dialog.setOnShowListener(new OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    if (mHasAd && llContainer != null) {
                        loadAd(llContainer);
                    }
                }
            });


            dialog.setCancelable(false);

            return dialog;
        }

        private void loadAd(LinearLayout llAdContainer) {
            AdManager.getInstance().showImageAd(mContext, AdConfigs.AD_IMAGE_LOADING_DIALOG, llAdContainer, new AdManager.Callback() {
                @Override
                public void adClose() {

                }
            });
        }

    }

    public void setProgress(int progress) {
        LoadingDialogProgress progressView = findViewById(R.id.loading_progress);
        if (progressView != null) {
            progressView.setProgress(progress);
        }
    }

    public void setTitleStr(String title) {
        AppCompatTextView tvTitle = findViewById(R.id.tv_dialog_title);
        if (tvTitle != null) {
            tvTitle.setText(title);
        }
    }

    public void setContentStr(String content) {
        AppCompatTextView tvContent = findViewById(R.id.tv_content_1);
        if (tvContent != null) {
            tvContent.setText(content);
            if (tvContent.getVisibility() != View.VISIBLE) {
                tvContent.setVisibility(View.VISIBLE);
            }
        }
    }

    public interface DialogDismissListener {
        void onDismiss();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        AdManager.getInstance().imageAdResume();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        AdManager.getInstance().imageAdPause();
    }

}

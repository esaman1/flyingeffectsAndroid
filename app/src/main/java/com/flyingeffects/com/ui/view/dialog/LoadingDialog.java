package com.flyingeffects.com.ui.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.flyingeffects.com.R;


public class LoadingDialog extends Dialog {

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
        private String mPositiveBtnStr;
        private String mCancelBtnStr;
        private View mView;
        private DialogBtnClickListener mDialogBtnClickListener;
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

        public Builder setContentView(View v) {
            mView = v;
            return this;
        }

        public Builder setContentView(@LayoutRes int layoutInt) {
            mView = LayoutInflater.from(mContext).inflate(layoutInt, null);
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         */
        public Builder setPositiveButton(int positiveBtnStr) {
            mPositiveBtnStr = (String) mContext
                    .getText(positiveBtnStr);
            return this;
        }

        public Builder setPositiveButton(String positiveBtnStr) {
            mPositiveBtnStr = positiveBtnStr;
            return this;
        }


        public Builder setNegativeButton(int cancelBtnStr) {
            mCancelBtnStr = (String) mContext
                    .getText(cancelBtnStr);
            return this;
        }

        public Builder setNegativeButton(String cancelBtnStr) {
            mCancelBtnStr = cancelBtnStr;
            return this;
        }

        public Builder setDialogBtnClickListener(DialogBtnClickListener listener) {
            mDialogBtnClickListener = listener;
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
                        .inflate(R.layout.dialog_common_message, null);
            }
            dialog.setContentView(mView);
            if (mTitle == null) {
                mTitle = mContext.getString(R.string.make_sure_exit);
            }
            LinearLayout llAdContainer = mView.findViewById(R.id.ll_ad_container);

            // set the dialog title
            ((TextView) mView.findViewById(R.id.tv_dialog_title)).setText(mTitle);
            // set the confirm button
            if (mPositiveBtnStr == null) {
                mPositiveBtnStr = mContext.getString(R.string.make_sure);
            }

            // set the cancel button
            if (mCancelBtnStr == null) {
                mCancelBtnStr = mContext.getString(R.string.cancel);
            }

            if (!TextUtils.isEmpty(mPositiveBtnStr)) {
                ((TextView) mView.findViewById(R.id.tv_positive_button))
                        .setText(mPositiveBtnStr);
            }

            if (!TextUtils.isEmpty(mCancelBtnStr)) {
                ((TextView) mView.findViewById(R.id.tv_cancel_button))
                        .setText(mCancelBtnStr);
            }


            if (mDialogBtnClickListener != null) {
                mView.findViewById(R.id.tv_positive_button)
                        .setOnClickListener(v -> mDialogBtnClickListener.onPositiveBtnClick(dialog));
                mView.findViewById(R.id.tv_cancel_button)
                        .setOnClickListener(v -> mDialogBtnClickListener.onCancelBtnClick(dialog));
            }

            // set the content message
            if (mMessage == null) {
                mMessage = "已为您复制微信号";
            }

            ((TextView) mView.findViewById(R.id.tv_content_1)).setText(mMessage);


            if (mDialogDismissListener != null) {
                dialog.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mDialogDismissListener.onDismiss();
                    }
                });
            }

            return dialog;
        }

    }

    public interface DialogBtnClickListener {

        void onPositiveBtnClick(LoadingDialog dialog);

        void onCancelBtnClick(LoadingDialog dialog);

    }

    public interface DialogDismissListener {
        void onDismiss();
    }

}

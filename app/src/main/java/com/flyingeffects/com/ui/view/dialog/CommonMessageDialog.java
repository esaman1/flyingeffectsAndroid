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
import androidx.constraintlayout.widget.Group;

import com.flyingeffects.com.R;
import com.flyingeffects.com.manager.AdConfigs;
import com.flyingeffects.com.manager.AdManager;


public class CommonMessageDialog extends Dialog {
    public static final int AD_STATUS_MIDDLE = 1;
    public static final int AD_STATUS_BOTTOM = 2;
    public static final int AD_STATUS_MIDDLE_WITHOUT_CONTENT= 3;
    public static final int AD_STATUS_BOTTOM_WITHOUT_TITLE_AND_CONTENT = 4;
    public static final int AD_STATUS_NONE = 0;


    protected CommonMessageDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected CommonMessageDialog(@NonNull Context context) {
        this(context, R.style.CustomDialog);
    }

    public static CommonMessageDialog.Builder getBuilder(Context context) {
        return new CommonMessageDialog.Builder(context);
    }

    public static class Builder {
        private Context mContext;
        private String mTitle;
        private String mMessage;
        private String mMessage2;
        private String mMessage3;
        private String mPositiveBtnStr;
        private String mCancelBtnStr;
        private View mView;
        private int mAdStatus;
        private DialogBtnClickListener mDialogBtnClickListener;
        private DialogDismissListener mDialogDismissListener;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setMessage(String message) {
            mMessage = message;
            return this;
        }

        public Builder setMessage(int message) {
            mMessage = (String) mContext.getText(message);
            return this;
        }

        public Builder setMessage2(String message) {
            mMessage2 = message;
            return this;
        }

        public Builder setMessage2(int message) {
            mMessage2 = (String) mContext.getText(message);
            return this;
        }


        public Builder setMessage3(String message) {
            mMessage3 = message;
            return this;
        }

        public Builder setMessage3(int message) {
            mMessage3 = (String) mContext.getText(message);
            return this;
        }

        public Builder setAdStatus(int status) {
            mAdStatus = status;
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


        public CommonMessageDialog build() {
            // instantiate the dialog with the custom Theme
            final CommonMessageDialog dialog = new CommonMessageDialog(mContext);

            if (mView == null) {
                mView = LayoutInflater.from(mContext)
                        .inflate(R.layout.dialog_common_message, null);
            }
            dialog.setContentView(mView);

            LinearLayout llAdContainer = mView.findViewById(R.id.ll_ad_container);

            if (mAdStatus != AD_STATUS_NONE) {
                loadAd(llAdContainer);
            }

            if (mAdStatus == AD_STATUS_BOTTOM) {
                Group groupAdDialog = mView.findViewById(R.id.group_ad_dialog);
                mView.findViewById(R.id.iv_dialog_ad_close).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        groupAdDialog.setVisibility(View.GONE);
                    }
                });
                mView.findViewById(R.id.iv_dialog_close).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialogBtnClickListener.onCancelBtnClick(dialog);
                    }
                });
            }

            if (!TextUtils.isEmpty(mTitle)) {
                ((TextView) mView.findViewById(R.id.tv_dialog_title)).setText(mTitle);
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

            if (!TextUtils.isEmpty(mMessage)) {
                ((TextView) mView.findViewById(R.id.tv_content_1)).setText(mMessage);
            }

            if (!TextUtils.isEmpty(mMessage2)) {
                ((TextView) mView.findViewById(R.id.tv_content_2))
                        .setText(mMessage2);
            }

            if (!TextUtils.isEmpty(mMessage3)) {
                ((TextView) mView.findViewById(R.id.tv_content_3))
                        .setText(mMessage3);
            }

            dialog.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (mDialogDismissListener != null) {
                        mDialogDismissListener.onDismiss();
                    }
                    if (llAdContainer != null) {
                        AdManager.getInstance().ImageAdClose(llAdContainer);
                    }
                }
            });


            return dialog;
        }

        private void loadAd(LinearLayout llAdContainer) {
            AdManager.getInstance().showImageAd(mContext, AdConfigs.AD_IMAGE, llAdContainer, new AdManager.Callback() {
                @Override
                public void adClose() {

                }
            });
        }

    }

    public interface DialogBtnClickListener {

        void onPositiveBtnClick(CommonMessageDialog dialog);

        void onCancelBtnClick(CommonMessageDialog dialog);

    }

    public interface DialogDismissListener {
        void onDismiss();
    }

}

package com.shixing.sxve.ui.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shixing.sxve.R;

public class SXProgressDialog extends AppCompatDialogFragment {

    private TextView mProgress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.sxve_progress_dialog);
        setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sxve_progress_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mProgress = view.findViewById(R.id.progress);
    }

    public void setProgress(int progress) {
        if (mProgress.getVisibility() != View.VISIBLE) {
            mProgress.setVisibility(View.VISIBLE);
        }
        mProgress.setText(progress + "%");
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mProgress.setVisibility(View.GONE);
        super.onDismiss(dialog);
    }
}

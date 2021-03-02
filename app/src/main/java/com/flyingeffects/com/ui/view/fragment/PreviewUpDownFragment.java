package com.flyingeffects.com.ui.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.flyingeffects.com.databinding.FragmentUpDownPreviewBinding;

public class PreviewUpDownFragment extends Fragment {

    private FragmentUpDownPreviewBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentUpDownPreviewBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

}

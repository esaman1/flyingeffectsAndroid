package com.flyingeffects.com.ui.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.flyingeffects.com.databinding.FragmentUpDownPreviewBinding;
import com.flyingeffects.com.utils.LogUtil;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

public class PreviewUpDownFragment extends Fragment {
    private static final String TAG = "PreviewUpDownFragment";
    private FragmentUpDownPreviewBinding mBinding;

    public TTNativeExpressAd mAd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentUpDownPreviewBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        //bundle.getSerializable()


    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBinding != null) {
            mBinding.videoItemPlayer.onVideoResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBinding != null) {
            mBinding.videoItemPlayer.onVideoPause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }


}

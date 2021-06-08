package com.flyingeffects.com.ui.view.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.JadePagerAdapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.databinding.FragmentJadeAdjustBinding;
import com.flyingeffects.com.entity.FontColor;
import com.flyingeffects.com.entity.FontEnity;
import com.flyingeffects.com.entity.JadeTypeFace;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.DownloadVideoManage;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.view.mine.CreateViewForAddText;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.imaginstudio.imagetools.pixellab.GradientMaker;
import com.orhanobut.hawk.Hawk;
import com.shixing.sxve.ui.view.WaitingDialog;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * 玉体字调节fragment
 * Created by Try sven775288@gmail.com on 2021/5/26
 */
public class JadeAdjustFragment extends Fragment {

    private static final String TAG = "JadeAdjustFragment";

    private FragmentJadeAdjustBinding binding;

    private String[] stringArray;

    private TabLayout tabLayout;

    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();

    private JadePagerAdapter jadePagerAdapter;

    private int lastPosition;

    private downCallback callback;

    private String mTTFFolder;


    public JadeAdjustFragment(JadeAdjustFragment.onAdjustParamsChangeCallBack onAdjustParamsChangeCallBack) {
        this.onAdjustParamsChangeCallBack = onAdjustParamsChangeCallBack;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.TransBottomSheetDialogStyle);
        FileManager fileManager = new FileManager();
        mTTFFolder = fileManager.getFileCachePath(getActivity(), "fontStyle");
        callback = new downCallback() {
            @Override
            public void isSuccess(String path) {
                if (onAdjustParamsChangeCallBack != null) {
                    onAdjustParamsChangeCallBack.onTypeFaceChange(path);
                }
            }
        };
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_jade_adjust, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        stringArray = getResources().getStringArray(R.array.jade_option);
        initTabLayoutWithViewPager();
        initInputEditText();
        getColors();
        getFonts();
        getJadeTypeFace();
        getLocalJadeTypeFace();
    }

    private void getLocalJadeTypeFace() {
        List<JadeTypeFace> local_jade_type_face = (List<JadeTypeFace>) Hawk.get("local_jade_type_face", null);
        jadePagerAdapter.setLocalJadeTypeFaces(local_jade_type_face);
        jadePagerAdapter.notifyDataSetChanged();
    }

    private void getJadeTypeFace() {
        HashMap<String, String> params3 = new HashMap<>();
        Observable ob3 = Api.getDefault().fontstylelist(BaseConstans.getRequestHead(params3));
        HttpUtil.getInstance().toSubscribe(ob3, new ProgressSubscriber<List<JadeTypeFace>>(getActivity()) {
            @Override
            protected void onSubError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void onSubNext(List<JadeTypeFace> data) {
                jadePagerAdapter.setJadeTypeFaces(data);
                jadePagerAdapter.notifyDataSetChanged();
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }

    private void getFonts() {
        HashMap<String, String> params = new HashMap<>();
        Observable ob = Api.getDefault().fontList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<FontEnity>>(getActivity()) {
            @Override
            protected void onSubError(String message) {

            }

            @Override
            protected void onSubNext(List<FontEnity> data) {
                jadePagerAdapter.setFontEnityList(data);
                jadePagerAdapter.notifyDataSetChanged();
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }


    private void initInputEditText() {
        binding.input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (OnInputChangeCallBack != null) {
                    OnInputChangeCallBack.onChange(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });
    }

    private void initTabLayoutWithViewPager() {

        tabLayout = binding.tlTabsBj;
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                if (view != null) {
                    AppCompatTextView tvTabText = view.findViewById(R.id.tv_tab_item_text);
                    tvTabText.setTextSize(18);
                    tvTabText.setTextColor(Color.parseColor("#ffffff"));
                }
                if (tab.getPosition() == 0) {
                    if (isVisible()) {
                        showSoftInput(binding.input);
                    }
                } else {
                    hideSoftInput();
                    binding.input.clearFocus();
                    lastPosition = tab.getPosition();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                if (view != null) {
                    AppCompatTextView tvTabText = view.findViewById(R.id.tv_tab_item_text);
                    tvTabText.setTextSize(16);
                    tvTabText.setTextColor(Color.parseColor("#797979"));
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        jadePagerAdapter = new JadePagerAdapter(stringArray, getActivity());
        jadePagerAdapter.setOnAdjustParamsChangeCallBack(onAdjustParamsChangeCallBack);
        jadePagerAdapter.setOnTypeFaceClick(new JadePagerAdapter.onTypeFaceClick() {
            @Override
            public void onClick(FontEnity fontEnity) {
                downFile(fontEnity.getFile());
            }
        });
        binding.viewpager.setOffscreenPageLimit(10);
        binding.viewpager.setAdapter(jadePagerAdapter);
        new TabLayoutMediator(binding.tlTabsBj, binding.viewpager, true, true, (tab, position) -> {
            LinearLayout customView = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.item_home_tab, null);
            AppCompatTextView tvTabText = customView.findViewById(R.id.tv_tab_item_text);
            tvTabText.setText(stringArray[position]);
            tvTabText.setTextColor(Color.parseColor("#797979"));
            if (position == 0) {
                tvTabText.setTextSize(18);
                tvTabText.setTextColor(Color.parseColor("#ffffff"));
            }
            tab.setCustomView(customView);
        }).attach();
    }

    @Override
    public void onPause() {
        lifecycleSubject.onNext(ActivityLifeCycleEvent.PAUSE);
        super.onPause();
    }

    @Override
    public void onStop() {
        lifecycleSubject.onNext(ActivityLifeCycleEvent.STOP);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        lifecycleSubject.onNext(ActivityLifeCycleEvent.DESTROY);
//        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    private void showSoftInput(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.input.getWindowToken(), 0);
    }

    public interface OnInputChangeCallBack {
        void onChange(String string);
    }

    private OnInputChangeCallBack OnInputChangeCallBack;

    public JadeAdjustFragment.OnInputChangeCallBack getOnInputChangeCallBack() {
        return OnInputChangeCallBack;
    }

    private onAdjustParamsChangeCallBack onAdjustParamsChangeCallBack;

    public interface onAdjustParamsChangeCallBack {
        void onInnerColorChange(boolean enabled, float radius, float dx, float dy, int color);

        void onEmossChange(boolean enabled, int LightAngle, int Intensity, int Ambient, int Hardness, int Bevel);

        void on3Dchange(int Depth, int DepthDarken, int Quality, boolean StokeInclude, int obliqueAngle, int color);

        void onTextColorChange(int color, int start, int end, boolean isSimple);

        void onTextColorChange(GradientMaker.GradientFill gradientFill);

        void onTypeFaceChange(String path);

        void onJadeTypeFaceChange(JadeTypeFace jadeTypeFace);
    }

    public onAdjustParamsChangeCallBack getOnAdjustParamsChange() {
        return onAdjustParamsChangeCallBack;
    }

    public void setOnAdjustParamsChange(onAdjustParamsChangeCallBack onAdjustParamsChangeCallBack) {
        this.onAdjustParamsChangeCallBack = onAdjustParamsChangeCallBack;
    }

    public void setOnInputChangeCallBack(JadeAdjustFragment.OnInputChangeCallBack onInputChangeCallBack) {
        OnInputChangeCallBack = onInputChangeCallBack;
    }

    public TabLayout getTabLayout() {
        return tabLayout;
    }

    private void getColors() {
        //单色
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "1");
        Observable ob = Api.getDefault().fontColor(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<FontColor>>(getActivity()) {
            @Override
            protected void onSubError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void onSubNext(List<FontColor> data) {
                jadePagerAdapter.setSimpleColors(data);
                jadePagerAdapter.notifyDataSetChanged();
//                jadePagerAdapter.notifyItemChanged(4);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);


        //渐变
        HashMap<String, String> params2 = new HashMap<>();
        params2.put("type", "2");
        Observable ob2 = Api.getDefault().fontColor(BaseConstans.getRequestHead(params2));
        HttpUtil.getInstance().toSubscribe(ob2, new ProgressSubscriber<List<FontColor>>(getActivity()) {
            @Override
            protected void onSubError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void onSubNext(List<FontColor> data) {
                jadePagerAdapter.setGradientColors(data);
                jadePagerAdapter.notifyDataSetChanged();
//                jadePagerAdapter.notifyItemChanged(4);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }

    public int getLastPosition() {
        return lastPosition;
    }

    private void downFile(String path) {
        int index = path.lastIndexOf("/");
        String newStr = path.substring(index);
        String name = mTTFFolder + newStr;
        File file = new File(name);
        if (file.exists()) {
            if (callback != null) {
                callback.isSuccess(name);
            }
        } else {
            WaitingDialog.openPragressDialog(getActivity());
            Observable.just(path).subscribeOn(Schedulers.io()).subscribe(s -> {
                DownloadVideoManage manage = new DownloadVideoManage(isSuccess -> {
                    callback.isSuccess(name);
                    WaitingDialog.closeProgressDialog();
                });
                manage.downloadVideo(path, name);
            });
        }
    }

    public interface downCallback {
        void isSuccess(String path);
    }
}
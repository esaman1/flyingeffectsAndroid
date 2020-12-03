package com.flyingeffects.com.ui.view.fragment;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.enity.SecondaryTypeEntity;
import com.flyingeffects.com.utils.screenUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import butterknife.BindView;

/**
 * @author ZhouGang
 * @date 2020/12/3
 * 二级分类容器的fragment
 */
public class SecondaryTypeFragment extends BaseFragment {
    @BindView(R.id.ll_type)
    LinearLayout mLLType;

    List<SecondaryTypeEntity> mTypeEntities;
    FragmentTransaction transaction;
    ArrayList<Fragment> fragments = new ArrayList<>();
    List<TextView> mTextViews = new ArrayList<>();
    /**
     * 0是模板 1是背景  2是换脸
     */
    int type;
    String category_id;
    int secondaryIndex;

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_secondary_type;
    }

    @Override
    protected void initView() {
        mTypeEntities = (List<SecondaryTypeEntity>) getArguments().getSerializable("secondaryType");
        type = getArguments().getInt("type");
        category_id = getArguments().getString("id");
        secondaryIndex = getArguments().getInt("secondaryIndex");
    }

    @Override
    protected void initAction() {

    }

    @Override
    protected void initData() {
        mLLType.removeAllViews();
        fragments.clear();
        mTextViews.clear();
        transaction = getChildFragmentManager().beginTransaction();
        //把两种状态一次性添加
        int[][] states = new int[][]{new int[]{-android.R.attr.state_selected}, new int[]{android.R.attr.state_selected}};
        //把两种颜色一次性添加
        int[] colors = new int[]{Color.parseColor("#787878"), Color.parseColor("#46AAFF")};
        ColorStateList colorStateList = new ColorStateList(states, colors);
        if (mTypeEntities != null && mTypeEntities.size() > 0) {
            for (int i = 0; i < mTypeEntities.size(); i++) {
                TextView textView = new TextView(getContext());
                textView.setText(mTypeEntities.get(i).getName());
                textView.setTextColor(colorStateList);
                textView.setTextSize(screenUtil.dip2px(getContext(), 4));
                textView.setBackground(getResources().getDrawable(R.drawable.secondary_type_selecrot));
                textView.setGravity(Gravity.CENTER);
                textView.setSelected(false);
                textView.setPadding(screenUtil.dip2px(getContext(), 10), screenUtil.dip2px(getContext(), 3),
                        screenUtil.dip2px(getContext(), 10), screenUtil.dip2px(getContext(), 3));
                textView.setTag(i);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int index = (int) v.getTag();
                        transaction = getChildFragmentManager().beginTransaction();
                        transaction.replace(R.id.fl_container, fragments.get(index));
                        transaction.commitAllowingStateLoss();
                        for (int i = 0; i < mTextViews.size(); i++) {
                            if (i == index) {
                                mTextViews.get(i).setSelected(true);
                            } else {
                                mTextViews.get(i).setSelected(false);
                            }
                        }
                        if (mSelectedListener != null) {
                            mSelectedListener.typeSelected(index);
                        }
                    }
                });
                mLLType.addView(textView);
                mTextViews.add(textView);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
                if (i != mTypeEntities.size() - 1) {
                    layoutParams.setMargins(0, 0, screenUtil.dip2px(getContext(), 20), 0);
                }
                textView.setLayoutParams(layoutParams);
                if (type == 0) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("id", category_id);
                    bundle.putString("tc_id", mTypeEntities.get(i).getId());
                    bundle.putSerializable("num", i);
                    bundle.putSerializable("from", 0);
                    HomeTemplateItemFragment fragment = new HomeTemplateItemFragment();
                    fragment.setArguments(bundle);
                    fragments.add(fragment);
                } else if (type == 1) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("id", category_id);
                    bundle.putString("tc_id", mTypeEntities.get(i).getId());
                    bundle.putSerializable("from", 1);
                    bundle.putSerializable("num", i);
                    fragBjItem fragment = new fragBjItem();
                    fragment.setArguments(bundle);
                    fragments.add(fragment);
                } else if (type == 2) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("id", category_id);
                    bundle.putString("tc_id", mTypeEntities.get(i).getId());
                    bundle.putSerializable("num", i);
                    bundle.putSerializable("from", 4);
                    HomeTemplateItemFragment fragment = new HomeTemplateItemFragment();
                    fragment.setArguments(bundle);
                    fragments.add(fragment);
                }
            }
        }
        if (!fragments.isEmpty() && !mTextViews.isEmpty()) {
            transaction.replace(R.id.fl_container, fragments.get(secondaryIndex));
            transaction.commitAllowingStateLoss();
            mTextViews.get(secondaryIndex).setSelected(true);
        }
    }

    public interface SecondaryTypeSelectedListener {
        /**
         * 选中二级分类的下标
         *
         * @param pos 位置
         */
        void typeSelected(int pos);
    }

    SecondaryTypeSelectedListener mSelectedListener;

    public void setSelectedListener(SecondaryTypeSelectedListener selectedListener) {
        mSelectedListener = selectedListener;
    }
}

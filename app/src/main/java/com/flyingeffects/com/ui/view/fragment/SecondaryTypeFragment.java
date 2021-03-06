package com.flyingeffects.com.ui.view.fragment;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.entity.NewFragmentTemplateItem;
import com.flyingeffects.com.entity.SecondaryTypeEntity;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.utils.screenUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import butterknife.BindView;

/**
 * @author ZhouGang
 * @date 2020/12/3
 * 二级分类容器的fragment
 */
public class SecondaryTypeFragment extends BaseFragment {
    private static final String TAG = "SecondaryTypeFragment";
    public static final String BUNDLE_KEY_SECONDARY_TYPE = "secondaryType";
    public static final String BUNDLE_KEY_CATEGORY_ID = "id";
    public static final String BUNDLE_KEY_TYPE = "type";
    public static final String BUNDLE_KEY_FROM = "from";
    public static final String BUNDLE_KEY_NUM = "num";
    public static final String BUNDLE_KEY_HOME_PAGE_NUM = "homePageNum";
    public static final String BUNDLE_KEY_TEMPLATE_ITEM = "templateItem";
    public static final String BUNDLE_KEY_CATEGORY_TYPE_NAME = "categoryTabName";

    public static final int BUNDLE_VALUE_TYPE_TEMPLATE = 0;
    public static final int BUNDLE_VALUE_TYPE_BACKGROUND = 1;
    public static final int BUNDLE_VALUE_TYPE_FACE = 2;


    @BindView(R.id.ll_type)
    LinearLayout mLLType;

    List<SecondaryTypeEntity> mTypeEntities;
    FragmentTransaction transaction;
    ArrayList<Fragment> fragments = new ArrayList<>();
    List<TextView> mTextViews = new ArrayList<>();
    /**
     * 0是模板 1是背景  2是换脸
     */
    int type, from;

    String category_id, categoryTabName;
    private NewFragmentTemplateItem templateItem;
    private int homePageNum;
    /**
     * 上个页面是哪一个
     */
    private int num;

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_secondary_type;
    }

    @Override
    protected void initView() {
        mTypeEntities = (List<SecondaryTypeEntity>) getArguments().getSerializable(BUNDLE_KEY_SECONDARY_TYPE);
        type = getArguments().getInt(BUNDLE_KEY_TYPE);
        category_id = getArguments().getString(BUNDLE_KEY_CATEGORY_ID);
        from = getArguments().getInt(BUNDLE_KEY_FROM);
        num = getArguments().getInt(BUNDLE_KEY_NUM);
        homePageNum = getArguments().getInt(BUNDLE_KEY_HOME_PAGE_NUM);
        templateItem = (NewFragmentTemplateItem) getArguments().getSerializable(BUNDLE_KEY_TEMPLATE_ITEM);
        categoryTabName = getArguments().getString(BUNDLE_KEY_CATEGORY_TYPE_NAME);

        if (mTypeEntities == null) {
            mTypeEntities = new ArrayList<>();
        }
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
        for (int i = 0; i < mTypeEntities.size(); i++) {

            TextView textView = new TextView(getContext());
            textView.setText(mTypeEntities.get(i).getName());
            textView.setTextColor(colorStateList);
            textView.setTextSize(14);
            textView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.secondary_type_selecrot));
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
                        mTextViews.get(i).setSelected(i == index);
                    }
                    if (type == 0) {
                        StatisticsEventAffair.getInstance().setFlag(getActivity(), "21_mb_sub_tab", categoryTabName + " - " + mTypeEntities.get(index).getName());
                    } else if (type == 1) {
                        StatisticsEventAffair.getInstance().setFlag(getActivity(), "21_bj_sub_tab", categoryTabName + " - " + mTypeEntities.get(index).getName());
                    } else if (type == 2) {
                        StatisticsEventAffair.getInstance().setFlag(getActivity(), "21_fece_sub_tab", categoryTabName + " - " + mTypeEntities.get(index).getName());
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
                bundle.putSerializable("num", num);
                bundle.putSerializable("from", 0);
                bundle.putSerializable("homePageNum", homePageNum);

                HomeTemplateItemFragment fragment = new HomeTemplateItemFragment();
                fragment.setArguments(bundle);
                fragments.add(fragment);
            } else if (type == 1) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("id", category_id);
                bundle.putString("tc_id", mTypeEntities.get(i).getId());
                bundle.putSerializable("from", from);
                bundle.putSerializable("num", num);
                if (templateItem != null) {
                    bundle.putSerializable("cover", templateItem.getImage());
                }
                fragBjItem fragment = new fragBjItem();
                fragment.setArguments(bundle);
                fragments.add(fragment);
            } else if (type == 2) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("id", category_id);
                bundle.putString("tc_id", mTypeEntities.get(i).getId());
                bundle.putSerializable("num", num);
                bundle.putSerializable("from", 4);
                bundle.putSerializable("homePageNum", homePageNum);
                HomeTemplateItemFragment fragment = new HomeTemplateItemFragment();
                fragment.setArguments(bundle);
                fragments.add(fragment);
            }
        }
        if (!fragments.isEmpty() && !mTextViews.isEmpty()) {
            transaction.replace(R.id.fl_container, fragments.get(0));
            transaction.commitAllowingStateLoss();
            mTextViews.get(0).setSelected(true);
        }
    }

    public static Bundle buildArgument(List<SecondaryTypeEntity> typeEntities, int type, String categoryId, int from,
                                       int num, int homePageNum, NewFragmentTemplateItem templateItem, String categoryTabName) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_KEY_SECONDARY_TYPE, (Serializable) typeEntities);
        bundle.putInt(BUNDLE_KEY_TYPE, type);
        bundle.putString(BUNDLE_KEY_CATEGORY_ID, categoryId);
        bundle.putInt(BUNDLE_KEY_FROM, from);
        bundle.putInt(BUNDLE_KEY_NUM, num);
        bundle.putInt(BUNDLE_KEY_HOME_PAGE_NUM, homePageNum);
        bundle.putSerializable(BUNDLE_KEY_TEMPLATE_ITEM, templateItem);
        bundle.putString(BUNDLE_KEY_CATEGORY_TYPE_NAME, categoryTabName);
        return bundle;
    }
}

package com.flyingeffects.com.adapter;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.R;
import com.flyingeffects.com.enity.SearchTemplateInfoEntity;

/**
 * @author ZhouGang
 * @date 2020/10/12
 * 模糊查询模板
 */
public class SearchTemplateItemAdapter extends BaseQuickAdapter<SearchTemplateInfoEntity, BaseViewHolder> {

    String inquireWord = "";
    public SearchTemplateItemAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper,SearchTemplateInfoEntity item) {
        if (!TextUtils.isEmpty(inquireWord) && item.getName().contains(inquireWord)) {
            int index = item.getName().indexOf(inquireWord);
            SpannableString spannableString = new SpannableString(item.getName());
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#5496FF")),
                    index, inquireWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            helper.setText(R.id.tv_content, spannableString);
        } else {
            helper.setText(R.id.tv_content, item.getName());
        }
    }

    /**
     *设置模糊查询关键字
     * @param str
     */
    public void setInquireWordColor(String str) {
        this.inquireWord = str;
        notifyDataSetChanged();
    }
}

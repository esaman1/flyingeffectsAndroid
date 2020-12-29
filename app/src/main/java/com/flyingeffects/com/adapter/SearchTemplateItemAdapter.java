package com.flyingeffects.com.adapter;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.R;
import com.flyingeffects.com.enity.SearchTemplateInfoEntity;
import com.flyingeffects.com.utils.StringUtil;

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
        TextView textView = helper.getView(R.id.tv_content);
        if (helper.getAdapterPosition() == 0) {
            textView.setCompoundDrawables(null, null, null, null);
            helper.setGone(R.id.im_arrow, false);
            textView.setText("搜索\"" + item.getName() + "\"");
            textView.setTextColor(Color.parseColor("#5496FF"));
        } else {
            Drawable drawable = mContext.getDrawable(R.mipmap.gray_search);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            textView.setCompoundDrawablePadding(StringUtil.dip2px(mContext, 10));
            textView.setCompoundDrawables(drawable, null, null, null);
            helper.setGone(R.id.im_arrow, true);
            textView.setTextColor(mContext.getResources().getColor(R.color.white));
            if (!TextUtils.isEmpty(inquireWord) && item.getName().contains(inquireWord)) {
                int index = item.getName().indexOf(inquireWord);
                SpannableString spannableString = new SpannableString(item.getName());
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#5496FF")),
                        index, index + inquireWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(spannableString);
            } else {
                textView.setText(item.getName());
            }
        }

    }

    /**
     *设置模糊查询关键字
     * @param str
     */
    public void setInquireWordColor(String str) {
        this.inquireWord = str;
    }
}

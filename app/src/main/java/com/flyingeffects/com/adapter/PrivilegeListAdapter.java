package com.flyingeffects.com.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.R;
import com.flyingeffects.com.entity.PrivilegeEntity;

import java.util.List;

public class PrivilegeListAdapter extends BaseQuickAdapter<PrivilegeEntity, BaseViewHolder> {

    public PrivilegeListAdapter(@Nullable @org.jetbrains.annotations.Nullable List<PrivilegeEntity> data) {
        super(R.layout.item_privilege_list, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PrivilegeEntity item) {
        helper.setText(R.id.tv_vip_privilege, item.getName())
                .setImageResource(R.id.iv_vip_privilege, item.getResId());
    }
}

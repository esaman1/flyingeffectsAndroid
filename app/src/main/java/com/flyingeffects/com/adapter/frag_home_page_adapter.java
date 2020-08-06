package com.flyingeffects.com.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.R;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.constans.UiStep;
import com.flyingeffects.com.enity.DownVideoPath;
import com.flyingeffects.com.enity.UserFollowEnity;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.manager.AlbumManager;
import com.flyingeffects.com.manager.GlideRoundTransform;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.ui.model.GetPathTypeModel;
import com.flyingeffects.com.ui.view.activity.VideoCropActivity;
import com.flyingeffects.com.ui.view.activity.intoOtherAppActivity;
import com.shixing.sxve.ui.albumType;

import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * user :TongJu  ; email:jutongzhang@sina.com
 * time：2019/1/25
 * describe:首页适配
 **/
public class frag_home_page_adapter extends BaseQuickAdapter<UserFollowEnity, BaseViewHolder> {

    private Context context;
    public final static String TAG = "frag_home_page_adapter";


    public frag_home_page_adapter(int layoutResId, @Nullable List<UserFollowEnity> allData, Context context) {
        super(layoutResId, allData);
        this.context = context;
    }


    @Override
    protected void convert(final BaseViewHolder helper, final UserFollowEnity item) {
        int offset = helper.getLayoutPosition();

    }


}










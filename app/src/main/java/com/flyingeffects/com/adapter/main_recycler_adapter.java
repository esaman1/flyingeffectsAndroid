package com.flyingeffects.com.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.R;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.constans.UiStep;
import com.flyingeffects.com.enity.DownVideoPath;
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
public class main_recycler_adapter extends BaseQuickAdapter<new_fag_template_item, BaseViewHolder> {

    private Context context;
    public final static String TAG = "main_recycler_adapter";
    //0 模板  1 背景 2 搜索/我的收藏 3 表示背景模板下载
    private int fromType;

    public main_recycler_adapter(int layoutResId, @Nullable List<new_fag_template_item> allData, Context context, int fromType) {
        super(layoutResId, allData);
        this.context = context;
        this.fromType = fromType;
    }


    @Override
    protected void convert(final BaseViewHolder helper, final new_fag_template_item item) {
        int offset = helper.getLayoutPosition();
        Glide.with(context)
                .load(item.getImage())
                .apply(RequestOptions.bitmapTransform(new GlideRoundTransform(context, 5)))
            .apply(RequestOptions.placeholderOf(R.mipmap.placeholder))
                .into((ImageView) helper.getView(R.id.iv_cover));
        ImageView iv_show_author = helper.getView(R.id.iv_show_author);




        RelativeLayout ConstraintLayout_addVideo = helper.getView(R.id.ConstraintLayout_addVideo);
        RelativeLayout ll_relative_2 = helper.getView(R.id.ll_relative_2);
        LinearLayout ll_relative_1 = helper.getView(R.id.ll_relative_1);
        RelativeLayout ll_relative_0 = helper.getView(R.id.ll_relative_0);
        TextView tv_name = helper.getView(R.id.tv_name);
        tv_name.setText(item.getTitle());
        if (fromType == 1) {
            if (offset == 1) {
                ll_relative_2.setVisibility(View.GONE);
                ll_relative_1.setVisibility(View.VISIBLE);
                ll_relative_0.setVisibility(View.VISIBLE);
                ConstraintLayout_addVideo.setVisibility(View.VISIBLE);
                helper.setText(R.id.firstline, BaseConstans.configList.getFirstline());
                helper.setText(R.id.secondline, BaseConstans.configList.getSecondline());
                helper.setText(R.id.thirdline, BaseConstans.configList.getThirdline());
                ConstraintLayout_addVideo.setOnClickListener(v -> {
                    Intent intent = new Intent(context, intoOtherAppActivity.class);
                    intent.putExtra("wx", "");
                    intent.putExtra("kuaishou", "");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                });
            } else {
                ConstraintLayout_addVideo.setVisibility(View.GONE);
            }
//            //背景
//            iv_show_author.setVisibility(View.VISIBLE);
//            tv_name.setVisibility(View.GONE);
//            Glide.with(context)
//                    .load(item.getAuth_image())
//                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
//                    .into(iv_show_author);

            helper.setText(R.id.tv_name2,item.getAuth());
            ImageView iv_show_author_template=helper.getView(R.id.iv_show_author_template);
            Glide.with(context)
                    .load(item.getAuth_image())
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(iv_show_author_template);
            iv_show_author.setVisibility(View.GONE);
            helper.setText(R.id.tv_zan_count,item.getPraise());
            tv_name.setVisibility(View.VISIBLE);
            ImageView iv_zan_state=helper.getView(R.id.iv_zan_state);
            iv_zan_state.setImageResource(item.getIs_praise()!=0?R.mipmap.zan_clicked:R.mipmap.zan_unclicked);
        } else if (fromType == 3) {
            //背景下载
            if (offset == 0) {
                ll_relative_2.setVisibility(View.VISIBLE);
                ll_relative_1.setVisibility(View.GONE);
                ll_relative_0.setVisibility(View.GONE);
                ConstraintLayout_addVideo.setVisibility(View.VISIBLE);
                ConstraintLayout_addVideo.setOnClickListener(v -> {
                    AlbumManager.chooseAlbum(context, 1, 1, (tag, paths, isCancel, albumFileList) -> {
                        if(!isCancel){

                            if (UiStep.isFromDownBj) {
                                statisticsEventAffair.getInstance().setFlag(context, "7_local" );
                            }else{
                                statisticsEventAffair.getInstance().setFlag(context, "8_local" );
                            }


                           // EventBus.getDefault().post(new DownVideoPath(paths.get(0)));
                            String pathType = GetPathTypeModel.getInstance().getMediaType(paths.get(0));
                            if (albumType.isImage(pathType)) {
                                EventBus.getDefault().post(new DownVideoPath(paths.get(0)));
                            }else{
                                //如果选择的视频
                                Intent intent = new Intent(context, VideoCropActivity.class);
                                intent.putExtra("videoPath",paths.get(0));
                                intent.putExtra("comeFrom", FromToTemplate.ISFROMEDOWNVIDEOFORUSER);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(intent);
                            }
                        }
                    }, "");
                });
            } else {
                ConstraintLayout_addVideo.setVisibility(View.GONE);
            }
            iv_show_author.setVisibility(View.GONE);
            tv_name.setVisibility(View.VISIBLE);
        } else {
            //模板
            if (offset == 1 && fromType == 0) {
                ll_relative_1.setVisibility(View.VISIBLE);
                ll_relative_0.setVisibility(View.VISIBLE);
                ll_relative_2.setVisibility(View.GONE);
                helper.setText(R.id.firstline, BaseConstans.configList.getFirstline());
                helper.setText(R.id.secondline, BaseConstans.configList.getSecondline());
                helper.setText(R.id.thirdline, BaseConstans.configList.getThirdline());
                ConstraintLayout_addVideo.setVisibility(View.VISIBLE);
                ConstraintLayout_addVideo.setOnClickListener(v -> {
                    Intent intent = new Intent(context, intoOtherAppActivity.class);
                    intent.putExtra("wx", "");
                    intent.putExtra("kuaishou", "");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                });
            } else {
                ConstraintLayout_addVideo.setVisibility(View.GONE);
            }
            helper.setText(R.id.tv_name2,item.getAuth());
            ImageView iv_show_author_template=helper.getView(R.id.iv_show_author_template);
            Glide.with(context)
                    .load(item.getAuth_image())
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(iv_show_author_template);
            iv_show_author.setVisibility(View.GONE);
            helper.setText(R.id.tv_zan_count,item.getPraise());
            tv_name.setVisibility(View.VISIBLE);
            ImageView iv_zan_state=helper.getView(R.id.iv_zan_state);
            iv_zan_state.setImageResource(item.getIs_praise()!=0?R.mipmap.zan_clicked:R.mipmap.zan_unclicked);
        }
    }


}










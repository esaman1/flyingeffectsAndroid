package com.flyingeffects.com.ui.model;

import android.content.Context;

import com.flyingeffects.com.entity.StickerForParents;
import com.flyingeffects.com.view.StickerView;
import com.lansosdk.box.ViewLayerRelativeLayout;





/**
 * description ：这个model 主要还是用于记录位置信息， 然后传递给蓝松图层
 * creation date: 2020/3/13
 * user : zhangtongju
 */
public class AnimStickerModel {


    private StickerView stickerView;
    private Context context;
    private  ViewLayerRelativeLayout viewLayerRelativeLayout;


    public AnimStickerModel(Context context, ViewLayerRelativeLayout viewLayerRelativeLayout,StickerView stickerView){
        this. context= context;
        this.viewLayerRelativeLayout=viewLayerRelativeLayout;
        this.stickerView= stickerView;
    }


    public StickerView getStickerView(){
        return  stickerView;
    }




    /**
     * description ：得到StickerView 在父布局的真实位置参数，最后设置在蓝松的mv 图层中
     * creation date: 2020/3/13
     * user : zhangtongju
     */
    public StickerForParents getParameterData(){
        int stickerViewWidth = stickerView.getMeasuredWidth();
        int viewLayerWidth=viewLayerRelativeLayout.getWidth()/2; //默认宽度是一半，但是这里可能要区分高度
        float realScale=viewLayerWidth/(float)stickerViewWidth;
        float Scale= Math.abs( stickerView.getScale()-1);
        Scale=realScale+Scale;
        float roation = stickerView.getRotateAngle();
        StickerForParents stickerForParents=new StickerForParents();
        stickerForParents.setRoation(roation);
        stickerForParents.setScale(Scale);
        return  stickerForParents;
    }





}

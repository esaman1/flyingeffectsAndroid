package com.flyingeffects.com.ui.model;

import android.content.Context;

import com.flyingeffects.com.enity.StickerForParents;
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
        int stickerViewWidth = stickerView.getWidth();
        int viewLayerWidth=viewLayerRelativeLayout.getWidth();
        float realScale=viewLayerWidth/(float)stickerViewWidth;
        float Scale=stickerView.getScale();
        Scale=realScale+Scale;
        float roation = stickerView.getRotateAngle();
        StickerForParents stickerForParents=new StickerForParents();
        stickerForParents.setRoation(roation);
        stickerForParents.setScale(Scale);
        return  stickerForParents;
    }





}

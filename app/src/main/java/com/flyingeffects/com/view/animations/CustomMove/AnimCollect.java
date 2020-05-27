package com.flyingeffects.com.view.animations.CustomMove;


import com.flyingeffects.com.enity.StickerAnim;
import com.flyingeffects.com.view.StickerView;

import java.util.ArrayList;
import java.util.List;

import static com.flyingeffects.com.view.animations.CustomMove.AnimType.EIGHTBORTHER;
import static com.flyingeffects.com.view.animations.CustomMove.AnimType.LEFTTORIGHT;

/**
 * description ：全部动画集合
 * creation date: 2020/5/25
 * user : zhangtongju
 */
public  class AnimCollect {

    baseAnimModel animModel;



    public int getAnimid(AnimType type) {
        switch (type) {
            case EIGHTBORTHER:
                return 2;
            case LEFTTORIGHT:
                return 1;
            case NULL:
                return 0;
        }
        return 0;
    }



    public int getAnimNeedSubLayerCount(AnimType type) {
        switch (type) {
            case EIGHTBORTHER:
                return 12;
            case LEFTTORIGHT:
                return 2;
        }
        return 0;
    }


    public void startAnimForChooseAnim(AnimType type, StickerView mainStickerView, List<StickerView> subLayer) {

        switch (type) {
            //8个动画飞天效果
            case EIGHTBORTHER:
                animModel=new ItemEightBorther();
                ((ItemEightBorther)animModel).toChangeStickerView(mainStickerView, subLayer);
                break;
            //左进右出
            case LEFTTORIGHT:
                animModel=new ItemRightToLeft();
                ((ItemRightToLeft)animModel).toChangeStickerView(mainStickerView, subLayer,0);
                break;
        }

    }

    public void stopAnim(){
        if(animModel!=null){
            animModel.StopAnim();
        }
    }




    public ArrayList<StickerAnim> getAnimList() {




        ArrayList<StickerAnim> list = new ArrayList<>();
        StickerAnim delected = new StickerAnim();
        delected.setName("删除动画");
        delected.setAnimType(LEFTTORIGHT);
        list.add(delected);

        StickerAnim stickerAnim = new StickerAnim();
        stickerAnim.setName("左到右");
        stickerAnim.setAnimType(LEFTTORIGHT);
        list.add(stickerAnim);
        StickerAnim stickerAnim2 = new StickerAnim();
        stickerAnim2.setName("8兄弟");
        stickerAnim2.setAnimType(EIGHTBORTHER);
        list.add(stickerAnim2);
        return list;
    }


}

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
public class AnimCollect {

    static AnimCollect manager;

    public static AnimCollect getInstance() {
        if (manager == null) {
            manager = new AnimCollect();
        }
        return manager;
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
                ItemEightBorther.getInstance().toChangeStickerView(mainStickerView, subLayer);
                break;
            //左进右出
            case LEFTTORIGHT:
                ItemRightToLeft.getInstance().toChangeStickerView(mainStickerView, subLayer, 0);
                break;
        }

    }


    public ArrayList<StickerAnim> getAnimList() {
        ArrayList<StickerAnim> list = new ArrayList<>();
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

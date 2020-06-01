package com.flyingeffects.com.view.animations.CustomMove;


import com.flyingeffects.com.enity.StickerAnim;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.view.StickerView;
import com.lansosdk.box.Layer;
import com.lansosdk.box.SubLayer;

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

    private baseAnimModel animModel;
    private ArrayList<baseAnimModel> listForBaseAnimMode = new ArrayList<>();

    /**
     * description ：获得动画对应的id
     * creation date: 2020/5/27
     * user : zhangtongju
     */
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


    /**
     * description ：获得动画需要的分身数量
     * creation date: 2020/5/27
     * user : zhangtongju
     */
    public int getAnimNeedSubLayerCount(AnimType type) {
        switch (type) {
            case EIGHTBORTHER:
                return 12;
            case LEFTTORIGHT:
                return 2;
        }
        return 0;
    }


    /**
     * description ：获得动画时长
     * creation date: 2020/5/27
     * user : zhangtongju
     */
    public int getAnimNeedSubLayerTime(AnimType type) {
        switch (type) {
            case EIGHTBORTHER:
                return 10000;
            case LEFTTORIGHT:
                return 3000;
            case BOTTOMTOCENTER:
            case SWINGUPANDDOWN:
                return 2000;
        }
        return 0;
    }


    /**
     * description ：开启全部动画
     * creation date: 2020/5/27
     * user : zhangtongju
     */
    public void startAnimForChooseAnim(AnimType type, StickerView mainStickerView, List<StickerView> subLayer) {
        switch (type) {
            //8个动画飞天效果
            case EIGHTBORTHER:
                LogUtil.d("LEFTTORIGHT", "EIGHTBORTHER-subLayer大小为" + subLayer.size());
                ItemEightBorther itemEightBorther = new ItemEightBorther();
                itemEightBorther.toChangeStickerView(mainStickerView, subLayer);
                listForBaseAnimMode.add(itemEightBorther);
                break;
            //左进右出
            case LEFTTORIGHT:
                ItemRightToLeft itemRightToLeft = new ItemRightToLeft();
                LogUtil.d("LEFTTORIGHT", "LEFTTORIGHT-subLayer大小为" + subLayer.size());
                itemRightToLeft.toChangeStickerView(mainStickerView, subLayer);
                listForBaseAnimMode.add(itemRightToLeft);
                break;

            case BOTTOMTOCENTER:
                ItemBottomToCenter itemBottomToCenter = new ItemBottomToCenter();
                LogUtil.d("LEFTTORIGHT", "LEFTTORIGHT-subLayer大小为" + subLayer.size());
                itemBottomToCenter.toChangeStickerView(mainStickerView, subLayer);
                listForBaseAnimMode.add(itemBottomToCenter);
                break;


            case SWINGUPANDDOWN:
                SwingUpAndDownToCenter swingUpAndDownToCenter = new SwingUpAndDownToCenter();
                LogUtil.d("LEFTTORIGHT", "LEFTTORIGHT-subLayer大小为" + subLayer.size());
                swingUpAndDownToCenter.toChangeStickerView(mainStickerView, subLayer);
                listForBaseAnimMode.add(swingUpAndDownToCenter);
                break;

        }
    }


    /**
     * description ：后台开启全部动画
     * creation date: 2020/5/27
     * user : zhangtongju
     */
    public void startAnimForChooseAnim(AnimType type, Layer mainStickerView, ArrayList<SubLayer> listForSubLayer, LayerAnimCallback callback, float percentage) {
        switch (type) {
            //8个动画飞天效果
            case EIGHTBORTHER:
                if (animModel != null) {
                    ((ItemEightBorther) animModel).getLansongTranslation(callback, percentage, listForSubLayer);
                } else {
                    animModel = new ItemEightBorther();
                    ((ItemEightBorther) animModel).toChangeSubLayer(mainStickerView, listForSubLayer, callback, percentage);
                }
                break;
//            //左进右出
            case LEFTTORIGHT:
                if (animModel != null) {
                    ((ItemRightToLeft) animModel).toChangeSubLayer(callback, percentage);
                } else {
                    animModel = new ItemRightToLeft();
                    ((ItemRightToLeft) animModel).getSubLayerData(mainStickerView, callback, percentage);
                }
                break;
        }
    }


    /**
     * 停止全部动画
     */
    public void stopAnim() {

        if (listForBaseAnimMode != null && listForBaseAnimMode.size() > 0) {
            for (baseAnimModel animModel : listForBaseAnimMode
            ) {
                if (animModel != null) {
                    animModel.StopAnim();
                    animModel = null;
                }
            }
            listForBaseAnimMode.clear();
        }

    }


    /**
     * description ：得到全部动画
     * creation date: 2020/5/27
     * user : zhangtongju
     */

    public ArrayList<StickerAnim> getAnimList() {
        ArrayList<StickerAnim> list = new ArrayList<>();
        StickerAnim delected = new StickerAnim();
        delected.setName("删除动画");
        delected.setAnimType(AnimType.LEFTTORIGHT);
        list.add(delected);
        StickerAnim stickerAnim = new StickerAnim();
        stickerAnim.setName("左到右");
        stickerAnim.setAnimType(AnimType.LEFTTORIGHT);
        list.add(stickerAnim);
        StickerAnim stickerAnim2 = new StickerAnim();
        stickerAnim2.setName("8兄弟");
        stickerAnim2.setAnimType(AnimType.EIGHTBORTHER);
        list.add(stickerAnim2);

        StickerAnim stickerAnim3 = new StickerAnim();
        stickerAnim3.setName("右去上");
        stickerAnim3.setAnimType(AnimType.BOTTOMTOCENTER);
        list.add(stickerAnim3);

        StickerAnim stickerAnim4 = new StickerAnim();
        stickerAnim4.setName("上下摇摆");
        stickerAnim4.setAnimType(AnimType.SWINGUPANDDOWN);
        list.add(stickerAnim4);

        return list;
    }


}

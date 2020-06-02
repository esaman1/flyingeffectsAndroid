package com.flyingeffects.com.view.animations.CustomMove;


import com.flyingeffects.com.enity.StickerAnim;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.view.StickerView;
import com.lansosdk.box.Layer;
import com.lansosdk.box.SubLayer;

import java.util.ArrayList;
import java.util.List;

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
            case SUPERSTAR:
                return 2;
            case BOTTOMTOUP:
            case LEFTANDRIGHTDISSMISS:
                return 1;
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
            case LEFTANDRIGHTDISSMISS:

                return 2000;
            case ROATION:
            case  BOTTOMTOUP:
                return  4000;
            case SUPERSTAR:
                return  2000;

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

            case ROATION:
                Rotate rotate = new Rotate();
                rotate.toChangeStickerView(mainStickerView, subLayer);
                listForBaseAnimMode.add(rotate);
                break;


            case BOTTOMTOUP:
                ItemBottomToTop itemBottomToTop = new ItemBottomToTop();
                itemBottomToTop.toChangeStickerView(mainStickerView, subLayer);
                listForBaseAnimMode.add(itemBottomToTop);
                break;

            case LEFTANDRIGHTDISSMISS:
                ItemLeftAndRightDissmiss itemLeftAndRightDissmiss = new ItemLeftAndRightDissmiss();
                itemLeftAndRightDissmiss.toChangeStickerView(mainStickerView, subLayer);
                listForBaseAnimMode.add(itemLeftAndRightDissmiss);

                break;

            case SUPERSTAR:
                ItemCloned itemCloned =new ItemCloned();
                itemCloned.toChangeStickerView(mainStickerView, subLayer);
                listForBaseAnimMode.add(itemCloned);
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
           //左进右出
            case LEFTTORIGHT:
                if (animModel != null) {
                    ((ItemRightToLeft) animModel).toChangeSubLayer(callback, percentage);
                } else {
                    animModel = new ItemRightToLeft();
                    ((ItemRightToLeft) animModel).getSubLayerData(mainStickerView, callback, percentage);
                }
                break;
            //底部居中位置
            case BOTTOMTOCENTER:
                if (animModel != null) {
                    ((ItemBottomToCenter) animModel).toChangeSubLayer(callback, percentage);
                } else {
                    animModel = new ItemBottomToCenter();
                    ((ItemBottomToCenter) animModel).initToChangeSubLayer(mainStickerView, callback, percentage);
                }
                break;


            case SWINGUPANDDOWN:
                if (animModel != null) {
                    ((SwingUpAndDownToCenter) animModel).toChangeSubLayer(callback, percentage);
                } else {
                    animModel = new SwingUpAndDownToCenter();
                    ((SwingUpAndDownToCenter) animModel).initToChangeSubLayer(mainStickerView, callback, percentage);
                }
                break;

            case ROATION:

                if (animModel != null) {
                    ((Rotate) animModel).toChangeSubLayer(callback, percentage);
                } else {
                    animModel = new Rotate();
                    ((Rotate) animModel).initToChangeSubLayer(mainStickerView, callback, percentage);
                }
                break;


            case BOTTOMTOUP:
                if (animModel != null) {
                    ((ItemBottomToTop) animModel).toChangeSubLayer(callback, percentage);
                } else {
                    animModel = new ItemBottomToTop();
                    ((ItemBottomToTop) animModel).initSubLayerData(mainStickerView, callback, percentage);
                }
                break;

            case LEFTANDRIGHTDISSMISS:
//                ItemLeftAndRightDissmiss itemLeftAndRightDissmiss = new ItemLeftAndRightDissmiss();
//                itemLeftAndRightDissmiss.toChangeStickerView(mainStickerView, subLayer);
//                listForBaseAnimMode.add(itemLeftAndRightDissmiss);

                break;

            case SUPERSTAR:
//                ItemCloned itemCloned =new ItemCloned();
//                itemCloned.toChangeStickerView(mainStickerView, subLayer);
//                listForBaseAnimMode.add(itemCloned);



        }
    }


    /**
     * 停止全部动画
     */
    public void stopAnim() {


        //这里针对保存的时候
        if(animModel!=null){
            animModel.StopAnim();
            animModel=null;
        }

        //这里针对预览页面
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


        StickerAnim stickerAnim5 = new StickerAnim();
        stickerAnim5.setName("旋转动画");
        stickerAnim5.setAnimType(AnimType.ROATION);
        list.add(stickerAnim5);


        StickerAnim stickerAnim6 = new StickerAnim();
        stickerAnim6.setName("底部到頂部");
        stickerAnim6.setAnimType(AnimType.BOTTOMTOUP);
        list.add(stickerAnim6);


        StickerAnim stickerAnim7= new StickerAnim();
        stickerAnim7.setName("左右消失");
        stickerAnim7.setAnimType(AnimType.LEFTANDRIGHTDISSMISS);
        list.add(stickerAnim7);


        StickerAnim stickerAnim8= new StickerAnim();
        stickerAnim8.setName("超级分身");
        stickerAnim8.setAnimType(AnimType.SUPERSTAR);
        list.add(stickerAnim8);

        return list;
    }


}

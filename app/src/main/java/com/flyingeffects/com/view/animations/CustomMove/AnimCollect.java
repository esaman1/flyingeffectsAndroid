package com.flyingeffects.com.view.animations.CustomMove;


import com.flyingeffects.com.R;
import com.flyingeffects.com.enity.StickerAnim;
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


    private ItemEightBorther itemEightBorther;
    private ItemRightToLeft rightToLeft;
    private ItemBottomToCenter itemBottomToCenter;
    //    private SwingUpAndDownToCenter swingUpAndDownToCenter;
    private Rotate rotate;
    private ItemBottomToTop itemBottomToTop;
    private ItemLeftAndRightDissmiss itemLeftAndRightDissmiss;
    private ItemCloned itemCloned;
    private ArrayList<baseAnimModel> listForKeepBaseAnimMode = new ArrayList<>();
    private ArrayList<baseAnimModel> listForBaseAnimMode = new ArrayList<>();

    /**
     * description ：获得动画对应的id
     * creation date: 2020/5/27
     * user : zhangtongju
     */
    public int getAnimid(AnimType type) {
        switch (type) {
            case LEFTTORIGHT:
                return 1;
            case EIGHTBORTHER:
                return 2;
            case BOTTOMTOCENTER:
                return 3;
            case SWINGUPANDDOWN:
                return 4;
            case ROATION:
                return 5;
            case BOTTOMTOUP:
                return 6;
            case LEFTANDRIGHTDISSMISS:
                return 7;
            case SUPERSTAR:
                return 8;


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
            case BOTTOMTOUP:
                return 4000;
            case SUPERSTAR:
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
                ItemEightBorther itemEightBorther = new ItemEightBorther();
                itemEightBorther.toChangeStickerView(mainStickerView, subLayer);
                listForBaseAnimMode.add(itemEightBorther);
                break;
            //左进右出
            case LEFTTORIGHT:
                ItemRightToLeft itemRightToLeft = new ItemRightToLeft();
                itemRightToLeft.toChangeStickerView(mainStickerView, subLayer);
                listForBaseAnimMode.add(itemRightToLeft);
                break;

            case BOTTOMTOCENTER:
                ItemBottomToCenter itemBottomToCenter = new ItemBottomToCenter();
                itemBottomToCenter.toChangeStickerView(mainStickerView, subLayer);
                listForBaseAnimMode.add(itemBottomToCenter);
                break;


            case SWINGUPANDDOWN:
                SwingUpAndDownToCenter swingUpAndDownToCenter = new SwingUpAndDownToCenter();
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
                ItemCloned itemCloned = new ItemCloned();
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
       int  nowMainLayerId;
        switch (type) {
            //8个动画飞天效果
            case EIGHTBORTHER:


                nowMainLayerId = mainStickerView.getId();
                ItemEightBorther itemEightBorther = null;
                for (baseAnimModel model : listForKeepBaseAnimMode
                ) {
                    if (model.LayerId == nowMainLayerId) {
                        itemEightBorther = (ItemEightBorther) model;
                        break;
                    }
                }

                if (itemEightBorther != null) {
                    itemEightBorther.getLansongTranslation(callback, percentage, listForSubLayer);
                } else {
                    itemEightBorther = new ItemEightBorther();
                    itemEightBorther.toChangeSubLayer(mainStickerView, listForSubLayer, callback, percentage);
                    listForKeepBaseAnimMode.add(itemEightBorther);
                }
                break;
            //左进右出
            case LEFTTORIGHT:


                nowMainLayerId = mainStickerView.getId();
                ItemRightToLeft rightToLeft = null;
                for (baseAnimModel model : listForKeepBaseAnimMode
                ) {
                    if (model.LayerId == nowMainLayerId) {
                        rightToLeft = (ItemRightToLeft) model;
                        break;
                    }
                }


                if (rightToLeft != null) {
                    (rightToLeft).toChangeSubLayer(callback, percentage);
                } else {
                    rightToLeft = new ItemRightToLeft();
                    (rightToLeft).getSubLayerData(mainStickerView, callback, percentage);
                    listForKeepBaseAnimMode.add(rightToLeft);
                }
                break;

            //底部居中位置
            case BOTTOMTOCENTER:

                nowMainLayerId = mainStickerView.getId();
                ItemBottomToCenter itemBottomToCenter = null;
                for (baseAnimModel model : listForKeepBaseAnimMode
                ) {
                    if (model.LayerId == nowMainLayerId) {
                        itemBottomToCenter = (ItemBottomToCenter) model;
                        break;
                    }
                }




                if (itemBottomToCenter != null) {
                    (itemBottomToCenter).toChangeSubLayer(callback, percentage);
                } else {
                    itemBottomToCenter = new ItemBottomToCenter();
                    (itemBottomToCenter).initToChangeSubLayer(mainStickerView, callback, percentage);
                    listForKeepBaseAnimMode.add(itemBottomToCenter);
                }
                break;


            case SWINGUPANDDOWN:
                int id = mainStickerView.getId();
                SwingUpAndDownToCenter swingUpAndDownToCenter = null;
                for (baseAnimModel model : listForKeepBaseAnimMode
                ) {
                    if (model.LayerId == id) {
                         swingUpAndDownToCenter = (SwingUpAndDownToCenter) model;
                         break;
                    }
                }
                if (swingUpAndDownToCenter != null) {
                    (swingUpAndDownToCenter).toChangeSubLayer(callback, percentage);
                } else {
                    swingUpAndDownToCenter = new SwingUpAndDownToCenter();
                    (swingUpAndDownToCenter).initToChangeSubLayer(mainStickerView, callback, percentage);
                    listForKeepBaseAnimMode.add(swingUpAndDownToCenter);
                }
                break;

            case ROATION:


                nowMainLayerId = mainStickerView.getId();
                Rotate rotate = null;
                for (baseAnimModel model : listForKeepBaseAnimMode
                ) {
                    if (model.LayerId == nowMainLayerId) {
                        rotate = (Rotate) model;
                        break;
                    }
                }




                if (rotate != null) {
                    (rotate).toChangeSubLayer(callback, percentage);
                } else {
                    rotate = new Rotate();
                    (rotate).initToChangeSubLayer(mainStickerView, callback, percentage);
                    listForKeepBaseAnimMode.add(rotate);
                }
                break;


            case BOTTOMTOUP:


                nowMainLayerId = mainStickerView.getId();
                ItemBottomToTop itemBottomToTop = null;
                for (baseAnimModel model : listForKeepBaseAnimMode
                ) {
                    if (model.LayerId == nowMainLayerId) {
                        itemBottomToTop = (ItemBottomToTop) model;
                        break;
                    }
                }


                if (itemBottomToTop != null) {
                    (itemBottomToTop).toChangeSubLayer(callback, percentage);
                } else {
                    itemBottomToTop = new ItemBottomToTop();
                    (itemBottomToTop).initSubLayerData(mainStickerView, callback, percentage);
                    listForKeepBaseAnimMode.add(itemBottomToTop);
                }
                break;

            case LEFTANDRIGHTDISSMISS:

                nowMainLayerId = mainStickerView.getId();
                ItemLeftAndRightDissmiss itemLeftAndRightDissmiss = null;
                for (baseAnimModel model : listForKeepBaseAnimMode
                ) {
                    if (model.LayerId == nowMainLayerId) {
                        itemLeftAndRightDissmiss = (ItemLeftAndRightDissmiss) model;
                        break;
                    }
                }


                if (itemLeftAndRightDissmiss != null) {
                    (itemLeftAndRightDissmiss).toChangeSubLayer(callback, percentage);
                } else {
                    itemLeftAndRightDissmiss = new ItemLeftAndRightDissmiss();
                    (itemLeftAndRightDissmiss).initToChangeSubLayer(mainStickerView, callback, percentage);
                    listForKeepBaseAnimMode.add(itemLeftAndRightDissmiss);
                }

                break;

            case SUPERSTAR:

                nowMainLayerId = mainStickerView.getId();
                ItemCloned itemCloned = null;
                for (baseAnimModel model : listForKeepBaseAnimMode
                ) {
                    if (model.LayerId == nowMainLayerId) {
                        itemCloned = (ItemCloned) model;
                        break;
                    }
                }

                if (itemCloned != null) {
                    (itemCloned).toChangeSubLayer(callback, percentage);
                } else {
                    itemCloned = new ItemCloned();
                    (itemCloned).initToChangeSubLayer(mainStickerView, callback, percentage);
                    listForKeepBaseAnimMode.add(itemCloned);
                }
        }
    }


    /**
     * 停止全部动画
     */
    public void stopAnim() {


        //这里针对保存页面
        if (listForKeepBaseAnimMode != null && listForKeepBaseAnimMode.size() > 0) {
            for (baseAnimModel animModel : listForKeepBaseAnimMode
            ) {
                if (animModel != null) {
                    animModel.StopAnim();
                    animModel = null;
                }
            }
            listForKeepBaseAnimMode.clear();
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
        stickerAnim.setName("左分身");
        stickerAnim.setIcon(R.mipmap.anim_zdy);
        stickerAnim.setAnimType(AnimType.LEFTTORIGHT);
        list.add(stickerAnim);
        StickerAnim stickerAnim2 = new StickerAnim();
        stickerAnim2.setName("多人旋转");
        stickerAnim2.setIcon(R.mipmap.anim_drxz);
        stickerAnim2.setAnimType(AnimType.EIGHTBORTHER);
        list.add(stickerAnim2);

        StickerAnim stickerAnim3 = new StickerAnim();
        stickerAnim3.setName("从下往上");
        stickerAnim3.setIcon(R.mipmap.anim_cxws);
        stickerAnim3.setAnimType(AnimType.BOTTOMTOCENTER);
        list.add(stickerAnim3);

        StickerAnim stickerAnim4 = new StickerAnim();
        stickerAnim4.setName("上下抖动");
        stickerAnim4.setIcon(R.mipmap.amin_sxdd);
        stickerAnim4.setAnimType(AnimType.SWINGUPANDDOWN);
        list.add(stickerAnim4);


        StickerAnim stickerAnim5 = new StickerAnim();
        stickerAnim5.setName("圆心旋转");
        stickerAnim5.setIcon(R.mipmap.anim_yxxx);
        stickerAnim5.setAnimType(AnimType.ROATION);
        list.add(stickerAnim5);


        StickerAnim stickerAnim6 = new StickerAnim();
        stickerAnim6.setName("飞天分身");
        stickerAnim6.setIcon(R.mipmap.anim_ftfs);
        stickerAnim6.setAnimType(AnimType.BOTTOMTOUP);
        list.add(stickerAnim6);


        StickerAnim stickerAnim7 = new StickerAnim();
        stickerAnim7.setName("左右分身");
        stickerAnim7.setIcon(R.mipmap.anim_zyfs);
        stickerAnim7.setAnimType(AnimType.LEFTANDRIGHTDISSMISS);
        list.add(stickerAnim7);


        StickerAnim stickerAnim8 = new StickerAnim();
        stickerAnim8.setName("一变三");
        stickerAnim8.setIcon(R.mipmap.anim_ybs);
        stickerAnim8.setAnimType(AnimType.SUPERSTAR);
        list.add(stickerAnim8);

        return list;
    }


}

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


    private ArrayList<baseAnimModel> listForKeepBaseAnimMode = new ArrayList<>();
    private ArrayList<baseAnimModel> listForBaseAnimMode = new ArrayList<>();

    /**
     * description ：获得动画对应的id,来自按钮的顺序
     * creation date: 2020/5/27
     * user : zhangtongju
     */
    public int getAnimid(AnimType type) {
        switch (type) {
            case LEFTTORIGHT:
                return 5;
            case EIGHTBORTHER:
                return 8;
            case BOTTOMTOCENTER:
                return 3;
            case SWINGUPANDDOWN:
                return 1;
            case ROATION:
                return 7;
            case BOTTOMTOUP:
                return 4;
            case LEFTANDRIGHTDISSMISS:
                return 2;
            case SUPERSTAR:
                return 6;
            case BOTTOMTOCENTER2:
                return 0;


            case SUPERSTAR2:
                return 9;

            case CIRCLECLONED:
                return 10;
            case CIRCLECLONED2:
                return 11;


            case FIVEPOINTSTART:


                return 12;
            case FIVEPOINTSTART2:
                return 13;

            case Z:
                return 14;
            case FOUNDER:
                return 15;

            case MAKEMOVEONE:
                return 16;

            case LOVE:
                return 17;

            case SUPERLOVE:
                return 18;

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
            case SUPERSTAR2:
                return 2;
            case BOTTOMTOUP:
            case LEFTANDRIGHTDISSMISS:
            case MAKEMOVEONE:
                return 1;
            case CIRCLECLONED:
                return 10;
            case CIRCLECLONED2:
                return 9;

            case FIVEPOINTSTART:
            case FIVEPOINTSTART2:
                return 20;
            case Z:
                return 15;
            case FOUNDER:
                return 36;
            case LOVE:
            case SUPERLOVE:
                return 16;

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
            case FIVEPOINTSTART:
                return 10000;
            case LEFTTORIGHT:
            case LEFTANDRIGHTDISSMISS:
            case FIVEPOINTSTART2:
            case Z:
            case CIRCLECLONED2:
                return 3000;
            case BOTTOMTOCENTER:
            case BOTTOMTOCENTER2:
            case SWINGUPANDDOWN:
                return 2000;
            case ROATION:
            case BOTTOMTOUP:
                return 4000;
            case SUPERSTAR:
            case SUPERSTAR2:
            case LOVE:
                return 2000;
            case FOUNDER:
                return 1000;

            case MAKEMOVEONE:
            case SUPERLOVE:
                return 2000;

            case CIRCLECLONED:
                return 5000;
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


            case BOTTOMTOCENTER2:
                ItemBottomToCenter2 itemBottomToCenter2 = new ItemBottomToCenter2();
                itemBottomToCenter2.toChangeStickerView(mainStickerView, subLayer);
                listForBaseAnimMode.add(itemBottomToCenter2);
                break;

            case SUPERSTAR2:
                ItemCloned2 itemCloned2 = new ItemCloned2();
                itemCloned2.toChangeStickerView(mainStickerView, subLayer);
                listForBaseAnimMode.add(itemCloned2);
                break;


            case CIRCLECLONED:
                CircleCloned circleCloned = new CircleCloned();
                circleCloned.toChangeStickerView(mainStickerView, subLayer);
                listForBaseAnimMode.add(circleCloned);
                break;

            case CIRCLECLONED2:
                CircleCloned2 circleCloned2 = new CircleCloned2();
                circleCloned2.toChangeStickerView(mainStickerView, subLayer);
                listForBaseAnimMode.add(circleCloned2);
                break;

            case FIVEPOINTSTART:
                FivePointStar fivePointStar = new FivePointStar();
                fivePointStar.toChangeStickerView(mainStickerView, subLayer);
                listForBaseAnimMode.add(fivePointStar);
                break;
            case FIVEPOINTSTART2:
                FivePointStar2 fivePointStar2 = new FivePointStar2();
                fivePointStar2.toChangeStickerView(mainStickerView, subLayer);
                listForBaseAnimMode.add(fivePointStar2);
                break;

            case Z:
                ZAnim zAnim = new ZAnim();
                zAnim.toChangeStickerView(mainStickerView, subLayer);
                listForBaseAnimMode.add(zAnim);
                break;


            case FOUNDER:
                FounderAnim founderAnim = new FounderAnim();
                founderAnim.toChangeStickerView(mainStickerView, subLayer);
                listForBaseAnimMode.add(founderAnim);
                break;


            case MAKEMOVEONE:
                MakeMoveOneAnim makeMoveOneAnim = new MakeMoveOneAnim();
                makeMoveOneAnim.toChangeStickerView(mainStickerView, subLayer);
                listForBaseAnimMode.add(makeMoveOneAnim);
                break;


            case LOVE:
                LoveAnim loveAnim = new LoveAnim();
                loveAnim.toChangeStickerView(mainStickerView, subLayer);
                listForBaseAnimMode.add(loveAnim);
                break;


            case SUPERLOVE:
                SuperLoveAnim superLoveAnim = new SuperLoveAnim();
                superLoveAnim.toChangeStickerView(mainStickerView, subLayer);
                listForBaseAnimMode.add(superLoveAnim);

                break;


        }
    }


    /**
     * description ：后台开启全部动画
     * creation date: 2020/5/27
     * user : zhangtongju
     */
    public void startAnimForChooseAnim(AnimType type, Layer mainStickerView, ArrayList<SubLayer> listForSubLayer, LayerAnimCallback callback, float percentage) {
        int nowMainLayerId;
        switch (type) {
            //8个动画飞天效果
            case EIGHTBORTHER:


                nowMainLayerId = mainStickerView.getId();
                ItemEightBorther itemEightBorther = null;
                for (baseAnimModel model : listForKeepBaseAnimMode
                ) {
                    if (model.getLayerId() == nowMainLayerId) {
                        itemEightBorther = (ItemEightBorther) model;
                        break;
                    }
                }

                if (itemEightBorther != null) {
                    itemEightBorther.getLansongTranslation(callback, percentage, listForSubLayer);
                } else {
                    itemEightBorther = new ItemEightBorther();
                    itemEightBorther.setLayerId(mainStickerView.getId());
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
                    if (model.getLayerId() == nowMainLayerId) {
                        rightToLeft = (ItemRightToLeft) model;
                        break;
                    }
                }


                if (rightToLeft != null) {
                    (rightToLeft).toChangeSubLayer(callback, percentage);
                } else {
                    rightToLeft = new ItemRightToLeft();
                    rightToLeft.setLayerId(mainStickerView.getId());
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
                    if (model.getLayerId() == nowMainLayerId) {
                        itemBottomToCenter = (ItemBottomToCenter) model;
                        break;
                    }
                }


                if (itemBottomToCenter != null) {
                    (itemBottomToCenter).toChangeSubLayer(callback, percentage);
                } else {
                    itemBottomToCenter = new ItemBottomToCenter();
                    itemBottomToCenter.setLayerId(mainStickerView.getId());
                    (itemBottomToCenter).initToChangeSubLayer(mainStickerView, callback, percentage);
                    listForKeepBaseAnimMode.add(itemBottomToCenter);
                }
                break;


            case SWINGUPANDDOWN:
                int id = mainStickerView.getId();
                SwingUpAndDownToCenter swingUpAndDownToCenter = null;
                for (baseAnimModel model : listForKeepBaseAnimMode
                ) {
                    if (model.getLayerId() == id) {
                        swingUpAndDownToCenter = (SwingUpAndDownToCenter) model;
                        break;
                    }
                }
                if (swingUpAndDownToCenter != null) {
                    (swingUpAndDownToCenter).toChangeSubLayer(callback, percentage);
                } else {
                    swingUpAndDownToCenter = new SwingUpAndDownToCenter();
                    swingUpAndDownToCenter.setLayerId(mainStickerView.getId());
                    (swingUpAndDownToCenter).initToChangeSubLayer(mainStickerView, callback, percentage);
                    listForKeepBaseAnimMode.add(swingUpAndDownToCenter);
                }
                break;

            case ROATION:

                nowMainLayerId = mainStickerView.getId();
                Rotate rotate = null;
                for (baseAnimModel model : listForKeepBaseAnimMode
                ) {
                    if (model.getLayerId() == nowMainLayerId) {
                        rotate = (Rotate) model;
                        break;
                    }
                }

                if (rotate != null) {
                    (rotate).toChangeSubLayer(callback, percentage);
                } else {
                    rotate = new Rotate();
                    rotate.setLayerId(mainStickerView.getId());
                    (rotate).initToChangeSubLayer(mainStickerView, callback, percentage);
                    listForKeepBaseAnimMode.add(rotate);
                }
                break;


            case BOTTOMTOUP:


                nowMainLayerId = mainStickerView.getId();
                ItemBottomToTop itemBottomToTop = null;
                for (baseAnimModel model : listForKeepBaseAnimMode
                ) {
                    if (model.getLayerId() == nowMainLayerId) {
                        itemBottomToTop = (ItemBottomToTop) model;
                        break;
                    }
                }


                if (itemBottomToTop != null) {
                    (itemBottomToTop).toChangeSubLayer(callback, percentage);
                } else {
                    itemBottomToTop = new ItemBottomToTop();
                    itemBottomToTop.setLayerId(mainStickerView.getId());
                    (itemBottomToTop).initSubLayerData(mainStickerView, callback, percentage);
                    listForKeepBaseAnimMode.add(itemBottomToTop);
                }
                break;

            case LEFTANDRIGHTDISSMISS:

                nowMainLayerId = mainStickerView.getId();
                ItemLeftAndRightDissmiss itemLeftAndRightDissmiss = null;
                for (baseAnimModel model : listForKeepBaseAnimMode
                ) {
                    if (model.getLayerId() == nowMainLayerId) {
                        itemLeftAndRightDissmiss = (ItemLeftAndRightDissmiss) model;
                        break;
                    }
                }


                if (itemLeftAndRightDissmiss != null) {
                    (itemLeftAndRightDissmiss).toChangeSubLayer(callback, percentage);
                } else {
                    itemLeftAndRightDissmiss = new ItemLeftAndRightDissmiss();
                    itemLeftAndRightDissmiss.setLayerId(mainStickerView.getId());
                    (itemLeftAndRightDissmiss).initToChangeSubLayer(mainStickerView, callback, percentage);
                    listForKeepBaseAnimMode.add(itemLeftAndRightDissmiss);
                }

                break;

            case SUPERSTAR:

                nowMainLayerId = mainStickerView.getId();
                ItemCloned itemCloned = null;
                for (baseAnimModel model : listForKeepBaseAnimMode
                ) {
                    if (model.getLayerId() == nowMainLayerId) {
                        itemCloned = (ItemCloned) model;
                        break;
                    }
                }

                if (itemCloned != null) {
                    (itemCloned).toChangeSubLayer(callback, percentage);
                } else {
                    itemCloned = new ItemCloned();
                    itemCloned.setLayerId(mainStickerView.getId());
                    (itemCloned).initToChangeSubLayer(mainStickerView, callback, percentage);
                    listForKeepBaseAnimMode.add(itemCloned);
                }
                break;

            case SUPERSTAR2:
                nowMainLayerId = mainStickerView.getId();
                ItemCloned2 itemCloned2 = null;
                for (baseAnimModel model : listForKeepBaseAnimMode
                ) {
                    if (model.getLayerId() == nowMainLayerId) {
                        itemCloned2 = (ItemCloned2) model;
                        break;
                    }
                }

                if (itemCloned2 != null) {
                    (itemCloned2).toChangeSubLayer(callback, percentage);
                } else {
                    itemCloned2 = new ItemCloned2();
                    itemCloned2.setLayerId(mainStickerView.getId());
                    (itemCloned2).initToChangeSubLayer(mainStickerView, callback, percentage);
                    listForKeepBaseAnimMode.add(itemCloned2);
                }
                break;

            //底部居中位置
            case BOTTOMTOCENTER2:
                nowMainLayerId = mainStickerView.getId();
                ItemBottomToCenter2 itemBottomToCenter2 = null;
                for (baseAnimModel model : listForKeepBaseAnimMode
                ) {
                    if (model.getLayerId() == nowMainLayerId) {
                        itemBottomToCenter2 = (ItemBottomToCenter2) model;
                        break;
                    }
                }
                if (itemBottomToCenter2 != null) {
                    (itemBottomToCenter2).toChangeSubLayer(callback, percentage);
                } else {
                    itemBottomToCenter2 = new ItemBottomToCenter2();
                    itemBottomToCenter2.setLayerId(mainStickerView.getId());
                    itemBottomToCenter2.initToChangeSubLayer(mainStickerView, callback, percentage);
                    listForKeepBaseAnimMode.add(itemBottomToCenter2);
                }
                break;


            case CIRCLECLONED:
                nowMainLayerId = mainStickerView.getId();
                CircleCloned CircleCloned = null;
                for (baseAnimModel model : listForKeepBaseAnimMode
                ) {
                    if (model.getLayerId() == nowMainLayerId) {
                        CircleCloned = (CircleCloned) model;
                        break;
                    }
                }
                if (CircleCloned != null) {
                    (CircleCloned).toChangeSubLayer(listForSubLayer, callback, percentage);
                } else {
                    CircleCloned = new CircleCloned();
                    CircleCloned.setLayerId(mainStickerView.getId());
                    CircleCloned.initToChangeSubLayer(mainStickerView, listForSubLayer, callback, percentage);
                    listForKeepBaseAnimMode.add(CircleCloned);
                }
                break;


            case CIRCLECLONED2:
                nowMainLayerId = mainStickerView.getId();
                CircleCloned2 CircleCloned2 = null;
                for (baseAnimModel model : listForKeepBaseAnimMode
                ) {
                    if (model.getLayerId() == nowMainLayerId) {
                        CircleCloned2 = (CircleCloned2) model;
                        break;
                    }
                }
                if (CircleCloned2 != null) {
                    (CircleCloned2).toChangeSubLayer(listForSubLayer, callback, percentage);
                } else {
                    CircleCloned2 = new CircleCloned2();
                    CircleCloned2.setLayerId(mainStickerView.getId());
                    CircleCloned2.initToChangeSubLayer(mainStickerView, listForSubLayer, callback, percentage);
                    listForKeepBaseAnimMode.add(CircleCloned2);
                }
                break;


            case FIVEPOINTSTART:
                nowMainLayerId = mainStickerView.getId();
                FivePointStar fivePointStar = null;
                for (baseAnimModel model : listForKeepBaseAnimMode
                ) {
                    if (model.getLayerId() == nowMainLayerId) {
                        fivePointStar = (FivePointStar) model;
                        break;
                    }
                }
                if (fivePointStar != null) {
                    (fivePointStar).toChangeSubLayer(listForSubLayer, callback, percentage);
                } else {
                    fivePointStar = new FivePointStar();
                    fivePointStar.setLayerId(mainStickerView.getId());
                    fivePointStar.initToChangeSubLayer(mainStickerView, listForSubLayer, callback, percentage);
                    listForKeepBaseAnimMode.add(fivePointStar);
                }
                break;


            case FIVEPOINTSTART2:
                nowMainLayerId = mainStickerView.getId();
                FivePointStar2 fivePointStar2 = null;
                for (baseAnimModel model : listForKeepBaseAnimMode
                ) {
                    if (model.getLayerId() == nowMainLayerId) {
                        fivePointStar2 = (FivePointStar2) model;
                        break;
                    }
                }
                if (fivePointStar2 != null) {
                    fivePointStar2.toChangeSubLayer(listForSubLayer, callback, percentage);
                } else {
                    fivePointStar2 = new FivePointStar2();
                    fivePointStar2.setLayerId(mainStickerView.getId());
                    fivePointStar2.initToChangeSubLayer(mainStickerView, listForSubLayer, callback, percentage);
                    listForKeepBaseAnimMode.add(fivePointStar2);
                }
                break;



            case Z:
                nowMainLayerId = mainStickerView.getId();
                ZAnim zAnim = null;
                for (baseAnimModel model : listForKeepBaseAnimMode
                ) {
                    if (model.getLayerId() == nowMainLayerId) {
                        zAnim = (ZAnim) model;
                        break;
                    }
                }
                if (zAnim != null) {
                    zAnim.toChangeSubLayer(listForSubLayer, callback, percentage);
                } else {
                    zAnim = new ZAnim();
                    zAnim.setLayerId(mainStickerView.getId());
                    zAnim.initToChangeSubLayer(mainStickerView, listForSubLayer, callback, percentage);
                    listForKeepBaseAnimMode.add(zAnim);
                }
                break;



            case FOUNDER:
                nowMainLayerId = mainStickerView.getId();
                FounderAnim founderAnim = null;
                for (baseAnimModel model : listForKeepBaseAnimMode
                ) {
                    if (model.getLayerId() == nowMainLayerId) {
                        founderAnim = (FounderAnim) model;
                        break;
                    }
                }
                if (founderAnim != null) {
                    founderAnim.toChangeSubLayer(listForSubLayer, callback, percentage);
                } else {
                    founderAnim = new FounderAnim();
                    founderAnim.setLayerId(mainStickerView.getId());
                    founderAnim.initToChangeSubLayer(mainStickerView, listForSubLayer, callback, percentage);
                    listForKeepBaseAnimMode.add(founderAnim);
                }
                break;




            case MAKEMOVEONE:
                nowMainLayerId = mainStickerView.getId();
                MakeMoveOneAnim makeMoveOneAnim = null;
                for (baseAnimModel model : listForKeepBaseAnimMode
                ) {
                    if (model.getLayerId() == nowMainLayerId) {
                        makeMoveOneAnim = (MakeMoveOneAnim) model;
                        break;
                    }
                }
                if (makeMoveOneAnim != null) {
                    makeMoveOneAnim.toChangeSubLayer(listForSubLayer, callback, percentage);
                } else {
                    makeMoveOneAnim = new MakeMoveOneAnim();
                    makeMoveOneAnim.setLayerId(mainStickerView.getId());
                    makeMoveOneAnim.initToChangeSubLayer(mainStickerView, listForSubLayer, callback, percentage);
                    listForKeepBaseAnimMode.add(makeMoveOneAnim);
                }
                break;


            case LOVE:
                nowMainLayerId = mainStickerView.getId();
                LoveAnim loveAnim = null;
                for (baseAnimModel model : listForKeepBaseAnimMode
                ) {
                    if (model.getLayerId() == nowMainLayerId) {
                        loveAnim = (LoveAnim) model;
                        break;
                    }
                }
                if (loveAnim != null) {
                    loveAnim.toChangeSubLayer(listForSubLayer, callback, percentage);
                } else {
                    loveAnim = new LoveAnim();
                    loveAnim.setLayerId(mainStickerView.getId());
                    loveAnim.initToChangeSubLayer(mainStickerView, listForSubLayer, callback, percentage);
                    listForKeepBaseAnimMode.add(loveAnim);
                }
                break;



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

        StickerAnim stickerAnim9 = new StickerAnim();
        stickerAnim9.setName("下往上停");
        stickerAnim9.setIcon(R.mipmap.anim_cxws);
        stickerAnim9.setAnimType(AnimType.BOTTOMTOCENTER2);
        list.add(stickerAnim9);

        StickerAnim stickerAnim4 = new StickerAnim();
        stickerAnim4.setName("上下抖动");
        stickerAnim4.setIcon(R.mipmap.amin_sxdd);
        stickerAnim4.setAnimType(AnimType.SWINGUPANDDOWN);
        list.add(stickerAnim4);

        StickerAnim stickerAnim7 = new StickerAnim();
        stickerAnim7.setName("左右分身");
        stickerAnim7.setIcon(R.mipmap.anim_zyfs);
        stickerAnim7.setAnimType(AnimType.LEFTANDRIGHTDISSMISS);
        list.add(stickerAnim7);

        StickerAnim stickerAnim3 = new StickerAnim();
        stickerAnim3.setName("从下往上");
        stickerAnim3.setIcon(R.mipmap.anim_cxws);
        stickerAnim3.setAnimType(AnimType.BOTTOMTOCENTER);
        list.add(stickerAnim3);

        StickerAnim stickerAnim6 = new StickerAnim();
        stickerAnim6.setName("飞天分身");
        stickerAnim6.setIcon(R.mipmap.anim_ftfs);
        stickerAnim6.setAnimType(AnimType.BOTTOMTOUP);
        list.add(stickerAnim6);

        StickerAnim stickerAnim = new StickerAnim();
        stickerAnim.setName("左分身");
        stickerAnim.setIcon(R.mipmap.anim_zdy);
        stickerAnim.setAnimType(AnimType.LEFTTORIGHT);
        list.add(stickerAnim);

        StickerAnim stickerAnim8 = new StickerAnim();
        stickerAnim8.setName("一变三");
        stickerAnim8.setIcon(R.mipmap.anim_ybs);
        stickerAnim8.setAnimType(AnimType.SUPERSTAR);
        list.add(stickerAnim8);

        StickerAnim stickerAnim5 = new StickerAnim();
        stickerAnim5.setName("圆心旋转");
        stickerAnim5.setIcon(R.mipmap.anim_yxxx);
        stickerAnim5.setAnimType(AnimType.ROATION);
        list.add(stickerAnim5);

        StickerAnim stickerAnim2 = new StickerAnim();
        stickerAnim2.setName("多人旋转");
        stickerAnim2.setIcon(R.mipmap.anim_drxz);
        stickerAnim2.setAnimType(AnimType.EIGHTBORTHER);
        list.add(stickerAnim2);


        StickerAnim stickerAnim10 = new StickerAnim();
        stickerAnim10.setName("一变三停");
        stickerAnim10.setIcon(R.mipmap.anim_ybs);
        stickerAnim10.setAnimType(AnimType.SUPERSTAR2);
        list.add(stickerAnim10);

        StickerAnim stickerAnim11 = new StickerAnim();
        stickerAnim11.setName("圆圈");
        stickerAnim11.setIcon(R.mipmap.anim_ybs);
        stickerAnim11.setAnimType(AnimType.CIRCLECLONED);
        list.add(stickerAnim11);

        StickerAnim stickerAnim12 = new StickerAnim();
        stickerAnim12.setName("圆圈分身");
        stickerAnim12.setIcon(R.mipmap.anim_ybs);
        stickerAnim12.setAnimType(AnimType.CIRCLECLONED2);
        list.add(stickerAnim12);

        StickerAnim stickerAnim13 = new StickerAnim();
        stickerAnim13.setName("五角星");
        stickerAnim13.setIcon(R.mipmap.anim_ybs);
        stickerAnim13.setAnimType(AnimType.FIVEPOINTSTART);
        list.add(stickerAnim13);

        StickerAnim stickerAnim14 = new StickerAnim();
        stickerAnim14.setName("五角星2");
        stickerAnim14.setIcon(R.mipmap.anim_ybs);
        stickerAnim14.setAnimType(AnimType.FIVEPOINTSTART2);
        list.add(stickerAnim14);

        StickerAnim stickerAnim15 = new StickerAnim();
        stickerAnim15.setName("Z形");
        stickerAnim15.setIcon(R.mipmap.anim_ybs);
        stickerAnim15.setAnimType(AnimType.Z);
        list.add(stickerAnim15);

        StickerAnim stickerAnim16 = new StickerAnim();
        stickerAnim16.setName("方正");
        stickerAnim16.setIcon(R.mipmap.anim_ybs);
        stickerAnim16.setAnimType(AnimType.FOUNDER);
        list.add(stickerAnim16);

        StickerAnim stickerAnim17 = new StickerAnim();
        stickerAnim17.setName("动起来1");
        stickerAnim17.setIcon(R.mipmap.anim_ybs);
        stickerAnim17.setAnimType(AnimType.MAKEMOVEONE);
        list.add(stickerAnim17);


        StickerAnim stickerAnim18 = new StickerAnim();
        stickerAnim18.setName("桃心分身");
        stickerAnim18.setIcon(R.mipmap.anim_ybs);
        stickerAnim18.setAnimType(AnimType.LOVE);
        list.add(stickerAnim18);


        StickerAnim stickerAnim19 = new StickerAnim();
        stickerAnim19.setName("超级桃心");
        stickerAnim19.setIcon(R.mipmap.anim_ybs);
        stickerAnim19.setAnimType(AnimType.SUPERLOVE);
        list.add(stickerAnim19);


        return list;
    }


}

package com.flyingeffects.com.view.animations.beans;


import com.flyingeffects.com.R;

public enum AnimIcon2 {
    ANIM_ICON_DEFAULT(-1,-1,false),
    ANIM_ICON_1(1, R.drawable.ic_launcher_round,false),
    ANIM_ICON_2(2,R.drawable.ic_launcher_round,false),
    ANIM_ICON_3(3,R.drawable.ic_launcher_round,false),
    ANIM_ICON_4(4,R.drawable.ic_launcher_round,false),
    ANIM_ICON_5(5,R.drawable.ic_launcher_round,false),
    ANIM_ICON_6(6,R.drawable.ic_launcher_round,false),
    ANIM_ICON_7(7,R.drawable.ic_launcher_round,false),
    ANIM_ICON_8(8,R.drawable.ic_launcher_round,false),
    ANIM_ICON_9(9,R.drawable.ic_launcher_round,false),
    ANIM_ICON_10(10,R.drawable.ic_launcher_round,false),
    ANIM_ICON_11(11,R.drawable.ic_launcher_round,false),
    ANIM_ICON_12(12,R.drawable.ic_launcher_round,false),
    ANIM_ICON_13(13,R.drawable.ic_launcher_round,false),
    ANIM_ICON_14(14,R.drawable.ic_launcher_round,false),
    ANIM_ICON_15(15,R.drawable.ic_launcher_round,false),
    ANIM_ICON_16(16,R.drawable.ic_launcher_round,false),
    ANIM_ICON_17(17,R.drawable.ic_launcher_round,false),
    ANIM_ICON_18(18,R.drawable.ic_launcher_round,false),
    ANIM_ICON_19(19,R.drawable.ic_launcher_round,false),
    ANIM_ICON_20(20,R.drawable.ic_launcher_round,false),
    ANIM_ICON_21(21,R.drawable.ic_launcher_round,false);
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;
    private int resId;



    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public static int getResIdById(int id){
        AnimIcon2 target=null;
        for (AnimIcon2 icon: AnimIcon2.values()){
            if (icon.getId()==id){
                target=icon;
                break;
            }
        }
        if (target==null){
            return ANIM_ICON_DEFAULT.getResId();
        }
        return target.getResId();
    }
    private boolean selected;


    AnimIcon2(int key, int resId, boolean selected){
        this.id=key;
        this.resId=resId;
        this.selected=selected;
    }
}

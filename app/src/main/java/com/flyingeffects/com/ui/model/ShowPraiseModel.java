package com.flyingeffects.com.ui.model;


import com.orhanobut.hawk.Hawk;

import java.util.Calendar;


/**
 * description ：显示好评弹窗规则，规则1：如果是新用户（保存数量小于5）,用户已提交过好评，多次关闭好评弹出(>=3次)，当天有过弹窗
 * creation date: 2020/9/2
 * user : zhangtongju
 */
public class ShowPraiseModel {


    /**
     * description ：判断是不是新用户，保存数量小于5次的
     * creation date: 2020/9/2
     * user : zhangtongju
     */
    public  static  boolean getIsNewUser() {
        int count =Hawk.get("keepAlbumNum");
        return count<5;
    }

    public static void keepAlbumCount(){
        int num= Hawk.get("keepAlbumNum");
        num++;
        Hawk.put("keepAlbumNum", num);
    }




    /**
     * description ：统计关闭好评弹窗次数
     * creation date: 2020/9/3
     * user : zhangtongju
     */
    public  static  void statisticsCloseNum() {
        int num = Hawk.get("statisticsCloseNum");
        num++;
        Hawk.put("statisticsCloseNum", num);
    }


    /**
     * 关闭三次后就不显示弹窗
     */
    public  static  boolean canShowAlert() {
        int num = Hawk.get("statisticsCloseNum");
        return num < 3;
    }


    /**
     * 当日已经有了弹窗，那么不显示弹窗
     */
    public  static  boolean ToDayHasShowAd() {
        long showAdAlertTime = Hawk.get("showAdAlertTime");
        boolean hasShow=isSameDate(showAdAlertTime);
        if(hasShow){
            return true;
        }else{
            Hawk.put("showAdAlertTime",System.currentTimeMillis());
            return false;
        }
    }


    /**
     * description ：判断传入时间和当前时间相比
     * 是否还是同一天，true 同一天
     * creation date: 2020/9/3
     * user : zhangtongju
     */
    private static boolean isSameDate(long timestamp) {
        //当前时间
        Calendar currentTime = Calendar.getInstance();
        //要转换的时间
        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(timestamp);
        //年相同
        if (currentTime.get(Calendar.YEAR) == time.get(Calendar.YEAR)) {
            //获取一年中的第几天并相减，取差值
            return currentTime.get(Calendar.DAY_OF_YEAR) - time.get(Calendar.DAY_OF_YEAR) == 0;
        } else {
            return false;
        }
    }



    public  static  void setHasComment() {
        Hawk.put("setHasComment", 1);
    }


    /**
     * 关闭三次后就不显示弹窗
     */
    public  static  boolean getHasComment() {
        int getHasComment=Hawk.get("setHasComment");
        return getHasComment!=0;
    }



}

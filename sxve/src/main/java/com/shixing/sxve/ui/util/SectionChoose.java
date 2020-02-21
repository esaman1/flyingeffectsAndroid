package com.shixing.sxve.ui.util;

import java.util.ArrayList;

/**
 * description ：区间选择
 * date: ：2019/5/18 10:15
 * author: 张同举 @邮箱 jutongzhang@sina.com
 */
public class SectionChoose {

//    private static int nowChooseItem;
//    public  static int section(ArrayList<Double> list, int nowProgress,int fps) {
//        for (int i = 1; i <=list.size(); i++) {
//            if(i>=list.size()){ //最后一个了
//               return  i-1;
//            }
//            int time= (int) (list.get(i)*fps);
//            if (nowProgress <time) {
//                if(  nowChooseItem!=i){
//                    return i-1;
//                }
//                nowChooseItem=i;
//            }
//        }
//        return 0;
//    }




    private static int nowChooseItem;
    public  static int section(ArrayList<Double> list, int nowProgress,int fps) {
        for (int i = 1; i <=list.size(); i++) {
            if(i>=list.size()){ //最后一个了
                return  i-1;
            }
            int time= (int) (list.get(i)*1000);
            if (nowProgress <time) {
                if(  nowChooseItem!=i){
                    return i-1;
                }
                nowChooseItem=i;
            }
        }
        return 0;
    }



}

package com.flyingeffects.com.enity;

import java.io.Serializable;


/**
 * description ：
 * creation date: 2020/8/10
 * user : zhangtongju
 */
public class CreateTemplateScrollViewPosition implements Serializable {

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public float getScrollViewHeight() {
        return scrollViewHeight;
    }

    public void setScrollViewHeight(float scrollViewHeight) {
        this.scrollViewHeight = scrollViewHeight;
    }

    float percentage;
    float scrollViewHeight;

    public CreateTemplateScrollViewPosition(float percentage,float scrollViewHeight){

        if(percentage+scrollViewHeight>1){
            this.percentage=percentage-scrollViewHeight;
            this.scrollViewHeight=percentage;
        }else{
            this.percentage=percentage;
            this.scrollViewHeight=scrollViewHeight;
        }

    }








}

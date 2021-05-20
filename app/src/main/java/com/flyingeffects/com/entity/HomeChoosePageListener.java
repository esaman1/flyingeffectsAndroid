package com.flyingeffects.com.entity;


/**
 * description ：主页页面选择通知
 * creation date: 2021/3/18
 * user : zhangtongju
 */
public class HomeChoosePageListener {

    public int getPager() {
        return pager;
    }

    public void setPager(int pager) {
        this.pager = pager;
    }

    private int pager;

    public HomeChoosePageListener(int pager) {
        this.pager = pager;
    }

}

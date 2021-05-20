package com.flyingeffects.com.entity;

import java.io.Serializable;
import java.util.List;

public class ListForUpAndDown implements Serializable {

    public List<NewFragmentTemplateItem> getAllData() {
        return allData;
    }

    public void setAllData(List<NewFragmentTemplateItem> allData) {
        this.allData = allData;
    }

    private List<NewFragmentTemplateItem> allData ;


    public ListForUpAndDown(List<NewFragmentTemplateItem> allData){
        this.allData=allData;
    }

}

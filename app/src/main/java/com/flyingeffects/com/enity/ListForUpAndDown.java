package com.flyingeffects.com.enity;

import java.io.Serializable;
import java.util.List;

public class ListForUpAndDown implements Serializable {

    public List<new_fag_template_item> getAllData() {
        return allData;
    }

    public void setAllData(List<new_fag_template_item> allData) {
        this.allData = allData;
    }

    private List<new_fag_template_item> allData ;


    public ListForUpAndDown(List<new_fag_template_item> allData){
        this.allData=allData;
    }

}

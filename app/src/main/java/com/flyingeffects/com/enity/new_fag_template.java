package com.flyingeffects.com.enity;

import java.io.Serializable;
import java.util.ArrayList;

public class new_fag_template implements Serializable {


    public ArrayList<new_fag_template_item> getTemplate_item() {
        return template_item;
    }

    public void setTemplate_item(ArrayList<new_fag_template_item> template_item) {
        this.template_item = template_item;
    }

    ArrayList<new_fag_template_item> template_item;


}

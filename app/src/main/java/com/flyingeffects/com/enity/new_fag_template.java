package com.flyingeffects.com.enity;

import java.io.Serializable;
import java.util.ArrayList;

public class new_fag_template implements Serializable {


    public ArrayList<NewFragmentTemplateItem> getTemplate_item() {
        return template_item;
    }

    public void setTemplate_item(ArrayList<NewFragmentTemplateItem> template_item) {
        this.template_item = template_item;
    }

    ArrayList<NewFragmentTemplateItem> template_item;


}

package com.flyingeffects.com.enity;

import java.io.Serializable;
import java.util.ArrayList;

public class SystemMessageCountAllEntiy implements Serializable {


    public ArrayList<systemessagelist> getSystem_message() {
        return system_message;
    }

    public void setSystem_message(ArrayList<systemessagelist> system_message) {
        this.system_message = system_message;
    }

    private ArrayList<systemessagelist>system_message;
}

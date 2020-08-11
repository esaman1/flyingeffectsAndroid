package com.flyingeffects.com.enity;

public class DeleteMessage {

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    int position;

    public DeleteMessage(int position) {
        this.position = position;
    }
}

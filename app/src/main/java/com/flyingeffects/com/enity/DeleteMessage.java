package com.flyingeffects.com.enity;

public class DeleteMessage {

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isFirstComment() {
        return isFirstComment;
    }

    public void setFirstComment(boolean firstComment) {
        isFirstComment = firstComment;
    }

    boolean isFirstComment;

    int position;

    public DeleteMessage(int position,boolean isFirstComment) {
        this.position = position;
        this.isFirstComment=isFirstComment;
    }
}

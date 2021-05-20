package com.flyingeffects.com.entity;

import java.io.Serializable;

public class SelectMusicCollet implements Serializable {

    public String getMusic_id() {
        return music_id;
    }

    public void setMusic_id(String music_id) {
        this.music_id = music_id;
    }

    private String music_id;

    public SelectMusicCollet(String music_id) {
        this.music_id = music_id;
    }


}

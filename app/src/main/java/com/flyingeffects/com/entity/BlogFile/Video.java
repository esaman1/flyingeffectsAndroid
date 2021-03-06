package com.flyingeffects.com.entity.BlogFile;

import java.io.Serializable;
import java.util.Comparator;

/**
 * @创建人 chaychan
 * @创建时间 2016/7/23  17:20
 */
public class Video implements Serializable, Comparator<Video> {
    private int id = 0;
    private String path = null;
    private String name = null;
    private String resolution = null;// 分辨率
    private long size = 0;
    private long date = 0;
    private long duration = 0;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    private boolean isChecked=false;

    public Video(int id, String path, String name, String resolution, long size, long date, long duration) {
        this.id = id;
        this.path = path;
        this.name = name;
        this.resolution = resolution;
        this.size = size;
        this.date = date;
        this.duration = duration;
    }

    public Video() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }


    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    private boolean isPlaying;





    @Override
    public String toString() {
        return "Video [id=" + id + ", path=" + path + ", name=" + name + ", resolution=" + resolution + ", size=" + size + ", date=" + date
                + ", duration=" + duration + "]";
    }


    @Override
    public int compare(Video o1, Video o2) {
        long i =  o2.getDate()-o1.getDate();//先按照年龄排序
        return (int) i;
}
}

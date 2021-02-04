package com.flyingeffects.com.enity;

import java.io.Serializable;

public class StickerList implements Serializable {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThumbnailimage() {
        return thumbnailimage;
    }

    public void setThumbnailimage(String thumbnailimage) {
        this.thumbnailimage = thumbnailimage;
    }

    public String getSourcefile() {
        return sourcefile;
    }

    public void setSourcefile(String sourcefile) {
        this.sourcefile = sourcefile;
    }

    public String getWeigh() {
        return weigh;
    }

    public void setWeigh(String weigh) {
        this.weigh = weigh;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getIs_circle() {
        return is_circle;
    }

    public void setIs_circle(String is_circle) {
        this.is_circle = is_circle;
    }

    private String title;
    private String image;
    private String thumbnailimage;
    private String sourcefile;
    private String weigh;
    private String create_time;
    private String is_circle;

    public int getIsDownload() {
        return isDownload;
    }

    public void setIsDownload(int isDownload) {
        this.isDownload = isDownload;
    }

    /**
     * 是否已经下载，0 未下载，1 已下载
     */
    private int isDownload;

    public boolean isClearSticker() {
        return isClearSticker;
    }

    public void setClearSticker(boolean clearSticker) {
        isClearSticker = clearSticker;
    }

    /**
     * 是否是清理帖子按钮，默认为true
     */
    private boolean isClearSticker=false;


    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    private boolean isChecked=false;






    public String getAppstickercategory_id() {
        return appstickercategory_id;
    }

    public void setAppstickercategory_id(String appstickercategory_id) {
        this.appstickercategory_id = appstickercategory_id;
    }

    public String getCreate_time_text() {
        return create_time_text;
    }

    public void setCreate_time_text(String create_time_text) {
        this.create_time_text = create_time_text;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }





    //拍摄贴新增加的数据
    private String appstickercategory_id;
    private String create_time_text;
    private String  status;
    private String file_name;

}

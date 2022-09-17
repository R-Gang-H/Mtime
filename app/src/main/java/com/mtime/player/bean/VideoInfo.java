package com.mtime.player.bean;

import java.io.Serializable;

/**
 * Created by JiaJunHui on 2018/6/20.
 */
public class VideoInfo implements Serializable {

    private String vid;
    private String sourceType;

    public VideoInfo() {
    }

    public VideoInfo(String vid, String sourceType) {
        this.vid = vid;
        this.sourceType = sourceType;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

}

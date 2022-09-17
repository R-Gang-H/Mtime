package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by LEE on 7/28/16.
 */
public class MovieVideoItem extends MBaseBean {
    private String url; // 主视频
    private String videoId; // 主视频id
    private String title; // 主视频标题
    private String hightUrl; // 高清地址
    private String img; // 视频图片
    private int count; // 视频总数

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHightUrl() {
        return hightUrl;
    }

    public void setHightUrl(String hightUrl) {
        this.hightUrl = hightUrl;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

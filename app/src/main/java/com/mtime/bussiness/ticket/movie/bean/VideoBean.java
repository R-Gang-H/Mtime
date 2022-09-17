package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

public class VideoBean extends MBaseBean {
    private String videoId;
    private String url;
    private String image;

    public String getHightUrl() {
        return hightUrl;
    }

    public void setHightUrl(String hightUrl) {
        this.hightUrl = hightUrl;
    }

    private String hightUrl;

    public String getVideoId() {
	return videoId;
    }

    public void setVideoId(final String videoId) {
	this.videoId = videoId;
    }

    public String getUrl() {
	return url;
    }

    public void setUrl(final String url) {
	this.url = url;
    }

    public String getImage() {
	return image;
    }

    public void setImage(final String image) {
	this.image = image;
    }

    public int getLength() {
	return length;
    }

    public void setLength(final int length) {
	this.length = length;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(final String title) {
	this.title = title;
    }

    private int length;
    private String title;
}

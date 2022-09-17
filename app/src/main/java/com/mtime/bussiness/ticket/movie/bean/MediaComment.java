package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * 媒体评论Bean
 */
public class MediaComment extends MBaseBean {
    private int id;
    private int publishTime;
    private String title;
    private String summary;

    public int getId() {
        return id;
    }

    public int getPublishTime() {
        return publishTime;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPublishTime(int publishTime) {
        this.publishTime = publishTime;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}

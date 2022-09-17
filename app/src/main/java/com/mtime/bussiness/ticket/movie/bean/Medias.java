package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

/**
 * 影片媒体评论Bean
 */
public class Medias extends MBaseBean {
    private String name;
    private String icon;
    private List<MediaComment> comments;

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public List<MediaComment> getComments() {
        return comments;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setComments(List<MediaComment> comments) {
        this.comments = comments;
    }
}

package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;
import com.mtime.bussiness.ticket.movie.activity.MovieMoreInfoActivity;

/**
 * 用于{@link MovieMoreInfoActivity}
 */
@Deprecated
public class MoreInfoWebBean extends MBaseBean {
    private String text;
    private String url;

    public String getText() {
        return text;
    }

    public String getUrl() {
        return url;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

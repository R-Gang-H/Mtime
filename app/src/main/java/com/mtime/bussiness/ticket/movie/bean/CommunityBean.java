package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by guangshun on 16-5-13.
 */
public class CommunityBean extends MBaseBean {
    private String title;
    private String count;
    private String url;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public String getTitle(){
        return title;
    }
    public String getCount(){
        return count;
    }
    public String getUrl(){
        return url;
    }
}

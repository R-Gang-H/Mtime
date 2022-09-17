package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

public class TopList extends MBaseBean {
    private int    id;
    private String title;
    private String imageUrl;
    private String summary;
    private String name;
    private int    totalCount;
    
    public String getName() {
        return name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getSummary() {
        if (summary == null) {
            return "";
        }
        return summary;
    }
    
    public void setSummary(final String summary) {
        this.summary = summary;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(final String title) {
        this.title = title;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}

package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;

public class FavoriteCinemaListByCityIDBean extends MBaseBean {
    private String fid;
    private String id;
    private String title;
    private String address;
    
    public String getFid() {
        return fid;
    }
    
    public void setFid(String fid) {
        this.fid = fid;
    }
    
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
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
}

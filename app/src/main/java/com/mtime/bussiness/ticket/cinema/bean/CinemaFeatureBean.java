package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;

public class CinemaFeatureBean extends MBaseBean {
    private String name;
    private boolean isSupport;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public boolean isSupport() {
        return isSupport;
    }
    
    public void setSupport(boolean isSupport) {
        this.isSupport = isSupport;
    }
    
}

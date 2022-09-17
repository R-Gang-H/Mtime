package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

public class PayCardListBean extends MBaseBean {
    private String name;
    private int typeId;
    private String  url;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getTypeId() {
        return typeId;
    }
    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    
    
}

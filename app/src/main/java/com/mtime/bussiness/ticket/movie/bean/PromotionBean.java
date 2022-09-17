package com.mtime.bussiness.ticket.movie.bean;


import com.mtime.base.bean.MBaseBean;

public class PromotionBean extends MBaseBean {

    private String image;
    private String url;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

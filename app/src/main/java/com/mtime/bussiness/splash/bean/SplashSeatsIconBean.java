package com.mtime.bussiness.splash.bean;

import com.mtime.base.bean.MBaseBean;

public class SplashSeatsIconBean extends MBaseBean {

    private int imgId;
    private String imageUrl;

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getImageUrl() {
        if (imageUrl == null) {
            return "";
        } else {
            return imageUrl;
        }
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

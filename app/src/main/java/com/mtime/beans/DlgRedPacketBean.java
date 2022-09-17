package com.mtime.beans;

import com.mtime.base.bean.MBaseBean;

public class DlgRedPacketBean extends MBaseBean {
    // 购物购票成功后弹出的红包弹窗
    private int id;
    private String image;
    private String url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

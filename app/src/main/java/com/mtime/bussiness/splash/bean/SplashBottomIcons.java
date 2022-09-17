package com.mtime.bussiness.splash.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by zhuhui on 15/10/10.
 */
public class SplashBottomIcons extends MBaseBean {

    /**
     * type : 1
     * img :
     * selectedImg :
     * text : 首页
     */

    private String type;
    private String img;
    private String selectedImg;
    private String text;

    public void setType(String type) {
        this.type = type;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setSelectedImg(String selectedImg) {
        this.selectedImg = selectedImg;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public String getImg() {
        return img;
    }

    public String getSelectedImg() {
        return selectedImg;
    }

    public String getText() {
        return text;
    }
}

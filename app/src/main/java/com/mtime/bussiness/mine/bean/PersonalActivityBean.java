package com.mtime.bussiness.mine.bean;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;

public class PersonalActivityBean  extends MBaseBean implements Serializable {
    private static final long serialVersionUID = 516840708041254691L;
    private String title;
    private String color;
    private String url;

    public String getTitle() {
        if (title == null) {
            return "";
        }
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getColor() {
        if (color == null) {
            return "";
        }
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public String getUrl() {
        if (url == null) {
            return "";
        }
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
}

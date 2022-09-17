package com.mtime.bussiness.information.bean;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;

public class NewsImagesBean extends MBaseBean implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String gId;
    private String title;
    private String desc;
    private String url1;
    private String url2;

    public String getTitle() {
	return title;
    }

    public void setTitle(final String title) {
	this.title = title;
    }

    public String getDesc() {
	return desc;
    }

    public void setDesc(final String desc) {
	this.desc = desc;
    }

    public String getUrl1() {
	return url1;
    }

    public void setUrl1(final String url1) {
	this.url1 = url1;
    }

    public String getUrl2() {
	return url2;
    }

    public void setUrl2(final String url2) {
	this.url2 = url2;
    }

    public String getgId() {
	return gId;
    }

    public void setgId(final String gId) {
	this.gId = gId;
    }

}

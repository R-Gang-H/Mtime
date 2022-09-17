package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

public class CapchaMainBean extends MBaseBean {
    private boolean isVaild;
    private String codeId;
    private String url;

    public boolean isVaild() {
	return isVaild;
    }

    public void setVaild(final boolean isVaild) {
	this.isVaild = isVaild;
    }

    public String getCodeId() {
	return codeId;
    }

    public void setCodeId(final String codeId) {
	this.codeId = codeId;
    }

    public String getUrl() {
	return url;
    }

    public void setUrl(final String url) {
	this.url = url;
    }

}

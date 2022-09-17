package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

public class ProviderBean extends MBaseBean {
    private int id;
    private int dId;
    private int level;

    public int getId() {
	return id;
    }

    public void setId(final int id) {
	this.id = id;
    }

    public int getdId() {
	return dId;
    }

    public void setdId(final int dId) {
	this.dId = dId;
    }

    public int getLevel() {
	return level;
    }

    public void setLevel(final int level) {
	this.level = level;
    }

}

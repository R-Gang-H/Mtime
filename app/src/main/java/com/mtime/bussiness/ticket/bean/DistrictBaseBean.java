package com.mtime.bussiness.ticket.bean;

import com.mtime.base.bean.MBaseBean;

public class DistrictBaseBean extends MBaseBean {
    private int id;
    private String name;

    public int getId() {
	return id;
    }

    public void setId(final int id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(final String name) {
	this.name = name;
    }
}

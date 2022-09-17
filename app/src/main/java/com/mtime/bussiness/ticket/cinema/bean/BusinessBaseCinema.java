package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;

public class BusinessBaseCinema extends MBaseBean {
    private int busId;
    private int cId;

    public int getBusId() {
	return busId;
    }

    public void setBusId(final int busId) {
	this.busId = busId;
    }

    public int getCId() {
	return cId;
    }

    public void setCId(final int cId) {
	this.cId = cId;
    }
}
